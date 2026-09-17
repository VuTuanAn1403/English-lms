package com.englishlms.course.repository;

import com.englishlms.course.entity.PaymentTransaction;
import com.englishlms.course.entity.PaymentTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    List<PaymentTransaction> findByOrderId(UUID orderId);

    Optional<PaymentTransaction> findByProviderTransactionId(String providerTransactionId);

    boolean existsByOrderIdAndStatus(UUID orderId, PaymentTransactionStatus status);
}
