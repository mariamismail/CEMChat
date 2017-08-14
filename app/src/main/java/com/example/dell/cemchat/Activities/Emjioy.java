package com.example.dell.cemchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;

import com.example.dell.cemchat.R;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconGridFragment;

import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import io.github.rockerhieu.emojicon.EmojiconHandler;
import io.github.rockerhieu.emojiconize.Emojiconize;

/**
 * Created by it on 12/07/2017.
 */


public class Emjioy extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {

   EmojiconEditText mEditEmojicon;
    EmojiconTextView mTxtEmojicon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emjioy);
        mEditEmojicon = (EmojiconEditText) findViewById(R.id.editEmojicon);
        mTxtEmojicon = (EmojiconTextView) findViewById(R.id.txtEmojicon);
        mEditEmojicon.addTextChangedListener(new TextWatcher() {

            /**
             * This notify that, within s,
             * the count characters beginning at start are about to be replaced by new text with length
             *
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * This notify that, somewhere within s, the text has been changed.
             *
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
            }

            /**
             * This notify that, within s, the count characters beginning at start have just
             * replaced old text that had length
             *
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //mTxtEmojicon.setText(s);

                Intent intent = new Intent();
                intent.putExtra("emotion", s.toString());
                setResult(600, intent);
               finish();
            }
        });

        setEmojiconFragment(false);
    }


    /**
         * Set the Emoticons in Fragment.
         * @param useSystemDefault
         */
        private void setEmojiconFragment(boolean useSystemDefault) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                    .commit();
        }


    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mEditEmojicon, emojicon);

    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEditEmojicon);

    }
}

