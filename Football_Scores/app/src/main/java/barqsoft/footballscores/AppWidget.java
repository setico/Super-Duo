package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import barqsoft.footballscores.service.AppWidgetService;
import barqsoft.footballscores.service.myFetchService;

/**
 * Created by setico on 09/09/15.
 */


public class AppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds){


        for (int i=0;i<appWidgetIds.length;i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

            context.startService(new Intent(context, AppWidgetService.class));

            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context,0,clickIntent,0);
            remoteViews.setOnClickPendingIntent(R.id.widget, clickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        }
    }


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, AppWidgetService.class));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.home_crest, description);
    }

    @Override
    public  void onReceive( Context context,Intent intent){
        super.onReceive(context, intent);



        if (myFetchService.ACTION_UPDATE_WIDGET.equals(intent.getAction())){
            context.startService(new Intent(context, AppWidgetService.class));
        }

    }
}
