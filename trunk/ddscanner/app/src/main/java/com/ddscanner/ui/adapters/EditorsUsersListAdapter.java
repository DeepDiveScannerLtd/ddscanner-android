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
 * Created by lashket on 2.7.16.
 */
public class EditorsUsersListAdapter extends RecyclerView.Adapter<EditorsUsersListAdapter.EditorsUsersListViewHolder> {

    private Context context;
    private ArrayList<UserOld> userOldArrayList;

    public EditorsUsersListAdapter(Context context, ArrayList<UserOld> userOlds) {
        userOldArrayList = userOlds;
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
        if (!userOldArrayList.get(position).getPicture().contains("http")) {
            Picasso.with(context)
                    .load(R.drawable.avatar_profile_dds)
                    .resize(Math.round(Helpers.convertDpToPixel(58, context)), Math.round(Helpers.convertDpToPixel(58, context)))
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .into(holder.userAvatar);
        } else {
            Picasso.with(context)
                    .load(userOldArrayList.get(position).getPicture())
                    .resize(Math.round(Helpers.convertDpToPixel(58, context)), Math.round(Helpers.convertDpToPixel(58, context)))
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .into(holder.userAvatar);
        }
        holder.userName.setText(userOldArrayList.get(position).getName());
        if (position == 0) {
            holder.info.setText(R.string.creator);
        } else {
            holder.info.setText(R.string.editor);
        }
        if (userOldArrayList.get(position).getAuthor() != null && userOldArrayList.get(position).getAuthor().equals("social")) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ForeignProfileActivity.show(context, userOldArrayList.get(position).getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userOldArrayList.size();
    }

    public class EditorsUsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userAvatar;
        private TextView userName;
        private TextView info;

        public EditorsUsersListViewHolder(View v) {
            super(v);
//            v.setOnClickListener(this);
            userAvatar = (ImageView) v.findViewById(R.id.user_avatar);
            userName = (TextView) v.findViewById(R.id.user_name);
            info = (TextView) v.findViewById(R.id.count);
        }

        @Override
        public void onClick(View v) {
//            if (userOldArrayList.get(getAdapterPosition()).getAuthor() != null && userOldArrayList.get(getAdapterPosition()).getAuthor().equals("social")) {
//                ForeignProfileActivity.showForResult(context, userOldArrayList.get(getAdapterPosition()).getId());
//            }
        }
    }

}
