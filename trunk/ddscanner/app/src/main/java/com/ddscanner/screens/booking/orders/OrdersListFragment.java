package com.ddscanner.screens.booking.orders;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.databinding.FragmentOffersListBinding;
import com.ddscanner.entities.Order;

import java.util.ArrayList;

public class OrdersListFragment extends Fragment {

    private FragmentOffersListBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offers_list, container, false);
        binding.offersList.setLayoutManager(new LinearLayoutManager(getContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setList();
    }

    private void setList() {
        ArrayList<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setImage("http://www.scubadiving.com/sites/scubadiving.com/files/styles/large_1x_/public/scuba-myths-shutterstock_208265431.jpg");
        order.setOrderDate("17 Feb 2017, 7:30-15:00");
        order.setOrderName("Diving to Racha islands");
        order.setPeoples("Child x 2, Adult x 1");
        order.setPrice("7800 B");
        orders.add(order);
        orders.add(order);
        orders.add(order);
        orders.add(order);
        orders.add(order);
        orders.add(order);
        orders.add(order);
        orders.add(order);
        orders.add(order);
        orders.add(order);
        binding.offersList.setAdapter(new OrderListAdapter(orders));
    }

}
