package es.in2.wallet.data.api.service;

import reactor.core.publisher.Mono;

public interface WalletCryptoCommunicationService {
    Mono<String> deletePrivateKeyAssociateToDID(String did);
}
