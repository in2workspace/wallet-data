package es.in2.walletdata.service;

import reactor.core.publisher.Mono;

public interface WalletCryptoService {
    Mono<String> deletePrivateKeyAssociateToDID(String did);
}
