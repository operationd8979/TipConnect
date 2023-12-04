package Tip.Connect.model.reponse;

import Tip.Connect.model.Chat.Record;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.util.List;

public class SearchResponse extends HttpResponse{

    @JsonProperty("user_aim")
    private TinyUser userAim;

//    @JsonProperty("messages")
//    private List<Record> listMessage;

    @JsonProperty("offset")
    private int offset;

    public SearchResponse(@NonNull int code) {
        super(code);
    }

    public SearchResponse(builder builder){
        super(builder.code);
        this.userAim = builder.userAim;
//        this.listMessage = builder.listMessage;
        this.offset = builder.offset;
    }

    public static class builder{
        private int code;
        private TinyUser userAim;
//        private List<Record> listMessage;
        private int offset;
        public builder code(int code){
            this.code = code;
            return this;
        }
        public builder tinyUser(TinyUser userAim){
            this.userAim = userAim;
            return this;
        }
//        public builder listMessage(List<Record> listMessage){
//            this.listMessage = listMessage;
//            return this;
//        }
        public builder offset(int offset){
            this.offset = offset;
            return this;
        }
        public SearchResponse build(){
            return new SearchResponse(this);
        }
    }

}
