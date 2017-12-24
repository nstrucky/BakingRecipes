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
 * Created by nicks on 12/11/2017.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    Context context;
    List<Step> steps;
    OnStepClickedListener onStepClickedListener;

    public StepAdapter(Context context, List<Step> steps,
                       OnStepClickedListener onStepClickedListener) {
        super();
        this.context = context;
        this.steps = steps;
        this.onStepClickedListener = onStepClickedListener;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.list_item_step,parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        if (steps == null) return;
        Step step = steps.get(position);
        String text;
        if (position == 0) {
            text = step.getDescription();
        } else {
            text = String.format("%d. %s", position, step.getShortDescription());
            String url = step.getThumbnailUrl();
            if (url != null && !url.isEmpty()) {
                Log.d("StepAdapter", "Url: " + url);
                Picasso.with(context)
                        .load(url)
                        .into(holder.stepImage);
            }

        }

        holder.stepName.setText(text);
        holder.itemView.setTag(step.getId());
    }

    @Override
    public int getItemCount() {
        if (steps == null || steps.size() < 1) return 0;
        return steps.size();
    }

    class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_step_name) TextView stepName;
        @BindView(R.id.iv_step_image) ImageView stepImage;

        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStepClickedListener.onStepClicked(view);
                }
            });
        }


    }

    public interface OnStepClickedListener {
        void onStepClicked(View view);
    }
}
