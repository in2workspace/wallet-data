package es.in2.walletdata.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import es.in2.walletdata.domain.*;
import es.in2.walletdata.exception.FailedCommunicationException;
import es.in2.walletdata.exception.NoSuchDidException;
import es.in2.walletdata.exception.NoSuchVerifiableCredentialException;
import es.in2.walletdata.exception.ParseErrorException;
import es.in2.walletdata.service.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.*;

import static es.in2.walletdata.utils.Utils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<UserEntity> saveVC(UserEntity userEntity, String vcJwt) {
        // Extract the JSON content from the VC JWT.
        return extractVcJsonFromVcJwt(vcJwt)
                .flatMap(vcJson -> extractVerifiableCredentialIdFromVcJson(vcJson)
                        .map(vcId -> {
                            // Create new VCAttributes for both the VC JWT and its JSON content.
                            VCAttribute newVCJwt = new VCAttribute(vcId, VC_JWT, vcJwt);
                            VCAttribute newVCJson = new VCAttribute(vcId, VC_JSON, vcJson);

                            // Update the list of VCAttributes in the UserEntity.
                            List<VCAttribute> updatedVCs = new ArrayList<>(userEntity.vcs().value());
                            updatedVCs.add(newVCJwt);
                            updatedVCs.add(newVCJson);

                            // Create a new EntityAttribute for the updated list of VCAttributes.
                            EntityAttribute<List<VCAttribute>> vcs = new EntityAttribute<>(userEntity.vcs().type(), updatedVCs);

                            // Return the updated UserEntity with the new list of VCAttributes.
                            return new UserEntity(userEntity.id(), userEntity.type(), userEntity.userData(), userEntity.dids(), vcs);
                        }))
                // Log a success message when the VC has been successfully added to the UserEntity.
                .doOnSuccess(updatedUserEntity -> log.info("Verifiable Credential saved successfully for user: {}", updatedUserEntity.id()));
    }

    private Mono<JsonNode> extractVcJsonFromVcJwt(String vcJwt) {
        return Mono.fromCallable(() -> SignedJWT.parse(vcJwt))
                .onErrorMap(ParseException.class, e -> new ParseErrorException("Error while parsing VC JWT: " + e.getMessage()))
                .handle((parsedVcJwt, sink) -> {
                    try {
                        JsonNode jsonObject = objectMapper.readTree(parsedVcJwt.getPayload().toString());
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
    public Mono<List<VcBasicData>> getUserVCsInJson(UserEntity userEntity) {
        return Mono.just(userEntity)
                .flatMapMany(user -> Flux.fromIterable(user.vcs().value()))
                .filter(vcAttribute -> VC_JSON.equals(vcAttribute.type()))
                .flatMap(item -> {
                    LinkedHashMap<?, ?> vcDataValue = (LinkedHashMap<?, ?>) item.value();
                    JsonNode jsonNode = objectMapper.convertValue(vcDataValue, JsonNode.class);

                    return getVcTypeListFromVcJson(jsonNode)
                            .map(vcTypeList -> new VcBasicData(
                                    item.id(),
                                    vcTypeList,
                                    jsonNode.get(CREDENTIAL_SUBJECT)
                            ));
                })
                .collectList()
                .onErrorResume(NoSuchVerifiableCredentialException.class, Mono::error);
    }

    @Override
    public Mono<List<VcBasicData>> getSelectableVCsByVcTypeList(List<String> vcTypeList, UserEntity userEntity) {
        return getVerifiableCredentialsByFormat(userEntity, VC_JSON)
                .flatMapMany(Flux::fromIterable)
                .flatMap(item -> {
                    // Parse the VC stored into a JsonNode object
                    LinkedHashMap<?, ?> vcDataValue = (LinkedHashMap<?, ?>) item.value();
                    JsonNode jsonNode = objectMapper.convertValue(vcDataValue, JsonNode.class);

                    // Create a Mono<List<String>> of the VC types
                    return getVcTypeListFromVcJson(jsonNode)
                            .flatMap(vcDataTypeList -> {
                                // If vc_types matches with vc_types requested, build VcBasicDataDTO and return it wrapped in a Mono
                                if (new HashSet<>(vcDataTypeList).containsAll(vcTypeList)) {
                                    VcBasicData dto = new VcBasicData(
                                            jsonNode.get("id").asText(),
                                            vcDataTypeList,
                                            jsonNode.get(CREDENTIAL_SUBJECT)
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
    public Mono<String> extractDidFromVerifiableCredential(UserEntity userEntity, String vcId) {
        // Defer the execution until subscription
        return Mono.defer(() -> {
            List<VCAttribute> vcAttributes = userEntity.vcs().value();

            // Find the specified VC by ID and type, then wrap it in a Mono
            return Mono.justOrEmpty(vcAttributes.stream()
                            .filter(vc -> vc.id().equals(vcId) && vc.type().equals(VC_JSON))
                            .findFirst())
                    // If the VC is not found, return an error Mono
                    .switchIfEmpty(Mono.error(new NoSuchVerifiableCredentialException("VC not found: " + vcId)))
                    // Extract the DID from the VC
                    .flatMap(vcToExtract -> {
                        try {
                            JsonNode credentialNode = objectMapper.convertValue(vcToExtract.value(), JsonNode.class);
                            JsonNode didNode = credentialNode.path(CREDENTIAL_SUBJECT).path("id");

                            // If the DID is missing in the VC, return an error Mono
                            if (didNode.isMissingNode()) {
                                return Mono.error(new NoSuchVerifiableCredentialException("DID not found in VC: " + vcId));
                            }

                            // Return the DID as a Mono<String>
                            return Mono.just(didNode.asText());
                        } catch (Exception e) {
                            // If an error occurs during processing, return an error Mono
                            return Mono.error(new RuntimeException("Error processing VC: " + vcId, e));
                        }
                    });
        });
    }

    @Override
    public Mono<UserEntity> deleteVerifiableCredential(UserEntity userEntity, String vcId, String did) {
        // Remove the associated DID from the user entity's DID list
        List<DidAttribute> updatedDids = userEntity.dids().value().stream()
                .filter(didAttr -> !didAttr.value().equals(did))
                .toList();

        // Remove the credential from the user entity's VC list
        List<VCAttribute> updatedVCs = userEntity.vcs().value().stream()
                .filter(vcAttribute -> !vcAttribute.id().equals(vcId))
                .toList();

        // Create a new UserEntity with the updated lists
        UserEntity updatedUserEntity = new UserEntity(
                userEntity.id(),
                userEntity.type(),
                userEntity.userData(),
                new EntityAttribute<>(userEntity.dids().type(), updatedDids),
                new EntityAttribute<>(userEntity.vcs().type(), updatedVCs)
        );

        // Log the successful operation and return the updated entity
        log.info("Verifiable Credential with ID: {} and associated DID deleted successfully for user: {}", vcId, userEntity.id());
        return Mono.just(updatedUserEntity);
    }


    @Override
    public Mono<List<VCAttribute>> getVerifiableCredentialsByFormat(UserEntity userEntity, String format) {
        // Filter VCAttributes based on the given format
        List<VCAttribute> filteredVCs = userEntity.vcs().value().stream()
                .filter(vcAttribute -> vcAttribute.type().equals(format))
                .toList();

        // Return the filtered list of VCAttributes wrapped in a Mono
        return Mono.just(filteredVCs);
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
    public Mono<String> getVerifiableCredentialByIdAndFormat(UserEntity userEntity, String id, String format) {
        // Attempt to find the VCAttribute matching the given id and format
        Optional<VCAttribute> optionalVcAttribute = userEntity.vcs().value().stream()
                .filter(vc -> vc.id().equals(id) && vc.type().equals(format))
                .findFirst();

        // If the VCAttribute is not found, return an error
        if (optionalVcAttribute.isEmpty()) {
            String errorMessage = "No VCAttribute found for id " + id + " and format " + format;
            log.error(errorMessage);
            return Mono.error(new NoSuchElementException(errorMessage));
        }

        VCAttribute vcAttribute = optionalVcAttribute.get();

        // Convert the 'value' of VCAttribute to String
        Object value = vcAttribute.value();
        if (value instanceof String string) {  // Use pattern matching for instanceof
            return Mono.just(string);
        } else {
            // Convert non-String values to JSON String using ObjectMapper
            try {
                String jsonValue = objectMapper.writeValueAsString(value);
                return Mono.just(jsonValue);
            } catch (JsonProcessingException e) {
                log.error("Error processing VCAttribute value to JSON string", e);
                return Mono.error(e);
            }
        }
    }


    @Override
    public Mono<UserEntity> saveDid(UserEntity userEntity, String did, DidMethods didMethod) {
        // Create new DidAttribute for the provided DID
        DidAttribute newDid = new DidAttribute(didMethod.getStringValue(), did);

        // Add the new DID to the list of existing DIDs
        List<DidAttribute> updatedDids = new ArrayList<>(userEntity.dids().value());
        updatedDids.add(newDid);

        // Construct the updated EntityAttribute for DIDs
        EntityAttribute<List<DidAttribute>> dids = new EntityAttribute<>(PROPERTY_TYPE, updatedDids);

        // Create the updated user entity with the new DID
        UserEntity updatedUserEntity = new UserEntity(
                userEntity.id(),
                userEntity.type(),
                userEntity.userData(),
                dids,
                userEntity.vcs()
        );

        // Return the updated user entity
        return Mono.just(updatedUserEntity)
                .doOnSuccess(ue -> log.info("DID saved successfully for user: {}", userEntity.id()))
                .onErrorResume(e -> {
                    log.error("Error while saving DID for user: " + userEntity.id(), e);
                    return Mono.error(e); // Re-throw the error to be handled upstream
                });
    }

    @Override
    public Mono<List<String>> getDidsByUserEntity(UserEntity userEntity) {
        // Extract the DIDs from the UserEntity
        List<String> dids = userEntity.dids().value().stream()
                .map(DidAttribute::value)
                .toList(); // Use Stream.toList() for an unmodifiable list

        // Log the operation result
        log.info("Fetched DIDs for user: {}", userEntity.id());

        // Return the list of DIDs
        return Mono.just(dids);
    }


    @Override
    public Mono<UserEntity> deleteSelectedDidFromUserEntity(String did, UserEntity userEntity) {
        // Create a list of DIDs without the one to be deleted
        List<DidAttribute> originalDids = userEntity.dids().value();
        List<DidAttribute> updatedDids = originalDids.stream()
                .filter(didAttr -> !didAttr.value().equals(did))
                .toList(); // Use Stream.toList() for an unmodifiable list

        // Check if the DID was found and deleted
        if (originalDids.size() == updatedDids.size()) {
            return Mono.error(new NoSuchDidException("DID not found: " + did));
        }

        // Create an updated UserEntity with the remaining DIDs
        UserEntity updatedUserEntity = new UserEntity(
                userEntity.id(),
                userEntity.type(),
                userEntity.userData(),
                new EntityAttribute<>(userEntity.dids().type(), updatedDids),
                userEntity.vcs()
        );

        // Log the operation result
        log.info("Deleted DID: {} for user: {}", did, userEntity.id());

        // Return the updated UserEntity wrapped in a Mono
        return Mono.just(updatedUserEntity);
    }

    @Override
    public Mono<UserAttribute> getUserDataFromUserEntity(UserEntity userEntity) {
        // Extract the UserData from the user entity
        UserAttribute userData = userEntity.userData().value();

        // Log the fetched user data
        log.debug("Fetched user data for userId: {}", userEntity.id());

        // Return the UserData wrapped in a Mono
        return Mono.just(userData);
    }

    @Override
    public Mono<UserEntity> createUserEntity(UserRequest userRequest) {
        // Create the UserAttribute from the provided data
        UserAttribute userAttribute = new UserAttribute(userRequest.username(), userRequest.email());
        EntityAttribute<UserAttribute> userData = new EntityAttribute<>(PROPERTY_TYPE, userAttribute);

        // Construct the UserEntity
        UserEntity userEntity = new UserEntity(
                "urn:entities:userId:" + userRequest.userId(),
                "userEntity",
                userData,
                new EntityAttribute<>(PROPERTY_TYPE, new ArrayList<>()),
                new EntityAttribute<>(PROPERTY_TYPE, new ArrayList<>())
        );

        // Log the creation of the entity
        log.debug("UserEntity created for: {}", userRequest.userId());

        // Return the created UserEntity wrapped in a Mono
        return Mono.just(userEntity);
    }

}
