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
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PhotosListAdapterWithoutCover extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int VIEW_TYPE_ADD_PHOTO = 1;
    private static final int VIEW_TYPE_PHOTO = 2;


    private Context context;
    private List<String> serverPhotos = new ArrayList<>();
    private List<String> devicePhotos = new ArrayList<>();
    private List<String> allPhotos = new ArrayList<>();
    private List<String> deletedPhotos = new ArrayList<>();
    private int coverPhotoPosition = 0;

    public PhotosListAdapterWithoutCover(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEW_TYPE_PHOTO:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.list_images_item, parent, false);
                return new EditReviewPhotoListViewHolder(itemView);

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) != VIEW_TYPE_ADD_PHOTO) {
            EditReviewPhotoListViewHolder viewHolder = (EditReviewPhotoListViewHolder) holder;
            viewHolder.coverLabel.setVisibility(View.GONE);
            if (position < serverPhotos.size()) {
                Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, allPhotos.get(position), "1")).resize(Math.round(Helpers.convertDpToPixel(70, context)),Math.round(Helpers.convertDpToPixel(70, context))).placeholder(R.drawable.placeholder_photo_wit_round_corners).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.photo);
            } else {
                String path = allPhotos.get(holder.getAdapterPosition());
                if (!path.contains("file:")) {
                    path = "file://" + path;
                }
                Picasso.with(context).load(path).resize(Math.round(Helpers.convertDpToPixel(70, context)),Math.round(Helpers.convertDpToPixel(70, context))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.photo);
            }
        }
    }

    public void addDevicePhotos(ArrayList<String> photos) {
        devicePhotos.addAll(photos);
        photosAdded();
    }

    public void addServerPhoto(ArrayList<String> photos) {
        serverPhotos.addAll(photos);
        photosAdded();
    }

    private void photosAdded() {
        allPhotos = new ArrayList<>();
        allPhotos.addAll(serverPhotos);
        allPhotos.addAll(devicePhotos);
        notifyDataSetChanged();
    }

    private void removePhoto(String id) {
        if (devicePhotos.contains(id)) {
            devicePhotos.remove(id);
        }
        if (serverPhotos.contains(id)) {
            serverPhotos.remove(id);
            deletedPhotos.add(id);
        }
        photosAdded();
    }

    public List<String> getDeletedPhotos() {
        return deletedPhotos;
    }

    public List<String> getNewPhotos() {
        return devicePhotos;
    }

    @Override
    public int getItemViewType(int position) {
        if (allPhotos.size() == position) {
            return VIEW_TYPE_ADD_PHOTO;
        }
        return VIEW_TYPE_PHOTO;
    }

    @Override
    public int getItemCount() {
        if (allPhotos == null) {
            return 1;
        }
        return allPhotos.size() + 1;
    }

    class EditReviewPhotoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView photo;
        ImageView icDelete;
        TextView coverLabel;

        EditReviewPhotoListViewHolder(View v) {
            super(v);
            coverLabel = (TextView) v.findViewById(R.id.cover_button);
            photo = (ImageView) v.findViewById(R.id.add_ds_photo);
            icDelete = (ImageView) v.findViewById(R.id.add_ds_photo_delete);
            icDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_ds_photo_delete:
                    removePhoto(allPhotos.get(getAdapterPosition()));
                    break;
                default:
                    coverPhotoPosition = getAdapterPosition();
                    break;
            }

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