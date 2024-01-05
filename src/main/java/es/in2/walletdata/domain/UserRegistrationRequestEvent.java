package es.in2.walletdata.domain;

import lombok.Builder;

@Builder
public record UserRegistrationRequestEvent(
        String correlationId,
        String id,
        String username,
        String email,
        String password
) {
}
