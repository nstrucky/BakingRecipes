package com.ventoray.bakingrecipes.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ventoray.bakingrecipes.R;

import java.util.List;

/**
 * Created by nicks on 12/10/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;
    private Context context;
    private OnRecipeCardClicked cardClickListener;


    public RecipeAdapter(Context context, List<Recipe> recipes,
                         OnRecipeCardClicked cardClickListener) {
        super();
        this.recipes = recipes;
        this.context = context;
        this.cardClickListener = cardClickListener;
    }


    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.recipeNameTextView.setText(recipe.getName());
        holder.servingSizeTextView.setText(String.valueOf(recipe.getServings()));
        holder.itemView.setTag(recipe.getId());

    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                        .inflate(R.layout.list_item_recipe, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (recipes == null || recipes.size() < 1) return 0;
        return recipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView recipeNameTextView;
        TextView servingSizeTextView;

        public RecipeViewHolder(View itemView) {
            super(itemView);

            recipeNameTextView = itemView.findViewById(R.id.tv_recipe_name);
            servingSizeTextView = itemView.findViewById(R.id.tv_serving_size);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardClickListener.onRecipeCardClicked(view);
                }
            });
        }
    }

    public interface OnRecipeCardClicked {
        void onRecipeCardClicked(View itemView);
    }


}
