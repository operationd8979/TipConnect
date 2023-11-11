package Tip.Connect.service;

import Tip.Connect.constant.ErrorMessages;
import Tip.Connect.email.EmailSender;
import Tip.Connect.model.*;
import Tip.Connect.model.reponse.AuthenticationReponse;
import Tip.Connect.model.reponse.ErrorReponse;
import Tip.Connect.model.reponse.HttpReponse;
import Tip.Connect.model.reponse.TinyUser;
import Tip.Connect.model.request.RegisterRequest;
import Tip.Connect.repository.AppUserRepository;
import Tip.Connect.utility.DataRetrieveUtil;
import Tip.Connect.validator.EmailValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    private final DataRetrieveUtil dataRetrieveUtil;

    private final String RESPONSE_SUCCESSFUL_MESSAGE = "Your account has been created successfully, an email has been sent to your inbox.Please confirm to activate your account.";

    @Transactional()
    public ResponseEntity<HttpReponse> register(HttpServletResponse httpServletResponse, RegisterRequest request) {
        try{
            boolean isValidEmail = emailValidator.test(request.getEmail());
            if(!isValidEmail){
                throw new IllegalStateException(ErrorMessages.INVALID_EMAIL.getMessage());
            }
            String token = appUserService.signUp(
                    new AppUser(
                            request.getLastName(),
                            request.getFirstName(),
                            request.getEmail(),
                            request.getPassword(),
                            AppUserRole.USER)
            );
            if(token==null){
                return ResponseEntity.ok(new ErrorReponse.builder().code(ErrorMessages.EXISTED_EMAIL.getCode()).errorMessage(ErrorMessages.EXISTED_EMAIL.getMessage()).build());
            }
            String link = "http://localhost:8080/api/v1/registration/confirm?token="+token;
            emailSender.send(request.getEmail(),buildEmail(request.getFirstName(),link));

            var userDetails = appUserRepository.findByEmail(request.getEmail()).orElseThrow(()->new IllegalStateException("User not found!"));
            TinyUser tinyUser = dataRetrieveUtil.TranslateAppUserToTiny(userDetails);

            final String accessToken = jwtService.generateToken(userDetails);
            final String refreshToken = jwtService.generateRefreshToken(userDetails);

            Cookie accessTokenCookie = new Cookie("access_token", accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setSecure(false);
            Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setSecure(false);

            httpServletResponse.addCookie(accessTokenCookie);
            httpServletResponse.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new AuthenticationReponse.builder()
                    .code(200).tinyUser(tinyUser)
                    .message(RESPONSE_SUCCESSFUL_MESSAGE).build());
        }catch (Exception ex){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.ok(new ErrorReponse.builder().code(ErrorMessages.UNKNOWN_EXCEPTION.getCode()).errorMessage(ex.getMessage()).build());
        }
    }


    public ResponseEntity<String> confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(()->new IllegalStateException("token not found"));
        if(confirmationToken.getConfirmedAt()!=null){
            //throw new IllegalStateException("token already confirmed");
            return ResponseEntity.ok("token already confirmed");
        }
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if(expiredAt.isBefore(LocalDateTime.now())){
            //throw new IllegalStateException("token expired");
            return ResponseEntity.ok("token expired");
        }
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());
        return ResponseEntity.ok("confirmed");
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
