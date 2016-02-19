package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import travel.ilave.deepdivescanner.R;

/**
 * Created by lashket on 17.2.16.
 */
public class SealifeDetails extends AppCompatActivity {

    private TextView sealifeName;
    private TextView sealifeDescription;
    private ImageView close;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sealife_full);
        findViews();
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
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, SealifeDetails.class);
        context.startActivity(intent);
    }
}
