package com.sonicelab_v1.class_websocket_v1.stomp;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "chat_tb")
@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer roomId;

    @Column(nullable = false)
    private String sender;

    private String msg;

    @Builder
    public Chat(Integer id, String msg, String sender, Integer roomId) {
        this.id = id;
        this.msg = msg;
        this.sender = sender;
        this.roomId = roomId;
    }
}
