package com.sonicelab_v1.class_websocket_v1.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

    // 서버 어디서든지 특정 주소(/sub/...)를 구독한 브라우저들에게 메세지를 보낼 수 있게 해주는 도구
    private final SimpMessagingTemplate messagingTemplate; // 브로드캐스트 가능, 특정 방에 메세지를 던지는 역할을 함
    private final ChatService chatService;


    // 사용자가 첫번째 endpoint(첫 화면)로 연결하면 자동으로 1번 방으로 보낸다.
    @GetMapping("/")
    public String home() {
        return "redirect:/rooms/1";
    }

    @GetMapping("/rooms/{roomId}")
    public String room(@PathVariable("roomId") Integer roomId, Model model) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("chats", chatService.findByRoomId(roomId));
        return "index";
    }

    /**
     * 채팅 메세지 수신 (STOMP)
     * 이 메서드는 URL로 불리지 않는다.
     * 브라우저는 ws://localhost:8080/ws-stomp 로 연결을 한 번 열어두고,
     * 그 연결 안으로 destination:/pub/chat/{1} 이 적힌 SEND 프레임을 보낸다.
     * 스프링 /pub을 떼고 남은 chat/1을 보고 메서드를 찾는다.
     */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") Integer roomId, ChatMessageDto request) {
        log.info("수신: {}번 방, {}", roomId, request);
        // 빈 메세지는 거른다.(브라우저 검사는 우회할 수 있으므로 서버에서도 확인한다.)
        if (request.msg() == null || request.msg().isBlank()) {
            return;
        }

        // 2. 이름이 비어있다면 "익명"으로 둔다.
        String sender = (request.sender() == null || request.sender().isBlank()) ? "익명" : request.sender().trim();

        // 저장
        Chat chat = chatService.save(roomId, sender, request.msg().trim());

        // 중요 (메세지 브로커에게 전달)
        // Websocket에서 만들었던 직접 작성한 broadcast() 반복문이 이 한 줄로 구현된다.
        messagingTemplate.convertAndSend("/sub/chat/" + roomId,
                new ChatMessageDto(chat.getSender(), chat.getMsg()));

    }


}
