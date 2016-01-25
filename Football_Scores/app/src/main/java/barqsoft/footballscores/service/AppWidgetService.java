package barqsoft.footballscores.service;


import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.AppWidget;
import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by setico on 15/01/2016.
 */
public class AppWidgetService extends IntentService {

    private static final String[] SCORES_PROJECTION =new String[] {
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.TIME_COL

    };

    // projection columns index
    private static final int INDEX_AWAY = 0;
    private static final int INDEX_AWAY_GOALS = 1;
    private static final int INDEX_HOME = 2;
    private static final int INDEX_HOME_GOALS = 3;
    private static final int INDEX_TIME = 4;

    private AppWidgetManager mAppWidgetManager;
    private int[] mAppWidgetIds;
    private String[] fragmentDateArray = new String[1];

    public AppWidgetService(String name) {
        super(name);
    }

    public AppWidgetService() {
        super("AppWidget");
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName(this,AppWidget.class));

        Date fragmentdate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        fragmentDateArray[0] = mformat.format(fragmentdate);

        Context context = getApplicationContext();

        Uri scoreWithDate = DatabaseContract.scores_table.buildScoreWithDate();

        // we'll query our contentProvider, as always
        Cursor cursor = context.getContentResolver().query(scoreWithDate, SCORES_PROJECTION, null, fragmentDateArray, null);

        //let's get matches randomly
        if(cursor!=null) {
            int position = (int) (Math.random() * cursor.getCount());

            if (cursor.move(position + 1)) {
                String awayTeam = cursor.getString(INDEX_AWAY);
                int awayGoals = cursor.getInt(INDEX_AWAY_GOALS);
                String homeTeam = cursor.getString(INDEX_HOME);
                int homeGoals = cursor.getInt(INDEX_HOME_GOALS);
                String matchTime = cursor.getString(INDEX_TIME);


                for (int i = 0; i < mAppWidgetIds.length; i++) {
                    int appWidgetId = mAppWidgetIds[i];

                    RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget);
                    remoteViews.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(homeTeam));
                    remoteViews.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(awayTeam));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        setRemoteContentDescription(remoteViews, homeTeam + " Vs " + awayTeam);
                    }

                    remoteViews.setTextViewText(R.id.home_name, homeTeam);
                    remoteViews.setTextViewText(R.id.score_textview, Utilies.getScores(homeGoals, awayGoals));
                    remoteViews.setTextViewText(R.id.away_name, awayTeam);
                    remoteViews.setTextViewText(R.id.match_time_textview, matchTime);

                    Intent fartTent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, fartTent, 0);

                    remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

                    mAppWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.home_crest, description);
    }
}