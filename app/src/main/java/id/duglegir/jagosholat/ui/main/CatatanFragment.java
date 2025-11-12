package id.duglegir.jagosholat.ui.main;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull; // Import baru
import androidx.annotation.Nullable; // Import baru
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Import ini tidak lagi digunakan
import android.widget.TextView; // Import ini tidak lagi digunakan
import android.widget.Toast;

import java.util.Random;

import id.duglegir.jagosholat.util.FunctionHelper;
import id.duglegir.jagosholat.util.JadwalHelper;
import id.duglegir.jagosholat.model.DataOperation;
import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.databinding.FragmentCatatanBinding; // <-- IMPORT BARU UNTUK VIEW BINDING


public class CatatanFragment extends Fragment {

    // ---------------------------------------------------------------------------------------------
    // Deklarasi Class Helper Buatan Sendiri
    private FunctionHelper functionHelper = new FunctionHelper();
    private JadwalHelper jadwalHelper = new JadwalHelper();
    private DataOperation crud = new DataOperation();
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Deklarasi Requirement Variable
    private String cekid;
    private final String bukanWaktuSholat = "Belum Masuk Waktu Sholat";
    private String[] mHadistArab = {"hadis_arab_0","hadis_arab_1","hadis_arab_2","hadis_arab_3","hadis_arab_4","hadis_arab_5"};
    private String[] mHadistText = {"hadis_text_0","hadis_text_1","hadis_text_2","hadis_text_3","hadis_text_4","hadis_text_5"};
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Deklarasi View Binding (MENGGANTIKAN DEKLARASI ELEMENT XML)
    private FragmentCatatanBinding binding;
    // ---------------------------------------------------------------------------------------------

    private String isi_tanggal, isi_sholat, isi_waktu, isi_status;
    private String id_ibadah;

    public CatatanFragment() {
        // Required empty public constructor
    }

    // Cek di dalam table belum ada data sama sekali -----------------------------------------------
    public boolean isEmptyRowTable() {
        Cursor res = null;
        try {
            // Gunakan requireContext() untuk konteks yang aman (tidak null)
            res = crud.getDataTanggalJenis(requireContext(), isi_tanggal, isi_sholat);
            int cek = res.getCount();
            return cek == 0;
        } finally {
            // PERBAIKAN: Cek null sebelum menutup cursor untuk menghindari crash
            if (res != null) {
                res.close();
            }
        }
    }
    // ---------------------------------------------------------------------------------------------

    // Method untuk mengecek apakah data sudah terisi / dengan cara search di database -------------
    public String cekDataSudahAda() {
        Cursor res = crud.getDataTanggalJenis(requireContext(), isi_tanggal, isi_sholat);
        try{
            while (res.moveToNext()) {
                cekid = res.getString(2);
            }
        } finally {
            // PERBAIKAN: Cek null sebelum menutup cursor
            if (res != null) {
                res.close();
            }
        }
        return cekid;
    }
    // ---------------------------------------------------------------------------------------------

    // Method langsung isi dalam database ----------------------------------------------------------
    public void insertDataToDatabase() {
        boolean isInserted = crud.insertData(requireContext(), id_ibadah, isi_tanggal, isi_sholat, isi_waktu, isi_status);
        if (isInserted) {
            Toast.makeText(requireContext(), "Alhamdullilah " + isi_sholat, Toast.LENGTH_LONG).show();
            binding.btnSimpan.setVisibility(View.GONE); // Gunakan binding
        } else {
            Toast.makeText(requireContext(), "Data Not Inserted", Toast.LENGTH_LONG).show();
        }
    }
    // ---------------------------------------------------------------------------------------------

    // Method untuk menyimpan data ketika button "Simpan" di tekan ---------------------------------
    public void addData(String mShalat) {
        try {
            if (isEmptyRowTable()) {
                if (mShalat.equals(bukanWaktuSholat)) {
                    Toast.makeText(requireContext(), bukanWaktuSholat, Toast.LENGTH_LONG).show();
                } else {
                    insertDataToDatabase();
                }
            } else {
                if (cekDataSudahAda().equals(mShalat)) {
                    Toast.makeText(requireContext(), "Data Sudah Tercatat", Toast.LENGTH_LONG).show();
                } else if (mShalat.equals(bukanWaktuSholat)) {
                    Toast.makeText(requireContext(), bukanWaktuSholat, Toast.LENGTH_LONG).show();
                } else {
                    insertDataToDatabase();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Mengatur Tombol
    public void tampilanButtonSimpan(String mShalat){
        // Gunakan binding untuk mengakses btn_simpan
        if (mShalat.equalsIgnoreCase(bukanWaktuSholat)){
            binding.btnSimpan.setVisibility(View.GONE);
        } else {
            if (isEmptyRowTable()){
                binding.btnSimpan.setVisibility(View.VISIBLE);
            } else {
                if (!cekDataSudahAda().equals(mShalat)){
                    binding.btnSimpan.setVisibility(View.VISIBLE);
                } else {
                    binding.btnSimpan.setVisibility(View.GONE);
                }
            }
        }
    }
    // ---------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout menggunakan View Binding
        binding = FragmentCatatanBinding.inflate(inflater, container, false);
        // Kembalikan root view dari binding
        return binding.getRoot();
    }

    // Gunakan onViewCreated untuk semua logika yang berinteraksi dengan view
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // HAPUS SEMUA findViewById, kita sekarang menggunakan 'binding'

        // Set tampilan tanggal dan waktu ----------------------------------------------------------
        functionHelper.getSystemTime();
        functionHelper.getSystemRealTime();
        functionHelper.getSumRealTime();
        // Gunakan binding untuk mengakses txt_sholat
        jadwalHelper.setJadwalShalat(binding.txtSholat);
        // Gunakan binding untuk mengakses txt_tanggal
        binding.txtTanggal.setText(functionHelper.getDateToday());
        // -----------------------------------------------------------------------------------------

        // Get Data dari XML Layout (menggunakan binding) -----------------------------------------
        isi_sholat = binding.txtSholat.getText().toString();
        isi_waktu = functionHelper.getOutputStringTime();
        isi_tanggal = functionHelper.getDateToday();
        isi_status = "Shalat";
        // -----------------------------------------------------------------------------------------

        // Set Data Random Hadist untuk XML Layout (menggunakan binding) --------------------------
        Random randomInt = new Random();
        int maxRandom = mHadistArab.length - 1;
        int minRandom = 0;
        int getIndexArrayHadis = randomInt.nextInt(maxRandom - minRandom + 1) + minRandom;

        // Gunakan requireContext() untuk mendapatkan package name
        int mResIdHadistArab = getResources().getIdentifier(mHadistArab[getIndexArrayHadis],"string", requireContext().getPackageName());
        int mResIdHadistText = getResources().getIdentifier(mHadistText[getIndexArrayHadis],"string", requireContext().getPackageName());

        binding.txtHadistArab.setText(mResIdHadistArab);
        binding.txtHadistText.setText(mResIdHadistText);
        // -----------------------------------------------------------------------------------------

        // Panggil method untuk mencatat -----------------------------------------------------------
        id_ibadah = "IDS" + functionHelper.getRandomChar();

        tampilanButtonSimpan(isi_sholat);

        // Gunakan binding untuk setOnClickListener
        binding.btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData(isi_sholat);
            }
        });
        // -----------------------------------------------------------------------------------------
    }

    // TAMBAHKAN onDestroyView untuk membersihkan binding (mencegah memory leak)
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}