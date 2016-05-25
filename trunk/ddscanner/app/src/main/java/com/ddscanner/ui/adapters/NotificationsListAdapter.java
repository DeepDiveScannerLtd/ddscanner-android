package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.entities.Notification;

import java.util.List;

/**
 * Created by lashket on 25.5.16.
 */
public class NotificationsListAdapter
        extends RecyclerView.Adapter<NotificationsListAdapter.NotificationListViewHolder>{

    private List<Notification> notifications;
    private Context context;

    @Override
    public void onBindViewHolder(NotificationListViewHolder holder, int position) {
        Notification notification = new Notification();
        notification = notifications.get(position);
    }

    @Override
    public NotificationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class NotificationListViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView text;
        private TextView timeAgo;

        public NotificationListViewHolder(View v) {
            super(v);
        }

    }

}
