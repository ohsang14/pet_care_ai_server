package com.ohsang.petcareai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // 1. 'uploads' 폴더의 실제 물리적 경로
    //    System.getProperty("user.dir")는 프로젝트의 루트 디렉토리입니다.
    //    (예: /Users/osanghyeon/Desktop/project/Mypetserver/pet_care_ai_server)
    private final String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 2. '/images/**' URL 요청이 오면
        registry.addResourceHandler("/images/**")

                // 3. 'file:/[프로젝트경로]/uploads/' 폴더에서 파일을 찾아 반환
                .addResourceLocations("file:" + uploadPath);
    }
}