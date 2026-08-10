package com.englishlms.course.service;

import com.englishlms.course.dto.CourseAccessResponse;
import com.englishlms.course.dto.CreateOrderRequest;
import com.englishlms.course.dto.EnrollmentResponse;
import com.englishlms.course.dto.OrderResponse;
import com.englishlms.course.entity.Course;
import com.englishlms.course.entity.CourseOrder;
import com.englishlms.course.entity.Enrollment;
import com.englishlms.course.entity.EnrollmentStatus;
import com.englishlms.course.entity.Lesson;
import com.englishlms.course.entity.OrderStatus;
import com.englishlms.course.exception.CourseAlreadyOwnedException;
import com.englishlms.course.exception.CoursePurchaseRequiredException;
import com.englishlms.course.exception.InvalidPaymentSignatureException;
import com.englishlms.course.mapper.CourseMapper;
import com.englishlms.course.payment.MockPaymentGateway;
import com.englishlms.course.payment.VnPayPaymentGateway;
import com.englishlms.course.repository.CourseOrderRepository;
import com.englishlms.course.repository.CourseRepository;
import com.englishlms.course.repository.EnrollmentRepository;
import com.englishlms.course.repository.LessonRepository;
import com.englishlms.course.repository.PaymentTransactionRepository;
import com.englishlms.course.service.impl.CourseServiceImpl;
import com.englishlms.course.service.impl.LessonServiceImpl;
import com.englishlms.course.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private CourseOrderRepository orderRepository;
    @Mock private PaymentTransactionRepository paymentTransactionRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private LessonRepository lessonRepository;
    @Mock private CourseMapper courseMapper;
    @Mock private VnPayPaymentGateway vnPayPaymentGateway;
    @Mock private MockPaymentGateway mockPaymentGateway;

    private OrderServiceImpl orderService;
    private CourseServiceImpl courseService;
    private LessonServiceImpl lessonService;

    private Course paidCourse;
    private Course freeCourse;
    private String studentEmail;
    private UUID courseId;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(
                orderRepository, paymentTransactionRepository,
                courseRepository, enrollmentRepository, lessonRepository,
                vnPayPaymentGateway, mockPaymentGateway
        );

        courseService = new CourseServiceImpl(
                courseRepository, lessonRepository, enrollmentRepository,
                null, courseMapper
        );

        lessonService = new LessonServiceImpl(
                lessonRepository, courseRepository, enrollmentRepository,
                null, courseMapper
        );

        studentEmail = "student@gmail.com";
        courseId = UUID.randomUUID();

        paidCourse = Course.builder()
                .id(courseId)
                .title("Paid English Course")
                .price(new BigDecimal("499000.00"))
                .salePrice(new BigDecimal("299000.00"))
                .currency("VND")
                .published(true)
                .build();

        freeCourse = Course.builder()
                .id(UUID.randomUUID())
                .title("Free Starter Course")
                .price(BigDecimal.ZERO)
                .currency("VND")
                .published(true)
                .build();
    }

    // ==================== TRIAL & LESSON ACCESS TESTS ====================

    @Test
    @DisplayName("Trial: Student bắt đầu học thử tạo enrollment trạng thái TRIAL")
    void startTrial_CreatesTrialEnrollment() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(paidCourse));
        when(enrollmentRepository.findByStudentEmailAndCourseId(studentEmail, courseId)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> i.getArgument(0));

        EnrollmentResponse mockResp = EnrollmentResponse.builder()
                .id(UUID.randomUUID())
                .status(EnrollmentStatus.TRIAL)
                .build();
        when(courseMapper.toEnrollmentResponse(any(Enrollment.class))).thenReturn(mockResp);

        EnrollmentResponse resp = courseService.startTrial(courseId, studentEmail, "Student A");

        assertNotNull(resp);
        assertEquals(EnrollmentStatus.TRIAL, resp.getStatus());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Lesson Access: Xem bài thứ 6 của khóa trả phí mà chưa mua -> Ném CoursePurchaseRequiredException (403)")
    void getLessonById_Lesson6ForTrialStudent_ThrowsForbiddenException() {
        UUID lessonId = UUID.randomUUID();
        Lesson lesson6 = Lesson.builder()
                .id(lessonId)
                .course(paidCourse)
                .title("Lesson 6: Advanced")
                .lessonOrder(6)
                .build();

        List<Lesson> lessons = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            lessons.add(Lesson.builder().id(i == 6 ? lessonId : UUID.randomUUID()).course(paidCourse).lessonOrder(i).build());
        }

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson6));
        when(lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId)).thenReturn(lessons);
        when(enrollmentRepository.findByStudentEmailAndCourseId(studentEmail, courseId)).thenReturn(Optional.empty());

        assertThrows(CoursePurchaseRequiredException.class, () ->
                lessonService.getLessonByIdWithAuthorization(lessonId, studentEmail, "ROLE_STUDENT")
        );
    }

    // ==================== ORDER & PAYMENT TESTS ====================

    @Test
    @DisplayName("Order: Giá đơn hàng lấy từ DB, không phụ thuộc frontend")
    void createOrder_UsesEffectivePriceFromDatabase() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(paidCourse));
        when(enrollmentRepository.findByStudentEmailAndCourseId(studentEmail, courseId)).thenReturn(Optional.empty());
        when(orderRepository.findFirstByUserIdAndCourseIdAndStatusAndExpiresAtAfter(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(orderRepository.save(any(CourseOrder.class))).thenAnswer(i -> i.getArgument(0));

        CreateOrderRequest request = CreateOrderRequest.builder()
                .courseId(courseId)
                .paymentProvider("VNPAY")
                .build();

        OrderResponse order = orderService.createOrder(studentEmail, request);

        assertNotNull(order);
        assertEquals(new BigDecimal("299000.00"), order.getTotalAmount());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    @DisplayName("Order: Không cho phép mua lại khóa học đã sở hữu (409 Conflict)")
    void createOrder_AlreadyOwned_ThrowsConflictException() {
        Enrollment activeEnrollment = Enrollment.builder()
                .course(paidCourse)
                .status(EnrollmentStatus.ACTIVE)
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(paidCourse));
        when(enrollmentRepository.findByStudentEmailAndCourseId(studentEmail, courseId))
                .thenReturn(Optional.of(activeEnrollment));

        CreateOrderRequest request = CreateOrderRequest.builder().courseId(courseId).build();

        assertThrows(CourseAlreadyOwnedException.class, () -> orderService.createOrder(studentEmail, request));
    }

    @Test
    @DisplayName("Callback: Chữ ký không hợp lệ -> Ném InvalidPaymentSignatureException")
    void processPaymentCallback_InvalidSignature_ThrowsException() {
        CourseOrder order = CourseOrder.builder()
                .orderCode("LMS123456")
                .totalAmount(new BigDecimal("299000.00"))
                .status(OrderStatus.PENDING)
                .paymentProvider("VNPAY")
                .build();

        when(orderRepository.findByOrderCode("LMS123456")).thenReturn(Optional.of(order));
        when(vnPayPaymentGateway.verifyCallback(any())).thenReturn(false);

        Map<String, String> queryParams = Map.of("vnp_TxnRef", "LMS123456", "vnp_SecureHash", "invalid");

        assertThrows(InvalidPaymentSignatureException.class, () ->
                orderService.processPaymentCallback("VNPAY", queryParams)
        );
    }

    @Test
    @DisplayName("Callback: Callback trùng (Idempotent) -> Trả về đơn hàng PAID mà không tăng doanh thu / tạo trùng enrollment")
    void processPaymentCallback_DuplicatePaidOrder_ReturnsImmediatelyWithoutReProcessing() {
        CourseOrder paidOrder = CourseOrder.builder()
                .orderCode("LMS123456")
                .status(OrderStatus.PAID)
                .totalAmount(new BigDecimal("299000.00"))
                .build();

        when(orderRepository.findByOrderCode("LMS123456")).thenReturn(Optional.of(paidOrder));

        Map<String, String> queryParams = Map.of("vnp_TxnRef", "LMS123456");

        OrderResponse resp = orderService.processPaymentCallback("VNPAY", queryParams);

        assertNotNull(resp);
        assertEquals(OrderStatus.PAID, resp.getStatus());
        verify(enrollmentRepository, never()).save(any());
        verify(paymentTransactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Callback: Thanh toán thành công -> Đơn hàng chuyển PAID và Enrollment nâng lên ACTIVE")
    void processPaymentCallback_SuccessfulPayment_UpgradesEnrollmentToActive() {
        CourseOrder order = CourseOrder.builder()
                .id(UUID.randomUUID())
                .orderCode("LMS123456")
                .userId(UUID.nameUUIDFromBytes(studentEmail.getBytes()))
                .userEmailSnapshot(studentEmail)
                .course(paidCourse)
                .totalAmount(new BigDecimal("299000.00"))
                .currency("VND")
                .status(OrderStatus.PENDING)
                .paymentProvider("VNPAY")
                .build();

        when(orderRepository.findByOrderCode("LMS123456")).thenReturn(Optional.of(order));
        when(vnPayPaymentGateway.verifyCallback(any())).thenReturn(true);
        when(vnPayPaymentGateway.extractResponseCode(any())).thenReturn("00");
        when(vnPayPaymentGateway.getProviderName()).thenReturn("VNPAY");
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("vnp_TxnRef", "LMS123456");
        queryParams.put("vnp_ResponseCode", "00");
        queryParams.put("vnp_Amount", "29900000");

        OrderResponse resp = orderService.processPaymentCallback("VNPAY", queryParams);

        assertNotNull(resp);
        assertEquals(OrderStatus.PAID, resp.getStatus());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }
}
