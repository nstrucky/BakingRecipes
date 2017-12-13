package com.ventoray.bakingrecipes.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicks on 12/7/2017.
 */

public class Recipe implements Parcelable {

    private long id;
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private int servings;
    private String image;
    private String ingredientSummary;

    public Recipe() {

    }

    public Recipe(Parcel in) {

        id = in.readLong();
        name = in.readString();
        ingredients = new ArrayList<>();
        in.readList(ingredients, null);
        steps = new ArrayList<>();
        in.readList(steps, null);
        servings = in.readInt();
        image = in.readString();
        ingredientSummary = in.readString();
    }

    public static final Parcelable.Creator<Recipe> CREATOR =
            new Parcelable.Creator<Recipe>() {

                @Override
                public Recipe createFromParcel(Parcel in) {
                    return new Recipe(in);
                }

                @Override
                public Recipe[] newArray(int size) {
                    return new Recipe[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeLong(id);
        out.writeString(name);
        out.writeList(ingredients);
        out.writeList(steps);
        out.writeInt(servings);
        out.writeString(image);
        out.writeString(ingredientSummary);
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
