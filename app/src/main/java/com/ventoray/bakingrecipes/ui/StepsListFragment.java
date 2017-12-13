package com.ventoray.bakingrecipes.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.data.Step;
import com.ventoray.bakingrecipes.data.StepAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ventoray.bakingrecipes.ui.StepsActivity.KEY_INGREDIENTS;
import static com.ventoray.bakingrecipes.ui.StepsActivity.KEY_STEPS;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepsListFragment extends Fragment {

    @BindView(R.id.button_ingredients) Button ingredientsButton;
    @BindView(R.id.recycler_steps) RecyclerView recipesRecyclerView;
    private StepAdapter adapter;
    private List<Step> steps;
    private String ingredientSummary;

    public StepsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_steps_list, container, false);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null) {
            steps = args.getParcelableArrayList(KEY_STEPS);
            ingredientSummary = args.getString(KEY_INGREDIENTS);
            if (savedInstanceState == null)
            setUpRecipesRecyclerView();
        }

        return view;
    }

    private void setUpRecipesRecyclerView() {

        if (steps != null) {
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            adapter = new StepAdapter(getContext(), steps);
            recipesRecyclerView.setNestedScrollingEnabled(false);
            recipesRecyclerView.setLayoutManager(llm);
            recipesRecyclerView.setAdapter(adapter);

        } else {
            Log.d("Fragmentation Mon", "Null recipes list");
        }

    }

    @OnClick(R.id.button_ingredients)
    public void showIngredients() {
        if (ingredientSummary == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.ingredients);
        builder.setMessage(ingredientSummary);
        builder.create().show();
    }

}
