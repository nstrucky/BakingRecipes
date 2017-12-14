package com.ventoray.bakingrecipes.ui;


import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.data.Step;
import com.ventoray.bakingrecipes.util.ScreenUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ventoray.bakingrecipes.ui.StepsActivity.KEY_STEP_ID;
import static com.ventoray.bakingrecipes.ui.StepsActivity.KEY_STEP_PARCEL;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepFragment extends Fragment {

    @StringDef({
            PREVIOUS_BUTTON,
            NEXT_BUTTON
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface NavigationButtons{}

    public static final String PREVIOUS_BUTTON =
            "com.ventoray.bakingrecipes.ui.StepFragment.previousButton";
    public static final String NEXT_BUTTON =
            "com.ventoray.bakingrecipes.ui.StepFragment.nextButton";

    private StepsNavigationListener navigationListener;
    private Step step;

    @BindView(R.id.tv_test_textview) TextView testText;
    @BindView(R.id.fab_next) FloatingActionButton nextButton;
    @BindView(R.id.fab_previous) FloatingActionButton previousButton;

    private boolean isTablet;

    public StepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, view);
        isTablet = ScreenUtils.isTablet(getContext());
        if (!isTablet) navigationListener = (StepsNavigationListener) getActivity();


        Bundle args = getArguments();

        if (args != null) {
            String text = String.valueOf(args.getLong(KEY_STEP_ID));
            step = args.getParcelable(KEY_STEP_PARCEL);
            testText.setText(text);
        }

        return view;
    }

    @OnClick({R.id.fab_previous, R.id.fab_next})
    public void onNavButtonsClicked(View view) {
        String button = view.getId() == R.id.fab_next ? NEXT_BUTTON : PREVIOUS_BUTTON;
        navigationListener.onNavigationButtonClicked(button, step.getId());
    }

    public interface StepsNavigationListener {
        void onNavigationButtonClicked(@NavigationButtons String navigationButton, long id);
    }

}
