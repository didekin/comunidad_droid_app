package com.didekindroid.incidencia.activity;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.common.activity.FechaPickerFr;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.didekin.common.dominio.DataPatterns.LINE_BREAK;
import static com.didekin.incidservice.dominio.IncidDataPatterns.INCID_RESOLUCION_DESC;
import static com.didekindroid.common.utils.UIutils.formatTimeToString;
import static com.didekindroid.common.utils.UIutils.getIntFromStringDecimal;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionRegFr extends Fragment implements OnDateSetListener {

    private static final String TAG = IncidResolucionRegFr.class.getCanonicalName();

    ResolucionBean mResolucionBean;
    IncidUserDataSupplier mActivitySupplier;
    View mFragmentView;
    TextView fechaView;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Log.d(TAG, "onAttach()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mFragmentView = inflater.inflate(R.layout.incid_resolucion_reg_frg, container, false);
        TextView mFechaView = (TextView) mFragmentView.findViewById(R.id.incid_resolucion_fecha_view);
        mFechaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "onClick()");
                FechaPickerFr fechaPicker = new FechaPickerFr();
                fechaPicker.show(getActivity().getFragmentManager(), "fechaPicker");
            }
        });
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mActivitySupplier = (IncidUserDataSupplier) getActivity();
    }

    public ResolucionBean makeResolucionBeanFromView(StringBuilder errorMsg)
    {
        Log.d(TAG, "makeResolucionBeanFromView()");

        if (mResolucionBean == null) {
            errorMsg.append(getResources().getString(R.string.incid_resolucion_fecha_prev_msg))
                    .append(LINE_BREAK.getRegexp());
            return null;
        }
        if (!mResolucionBean.validateBean(errorMsg)){
            return null;
        }
        return mResolucionBean;
    }

    //  ======================== INTERFACE COMMUNICATIONS METHODS ==========================

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        Log.d(TAG, "onDateSet()");

        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        fechaView = (TextView) mFragmentView.findViewById(R.id.incid_resolucion_fecha_view);
        long timeFecha = calendar.getTimeInMillis();

        mResolucionBean = new ResolucionBean();
        mResolucionBean.fechaPrevista = timeFecha;

        fechaView.setText(formatTimeToString(timeFecha));
        fechaView.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        fechaView.setTypeface(Typeface.DEFAULT);
    }

//    ============================================================================================
//    .................................... INNER CLASSES .................................
//    ============================================================================================

    class ResolucionBean {

        long fechaPrevista;
        String descripcion;
        int costePrev;

        ResolucionBean()
        {
        }

        boolean validateBean(StringBuilder errorMsg)
        {
            return validateFechaPrev(errorMsg) & validateDescripcion(errorMsg) & validateCostePrev(errorMsg);
        }

        boolean validateFechaPrev(StringBuilder errorMsg)
        {
            String fechaPrevistaVw = fechaView.getText().toString();

            if (fechaPrevistaVw.equals(getResources().getString(R.string.incid_resolucion_fecha_default_txt))
                    || fechaPrevista < mActivitySupplier.getIncidImportancia().getIncidencia().getFechaAlta().getTime()) {
                errorMsg.append(getResources().getString(R.string.incid_resolucion_fecha_prev_msg)).append(LINE_BREAK.getRegexp());
                Log.d(TAG, "validateFechaPrev(): false");
                return false;
            }
            return fechaPrevistaVw.equals(formatTimeToString(fechaPrevista));
        }

        boolean validateDescripcion(StringBuilder errorMsg)
        {
            descripcion = ((EditText)mFragmentView.findViewById(R.id.incid_resolucion_desc_ed)).getText().toString();

            if (!INCID_RESOLUCION_DESC.isPatternOk(descripcion)) {
                errorMsg.append(getResources().getString(R.string.incid_resolucion_descrip_msg)).append(LINE_BREAK.getRegexp());
                Log.d(TAG, "validateDescripcion(): false");
                return false;
            }
            return true;
        }

        boolean validateCostePrev(StringBuilder errorMsg)
        {
            String costePrevText = ((EditText) mFragmentView.findViewById(R.id.incid_resolucion_coste_prev_ed)).getText().toString();

            if (costePrevText.isEmpty()) {
                return true;
            }

            try {
                costePrev = getIntFromStringDecimal(costePrevText);
            } catch (ParseException e) {
                errorMsg.append(getResources().getString(R.string.incid_resolucion_coste_prev_msg)).append(LINE_BREAK.getRegexp());
                Log.d(TAG, "validateCostePrev(): false");
                return false;
            }

            return true;
        }
    }
}
