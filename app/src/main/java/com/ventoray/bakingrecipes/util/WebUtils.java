package com.ventoray.bakingrecipes.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.ventoray.bakingrecipes.data.Recipe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.ventoray.bakingrecipes.util.FileUtils.createTmpVidFile;
import static com.ventoray.bakingrecipes.util.RecipeRetriever.RECIPE_URL;

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

    static HttpURLConnection makeHTTPUrlConnection(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.connect();

        if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.e("WebUtils", "Error connecting with server: " +
                    httpURLConnection.getResponseCode()
                    + "\n" + httpURLConnection.getResponseMessage());
            return null;
        }

        return httpURLConnection;
    }


    static String getJsonResponse(HttpURLConnection connection) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            Log.d("WebUtils", "\n"+line);
        }

        return stringBuilder.toString();

    }







}
