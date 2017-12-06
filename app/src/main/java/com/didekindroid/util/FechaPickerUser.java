package com.didekindroid.util;

import android.widget.TextView;

import com.didekindroid.incidencia.core.resolucion.ResolucionBean;

/**
 * User: pedro@didekin
 * Date: 01/12/2017
 * Time: 15:15
 */

public class FechaPickerUser implements FechaPickerUserIf {

    private final TextView fechaView;
    private final FechaPickerBean pickerBean;

    public FechaPickerUser(TextView fechaView, ResolucionBean pickerBean)
    {
        this.fechaView = fechaView;
        this.pickerBean = pickerBean;
    }

    @Override
    public TextView getFechaView()
    {
        return fechaView;
    }

    @Override
    public FechaPickerBean getPickerBean()
    {
        return pickerBean;
    }
}
