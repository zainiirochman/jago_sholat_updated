package id.duglegir.jagosholat.util; // Pastikan package Anda benar

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.ui.main.MainActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "JADWAL_SHOLAT_CHANNEL";
    private static final CharSequence CHANNEL_NAME = "Notifikasi Jadwal Sholat";

    private Context mContext;

    public NotificationHelper(Context context) {
        this.mContext = context;
    }

    // 1. Membuat Channel (Wajib untuk Android 8+)
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel untuk notifikasi jadwal sholat");
            channel.enableVibration(true);

            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // 2. Membuat dan Menampilkan Notifikasi
    public void showNotification(String title, String message) {
        // Buat Intent untuk membuka MainActivity saat notifikasi di-klik
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Buat Notifikasi
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo_jago_sholat) // GANTI DENGAN IKON ANDA
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}); // Pola getar

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tampilkan notifikasi
        // (Kita gunakan ID 1 agar notifikasi saling menimpa, bukan menumpuk)
        notificationManager.notify(1, builder.build());
    }
}