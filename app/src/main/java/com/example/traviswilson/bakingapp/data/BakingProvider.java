package com.example.traviswilson.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.os.Build.ID;

/**
 * Created by traviswilson on 7/26/17.
 */

public class BakingProvider extends ContentProvider {
    //TODO: ContentResolver.notifyChange

    private final String LOG_TAG = getClass().getSimpleName();
    private BakingDBHelper mOpenHelper;
    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private static final int MAIN = 100;
    private static final int MAIN_WITH_STEPS = 102;
    private static final int MAIN_WITH_INGREDIENTS = 103;
    private static final int STEP = 104;
    private static final int INGREDIENTS = 105;
    private static SQLiteQueryBuilder mMainWithStepsBuilder;
    private static SQLiteQueryBuilder mMainWithIngredientsBuilder;

    static{
        mMainWithIngredientsBuilder = new SQLiteQueryBuilder();
        mMainWithIngredientsBuilder.setTables(
                BakingContract.RecipeIngredients.TABLE_NAME+ " INNER JOIN "
                + BakingContract.RecipeMain.TABLE_NAME +" ON " + BakingContract.RecipeMain.TABLE_NAME
                +"."+ BakingContract.RecipeMain._ID +" = "+BakingContract.RecipeIngredients.TABLE_NAME
                + "."+BakingContract.RecipeIngredients.MAIN_KEY
        );
        mMainWithStepsBuilder = new SQLiteQueryBuilder();
        mMainWithStepsBuilder.setTables(
                BakingContract.RecipeStep.TABLE_NAME+ " INNER JOIN "
                        + BakingContract.RecipeMain.TABLE_NAME +" ON " + BakingContract.RecipeMain.TABLE_NAME
                        +"."+ BakingContract.RecipeMain._ID +" = "+BakingContract.RecipeStep.TABLE_NAME
                        + "."+BakingContract.RecipeStep.MAIN_KEY
        );

    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BakingContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, BakingContract.PATH_RECIPE_MAIN, MAIN); //Fragment Recipe
        matcher.addURI(authority,
                 BakingContract.RecipeStep.MAIN_ADJOINED,
                 MAIN_WITH_STEPS);
        matcher.addURI(authority,
                BakingContract.RecipeIngredients.MAIN_ADJOINED,
                 MAIN_WITH_INGREDIENTS);

        Log.v("Baking Provider" ,
                authority + "/" +  BakingContract.RecipeStep.MAIN_ADJOINED);
        Log.v("Baking Provider",
                BakingContract.RecipeStep.CONTENT_URI_WITH_MAIN_ADJOINED.toString());

        //Note that both ingredients and steps must be accessed with main on inner join clause
        //defined below, since the search is done based on the title, and the tables are linked
        //via foreign key relationship

        //Now add the URIs only used in inserting
        matcher.addURI(authority, BakingContract.PATH_RECIPE_STEP, STEP );
        matcher.addURI(authority, BakingContract.PATH_RECIPE_INGREDIENTS, INGREDIENTS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new BakingDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.v(LOG_TAG,""+uri.toString());
        switch (mUriMatcher.match(uri)){
            case MAIN:
                return mOpenHelper.getReadableDatabase().query(
                        BakingContract.RecipeMain.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            case MAIN_WITH_INGREDIENTS:
                return mMainWithIngredientsBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            case MAIN_WITH_STEPS:
                return mMainWithStepsBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    /**
     * Functionality works as following: first one must insert mainTable information, then this method
     * returns the uri of the row containing the ID of each mainItem, which is returned and used for
     * inserting the foreign key in the table DB for STEP and INGREDIENTS tables.
     */
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.v(LOG_TAG, "insert:"+mUriMatcher.match(uri));
        switch (mUriMatcher.match(uri)){
            case MAIN:
                return BakingContract.RecipeMain.getUriFromID(mOpenHelper.getWritableDatabase().insert(
                        BakingContract.RecipeMain.TABLE_NAME,
                        null,
                        contentValues
                )
                );
            case STEP:
                mOpenHelper.getWritableDatabase().insert(
                        BakingContract.RecipeStep.TABLE_NAME,
                        null,
                        contentValues
                );
                return null;
            case INGREDIENTS:
                mOpenHelper.getWritableDatabase().insert(
                        BakingContract.RecipeIngredients.TABLE_NAME,
                        null,
                        contentValues
                );
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)){
            case MAIN:
                return db.delete(BakingContract.RecipeMain.TABLE_NAME, s, strings);
            case STEP:
                return db.delete(BakingContract.RecipeStep.TABLE_NAME, s, strings);
            case INGREDIENTS:
                return db.delete(BakingContract.RecipeIngredients.TABLE_NAME, s ,strings);
            default:
                throw new UnsupportedOperationException();
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)){
            case MAIN:
                return db.update(BakingContract.RecipeMain.TABLE_NAME, contentValues, s, strings);
            case STEP:
                return db.update(BakingContract.RecipeStep.TABLE_NAME, contentValues,  s, strings);
            case INGREDIENTS:
                return db.update(BakingContract.RecipeIngredients.TABLE_NAME, contentValues,  s ,strings);
            default:
                throw new UnsupportedOperationException();
        }
    }
}
