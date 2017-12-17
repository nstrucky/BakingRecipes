package com.ventoray.bakingrecipes.util;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

/**
 * Created by nicks on 12/17/2017.
 */

public class VideoDownloadAsyncTask extends AsyncTaskLoader<Uri> {

    private Context context;
    private String videoUrl;

    public VideoDownloadAsyncTask(Context context, String videoUrl) {
        super(context);
        this.videoUrl = videoUrl;
        this.context = context;
    }

    @Override
    public Uri loadInBackground() {
        Uri uri = null;
        try {
            uri = WebUtils.connectAndWriteToFile(context, videoUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }
}
