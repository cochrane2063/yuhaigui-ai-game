package com.yupi.yuhaigui.database.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer sessionId;

    private String username;

    private Long roomId;

    public ChatRoom() {

    }

    public ChatRoom(String username, Long roomId) {
        this.username = username;
        this.roomId = roomId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getRoomId() {
        return roomId;
    }
}
