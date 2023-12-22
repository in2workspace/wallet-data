package es.in2.walletdata.api.controller;

import es.in2.walletdata.controller.DidController;
import es.in2.walletdata.domain.DidRequest;
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
class DidControllerTest {

    @Mock
    private UserDataFacadeService userDataFacadeService;

    @InjectMocks
    private DidController didController;

    @Test
    void testSaveDid() {
        // Arrange
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTg3NTg0NjcsImlhdCI6MTY5ODc1ODE2NywianRpIjoiMjljOGFlY2UtMmRmZi00NTZkLTk3OGItM2Y5MDk0MDJkOGFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYWViNWY4NDktMzkwOS00OGQzLTk3MDItYTc4MzY3YmEyNGY1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJmY2Q5Y2MwYS1iOTQ3LTRmM2UtYjgxZC01ODNjMjgwNjg3MWMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiZmNkOWNjMGEtYjk0Ny00ZjNlLWI4MWQtNTgzYzI4MDY4NzFjIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbnRvbmlvIiwiZW1haWwiOiJhbnRvbmlvQGV4YW1wbGUuY29tIn0.fFG9tgRQvOPmk7lLRLbuiLU5tO-zr7a2frV2aumBxD_EqA5FeadWH_u90ZOgDJzmGk0jJNihwoDmJqJLIivs16l6R9bGZONJLEc2aw64J1IaMeJo0rHkIMpXx7Vf7BnKLwo1Jj1pvEsbmhNdYxj6PEYyUtqucwFqbYMp01bGFhKFmbVjMv39RvnVTK-HPk2wceAKEDogbXPnIQ9bhF2uJktWmAxhyFqv1Ll59HwcqDuSVaE32ka59K5OMWt6oOyYIxxaWfMFqRh3aEvbMm8HYV2tUq75uonpAR3K_I9OapCBb2BYhirbP3Vvx4MQYhQ90EBUPEWN14Sa0ic9xdTSIw";
        DidRequest didRequest = DidRequest.builder()
                .did("did:test:123")
                .didType("key")
                .build();
        // Mock
        Mockito.when(userDataFacadeService.saveDidByUserId(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(Mono.empty());
        // Act & Assert
        WebTestClient
                .bindToController(didController)
                .build()
                .post()
                .uri("/api/v2/dids")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(didRequest)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void testGetDidListByUserId() {
        // Arrange
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTg3NTg0NjcsImlhdCI6MTY5ODc1ODE2NywianRpIjoiMjljOGFlY2UtMmRmZi00NTZkLTk3OGItM2Y5MDk0MDJkOGFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYWViNWY4NDktMzkwOS00OGQzLTk3MDItYTc4MzY3YmEyNGY1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJmY2Q5Y2MwYS1iOTQ3LTRmM2UtYjgxZC01ODNjMjgwNjg3MWMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiZmNkOWNjMGEtYjk0Ny00ZjNlLWI4MWQtNTgzYzI4MDY4NzFjIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbnRvbmlvIiwiZW1haWwiOiJhbnRvbmlvQGV4YW1wbGUuY29tIn0.fFG9tgRQvOPmk7lLRLbuiLU5tO-zr7a2frV2aumBxD_EqA5FeadWH_u90ZOgDJzmGk0jJNihwoDmJqJLIivs16l6R9bGZONJLEc2aw64J1IaMeJo0rHkIMpXx7Vf7BnKLwo1Jj1pvEsbmhNdYxj6PEYyUtqucwFqbYMp01bGFhKFmbVjMv39RvnVTK-HPk2wceAKEDogbXPnIQ9bhF2uJktWmAxhyFqv1Ll59HwcqDuSVaE32ka59K5OMWt6oOyYIxxaWfMFqRh3aEvbMm8HYV2tUq75uonpAR3K_I9OapCBb2BYhirbP3Vvx4MQYhQ90EBUPEWN14Sa0ic9xdTSIw";
        List<String> dids = List.of("did:test:123", "did:test:456");
        // Mock
        Mockito.when(userDataFacadeService.getDidsByUserId("aeb5f849-3909-48d3-9702-a78367ba24f5"))
                .thenReturn(Mono.just(dids));
        // Act & Assert
        WebTestClient
                .bindToController(didController)
                .build()
                .get()
                .uri("/api/v2/dids")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0]").isEqualTo("did:test:123")
                .jsonPath("$[1]").isEqualTo("did:test:456");
    }

}
