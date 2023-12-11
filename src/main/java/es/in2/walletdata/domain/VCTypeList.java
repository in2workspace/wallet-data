package es.in2.walletdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record VCTypeList(
        @JsonProperty("vcTypes") List<String> vcTypes
) {
}
