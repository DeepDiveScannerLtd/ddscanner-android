package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

    private TextView length,weight,depth,scname,order,distribution,scclass,habitat;
    private ImageView photo;
    private Sealife sealife;
    private String pathMedium;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sealife_full);
        findViews();
        sealife =(Sealife) getIntent().getSerializableExtra("SEALIFE");
        pathMedium = getIntent().getStringExtra("PATH");
        Picasso.with(this).load(pathMedium + sealife.getImage()).into(photo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(sealife.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContent();

    }

    public void findViews() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_collapse);
        photo = (ImageView) findViewById(R.id.sealife_full_photo);
        length = (TextView) findViewById(R.id.length);
        weight = (TextView) findViewById(R.id.weight);
        depth = (TextView) findViewById(R.id.depth);
        scname = (TextView) findViewById(R.id.scname);
        order = (TextView) findViewById(R.id.order);
        distribution = (TextView) findViewById(R.id.distribution);
        scclass = (TextView) findViewById(R.id.scclass);
        habitat = (TextView) findViewById(R.id.habitat);
    }

    private void setContent() {
        length.setText(sealife.getLength());
        weight.setText(sealife.getWeight());
        depth.setText(sealife.getDepth());
        scname.setText(sealife.getScName());
        order.setText(sealife.getOrder());
        distribution.setText(sealife.getDistribution());
        scclass.setText(sealife.getScCLass());
        habitat.setText(sealife.getHabitat());
    }

    public static void show(Context context, Sealife sealife, String pathMedium) {
        Intent intent = new Intent(context, SealifeDetails.class);
        intent.putExtra("SEALIFE", sealife);
        intent.putExtra("PATH", pathMedium);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
