package com.printScript.permissionsManager.config;

import com.printScript.permissionsManager.services.WebClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
