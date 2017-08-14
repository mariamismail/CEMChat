package com.example.dell.cemchat.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dell.cemchat.Models.User;
import com.example.dell.cemchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 5/24/2017.
 */

public class UserProfileActivity extends AppCompatActivity {
    static final int REQUEST_PROFILE =4;
    EditText userName;
    EditText userphone;
    Button save;
    ImageView profile;
    Activity activity;
    String id;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    String downloadUrl;
    String myphotoUrl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        toolbar.setLogo(R.drawable.logo);
        toolbar.setTitle("            Update Profile");
        setSupportActionBar(toolbar);

        userName = (EditText)findViewById(R.id.user_name);
        userphone= (EditText)findViewById(R.id.phoneNum);
        save= (Button)findViewById(R.id.save);
        profile=(ImageView) findViewById(R.id.profileButton);
        activity=this;
       id= getIntent().getStringExtra("id");


        SharedPreferences sharedPref = this.getSharedPreferences("my file",Context.MODE_PRIVATE);
        if(sharedPref.getString("myphoto","").equals("")){

            profile.setImageDrawable(getResources().getDrawable(R.drawable.profile_img));
        }
        else{   myphotoUrl= sharedPref.getString("myphoto","");
            Glide.with(activity)
                    .load(myphotoUrl)
                    .apply(RequestOptions.overrideOf(800,50))
                    .into(profile);}



        String name= FirebaseAuth.getInstance()
                .getCurrentUser()
                .getDisplayName();
        if(name!=null){
            userName.setText(name);
        }

profile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        updatePB();
    }
});
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = userName.getText().toString();
                String phone = userphone.getText().toString();
                if ((Name.isEmpty()) &(phone.isEmpty())) {
                    Toast.makeText(activity,
                            "Please Compelete Required Data",
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                try{
                    User user = new User();
                    user.setName(Name);
                    user.setPhone(phone);
                    user.setUserId(id);
                    user.setToken(FirebaseInstanceId.getInstance().getToken());
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(Name)
                            // .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                            .build();
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


                    firebaseUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(activity,
                                                "User profile updated.",
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });


                    FirebaseDatabase.getInstance().getReference().child("users").child(id).setValue(user);
                    FirebaseDatabase.getInstance().getReference().child("phones").child(id).setValue(phone);
                    setResult(RESULT_OK);
                    finish();
                }
                catch (DatabaseException e){
                    Toast.makeText(activity,
                            "Can't Update now try again later",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
            }
        });
    }
    private void updatePB(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_PROFILE );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            Uri selectedimg = data.getData();
            StorageReference profilesRef = storageRef.child("profilePic").child(id);//.child(selectedimg.getLastPathSegment());
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
                    namemap.put("photo",downloadUrl);
                    FirebaseDatabase.getInstance().getReference().child("users").child(id).updateChildren(namemap);
String pic=FirebaseStorage.getInstance().getReference().child("profilePic").child(id).getDownloadUrl().toString();
                    Glide.with(activity)
                            //.load(FirebaseStorage.getInstance().getReference().child("profilePic").child(id).getDownloadUrl().toString())
                            .load(downloadUrl )
                            .apply(RequestOptions.overrideOf(800,500))
                            .into(profile);

                    Toast.makeText(activity,
                            " Profile Picture Successfully Updated",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
    }
}
