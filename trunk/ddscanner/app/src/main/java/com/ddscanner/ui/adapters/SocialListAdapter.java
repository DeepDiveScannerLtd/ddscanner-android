package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.ContactUsEntity;
import com.ddscanner.events.SocialLinkOpenEvent;
import com.ddscanner.ui.views.DDProgressBarView;

import java.util.ArrayList;

/**
 * Created by lashket on 6.8.16.
 */
public class SocialListAdapter extends RecyclerView.Adapter<SocialListAdapter.SocialListViewHolder> {

    private ArrayList<ContactUsEntity> contactUsEntities;
    private Context context;

    public SocialListAdapter(ArrayList<ContactUsEntity> contactUsEntities, Context context) {
        this.contactUsEntities = contactUsEntities;
        this.context = context;
    }

    @Override
    public SocialListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_contact_us, parent, false);
        return new SocialListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SocialListViewHolder holder, int position) {
        ContactUsEntity object = contactUsEntities.get(position);
        if (object.getSubTitle() != null) {
            holder.subTitle.setVisibility(View.VISIBLE);
            holder.subTitle.setText(object.getSubTitle());
        }
        holder.icon.setImageResource(object.getIconResId());
        holder.title.setText(object.getTitle());
    }

    @Override
    public int getItemCount() {
        return contactUsEntities.size();
    }

    public class SocialListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected TextView title;
        protected TextView subTitle;
        protected ImageView icon;

        public SocialListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            title = (TextView) v.findViewById(R.id.title);
            subTitle = (TextView) v.findViewById(R.id.subTitle);
            icon = (ImageView) v.findViewById(R.id.icon);
        }

        @Override
        public void onClick(View view) {
            DDScannerApplication.bus.post(new SocialLinkOpenEvent(contactUsEntities.get(getAdapterPosition()).getIntent()));
        }
    }

}
