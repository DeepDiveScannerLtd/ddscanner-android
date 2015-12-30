package travel.ilave.deepdivescanner.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (DatePickerDialog.OnDateSetListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

}
