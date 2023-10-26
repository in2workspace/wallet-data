package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.UserAttribute;
import es.in2.wallet.data.api.model.UserRequestDTO;
import es.in2.wallet.data.api.service.OrionLDService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;


@WebFluxTest(UserController.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private OrionLDService orionLDService;

    @Test
    void testRegisterUser() {
        UserRequestDTO userRequest = new UserRequestDTO();
        userRequest.setUserId("user123");
        userRequest.setUsername("John Doe");

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
        String userId = "user123";
        UserAttribute expectedUserAttribute = new UserAttribute();
        expectedUserAttribute.setUsername("John Doe");
        expectedUserAttribute.setEmail("john@example.com");

        Mockito.when(orionLDService.getUserDataByUserId(userId)).thenReturn(Mono.just(expectedUserAttribute));

        webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/users")
                        .queryParam("userId", userId)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserAttribute.class)
                .value(userAttribute -> {
                    assertThat(userAttribute.getUsername()).isEqualTo(expectedUserAttribute.getUsername());
                    assertThat(userAttribute.getEmail()).isEqualTo(expectedUserAttribute.getEmail());
                });
    }


}

