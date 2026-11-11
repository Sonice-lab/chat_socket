package com.sonicelab_v1.class_websocket_v1.websocket.config;

import com.sonicelab_v1.class_websocket_v1.websocket.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // 2. 핸들러 필드 주입 추가 (클래스명은 실제 생성하신 핸들러 이름에 맞게 맞춰주세요)
    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 허용 출처를 지정하지 않으면 같은 출처(같은 주소와 포트에서 열린 페이지)의 연결만 받읔
        registry.addHandler(chatWebSocketHandler, "/ws-chat"); //DI 처리
//                .setAllowedOrigins("*")
//                .setAllowedOrigins("http://localhost:5173")
//                .setAllowedOrigins("http://app.example.com");

    }

}
