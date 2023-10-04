package Tip.Connect.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class HttpReponse {
    @NonNull
    @JsonProperty("code")
    protected int code;

    public int getCode(){
        return code;
    }

}
