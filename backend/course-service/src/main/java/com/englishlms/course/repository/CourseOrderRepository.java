package com.englishlms.course.repository;

import com.englishlms.course.entity.CourseOrder;
import com.englishlms.course.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseOrderRepository extends JpaRepository<CourseOrder, UUID>, JpaSpecificationExecutor<CourseOrder> {

    Optional<CourseOrder> findByOrderCode(String orderCode);

    Optional<CourseOrder> findByOrderCodeAndUserId(String orderCode, UUID userId);

    Page<CourseOrder> findByUserId(UUID userId, Pageable pageable);

    Optional<CourseOrder> findFirstByUserIdAndCourseIdAndStatusAndExpiresAtAfter(
            UUID userId, UUID courseId, OrderStatus status, LocalDateTime now);

    boolean existsByUserIdAndCourseIdAndStatus(UUID userId, UUID courseId, OrderStatus status);

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM CourseOrder o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM CourseOrder o WHERE o.status = 'PAID' AND o.paidAt BETWEEN :from AND :to")
    BigDecimal sumRevenueBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(o) FROM CourseOrder o WHERE o.status = 'PAID' AND o.paidAt BETWEEN :from AND :to")
    long countPaidOrdersBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(DISTINCT o.userId) FROM CourseOrder o WHERE o.status = 'PAID' AND o.paidAt BETWEEN :from AND :to")
    long countDistinctPaidUsersBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(o) FROM CourseOrder o WHERE o.status = :status AND o.createdAt BETWEEN :from AND :to")
    long countOrdersByStatusBetween(@Param("status") OrderStatus status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT o.course.id AS courseId, o.courseTitleSnapshot AS courseTitle, SUM(o.totalAmount) AS revenue, COUNT(o) AS purchaseCount " +
           "FROM CourseOrder o WHERE o.status = 'PAID' AND o.paidAt BETWEEN :from AND :to " +
           "GROUP BY o.course.id, o.courseTitleSnapshot ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> findTopCoursesByRevenueBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

    @Query("SELECT o.course.id AS courseId, o.courseTitleSnapshot AS courseTitle, SUM(o.totalAmount) AS revenue, COUNT(o) AS purchaseCount " +
           "FROM CourseOrder o WHERE o.status = 'PAID' AND o.paidAt BETWEEN :from AND :to " +
           "GROUP BY o.course.id, o.courseTitleSnapshot ORDER BY COUNT(o) DESC")
    List<Object[]> findTopCoursesByPurchasesBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

    @Query("SELECT o FROM CourseOrder o WHERE o.status = 'PENDING' AND o.expiresAt < :now")
    List<CourseOrder> findExpiredOrders(@Param("now") LocalDateTime now);
}
