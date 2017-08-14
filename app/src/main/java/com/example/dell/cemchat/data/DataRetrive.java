package com.example.dell.cemchat.data;

import com.example.dell.cemchat.Models.ChatMessage;
import com.example.dell.cemchat.Models.ChatRoomModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by DELL on 5/3/2017.
 */
public class DataRetrive {


    private static DatabaseReference reference;


    private static DatabaseReference messagesref ;
    String name;
    String photourl;
    private   String userid;
    private String[] recID;
    private OnFailureListener Listener;
    private String GroupName;
    private DatabaseReference userref;
    private   long i;
    private String[] profile;
    private   String chatRoomKey;
    private   String groupKey;
    private static DataRetrive instance;
     ArrayList<ChatMessage> msgsToForward;
    public static DataRetrive getInstance() {

        if (instance == null) {
            instance = new DataRetrive();

     reference= FirebaseDatabase.getInstance().getReference();
            messagesref= FirebaseDatabase.getInstance().getReference().child("messages");

        }
        return instance;
    }

    public String deleteChatRoom(OnFailureListener listener,String userId,String[] RecvId){
        userid=userId;
        recID=RecvId;
        Listener=listener;
        DatabaseReference chatId = reference.child("chats");
        chatRoomKey = chatId.push().getKey();
        chatId.child(chatRoomKey).setValue(new ChatRoomModel("","")).addOnFailureListener(listener);

        return chatRoomKey;


    }

    public void DeleteMsg( final String chatRoomr,final ArrayList<String> MessageKey ){



        // Read the chat room key and get all messages with that key and delete
        //
        if(MessageKey==null) {
            //Delete All Chat no defined msgs
            messagesref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String Chatkey = child.child("chatKey").getValue().toString();
                        if (Chatkey.equals(chatRoomr)) {
                            String Msgkey = child.getKey();
                            FirebaseDatabase.getInstance().getReference().child("messages").child(chatRoomr).child(Msgkey).removeValue();

                        }


                    }
                    FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomr).setValue(new ChatRoomModel("", ""));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else { //Delete only  messag by msg
            for (String key:MessageKey){
            FirebaseDatabase.getInstance().getReference().child("messages").child(chatRoomr).child(key).removeValue();
            FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomr).child("roomlastMsg").setValue("");
                FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomr).child("messageTime").setValue("");
        }}



    }
//public String[] getProfile(String userid){
//
//FirebaseDatabase.getInstance().getReference().child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
//    @Override
//    public void onDataChange(DataSnapshot dataSnapshot) {
//        if(dataSnapshot.child("photo").exists()){
//        photourl= dataSnapshot.child("photo").getValue().toString();}
//         name=dataSnapshot.child("name").getValue().toString();
//        profile[0]=name;
//        profile[1]= photourl;
//
//    }
//
//    @Override
//    public void onCancelled(DatabaseError databaseError) {
//
//    }
//
//});
//
//
//
//    return profile;
//}

    public void forwardMsg(final String memberRoom, String chatRoom, final ArrayList<String>msgs){
     msgsToForward=new ArrayList<>();
        messagesref.child(chatRoom).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               for(String msgkey:msgs){
                ChatMessage message=   dataSnapshot.child(msgkey).getValue(ChatMessage.class);
                   //msgsToForward.add(message);
                   messagesref.child(memberRoom).push().setValue(message);

               }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
