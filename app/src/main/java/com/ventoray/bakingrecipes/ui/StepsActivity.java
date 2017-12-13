package com.ventoray.bakingrecipes.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.data.Recipe;
import com.ventoray.bakingrecipes.data.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ventoray.bakingrecipes.ui.MainActivity.KEY_PARCEL_RECIPE;

public class StepsActivity extends AppCompatActivity {

    public static final String KEY_STEPS = "keySteps";
    public static final String KEY_INGREDIENTS = "keyIngredients";

    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            recipe = (Recipe) intent.getParcelableExtra(KEY_PARCEL_RECIPE);
        }

        Fragment stepsFragment = new StepsListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_STEPS, (ArrayList<? extends Parcelable>) recipe.getSteps());
        args.putString(KEY_INGREDIENTS, recipe.getIngredientSummary());
        stepsFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_steps, stepsFragment)
                .commit();

    }
}
