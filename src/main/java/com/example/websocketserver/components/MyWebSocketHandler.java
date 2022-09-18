package com.example.websocketserver.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ObjectMapper objectMapper;

    public MyWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.sessionList = new ArrayList<>();
    }

    private List<WebSocketSession> sessionList;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);
        session.sendMessage(new TextMessage("Hello"));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Message payload = objectMapper.readValue(message.getPayload(), Message.class);
        StringBuilder stringBuilder = new StringBuilder();
        String format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        stringBuilder.append(format).append(" ").append(payload.getSender()).append(": ").append(payload.getText());
        sessionList.stream()
//                .filter(s -> !s.equals(session))
                .forEach(s -> {
            try {
                s.sendMessage(new TextMessage(stringBuilder.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionList.remove(session);
    }
}
