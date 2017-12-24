package com.ventoray.bakingrecipes.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ventoray.bakingrecipes.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nicks on 12/10/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private static final float PICTURE_PRESENT_TEXT_SIZE = 12f;

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
        String imageUrl = recipe.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d("RecipeAdapter", "Url: " + imageUrl);
            holder.recipeNameTextView.setTextSize(PICTURE_PRESENT_TEXT_SIZE);
            Picasso.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.example_appwidget_preview)
                    .into(holder.imageView);
        }

        holder.recipeNameTextView.setText(recipe.getName());
        holder.servingSizeTextView.setText(String.format("%s%n    %d",
                context.getString(R.string.serves), recipe.getServings()));
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

        @BindView(R.id.tv_serving_size) TextView servingSizeTextView;
        @BindView(R.id.tv_recipe_name) TextView recipeNameTextView;
        @BindView(R.id.imageView) ImageView imageView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

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
