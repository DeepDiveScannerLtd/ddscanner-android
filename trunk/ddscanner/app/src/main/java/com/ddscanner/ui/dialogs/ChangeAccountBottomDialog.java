package com.ddscanner.ui.dialogs;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.adapters.AccountsListAdapter;

public class ChangeAccountBottomDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private RecyclerView accountsList;
    private TextView addAccountButton;

    private BottomSheetBehavior.BottomSheetCallback bottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.dialog_choose_account, null);
        accountsList = (RecyclerView) view.findViewById(R.id.users_recycler_view);
        addAccountButton = (TextView) view.findViewById(R.id.add_account_button);
        addAccountButton.setOnClickListener(this);
        dialog.setContentView(view);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(bottomSheetBehaviorCallback);
        }
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        accountsList.setLayoutManager(linearLayoutManager);
        accountsList.setAdapter(new AccountsListAdapter());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_account_button:

                break;
        }
    }
}
