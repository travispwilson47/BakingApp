package com.example.traviswilson.bakingapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.data.BakingContract;

import org.w3c.dom.Text;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.view.View.inflate;
import static com.example.traviswilson.bakingapp.R.id.cardView;

/**
 * Created by traviswilson on 8/1/17.
 */

public class RecipeCardAdapter extends RecipeAdapter {
    private static final String LOG_TAG = RecipeCardAdapter.class.toString();
    Callback listener;

    public RecipeCardAdapter(Context context, Cursor c, int flags, MyRecyclerAdapter.Callback listener) {
        super(context, c, flags);
        try {
            this.listener = (Callback) listener;
        } catch (ClassCastException e){
            throw new ClassCastException("Fragment using this class must implement this specific subclass " +
                    "of the Callback Listener (DetailCard) ");
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        CardView view = (CardView) LayoutInflater.from(context).inflate(R.layout.recipe_item, viewGroup, false);
        view.setTag(new ViewHolder(view));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipeTitle = (String) ( (TextView) view.findViewById(R.id.recipe_title)).getText();
                listener.onRecipeClicked(recipeTitle);
            }
        });
        return view;
    }

    public interface Callback extends MyRecyclerAdapter.Callback {
        public void onRecipeClicked(String recipeTitle);
    }

    public static class ViewHolder{
        TextView recipeTitle;
        public ViewHolder(CardView cardView){
            recipeTitle = cardView.findViewById(R.id.recipe_title);
        }

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder v = (ViewHolder) view.getTag();
        v.recipeTitle.setText(cursor.getString(cursor.getColumnIndex(BakingContract.RecipeMain.NAME)));
    }
}
