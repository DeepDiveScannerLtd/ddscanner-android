package com.ddscanner.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Sealife;
import com.ddscanner.ui.activities.AddDiveSpotActivity;
import com.ddscanner.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuzukPc on 06.04.2016.
 */
public class AddPhotoToDsListAdapter extends RecyclerView.Adapter<AddPhotoToDsListAdapter.PhotoListViewHolder> {

    private static final String TAG = AddPhotoToDsListAdapter.class.getSimpleName();
    private Context context;
    private List<String> uris;
    private List<String> newImagesUriList = new ArrayList<>();
    private List<String> deletedImages = new ArrayList<>();
    private TextView textView;

    public AddPhotoToDsListAdapter(List<String> uris, Context context, TextView textView) {
        this.context = context;
        this.uris = uris;
        this.textView = textView;
    }

    @Override
    public PhotoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_images_item, parent, false);
        return new PhotoListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoListViewHolder holder, final int position) {
        String path = uris.get(position);
        if (!uris.get(position).contains(Constants.images)) {
            newImagesUriList.add(uris.get(position));
            path = "file://" + uris.get(position);
        }
        Picasso.with(context).load(path).resize(110, 80).centerCrop().into(holder.photo);
        holder.icDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uris.get(position).contains(Constants.images)) {
                    deletedImages.add(uris.get(position));
                }
                uris.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, uris.size());
            }
        });
    }

    public List<String> getListOfDeletedImages() {
        if (deletedImages.size() == 0) {
            return null;
        }
        return deletedImages;
    }

    public List<String> getNewFilesUrisList() {
        if (newImagesUriList.size() == 0) {
            return null;
        }
        return newImagesUriList;
    }

    @Override
    public int getItemCount() {
        if (uris.size() > 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
        return uris.size();
    }

    public static class PhotoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView photo;
        protected ImageView icDelete;

        public PhotoListViewHolder(View v) {
            super(v);
            photo = (ImageView) v.findViewById(R.id.add_ds_photo);
            icDelete = (ImageView) v.findViewById(R.id.add_ds_photo_delete);
        }

        @Override
        public void onClick(View v) {

        }
    }



}
