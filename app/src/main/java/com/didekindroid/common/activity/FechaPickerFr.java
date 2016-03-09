package com.didekindroid.common.activity;


import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class FechaPickerFr extends DialogFragment {

    private static final String TAG = FechaPickerFr.class.getCanonicalName();
    OnDateSetListener fechaFragment;

    public static FechaPickerFr newInstance(OnDateSetListener onDateSetListener)
    {
        Log.d(TAG, "newInstance()");
        FechaPickerFr fechaPickerFr = new FechaPickerFr();
        fechaPickerFr.fechaFragment = onDateSetListener;
        return fechaPickerFr;
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

        return new DatePickerDialog(getActivity(), fechaFragment, year, month, day);
    }

//    ===========================================================================================
//    .................................... INNER CLASSES .................................
//    ===========================================================================================


}
