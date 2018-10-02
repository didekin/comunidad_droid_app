package com.didekindroid.incidencia.core.resolucion;

import android.widget.TextView;

import com.didekindroid.lib_one.util.FechaPickerBean;
import com.didekindroid.lib_one.util.FechaPickerUserIf;

/**
 * User: pedro@didekin
 * Date: 01/12/2017
 * Time: 15:15
 */

public class FechaPickerResolucion implements FechaPickerUserIf {

    private final TextView fechaView;
    private final FechaPickerBean pickerBean;

    FechaPickerResolucion(TextView fechaView, ResolucionBean pickerBean)
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
