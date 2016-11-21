package com.ddscanner.screens.divespot.details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Photo;
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.ImageLoadedCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiveSpotPhotosAdapter extends RecyclerView.Adapter<DiveSpotPhotosAdapter.DiveSpotsPhotosAdapterViewHolder>{

    public ArrayList<Photo> photos;
    public String path;
    public Context context;
    private int photoSize;

    public DiveSpotPhotosAdapter(ArrayList<Photo> photos, Context context) {
        this.photos = photos;
        this.context = context;
        photoSize = (int) context.getResources().getDimension(R.dimen.image_in_divespot_small);
    }

    @Override
    public DiveSpotsPhotosAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.dive_spot_photos_list_item, parent, false);
        return new DiveSpotsPhotosAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DiveSpotsPhotosAdapterViewHolder holder, int position) {
        if (photos.size() > 8 && position == 7) {
            Picasso.with(context)
                    .load(DDScannerApplication.getInstance().getString(R.string.server_api_address) + Constants.IMAGE_PATH_PREVIEW + photos.get(position).getUrl())
                    .transform(new TransformationRoundImage(Math.round(Helpers.convertDpToPixel(2, context)),0))
                    .resize(Math.round(Helpers.convertDpToPixel(photoSize, context)),Math.round(Helpers.convertDpToPixel(photoSize, context)))
                    .centerCrop()
                    .into(holder.photo);
            holder.morePhotos.setText("+" + String.valueOf(photos.size() - 8));
            holder.morePhotos.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(context)
                    .load(DDScannerApplication.getInstance().getString(R.string.server_api_address) + Constants.IMAGE_PATH_PREVIEW + photos.get(position).getUrl())
                    .transform(new TransformationRoundImage(Math.round(Helpers.convertDpToPixel(2, context)),0))
                    .resize(Math.round(Helpers.convertDpToPixel(photoSize, context)),Math.round(Helpers.convertDpToPixel(photoSize, context)))
                    .centerCrop()
                    .into(holder.photo, new ImageLoadedCallback(holder.progressBar){
                        @Override
                        public void onSuccess() {
                            if (holder.progressBar != null) {
                                holder.progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        if (photos == null) {
            return 0;
        }
        if (photos.size() > 8) {
            return 8;
        }
        return photos.size();
    }

    public class DiveSpotsPhotosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView photo;
        protected TextView morePhotos;
        protected ProgressBar progressBar;

        public DiveSpotsPhotosAdapterViewHolder(View v) {
            super(v);
            photo = (ImageView) v.findViewById(R.id.image);
            photo.setOnClickListener(this);
            morePhotos = (TextView) v.findViewById(R.id.number_of_more_images);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
//            DiveSpotPhotosActivity.showForResult(context, photos, path, reviewsImages);
            DDScannerApplication.bus.post(new OpenPhotosActivityEvent());
        }
    }

}
