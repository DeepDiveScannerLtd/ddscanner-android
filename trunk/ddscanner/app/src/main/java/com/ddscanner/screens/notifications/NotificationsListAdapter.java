package com.ddscanner.screens.notifications;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    private static final String TAG = NotificationsListAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_AVATAR_TEXT = 0;
    private static final int VIEW_TYPE_WITH_PHOTO = 1;
    private static final int VIEW_TYPE_WITH_PHOTOS_LIST = 2;
    private static final int VIEW_TYPE_PAGINATION = 3;
    private static final int VIEW_TYPE_EMPTY = 4;

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
            case VIEW_TYPE_PAGINATION:
                return new PaginationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paginaion_loader, parent, false));
            case VIEW_TYPE_AVATAR_TEXT:
                return new TextAndPhotoItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_with_photo_and_text, parent, false));
            case VIEW_TYPE_WITH_PHOTO:
                return new SinglePhotoItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_with_single_photo, parent, false));
            case VIEW_TYPE_WITH_PHOTOS_LIST:
                return new PhotosListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_with_photo_list, parent, false));
            case VIEW_TYPE_EMPTY:
                return new EmptyItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_item, parent, false));
                default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_PAGINATION) {
            return;
        }
        NotificationEntity notification = notifications.get(position);
        if (notification.isNew()) {
            DDScannerApplication.getInstance().addNotificationToList(notification.getId());
        }
        try {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_AVATAR_TEXT:
                    TextAndPhotoItemViewHolder textAndPhotoItemViewHolder = (TextAndPhotoItemViewHolder) holder;
                    textAndPhotoItemViewHolder.notificationText.setText(notification.getText(isSelf, userType));
                    if (!isSelf || (isSelf && !notification.getActivityType().equals(ActivityTypes.ACHIEVEMENT_GETTED))) {
                        loadUserPhoto(notification.getUser().getPhoto(), textAndPhotoItemViewHolder.userAvatar);
                    } else {
                        textAndPhotoItemViewHolder.userAvatar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_title));
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
                    switch (notification.getActivityType()) {
                        case DIVE_SPOT_PHOTOS_ADDED:
                            Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, notification.getPhotos().get(0).getId(), "1")).resize(Math.round(Helpers.convertDpToPixel(34, context)), Math.round(Helpers.convertDpToPixel(34, context))).placeholder(R.drawable.placeholder_photo_wit_round_corners).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).centerCrop().into(singlePhotoItemViewHolder.photo);
                            break;
                        case DIVE_SPOT_MAPS_ADDED:
                            Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, notification.getMaps().get(0).getId(), "1")).resize(Math.round(Helpers.convertDpToPixel(34, context)), Math.round(Helpers.convertDpToPixel(34, context))).placeholder(R.drawable.placeholder_photo_wit_round_corners).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).centerCrop().into(singlePhotoItemViewHolder.photo);
                            break;
                        case DIVE_SPOT_PHOTO_LIKE:
                            Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, notification.getPhotos().get(0).getId(), "1")).resize(Math.round(Helpers.convertDpToPixel(34, context)), Math.round(Helpers.convertDpToPixel(34, context))).placeholder(R.drawable.placeholder_photo_wit_round_corners).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).centerCrop().into(singlePhotoItemViewHolder.photo);
                            break;
                    }
                    break;
                case VIEW_TYPE_WITH_PHOTOS_LIST:
                    PhotosListItemViewHolder photosListItemViewHolder = (PhotosListItemViewHolder) holder;
                    photosListItemViewHolder.notificationText.setText(notification.getText(isSelf, userType));
                    photosListItemViewHolder.photosList.setLayoutManager(new GridLayoutManager(context, 6));
                    photosListItemViewHolder.photosList.setNestedScrollingEnabled(false);
                    if (notification.getLinks() != null) {
                        LinkBuilder.on(photosListItemViewHolder.notificationText).addLinks(notification.getLinks()).build();
                    }
                    loadUserPhoto(notification.getUser().getPhoto(), photosListItemViewHolder.userAvatar);
                    NotificationPhotosListAdapter adapter = (NotificationPhotosListAdapter) photosListItemViewHolder.photosList.getAdapter();
                    switch (notification.getActivityType()) {
                        case DIVE_SPOT_MAPS_ADDED:
                            if (notification.getMaps() != null) {
//                                photosListItemViewHolder.photosList.setAdapter(new NotificationPhotosListAdapter(context, notification.getMaps(), notification.getMapsCount(), notification.getId()));
                                adapter.setData(notification.getMaps(), notification.getMapsCount(), notification.getId());
                            }
                            break;
                        case DIVE_SPOT_PHOTOS_ADDED:
                            if (notification.getPhotos() != null) {
//                                photosListItemViewHolder.photosList.setAdapter(new NotificationPhotosListAdapter(context, notification.getPhotos(), notification.getPhotosCount(), notification.getId()));
                                adapter.setData(notification.getPhotos(), notification.getPhotosCount(), notification.getId());
                            }
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public String getLastNotificationDate() {
        try {
            return notifications.get(notifications.size() - 1).getDate();
        } catch (Exception e) {
            return notifications.get(notifications.size() - 2).getDate();
        }
    }

    public String getFirstNotificationId() {
        if (notifications.size() > 0) {
            return notifications.get(0).getId();
        }
        return "";
    }

    public void startLoading() {
        notifications.add(null);
        notifyItemInserted(notifications.size() - 1);
    }

    public void dataLoaded() {
        notifications.remove(notifications.size() - 1);
        notifyItemRemoved(notifications.size() - 1);
    }

    private void loadUserPhoto(String photoId, ImageView view) {
        if (!photoId.isEmpty()) {
            Picasso.with(context).load(context.getString(R.string.base_photo_url, photoId, "1")).placeholder(R.drawable.gray_circle_placeholder).error(R.drawable.notif_default_avatar).resize(Math.round(Helpers.convertDpToPixel(34, context)), Math.round(Helpers.convertDpToPixel(34, context))).centerCrop().transform(new CropCircleTransformation()).into(view);
        } else {
            view.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.notif_default_avatar));
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (notifications.get(position) == null) {
            return VIEW_TYPE_PAGINATION;
        }
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
                if (notifications.get(position).getPhotos().size() == 1) {
                    return VIEW_TYPE_WITH_PHOTO;
                }
                return VIEW_TYPE_WITH_PHOTOS_LIST;
            case DIVE_SPOT_MAPS_ADDED:
                if (notifications.get(position).getMaps().size() == 1) {
                    return VIEW_TYPE_WITH_PHOTO;
                }
                return VIEW_TYPE_WITH_PHOTOS_LIST;
            case VALIDATING_ERROR:
                return VIEW_TYPE_EMPTY;
        }
        return -1;
    }

    private class EmptyItemViewHolder extends RecyclerView.ViewHolder {

        EmptyItemViewHolder(View view) {
            super(view);
        }

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
                    switch (notifications.get(getAdapterPosition()).getActivityType()) {
                        case DIVE_SPOT_MAPS_ADDED:
                            DDScannerApplication.getInstance().getDiveSpotPhotosContainer().setPhotos(notifications.get(getAdapterPosition()).getMaps());
                            ImageSliderActivity.showForResult(context, DDScannerApplication.getInstance().getDiveSpotPhotosContainer().getPhotos(), 0, 0, PhotoOpenedSource.NOTIFICATION, notifications.get(getAdapterPosition()).getId());
                            break;
                        case DIVE_SPOT_PHOTOS_ADDED:
                            DDScannerApplication.getInstance().getDiveSpotPhotosContainer().setPhotos(notifications.get(getAdapterPosition()).getPhotos());
                            ImageSliderActivity.showForResult(context, DDScannerApplication.getInstance().getDiveSpotPhotosContainer().getPhotos(), 0, 0, PhotoOpenedSource.NOTIFICATION, notifications.get(getAdapterPosition()).getId());
                            break;
                    }
//                    if (notifications.get(getAdapterPosition()).getPhotos() != null) {
//                        DDScannerApplication.getInstance().getDiveSpotPhotosContainer().setPhotos(notifications.get(getAdapterPosition()).getPhotos());
//                        ImageSliderActivity.showForResult(context, DDScannerApplication.getInstance().getDiveSpotPhotosContainer().getPhotos(), 0, 0, PhotoOpenedSource.NOTIFICATION, notifications.get(getAdapterPosition()).getId());
//                    }
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

    class PaginationViewHolder extends RecyclerView.ViewHolder {

        PaginationViewHolder(View view){
            super(view);
        }

    }

}
