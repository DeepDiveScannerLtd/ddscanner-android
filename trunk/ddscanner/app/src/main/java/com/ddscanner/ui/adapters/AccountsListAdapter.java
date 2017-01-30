package com.ddscanner.ui.adapters;

import android.accounts.AccountsException;
import android.content.Context;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class AccountsListAdapter extends RecyclerView.Adapter<AccountsListAdapter.AccountsListViewHolder> {

    private ArrayList<BaseUser> users = new ArrayList<>();
    private ArrayList<Integer> types = new ArrayList<>();
    private int activeUserType;
    private Context context;

    public AccountsListAdapter(ArrayList<BaseUser> users) {
        this.users = users;
    }

    @Override
    public void onBindViewHolder(AccountsListViewHolder holder, int position) {
        if (users.get(position).isActive()) {
            holder.icCheck.setVisibility(View.VISIBLE);
        }
        holder.userType.setText(Helpers.getUserType(users.get(position).getType()));
        holder.userName.setText(users.get(position).getName());
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, users.get(position).getPhoto(), "1")).placeholder(R.drawable.avatar_changeacc).error(R.drawable.avatar_changeacc).resize(Math.round(Helpers.convertDpToPixel(36, context)),Math.round(Helpers.convertDpToPixel(36, context))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL)).into(holder.avatar);
    }

    @Override
    public AccountsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        context = parent.getContext();
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
        private ImageView avatar;

        public AccountsListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            userName = (TextView) v.findViewById(R.id.user_name);
            userType = (TextView) v.findViewById(R.id.user_type);
            icCheck = (ImageView) v.findViewById(R.id.active_user);
            avatar = (ImageView) v.findViewById(R.id.avatar);
        }

        @Override
        public void onClick(View view) {
            if (!users.get(getAdapterPosition()).isActive()) {
                DDScannerApplication.bus.post(new ChangeAccountEvent(users.get(getAdapterPosition()).getId()));
            }
        }
    }

}
