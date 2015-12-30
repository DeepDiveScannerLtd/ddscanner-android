package travel.ilave.deepdivescanner.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.Product;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class ProductInfoDialog extends DialogFragment implements View.OnClickListener {
    Context mContext;

    public interface OnExploreClickListener {
        void onExploreClicked();
    }

    public static final String PRODUCT = "PRODUCT";

    private Product product;
    private OnExploreClickListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getBaseContext();
        listener = (OnExploreClickListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        product = (Product) getArguments().getParcelable(PRODUCT);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_fragment_product, null);
        builder.setView(dialogView);
        builder.setTitle(product.getName());

        Dialog dialog = builder.create();
        dialog.getWindow().setLayout(800, 600);
        Button explore = (Button) dialogView.findViewById(R.id.explore);
        TextView from = (TextView) dialogView.findViewById(R.id.price);
        LinearLayout stars = (LinearLayout) dialogView.findViewById(R.id.stars);
        explore.setOnClickListener(this);

        from.setText("From " + product.getPrice() + "$");
        for (int i = 0; i < product.getRating(); i++) {
            ImageView iv = new ImageView(getActivity());
            iv.setImageResource(R.drawable.ic_star_white_24dp);
            stars.addView(iv);
        }
        for (int i = 0; i < 5 - product.getRating(); i++) {
            ImageView iv = new ImageView(getActivity());
            iv.setImageResource(R.drawable.ic_star_border_white_24dp);
            iv.setAlpha(0.6f);
            stars.addView(iv);
        }
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //get window size
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //set dialog size in %
        int dialogWidth = (int) (width * 0.8);
        int dialogHeight = (int) (height * 0.6);
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Override
    public void onClick(View view) {
        this.dismiss();
        listener.onExploreClicked();
    }
}
