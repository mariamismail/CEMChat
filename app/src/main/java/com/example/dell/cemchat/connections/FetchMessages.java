package com.example.dell.cemchat.connections;

import android.os.AsyncTask;

import com.example.dell.cemchat.Models.ChatMessage;

import java.util.List;

/**
 * Created by DELL on 5/9/2017.
 */

public class FetchMessages extends AsyncTask<String, Void, List<ChatMessage>> {

    public String id;
    @Override
    protected List<ChatMessage> doInBackground(String[] params) {
        id=params[0];
        return null;
    }
}
