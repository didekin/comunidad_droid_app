package com.didekindroid.oferta.db;

import android.provider.BaseColumns;
import com.didekindroid.repository.SqLiteTypes;

/**
 * User: pedro
 * Date: 09/02/15
 * Time: 15:47
 */
public enum OfferTable implements BaseColumns {

    INSTANCE;

    //    Table name.
    public final String TB_NAME = "offer";
    //    Table fields.
    public final String TITLE = "title";
    public final String DESCRIPTION = "description";
    public final String DATE = "date";

    public final String SQL_CREATE =
            "CREATE TABLE " + TB_NAME
                    + " ("
                    + _ID + " INTEGER PRIMARY KEY,"
                    + TITLE + SqLiteTypes.TEXT.name + ","
                    + DESCRIPTION + SqLiteTypes.TEXT.name + ","
                    + DATE + SqLiteTypes.NUM.name
                    + ");";

    public final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + TB_NAME;

    //    Campos auxiliares para la consulta de ofertas.
    public final String[] setColumnsAll = {_ID, TITLE, DESCRIPTION, DATE};
    public final int[] indexColumnsAll = {0,1,2,3};
    public final String sortColumnsAll = DATE + " ASC";

    //    Campos auxiliares para la consulta de una oferta.
    private String whereOneOffer = _ID + " = ?";


}
