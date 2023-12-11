package es.in2.walletdata.service;

import es.in2.walletdata.domain.UserEntity;
import reactor.core.publisher.Mono;

public interface BrokerAdapterService {
    Mono<Void> storeUserInContextBroker(UserEntity userEntity);
    Mono<UserEntity> getUserEntityFromContextBroker(String userId);
    Mono<Void> updateUserEntityInContextBroker(UserEntity userEntity, String userId);
}
