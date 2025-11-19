package com.aiyowei.weibao.controller;

import com.aiyowei.weibao.common.ApiResponse;
import com.aiyowei.weibao.dto.CartItemRequest;
import com.aiyowei.weibao.dto.OrderDetailDto;
import com.aiyowei.weibao.dto.OrderPreviewRequest;
import com.aiyowei.weibao.dto.OrderPreviewResponse;
import com.aiyowei.weibao.dto.OrderSubmitRequest;
import com.aiyowei.weibao.dto.OrderSummaryDto;
import com.aiyowei.weibao.service.MockDataService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final MockDataService mockDataService;

    @PostMapping("/preview")
    public ApiResponse<OrderPreviewResponse> preview(@Valid @RequestBody OrderPreviewRequest request) {
        syncCart(request.getCartItems());
        OrderPreviewResponse response = mockDataService.previewOrder(currentFamilyId());
        return ApiResponse.success(response);
    }

    @PostMapping("/submit")
    public ApiResponse<OrderDetailDto> submit(@Valid @RequestBody OrderSubmitRequest request) {
        syncCart(request.getCartItems());
        OrderDetailDto order = mockDataService.submitOrder(currentFamilyId(), request.getCoinUse());
        return ApiResponse.success("下单成功", order);
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailDto> detail(@PathVariable Long id) {
        Optional<OrderDetailDto> order = mockDataService.getOrder(id);
        return order
            .map(ApiResponse::success)
            .orElseGet(() -> ApiResponse.failure(404, "订单不存在"));
    }

    @GetMapping("/family")
    public ApiResponse<List<OrderSummaryDto>> familyOrders() {
        return ApiResponse.success(mockDataService.getFamilyOrders(currentFamilyId()));
    }

    private Long currentFamilyId() {
        return mockDataService.getCurrentFamilyId();
    }

    private void syncCart(List<CartItemRequest> items) {
        items.forEach(item -> mockDataService.upsertCartItem(currentFamilyId(), item));
    }
}


