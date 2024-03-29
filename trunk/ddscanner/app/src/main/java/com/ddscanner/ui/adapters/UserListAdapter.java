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
import com.ddscanner.entities.User;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private Context context;
    private ArrayList<User> users;

    public  UserListAdapter(Context context, ArrayList<User> userOlds) {
        users = userOlds;
        this.context = context;
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_people, parent, false);
        return new UserListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserListViewHolder holder, int position) {
        Picasso.with(context)
                .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, users.get(position).getPhoto(), "1"))
                .placeholder(R.drawable.gray_circle_placeholder)
                .error(R.drawable.avatar_profile_default)
                .resize(Math.round(Helpers.convertDpToPixel(58, context)), Math.round(Helpers.convertDpToPixel(58, context)))
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(holder.userAvatar);
        holder.userName.setText(users.get(position).getName());
        int reviewsCount = users.get(position).getCounters().getCommentsCount();
        if (reviewsCount < 2) {
            holder.info.setText(DDScannerApplication.getInstance().getString(R.string.checkins_reiew_count_smaller_of_two, String.valueOf(reviewsCount)));
        } else {
            holder.info.setText(DDScannerApplication.getInstance().getString(R.string.checkins_reiew_count, String.valueOf(reviewsCount)));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userAvatar;
        private TextView userName;
        private TextView info;
        private Context context;

        public UserListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            context = v.getContext();
            userAvatar = v.findViewById(R.id.user_avatar);
            userName = v.findViewById(R.id.user_name);
            info = v.findViewById(R.id.count);
        }

        @Override
        public void onClick(View v) {
            if (!users.get(getAdapterPosition()).getId().equals(DDScannerApplication.getInstance().getString(R.string.dds_server_id))) {
                UserProfileActivity.show(context, users.get(getAdapterPosition()).getId(), users.get(getAdapterPosition()).getType());
            }
        }
    }

}
