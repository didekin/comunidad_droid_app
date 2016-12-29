package com.didekinaar.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;


import com.didekinaar.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

import static android.provider.BaseColumns._ID;
import static com.didekinaar.comunidad.ComunidadDataDb.ComunidadAutonoma.CREATE_C_AUTONOMA;
import static com.didekinaar.comunidad.ComunidadDataDb.ComunidadAutonoma.TB_C_AUTONOMA;
import static com.didekinaar.comunidad.ComunidadDataDb.ComunidadAutonoma.cu_nombre;

/**
 * User: pedro@didekin
 * Date: 16/06/15
 * Time: 09:19
 */
class MockDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "mock.db";
    /*This number has to be changed in future versions, to get executed onUpgrade() method.*/
    private static final int DB_VERSION = 1;

    private final Context mContext;

    MockDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Timber.i("In onCreate()");
        db.execSQL(CREATE_C_AUTONOMA);
        try {
            loadComunidadesAutonomas(db);
        } catch (IOException e) {
            Timber.e("IOException in loadComunidades");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        Timber.d("In onOpen()");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Timber.d("In onUpgrade()");
        Timber.w("Upgrading database from version %d to %d%n", oldVersion, newVersion);
    }

    private int loadComunidadesAutonomas(SQLiteDatabase db) throws IOException
    {
        Timber.i("In loadComunidadesAutonomas()");

        final Resources resources = mContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.comunidad_autonoma);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int pkCounter = 0;

        try {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] strings = TextUtils.split(line, ":");
                if (strings.length < 2) continue;

                long id = addComunidad(db, Short.parseShort(strings[0].trim()), strings[1].trim());

                if (id < 0) {
                    --pkCounter;
                    Timber.e("Unable to add comunidad: %s  %s%n", strings[0].trim(), strings[1].trim());
                } else {
                    ++pkCounter;
                }
            }
        } finally {
            reader.close();
        }

        Timber.i("Done loading comunidades file in DB.");

        return pkCounter;
    }

    private long addComunidad(SQLiteDatabase db, short pk, String nombre)
    {
        Timber.i("En addComunidad()");

        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(cu_nombre, nombre);

        return db.insert(TB_C_AUTONOMA, null, values);
    }

}
