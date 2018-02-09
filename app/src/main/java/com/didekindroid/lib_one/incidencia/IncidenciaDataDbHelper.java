package com.didekindroid.lib_one.incidencia;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.didekindroid.R;
import com.didekindroid.lib_one.incidencia.spinner.AmbitoIncidValueObj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.provider.BaseColumns._ID;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.cursor_should_be_in_first_position;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 16:43
 */
public class IncidenciaDataDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "incidencia.db";
    private static final int DB_VERSION = 1;

    private final Context mContext;
    int mAmbitoIncidenciaCounter;
    private SQLiteDatabase mDataBase;

    public IncidenciaDataDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Timber.i("onCreate()");
        mDataBase = db;
        mDataBase.execSQL(IncidenciaDataDb.AmbitoIncidencia.CREATE_AMBITO_INCIDENCIA);

        try {
            loadAmbitoIncidencia();
        } catch (Exception e) {
            Timber.e(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        Timber.d("In onOpen()");

        if (mDataBase == null) {
            mDataBase = db;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Timber.w("Upgrading database from version %d  to  %d%n", oldVersion, newVersion);

        if (mDataBase == null) {
            mDataBase = db;
        }
        dropAllTables();
        onCreate(mDataBase);
    }

    void dropAllTables()
    {
        Timber.d("dropAllTables()");
        if (mDataBase != null) {
            dropAmbitoIncidencia();
        }
    }

//    =====================================================
//    ............. ÁMBITO DE INCIDENCIAS ..................
//    =====================================================

    int loadAmbitoIncidencia() throws IOException
    {
        Timber.i("In loadAmbitoIncidencia()");

        final Resources resources = mContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.ambito_incidencia); // TODO: sustituir por R de librería.
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
                    Timber.e("Unable to add ambito de incidencia: %s  %s%n", strings[0].trim(), strings[1].trim());
                } else {
                    ++pkCounter;
                }
            }
        } finally {
            reader.close();
        }

        Timber.i("Done loading tipos de incidencia file in DB.");
        mAmbitoIncidenciaCounter = pkCounter;
        return pkCounter;
    }

    private long addAmbitoIncidencia(short pk, String nombre)
    {
        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(IncidenciaDataDb.AmbitoIncidencia.ambito, nombre);
        return mDataBase.insert(IncidenciaDataDb.AmbitoIncidencia.TB_AMBITO_INCIDENCIA, null, values);
    }

    Cursor doAmbitoIncidenciaCursor()
    {
        Timber.d("En doAmbitoIncidenciaCursor()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        String[] tableColumns = new String[]{_ID, IncidenciaDataDb.AmbitoIncidencia.ambito};
        Cursor cursor = mDataBase.query(IncidenciaDataDb.AmbitoIncidencia.TB_AMBITO_INCIDENCIA, tableColumns, null, null, null, null, null);

        if (cursor == null) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        return cursor;
    }

    List<AmbitoIncidValueObj> getAmbitoIncidList(Cursor cursor)
    {
        Timber.d("getAmbitoIncidList(Cursor cursor)");
        assertTrue(cursor.moveToFirst(), cursor_should_be_in_first_position);

        List<AmbitoIncidValueObj> list = new ArrayList<>(cursor.getCount());
        AmbitoIncidValueObj ambitoObj;
        while (!cursor.isAfterLast()) {
            ambitoObj = new AmbitoIncidValueObj((short) cursor.getInt(0), cursor.getString(1));
            list.add(ambitoObj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public List<AmbitoIncidValueObj> getAmbitoIncidList()
    {
        Timber.d("getAmbitoIncidList()");
        return getAmbitoIncidList(doAmbitoIncidenciaCursor());
    }

    public String getAmbitoDescByPk(short pkAmbito)
    {
        Timber.d("getAmbitoDescByPk()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        String[] tableColumns = new String[]{IncidenciaDataDb.AmbitoIncidencia.ambito};
        String whereClause = _ID + " = ?";
        String[] whereClauseArgs = new String[]{String.valueOf(pkAmbito)};
        Cursor cursor = mDataBase.query(IncidenciaDataDb.AmbitoIncidencia.TB_AMBITO_INCIDENCIA, tableColumns, whereClause, whereClauseArgs, null, null, null);

        if (cursor == null) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String ambitoDesc = cursor.getString(0);
        cursor.close();
        return ambitoDesc;
    }

    void dropAmbitoIncidencia()
    {
        Timber.d("dropAmbitoIncidencia()");
        mDataBase.execSQL(IncidenciaDataDb.AmbitoIncidencia.DROP_AMBITO_INCIDENCIA);
        mAmbitoIncidenciaCounter = 0;
    }
}
