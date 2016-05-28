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
import com.ddscanner.ui.views.TransformationRoundImage;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lashket on 28.4.16.
 */
public class EditorsListAdapter extends RecyclerView.Adapter<EditorsListAdapter.UserListViewHolder> {

    private Context context;
    private List<User> userArrayList;

    public EditorsListAdapter(Context context, List<User> users) {
        userArrayList = users;
        this.context = context;
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
        Picasso.with(context).load(userArrayList.get(position).getPicture()).resize(35,35)
                .centerCrop().transform(new TransformationRoundImage(50,0)).into(holder.userAvatar);
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
            DDScannerApplication.bus.post(new ShowUserDialogEvent(userArrayList.get(getAdapterPosition())));
        }
    }

}
