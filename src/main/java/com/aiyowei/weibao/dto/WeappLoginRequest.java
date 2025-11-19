package com.aiyowei.weibao.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WeappLoginRequest {
    @NotBlank
    private String code;
    private String avatar;
    private String nickname;
}

