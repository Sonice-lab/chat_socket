package com.sonicelab_v1.class_websocket_v1.stomp;


/**
 * 브라우저와 주고 받는 메시지 한 건을 JSON으로 구조화
 * {"sender" : "홍길동", "msg" : "안녕"}
 *
 * record: 괄호 안에 필드를 선언하면 생성자를 자동으로 만들어주며,
 * 접근자(Vector), equals, toString 자동으로 만들어주는 불변 객체
 */
public record ChatMessageDto(String sender, String msg) {}
