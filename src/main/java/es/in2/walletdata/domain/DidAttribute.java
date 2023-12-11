package es.in2.walletdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DidAttribute(
        @JsonProperty("type") String type,
        @JsonProperty("value") String value) {
}
