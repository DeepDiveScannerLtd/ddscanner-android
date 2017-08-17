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
import com.ddscanner.entities.SpotPhotoEditScreenEntity;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PhotosListAdapterWithCover extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int VIEW_TYPE_ADD_PHOTO = 1;
    private static final int VIEW_TYPE_PHOTO = 2;

    private static final String TAG = PhotosListAdapterWithCover.class.getSimpleName();
    private Context context;
    private List<SpotPhotoEditScreenEntity> serverPhotos = new ArrayList<>();
    private List<SpotPhotoEditScreenEntity> devicePhotos = new ArrayList<>();
    private List<SpotPhotoEditScreenEntity> allPhotos = new ArrayList<>();
    private List<SpotPhotoEditScreenEntity> deletedPhotos = new ArrayList<>();
    private int coverPhotoPosition = 0;
    private int previousCoverPhotoPosition;
    private String userServerId;

    public PhotosListAdapterWithCover(Context context, String userServerId) {
        this.context = context;
        this.coverPhotoPosition = -1;
        this.userServerId = userServerId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEW_TYPE_PHOTO:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.list_images_item, parent, false);
                return new PhotosListAdapterWithCover.EditSpotListPhotoViewHolder(itemView);

            case VIEW_TYPE_ADD_PHOTO:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.item_add_photo_to_dive_spot, parent, false);
                return new PhotosListAdapterWithCover.AddPhotoButtonViewHolder(itemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) != VIEW_TYPE_ADD_PHOTO) {
            EditSpotListPhotoViewHolder viewHolder = (EditSpotListPhotoViewHolder) holder;
            viewHolder.coverLabel.setVisibility(View.GONE);
            viewHolder.icDelete.setVisibility(View.GONE);
            if (allPhotos.get(viewHolder.getAdapterPosition()).getAuthorId().equals(userServerId) && allPhotos.size() > 1) {
                viewHolder.icDelete.setVisibility(View.VISIBLE);
            }
            if (allPhotos.get(viewHolder.getAdapterPosition()).isCover()) {
                viewHolder.coverLabel.setVisibility(View.VISIBLE);
            }
            if (position < serverPhotos.size()) {
                Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, allPhotos.get(position).getPhotoPath(), "1")).resize(Math.round(Helpers.convertDpToPixel(70, context)),Math.round(Helpers.convertDpToPixel(70, context))).centerCrop().placeholder(R.drawable.placeholder_photo_wit_round_corners).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.photo);
            } else {
                String path = allPhotos.get(holder.getAdapterPosition()).getPhotoPath();
                if (!path.contains("file:")) {
                    path = "file://" + path;
                }
                Picasso.with(context).load(path).resize(Math.round(Helpers.convertDpToPixel(70, context)),Math.round(Helpers.convertDpToPixel(70, context))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.photo);
            }

        }
    }

    public void addDevicePhotos(ArrayList<SpotPhotoEditScreenEntity> photos) {
        if (allPhotos.size() == 0) {
            photos.get(0).setCover(true);
            coverPhotoPosition = 0;
        }
        devicePhotos.addAll(photos);
        photosAdded(false);
    }

    public void addServerPhoto(ArrayList<SpotPhotoEditScreenEntity> photos) {
        serverPhotos.addAll(photos);
        photosAdded(false);
    }

    public Integer getDevicePhotoCoverNumber() {
        if (devicePhotos.size() == 0) {
            return null;
        }
        for (SpotPhotoEditScreenEntity photo : devicePhotos) {
            if (photo.isCover()) {
                return devicePhotos.indexOf(photo) + 1;
            }
        }
        return null;
    }

    public String getServerPhotoCoverId() {
        if (serverPhotos.size() == 0) {
            return null;
        }
        for (SpotPhotoEditScreenEntity photo : serverPhotos) {
            if (photo.isCover()) {
                return photo.getPhotoPath();
            }
        }
        return null;
    }

    private void photosAdded(boolean isCoverDeleted) {
        allPhotos = new ArrayList<>();
        allPhotos.addAll(serverPhotos);
        allPhotos.addAll(devicePhotos);
        if (isCoverDeleted && allPhotos.size() > 0) {
            allPhotos.get(0).setCover(true);
            coverPhotoPosition = 0;
        }
        notifyDataSetChanged();
    }

    private void removePhoto(String id) {
        boolean isCoverDeleted = false;
        for (SpotPhotoEditScreenEntity photo : devicePhotos) {
            if (photo.getPhotoPath().equals(id)) {
                if (photo.isCover()) {
                    isCoverDeleted = true;
                }
                devicePhotos.remove(devicePhotos.get(devicePhotos.indexOf(photo)));
                break;
            }
        }

        for (SpotPhotoEditScreenEntity serverPhoto : serverPhotos) {
            if (serverPhoto.getPhotoPath().equals(id)) {
                if (serverPhoto.isCover()) {
                    isCoverDeleted = true;
                }
                serverPhotos.remove(serverPhotos.indexOf(serverPhoto));
                deletedPhotos.add(serverPhoto);
                break;
            }
        }
        photosAdded(isCoverDeleted);
    }

    public int getCoverPhotoPositionForAddDiveSpot() {
        return coverPhotoPosition + 1;
    }

    public List<SpotPhotoEditScreenEntity> getDeletedPhotos() {
        return deletedPhotos;
    }

    public List<SpotPhotoEditScreenEntity> getNewPhotos() {
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

        ImageView photo;
        ImageView icDelete;
        TextView coverLabel;

        EditSpotListPhotoViewHolder(View v) {
            super(v);
            coverLabel = v.findViewById(R.id.cover_button);
            photo = v.findViewById(R.id.add_ds_photo);
            icDelete = v.findViewById(R.id.add_ds_photo_delete);
            icDelete.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_ds_photo_delete:
                    removePhoto(allPhotos.get(getAdapterPosition()).getPhotoPath());
                    break;
                default:
                    for (SpotPhotoEditScreenEntity photo : allPhotos) {
                        if (photo.isCover()) {
                            photo.setCover(false);
                            notifyItemChanged(allPhotos.indexOf(photo));
                            break;
                        }
                    }
                    coverPhotoPosition = getAdapterPosition();
                    allPhotos.get(getAdapterPosition()).setCover(true);
                    notifyItemChanged(getAdapterPosition());
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
