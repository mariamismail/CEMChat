package com.example.dell.cemchat.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dell.cemchat.Activities.ChatRoom;
import com.example.dell.cemchat.Activities.MainActivity;
import com.example.dell.cemchat.Models.ChatRoomModel;
import com.example.dell.cemchat.Models.User;
import com.example.dell.cemchat.R;
import com.example.dell.cemchat.data.DataRetrive;
import com.example.dell.cemchat.data.DataSource;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 5/4/2017.
 */

public class ContactsFragment extends Fragment {
    SharedPreferences prefrence;

    ArrayList<String> phonesNum = new ArrayList<>();
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    String userid;
    DatabaseReference reference;
    DatabaseReference UsersIDs;
    RecyclerView listOfContacts;
    Activity activity;
    FirebaseRecyclerAdapter adapter;
    String key;
    String userkey;
    String userName;
    String userPic;
    String friendID;
    ArrayList<String> usersNames;
    ArrayList<String> profiles;
    ArrayList<String> ids;
    ArrayList<String> members;
    String Roomkey;
    String memberRoom;
    boolean member;
    String groupKey;
    Button invite;
    String chatRoom;
    ArrayList<String> selectedMsgs;
    int i = 0;
    int fragmentId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contacts, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_contacts_refresh:
               addContactsToFirabase();
                return true;

            case R.id.menu_invite:
              sendInvitation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        listOfContacts = (RecyclerView) rootView.findViewById(R.id.list_of_contacts);
        Roomkey = null;
        invite = (Button) rootView.findViewById(R.id.invitefriend);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        activity =getActivity();
        members=new ArrayList<>();
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fragmentId = getArguments().getInt("id");
        groupKey = getArguments().getString("groupKey");
        if (getArguments().getStringArrayList("members")!=null){
        members= getArguments().getStringArrayList("members");}

       chatRoom= getArguments().getString("callingActivity");
        if (getArguments().getStringArrayList("selectedMsgs")!=null){
            selectedMsgs= getArguments().getStringArrayList("selectedMsgs");}

        reference = FirebaseDatabase.getInstance().getReference();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setReverseLayout(false);
        //listOfContacts.setHasFixedSize(true);
        listOfContacts.setLayoutManager(layoutManager);
        UsersIDs = reference.child("usersId").child(userid).child("contacts");
        usersNames = new ArrayList<String>();
        profiles = new ArrayList<String>();
        ids= new ArrayList<>();
        member=false;


        if (fragmentId == 1) {
            invite.setVisibility(View.VISIBLE);
            invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sendInvitation();

                }
            });
        }

        reference.child("usersId").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("contacts")) {
                    loadContactsFromUsers();
                    listOfContacts.setAdapter(adapter);


                } else {
                    addContactsToFirabase();
                    loadContactsFromUsers();
                    listOfContacts.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return rootView;
    }


    private void loadContactsFromUsers() {


        adapter = new FirebaseRecyclerAdapter<String, ContactsFragment.ViewHolder>(
                String.class, R.layout.user, ContactsFragment.ViewHolder.class, UsersIDs) {



            protected void populateViewHolder(final ContactsFragment.ViewHolder viewHolder, final String model, final int position) {

                key = this.getRef(position).child(model).getKey();

                DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                rf.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {

                       TextView Name=  ((TextView) viewHolder.itemView.findViewById(R.id.user_name));
                        User user = dataSnapshot.getValue(User.class);
                        usersNames.add(user.getName());
                        Name.setText(user.getName());
                        Toast.makeText(activity,
                                user.getName(),
                                Toast.LENGTH_LONG);

                            if (members!=null&members.contains(user.getName())) {
                                listOfContacts.setClickable(true);

                                Name.setTextColor(Color.GRAY);
                                ((RelativeLayout) viewHolder.itemView.findViewById(R.id.user_name).getParent()).setEnabled(false);
                                ((RelativeLayout) viewHolder.itemView.findViewById(R.id.user_name).getParent()).setClickable(false);

                            }

                        String url = user.getPhoto();
                        profiles.add(url);

                        ids.add(user.getUserId());
                        ImageView profile = ((ImageView) viewHolder.itemView.findViewById(R.id.user_image));
                        if (url == null) {
                            profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
                        } else {

                            Glide.with(activity)
                                    .load(url)
                                    .apply(RequestOptions.overrideOf(600, 200)).apply(RequestOptions.circleCropTransform())
                                    .into(profile);
                        }

                        ((RelativeLayout) viewHolder.itemView.findViewById(R.id.user_name).getParent()).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                userkey = getRef(position).child(model).getKey();
                                userName = usersNames.get(position);
                                userPic = profiles.get(position);
                                friendID= ids.get(position);
                                //cheak how is calling the fragment
                                if (fragmentId == 1) {
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild("chats")) {
                                                searchMembers(userkey);

                                            } else {  //create new room
                                                //first launch
                                                String[] users = {userid, userkey};
                                                Roomkey = createNewRoom(users);

                                                activity.startActivity(new Intent(activity, ChatRoom.class)
                                                        .putExtra("Obj", Roomkey).putExtra("id", 1).putExtra("friendName", userName).putExtra("friendPhoto", userPic).putExtra("friendId",friendID));

                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                } else if (fragmentId == 2) {


                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("Add Members")
                                            .setMessage("Are you sure you want to add " + userName + " as member")
                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    DataSource ins = DataSource.getInstance();
                                                    String[] key = {userkey};
                                                    ins.addGroupMember(key, groupKey);
                                                    Toast.makeText(activity,
                                                            userName+" is Added ",
                                                            Toast.LENGTH_LONG)
                                                            .show();
                                                   getActivity().finish();


                                                }
                                            })
                                            .setNegativeButton(("Cancel"), new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();

                                                }
                                            })


                                            // Create the AlertDialog object and return it
                                            .create().show();

                                }
                                else if (fragmentId==3){
                                    searchMembers(friendID);
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("Forward To")
                                            .setMessage("Are you sure you want to forward messages to" + userName)
                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    DataRetrive.getInstance().forwardMsg(memberRoom,chatRoom,selectedMsgs);
                                                    Toast.makeText(activity,
                                                            "Messages forwarded",
                                                            Toast.LENGTH_LONG)
                                                            .show();
                                                    getActivity().finish();


                                                }
                                            })
                                            .setNegativeButton(("Cancel"), new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();

                                                }
                                            })


                                            // Create the AlertDialog object and return it
                                            .create().show();

                                }


                            }
                        });
                    }

                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });


            }


        };


    }

    private void searchMembers( final String ID) {


        Query query = FirebaseDatabase.getInstance().getReference().child("mebers");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberRoom = null;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //if the user and the contact are members in any chatkey(members child)

                    if (child.hasChild(ID) & child.hasChild(userid)) {

                        memberRoom = child.getKey();
                        if(fragmentId==1) {
                            activity.startActivity(new Intent(activity, ChatRoom.class)
                                    .putExtra("Obj", memberRoom).putExtra("id", 1).putExtra("friendName", userName).putExtra("friendPhoto", userPic).putExtra("friendId", friendID));
                        }


                        break;
                    }

                }
                if (memberRoom == null) {
                    String[] users = {userid, userkey};
                    memberRoom = createNewRoom(users);
                    if(fragmentId==1){
                    activity.startActivity(new Intent(activity, ChatRoom.class)
                            .putExtra("Obj", memberRoom).putExtra("id", 1).putExtra("friendName", userName).putExtra("friendPhoto", userPic).putExtra("friendId",friendID));
                }
                }
            }

            public void onCancelled(DatabaseError firebaseError) {
            }
        });


    }
    private void sendInvitation(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Download CEM Chat and let's start chatting ");
        sendIntent.putExtra(Intent.EXTRA_TEXT, " https://drive.google.com/open?id=0B2kmw1bROpzmSlIwVXNYVmJ0OWM");

        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }
    private void addContactsToFirabase() {

        FirebaseDatabase.getInstance().getReference().child("usersId").child(userid).child("contacts").setValue(null);
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (phones.moveToNext()) {

            String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));

            if (Integer.parseInt(phones.getString(
                    phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor pCur = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                while (pCur.moveToNext()) {
                    String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    // String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    // String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneNo =phoneNo.replace(" ","");
                    phonesNum.add(phoneNo);


                }
                pCur.close();
            }
        }
        phones.close();
if(phonesNum.contains("01006743253")){
    member=true;
}
        FirebaseDatabase.getInstance().getReference().child("phones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String phone = child.getValue().toString();

                    if (phonesNum.contains(phone)) {
                        String id = child.getKey();
                        Map<String, Object> result = new HashMap<>();
                        result.put(String.valueOf(i), id);
                        i = i + 1;
                        FirebaseDatabase.getInstance().getReference().child("usersId").child(userid).child("contacts").updateChildren(result);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOG_TAG, databaseError.getMessage());
            }
        });
    }


    private String createNewRoom(String[] users) {
        DataSource instance = DataSource.getInstance();
        String NewRoom = instance.addChatRoom(users);
        instance.addUserChatKey();
        instance.addMebers(users);

        return NewRoom;
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
