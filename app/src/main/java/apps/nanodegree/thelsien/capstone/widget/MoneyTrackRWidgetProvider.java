package apps.nanodegree.thelsien.capstone.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by frodo on 2016. 11. 19..
 */

public class MoneyTrackRWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_DATA_UPDATED = "app.nanodegree.thelsien.capstone.ACTION_DATA_UPDATED";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, MoneyTrackRWidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, MoneyTrackRWidgetIntentService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, MoneyTrackRWidgetIntentService.class));
        }
    }
}
