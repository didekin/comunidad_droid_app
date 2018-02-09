package com.didekindroid.lib_one.comunidad.repository;

import android.provider.BaseColumns;

import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.ComunidadAutonoma.TB_C_AUTONOMA;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Provincia.TB_PROVINCIA;

/**
 * User: pedro
 * Date: 16/12/14
 * Time: 16:22
 */
public final class ComunidadDataDb {

    private ComunidadDataDb()
    {
    }

    static final String SQL_ENABLE_FK = "PRAGMA foreign_keys=ON;";

    public interface TipoVia extends BaseColumns {

        int NUMBER_RECORDS = 323;

        String TB_TIPO_VIA = "tipo_via";

        String tipovia = "tipovia";

        String CREATE_TIPO_VIA = "CREATE TABLE " + TB_TIPO_VIA
                + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + tipovia + " TEXT"
                + ");";

        String DROP_TIPO_VIA = "DROP TABLE IF EXISTS " + TB_TIPO_VIA;
    }

    public interface ComunidadAutonoma extends BaseColumns {

        int NUMBER_RECORDS = 20;

        String TB_C_AUTONOMA = "comunidad_autonoma";

        String cu_nombre = "nombre";

        String CREATE_C_AUTONOMA = "CREATE TABLE " + TB_C_AUTONOMA
                + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + cu_nombre + " TEXT"
                + ");";

        String DROP_C_AUTONOMA = "DROP TABLE IF EXISTS " + TB_C_AUTONOMA;
    }

    public interface Provincia extends BaseColumns {

        int NUMBER_RECORDS = 53;

        String TB_PROVINCIA = "provincia";
        String ca_id = "ca_id";
        String pr_nombre = "nombre";

        String CREATE_PROVINCIA = "CREATE TABLE " + TB_PROVINCIA
                + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + ca_id + " INTEGER,"
                + pr_nombre + " TEXT,"
                + " FOREIGN KEY(" + ca_id + ") REFERENCES " + TB_C_AUTONOMA + "(" + ComunidadAutonoma._ID + ")"
                + ");";

        String DROP_PROVINCIA = "DROP TABLE IF EXISTS " + TB_PROVINCIA;

        String CREATE_INDEX_CA_FK = "CREATE INDEX cautonoma_index ON " + TB_PROVINCIA + "(" + ca_id + ");";
    }

    public interface Municipio extends BaseColumns {

        String TB_MUNICIPIO = "municipio";
        String pr_id = "pr_id";
        String m_cd = "m_cd";
        String mu_nombre = "nombre";

        String CREATE_MUNICIPIO = "CREATE TABLE " + TB_MUNICIPIO
                + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + pr_id + " INTEGER,"
                + m_cd + " INTEGER,"
                + mu_nombre + " TEXT,"
                + " FOREIGN KEY(" + pr_id + ") REFERENCES " + TB_PROVINCIA + "(" + Provincia._ID + ")"
                + ");";

        String DROP_MUNICIPIO = "DROP TABLE IF EXISTS " + TB_MUNICIPIO;

        String CREATE_INDEX_PROV_FK = "CREATE INDEX provincia_index ON " + TB_MUNICIPIO + "(" + pr_id + ");";

        @SuppressWarnings("unused")
        String CREATE_INDEX_UNIQUE_MUN = "CREATE UNIQUE INDEX municipio_unico ON "
                + TB_MUNICIPIO + "(" + pr_id + "," + m_cd + ");";
    }

}
