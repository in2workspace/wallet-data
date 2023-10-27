package es.in2.wallet.data.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    @JsonProperty("userId")
    @NotBlank(message = "UserId cannot be blank")
    private String userId;

    @JsonProperty("username")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @JsonProperty("email")
    @NotBlank(message = "Email cannot be blank")
    private String email;
}
