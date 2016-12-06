package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.events.ImageDeletedEvent;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by SuzukPc on 06.04.2016.
 */
public class AddPhotoToDsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ADD_PHOTO = 1;
    private static final int VIEW_TYPE_PHOTO = 2;

    private static final String TAG = AddPhotoToDsListAdapter.class.getSimpleName();
    private Context context;
    private List<String> uris;
    private List<String> newImagesUriList = new ArrayList<>();
    private List<String> deletedImages = new ArrayList<>();

    public AddPhotoToDsListAdapter(List<String> uris, Context context) {
        this.context = context;
        this.uris = uris;
        this.deletedImages = new ArrayList<>();
        for (String uri : uris) {
            if (!uri.contains(Constants.images)) {
                this.newImagesUriList.add(uri);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEW_TYPE_PHOTO:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.list_images_item, parent, false);
                return new PhotoListViewHolder(itemView);

            case VIEW_TYPE_ADD_PHOTO:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.item_add_photo_to_dive_spot, parent, false);
                return new AddPhotoButtonViewHolder(itemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == VIEW_TYPE_PHOTO) {
            PhotoListViewHolder photoListViewHolder = (PhotoListViewHolder) holder;
            String path = uris.get(holder.getAdapterPosition());
            if (!path.contains(Constants.images) && !path.contains("file:")) {
                path = "file://" + path;
            }
            Picasso.with(context).load(path).resize(Math.round(Helpers.convertDpToPixel(70, context)),Math.round(Helpers.convertDpToPixel(70, context))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).into(photoListViewHolder.photo);
            photoListViewHolder.icDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uris.get(holder.getAdapterPosition()).contains(Constants.images)) {
                        deletedImages.add(uris.get(holder.getAdapterPosition()));
                    }
                    DDScannerApplication.bus.post(new ImageDeletedEvent(position));
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (uris.size() == position) {
            return VIEW_TYPE_ADD_PHOTO;
        }
        return VIEW_TYPE_PHOTO;
    }

    public List<String> getListOfDeletedImages() {
        if (this.deletedImages.size() == 0) {
            return null;
        }
        return this.deletedImages;
    }

    public List<String> getNewFilesUrisList() {
        if (this.newImagesUriList.size() == 0) {
            return null;
        }
        return this.newImagesUriList;
    }

    @Override
    public int getItemCount() {
        if (uris == null) {
            return 1;
        }
        return uris.size() + 1;
    }

     class PhotoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

    class AddPhotoButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AddPhotoButtonViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            DDScannerApplication.bus.post(new AddPhotoDoListEvent());
        }
    }

}
