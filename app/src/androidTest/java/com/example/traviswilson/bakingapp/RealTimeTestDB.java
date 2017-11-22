package com.example.traviswilson.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.traviswilson.bakingapp.data.BakingContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by traviswilson on 7/28/17.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class RealTimeTestDB {
    private static final String LOG_TAG = "RealTimeTestDB";
    Context mContext;
    @Before
    public void setUpTest(){
        mContext = InstrumentationRegistry.getTargetContext();
    }
    @Test
    public void testDB(){
        Cursor cursor = mContext.getContentResolver().query(BakingContract.RecipeMain.CONTENT_URI, null, null, null, null);
        Log.v(LOG_TAG, "In main: "+cursor.getCount());
        Cursor cursor2 = mContext.getContentResolver().query(BakingContract.RecipeIngredients.CONTENT_URI_WITH_MAIN_ADJOINED, null, null , null , null);
        Log.v(LOG_TAG, "In Ingredients " + cursor2.getCount());
        Cursor cursor3 = mContext.getContentResolver().query(BakingContract.RecipeStep.CONTENT_URI_WITH_MAIN_ADJOINED, null, null, null, null);
        Log.v(LOG_TAG, "In Step "+ cursor3.getCount());
    }
}
