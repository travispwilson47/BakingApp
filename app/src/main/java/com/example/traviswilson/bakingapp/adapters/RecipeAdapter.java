package com.example.traviswilson.bakingapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;

/**
 * Created by traviswilson on 10/31/17.
 */

public abstract class RecipeAdapter extends CursorAdapter {
    public RecipeAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
}
