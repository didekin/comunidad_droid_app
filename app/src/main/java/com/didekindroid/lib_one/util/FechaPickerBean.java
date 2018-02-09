package com.didekindroid.lib_one.util;

import java.io.Serializable;
import java.util.Calendar;

/**
 * User: pedro@didekin
 * Date: 16/11/16
 * Time: 19:59
 */

@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface FechaPickerBean extends Serializable {
    void setFechaPrevista(Calendar fechaPrevista);
}
