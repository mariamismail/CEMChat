package com.example.dell.cemchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dell.cemchat.Activities.ChatRoom;
import com.example.dell.cemchat.Models.User;
import com.example.dell.cemchat.R;
import com.example.dell.cemchat.data.DataSource;
import com.example.dell.cemchat.fragments.ContactsFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.PublicKey;
import java.util.ArrayList;

/**
 * Created by DELL on 6/7/2017.
 */

public class ContactsAdapter extends FirebaseRecyclerAdapter  {
    DatabaseReference reference;
    DatabaseReference UsersIDs;
    FirebaseRecyclerAdapter adapter;
    String key;
    String userkey;
    String userName;
    ArrayList<String> usersNames;



    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {

    }

    public ContactsAdapter(Class modelClass, int modelLayout, Class viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    public void   loadContactsFromUsers(){

    adapter = new FirebaseRecyclerAdapter<String,ContactsFragment.ViewHolder>(
    String.class, R.layout.user, ContactsFragment.ViewHolder.class,UsersIDs){




        protected void populateViewHolder(final ContactsFragment.ViewHolder viewHolder, final String model, final int position) {

            key = this.getRef(position).child(model).getKey();

            DatabaseReference rf= FirebaseDatabase.getInstance().getReference().child("users").child(key);
            rf.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {


                    final User user=  dataSnapshot.getValue(User.class);
                    usersNames.add(user.getName());

                    ( (TextView)viewHolder.itemView.findViewById(R.id.user_name)).setText(user.getName());
                    ( (RelativeLayout)viewHolder.itemView.findViewById(R.id.user_name).getParent()).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userkey= getRef(position).child(model).getKey();
                        }
                    });

                }

                public void onCancelled(DatabaseError firebaseError) { }
            });


        }


    };

}}
