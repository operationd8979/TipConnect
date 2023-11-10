package Tip.Connect.model.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;


public class AuthenticationReponse extends HttpReponse {

    @JsonProperty("user")
    private TinyUser user;
    @JsonProperty("message")
    private String message;


    public AuthenticationReponse(int code){
        super(code);
    }

    public AuthenticationReponse(@NonNull int code, String message, TinyUser user){
        super(code);
        this.message = message;
        this.user = user;
    }

    public AuthenticationReponse(builder builder){
        super(builder.code);
        this.message = builder.message;
        this.user = builder.user;
    }

    public static class builder{
        private int code;
        private TinyUser user;
        private String message;
        public builder code(int code){
            this.code = code;
            return this;
        }
        public builder userId(String userId){
            if(this.user==null){
                this.user = new TinyUser();
            }
            this.user.setUserId(userId);
            return this;
        }
        public builder email(String email){
            if(this.user==null){
                this.user = new TinyUser();
            }
            this.user.setEmail(email);
            return this;
        }
        public builder firstName(String firstName){
            if(this.user==null){
                this.user = new TinyUser();
            }
            this.user.setFirstName(firstName);
            return this;
        }
        public builder lastName(String lastName){
            if(this.user==null){
                this.user = new TinyUser();
            }
            this.user.setLastName(lastName);
            return this;
        }
        public builder fullName(String fullName){
            if(this.user==null){
                this.user = new TinyUser();
            }
            this.user.setFullName(fullName);
            return this;
        }
        public builder role(String role){
            if(this.user==null){
                this.user = new TinyUser();
            }
            this.user.setRole(role);
            return this;
        }
        public builder enable(Boolean enable){
            if(this.user==null){
                this.user = new TinyUser();
            }
            this.user.setEnable(enable);
            return this;
        }
        public builder urlAvatar(String urlAvatar){
            if(this.user==null){
                this.user = new TinyUser();
            }
            this.user.setUrlAvatar(urlAvatar);
            return this;
        }
        public builder message(String message){
            this.message = message;
            return this;
        }
        public builder tinyUser(TinyUser user){
            this.user = user;
            return this;
        }
        public AuthenticationReponse build(){
            return new AuthenticationReponse(this);
        }
    }


}
