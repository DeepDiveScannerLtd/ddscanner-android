package com.ddscanner.ui.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddscanner.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagViewHolder> {

    private ArrayList<String> strings = new ArrayList<>();

    public TagsAdapter() {
        strings.add("one");
        strings.add("four");
        strings.add("mnogogogog");
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        holder.textView.setText(strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text)
        TextView textView;

        TagViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
