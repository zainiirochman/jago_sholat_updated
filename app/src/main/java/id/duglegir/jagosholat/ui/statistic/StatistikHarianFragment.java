package id.duglegir.jagosholat.ui.statistic;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull; // Import baru
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager; // Import baru
import androidx.loader.content.CursorLoader; // Import baru
import androidx.loader.content.Loader; // Import baru
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList; // Import ini tidak lagi digunakan

import id.duglegir.jagosholat.util.FunctionHelper;
import id.duglegir.jagosholat.model.StatistikWord; // Import ini tidak lagi digunakan
import id.duglegir.jagosholat.model.DataContract.DataEntry;
import id.duglegir.jagosholat.model.DataOperation;
import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.databinding.FragmentStatistikHarianBinding; // <-- IMPORT BARU UNTUK VIEW BINDING

public class StatistikHarianFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    // ---------------------------------------------------------------------------------------------
    // Deklarasi Kebutuhan
    private static final int DATA_LOADER = 0;

    // HAPUS arrayWord. Ini tidak aman & duplikat data. Kita akan ambil data dari cursor.
    // private ArrayList<StatistikWord> arrayWord = new ArrayList<>();
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Deklarasi XML Alert Dialog
    private AlertDialog.Builder dialog;
    private LayoutInflater inflater; // Ini tidak perlu jadi variabel global
    private View dialogView;
    private TextView txt_waktu;
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Deklarasi Class Helper yang diperlukan
    private FunctionHelper functionHelper = new FunctionHelper();
    private DataOperation crud = new DataOperation();
    private StatistikHarianCursorAdapter mCursorAdapter;
    // ---------------------------------------------------------------------------------------------

    // Deklarasi View Binding
    private FragmentStatistikHarianBinding binding;

    public StatistikHarianFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout HANYA dengan binding
        binding = FragmentStatistikHarianBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // PINDAHKAN SEMUA LOGIKA VIEW KE onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // HAPUS SEMUA findViewById

        // Setup ListView menggunakan binding
        binding.listViewStatistik.setEmptyView(binding.emptyViews);
        mCursorAdapter = new StatistikHarianCursorAdapter(requireContext(), null);
        binding.listViewStatistik.setAdapter(mCursorAdapter);

        // HAPUS query di UI thread. Ini akan kita pindahkan ke onLoadFinished
        // mProgressBar.setProgress(getProgress());
        // getDataFromTable();
        // -----------------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------------
        binding.listViewStatistik.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // CARA BARU YANG AMAN: Ambil data dari cursor di adapter
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                // Ambil data dari cursor
                int idColoumnIndex = cursor.getColumnIndex(DataEntry._ID);
                int waktuColoumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_WAKTU);
                String getArrayId = cursor.getString(idColoumnIndex);
                String getArrayWaktu = cursor.getString(waktuColoumnIndex);

                DialogForm(getArrayId, getArrayWaktu);
                // ---------------------------------------------------------------------------------
            }
        });
        // -----------------------------------------------------------------------------------------

        // Mulai Loader di sini (menggantikan onActivityCreated)
        // Gunakan LoaderManager.getInstance(this) yang baru
        LoaderManager.getInstance(this).initLoader(DATA_LOADER, null, this);
    }

    // ---------------------------------------------------------------------------------------------
    // Pop Up update waktu
    private void DialogForm(final String mID, String mWaktu) {

        // -----------------------------------------------------------------------------------------
        // Deklarasi Element XML Update View
        // Gunakan requireActivity() untuk context yang aman
        dialog = new AlertDialog.Builder(requireActivity());
        inflater = requireActivity().getLayoutInflater(); // Dapatkan inflater dari activity
        dialogView = inflater.inflate(R.layout.content_statistik_update, null);
        txt_waktu = dialogView.findViewById(R.id.txt_waktu_update);
        // -----------------------------------------------------------------------------------------
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        txt_waktu.setText(mWaktu);
        // -----------------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------------
        // Set Waktu
        txt_waktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gunakan requireActivity() untuk context
                TimePickerDialog mTimePickerDialog = new TimePickerDialog(requireActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        functionHelper.getFormatTimePicker(txt_waktu, hourOfDay, minute);
                    }
                }, functionHelper.getSystemJam(), functionHelper.getSystemMenit(), true);

                mTimePickerDialog.show();
            }
        });
        // -----------------------------------------------------------------------------------------


        dialog.setPositiveButton("CATAT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ---------------------------------------------------------------------------------
                String tempWaktu = txt_waktu.getText().toString();
                String selection = DataEntry._ID + " = '" + mID + "'";
                // ---------------------------------------------------------------------------------

                // ---------------------------------------------------------------------------------
                // Gunakan requireContext()
                boolean isUpdated = crud.updateDataWaktu(requireContext(), tempWaktu, selection, null);
                if (isUpdated) {
                    Toast.makeText(requireContext(), "Waktu Telah Diubah", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireContext(), "Data Not Updadted", Toast.LENGTH_LONG).show();
                }
                // ---------------------------------------------------------------------------------
                dialog.dismiss(); // Keluar Dari Dialog
            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Keluar Dari Dialog
            }
        });

        dialog.show();
    }
    // ---------------------------------------------------------------------------------------------

    // HAPUS FUNGSI INI. Sangat berbahaya (query di UI thread) dan duplikat data.
    // public void getDataFromTable() { ... }
    // ---------------------------------------------------------------------------------------------

    // HAPUS FUNGSI INI. Sangat berbahaya (query di UI thread).
    // public int getProgress(){ ... }
    // ---------------------------------------------------------------------------------------------

    // HAPUS onActivityCreated() yang usang
    // @Override
    // public void onActivityCreated(@Nullable Bundle savedInstanceState) { ... }

    @NonNull // Tambahkan @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) { // Tambahkan @Nullable
        String selection = DataEntry.COLUMN_TANGGAL + " = '" + functionHelper.getDateToday() + "'";
        // Gunakan requireActivity() untuk context
        return new CursorLoader(requireActivity(),
                DataEntry.CONTENT_URI,
                crud.getProjection(),
                selection,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) { // Tambahkan @NonNull
        mCursorAdapter.swapCursor(data);

        // HITUNG DAN SET PROGRESS DI SINI (aman, di background thread)
        int countTable = (data != null) ? data.getCount() : 0;
        int progress = countTable * 20;

        // Pastikan binding masih ada sebelum diakses
        if (binding != null) {
            binding.statProgressBar.setProgress(progress);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { // Tambahkan @NonNull
        mCursorAdapter.swapCursor(null);
        // Reset progress juga
        if (binding != null) {
            binding.statProgressBar.setProgress(0);
        }
    }

    // TAMBAHKAN ONDESTROYVIEW untuk membersihkan binding
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Set binding ke null untuk menghindari memory leak
    }
}