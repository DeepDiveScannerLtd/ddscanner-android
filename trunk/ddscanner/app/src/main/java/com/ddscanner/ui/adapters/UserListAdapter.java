package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.UserOld;
import com.ddscanner.ui.activities.ForeignProfileActivity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lashket on 28.4.16.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private Context context;
    private ArrayList<UserOld> userOldArrayList;

    public UserListAdapter(Context context, ArrayList<UserOld> userOlds) {
        userOldArrayList = userOlds;
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
                .load(userOldArrayList.get(position).getPicture())
                .resize(Math.round(Helpers.convertDpToPixel(58, context)), Math.round(Helpers.convertDpToPixel(58, context)))
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(holder.userAvatar);
        holder.userName.setText(userOldArrayList.get(position).getName());
        holder.info.setText(userOldArrayList.get(position).getCountComment() + " reviews, " +
                userOldArrayList.get(position).getCountLike() + " likes");
    }

    @Override
    public int getItemCount() {
        return userOldArrayList.size();
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
            userAvatar = (ImageView) v.findViewById(R.id.user_avatar);
            userName = (TextView) v.findViewById(R.id.user_name);
            info = (TextView) v.findViewById(R.id.count);
        }

        @Override
        public void onClick(View v) {
           ForeignProfileActivity.show(context, userOldArrayList.get(getAdapterPosition()).getId());
        }
    }

}
