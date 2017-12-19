package com.ventoray.bakingrecipes.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import static com.ventoray.bakingrecipes.util.FileUtils.createTmpVidFile;

/**
 * Created by Nick on 12/16/2017.
 */

public class WebUtils {




    public static Uri connectAndWriteToFile(Context context, String videoUrl) throws IOException {
        File outputFile = createTmpVidFile(context);
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
            Log.e("DownLoadTask", "Error connecting with server: " +
                    httpURLConnection.getResponseCode()
                    + "\n" + httpURLConnection.getResponseMessage());
            return null;
        }

        return httpURLConnection;
    }



}
