package com.ddscanner.screens.booking.orders;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.ddscanner.entities.Order;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class OrderItemViewModel {

    private Order order;

    public OrderItemViewModel(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    @BindingAdapter({"loadPhotoFrom"})
    public static void loadOfferImage(ImageView view, OrderItemViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(viewModel.getOrder().getImage()).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, view.getContext())), 0, RoundedCornersTransformation.CornerType.TOP)).into(view);
        }
    }

}
