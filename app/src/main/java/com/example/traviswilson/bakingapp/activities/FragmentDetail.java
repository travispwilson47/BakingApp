package com.example.traviswilson.bakingapp.activities;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.traviswilson.bakingapp.R;
import com.example.traviswilson.bakingapp.adapters.MyRecyclerAdapter;
import com.example.traviswilson.bakingapp.adapters.RecipeAdapter;
import com.example.traviswilson.bakingapp.adapters.RecipeDetailIngredientAdapter;
import com.example.traviswilson.bakingapp.adapters.RecipeDetailStepAdapter;
import com.example.traviswilson.bakingapp.data.BakingContract;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by traviswilson on 10/30/17.
 * Contains a constraint layout that displays a card holding ingredients and card holding steps,
 * the steps must be able to be clicked on so that they can go to a detail view, also this clicking
 * should have the relevant animation for 'digging' into information
 */

public class FragmentDetail extends Fragment implements RecipeDetailStepAdapter.Callback
,RecipeDetailIngredientAdapter.Callback  {
    private static final String LOG_TAG = FragmentDetail.class.toString();
    //TODO: make onClickListener for this class to send the name to the StepDetailsFragment

    Callback mListener;
    private RecyclerView recyclerViewIngredient;
    private RecyclerView recyclerViewStep;

    public static final int DETAIL_INGREDIENT_LOADER = 0;
    public static final int DETAIL_STEP_LOADER = 1;

    private static String recipeName;
    private static String[] recipeIngredientProjection =
            {BakingContract.RecipeMain.TABLE_NAME+ "." + BakingContract.RecipeMain._ID,
                    BakingContract.RecipeIngredients.QUANTITY, BakingContract.RecipeIngredients.MEASURE};
    private static String[] recipeStepProjection =
            {BakingContract.RecipeMain.TABLE_NAME+ "." + BakingContract.RecipeMain._ID,
                    BakingContract.RecipeStep.SHORT_DESCRIPTION, BakingContract.RecipeStep.STEP_ID};

    public void onFinishIngredientDataLoad(Cursor cursor) {
        recyclerViewIngredient.swapAdapter(
                new MyRecyclerAdapter(getActivity(), cursor, MyRecyclerAdapter.AdapterType.DETAIL_INGREDIENT, this),
                true);
    }

    public void onFinishStepDataLoad(Cursor cursor) {
        recyclerViewStep.swapAdapter(
                new MyRecyclerAdapter(getActivity(), cursor, MyRecyclerAdapter.AdapterType.DETAIL_STEP, this),
                true);
    }

    public void onIngredientDataReset() {
        recyclerViewIngredient.swapAdapter(null, true);
    }

    public void onStepsDataReset() {
        recyclerViewStep.swapAdapter(null, true);
    }

    public interface Callback{
        public void onStepClicked(String recipeStepNumber, String stepDescription,
                                  String videoURL);
        public String requestRecipeTitle();
    }
    //Enforce the callback on the Activity
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            mListener = (Callback) getActivity();
        } catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() +"Must implement FragmentDetail" +
                    ".CallBack ");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        recipeName = mListener.requestRecipeTitle();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        recyclerViewIngredient = rootView.findViewById(R.id.ingredientRecyclerView);
        recyclerViewIngredient.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewStep = rootView.findViewById(R.id.stepRecyclerView);
        recyclerViewStep.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onStepClicked(String recipeStepNumber,
                              String stepDescription, String videoURL) {
        mListener.onStepClicked(recipeStepNumber, stepDescription, videoURL);
    }
}