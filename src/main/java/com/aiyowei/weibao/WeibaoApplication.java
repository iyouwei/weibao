package com.aiyowei.weibao;

import com.aiyowei.weibao.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class WeibaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeibaoApplication.class, args);
    }

}
