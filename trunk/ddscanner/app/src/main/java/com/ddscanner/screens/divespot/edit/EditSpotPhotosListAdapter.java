package com.ddscanner.screens.divespot.edit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class EditSpotPhotosListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int VIEW_TYPE_ADD_PHOTO = 1;
    private static final int VIEW_TYPE_PHOTO = 2;

    private static final String TAG = EditSpotPhotosListAdapter.class.getSimpleName();
    private Context context;
    private List<String> serverPhotos = new ArrayList<>();
    private List<String> devicePhotos = new ArrayList<>();
    private List<String> allPhotos = new ArrayList<>();
    private List<String> deletedPhotos = new ArrayList<>();

    public EditSpotPhotosListAdapter(Context context) {
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
                return new EditSpotPhotosListAdapter.EditSpotListPhotoViewHolder(itemView);

            case VIEW_TYPE_ADD_PHOTO:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.item_add_photo_to_dive_spot, parent, false);
                return new EditSpotPhotosListAdapter.AddPhotoButtonViewHolder(itemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) != VIEW_TYPE_ADD_PHOTO) {
            EditSpotListPhotoViewHolder viewHolder = (EditSpotListPhotoViewHolder) holder;
            if (position < serverPhotos.size()) {
                Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, allPhotos.get(position), "1")).resize(Math.round(Helpers.convertDpToPixel(70, context)),Math.round(Helpers.convertDpToPixel(70, context))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.photo);
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

    class EditSpotListPhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView photo;
        protected ImageView icDelete;

        public EditSpotListPhotoViewHolder(View v) {
            super(v);
            photo = (ImageView) v.findViewById(R.id.add_ds_photo);
            icDelete = (ImageView) v.findViewById(R.id.add_ds_photo_delete);
            icDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            removePhoto(allPhotos.get(getAdapterPosition()));
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
