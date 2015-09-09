package com.didekindroid.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import com.didekindroid.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.provider.BaseColumns._ID;
import static com.didekindroid.repository.MasterDataDb.ComunidadAutonoma.TB_C_AUTONOMA;
import static com.didekindroid.repository.MasterDataDb.ComunidadAutonoma.cu_nombre;

/**
 * User: pedro@didekin
 * Date: 16/06/15
 * Time: 09:19
 */
public class MockDbHelper extends SQLiteOpenHelper {

    private static final String TAG = MockDbHelper.class.getCanonicalName();
    public static final String DB_NAME = "mock.db";
    /*This number has to be changed in future versions, to get executed onUpgrade() method.*/
    public static final int DB_VERSION = 1;
    private final Context mContext;

    public MockDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.i(TAG, "In onCreate()");
        db.execSQL(MasterDataDb.ComunidadAutonoma.CREATE_C_AUTONOMA);
        try {
            loadComunidadesAutonomas(db);
        } catch (IOException e) {
            Log.e(TAG, "IOException in loadComunidades");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        Log.d(TAG, "In onOpen()");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d(TAG, "In onUpgrade()");
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
    }

    private int loadComunidadesAutonomas(SQLiteDatabase db) throws IOException
    {
        Log.i(TAG, "In loadComunidadesAutonomas()");

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
                    Log.e(TAG, "Unable to add comunidad: " + strings[0].trim() + " " + strings[1].trim());
                } else {
                    ++pkCounter;
                }
            }
        } finally {
            reader.close();
        }

        Log.i(TAG, "Done loading comunidades file in DB.");

        return pkCounter;
    }

    private long addComunidad(SQLiteDatabase db, short pk, String nombre)
    {
        Log.i(TAG, "En addComunidad()");

        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(cu_nombre, nombre);

        long comunidadPk = db.insert(TB_C_AUTONOMA, null, values);
        return comunidadPk;
    }

}
