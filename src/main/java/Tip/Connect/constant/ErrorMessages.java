package Tip.Connect.constant;

import Tip.Connect.model.ErrorReponse;

public class ErrorMessages {
    public static final String USERNAME_NOT_FOUND_ERROR_MESSAGE = "Username does not exist";
    public static final String ILLEGAL_PASSWORD_MESSAGE = "Invalid password";
    public static final String EXISTED_EMAIL_MESSAGE = "Email already exists";
    public static final String INVALID_EMAIL_MESSAGE = "Invalid email";
    public static final ErrorReponse UNKNOWN_EXCEPTION = new ErrorReponse(499,null);
    public static final ErrorReponse USERNAME_NOT_FOUND_ERROR = new ErrorReponse(403,USERNAME_NOT_FOUND_ERROR_MESSAGE);
    public static final ErrorReponse ILLEGAL_PASSWORD = new ErrorReponse(403,ILLEGAL_PASSWORD_MESSAGE);
    public static final ErrorReponse EXISTED_EMAIL = new ErrorReponse(403,EXISTED_EMAIL_MESSAGE);
    public static final ErrorReponse INVALID_EMAIL = new ErrorReponse(403,INVALID_EMAIL_MESSAGE);

}
