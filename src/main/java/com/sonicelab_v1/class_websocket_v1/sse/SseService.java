package com.sonicelab_v1.class_websocket_v1.sse;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SSE
 * 브라우저가 한 번 연결을 열어두면 서버가 그 연결로 메시지를 계속 밀어넣는다.
 * 방향은 단방향이며, 서버 --> 브라우저 한 쪽 뿐임.
 * 브라우저가 별도 HTTP 요청을 보낼때는 새로운 요청을 만들어야 한다.
 * 스프링부트에서 제공하는 부분에 대해 알고있어야 함.
 */
@Slf4j
@Service
public class SseService {

    // 연결된 탭마다 SseEmitter 하나 맵핑됨
    // ArrayList -> 동시성 문제 발생, 멀티 스레드 환경에서 오류가 발생할 가능성이 높음
    private final List<SseEmitter> emitterList = new CopyOnWriteArrayList<>();

    // 연결 유지 시간 (10분)
    private static final long TIMEOUT = 10 * 60 * 1000L;

    // 브라우저가 연결을 요청하면 SseEmitter 객체를 생성해서 List에 보관하고 돌려준다.
    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        emitterList.add(emitter);

        // 연결이 정상 종료되면 목록에서 뺀다.
        emitter.onCompletion(() -> emitterList.remove(emitter));

        // 시간이 지나면 목록에서 빼고 연결을 마무리한다.
        emitter.onTimeout(() -> {
            emitterList.remove(emitter);

            // 정상 완료
            emitter.complete();
        });

        // 네트워크 오류 등으로 연결이 끊길 경우 목록에서 뺀다.
        emitter.onError(e -> emitterList.remove(emitter));

        // 연결 확인 이벤트를 바로 브라우저 측으로 한 번 보낸다.
        // 첫 데이터가 나갈 때 응답 헤더도 함께 전송되어 브라우저의 onopen이 실행된다.
        // 아무것도 보내지 않을 채 TIMEOUT이 지나면 스프링이 503 응답으로 끝내므로 한 건은 먼저 보내둔다.
        try {
            emitter.send(SseEmitter.event().name("connect").data("연결 성공!"));
        } catch (IOException e) {
            // throw new RuntimeException(e);
            emitterList.remove(emitter);
        }

        log.info("SSE 연결 추가, 현재 연결 수: {}", emitterList.size());

        return emitter; // 웹 브라우저의 자바스크립트 측 코드로 던짐
    }

    // 연결된 모든 브라우저에 "chat" 이벤트로 메시지를 보낸다.
    public void send(String msg) {
        for (SseEmitter emitter : emitterList) {
            try {
                emitter.send(SseEmitter.event().name("chat").data(msg));
            } catch (IOException e) {
                // 이미 끊긴 연결일 경우 보내기에 실패하므로 목록에서 뺀다.
                emitterList.remove(emitter);
            }
        }
    }
}
