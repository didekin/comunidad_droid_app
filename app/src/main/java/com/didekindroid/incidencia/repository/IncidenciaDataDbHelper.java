package com.didekindroid.incidencia.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.didekindroid.R;
import com.didekindroid.common.utils.UIutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.provider.BaseColumns._ID;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.CREATE_AMBITO_INCIDENCIA;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.DROP_AMBITO_INCIDENCIA;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.TB_AMBITO_INCIDENCIA;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.ambito;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 16:43
 */
public class IncidenciaDataDbHelper extends SQLiteOpenHelper {

    private static final String TAG = IncidenciaDataDbHelper.class.getCanonicalName();
    public static final String DB_NAME = "incidencia.db";
    public static final int DB_VERSION = 1;
    public static final String PK_AMBITO_NULL_MSG = TAG + " getAmbitoDescByPk(), ambitoPk == null";

    private final Context mContext;
    private SQLiteDatabase mDataBase;
    int mAmbitoIncidenciaCounter;

    public IncidenciaDataDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.i(TAG, "onCreate()");
        mDataBase = db;
        mDataBase.execSQL(CREATE_AMBITO_INCIDENCIA);

        try {
            loadAmbitoIncidencia();
        } catch (Exception e) {
            UIutils.doRuntimeException(e, TAG);
        }

    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        Log.d(TAG, "In onOpen()");

        if (mDataBase == null) {
            mDataBase = db;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d(TAG, "In onUpgrade()");
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        if (mDataBase == null) {
            mDataBase = db;
        }
        dropAllTables();
        onCreate(mDataBase);
    }

    public void dropAllTables()
    {
        Log.d(TAG, "dropAllTables()");
        if (mDataBase != null) {
            dropAmbitoIncidencia();
        }
    }

//    =====================================================
//    ............. ÁMBITO DE INCIDENCIAS ..................
//    =====================================================

    int loadAmbitoIncidencia() throws IOException
    {
        Log.i(TAG, "In loadAmbitoIncidencia()");

        final Resources resources = mContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.ambito_incidencia);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int pkCounter = 0;

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strings = TextUtils.split(line, ":");
                if (strings.length < 2) {
                    continue;
                }
                long id = addAmbitoIncidencia(Short.parseShort(strings[0].trim()), strings[1].trim());
                if (id < 0) {
                    Log.e(TAG, "Unable to add ambito de incidencia: " + strings[0].trim() + " " + strings[1].trim());
                } else {
                    ++pkCounter;
                }
            }
        } finally {
            reader.close();
        }

        Log.i(TAG, "Done loading tipos de incidencia file in DB.");
        mAmbitoIncidenciaCounter = pkCounter;
        return pkCounter;
    }

    private long addAmbitoIncidencia(short pk, String nombre)
    {
        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(ambito, nombre);
        return mDataBase.insert(TB_AMBITO_INCIDENCIA, null, values);
    }

    public Cursor doAmbitoIncidenciaCursor()
    {
        Log.d(TAG, "En doAmbitoIncidenciaCursor()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        String[] tableColumns = new String[]{_ID, ambito};
        Cursor cursor = mDataBase.query(TB_AMBITO_INCIDENCIA, tableColumns, null, null, null, null, null);

        if (cursor == null) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        return cursor;
    }

    public String getAmbitoDescByPk(short pkAmbito)
    {
        Log.d(TAG, "getAmbitoDescByPk()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        String[] tableColumns = new String[]{ambito};
        String whereClause = _ID + " = ?";
        String[] whereClauseArgs = new String[]{String.valueOf(pkAmbito)};
        Cursor cursor = mDataBase.query(TB_AMBITO_INCIDENCIA,tableColumns,whereClause,whereClauseArgs,null,null,null);

        if (cursor == null){
            throw new IllegalStateException(PK_AMBITO_NULL_MSG);
        }
        if (!cursor.moveToFirst()){
            cursor.close();
            throw new IllegalStateException(PK_AMBITO_NULL_MSG);
        }

        String ambitoDesc = cursor.getString(0);
        cursor.close();
        return ambitoDesc;
    }

    void dropAmbitoIncidencia()
    {
        Log.d(TAG, "dropAmbitoIncidencia()");
        mDataBase.execSQL(DROP_AMBITO_INCIDENCIA);
        mAmbitoIncidenciaCounter = 0;
    }
}
