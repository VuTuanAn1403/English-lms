package com.englishlms.course.payment;

import com.englishlms.course.entity.CourseOrder;

import java.util.Map;

public interface PaymentGateway {

    String getProviderName();

    String createPaymentUrl(CourseOrder order, String returnUrl);

    boolean verifyCallback(Map<String, String> queryParams);

    String extractProviderTransactionId(Map<String, String> queryParams);

    String extractResponseCode(Map<String, String> queryParams);
}
