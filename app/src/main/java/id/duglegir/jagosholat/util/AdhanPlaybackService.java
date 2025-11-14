package id.duglegir.jagosholat.util; // Pastikan package Anda benar

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.ui.main.MainActivity;

public class AdhanPlaybackService extends Service {

    public static final String EXTRA_PRAYER_NAME = "EXTRA_PRAYER_NAME";
    private static final String CHANNEL_ID = "ADHAN_PLAYBACK_CHANNEL";
    private static final int NOTIFICATION_ID = 2; // ID unik untuk foreground service

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME);
        if (prayerName == null) prayerName = "Waktu Sholat";

        Log.d("AdhanService", "Memulai service untuk: " + prayerName);

        // 1. Buat Notifikasi untuk Foreground Service
        // (Ini wajib oleh Android agar service tidak dimatikan)
        Notification notification = buildForegroundNotification(prayerName);
        startForeground(NOTIFICATION_ID, notification);

        // 2. Pilih file Adzan
        int adhanResourceId;
        if (prayerName.equals("Subuh")) {
            adhanResourceId = R.raw.adhan_fajr; // File Anda: res/raw/adhan_fajr.mp3
        } else {
            adhanResourceId = R.raw.adhan_main; // File Anda: res/raw/adhan_main.mp3
        }

        // Hentikan pemutaran lama jika ada
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        // 3. Mulai putar Adzan
        mediaPlayer = MediaPlayer.create(this, adhanResourceId);

        if (mediaPlayer == null) {
            Log.e("AdhanService", "Gagal membuat MediaPlayer. File audio ada?");
            stopSelf(); // Hentikan service jika file tidak ada
            return START_NOT_STICKY;
        }

        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        // 4. Set listener untuk berhenti otomatis saat Adzan selesai
        mediaPlayer.setOnCompletionListener(mp -> {
            Log.d("AdhanService", "Adzan selesai diputar. Menghentikan service.");
            stopSelf(); // Hentikan service
        });

        mediaPlayer.start();

        return START_NOT_STICKY; // Jangan mulai ulang service jika dimatikan
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Pastikan MediaPlayer dilepas
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Fungsi untuk membuat notifikasi Foreground Service
    private Notification buildForegroundNotification(String prayerName) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Adzan " + prayerName)
                .setContentText("Sedang memutar Adzan...")
                .setSmallIcon(R.drawable.ic_logo_jago_sholat) // Ikon Anda
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true) // Hanya berbunyi sekali
                .setOngoing(true) // Notifikasi tidak bisa di-swipe
                .build();
    }

    // Buat Channel (Wajib untuk Android 8+)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notifikasi Pemutaran Adzan",
                    NotificationManager.IMPORTANCE_LOW // LOW agar tidak ada suara notifikasi
            );
            channel.setDescription("Channel untuk service pemutar Adzan");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Tidak perlu di-bind
    }
}