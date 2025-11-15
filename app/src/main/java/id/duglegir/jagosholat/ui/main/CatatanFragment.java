package id.duglegir.jagosholat.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Map;
import java.util.Random;

import id.duglegir.jagosholat.util.FunctionHelper;
import id.duglegir.jagosholat.util.JadwalHelper;
import id.duglegir.jagosholat.model.DataOperation;
import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.databinding.FragmentCatatanBinding;


public class CatatanFragment extends Fragment {
    private FunctionHelper functionHelper = new FunctionHelper();
    private JadwalHelper jadwalHelper = new JadwalHelper();
    private DataOperation crud = new DataOperation();
    private String cekid;
    private final String bukanWaktuSholat = "Belum Masuk Waktu Sholat";
    private String[] mHadistArab = {"hadis_arab_0","hadis_arab_1","hadis_arab_2","hadis_arab_3","hadis_arab_4","hadis_arab_5"};
    private String[] mHadistText = {"hadis_text_0","hadis_text_1","hadis_text_2","hadis_text_3","hadis_text_4","hadis_text_5"};
    private FragmentCatatanBinding binding;
    private String isi_tanggal, isi_sholat, isi_waktu, isi_status;
    private String id_ibadah;

    private FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude = -6.1744;  
    private double currentLongitude = 106.8294; 

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), (Map<String, Boolean> isGrantedMap) -> {
                Boolean fineGranted = isGrantedMap.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarseGranted = isGrantedMap.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                if ((fineGranted != null && fineGranted) || (coarseGranted != null && coarseGranted)) {
                    
                    startGpsFetchAndRefresh();
                } else {
                    
                    refreshContent();
                }
            });

    public CatatanFragment() {}

    public boolean isEmptyRowTable() {
        Cursor res = null;
        try {

            res = crud.getDataTanggalJenis(requireContext(), isi_tanggal, isi_sholat);
            int cek = res.getCount();
            return cek == 0;
        } finally {

            if (res != null) {
                res.close();
            }
        }
    }


    public String cekDataSudahAda() {
        Cursor res = crud.getDataTanggalJenis(requireContext(), isi_tanggal, isi_sholat);
        try{
            while (res.moveToNext()) {
                cekid = res.getString(2);
            }
        } finally {

            if (res != null) {
                res.close();
            }
        }
        return cekid;
    }


    public void insertDataToDatabase() {
        boolean isInserted = crud.insertData(requireContext(), id_ibadah, isi_tanggal, isi_sholat, isi_waktu, isi_status);
        if (isInserted) {
            Toast.makeText(requireContext(), "Alhamdullilah " + isi_sholat, Toast.LENGTH_LONG).show();
            binding.btnSimpan.setVisibility(View.GONE); 
        } else {
            Toast.makeText(requireContext(), "Data Not Inserted", Toast.LENGTH_LONG).show();
        }
    }


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

    public void tampilanButtonSimpan(String mShalat){

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCatatanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        binding.btnSimpan.setOnClickListener(v -> {
            addData(isi_sholat);
        });

        binding.swipeRefreshCatatan.setOnRefreshListener(() -> {
            
            checkAndRequestPermissions();
        });

        
        checkAndRequestPermissions();
    }

    
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            
            startGpsFetchAndRefresh();
        } else {
            
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @SuppressLint("MissingPermission") 
    private void startGpsFetchAndRefresh() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                    }
                    
                    refreshContent();
                })
                .addOnFailureListener(e -> {
                    
                    refreshContent();
                });
    }

    private void refreshContent(){
        if (!binding.swipeRefreshCatatan.isRefreshing()) {
            binding.swipeRefreshCatatan.setRefreshing(true);
        }

        functionHelper.getSystemTime();
        functionHelper.getSystemRealTime();
        functionHelper.getSumRealTime();
        jadwalHelper.setJadwalShalat(binding.txtSholat, currentLatitude, currentLongitude);
        binding.txtTanggal.setText(functionHelper.getDateToday());

        isi_sholat = binding.txtSholat.getText().toString();
        isi_waktu = functionHelper.getOutputStringTime();
        isi_tanggal = functionHelper.getDateToday();
        isi_status = "Shalat";

        Random randomInt = new Random();
        int maxRandom = mHadistArab.length - 1;
        int minRandom = 0;
        int getIndexArrayHadis = randomInt.nextInt(maxRandom - minRandom + 1) + minRandom;

        int mResIdHadistArab = getResources().getIdentifier(mHadistArab[getIndexArrayHadis],"string", requireContext().getPackageName());
        int mResIdHadistText = getResources().getIdentifier(mHadistText[getIndexArrayHadis],"string", requireContext().getPackageName());
        binding.txtHadistArab.setText(mResIdHadistArab);
        binding.txtHadistText.setText(mResIdHadistText);

        id_ibadah = "IDS" + functionHelper.getRandomChar();

        tampilanButtonSimpan(isi_sholat);

        binding.swipeRefreshCatatan.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}