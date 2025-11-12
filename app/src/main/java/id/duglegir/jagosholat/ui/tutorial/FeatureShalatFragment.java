package id.duglegir.jagosholat.ui.tutorial;

import android.os.Bundle;
import androidx.annotation.NonNull; // Import baru
import androidx.annotation.Nullable; // Import baru
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Import ini tidak diperlukan lagi (diganti binding)

import com.github.chrisbanes.photoview.PhotoView; // Import ini tidak diperlukan lagi

import id.duglegir.jagosholat.R;
// IMPORT BINDING (berdasarkan R.layout.fragment_feature_tutor_image)
import id.duglegir.jagosholat.databinding.FragmentFeatureTutorImageBinding;


public class FeatureShalatFragment extends Fragment {

    // 1. Deklarasi View Binding
    private FragmentFeatureTutorImageBinding binding;

    // ---------------------------------------------------------------------------------------------
    // Variabel Anda tetap sama, tidak perlu diubah
    private String imageResId[] = {"sholat_0","sholat_1","sholat_2","sholat_3","sholat_4","sholat_5","sholat_6","sholat_7","sholat_8","sholat_9","sholat_10"};
    private int imageRes, i=0;
    // ---------------------------------------------------------------------------------------------

    public FeatureShalatFragment() {
        // Required empty public constructor
    }

    public void setImage(int x){
        // Ganti getActivity() dengan requireContext()
        // Ganti imageContain dengan binding.photoView
        imageRes = getResources().getIdentifier(imageResId[x],"drawable", requireContext().getPackageName());
        binding.photoView.setImageResource(imageRes);
    }

    public void nextImage(){
        if (!(i == imageResId.length-1)) {
            i++;
            setImage(i);
        }
    }

    public void previousImage(){
        if (!(i == 0)) {
            i--;
            setImage(i);
            // Ganti next dengan binding.btnImageNext
            binding.btnImageNext.setVisibility(View.VISIBLE);
        }
    }

    // 2. Sederhanakan onCreateView
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout HANYA dengan binding
        binding = FragmentFeatureTutorImageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // 3. Pindahkan Semua Logika ke onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // -----------------------------------------------------------------------------------------
        // HAPUS SEMUA findViewById
        // -----------------------------------------------------------------------------------------

        // 4. Ganti Cara Mengakses Elemen (gunakan binding)
        binding.photoView.setImageResource(R.drawable.sholat_0);
        binding.photoView.setScaleType(ImageView.ScaleType.FIT_XY);

        binding.btnImageNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextImage();
            }
        });
        binding.btnImagePrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousImage();
            }
        });
    }

    // 5. Tambahkan onDestroyView
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Set binding ke null untuk menghindari memory leak
    }
}