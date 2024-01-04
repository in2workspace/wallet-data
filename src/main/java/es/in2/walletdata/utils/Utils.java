package es.in2.walletdata.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Slf4j
public class Utils {
    private Utils(){
        throw new IllegalStateException("Utility class");
    }
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String VC_JWT = "vc_jwt";
    public static final String VC_JSON = "vc_json";
    public static final String PROPERTY_TYPE = "Property";
    public static final String INVALID_AUTH_HEADER = "Invalid Authorization header";
    public static final String BEARER = "Bearer ";
    public static final String CREDENTIAL_SUBJECT = "credentialSubject";

    public static final String GLOBAL_ENDPOINTS_API = "/api/v2/*";
    public static final String ALLOWED_METHODS = "*";

    // Choosing a factory method to instantiate WebClient with specific base URLs enhances flexibility,
    // preserves immutability and thread safety, prevents shared state issues, and eases code maintenance and scalability.
    // This approach is particularly beneficial in environments with variable base URLs,
    // ensuring a more robust and adaptable software design.
    private static WebClient createWebClient(String baseUrl) {
        //Configure the WebClient as you need
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
    public static Mono<String> getRequest(String domain, String url, List<Map.Entry<String, String>> headers) {
        WebClient webClient = createWebClient(domain);
        return webClient
                .get()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK, clientResponse ->
                        Mono.error(new RuntimeException("Error during get request:" + clientResponse.statusCode())))
                .bodyToMono(String.class)
                .doOnNext(response -> logCRUD(url, headers, "", response, "GET"));
    }

    public static Mono<String> postRequest(String domain, String url, List<Map.Entry<String, String>> headers, String body) {
        WebClient webClient = createWebClient(domain);
        return webClient
                .post()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status != HttpStatus.CREATED && status != HttpStatus.NO_CONTENT, clientResponse ->
                        Mono.error(new RuntimeException("Error during post request:" + clientResponse.statusCode())))
                .bodyToMono(String.class)
                .doOnNext(response -> logCRUD(url, headers, body, response, "POST"));
    }

    public static Mono<String> patchRequest(String domain, String url, List<Map.Entry<String, String>> headers, String body) {
        WebClient webClient = createWebClient(domain);
        return webClient
                .patch()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status != HttpStatus.NO_CONTENT, clientResponse ->
                        Mono.error(new RuntimeException("Error during patch request:" + clientResponse.statusCode())))
                .bodyToMono(String.class)
                .doOnNext(response -> logCRUD(url, headers, body, response, "PATCH"));
    }
    public static Mono<String> deleteRequest(String domain,String url, List<Map.Entry<String, String>> headers) {
        WebClient webClient = createWebClient(domain);
        return webClient.delete()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK && status != HttpStatus.NO_CONTENT, clientResponse ->
                        Mono.error(new RuntimeException("Error during delete request: " + clientResponse.statusCode())))
                .bodyToMono(String.class)
                .doOnNext(response -> logCRUD(url, headers, "", response, "DELETE"));
    }

    public static void logCRUD(String url, List<Map.Entry<String, String>> headers, String requestBody, String responseBody, String method) {
        log.debug("********************************************************************************");
        log.debug(">>> METHOD: {}", method);
        log.debug(">>> URI: {}", url);
        log.debug(">>> HEADERS: {}", headers);
        log.debug(">>> BODY: {}", requestBody);
        log.debug("<<< BODY: {}", responseBody);
        log.debug("********************************************************************************");
    }
    public static Mono<String> getUserIdFromToken(String authorizationHeader) {
        return Mono.just(authorizationHeader)
                .filter(header -> header.startsWith(BEARER))
                .map(header -> header.substring(7))
                .flatMap(token -> {
                    try {
                        SignedJWT parsedVcJwt = SignedJWT.parse(token);
                        JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
                        return Mono.just(jsonObject.get("sub").asText());
                    } catch (ParseException | JsonProcessingException e) {
                        return Mono.error(e);
                    }
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER)));
    }
}
