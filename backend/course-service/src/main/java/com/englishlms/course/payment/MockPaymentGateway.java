package com.englishlms.course.payment;

import com.englishlms.course.entity.CourseOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MockPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentGateway.class);

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public String getProviderName() {
        return "MOCK";
    }

    @Override
    public String createPaymentUrl(CourseOrder order, String returnUrl) {
        log.info("Generated MOCK payment URL for order [{}]", order.getOrderCode());
        return frontendUrl + "/payment/mock-checkout?orderCode=" + order.getOrderCode();
    }

    @Override
    public boolean verifyCallback(Map<String, String> queryParams) {
        if (queryParams == null) return false;
        String status = queryParams.get("status");
        return "SUCCESS".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status) || "00".equals(status);
    }

    @Override
    public String extractProviderTransactionId(Map<String, String> queryParams) {
        if (queryParams != null && queryParams.containsKey("transactionId")) {
            return queryParams.get("transactionId");
        }
        return "MOCK_TX_" + System.currentTimeMillis();
    }

    @Override
    public String extractResponseCode(Map<String, String> queryParams) {
        if (queryParams != null && queryParams.containsKey("responseCode")) {
            return queryParams.get("responseCode");
        }
        return "00";
    }
}
