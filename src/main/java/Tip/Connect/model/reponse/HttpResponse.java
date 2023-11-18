package Tip.Connect.model.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class HttpResponse {
    @NonNull
    @JsonProperty("code")
    protected int code;

    public int getCode(){
        return code;
    }

    public void setCode(int code){
        this.code = code;
    }

}
