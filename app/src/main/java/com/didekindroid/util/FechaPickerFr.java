package com.didekindroid.util;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.didekindroid.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

import timber.log.Timber;

import static android.graphics.Typeface.DEFAULT;
import static android.support.v4.content.ContextCompat.getColor;
import static com.didekindroid.util.UIutils.formatTimeToString;

public class FechaPickerFr extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    FechaPickerUser fechaFragment;

    public static FechaPickerFr newInstance(FechaPickerUser fragmentListener)
    {
        Timber.d("newInstance()");
        FechaPickerFr fechaPickerFr = new FechaPickerFr();
        fechaPickerFr.fechaFragment = fragmentListener;
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

        fechaFragment.getFechaView().setText(formatTimeToString(calendar.getTimeInMillis()));
        fechaFragment.getFechaView().setTextColor(getColor(getActivity(), R.color.black));
        fechaFragment.getFechaView().setTypeface(DEFAULT);
        fechaFragment.getBean().setFechaPrevista(calendar);
    }

//    ===========================================================================================
//    .................................... INNER CLASSES .................................
//    ===========================================================================================

    public static class FechaPickerHelper {

        public static TextView initFechaViewForPicker(final FechaPickerUser fechaPickerUser, TextView fechaView)
        {
            Timber.d("initFechaViewForPicker()");

            fechaView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Timber.d("onClickLinkToImportanciaUsers()");
                    FechaPickerFr fechaPicker = newInstance(fechaPickerUser);
                    fechaPicker.show(fechaPickerUser.getActivity().getFragmentManager(), "fechaPicker");
                }
            });

            return fechaView;
        }
    }
}
