package com.example.dell.cemchat.Models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by DELL on 5/2/2017.
 */

public class ChatRoomModel implements Serializable {
    private String roomTitle;
    private String roomlastMsg;
    private String roomLastTime;
    private long messageTime;

    public ChatRoomModel(String roomTitle, String roomlastMsg) {
        this.roomTitle = roomTitle;
        this.roomlastMsg = roomlastMsg;
        this.roomLastTime = roomLastTime;
        messageTime = new Date().getTime();
    }

    public ChatRoomModel() {
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getRoomlastMsg() {
        return roomlastMsg;
    }

    public void setRoomlastMsg(String roomlastMsg) {
        this.roomlastMsg = roomlastMsg;
    }

    public String getRoomLastTime() {
        return roomLastTime;
    }

    public void setRoomLastTime(String roomLastTime) {
        this.roomLastTime = roomLastTime;
    }
}
