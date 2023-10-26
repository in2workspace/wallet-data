package es.in2.wallet.data.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialRequestDTO {
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("credential")
    private String credential;
}