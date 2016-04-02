package com.ddscanner.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.ddscanner.R;
import com.squareup.picasso.Picasso;

/**
 * Created by lashket on 15.12.15.
 */
public class IconsAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private String[] imageUrls;

    public IconsAdapter(Context context, String[] imageUrls) {
        super(context, R.layout.sealife_icons, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sealife_icons, parent, false);
        }

        Picasso
                .with(context)
                .load(imageUrls[position])
                .into((ImageView) convertView);
        return convertView;
    }
}
