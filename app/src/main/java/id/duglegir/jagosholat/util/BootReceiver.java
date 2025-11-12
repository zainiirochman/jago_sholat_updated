package id.duglegir.jagosholat.util; // Pastikan package Anda benar

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Cek apakah aksinya adalah BOOT_COMPLETED
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "HP Selesai Reboot. Menjadwalkan ulang alarm...");
            // Panggil Penjadwal Alarm
            AlarmScheduler.scheduleAlarms(context);
        }
    }
}