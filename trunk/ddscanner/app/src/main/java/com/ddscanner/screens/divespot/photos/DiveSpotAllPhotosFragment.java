package com.ddscanner.screens.divespot.photos;

import android.graphics.Rect;
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
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;

public class DiveSpotAllPhotosFragment extends Fragment {

    private ArrayList<DiveSpotPhoto> images;

    private RecyclerView recyclerView;

    private String diveSpotId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_photos, container, false);
        recyclerView = view.findViewById(R.id.photos);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
       // recyclerView.addItemDecoration(new GridSpacingItemDecoration(3));
        recyclerView.setAdapter(new AllPhotosDiveSpotAdapter(images, getActivity(), PhotoOpenedSource.ALL, diveSpotId));
        return view;
    }


    public void setList(ArrayList<DiveSpotPhoto> images, String diveSpotId) {
        if (recyclerView == null) {
            this.diveSpotId = diveSpotId;
            this.images = images;
            return;
        }
        this.diveSpotId = diveSpotId;
        recyclerView.setAdapter(new AllPhotosDiveSpotAdapter(images, getActivity(), PhotoOpenedSource.ALL, diveSpotId));
    }
}
