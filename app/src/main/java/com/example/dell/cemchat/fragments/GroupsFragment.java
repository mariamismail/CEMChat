package com.example.dell.cemchat.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dell.cemchat.Activities.AddMembersActivity;
import com.example.dell.cemchat.Activities.ChatRoom;
import com.example.dell.cemchat.Models.ChatRoomModel;
import com.example.dell.cemchat.Models.GroupModel;
import com.example.dell.cemchat.R;
import com.example.dell.cemchat.data.DataSource;
import com.firebase.ui.database.FirebaseListAdapter;
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

/**
 * Created by DELL on 5/18/2017.
 */

public class GroupsFragment extends Fragment implements OnFailureListener {
    private static final int REQUEST_CODE=123;
      RecyclerView listOfGroupsCahts;
    FloatingActionButton fab;
    DatabaseReference reference;
    DatabaseReference groupskeys;
    Activity activity;
    Fragment fragment;
    String userid;
    String newgroupName;
    String groupName;
    String groupPhoto;
    ArrayList<String> keys;
    ArrayList<String>Names;
    ArrayList<String>ids;
    ArrayList<String>photos;
    String key;
    LinearLayoutManager layoutManager;
    TextView layoutName;
    ImageView layoutView;
    TextView  notification;

    FirebaseRecyclerAdapter<String,ViewHolder> adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fargment_groups, container, false);
        listOfGroupsCahts = (RecyclerView) rootView.findViewById(R.id.list_of_chats_groups);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab_group);

        activity = this.getActivity();

        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        fragment = this;
        reference = FirebaseDatabase.getInstance().getReference();
        keys = new ArrayList<>();
        Names = new ArrayList<>();
        photos = new ArrayList<>();
        ids = new ArrayList<>();
        groupskeys = reference.child("usersId").child(userid).child("groupschats");
        ;
        layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setReverseLayout(false);
        listOfGroupsCahts.setHasFixedSize(true);
        listOfGroupsCahts.setLayoutManager(layoutManager);


        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                // Create and show the dialog.
                InsertnameDialogFragment newFragment = new InsertnameDialogFragment();
                newFragment.setTargetFragment(fragment, REQUEST_CODE);
                newFragment.show(ft, "dialog");


            }
        });
        SetupAdapter();

        return rootView;

    }

    private void SetupAdapter(){
        try {
            adapter = new FirebaseRecyclerAdapter<String, ViewHolder>(
                    String.class, R.layout.chat_room_msg, ViewHolder.class, groupskeys) {

                @Override
                protected String parseSnapshot( final DataSnapshot snapshot) {

                    String roomkey = snapshot.getValue().toString();
                    FirebaseDatabase.getInstance().getReference().child("groups").child(roomkey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                         groupName= dataSnapshot.child("groupName").getValue().toString();
                            Names.add(groupName);
                            if (dataSnapshot.child("groupPhoto").getValue() != null) {
                                groupPhoto = dataSnapshot.child("groupPhoto").getValue().toString();
                                photos.add(groupPhoto);
                            }
                            else {photos.add(null);
                                groupPhoto=null;}
                            layoutName = ((TextView) layoutManager.findViewByPosition(Integer.valueOf(snapshot.getKey())).findViewById(R.id.message_user));
                            layoutView = (ImageView) layoutManager.findViewByPosition(Integer.valueOf(snapshot.getKey())).findViewById(R.id.user_image);

                            layoutName.setText(groupName);
                            if(groupPhoto!=null) {
                                Glide.with(activity).load(groupPhoto).apply(RequestOptions.circleCropTransform()).apply(RequestOptions.overrideOf(100, 100)).into(layoutView);
                            }
                            else layoutView.setImageDrawable(getResources().getDrawable(R.drawable.ic_group_black_24dp
                            ));
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

                    DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("GroupsChats").child(key);
                    rf.addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String g=dataSnapshot.child("unseen").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue().toString();

                            ChatRoomModel chatRoomModel = dataSnapshot.getValue(ChatRoomModel.class);

                            notification=((TextView) viewHolder.itemView.findViewById(R.id.notifications));
                            if (Integer.valueOf(g)!=0) {
                                notification.setVisibility(View.VISIBLE);
                                notification.setText(g);
                            }




                            ((TextView) viewHolder.itemView.findViewById(R.id.message_text)).setText(chatRoomModel.getRoomlastMsg());
                            ((TextView) viewHolder.itemView.findViewById(R.id.message_user)).setText(groupName);
                            ((TextView) viewHolder.itemView.findViewById(R.id.message_time)).setText(DateFormat.format("dd-MM-yyyy\nHH:mm:ss",
                                    chatRoomModel.getMessageTime()));
                      //((ImageView) viewHolder.itemView.findViewById(R.id.user_image)).setImageResource();
                        }

                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                    ((RelativeLayout) viewHolder.itemView.findViewById(R.id.message_user).getParent()).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            activity.startActivity(new Intent(activity, ChatRoom.class)
                                    .putExtra("Obj", keys.get(position))
                                    .putExtra("id" ,2)
                                    .putExtra("groupName",Names.get(position))
                                    .putExtra("groupPhoto",photos.get(position)));

//

                        }
                    });

                }


            };

            listOfGroupsCahts.setAdapter(adapter);
        }
        catch (DatabaseException e) {

            Toast.makeText(activity,
                    "Dtabase error try again later " ,
                    Toast.LENGTH_LONG)
                    .show();
        }
        catch (IndexOutOfBoundsException e){  Toast.makeText(activity,
                " Error try to refresh " ,
                Toast.LENGTH_LONG)
                .show();}


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == 122 && data != null) {

            newgroupName= data.getStringExtra("name");
            newGroup();

        }
    }

    private void newGroup(){
        DataSource instance=  DataSource.getInstance();

       String groupRoom= instance.addGroup(this,userid,newgroupName);
        String[]mem={userid};
        instance.addGroupMember(mem,groupRoom);



    }



    @Override
    public void onFailure(@NonNull Exception e) {

    }


   private static class ViewHolder extends RecyclerView.ViewHolder{



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



