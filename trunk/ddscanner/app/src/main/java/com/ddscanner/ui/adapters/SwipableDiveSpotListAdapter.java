package com.ddscanner.ui.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;
import com.ddscanner.ui.activities.SocialNetworks;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lashket on 18.5.16.
 */
public class SwipableDiveSpotListAdapter
        extends RecyclerView.Adapter<SwipableDiveSpotListAdapter.SwipableDiveSpotListViewHolder> {

    private static final String TAG = SwipableDiveSpotListAdapter.class.getSimpleName();

    public ArrayList<DiveSpot> divespots;
    private Context context;
    private Helpers helpers = new Helpers();
    private EventsTracker.SpotViewSource spotViewSource;

    public SwipableDiveSpotListAdapter(ArrayList<DiveSpot> divespots, Context context, EventsTracker.SpotViewSource spotViewSource) {
        this.divespots = divespots;
        this.context = context;
        this.spotViewSource = spotViewSource;

    }

    @Override
    public SwipableDiveSpotListViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.product_item, viewGroup, false);
        return new SwipableDiveSpotListViewHolder(itemView);

    }

    public void removeItem(int position, boolean isCheckin) {
        if (isCheckin) {
            checkOut(String.valueOf(divespots.get(position).getId()));
        } else {
            removeFromFavorites(String.valueOf(divespots.get(position).getId()));
        }
        divespots.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, divespots.size());
    }

    public void undoItem(DiveSpot diveSpot, int position, boolean isCheckin) {
        divespots.add(position, diveSpot);
        notifyItemInserted(position);
        if (isCheckin) {
            checkIn(String.valueOf(divespots.get(position).getId()));
        } else {
            addToFavorites(String.valueOf(divespots.get(position).getId()));
        }
    }

    @Override
    public void onBindViewHolder(final SwipableDiveSpotListViewHolder swipableDiveSpotListViewHolder, final int position) {
        DiveSpot divespot = divespots.get(position);
        swipableDiveSpotListViewHolder.progressBar.getIndeterminateDrawable().
                setColorFilter(context.getResources().getColor(R.color.primary),
                        PorterDuff.Mode.MULTIPLY);
        if (divespot.getImage() != null) {
            Picasso.with(context).load(divespot.getImage()).resize(130, 130).centerCrop()
                    .transform(new TransformationRoundImage(2,0))
                    .into(swipableDiveSpotListViewHolder.imageView,
                            new ImageLoadedCallback(swipableDiveSpotListViewHolder.progressBar) {
                                @Override
                                public void onSuccess() {
                                    if (this.progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
        } else {
            swipableDiveSpotListViewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.list_photo_default));
        }
        if(divespot.getName() != null) {
            swipableDiveSpotListViewHolder.title.setText(divespot.getName());
        }
        swipableDiveSpotListViewHolder.stars.removeAllViews();
        for (int k = 0; k < divespot.getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_full);
            iv.setPadding(0,0,5,0);
            swipableDiveSpotListViewHolder.stars.addView(iv);
        }
        for (int k = 0; k < 5 - divespot.getRating(); k++) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(R.drawable.ic_list_star_empty);
            iv.setPadding(0,0,5,0);
            swipableDiveSpotListViewHolder.stars.addView(iv);
        }
    }

    @Override
    public int getItemCount() {
        if (divespots != null) {
            return divespots.size();
        }
        return 0;
    }

    private void checkOut(String id) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance()
                .checkOutUser(id, helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                try {
                    responseString = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LogUtils.i("response body is " + responseString);
                try {
                    ErrorsParser.checkForError(response.code(), responseString);
                } catch (ServerInternalErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (BadRequestException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (ValidationErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (NotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (UnknownErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (DiveSpotNotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (UserNotFoundException e) {
                    // TODO Handle
                    SharedPreferenceHelper.logout();
                    DDScannerApplication.bus.post(new ShowLoginActivityIntent());
                } catch (CommentNotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(context);
            }
        });
    }
    private void checkIn(String id) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance()
                .checkIn(id, helpers.getRegisterRequest());
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                try {
                    responseString = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LogUtils.i("response body is " + responseString);
                try {
                    ErrorsParser.checkForError(response.code(), responseString);
                } catch (ServerInternalErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (BadRequestException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (ValidationErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (NotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (UnknownErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (DiveSpotNotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (UserNotFoundException e) {
                    // TODO Handle
                    SharedPreferenceHelper.logout();
                    DDScannerApplication.bus.post(new ShowLoginActivityIntent());
                } catch (CommentNotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(context);
            }
        });
    }
    private void addToFavorites(String id) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance()
                .addDiveSpotToFavourites(id, helpers.getRegisterRequest());
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                try {
                    responseString = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LogUtils.i("response body is " + responseString);
                try {
                    ErrorsParser.checkForError(response.code(), responseString);
                } catch (ServerInternalErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (BadRequestException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (ValidationErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (NotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (UnknownErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (DiveSpotNotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (UserNotFoundException e) {
                    // TODO Handle
                    SharedPreferenceHelper.logout();
                    DDScannerApplication.bus.post(new ShowLoginActivityIntent());
                } catch (CommentNotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(context);
            }
        });
    }

    private void removeFromFavorites(String id) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance()
                .removeSpotFromFavorites(id, helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                try {
                    responseString = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LogUtils.i("response body is " + responseString);
                try {
                    ErrorsParser.checkForError(response.code(), responseString);
                } catch (ServerInternalErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (BadRequestException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (ValidationErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (NotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (UnknownErrorException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (DiveSpotNotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                } catch (UserNotFoundException e) {
                    // TODO Handle
                    SharedPreferenceHelper.logout();
                    DDScannerApplication.bus.post(new ShowLoginActivityIntent());
                } catch (CommentNotFoundException e) {
                    // TODO Handle
                    helpers.showToast(context, R.string.toast_server_error);
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(context);
            }
        });
    }

    public class SwipableDiveSpotListViewHolder extends RecyclerView.ViewHolder
                                                        implements View.OnClickListener{
        protected ImageView imageView;
        protected TextView title;
        protected LinearLayout stars;
        protected ProgressBar progressBar;
        private Context mContext;


        public SwipableDiveSpotListViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.product_logo);
            title = (TextView) itemView.findViewById(R.id.product_title);
            stars = (LinearLayout) itemView.findViewById(R.id.stars);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
            DiveSpotDetailsActivity
                    .show(mContext, String.valueOf(divespots.get(getAdapterPosition()).getId()), spotViewSource);
        }
    }


    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public  ImageLoadedCallback(ProgressBar progBar){
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }

}
