package com.ddscanner.ui.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.interfaces.PhotoItemCLickListener;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PhotosGridListAdapter extends RecyclerView.Adapter<PhotosGridListAdapter.PhotosGridViewHolder> {

    private int layoutId;
    private ArrayList<DiveSpotPhoto> photos = new ArrayList<>();
    private int photosCount;
    private int maxPhotosCount;
    private Context context;
    private PhotoItemCLickListener photoItemCLickListener;

    public PhotosGridListAdapter(int layoutId, ArrayList<DiveSpotPhoto> photos, int photosCount, int maxPhotosCount, PhotoItemCLickListener photoItemCLickListener) {
        this.layoutId = layoutId;
        this.photos = photos;
        this.photosCount = photosCount;
        this.maxPhotosCount = maxPhotosCount;
        this.photoItemCLickListener = photoItemCLickListener;
    }

    @Override
    public PhotosGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new PhotosGridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotosGridViewHolder holder, int position) {
        if (photosCount > maxPhotosCount && position == maxPhotosCount - 1) {
            Picasso.with(context)
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photos.get(position).getId(), "1"))
                    .transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)),0, RoundedCornersTransformation.CornerType.ALL))
                    .into(holder.photo);
            holder.morePhotos.setText(String.format("+%s", String.valueOf(photosCount - maxPhotosCount + 1)));
            holder.morePhotos.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(context)
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photos.get(position).getId(), "1"))
                    .transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)),0, RoundedCornersTransformation.CornerType.ALL))
                    .placeholder(R.drawable.placeholder_photo_wit_round_corners)
                    .error(R.drawable.placeholder_photo_wit_round_corners)
                    .into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotosGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView morePhotos;
        ShapedImageView photo;

        public PhotosGridViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
            photo = itemView.findViewById(R.id.image);
            morePhotos = itemView.findViewById(R.id.number_of_more_images);
        }

        @Override
        public void onClick(View view) {
            if (photosCount > maxPhotosCount && getAdapterPosition() == maxPhotosCount - 1) {
                photoItemCLickListener.onClick(true, getAdapterPosition());
                return;
            }
            photoItemCLickListener.onClick(false, getAdapterPosition());
    }
    }

}
