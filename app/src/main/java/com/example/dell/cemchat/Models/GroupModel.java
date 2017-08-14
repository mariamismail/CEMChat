package com.example.dell.cemchat.Models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by DELL on 5/18/2017.
 */

public class GroupModel implements Serializable {

    private String groupName;
    private String groupAdmin;
    private String groupPhoto;
    private String chatKey;
    private String groupId;
    private long dataCreated;
    private String StringdataCreated;
    public GroupModel() {
    }


    public GroupModel(String groupName, String groupAdmin, String groupPhoto, String chatKey, String groupId) {
        this.groupName = groupName;
        this.groupAdmin = groupAdmin;
        this.groupPhoto = groupPhoto;
        this.chatKey = chatKey;
        this.groupId = groupId;


        dataCreated= new Date().getTime();
        StringdataCreated=Long.toString(dataCreated);
    }
    public long getDataCreated() {
        return dataCreated;
    }

    public void setDataCreated(long dataCreated) {
        this.dataCreated = dataCreated;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(String groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    public String getGroupPhoto() {
        return groupPhoto;
    }

    public void setGroupPhoto(String groupPhoto) {
        this.groupPhoto = groupPhoto;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
