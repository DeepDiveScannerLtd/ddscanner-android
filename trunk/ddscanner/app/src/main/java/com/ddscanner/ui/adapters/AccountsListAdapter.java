package com.ddscanner.ui.adapters;

import android.accounts.AccountsException;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;

public class AccountsListAdapter extends RecyclerView.Adapter<AccountsListAdapter.AccountsListViewHolder> {

    @Override
    public void onBindViewHolder(AccountsListViewHolder holder, int position) {

    }

    @Override
    public AccountsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountsListViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    class AccountsListViewHolder extends RecyclerView.ViewHolder {

        public AccountsListViewHolder(View v) {
            super(v);
        }

    }

}
