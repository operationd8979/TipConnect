package Tip.Connect.model.reponse;

import Tip.Connect.model.Chat.WsRecord.RawChat;
import Tip.Connect.model.Relationship.TypeRelationShip;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class RelationShipResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;
    @JsonProperty("urlPic")
    private String urlPic;

    @JsonProperty("friends")
    private List<TinyUser> friends;
    @JsonProperty("type")
    private TypeRelationShip type;
    @JsonProperty("message")
    private RawChat message;
    @JsonProperty("timeStamp")
    private String timeStamp;
    @JsonProperty("isGroup")
    private boolean isGroup = false;

    public RelationShipResponse(String id, String name, String urlPic, List<TinyUser> friends, TypeRelationShip type, RawChat message) {
        this.id = id;
        this.name = name;
        this.urlPic = urlPic;
        this.friends = friends;
        this.type = type;
        this.message = message;
    }
    public RelationShipResponse(String id, String name, String urlPic, List<TinyUser> friends, TypeRelationShip type, RawChat message, boolean isGroup) {
        this.id = id;
        this.name = name;
        this.urlPic = urlPic;
        this.friends = friends;
        this.type = type;
        this.message = message;
        this.isGroup = isGroup;
    }
}
