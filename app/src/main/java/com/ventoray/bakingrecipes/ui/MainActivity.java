package com.ventoray.bakingrecipes.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.testing.RecipeIdlingResource;
import com.ventoray.bakingrecipes.model.Recipe;
import com.ventoray.bakingrecipes.model.RecipeAdapter;
import com.ventoray.bakingrecipes.util.FileUtils;
import com.ventoray.bakingrecipes.util.RecipeRetriever;
import com.ventoray.bakingrecipes.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeCardClicked,
        RecipeRetriever.RecipeAsyncTask.RecipesRetrievedListener {

    private List<Recipe> recipes;
    private RecipeAdapter recipeAdapter;
    @Nullable
    private RecipeIdlingResource idlingResource;
    public static final String KEY_PARCEL_RECIPE = "keyParcelRecipe";
    public static final String KEY_SAVEDINSTANCE_RECIPE_LIST = "savedInstanceRecipeList";


    @BindView(R.id.recycler_recipes)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getIdlingResource();
        ButterKnife.bind(this);

        setUpRecyclerView();
        retrieveData(savedInstanceState);
    }

    private void retrieveData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            retrieveRecipesFromWeb();
        } else if (savedInstanceState.containsKey(KEY_SAVEDINSTANCE_RECIPE_LIST)){
            ArrayList<Recipe> recipesSaved
                    = savedInstanceState.getParcelableArrayList(KEY_SAVEDINSTANCE_RECIPE_LIST);
            if (recipesSaved == null || recipes == null) {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Error retrieving data for recipes.");
                return;
            }
            recipes.clear();
            recipes.addAll(recipesSaved);
            recipeAdapter.notifyDataSetChanged();
            saveIngredientsFile();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (recipes != null) {
            outState.putParcelableArrayList(KEY_SAVEDINSTANCE_RECIPE_LIST, (ArrayList) recipes);
        }

        super.onSaveInstanceState(outState);
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
        recipes = new ArrayList<>();
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

    private void retrieveRecipesFromWeb() {
        new RecipeRetriever.RecipeAsyncTask(this, idlingResource).execute();

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


    @Override
    public void onRecipesRetrieved(List<Recipe> recipes) {
        if (recipes == null) return;
        this.recipes.clear();
        this.recipes.addAll(recipes);
        recipeAdapter.notifyDataSetChanged();
        saveIngredientsFile();
    }


    @NonNull
    @VisibleForTesting
    public RecipeIdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new RecipeIdlingResource();
        }
        return idlingResource;
    }

}
