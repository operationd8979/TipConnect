package Tip.Connect.model.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TinyUser {
    private String userId;
    private String fullName;
    private String role;
    private boolean enable;
    private String urlAvatar;

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
    public void setUrlAvatar(String urlAvatar) { this.urlAvatar = urlAvatar; }
}