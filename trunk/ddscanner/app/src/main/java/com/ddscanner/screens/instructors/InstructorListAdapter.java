package com.ddscanner.screens.instructors;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Instructor;
import com.ddscanner.events.IsCommentLikedEvent;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class InstructorListAdapter extends RecyclerView.Adapter<InstructorListAdapter.InstructorListViewHolder> {

    private ArrayList<Instructor> instructors;
    private Context context;
    private ArrayList<String> showedInstructors = new ArrayList<>();

    public InstructorListAdapter(ArrayList<Instructor> instructors, Context context) {
        this.instructors = instructors;
        this.context = context;
    }

    @Override
    public InstructorListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_instructor, parent, false);
        return new InstructorListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InstructorListViewHolder holder, int position) {
        holder.name.setText(instructors.get(position).getName());
        holder.isNew.setVisibility(View.GONE);
        if (instructors.get(position).getIsNew()) {
            holder.isNew.setVisibility(View.VISIBLE);
            if (showedInstructors.indexOf(instructors.get(position).getId()) == -1) {
                showedInstructors.add(instructors.get(position).getId());
            }
        }
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, instructors.get(position).getPhoto(), "1")).placeholder(R.drawable.review_default_avatar).error(R.drawable.review_default_avatar).resize(Math.round(Helpers.convertDpToPixel(60, context)),Math.round(Helpers.convertDpToPixel(60, context))).centerCrop().transform(new CropCircleTransformation()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        if (instructors == null) {
            return 0;
        }
        return instructors.size();
    }

    public void remove(int position) {
        if (showedInstructors.indexOf(instructors.get(position).getId()) != -1) {
            showedInstructors.remove(showedInstructors.indexOf(instructors.get(position).getId()));
        }
        instructors.remove(position);
        notifyItemRemoved(position);
    }

    public ArrayList<String> getShowedInstructors() {
        return this.showedInstructors;
    }

    class InstructorListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView isNew;
        private TextView name;
        private ImageView avatar;

        InstructorListViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            isNew = (TextView) view.findViewById(R.id.is_new);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            name = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public void onClick(View view) {
            UserProfileActivity.show(context, instructors.get(getAdapterPosition()).getId());
        }
    }

}
