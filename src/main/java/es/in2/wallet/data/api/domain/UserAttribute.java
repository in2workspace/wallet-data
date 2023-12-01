package es.in2.wallet.data.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAttribute {
    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

}
