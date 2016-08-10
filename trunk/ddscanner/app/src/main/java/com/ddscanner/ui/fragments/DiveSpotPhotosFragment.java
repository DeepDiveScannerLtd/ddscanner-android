package com.ddscanner.ui.fragments;

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
import com.ddscanner.entities.Image;
import com.ddscanner.ui.adapters.AllPhotosDiveSpotAdapter;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;

/**
 * Created by lashket on 11.5.16.
 */
public class DiveSpotPhotosFragment extends Fragment {

    private ArrayList<Image> images;

    private RecyclerView recyclerView;

    private Helpers helpers = new Helpers();

    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_divespot_photo, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.photos);
        Bundle bundle = getArguments();
        images = bundle.getParcelableArrayList("diveSpotImages");
        path = bundle.getString("path");
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3));
        recyclerView.setAdapter(new AllPhotosDiveSpotAdapter(images, getContext(), path));
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
                outRect.top = Math.round(helpers.convertDpToPixel(Float.valueOf(4), getContext()));
            }
        }
    }
}
