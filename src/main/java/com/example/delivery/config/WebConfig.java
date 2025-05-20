package com.example.delivery.config;


import com.example.delivery.interceptors.VisitCounterInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final VisitCounterInterceptor visitCounterInterceptor;

    public WebConfig(VisitCounterInterceptor visitCounterInterceptor) {
        this.visitCounterInterceptor = visitCounterInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(visitCounterInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/visits/**",
                        "/error",
                        "/static/**",
                        "/webjars/**",
                        "/favicon.ico"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // React порт
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

