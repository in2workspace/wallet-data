package es.in2.walletdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DidRequest(
        @JsonProperty("did") String did,
        @JsonProperty("didType") String didType
) {
}
