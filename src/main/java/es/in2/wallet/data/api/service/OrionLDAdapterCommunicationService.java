package es.in2.wallet.data.api.service;

import es.in2.wallet.data.api.model.UserEntity;
import reactor.core.publisher.Mono;

public interface OrionLDAdapterCommunicationService {
    Mono<Void> storeUserInContextBroker(UserEntity userEntity);
    Mono<UserEntity> getUserEntityFromContextBroker(String userId);
    Mono<Void> updateUserEntityInContextBroker(UserEntity userEntity, String userId);
}
