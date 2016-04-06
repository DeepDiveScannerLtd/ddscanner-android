package com.ddscanner.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.R;
import com.ddscanner.ui.activities.AddDiveSpotActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by SuzukPc on 06.04.2016.
 */
public class AddPhotoToDsListAdapter extends RecyclerView.Adapter<AddPhotoToDsListAdapter.PhotoListViewHolder> {

    private static final String TAG = AddPhotoToDsListAdapter.class.getSimpleName();
    private Context context;
    private List<String> uris;

    public AddPhotoToDsListAdapter(List<String> uris, Context context) {
        this.context = context;
        this.uris = uris;
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
        Log.i(TAG, uris.get(position));
        String path = "file://" + uris.get(position);
        File f = new File(uris.get(position));
        Log.i(TAG, f.getAbsolutePath());
        Picasso.with(context).load(path).resize(110, 80).centerCrop().into(holder.photo);
        holder.icDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uris.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return uris.size();
    }

    public static class PhotoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AddDiveSpotActivity addDiveSpotActivity = new AddDiveSpotActivity();

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
