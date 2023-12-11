package es.in2.walletdata.service.impl;

import es.in2.walletdata.configuration.properties.WalletCryptoProperties;
import es.in2.walletdata.exception.FailedCommunicationException;
import es.in2.walletdata.service.WalletCryptoService;
import es.in2.walletdata.utils.ApplicationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletCryptoServiceImpl implements WalletCryptoService {

    private final ApplicationUtils applicationUtils;
    private final WalletCryptoProperties walletCryptoProperties;
    @Override
    public Mono<String> deletePrivateKeyAssociateToDID(String did) {
        // Build the URL for the DELETE request, including the DID as a query parameter
        String deleteUrl = walletCryptoProperties.url() + "/api/v1/credentials?did=" + did;

        // Perform the DELETE request using ApplicationUtils
        return applicationUtils.deleteRequest(deleteUrl, new ArrayList<>())
                // Log a message on successful deletion of the private key
                .doOnSuccess(response -> log.info("Successfully deleted private key associated with DID: {}", did))
                // Transform any error into a FailedCommunicationException
                .onErrorMap(error -> {
                    log.error("Error deleting private key associated with DID: {}. Error: {}", did, error.getMessage());
                    return new FailedCommunicationException("Failed to communicate with the wallet-crypto service for DID: " + did);
                })
                // Convert the response to Mono<Void> to handle completion only
                .then(Mono.just(did));
    }


}

