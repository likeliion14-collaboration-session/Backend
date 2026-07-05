package com.likelion_collb.global.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocation = "file:" + uploadDir;
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }
        registry.addResourceHandler("/images/**")
                .addResourceLocations(resourceLocation);
    }
}
