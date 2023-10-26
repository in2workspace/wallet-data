package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.CredentialRequestDTO;
import es.in2.wallet.data.api.model.VCTypeListDTO;
import es.in2.wallet.data.api.model.VcBasicDataDTO;
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

@WebFluxTest(VerifiableCredentialController.class)
class VerifiableCredentialControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrionLDService orionLDService;

    @Test
    void testGetVerifiableCredentialList() {
        String userId = "user123";
        List<VcBasicDataDTO> credentials = List.of(
                new VcBasicDataDTO("id1", List.of("VerifiableCredential","LEARCredential"),"John Doe"),
                new VcBasicDataDTO("id2", List.of("VerifiableCredential","LEARCredential"), "User1")
        );

        Mockito.when(orionLDService.getUserVCsInJson(userId))
                .thenReturn(Mono.just(credentials));

        webTestClient.get()
                .uri("/api/credentials?userId=" + userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].id").isEqualTo("id1")
                .jsonPath("$[0].vcType[0]").isEqualTo("VerifiableCredential")
                .jsonPath("$[0].vcType[1]").isEqualTo("LEARCredential")
                .jsonPath("$[0].credentialSubject").isEqualTo("John Doe")
                .jsonPath("$[1].id").isEqualTo("id2")
                .jsonPath("$[1].vcType[0]").isEqualTo("VerifiableCredential")
                .jsonPath("$[1].vcType[1]").isEqualTo("LEARCredential")
                .jsonPath("$[1].credentialSubject").isEqualTo("User1");
    }

    @Test
    void testDeleteVerifiableCredential() {
        String credentialId = "credential123";
        String userId = "user123";

        Mockito.when(orionLDService.deleteVerifiableCredential(credentialId, userId))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/api/credentials")
                        .queryParam("credentialId", credentialId)
                        .queryParam("userId", userId)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testSaveVerifiableCredential() {
        CredentialRequestDTO credentialRequestDTO = new CredentialRequestDTO();
        credentialRequestDTO.setUserId("user123");
        credentialRequestDTO.setCredential("someCredential");

        Mockito.when(orionLDService.saveVC("someCredential", "user123"))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(credentialRequestDTO)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetVerifiableCredentialListByVCType() {
        String userId = "user123";
        List<String> typeList = List.of("VerifiableCredential","LEARCredential");
        VCTypeListDTO vcTypeList = new VCTypeListDTO();
        vcTypeList.setVcTypes(typeList);
        vcTypeList.setUserId(userId);

        List<VcBasicDataDTO> credentials = List.of(
                new VcBasicDataDTO("id1", List.of("VerifiableCredential","LEARCredential"),"John Doe"),
                new VcBasicDataDTO("id2", List.of("VerifiableCredential","LEARCredential"), "User1")
        );

        Mockito.when(orionLDService.getSelectableVCsByVcTypeList(typeList,userId))
                .thenReturn(Mono.just(credentials));

        webTestClient.post()
                .uri("/api/credentials/types")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(vcTypeList)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].id").isEqualTo("id1")
                .jsonPath("$[0].vcType[0]").isEqualTo("VerifiableCredential")
                .jsonPath("$[0].vcType[1]").isEqualTo("LEARCredential")
                .jsonPath("$[0].credentialSubject").isEqualTo("John Doe")
                .jsonPath("$[1].id").isEqualTo("id2")
                .jsonPath("$[1].vcType[0]").isEqualTo("VerifiableCredential")
                .jsonPath("$[1].vcType[1]").isEqualTo("LEARCredential")
                .jsonPath("$[1].credentialSubject").isEqualTo("User1");
    }

}

