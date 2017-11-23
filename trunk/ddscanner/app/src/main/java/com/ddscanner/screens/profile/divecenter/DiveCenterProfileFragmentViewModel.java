package com.ddscanner.screens.profile.divecenter;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Address;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.screens.divecenter.request.SendRequestActivity;
import com.ddscanner.utils.EmailIntentBuilder;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.PhoneCallIntentBuilder;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DiveCenterProfileFragmentViewModel {

    private DiveCenterProfile diveCenterProfile;

    public DiveCenterProfileFragmentViewModel(DiveCenterProfile diveCenterProfile) {
        this.diveCenterProfile = diveCenterProfile;
    }

    public DiveCenterProfile getDiveCenterProfile() {
        return diveCenterProfile;
    }

    @BindingAdapter({"loadLanguagesFrom"})
    public static void loadLanguagesFrom(TextView view, DiveCenterProfileFragmentViewModel viewModel) {
        if (viewModel != null) {
            if (viewModel.getDiveCenterProfile().getLanguages() == null || viewModel.getDiveCenterProfile().getLanguages().size() == 0) {
                view.setText("0");
                return;
            }
            if (viewModel.getDiveCenterProfile().getLanguages().size() == 2) {
                view.setText(String.format("%s, %s", viewModel.getDiveCenterProfile().getLanguages().get(0), viewModel.getDiveCenterProfile().getLanguages().get(1)));
                return;
            }
            if (viewModel.getDiveCenterProfile().getLanguages().size() == 1) {
                view.setText(viewModel.getDiveCenterProfile().getLanguages().get(0));
                return;
            }
            view.setText(String.valueOf(viewModel.getDiveCenterProfile().getLanguages().size()));
        }
    }

    @BindingAdapter({"countEditedFrom"})
    public static void editedCount(TextView view, DiveCenterProfileFragmentViewModel diveCenterProfileFragmentViewModel) {
        if (diveCenterProfileFragmentViewModel != null) {
            view.setText(getDiveSpotString(diveCenterProfileFragmentViewModel.getDiveCenterProfile().getEditedSpotsCount()));
        }
    }

    @BindingAdapter({"countAddedFrom"})
    public static void addedCount(TextView view, DiveCenterProfileFragmentViewModel diveCenterProfileFragmentViewModel) {
        if (diveCenterProfileFragmentViewModel != null) {
            view.setText(getDiveSpotString(diveCenterProfileFragmentViewModel.getDiveCenterProfile().getCreatedSpotsCount()));
        }
    }

    @BindingAdapter({"myDiveSpotsCount"})
    public static void mySpotsCount(TextView view, DiveCenterProfileFragmentViewModel diveCenterProfileFragmentViewModel) {
        if (diveCenterProfileFragmentViewModel !=null) {
            if (diveCenterProfileFragmentViewModel.getDiveCenterProfile().getWorkingCount() > 0) {
                view.setVisibility(View.VISIBLE);
                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId().equals(diveCenterProfileFragmentViewModel.diveCenterProfile.getId().toString())) {
                    view.setText(DDScannerApplication.getInstance().getString(R.string.dive_center_spots_count, String.valueOf(diveCenterProfileFragmentViewModel.getDiveCenterProfile().getWorkingCount())));
                    return;
                }
                view.setText(DDScannerApplication.getInstance().getString(R.string.dive_center_spots_count_foreign, String.valueOf(diveCenterProfileFragmentViewModel.getDiveCenterProfile().getWorkingCount())));
            }
        }
    }

    @BindingAdapter({"loadImageFrom"})
    public static void loadProfileImage(ImageView view, DiveCenterProfileFragmentViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getDiveCenterProfile().getPhoto(), "1")).resize(Math.round(Helpers.convertDpToPixel(65, view.getContext())), Math.round(Helpers.convertDpToPixel(65, view.getContext()))).placeholder(R.drawable.placeholder_photo_wit_round_corners).error(R.drawable.avatar_dc_profile_def).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, view.getContext())), 0, RoundedCornersTransformation.CornerType.ALL)).into(view);
        }
    }

    @BindingAdapter({"setAddressFrom"})
    public static void setAddresses(TextView view, DiveCenterProfileFragmentViewModel viewModel) {
        String outString = "";
        if (viewModel != null && viewModel.getDiveCenterProfile().getAddresses() != null) {
            for (Address address : viewModel.getDiveCenterProfile().getAddresses()) {
                outString = outString + address.getName() + " ";
            }
        }
        view.setText(outString);
    }

    @BindingAdapter({"loadAllBrandsText"})
    public static void setBrandsText(TextView view, DiveCenterProfileFragmentViewModel viewModel) {
        if (viewModel != null) {
            view.setText(view.getContext().getString(R.string.al_brands_pattern, viewModel.getDiveCenterProfile().getBrandsCount()));
        }
    }

    @BindingAdapter({"setEmails"})
    public static void setEmailsFrom(TextView view, DiveCenterProfileFragmentViewModel viewModel) {
        StringBuilder outString = new StringBuilder();
        ArrayList<Link> links = new ArrayList<>();
        if (viewModel != null && viewModel.getDiveCenterProfile().getEmails() != null) {
            for (String email : viewModel.getDiveCenterProfile().getEmails()) {
                StringBuilder append = outString.append(email);
                Link link = new Link(email);
                link.setUnderlined(false);
                link.setTextColor(ContextCompat.getColor(view.getContext(), R.color.dive_center_charachteristic_color));
                link.setOnLongClickListener(clickedText -> EmailIntentBuilder.from(view.getContext()).to(email).start());
                link.setOnClickListener(clickedText -> {
//                    if (viewModel.getDiveCenterProfile().isForBooking()) {
                        EventsTracker.trackBookingDcProfileEmailClick();
                        SendRequestActivity.show(view.getContext(), viewModel.getDiveCenterProfile().getDiveSpotBookingId(), viewModel.getDiveCenterProfile().getId());
//                        return;
//                    }
//                    EmailIntentBuilder.from(view.getContext()).to(email).start();
                });
                links.add(link);
                if (viewModel.getDiveCenterProfile().getEmails().indexOf(email) != viewModel.getDiveCenterProfile().getEmails().size() - 1) {
                    append.append(", ");
                }
            }
        }
        view.setText(outString.toString());
        if (links.size() > 0) {
            LinkBuilder.on(view).addLinks(links).build();
        }
    }

    @BindingAdapter({"setPhones"})
    public static void setPhones(TextView view, DiveCenterProfileFragmentViewModel viewModel){
        StringBuilder outString = new StringBuilder();
        ArrayList<Link> links = new ArrayList<>();
        if (viewModel != null && viewModel.getDiveCenterProfile().getPhones() != null) {
            for (String phone : viewModel.getDiveCenterProfile().getPhones()) {
                StringBuilder append = outString.append(phone);
                Link link = new Link(phone);
                link.setUnderlined(false);
                link.setTextColor(ContextCompat.getColor(view.getContext(), R.color.dive_center_charachteristic_color));
                link.setOnClickListener(clickedText -> {
                    if (viewModel.getDiveCenterProfile().isForBooking()) {
                        EventsTracker.trackBookingDcProfilePhoneClick();
                    }
                    PhoneCallIntentBuilder.from(view.getContext()).to(phone).start();
                });
                links.add(link);
                if (viewModel.getDiveCenterProfile().getPhones().indexOf(phone) != viewModel.getDiveCenterProfile().getPhones().size() - 1) {
                    append.append(", ");
                }
            }
        }

        view.setText(outString);
        if (links.size() > 0) {
            LinkBuilder.on(view).addLinks(links).build();
        }
    }

    @BindingAdapter("arrowVisibilityFrom")
    public static void setVisibilityForInstructorsArrow(ImageView view, DiveCenterProfileFragmentViewModel viewModel) {
        if (viewModel != null) {
            if (viewModel.getDiveCenterProfile().getInstructorsCount() != null && Integer.parseInt(viewModel.getDiveCenterProfile().getInstructorsCount()) > 0) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    @BindingAdapter({"loadServiceTypeFrom"})
    public static void setServiceType(TextView view, DiveCenterProfileFragmentViewModel viewModel) {
        if (viewModel != null) {
            switch (viewModel.getDiveCenterProfile().getServiceType()) {
                case COMPANY:
                    view.setText(R.string.company);
                    break;
                case RESELLER:
                    view.setText(R.string.reseller);
                    break;
            }
        }
    }

    private static String getDiveSpotString(int count) {
        if (count > 1 || count == 0) {
            return String.valueOf(count) + DDScannerApplication.getInstance().getString(R.string.dive_spos);
        }
        if (count == 1) {
            return String.valueOf(count) + DDScannerApplication.getInstance().getString(R.string.one_dive_spot);
        }
        return "";
    }

}
