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
import com.ddscanner.screens.divespot.photos.AllPhotosDiveSpotAdapter;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;

/**
 * Created by lashket on 11.5.16.
 */
public class DiveSpotAllPhotosFragment extends Fragment {

    private ArrayList<DiveSpotPhoto> images;

    private RecyclerView recyclerView;

    private String path;

    private String diveSpotId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_photos, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.photos);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
       // recyclerView.addItemDecoration(new GridSpacingItemDecoration(3));
        recyclerView.setAdapter(new AllPhotosDiveSpotAdapter(images, getActivity(), diveSpotId, PhotoOpenedSource.ALL, false));
        return view;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;

        public GridSpacingItemDecoration(int spanCount) {
            this.spanCount = spanCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position >= spanCount) {
                outRect.top = Math.round(Helpers.convertDpToPixel(Float.valueOf(4), getContext()));
            }
        }
    }

    public void setList(ArrayList<DiveSpotPhoto> images, String path, String diveSpotId) {
        this.path = path;
        this.diveSpotId = diveSpotId;
        if (recyclerView == null) {
            this.images = images;
            return;
        }
        recyclerView.setAdapter(new AllPhotosDiveSpotAdapter(images, getActivity(), this.diveSpotId, PhotoOpenedSource.ALL, false));
    }
}
