package com.yupi.yuhaigui.database.model;

import dev.langchain4j.data.message.ChatMessageType;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer messageId;

    private Integer sessionId;

    private Integer messageOrder;

    private ChatMessageType type;

    private String message;

    public ChatMessage() {

    }

    public ChatMessage(Integer sessionId, Integer messageOrder, ChatMessageType type, String message) {
        this.sessionId = sessionId;
        this.messageOrder = messageOrder;
        this.type = type;
        this.message = message;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setMessageOrder(Integer messageOrder) {
        this.messageOrder = messageOrder;
    }

    public Integer getMessageOrder() {
        return messageOrder;
    }

    public void setType(ChatMessageType type) {
        this.type = type;
    }

    public ChatMessageType getType() {
        return type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
