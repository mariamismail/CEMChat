package com.example.dell.cemchat.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.example.dell.cemchat.R;


/**
 * Created by DELL on 5/3/2017.
 */

public  class InsertnameDialogFragment extends DialogFragment {
    EditText userinput ;
    String inputvalue;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction


        View rootView= getActivity().getLayoutInflater().inflate(R.layout.fragment_insert,null);
        userinput = (EditText) rootView.findViewById(R.id.item_added);
        return new AlertDialog.Builder(getActivity()).setView(rootView)
                               .setTitle("Choose Group Name")
                               .setPositiveButton("ok", new DialogInterface.OnClickListener() {

       @Override
            public void onClick(DialogInterface dialog, int id) {
                inputvalue =  userinput.getText().toString();
           Intent intent = new Intent();
           intent.putExtra("name",inputvalue);
     getTargetFragment().onActivityResult(getTargetRequestCode(),122,intent);
                // Do something with value in inputvalue
            }
        })
        .setNegativeButton(("Cancel"), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        })


        // Create the AlertDialog object and return it
      .create();
    }

}
