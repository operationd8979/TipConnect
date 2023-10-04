package Tip.Connect.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

//@Builder
//@Data
public class AuthenticationReponse extends HttpReponse {

    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("refresh_token")
    private String refreshToken;


    public AuthenticationReponse(int code){
        super(code);
    }

    public AuthenticationReponse(int code,String refreshToken,String fullName){
        super(code);
        this.refreshToken = refreshToken;
        this.fullName = fullName;
    }

    public AuthenticationReponse(builder builder){
        super(builder.code);
        this.refreshToken = builder.refreshToken;
        this.fullName = builder.fullName;
    }

    public static class builder{
        private int code;
        private String fullName;
        private String refreshToken;
        public builder code(int code){
            this.code = code;
            return this;
        }
        public builder fullName(String fullName){
            this.fullName = fullName;
            return this;
        }
        public builder refreshToken(String refreshToken){
            this.refreshToken = refreshToken;
            return this;
        }
        public AuthenticationReponse build(){
            return new AuthenticationReponse(this);
        }
    }


}
