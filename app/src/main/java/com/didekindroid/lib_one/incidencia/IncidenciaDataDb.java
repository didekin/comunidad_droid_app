package com.didekindroid.lib_one.incidencia;

import android.provider.BaseColumns;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 16:43
 */
public final class IncidenciaDataDb {

    private IncidenciaDataDb(){}


    public interface AmbitoIncidencia extends BaseColumns {

        String TB_AMBITO_INCIDENCIA = "ambito_incidencia";
        String ambito = "ambito";

        String CREATE_AMBITO_INCIDENCIA = "CREATE TABLE " + TB_AMBITO_INCIDENCIA
                + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + ambito + " TEXT"
                + ");";

        String DROP_AMBITO_INCIDENCIA = "DROP TABLE IF EXISTS " + TB_AMBITO_INCIDENCIA;

        int AMBITO_INCID_COUNT = 53;
    }
}
