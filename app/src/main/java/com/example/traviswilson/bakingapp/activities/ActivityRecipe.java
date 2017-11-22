package com.example.traviswilson.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.sync.BakingSyncAdapter;

/**
 * Created by traviswilson on 7/26/17.
 * Holder class for FragmentRecipe.
 */
public class ActivityRecipe extends AppCompatActivity implements FragmentRecipe.Callback {
    public static final String RECIPE_TITLE_KEY= "key";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BakingSyncAdapter.initializeSyncAdapter(this);
        BakingSyncAdapter.syncImmediately(this);
        setContentView(R.layout.activity_recipe);
    }

    @Override
    public void onRecipeClicked(String recipeTitle) {
        Intent i = new Intent(this, ActivityDetail.class);
        i.putExtra(RECIPE_TITLE_KEY, recipeTitle);
        startActivity(i);
    }
}
