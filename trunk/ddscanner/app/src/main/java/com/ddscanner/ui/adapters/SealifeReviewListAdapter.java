package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.screens.sealife.details.SealifeDetailsActivity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class SealifeReviewListAdapter extends RecyclerView.Adapter<SealifeReviewListAdapter.SealifeReviewItemViewHolder> {

    private ArrayList<SealifeShort> sealifes = new ArrayList<>();
    private Context context;

    public SealifeReviewListAdapter(ArrayList<SealifeShort> sealifes, Context context) {
        this.sealifes = sealifes;
        this.context = context;
    }

    @Override
    public SealifeReviewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_sealife, parent, false);
        return new SealifeReviewItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SealifeReviewItemViewHolder holder, int position) {
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, sealifes.get(position).getImage(), "1"))
                .resize(Math.round(Helpers.convertDpToPixel(100, context)), Math.round(Helpers.convertDpToPixel(60, context)))
                .centerCrop()
                .placeholder(R.drawable.placeholder_photos_activity)
                .transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context)), 0, RoundedCornersTransformation.CornerType.ALL))
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return sealifes.size();
    }

    protected class SealifeReviewItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView photo;

        SealifeReviewItemViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            photo = (ImageView) view.findViewById(R.id.photo);
        }

        @Override
        public void onClick(View view) {
            SealifeDetailsActivity.show(context, sealifes.get(getAdapterPosition()).getId());
        }
    }

}
