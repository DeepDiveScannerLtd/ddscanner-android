package com.ddscanner.screens.notifications;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.ActivityTypes;
import com.ddscanner.entities.NotificationEntity;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.screens.photo.slider.ImageSliderActivity;
import com.ddscanner.screens.reiews.list.ReviewsActivity;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.LinkConsumableTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class NotificationsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_AVATAR_TEXT = 0;
    private static final int VIEW_TYPE_WITH_PHOTO = 1;
    private static final int VIEW_TYPE_WITH_PHOTOS_LIST = 2;

    private Activity context;
    private boolean isSelf;
    private ArrayList<NotificationEntity> notifications = new ArrayList<>();
    private SharedPreferenceHelper.UserType userType;

    public NotificationsListAdapter(Activity context, boolean isSelf, SharedPreferenceHelper.UserType userType) {
        this.context = context;
        this.isSelf = isSelf;
        this.userType = userType;
    }

    public void add(ArrayList<NotificationEntity> notifications) {
        this.notifications.addAll(notifications);
        notifyDataSetChanged();
    }

    public void setNotifications(ArrayList<NotificationEntity> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
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
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NotificationEntity notification = notifications.get(position);
        if (notification.isNew()) {
            DDScannerApplication.getInstance().addNotificationToList(notification.getId());
        }
        switch (getItemViewType(position)) {
            case VIEW_TYPE_AVATAR_TEXT:
                TextAndPhotoItemViewHolder textAndPhotoItemViewHolder = (TextAndPhotoItemViewHolder) holder;
                textAndPhotoItemViewHolder.notificationText.setText(notification.getText(isSelf, userType));
                if (!isSelf || (isSelf && !notification.getActivityType().equals(ActivityTypes.ACHIEVEMENT_GETTED))) {
                    loadUserPhoto(notification.getUser().getPhoto(), textAndPhotoItemViewHolder.userAvatar);
                }
                if (notifications.get(position).getLinks() != null) {
                    try {
                        LinkBuilder.on(textAndPhotoItemViewHolder.notificationText).addLinks(notification.getLinks()).build();
                    } catch (Exception e) {

                    }
                }
                break;
            case VIEW_TYPE_WITH_PHOTO:
                SinglePhotoItemViewHolder singlePhotoItemViewHolder = (SinglePhotoItemViewHolder) holder;
                singlePhotoItemViewHolder.notificationText.setText(notification.getText(isSelf, userType));
                if (notification.getLinks() != null) {
                    LinkBuilder.on(singlePhotoItemViewHolder.notificationText).addLinks(notification.getLinks()).build();
                }
                loadUserPhoto(notification.getUser().getPhoto(), singlePhotoItemViewHolder.userAvatar);
                Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, notification.getPhotos().get(0).getId(), "1")).resize(Math.round(Helpers.convertDpToPixel(36, context)), Math.round(Helpers.convertDpToPixel(36, context))).placeholder(R.drawable.placeholder_photo_wit_round_corners).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).centerCrop().into(singlePhotoItemViewHolder.photo);
                break;
            case VIEW_TYPE_WITH_PHOTOS_LIST:
                PhotosListItemViewHolder photosListItemViewHolder = (PhotosListItemViewHolder) holder;
                photosListItemViewHolder.notificationText.setText(notification.getText(isSelf, userType));
                if (notification.getLinks() != null) {
                    LinkBuilder.on(photosListItemViewHolder.notificationText).addLinks(notification.getLinks()).build();
                }
                loadUserPhoto(notification.getUser().getPhoto(), photosListItemViewHolder.userAvatar);
                NotificationPhotosListAdapter adapter = (NotificationPhotosListAdapter) photosListItemViewHolder.photosList.getAdapter();
                adapter.setData(notification.getPhotos(), notification.getPhotosCount(), notification.getId());
                break;
        }
    }

    public String getLastNotificationDate() {
        return notifications.get(notifications.size() - 1).getDate();
    }

    public String getFirstNotificationId() {
        if (notifications.size() > 0) {
            return notifications.get(0).getId();
        }
        return "";
    }

    private void loadUserPhoto(String photoId, ImageView view) {
        Picasso.with(context).load(context.getString(R.string.base_photo_url, photoId, "1")).placeholder(R.drawable.gray_circle_placeholder).error(R.drawable.review_default_avatar).resize(Math.round(Helpers.convertDpToPixel(36, context)), Math.round(Helpers.convertDpToPixel(36, context))).centerCrop().transform(new CropCircleTransformation()).into(view);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
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
            case INSTRUCTOR_LEFT_DIVE_CENTER:
                return VIEW_TYPE_AVATAR_TEXT;
            case DIVE_SPOT_PHOTO_LIKE:
                return VIEW_TYPE_WITH_PHOTO;
            case DIVE_SPOT_PHOTOS_ADDED:
            case DIVE_SPOT_MAPS_ADDED:
                if (notifications.get(position).getPhotos().size() == 1) {
                    return VIEW_TYPE_WITH_PHOTO;
                }
                return VIEW_TYPE_WITH_PHOTOS_LIST;
        }
        return -1;
    }

    private class TextAndPhotoItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userAvatar;
        private LinkConsumableTextView notificationText;

        TextAndPhotoItemViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
            notificationText = (LinkConsumableTextView) view.findViewById(R.id.notification_text);

            userAvatar.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.user_avatar:
                    UserProfileActivity.show(context, notifications.get(getAdapterPosition()).getUser().getId(), notifications.get(getAdapterPosition()).getUser().getType());
                    break;
                default:
                    switch (notifications.get(getAdapterPosition()).getActivityType()) {
                        case DIVE_SPOT_REVIEW_ADDED:
                        case DIVE_SPOT_REVIEW_LIKE:
                        case DIVE_SPOT_REVIEW_DISLIKE:
                            ReviewsActivity.showForResult(context, notifications.get(getAdapterPosition()).getReview().getId() ,-1, ReviewsOpenedSource.SINGLE);
                            break;
                        case DIVE_SPOT_ADDED:
                        case DIVE_SPOT_CHANGED:
                        case DIVE_SPOT_CHECKIN:
                            DiveSpotDetailsActivity.show(context, notifications.get(getAdapterPosition()).getDiveSpot().getId().toString(), EventsTracker.SpotViewSource.FROM_ACTIVITIES);
                            break;
                        case INSTRUCTOR_LEFT_DIVE_CENTER:
                        case DIVE_CENTER_INSTRUCTOR_ADD:
                        case DIVE_CENTER_INSTRUCTOR_REMOVE:
                            UserProfileActivity.show(context, notifications.get(getAdapterPosition()).getUser().getId(), notifications.get(getAdapterPosition()).getUser().getType());
                        break;
                    }
                    break;
            }
        }

    }

    private class SinglePhotoItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userAvatar;
        private LinkConsumableTextView notificationText;
        private ImageView photo;

        SinglePhotoItemViewHolder(View view) {
            super(view);
            userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
            notificationText = (LinkConsumableTextView) view.findViewById(R.id.notification_text);
            photo = (ImageView) view.findViewById(R.id.added_photo);

            photo.setOnClickListener(this);
            userAvatar.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.user_avatar:
                    UserProfileActivity.show(context, notifications.get(getAdapterPosition()).getUser().getId(), notifications.get(getAdapterPosition()).getUser().getType());
                    break;
                case R.id.added_photo:
                    DDScannerApplication.getInstance().getDiveSpotPhotosContainer().setPhotos(notifications.get(getAdapterPosition()).getPhotos());
                    ImageSliderActivity.showForResult(context, DDScannerApplication.getInstance().getDiveSpotPhotosContainer().getPhotos(), 0, 0, PhotoOpenedSource.NOTIFICATION, notifications.get(getAdapterPosition()).getId());
                    break;
                default:
                    switch (notifications.get(getAdapterPosition()).getActivityType()) {
                        case DIVE_SPOT_PHOTOS_ADDED:
                        case DIVE_SPOT_MAPS_ADDED:
                            DiveSpotDetailsActivity.show(context, notifications.get(getAdapterPosition()).getDiveSpot().getId().toString(), EventsTracker.SpotViewSource.FROM_ACTIVITIES);
                            break;
                    }
            }
        }
    }

    private class  PhotosListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userAvatar;
        private LinkConsumableTextView notificationText;
        private RecyclerView photosList;

        PhotosListItemViewHolder(View view) {
            super(view);
            userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
            notificationText = (LinkConsumableTextView) view.findViewById(R.id.notification_text);
            photosList = (RecyclerView) view.findViewById(R.id.photos_list);
            photosList.setLayoutManager(new GridLayoutManager(context, 6));
            photosList.setNestedScrollingEnabled(false);
            photosList.setAdapter(new NotificationPhotosListAdapter(context));
            view.setOnClickListener(this);
            userAvatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.user_avatar:
                    UserProfileActivity.show(context, notifications.get(getAdapterPosition()).getUser().getId(), notifications.get(getAdapterPosition()).getUser().getType());
                    break;
                default:
                    switch (notifications.get(getAdapterPosition()).getActivityType()) {
                        case DIVE_SPOT_PHOTOS_ADDED:
                        case DIVE_SPOT_MAPS_ADDED:
                            DiveSpotDetailsActivity.show(context, notifications.get(getAdapterPosition()).getDiveSpot().getId().toString(), EventsTracker.SpotViewSource.FROM_ACTIVITIES);
                            break;
                    }
            }
        }
    }

}
