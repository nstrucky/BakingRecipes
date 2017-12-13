package com.ventoray.bakingrecipes.data;

/**
 * Created by nicks on 12/7/2017.
 */

public class Ingredient {

    double quantity;
    String name;
    String unit;

    public Ingredient(double quantity, String name, String unit) {
        this.quantity = quantity;
        this.name = name;
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
