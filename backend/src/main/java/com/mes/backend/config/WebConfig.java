package com.mes.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 설정
 * CORS(Cross-Origin Resource Sharing) 정책 설정
 * 
 * 프론트엔드(React)와 백엔드(Spring Boot)가 다른 포트에서 실행되므로
 * CORS 설정이 필요함
 */
@Configuration   // 
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로 허용
                .allowedOrigins("http://localhost:5173") // 프론트 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP 메서드
                .allowCredentials(true);
    }
}