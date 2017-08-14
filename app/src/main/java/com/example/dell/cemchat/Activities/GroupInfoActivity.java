package com.example.dell.cemchat.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dell.cemchat.Models.GroupModel;
import com.example.dell.cemchat.Models.User;
import com.example.dell.cemchat.R;
import com.example.dell.cemchat.utils.DateHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by it on 16/07/2017.
 */

public class GroupInfoActivity extends AppCompatActivity {
TextView groupCounter;
TextView groupcraeted;
ImageView groupIcon;
    RecyclerView members;
Activity activity;
    String groupKey;
    String friendID;
    String fixedText;
    FloatingActionButton addmember;


String currentuser;
String groupname;
String groupAdmin;
String dataCreated;
ArrayList <User> memberList;
    ArrayList <User>groupList;
    ArrayList <String> membersNames;
DatabaseReference groupNode;
DatabaseReference friendNode;
String downloadUrl;
   Toolbar toolbar;
    MoviesAdapter adapter;
    CollapsingToolbarLayout collapsingToolbar;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    static final int REQUEST_PROFILE =4;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
         toolbar = (Toolbar) findViewById(R.id.group_info_toolbar);

        //setSupportActionBar(toolbar);
       // groupCounter=(TextView) findViewById(R.id.group_counts);
        groupIcon=(ImageView) findViewById(R.id.group_icon);
        groupcraeted=(TextView) findViewById(R.id.group_name_created);
        members=(RecyclerView) findViewById(R.id.list_of_members);
        memberList=new ArrayList();
        groupList=new ArrayList();
        membersNames=new ArrayList<>();
         collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
       if(getIntent().getStringExtra("groupkey")!=null){
        groupKey=getIntent().getStringExtra("groupkey");
           groupNode= FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey);}
        else{ friendID=getIntent().getStringExtra("friendId");
           friendNode= FirebaseDatabase.getInstance().getReference().child("users").child(friendID);}
          addmember=(FloatingActionButton) findViewById(R.id.add_member);
        activity=this;
 currentuser=FirebaseAuth.getInstance().getCurrentUser().getUid();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        members.setHasFixedSize(true);
        members.setLayoutManager(layoutManager);




        if(groupKey!=null) {
            fixedText="Participants";
            createMemberList();
            groupNode.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    GroupModel model = dataSnapshot.getValue(GroupModel.class);
                    groupname = model.getGroupName();
                    groupAdmin = model.getGroupAdmin();

                    String time = DateFormat.format("HH:mm:ss",
                            model.getDataCreated()).toString();

                    Date messageTime = new Date(model.getDataCreated());
                    String day = DateHelper.getInstance().compareDate(messageTime);

                    // toolbar.setTitle(groupname);
                    collapsingToolbar.setTitle(groupname);

                    collapsingToolbar.setCollapsedTitleTextColor(Color.BLACK);
                    groupcraeted.setText("Created by " + groupAdmin + " at " + day);

                    if (model.getGroupPhoto() != null) {
                        Glide.with(activity).load(model.getGroupPhoto()).apply(RequestOptions.overrideOf(800, 500)).into(groupIcon);
                    } else {
                        groupIcon.setImageDrawable(getResources().getDrawable(R.drawable.if_community_3319));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            groupIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updatePB();
                }
            });

        }
        else{
           fixedText="Groups in Common";
            addmember.setVisibility(View.INVISIBLE);
            createGroupList();

            friendNode.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user= dataSnapshot.getValue(User.class);

                    collapsingToolbar.setTitle(user.getName());
                    groupcraeted.setText(user.getPhone());

                    if (user.getPhoto() != null) {
                        Glide.with(activity).load(user.getPhoto()).apply(RequestOptions.overrideOf(800, 500)).into(groupIcon);
                    } else {
                        groupIcon.setImageDrawable(getResources().getDrawable(R.drawable.profile_img));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for ( User user: memberList){
                    membersNames.add(user.getName());
                }
                Intent intent = new Intent(activity, AddMembersActivity.class);
                intent.putExtra("groupkey", groupKey).putStringArrayListExtra("members",membersNames);
                startActivity(intent);
            }
        });


    }



   private ArrayList<User> createMemberList(){
        FirebaseDatabase.getInstance().getReference().child("mebers").child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot chid : dataSnapshot.getChildren()) {


                if (!chid.getKey().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                    FirebaseDatabase.getInstance().getReference().child("users").child(chid.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User member= dataSnapshot.getValue(User.class);
                            memberList.add(member);

                               setUpAdapter();






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
       return memberList;
   }

   private ArrayList<User>createGroupList(){

       FirebaseDatabase.getInstance().getReference().child("mebers").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               for(DataSnapshot child : dataSnapshot.getChildren()){

                 if(child.hasChild(friendID)& child.hasChild(currentuser)){
                     FirebaseDatabase.getInstance().getReference().child("groups").child(child.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             GroupModel model=dataSnapshot.getValue(GroupModel.class);
                                 if (model!=null){
                                     User user=new User();
                                     user.setName(model.getGroupName());
                                     user.setPhoto(model.getGroupPhoto());
                                     memberList.add(user);}
                                       setUpAdapter();


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


      return memberList;
   }


        class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

           private List<User> memberList;

            class MyViewHolder extends RecyclerView.ViewHolder {
               public TextView membername;
                public ImageView memberimage;
                public TextView particpate;

               public MyViewHolder(View view) {
                   super(view);
                   membername = (TextView) view.findViewById(R.id.user_name);
                   memberimage =(ImageView)view.findViewById(R.id.user_image);
                   particpate = (TextView) view.findViewById(R.id.participate);
               }
           }


           public MoviesAdapter(ArrayList<User> moviesList) {
               this.memberList = moviesList;
           }

           @Override
           public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
               View itemView = LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.member_user, parent, false);

               return new MyViewHolder(itemView);
           }

           @Override
           public void onBindViewHolder(MyViewHolder holder, int position) {
               if (position==0){
                   holder.particpate.setVisibility(View.VISIBLE);
                   holder.particpate.setText(fixedText);
               }
              User user = memberList.get(position);

               holder.membername.setText(user.getName());
               Glide.with(activity).load(user.getPhoto()).apply(RequestOptions.circleCropTransform()).apply(RequestOptions.overrideOf(100, 100)).into( holder.memberimage);

           }

           @Override
           public int getItemCount() {
               return memberList.size();
           }
       }



    private void updatePB(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_PROFILE );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Uri selectedimg = data.getData();
            StorageReference profilesRef = storageRef.child("profilePic").child(groupKey);//.child(selectedimg.getLastPathSegment());
            final UploadTask uploadTask = profilesRef.putFile(selectedimg);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                @SuppressWarnings("VisibleForTests")
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = taskSnapshot.getDownloadUrl().toString();@SuppressWarnings("VisibleForTests")
                    SharedPreferences preferences = activity.getSharedPreferences("my file", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("myphoto", downloadUrl).commit();
                    Map<String, Object> namemap= new HashMap<>() ;
                    namemap.put("groupPhoto",downloadUrl);
                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).updateChildren(namemap);
                    String pic= FirebaseStorage.getInstance().getReference().child("profilePic").child(groupKey).getDownloadUrl().toString();
                    Glide.with(activity)
                            //.load(FirebaseStorage.getInstance().getReference().child("profilePic").child(id).getDownloadUrl().toString())
                            .load(downloadUrl )
                            .apply(RequestOptions.overrideOf(800,500))
                            .into(groupIcon);

                    Toast.makeText(activity,
                            " Profile Picture Successfully Updated",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
    }

    private void setUpAdapter(){

        adapter= new MoviesAdapter(memberList);
        members.setNestedScrollingEnabled(false);
        members.setAdapter(adapter);
    }
}
