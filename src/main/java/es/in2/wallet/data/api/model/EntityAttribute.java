package es.in2.wallet.data.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityAttribute<T> {
    @JsonProperty("type")
    private String type = "Property";

    @JsonProperty("value")
    private T value;
}
