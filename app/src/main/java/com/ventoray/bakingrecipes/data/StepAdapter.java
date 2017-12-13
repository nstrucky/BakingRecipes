package com.ventoray.bakingrecipes.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public StepAdapter(Context context, List<Step> steps) {
        super();
        this.context = context;
        this.steps = steps;
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
        holder.stepName.setText(String.format("%d. %s", position+1, step.getShortDescription()));
    }

    @Override
    public int getItemCount() {
        if (steps == null || steps.size() < 1) return 0;
        return steps.size();
    }

    class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_step_name) TextView stepName;
//        TextView stepName;

        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            stepName = itemView.findViewById(R.id.tv_step_name);
        }
    }
}
