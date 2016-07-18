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
import com.ddscanner.events.ShowUserDialogEvent;
import com.ddscanner.ui.activities.ForeignProfileActivity;
import com.ddscanner.ui.views.TransformationRoundImage;
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
    private List<User> userArrayList;
    private int avatarImageSize;
    private int avatarImageRadius;
    private Helpers helpers = new Helpers();

    public EditorsListAdapter(Context context, List<User> users) {
        userArrayList = users;
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
        Picasso.with(context).load(userArrayList.get(position).getPicture())
                .resize(Math.round(helpers.convertDpToPixel(avatarImageSize, context)), Math.round(helpers.convertDpToPixel(avatarImageSize, context)))
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(holder.userAvatar);
        holder.userName.setText(userArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
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
            ForeignProfileActivity.show(context, userArrayList.get(getAdapterPosition()).getId());
        }
    }

}
