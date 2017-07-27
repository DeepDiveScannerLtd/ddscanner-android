package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.events.SealifeChoosedEvent;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class SealifeSearchAdapter extends RecyclerView.Adapter<SealifeSearchAdapter.SearchListViewHolder> {

    private final LayoutInflater layoutInflater;
    private List<SealifeShort> sealifes;
    private TextView results;
    private RelativeLayout notFounLayout;
    private RecyclerView rcList;
    private static Context context;
    private SealifeSectionedRecyclerViewAdapter sectionAdapter;

    public SealifeSearchAdapter(Context context, List<SealifeShort> models) {
        layoutInflater = LayoutInflater.from(context);
        sealifes = new ArrayList<>(models);
        SealifeSearchAdapter.context = context;
    }

    @Override
    public SearchListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = layoutInflater.inflate(R.layout.item_sealife, parent, false);
        return new SearchListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchListViewHolder holder, int position) {
        final SealifeShort model = sealifes.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return sealifes.size();
    }

    public void animateTo(List<SealifeShort> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<SealifeShort> newModels) {
        for (int i = sealifes.size() - 1; i >= 0; i--) {
            final SealifeShort model = sealifes.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<SealifeShort> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final SealifeShort model = newModels.get(i);
            if (!sealifes.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<SealifeShort> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final SealifeShort model = newModels.get(toPosition);
            final int fromPosition = sealifes.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public SealifeShort removeItem(int position) {
        final SealifeShort model = sealifes.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, SealifeShort model) {
        sealifes.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final SealifeShort model = sealifes.remove(fromPosition);
        sealifes.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void setSectionAdapter(SealifeSectionedRecyclerViewAdapter sectionAdapter) {
        this.sectionAdapter = sectionAdapter;
    }

    public class SearchListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView name;
        private ImageView photo;

        public SearchListViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            photo = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        public void bind(SealifeShort model) {
            name.setText(model.getName());
            Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, model.getImage(), "2")).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, context )), 0, RoundedCornersTransformation.CornerType.TOP)).into(photo);
        }

        @Override
        public void onClick(View v) {
            int position;
            if (sectionAdapter != null) {
                position = sectionAdapter.sectionedPositionToPosition(getAdapterPosition());
            } else {
                position = getAdapterPosition();
            }
            DDScannerApplication.bus.post(new SealifeChoosedEvent(sealifes.get(position)));
        }
    }
    
}
