package Tip.Connect.service;

import Tip.Connect.constant.ErrorMessages;
import Tip.Connect.controller.LiveController;
import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Auth.ConfirmationToken;
import Tip.Connect.model.Chat.GifItem;
import Tip.Connect.model.Chat.RecordType;
import Tip.Connect.model.Chat.WsRecord.MessageChat;
import Tip.Connect.model.Chat.WsRecord.NotificationChat;
import Tip.Connect.model.Chat.WsRecord.RawChat;
import Tip.Connect.model.Relationship.*;
import Tip.Connect.model.Chat.Record;
import Tip.Connect.model.reponse.*;
import Tip.Connect.model.request.LoginRequest;
import Tip.Connect.model.request.UpdateRequest;
import Tip.Connect.repository.*;
import Tip.Connect.utility.DataRetrieveUtil;
import Tip.Connect.validator.EmailValidator;
import Tip.Connect.validator.PhoneValidator;
import Tip.Connect.websocket.config.PrincipalUser;
import Tip.Connect.websocket.config.UserInterceptor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final RelationShipRepository relationShipRepository;
    private final DetailRelationShipRepository detailRelationShipRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final GifItemRepository gifItemRepository;


    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final JwtService jwtService;

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DataRetrieveUtil dataRetrieveUtil;

    private final EmailValidator emailValidator;
    private final PhoneValidator phoneValidator;

    public StreamingResponseBody getListLive(String userID){
        var userDetails = appUserRepository.findById(userID).orElse(null);
        if(userDetails == null){
            return null;
        }
        StreamingResponseBody stream = outputStream -> {
            List<String> listLive = LiveController.liveList;
            List<AppUser> listUserLive = new ArrayList<>();
            for(String l:listLive){
                AppUser live = loadUserByUserid(l);
                listUserLive.add(live);
            }
            List<TinyUser> listUserLiveResponse = new ArrayList<>();
            for(AppUser liver: listUserLive){
                listUserLiveResponse.add(dataRetrieveUtil.TranslateAppUserToTiny(liver));
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Stream<TinyUser> streamLive = listUserLiveResponse.stream();
            JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
            if(streamLive!=null){
                try{
                    Iterator<TinyUser> iteratorLive = streamLive.iterator();
                    jsonGenerator.writeStartArray();
                    while(iteratorLive.hasNext()) {
                        TinyUser liver = iteratorLive.next();
                        jsonGenerator.writeObject(liver);
                    }
                    jsonGenerator.writeEndArray();
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    if(streamLive != null) {
                        streamLive.close();
                    }
                    if(jsonGenerator != null)  {
                        jsonGenerator.close();
                    }
                }
            }
        };
        return stream;
    }

    public TinyUser searchAimUser(String userID,String query){
        var user = appUserRepository.findById(userID).orElse(null);
        if(emailValidator.test(query)||user==null){
            AppUser queryUser = loadUserByUsername(query);
            if(queryUser!=null){
                TinyUser aimUser = dataRetrieveUtil.TranslateAppUserToTiny(queryUser);
                if(aimUser.getUserID().equals(userID)){
                    aimUser.setState(StateAimUser.SELF);
                }
                if(user.getSentFriendRequests().stream().anyMatch(r->r.getReceiver().getEmail().equals(query)&&!r.isEnable())){
                    aimUser.setState(StateAimUser.ONSEND);
                }
                if(user.getReceivedFriendRequests().stream().anyMatch(r->r.getSender().getEmail().equals(query)&&!r.isEnable())){
                    aimUser.setState(StateAimUser.ONWAIT);
                }
                List<FriendShip> friendShips = user.getDetailRelationShipList().stream().map(d->d.getRelationShip()).filter(FriendShip.class::isInstance).map(FriendShip.class::cast).collect(Collectors.toList());
                if(friendShips.stream().anyMatch(f->f.getListDetailRelationShip().stream().map(d->d.getUser()).findFirst().orElse(null).getEmail().equals(query))){
                    aimUser.setState(StateAimUser.FRIEND);
                }
                return aimUser;
            }
        }
        return null;
    }

    public List<RawChat> searchMessages(String userID,String query){
        AppUser user = loadUserByUserid(userID);
        if(user == null){
            return null;
        }
        List<RelationShip> myRelationShips = user.getDetailRelationShipList().stream().map(d->d.getRelationShip()).collect(Collectors.toList());
        List<Record> listChat = new ArrayList<>();
        for(RelationShip relationShip: myRelationShips){
            listChat.addAll(relationShip.getListChat());
        }
        List<Record> messages = listChat.stream().filter(c->c.isContainContent(query)&&c.getType().equals(RecordType.MESSAGE)).collect(Collectors.toList());
        messages.sort(Comparator.comparingLong(Record::getTimeStampLong).reversed());
        return dataRetrieveUtil.TranslateRecordToResponse(messages,userID);
    }

    public HttpResponse getUserInfo(String userID){
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

    public HttpResponse updateUserInfo(HttpServletRequest request, UpdateRequest updateRequest){
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

    public HttpResponse updateAvatar(HttpServletRequest request, String urlAvatar){
        var user = getUserByHttpRequest(request);
        if(user==null){
            return new ErrorReponse.builder().code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode()).errorMessage(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage()).build();
        }
        user.setUrlAvatar(urlAvatar);
        appUserRepository.save(user);
        TinyUser tinyUser = dataRetrieveUtil.TranslateAppUserToTiny(user);
        return new AuthenticationReponse.builder().code(200).message("Upload avatar successfully!").build();
    }

    public HttpResponse updateTypeFriend(String userID, String relationShipID, TypeRelationShip type){
        AppUser user = loadUserByUserid(userID);
        if(user!=null){
            DetailRelationShip detailRelationShip = user.getDetailRelationShipList().stream().filter(d->d.getRelationShip().getRelationshipID().equals(relationShipID)).findFirst().orElse(null);
            if(detailRelationShip!=null){
                detailRelationShip.setType(type);
                detailRelationShipRepository.save(detailRelationShip);
                return new MessageResponse(200,"OK");
            }
        }
        return new ErrorReponse.builder().code(ErrorMessages.NOT_FOUND.getCode()).errorMessage(ErrorMessages.NOT_FOUND.getMessage()).build();
    }

    @Transactional
    public List<String> getListFriendID(String userID){
        var userDetails = appUserRepository.findById(userID).orElse(null);
        if(userDetails == null){
            return null;
        }
        List<RelationShip> relationShips = userDetails.getDetailRelationShipList()
                .stream().map(d->d.getRelationShip()).collect(Collectors.toList());
        List<FriendShip> friendShips = relationShips.stream()
                .filter(FriendShip.class::isInstance).map(FriendShip.class::cast).collect(Collectors.toList());
        List<String> friendIDs = friendShips.stream().map(f->f.getListAppUser().stream().filter(a->!a.equals(userDetails)).findFirst().orElse(null).getId()).collect(Collectors.toList());
        return friendIDs;
    }

    public StreamingResponseBody getListFriend(String userID){
        var userDetails = appUserRepository.findById(userID).orElse(null);
        if(userDetails == null){
            return null;
        }
        StreamingResponseBody stream = outputStream -> {
            List<DetailRelationShip> detailRelationShips = userDetails.getDetailRelationShipList();
            List<RelationShipResponse> listFriend = dataRetrieveUtil.TranslateRelationShipToResponse(detailRelationShips);
            for(RelationShipResponse response: listFriend){
                System.out.println(response);
            }
            //check online status
            Map<String, PrincipalUser> map = UserInterceptor.loggedInUsers;
            ObjectMapper objectMapper = new ObjectMapper();
            Stream<RelationShipResponse> streamFriend = listFriend.stream();
            JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
            if(streamFriend!=null){
                try{
                    Iterator<RelationShipResponse> friendShipIterator = streamFriend.iterator();
                    jsonGenerator.writeStartArray();
                    while(friendShipIterator.hasNext()) {
                        RelationShipResponse relationShipResponse = friendShipIterator.next();
                        if(!relationShipResponse.isGroup()){
                            if(map.containsKey(relationShipResponse.getFriends().stream().findFirst().orElse(null).getUserID())){
                                relationShipResponse.setTimeStamp(Long.toString(new Date().getTime()));
                            }
                        }
                        jsonGenerator.writeObject(relationShipResponse);
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
            List<FriendRequest> listRaw = friendRequestRepository.findAll().stream().filter(r->r.getReceiver()==userDetails&&!r.isEnable()).toList();
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

    public StreamingResponseBody getMessages(String userID,String relationShipID,String offset,int limit) {
        AppUser user = appUserRepository.findById(userID).orElse(null);
        RelationShip relationShip = relationShipRepository.findById(relationShipID).orElse(null);
        if(user == null|| relationShip==null){
            return null;
        }
        StreamingResponseBody stream = outputStream -> {
            List<Record> listChat = relationShip.getListChat();
            listChat.sort(Comparator.comparingLong(Record::getTimeStampLong));
            String newOffset = "";
            if(offset.equals("")){
                int length = listChat.size();
                int start = Math.max(length - limit, 0);
                listChat = listChat.subList(start, length);
                if(listChat.size()>0){
                    newOffset = listChat.get(0).getRecordID();
                }
            }
            else{
                int index = -1;
                for (int i = 0; i < listChat.size(); i++) {
                    if (listChat.get(i).getRecordID().equals(offset)) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    listChat = listChat.subList(0, index);
                }
                if (index != 0){
                    int length = listChat.size();
                    int start = Math.max(length - limit, 0);
                    listChat = listChat.subList(start, length);
                    newOffset = listChat.get(0).getRecordID();
                }
            }
            List<RawChat> listChatResponse = dataRetrieveUtil.TranslateRecordToResponse(listChat,userID);

            if(listChatResponse.size()>0)
                listChatResponse.get(0).setOffset(newOffset);

            ObjectMapper objectMapper = new ObjectMapper();
            Stream<RawChat> rawChatStream = listChatResponse.stream();
            JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
            if(rawChatStream!=null){
                try{
                    Iterator<RawChat> rawChatIterator = rawChatStream.iterator();
                    jsonGenerator.writeStartArray();
                    while(rawChatIterator.hasNext()) {
                        RawChat rawChat = rawChatIterator.next();
                        jsonGenerator.writeObject(rawChat);
                    }
                    jsonGenerator.writeEndArray();
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    if(rawChatStream != null) {
                        rawChatStream.close();
                    }
                    if(jsonGenerator != null)  {
                        jsonGenerator.close();
                    }
                }
            }
        };
        return stream;
    }

    public StreamingResponseBody getGifItem(){
        StreamingResponseBody stream = outputStream -> {
            List<GifItem> listGifItem = gifItemRepository.findAll();
            ObjectMapper objectMapper = new ObjectMapper();
            Stream<GifItem> gifItemStream = listGifItem.stream();
            JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
            if(gifItemStream!=null){
                try{
                    Iterator<GifItem> gifItemIterator = gifItemStream.iterator();
                    jsonGenerator.writeStartArray();
                    while(gifItemIterator.hasNext()) {
                        GifItem gifItemResponse = gifItemIterator.next();
                        jsonGenerator.writeObject(gifItemResponse);
                    }
                    jsonGenerator.writeEndArray();
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    if(gifItemStream != null) {
                        gifItemStream.close();
                    }
                    if(jsonGenerator != null)  {
                        jsonGenerator.close();
                    }
                }
            }
        };
        return stream;
    }

    public HttpResponse addFriend(String userID,String friendID){
        AppUser user = appUserRepository.findById(userID).orElse(null);
        AppUser friend = appUserRepository.findById(friendID).orElse(null);
        if(user==null||friend==null){
            return new MessageResponse(ErrorMessages.INVALID_PARAMS.getCode(), ErrorMessages.INVALID_PARAMS.getMessage());
        }
        FriendRequest request = friendRequestRepository.findAll().stream().filter(r->r.getSender().getId().equals(user.getId())&&r.getReceiver().getId().equals(friend.getId())).findFirst().orElse(null);
        if(request!=null){
            return new MessageResponse(ErrorMessages.CONFLICT_UNIT.getCode(), ErrorMessages.CONFLICT_UNIT.getMessage());
        }
        request = new FriendRequest(user,friend);
        friendRequestRepository.save(request);

        FriendRResponse friendRResponse = dataRetrieveUtil.TranslateFriendRequestToTiny(request);
        simpMessagingTemplate.convertAndSendToUser(friendID,"/private",new NotificationChat(friendRResponse,101));

        return new MessageResponse(200,"OK");
    }

    public HttpResponse cancelFriendRequest(String userID,String friendID){
        AppUser user = appUserRepository.findById(userID).orElse(null);
        AppUser friend = appUserRepository.findById(friendID).orElse(null);
        if(user==null||friend==null){
            return new MessageResponse(ErrorMessages.INVALID_PARAMS.getCode(), ErrorMessages.INVALID_PARAMS.getMessage());
        }
        FriendRequest request = friendRequestRepository.findAll().stream().filter(r->r.getSender().getId().equals(user.getId())&&r.getReceiver().getId().equals(friend.getId())).findFirst().orElse(null);
        if(request==null){
            return new MessageResponse(ErrorMessages.NOT_FOUND.getCode(), ErrorMessages.NOT_FOUND.getMessage());
        }
        if(request.isEnable()){
            return new MessageResponse(ErrorMessages.CONFLICT_UNIT.getCode(), ErrorMessages.CONFLICT_UNIT.getMessage());
        }
        friendRequestRepository.delete(request);
        FriendRResponse friendRResponse = dataRetrieveUtil.TranslateFriendRequestToTiny(request);
        simpMessagingTemplate.convertAndSendToUser(friendID,"/private",new NotificationChat(friendRResponse,102));
        return new MessageResponse(200,"OK");
    }

    @Transactional()
    public HttpResponse acceptFriendRequest(String userID,String requestID){
        FriendRequest friendRequest = friendRequestRepository.findById(requestID).orElse(null);
        if(friendRequest==null){
            return new MessageResponse(ErrorMessages.NOT_FOUND.getCode(), ErrorMessages.NOT_FOUND.getMessage());
        }
        if(friendRequest.isEnable()){
            return new MessageResponse(ErrorMessages.CONFLICT_UNIT.getCode(), ErrorMessages.CONFLICT_UNIT.getMessage());
        }
        AppUser user1 = friendRequest.getSender();
        AppUser user2 = friendRequest.getReceiver();
        friendRequest.setEnable(true);
        friendRequestRepository.save(friendRequest);

        FriendShip friendShip = new FriendShip(friendRequest);
        relationShipRepository.save(friendShip);

        DetailRelationShip detailRelationShip1 = new DetailRelationShip(user1,friendShip);
        DetailRelationShip detailRelationShip2 = new DetailRelationShip(user2,friendShip);
        detailRelationShipRepository.save(detailRelationShip1);
        detailRelationShipRepository.save(detailRelationShip2);

        TinyUser tinyUser1 = dataRetrieveUtil.TranslateAppUserToTiny(user1);
        TinyUser tinyUser2 = dataRetrieveUtil.TranslateAppUserToTiny(user2);

        RelationShipResponse relationShipResponse1 = new RelationShipResponse(friendShip.getRelationshipID(),user2.getFullName(),user2.getUrlAvatar(),List.of(tinyUser2),detailRelationShip1.getType(),null);
        RelationShipResponse relationShipResponse2 = new RelationShipResponse(friendShip.getRelationshipID(),user1.getFullName(),user1.getUrlAvatar(),List.of(tinyUser1),detailRelationShip1.getType(),null);
        simpMessagingTemplate.convertAndSendToUser(user1.getId(),"/private",new NotificationChat(relationShipResponse1,101));
        simpMessagingTemplate.convertAndSendToUser(user2.getId(),"/private",new NotificationChat(relationShipResponse2,101));

        return new MessageResponse(200,"OK");
    }

    @Transactional()
    public HttpResponse denyFriendRequest(String userID,String requestID){
        FriendRequest friendRequest = friendRequestRepository.findById(requestID).orElse(null);
        if(friendRequest==null){
            return new MessageResponse(ErrorMessages.NOT_FOUND.getCode(), ErrorMessages.NOT_FOUND.getMessage());
        }
        if(friendRequest.isEnable()){
            return new MessageResponse(ErrorMessages.CONFLICT_UNIT.getCode(), ErrorMessages.CONFLICT_UNIT.getMessage());
        }
        friendRequestRepository.delete(friendRequest);
        return new MessageResponse(200,"OK");
    }

    @Transactional()
    public synchronized String signUp(AppUser appUser) {
        System.out.println("[tiến hành đăng ký]");
        System.out.println("[tồn tại user]:"+appUserRepository.findByEmail(appUser.getEmail()).isPresent());
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
        }finally {
            System.out.println("[hoàn tất đăng ký]");
        }
    }

    public HttpResponse login(LoginRequest request, HttpServletResponse reponse) {
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
