package id.duglegir.jagosholat.ui.tutorial;

import android.os.Bundle;
import androidx.annotation.NonNull; // Import baru
import androidx.annotation.Nullable; // Import baru
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// import android.widget.ListView; // Tidak diperlukan lagi

import java.util.ArrayList;

import id.duglegir.jagosholat.util.JSONHelper;
import id.duglegir.jagosholat.model.NiatShalat;
import id.duglegir.jagosholat.R;
// IMPORT BINDING (berdasarkan R.layout.fragment_feature_tutor_text)
import id.duglegir.jagosholat.databinding.FragmentFeatureTutorTextBinding;


public class FeatureNiatFragment extends Fragment {

    // 1. Deklarasi View Binding
    // Fragment ini menggunakan layout yang sama dengan FeatureDoaFragment,
    // jadi nama binding-nya juga sama.
    private FragmentFeatureTutorTextBinding binding;

    public FeatureNiatFragment() {
        // Required empty public constructor
    }

    // 2. Sederhanakan onCreateView
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout HANYA dengan binding
        binding = FragmentFeatureTutorTextBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // 3. Pindahkan Semua Logika ke onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // -----------------------------------------------------------------------------------------
        ArrayList<NiatShalat> arrayWords = JSONHelper.extractNiatShalat();
        // -----------------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------------
        // 4. Ganti Cara Mengakses Elemen
        // Ganti mListView menjadi binding.listViewFeature
        // Ganti getActivity() dengan requireContext()
        NiatShalatAdapter call = new NiatShalatAdapter(requireContext(), arrayWords);
        binding.listViewFeature.setAdapter(call);
        // -----------------------------------------------------------------------------------------
    }

    // 5. Tambahkan onDestroyView
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Set binding ke null untuk menghindari memory leak
    }
}