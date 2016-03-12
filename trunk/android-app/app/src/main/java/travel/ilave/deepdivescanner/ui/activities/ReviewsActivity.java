package travel.ilave.deepdivescanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Comment;
import travel.ilave.deepdivescanner.ui.adapters.ReviewsListAdapter;
import travel.ilave.deepdivescanner.utils.SharedPreferenceHelper;

/**
 * Created by lashket on 12.3.16.
 */
public class ReviewsActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Comment> comments;
    private RecyclerView commentsRc;
    private HashMap<String, String> values;
    private ImageView ds_photo;
    private TextView ds_title;
    private TextView ds_reviews;
    private LinearLayout stars;
    private Toolbar toolbar;
    private int rating;
    private Button leaveReview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        comments = (ArrayList<Comment>) getIntent().getSerializableExtra("COMMENTS");
        values = (HashMap<String, String>) getIntent().getSerializableExtra("VALUES");
        rating = getIntent().getIntExtra("RATING", 0);
        findViews();
        toolbarSettings();
        setContent();
    }

    private void findViews() {
        commentsRc = (RecyclerView) findViewById(R.id.comm_rc);
        ds_photo = (ImageView) findViewById(R.id.ds_photo);
        ds_title = (TextView) findViewById(R.id.ds_title);
        ds_reviews = (TextView) findViewById(R.id.ds_reviews);
        stars = (LinearLayout) findViewById(R.id.stars);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        leaveReview = (Button) findViewById(R.id.leave_review);
        leaveReview.setOnClickListener(this);
        commentsRc.setAdapter(new ReviewsListAdapter());
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reviews");
    }

    private void setContent() {
        if (!values.get("image").equals("")) {
            Picasso.with(this).load(values.get("image")).resize(55, 55).centerCrop().into(ds_photo);
        } else {
            ds_photo.setImageDrawable(ContextCompat.getDrawable(ReviewsActivity.this, R.drawable.rewiews_photo));
        }
        ds_title.setText(values.get("name"));
        if (comments != null) {
            ds_reviews.setText(comments.size() + " reviews");
        }

        for (int i = 0; i < rating; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_flag_full_small);
            iv.setPadding(3,0,3,0);
            stars.addView(iv);
        }
        for (int i = 0; i < 5 - rating; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_flag_empty_small);
            iv.setPadding(3, 0, 3, 0);
            stars.addView(iv);
        }
    }

    public static void show(Context context, ArrayList<Comment> comments, HashMap<String, String> values, int rating) {
        Intent intent = new Intent(context, ReviewsActivity.class);
        intent.putExtra("COMMENTS", comments);
        intent.putExtra("VALUES", values);
        intent.putExtra("RATING", rating);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.leave_review:
                LeaveReviewActivity.show(ReviewsActivity.this);
                break;
        }
    }
}
