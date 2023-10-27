package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.DidRequestDTO;
import es.in2.wallet.data.api.service.OrionLDService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest(DidController.class)
class DidControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private OrionLDService orionLDService;

    @Test
    void testSaveDid() {
        DidRequestDTO didRequestDTO = new DidRequestDTO();
        didRequestDTO.setDid("did:test:123");
        didRequestDTO.setDidType("did:key");
        didRequestDTO.setUserId("user1");

        Mockito.when(orionLDService.saveDid(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
                .thenReturn(Mono.empty());

        webClient.post()
                .uri("/api/dids")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(didRequestDTO)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void testGetDidListByUserId() {
        String userId = "user1";
        List<String> dids = List.of("did:test:123", "did:test:456");

        Mockito.when(orionLDService.getDidsByUserId(userId))
                .thenReturn(Mono.just(dids));

        webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/dids")
                        .queryParam("userId", userId)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0]").isEqualTo("did:test:123")
                .jsonPath("$[1]").isEqualTo("did:test:456");
    }

    @Test
    void testDeleteDid() {
        String did = "did:test:123";
        String userId = "user1";

        Mockito.when(orionLDService.deleteSelectedDid(did, userId))
                .thenReturn(Mono.empty());

        webClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/api/dids")
                        .queryParam("did", did)
                        .queryParam("userId", userId)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }
}

