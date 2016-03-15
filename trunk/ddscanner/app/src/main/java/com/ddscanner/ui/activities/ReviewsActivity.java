package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
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

import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.ui.adapters.ReviewsListAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

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
        Bundle bundle = getIntent().getExtras();
        comments = (ArrayList<Comment>) bundle.getSerializable("COMMENTS");
//        Log.i("Reviews", comments.get(0).getComment());
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
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reviews");
    }

    private void setContent() {
        if (!values.get("image").equals("")) {
            Picasso.with(this).load(values.get("image")).resize(55, 55).centerCrop().transform(new RoundedTransformation(3,3)).into(ds_photo);
        } else {
            ds_photo.setImageDrawable(ContextCompat.getDrawable(ReviewsActivity.this, R.drawable.rewiews_photo));
        }
        ds_title.setText(values.get("name"));
        if (comments != null) {
            if (comments.size() == 1) {
                ds_reviews.setText(comments.size() + " review");
            } else {
                ds_reviews.setText(comments.size() + " reviews");
            }
        }
        commentsRc.setHasFixedSize(true);
        commentsRc.setLayoutManager(new LinearLayoutManager(this));
        commentsRc.setAdapter(new ReviewsListAdapter(comments, ReviewsActivity.this));
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
        Bundle bundle = new Bundle();
        bundle.putSerializable("COMMENTS", comments);
        intent.putExtras(bundle);
        intent.putExtra("VALUES", values);
        intent.putExtra("RATING", rating);
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
                Log.i("ReviewsActivity", comment.getComment());
                if (comments == null) {
                    comments = new ArrayList<Comment>();
                }
                comments.add(0, comment);
                commentsRc.setAdapter(new ReviewsListAdapter(comments, ReviewsActivity.this));
                if (comments.size() == 1) {
                    ds_reviews.setText(comments.size() + " review");
                } else {
                    ds_reviews.setText(comments.size() + " reviews");
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.leave_review:
                Intent intent = new Intent(ReviewsActivity.this, LeaveReviewActivity.class);
                intent.putExtra("id", values.get("id"));
                startActivityForResult(intent, 9001);
                break;
        }
    }

    public class RoundedTransformation implements com.squareup.picasso.Transformation {
        private final int radius;
        private final int margin;  // dp

        // radius is corner radii in dp
        // margin is the board in dp
        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }

            return output;
        }

        @Override
        public String key() {
            return "rounded";
        }
    }
}
