package Tip.Connect.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorReponse extends HttpReponse{

    @JsonProperty("error_message")
    private String errorMessage;

    public ErrorReponse(int code,String message){
        super(code);
        this.errorMessage = message;
    }

    public String getMessage(){
        return errorMessage;
    }

    public ErrorReponse(ErrorReponse.builder builder){
        super(builder.code);
        this.errorMessage = builder.errorMessage;
    }

    public static class builder{
        private int code;
        private String errorMessage;
        public ErrorReponse.builder code(int code){
            this.code = code;
            return this;
        }
        public ErrorReponse.builder errorMessage(String errorMessage){
            this.errorMessage = errorMessage;
            return this;
        }
        public ErrorReponse build(){
            return new ErrorReponse(this);
        }
    }

}