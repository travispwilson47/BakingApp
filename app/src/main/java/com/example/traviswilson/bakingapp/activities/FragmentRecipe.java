package com.example.traviswilson.bakingapp.activities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.adapters.MyRecyclerAdapter;
import com.example.traviswilson.bakingapp.adapters.RecipeAdapter;
import com.example.traviswilson.bakingapp.adapters.RecipeCardAdapter;
import com.example.traviswilson.bakingapp.data.BakingContract;

/**
 * Created by traviswilson on 7/26/17.
 * Class contains a recyclerView that is populated by the titles from the
 * stored information.
 */

public class FragmentRecipe extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, RecipeCardAdapter.Callback {
    private static final String LOG_TAG = FragmentRecipe.class.toString();
    RecyclerView recyclerView;
    private static final int TITLE_CARD_LOADER = 0;

    Callback mListener;

    String[] projection = {BakingContract.RecipeMain._ID, BakingContract.RecipeMain.NAME};

    //TODO: make an onClickListener that sends the name by callbackListener and through intent…
    public interface Callback{
        public void onRecipeClicked(String recipeTitle);
    }
    //Enforce the callback on the Activity
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            mListener = (Callback) getActivity();
        } catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() +"Must implement FragmentRecipe" +
                    ".CallBack ");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        getLoaderManager().initLoader(TITLE_CARD_LOADER, null, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        recyclerView = rootView.findViewById(R.id.recipe_recycleView);
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //NO id other than TITLE_CARD_LOADER
        return new CursorLoader(getActivity(), BakingContract.RecipeMain.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        recyclerView.swapAdapter(new MyRecyclerAdapter(getActivity(), data, MyRecyclerAdapter.AdapterType.CARD, this)
                , true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recyclerView.swapAdapter(null, true);
    }

    @Override
    public void onRecipeClicked(String recipeTitle) {
        mListener.onRecipeClicked(recipeTitle);
    }
}
