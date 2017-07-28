package com.example.traviswilson.bakingapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DownloadManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.data.BakingContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.id;
import static android.util.Log.v;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by traviswilson on 7/27/17.
 */

public class BakingSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final int SYNC_INTERVAL = 1000 * 60 * 60 * 3; // = 3 hours
    private static final int SYNC_FLEX_TIME = SYNC_INTERVAL / 3;
    private static final String LOG_TAG = "BakingSyncAdapter" ;

    //public static final String ID_INDEX =

    public BakingSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        final String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
        //The project url that the baking recipes come from.
        Log.v(LOG_TAG, "Begining Sync");

        OkHttpClient client = new OkHttpClient();
        try {
            Log.v(LOG_TAG, "Made it to before execute");
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            Log.v(LOG_TAG, "Made it to after execute");
            getInfoFromJSON(response.body().string());
        } catch (IOException e){
            e.printStackTrace();
            Log.v(LOG_TAG, "Error with http!");
        }
    }

    private void getInfoFromJSON(String string) {
        try {
            if (string == null || string.equals("")) {
                Toast.makeText(getContext(), "No output from server", Toast.LENGTH_SHORT);
                Log.v(LOG_TAG, "Nothing from our server :(");
                return;
            }
            Log.v(LOG_TAG, "Made it to parsing");
            Vector<ContentValues> stepContentValues = new Vector<>();
            Vector<ContentValues> ingredContentValues = new Vector<>();
            JSONArray starterArray = new JSONArray(string);
            for (int i = 0; i < starterArray.length(); i++){
                JSONObject recipe = starterArray.getJSONObject(i);
                long id_row = insertMain(recipe);
                if (id_row < 0) {
                    Toast.makeText(getContext(), "Error parsing recipe number "+i
                            + "Error code: "+id_row, Toast.LENGTH_SHORT)
                            .show();
                    continue;
                }
                JSONArray steps = recipe.getJSONArray("steps");
                for (int ii = 0; ii < steps.length() ; ii++){
                    ContentValues contentValuesForStep = new ContentValues();
                    contentValuesForStep.put(BakingContract.RecipeStep.MAIN_KEY, id_row);
                    JSONObject step = steps.getJSONObject(ii);
                    contentValuesForStep.put(BakingContract.RecipeStep.STEP_ID, step.getInt("id"));
                    contentValuesForStep.put(BakingContract.RecipeStep.SHORT_DESCRIPTION, step.getString("shortDescription"));
                    contentValuesForStep.put(BakingContract.RecipeStep.VIDEO_URL,step.getString("videoURL"));
                    contentValuesForStep.put(BakingContract.RecipeStep.DESCRIPTION, step.getString("description"));
                    contentValuesForStep.put(BakingContract.RecipeStep.THUMB_NAIL_URL,step.getString("thumbnailURL"));
                    stepContentValues.add(contentValuesForStep);
                }
                JSONArray ingredients = recipe.getJSONArray("ingredients");
                for (int ii= 0 ; ii < ingredients.length(); ii++ ){
                    ContentValues contentValuesForIngred = new ContentValues();
                    contentValuesForIngred.put(BakingContract.RecipeIngredients.MAIN_KEY, id_row);
                    JSONObject ingredient = ingredients.getJSONObject(i);
                    contentValuesForIngred.put(BakingContract.RecipeIngredients.QUANTITY, ingredient.getInt("quantity"));
                    contentValuesForIngred.put(BakingContract.RecipeIngredients.MEASURE, ingredient.getString("measure"));
                    contentValuesForIngred.put(BakingContract.RecipeIngredients.INGREDIENT, ingredient.getString("ingredient"));
                    ingredContentValues.add(contentValuesForIngred);
                }
            }
            //One large bulk insert of all ingredients and steps across all recipes
            getContext().getContentResolver().bulkInsert(BakingContract.RecipeStep.CONTENT_URI,
                    stepContentValues.toArray(new ContentValues[stepContentValues.size()]));
            getContext().getContentResolver().bulkInsert(BakingContract.RecipeIngredients.CONTENT_URI,
                    ingredContentValues.toArray(new ContentValues[ingredContentValues.size()]));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Error code : -1 returned.
     * @param recipe
     * @return
     */
    private long insertMain(JSONObject recipe){
        //check for JSON errors. These Error codes are based on the given output, and since this,
        //according to the project spec, might change, we check for JSON errors here.
        if (!recipe.has("id")) return -1L;
        if (!recipe.has("name")) return  -2L;
        if (!recipe.has("steps")) return -3L;
        if (!recipe.has("servings")) return -4L;
        if (!recipe.has("image")) return -5L;
        if (!recipe.has("ingredients") )return -6L;
        if (!recipe.has("servings")) return -7L;
        //test ingredients array for erroneous JSON
        try {
            JSONArray ingredients = recipe.getJSONArray("ingredients");
            for (int i = 0; i < ingredients.length() ; i++){
                JSONObject ingredient = ingredients.getJSONObject(i);
                ingredient.getInt("quantity");
                ingredient.getString("measure");
                ingredient.getString("ingredient");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return -8L;
        }
        try {
            JSONArray steps = recipe.getJSONArray("steps");
            for (int i = 0; i < steps.length() ; i++){
                JSONObject step = steps.getJSONObject(i);
                step.getInt("id");
                step.getString("shortDescription");
                step.getString("description");
                step.getString("videoURL");
                step.getString("thumbnailURL");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return -9L;
        }
        //OK, no errors in this recipe, we can now insert into the DB.

        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(BakingContract.RecipeMain.RECIPE_ID, recipe.getInt("id"));
            contentValues.put(BakingContract.RecipeMain.SERVINGS, recipe.getInt("servings"));
            contentValues.put(BakingContract.RecipeMain.IMAGE, recipe.getString("image"));
            contentValues.put(BakingContract.RecipeMain.NAME, recipe.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
            return -10L;
        }
         return ContentUris.parseId(getContext().getContentResolver()
                 .insert(BakingContract.RecipeMain.CONTENT_URI, contentValues));
    }

    private static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        //Create the account type and default account

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));


        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            v("Made it to here", "Important 2");
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                v("Error in adding account", "");
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to set the syncing with the account once created
     * @param newAccount account to use to set periodic sync
     * @param context
     */
    private static void onAccountCreated(Account newAccount, Context context) {
        //when we create the account, configure the syncadapter period
        BakingSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEX_TIME);
        //Then we set the cr to sync automatically to enable the above code
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        //This is a method called on the first time account has been created so we sync immediately
        syncImmediately(context);

    }

    /**
     * Method to fire the sync adapter now. Fires the very first time the account has been created
     * so we turn the shared preferences on now.
     * @param context
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}

