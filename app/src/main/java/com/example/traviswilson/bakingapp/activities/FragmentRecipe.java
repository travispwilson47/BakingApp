package com.example.traviswilson.bakingapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.traviswilson.bakingapp.R;

/**
 * Created by traviswilson on 7/26/17.
 * Class contains a recyclerView that is populated by the titles and possibly Images from the
 * stored information.
 */

public class FragmentRecipe extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

}
