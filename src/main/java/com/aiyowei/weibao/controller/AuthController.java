package com.aiyowei.weibao.controller;

import com.aiyowei.weibao.common.ApiResponse;
import com.aiyowei.weibao.dto.LoginResponse;
import com.aiyowei.weibao.dto.SmsLoginRequest;
import com.aiyowei.weibao.dto.WeappLoginRequest;
import com.aiyowei.weibao.service.JwtService;
import com.aiyowei.weibao.service.MockDataService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MockDataService mockDataService;
    private final JwtService jwtService;

    @PostMapping("/weapp-login")
    public ApiResponse<LoginResponse> weappLogin(@Valid @RequestBody WeappLoginRequest request) {
        LoginResponse response = mockDataService.login(request.getNickname());
        return ApiResponse.success(withJwt(response));
    }

    @PostMapping("/sms-login")
    public ApiResponse<LoginResponse> smsLogin(@Valid @RequestBody SmsLoginRequest request) {
        LoginResponse response = mockDataService.login(null);
        return ApiResponse.success(withJwt(response));
    }

    @GetMapping("/profile")
    public ApiResponse<LoginResponse> profile(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Long userId = jwtService.resolveUserId(authorization);
        if (userId == null) {
            return ApiResponse.failure(401, "未登录");
        }
        LoginResponse response = mockDataService.profile(userId);
        response.setToken(jwtService.generateToken(response.getUser().getId()));
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success("退出成功", null);
    }

    private LoginResponse withJwt(LoginResponse response) {
        if (response == null || response.getUser() == null) {
            return response;
        }
        response.setToken(jwtService.generateToken(response.getUser().getId()));
        return response;
    }
}

