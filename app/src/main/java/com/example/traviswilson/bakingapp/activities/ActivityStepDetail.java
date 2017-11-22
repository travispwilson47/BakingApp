package com.example.traviswilson.bakingapp.activities;

/**
 * Created by traviswilson on 11/1/17.
 */

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.database.DatabaseUtilsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.data.BakingContract;

import java.util.ArrayList;

import static com.example.traviswilson.bakingapp.activities.ActivityDetail.RECIPE_DESCRIPTION_KEY;
import static com.example.traviswilson.bakingapp.activities.ActivityDetail.RECIPE_NAME_KEY;
import static com.example.traviswilson.bakingapp.activities.ActivityDetail.RECIPE_STEP_NUMBER_KEY;
import static com.example.traviswilson.bakingapp.activities.ActivityDetail.RECIPE_VIDEO_URL_KEY;


public class ActivityStepDetail extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        ViewPager.OnPageChangeListener{

    private static final String LOG_TAG = ActivityStepDetail.class.toString();
    private ViewPager mPager;

    private FragmentStepDetail[] fragmentList;

    private int startItem;
    private String recipeName;
    private int numPages; // to be set in the onCreate
    private Cursor mCursor;

    private int STEP_DETAIL_LOADER = 0;

    private static String[] recipeStepProjection =
            {BakingContract.RecipeMain.TABLE_NAME+ "." + BakingContract.RecipeMain._ID, BakingContract.RecipeStep.DESCRIPTION,
                    BakingContract.RecipeStep.VIDEO_URL};


    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        recipeName = getIntent().getStringExtra(RECIPE_NAME_KEY);
        startItem = parseStartItem();
        numPages = getIntent().getIntExtra(ActivityDetail.RECIPE_TOTAL_STEPS_KEY, -1);
        Log.v(LOG_TAG, recipeName);
        // Instantiate a ViewPager and a PagerAdapter.
        getLoaderManager().initLoader(STEP_DETAIL_LOADER, null, this);
    }
    private int parseStartItem(){
        String precursor = getIntent().getStringExtra(RECIPE_STEP_NUMBER_KEY);
        if (precursor.equals("")){
            return 0;
        }
        return Integer.parseInt(getIntent().getStringExtra(RECIPE_STEP_NUMBER_KEY));
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == startItem) {
            // If the user is currently looking at the first step it checked out, then allow the
            // system to handle the back by popping back stack.
            Log.v(LOG_TAG, "on start item");
            super.onBackPressed();
        } else if ( mPager.getCurrentItem() > startItem){
            // Otherwise, select the previous step.
            Log.v(LOG_TAG, "greater than start item");
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            Log.v(LOG_TAG, "less than start item");
        } //both of these move the user towards the item they started with.
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Loader for Ingredients
        return new CursorLoader(this,
                BakingContract.RecipeStep.CONTENT_URI_WITH_MAIN_ADJOINED,
                recipeStepProjection,
                BakingContract.RecipeMain.NAME,
                new String[]{recipeName},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.v(LOG_TAG, "On load finished called");
            mPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mCursor = cursor;
            formFragmentArray(mCursor);
            Log.v(LOG_TAG, DatabaseUtils.dumpCursorToString(mCursor));
            mCursor.moveToFirst();
            mPager.setOnPageChangeListener(this);
            mPager.setAdapter(mPagerAdapter);

            mPager.setCurrentItem(startItem);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor.close();
        mCursor = null;
        mPager = null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        FragmentStepDetail frag = fragmentList[mPager.getCurrentItem()];
        if (frag != null){
            frag.onPageScrolled();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void formFragmentArray(Cursor cursor){
        fragmentList = new FragmentStepDetail[numPages];
        int i;
        if(cursor.moveToFirst()){
            i = -1;
            do{
                FragmentStepDetail fragmentStepDetail = new FragmentStepDetail();
                Bundle b = new Bundle();
                b.putString(RECIPE_NAME_KEY, recipeName);
                b.putString(RECIPE_VIDEO_URL_KEY,  mCursor.getString(mCursor.getColumnIndex(
                        BakingContract.RecipeStep.VIDEO_URL)));
                b.putString(RECIPE_DESCRIPTION_KEY, mCursor.getString(mCursor.getColumnIndex(
                        BakingContract.RecipeStep.DESCRIPTION)));
                fragmentStepDetail.setArguments(b);
                fragmentList[++i] = fragmentStepDetail;
            } while (cursor.moveToNext());
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList[position];
        }

        @Override
        public int getCount() {
            return numPages;
        }
    }
}
