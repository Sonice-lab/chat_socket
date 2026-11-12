package com.sonicelab_v1.class_websocket_v1.stomp;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    @Query("select c from Chat c where c.roomId = :roomId order by c.id asc")
    // 1번방 메세지는 1번방만 뿌린다.
    List<Chat> findByRoomId(@Param("roomId") Integer roomId);

}
