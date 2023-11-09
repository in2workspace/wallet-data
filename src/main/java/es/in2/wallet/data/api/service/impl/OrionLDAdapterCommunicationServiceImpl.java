package es.in2.wallet.data.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wallet.data.api.exception.FailedCommunicationException;
import es.in2.wallet.data.api.exception.NoSuchUserEntity;
import es.in2.wallet.data.api.model.UserEntity;
import es.in2.wallet.data.api.service.OrionLDAdapterCommunicationService;
import es.in2.wallet.data.api.utils.ApplicationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.in2.wallet.data.api.utils.ApiUtils.CONTENT_TYPE;
import static es.in2.wallet.data.api.utils.ApiUtils.CONTENT_TYPE_APPLICATION_JSON;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrionLDAdapterCommunicationServiceImpl implements OrionLDAdapterCommunicationService {
    private final ApplicationUtils applicationUtils;
    @Value("${app.url.orion-ld-adapter}")
    private String orionldAdapterURL;
    @Override
    public Mono<Void> storeUserInContextBroker(UserEntity userEntity) {
        // Building the URL for the POST request
        String url = orionldAdapterURL + "/api/v1/publish";

        // Preparing request headers
        List<Map.Entry<String, String>> headers = new ArrayList<>();
        headers.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));

        // Transforming the UserEntity to JSON String
        return Mono.fromCallable(() -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userEntity);
                })
                .doOnNext(log::info) // Logging the request body
                // Performing the POST request
                .flatMap(requestBody -> applicationUtils.postRequest(url, headers, requestBody))
                // Handling errors, if any
                .onErrorResume(e -> {
                    log.error("Error while creating UserEntity in ContextBroker for userId: {}", userEntity.getId(), e);
                    return Mono.error(new FailedCommunicationException("Error creating UserEntity for userId: " + userEntity.getId()));
                })
                .then(); // Converting the result to Mono<Void> to signify completion
    }

    // Method to get UserEntity from ContextBroker reactively
    @Override
    public Mono<UserEntity> getUserEntityFromContextBroker(String userId) {
        // Building the URL for the GET request
        String url = orionldAdapterURL + "/api/v1/entities/urn:entities:userId:" + userId;

        // Using the ApplicationUtils class to perform the GET request
        return applicationUtils.getRequest(url, new ArrayList<>())
                // Using flatMap to transform the response into a UserEntity
                .flatMap(response -> {
                    try {
                        // Setting up ObjectMapper for deserialization
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

                        // Deserializing the response into a UserEntity
                        UserEntity userEntity = objectMapper.readValue(response, UserEntity.class);
                        log.debug("User Entity: {}", userEntity);

                        // Returning the UserEntity wrapped in a Mono
                        return Mono.just(userEntity);
                    } catch (Exception e) {
                        // Logging and returning an error Mono if deserialization fails
                        log.error("Error while deserializing UserEntity for userId: " + userId, e);
                        return Mono.error(new FailedCommunicationException("Error deserializing UserEntity for userId: " + userId));
                    }
                })
                // Using onErrorResume to handle other errors that may occur during the GET request
                .onErrorResume(e -> {
                    log.error("Error while fetching UserEntity from ContextBroker for userId: " + userId, e);
                    return Mono.error(new NoSuchUserEntity("Error retrieving UserEntity for userId: " + userId));
                });
    }

    @Override
    public Mono<Void> updateUserEntityInContextBroker(UserEntity userEntity, String userId) {
        String url = orionldAdapterURL + "/api/v1/update";

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userEntity);
        } catch (JsonProcessingException e) {
            log.error("Error while updating UserEntity in ContextBroker for userId: " + userId, e);
            return Mono.error(new FailedCommunicationException("Error updating UserEntity in ContextBroker for userId: " + userId));
        }

        List<Map.Entry<String, String>> headers = new ArrayList<>();
        headers.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));

        return applicationUtils.patchRequest(url, headers, requestBody)
                .then() // Ignore the response body and complete the Mono when the request is done
                .onErrorResume(e -> {
                    log.error("Error while updating user entity: " + userEntity, e);
                    return Mono.error(new FailedCommunicationException("Error while updating user entity:  " + userEntity));
                })
                .doOnSuccess(aVoid -> log.info("UserEntity updated successfully for userId: {}", userId));
    }
}
