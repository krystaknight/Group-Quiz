package edu.umsl.quizlet;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import edu.umsl.quizlet.GroupListing.GroupPageActivity;

public class GroupService extends IntentService {

    private static final String TAG = "GroupService";
    private static final long UPDATE_INTERVAL = 3 * 1000;
    private static final long DELAY_INTERVAL = 0;

    private Timer mTimer;

    public GroupService() {
        super(TAG);
        mTimer = new Timer();
    }

    @Override
    public void onCreate() {
        Log.d("SERVICE", "Service Started");
        super.onCreate();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "onHandleIntent " + intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("SERVICE", "Broadcast!");
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(GroupPageActivity.BROADCASTGROUPSTATUS);
                broadcastIntent.putExtra(GroupPageActivity.BROADCASTGROUPSTATUS, "GroupStatus");
                sendBroadcast(broadcastIntent);
            }
        }, DELAY_INTERVAL, UPDATE_INTERVAL);

        return START_NOT_STICKY;
    }
}