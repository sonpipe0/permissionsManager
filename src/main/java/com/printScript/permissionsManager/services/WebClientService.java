package com.printScript.permissionsManager.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebClientService {

    private WebClient webClient;
    private final String snippetServiceUrl;
    private final String printScriptUrl;

    public WebClientService() {
        this.snippetServiceUrl = System.getenv("SNIPPET_SERVICE_URL");
        this.printScriptUrl = System.getenv("PRINT_SCRIPT_SERVICE_URL");
        this.webClient = WebClient.builder().build();
    }

    public WebClientService snippetServiceWebClient() {
        this.webClient = WebClient.builder().baseUrl(snippetServiceUrl).build();
        return this;
    }

    public WebClientService printScriptWebClient() {
        this.webClient = WebClient.builder().baseUrl(printScriptUrl).build();
        return this;
    }

    public <T, W> Mono<W> post(String path, T body, Class<W> response) {
        return webClient.post()
                .uri(path)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(response);
    }

    public <T> Mono<T> get(String path, Class<T> response) {
        return webClient.get()
                .uri(path)
                .retrieve()
                .bodyToMono(response);
    }
}
