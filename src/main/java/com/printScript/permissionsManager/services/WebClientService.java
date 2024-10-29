package com.printScript.permissionsManager.services;

import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;

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

    public Mono<JsonNode> get(String path, Consumer<HttpHeaders> headers,
            Function<WebClientResponseException, Mono<JsonNode>> errorHandler, Class<JsonNode> response) {
        return webClient.get().uri(path).headers(headers).retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.createException().flatMap(Mono::error))
                .bodyToMono(response).onErrorResume(WebClientResponseException.class, errorHandler);
    }

    public <T> Mono<JsonNode> postObject(String path, T body, Consumer<HttpHeaders> headers,
            Function<WebClientResponseException, Mono<JsonNode>> errorHandler) {
        return webClient.post().uri(path).bodyValue(body).accept(MediaType.APPLICATION_JSON).headers(headers).retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.createException().flatMap(Mono::error))
                .bodyToMono(JsonNode.class).onErrorResume(WebClientResponseException.class, errorHandler);
    }

    public Mono<JsonNode> uploadMultipart(String path, MultiValueMap<String, Object> multipartData,
            Consumer<HttpHeaders> headers, Function<WebClientResponseException, Mono<JsonNode>> errorHandler) {
        return webClient.post().uri(path).contentType(MediaType.MULTIPART_FORM_DATA).headers(headers)
                .bodyValue(multipartData).retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.createException().flatMap(Mono::error))
                .bodyToMono(JsonNode.class).onErrorResume(WebClientResponseException.class, errorHandler);
    }
}
