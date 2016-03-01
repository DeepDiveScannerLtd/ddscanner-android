package travel.ilave.deepdivescanner.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Offer;

/**
 * Created by Admin on 28.11.2015.
 */
public class OffersAdapter extends BaseAdapter {
    ArrayList<Offer> mList;
    LayoutInflater mInflater;
    Context mContext;

    private OnOptionSelectedListener onOptionSelectedListener;

    public OffersAdapter(Context context, ArrayList<Offer> list, OnOptionSelectedListener onOptionSelectedListener) {
        mList = list;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onOptionSelectedListener = onOptionSelectedListener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Offer getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Offer product = mList.get(position);
        if (mList.get(position).isExpanded()) {
            View view = mInflater.inflate(R.layout.full_offer_item, parent, false);
            TextView title = (TextView) view.findViewById(R.id.offer_title);
            title.setText(product.getName());

            TextView durationText = (TextView) view.findViewById(R.id.offer_duration);
            TextView startText = (TextView) view.findViewById(R.id.offer_start);
            TextView inclusiveText = (TextView) view.findViewById(R.id.offer_inclusive);
            durationText.setText(product.getDuration());
            startText.setText(product.getMeetingPoint());
            inclusiveText.setText(product.getInclusions());
            Button choseOption = (Button) view.findViewById(R.id.chose_this_option);
            choseOption.setTag(position);
            choseOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOptionSelectedListener.onOptionsSelected(mList.get((Integer) view.getTag()));
                }
            });

            return view;
        }
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.simple_offer_item, parent, false);
        }
        TextView title = (TextView) view.findViewById(R.id.simple_offer_item);
        title.setText(product.getName());

        return view;
    }

    public void setItemsUnexpanded() {
        for (Offer item : mList) {
            item.setIsExpanded(false);
        }
    }

    public interface OnOptionSelectedListener {
        void onOptionsSelected(Offer offer);
    }
}
