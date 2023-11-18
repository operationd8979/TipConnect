package Tip.Connect.service;

import Tip.Connect.constant.ErrorMessages;
import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Auth.ConfirmationToken;
import Tip.Connect.model.Relationship.FriendRequest;
import Tip.Connect.model.Relationship.FriendShip;
import Tip.Connect.model.Chat.Record;
import Tip.Connect.model.reponse.*;
import Tip.Connect.model.request.LoginRequest;
import Tip.Connect.model.request.UpdateRequest;
import Tip.Connect.repository.AppUserRepository;
import Tip.Connect.repository.FriendRequestRepository;
import Tip.Connect.utility.DataRetrieveUtil;
import Tip.Connect.validator.EmailValidator;
import Tip.Connect.validator.PhoneValidator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final FriendRequestRepository friendRequestRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final JwtService jwtService;

    private final DataRetrieveUtil dataRetrieveUtil;

    private final EmailValidator emailValidator;
    private final PhoneValidator phoneValidator;


    public TinyUser searchAimUser(String userID,String query){
        var user = appUserRepository.findById(userID).orElse(null);
        if(emailValidator.test(query)||user==null){
            AppUser queryUser = loadUserByUsername(query);
            if(queryUser!=null){
                TinyUser aimUser = dataRetrieveUtil.TranslateAppUserToTiny(queryUser);
                if(aimUser.getUserID().equals(userID)){
                    aimUser.setState(StateAimUser.SELF);
                }
                if(user.getListFrienst().stream().anyMatch(f->f.getFriend().getEmail().equals(query))){
                    aimUser.setState(StateAimUser.FRIEND);
                }
                if(user.getFriendRequests().stream().anyMatch(r->r.getReceiver().getEmail().equals(query))){
                    aimUser.setState(StateAimUser.ONSEND);
                }
                if(queryUser.getFriendRequests().stream().anyMatch(r->r.getReceiver().getEmail().equals(user.getEmail()))){
                    aimUser.setState(StateAimUser.ONWAIT);
                }
                return aimUser;
            }
        }
        return null;
    }

    public List<Record> searchMessages(String userID,String query){
        List<Record> recordList = new ArrayList<>();
        return recordList;
    }

    public HttpReponse getUserInfo(String userID){
        var userDetails = appUserRepository.findById(userID).orElse(null);
        if(userDetails == null){
            return null;
        }
        TinyUser tinyUser = dataRetrieveUtil.TranslateAppUserToTiny(userDetails);
        return new AuthenticationReponse.builder()
                .code(200)
                .tinyUser(tinyUser)
                .message(null)
                .build();
    }

    public HttpReponse updateUserInfo(HttpServletRequest request, UpdateRequest updateRequest){
        var user = getUserByHttpRequest(request);
        if(user==null){
            return new ErrorReponse.builder().code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode()).errorMessage(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage()).build();
        }
        if(!bCryptPasswordEncoder.matches(updateRequest.password(),user.getPassword())){
            return new ErrorReponse.builder().code(ErrorMessages.ILLEGAL_PASSWORD.getCode()).errorMessage(ErrorMessages.ILLEGAL_PASSWORD.getMessage()).build();
        }
        user.setFirstName(updateRequest.firstName());
        user.setLastName(updateRequest.lastName());
        if(updateRequest.newPassword()!=""&&updateRequest.newPassword()!=null){
            user.setPassword(bCryptPasswordEncoder.encode(updateRequest.newPassword()));
        }
        appUserRepository.save(user);
        TinyUser tinyUser = dataRetrieveUtil.TranslateAppUserToTiny(user);
        return new AuthenticationReponse.builder().code(200).tinyUser(tinyUser).build();
    }

    public HttpReponse updateAvatar(HttpServletRequest request, String urlAvatar){
        var user = getUserByHttpRequest(request);
        if(user==null){
            return new ErrorReponse.builder().code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode()).errorMessage(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage()).build();
        }
        user.setUrlAvatar(urlAvatar);
        appUserRepository.save(user);
        TinyUser tinyUser = dataRetrieveUtil.TranslateAppUserToTiny(user);
        return new AuthenticationReponse.builder().code(200).message("Upload avatar successfully!").build();
    }

    public StreamingResponseBody getListFriend(String userID){
        var userDetails = appUserRepository.findById(userID).orElse(null);
        if(userDetails == null){
            return null;
        }
        StreamingResponseBody stream = outputStream -> {
            List<FriendShip> listRaw = userDetails.getListFrienst();
            List<FriendShipRespone> listFriend = dataRetrieveUtil.TranslateFriendShipToTiny(listRaw);

//            String urlAvatar = "https://firebasestorage.googleapis.com/v0/b/tipconnect-14d4b.appspot.com/o/Default%2FdefaultAvatar.jpg?alt=media&token=a0a33d34-e4c4-4ed0-8b52-6da79b7b048a";
//            for(int i = 0;i<102;i++){
//                String str = Integer.toString(i);
//                TinyUser friend = new TinyUser(str,"Name",str,"Name "+str,urlAvatar);
//                listFriend.add(new FriendShipRespone(Integer.toUnsignedLong(i),friend, TypeFriendShip.COMMON));
//            }

            ObjectMapper objectMapper = new ObjectMapper();
            Stream<FriendShipRespone> streamFriend = listFriend.stream();
            JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);

            if(streamFriend!=null){
                try{
                    int i = 0;
                    Iterator<FriendShipRespone> friendShipIterator = streamFriend.iterator();
                    jsonGenerator.writeStartArray();
                    while(friendShipIterator.hasNext()) {
                        FriendShipRespone friendShipRespone = friendShipIterator.next();
                        jsonGenerator.writeObject(friendShipRespone);
                        i++;
                        if(i==10){
                            i = 0;
                            jsonGenerator.writeEndArray();
                            jsonGenerator.writeStartArray();
                        }

//                            try{
//                                Thread.sleep(300);
//                            }catch (InterruptedException e){
//                                e.printStackTrace();
//                            }

                    }
                    jsonGenerator.writeEndArray();
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    if(streamFriend != null) {
                        streamFriend.close();
                    }
                    if(jsonGenerator != null)  {
                        jsonGenerator.close();
                    }
                }
            }
        };
        return stream;
    }

    public StreamingResponseBody getFriendRequests(String userID) {
        AppUser userDetails = appUserRepository.findById(userID).orElse(null);
        if(userDetails == null){
            return null;
        }
        StreamingResponseBody stream = outputStream -> {
            List<FriendRequest> listRaw = friendRequestRepository.findAll().stream().filter(r->r.getReceiver()==userDetails).toList();
            List<FriendRResponse> listFRResponse = dataRetrieveUtil.TranslateFriendRequestToResponse(listRaw);

            ObjectMapper objectMapper = new ObjectMapper();
            Stream<FriendRResponse> streamFRequest = listFRResponse.stream();
            JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);

            if(streamFRequest!=null){
                try{
                    Iterator<FriendRResponse> friendRResponseIterator = streamFRequest.iterator();
                    jsonGenerator.writeStartArray();
                    while(friendRResponseIterator.hasNext()) {
                        FriendRResponse friendRResponse = friendRResponseIterator.next();
                        jsonGenerator.writeObject(friendRResponse);
                    }
                    jsonGenerator.writeEndArray();
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    if(streamFRequest != null) {
                        streamFRequest.close();
                    }
                    if(jsonGenerator != null)  {
                        jsonGenerator.close();
                    }
                }
            }
        };
        return stream;
    }

    public void addFriend(String userID,String friendID){
        AppUser user = appUserRepository.findById(userID).orElse(null);
        AppUser friend = appUserRepository.findById(friendID).orElse(null);
        if(user==null||friend==null){
            return;
        }
        FriendRequest request = new FriendRequest(user,friend);
        friendRequestRepository.save(request);
    }

    @Transactional()
    public String signUp(AppUser appUser) {
        try{
            boolean userExists = appUserRepository.findByEmail(appUser.getEmail()).isPresent();
            if(userExists){
                throw new IllegalStateException(ErrorMessages.EXISTED_EMAIL.getMessage());
            }
            String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
            appUser.setPassword(encodedPassword);
            appUserRepository.save(appUser);
            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    appUser
            );
            confirmationTokenService.saveConfirmationToken(confirmationToken);
            return token;
        }catch (Exception ex){
            return null;
        }
    }

    public HttpReponse login(LoginRequest request, HttpServletResponse reponse) {
        try{
            var userDetails = appUserRepository.findByEmail(request.email()).orElseThrow(()->new IllegalStateException("User not found!"));
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

            reponse.addCookie(accessTokenCookie);
            reponse.addCookie(refreshTokenCookie);

            return new AuthenticationReponse.builder()
                    .code(200)
                    .tinyUser(tinyUser)
                    .message(null)
                    .build();
        }catch (IllegalStateException ex){
            return new ErrorReponse.builder()
                    .code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode())
                    .errorMessage(ErrorMessages.UNKNOWN_EXCEPTION.getMessage())
                    .build();
        }
    }

    @Override
    public AppUser loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email).orElse(null);
    }

    public AppUser loadUserByUserid(String userID) throws UsernameNotFoundException {
        return appUserRepository.findById(userID).orElse(null);
    }

    public String getIdByEmail(String email) throws UsernameNotFoundException {
        var user = appUserRepository.findByEmail(email).orElse(null);
        return user!=null?user.getId():null;
    }

    public String getUserIdByHttpRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String token = null;
        if(cookies!=null){
            for(Cookie cookie: cookies){
                if("access_token".equals(cookie.getName())){
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if(token==null){
            return null;
        }
        String email = jwtService.extractUsername(token);
        String userId = getIdByEmail(email);
        return userId;
    }

    public AppUser getUserByHttpRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String token = null;
        if(cookies!=null){
            for(Cookie cookie: cookies){
                if("access_token".equals(cookie.getName())){
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if(token==null){
            return null;
        }
        String email = jwtService.extractUsername(token);
        AppUser user = loadUserByUsername(email);
        return user;
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }

    public AuthenticationReponse refreshToken(String refreshToken) {
        try{
            final String username = jwtService.extractUsername(refreshToken);
            if(username!= null){
                UserDetails userDetails = loadUserByUsername(username);
                if(jwtService.isTokenValid(refreshToken,userDetails)){
                    return new AuthenticationReponse.builder()
                            .fullName(jwtService.generateToken(userDetails)).build();
                }
            }
            return null;
        }catch (Exception ex){
            return new AuthenticationReponse.builder()
                    .fullName(ex.getMessage()).build();
        }
    }


}
