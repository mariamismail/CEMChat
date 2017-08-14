package com.example.dell.cemchat.connections;

import com.example.dell.cemchat.Models.ChatMessage;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 5/9/2017.
 */

public interface DataListener {
    void onDownloadFinished(ArrayList<ChatMessage> messages);

}
