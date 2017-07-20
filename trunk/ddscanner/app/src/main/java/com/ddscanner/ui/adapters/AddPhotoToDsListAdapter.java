package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class AddPhotoToDsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ADD_PHOTO = 1;
    private static final int VIEW_TYPE_PHOTO = 2;

    private static final String TAG = AddPhotoToDsListAdapter.class.getSimpleName();
    private Context context;
    private List<String> uris;

    public AddPhotoToDsListAdapter(List<String> uris, Context context) {
        this.context = context;
        this.uris = uris;
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
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (uris.size() == position) {
            return VIEW_TYPE_ADD_PHOTO;
        }
        return VIEW_TYPE_PHOTO;
    }

    public void addPhotos(ArrayList<String> photos) {
        this.uris.addAll(photos);
        notifyDataSetChanged();
    }

    public void deletePhoto(int position) {
        this.uris.remove(position);
        notifyItemRemoved(position);
    }

    public List<String> getNewFilesUrisList() {
        if (this.uris == null) {
            this.uris = new ArrayList<>();
        }
        return this.uris;
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
            icDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            deletePhoto(getAdapterPosition());
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
