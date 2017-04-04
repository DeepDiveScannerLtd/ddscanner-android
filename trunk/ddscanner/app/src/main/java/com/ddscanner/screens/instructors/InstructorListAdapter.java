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
import com.ddscanner.events.RemoveInstructorEvent;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class InstructorListAdapter extends RecyclerView.Adapter<InstructorListAdapter.InstructorListViewHolder> {

    private ArrayList<Instructor> instructors;
    private Context context;
    private ArrayList<String> showedInstructors = new ArrayList<>();
    private boolean isSelf;

    public InstructorListAdapter(ArrayList<Instructor> instructors, Context context, boolean isSelf) {
        this.instructors = instructors;
        this.context = context;
        this.isSelf = isSelf;
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
        holder.removeButton.setVisibility(View.GONE);
        if (isSelf) {
            holder.removeButton.setVisibility(View.VISIBLE);
            if (instructors.get(position).getIsNew()) {
                holder.isNew.setVisibility(View.VISIBLE);
                if (showedInstructors.indexOf(instructors.get(position).getId()) == -1) {
                    showedInstructors.add(instructors.get(position).getId());
                }
            }
        }
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, instructors.get(position).getPhoto(), "1")).placeholder(R.drawable.gray_circle_placeholder).error(R.drawable.avatar_profile_default).resize(Math.round(Helpers.convertDpToPixel(35, context)),Math.round(Helpers.convertDpToPixel(35, context))).centerCrop().transform(new CropCircleTransformation()).into(holder.avatar);
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
        private ImageView removeButton;

        InstructorListViewHolder(View view) {
            super(view);
            isNew = (TextView) view.findViewById(R.id.is_new);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            name = (TextView) view.findViewById(R.id.name);
            removeButton = (ImageView) view.findViewById(R.id.remove);
            removeButton.setOnClickListener(this);
            avatar.setOnClickListener(this);
            name.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.avatar:
                case R.id.name:
                    UserProfileActivity.show(context, instructors.get(getAdapterPosition()).getId(), instructors.get(getAdapterPosition()).getType());
                    break;
                case R.id.remove:
                    DDScannerApplication.bus.post(new RemoveInstructorEvent(instructors.get(getAdapterPosition()).getId(), getAdapterPosition()));
                    break;
            }
        }
    }

}
