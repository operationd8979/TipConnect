package Tip.Connect.websocket.config;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserInterceptor implements ChannelInterceptor {

    private Map<String, PrincipalUser> loggedInUsers = new ConcurrentHashMap<>();

    @Override
    public org.springframework.messaging.Message<?> preSend(org.springframework.messaging.Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);

            if (raw instanceof Map) {
                Object name = ((Map) raw).get("userID");
                System.out.println(name);
                if (name instanceof ArrayList) {
                    String username = ((ArrayList<String>) name).get(0).toString();

                    if(loggedInUsers.containsKey(username)){
                        accessor.setUser(loggedInUsers.get(username));
                    }
                    else{
                        PrincipalUser user = new PrincipalUser(username);
                        accessor.setUser(user);
                        loggedInUsers.put(username,user);
                    }
                }
            }
        }
        return message;
    }
}
