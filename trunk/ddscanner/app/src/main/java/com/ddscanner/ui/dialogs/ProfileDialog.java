package com.ddscanner.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.User;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

public class ProfileDialog extends DialogFragment implements View.OnClickListener {

    private Context context;
    private User user;

    private TextView name;
    private TextView comments;
    private TextView likes;
    private TextView checkins;
    private TextView added;
    private TextView edited;
    private TextView about;
    private ImageView avatar;
    private TextView link;
    private LinearLayout open;
    private ImageView closeDialog;

    public static ProfileDialog newInstance(User user) {
        ProfileDialog profileDialog = new ProfileDialog();
        Bundle args = new Bundle();
        args.putParcelable("USER", user);
        profileDialog.setArguments(args);

        return profileDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = getArguments().getParcelable("USER");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getBaseContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_profile_info, container,false);
        setUi(v);
        name = (TextView) v.findViewById(R.id.name);
        comments = (TextView) v.findViewById(R.id.comments);
        likes = (TextView) v.findViewById(R.id.likes);
        checkins = (TextView) v.findViewById(R.id.checkIn);
        added = (TextView) v.findViewById(R.id.added);
        edited = (TextView) v.findViewById(R.id.edited);
        about = (TextView) v.findViewById(R.id.about);
        avatar = (ImageView) v.findViewById(R.id.userAvatar);
        link = (TextView) v.findViewById(R.id.linkText);
        open = (LinearLayout) v.findViewById(R.id.link);
        closeDialog = (ImageView) v.findViewById(R.id.close_dialog);

        name.setText(user.getName());
        comments.setText(user.getCountComment());
        likes.setText(user.getCountLike());
        checkins.setText(user.getCountCheckin());
        added.setText(user.getCountAdd());
        edited.setText(user.getCountEdit());
        if (user.getAbout() != null) {
            about.setText(user.getAbout());
        }
        Picasso.with(context).load(user.getPicture()).resize(80,80).centerCrop()
                .transform(new TransformationRoundImage(50,0)).into(avatar);
        open.setOnClickListener(this);
        closeDialog.setOnClickListener(this);

        switch (user.getType()) {
            case "fb":
                link.setText("Open on facebook");
                break;
            case "tw":
                link.setText("Open on twitter");
                break;
            case "go":
                link.setText("Open on Google+");
                break;
        }
        return v;
    }

    private void setUi(View v) {
        name = (TextView) v.findViewById(R.id.name);
        comments = (TextView) v.findViewById(R.id.comments);
        likes = (TextView) v.findViewById(R.id.likes);
        checkins = (TextView) v.findViewById(R.id.checkIn);
        added = (TextView) v.findViewById(R.id.added);
        edited = (TextView) v.findViewById(R.id.edited);
        about = (TextView) v.findViewById(R.id.about);
        avatar = (ImageView) v.findViewById(R.id.userAvatar);
        link = (TextView) v.findViewById(R.id.linkText);
        open = (LinearLayout) v.findViewById(R.id.link);
        closeDialog = (ImageView) v.findViewById(R.id.close_dialog);

        name.setText(user.getName());
        comments.setText(user.getCountComment());
        likes.setText(user.getCountLike());
        checkins.setText(user.getCountCheckin());
        added.setText(user.getCountAdd());
        edited.setText(user.getCountEdit());
        if (user.getAbout() != null) {
            about.setText(user.getAbout());
        }
        Picasso.with(context).load(user.getPicture()).resize(80,80).centerCrop()
                .transform(new TransformationRoundImage(50,0)).into(avatar);
        open.setOnClickListener(this);
        closeDialog.setOnClickListener(this);

        switch (user.getType()) {
            case "fb":
                link.setText("Open on facebook");
                break;
            case "tw":
                link.setText("Open on twitter");
                break;
            case "go":
                link.setText("Open on Google+");
                break;
        }

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link:
                openLink(user.getSocialId(), user.getType());
                break;
            case R.id.close_dialog:
                getDialog().dismiss();
                break;
        }
    }

    private void openLink(String userName, String socialNetwork) {
        switch (socialNetwork) {
            case "tw":
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("twitter://user?screen_name=" + userName));
                    startActivity(intent);

                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/#!/" + userName)));
                }
                break;
            case "go":
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://plus.google.com/" + userName));
                    intent.setPackage("com.google.android.apps.plus"); // don't open the browser, make sure it opens in Google+ app
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + userName)));
                }
                break;
            case "fb":
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + userName));
                    startActivity(intent1);

                }catch(Exception e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + userName)));
                }
                break;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;
        int dialogWidth = Math.round(Helpers.convertDpToPixel(285, getActivity())); // specify a value here
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT; // specify a value here

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

        // ... other stuff you want to do in your onStart() method
    }
}
