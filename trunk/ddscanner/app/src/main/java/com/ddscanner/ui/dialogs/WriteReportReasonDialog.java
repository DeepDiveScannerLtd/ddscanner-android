package com.ddscanner.ui.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.interfaces.ReportReasonIsWritenListener;

public class WriteReportReasonDialog extends DialogFragment {

    private EditText reasonText;
    private TextView sendButton;
    private TextView cancelButton;
    private TextView errorView;

    public WriteReportReasonDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_report_other, null);
        builder.setTitle(null);
        builder.setView(view);
        reasonText = view.findViewById(R.id.reason_input);
        errorView = view.findViewById(R.id.error_view);
        sendButton = view.findViewById(R.id.send_button);
        cancelButton = view.findViewById(R.id.cancel_button);
        sendButton.setOnClickListener(view1 -> {
            if (reasonText.getText().toString().trim().length() > 0) {
                ReportReasonIsWritenListener reportReasonIsWritenListener;
                reportReasonIsWritenListener = (ReportReasonIsWritenListener) getActivity();
                reportReasonIsWritenListener.onReasonWriten(reasonText.getText().toString().trim());
                this.dismiss();
                return;
            }
            errorView.setVisibility(View.VISIBLE);
        });
        cancelButton.setOnClickListener(view1 -> this.dismiss());
        return builder.create();
    }
}
