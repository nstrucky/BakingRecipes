package com.ventoray.bakingrecipes.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.data.Recipe;
import com.ventoray.bakingrecipes.data.RecipeAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeListFragment extends Fragment {

    RecyclerView recipesRecyclerView;
    RecipeAdapter adapter;


    public RecipeListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        recipesRecyclerView = view.findViewById(R.id.recycler_recipes);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        setUpRecipesRecyclerView();
    }
/*
    private void setUpRecipesRecyclerView() {

        if (recipes != null) {
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            adapter = new RecipeAdapter(getContext(), recipes);
            recipesRecyclerView.setNestedScrollingEnabled(false);
            recipesRecyclerView.setLayoutManager(llm);
            recipesRecyclerView.setAdapter(adapter);

            Log.d("Fragmentation Mon", "List count: "+ recipes.size());
        } else {
            Log.d("Fragmentation Mon", "Null recipes list");
        }

    }*/

}
