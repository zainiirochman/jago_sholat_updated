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
// import android.widget.ListView; // Tidak diperlukan lagi
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

// import java.util.ArrayList; // Tidak diperlukan lagi

import id.duglegir.jagosholat.util.FunctionHelper;
// import id.duglegir.jagosholat.model.StatistikWord; // Tidak diperlukan lagi
import id.duglegir.jagosholat.model.DataContract;
import id.duglegir.jagosholat.model.DataOperation;
import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.databinding.FragmentStatistikSemuaBinding; // <-- IMPORT BARU UNTUK VIEW BINDING


public class StatistikSemuaFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // ---------------------------------------------------------------------------------------------
    private static final int DATA_LOADER = 0;
    // HAPUS arrayWord. Kita akan ambil data dari cursor.
    // private ArrayList<StatistikWord> arrayWord = new ArrayList<>();
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Deklarasi XML Alert Dialog
    private AlertDialog.Builder dialog;
    // private LayoutInflater inflater; // Tidak perlu jadi variabel global
    private View dialogView;
    private TextView txt_waktu;
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Deklarasi Class Helper yang diperlukan
    private FunctionHelper functionHelper = new FunctionHelper();
    private DataOperation crud = new DataOperation();
    private StatistikSemuaCursorAdapter mCursorAdapter;
    // ---------------------------------------------------------------------------------------------

    // Deklarasi View Binding
    private FragmentStatistikSemuaBinding binding;

    public StatistikSemuaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout HANYA dengan binding
        binding = FragmentStatistikSemuaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // PINDAHKAN SEMUA LOGIKA VIEW KE onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // HAPUS SEMUA findViewById

        // -----------------------------------------------------------------------------------------
        // Setup ListView menggunakan binding
        binding.listViewStatistik.setEmptyView(binding.emptyViews);
        mCursorAdapter = new StatistikSemuaCursorAdapter(requireContext(), null);
        binding.listViewStatistik.setAdapter(mCursorAdapter);

        // HAPUS PANGGILAN getDataFromTable() YANG BERBAHAYA
        // -----------------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------------
        binding.listViewStatistik.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // CARA BARU YANG AMAN: Ambil data dari cursor di adapter
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                // Ambil data dari cursor
                int idColoumnIndex = cursor.getColumnIndex(DataContract.DataEntry._ID);
                int waktuColoumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_WAKTU);
                String getArrayId = cursor.getString(idColoumnIndex);
                String getArrayWaktu = cursor.getString(waktuColoumnIndex);

                DialogForm(getArrayId, getArrayWaktu);
                // ---------------------------------------------------------------------------------
            }
        });
        // -----------------------------------------------------------------------------------------

        // Mulai Loader di sini (menggantikan onActivityCreated)
        LoaderManager.getInstance(this).initLoader(DATA_LOADER, null, this);
    }


    // ---------------------------------------------------------------------------------------------
    // Pop Up update waktu
    private void DialogForm(final String mID, String mWaktu) {

        // -----------------------------------------------------------------------------------------
        // Deklarasi Element XML Update View
        dialog = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater(); // Dapatkan inflater dari activity
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
                String selection = DataContract.DataEntry._ID + " = '" + mID + "'";
                // ---------------------------------------------------------------------------------

                // ---------------------------------------------------------------------------------
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

    // HAPUS onActivityCreated() yang usang
    // @Override
    // public void onActivityCreated(@Nullable Bundle savedInstanceState) { ... }

    @NonNull // Tambahkan @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) { // Tambahkan @Nullable
        return new CursorLoader(requireActivity(), // Gunakan requireActivity()
                DataContract.DataEntry.CONTENT_URI,
                crud.getProjection(),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) { // Tambahkan @NonNull
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { // Tambahkan @NonNull
        mCursorAdapter.swapCursor(null);
    }

    // TAMBAHKAN ONDESTROYVIEW untuk membersihkan binding
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Set binding ke null untuk menghindari memory leak
    }
}