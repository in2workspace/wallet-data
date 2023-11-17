package es.in2.wallet.data.api.service.impl;

import es.in2.wallet.data.api.exception.FailedCommunicationException;
import es.in2.wallet.data.api.service.WalletCryptoCommunicationService;
import es.in2.wallet.data.api.utils.ApplicationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletCryptoCommunicationServiceImpl implements WalletCryptoCommunicationService {

    private final ApplicationUtils applicationUtils;
    @Value("${app.url.wallet-crypto}")
    private String walletCryptoURL;
    @Override
    public Mono<Void> deletePrivateKeyAssociateToDID(String did) {
        // Build the URL for the DELETE request, including the DID as a query parameter
        String deleteUrl = walletCryptoURL + "/api/v1/credentials?did=" + did;

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
                .then();
    }


}

