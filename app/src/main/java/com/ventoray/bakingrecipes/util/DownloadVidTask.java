package com.ventoray.bakingrecipes.util;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nick on 12/16/2017.
 */

public class DownloadVidTask extends AsyncTask<Void, Void, Void> {


    private Context context;
    private String videoUrl;
    private Uri videoStorageUri;
    private DownloadCompleteListener downloadCompleteListener;
    private final String VIDEO_FILE_PREFIX = "temp_video";
    private final String TAG = "DownloadVidTask";


    public interface DownloadCompleteListener {
        void onDownloadComplete(Uri videoStorageUri);
    }

    public DownloadVidTask(Context context, DownloadCompleteListener downloadCompleteListener,
                           String videoUrl) {
        this.context = context;
        this.downloadCompleteListener = downloadCompleteListener;
        this.videoUrl = videoUrl;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        downloadCompleteListener.onDownloadComplete(videoStorageUri);

    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            writeToFile(makeHTTPUrlConnection());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private HttpURLConnection makeHTTPUrlConnection() throws IOException {
        URL url = new URL(videoUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.connect();

        if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.e(TAG, "Error connecting with server: " + httpURLConnection.getResponseCode()
                    + "\n" + httpURLConnection.getResponseMessage());
            return null;
        }

        return httpURLConnection;
    }

    private void writeToFile(HttpURLConnection connection) throws IOException {
        File outputFile = createFile();
        InputStream inputStream = connection.getInputStream();
        FileOutputStream fos = new FileOutputStream(outputFile);

        byte[] buffer = new byte[1024];
        int len1 = 0;
        while ((len1 = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, len1);
        }

        fos.close();
        inputStream.close();
    }

    private File createFile() throws IOException {
        File file = File.createTempFile(VIDEO_FILE_PREFIX, null, context.getCacheDir());
        file.deleteOnExit();
        videoStorageUri = Uri.fromFile(file);

        return file;
    }


}
