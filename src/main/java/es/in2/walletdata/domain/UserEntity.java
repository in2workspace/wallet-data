package es.in2.walletdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserEntity(
        @JsonProperty("id") String id,
        @JsonProperty("type") String type,
        @JsonProperty("userData") EntityAttribute<UserAttribute> userData,
        @JsonProperty("dids") EntityAttribute<List<DidAttribute>> dids,
        @JsonProperty("vcs") EntityAttribute<List<VCAttribute>> vcs
) {
}

