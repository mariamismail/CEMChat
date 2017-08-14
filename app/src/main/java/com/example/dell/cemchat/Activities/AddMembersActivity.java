package com.example.dell.cemchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.dell.cemchat.R;
import com.example.dell.cemchat.fragments.ContactsFragment;

import java.util.ArrayList;

/**
 * Created by DELL on 6/8/2017. contaning the contact fragment to add mem
 */

public class AddMembersActivity extends AppCompatActivity {
    Toolbar toolbarBottom;
    String key;
    ArrayList <String> mem;
    String chatroom;
    ArrayList<String> selectedMsgs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(getIntent()!=null){
//       String s= getIntent().getStringExtra("callingActivity");
//        if(s.equals("chatRoom")){
//            setTheme(R.style.AppTheme_NoActionBar);
//        }
//        else {setTheme(android.R.style.Theme_DeviceDefault_Dialog);}}
        setContentView(R.layout.activity_add_members);
        Bundle extras= new Bundle();
        if(this.getIntent().getStringExtra("groupkey")!=null){
        key=this.getIntent().getStringExtra("groupkey");
            mem= this.getIntent().getStringArrayListExtra("members");
            extras.putInt("id",2);
            extras.putString("groupKey",key);
            extras.putStringArrayList("members",mem);}

        else if(this.getIntent().getStringExtra("callingActivity")!=null) {
             chatroom = this.getIntent().getStringExtra("callingActivity");
             selectedMsgs = this.getIntent().getStringArrayListExtra("selectedMsgs");
            extras.putInt("id",3);
            extras.putString("callingActivity",chatroom);
            extras.putStringArrayList("selectedMsgs",selectedMsgs);
        }

        ContactsFragment fragment= new ContactsFragment();
        fragment .setArguments(extras);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_mem, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    if (resultCode==RESULT_OK){
        toolbarBottom.setVisibility(View.VISIBLE);

    }

    }
}
