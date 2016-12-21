package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.LanguageChangedEvent;
import com.ddscanner.events.LanguageChosedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class LanguageSearchAdapter extends RecyclerView.Adapter<LanguageSearchAdapter.LanguageSearchViewHolder> {

    private final LayoutInflater layoutInflater;
    private ArrayList<String> languages;
    private Context context;

    public LanguageSearchAdapter(Context context, ArrayList<String> languages) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.languages = languages;
    }

    @Override
    public LanguageSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_list_language, parent, false);
        return new LanguageSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LanguageSearchViewHolder holder, int position) {
        holder.bind(languages.get(position));
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public void animateTo(List<String> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<String> newModels) {
        for (int i = languages.size() - 1; i >= 0; i--) {
            final String model = languages.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<String> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final String model = newModels.get(i);
            if (!languages.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<String> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final String model = newModels.get(toPosition);
            final int fromPosition = languages.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
    
    public String removeItem(int position) {
        final String model = languages.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, String model) {
        languages.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final String model = languages.remove(fromPosition);
        languages.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
    
    class LanguageSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView languageName;

        public LanguageSearchViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            languageName = (TextView) view.findViewById(R.id.language_name);
        }

        public void bind(String name) {
            languageName.setText(name);
        }

        @Override
        public void onClick(View view) {
            DDScannerApplication.bus.post(new LanguageChosedEvent(languages.get(getAdapterPosition())));
        }
    }

}
