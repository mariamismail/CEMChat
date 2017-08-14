package com.example.dell.cemchat.Activities;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
 import android.graphics.Color;
 import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import com.example.dell.cemchat.utils.ActionModeBar;
import com.example.dell.cemchat.utils.FilesHelper;
import com.rockerhieu.emojicon.EmojiconTextView;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
 import android.view.ActionMode;
 import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

 import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.dell.cemchat.Models.ChatMessage;
import com.example.dell.cemchat.R;
import com.example.dell.cemchat.data.DataRetrive;
import com.example.dell.cemchat.data.DataSource;
import com.example.dell.cemchat.utils.DateHelper;
import com.example.dell.cemchat.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.rockerhieu.emojicon.EmojiconEditText;

import java.io.File;
 import java.util.ArrayList;
 import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.bumptech.glide.request.RequestOptions.overrideOf;

/**
 * Created by DELL on 4/30/2017.
 */

public class ChatRoom extends AppCompatActivity  {
    private FirebaseRecyclerAdapter adapter;
    ActionMode.Callback mActionModeCallback;
    LinearLayoutManager layoutManager;
    DatabaseReference chatRoomref;
    EmojiconEditText input;
    Menu mainMenu;
    String Msgkey;
    String profilkey;
    OnProgressListener listener;
    String downloadUrl;
    ActionMode mActionMode;
    private RecyclerView listOfMessages;
    private ProgressBar bar;
    ProgressDialog pd;
    public  FrameLayout download;
    ImageView ProfilePic;
    Activity activity;
    Toolbar toolbar;
    static int recyclerpos;
    FloatingActionButton fab;
    FloatingActionButton fab1;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORGE = 121;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    StorageReference riversRef;
    String key;
    String name;
    int fragmentId;
    String friendId;
    String friendToken;
    ImageView emotion;
    Uri selectedimg;
    Intent detailsIntent;
    TextView mTitle;
    ArrayList<String>selectdkeys;
     String messageText;


    public ChatRoom() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_room);

        Msgkey = (String) getIntent().getExtras().get("Obj");
        activity = this;
        friendToken = (String) getIntent().getExtras().get("friendToken");
        fragmentId = getIntent().getIntExtra("id", 1);
        chatRoomref = FirebaseDatabase.getInstance().getReference().child("messages").child(Msgkey);
        friendId = (String) getIntent().getExtras().get("friendId");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        detailsIntent = new Intent(activity, GroupInfoActivity.class);
        //update unseen
        selectdkeys=new ArrayList<>();
        Map<String, Object> unseen = new HashMap<>();
        unseen.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), "0");
        if (fragmentId == 1) {
            FirebaseDatabase.getInstance().getReference().child("chats").child(Msgkey).child("unseen").updateChildren(unseen);
            profilkey = (String) getIntent().getExtras().get("friendPhoto");

            name = getIntent().getStringExtra("friendName");
            detailsIntent.putExtra("friendName",name).putExtra("friendPhoto",profilkey).putExtra("friendId",friendId);

        } else if (fragmentId == 2) {
            FirebaseDatabase.getInstance().getReference().child("GroupsChats").child(Msgkey).child("unseen").updateChildren(unseen);
            profilkey = (String) getIntent().getExtras().get("groupPhoto");
            name = getIntent().getStringExtra("groupName");
            detailsIntent.putExtra("groupkey", Msgkey);
        }


        input = (EmojiconEditText) findViewById(R.id.input);
        toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        emotion = (ImageView) findViewById(R.id.emotion);
         mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);



        ProfilePic = (ImageView) toolbar.findViewById(R.id.user_image);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (fragmentId == 1) {
            if (profilkey != null) {

                Glide.with(activity)

                        .load(profilkey)
                        .apply(RequestOptions.overrideOf(600, 200))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .into(ProfilePic);
            } else {
                ProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
            }
        } else if (fragmentId == 2) {

            if (profilkey != null) {
                Glide.with(activity)

                        .load(profilkey)
                        .apply(RequestOptions.overrideOf(600, 200))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .into(ProfilePic);

            } else {
                ProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.ic_group_black_24dp));
            }


        }

//

        toolbar.setTitle(name);
        mTitle.setText(toolbar.getTitle());

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(detailsIntent);
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        listOfMessages = (RecyclerView) findViewById(R.id.list_of_messages);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        listOfMessages.setHasFixedSize(true);
        listOfMessages.setLayoutManager(layoutManager);
        pd = new ProgressDialog(activity);
//        boolean result = checkPermission();
        boolean result = Utils.checkPermission(this);
        if (result) {
            setupAdapter();

            input.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listOfMessages.getLayoutManager().scrollToPosition(recyclerpos);

                }
            });

            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence sc, int i, int i1, int i2) {
                    fab.setVisibility(View.VISIBLE);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragmentId == 1 & !input.getText().toString().equals("")) {
                    DataSource.getInstance().newMsg(Msgkey, input.getText().toString(), downloadUrl, null, friendId,friendToken);
                   // DataSource.getInstance().sendFCM(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), friendToken);
                } else if (fragmentId == 2 & !input.getText().toString().equals("")) {
                    DataSource.getInstance().newgroupMsg(Msgkey, input.getText().toString(), downloadUrl, null);
                }
                adapter.notifyDataSetChanged();
                input.setText("");
            }
        });


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addAttachment();
                DataSource.getInstance().addAttachment(activity);
            }
        });


        emotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, Emjioy.class);

                startActivityForResult(intent, 500);
            }
        });

        mActionModeCallback=new ActionMode.Callback(){
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mActionMode.setTitle("item selected");
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       mainMenu=menu;
        mainMenu.clear();
        if (fragmentId == 1) {
            getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        } else if (fragmentId == 2) {
            getMenuInflater().inflate(R.menu.menu_group, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete_chat) {

            DataRetrive instance = DataRetrive.getInstance();
            instance.DeleteMsg(Msgkey, null);


        }


         else if (item.getItemId() == R.id.menu_group_details) {
            Intent intent = new Intent(activity, GroupInfoActivity.class);
            intent.putExtra("groupkey", Msgkey);
            startActivity(intent);


        }

        else if (item.getItemId() == R.id.menu_delet_msg) {

           // delete msg
            DataRetrive.getInstance().DeleteMsg(Msgkey,selectdkeys);
            setOrginToolbar();
            Toast.makeText(activity,
                    "Message deleted",
                    Toast.LENGTH_LONG)
                    .show();

        }
        else if (item.getItemId() == R.id.menu_forword_msg) {

            Intent intent = new Intent(activity, AddMembersActivity.class);
            intent.putExtra("callingActivity",Msgkey);
            intent.putStringArrayListExtra("selectedMsgs",selectdkeys);
            startActivity(intent);
            setOrginToolbar();

        }
        else if (item.getItemId() == R.id.menu_copy_msg) {
            setOrginToolbar();
          setClipboard(activity,messageText);

            Toast.makeText(activity,
                    "Message copied",
                    Toast.LENGTH_LONG)
                    .show();
        }

        return true;
    }

    private void setClipboard(Activity activity1, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity1.getSystemService(activity1.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity1.getSystemService(activity1.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private void setActionModebar(){
        mainMenu.clear();

        activity. getMenuInflater().inflate(R.menu.menu_edits, mainMenu);

        toolbar.setTitle("item selected");
        toolbar.setBackgroundColor(Color.GRAY);
        mTitle.setVisibility(View.INVISIBLE);
        ProfilePic.setVisibility(View.INVISIBLE);

    }
    private void setOrginToolbar(){
        toolbar.setBackground(activity.getResources().getDrawable(R.drawable.topbar));
        mTitle.setVisibility(View.VISIBLE);
        ProfilePic.setVisibility(View.VISIBLE);
        mainMenu.clear();
        activity. getMenuInflater().inflate(R.menu.menu_chat_room, mainMenu);
        toolbar.setTitle(null);
        setupAdapter();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {


        public ViewHolder(View itemView) {
            super(itemView);


        }

    }
//


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(activity,
                    "Failed to upload",
                    Toast.LENGTH_LONG);

             //File Chooser intent result
        } else if (resultCode == RESULT_OK) {
            selectedimg = data.getData();
            riversRef = storageRef.child(Msgkey + "/" + selectedimg.getLastPathSegment());
            //see if it is an image
            if(selectedimg.toString().contains("media")) {
             //preview the image
                activity.startActivityForResult(new Intent(activity, PreviewImage.class).putExtra("data", data.getDataString()), 700);
            }
            else { FilesHelper.getInstance()
                    .uploadFile(riversRef,selectedimg,activity,null,Msgkey,friendId,friendToken);}

            //emotion activity result
        } else if (resultCode == 600) {
            String y = data.getStringExtra("emotion");
            input.setText(y);

            //Image preview activity result
        } else if (resultCode == 800) {
            final String text = data.getStringExtra("input");

            FilesHelper.getInstance()
                    .uploadFile(riversRef,selectedimg,activity,text,Msgkey,friendId,friendToken);
        }


    }


    private void setupAdapter() {


        adapter = new FirebaseRecyclerAdapter<ChatMessage, ViewHolder>(
                ChatMessage.class, R.layout.message, ViewHolder.class, chatRoomref) {


            @Override
            protected ChatMessage parseSnapshot(DataSnapshot snapshot) {

                ChatMessage message = super.parseSnapshot(snapshot);
                String ChatKey = snapshot.child("chatKey").getValue().toString();
                recyclerpos = (adapter.getItemCount() - 1);
                //listOfMessages.smoothScrollToPosition(recyclerpos);
                return message;
            }

            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, final ChatMessage model, final int position) {
                RelativeLayout messagebodyLayout = ((RelativeLayout) viewHolder.itemView.findViewById(R.id.message_body_layout));
                TextView txtView = ((EmojiconTextView) messagebodyLayout.findViewById(R.id.message_text));
                download = (FrameLayout) messagebodyLayout.findViewById(R.id.attachment_download);
                FrameLayout imgHolder = (FrameLayout) messagebodyLayout.findViewById(R.id.image_view_holder);
                messagebodyLayout.removeAllViews();
                messagebodyLayout.addView(txtView);
                imgHolder.removeAllViews();
                messagebodyLayout.addView(imgHolder);
                messagebodyLayout.addView(download);
                String txt = model.getMessageText();
                String time = DateFormat.format("HH:mm:ss",
                        model.getMessageTime()).toString();
                txtView.setVisibility(View.VISIBLE);
//
                String messageSender = model.getMessageSender();
                if (messageSender.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                    txtView.setBackground(getResources().getDrawable(R.drawable.patchedwhite));
                } else {
                    txtView.setBackground(getResources().getDrawable(R.drawable.patchedgreen));
                }
                ((TextView) viewHolder.itemView.findViewById(R.id.message_user)).setText(model.getMessageSender());
                if (!txt.equals("")) {
                    txtView.setText(txt);
                } else {
                    txtView.setVisibility(View.INVISIBLE);
                }
                Date messageTime = new Date(model.getMessageTime());
                String day = DateHelper.getInstance().compareDate(messageTime);
                ((TextView) viewHolder.itemView.findViewById(R.id.message_time)).setText(day + " At" + time);
                //  bar=(ProgressBar)messagebodyLayout.findViewById(R.id.progressBar);



//                        httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getMessageUri());
                String uri = model.getMessageUri();
                if (uri != null) {
                    String type = model.getContentType();
                    if (type.contains("image")) { //if type is iamge
                        // adapter.getItem(getItemCount()) ;
                        imgHolder.setVisibility(View.VISIBLE);
                        ProgressBar imageprogress = new ProgressBar(activity, null, android.R.attr.progressBarStyleSmall);
                        imgHolder.addView(imageprogress);
                        ImageView img = new ImageView(activity);

                        Glide.with(activity)
                                .load(model.getMessageUri())
                                .apply(RequestOptions.overrideOf(600, 200))
                                .into(img);
                        imgHolder.addView(img);

                        imgHolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                activity.startActivity(new Intent(activity, PreviewImage.class).putExtra("Uri", model.getMessageUri()));
                            }
                        });


                    } else { //if type is file show download button then downlaod it and open

                        File baseDir = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS), "CEM Chat");
                        File file = new File(baseDir, model.getMessageText());
                        FilesHelper.getInstance().downloadFile(file,download,activity,Msgkey,model.getMessageText());



                    }
                }


                ((RelativeLayout) viewHolder.itemView.findViewById(R.id.message_user).getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //first item

                        if(toolbar.getTitle()!=null&selectdkeys.isEmpty()){
                            toolbar.setTitle("1");
                            v.setBackgroundColor(Color.WHITE);
                             selectdkeys.add(model.getMessageKey());
                              messageText=model.getMessageText();}

                        // the other items
                        else if (toolbar.getTitle()!=null&!selectdkeys.contains(model.getMessageKey())) {
                            selectdkeys.add(model.getMessageKey());
                            messageText=messageText+ "\n"+ model.getMessageText();
                            v.setBackgroundColor(Color.WHITE);
                                toolbar.setTitle(String.valueOf(selectdkeys.size()));
                            }

                        else  if(toolbar.getTitle()==null) {
                            String messagekey = getRef(position).getKey();

                            if (model.getMessageUri()!=null){
                                String type=  model.getContentType();
                                File baseDir = new File(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOWNLOADS), "CEM Chat");
                                       String filename = model.getMessageText();
                                      File file = new File(baseDir, filename);
                                switch (type){

                                    case "application/x-zip"  :
                                        FilesHelper.getInstance().openFile(file,activity,filename);

                                    case "application/octet-stream" :
                                        FilesHelper.getInstance().openFile(file,activity,filename);

                                }
                            }
                           // DatabaseReference msgs = FirebaseDatabase.getInstance().getReference().child("messages").child(messagekey);
                        }
                        //search if this message has uri and its type if image done if if file craete inetent
//                        msgs.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.hasChild("messageUri")) {
//
//                                    String type = dataSnapshot.child("contentType").getValue().toString();
//                                    if (type.equals("application/x-zip")) {
//                                        //  Chaek if in memory
//                                        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//                                        String filename = dataSnapshot.child("messageText").getValue().toString();
//                                        File file = new File(baseDir, filename);
//
//                                        //open the file
//                                        FilesHelper.getInstance().openFile(file,activity,filename);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });

                    }
                });

                ((RelativeLayout) viewHolder.itemView.findViewById(R.id.message_user).getParent()).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setActionModebar();
                        FrameLayout frame=new FrameLayout(activity);
                        frame.setBackgroundColor(Color.WHITE);
                        ((RelativeLayout) viewHolder.itemView.findViewById(R.id.message_user).getParent()).setSelected(true);
                        ((RelativeLayout) viewHolder.itemView.findViewById(R.id.message_user).getParent()).addView(frame);

                         v.setSelected(true);

                        return false;

                    }
                });

            }


        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition =
                        layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    listOfMessages.scrollToPosition(positionStart);
                }
            }
        });

        listOfMessages.setAdapter(adapter);

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupAdapter();

                } else {
                    Toast.makeText(activity,
                            "Access Denied",
                            Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }




}
