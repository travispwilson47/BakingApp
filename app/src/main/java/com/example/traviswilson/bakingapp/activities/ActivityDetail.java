package com.example.traviswilson.bakingapp.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.adapters.MyRecyclerAdapter;
import com.example.traviswilson.bakingapp.data.BakingContract;

import static android.R.attr.start;

/**
 * Created by traviswilson on 10/30/17.
 */
public class ActivityDetail extends AppCompatActivity implements FragmentDetail.Callback, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String RECIPE_NAME_KEY = "key";
    public static final String RECIPE_STEP_NUMBER_KEY = "key2";
    private static final String FRAGMENT_STEP_DETAIL_TAG = "fragTag";
    private static final String RECIPE_STEP_ONE = "0";
    public static final String RECIPE_VIDEO_URL_KEY = "key";
    public static final String RECIPE_DESCRIPTION_KEY = "key2";
    public static final String RECIPE_TOTAL_STEPS_KEY = "key3";
    private static final String LOG_TAG = ActivityDetail.class.toString() ;

    private boolean modeTablet;

    FragmentDetail mDetailFragment;
    FragmentStepDetail mDetailStepFragment;

    private int totalSteps;

    private boolean fragmentSet;

    public static final int DETAIL_INGREDIENT_LOADER = 0;
    public static final int DETAIL_STEP_LOADER = 1;

    private static String[] recipeIngredientProjection =
            {BakingContract.RecipeMain.TABLE_NAME+ "." + BakingContract.RecipeMain._ID,
                    BakingContract.RecipeIngredients.QUANTITY, BakingContract.RecipeIngredients.MEASURE,
                    BakingContract.RecipeIngredients.INGREDIENT};
    private static String[] recipeStepProjection =
            {BakingContract.RecipeMain.TABLE_NAME+ "." + BakingContract.RecipeMain._ID,
                    BakingContract.RecipeStep.SHORT_DESCRIPTION, BakingContract.RecipeStep.DESCRIPTION,
                    BakingContract.RecipeStep.VIDEO_URL, BakingContract.RecipeStep.STEP_ID};


    String recipeTitle;
    String videoURL1;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        recipeTitle = getIntent().getStringExtra(ActivityRecipe.RECIPE_TITLE_KEY);
        setContentView(R.layout.activity_detail);
        modeTablet = findViewById(R.id.detail_step_container) != null;
        //In tablet mode, Activity detail has master detail flow, in the other mode, it has a
        //Third activity in which the Step_Details cycle through in presentation style
        getLoaderManager().initLoader(DETAIL_INGREDIENT_LOADER, null, this);
        getLoaderManager().initLoader(DETAIL_STEP_LOADER, null, this);



        mDetailFragment = (FragmentDetail) getFragmentManager().findFragmentById(R.id.detail_fragment);
    }
    //Method called first time the details for the FragmentStepDetail loads
    private void setUpFragment(Cursor cursor){
        cursor.moveToFirst();
        mDetailStepFragment = new FragmentStepDetail();
        Bundle b = new Bundle();
        b.putString(RECIPE_NAME_KEY, recipeTitle);
        b.putString(RECIPE_STEP_NUMBER_KEY, RECIPE_STEP_ONE);
        b.putString(RECIPE_VIDEO_URL_KEY,  cursor.getString(cursor.getColumnIndex(
                BakingContract.RecipeStep.VIDEO_URL)));
        b.putString(RECIPE_DESCRIPTION_KEY, cursor.getString(cursor.getColumnIndex(
                BakingContract.RecipeStep.DESCRIPTION)));
        mDetailStepFragment.setArguments(b);

        getSupportFragmentManager().beginTransaction().replace(R.id.detail_step_container, mDetailStepFragment,
                FRAGMENT_STEP_DETAIL_TAG).commit();
        fragmentSet = true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Loader for Ingredients
        if (i == DETAIL_INGREDIENT_LOADER) {
            return new CursorLoader(this,
                    BakingContract.RecipeIngredients.CONTENT_URI_WITH_MAIN_ADJOINED,
                    recipeIngredientProjection,
                    BakingContract.RecipeMain.NAME,
                    new String[]{recipeTitle},
                    null);
        } else {
            Log.v(LOG_TAG, recipeTitle);
            return new CursorLoader(this,
                    BakingContract.RecipeStep.CONTENT_URI_WITH_MAIN_ADJOINED,
                    recipeStepProjection,
                    BakingContract.RecipeMain.NAME,
                    new String[]{recipeTitle},
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == DETAIL_INGREDIENT_LOADER) {
            mDetailFragment.onFinishIngredientDataLoad(cursor);
        } else {
            mDetailFragment.onFinishStepDataLoad(cursor);
            if (!fragmentSet && modeTablet) setUpFragment(cursor);
            totalSteps = cursor.getCount();
        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == DETAIL_INGREDIENT_LOADER){
            mDetailFragment.onIngredientDataReset();
        } else {
            mDetailFragment.onStepsDataReset();
        }
    }

    @Override
    public void onStepClicked(String recipeStepNumber, String stepDescription, String videoURL) {
        if (!modeTablet) {
            Intent i = new Intent(this, ActivityStepDetail.class);
            i.putExtra(ActivityDetail.RECIPE_NAME_KEY, recipeTitle);
            i.putExtra(ActivityDetail.RECIPE_STEP_NUMBER_KEY, recipeStepNumber);
            i.putExtra(ActivityDetail.RECIPE_TOTAL_STEPS_KEY, totalSteps);
            //The other items are not used since the data is reloaded in the new activity.
            startActivity(i);
        } else{
            mDetailStepFragment = new FragmentStepDetail();
            Bundle b = new Bundle();
            b.putString(RECIPE_NAME_KEY, recipeTitle);
            b.putString(RECIPE_STEP_NUMBER_KEY, recipeStepNumber);
            b.putString(RECIPE_VIDEO_URL_KEY,  videoURL);
            b.putString(RECIPE_DESCRIPTION_KEY, stepDescription);

            mDetailStepFragment.setArguments(b);

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_step_container, mDetailStepFragment,
                    FRAGMENT_STEP_DETAIL_TAG).commit();
        }
    }

    @Override
    public String requestRecipeTitle() {
        return recipeTitle;
    }


}
