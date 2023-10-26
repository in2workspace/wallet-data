package es.in2.wallet.data.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VCTypeListDTO {
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("vcTypes")
    private List<String> vcTypes;
}
