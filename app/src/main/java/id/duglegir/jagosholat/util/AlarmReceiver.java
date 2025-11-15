package id.duglegir.jagosholat.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_PRAYER_NAME = "EXTRA_PRAYER_NAME";

    @Override
    public void onReceive(Context context, Intent intent) {

        String prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME);
        if (prayerName == null) prayerName = "Sholat";

        Log.d("AlarmReceiver", "Alarm Diterima: " + prayerName + ". Memulai Service...");

        Intent serviceIntent = new Intent(context, AdhanPlaybackService.class);
        serviceIntent.putExtra(EXTRA_PRAYER_NAME, prayerName);

        ContextCompat.startForegroundService(context, serviceIntent);

        AlarmScheduler.scheduleNextDayAlarms(context);
    }
}