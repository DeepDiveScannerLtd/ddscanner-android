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

import java.util.ArrayList;
import java.util.List;

public class CharacteristicSpinnerItemsAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> values;
    private Activity activity;
    private ArrayList<String> data;
    public Resources res;
    LayoutInflater inflater;

    public CharacteristicSpinnerItemsAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<String> objects) {
        super(activitySpinner, textViewResourceId, objects);
        this.data = objects;
        inflater = (LayoutInflater)activitySpinner.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = getRowView(position, parent);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = getRowView(position, parent);
        return view;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    private View getRowView(int position, ViewGroup parent) {
        View row = inflater.inflate(R.layout.item_language_spinner, parent, false);
        TextView textView = (TextView) row.findViewById(R.id.spinner_text);
        textView.setText(data.get(position));
        if (position == 0) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.defaultGrayTitleColor));
        }
        return row;
    }

}