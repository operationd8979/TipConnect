package Tip.Connect.model.reponse;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TinyUser {
    private String userID;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String role;
    private boolean enable;
    private String urlAvatar;
    private StateAimUser state = StateAimUser.AVAIBLE;

    public TinyUser(String userID,String email,String firstName,String lastName,String fullName,String role,boolean enable,String urlAvatar){
        this.userID = userID;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.role = role;
        this.enable = enable;
        this.urlAvatar = urlAvatar;
    }

    public TinyUser(String userID,String firstName,String lastName,String fullName,String urlAvatar){
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.role = role;
        this.enable = enable;
        this.urlAvatar = urlAvatar;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
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
        this.userID = userId;
    }
    public void setUrlAvatar(String urlAvatar) { this.urlAvatar = urlAvatar; }
    public void setState(StateAimUser state) { this.state = state; }
}