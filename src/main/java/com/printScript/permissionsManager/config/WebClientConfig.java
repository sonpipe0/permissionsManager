package com.printScript.permissionsManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.printScript.permissionsManager.services.WebClientService;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClientService printScriptWebClient() {
        WebClientService webClientService = new WebClientService();
        return webClientService.printScriptWebClient();
    }

    @Bean
    public WebClientService snippetServiceWebClient() {
        WebClientService webClientService = new WebClientService();
        return webClientService.snippetServiceWebClient();
    }
}
