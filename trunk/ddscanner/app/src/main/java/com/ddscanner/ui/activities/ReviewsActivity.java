package com.ddscanner.ui.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.events.ShowUserDialogEvent;
import com.ddscanner.ui.adapters.ReviewsListAdapter;
import com.ddscanner.ui.dialogs.ProfileDialog;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.Helpers;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Comment> comments;
    private RecyclerView commentsRc;

    private Toolbar toolbar;
    private FloatingActionButton leaveReview;

    private String diveSpotId;
    private Helpers helpers = new Helpers();

    private boolean isHasNewComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Bundle bundle = getIntent().getExtras();
        comments = (ArrayList<Comment>) bundle.getSerializable("COMMENTS");
        diveSpotId = bundle.getString("DIVESPOTID");
        findViews();
        toolbarSettings();
        setContent();
    }

    private void findViews() {
        commentsRc = (RecyclerView) findViewById(R.id.reviews_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        leaveReview = (FloatingActionButton) findViewById(R.id.fab_write_review);
        leaveReview.setOnClickListener(this);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.reviews);
    }

    private void setContent() {
        commentsRc.setHasFixedSize(true);
        commentsRc.setLayoutManager(new LinearLayoutManager(this));
        commentsRc.setAdapter(new ReviewsListAdapter(comments, ReviewsActivity.this));
    }

    public static void show(Context context, ArrayList<Comment> comments, String id) {
        Intent intent = new Intent(context, ReviewsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("COMMENTS", comments);
        bundle.putString("DIVESPOTID", id);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 9001) {
            if(resultCode == Activity.RESULT_OK){
                Comment comment = (Comment)data.getSerializableExtra("COMMENT");
                if (comments == null) {
                    comments = new ArrayList<Comment>();
                }
                comments.add(0, comment);
                commentsRc.setAdapter(new ReviewsListAdapter(comments, ReviewsActivity.this));
                isHasNewComment = true;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_write_review:
                Intent intent = new Intent(ReviewsActivity.this, LeaveReviewActivity.class);
                intent.putExtra("id", diveSpotId);
                startActivityForResult(intent, 9001);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Subscribe
    public void showDialog(ShowUserDialogEvent event) {
        helpers.showDialog(event.getUser(), getFragmentManager());
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        if (isHasNewComment) {
            setResult(RESULT_OK, returnIntent);
            finish();
        } else {
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }
}
