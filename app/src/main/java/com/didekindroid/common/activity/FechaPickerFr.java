package com.didekindroid.common.activity;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class FechaPickerFr extends DialogFragment {

    private static final String TAG = FechaPickerFr.class.getCanonicalName();
    ActivityForFechaPicker mActivity;

    @Override
    public void onAttach(Activity activity)
    {
        Log.d(TAG, "onAttach()");
        super.onAttach(activity);
        mActivity = (ActivityForFechaPicker) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateDialog()");

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        OnDateSetListener fechaFragment = mActivity.giveMyFechaFragment();
        return new DatePickerDialog(mActivity.getActivity(), fechaFragment, year, month, day);
    }

//    ===========================================================================================
//    .................................... INNER CLASSES .................................
//    ===========================================================================================

    public interface ActivityForFechaPicker<T extends Activity> {
        OnDateSetListener giveMyFechaFragment();
        T getActivity();
    }
}
