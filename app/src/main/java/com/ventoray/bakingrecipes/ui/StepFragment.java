package com.ventoray.bakingrecipes.ui;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ventoray.bakingrecipes.R;
import com.ventoray.bakingrecipes.model.Step;
import com.ventoray.bakingrecipes.util.VideoDownloadAsyncTask;
import com.ventoray.bakingrecipes.util.ScreenUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ventoray.bakingrecipes.ui.StepsActivity.KEY_STEP_PARCEL;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepFragment extends Fragment implements LoaderManager.LoaderCallbacks<Uri>,
        ExoPlayer.EventListener {

    @StringDef({
            PREVIOUS_BUTTON,
            NEXT_BUTTON
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface NavigationButtons {
    }

    public interface StepsNavigationListener {
        void onNavigationButtonClicked(@NavigationButtons String navigationButton, long id);
    }

    public static final int DOWNLOAD_VIDEO_TASK = 1000;
    public static final String PREVIOUS_BUTTON =
            "com.ventoray.bakingrecipes.ui.StepFragment.previousButton";
    public static final String NEXT_BUTTON =
            "com.ventoray.bakingrecipes.ui.StepFragment.nextButton";
    public static final String PLAYER_STATE_POSITION_KEY = "playerStatePositionKey";
    public static final String CURRENT_VIDEO_URI_KEY = "currentVideoUriKey";
    public static final String CONTINUE_LOADING_KEY = "continueLoadingKey";
    public static final String SHOULD_PLAY_KEY = "shouldPlayKey";

    private StepsNavigationListener navigationListener;
    private Step step;
    private Uri videoStorageUri;

    @BindView(R.id.exo_player_view)
    SimpleExoPlayerView simpleExoPlayerView;
    @BindView(R.id.tv_step_instructions)
    TextView instructionsTextView;
    @BindView(R.id.tv_no_video)
    TextView noVideoTextView;
    @BindView(R.id.fab_next)
    FloatingActionButton nextButton;
    @BindView(R.id.fab_previous)
    FloatingActionButton previousButton;
    @BindView(R.id.progress_video)
    FrameLayout progressBar;

    private boolean isTablet;
    private boolean isLandscape;
    private boolean continueLoading = true;
    private boolean shouldPlay = true;
    private SimpleExoPlayer simpleExoPlayer;

    public StepFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (args != null) {
            step = args.getParcelable(KEY_STEP_PARCEL);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(CONTINUE_LOADING_KEY)) {
            continueLoading = savedInstanceState.getBoolean(CONTINUE_LOADING_KEY);
        }

        if (continueLoading) {
            getLoaderManager().initLoader(DOWNLOAD_VIDEO_TASK, null, this);
        }

        setUpMediaSession();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, view);
        isTablet = ScreenUtils.isTablet(getContext());
        isLandscape = ScreenUtils.isLandscape(getContext());
        if (continueLoading) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (!isTablet) navigationListener = (StepsNavigationListener) getActivity();

        String instructions;
        if ((instructions = step.getDescription()) != null)
            instructionsTextView.setText(instructions);

        if (isLandscape && !isTablet) {
            nextButton.setVisibility(View.GONE);
            previousButton.setVisibility(View.GONE);
            instructionsTextView.setVisibility(View.GONE);

        } else if (isTablet) {
            nextButton.setVisibility(View.GONE);
            previousButton.setVisibility(View.GONE);
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
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        simpleExoPlayer =
                ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
        simpleExoPlayer.addListener(this);

        simpleExoPlayerView.setPlayer(simpleExoPlayer);

    }

    private void preparePlayer(SimpleExoPlayer simpleExoPlayer, Uri storageUri) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), "BakingRecipes"), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(storageUri,
                dataSourceFactory, extractorsFactory, null, null);
        simpleExoPlayer.prepare(videoSource);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    private void releasePlayer() {
        simpleExoPlayer.stop();
        simpleExoPlayer.release();
        simpleExoPlayer = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        simpleExoPlayer.stop();
        long position = simpleExoPlayer.getCurrentPosition();
        outState.putLong(PLAYER_STATE_POSITION_KEY, position);
        outState.putBoolean(CONTINUE_LOADING_KEY, continueLoading);
        outState.putBoolean(SHOULD_PLAY_KEY, shouldPlay);
        if (videoStorageUri != null) {
            outState.putString(CURRENT_VIDEO_URI_KEY, videoStorageUri.toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        long position = 0;
        if (savedInstanceState == null) return;

        if (savedInstanceState.containsKey(PLAYER_STATE_POSITION_KEY)) {
            position = savedInstanceState.getLong(PLAYER_STATE_POSITION_KEY);
        }

        if (savedInstanceState.containsKey(SHOULD_PLAY_KEY)) {
            shouldPlay = savedInstanceState.getBoolean(SHOULD_PLAY_KEY);
        }

        if (savedInstanceState.containsKey(CURRENT_VIDEO_URI_KEY)) {
            videoStorageUri = Uri.parse(savedInstanceState.getString(CURRENT_VIDEO_URI_KEY));
        }

        if (videoStorageUri != null) {
            simpleExoPlayer.seekTo(position);
            if (shouldPlay) {
                simpleExoPlayer.setPlayWhenReady(true);
            } else {
                simpleExoPlayer.setPlayWhenReady(false);
            }

            simpleExoPlayerView.setVisibility(View.VISIBLE);
            preparePlayer(simpleExoPlayer, videoStorageUri);
        } else if (!continueLoading) {
            noVideoTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<Uri> onCreateLoader(int id, Bundle args) {
        Loader<Uri> loader = new VideoDownloadAsyncTask(getActivity(), step.getVideoUrl());
        loader.forceLoad();

        return loader;
    }

    @Override
    public void onLoaderReset(Loader<Uri> loader) {
        videoStorageUri = null;
    }

    @Override
    public void onLoadFinished(Loader<Uri> loader, Uri data) {
        progressBar.setVisibility(View.GONE);
        continueLoading = false;

        if (data == null) {
            noVideoTextView.setVisibility(View.VISIBLE);
            return;
        }

        videoStorageUri = data;
        simpleExoPlayerView.setVisibility(View.VISIBLE);

        preparePlayer(simpleExoPlayer, data);

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playWhenReady && playbackState == ExoPlayer.STATE_READY) {
            playbackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    simpleExoPlayer.getCurrentPosition(), 1f);
            shouldPlay = true;
        } else if (playbackState == ExoPlayer.STATE_READY) {
            playbackStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    simpleExoPlayer.getCurrentPosition(), 1f);
            shouldPlay = false;
            Toast.makeText(getActivity(), R.string.paused, Toast.LENGTH_SHORT).show();
        }
        mediaSession.setPlaybackState(playbackStateBuilder.build());

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity().isChangingConfigurations()) {
            return;
        }
        releasePlayer();
        mediaSession.setActive(false);
    }

    /****************************************************************************************
     *  MediaSession code for future functionality...tabling for now
     * **************************************************************************************
     */

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder playbackStateBuilder;


    private void setUpMediaSession() {
        mediaSession = new MediaSessionCompat(this.getContext(), "StepFragment");
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setMediaButtonReceiver(null);
        playbackStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSession.setPlaybackState(playbackStateBuilder.build());
        mediaSession.setCallback(new MediaSessionCallback());
        mediaSession.setActive(true);

    }


    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            simpleExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            simpleExoPlayer.setPlayWhenReady(false);

        }
    }



}

