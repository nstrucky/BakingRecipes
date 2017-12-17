package com.ventoray.bakingrecipes.util;

import android.content.ContentValues;
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

public class WebUtils {


    private static final String VIDEO_FILE_PREFIX = "temp_video";

    public static Uri connectAndWriteToFile(Context context, String videoUrl) throws IOException {
        File outputFile = createFile(context);
        HttpURLConnection connection = makeHTTPUrlConnection(videoUrl);
        if (connection == null) return null;

        InputStream inputStream = connection.getInputStream();
        FileOutputStream fos = new FileOutputStream(outputFile);

        byte[] buffer = new byte[1024];
        int len1 = 0;
        while ((len1 = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, len1);
        }

        fos.close();
        inputStream.close();

        return Uri.fromFile(outputFile);
    }

    private static HttpURLConnection makeHTTPUrlConnection(String videoUrl) throws IOException {
        URL url = new URL(videoUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.connect();

        if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.e("DownLoadTask", "Error connecting with server: " + httpURLConnection.getResponseCode()
                    + "\n" + httpURLConnection.getResponseMessage());
            return null;
        }

        return httpURLConnection;
    }



    private static File createFile(Context context) throws IOException {
        File file = File.createTempFile(VIDEO_FILE_PREFIX, null, context.getCacheDir());
        file.deleteOnExit();

        return file;
    }


}
