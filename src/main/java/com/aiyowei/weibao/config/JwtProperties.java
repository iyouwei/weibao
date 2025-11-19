package com.aiyowei.weibao.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * HMAC 加密密钥，需至少 32 字节。
     */
    private String secret;

    /**
     * 过期时间（秒）。
     */
    private long expiration = 86400;
}


