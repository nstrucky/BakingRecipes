package com.ventoray.bakingrecipes.util;

import android.content.Context;

import com.ventoray.bakingrecipes.R;

/**
 * Created by nicks on 12/11/2017.
 */

public class ScreenUtils {

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);

    }


}
