package travel.ilave.deepdivescanner.ui.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import travel.ilave.deepdivescanner.R;

/**
 * Created by lashket on 4.3.16.
 */
public class SLiderImagesFragment extends Fragment {

    public static final String IMAGE_URL = "IMAGE_URL";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String imageUrl = getArguments().getString(IMAGE_URL);
        View view = inflater.inflate(R.layout.slider_image_fragment, container, false);

        ImageView imageView = (ImageView)view.findViewById(R.id.slider_image);
        //ameLayout frameLayout = (FrameLayout) view.findViewById(R.id.framelayout_slider);
        Picasso.with(getActivity()).load(imageUrl).into(imageView);
      /*  Picasso.with(getActivity()).load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Drawable drawable = new BitmapDrawable(bitmap);
                drawable.setColorFilter(Color.parseColor("#99000000"), PorterDuff.Mode.SRC_ATOP);
                frameLayout.setBackgroundDrawable(drawable);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });*/
        return view;
    }

}
