package com.example.dell.cemchat.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dell.cemchat.Activities.ChatRoom;
import com.example.dell.cemchat.Models.ChatMessage;
import com.example.dell.cemchat.Models.ChatRoomModel;
import com.example.dell.cemchat.Models.User;
import com.example.dell.cemchat.R;
import com.example.dell.cemchat.data.DataSource;
import com.example.dell.cemchat.utils.DateHelper;
import com.example.dell.cemchat.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by DELL on 5/10/2017.
 */

public class MessagesFragment extends Fragment implements OnFailureListener {
    RecyclerView listOfMessages;
    DatabaseReference reference;
    DatabaseReference chatskeys;
    ArrayList<String> keys;
    String key;
    int pos;
    LinearLayoutManager layoutManager;
    ArrayList<String> Names;
    ArrayList<String> tokens;
    ArrayList<String> ids;
    ArrayList<String> photos;
    ArrayList<User> users;
    Activity activity;
    long position;
    String userid;
    String friend;
    String friendName;
    String friendID;
    String friendPhoto;
    String friendToken;
    TextView notification;
    TextView messageText;
    TextView userName;
    TextView layoutName;
    ImageView layoutView;
    ImageView userImage;
    RelativeLayout msgView;
    FirebaseRecyclerAdapter<String, ViewHolder> adapter;
    int i=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_msgs, container, false);
        listOfMessages = (RecyclerView) rootView.findViewById(R.id.list_of_chats);
        // FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        activity = this.getActivity();
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        keys = new ArrayList<>();
        Names = new ArrayList<>();
        photos = new ArrayList<>();
        ids = new ArrayList<>();
        users=new ArrayList<>();
        tokens = new ArrayList<>();
        chatskeys = reference.child("usersId").child(userid).child("chats");
        layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setReverseLayout(false);
        //listOfMessages.setHasFixedSize(true);
        listOfMessages.setLayoutManager(layoutManager);

//    fab.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            SetupAdapter();
//        }
//    });

        SetupAdapter();
        return rootView;
    }


    @Override
    public void onFailure(@NonNull Exception e) {

    }

    private void SetupAdapter() {
        try {
            adapter = new FirebaseRecyclerAdapter<String, ViewHolder>(
                    String.class, R.layout.chat_room_msg, ViewHolder.class, chatskeys) {
                @Override
                protected String parseSnapshot(final DataSnapshot snapshot) {
                    String roomkey = snapshot.getValue().toString();
                    FirebaseDatabase.getInstance().getReference().child("mebers").child(roomkey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            friendName=null;

                            for (DataSnapshot chid : dataSnapshot.getChildren()) {
                                if (!chid.getKey().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    friendID = chid.getKey().toString();
                                    ids.add(friendID);
                                    FirebaseDatabase.getInstance().getReference().child("users").child(chid.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User user= dataSnapshot.getValue(User.class);
                                            users.add(user);
                                                friendName=user.getName();
                                            Names.add(friendName);

                                            if (dataSnapshot.child("photo").getValue() != null) {
                                                friendPhoto = dataSnapshot.child("photo").getValue().toString();
                                                photos.add(friendPhoto);
                                            }
                                           else {photos.add(null);
                                                friendPhoto=null;}
                                            if (dataSnapshot.child("token").getValue() != null) {
                                                friendToken = dataSnapshot.child("token").getValue().toString();
                                                tokens.add(friendToken);
                                            }
                                            else {tokens.add(null);
                                                friendToken=null;

                                            }



                                            layoutName = ((TextView) layoutManager.findViewByPosition(Integer.valueOf(snapshot.getKey())).findViewById(R.id.message_user));
                                            layoutView = (ImageView) layoutManager.findViewByPosition(Integer.valueOf(snapshot.getKey())).findViewById(R.id.user_image);
                                          //  photos.get(Integer.valueOf(snapshot.getKey())
                                            layoutName.setText(friendName);
                                            if(friendPhoto!=null) {
                                                Glide.with(activity).load(friendPhoto).apply(RequestOptions.circleCropTransform()).apply(RequestOptions.overrideOf(100, 100)).into(layoutView);
                                            }
                                            else layoutView.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    return super.parseSnapshot(snapshot);

                }

                protected void populateViewHolder(final ViewHolder viewHolder, String model, final int position) {

                    key = this.getRef(position).child(model).getKey();
                    keys.add(key);
                    DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("chats").child(key);
                    rf.addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String g = dataSnapshot.child("unseen").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue().toString();

                            ChatRoomModel chatRoomModel = dataSnapshot.getValue(ChatRoomModel.class);
                            notification = ((TextView) viewHolder.itemView.findViewById(R.id.notifications));
                            if (Integer.valueOf(g) != 0) {
                                notification.setVisibility(View.VISIBLE);
                                notification.setText(g);
                            }
                            msgView=(RelativeLayout) viewHolder.itemView.findViewById(R.id.message_view);
                            messageText = ((TextView) viewHolder.itemView.findViewById(R.id.message_text));
                            userName = ((TextView) viewHolder.itemView.findViewById(R.id.message_user));
                            userImage = ((ImageView) viewHolder.itemView.findViewById(R.id.user_image));
                            String time = DateFormat.format("HH:mm:ss",
                                    chatRoomModel.getMessageTime()).toString();
                            messageText.setText(chatRoomModel.getRoomlastMsg());
                            Date messageTime = new Date(chatRoomModel.getMessageTime());
                            String day = DateHelper.getInstance().compareDate(messageTime);
                            ((TextView) viewHolder.itemView.findViewById(R.id.message_time)).setText(day + " At" + time);

//
                        }

                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                    ((RelativeLayout) viewHolder.itemView.findViewById(R.id.message_user).getParent()).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (users != null) {
                                for (User user : users){
                                    if (user.getUserId().equals(ids.get(position))){
                                        activity.startActivity(new Intent(activity, ChatRoom.class)
                                                .putExtra("Obj", keys.get(position))
                                                .putExtra("friendName", user.getName())
                                                .putExtra("friendPhoto",user.getPhoto())
                                                .putExtra("friendId", ids.get(position))
                                                .putExtra("friendToken", user.getToken()));
                                        break;
                                    }
                                }


                            }

                        }
                    });

                }


            };


            listOfMessages.setAdapter(adapter);
        } catch (DatabaseException e) {

            Toast.makeText(activity,
                    "Dtabase error try again later ",
                    Toast.LENGTH_LONG)
                    .show();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(activity,
                    " Error try to refresh ",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}
