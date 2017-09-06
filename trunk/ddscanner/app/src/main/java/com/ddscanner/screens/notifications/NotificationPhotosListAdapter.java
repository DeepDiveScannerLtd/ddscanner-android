package com.ddscanner.screens.notifications;

import android.app.Activity;
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
import com.ddscanner.screens.photo.slider.ImageSliderActivity;
import com.ddscanner.ui.activities.PhotosGalleryActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationPhotosListAdapter extends RecyclerView.Adapter<NotificationPhotosListAdapter.NotificationPhotoViewHolder> {

    private ArrayList<DiveSpotPhoto> photos = new ArrayList<>();
    private Activity context;
    private int photosCount;
    private static final int MAX_PHOTOS = 6;
    private String sourceId;

    public NotificationPhotosListAdapter(Activity context) {
        this.context = context;
    }

    public void setData(ArrayList<DiveSpotPhoto> photos, int photosCount, String sourceId) {
        this.photos = photos;
        this.photosCount = photosCount;
        this.sourceId = sourceId;
        notifyDataSetChanged();
    }

    @Override
    public NotificationPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificationPhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_photo_list, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationPhotoViewHolder holder, int position) {
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photos.get(position).getId(), "1")).placeholder(R.drawable.placeholder_photo_wit_round_corners).into(holder.photo);
        if (position == MAX_PHOTOS - 1 && photosCount > MAX_PHOTOS) {
            holder.morePhotos.setText(DDScannerApplication.getInstance().getString(R.string.plus_photos_pattern, String.valueOf(photosCount - MAX_PHOTOS + 1)));
            holder.morePhotos.setVisibility(View.VISIBLE);
        } else {
            holder.morePhotos.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class NotificationPhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView photo;
        TextView morePhotos;

        NotificationPhotoViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            photo = view.findViewById(R.id.image);
            morePhotos = view.findViewById(R.id.number_of_more_images);
        }

        @Override
        public void onClick(View view) {
            if (MAX_PHOTOS <= photosCount && getAdapterPosition() == MAX_PHOTOS - 1) {
                PhotosGalleryActivity.show(sourceId, context, PhotoOpenedSource.NOTIFICATION, null);
                return;
            }
            DDScannerApplication.getInstance().getDiveSpotPhotosContainer().setPhotos(photos);
            ImageSliderActivity.showForResult(context, photos, getAdapterPosition(), -1, PhotoOpenedSource.NOTIFICATION, sourceId);
        }
    }

}
