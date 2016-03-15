package travel.ilave.deepdivescanner.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import travel.ilave.deepdivescanner.R;

/**
 * Created by lashket on 28.1.16.
 */
public class SubscribeDialog extends DialogFragment implements View.OnClickListener {

    private Context context;
    private EditText firstName;
    private EditText lastName;
    private EditText eMail;
    private Button btnOk;
    private Button btnCancel;
    private Map<String,String> map = new HashMap<String, String>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getBaseContext();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.succes_subscribe_dialog, null);
        builder.setView(dialogView);
        btnOk = (Button) dialogView.findViewById(R.id.okbutton_subscribe);
        btnCancel = (Button) dialogView.findViewById(R.id.cancel_button_subscribe);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


        Dialog dialog = builder.create();
        return dialog;
    }

   /* private void sendSubscribeRequest(Map<String, String> map) {
        RestClient.getServiceInstance().subscribe(map, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                System.out.println(s.getBody());
                System.out.println(s.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error.getMessage());
            }
        });
    }*/

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String version = new String();
        intent.setData(Uri.parse("mailto:"));
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@ddscanner.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback about DDScanner");
        intent.putExtra(Intent.EXTRA_TEXT, "My device info:"+"\nOS version: " + System.getProperty("os.version") + "\nAPI level: " + Build.VERSION.SDK
                + "\nDevice: " + Build.DEVICE + "\nModel: " + Build.MODEL + "\nProduct: " + Build.PRODUCT + "\nApp version: " + version + "\n--" + "\nMy feedback:" + "\n");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.okbutton_subscribe:
                sendEmail();
                this.dismiss();
                break;
            case R.id.cancel_button_subscribe:
                this.dismiss();
                break;
            default:
                break;
        }
    }
}
