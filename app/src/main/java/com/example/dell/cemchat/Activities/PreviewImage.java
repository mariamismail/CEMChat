package com.example.dell.cemchat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dell.cemchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by it on 13/07/2017.
 */

public class PreviewImage extends AppCompatActivity {
    EditText userinput ;
    String inputvalue;
    ImageView imageAdded;
    FloatingActionButton ok;
    FloatingActionButton share;
    FloatingActionButton download;
    Activity callingActivity;
    Activity activity;
    Uri selectedImage;
    String uri;
    TextInputLayout textInputLayout;
    static final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM), "CEM Chat");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview_image);
        imageAdded = (ImageView)findViewById(R.id.image_added);
        textInputLayout=(TextInputLayout) findViewById(R.id.text_layout);
        ok=(FloatingActionButton) findViewById(R.id.send);
        share=(FloatingActionButton)  findViewById(R.id.share);
        download=(FloatingActionButton)  findViewById(R.id.image_download);
        userinput= (EditText)findViewById(R.id.caption);
        callingActivity=this.getParent();
        activity=this;


        if(getIntent().getStringExtra("data")!=null){

            textInputLayout.setVisibility(View.VISIBLE);
            download.setVisibility(View.INVISIBLE);
            share.setVisibility(View.INVISIBLE);
        selectedImage =Uri.parse(getIntent().getStringExtra("data"));
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        imageAdded.setImageBitmap(BitmapFactory.decodeFile(picturePath,options));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userinput.getText().toString()!=null)
                { inputvalue= userinput.getText().toString();}
                Intent result= new Intent();
                result.putExtra("ok",true);

              setResult(800,result);

                finish();

            }
        });



    }

     else  if(getIntent().getStringExtra("Uri")!=null){
            uri=getIntent().getStringExtra("Uri");
           userinput.setVisibility(View.INVISIBLE);
            ok.setVisibility(View.INVISIBLE);
            Glide.with(this)
                    .load(getIntent().getStringExtra("Uri"))
                    .apply(RequestOptions.overrideOf(800,500))
                    .into(imageAdded);


            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadImage();
                }
            });
        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();
            }
        });

    }

private File downloadImage(){
    if(!imageRoot.exists()){
        imageRoot.mkdirs();}
   String name =uri.substring(uri.indexOf("%") + 1, uri.indexOf("?"));

    final File image = new File(imageRoot,name+".jpg");
    StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(getIntent().getStringExtra("Uri"));
    httpsReference
            .getFile(image).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

            Toast.makeText(activity,
                    "Image has been downloaded",
                    Toast.LENGTH_LONG)
                    .show();

        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(activity,
                    "Can't Download image try again later",
                    Toast.LENGTH_LONG)
                    .show();
        }
    });
    return image;
}

private void shareImage(){

    File temp= downloadImage();
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(temp));
    shareIntent.setType("image/jpeg");
    startActivity(Intent.createChooser(shareIntent, "share to"));

     // temp.delete();
}

}
