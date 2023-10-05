package Tip.Connect.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

//@Builder
//@Data
public class AuthenticationReponse extends HttpReponse {

    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("message")
    private String message;


    public AuthenticationReponse(int code){
        super(code);
    }

    public AuthenticationReponse(int code,String message,String fullName){
        super(code);
        this.message = message;
        this.fullName = fullName;
    }

    public AuthenticationReponse(builder builder){
        super(builder.code);
        this.message = builder.message;
        this.fullName = builder.fullName;
    }

    public static class builder{
        private int code;
        private String fullName;
        private String message;
        public builder code(int code){
            this.code = code;
            return this;
        }
        public builder fullName(String fullName){
            this.fullName = fullName;
            return this;
        }
        public builder message(String message){
            this.message = message;
            return this;
        }
        public AuthenticationReponse build(){
            return new AuthenticationReponse(this);
        }
    }


}
