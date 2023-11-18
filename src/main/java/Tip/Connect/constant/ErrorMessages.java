package Tip.Connect.constant;

import Tip.Connect.model.reponse.ErrorReponse;

public class ErrorMessages {
    private static final String USERNAME_NOT_FOUND_ERROR_MESSAGE = "Username does not exist";
    private static final String ILLEGAL_PASSWORD_MESSAGE = "Invalid password";
    private static final String EXISTED_EMAIL_MESSAGE = "Email already exists";
    private static final String INVALID_EMAIL_MESSAGE = "Invalid email";
    private static final String INVALID_PARAMS_MESSAGE = "Invalid parameters";
    private static final String CONFLICT_UNIT_MESSAGE = "Conflict unit";
    private static final String NOT_FOUND_MESSAGE = "Not found";


    public static final ErrorReponse UNKNOWN_EXCEPTION = new ErrorReponse(499,null);
    public static final ErrorReponse USERNAME_NOT_FOUND_ERROR = new ErrorReponse(403,USERNAME_NOT_FOUND_ERROR_MESSAGE);
    public static final ErrorReponse ILLEGAL_PASSWORD = new ErrorReponse(401,ILLEGAL_PASSWORD_MESSAGE);
    public static final ErrorReponse EXISTED_EMAIL = new ErrorReponse(409,EXISTED_EMAIL_MESSAGE);
    public static final ErrorReponse INVALID_EMAIL = new ErrorReponse(403,INVALID_EMAIL_MESSAGE);
    public static final ErrorReponse INVALID_PARAMS = new ErrorReponse(403,INVALID_PARAMS_MESSAGE);
    public static final ErrorReponse CONFLICT_UNIT = new ErrorReponse(409,CONFLICT_UNIT_MESSAGE);
    public static final ErrorReponse NOT_FOUND = new ErrorReponse(404,NOT_FOUND_MESSAGE);

}
