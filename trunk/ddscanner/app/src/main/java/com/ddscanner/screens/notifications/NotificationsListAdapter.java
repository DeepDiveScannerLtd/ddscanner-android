package com.ddscanner.screens.notifications;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Notification;

import java.util.ArrayList;

public class NotificationsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_AVATAR_TEXT = 0;
    private static final int VIEW_TYPE_WITH_PHOTO = 1;
    private static final int VIEW_TYPE_WITH_PHOTOS_LIST = 2;

    private Context context;
    private ArrayList<Notification> notifications = new ArrayList<>();

    public NotificationsListAdapter(Context context) {
        this.context = context;
    }

    public void add(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_AVATAR_TEXT:
                return new TextAndPhotoItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_with_photo_and_text, parent, false));
            case VIEW_TYPE_WITH_PHOTO:
                return new SinglePhotoItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_with_single_photo, parent, false));
            case VIEW_TYPE_WITH_PHOTOS_LIST:
                return new PhotosListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_with_photo_list, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        switch (notifications.get(position).getActivityType()) {
            case DIVE_SPOT_ADDED:
            case DIVE_SPOT_CHANGED:
            case DIVE_SPOT_CHECKIN:
            case DIVE_SPOT_REVIEW_ADDED:
            case ACHIEVEMENT_GETTED:
            case DIVE_SPOT_REVIEW_DISLIKE:
            case DIVE_SPOT_REVIEW_LIKE:
            case DIVE_CENTER_INSTRUCTOR_ADD:
            case DIVE_CENTER_INSTRUCTOR_REMOVE:
                return VIEW_TYPE_AVATAR_TEXT;
        }
        return -1;
    }

    class TextAndPhotoItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView userAvatar;
        private TextView notificationText;

        TextAndPhotoItemViewHolder(View view) {
            super(view);
            userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
            notificationText = (TextView) view.findViewById(R.id.notification_text);
        }

    }

    class SinglePhotoItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView userAvatar;
        private TextView notificationText;
        private ImageView photo;

        SinglePhotoItemViewHolder(View view) {
            super(view);
            userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
            notificationText = (TextView) view.findViewById(R.id.notification_text);
            photo = (ImageView) view.findViewById(R.id.added_photo);
        }

    }

    class  PhotosListItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView userAvatar;
        private TextView notificationText;
        private RecyclerView photosList;

        PhotosListItemViewHolder(View view) {
            super(view);
            userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
            notificationText = (TextView) view.findViewById(R.id.notification_text);
            photosList = (RecyclerView) view.findViewById(R.id.photos_list);
        }

    }

}