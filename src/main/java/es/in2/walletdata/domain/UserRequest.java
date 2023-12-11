package es.in2.walletdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserRequest(
        @JsonProperty("userId") @NotBlank(message = "UserId cannot be blank") String userId,
        @JsonProperty("username") @NotBlank(message = "Username cannot be blank") String username,
        @JsonProperty("email") @NotBlank(message = "Email cannot be blank") String email
) {
}
