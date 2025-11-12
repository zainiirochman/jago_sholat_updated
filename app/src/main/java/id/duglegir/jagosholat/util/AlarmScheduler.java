package id.duglegir.jagosholat.util; // Pastikan package Anda benar

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class AlarmScheduler {

    // KITA ASUMSIKAN NAMA JADWAL SHOLAT ANDA
    private static final String[] PRAYER_NAMES = {"Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya"};

    // Panggil ini untuk menjadwalkan SEMUA alarm
    public static void scheduleAlarms(Context context) {
        for (String prayerName : PRAYER_NAMES) {
            schedulePrayerAlarm(context, prayerName);
        }
        Log.d("AlarmScheduler", "Semua alarm telah dijadwalkan.");
    }

    // Panggil ini dari AlarmReceiver untuk menjadwalkan ulang besok
    public static void scheduleNextDayAlarms(Context context) {
        // Tambahkan 1 menit delay agar tidak tumpang tindih
        long delay = 60 * 1000;

        // Kita gunakan set() agar tidak membebani sistem
        // Ini akan dijadwalkan ulang saat HP idle
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BootReceiver.class); // Panggil BootReceiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 99, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
        Log.d("AlarmScheduler", "Menjadwalkan ulang semua alarm untuk besok...");
    }


    private static void schedulePrayerAlarm(Context context, String prayerName) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 1. Dapatkan waktu sholat dalam milidetik
        long prayerTimeMillis = getPrayerTimeInMillis(prayerName);

        // Jika waktu tidak valid (misal: "00:00") atau sudah lewat
        if (prayerTimeMillis <= System.currentTimeMillis()) {
            // Jika sudah lewat, kita jadwalkan untuk besok
            prayerTimeMillis = getPrayerTimeInMillis(prayerName, true);
        }

        // 2. Buat Intent untuk AlarmReceiver
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_TITLE, "Waktu " + prayerName + " Tiba");
        intent.putExtra(AlarmReceiver.EXTRA_MESSAGE, "Saatnya menunaikan sholat " + prayerName);

        // Kita butuh ID unik untuk tiap alarm (Subuh=0, Dzuhur=1, dst.)
        int requestCode = getRequestCode(prayerName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 3. Set Alarm Tepat Waktu
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ (Wajib cek izin)
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, prayerTimeMillis, pendingIntent);
                } else {
                    // Fallback jika izin tidak ada (alarm mungkin tidak presisi)
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, prayerTimeMillis, pendingIntent);
                }
            } else {
                // Android 11 ke bawah
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, prayerTimeMillis, pendingIntent);
            }
            Log.d("AlarmScheduler", "Alarm " + prayerName + " di-set untuk: " + prayerTimeMillis);

        } catch (SecurityException e) {
            Log.e("AlarmScheduler", "Gagal set alarm: " + e.getMessage());
        }
    }

    // Fungsi "INTI" untuk mengubah "04:30" menjadi milidetik
    private static long getPrayerTimeInMillis(String prayerName) {
        return getPrayerTimeInMillis(prayerName, false); // Default untuk hari ini
    }

    private static long getPrayerTimeInMillis(String prayerName, boolean isForTomorrow) {
        // === GANTI KODE DI BAWAH INI DENGAN JADWAL HELPER ANDA ===
        // Saya hanya berasumsi, sesuaikan dengan kode Anda
        String timeString = "00:00";

        // JadwalHelper jadwalHelper = new JadwalHelper(context); // Mungkin perlu context?
        // switch (prayerName) {
        //     case "Subuh": timeString = jadwalHelper.getWaktuSubuh(); break; // misal: "04:30"
        //     case "Dzuhur": timeString = jadwalHelper.getWaktuDzuhur(); break; // misal: "11:45"
        //     case "Ashar": timeString = jadwalHelper.getWaktuAshar(); break; // misal: "15:00"
        //     case "Maghrib": timeString = jadwalHelper.getWaktuMaghrib(); break; // misal: "17:55"
        //     case "Isya": timeString = jadwalHelper.getWaktuIsya(); break; // misal: "19:05"
        // }

        // --- HAPUS INI NANTI (HANYA UNTUK CONTOH) ---
        switch (prayerName) {
            case "Subuh": timeString = "04:30"; break;
            case "Dzuhur": timeString = "11:45"; break;
            case "Ashar": timeString = "15:00"; break;
            case "Maghrib": timeString = "17:55"; break;
            case "Isya": timeString = "19:05"; break;
        }
        // --- HAPUS SAMPAI SINI ---

        // Parsing "HH:mm"
        try {
            String[] parts = timeString.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Jika untuk besok
            if (isForTomorrow) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            // Jika waktu hari ini sudah lewat, otomatis set untuk besok
            else if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            return calendar.getTimeInMillis();

        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Waktu tidak valid
        }
    }

    // Helper untuk ID unik PendingIntent
    private static int getRequestCode(String prayerName) {
        switch (prayerName) {
            case "Subuh": return 0;
            case "Dzuhur": return 1;
            case "Ashar": return 2;
            case "Maghrib": return 3;
            case "Isya": return 4;
            default: return 5;
        }
    }
}