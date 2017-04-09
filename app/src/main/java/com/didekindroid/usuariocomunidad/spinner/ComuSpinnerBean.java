package com.didekindroid.usuariocomunidad.spinner;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 03/04/17
 * Time: 14:17
 */

public interface ComuSpinnerBean extends Serializable {
    long getComunidadId();

    ComuSpinnerBean setComunidadId(long comunidadId);
}
