package com.didekindroid.incidencia;

import android.os.Bundle;

import com.didekindroid.lib_one.util.BundleKey;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import java.io.Serializable;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum IncidBundleKey implements BundleKey {

    INCID_ACTIVITY_VIEW_ID,
    INCID_IMPORTANCIA_NUMBER,
    INCID_IMPORTANCIA_OBJECT,
    INCID_RESOLUCION_BUNDLE{
        @Override
        public Bundle getBundleForKey(Serializable resolBundle)
        {
            Bundle bundle = new Bundle(1);
            bundle.putSerializable(key, IncidAndResolBundle.class.cast(resolBundle));
            return bundle;
        }
    },
    INCIDENCIA_ID_LIST_SELECTED,
    INCID_CLOSED_LIST_FLAG {
        @Override
        public Bundle getBundleForKey(Serializable flagValue)
        {
            Bundle bundle = new Bundle(1);
            bundle.putBoolean(key, Boolean.class.cast(flagValue));
            return bundle;
        }
    },
    INCIDENCIA_OBJECT {
        @Override
        public Bundle getBundleForKey(Serializable incidencia)
        {
            Bundle bundle = new Bundle(1);
            bundle.putSerializable(key, Incidencia.class.cast(incidencia));
            return bundle;
        }
    },
    INCID_RESOLUCION_OBJECT,;

    private static final String intentPackage = "com.didekindroid.incidencia.IncidBundleKey.";

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
