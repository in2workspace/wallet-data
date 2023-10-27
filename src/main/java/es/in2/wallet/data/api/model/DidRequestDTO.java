package es.in2.wallet.data.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DidRequestDTO {
    @JsonProperty("did")
    private String did;
    @JsonProperty("didType")
    private String didType;
    @JsonProperty("userId")
    private String userId;

}
