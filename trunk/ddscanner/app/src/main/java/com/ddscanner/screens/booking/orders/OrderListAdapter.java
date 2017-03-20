package com.ddscanner.screens.booking.orders;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.databinding.ItemOrderBinding;
import com.ddscanner.entities.Order;

import java.util.ArrayList;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private ArrayList<Order> orders = new ArrayList<>();

    public OrderListAdapter(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        holder.binding.setOrderViewModel(new OrderItemViewModel(orders.get(position)));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        ItemOrderBinding binding;

        OrderViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

    }

}
