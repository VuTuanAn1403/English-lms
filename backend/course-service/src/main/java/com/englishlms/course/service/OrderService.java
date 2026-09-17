package com.englishlms.course.service;

import com.englishlms.course.dto.CreateOrderRequest;
import com.englishlms.course.dto.CreatePaymentResponse;
import com.englishlms.course.dto.OrderResponse;
import com.englishlms.course.dto.PageResponse;
import com.englishlms.course.dto.RevenueReportResponse;

import java.util.Map;
import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(String email, CreateOrderRequest request);

    CreatePaymentResponse createPaymentUrl(String orderCode, String email, String returnUrl);

    OrderResponse processPaymentCallback(String provider, Map<String, String> queryParams);

    OrderResponse processMockPayment(String orderCode, String email, String status);

    PageResponse<OrderResponse> getMyOrders(String email, int page, int size);

    OrderResponse getOrderByCode(String orderCode, String email, String role);

    OrderResponse cancelOrder(String orderCode, String email);

    PageResponse<OrderResponse> getAllOrdersForAdmin(int page, int size, String search, String status, String courseIdStr, String fromStr, String toStr);

    RevenueReportResponse getRevenueReport(String fromStr, String toStr, UUID courseId, String groupBy);
}
