package com.ventoray.bakingrecipes.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.model.Recipe;
import com.ventoray.bakingrecipes.model.Step;
import com.ventoray.bakingrecipes.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ventoray.bakingrecipes.ui.MainActivity.KEY_PARCEL_RECIPE;
import static com.ventoray.bakingrecipes.ui.StepFragment.NEXT_BUTTON;

public class StepsActivity extends AppCompatActivity
        implements StepsListFragment.OnStepSelectedListener,
        StepFragment.StepsNavigationListener {

    public static final String KEY_STEPS_LIST = "keyStepsList";
    public static final String KEY_INGREDIENTS = "keyIngredients";
    public static final String KEY_STEP_PARCEL = "keyStepParcel";
    public static final String KEY_STEP_ID = "keyStepId";
    public static final String KEY_LIST_STATE = "keyListState";

    private Recipe recipe;
    private List<Step> steps;
    private boolean isTablet;
    private boolean listActivityState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        ButterKnife.bind(this);
        setUpActionBar();
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_LIST_STATE)) {
            listActivityState = savedInstanceState.getBoolean(KEY_LIST_STATE);
        }

        isTablet = ScreenUtils.isTablet(this);

        Intent intent = getIntent();
        if (intent != null) {
            recipe = (Recipe) intent.getParcelableExtra(KEY_PARCEL_RECIPE);
            steps = recipe.getSteps();

        }

        if (isTablet) {
            findViewById(R.id.container_step).setVisibility(View.VISIBLE);
            if (savedInstanceState == null) {
                replaceStepFragment(steps.get(0).getId());
                addStepsListFragment();
            }

        } else if (listActivityState){
            if (savedInstanceState == null)
            addStepsListFragment();
        }

        setTitle(recipe.getName());
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_LIST_STATE, listActivityState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStepSelected(long id) {
            replaceStepFragment(id);
    }

    @Override
    public void onNavigationButtonClicked(String navigationButton, long id) {
        int stepsListSize = steps.size();
        int position = 0;

        if (navigationButton.equals(NEXT_BUTTON)) {
            for (int i = 0; i < stepsListSize; i++) {
                Step step = steps.get(i);
                if (step.getId() == id) position = i;
            }
            if (position == stepsListSize - 1) return;
            id = steps.get(position + 1).getId();
            replaceStepFragment(id);

        } else {
            for (int i = 0; i < stepsListSize; i++) {
                Step step = steps.get(i);
                if (step.getId() == id) position = i;
            }
            if (position == 0) return;
            id = steps.get(position - 1).getId();
            replaceStepFragment(id);
        }
    }



    private void addStepsListFragment() {
        Fragment stepsFragment = new StepsListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_STEPS_LIST,
                (ArrayList<? extends Parcelable>) recipe.getSteps());
        args.putString(KEY_INGREDIENTS, recipe.getIngredientSummary());
        stepsFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_steps, stepsFragment)
                .commit();
    }

    /**
     * Replaces the StepsListFragment in container_steps FrameLayout in activity_steps with a
     *  new stepFragment
     * @param id - the id associated with a given step retrieved from view tag in Steps Recycler
     */
    private void replaceStepFragment(long id) {
        int position = 0;
        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            if (step.getId() == id) position = i;
        }
        Bundle args = new Bundle();
        args.putParcelable(KEY_STEP_PARCEL, steps.get(position));
        args.putLong(KEY_STEP_ID, id);
        Fragment stepFragment = new StepFragment();
        stepFragment.setArguments(args);

        if (!isTablet) listActivityState = false;
        int container = isTablet ? R.id.container_step : R.id.container_steps;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(container, stepFragment)
                .commit();
    }

}
