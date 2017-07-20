package com.ddscanner.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Translation;
import com.ddscanner.events.ChangeTranslationEvent;

import java.util.ArrayList;

public class TranslationsListAdapter extends RecyclerView.Adapter<TranslationsListAdapter.TranslationListViewHolder> {

    private ArrayList<Translation> translations = new ArrayList<>();

    @Override
    public TranslationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TranslationListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_translation, parent, false));
    }

    @Override
    public void onBindViewHolder(TranslationListViewHolder holder, int position) {
        holder.diveSpotName.setText(translations.get(position).getName());
        holder.languageName.setText(translations.get(position).getLanguage());
    }

    @Override
    public int getItemCount() {
        return translations.size();
    }

    public void add(Translation translation) {
        if (translations.size() > 0) {
            for (Translation object : translations) {
                if (object.getCode().equals(translation.getCode())) {
                    translations.set(translations.indexOf(object), translation);
                    notifyDataSetChanged();
                    return;
                }
            }
        }
        translations.add(translation);
        notifyItemInserted(translations.size());
    }

    public ArrayList<Translation> getTranslations() {
        return this.translations;
    }

    public void addTranslationsList(ArrayList<Translation> translations) {
        this.translations.addAll(translations);
        notifyDataSetChanged();
    }

    private void removeTranslation(int position) {
        translations.remove(position);
        notifyItemRemoved(position);
    }

    class TranslationListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView languageName;
        private TextView diveSpotName;
        private ImageView removeTranslations;

        TranslationListViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            languageName = (TextView) view.findViewById(R.id.language_name);
            diveSpotName = (TextView) view.findViewById(R.id.name);
            removeTranslations = (ImageView) view.findViewById(R.id.remove);
            removeTranslations.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.remove:
                    removeTranslation(getAdapterPosition());
                    break;
                default:
                    DDScannerApplication.bus.post(new ChangeTranslationEvent(translations.get(getAdapterPosition())));
                    break;
            }
        }
    }

}
