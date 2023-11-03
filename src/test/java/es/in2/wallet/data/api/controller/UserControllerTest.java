package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.UserAttribute;
import es.in2.wallet.data.api.model.UserRequestDTO;
import es.in2.wallet.data.api.service.OrionLDService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;


@WebFluxTest(UserController.class)
class UserControllerTest {

    private WebTestClient webClient;

    @MockBean
    private OrionLDService orionLDService;

    @BeforeEach
    void setUp() {
        webClient = WebTestClient.bindToController(new UserController(orionLDService))
                .configureClient()
                .build();
    }
    @Test
    void testRegisterUser() {
        UserRequestDTO userRequest = new UserRequestDTO();
        userRequest.setUserId("123");
        userRequest.setUsername("John Doe");
        userRequest.setEmail("jhon@example.com");

        Mockito.when(orionLDService.registerUserInContextBroker(userRequest)).thenReturn(Mono.empty());

        webClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void testGetUserDataByUserId() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTg3NTg0NjcsImlhdCI6MTY5ODc1ODE2NywianRpIjoiMjljOGFlY2UtMmRmZi00NTZkLTk3OGItM2Y5MDk0MDJkOGFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYWViNWY4NDktMzkwOS00OGQzLTk3MDItYTc4MzY3YmEyNGY1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJmY2Q5Y2MwYS1iOTQ3LTRmM2UtYjgxZC01ODNjMjgwNjg3MWMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiZmNkOWNjMGEtYjk0Ny00ZjNlLWI4MWQtNTgzYzI4MDY4NzFjIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbnRvbmlvIiwiZW1haWwiOiJhbnRvbmlvQGV4YW1wbGUuY29tIn0.fFG9tgRQvOPmk7lLRLbuiLU5tO-zr7a2frV2aumBxD_EqA5FeadWH_u90ZOgDJzmGk0jJNihwoDmJqJLIivs16l6R9bGZONJLEc2aw64J1IaMeJo0rHkIMpXx7Vf7BnKLwo1Jj1pvEsbmhNdYxj6PEYyUtqucwFqbYMp01bGFhKFmbVjMv39RvnVTK-HPk2wceAKEDogbXPnIQ9bhF2uJktWmAxhyFqv1Ll59HwcqDuSVaE32ka59K5OMWt6oOyYIxxaWfMFqRh3aEvbMm8HYV2tUq75uonpAR3K_I9OapCBb2BYhirbP3Vvx4MQYhQ90EBUPEWN14Sa0ic9xdTSIw";

        UserAttribute expectedUserAttribute = new UserAttribute();
        expectedUserAttribute.setUsername("John Doe");
        expectedUserAttribute.setEmail("john@example.com");

        Mockito.when(orionLDService.getUserDataByUserId("aeb5f849-3909-48d3-9702-a78367ba24f5")).thenReturn(Mono.just(expectedUserAttribute));

        webClient.get()
                .uri("/api/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserAttribute.class)
                .value(userAttribute -> {
                    assertThat(userAttribute.getUsername()).isEqualTo(expectedUserAttribute.getUsername());
                    assertThat(userAttribute.getEmail()).isEqualTo(expectedUserAttribute.getEmail());
                });
    }


}

