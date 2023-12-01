package es.in2.wallet.data.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialRequestDTO {
    @JsonProperty("credential")
    private String credential;
}