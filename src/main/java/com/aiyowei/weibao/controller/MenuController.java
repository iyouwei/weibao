package com.aiyowei.weibao.controller;

import com.aiyowei.weibao.common.ApiResponse;
import com.aiyowei.weibao.common.PageResponse;
import com.aiyowei.weibao.dto.CategoryDto;
import com.aiyowei.weibao.dto.CategoryRequest;
import com.aiyowei.weibao.dto.DishDto;
import com.aiyowei.weibao.dto.DishRequest;
import com.aiyowei.weibao.service.MockDataService;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Validated
public class MenuController {

    private final MockDataService mockDataService;

    @GetMapping("/categories")
    public ApiResponse<List<CategoryDto>> categories() {
        return ApiResponse.success(mockDataService.getCategories());
    }

    @GetMapping("/dishes")
    public ApiResponse<PageResponse<DishDto>> dishes(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        List<DishDto> filtered = mockDataService.getDishes(categoryId, keyword);
        PageResponse<DishDto> pageResponse = new PageResponse<>(filtered, page, size, filtered.size());
        return ApiResponse.success(pageResponse);
    }

    @GetMapping("/dish/{id}")
    public ApiResponse<DishDto> dish(@PathVariable Long id) {
        return mockDataService.getDish(id)
            .map(ApiResponse::success)
            .orElseGet(() -> ApiResponse.failure(404, "菜品不存在"));
    }

    @PostMapping("/admin/category")
    public ApiResponse<CategoryDto> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success("创建成功", mockDataService.createCategory(request));
    }

    @PutMapping("/admin/category/{id}")
    public ApiResponse<CategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success("修改成功", mockDataService.updateCategory(id, request));
    }

    @DeleteMapping("/admin/category/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        mockDataService.deleteCategory(id);
        return ApiResponse.success("删除成功", null);
    }

    @PostMapping("/admin/dish")
    public ApiResponse<DishDto> createDish(@Valid @RequestBody DishRequest request) {
        return ApiResponse.success("创建成功", mockDataService.createDish(request));
    }

    @PutMapping("/admin/dish/{id}")
    public ApiResponse<DishDto> updateDish(@PathVariable Long id, @Valid @RequestBody DishRequest request) {
        return ApiResponse.success("更新成功", mockDataService.updateDish(id, request));
    }

    @DeleteMapping("/admin/dish/{id}")
    public ApiResponse<Void> deleteDish(@PathVariable Long id) {
        mockDataService.deleteDish(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/admin/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.success(mockDataService.dashboard());
    }
}

