package travel.ilave.deepdivescanner.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import travel.ilave.deepdivescanner.ui.fragments.PlaceImageFragment;
import travel.ilave.deepdivescanner.ui.fragments.SLiderImagesFragment;

/**
 * Created by lashket on 4.3.16.
 */
public class SliderImagesAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> productsImages;
    private String path;

    public SliderImagesAdapter(FragmentManager fm, ArrayList<String> productsImages) {
        super(fm);

        this.productsImages = productsImages;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new SLiderImagesFragment();
        Bundle args = new Bundle();
        args.putString(PlaceImageFragment.IMAGE_URL,"http://www.trizeri.travel/images/divespots/medium/" + productsImages.get(position));
        fragment.setArguments(args);
        System.out.println(productsImages.size());
        return fragment;
    }

    @Override
    public  int getCount() {
        return productsImages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
