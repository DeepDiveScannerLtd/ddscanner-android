package travel.ilave.deepdivescanner.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.ui.fragments.SuccessSubscribeDialog;

/**
 * Created by lashket on 17.1.16.
 */
public class SubscribeActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText firstName;
    private EditText lastName;
    private EditText eMail;
    private Button subscribe;
    private ImageView imageView;
    private Map<String,String> map = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = (EditText) findViewById(R.id.name_first);
        lastName = (EditText) findViewById(R.id.sub_lastname);
        eMail = (EditText) findViewById(R.id.email_address);
        subscribe = (Button) findViewById(R.id.button_subscribe);
        subscribe.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.ic_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onClick(View view) {
        map.put("firstName", firstName.getText().toString());
        map.put("lastName", lastName.getText().toString());
        map.put("email", eMail.getText().toString());
        System.out.println(map);
        /*SuccessSubscribeDialog dialog = new SuccessSubscribeDialog();
        dialog.show(getFragmentManager(), "");*/
       // sendSubscribeRequest(map);

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.succes_subscribe_dialog);
        dialog.show();
    }

    private void sendSubscribeRequest(Map<String, String> map) {
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
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, SubscribeActivity.class);
        context.startActivity(intent);
    }

}
