package Tip.Connect.model.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageResponse extends HttpResponse{

    @JsonProperty("message")
    private String message;

    public MessageResponse(int code,String message){
        super(code);
        this.message = message;
    }
}
