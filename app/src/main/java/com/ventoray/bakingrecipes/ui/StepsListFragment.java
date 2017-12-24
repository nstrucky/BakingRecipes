package com.ventoray.bakingrecipes.ui;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.model.Step;
import com.ventoray.bakingrecipes.model.StepAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ventoray.bakingrecipes.ui.StepsActivity.KEY_INGREDIENTS;
import static com.ventoray.bakingrecipes.ui.StepsActivity.KEY_STEPS_LIST;
import static com.ventoray.bakingrecipes.ui.StepsListFragment.IngredientsDialog.INGREDIENTS_MESSAGE_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepsListFragment extends Fragment implements StepAdapter.OnStepClickedListener {

    @BindView(R.id.button_ingredients)
    Button ingredientsButton;
    @BindView(R.id.recycler_steps)
    RecyclerView recipesRecyclerView;
    private StepAdapter adapter;
    private List<Step> steps;
    private String ingredientSummary;
    private OnStepSelectedListener onStepSelectedListener;


    public interface OnStepSelectedListener {
        void onStepSelected(long id);
    }

    public StepsListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            steps = args.getParcelableArrayList(KEY_STEPS_LIST);
            ingredientSummary = args.getString(KEY_INGREDIENTS);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_steps_list, container, false);
        ButterKnife.bind(this, view);
        setUpRecipesRecyclerView();
        onStepSelectedListener = (OnStepSelectedListener) getActivity();
        return view;
    }

    private void setUpRecipesRecyclerView() {

        if (steps != null) {
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            adapter = new StepAdapter(getContext(), steps, this);
            recipesRecyclerView.addItemDecoration(
                    new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL)
            );
            recipesRecyclerView.setNestedScrollingEnabled(false);
            recipesRecyclerView.setLayoutManager(llm);
            recipesRecyclerView.setAdapter(adapter);

        } else {
            Log.d("Fragmentation Mon", "Null recipes list");
        }

    }

    @Override
    public void onStepClicked(View view) {
        long id = (long) view.getTag();
        onStepSelectedListener.onStepSelected(id);

    }

    @OnClick(R.id.button_ingredients)
    public void showIngredients() {
        if (ingredientSummary == null) return;
        IngredientsDialog ingredientsDialog = new IngredientsDialog();
        Bundle args = new Bundle();
        args.putString(INGREDIENTS_MESSAGE_KEY, ingredientSummary);

        ingredientsDialog.setArguments(args);

        FragmentManager manager = getFragmentManager();
        if (manager == null) {
            makeErrorToast();
            return;
        }

        ingredientsDialog.show(manager, "StepsListFragment");

    }


    public static class IngredientsDialog extends DialogFragment {

        public static final String INGREDIENTS_MESSAGE_KEY = "ingredientsMessageKey";
        private String message;

        public IngredientsDialog() {
            super();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args = getArguments();

            if (args != null && args.containsKey(INGREDIENTS_MESSAGE_KEY)) {
                message = args.getString(INGREDIENTS_MESSAGE_KEY);
            } else {
                message = getString(R.string.error);
            }
            Context context = getContext();
            if (context == null) {
                Log.e("IngredientsDialog", "Error establishing context");

            }

            return new AlertDialog.Builder(context)
                    .setMessage(message)
                    .setTitle(getString(R.string.ingredients))
                    .setPositiveButton(R.string.ok, null)
                    .create();
        }
    }

    private void makeErrorToast() {
        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
    }

}
