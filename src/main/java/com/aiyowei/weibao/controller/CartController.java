package com.aiyowei.weibao.controller;

import com.aiyowei.weibao.common.ApiResponse;
import com.aiyowei.weibao.dto.CartItemDto;
import com.aiyowei.weibao.dto.CartItemRequest;
import com.aiyowei.weibao.service.MockDataService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final MockDataService mockDataService;

    @GetMapping
    public ApiResponse<List<CartItemDto>> getCart() {
        return ApiResponse.success(mockDataService.getCart(currentFamilyId()));
    }

    @PutMapping("/item")
    public ApiResponse<List<CartItemDto>> upsertItem(@Valid @RequestBody CartItemRequest request) {
        return ApiResponse.success("已更新购物车", mockDataService.upsertCartItem(currentFamilyId(), request));
    }

    @DeleteMapping("/item/{dishId}")
    public ApiResponse<List<CartItemDto>> deleteItem(@PathVariable Long dishId) {
        return ApiResponse.success("已移除菜品", mockDataService.removeCartItem(currentFamilyId(), dishId));
    }

    private Long currentFamilyId() {
        return mockDataService.getCurrentFamilyId();
    }
}


