package com.didekindroid.incidencia.repository;

import android.provider.BaseColumns;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 16:43
 */
public final class IncidenciaDataDb {

    private IncidenciaDataDb(){}


    public interface TipoIncidencia extends BaseColumns {

        String TB_TIPO_INCIDENCIA = "tipo_incidencia";
        String tipo = "tipo";

        String CREATE_TIPOINCIDENCIA = "CREATE TABLE " + TB_TIPO_INCIDENCIA
                + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + tipo + " TEXT"
                + ");";

        String DROP_TIPOINCIDENCIA = "DROP TABLE IF EXISTS " + TB_TIPO_INCIDENCIA;

        int TIPOINCID_COUNT = 53;
    }
}
