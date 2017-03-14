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

/**
 * Created by lashket on 2.7.16.
 */
public class EditorsUsersListAdapter extends RecyclerView.Adapter<EditorsUsersListAdapter.EditorsUsersListViewHolder> {

    private Context context;
    private ArrayList<User> users;

    public EditorsUsersListAdapter(Context context, ArrayList<User> Users) {
        users = Users;
        this.context = context;
    }

    @Override
    public EditorsUsersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_people, parent, false);
        return new EditorsUsersListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EditorsUsersListViewHolder holder, final int position) {
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, users.get(position).getPhoto(), "1")).resize(Math.round(Helpers.convertDpToPixel(58, context)), Math.round(Helpers.convertDpToPixel(58, context))).centerCrop().transform(new CropCircleTransformation()).into(holder.userAvatar);
        holder.userName.setText(users.get(position).getName());
        if (position == 0) {
            holder.creatorLabel.setVisibility(View.VISIBLE);
            holder.info.setText(R.string.creator);
        } else {
            holder.creatorLabel.setVisibility(View.GONE);
            holder.info.setText(R.string.editor);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class EditorsUsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userAvatar;
        private TextView userName;
        private TextView info;
        private ImageView creatorLabel;

        public EditorsUsersListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            userAvatar = (ImageView) v.findViewById(R.id.user_avatar);
            userName = (TextView) v.findViewById(R.id.user_name);
            info = (TextView) v.findViewById(R.id.count);
            creatorLabel = (ImageView) v.findViewById(R.id.creator_label);
        }

        @Override
        public void onClick(View v) {
            UserProfileActivity.show(context, users.get(getAdapterPosition()).getId(), users.get(getAdapterPosition()).getType());
        }
    }

}
