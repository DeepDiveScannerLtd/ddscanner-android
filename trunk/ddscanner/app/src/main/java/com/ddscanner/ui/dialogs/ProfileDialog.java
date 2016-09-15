package com.ddscanner.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

public class ProfileDialog extends DialogFragment implements View.OnClickListener {

    private Context context;
    private User user;

    private String FACEBOOK_URL;
    private String FACEBOOK_PAGE_ID;

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
        args.putParcelable(Constants.PROFILE_DIALOG_INTENT_USER, user);
        profileDialog.setArguments(args);
        return profileDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable(Constants.PROFILE_DIALOG_INTENT_USER);
        FACEBOOK_URL = Constants.PROFILE_DIALOG_FACEBOOK_URL + user.getSocialId();
        FACEBOOK_PAGE_ID = user.getSocialId();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getBaseContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_profile_info, container, false);
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
        Picasso.with(context).load(user.getPicture()).resize(80, 80).centerCrop()
                .transform(new TransformationRoundImage(50, 0)).into(avatar);
        open.setOnClickListener(this);
        closeDialog.setOnClickListener(this);

        switch (user.getType()) {
            case "fb":
                link.setText(R.string.open_on_facebook);
                break;
            case "tw":
                link.setText(R.string.open_on_twitter);
                break;
            case "go":
                link.setText(R.string.open_on_google_plus);
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
        about.setVisibility(View.GONE);
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
            about.setVisibility(View.VISIBLE);
        }
        Picasso.with(context).load(user.getPicture()).resize(80, 80).centerCrop()
                .transform(new TransformationRoundImage(50, 0)).into(avatar);
        open.setOnClickListener(this);
        closeDialog.setOnClickListener(this);

        switch (user.getType()) {
            case "fb":
                link.setText(R.string.open_on_facebook);
                break;
            case "tw":
                link.setText(R.string.open_on_twitter);
                break;
            case "go":
                link.setText(R.string.open_on_google_plus);
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
                            Uri.parse(Constants.PROFILE_DIALOG_TWITTER_URI + userName));
                    startActivity(intent);

                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(Constants.PROFILE_DIALOG_TWITTER_URL + userName)));
                }
                break;
            case "go":
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Constants.PROFILE_DIALOG_GOOGLE_URL + userName));
                    intent.setPackage("com.google.android.apps.plus");
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW, Uri.parse(Constants.PROFILE_DIALOG_GOOGLE_URL + userName)));
                }
                break;
            case "fb":
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getActivity());
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
                break;
        }
    }

    /**
     * Check version of facebook app. According this create uri to open this
     *
     * @param context
     * @return facebook URL to open app correctly
     * @author Andrei Lashkevich
     */

    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                return Constants.PROFILE_DIALOG_FACEBOOK_OLD_URI + FACEBOOK_URL;
            } else {
                return Constants.PROFILE_DIALOG_FACEBOOK_NEW_URI + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;
        int dialogWidth = Math.round(Helpers.convertDpToPixel(285, getActivity()));
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }
}
