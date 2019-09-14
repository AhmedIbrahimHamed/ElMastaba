package com.example.android.elmastaba.Widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.elmastaba.MainActivity;
import com.example.android.elmastaba.R;
import com.example.android.elmastaba.fragments.MyChatRoomsFragment;

import static android.os.Build.VERSION_CODES.N;

/**
 * Implementation of App Widget functionality.
 */
public class RoomsWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(
                context.getPackageName(), R.layout.rooms_widget_layout);

        setRemoteAdapter(context, views);

        views.setEmptyView(R.id.widget_layout_rooms_list, R.id.widget_layout_list_empty_text);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int widgets = appWidgetIds.length;
        for (int i = 0; i < widgets; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_layout_rooms_list);

        super.onReceive(context, intent);
    }

    @SuppressWarnings("deprecation")
    static private void setRemoteAdapterV11(Context context, RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_layout_rooms_list,
                new Intent(context.getApplicationContext(), RoomsService.class));

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    static private void setRemoteAdapter(Context context, @NonNull RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_layout_rooms_list,
                new Intent(context.getApplicationContext(), RoomsService.class));
    }

}

