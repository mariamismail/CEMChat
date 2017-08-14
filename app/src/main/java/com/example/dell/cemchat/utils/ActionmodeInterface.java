package com.example.dell.cemchat.utils;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by it on 26/07/2017.
 */

public interface ActionmodeInterface extends ActionMode.Callback {
    @Override
    boolean onCreateActionMode(ActionMode mode, Menu menu);

    @Override
    boolean onPrepareActionMode(ActionMode mode, Menu menu);

    @Override
    boolean onActionItemClicked(ActionMode mode, MenuItem item);

    @Override
    void onDestroyActionMode(ActionMode mode);
}
