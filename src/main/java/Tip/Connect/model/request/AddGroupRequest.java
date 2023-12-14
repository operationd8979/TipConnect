package Tip.Connect.model.request;

import lombok.ToString;

import java.util.List;

public record AddGroupRequest(String nameGroup, String urlAvatar, String[] listUserID) {

}
