package com.sonicelab_v1.class_websocket_v1.sse;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SseController {

    private final SseService sseService;

    // 중요 (약속)
    /**
     * 브라우저가 EventSource가 연결하는 주소
     * 응답 타입: (text/event-stream은 브라우저가 이 응답을 "이벤트 스트림으로 해석하게 하는 표시")
     * 이 요청은 응답이 바로 끝나지 않고, 서버가 메세지를 보낼때까지 열린채로 남는다.
     */
    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect () {
        return sseService.connect();
    }

}
