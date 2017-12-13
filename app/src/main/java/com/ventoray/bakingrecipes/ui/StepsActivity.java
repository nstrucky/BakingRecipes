package com.ventoray.bakingrecipes.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.data.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ventoray.bakingrecipes.ui.MainActivity.KEY_PARCEL_RECIPE;

public class StepsActivity extends AppCompatActivity {

    @BindView(R.id.tv_test_steps) TextView testTextView;

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

        if (recipe != null) {
            testTextView.setText(recipe.getName());
        } else {
            Toast.makeText(this, "Nope", Toast.LENGTH_SHORT).show();
        }

    }
}
