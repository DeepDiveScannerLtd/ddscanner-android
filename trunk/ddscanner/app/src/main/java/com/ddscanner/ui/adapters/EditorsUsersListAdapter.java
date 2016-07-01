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
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lashket on 2.7.16.
 */
public class EditorsUsersListAdapter extends RecyclerView.Adapter<EditorsUsersListAdapter.EditorsUsersListViewHolder> {

    private Context context;
    private ArrayList<User> userArrayList;
    private Helpers helpers = new Helpers();

    public EditorsUsersListAdapter(Context context, ArrayList<User> users) {
        userArrayList = users;
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
    public void onBindViewHolder(EditorsUsersListViewHolder holder, int position) {
        Picasso.with(context)
                .load(userArrayList.get(position).getPicture())
                .resize(Math.round(helpers.convertDpToPixel(58, context)), Math.round(helpers.convertDpToPixel(58, context)))
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(holder.userAvatar);
        holder.userName.setText(userArrayList.get(position).getName());
        if (position == 0) {
            holder.info.setText(R.string.creator);
        } else {
            holder.info.setText(R.string.editor);
        }
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class EditorsUsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userAvatar;
        private TextView userName;
        private TextView info;

        public EditorsUsersListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            userAvatar = (ImageView) v.findViewById(R.id.user_avatar);
            userName = (TextView) v.findViewById(R.id.user_name);
            info = (TextView) v.findViewById(R.id.count);
        }

        @Override
        public void onClick(View v) {
            DDScannerApplication.bus.post(new ShowUserDialogEvent(userArrayList.get(getAdapterPosition())));
        }
    }

}
