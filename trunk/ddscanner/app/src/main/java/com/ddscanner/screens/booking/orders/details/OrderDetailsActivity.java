package com.ddscanner.screens.booking.orders.details;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ddscanner.R;
import com.ddscanner.databinding.ActivityOrderDetailsBinding;
import com.ddscanner.entities.Order;
import com.ddscanner.entities.OrderDetails;
import com.google.gson.Gson;

public class OrderDetailsActivity extends AppCompatActivity {

    private static final String ARG_ORDER = "ORDER";

    ActivityOrderDetailsBinding binding;

    public static void show(Context context, String order) {
        Intent intent = new Intent(context, OrderDetailsActivity.class);
        intent.putExtra(ARG_ORDER, order);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        setupView(new Gson().fromJson(getIntent().getStringExtra(ARG_ORDER), Order.class));
    }

    private void setupView(Order order) {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderName(order.getOrderName());
        orderDetails.setOrderDate(order.getOrderDate());
        orderDetails.setPeoples(order.getPeoples());
        orderDetails.setPrice(order.getPrice());
        orderDetails.setDiscount("750 B");
        orderDetails.setBookingId("1254322");
        orderDetails.setPickupPoint("Minsk, Hotel Belarus");
        orderDetails.setCancelation("Free");
        binding.setViewModel(new OrderDetailsActivityViewModel(orderDetails));
    }

}
