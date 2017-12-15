package com.ventoray.bakingrecipes.ui;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
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

    @BindView(R.id.exo_player_view) SimpleExoPlayerView simpleExoPlayerView;
    @BindView(R.id.tv_step_instructions) TextView instructionsTextView;
    @BindView(R.id.fab_next) FloatingActionButton nextButton;
    @BindView(R.id.fab_previous) FloatingActionButton previousButton;


    private boolean isTablet;
    private boolean isLandscape;

    public StepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, view);
        isTablet = ScreenUtils.isTablet(getContext());
        isLandscape = ScreenUtils.isLandscape(getContext());

        if (!isTablet) navigationListener = (StepsNavigationListener) getActivity();


        Bundle args = getArguments();

        if (args != null) {
            String text = String.valueOf(args.getLong(KEY_STEP_ID));
            step = args.getParcelable(KEY_STEP_PARCEL);
            String instructions;
            if ((instructions = step.getDescription()) != null)
            instructionsTextView.setText(instructions);
        }

        if (isLandscape && !isTablet) {
            nextButton.setVisibility(View.GONE);
            previousButton.setVisibility(View.GONE);
            instructionsTextView.setVisibility(View.GONE);
        }

        createExoPlayer();

        return view;
    }

    @OnClick({R.id.fab_previous, R.id.fab_next})
    public void onNavButtonsClicked(View view) {
        String button = view.getId() == R.id.fab_next ? NEXT_BUTTON : PREVIOUS_BUTTON;
        navigationListener.onNavigationButtonClicked(button, step.getId());
    }


    private void createExoPlayer() {
        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the player
        SimpleExoPlayer simpleExoPlayer =
                ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

        simpleExoPlayerView.setPlayer(simpleExoPlayer);
        preparePlayer(simpleExoPlayer);
    }


    private void preparePlayer(SimpleExoPlayer simpleExoPlayer) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                // TODO: 12/14/2017 figure out if this is right
                Util.getUserAgent(getContext(), getContext().getApplicationInfo().name), bandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource(getStepVideoUri(),
                dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source.
        simpleExoPlayer.prepare(videoSource);
    }

    private Uri getStepVideoUri() {
        Uri uri = Uri.parse(step.getVideoUrl());
        return uri;
    }



    public interface StepsNavigationListener {
        void onNavigationButtonClicked(@NavigationButtons String navigationButton, long id);
    }





}
