package Tip.Connect.model.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public class SearchResponse extends HttpReponse{

    @JsonProperty("user")
    private TinyUser user;

    public SearchResponse(@NonNull int code) {
        super(code);
    }
}
