package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Sealife;

/**
 * Created by lashket on 17.2.16.
 */
public class SealifeDetails extends AppCompatActivity {

    private TextView sealifeName;
    private TextView sealifeDescription;
    private ImageView close;
    private ImageView photo;
    private Sealife sealife;
    private String pathMedium;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sealife_full);
        findViews();
        sealife =(Sealife) getIntent().getSerializableExtra("SEALIFE");
        pathMedium = getIntent().getStringExtra("PATH");
        Picasso.with(this).load(pathMedium + sealife.getImage()).into(photo);
        sealifeName.setText(sealife.getName());
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void findViews() {
        sealifeName = (TextView) findViewById(R.id.sealife_name);
        close = (ImageView) findViewById(R.id.sealife_close);
        photo = (ImageView) findViewById(R.id.sealife_full_photo);
    }

    public static void show(Context context, Sealife sealife, String pathMedium) {
        Intent intent = new Intent(context, SealifeDetails.class);
        intent.putExtra("SEALIFE", sealife);
        intent.putExtra("PATH", pathMedium);
        context.startActivity(intent);
    }
}
