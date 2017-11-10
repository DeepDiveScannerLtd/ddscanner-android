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

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class DiveSpotAllPhotosFragment extends Fragment {

    DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>> photosResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotPhoto> result) {
            recyclerView.setAdapter(new AllPhotosDiveSpotAdapter(result, getActivity(), PhotoOpenedSource.DIVESPOT, diveSpotId));
            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {

        }
    };

    private ArrayList<DiveSpotPhoto> images;

    private RecyclerView recyclerView;

    private String diveSpotId;

    private ProgressView progressView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews_photo, container, false);
        recyclerView = view.findViewById(R.id.photos);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
       // recyclerView.addItemDecoration(new GridSpacingItemDecoration(3));
        recyclerView.setAdapter(new AllPhotosDiveSpotAdapter(images, getActivity(), PhotoOpenedSource.ALL, diveSpotId));
        progressView = view.findViewById(R.id.progress_view);
        return view;
    }

    public void loadPhotos(String diveSpotId) {
        this.diveSpotId = diveSpotId;
        DDScannerApplication.getInstance().getDdScannerRestClient(getActivity()).getAllDiveSpotPhotos(photosResultListener, diveSpotId);
    }

}
