package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.activities.ShowImageActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lashket on 26.4.16.
 */
public class DiveSpotsPhotosAdapter extends RecyclerView.Adapter<DiveSpotsPhotosAdapter.DiveSpotsPhotosAdapterViewHolder> {

    public static ArrayList<String> photos;
    public static String path;
    public static Context context;

    public DiveSpotsPhotosAdapter(ArrayList<String> photos, String path, Context context) {
        this.photos = photos;
        this.path = path;
        this.context = context;
    }

    @Override
    public DiveSpotsPhotosAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.dive_spot_photos_list_item, parent, false);
        return new DiveSpotsPhotosAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DiveSpotsPhotosAdapterViewHolder holder, int position) {
        if (photos.size() > 8 && position == 7) {
            Picasso.with(context).load(path + photos.get(position)).transform(new TransformationRoundImage(2,0)).resize(70,70).centerCrop().into(holder.photo);
            holder.morePhotos.setText("+" + String.valueOf(photos.size() - 8));
            holder.morePhotos.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(context).load(path + photos.get(position)).transform(new TransformationRoundImage(2,0)).resize(70,70).centerCrop().into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        if (photos.size() > 8) {
            return 8;
        }
        return photos.size();
    }

    public static class DiveSpotsPhotosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView photo;
        protected TextView morePhotos;

        public DiveSpotsPhotosAdapterViewHolder(View v) {
            super(v);
            photo = (ImageView) v.findViewById(R.id.image);
            photo.setOnClickListener(this);
            morePhotos = (TextView) v.findViewById(R.id.number_of_more_images);
        }

        @Override
        public void onClick(View v) {
            if (photos.size() < 8 && getPosition()!= 7) {
                ShowImageActivity.show(context, path + photos.get(getPosition()));
            }
        }
    }

}
