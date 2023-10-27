package es.in2.wallet.data.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class VcBasicDataDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("vcType")
    private List<String> vcType;

    @JsonProperty("credentialSubject")
    private Object credentialSubject;
}
