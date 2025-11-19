package com.aiyowei.weibao.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinFamilyRequest {
    @NotBlank
    private String code;
}

