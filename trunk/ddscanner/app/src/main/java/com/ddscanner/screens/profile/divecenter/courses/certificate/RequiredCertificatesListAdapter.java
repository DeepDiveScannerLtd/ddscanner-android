package com.ddscanner.screens.profile.divecenter.courses.certificate;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Certificate;
import com.ddscanner.interfaces.ListItemClickListener;

import java.util.ArrayList;

public class RequiredCertificatesListAdapter extends RecyclerView.Adapter<RequiredCertificatesListAdapter.RequiredCertificateItemViewHolder> {

    private ListItemClickListener<Certificate> listItemClickListener;
    private ArrayList<Certificate> certificates = new ArrayList<>();
    private Context context;

    public RequiredCertificatesListAdapter(ListItemClickListener<Certificate> listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public void setCertificates(ArrayList<Certificate> certificates) {
        this.certificates = certificates;
        notifyDataSetChanged();
    }

    @Override
    public RequiredCertificateItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_required_certificate, parent, false);
        return new RequiredCertificateItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequiredCertificateItemViewHolder holder, int position) {
        holder.logo.setImageDrawable(ContextCompat.getDrawable(context, certificates.get(position).getResourceId()));
        holder.name.setText(certificates.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return certificates.size();
    }

    class RequiredCertificateItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView logo;
        private TextView name;

        public RequiredCertificateItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
            logo = itemView.findViewById(R.id.logo);
            name = itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View v) {
            listItemClickListener.onItemClick(certificates.get(getAdapterPosition()));
        }
    }

}
