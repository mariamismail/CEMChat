package com.example.dell.cemchat.services;

import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by it on 20/07/2017.
 */

public class FcmSender extends AsyncTask <String, Void,Void> {
    public FcmSender() {
        super();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... urls) {



        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + "AAAAKhnkcxA:APA91bFVKHLF8-rhOIQR8MkD4tkxPTTN9SLie_MungIFF5ewjxR-oJDvqhVURqmdPU0-hP2vP_DkaS-22MChnu109cVNPgID9h12Z5n_zMxF9Hn34Bfsk7s1GU3OFrIQJlWPrxahaUKf");
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            json.put("to", urls[2]);
            JSONObject info = new JSONObject();
            info.put("title", urls[1]); // Notification title
            info.put("body", urls[0]); // Notification body
            json.put("notification", info);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            conn.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
