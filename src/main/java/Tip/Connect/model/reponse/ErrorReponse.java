package Tip.Connect.model.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public class ErrorReponse extends HttpResponse{

    @JsonProperty("error_message")
    private String errorMessage;

    public ErrorReponse(@NonNull int code, String message){
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