package com.example.traviswilson.bakingapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.traviswilson.bakingapp.data.BakingContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Vector;

import static junit.framework.Assert.assertTrue;

/**
 * Created by traviswilson on 7/27/17.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class TestDB {
    private static final String LOG_TAG = "TestDB";
    private static final int NUM_STEPS = 5;
    Context mContext;
    @Before
    public void setUpDB(){
        mContext = InstrumentationRegistry.getTargetContext();
    }
    @Test
    public void testInsertAndDeleteMain() {
        //Test insert
        mContext.getContentResolver().insert(BakingContract.RecipeMain.CONTENT_URI, TestUtils.createContentValuesForMain());
        Cursor cursor = mContext.getContentResolver().query(BakingContract.RecipeMain.CONTENT_URI, null, null, null, null);
        assertTrue(cursor != null);
        cursor.close();
        //Test delete
        int rows = mContext.getContentResolver().delete(BakingContract.RecipeMain.CONTENT_URI, null, null);
        Cursor cursor2 = mContext.getContentResolver().query(BakingContract.RecipeMain.CONTENT_URI, null, null, null, null);
        assertTrue(cursor2 == null || cursor2.getCount() == 0);
        assertTrue(rows == 1);
    }
    @Test
    public void testStepAndIngredientsTable(){


        //Test StepTable and Ingredients table

        Uri mUri = mContext.getContentResolver().insert(BakingContract.RecipeMain.CONTENT_URI, TestUtils.createContentValuesForMain());
        long id = ContentUris.parseId(mUri);


        //Test StepTable:

        Vector<ContentValues> values = new Vector<>();
        for (int i = 0 ; i< NUM_STEPS; i++){
            values.add(TestUtils.createRandomContentValuesForSteps(id, i));
        }
        for (int i = 0 ; i < NUM_STEPS ; i++) {
            mContext.getContentResolver().insert(BakingContract.RecipeStep.CONTENT_URI, TestUtils.createRandomContentValuesForSteps(id, i));
        }
        mContext.getContentResolver().bulkInsert(BakingContract.RecipeStep.CONTENT_URI,
                 values.toArray(new ContentValues[values.size()]));
        Cursor cursor3 = mContext.getContentResolver()
                .query(BakingContract.RecipeStep.CONTENT_URI_WITH_MAIN_ADJOINED, null , null , null, null);
        Log.v(LOG_TAG,cursor3.getCount()+"");
        assertTrue(cursor3 != null && cursor3.getCount() == 10);
        mContext.getContentResolver().delete(BakingContract.RecipeStep.CONTENT_URI, null, null);
        Cursor cursor5 = mContext.getContentResolver()
                .query(BakingContract.RecipeStep.CONTENT_URI_WITH_MAIN_ADJOINED, null , null , null, null);
        assertTrue(cursor5 == null || cursor5.getCount() == 0);

        //Test IngredientsTable:

        Vector<ContentValues> values2 = new Vector<>();
        for (int i = 0 ; i< NUM_STEPS; i++){
            values2.add(TestUtils.createRandomContentValuesForIngredients(id, i));
        }
        for (int i = 0 ; i < NUM_STEPS ; i++) {
            mContext.getContentResolver().insert(BakingContract.RecipeIngredients.CONTENT_URI, TestUtils.createRandomContentValuesForIngredients(id, i));
        }
        mContext.getContentResolver().bulkInsert(BakingContract.RecipeIngredients.CONTENT_URI,
                values2.toArray(new ContentValues[values2.size()]));
        Cursor cursor4 = mContext.getContentResolver()
                .query(BakingContract.RecipeIngredients.CONTENT_URI_WITH_MAIN_ADJOINED, null , null , null, null);
        Log.v(LOG_TAG,cursor4.getCount()+" = Ingredients");
        assertTrue(cursor4 != null && cursor4.getCount() == 10);
        mContext.getContentResolver().delete(BakingContract.RecipeIngredients.CONTENT_URI, null, null);
        Cursor cursor6 = mContext.getContentResolver()
                .query(BakingContract.RecipeIngredients.CONTENT_URI_WITH_MAIN_ADJOINED, null , null , null, null);
        assertTrue(cursor5 == null || cursor5.getCount() == 0);

        mContext.getContentResolver().delete(BakingContract.RecipeMain.CONTENT_URI, null, null);
    }
}
