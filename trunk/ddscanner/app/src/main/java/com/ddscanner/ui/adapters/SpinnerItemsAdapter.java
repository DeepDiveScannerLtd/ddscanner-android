package com.ddscanner.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ddscanner.R;

import java.util.List;

public class SpinnerItemsAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;

    public SpinnerItemsAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) inflater.inflate(R.layout.spinner_item, parent, false);
        view.setText(getItem(position));
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) inflater.inflate(R.layout.spinner_drop_down_item, parent, false);
        view.setText(getItem(position));

        if (position == 0) {
            view.setPadding(2 * view.getPaddingLeft(), 2 * view.getPaddingTop(),2 *  view.getPaddingRight(), view.getPaddingBottom());
        } else if (position == getCount() - 1) {
            view.setPadding(2 * view.getPaddingLeft(), view.getPaddingTop(), 2 * view.getPaddingRight(), 2 * view.getPaddingBottom());
        } else {
            view.setPadding(2 * view.getPaddingLeft(), view.getPaddingTop(),2 *  view.getPaddingRight(), view.getPaddingBottom());
        }
        return view;
    }
}
