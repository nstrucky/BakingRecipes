package com.ventoray.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ventoray.bakingrecipes.util.FileUtils;

/**
 * Created by Nick on 12/18/2017.
 */

public class StackWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewFactory(this.getApplicationContext());
    }


    class StackRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private String[] ingredientsArray;

        public StackRemoteViewFactory(Context context) {
            this.context = context;
        }


        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews remoteViews =
                    new RemoteViews(context.getPackageName(), R.layout.stackwidget_item);

            String ingredients = ingredientsArray[i];

            remoteViews.setTextViewText(R.id.tv_ingredients_stack_item, ingredients);

            return remoteViews;
        }


        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            ingredientsArray = FileUtils.getIngredientsArray(context);

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (ingredientsArray == null || ingredientsArray.length == 0) return 0;
            return ingredientsArray.length;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }


}
