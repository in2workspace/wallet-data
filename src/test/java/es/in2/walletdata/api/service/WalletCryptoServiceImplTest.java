package es.in2.walletdata.api.service;

import es.in2.walletdata.configuration.properties.WalletCryptoProperties;
import es.in2.walletdata.exception.FailedCommunicationException;
import es.in2.walletdata.service.impl.WalletCryptoServiceImpl;
import es.in2.walletdata.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static es.in2.walletdata.utils.Utils.deleteRequest;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletCryptoServiceImplTest {
    @Mock
    private WalletCryptoProperties walletCryptoProperties;
    @InjectMocks
    private WalletCryptoServiceImpl walletCryptoService;

    @Test
    void deletePrivateKeyAssociateToDIDTest(){
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
            String didToDelete = "did:key:123";

            when(walletCryptoProperties.domain()).thenReturn("https://example.com");
            when(walletCryptoProperties.url()).thenReturn("/url");

            when(deleteRequest("https://example.com","/url?did=" + didToDelete ,new ArrayList<>())).thenReturn(Mono.just("did deleted"));

            StepVerifier.create(walletCryptoService.deletePrivateKeyAssociateToDID(didToDelete))
                    .expectNext(didToDelete)
                    .verifyComplete();
        }
    }

    @Test
    void deletePrivateKeyAssociateToDIDFailedCommunicationExceptionTest(){
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
            String didToDelete = "did:key:123";

            when(walletCryptoProperties.domain()).thenReturn("https://example.com");
            when(walletCryptoProperties.url()).thenReturn("/url");

            when(deleteRequest("https://example.com","/url?did=" + didToDelete ,new ArrayList<>())).thenReturn(Mono.error(new RuntimeException("Communication error")));

            StepVerifier.create(walletCryptoService.deletePrivateKeyAssociateToDID(didToDelete))
                    .expectError(FailedCommunicationException.class)
                    .verify();
        }
    }
}
