package id.duglegir.jagosholat.util;

import android.content.Context;
import androidx.annotation.NonNull; // Import baru
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
// import androidx.fragment.app.FragmentPagerAdapter; // HAPUS IMPORT LAMA
import androidx.viewpager2.adapter.FragmentStateAdapter; // <-- IMPORT BARU

// Import semua fragment Anda
import id.duglegir.jagosholat.ui.tutorial.FeatureWudhuFragment;
import id.duglegir.jagosholat.ui.tutorial.FeatureNiatFragment;
import id.duglegir.jagosholat.ui.tutorial.FeatureShalatFragment;
import id.duglegir.jagosholat.ui.tutorial.FeatureDoaFragment;
import id.duglegir.jagosholat.R;


public class FeaturePagerAdapter extends FragmentStateAdapter { // <-- GANTI EXTENDS

    private Context mContext;

    // 1. BUAT CONSTRUCTOR BARU (menerima Fragment)
    // Constructor lama (HAPUS): public FeaturePagerAdapter(Context context, FragmentManager fm ) { ... }
    public FeaturePagerAdapter(Fragment fragment, Context context) {
        super(fragment);
        mContext = context;
    }

    // 2. GANTI getItem() menjadi createFragment()
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0 :
                return new FeatureWudhuFragment();
            case 1 :
                return new FeatureNiatFragment();
            case 2 :
                return new FeatureShalatFragment();
            case 3 :
                return new FeatureDoaFragment();
            default:
                // Seharusnya tidak pernah terjadi, tapi aman untuk memiliki fallback
                return new Fragment();
        }
    }

    // 3. GANTI getCount() menjadi getItemCount()
    @Override
    public int getItemCount() {
        return 4; // Jumlah total tab Anda
    }

    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return mContext.getString(R.string.btn_tutor_wudhu);
            case 1 :
                return mContext.getString(R.string.btn_niat_sholat);
            case 2 :
                return mContext.getString(R.string.btn_tutor_sholat);
            case 3 :
                return mContext.getString(R.string.btn_doa);
            default:
                return null;
        }
    }
}