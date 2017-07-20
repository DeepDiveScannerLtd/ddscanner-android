package com.ddscanner.screens.booking.offers.dailytours;

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
import com.ddscanner.entities.DiveCenterShort;
import com.ddscanner.entities.Offer;

import java.util.ArrayList;

public class DalyToursFragment extends Fragment {

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
        setupList();
    }

    private void setupList() {
        ArrayList<DiveCenterShort> diveCenterShorts = new ArrayList<>();
        DiveCenterShort diveCenterShort = new DiveCenterShort();
        diveCenterShort.setAddress("Minsk bla bla bla");
        diveCenterShort.setName("Lashketuk");
        diveCenterShort.setPhoto("https://pp.userapi.com/c626824/v626824069/3fae/lZ_07Lvm9MA.jpg");
        diveCenterShorts.add(diveCenterShort);
        diveCenterShorts.add(diveCenterShort);
        diveCenterShorts.add(diveCenterShort);
        diveCenterShorts.add(diveCenterShort);
        diveCenterShorts.add(diveCenterShort);
        diveCenterShorts.add(diveCenterShort);
        ArrayList<String> spots = new ArrayList<>();
        spots.add("Kon mok");
        spots.add("Blue Hole");
        spots.add("Tuk tuker");
        Offer offer = new Offer("1", "Good offer", "1500 B", spots, "https://pp.userapi.com/c626824/v626824069/3fae/lZ_07Lvm9MA.jpg");
        ArrayList<Offer> offers = new ArrayList<>();
        offers.add(offer);
        offers.add(offer);
        binding.offersList.setAdapter(new DailyToursListAdapter(offers, diveCenterShorts));
    }

}
