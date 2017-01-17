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
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lashket on 28.4.16.
 */
public class EditorsListAdapter extends RecyclerView.Adapter<EditorsListAdapter.UserListViewHolder> {

    private static final String TAG = EditorsListAdapter.class.getName();

    private Context context;
    private List<UserOld> userOldArrayList;
    private int avatarImageSize;
    private int avatarImageRadius;

    public EditorsListAdapter(Context context, List<UserOld> userOlds) {
        userOldArrayList = userOlds;
        this.context = context;
        avatarImageRadius = (int) context.getResources().getDimension(R.dimen.editor_avatar_radius);
        avatarImageSize = 2 * avatarImageRadius;
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_editor, parent, false);
        return new UserListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserListViewHolder holder, int position) {
        Picasso.with(context).load(userOldArrayList.get(position).getPicture())
                .resize(Math.round(Helpers.convertDpToPixel(avatarImageSize, context)), Math.round(Helpers.convertDpToPixel(avatarImageSize, context)))
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(holder.userAvatar);
        holder.userName.setText(userOldArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return userOldArrayList.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userAvatar;
        private TextView userName;

        public UserListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            userAvatar = (ImageView) v.findViewById(R.id.user_avatar);
            userName = (TextView) v.findViewById(R.id.user_name);
        }

        @Override
        public void onClick(View v) {
            //TODO show user profile activity
        }
    }

}
