package com.ventoray.bakingrecipes.data;

import java.util.List;

/**
 * Created by nicks on 12/7/2017.
 */

public class Recipe {

    private long id;
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private int servings;
    private String image;
    private String ingredientSummary;

    public Recipe() {

    }

    public Recipe(long id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
        ingredientSummary = createIngredientSummary(ingredients);
    }

    private String createIngredientSummary(List<Ingredient> ingredients) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < ingredients.size(); i++) {
            String name = ingredients.get(i).getName();

            builder.append(String.format("%d. %s%n",
                    i+1, name));
        }

        return builder.toString();
    }

    public String getIngredientSummary() {
        return ingredientSummary;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

}
