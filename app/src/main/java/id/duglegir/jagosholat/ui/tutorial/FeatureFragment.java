package id.duglegir.jagosholat.ui.tutorial;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator; // <-- IMPORT BARU

import androidx.annotation.NonNull; // Import baru
import androidx.annotation.Nullable; // Import baru
import androidx.fragment.app.Fragment;
// import androidx.viewpager.widget.ViewPager; // HAPUS IMPORT LAMA
import androidx.viewpager2.widget.ViewPager2; // <-- IMPORT BARU
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.duglegir.jagosholat.util.FeaturePagerAdapter;
import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.databinding.FragmentFeatureBinding; // <-- IMPORT BINDING

public class FeatureFragment extends Fragment {

    // 1. Deklarasi View Binding
    private FragmentFeatureBinding binding;
    private FeaturePagerAdapter featurePagerAdapter;

    public FeatureFragment() {
        // Required empty public constructor
    }

    // 2. Sederhanakan onCreateView
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout menggunakan binding
        binding = FragmentFeatureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // 3. Pindahkan semua logika ke onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // -----------------------------------------------------------------------------------------
        // Membuat ViewPager (SLIDER)
        // Gunakan constructor baru ('this' (Fragment) dan requireContext())
        featurePagerAdapter = new FeaturePagerAdapter(this, requireContext());
        binding.viewpagerFeature.setAdapter(featurePagerAdapter);
        // -----------------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------------
        // CARA BARU menghubungkan TabLayout ke ViewPager2
        new TabLayoutMediator(binding.tablayoutFeature, binding.viewpagerFeature,
                (tab, position) -> tab.setText(featurePagerAdapter.getPageTitle(position))
        ).attach();
        // -----------------------------------------------------------------------------------------
    }

    // 4. Tambahkan onDestroyView (wajib untuk fragment)
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Set binding ke null
    }
}