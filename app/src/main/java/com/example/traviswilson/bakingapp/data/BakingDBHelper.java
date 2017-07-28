package com.example.traviswilson.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.traviswilson.bakingapp.R;

import static com.example.traviswilson.bakingapp.data.BakingContract.*;

/**
 * Created by traviswilson on 7/26/17.
 */

public class BakingDBHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "baking.db";
    private static final int DATABASE_VERSION = 5;
    public BakingDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MAIN_TABLE = "CREATE TABLE " + RecipeMain.TABLE_NAME + " (" +
                RecipeMain._ID + " INTEGER PRIMARY KEY, " +
                RecipeMain.IMAGE + " TEXT, "+
                RecipeMain.NAME + " TEXT NOT NULL, " +
                RecipeMain.RECIPE_ID + " INTEGER NOT NULL, " +
                RecipeMain.SERVINGS + " INTEGER );" ;


        final String SQL_CREATE_STEPS_TABLE = "CREATE TABLE " + RecipeStep.TABLE_NAME +" ("+
                RecipeStep._ID + " INTEGER PRIMARY KEY, " + //None of these columns are "NOT NULL"
                RecipeStep.SHORT_DESCRIPTION + " TEXT, " + // Because the JSON might not be perfect and we
                RecipeStep.STEP_ID + " INTEGER, " + //May still need to store the information
                RecipeStep.THUMB_NAIL_URL + " TEXT, " +
                RecipeStep.VIDEO_URL + " TEXT, " +
                RecipeStep.MAIN_KEY  + " INTEGER NOT NULL, "

                + "FOREIGN KEY (" + RecipeStep.MAIN_KEY + ") REFERENCES "+
                RecipeMain.TABLE_NAME + " (" + RecipeMain._ID + "));";
        final String SQL_CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + RecipeIngredients.TABLE_NAME
                + " (" +
                RecipeIngredients._ID + " INTEGER PRIMARY KEY, " +
                RecipeIngredients.INGREDIENT + " TEXT NOT NULL, " +
                RecipeIngredients.MEASURE + " TEXT NOT NULL, " +
                RecipeIngredients.QUANTITY + " REAL NOT NULL, " +
                RecipeIngredients.MAIN_KEY + " INTEGER NOT NULL, "

                + "FOREIGN KEY (" + RecipeIngredients.MAIN_KEY + ") REFERENCES "+
                RecipeMain.TABLE_NAME + " (" + RecipeMain._ID + "));";
        sqLiteDatabase.execSQL(SQL_CREATE_MAIN_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STEPS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO: Get rid of this when done testing
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeMain.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeStep.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeIngredients.TABLE_NAME);
        onCreate(sqLiteDatabase);
        //Run in Alter Statements if db is upgraded, since dropping the table regardless
        //is not a good idea.
    }
}
