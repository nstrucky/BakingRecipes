package com.ventoray.bakingrecipes.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.data.Recipe;
import com.ventoray.bakingrecipes.data.RecipeAdapter;
import com.ventoray.bakingrecipes.util.RecipeRetriever;
import com.ventoray.bakingrecipes.util.ScreenUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private List<Recipe> recipes;
    private RecipeAdapter recipeAdapter;
    @BindView(R.id.recycler_recipes) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        retrieveRecipes();
        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        recipeAdapter = new RecipeAdapter(this, recipes);
        RecyclerView.LayoutManager layoutManager;
        if (ScreenUtils.isTablet(this)) {
            layoutManager = new GridLayoutManager(this, 3,
                    LinearLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recipeAdapter);
    }

    private void retrieveRecipes() {
        InputStream inputStream = getResources().openRawResource(R.raw.recipes);

        try {
            recipes = RecipeRetriever.readJsonStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
