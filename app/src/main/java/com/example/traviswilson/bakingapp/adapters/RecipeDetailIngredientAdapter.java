package com.example.traviswilson.bakingapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.Utility;
import com.example.traviswilson.bakingapp.data.BakingContract;


/**
 * Created by traviswilson on 11/1/17.
 */

public class RecipeDetailIngredientAdapter extends RecipeAdapter {

    private static final String LOG_TAG = RecipeDetailIngredientAdapter.class.toString();
    Callback listener;
    public RecipeDetailIngredientAdapter(Context context, Cursor c, int flags, MyRecyclerAdapter.Callback listener) {
        super(context, c, flags);
        try {
            this.listener = (Callback) listener;
        } catch (ClassCastException e){
            throw new ClassCastException("Fragment using this class must implement this specific subclass " +
                    "of the Callback Listener (DetailIngredient)");
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.ingredient_item, viewGroup, false);
        view.setTag(new ViewHolder(view));
        return view;
    }
    public static class ViewHolder {
        TextView ingredientName;
        TextView ingredientAmount;

        public ViewHolder(View ingredientView){
            ingredientAmount = ingredientView.findViewById(R.id.measurement);
            ingredientName = ingredientView.findViewById(R.id.ingredient);
        }
    }

    //Marker interface, all adapter listeners must implement a subclass of the MyRecyclerAdapter.Callback
    //This is to prevent MyRecyclerAdapter.Callback from knowing the specific details of what is actually
    //sent back.
    public interface Callback extends MyRecyclerAdapter.Callback {}


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder v = (ViewHolder) view.getTag();
        Log.v(LOG_TAG,Utility.formatString(context,
                cursor.getString(cursor.getColumnIndex(BakingContract.RecipeIngredients.INGREDIENT))));
        v.ingredientAmount.setText(Utility.getMeasurementFromData(
                cursor.getString(cursor.getColumnIndex(BakingContract.RecipeIngredients.QUANTITY)),
                cursor.getString(cursor.getColumnIndex(BakingContract.RecipeIngredients.MEASURE))));
        v.ingredientName.setText(Utility.formatString(context,
                cursor.getString(cursor.getColumnIndex(BakingContract.RecipeIngredients.INGREDIENT))));
    }
}
