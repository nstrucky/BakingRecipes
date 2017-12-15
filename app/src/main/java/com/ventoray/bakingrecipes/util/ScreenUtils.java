package com.ventoray.bakingrecipes.util;

import android.content.Context;

import com.ventoray.bakingrecipes.R;

/**
 * Created by nicks on 12/11/2017.
 */

public class ScreenUtils {

    /**
     * Determines whether the device is a tablet by measuring the Portrait mode width where
     * 600dp is considered a tablet device.
     * @param context
     * @return - boolean
     */
    public static boolean isTablet(Context context) {
        boolean isWide = context.getResources().getBoolean(R.bool.isWide);
        boolean isTall = context.getResources().getBoolean(R.bool.isTall);
        return isWide && isTall;
    }

    /**
     * Determines whether the device is in Landscape orientation or not
     * @param context
     * @return - bool
     */
    public static boolean isLandscape(Context context) {
        return context.getResources().getBoolean(R.bool.isLandscape);
    }


}
