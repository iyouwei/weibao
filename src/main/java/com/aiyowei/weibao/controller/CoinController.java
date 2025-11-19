package com.aiyowei.weibao.controller;

import com.aiyowei.weibao.common.ApiResponse;
import com.aiyowei.weibao.dto.CoinClaimRequest;
import com.aiyowei.weibao.dto.CoinHistoryDto;
import com.aiyowei.weibao.dto.CoinTaskDto;
import com.aiyowei.weibao.dto.WalletDto;
import com.aiyowei.weibao.service.MockDataService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coin")
@RequiredArgsConstructor
public class CoinController {

    private final MockDataService mockDataService;

    @GetMapping("/balance")
    public ApiResponse<WalletDto> balance() {
        return ApiResponse.success(mockDataService.getWallet());
    }

    @GetMapping("/tasks")
    public ApiResponse<List<CoinTaskDto>> tasks() {
        return ApiResponse.success(mockDataService.getTasks());
    }

    @PostMapping("/claim")
    public ApiResponse<WalletDto> claim(@Valid @RequestBody CoinClaimRequest request) {
        WalletDto wallet = mockDataService.claimTask(request.getTaskCode());
        return ApiResponse.success("领取成功", wallet);
    }

    @GetMapping("/history")
    public ApiResponse<List<CoinHistoryDto>> history() {
        return ApiResponse.success(mockDataService.getHistories());
    }
}


