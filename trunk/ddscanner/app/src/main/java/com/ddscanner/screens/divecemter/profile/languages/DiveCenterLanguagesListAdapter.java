package com.ddscanner.screens.divecemter.profile.languages;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.databinding.ItemDiveCenterLanguageBinding;
import com.ddscanner.entities.Language;

import java.util.ArrayList;

public class DiveCenterLanguagesListAdapter extends RecyclerView.Adapter<DiveCenterLanguagesListAdapter.DiveCenterLanguagListItemViewHolder> {

    private ArrayList<Language> languages = new ArrayList<>();

    public DiveCenterLanguagesListAdapter(ArrayList<Language> languages) {
        this.languages = languages;
    }

    @Override
    public DiveCenterLanguagListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDiveCenterLanguageBinding binding = ItemDiveCenterLanguageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DiveCenterLanguagListItemViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(DiveCenterLanguagListItemViewHolder holder, int position) {
        holder.binding.setViewModel(new DiveCenterLanguageItemViewModel(languages.get(position)));
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    class DiveCenterLanguagListItemViewHolder extends RecyclerView.ViewHolder {

        ItemDiveCenterLanguageBinding binding;

        public DiveCenterLanguagListItemViewHolder(View view) {
            super(view);

            binding = DataBindingUtil.bind(view);
        }

    }

}
