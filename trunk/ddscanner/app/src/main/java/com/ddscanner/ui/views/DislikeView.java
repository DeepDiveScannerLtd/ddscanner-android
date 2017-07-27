package com.ddscanner.ui.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;

public class DislikeView extends LinearLayout {

    private ImageView dislikeImage;
    private TextView count;
    private boolean isDisliked;
    private String dislikesCount;

    public DislikeView(Context context) {
        super(context);

        init(null);
    }

    public DislikeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public DislikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_dislike, this);
        dislikeImage = findViewById(R.id.dislikes_image);
        count = findViewById(R.id.dislikes_count);
    }

    public void setDisikeValues(boolean isDisliked, String dislikesCount) {
        this.isDisliked = isDisliked;
        this.dislikesCount = dislikesCount;
        updateViewsAccordingState();
    }

    private void updateViewsAccordingState() {
        if (isDisliked) {
            dislikeImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_review_dislike));
        } else {
            dislikeImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_review_dislike_empty));
        }
        count.setText(dislikesCount);
    }
    
}
