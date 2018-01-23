package com.didekindroid.util;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.didekindroid.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

import timber.log.Timber;

import static android.graphics.Typeface.DEFAULT;
import static android.support.v4.content.ContextCompat.getColor;
import static com.didekindroid.util.UIutils.formatTimeToString;

public class FechaPickerFr extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    FechaPickerUserIf fechaPickerUser;

    public static FechaPickerFr newInstance(FechaPickerUserIf fechaPickerUserIn)
    {
        Timber.d("newInstance()");
        FechaPickerFr fechaPickerFr = new FechaPickerFr();
        fechaPickerFr.fechaPickerUser = fechaPickerUserIn;
        return fechaPickerFr;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Timber.d("onCreateDialog()");

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        Timber.d("onDateSet()");

        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);

        fechaPickerUser.getFechaView().setText(formatTimeToString(calendar.getTimeInMillis()));
        fechaPickerUser.getFechaView().setTextColor(getColor(getActivity(), R.color.black));
        fechaPickerUser.getFechaView().setTypeface(DEFAULT);
        fechaPickerUser.getPickerBean().setFechaPrevista(calendar);
    }
}
