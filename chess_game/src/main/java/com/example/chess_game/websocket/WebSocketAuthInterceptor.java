
package com.example.chess_game.websocket;

import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    public WebSocketAuthInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {

            String header = accessor.getFirstNativeHeader(
                    "Authorization"
            );

            if (header == null || !header.startsWith("Bearer ")) {
                throw new MessagingException("Unauthorized");
            }

            Jwt jwt = jwtDecoder.decode(header.substring(7));

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            jwt.getSubject(),
                            null,
                            List.of()
                    );

            accessor.setUser(authentication);

            return message;
        }

        if (StompCommand.SUBSCRIBE.equals(command)) {

            if (accessor.getUser() == null) {
                throw new MessagingException("Unauthorized");
            }

            if (!"/user/queue/notifications".equals(
                    accessor.getDestination()
            )) {
                throw new MessagingException(
                        "Subscription not allowed"
                );
            }
        }

        if (StompCommand.SEND.equals(command)) {
            throw new MessagingException(
                    "Use the REST API to submit commands"
            );
        }

        return message;
    }
}
