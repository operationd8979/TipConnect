package Tip.Connect.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class AuthenticationReponse {

    @NonNull
    private int code;

    private String errorMessage;

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;


    public AuthenticationReponse(int code){
        this.code = code;
    }

}
