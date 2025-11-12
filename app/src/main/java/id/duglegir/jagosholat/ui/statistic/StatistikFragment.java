package id.duglegir.jagosholat.ui.statistic;

import android.os.Bundle;
import androidx.annotation.NonNull; // Import baru
import androidx.annotation.Nullable; // Import baru
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// import android.widget.Button; // Tidak diperlukan lagi

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

// Pastikan path import ini benar
import id.duglegir.jagosholat.ui.statistic.StatistikGrafikFragment;
import id.duglegir.jagosholat.ui.statistic.StatistikHarianFragment;
import id.duglegir.jagosholat.ui.statistic.StatistikSemuaFragment;
import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.databinding.FragmentStatistikBinding; // <-- IMPORT BARU UNTUK VIEW BINDING


public class StatistikFragment extends Fragment {

    // Deklarasi View Binding
    private FragmentStatistikBinding binding;

    public StatistikFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout menggunakan View Binding
        binding = FragmentStatistikBinding.inflate(inflater, container, false);
        // Kembalikan root view dari binding
        return binding.getRoot();
    }

    // PINDAHKAN SEMUA LOGIKA VIEW KE onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // HAPUS SEMUA findViewById

        // -----------------------------------------------------------------------------------------
        // Fragment Pertama yang di panggil yaitu Grafik
        // Panggil ini di onViewCreated agar yakin layout sudah ada
        StatistikGrafikFragment mStatistikGrafikFragment = new StatistikGrafikFragment();
        exchangeFragment(R.id.fragStatistik, mStatistikGrafikFragment);
        // -----------------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------------
        // Button pada Statistik Fragment (Gunakan binding)
        binding.btnStatHarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatistikHarianFragment mStatistikHarianFragment = new StatistikHarianFragment();
                exchangeFragment(R.id.fragStatistik, mStatistikHarianFragment);
            }
        });

        binding.btnStatGrafik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatistikGrafikFragment mStatistikGrafikFragment = new StatistikGrafikFragment();
                exchangeFragment(R.id.fragStatistik, mStatistikGrafikFragment);
            }
        });

        binding.btnStatSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatistikSemuaFragment mStatistikSemuaFragment = new StatistikSemuaFragment();
                exchangeFragment(R.id.fragStatistik, mStatistikSemuaFragment);
            }
        });
        // -----------------------------------------------------------------------------------------
    }


    // Method untuk pindah Fragment ----------------------------------------------------------------
    public void exchangeFragment(int frameLayout, Fragment mFragment){
        // PERBAIKAN KRITIS: Gunakan getChildFragmentManager()
        // Ini karena Anda mengganti Fragment di dalam Fragment ini sendiri
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(frameLayout, mFragment);
        ft.commit();
    }
    // ---------------------------------------------------------------------------------------------

    // TAMBAHKAN ONDESTROYVIEW untuk membersihkan binding
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Set binding ke null untuk menghindari memory leak
    }

    // Kelas static ini tidak perlu diubah, sudah benar.
    public static class MyXAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
        private String[] mValues;
        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }
}