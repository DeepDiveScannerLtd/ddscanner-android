package com.ddscanner.screens.dialogs.popup;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Popup;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.LinkConsumableTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AchievementPopupDialogViewModel {

    private Popup popup;

    public AchievementPopupDialogViewModel(Popup popup) {
        this.popup = popup;
    }

    public Popup getPopup() {
        return popup;
    }

    @BindingAdapter({"loadImageFrom"})
    public static void loadImage(ImageView view, AchievementPopupDialogViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(viewModel.getPopup().getImage()).into(view);
        }
    }

    @BindingAdapter({"setTextFrom"})
    public static void loadText(LinkConsumableTextView view, AchievementPopupDialogViewModel viewModel) {
        if (viewModel != null) {
            ArrayList<Link> links = new ArrayList<>();
            Link link = new Link("");
            link.setUnderlined(false);
            link.setTextColor(ContextCompat.getColor(DDScannerApplication.getInstance(), R.color.notification_clickable_text_color));
            link.setHighlightAlpha(0);
            String text = viewModel.getPopup().getMessage();
            text = String.format(text, viewModel.getPopup().getHighlight().toArray());
            for (String highight : viewModel.getPopup().getHighlight()) {
                link.setText(highight);
                links.add(link);
            }
            LinkBuilder.on(view).addLinks(links).build();
            view.setText(text);
        }
    }
}
