package com.ddscanner.ui.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;

public class LikeView extends LinearLayout {

    private ImageView likeImage;
    private TextView count;
    private boolean isLiked;
    private String likesCount;

    public LikeView(Context context) {
        super(context);

        init(null);
    }

    public LikeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public LikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_like, this);

        likeImage = (ImageView) findViewById(R.id.likes_image);
        count = (TextView) findViewById(R.id.likes_count);
    }

    public void setLikeValues(boolean isLiked, String likesCount) {
        this.isLiked = isLiked;
        this.likesCount = likesCount;
        updateViewsAccordingState();
    }

    private void updateViewsAccordingState() {
        if (isLiked) {
            likeImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_like_review));
        } else {
            likeImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_review_like_empty));
        }
        count.setText(likesCount);
    }

}
