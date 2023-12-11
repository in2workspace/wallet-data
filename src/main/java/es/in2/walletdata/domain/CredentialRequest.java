package es.in2.walletdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record CredentialRequest(@JsonProperty("credential") String credential) {
}