package travel.ilave.deepdivescanner.ui.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import travel.ilave.deepdivescanner.R;


/**
 * Created by Vitaly on 28.11.2015.
 */
public class PlaceImageFragment extends Fragment {

    public static final String IMAGE_URL = "IMAGE_URL";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String imageUrl = getArguments().getString(IMAGE_URL);

        SimpleDraweeView imageView = (SimpleDraweeView) inflater.inflate(R.layout.fragment_image, container, false);
       // ImageView imageView = (ImageView) inflater.inflate(R.layout.fragment_image, container, false);
        Uri uri = Uri.parse(imageUrl);
        imageView.setImageURI(uri);
     //   Picasso.with(getActivity()).load(imageUrl).into(imageView);
        return imageView;
    }
}
