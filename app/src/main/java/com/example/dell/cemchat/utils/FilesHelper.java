package com.example.dell.cemchat.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dell.cemchat.R;
import com.example.dell.cemchat.data.DataSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by it on 25/07/2017.
 */

public class FilesHelper {
    private static FilesHelper ourInstance ;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    String  downloadUrl;
      FrameLayout downlaod;

    public static FilesHelper getInstance() {

        if(ourInstance==null){

            ourInstance = new FilesHelper();

        }


        return ourInstance;
    }

    private FilesHelper() {

    }

    public void uploadFile(StorageReference reference, Uri selectedimg, final Activity activity, final String text,final String Msgkey,final String friendId,final String friendToken){


        final UploadTask uploadTask = reference.putFile(selectedimg);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(activity,
                        "Unable to upload now please try again later",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")

                String downUrl = taskSnapshot.getDownloadUrl().toString();
                @SuppressWarnings("VisibleForTests")


                String s=   taskSnapshot.getMetadata().getName();@SuppressWarnings("VisibleForTests")
                        String b = taskSnapshot.getMetadata().getContentType();
                downloadUrl = downUrl;
               if (b.contains("image")){
                if (text != null) {
                    DataSource.getInstance().newMsg(Msgkey, text, downloadUrl, b, friendId,friendToken);
                    downloadUrl=null;
                } else {
                    DataSource.getInstance().newMsg(Msgkey, "", downloadUrl, b, friendId,friendToken);
                    downloadUrl=null;
                }}

                else {DataSource.getInstance().newMsg(Msgkey,s, downloadUrl, b, friendId,friendToken);
                   downloadUrl=null;}


                Toast.makeText(activity,
                        "Successfully Uploaded",
                        Toast.LENGTH_LONG)
                        .show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(activity,
                        "Uploading........",
                        Toast.LENGTH_LONG)
                        .show();
            }


        });
    }

    public void downloadFile(File file, FrameLayout download , final Activity activity, final String Msgkey, final String messageText){

        downlaod = download;
        if (!file.exists()) {

            downlaod.setVisibility(View.VISIBLE);
            downlaod.setBackground(activity.getResources().getDrawable(R.drawable.ic_file_download_black_24dp));
            downlaod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //download.setVisibility(View.GONE);

                    final ProgressBar fileProgress = new ProgressBar(activity, null, android.R.attr.progressBarStyleSmall);
                    downlaod .addView(fileProgress);
                    // bar.setVisibility(View.VISIBLE);

                    File baseDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS), "CEM Chat");
                    if(!baseDir.exists()){
                        baseDir.mkdirs();}
                    final File file = new File(baseDir, messageText);

                    StorageReference islandRef = storageRef.child(Msgkey + "/" +messageText);

                    islandRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            downlaod.setVisibility(View.INVISIBLE);

                            // Local temp file has been created
                            Uri filepath = Uri.fromFile(file);
                            //create intent to open it
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(filepath, "application/msword");
                            intent.setDataAndType(filepath, "*/*");


                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(activity,
                                    "Failed downloading",
                                    Toast.LENGTH_LONG)
                                    .show();

                            //  bar.setVisibility(View.GONE);
                            downlaod.setVisibility(View.VISIBLE);

                        }
                    });
                }
            });
        }



    }
 public void openFile(File file,Activity activity,String filename){
     if (file.exists()) {


         Uri filepath = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);
         Intent intent = new Intent(Intent.ACTION_VIEW);
         if (filename.contains(".docx") || filename.contains(".doc")) {
             intent.setDataAndType(filepath, "application/msword");
         } else if (filename.contains(".pdf")) {
             intent.setDataAndType(filepath, "application/pdf");
         } else if (filename.contains(".ppt") || filename.contains(".pptx")) {
             intent.setDataAndType(filepath, "application/vnd.ms-powerpoint");
         }
         else if (filename.contains(".txt")) {

             intent.setDataAndType(filepath,"text/plain");
         }

         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
         activity.startActivity(intent);

     }


 }



    }

