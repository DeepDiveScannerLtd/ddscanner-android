package com.ddscanner.screens.diecenters.list;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.interfaces.ListItemClickListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divecenter.request.SendRequestActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;

import java.util.ArrayList;

public class DiveCentersActivityNew extends BaseAppCompatActivity implements DialogClosedListener{

    private DDScannerRestClient.ResultListener<ArrayList<DiveCenter>> diveCentersResponseEntityResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveCenter>>() {
        @Override
        public void onSuccess(ArrayList<DiveCenter> result) {
            diveCentersListAdapterNew.setData(result);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_CENTERS_CLUSTER_MANAGER_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_DIVE_CENTERS_CLUSTER_MANAGER_UNEXPECTED_ERROR, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    public static void show(Context context, String diveSpotId) {
        Intent intent = new Intent(context, DiveCentersActivityNew.class);
        intent.putExtra("id", diveSpotId);
        context.startActivity(intent);
    }

    private RecyclerView diveCentersList;
    private String diveSpotId;
    private DiveCentersListAdapterNew diveCentersListAdapterNew;
    private ListItemClickListener<DiveCenter> listItemClickListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dve_centers_new);
        setupToolbar(R.string.dive_centers, R.id.toolbar);
        diveSpotId = getIntent().getStringExtra("id");
        listItemClickListener = item -> {
            SendRequestActivity.show(this, diveSpotId, item.getId());
        };
        diveCentersListAdapterNew = new DiveCentersListAdapterNew(listItemClickListener);
        diveCentersList = findViewById(R.id.dive_centers_list);
        diveCentersList.setLayoutManager(new LinearLayoutManager(this));
        diveCentersList.setAdapter(diveCentersListAdapterNew);
//        diveCentersListAdapterNew.setListItemClickListener(listItemClickListener);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenters(diveSpotId, diveCentersResponseEntityResultListener);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        onBackPressed();
    }
}
