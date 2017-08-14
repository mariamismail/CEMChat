package com.example.dell.cemchat.data;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dell.cemchat.Activities.MainActivity;
import com.example.dell.cemchat.Models.ChatMessage;
import com.example.dell.cemchat.Models.ChatRoomModel;
import com.example.dell.cemchat.Models.GroupModel;
import com.example.dell.cemchat.Models.Member;
import com.example.dell.cemchat.services.FcmSender;
import com.firebase.ui.auth.ui.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DELL on 4/30/2017.
 */
public class DataSource  {


    private static DatabaseReference reference;
    private   String userid;
    private String[] chatRecver;
    private String[] groupRecver;
    private String[] ChatMembers;
    private String[] GroupMembers;
    private OnFailureListener Listener;
    private String GroupName;

    private   long chatsCounts;

    private   long groupsCount;
   private   String chatRoomKey;
    private   String groupchatRoomKey;

    private static DataSource instance;
    public static DataSource getInstance() {

        if (instance == null) {
           instance = new DataSource();
        reference=FirebaseDatabase.getInstance().getReference();

        }
        return instance;
    }



    public  String addChatRoom(String[] RecvId){

        chatRecver=RecvId;

        DatabaseReference chatId = reference.child("chats");
        chatRoomKey = chatId.push().getKey();
        chatId.child(chatRoomKey).setValue(new ChatRoomModel("",""));
        FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomKey).child("unseen").child(chatRecver[0]).setValue("0");
        FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomKey).child("unseen").child(chatRecver[1]).setValue("0");
       return chatRoomKey;


    }


    public String addGroup(OnFailureListener listener,String userId,String name){
        //userid=userId;
        userid=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
       // recID=RecvId;
        Listener=listener;
        GroupName=name;



        DatabaseReference chatId = reference.child("GroupsChats");
       groupchatRoomKey = chatId.push().getKey();
        chatId.child( groupchatRoomKey).setValue(new ChatRoomModel(GroupName, "Welcome")).addOnFailureListener(listener);

        DatabaseReference groupId = reference.child("groups");

        groupId.child(groupchatRoomKey).setValue(new GroupModel(GroupName,userid,null, groupchatRoomKey,groupchatRoomKey));
return groupchatRoomKey ;
        //reference.child("usersId/"+userid+"/groupschats").child (String.valueOf(0)).setValue( groupchatRoomKey);
    }




    public void addUserChatKey(){
        for(String user:chatRecver){
      final    DatabaseReference   chats =  reference.child("usersId/"+user+"/chats");
            chats .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        chatsCounts=dataSnapshot.getChildrenCount();
                        chats.child (String.valueOf(chatsCounts)).setValue(chatRoomKey).addOnFailureListener(Listener);

                 }
                    else { chatsCounts=0;
                        chats.child (String.valueOf(chatsCounts)).setValue(chatRoomKey).addOnFailureListener(Listener);

                  }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }




    }

    public void addUserGroups(String[] RecvId){
        for(String user:RecvId){
      final    DatabaseReference   groups =  reference.child("usersId/"+user+"/groupschats");
            groups.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                      groupsCount  =dataSnapshot.getChildrenCount();

                        groups .child (String.valueOf(groupsCount)).setValue(groupchatRoomKey).addOnFailureListener(Listener);

                    }
                    else groupsCount=0;

                    groups .child (String.valueOf(groupsCount)).setValue(groupchatRoomKey).addOnFailureListener(Listener);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }




    }
    public void addMebers(String[]memb) {
      ChatMembers=memb;
        for (String user:ChatMembers) {
            DatabaseReference mem1 = reference.child("mebers/" + chatRoomKey).child(user);
            mem1.setValue(true);

        }
    }

    public void addGroupMember(String[]memberkey,String groupKey) {
       GroupMembers=memberkey;
        groupchatRoomKey=groupKey;
        for (String user:GroupMembers) {
            Map<String, Object> txtmap= new HashMap<>() ;
            txtmap.put(user,true);
            FirebaseDatabase.getInstance().getReference().child("mebers/" + groupchatRoomKey).updateChildren(txtmap);
            FirebaseDatabase.getInstance().getReference().child("GroupsChats").child(groupchatRoomKey).child("unseen").child(user).setValue("0");
            addUserGroups(memberkey);
        }

    }

   public void newMsg(final String  chatRoomref , final String msgTxt,String msgUri,String contentType,final String frienId,final String friendToken){


        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
//
       DatabaseReference messgesRefrence=reference.child("messages").child(chatRoomref);

        String msgkey=  messgesRefrence .push().getKey();
       messgesRefrence.child(msgkey).setValue(new ChatMessage(msgTxt,
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getDisplayName(),msgkey,msgUri,chatRoomref,contentType)
        );
       // FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomref).setValue(new ChatRoomModel(name,msgTxt));
        Map<String, Object> txtmap= new HashMap<>() ;
        txtmap.put("roomlastMsg",msgTxt);
        FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomref).updateChildren(txtmap);

       final Map<String, Object> notify= new HashMap<>() ;

       FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomref).child("unseen").child(frienId).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               String j=dataSnapshot.getValue().toString();
           int num= Integer.valueOf(dataSnapshot.getValue().toString());
               notify.put(frienId,String.valueOf(num+1));
               FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomref).child("unseen").updateChildren(notify);
               if(msgTxt.equals("")){
                   sendFCM("Photo",FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),friendToken);
               }
               else{sendFCM(msgTxt,FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),friendToken);}
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       })
               ;


    }


    public void newgroupMsg( final String  GroupRoomref ,String msgTxt,String msgUri,String contentType){


        DatabaseReference messgesRefrence=reference.child("messages").child( GroupRoomref);

        String msgkey=  messgesRefrence .push().getKey();
        messgesRefrence.child(msgkey).setValue(new ChatMessage(msgTxt,
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getDisplayName(),msgkey,msgUri,  GroupRoomref,contentType)
        );
       // FirebaseDatabase.getInstance().getReference().child("chats").child( GroupRoomref).setValue(new ChatRoomModel(name,msgTxt));
        Map<String, Object> txtmap= new HashMap<>() ;
        txtmap.put("roomlastMsg",msgTxt);
        FirebaseDatabase.getInstance().getReference().child("GroupsChats").child( GroupRoomref).updateChildren(txtmap);

        FirebaseDatabase.getInstance().getReference().child("GroupsChats").child(GroupRoomref).child("unseen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){

                    if (!child.getKey().equals(FirebaseAuth.getInstance()
                            .getCurrentUser().getUid())){
                        int num= Integer.valueOf(child.getValue().toString());

                        FirebaseDatabase.getInstance().getReference().child("GroupsChats")
                                .child(GroupRoomref).child("unseen").child(child.getKey())
                                .setValue(String.valueOf(num+1));
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

   public void   addAttachment(Activity activity){

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
      activity.startActivityForResult(Intent.createChooser(intent, "Choose File"), 1);


    }

   private   void sendFCM(String message,String senderName,String friendToken) {
        String[]url={message,senderName,friendToken};
        FcmSender sender= new FcmSender();
        sender.execute(url);
    }

}
