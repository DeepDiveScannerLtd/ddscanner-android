package com.ddscanner.screens.profile.divecenter;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Address;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

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
            if (viewModel.getDiveCenterProfile().getLanguages() == null) {
                view.setText("0");
                return;
            }
            if (viewModel.getDiveCenterProfile().getLanguages().size() < 3) {
                view.setText(String.format("%s, %s", viewModel.getDiveCenterProfile().getLanguages().get(0), viewModel.getDiveCenterProfile().getLanguages().get(1)));
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
    public static void mySpotsCount(Button view, DiveCenterProfileFragmentViewModel diveCenterProfileFragmentViewModel) {
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

    @BindingAdapter({"setEmails"})
    public static void setEmailsFrom(TextView view, DiveCenterProfileFragmentViewModel viewModel) {
        StringBuilder outString = new StringBuilder();
        if (viewModel != null && viewModel.getDiveCenterProfile().getEmails() != null) {
            for (String email : viewModel.getDiveCenterProfile().getEmails()) {
                StringBuilder append = outString.append(email);
                if (viewModel.getDiveCenterProfile().getEmails().indexOf(email) != viewModel.getDiveCenterProfile().getEmails().size() - 1) {
                    append.append(", ");
                }
            }
        }
        view.setText(outString.toString());
    }

    @BindingAdapter({"setPhones"})
    public static void setPhones(TextView view, DiveCenterProfileFragmentViewModel viewModel){
        StringBuilder outString = new StringBuilder();
        if (viewModel != null && viewModel.getDiveCenterProfile().getPhones() != null) {
            for (String phone : viewModel.getDiveCenterProfile().getPhones()) {
                StringBuilder append = outString.append(phone);
                if (viewModel.getDiveCenterProfile().getPhones().indexOf(phone) != viewModel.getDiveCenterProfile().getPhones().size() - 1) {
                    append.append(", ");
                }
            }
        }
        view.setText(outString);
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
