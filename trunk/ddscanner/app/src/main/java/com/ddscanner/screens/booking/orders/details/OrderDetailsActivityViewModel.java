package com.ddscanner.screens.booking.orders.details;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.OrderDetails;

public class OrderDetailsActivityViewModel {

    private OrderDetails orderDetails;

    public OrderDetailsActivityViewModel(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    @BindingAdapter({"loadTotalPriceTextFrom"})
    public static void loadTotalPrice(TextView view, OrderDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            view.setText(DDScannerApplication.getInstance().getString(R.string.pattern_total_pice, viewModel.getOrderDetails().getPrice()));
        }
    }

    @BindingAdapter({"loadBookingIdFrom"})
    public static void loadookingId(TextView view, OrderDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            view.setText(DDScannerApplication.getInstance().getString(R.string.pattern_booking_id, viewModel.getOrderDetails().getBookingId()));
        }
    }

    @BindingAdapter({"loadDiscountPriceFrom"})
    public static void loadDiscountPrice(TextView view, OrderDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            view.setText(DDScannerApplication.getInstance().getString(R.string.pattern_discount_price, viewModel.getOrderDetails().getDiscount()));
        }
    }
}
