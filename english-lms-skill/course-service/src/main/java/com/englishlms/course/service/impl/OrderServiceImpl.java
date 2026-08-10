package com.englishlms.course.service.impl;

import com.englishlms.course.dto.CreateOrderRequest;
import com.englishlms.course.dto.CreatePaymentResponse;
import com.englishlms.course.dto.OrderResponse;
import com.englishlms.course.dto.PageResponse;
import com.englishlms.course.dto.RevenueReportResponse;
import com.englishlms.course.dto.RevenueTimelineItem;
import com.englishlms.course.dto.TopCourseRevenueItem;
import com.englishlms.course.entity.Course;
import com.englishlms.course.entity.CourseOrder;
import com.englishlms.course.entity.Enrollment;
import com.englishlms.course.entity.EnrollmentStatus;
import com.englishlms.course.entity.OrderStatus;
import com.englishlms.course.entity.PaymentTransaction;
import com.englishlms.course.entity.PaymentTransactionStatus;
import com.englishlms.course.exception.AppException;
import com.englishlms.course.exception.CourseAlreadyOwnedException;
import com.englishlms.course.exception.CourseNotPublishedException;
import com.englishlms.course.exception.InvalidPaymentSignatureException;
import com.englishlms.course.exception.OrderAlreadyPaidException;
import com.englishlms.course.exception.OrderExpiredException;
import com.englishlms.course.exception.OrderNotFoundException;
import com.englishlms.course.exception.PaymentAmountMismatchException;
import com.englishlms.course.exception.PaymentFailedException;
import com.englishlms.course.exception.ResourceNotFoundException;
import com.englishlms.course.payment.MockPaymentGateway;
import com.englishlms.course.payment.PaymentGateway;
import com.englishlms.course.payment.VnPayPaymentGateway;
import com.englishlms.course.repository.CourseOrderRepository;
import com.englishlms.course.repository.CourseRepository;
import com.englishlms.course.repository.EnrollmentRepository;
import com.englishlms.course.repository.LessonRepository;
import com.englishlms.course.repository.PaymentTransactionRepository;
import com.englishlms.course.service.OrderService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final Random RANDOM = new Random();

    private final CourseOrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final VnPayPaymentGateway vnPayPaymentGateway;
    private final MockPaymentGateway mockPaymentGateway;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public OrderServiceImpl(
            CourseOrderRepository orderRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            LessonRepository lessonRepository,
            VnPayPaymentGateway vnPayPaymentGateway,
            MockPaymentGateway mockPaymentGateway
    ) {
        this.orderRepository = orderRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.vnPayPaymentGateway = vnPayPaymentGateway;
        this.mockPaymentGateway = mockPaymentGateway;
    }

    @Override
    public OrderResponse createOrder(String email, CreateOrderRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + request.getCourseId()));

        if (Boolean.FALSE.equals(course.getPublished())) {
            throw new CourseNotPublishedException("Khóa học hiện đang ngừng xuất bản. Không thể mua vào lúc này.");
        }

        if (course.isFree()) {
            throw new AppException("Khóa học này miễn phí, không cần tạo đơn hàng thanh toán.", HttpStatus.BAD_REQUEST);
        }

        // Check if student already owns active or legacy enrollment
        Optional<Enrollment> existingOpt = enrollmentRepository.findByStudentEmailAndCourseId(email, course.getId());
        if (existingOpt.isPresent() && existingOpt.get().getStatus().hasFullAccess()) {
            throw new CourseAlreadyOwnedException("Bạn đã sở hữu khóa học này rồi. Không thể mua lại.");
        }

        UUID userId = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        String provider = request.getPaymentProvider() != null ? request.getPaymentProvider().toUpperCase() : "VNPAY";

        // Reuse unexpired PENDING order if exists
        Optional<CourseOrder> existingOrderOpt = orderRepository.findFirstByUserIdAndCourseIdAndStatusAndExpiresAtAfter(
                userId, course.getId(), OrderStatus.PENDING, LocalDateTime.now());

        if (existingOrderOpt.isPresent()) {
            CourseOrder existingOrder = existingOrderOpt.get();
            existingOrder.setPaymentProvider(provider);
            CourseOrder saved = orderRepository.save(existingOrder);
            log.info("Reusing existing PENDING order [{}] for user [{}]", saved.getOrderCode(), email);
            return mapToOrderResponse(saved);
        }

        // Generate unique order code: LMS + yyyyMMddHHmmss + random 3-digit
        String timestampStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String orderCode = "LMS" + timestampStr + String.format("%03d", RANDOM.nextInt(1000));

        BigDecimal originalPrice = course.getPrice() != null ? course.getPrice() : BigDecimal.ZERO;
        BigDecimal effectivePrice = course.getEffectivePrice();
        BigDecimal discount = originalPrice.subtract(effectivePrice).max(BigDecimal.ZERO);

        CourseOrder order = CourseOrder.builder()
                .orderCode(orderCode)
                .userId(userId)
                .userEmailSnapshot(email)
                .course(course)
                .courseTitleSnapshot(course.getTitle())
                .originalPrice(originalPrice)
                .discountAmount(discount)
                .totalAmount(effectivePrice)
                .currency(course.getCurrency() != null ? course.getCurrency() : "VND")
                .status(OrderStatus.PENDING)
                .paymentProvider(provider)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        CourseOrder saved = orderRepository.save(order);
        log.info("Created new CourseOrder [{}] for user [{}] amount [{}]", saved.getOrderCode(), email, saved.getTotalAmount());

        return mapToOrderResponse(saved);
    }

    @Override
    public CreatePaymentResponse createPaymentUrl(String orderCode, String email, String returnUrl) {
        CourseOrder order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng với mã: " + orderCode));

        if (!order.getUserEmailSnapshot().equalsIgnoreCase(email)) {
            throw new AppException("Bạn không có quyền thao tác trên đơn hàng này.", HttpStatus.FORBIDDEN);
        }

        if (order.getStatus() == OrderStatus.PAID) {
            throw new OrderAlreadyPaidException("Đơn hàng này đã được thanh toán thành công trước đó.");
        }

        if (order.isExpired() || order.getStatus() == OrderStatus.EXPIRED) {
            order.setStatus(OrderStatus.EXPIRED);
            orderRepository.save(order);
            throw new OrderExpiredException("Đơn hàng đã hết hạn thanh toán (quá 15 phút). Vui lòng tạo đơn mới.");
        }

        PaymentGateway gateway = getGateway(order.getPaymentProvider());
        String defaultReturnUrl = (returnUrl != null && !returnUrl.isBlank()) ? returnUrl : frontendUrl + "/payment/result";
        String paymentUrl = gateway.createPaymentUrl(order, defaultReturnUrl);

        return CreatePaymentResponse.builder()
                .orderCode(order.getOrderCode())
                .paymentUrl(paymentUrl)
                .provider(order.getPaymentProvider())
                .totalAmount(order.getTotalAmount())
                .expiresAt(order.getExpiresAt())
                .build();
    }

    @Override
    @Transactional
    public OrderResponse processPaymentCallback(String providerStr, Map<String, String> queryParams) {
        String rawOrderCode = queryParams.get("vnp_TxnRef");
        if (rawOrderCode == null || rawOrderCode.isBlank()) {
            rawOrderCode = queryParams.get("orderCode");
        }

        if (rawOrderCode == null || rawOrderCode.isBlank()) {
            throw new OrderNotFoundException("Callback không chứa mã đơn hàng hợp lệ.");
        }

        final String orderCode = rawOrderCode;

        CourseOrder order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng: " + orderCode));

        // Idempotency: If already PAID, return immediately without re-processing
        if (order.getStatus() == OrderStatus.PAID) {
            log.info("Idempotent callback for already PAID order [{}]", orderCode);
            return mapToOrderResponse(order);
        }

        PaymentGateway gateway = getGateway(providerStr != null ? providerStr : order.getPaymentProvider());

        // Verify HMAC signature
        boolean isValidSignature = gateway.verifyCallback(queryParams);
        if (!isValidSignature) {
            log.error("Invalid payment signature for order [{}]", orderCode);
            throw new InvalidPaymentSignatureException("Chữ ký xác thực thanh toán không hợp lệ.");
        }

        String responseCode = gateway.extractResponseCode(queryParams);
        String providerTxId = gateway.extractProviderTransactionId(queryParams);

        boolean isSuccess = "00".equals(responseCode) || "SUCCESS".equalsIgnoreCase(responseCode) || "SUCCESS".equalsIgnoreCase(queryParams.get("status"));

        if (!isSuccess) {
            log.warn("Payment failed for order [{}] with response code [{}]", orderCode, responseCode);
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            PaymentTransaction tx = PaymentTransaction.builder()
                    .order(order)
                    .provider(gateway.getProviderName())
                    .providerTransactionId(providerTxId)
                    .amount(order.getTotalAmount())
                    .currency(order.getCurrency())
                    .status(PaymentTransactionStatus.FAILED)
                    .responseCode(responseCode)
                    .processedAt(LocalDateTime.now())
                    .build();
            paymentTransactionRepository.save(tx);

            throw new PaymentFailedException("Thanh toán thất bại từ nhà cung cấp (" + responseCode + ").");
        }

        // Verify amount
        String vnpAmountStr = queryParams.get("vnp_Amount");
        if (vnpAmountStr != null && !vnpAmountStr.isBlank()) {
            try {
                BigDecimal paidAmount = new BigDecimal(vnpAmountStr).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                if (paidAmount.compareTo(order.getTotalAmount()) != 0) {
                    log.error("Amount mismatch for order [{}]: expected [{}], got [{}]", orderCode, order.getTotalAmount(), paidAmount);
                    throw new PaymentAmountMismatchException("Số tiền thanh toán không khớp với tổng tiền đơn hàng.");
                }
            } catch (NumberFormatException e) {
                log.warn("Could not parse vnp_Amount [{}]", vnpAmountStr);
            }
        }

        // Transition Order -> PAID & PaymentTransaction -> SUCCESS & Upgrade Enrollment -> ACTIVE
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        CourseOrder updatedOrder = orderRepository.save(order);

        PaymentTransaction tx = PaymentTransaction.builder()
                .order(updatedOrder)
                .provider(gateway.getProviderName())
                .providerTransactionId(providerTxId)
                .amount(updatedOrder.getTotalAmount())
                .currency(updatedOrder.getCurrency())
                .status(PaymentTransactionStatus.SUCCESS)
                .responseCode(responseCode != null ? responseCode : "00")
                .processedAt(LocalDateTime.now())
                .build();
        paymentTransactionRepository.save(tx);

        // Upgrade or Create Enrollment -> ACTIVE
        upgradeOrCreateEnrollmentToActive(updatedOrder);

        log.info("Successfully processed payment for order [{}]! Enrollment set to ACTIVE.", orderCode);
        return mapToOrderResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse processMockPayment(String orderCode, String email, String status) {
        CourseOrder order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng với mã: " + orderCode));

        if (!order.getUserEmailSnapshot().equalsIgnoreCase(email)) {
            throw new AppException("Bạn không có quyền thanh toán cho đơn hàng này.", HttpStatus.FORBIDDEN);
        }

        if (order.getStatus() == OrderStatus.PAID) {
            log.info("Mock payment called on already PAID order [{}]", orderCode);
            return mapToOrderResponse(order);
        }

        if (order.isExpired() || order.getStatus() == OrderStatus.EXPIRED) {
            order.setStatus(OrderStatus.EXPIRED);
            orderRepository.save(order);
            throw new OrderExpiredException("Đơn hàng đã hết hạn.");
        }

        Map<String, String> mockParams = new HashMap<>();
        mockParams.put("orderCode", orderCode);
        mockParams.put("status", status != null ? status : "SUCCESS");
        mockParams.put("responseCode", "SUCCESS".equalsIgnoreCase(status) ? "00" : "99");
        mockParams.put("transactionId", "MOCK_" + System.currentTimeMillis());

        return processPaymentCallback("MOCK", mockParams);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getMyOrders(String email, int page, int size) {
        UUID userId = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CourseOrder> orderPage = orderRepository.findByUserId(userId, pageable);

        List<OrderResponse> items = orderPage.getContent().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());

        return PageResponse.<OrderResponse>builder()
                .items(items)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .isLast(orderPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByCode(String orderCode, String email, String role) {
        CourseOrder order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng: " + orderCode));

        boolean isAdmin = role != null && (role.contains("ADMIN") || role.contains("ROLE_ADMIN"));
        if (!isAdmin && !order.getUserEmailSnapshot().equalsIgnoreCase(email)) {
            throw new AppException("Bạn không có quyền xem đơn hàng này.", HttpStatus.FORBIDDEN);
        }

        return mapToOrderResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(String orderCode, String email) {
        CourseOrder order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng: " + orderCode));

        if (!order.getUserEmailSnapshot().equalsIgnoreCase(email)) {
            throw new AppException("Bạn không có quyền hủy đơn hàng này.", HttpStatus.FORBIDDEN);
        }

        if (order.getStatus() == OrderStatus.PAID) {
            throw new OrderAlreadyPaidException("Không thể hủy đơn hàng đã thanh toán thành công.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        CourseOrder updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrdersForAdmin(
            int page, int size, String search, String statusStr, String courseIdStr, String fromStr, String toStr
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<CourseOrder> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("orderCode")), pattern),
                        cb.like(cb.lower(root.get("userEmailSnapshot")), pattern),
                        cb.like(cb.lower(root.get("courseTitleSnapshot")), pattern)
                ));
            }

            if (statusStr != null && !statusStr.isBlank() && !"ALL".equalsIgnoreCase(statusStr)) {
                try {
                    OrderStatus status = OrderStatus.valueOf(statusStr.trim().toUpperCase());
                    predicates.add(cb.equal(root.get("status"), status));
                } catch (IllegalArgumentException ignored) {}
            }

            if (courseIdStr != null && !courseIdStr.isBlank()) {
                try {
                    UUID courseId = UUID.fromString(courseIdStr.trim());
                    predicates.add(cb.equal(root.get("course").get("id"), courseId));
                } catch (IllegalArgumentException ignored) {}
            }

            if (fromStr != null && !fromStr.isBlank()) {
                LocalDate fromDate = LocalDate.parse(fromStr.trim());
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate.atStartOfDay()));
            }

            if (toStr != null && !toStr.isBlank()) {
                LocalDate toDate = LocalDate.parse(toStr.trim());
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate.atTime(LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<CourseOrder> orderPage = orderRepository.findAll(spec, pageable);

        List<OrderResponse> items = orderPage.getContent().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());

        return PageResponse.<OrderResponse>builder()
                .items(items)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .isLast(orderPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponse getRevenueReport(String fromStr, String toStr, UUID courseId, String groupBy) {
        LocalDateTime from = (fromStr != null && !fromStr.isBlank())
                ? LocalDate.parse(fromStr.trim()).atStartOfDay()
                : LocalDate.now().minusMonths(1).atStartOfDay();

        LocalDateTime to = (toStr != null && !toStr.isBlank())
                ? LocalDate.parse(toStr.trim()).atTime(LocalTime.MAX)
                : LocalDate.now().atTime(LocalTime.MAX);

        BigDecimal totalRevenue = orderRepository.sumRevenueBetween(from, to);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        long paidOrdersCount = orderRepository.countPaidOrdersBetween(from, to);
        long distinctStudentsCount = orderRepository.countDistinctPaidUsersBetween(from, to);
        long pendingCount = orderRepository.countOrdersByStatusBetween(OrderStatus.PENDING, from, to);
        long failedCount = orderRepository.countOrdersByStatusBetween(OrderStatus.FAILED, from, to)
                + orderRepository.countOrdersByStatusBetween(OrderStatus.CANCELLED, from, to)
                + orderRepository.countOrdersByStatusBetween(OrderStatus.EXPIRED, from, to);

        BigDecimal aov = paidOrdersCount > 0
                ? totalRevenue.divide(new BigDecimal(paidOrdersCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Top courses by revenue
        List<Object[]> topCoursesData = orderRepository.findTopCoursesByRevenueBetween(from, to, PageRequest.of(0, 5));
        List<TopCourseRevenueItem> topCourses = new ArrayList<>();
        for (Object[] row : topCoursesData) {
            topCourses.add(TopCourseRevenueItem.builder()
                    .courseId((UUID) row[0])
                    .courseTitle((String) row[1])
                    .revenue(row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO)
                    .purchaseCount(row[3] != null ? (Long) row[3] : 0L)
                    .build());
        }

        // Timeline aggregation
        List<RevenueTimelineItem> timeline = new ArrayList<>();
        boolean isGroupByMonth = "MONTH".equalsIgnoreCase(groupBy);

        if (isGroupByMonth) {
            LocalDate current = from.toLocalDate().withDayOfMonth(1);
            LocalDate endMonth = to.toLocalDate().withDayOfMonth(1);
            while (!current.isAfter(endMonth)) {
                LocalDateTime startM = current.atStartOfDay();
                LocalDateTime endM = current.plusMonths(1).minusDays(1).atTime(LocalTime.MAX);

                BigDecimal mRev = orderRepository.sumRevenueBetween(startM, endM);
                long mCount = orderRepository.countPaidOrdersBetween(startM, endM);

                timeline.add(RevenueTimelineItem.builder()
                        .label(current.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                        .revenue(mRev != null ? mRev : BigDecimal.ZERO)
                        .paidOrdersCount(mCount)
                        .build());
                current = current.plusMonths(1);
            }
        } else { // Group by DAY
            LocalDate current = from.toLocalDate();
            LocalDate endDate = to.toLocalDate();
            while (!current.isAfter(endDate)) {
                LocalDateTime startD = current.atStartOfDay();
                LocalDateTime endD = current.atTime(LocalTime.MAX);

                BigDecimal dRev = orderRepository.sumRevenueBetween(startD, endD);
                long dCount = orderRepository.countPaidOrdersBetween(startD, endD);

                timeline.add(RevenueTimelineItem.builder()
                        .label(current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .revenue(dRev != null ? dRev : BigDecimal.ZERO)
                        .paidOrdersCount(dCount)
                        .build());
                current = current.plusDays(1);
            }
        }

        return RevenueReportResponse.builder()
                .totalRevenue(totalRevenue)
                .totalPaidOrders(paidOrdersCount)
                .totalStudentsCount(distinctStudentsCount)
                .averageOrderValue(aov)
                .pendingOrdersCount(pendingCount)
                .failedOrdersCount(failedCount)
                .revenueTimeline(timeline)
                .topCourses(topCourses)
                .build();
    }

    private void upgradeOrCreateEnrollmentToActive(CourseOrder order) {
        Course course = order.getCourse();
        String email = order.getUserEmailSnapshot();
        UUID userId = order.getUserId();

        Optional<Enrollment> existingOpt = enrollmentRepository.findByStudentEmailAndCourseId(email, course.getId());
        if (existingOpt.isPresent()) {
            Enrollment existing = existingOpt.get();
            existing.setStatus(EnrollmentStatus.ACTIVE);
            enrollmentRepository.save(existing);
            log.info("Upgraded existing enrollment [{}] for user [{}] to ACTIVE", existing.getId(), email);
        } else {
            int totalLessonsCount = (int) lessonRepository.countByCourseId(course.getId());
            String studentName = email.contains("@") ? email.split("@")[0] : email;

            Enrollment newEnrollment = Enrollment.builder()
                    .userId(userId)
                    .studentEmail(email)
                    .studentName(studentName)
                    .course(course)
                    .progress(0)
                    .completedLessons(0)
                    .totalLessons(totalLessonsCount)
                    .completed(false)
                    .status(EnrollmentStatus.ACTIVE)
                    .enrolledAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            enrollmentRepository.save(newEnrollment);
            log.info("Created new ACTIVE enrollment for user [{}] on course [{}]", email, course.getTitle());
        }
    }

    private PaymentGateway getGateway(String provider) {
        if ("MOCK".equalsIgnoreCase(provider)) {
            return mockPaymentGateway;
        }
        return vnPayPaymentGateway;
    }

    private OrderResponse mapToOrderResponse(CourseOrder entity) {
        if (entity == null) return null;
        return OrderResponse.builder()
                .id(entity.getId())
                .orderCode(entity.getOrderCode())
                .userId(entity.getUserId())
                .userEmailSnapshot(entity.getUserEmailSnapshot())
                .courseId(entity.getCourse() != null ? entity.getCourse().getId() : null)
                .courseTitleSnapshot(entity.getCourseTitleSnapshot())
                .originalPrice(entity.getOriginalPrice())
                .discountAmount(entity.getDiscountAmount())
                .totalAmount(entity.getTotalAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .paymentProvider(entity.getPaymentProvider())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .paidAt(entity.getPaidAt())
                .build();
    }
}
