package com.didekindroid.masterdata.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import com.didekindroid.R;
import com.didekindroid.common.IoHelper;
import com.didekindroid.masterdata.dominio.ComunidadAutonoma;
import com.didekindroid.masterdata.dominio.Municipio;
import com.didekindroid.masterdata.dominio.Provincia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.didekindroid.masterdata.repository.MasterDataDb.ComunidadAutonoma.*;
import static com.didekindroid.masterdata.repository.MasterDataDb.Municipio.*;
import static com.didekindroid.masterdata.repository.MasterDataDb.Provincia.*;
import static com.didekindroid.masterdata.repository.MasterDataDb.SQL_ENABLE_FK;
import static java.lang.String.valueOf;

/**
 * User: pedro
 * Date: 16/12/14
 * Time: 18:30
 */
public class MasterDataDbHelper extends SQLiteOpenHelper {

    private static final String TAG = MasterDataDbHelper.class.getCanonicalName();
    public static final String DB_NAME = "masterdata.db";
    /*This number has to be changed in future versions, to get executed onUpgrade() method.*/
    public static final int DB_VERSION = 1;

    private final Context mContext;
    private SQLiteDatabase mDataBase;
    protected int mMunicipiosCounter;
    protected int mComunidadesCounter;
    protected int mProvinciasCounter;

    public MasterDataDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.i(TAG, "In onCreate()");

        mDataBase = db;

        mDataBase.execSQL(CREATE_C_AUTONOMA);
        mDataBase.execSQL(CREATE_PROVINCIA);
        mDataBase.execSQL(CREATE_INDEX_CA_FK);
        mDataBase.execSQL(CREATE_MUNICIPIO);
        mDataBase.execSQL(CREATE_INDEX_PROV_FK);

        if (!mDataBase.isReadOnly()) {
            mDataBase.execSQL(SQL_ENABLE_FK);
        }

        try {
            mComunidadesCounter = loadComunidadesAutonomas();
        } catch (IOException e) {
            IoHelper.doRuntimeException(e,TAG);
        }

        try {
            mProvinciasCounter = loadProvincias();
        } catch (IOException e) {
            IoHelper.doRuntimeException(e,TAG);
        }

        try {
            mMunicipiosCounter = loadMunicipios();
        } catch (IOException e) {
            IoHelper.doRuntimeException(e,TAG);
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

//    ........... MUNICIPIOS ...............

    private int loadMunicipios() throws IOException
    {
        Log.i(TAG, "In loadMunicipios()");

        final Resources resources = mContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.municipios);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int pkCounter = 0;

        try {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] strings = TextUtils.split(line, ":");
                if (strings.length < 3) continue;

                long id = addMunicipio(
                        ++pkCounter,
                        Short.parseShort(strings[0].trim()),
                        Short.parseShort(strings[1].trim()),
                        strings[2].trim());

                if (id < 0) {
                    --pkCounter;
                    Log.e(TAG, "Unable to add municipio: " + strings[0].trim() + " " + strings[1].trim());
                }
            }
        } finally {
            reader.close();
        }

        Log.i(TAG, "Done loading municipios file in DB.");

        return pkCounter;
    }

    protected long addMunicipio(int pk, short provinciaPk, short codMunicipioInProv, String nombre)
    {

//        Log.d(TAG, "In addMunicipio()");

        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(pr_id, provinciaPk);
        values.put(m_cd, codMunicipioInProv);
        values.put(mu_nombre, nombre);

        long municipioPk = mDataBase.insert(TB_MUNICIPIO, null, values);
        return municipioPk;
    }

    protected Municipio getMunicipioFromDb(short codMunicipio, short provinciaId)
    {
        Log.d(TAG, "In getMunicipioFromDb()");

        String whereClause = _ID + " = ?";
        String[] whereArgs = new String[]{valueOf(codMunicipio), valueOf(provinciaId)};
        String[] tableColumns = new String[]{
                _ID
                , mu_nombre};

        Cursor cursor = mDataBase.query(TB_MUNICIPIO, tableColumns, whereClause, whereArgs,
                null, null, null);

        if (cursor == null) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        int nombreIndex = cursor.getColumnIndex(mu_nombre);
        Municipio municipio = new Municipio(codMunicipio, cursor.getString(nombreIndex), new Provincia(provinciaId));
        municipio.setmId((int) cursor.getLong(0));
        cursor.close();
        return municipio;
    }

    public Cursor getMunicipiosByPrId(short prId)
    {
        Log.i(TAG, "In getMunicipiosByPrId()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        String[] columns = new String[]{_ID,pr_id,m_cd,mu_nombre};
        String whereClause = pr_id + " = ?";
        String[] wherClauseArgs = new String[]{String.valueOf(prId)};

        Cursor cursor = mDataBase.query(TB_MUNICIPIO, columns, whereClause, wherClauseArgs, null, null, null);
        return cursor;
    }

//    ............. PROVINCIAS ..................

    private int loadProvincias() throws IOException
    {
        Log.i(TAG, "In loadProvincias()");

        final Resources resources = mContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.provincia);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int pkCounter = 0;

        try {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] strings = TextUtils.split(line, ":");
                if (strings.length < 3) continue;

                long id = addProvincia(Short.parseShort(strings[0].trim()),
                        Short.parseShort(strings[1].trim()),
                        strings[2].trim());

                if (id < 0) {
                    --pkCounter;
                    Log.e(TAG, "Unable to add provincia: " + strings[0].trim());
                } else {
                    ++pkCounter;
                }
            }
        } finally {
            reader.close();
        }

        Log.i(TAG, "Done loading provincias file in DB.");

        return pkCounter;
    }

    private long addProvincia(short pk, short comunidadPk, String nombre)
    {
//        Log.i(TAG, "En addProvincia()");

        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(ca_id, comunidadPk);
        values.put(pr_nombre, nombre);

        long provinciaPk = mDataBase.insert(TB_PROVINCIA, null, values);
        return provinciaPk;
    }

    public List<Provincia> getProvincias()
    {
        Log.d(TAG, "In getProvincias()");

        if (mDataBase == null || mProvinciasCounter == 0) {
            mDataBase = getReadableDatabase();
        }

        String[] tableColumns = new String[]{_ID, pr_nombre};
        Cursor cursor = mDataBase.query(TB_PROVINCIA, tableColumns, null, null, null, null, null);

        if (cursor == null) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        int pkIndex = cursor.getColumnIndex(_ID);
        int nombreIndex = cursor.getColumnIndex(pr_nombre);
        Provincia provincia;
        List<Provincia> provincias = new ArrayList<>();

        do {
            provincia = new Provincia(cursor.getShort(pkIndex), cursor.getString(nombreIndex));
            provincias.add(provincia);
        } while (cursor.moveToNext());

        cursor.close();
        return provincias;
    }

    public Cursor getProvinciasByCA(short caId)
    {
        Log.d(TAG, "In getProvinciasByCA(), caId = " + caId);

        if (mDataBase == null || mProvinciasCounter == 0) {
            mDataBase = getReadableDatabase();
        }

        String[] columns = new String[]{_ID, pr_nombre};
        String whereClause = MasterDataDb.Provincia.ca_id + " = ?";
        String[] wherClauseArgs = new String[]{String.valueOf(caId)};

        Cursor cursor = mDataBase.query(TB_PROVINCIA, columns, whereClause, wherClauseArgs, null, null, null);
        return cursor;

    }


//    ............. COMUNIDADES AUTÓNOMAS .................

    private int loadComunidadesAutonomas() throws IOException
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

                long id = addComunidad(Short.parseShort(strings[0].trim()), strings[1].trim());

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

    private long addComunidad(short pk, String nombre)
    {
        Log.i(TAG, "En addComunidad()");

        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(cu_nombre, nombre);

        long comunidadPk = mDataBase.insert(TB_C_AUTONOMA, null, values);
        return comunidadPk;
    }

    public List<ComunidadAutonoma> getComunidadesAu()
    {
        Log.d(TAG, "In getComunidadesAu()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        Cursor cursor = doComunidadesCursor();

        int pkIndex = cursor.getColumnIndex(_ID);
        int nombreIndex = cursor.getColumnIndex(cu_nombre);
        ComunidadAutonoma comunidad;
        List<ComunidadAutonoma> comunidades = new ArrayList<>();

        do {
            comunidad = new ComunidadAutonoma(cursor.getShort(pkIndex), cursor.getString(nombreIndex));
            comunidades.add(comunidad);
        } while (cursor.moveToNext());

        cursor.close();
        return comunidades;
    }

    public Cursor doComunidadesCursor()
    {
        Log.d(TAG, "In doComunidadesCursor()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        String[] tableColumns = new String[]{_ID, cu_nombre};
        Cursor cursor = mDataBase.query(TB_C_AUTONOMA, tableColumns, null, null, null, null, null);

        if (cursor == null) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        return cursor;
    }

//    ................ UTILITIES ...............

    protected void dropAllTables()
    {
        Log.d(TAG, "In dropAllTables()");

        if (mDataBase != null) {
            mDataBase.execSQL(DROP_MUNICIPIO);
            mMunicipiosCounter = 0;
            mDataBase.execSQL(DROP_PROVINCIA);
            mProvinciasCounter = 0;
            mDataBase.execSQL(DROP_C_AUTONOMA);
            mComunidadesCounter = 0;
        }
    }
}


    /*private void loadCustomers(final SQLiteDatabase db)  // TODO: async call. en vez de new Thread.
    {
        Log.i(TAG, "In loadCustomers()");

        new Thread(new Runnable() {
            public void run()
            {
                try {
                    loadCustomerFile(db);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }*/
