package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;

/**
 * Created by lashket on 28.4.16.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private Context context;

    public UserListAdapter(Context context) {
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

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class UserListViewHolder extends RecyclerView.ViewHolder {

        public UserListViewHolder(View v) {
            super(v);

        }

    }

}
