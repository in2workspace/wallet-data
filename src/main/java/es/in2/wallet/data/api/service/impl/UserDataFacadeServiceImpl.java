package es.in2.wallet.data.api.service.impl;

import es.in2.wallet.data.api.model.UserAttribute;
import es.in2.wallet.data.api.model.UserEntity;
import es.in2.wallet.data.api.model.UserRequestDTO;
import es.in2.wallet.data.api.model.VcBasicDataDTO;
import es.in2.wallet.data.api.service.OrionLDAdapterCommunicationService;
import es.in2.wallet.data.api.service.UserDataFacadeService;
import es.in2.wallet.data.api.service.UserDataService;
import es.in2.wallet.data.api.service.WalletCryptoCommunicationService;
import es.in2.wallet.data.api.utils.DidMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataFacadeServiceImpl implements UserDataFacadeService {
    private final UserDataService userDataService;
    private final OrionLDAdapterCommunicationService orionLDAdapterCommunicationService;
    private final WalletCryptoCommunicationService walletCryptoCommunicationService;

    @Override
    public Mono<Void> saveVerifiableCredentialByUserId(String userId, String vcJwt) {
        // Retrieve the UserEntity from the Context Broker
        return orionLDAdapterCommunicationService.getUserEntityFromContextBroker(userId)
                .flatMap(userEntity ->
                        // Save the Verifiable Credential to the UserEntity
                        userDataService.saveVC(userEntity, vcJwt)
                )
                .flatMap(updatedUserEntity ->
                        // Update the UserEntity back in the Context Broker
                        orionLDAdapterCommunicationService.updateUserEntityInContextBroker(updatedUserEntity, userId)
                )
                .doOnSuccess(aVoid -> log.info("Verifiable Credential saved and UserEntity updated successfully for userId: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error in saving Verifiable Credential for userId: " + userId, e);
                    return Mono.error(e);
                });
    }
    @Override
    public Mono<List<VcBasicDataDTO>> getUserVCs(String userId) {
        // Retrieve the UserEntity from the Context Broker
        return orionLDAdapterCommunicationService.getUserEntityFromContextBroker(userId)
                // Once the UserEntity is retrieved, use the userDataService to get VCs in JSON format
                .flatMap(userDataService::getUserVCsInJson)
                .doOnSuccess(vcBasicDataDTOList -> log.info("Retrieved VCs in JSON for userId: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error in retrieving VCs in JSON for userId: {}", userId, e);
                    return Mono.error(e);
                });
    }
    @Override
    public Mono<Void> deleteVerifiableCredentialById(String credentialId, String userId) {
        // Retrieve the UserEntity from the Context Broker
        return orionLDAdapterCommunicationService.getUserEntityFromContextBroker(userId)
                .flatMap(userEntity ->
                        // Extract DID from the Verifiable Credential
                        userDataService.extractDidFromVerifiableCredential(userEntity, credentialId)
                                .flatMap(did ->
                                        // Delete the Private Key associated with the DID
                                        walletCryptoCommunicationService.deletePrivateKeyAssociateToDID(did)
                                                .then(Mono.just(new AbstractMap.SimpleEntry<>(userEntity, did)))
                                )
                )
                .flatMap(entry -> {
                    UserEntity userEntity = entry.getKey();
                    String did = entry.getValue();
                    // Delete the Verifiable Credential and associated DID from the UserEntity
                    return userDataService.deleteVerifiableCredential(userEntity, credentialId, did);
                })
                .flatMap(updatedUserEntity ->
                        // Update the UserEntity back in the Context Broker
                        orionLDAdapterCommunicationService.updateUserEntityInContextBroker(updatedUserEntity, userId)
                )
                .doOnSuccess(aVoid -> log.info("Verifiable Credential deleted and UserEntity updated successfully for userId: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error in deleting Verifiable Credential for userId: " + userId, e);
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<List<VcBasicDataDTO>> getVCsByVcTypeList(String userId, List<String> vcTypeList) {
        // Retrieve the UserEntity from the Context Broker
        return orionLDAdapterCommunicationService.getUserEntityFromContextBroker(userId)
                // Once the UserEntity is retrieved, pass it to the userDataService
                .flatMap(userEntity -> userDataService.getSelectableVCsByVcTypeList(vcTypeList, userEntity))
                .doOnSuccess(vcBasicDataDTOList -> log.info("Selectable VCs retrieved for userId: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error retrieving selectable VCs for userId: {}", userId, e);
                    return Mono.error(e);
                });
    }
    @Override
    public Mono<Void> saveDidByUserId(String userId, String did, DidMethods didMethod) {
        // Retrieve the UserEntity from the Context Broker
        return orionLDAdapterCommunicationService.getUserEntityFromContextBroker(userId)
                .flatMap(userEntity ->
                        // Save the Did to the UserEntity
                        userDataService.saveDid(userEntity,did,didMethod)
                )
                .flatMap(updatedUserEntity ->
                        // Update the UserEntity back in the Context Broker
                        orionLDAdapterCommunicationService.updateUserEntityInContextBroker(updatedUserEntity, userId)
                )
                .doOnSuccess(aVoid -> log.info("Did saved and UserEntity updated successfully for userId: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error in saving Did for userId: " + userId, e);
                    return Mono.error(e);
                });
    }
    @Override
    public Mono<Void> deleteDid(String userId,String did){
        // Retrieve the UserEntity from the Context Broker
        return orionLDAdapterCommunicationService.getUserEntityFromContextBroker(userId)
                .flatMap(userEntity ->
                        // Delete the Did to the UserEntity by userId
                        userDataService.deleteSelectedDidFromUserEntity(did,userEntity)
                )
                .flatMap(updatedUserEntity ->
                        // Update the UserEntity back in the Context Broker
                        orionLDAdapterCommunicationService.updateUserEntityInContextBroker(updatedUserEntity, userId)
                )
                .doOnSuccess(aVoid -> log.info("Did deleted and UserEntity updated successfully for userId: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error in deleting Did for userId: " + userId, e);
                    return Mono.error(e);
                });
    }
    @Override
    public Mono<List<String>> getDidsByUserId(String userId) {
        // Retrieve the UserEntity from the Context Broker
        return orionLDAdapterCommunicationService.getUserEntityFromContextBroker(userId)
                // Use flatMap to transform the Mono<UserEntity> to a Mono<List<String>>
                .flatMap(userDataService::getDidsByUserEntity)
                .doOnSuccess(dids -> log.info("Retrieved DIDs for userId: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error in retrieving DIDs for userId: {}", userId, e);
                    return Mono.error(e);
                });
    }
    @Override
    public Mono<Void> createUserEntity(UserRequestDTO userRequestDTO) {
        // Create the UserEntity using the provided DTO
        return userDataService.createUserEntity(userRequestDTO)
                .flatMap(orionLDAdapterCommunicationService::storeUserInContextBroker)
                .doOnSuccess(aVoid -> log.info("UserEntity successfully persisted for: {}", userRequestDTO.getUserId()))
                .onErrorResume(e -> {
                    log.error("Error while persisting UserEntity for: {}", userRequestDTO.getUserId(), e);
                    return Mono.error(e); // Propagate the error
                });
    }
    @Override
    public Mono<UserAttribute> getUserDataByUserId(String userId) {
        // Retrieve the UserEntity from the Context Broker
        return orionLDAdapterCommunicationService.getUserEntityFromContextBroker(userId)
                // Once the UserEntity is retrieved, use the userDataService to get the UserData
                .flatMap(userDataService::getUserDataFromUserEntity)
                .doOnSuccess(userData -> log.debug("Fetched user data for userId: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error retrieving user data for userId: {}", userId, e);
                    return Mono.error(e);
                });
    }
    @Override
    public Mono<String> getVerifiableCredentialByIdAndFormat(String userId, String credentialId, String format) {
        // Retrieve the UserEntity from the Context Broker
        return orionLDAdapterCommunicationService.getUserEntityFromContextBroker(userId)
                // Once the UserEntity is retrieved, use the userDataService to get the UserData
                .flatMap(userEntity -> userDataService.getVerifiableCredentialByIdAndFormat(userEntity,credentialId,format))
                .doOnSuccess(userData -> log.debug("Fetched user data for userId: {}", userId))
                .onErrorResume(e -> {
                    log.error("Error retrieving user data for userId: {}", userId, e);
                    return Mono.error(e);
                });
    }


}
