package apps.nanodegree.thelsien.capstone.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import apps.nanodegree.thelsien.capstone.MainActivity;
import apps.nanodegree.thelsien.capstone.R;
import apps.nanodegree.thelsien.capstone.Utility;

/**
 * Created by frodo on 2016. 11. 19..
 */

public class MoneyTrackRWidgetIntentService extends IntentService {
    public MoneyTrackRWidgetIntentService() {
        super("MoneyMakRIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        float incomesSum = Utility.getIncomesSum(this);
        float spendingsSum = Utility.getSpendingsSum(this);
        float balance = incomesSum - spendingsSum;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                MoneyTrackRWidgetProvider.class));

        for (int appWidgetId : appWidgetIds) {
            int widgetHeight = getWidgetHeight(appWidgetManager, appWidgetId);
            int mediumHeight = getResources().getDimensionPixelSize(R.dimen.widget_medium_height);
            int largeHeight = getResources().getDimensionPixelSize(R.dimen.widget_large_height);
            int layoutId;

            if (widgetHeight >= largeHeight) {
                layoutId = R.layout.widget_layout_large;
            } else if (widgetHeight >= mediumHeight) {
                layoutId = R.layout.widget_layout_medium;
            } else {
                layoutId = R.layout.widget_layout_small;
            }

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setTextViewText(R.id.tv_balance, Utility.getValueFormatWithCurrency(this).format(balance));
            views.setTextViewText(R.id.tv_spendings, Utility.getValueFormatWithCurrency(this).format(spendingsSum));
            views.setTextViewText(R.id.tv_incomes, Utility.getValueFormatWithCurrency(this).format(incomesSum));

            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private int getWidgetHeight(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_min_height);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)) {
            int minHeightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minHeightDp,
                    displayMetrics);
        }
        return getResources().getDimensionPixelSize(R.dimen.widget_min_height);
    }
}
