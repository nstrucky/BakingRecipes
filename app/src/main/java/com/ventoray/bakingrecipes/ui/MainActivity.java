package com.ventoray.bakingrecipes.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.view.View;
import android.widget.Toast;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.data.Recipe;
import com.ventoray.bakingrecipes.data.RecipeAdapter;
import com.ventoray.bakingrecipes.util.FileUtils;
import com.ventoray.bakingrecipes.util.RecipeRetriever;
import com.ventoray.bakingrecipes.util.ScreenUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeCardClicked {

    private List<Recipe> recipes;
    private RecipeAdapter recipeAdapter;
    public static final String KEY_PARCEL_RECIPE = "keyParcelRecipe";


    @BindView(R.id.recycler_steps)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        retrieveRecipes();
        setUpRecyclerView();

    }

    @Override
    public void onRecipeCardClicked(View itemView) {
        Intent intent = new Intent(this, StepsActivity.class);
        long id = (long) itemView.getTag();
        for (Recipe recipe : recipes) {
            long tempId = recipe.getId();
            if (tempId == id) {
                intent.putExtra(KEY_PARCEL_RECIPE, recipe);
                break;
            }
        }
        startActivity(intent);
    }

    private void setUpRecyclerView() {
        recipeAdapter = new RecipeAdapter(this, recipes, this);
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

        saveIngredientsFile();

    }

    /**
     * this will be used for now...will change with increased functionality
     */
    private void saveIngredientsFile() {
        if (recipes == null || recipes.size() == 0) return;

        String[] ingredients = new String[recipes.size()];
        for (int i = 0; i < recipes.size(); i++) {
            ingredients[i] = recipes.get(i).getIngredientSummary();
        }

        FileUtils.createIngredientsFile(this, ingredients);
    }

}
