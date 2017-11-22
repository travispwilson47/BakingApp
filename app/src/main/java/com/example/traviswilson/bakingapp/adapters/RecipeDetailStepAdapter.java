package com.example.traviswilson.bakingapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.Utility;
import com.example.traviswilson.bakingapp.data.BakingContract;

import okhttp3.internal.Util;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by traviswilson on 10/31/17.
 */

public class RecipeDetailStepAdapter extends RecipeAdapter {
    Callback listener;

    public RecipeDetailStepAdapter(Context context, Cursor c, int flags
            , MyRecyclerAdapter.Callback listener) {
        super(context, c, flags);
        try {
            this.listener = (Callback) listener;
        } catch (ClassCastException e){
            throw new ClassCastException("Fragment using this class must implement this specific subclass " +
            "of the Callback Listener (DetailStep)");
        }
    }
    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.step_item, viewGroup,false);
        view.setTag(new ViewHolder(view));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder holder = (ViewHolder) view.getTag();
                String recipeStepNumber = cursor.getString(cursor.getColumnIndex(BakingContract.RecipeStep.STEP_ID));
                String stepDescription =
                        cursor.getString(cursor.getColumnIndex(BakingContract.RecipeStep.DESCRIPTION));
                String videoUrl =
                        cursor.getString(cursor.getColumnIndex(BakingContract.RecipeStep.VIDEO_URL));

                listener.onStepClicked(recipeStepNumber,stepDescription, videoUrl );
            }
        });
        return view;
    }

    public interface Callback extends MyRecyclerAdapter.Callback{
        public void onStepClicked(String recipeStepNumber, String stepDescription,
                                  String videoUrl);
    }

    public static class ViewHolder {
        TextView recipeShortDescription;
        TextView recipeStepNumber;

        public ViewHolder(View stepView){
            recipeShortDescription = stepView.findViewById(R.id.step);
            recipeStepNumber = stepView.findViewById(R.id.step_id);

        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder v = (ViewHolder) view.getTag();
        v.recipeStepNumber.setText(Utility.convertComputerScienceStepToHumanStep(
                cursor.getString(cursor.getColumnIndex(BakingContract.RecipeStep.STEP_ID))));
        v.recipeShortDescription.setText(Utility.formatString(context,
                cursor.getString(cursor.getColumnIndex(BakingContract.RecipeStep.SHORT_DESCRIPTION))));
    }
}
