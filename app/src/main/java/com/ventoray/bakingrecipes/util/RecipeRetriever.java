package com.ventoray.bakingrecipes.util;

import android.util.JsonReader;

import com.ventoray.bakingrecipes.data.Ingredient;
import com.ventoray.bakingrecipes.data.Recipe;
import com.ventoray.bakingrecipes.data.Step;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicks on 12/10/2017.
 */

public class RecipeRetriever {

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
}
