package com.ecommerce.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PaginationConfig {
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableArgumentResolverCustomizer() {
        return resolver -> resolver.setOneIndexedParameters(true);
    }
}
