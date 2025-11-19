package com.aiyowei.weibao.controller;

import com.aiyowei.weibao.common.ApiResponse;
import com.aiyowei.weibao.dto.AdminStatisticDto;
import com.aiyowei.weibao.dto.UploadResponse;
import com.aiyowei.weibao.service.MockDataService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MockDataService mockDataService;

    @GetMapping("/statistics")
    public ApiResponse<AdminStatisticDto> statistics() {
        return ApiResponse.success(mockDataService.getStatistics());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UploadResponse> upload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename() == null ? file.getName() : file.getOriginalFilename();
        String url = "https://cdn.example.com/" + UUID.randomUUID();
        return ApiResponse.success(new UploadResponse(url, fileName));
    }
}


