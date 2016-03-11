package com.didekindroid.common.activity;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.didekindroid.incidencia.dominio.ResolucionBean;

/**
 * User: pedro@didekin
 * Date: 10/03/16
 * Time: 10:27
 */
public interface FechaPickerUser {

    View getFragmentView();
    TextView getFechaView();
    Activity getActivity();
    ResolucionBean getBean();
}
