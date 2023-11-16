package Tip.Connect.model.reponse;

import Tip.Connect.model.Chat.Record;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.util.List;

public class SearchResponse extends HttpReponse{

    @JsonProperty("user_aim")
    private TinyUser userAim;

    @JsonProperty("messages")
    private List<Record> listMessage;

    public SearchResponse(@NonNull int code) {
        super(code);
    }

    public SearchResponse(builder builder){
        super(builder.code);
        this.userAim = builder.userAim;
        this.listMessage = builder.listMessage;
    }

    public static class builder{
        private int code;
        private TinyUser userAim;
        private List<Record> listMessage;
        public builder code(int code){
            this.code = code;
            return this;
        }
        public builder tinyUser(TinyUser userAim){
            this.userAim = userAim;
            return this;
        }
        public builder listMessage(List<Record> listMessage){
            this.listMessage = listMessage;
            return this;
        }
        public SearchResponse build(){
            return new SearchResponse(this);
        }
    }

}
