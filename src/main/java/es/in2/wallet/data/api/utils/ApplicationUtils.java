package es.in2.wallet.data.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ApplicationUtils {
    private final WebClient webClient;

    public ApplicationUtils(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<String> getRequest(String url, List<Map.Entry<String, String>> headers) {
        return webClient.get()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK, clientResponse ->
                        Mono.error(new RuntimeException("Error during get request:" + clientResponse.statusCode())))
                .bodyToMono(String.class)
                .doOnNext(response -> logCRUD(url, headers, "", response, "GET"));
    }

    public Mono<String> postRequest(String url, List<Map.Entry<String, String>> headers, String body) {
        return webClient.post()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK, clientResponse ->
                        Mono.error(new RuntimeException("Error during post request:" + clientResponse.statusCode())))
                .bodyToMono(String.class)
                .doOnNext(response -> logCRUD(url, headers, body, response, "POST"));
    }

    public Mono<String> patchRequest(String url, List<Map.Entry<String, String>> headers, String body) {
        return webClient.patch()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status != HttpStatus.NO_CONTENT, clientResponse ->
                        Mono.error(new RuntimeException("Error during patch request:" + clientResponse.statusCode())))
                .bodyToMono(String.class)
                .doOnNext(response -> logCRUD(url, headers, body, response, "PATCH"));
    }

    private void logCRUD(String url, List<Map.Entry<String, String>> headers, String requestBody, String responseBody, String method) {
        log.debug("********************************************************************************");
        log.debug(">>> METHOD: {}", method);
        log.debug(">>> URI: {}", url);
        log.debug(">>> HEADERS: {}", headers);
        log.debug(">>> BODY: {}", requestBody);
        log.debug("<<< BODY: {}", responseBody);
        log.debug("********************************************************************************");
    }
}
