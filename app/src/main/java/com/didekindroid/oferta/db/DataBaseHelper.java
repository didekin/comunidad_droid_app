package com.didekindroid.oferta.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * User: pedro@didekindroid
 * Date: 05/05/15
 * Time: 09:46
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "comunidades.db";

    private static final String TAG = "DataBaseHelper";


    public DataBaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /*
    * Scripts de creación de las diferentes tablas de la base de datos.
    */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d(TAG, "Enters onCreate()");
        //Log.d(TAG,"onCreate(): " + OfferTable.ServOne.SQL_CREATE);
        db.execSQL(OfferTable.INSTANCE.SQL_CREATE);
    }

    /*
    * Cuando se modifica la versión en la variable DB_VERSION, se lanza este método.
    * Parece exigir modificación de código fuente, compilación y despliegue.
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d(TAG, "Enters onUpgrade()" + " oldversion= " + oldVersion + " newVersion= " + newVersion);
        db.execSQL(OfferTable.INSTANCE.SQL_DELETE);
        onCreate(db);
    }
}
