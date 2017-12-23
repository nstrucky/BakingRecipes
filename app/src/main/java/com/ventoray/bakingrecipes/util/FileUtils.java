package com.ventoray.bakingrecipes.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Nick on 12/18/2017.
 */

public class FileUtils {

    private static final String VIDEO_FILE_PREFIX = "temp_video";
    public static final String INGREDIENTS_FILE = "ingredientsStringsFile";

    public static File createTmpVidFile(Context context) throws IOException {
        File file = File.createTempFile(VIDEO_FILE_PREFIX, null, context.getCacheDir());
        file.deleteOnExit();

        return file;
    }

    /**
     * Creates a file with the ingredient summaries for each recipe so that the StackWidget
     *  can display the ingredients
     * @param context
     * @param ingredients
     */
    public static void createIngredientsFile(Context context, String[] ingredients) {

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(INGREDIENTS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(ingredients);
            outputStream.close();
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param context
     * @return
     */
    public static String[] getIngredientsArray(Context context) {
        String[] ingredients = null;
        try {
            FileInputStream inputStream = context.openFileInput(INGREDIENTS_FILE);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            ingredients = (String[]) objectInputStream.readObject();
            inputStream.close();
            objectInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

}
