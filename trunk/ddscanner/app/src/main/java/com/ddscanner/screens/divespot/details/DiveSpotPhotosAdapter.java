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
import com.ddscanner.events.OpenPhotosActivityEvent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiveSpotPhotosAdapter extends RecyclerView.Adapter<DiveSpotPhotosAdapter.DiveSpotsPhotosAdapterViewHolder>{

    public ArrayList<String> photos;
    public String path;
    public Context context;
    private int photoSize;
    private int photosCount;

    public DiveSpotPhotosAdapter(ArrayList<String> photos, Context context, int photosCount) {
        this.photos = photos;
        this.context = context;
        photoSize = (int) context.getResources().getDimension(R.dimen.image_in_divespot_small);
        this.photosCount = photosCount;
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
        if (photosCount > 8 && position == 7) {
            Picasso.with(context)
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photos.get(position), "1"))
                    .placeholder(R.drawable.placeholder_photo_wit_round_corners)
                    .into(holder.photo);
            holder.morePhotos.setText("+" + String.valueOf(photosCount - 7));
            holder.morePhotos.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(context)
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photos.get(position), "1"))
                    .placeholder(R.drawable.placeholder_photo_wit_round_corners)
                    .into(holder.photo);
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

    public void addPhotos(ArrayList<String> newPhotos) {
        photos.addAll(newPhotos);
        notifyDataSetChanged();
    }

    class DiveSpotsPhotosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
