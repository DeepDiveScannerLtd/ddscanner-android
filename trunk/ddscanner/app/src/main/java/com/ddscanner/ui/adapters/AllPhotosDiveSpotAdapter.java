package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.R;
import com.ddscanner.ui.activities.ImageSliderActivity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lashket on 11.5.16.
 */
public class AllPhotosDiveSpotAdapter extends RecyclerView.Adapter<AllPhotosDiveSpotAdapter.AllPhotosDIveSpotViewHolder> {

    private String path;
    private ArrayList<String> images;
    private Context context;
    private Helpers helpers = new Helpers();

    public AllPhotosDiveSpotAdapter(ArrayList<String> photos, Context context) {
        images = photos;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(AllPhotosDIveSpotViewHolder holder, int position) {
        Picasso.with(context)
                .load(images.get(position))
                .resize(Math.round(helpers.convertDpToPixel(115, context)),Math.round(helpers.convertDpToPixel(115, context)))
                .centerCrop()
                .into(holder.image);
    }

    @Override
    public AllPhotosDIveSpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_photo_in_photos_activity, parent, false);
        return new AllPhotosDIveSpotViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class AllPhotosDIveSpotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView image;

        public AllPhotosDIveSpotViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            image = (ImageView) v.findViewById(R.id.image);
        }

        @Override
        public void onClick(View v) {
            ImageSliderActivity.show(context, images, getAdapterPosition());
        }
    }

}
