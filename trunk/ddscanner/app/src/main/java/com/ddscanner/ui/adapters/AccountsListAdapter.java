package com.ddscanner.ui.adapters;

import android.accounts.AccountsException;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.BaseUser;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.User;
import com.ddscanner.events.ChangeAccountEvent;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;

public class AccountsListAdapter extends RecyclerView.Adapter<AccountsListAdapter.AccountsListViewHolder> {

    private ArrayList<BaseUser> users = new ArrayList<>();
    private ArrayList<Integer> types = new ArrayList<>();
    private int activeUserType;

    public AccountsListAdapter(ArrayList<BaseUser> users) {
        this.users = users;
    }

    @Override
    public void onBindViewHolder(AccountsListViewHolder holder, int position) {
        if (users.get(position).isActive()) {
            holder.icCheck.setVisibility(View.VISIBLE);
        }
        holder.userName.setText(users.get(position).getName());
    }

    @Override
    public AccountsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountsListViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class AccountsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView userName;
        private TextView userType;
        private ImageView icCheck;

        public AccountsListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            userName = (TextView) v.findViewById(R.id.user_name);
            userType = (TextView) v.findViewById(R.id.user_type);
            icCheck = (ImageView) v.findViewById(R.id.active_user);
        }

        @Override
        public void onClick(View view) {
            if (!users.get(getAdapterPosition()).isActive()) {
                DDScannerApplication.bus.post(new ChangeAccountEvent(users.get(getAdapterPosition()).getId()));
            }
        }
    }

}
