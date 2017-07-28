package com.example.traviswilson.bakingapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by traviswilson on 7/26/17.
 */

public class BakingContract {
    public static final String CONTENT_AUTHORITY = "com.example.traviswilson.bakingapp.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    public static final String PATH_RECIPE_MAIN = "recipe_main";
    public static final String PATH_RECIPE_STEP= "recipe_step";
    public static final String PATH_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static class RecipeMain implements BaseColumns{
        public static final String TABLE_NAME = "main";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE_MAIN).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_MAIN;
        //Columns
        public static final String RECIPE_ID = "recipe_id";
        public static final String NAME = "name";
        public static final String SERVINGS = "servings";
        public static final String IMAGE = "image"; //Never has this quality *actually* been passed through the JSON

        public static Uri getUriFromID(long ID){
            return ContentUris.withAppendedId(CONTENT_URI, ID);
        }

    }
    public static class RecipeStep implements BaseColumns{
        public static final String TABLE_NAME = "step";
        static final String MAIN_ADJOINED = "StepwithMain";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE_STEP).build();
        public static final Uri CONTENT_URI_WITH_MAIN_ADJOINED = BASE_CONTENT_URI.buildUpon()
                .appendPath(MAIN_ADJOINED).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_STEP;
        //Columns
        public static final String STEP_ID = "step_id";
        public static final String SHORT_DESCRIPTION = "short_description";
        public static final String VIDEO_URL = "video_url";
        public static final String THUMB_NAIL_URL = "thumb_nail_url";
        public static final String MAIN_KEY = "main_key";
        public static Uri getUriFromID(long ID){
            return ContentUris.withAppendedId(CONTENT_URI, ID);
        }
    }
    public static class RecipeIngredients implements BaseColumns{
        public static final String TABLE_NAME = "ingredients";
        static final String MAIN_ADJOINED = "IngredientswithMain";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE_INGREDIENTS).build();
        public static final Uri CONTENT_URI_WITH_MAIN_ADJOINED = BASE_CONTENT_URI.buildUpon()
                .appendPath(MAIN_ADJOINED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE_INGREDIENTS;
        //Columns
        public static final String QUANTITY = "quantity";
        public static final String MEASURE = "measure";
        public static final String INGREDIENT = "ingredient";
        public static final String MAIN_KEY = "main_key";
        public static Uri getUriFromID(long ID){
            return ContentUris.withAppendedId(CONTENT_URI, ID);
        }

    }
}
