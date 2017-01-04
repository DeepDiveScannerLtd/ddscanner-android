package com.ddscanner.screens.divespot.photos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.screens.divespot.photos.AllPhotosDiveSpotAdapter;

import java.util.ArrayList;

/**
 * Created by lashket on 11.5.16.
 */
public class DiveSpotReviewsPhotoFragment extends Fragment {

    private ArrayList<DiveSpotPhoto> images;

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews_photo, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.photos);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(3));
        recyclerView.setAdapter(new AllPhotosDiveSpotAdapter(images, getActivity(), PhotoOpenedSource.REVIEWS, false));
        return view;
    }

    public void setList(ArrayList<DiveSpotPhoto> images) {
        if (recyclerView == null) {
            this.images = images;
            return;
        }
        recyclerView.setAdapter(new AllPhotosDiveSpotAdapter(images, getActivity(), PhotoOpenedSource.REVIEWS, false));
    }
}
