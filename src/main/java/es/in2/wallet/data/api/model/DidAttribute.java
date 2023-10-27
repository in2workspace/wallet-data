package es.in2.wallet.data.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DidAttribute {
    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private String value;

}
