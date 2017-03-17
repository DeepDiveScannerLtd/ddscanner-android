package com.ddscanner.screens.boocking.offers;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;

import com.ddscanner.R;
import com.ddscanner.databinding.ActivityOffersBinding;
import com.ddscanner.entities.DiveCenterShort;
import com.ddscanner.entities.Offer;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

import java.util.ArrayList;

public class OffersActivity extends BaseAppCompatActivity {

    private ActivityOffersBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_offers);
        binding.offersList.setLayoutManager(new LinearLayoutManager(this));
//        setupToolbar(R.id.toolbar, R.string.offers);
        setupList();
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, OffersActivity.class);
        context.startActivity(intent);
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
        Offer offer = new Offer("1", "Lashketuk", "Good offer", "1500 B", spots, "https://pp.userapi.com/c626824/v626824069/3fae/lZ_07Lvm9MA.jpg");
        ArrayList<Offer> offers = new ArrayList<>();
        offers.add(offer);
        offers.add(offer);
        binding.offersList.setAdapter(new OffersListAdapter(offers, diveCenterShorts));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}
