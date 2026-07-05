package com.likelion_collb.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("likelion-collab API")
                        .description("멋쟁이 사자처럼 14기 연합세션 api 명세서")
                        .version("v1"));
    }
}
