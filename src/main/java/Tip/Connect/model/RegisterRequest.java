package Tip.Connect.model;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegisterRequest {

    private final String email;
    private final String firstName;
    private final String lastName;
    private final String password;

}
