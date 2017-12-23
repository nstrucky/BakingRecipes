package com.ventoray.bakingrecipes.util;

import android.os.AsyncTask;
import android.util.JsonReader;

import com.ventoray.bakingrecipes.testing.RecipeIdlingResource;
import com.ventoray.bakingrecipes.model.Ingredient;
import com.ventoray.bakingrecipes.model.Recipe;
import com.ventoray.bakingrecipes.model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ventoray.bakingrecipes.util.WebUtils.getJsonResponse;
import static com.ventoray.bakingrecipes.util.WebUtils.makeHTTPUrlConnection;

/**
 * Created by nicks on 12/10/2017.
 */

public class RecipeRetriever {

    public static final String RECIPE_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    //recipe constants
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String INGREDIENTS = "ingredients";
    public static final String SERVINGS = "servings";
    public static final String STEPS = "steps";
    public static final String IMAGE = "image";

    //ingredient constants
    public static final String QUANTITY = "quantity";
    public static final String MEASURE = "measure";
    public static final String INGREDIENT = "ingredient";

    //steps constants
    public static final String STEP_ID = "id";
    public static final String SHORT_DESCRIPTION = "shortDescription";
    public static final String DESCRIPTION = "description";
    public static final String VIDEO_URL = "videoURL";
    public static final String THUMBNAIL_URL = "thumbnailURL";

    public static List<Recipe> readJsonStream(InputStream inputStream) throws IOException {
        JsonReader jsonReader =
                new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        try {
            return readRecipeArray(jsonReader);
        } finally {
            jsonReader.close();
        }
    }

    private static List<Recipe> readRecipeArray(JsonReader jsonReader) throws IOException {
        List<Recipe> recipeList = new ArrayList<>();

        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            recipeList.add(readRecipe(jsonReader));

        }
        jsonReader.endArray();
        return recipeList;
    }

    /**
     * Reads the object from the main array, constructs & returns a Recipe object that will be
     * added to the  List<Recipe> recipes
     * @param jsonReader
     * @return
     * @throws IOException
     */
    private static Recipe readRecipe(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String name = null;
        String image = null;
        int servings = 0;
        long id = -1;
        List<Ingredient> ingredients = null;
        List<Step> steps = null;

        while (jsonReader.hasNext()) {
            String itemName = jsonReader.nextName();

            if (itemName.equals(NAME)) {
                name = jsonReader.nextString();
            } else if (itemName.equals(SERVINGS)) {
                servings = jsonReader.nextInt();
            } else if (itemName.equals(ID)) {
                id = jsonReader.nextLong();
            } else if (itemName.equals(INGREDIENTS)) {
                ingredients = readIngredients(jsonReader);
            } else if (itemName.equals(STEPS)) {
                steps = readSteps(jsonReader);
            } else if (itemName.equals(IMAGE)) {
                image = jsonReader.nextString();

            } else {
                jsonReader.skipValue();
            }

        }
        jsonReader.endObject();
        return new Recipe(id, name, ingredients, steps, servings, image);
    }

    private static List<Step> readSteps(JsonReader jsonReader) throws IOException {
        List<Step> steps = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            steps.add(readStep(jsonReader));
        }
        jsonReader.endArray();
        return steps;
    }

    private static Step readStep(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        long id = -1;
        String shortDescription = "";
        String description = "";
        String videoUrl = "";
        String thumbnailUrl = "";

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (name.equals(STEP_ID)) {
                id = jsonReader.nextLong();
            } else if (name.equals(SHORT_DESCRIPTION)) {
                shortDescription = jsonReader.nextString();
            } else if (name.equals(DESCRIPTION)) {
                description = jsonReader.nextString();
            } else if (name.equals(VIDEO_URL)) {
                videoUrl = jsonReader.nextString();
            } else if (name.equals(THUMBNAIL_URL)) {
                thumbnailUrl = jsonReader.nextString();
            } else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
        return new Step(id, shortDescription, description, videoUrl, thumbnailUrl);
    }

    private static List<Ingredient> readIngredients(JsonReader jsonReader) throws IOException {
        List<Ingredient> ingredients = new ArrayList<>();
        jsonReader.beginArray();

        while (jsonReader.hasNext()) {
            ingredients.add(readIngredient(jsonReader));
        }

        jsonReader.endArray();
        return ingredients;
    }

    private static Ingredient readIngredient(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        double quantity = 0;
        String measure = "";
        String ingredient = "";

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals(QUANTITY)) {
                quantity = jsonReader.nextDouble();
            } else if (name.equals(MEASURE)) {
                measure = jsonReader.nextString();
            } else if (name.equals(INGREDIENT)) {
                ingredient = jsonReader.nextString();
            } else {
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
        return new Ingredient(quantity, ingredient, measure);
    }


/**************************************************************************************************
 *  The following are the web retrieval tools
 * ************************************************************************************************
 */

    public static List<Recipe> getRecipesFromUrl() throws IOException, JSONException {
        HttpURLConnection connection = makeHTTPUrlConnection(RECIPE_URL);
        if (connection == null) return null;
        String jsonString = getJsonResponse(connection);
        return parseRecipesFromJson(jsonString);
    }

    private static List<Recipe> parseRecipesFromJson(String json) throws JSONException {
        JSONArray results = new JSONArray(json);
        List<Recipe> recipes = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject recipeJson = results.getJSONObject(i);

            long id = recipeJson.getLong(ID);
            String name = recipeJson.getString(NAME);
            //get the ingredients
            JSONArray ingredientsJsonArray = recipeJson.getJSONArray(INGREDIENTS);
            List<Ingredient> ingredients = Arrays.asList(parseIngredients(ingredientsJsonArray));
            //get the steps
            JSONArray stepsJsonArray = recipeJson.getJSONArray(STEPS);
            List<Step> steps = Arrays.asList(parseSteps(stepsJsonArray));

            int servings = recipeJson.getInt(SERVINGS);
            String image = recipeJson.getString(IMAGE);

            recipes.add(new Recipe(id, name, ingredients, steps, servings, image));
        }
        return recipes;
    }


    private static Step[] parseSteps(JSONArray stepsJsonArray) throws JSONException {
        Step[] steps = new Step[stepsJsonArray.length()];

        for (int i = 0; i < steps.length; i++) {
            JSONObject stepJsonObject = stepsJsonArray.getJSONObject(i);

            long id = stepJsonObject.getLong(STEP_ID);
            String shortDescription = stepJsonObject.getString(SHORT_DESCRIPTION);
            String description = stepJsonObject.getString(DESCRIPTION);
            String videoUrl = stepJsonObject.getString(VIDEO_URL);
            String thumbnailUrl = stepJsonObject.getString(THUMBNAIL_URL);

            steps[i] = new Step(id, shortDescription, description, videoUrl, thumbnailUrl);
        }
        return steps;
    }


    private static Ingredient[] parseIngredients(JSONArray ingredientsJsonArray)
            throws JSONException {

        Ingredient[] ingredients = new Ingredient[ingredientsJsonArray.length()];

        for (int i = 0; i < ingredients.length; i++) {
            JSONObject ingredientJsonObject = ingredientsJsonArray.getJSONObject(i);

            double quantity = ingredientJsonObject.getDouble(QUANTITY);
            String ingredient = ingredientJsonObject.getString(INGREDIENT);
            String measure = ingredientJsonObject.getString(MEASURE);

            ingredients[i] = new Ingredient(quantity, ingredient, measure);

        }

        return ingredients;
    }

    public static class RecipeAsyncTask extends AsyncTask<Void, Void, List<Recipe>> {
        private RecipesRetrievedListener listener;
        private RecipeIdlingResource idlingResource;

        public RecipeAsyncTask(RecipesRetrievedListener listener,
                               RecipeIdlingResource idlingResource) {
            this.listener = listener;
            this.idlingResource = idlingResource;
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            super.onPostExecute(recipes);
            listener.onRecipesRetrieved(recipes);
            if (idlingResource != null) {
                idlingResource.setIdleState(true);
            }
        }

        @Override
        protected List<Recipe> doInBackground(Void... voids) {
            List<Recipe> recipes = null;
            if (idlingResource != null) {
                idlingResource.setIdleState(false);
            }

            try {
                recipes = RecipeRetriever.getRecipesFromUrl();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return recipes;
        }

        public interface RecipesRetrievedListener {
            void onRecipesRetrieved(List<Recipe> recipes);
        }
    }

}
