package Tip.Connect.service;

import Tip.Connect.constant.ErrorMessages;
import Tip.Connect.model.*;
import Tip.Connect.model.reponse.*;
import Tip.Connect.model.request.LoginRequest;
import Tip.Connect.repository.AppUserRepository;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final String USER_NOT_FOUND_MSG = "user with email %s not found";

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final JwtService jwtService;


//    public StreamingResponseBody getListFriend(String userId){
//        var userDetails = appUserRepository.findById(userId).orElse(null);
//        if(userDetails == null){
//            return null;
//        }
//        StreamingResponseBody stream = new StreamingResponseBody() {
//            @Override
//            public void writeTo(OutputStream outputStream) throws IOException {
//                List<FriendShip> listFriend = userDetails.getListFrienst();
//
//                AppUser user = new AppUser("Dung","Vo","operationd@gmail.com","123456", AppUserRole.USER);
//                user.setId("1");
//                for(int i = 0;i<10;i++){
//                    String str = Integer.toString(i);
//                    AppUser friend = new AppUser("Dung "+str,"Vo","operationd"+str+"@gmail.com" ,"123456", AppUserRole.USER);
//                    friend.setId("Tip"+str);
//                    listFriend.add(new FriendShip(Integer.toUnsignedLong(i),user,friend, TypeFriendShip.COMMON));
//                }
//
//                if(listFriend!=null){
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    listFriend.forEach(friendShip -> {
//                        try{
//                            String friendShipJson = objectMapper.writeValueAsString(translateFriendShip(friendShip));
//                            outputStream.write(friendShipJson.getBytes());
//                        }catch (IOException e){
//                            e.printStackTrace();
//                        }
//                    });
//                }
//            }
//        };
//        return stream;
//    }

    public StreamingResponseBody getListFriend(String userId){
        var userDetails = appUserRepository.findById(userId).orElse(null);
        if(userDetails == null){
            return null;
        }
        StreamingResponseBody stream = new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream outputStream) throws IOException {

                List<FriendShip> listFriend = userDetails.getListFrienst();
                AppUser user = new AppUser("Dung","Vo","operationd@gmail.com","123456", AppUserRole.USER);
                user.setId("1");
                for(int i = 0;i<100;i++){
                    String str = Integer.toString(i);
                    AppUser friend = new AppUser("Dung "+str,"Vo","operationd"+str+"@gmail.com" ,"123456", AppUserRole.USER);
                    friend.setId("Tip"+str);
                    listFriend.add(new FriendShip(Integer.toUnsignedLong(i),user,friend, TypeFriendShip.COMMON));
                }

                ObjectMapper objectMapper = new ObjectMapper();
                Stream<FriendShip> streamFriend = listFriend.stream();
                JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);

                if(streamFriend!=null){
                    try{
                        jsonGenerator.writeStartArray();
                        Iterator<FriendShip> friendShipIterator = streamFriend.iterator();
                        while(friendShipIterator.hasNext()) {
                            FriendShip friendShip = friendShipIterator.next();
                            jsonGenerator.writeObject(friendShip);

                            try{
                                Thread.sleep(300);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }

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
            }
        };
        return stream;
    }

    public FriendShipRespone translateFriendShip(FriendShip friendShip){
        FriendShipRespone friendShipRespone = new FriendShipRespone();
        friendShipRespone.setId(friendShip.getFriendShipId());
        friendShipRespone.setType(friendShip.getType());
        TinyUser tinyUser = new TinyUser();
        AppUser user = friendShip.getUser2();
        tinyUser.setUserId(user.getId());
        tinyUser.setFullName(user.getFirstName()+" "+user.getLastName());
        tinyUser.setUrlAvatar(user.getUrlAvatar());
        friendShipRespone.setFriend(tinyUser);
        return friendShipRespone;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        return appUserRepository.findByEmail(email)
//                .orElseThrow(()->new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,email)));
        return appUserRepository.findByEmail(email).orElse(null);
    }

    @Transactional
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
            String userId = userDetails.getId();
            String fullName = userDetails.getFirstName()+" "+userDetails.getLastName();
            String role = userDetails.getAppUserRole().toString();
            String urlAvatar = userDetails.getUrlAvatar();
            boolean enable = userDetails.getEnabled();

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
                    .userId(userId)
                    .fullName(fullName)
                    .role(role)
                    .enable(enable)
                    .urlAvatar(urlAvatar)
                    .message(null)
                    .build();
        }catch (IllegalStateException ex){
            return new ErrorReponse.builder()
                    .code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode())
                    .errorMessage(ErrorMessages.UNKNOWN_EXCEPTION.getMessage())
                    .build();
        }
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
