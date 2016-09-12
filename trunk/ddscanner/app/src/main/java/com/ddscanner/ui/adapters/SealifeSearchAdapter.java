package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Sealife;
import com.ddscanner.events.SealifeChoosedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 7.4.16.
 */
public class SealifeSearchAdapter extends RecyclerView.Adapter<SealifeSearchAdapter.SearchListViewHolder> {

    private final LayoutInflater mInflater;
    private List<Sealife> mModels;
    private TextView results;
    private RelativeLayout notFounLayout;
    private RecyclerView rcList;
    private static Context context;

    public SealifeSearchAdapter(Context context, List<Sealife> models) {
        mInflater = LayoutInflater.from(context);
        mModels = new ArrayList<>(models);
        this.context = context;
    }

    @Override
    public SearchListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.list_sealife_search_item, parent, false);
        return new SearchListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchListViewHolder holder, int position) {
        final Sealife model = mModels.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void animateTo(List<Sealife> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Sealife> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final Sealife model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Sealife> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Sealife model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Sealife> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Sealife model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Sealife removeItem(int position) {
        final Sealife model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Sealife model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Sealife model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class SearchListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView tvText;

        public SearchListViewHolder(View itemView) {
            super(itemView);

            tvText = (TextView) itemView.findViewById(R.id.tvText);
            tvText.setOnClickListener(this);
        }

        public void bind(Sealife model) {
            tvText.setText(model.getName());
        }

        @Override
        public void onClick(View v) {
            DDScannerApplication.bus.post(new SealifeChoosedEvent(mModels.get(getPosition())));
        }
    }
    
}
