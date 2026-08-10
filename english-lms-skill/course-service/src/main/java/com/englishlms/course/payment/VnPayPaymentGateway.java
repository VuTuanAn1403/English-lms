package com.englishlms.course.payment;

import com.englishlms.course.entity.CourseOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class VnPayPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(VnPayPaymentGateway.class);

    @Value("${vnpay.tmn-code:DEMOTMNCODE}")
    private String tmnCode;

    @Value("${vnpay.hash-secret:DEMOHASHSECRET1234567890ABCDEF}")
    private String hashSecret;

    @Value("${vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String payUrl;

    @Override
    public String getProviderName() {
        return "VNPAY";
    }

    @Override
    public String createPaymentUrl(CourseOrder order, String returnUrl) {
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        
        long amountInCents = order.getTotalAmount().multiply(new java.math.BigDecimal(100)).longValue();
        vnpParams.put("vnp_Amount", String.valueOf(amountInCents));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", order.getOrderCode());
        vnpParams.put("vnp_OrderInfo", "Thanh toan khoa hoc: " + order.getOrderCode());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_IpAddr", "127.0.0.1");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        java.time.ZoneId vnZone = java.time.ZoneId.of("Asia/Ho_Chi_Minh");
        java.time.ZonedDateTime createDateVn = java.time.ZonedDateTime.now(vnZone);
        java.time.ZonedDateTime expireDateVn = createDateVn.plusMinutes(15);

        vnpParams.put("vnp_CreateDate", createDateVn.format(formatter));
        vnpParams.put("vnp_ExpireDate", expireDateVn.format(formatter));

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnpParams.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        log.info("Generated VNPay payment URL for order [{}]", order.getOrderCode());
        return payUrl + "?" + query.toString();
    }

    @Override
    public boolean verifyCallback(Map<String, String> queryParams) {
        if (queryParams == null || !queryParams.containsKey("vnp_SecureHash")) {
            return false;
        }

        String vnpSecureHash = queryParams.get("vnp_SecureHash");
        Map<String, String> fields = new HashMap<>();

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && !key.isEmpty() && !key.equals("vnp_SecureHash") && !key.equals("vnp_SecureHashType")) {
                fields.put(key, value);
            }
        }

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }

        String calculatedHash = hmacSHA512(hashSecret, hashData.toString());
        return calculatedHash.equalsIgnoreCase(vnpSecureHash);
    }

    @Override
    public String extractProviderTransactionId(Map<String, String> queryParams) {
        return queryParams != null ? queryParams.get("vnp_TransactionNo") : null;
    }

    @Override
    public String extractResponseCode(Map<String, String> queryParams) {
        return queryParams != null ? queryParams.get("vnp_ResponseCode") : null;
    }

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
