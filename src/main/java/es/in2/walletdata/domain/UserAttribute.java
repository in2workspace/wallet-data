package es.in2.walletdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UserAttribute(
        @JsonProperty("username") String username,
        @JsonProperty("email") String email
) {
}
