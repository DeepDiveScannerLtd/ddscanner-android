package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Language;
import com.ddscanner.events.LanguageChosedEvent;

import java.util.ArrayList;
import java.util.List;

public class LanguageSearchAdapter extends RecyclerView.Adapter<LanguageSearchAdapter.LanguageSearchViewHolder> {

    private final LayoutInflater layoutInflater;
    private ArrayList<Language> languages;
    private Context context;

    public LanguageSearchAdapter(Context context, ArrayList<Language> languages) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.languages = new ArrayList<>(languages);
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

    public void animateTo(List<Language> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Language> newModels) {
        for (int i = languages.size() - 1; i >= 0; i--) {
            final Language model = languages.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Language> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Language model = newModels.get(i);
            if (!languages.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Language> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Language model = newModels.get(toPosition);
            final int fromPosition = languages.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
    
    public Language removeItem(int position) {
        final Language model = languages.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Language model) {
        languages.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Language model = languages.remove(fromPosition);
        languages.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
    
    class LanguageSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView languageName;

        public LanguageSearchViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            languageName = view.findViewById(R.id.language_name);
        }

        public void bind(Language name) {
            languageName.setText(name.getName());
        }

        @Override
        public void onClick(View view) {
            DDScannerApplication.bus.post(new LanguageChosedEvent(languages.get(getAdapterPosition())));
        }
    }

}
