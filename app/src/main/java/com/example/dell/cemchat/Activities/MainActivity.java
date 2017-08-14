package com.example.dell.cemchat.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dell.cemchat.Models.ChatMessage;
import com.example.dell.cemchat.R;
import com.example.dell.cemchat.adapter.TabsAdapter;
import com.example.dell.cemchat.fragments.ContactsFragment;
import com.example.dell.cemchat.fragments.GroupsFragment;
import com.example.dell.cemchat.fragments.MessagesFragment;
import com.example.dell.cemchat.utils.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    private static final int SIGN_IN_REQUEST_CODE=123;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 122;
    private FirebaseListAdapter<ChatMessage> adapter;
    private FirebaseAuth mAuth;
    String user;
    private ViewPager viewPger;
    private TabLayout tabLayout;
    private TabsAdapter tabAdapter;
    Activity activity;
    GoogleApiClient mGoogleApiClient;
    private ViewPager.OnPageChangeListener listener;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    static ArrayList<String> keyses;
    Boolean b;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        //toolbar.  getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.logo);
        activity=this;
      this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

     viewPger = (ViewPager) findViewById(R.id.tabsPager);
        tabLayout = (TabLayout) findViewById(R.id.tabsLayout);
        LinearLayout linearLayout = (LinearLayout)tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.GRAY);
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(10);
        linearLayout.setDividerDrawable(drawable);



//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//
//
//
//                .build();




        Boolean network=Utils.isNetworkConnected(this);
        if(network){
          //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {

            // Start sign in/sign up activity
            //startActivityForResult(intent,code)
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.drawable.com_facebook_button_login_logo)
                            .setTheme(R.style.GreenTheme)
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            String id=user1.getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(id).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
            String name=FirebaseAuth.getInstance()
                    .getCurrentUser()
                    .getDisplayName();
            if (user1 != null & name!=null) {

                Toast.makeText(this,
                        "Welcome " + name,
                        Toast.LENGTH_LONG)
                        .show();
            }




            // Load chat room contents
            user=FirebaseAuth.getInstance().getCurrentUser().getUid();

            boolean result = checkPermission();
            boolean googlePlay= isGooglePlayServicesAvailable(activity) ;
            if (result &googlePlay) {
                setupViewPager();
                tabLayout.setupWithViewPager(viewPger);
                tabLayout.getTabAt(0).setText("CHATS");
                tabLayout.getTabAt(1).setText("CONTACTS");
                tabLayout.getTabAt(2).setText("Groups");
                //View root = tabLayout.getChildAt(0);
//                if (root instanceof LinearLayout) {
//                    ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//                    GradientDrawable drawable = new GradientDrawable();
//                    drawable.setColor(getResources().getColor(R.color.tw__composer_black));
//                    drawable.setSize(2, 1);
//                    ((LinearLayout) root).setDividerPadding(10);
//                    ((LinearLayout) root).setDividerDrawable(drawable);
//                }

            }

        }

    }
        else {


            if(FirebaseAuth.getInstance().getCurrentUser() == null) {

                // Start sign in/sign up activity
                //startActivityForResult(intent,code)
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setTheme(R.style.GreenTheme)
                                .build(),
                        SIGN_IN_REQUEST_CODE
                );
            }
            Toast.makeText(this,
                "No Network Connection please connect and try again ",
                Toast.LENGTH_LONG)
                .show();
             // setupViewPager();
//            tabLayout.setupWithViewPager(viewPger);
//            tabLayout.getTabAt(0).setText("CHATS");
//            tabLayout.getTabAt(1).setText("CONTACTS");
//            tabLayout.getTabAt(2).setText("Groups");
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Access Contacts permission is necessary to update contacts!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupViewPager();
                } else {
//code for deny
                }
                break;
        }
    }
    private void setupViewPager( ) {




try {
    MessagesFragment fragment = new MessagesFragment();

        ContactsFragment fragment1= new ContactsFragment();
        Bundle extras= new Bundle();
        extras.putInt("id",1);
        fragment1 .setArguments(extras);

        GroupsFragment fragment2= new GroupsFragment();




        tabAdapter = new TabsAdapter(getSupportFragmentManager());

        tabAdapter.addFragment(fragment , "Mssages");

        tabAdapter.addFragment(fragment1 , "contacts");
        tabAdapter.addFragment(fragment2 , "Groups");

        viewPger.setAdapter(tabAdapter);


    }
catch (IndexOutOfBoundsException e){  Toast.makeText(activity,
        " Error try to refresh " ,
        Toast.LENGTH_LONG)
        .show();}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    //OnCompleteListener<TResult> 	Listener called when a Task completes and new onCompelte refers to the activity itself.
                    .addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();


                            // Close activity
                            //finish();
                            startActivityForResult(
                                    AuthUI.getInstance()
                                            .createSignInIntentBuilder()
                                            .setTheme(R.style.GreenTheme)
                                            .build(),
                                    SIGN_IN_REQUEST_CODE
                            );
                        }
                    });
        }
        if(item.getItemId() == R.id.menu_update_profile) {
            Intent intent = new Intent(activity, UserProfileActivity.class);
            intent.putExtra("id",user);
            startActivityForResult(intent,100);

        }

        if(item.getItemId() == R.id.menu_refresh) {
            setupViewPager();
            tabLayout.setupWithViewPager(viewPger);
            tabLayout.getTabAt(0).setText("CHATS");
            tabLayout.getTabAt(1).setText("CONTACTS");
            tabLayout.getTabAt(2).setText("Groups");


        }
        return true;
    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                user=FirebaseAuth.getInstance().getCurrentUser().getUid();

           //get the ptofile information from the activity
                DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("users");
       reference.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      b=   dataSnapshot.hasChild(user);
        if(!b){
            Intent intent = new Intent(activity, UserProfileActivity.class);
            intent.putExtra("id",user);
            startActivityForResult(intent,100);
        }
        setupViewPager();
        tabLayout.setupWithViewPager(viewPger);

        tabLayout.getTabAt(0).setText("CHATS");
        tabLayout.getTabAt(1).setText("CONTACTS");
        tabLayout.getTabAt(2).setText("Groups");

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});




            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }
        if(requestCode == 100&resultCode == RESULT_OK){

            setupViewPager();
            tabLayout.setupWithViewPager(viewPger);

            tabLayout.getTabAt(0).setText("CHATS");
            tabLayout.getTabAt(1).setText("CONTACTS");
            tabLayout.getTabAt(2).setText("Groups");

        }



    }

}
