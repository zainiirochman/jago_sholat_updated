package id.duglegir.jagosholat.ui.main;

import android.os.Bundle;
import androidx.annotation.NonNull; // Import baru
import androidx.annotation.Nullable; // Import baru
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// import android.widget.TextView; // Tidak diperlukan lagi

import id.duglegir.jagosholat.util.FunctionHelper;
import id.duglegir.jagosholat.util.JadwalHelper;
import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.databinding.FragmentJadwalBinding; // <-- IMPORT BARU UNTUK VIEW BINDING


public class JadwalFragment extends Fragment {

    // Deklarasi Class Helper ----------------------------------------------------------------------
    private JadwalHelper jadwalHelper = new JadwalHelper();
    private FunctionHelper functionHelper = new FunctionHelper();
    // ---------------------------------------------------------------------------------------------

    // Deklarasi Requirement Variable --------------------------------------------------------------
    private int countTime;
    // ---------------------------------------------------------------------------------------------

    // Deklarasi View Binding (MENGGANTIKAN DEKLARASI MANUAL) -----------------------------------
    private FragmentJadwalBinding binding;
    // ---------------------------------------------------------------------------------------------

    public JadwalFragment() {
        // Required empty public constructor
    }

    public void CekJadwal(){
        // PERBAIKAN: Gunakan 'binding' untuk mengakses TextView
        // Pastikan ID di file XML Anda benar (misal: binding.txtViewSholat)

        // PENTING: Cek null pada jadwalHelper.getMJadwalShalat() untuk menghindari crash
        String jadwalShalat = jadwalHelper.getMJadwalShalat();
        if (jadwalShalat == null) {
            // Handle jika jadwal shalat belum didapat, mungkin set default
            countTime = 0;
            binding.txtViewSholat.setText("Memuat...");
            return;
        }

        if (jadwalShalat.equals("Shalat Shubuh")){
            countTime = (jadwalHelper.getJmlWaktuDzuhur() - functionHelper.getSumWaktuDetik()) * functionHelper.getDetikKeMiliDetik();
            binding.txtViewSholat.setText("Dzuhur");
        } else if (jadwalShalat.equals("Shalat Dzuhur")){
            countTime = (jadwalHelper.getJmlWaktuAshar() - functionHelper.getSumWaktuDetik()) * functionHelper.getDetikKeMiliDetik();
            binding.txtViewSholat.setText("Ashar");
        } else if (jadwalShalat.equals("Shalat Ashar")){
            countTime = (jadwalHelper.getJmlWaktuMaghrib() - functionHelper.getSumWaktuDetik()) * functionHelper.getDetikKeMiliDetik();
            binding.txtViewSholat.setText("Maghrib");
        } else if (jadwalShalat.equals("Shalat Maghrib")){
            countTime = (jadwalHelper.getJmlWaktuIsya() - functionHelper.getSumWaktuDetik()) * functionHelper.getDetikKeMiliDetik();
            binding.txtViewSholat.setText("Isya");
        } else if (jadwalShalat.equals("Shalat Isya")){
            countTime = (jadwalHelper.getJmlWaktuShubuh() - functionHelper.getSumWaktuDetik()) * functionHelper.getDetikKeMiliDetik();
            binding.txtViewSholat.setText("Shubuh");
        } else {
            countTime = (jadwalHelper.getJmlWaktuDzuhur() - functionHelper.getSumWaktuDetik()) * functionHelper.getDetikKeMiliDetik();
            binding.txtViewSholat.setText("Dzuhur");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout menggunakan View Binding
        binding = FragmentJadwalBinding.inflate(inflater, container, false);
        // Kembalikan root view dari binding
        return binding.getRoot();
    }

    // PINDAHKAN SEMUA LOGIKA VIEW KE onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // HAPUS SEMUA findViewById dari onCreateView

        // -----------------------------------------------------------------------------------------
        // Panggil helper SETELAH view pasti ada
        CekJadwal();

        // Gunakan 'binding' untuk mengakses semua TextView
        jadwalHelper.setTimeOnline(
                binding.txtWaktuShubuh,
                binding.txtWaktuDzuhur,
                binding.txtWaktuAshar,
                binding.txtWaktuMaghrib,
                binding.txtWaktuIsya
        );
        jadwalHelper.CoundownTime(countTime, binding.countDown);
        // -----------------------------------------------------------------------------------------
    }

    // TAMBAHKAN ONDESTROYVIEW untuk membersihkan binding
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Set binding ke null untuk menghindari memory leak
    }
}