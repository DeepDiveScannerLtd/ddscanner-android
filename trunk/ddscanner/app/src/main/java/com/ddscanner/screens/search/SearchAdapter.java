package com.ddscanner.screens.search;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.interfaces.ListItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private ArrayList<BaseIdNamePhotoEntity> objectsList = new ArrayList<>();
    private ListItemClickListener<BaseIdNamePhotoEntity> listItemClickListener;
    private ArrayList<BaseIdNamePhotoEntity> checkdObjectsList = new ArrayList<>();

    public SearchAdapter(ListItemClickListener<BaseIdNamePhotoEntity> listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public void setObjectsList(ArrayList<BaseIdNamePhotoEntity> baseIdNamePhotoEntities) {
        this.objectsList = baseIdNamePhotoEntities;
        notifyDataSetChanged();
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_search_base, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        holder.name.setText(objectsList.get(position).getName());
        if (objectsList.get(position).isActive()) {
            holder.checkingIcon.setVisibility(View.VISIBLE);
        } else {
            holder.checkingIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return objectsList.size();
    }

    public ArrayList<BaseIdNamePhotoEntity> getCheckdObjectsList() {
        return this.checkdObjectsList;
    }

    public void animateTo(List<BaseIdNamePhotoEntity> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
        notifyDataSetChanged();
    }

    private void applyAndAnimateRemovals(List<BaseIdNamePhotoEntity> newModels) {
        for (int i = objectsList.size() - 1; i >= 0; i--) {
            final BaseIdNamePhotoEntity model = objectsList.get(i);
            if (!newModels.contains(model) && !objectsList.get(i).isActive()) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<BaseIdNamePhotoEntity> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final BaseIdNamePhotoEntity model = newModels.get(i);
            if (!objectsList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<BaseIdNamePhotoEntity> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final BaseIdNamePhotoEntity model = newModels.get(toPosition);
            final int fromPosition = objectsList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private BaseIdNamePhotoEntity removeItem(int position) {
        final BaseIdNamePhotoEntity model = objectsList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, BaseIdNamePhotoEntity model) {
        objectsList.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final BaseIdNamePhotoEntity model = objectsList.remove(fromPosition);
        objectsList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.language_name)
        TextView name;
        @BindView(R.id.checking_icon)
        ImageView checkingIcon;


        SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            if (objectsList.get(getAdapterPosition()).isActive()) {
                objectsList.get(getAdapterPosition()).setActive(false);
                notifyItemChanged(getAdapterPosition());
                checkdObjectsList.remove(objectsList.get(getAdapterPosition()));
                return;
            }
            objectsList.get(getAdapterPosition()).setActive(true);
            notifyItemChanged(getAdapterPosition());
            checkdObjectsList.add(objectsList.get(getAdapterPosition()));
        }
    }

}
