package com.ddscanner.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.ddscanner.screens.divespot.details.DiveSpotPhotosAdapter;
import com.ddscanner.screens.photo.slider.ImageSliderActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.ImageLoadedCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class UserPhotosListAdapter extends RecyclerView.Adapter<UserPhotosListAdapter.UserPhotosViewHolder> {

    private ArrayList<DiveSpotPhoto> photos = new ArrayList<>();
    private int photosCount;
    private Activity context;
    private int photoSize;
    private String userId;

    public UserPhotosListAdapter(ArrayList<DiveSpotPhoto> photos, int photosCount, Activity context, String userId) {
        this.photos = photos;
        this.photosCount = photosCount;
        this.context = context;
        this.userId = userId;
        photoSize = 75;
    }

    @Override
    public UserPhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.dive_spot_photos_list_item, parent, false);
        return new UserPhotosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserPhotosViewHolder holder, int position) {
        if (photosCount > 4 && position == 3) {
            Picasso.with(context)
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photos.get(position).getId(), "1"))
                    .transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)),0, RoundedCornersTransformation.CornerType.ALL))
                    .into(holder.photo);
            holder.morePhotos.setText("+" + String.valueOf(photosCount - 3));
            holder.morePhotos.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(context)
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photos.get(position).getId(), "1"))
                    .transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)),0, RoundedCornersTransformation.CornerType.ALL))
                    .placeholder(R.drawable.placeholder_photo_wit_round_corners)
                    .error(R.drawable.ds_list_photo_default)
                    .into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class UserPhotosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView photo;
        protected TextView morePhotos;
        protected ProgressBar progressBar;

        public UserPhotosViewHolder(View v) {
            super(v);
            photo = (ImageView) v.findViewById(R.id.image);
            photo.setOnClickListener(this);
            morePhotos = (TextView) v.findViewById(R.id.number_of_more_images);
        }

        @Override
        public void onClick(View view) {
            if (photosCount > 4) {
                DDScannerApplication.bus.post(new OpenPhotosActivityEvent());
                return;
            }
            DDScannerApplication.getInstance().getDiveSpotPhotosContainer().setPhotos(photos);
            ImageSliderActivity.showForResult(context, photos, getAdapterPosition(), ActivitiesRequestCodes.REQUEST_CODE_SHOW_USER_PROFILE_PHOTOS, PhotoOpenedSource.PROFILE, userId);
        }
    }

}
