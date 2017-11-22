package com.example.traviswilson.bakingapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.traviswilson.bakingapp.R;

/**
 * Created by traviswilson on 10/23/17.
 * This class serves as a wrapper class for RecipeAdapter
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private static final String LOG_TAG = MyRecyclerAdapter.class.toString();
    RecipeAdapter mAdapter;

    Context mContext;

    Callback listener;

    int count;

    public enum AdapterType {CARD, DETAIL_STEP, DETAIL_INGREDIENT}; //TODO: Add types as needed for Step_Detail (third fragment)

    public MyRecyclerAdapter(Context context, Cursor cursor, AdapterType type, Callback listener ){
        mContext = context;

        this.listener = listener;
        if (type.equals(AdapterType.CARD)){
            mAdapter = new RecipeCardAdapter(context, cursor, 0, listener);
            Log.v(LOG_TAG, "made it to init");
        } else if (type.equals(AdapterType.DETAIL_INGREDIENT)){
            mAdapter = new RecipeDetailIngredientAdapter(context, cursor, 0, listener);
        } else {
            mAdapter = new RecipeDetailStepAdapter(context, cursor, 0, listener);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mAdapter.newView(mContext, mAdapter.getCursor(), parent);
        return new ViewHolder(v);
    }
    //not meant to be implemented directly, but a sub callback is meant to be.
    //this is to do two things: prevent this class from knowing specific callback information (this class)
    //is at a higher level of abstraction) and to force the lower class to implement a subclass Callback.
    interface Callback{
    }


    @Override
    public int getItemCount() {
        return mAdapter.getCount();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Passing the binding operation to cursor loader
        mAdapter.getCursor().moveToPosition(position);
        mAdapter.bindView(holder.itemView, mContext, mAdapter.getCursor());
    }

    /**
     * Shell ViewHolder used to satisfy class requirement, actual viewHolder is in RecipeAdapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
