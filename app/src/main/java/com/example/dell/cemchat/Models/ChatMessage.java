package com.example.dell.cemchat.Models;

import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by DELL on 4/25/2017.
 */

public class ChatMessage implements Serializable {

    private String messageText;
    private String messageSender;
    private String messageKey;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private String contentType;
    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    private String chatKey;

    private String messageUri;
    private long messageTime;

    public ChatMessage(String messageText, String messageSender, String messageKey, String messageUri, String ChatKey,String contentType) {
        this.messageText = messageText;
        this.messageSender = messageSender;

        this.messageUri=messageUri;
        this.messageKey = messageKey;
        this.chatKey=ChatKey;
        this.contentType=contentType;
        // Initialize to current time
        messageTime = new Date().getTime();
    }


    public ChatMessage(){

    }

    public String getMessageUri() {
        return messageUri;
    }

    public void setMessageUri(String messageUri) {
        this.messageUri = messageUri;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender =messageSender;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
