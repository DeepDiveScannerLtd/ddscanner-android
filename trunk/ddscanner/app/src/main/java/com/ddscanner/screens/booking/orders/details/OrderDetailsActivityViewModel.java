package com.ddscanner.screens.booking.orders.details;

import com.ddscanner.entities.OrderDetails;

public class OrderDetailsActivityViewModel {

    private OrderDetails orderDetails;

    public OrderDetailsActivityViewModel(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }
}
