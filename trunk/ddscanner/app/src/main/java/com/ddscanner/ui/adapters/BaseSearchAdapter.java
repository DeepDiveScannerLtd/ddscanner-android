package com.ddscanner.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.events.ObjectChosedEvent;

import java.util.ArrayList;
import java.util.List;

public class BaseSearchAdapter extends RecyclerView.Adapter<BaseSearchAdapter.DiveCentersViewHolder> {
    
    private ArrayList<BaseIdNamePhotoEntity> objectsList = new ArrayList<>();
    private boolean isCheckable;
    private int lastCheckedPosition = -1;

    public BaseSearchAdapter(ArrayList<BaseIdNamePhotoEntity> objectsList, boolean isCheckable) {
        this.isCheckable = isCheckable;
        this.objectsList = new ArrayList<>(objectsList);
    }

    @Override
    public void onBindViewHolder(DiveCentersViewHolder holder, int position) {
        holder.textView.setText(objectsList.get(position).getName());
        if (objectsList.get(position).isActive()) {
            lastCheckedPosition = position;
            holder.checkIcon.setVisibility(View.VISIBLE);
        } else {
            holder.checkIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public DiveCentersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_language, parent, false);
        return new DiveCentersViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (objectsList == null) {
            return 0;
        }
        return objectsList.size();
    }

    public void animateTo(List<BaseIdNamePhotoEntity> models) {
//        this.objectsList = new ArrayList<>();
//        this.objectsList.addAll(models);
//        notifyDataSetChanged();
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

    public BaseIdNamePhotoEntity removeItem(int position) {
        final BaseIdNamePhotoEntity model = objectsList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, BaseIdNamePhotoEntity model) {
        objectsList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final BaseIdNamePhotoEntity model = objectsList.remove(fromPosition);
        objectsList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


    class DiveCentersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textView;
        private ImageView checkIcon;

        DiveCentersViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            textView = (TextView) view.findViewById(R.id.language_name);
            checkIcon = (ImageView) view.findViewById(R.id.checking_icon);
        }

        @Override
        public void onClick(View view) {
            DDScannerApplication.bus.post(new ObjectChosedEvent(objectsList.get(getAdapterPosition())));
            if (lastCheckedPosition != -1) {
                objectsList.get(lastCheckedPosition).setActive(false);
                notifyItemChanged(lastCheckedPosition);
            }
            objectsList.get(getAdapterPosition()).setActive(true);
            notifyItemChanged(getAdapterPosition());

        }

        public void bind(BaseIdNamePhotoEntity entity) {
            textView.setText(entity.getName());
        }

    }

}
