package com.example.traviswilson.bakingapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.sync.BakingSyncAdapter;

/**
 * Created by traviswilson on 7/26/17.
 * Holder class for FragmentRecipe.
 */

public class ActivityRecipe extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        BakingSyncAdapter.initializeSyncAdapter(this);
        setContentView(R.layout.activity_recipe);
    }
}
