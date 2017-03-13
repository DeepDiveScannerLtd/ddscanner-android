package com.ddscanner.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public BaseSearchAdapter(ArrayList<BaseIdNamePhotoEntity> objectsList, boolean isCheckable) {
        this.objectsList = objectsList;
        this.isCheckable = isCheckable;
    }

    @Override
    public void onBindViewHolder(DiveCentersViewHolder holder, int position) {
        holder.bind(objectsList.get(position));
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
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<BaseIdNamePhotoEntity> newModels) {
        for (int i = objectsList.size() - 1; i >= 0; i--) {
            final BaseIdNamePhotoEntity model = objectsList.get(i);
            if (!newModels.contains(model)) {
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

        DiveCentersViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            textView =  (TextView) view.findViewById(R.id.language_name);
        }

        @Override
        public void onClick(View view) {
            DDScannerApplication.bus.post(new ObjectChosedEvent(objectsList.get(getAdapterPosition())));
        }

        public void bind(BaseIdNamePhotoEntity entity) {
            textView.setText(entity.getName());
        }

    }

}
