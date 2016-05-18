package com.ddscanner.ui.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lashket on 18.5.16.
 */
public class SwipableDiveSpotListAdapter
        extends RecyclerSwipeAdapter<SwipableDiveSpotListAdapter.SwipableDiveSpotListViewHolder> {

    private static final String TAG = SwipableDiveSpotListAdapter.class.getSimpleName();
    public static ArrayList<DiveSpot> divespots;
    private Context context;


  /*  public SwipableDiveSpotListAdapter(ArrayList<DiveSpot> divespots, Context context) {
        this.divespots = divespots;
        this.context = context;
    }*/


    @Override
    public SwipableDiveSpotListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_product_swipable, viewGroup, false);
        return new SwipableDiveSpotListViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(SwipableDiveSpotListViewHolder swipableDiveSpotListViewHolder, int i) {
     /*   DiveSpot divespot = new DiveSpot();
        divespot = divespots.get(i);
        swipableDiveSpotListViewHolder.progressBar.getIndeterminateDrawable().
                setColorFilter(context.getResources().getColor(R.color.primary),
                        PorterDuff.Mode.MULTIPLY);
        if (divespot.getImage() != null) {
            Picasso.with(context).load(divespot.getImage()).resize(130, 130).centerCrop()
                    .transform(new TransformationRoundImage(2,0))
                    .into(swipableDiveSpotListViewHolder.imageView,
                            new ImageLoadedCallback(swipableDiveSpotListViewHolder.progressBar) {
                                @Override
                                public void onSuccess() {
                                    if (this.progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
        } else {
            swipableDiveSpotListViewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.list_photo_default));
        }
        if(divespot.getName() != null) {
            swipableDiveSpotListViewHolder.title.setText(divespot.getName());
        }
        swipableDiveSpotListViewHolder.stars.removeAllViews();
        for (int k = 0; k < divespot.getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(0,0,5,0);
            swipableDiveSpotListViewHolder.stars.addView(iv);
        }
        for (int k = 0; k < 5 - divespot.getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_empty);
            iv.setPadding(0,0,5,0);
            swipableDiveSpotListViewHolder.stars.addView(iv);
        }
*/

        swipableDiveSpotListViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        swipableDiveSpotListViewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right,
                swipableDiveSpotListViewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

        swipableDiveSpotListViewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });
    }

    @Override
    public int getItemCount() {
//        if (divespots == null) { return 0;
        return 10;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class SwipableDiveSpotListViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        protected ImageView imageView;
        protected TextView title;
        protected LinearLayout stars;
        protected ProgressBar progressBar;
        public Button mButton;


        public SwipableDiveSpotListViewHolder(View v) {
            super(v);
            swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe);
            imageView = (ImageView) v.findViewById(R.id.product_logo);
            title = (TextView) v.findViewById(R.id.product_title);
            stars = (LinearLayout) v.findViewById(R.id.stars);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            mButton = (Button) v.findViewById(android.R.id.button1);
        }

    }


    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public  ImageLoadedCallback(ProgressBar progBar){
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }

}
