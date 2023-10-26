package es.in2.wallet.data.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wallet.data.api.exception.*;
import es.in2.wallet.data.api.model.*;
import es.in2.wallet.data.api.service.OrionLDService;
import es.in2.wallet.data.api.utils.ApplicationUtils;
import es.in2.wallet.data.api.utils.DidMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.*;

import static es.in2.wallet.data.api.utils.ApiUtils.*;

@Service
public class OrionLDServiceImpl implements OrionLDService {
    private final ApplicationUtils applicationUtils;
    private final String contextBrokerEntitiesURL;

    @Autowired
    public OrionLDServiceImpl(ApplicationUtils applicationUtils, @Value("${app.url.orion-ld_context_broker}") String contextBrokerEntitiesURL) {
        this.applicationUtils = applicationUtils;
        this.contextBrokerEntitiesURL = contextBrokerEntitiesURL;
    }
    private static final Logger log = LoggerFactory.getLogger(OrionLDServiceImpl.class);

    @Override
    public Mono<Void> saveVC(String vcJwt, String userId) {
        return extractVcJsonFromVcJwt(vcJwt)
                .flatMap(vcJson ->
                        Mono.zip(
                                Mono.just(vcJson),
                                extractVerifiableCredentialIdFromVcJson(vcJson)
                        )
                )
                .flatMap(tuple -> {
                    JsonNode vcJson = tuple.getT1();
                    String vcId = tuple.getT2();
                    return getUserEntityFromContextBroker(userId)
                            .flatMap(userEntity -> {
                                VCAttribute newVCJwt = new VCAttribute(vcId, VC_JWT, vcJwt);
                                VCAttribute newVCJson = new VCAttribute(vcId, VC_JSON, vcJson);
                                List<VCAttribute> updatedVCs = new ArrayList<>(userEntity.getVcs().getValue());
                                updatedVCs.add(newVCJwt);
                                updatedVCs.add(newVCJson);
                                EntityAttribute<List<VCAttribute>> vcs = new EntityAttribute<>(userEntity.getVcs().getType(), updatedVCs);
                                UserEntity updatedUserEntity = new UserEntity(userEntity.getId(), userEntity.getType(), userEntity.getUserData(), userEntity.getDids(), vcs);
                                return updateUserEntityInContextBroker(updatedUserEntity, userId);
                            });
                })
                .doOnSuccess(v -> log.info("Verifiable Credential saved successfully for user: {}", userId))
                .then(); // Ignore the response body and complete the Mono when the request is done
    }



    private Mono<JsonNode> extractVcJsonFromVcJwt(String vcJwt) {
        return Mono.fromCallable(() -> SignedJWT.parse(vcJwt))
                .onErrorMap(ParseException.class, e -> new ParseErrorException("Error while parsing VC JWT: " + e.getMessage()))
                .handle((parsedVcJwt, sink) -> {
                    try {
                        JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
                        JsonNode vcJson = jsonObject.get("vc");
                        if (vcJson != null) {
                            log.debug("Verifiable Credential JSON extracted from VC JWT: {}", vcJson);
                            sink.next(vcJson);
                        } else {
                            sink.error(new ParseErrorException("VC JSON is missing in the payload"));
                        }
                    } catch (JsonProcessingException e) {
                        sink.error(new ParseErrorException("Error while processing JSON: " + e.getMessage()));
                    }
                });
    }



    private Mono<String> extractVerifiableCredentialIdFromVcJson(JsonNode vcJson) {
        return Mono.justOrEmpty(vcJson.get("id").asText())
                .flatMap(vcId -> {
                    if (vcId == null || vcId.trim().isEmpty()) {
                        log.error("The Verifiable Credential does not contain an ID.");
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "The Verifiable Credential does not contain an ID."));
                    }
                    log.debug("Verifiable Credential ID extracted: {}", vcId);
                    return Mono.just(vcId);
                });
    }
    @Override
    public Mono<List<VcBasicDataDTO>> getUserVCsInJson(String userId) {
        return getVerifiableCredentialsByUserIdAndFormat(VC_JSON, userId)
                .flatMapMany(Flux::fromIterable)
                .flatMap(item -> {
                    LinkedHashMap<?, ?> vcDataValue = (LinkedHashMap<?, ?>) item.getValue();
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.convertValue(vcDataValue, JsonNode.class);

                    return getVcTypeListFromVcJson(jsonNode)
                            .map(vcTypeList -> new VcBasicDataDTO(
                                    item.getId(),
                                    vcTypeList,
                                    jsonNode.get("credentialSubject")
                            ));
                })
                .collectList()
                .onErrorResume(NoSuchVerifiableCredentialException.class, Mono::error);
    }


    @Override
    public Mono<List<VcBasicDataDTO>> getSelectableVCsByVcTypeList(List<String> vcTypeList, String userId) {
        return getVerifiableCredentialsByUserIdAndFormat(VC_JSON, userId)
                .flatMapMany(Flux::fromIterable)
                .flatMap(item -> {
                    // Parse the VC stored into a JsonNode object
                    LinkedHashMap<?, ?> vcDataValue = (LinkedHashMap<?, ?>) item.getValue();
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.convertValue(vcDataValue, JsonNode.class);

                    // Create a Mono<List<String>> of the VC types
                    return getVcTypeListFromVcJson(jsonNode)
                            .flatMap(vcDataTypeList -> {
                                // If vc_types matches with vc_types requested, build VcBasicDataDTO and return it wrapped in a Mono
                                if (new HashSet<>(vcDataTypeList).containsAll(vcTypeList)) {
                                    VcBasicDataDTO dto = new VcBasicDataDTO(
                                            jsonNode.get("id").asText(),
                                            vcDataTypeList,
                                            jsonNode.get("credentialSubject")
                                    );
                                    return Mono.just(dto);
                                } else {
                                    return Mono.empty();
                                }
                            });
                })
                .collectList()
                .flatMap(result -> {
                    if (result.isEmpty()) {
                        return Mono.error(new NoSuchVerifiableCredentialException("No matching VC found"));
                    } else {
                        return Mono.just(result);
                    }
                })
                .onErrorResume(FailedCommunicationException.class, Mono::error);
    }

    @Override
    public Mono<Void> deleteVerifiableCredential(String id, String userId) {
        // Fetch current user entity from Context Broker
        return getUserEntityFromContextBroker(userId)
                .flatMap(userEntity -> {
                    List<VCAttribute> originalVCs = userEntity.getVcs().getValue();
                    List<VCAttribute> updatedVCs = originalVCs.stream()
                            .filter(vcAttribute -> !vcAttribute.getId().equals(id))
                            .toList();

                    if (originalVCs.size() == updatedVCs.size()) {
                        return Mono.error(new NoSuchVerifiableCredentialException("VC not found: " + id));
                    }

                    return Mono.just(new UserEntity(
                            userEntity.getId(),
                            userEntity.getType(),
                            userEntity.getUserData(),
                            userEntity.getDids(),
                            new EntityAttribute<>(userEntity.getVcs().getType(), updatedVCs)
                    ));
                })
                .flatMap(updatedUserEntity -> updateUserEntityInContextBroker(updatedUserEntity, userId))
                .doOnSuccess(unused -> log.info("Verifiable Credential with ID: {} deleted successfully for user: {}", id, userId))
                .onErrorResume(e -> {
                    log.error("Error while deleting Verifiable Credential for userId: " + userId, e);
                    return Mono.error(e); // Re-throw the error
                });
    }



    @Override
    public Mono<List<VCAttribute>> getVerifiableCredentialsByUserIdAndFormat(String format, String userId) {
        // Fetch current user entity from Context Broker
        return getUserEntityFromContextBroker(userId)
                .map(userEntity -> userEntity.getVcs().getValue().stream()
                        .filter(vcAttribute -> vcAttribute.getType().equals(format))
                        .toList())
                .onErrorResume(e -> {
                    log.error("Error while fetching Verifiable Credentials for userId: {}", userId, e);
                    return Mono.error(new NoSuchVerifiableCredentialException("Error retrieving Verifiable Credentials for userId: " + userId));
                });
    }


    private Mono<List<String>> getVcTypeListFromVcJson(JsonNode jsonNode) {
        // Initialize an empty list to store the types.
        List<String> result = new ArrayList<>();

        // Check if the "type" field is present and is an array.
        if (jsonNode.has("type") && jsonNode.get("type").isArray()) {
            // Iterate through the array elements and add them to the result list.
            jsonNode.get("type").forEach(node -> result.add(node.asText()));
            // Return the result list wrapped in a Mono.
            return Mono.just(result);
        } else {
            // Log a warning or throw an exception if the "type" field is not present or is not an array.
            return Mono.error(new IllegalStateException("The 'type' field is missing or is not an array in the provided JSON node."));
        }
    }

    @Override
    public Mono<String> getVerifiableCredentialByIdAndFormat(String id, String format, String userId) {
        return getUserEntityFromContextBroker(userId)
                .flatMap(userEntity -> {
                    VCAttribute vcAttribute = null;
                    for (VCAttribute vc : userEntity.getVcs().getValue()) {
                        if (vc.getId().equals(id) && vc.getType().equals(format)) {
                            vcAttribute = vc;
                            break;
                        }
                    }

                    if (vcAttribute == null) {
                        return Mono.error(new NoSuchElementException("No VCAttribute found for id " + id + " and format " + format + " for user " + userId));
                    }

                    // Convert the 'value' of VCAttribute to String
                    Object value = vcAttribute.getValue();
                    if (value instanceof String string) {
                        return Mono.just(string);
                    } else {
                        // Convert complex objects to String (JSON format) using ObjectMapper
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            String result = objectMapper.writeValueAsString(value);
                            return Mono.just(result);
                        } catch (JsonProcessingException e) {
                            return Mono.error(e);
                        }
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error while getting Verifiable Credentials for userId: " + userId, e);
                    return Mono.error(new NoSuchVerifiableCredentialException("Error while getting Verifiable Credential for userId: " + userId));
                });
    }


    @Override
    public Mono<Void> saveDid(String did, DidMethods didMethod, String userId) {
        return getUserEntityFromContextBroker(userId)
                .flatMap(userEntity -> {
                    // Create new DidAttribute for the provided DID
                    DidAttribute newDid = new DidAttribute(didMethod.getStringValue(), did);

                    // Add the new DID to the list of existing DIDs
                    List<DidAttribute> updatedDids = new ArrayList<>(userEntity.getDids().getValue());
                    updatedDids.add(newDid);

                    // Construct the updated user entity
                    EntityAttribute<List<DidAttribute>> dids = new EntityAttribute<>(PROPERTY_TYPE, updatedDids);
                    UserEntity updatedUserEntity = new UserEntity(
                            userEntity.getId(),
                            userEntity.getType(),
                            userEntity.getUserData(),
                            dids,
                            userEntity.getVcs()
                    );

                    // Update the user entity back to the Context Broker with the new DID
                    return updateUserEntityInContextBroker(updatedUserEntity, userId);
                })
                .doOnSuccess(aVoid -> log.info("DID saved successfully for user: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error while saving did for userId: " + userId, e);
                    return Mono.error(new NoSuchUserEntity("Error while saving did for userId: " + userId));
                })
                .then();
    }

    @Override
    public Mono<List<String>> getDidsByUserId(String userId) {
        // Fetch the current user entity from the Context Broker
        return getUserEntityFromContextBroker(userId)
                // Transform the UserEntity to extract the DIDs
                .map(userEntity -> userEntity.getDids().getValue().stream()
                        .map(DidAttribute::getValue)
                        .toList()
                )
                // Log the operation result
                .doOnSuccess(dids -> log.info("Fetched DIDs for user: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error while getting dids for userId: " + userId, e);
                    return Mono.error(new NoSuchUserEntity("Error while getting dids for userId: " + userId));
                });
    }

    @Override
    public Mono<Void> deleteSelectedDid(String did, String userId) {
        // Fetch the current user entity from the Context Broker
        return getUserEntityFromContextBroker(userId)
                // Transform the UserEntity to remove the specific DID
                .flatMap(userEntity -> {
                    List<DidAttribute> originalDids = userEntity.getDids().getValue();
                    List<DidAttribute> updatedDids = originalDids.stream()
                            .filter(didAttr -> !didAttr.getValue().equals(did))
                            .toList();
                    if (originalDids.size() == updatedDids.size()) {
                        return Mono.error(new NoSuchDidException("DID not found: " + did));
                    }

                    return Mono.just(new UserEntity(
                            userEntity.getId(),
                            userEntity.getType(),
                            userEntity.getUserData(),
                            new EntityAttribute<>(PROPERTY_TYPE, updatedDids),
                            userEntity.getVcs()
                    ));
                })
                // Update the user entity in the Context Broker
                .flatMap(updatedUserEntity -> updateUserEntityInContextBroker(updatedUserEntity, userId))
                // Log the operation result
                .doOnSuccess(unused -> log.info("Deleted DID: {} for user: {}", did, userId))
                .onErrorResume(e -> {
                    log.error("Error while deleting did for userId: " + userId, e);
                    return Mono.error(e); // Re-throw the error
                })
                // Return a Mono<Void> to indicate the completion of the operation
                .then();
    }



    @Override
    public Mono<UserAttribute> getUserDataByUserId(String userId) {
        // Fetch the current user entity from the Context Broker
        return getUserEntityFromContextBroker(userId)
                // Extract the UserData from the user entity
                .map(UserEntity::getUserData)
                .map(EntityAttribute::getValue)
                .doOnSuccess(userAttribute -> log.debug("Fetched user data for userId: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error while getting user data for userId: " + userId, e);
                    return Mono.error(new NoSuchUserEntity("Error while getting user data for userId: " + userId));
                });
    }
    @Override
    public Mono<Void> registerUserInContextBroker(UserRequestDTO userRequestDTO) {
        UserAttribute userAttribute = new UserAttribute(userRequestDTO.getUsername(), userRequestDTO.getEmail());
        EntityAttribute<UserAttribute> userData = new EntityAttribute<>(PROPERTY_TYPE, userAttribute);

        UserEntity userEntity = new UserEntity(
                "urn:entities:userId:" + userRequestDTO.getUserId(),
                "userEntity",
                userData,
                new EntityAttribute<>(PROPERTY_TYPE, new ArrayList<>()),
                new EntityAttribute<>(PROPERTY_TYPE, new ArrayList<>())
        );

        return storeUserInContextBroker(userEntity)
                .doOnSuccess(aVoid -> log.debug("Entity saved"))
                .onErrorResume(e -> {
                    log.error("Error while registering user: " + userEntity, e);
                    return Mono.error(new FailedCommunicationException("Error while registering user:  " + userEntity));
                });
    }

    // Reactive method to store UserEntity in ContextBroker
    private Mono<Void> storeUserInContextBroker(UserEntity userEntity) {
        // Building the URL for the POST request
        String url = contextBrokerEntitiesURL;

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
    private Mono<UserEntity> getUserEntityFromContextBroker(String userId) {
        // Building the URL for the GET request
        String url = contextBrokerEntitiesURL + "/urn:entities:userId:" + userId;

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

    private Mono<Void> updateUserEntityInContextBroker(UserEntity userEntity, String userId) {
        String url = contextBrokerEntitiesURL + "/urn:entities:userId:" + userId + "/attrs";

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
