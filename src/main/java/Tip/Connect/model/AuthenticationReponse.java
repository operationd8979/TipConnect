package Tip.Connect.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

//@Builder
//@Data
public class AuthenticationReponse extends HttpReponse {

    class User {
        private String fullName;
        private String role;

        public User() {
        }

        public User(String fullName, String role) {
            this.fullName = fullName != null ? fullName : "";
            this.role = role != null ? role : "";
        }


        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    @JsonProperty("user")
    private User user;
    @JsonProperty("message")
    private String message;


    public AuthenticationReponse(int code){
        super(code);
    }

    public AuthenticationReponse(int code,String message,User user){
        super(code);
        this.message = message;
        this.user = user;
    }

    public AuthenticationReponse(builder builder){
        super(builder.code);
        this.message = builder.message;
        this.user = new User(builder.fullName,builder.role);
    }

    public static class builder{
        private int code;
        private String fullName;
        private String role;
        private String message;
        public builder code(int code){
            this.code = code;
            return this;
        }
        public builder fullName(String fullName){
            this.fullName = fullName;
            return this;
        }
        public builder role(String role){
            this.role = role;
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
