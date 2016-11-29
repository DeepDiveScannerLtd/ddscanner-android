package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.R;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CheckedInDialogPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CheckedInDialogPhotosAdapter.class.getSimpleName();

    private static final int ViEW_TYPE_ADD_PHOTO = 1;
    private static final int ViEW_TYPE_PHOTO = 2;

    private List<String> uris = new ArrayList<>();
    private Context context;

    public CheckedInDialogPhotosAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ViEW_TYPE_ADD_PHOTO:
                Log.i(TAG, "Add photo view holder created");
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_add_photo, parent, false);
                return new AddPhotoViewHolder(view);
            case ViEW_TYPE_PHOTO:
                Log.i(TAG, "Photo view holder created");
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_checked_in_dialog_photo, parent, false);
                return new CheckedInDialogPhotosViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "View type - " + String.valueOf(getItemViewType(position)));
        if (getItemViewType(position) == ViEW_TYPE_PHOTO) {
            CheckedInDialogPhotosViewHolder checkedInDialogPhotosViewHolder = (CheckedInDialogPhotosViewHolder) holder;
            Picasso.with(context)
                    .load(uris.get(position))
                    .resize(Math.round(Helpers.convertDpToPixel(50, context)), Math.round(Helpers.convertDpToPixel(50, context)))
                    .centerCrop()
                    .transform(new TransformationRoundImage(2, 0))
                    .into(checkedInDialogPhotosViewHolder.image);
        }

    }



    @Override
    public int getItemCount() {
        if (uris == null) {
            return 1;
        }
        return uris.size() + 1;
    }

    public void addPhoto() {
        uris.add("https://pp.vk.me/c626824/v626824069/26b62/ybudsPZQNmc.jpg");
        notifyItemInserted(uris.size());
    }

    @Override
    public int getItemViewType(int position) {
        return (position == uris.size()) ? ViEW_TYPE_ADD_PHOTO : ViEW_TYPE_PHOTO;
    }

    class CheckedInDialogPhotosViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public CheckedInDialogPhotosViewHolder(View v) {
            super(v);

            image = (ImageView) v.findViewById(R.id.photo);
        }

    }

    class AddPhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AddPhotoViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            addPhoto();
        }
    }

}
