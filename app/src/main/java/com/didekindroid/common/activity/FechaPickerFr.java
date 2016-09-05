package com.didekindroid.common.activity;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.didekindroid.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

import timber.log.Timber;

import static com.didekindroid.common.utils.UIutils.formatTimeToString;

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
        long timeFecha = calendar.getTimeInMillis();

        fechaFragment.getFechaView().setText(formatTimeToString(timeFecha));
        fechaFragment.getFechaView().setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        fechaFragment.getFechaView().setTypeface(Typeface.DEFAULT);
        fechaFragment.getBean().setFechaPrevista(timeFecha);
    }

//    ===========================================================================================
//    .................................... INNER CLASSES .................................
//    ===========================================================================================

    public static class FechaPickerHelper {

        public static TextView initFechaSpinnerView(final FechaPickerUser fechaPickerUser)
        {
            Timber.d("initFechaSpinnerView()");

            TextView mFechaView = (TextView) fechaPickerUser.getFragmentView().findViewById(R.id.incid_resolucion_fecha_view);

            mFechaView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Timber.d("onClick()");
                    FechaPickerFr fechaPicker = FechaPickerFr.newInstance(fechaPickerUser);
                    fechaPicker.show(fechaPickerUser.getActivity().getFragmentManager(), "fechaPicker");
                }
            });

            return mFechaView;
        }
    }
}
