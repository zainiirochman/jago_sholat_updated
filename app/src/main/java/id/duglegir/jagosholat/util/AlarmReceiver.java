package id.duglegir.jagosholat.util; // Pastikan package Anda benar

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Ambil data (Judul & Pesan) dari Intent
        String title = intent.getStringExtra(EXTRA_TITLE);
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        if (title == null) {
            title = "Waktu Sholat";
        }

        Log.d("AlarmReceiver", "Alarm Diterima: " + title);

        // Buat helper dan tampilkan notifikasi
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotificationChannel(); // Buat channel (aman dipanggil berkali-kali)
        notificationHelper.showNotification(title, message);

        // JADWALKAN ULANG ALARM UNTUK HARI BERIKUTNYA
        // (Ini penting agar alarm tidak hanya sekali jalan)
        AlarmScheduler.scheduleNextDayAlarms(context);
    }
}