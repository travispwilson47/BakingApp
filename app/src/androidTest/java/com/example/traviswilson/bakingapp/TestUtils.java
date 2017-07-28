package com.example.traviswilson.bakingapp;

import android.content.ContentValues;

import com.example.traviswilson.bakingapp.data.BakingContract;

import java.util.Random;



/**
 * Created by traviswilson on 7/27/17.
 */

public class TestUtils {
    private static final int SHORT_DESC_LENGTH = 15;
    private static final int ASCII_MAX_VALUE = 127;

    static ContentValues createContentValuesForMain(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(BakingContract.RecipeMain.RECIPE_ID, 1);
        contentValues.put(BakingContract.RecipeMain.IMAGE, "");
        contentValues.put(BakingContract.RecipeMain.NAME, "Pudding");
        contentValues.put(BakingContract.RecipeMain.SERVINGS, 5);
        return contentValues;
    }
    static ContentValues createRandomContentValuesForSteps(long id, int stepNum){
        ContentValues contentValues = new ContentValues();
        contentValues.put(BakingContract.RecipeStep.SHORT_DESCRIPTION, generateRandomDescription());
        contentValues.put(BakingContract.RecipeStep.STEP_ID, "Step "+stepNum);
        contentValues.put(BakingContract.RecipeStep.THUMB_NAIL_URL, "");
        contentValues.put(BakingContract.RecipeStep.VIDEO_URL, "");
        contentValues.put(BakingContract.RecipeStep.MAIN_KEY, id);
        return contentValues;
    }
    static ContentValues createRandomContentValuesForIngredients(long id, int ingNum){
        ContentValues contentValues = new ContentValues();
        contentValues.put(BakingContract.RecipeIngredients.MAIN_KEY, id);
        contentValues.put(BakingContract.RecipeIngredients.INGREDIENT, generateRandomDescription());
        contentValues.put(BakingContract.RecipeIngredients.QUANTITY, 5);
        contentValues.put(BakingContract.RecipeIngredients.MEASURE, "cups");
        return contentValues;
    }
    private static String generateRandomDescription(){
        Random r = new Random(System.currentTimeMillis());
        StringBuilder builder = new StringBuilder();
        for (int i = 0 ; i < SHORT_DESC_LENGTH; i++ ){
            builder.append( (char) r.nextInt(ASCII_MAX_VALUE));
        }

        return builder.toString();
    }
}
