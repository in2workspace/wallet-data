package es.in2.walletdata.api.controller;

import es.in2.walletdata.controller.VerifiableCredentialController;
import es.in2.walletdata.domain.CredentialRequest;
import es.in2.walletdata.domain.VCTypeList;
import es.in2.walletdata.domain.VcBasicData;
import es.in2.walletdata.facade.UserDataFacadeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class VerifiableCredentialControllerTest {

    @Mock
    private UserDataFacadeService userDataFacadeService;

    @InjectMocks
    private VerifiableCredentialController verifiableCredentialController;

    @Test
    void testGetVerifiableCredentialList() {
        // Arrange
        List<VcBasicData> credentials = List.of(
                new VcBasicData("id1", List.of("VerifiableCredential", "LEARCredential"), "John Doe"),
                new VcBasicData("id2", List.of("VerifiableCredential", "LEARCredential"), "User1")
        );
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTg3NTg0NjcsImlhdCI6MTY5ODc1ODE2NywianRpIjoiMjljOGFlY2UtMmRmZi00NTZkLTk3OGItM2Y5MDk0MDJkOGFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYWViNWY4NDktMzkwOS00OGQzLTk3MDItYTc4MzY3YmEyNGY1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJmY2Q5Y2MwYS1iOTQ3LTRmM2UtYjgxZC01ODNjMjgwNjg3MWMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiZmNkOWNjMGEtYjk0Ny00ZjNlLWI4MWQtNTgzYzI4MDY4NzFjIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbnRvbmlvIiwiZW1haWwiOiJhbnRvbmlvQGV4YW1wbGUuY29tIn0.fFG9tgRQvOPmk7lLRLbuiLU5tO-zr7a2frV2aumBxD_EqA5FeadWH_u90ZOgDJzmGk0jJNihwoDmJqJLIivs16l6R9bGZONJLEc2aw64J1IaMeJo0rHkIMpXx7Vf7BnKLwo1Jj1pvEsbmhNdYxj6PEYyUtqucwFqbYMp01bGFhKFmbVjMv39RvnVTK-HPk2wceAKEDogbXPnIQ9bhF2uJktWmAxhyFqv1Ll59HwcqDuSVaE32ka59K5OMWt6oOyYIxxaWfMFqRh3aEvbMm8HYV2tUq75uonpAR3K_I9OapCBb2BYhirbP3Vvx4MQYhQ90EBUPEWN14Sa0ic9xdTSIw";
        // Mock
        Mockito.when(userDataFacadeService.getUserVCs("aeb5f849-3909-48d3-9702-a78367ba24f5"))
                .thenReturn(Mono.just(credentials));
        // Act & Assert
        WebTestClient
                .bindToController(verifiableCredentialController)
                .build()
                .get()
                .uri("/api/v2/credentials")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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
    void testGetSelectableVcs() {
        // Arrange
        String credentialId = "credential123";
        String format = "vc_json";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTg3NTg0NjcsImlhdCI6MTY5ODc1ODE2NywianRpIjoiMjljOGFlY2UtMmRmZi00NTZkLTk3OGItM2Y5MDk0MDJkOGFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYWViNWY4NDktMzkwOS00OGQzLTk3MDItYTc4MzY3YmEyNGY1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJmY2Q5Y2MwYS1iOTQ3LTRmM2UtYjgxZC01ODNjMjgwNjg3MWMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiZmNkOWNjMGEtYjk0Ny00ZjNlLWI4MWQtNTgzYzI4MDY4NzFjIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbnRvbmlvIiwiZW1haWwiOiJhbnRvbmlvQGV4YW1wbGUuY29tIn0.fFG9tgRQvOPmk7lLRLbuiLU5tO-zr7a2frV2aumBxD_EqA5FeadWH_u90ZOgDJzmGk0jJNihwoDmJqJLIivs16l6R9bGZONJLEc2aw64J1IaMeJo0rHkIMpXx7Vf7BnKLwo1Jj1pvEsbmhNdYxj6PEYyUtqucwFqbYMp01bGFhKFmbVjMv39RvnVTK-HPk2wceAKEDogbXPnIQ9bhF2uJktWmAxhyFqv1Ll59HwcqDuSVaE32ka59K5OMWt6oOyYIxxaWfMFqRh3aEvbMm8HYV2tUq75uonpAR3K_I9OapCBb2BYhirbP3Vvx4MQYhQ90EBUPEWN14Sa0ic9xdTSIw";
        // Mock
        Mockito.when(userDataFacadeService.getVerifiableCredentialByIdAndFormat("aeb5f849-3909-48d3-9702-a78367ba24f5",credentialId,format))
                .thenReturn(Mono.just("credential"));
        // Act & Assert
        WebTestClient
                .bindToController(verifiableCredentialController)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v2/credentials/id")
                        .queryParam("credentialId", credentialId)
                        .queryParam("format", format)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    void testDeleteVerifiableCredential() {
        // Arrange
        String credentialId = "credential123";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTg3NTg0NjcsImlhdCI6MTY5ODc1ODE2NywianRpIjoiMjljOGFlY2UtMmRmZi00NTZkLTk3OGItM2Y5MDk0MDJkOGFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYWViNWY4NDktMzkwOS00OGQzLTk3MDItYTc4MzY3YmEyNGY1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJmY2Q5Y2MwYS1iOTQ3LTRmM2UtYjgxZC01ODNjMjgwNjg3MWMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiZmNkOWNjMGEtYjk0Ny00ZjNlLWI4MWQtNTgzYzI4MDY4NzFjIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbnRvbmlvIiwiZW1haWwiOiJhbnRvbmlvQGV4YW1wbGUuY29tIn0.fFG9tgRQvOPmk7lLRLbuiLU5tO-zr7a2frV2aumBxD_EqA5FeadWH_u90ZOgDJzmGk0jJNihwoDmJqJLIivs16l6R9bGZONJLEc2aw64J1IaMeJo0rHkIMpXx7Vf7BnKLwo1Jj1pvEsbmhNdYxj6PEYyUtqucwFqbYMp01bGFhKFmbVjMv39RvnVTK-HPk2wceAKEDogbXPnIQ9bhF2uJktWmAxhyFqv1Ll59HwcqDuSVaE32ka59K5OMWt6oOyYIxxaWfMFqRh3aEvbMm8HYV2tUq75uonpAR3K_I9OapCBb2BYhirbP3Vvx4MQYhQ90EBUPEWN14Sa0ic9xdTSIw";
        // Mock
        Mockito.when(userDataFacadeService.deleteVerifiableCredentialById(credentialId, "aeb5f849-3909-48d3-9702-a78367ba24f5"))
                .thenReturn(Mono.empty());
        // Act & Assert
        WebTestClient
                .bindToController(verifiableCredentialController)
                .build()
                .delete()
                .uri(uriBuilder -> uriBuilder.path("/api/v2/credentials")
                        .queryParam("credentialId", credentialId)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testSaveVerifiableCredential() {
        // Arrange
        CredentialRequest credentialRequest = CredentialRequest.builder()
                .credential("someCredential")
                .build();
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTg3NTg0NjcsImlhdCI6MTY5ODc1ODE2NywianRpIjoiMjljOGFlY2UtMmRmZi00NTZkLTk3OGItM2Y5MDk0MDJkOGFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYWViNWY4NDktMzkwOS00OGQzLTk3MDItYTc4MzY3YmEyNGY1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJmY2Q5Y2MwYS1iOTQ3LTRmM2UtYjgxZC01ODNjMjgwNjg3MWMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiZmNkOWNjMGEtYjk0Ny00ZjNlLWI4MWQtNTgzYzI4MDY4NzFjIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbnRvbmlvIiwiZW1haWwiOiJhbnRvbmlvQGV4YW1wbGUuY29tIn0.fFG9tgRQvOPmk7lLRLbuiLU5tO-zr7a2frV2aumBxD_EqA5FeadWH_u90ZOgDJzmGk0jJNihwoDmJqJLIivs16l6R9bGZONJLEc2aw64J1IaMeJo0rHkIMpXx7Vf7BnKLwo1Jj1pvEsbmhNdYxj6PEYyUtqucwFqbYMp01bGFhKFmbVjMv39RvnVTK-HPk2wceAKEDogbXPnIQ9bhF2uJktWmAxhyFqv1Ll59HwcqDuSVaE32ka59K5OMWt6oOyYIxxaWfMFqRh3aEvbMm8HYV2tUq75uonpAR3K_I9OapCBb2BYhirbP3Vvx4MQYhQ90EBUPEWN14Sa0ic9xdTSIw";
        // Mock
        Mockito.when(userDataFacadeService.saveVerifiableCredentialByUserId("aeb5f849-3909-48d3-9702-a78367ba24f5", "someCredential"))
                .thenReturn(Mono.empty());
        // Act & Assert
        WebTestClient
                .bindToController(verifiableCredentialController)
                .build()
                .post()
                .uri("/api/v2/credentials")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(credentialRequest)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void testGetVerifiableCredentialListByVCType() {
        // Arrange
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTg3NTg0NjcsImlhdCI6MTY5ODc1ODE2NywianRpIjoiMjljOGFlY2UtMmRmZi00NTZkLTk3OGItM2Y5MDk0MDJkOGFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYWViNWY4NDktMzkwOS00OGQzLTk3MDItYTc4MzY3YmEyNGY1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJmY2Q5Y2MwYS1iOTQ3LTRmM2UtYjgxZC01ODNjMjgwNjg3MWMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiZmNkOWNjMGEtYjk0Ny00ZjNlLWI4MWQtNTgzYzI4MDY4NzFjIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbnRvbmlvIiwiZW1haWwiOiJhbnRvbmlvQGV4YW1wbGUuY29tIn0.fFG9tgRQvOPmk7lLRLbuiLU5tO-zr7a2frV2aumBxD_EqA5FeadWH_u90ZOgDJzmGk0jJNihwoDmJqJLIivs16l6R9bGZONJLEc2aw64J1IaMeJo0rHkIMpXx7Vf7BnKLwo1Jj1pvEsbmhNdYxj6PEYyUtqucwFqbYMp01bGFhKFmbVjMv39RvnVTK-HPk2wceAKEDogbXPnIQ9bhF2uJktWmAxhyFqv1Ll59HwcqDuSVaE32ka59K5OMWt6oOyYIxxaWfMFqRh3aEvbMm8HYV2tUq75uonpAR3K_I9OapCBb2BYhirbP3Vvx4MQYhQ90EBUPEWN14Sa0ic9xdTSIw";
        List<String> typeList = List.of("VerifiableCredential", "LEARCredential");
        VCTypeList vcTypeList = VCTypeList.builder()
                .vcTypes(typeList)
                .build();
        List<VcBasicData> credentials = List.of(
                new VcBasicData("id1", List.of("VerifiableCredential", "LEARCredential"), "John Doe"),
                new VcBasicData("id2", List.of("VerifiableCredential", "LEARCredential"), "User1")
        );
        // Mock
        Mockito.when(userDataFacadeService.getVCsByVcTypeList("aeb5f849-3909-48d3-9702-a78367ba24f5", typeList))
                .thenReturn(Mono.just(credentials));
        // Act & Assert
        WebTestClient
                .bindToController(verifiableCredentialController)
                .build()
                .post()
                .uri("/api/v2/credentials/types")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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

