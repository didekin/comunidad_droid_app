package com.didekindroid.incidencia.utils;

import android.os.Bundle;

import com.didekindroid.util.BundleKey;
import com.didekinlib.model.incidencia.dominio.Incidencia;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum IncidBundleKey implements BundleKey {

    AMBITO_INCIDENCIA_POSITION,
    INCID_ACTIVITY_VIEW_ID,
    INCID_IMPORTANCIA_NUMBER,
    INCID_IMPORTANCIA_OBJECT,
    INCID_RESOLUCION_BUNDLE,
    INCIDENCIA_ID_LIST_SELECTED,
    INCID_CLOSED_LIST_FLAG {
        @Override
        public Bundle getBundleForKey(Object flagValue)
        {
            Bundle bundle = new Bundle(1);
            bundle.putBoolean(key, Boolean.class.cast(flagValue));
            return bundle;
        }
    },
    INCIDENCIA_OBJECT {
        @Override
        public Bundle getBundleForKey(Object incidencia)
        {
            Bundle bundle = new Bundle(1);
            bundle.putSerializable(key, Incidencia.class.cast(incidencia));
            return bundle;
        }
    },
    INCID_RESOLUCION_OBJECT,;

    private static final String intentPackage = "com.didekindroid.incidencia.utils.IncidBundleKey.";

    public final String key;

    IncidBundleKey()
    {
        key = intentPackage.concat(name());
    }

    @Override
    public String getKey()
    {
        return key;
    }
}
