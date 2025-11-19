package com.aiyowei.weibao.controller;

import com.aiyowei.weibao.common.ApiResponse;
import com.aiyowei.weibao.dto.FamilyInfoDto;
import com.aiyowei.weibao.dto.InviteRequest;
import com.aiyowei.weibao.dto.JoinFamilyRequest;
import com.aiyowei.weibao.service.FamilyInviteService;
import com.aiyowei.weibao.service.MockDataService;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
public class FamilyController {

    private final MockDataService mockDataService;
    private final FamilyInviteService familyInviteService;

    @PostMapping("/invite")
    public ApiResponse<Map<String, Object>> createInvite(@Valid @RequestBody InviteRequest request) {
        return ApiResponse.success(familyInviteService.createInvite(request.getType()));
    }

    @PostMapping("/join")
    public ApiResponse<Map<String, Object>> joinFamily(@Valid @RequestBody JoinFamilyRequest request) {
        return ApiResponse.success(familyInviteService.joinFamily(request.getCode()));
    }

    @GetMapping("/info")
    public ApiResponse<FamilyInfoDto> familyInfo() {
        return ApiResponse.success(mockDataService.getFamilyInfo());
    }

    @PostMapping("/unbind")
    public ApiResponse<Void> unbind() {
        familyInviteService.unbindCurrentUser();
        return ApiResponse.success("解绑成功", null);
    }
}

