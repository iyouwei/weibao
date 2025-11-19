package com.aiyowei.weibao.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmsLoginRequest {
    @NotBlank
    private String phone;
    @NotBlank
    private String captcha;
}

