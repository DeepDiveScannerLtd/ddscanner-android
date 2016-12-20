package com.ddscanner.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.AddTranslationClickedEvent;
import com.ddscanner.events.LanguageChangedEvent;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class LanguagesSpinnerAdapter extends ArrayAdapter<String> {

    private static final String TAG = LanguagesSpinnerAdapter.class.getSimpleName();

    private Context context;
    private List<String> values;
    private Activity activity;
    private ArrayList<String> data;
    public Resources res;
    LayoutInflater inflater;

    public LanguagesSpinnerAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<String> objects) {
        super(activitySpinner, textViewResourceId, objects);
        this.data = objects;
        inflater = (LayoutInflater)activitySpinner.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == data.size()) {
            View view;
            view = inflater.inflate(R.layout.item_spinner_add_item, parent, false);
            return view;
        }
        View row = inflater.inflate(R.layout.item_language_spinner, parent, false);
        TextView textView = (TextView) row.findViewById(R.id.spinner_text);
        textView.setText(data.get(position));
        if (position == 0) {
            textView.setTextColor(Color.GRAY);
        }

        return row;
    }

    @Override
    public int getCount() {
        return data.size() + 1;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (position == data.size()) {
            View view;
            view = inflater.inflate(R.layout.item_spinner_add_item, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DDScannerApplication.bus.post(new AddTranslationClickedEvent());
                }
            });
            return view;
        }
        View row = inflater.inflate(R.layout.item_language_spinner, parent, false);
        TextView textView = (TextView) row.findViewById(R.id.spinner_text);
        textView.setText(data.get(position));
        if (position == 0) {
            textView.setTextColor(Color.GRAY);
            row.setOnClickListener(null);
        }
        row.setPadding(Math.round(Helpers.convertDpToPixel(25, getContext())),Math.round(Helpers.convertDpToPixel(15, getContext())),0,Math.round(Helpers.convertDpToPixel(15, getContext())));
        return row;
    }
}
