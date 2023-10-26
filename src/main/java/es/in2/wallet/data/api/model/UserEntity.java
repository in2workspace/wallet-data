package es.in2.wallet.data.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @JsonProperty("id")
    private String id;  // User ID

    @JsonProperty("type")
    private String type = "userEntity";

    @JsonProperty("userData")
    private EntityAttribute<UserAttribute> userData;

    @JsonProperty("dids")
    private EntityAttribute<List<DidAttribute>> dids; // Array attribute to store multiple DIDs

    @JsonProperty("vcs")
    private EntityAttribute<List<VCAttribute>> vcs;   // Array attribute to store multiple VCs

}

