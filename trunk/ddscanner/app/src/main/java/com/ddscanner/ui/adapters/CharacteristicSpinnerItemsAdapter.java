package com.ddscanner.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;

public class CharacteristicSpinnerItemsAdapter extends ArrayAdapter<String> {

    private ArrayList<String> data;
    public Resources res;
    private LayoutInflater inflater;
    private int padding;

    public CharacteristicSpinnerItemsAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<String> objects) {
        super(activitySpinner, textViewResourceId, objects);
        this.data = objects;
        inflater = (LayoutInflater)activitySpinner.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        padding = Math.round(Helpers.convertDpToPixel(15, getContext()));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getRowView(position, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getRowView(position, parent, true);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    private View getRowView(int position, ViewGroup parent, boolean isDropDown) {
        View row = inflater.inflate(R.layout.item_language_spinner, parent, false);
        TextView textView = row.findViewById(R.id.spinner_text);
        textView.setText(data.get(position));
        if (!isDropDown) {
            row.setPadding(0, padding, padding, padding);
        }
        if (position == 0) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.defaultGrayTitleColor));
        }
        return row;
    }

}
