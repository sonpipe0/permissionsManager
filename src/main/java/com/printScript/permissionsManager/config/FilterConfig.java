package com.printScript.permissionsManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.printScript.permissionsManager.filters.CorrelationIdFilter;

@Configuration
public class FilterConfig {

    @Bean
    public CorrelationIdFilter correlationIdFilter() {
        return new CorrelationIdFilter();
    }
}
