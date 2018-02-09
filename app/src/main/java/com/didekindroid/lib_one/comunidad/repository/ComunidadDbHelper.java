package com.didekindroid.lib_one.comunidad.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.didekindroid.R;
import com.didekindroid.lib_one.comunidad.spinner.TipoViaValueObj;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.provider.BaseColumns._ID;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.ComunidadAutonoma.CREATE_C_AUTONOMA;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.ComunidadAutonoma.DROP_C_AUTONOMA;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.ComunidadAutonoma.TB_C_AUTONOMA;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.ComunidadAutonoma.cu_nombre;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Municipio.CREATE_INDEX_PROV_FK;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Municipio.CREATE_MUNICIPIO;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Municipio.DROP_MUNICIPIO;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Municipio.TB_MUNICIPIO;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Municipio.m_cd;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Municipio.mu_nombre;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Municipio.pr_id;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Provincia.CREATE_INDEX_CA_FK;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Provincia.CREATE_PROVINCIA;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Provincia.DROP_PROVINCIA;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Provincia.TB_PROVINCIA;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Provincia.ca_id;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.Provincia.pr_nombre;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.SQL_ENABLE_FK;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.TipoVia.CREATE_TIPO_VIA;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.TipoVia.DROP_TIPO_VIA;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.TipoVia.TB_TIPO_VIA;
import static com.didekindroid.lib_one.comunidad.repository.ComunidadDataDb.TipoVia.tipovia;
import static com.didekindroid.lib_one.util.IoHelper.lineToLowerCase;

/**
 * User: pedro
 * Date: 16/12/14
 * Time: 18:30
 */
public class ComunidadDbHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "comunidad.db";
    /*This number has to be changed in future versions, to get executed onUpgrade() method.*/
    private static final int DB_VERSION = 1;

    private final Context mContext;
    int mMunicipiosCounter;
    int mComunidadesCounter;
    int mProvinciasCounter;
    int mTipoViaCounter;
    private SQLiteDatabase mDataBase;

    public ComunidadDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Timber.i("In onCreate()");

        mDataBase = db;

        mDataBase.execSQL(CREATE_TIPO_VIA);
        mDataBase.execSQL(CREATE_C_AUTONOMA);
        mDataBase.execSQL(CREATE_PROVINCIA);
        mDataBase.execSQL(CREATE_INDEX_CA_FK);
        mDataBase.execSQL(CREATE_MUNICIPIO);
        mDataBase.execSQL(CREATE_INDEX_PROV_FK);

        if (!mDataBase.isReadOnly()) {
            mDataBase.execSQL(SQL_ENABLE_FK);
        }

        try {
            loadTipoVia();
        } catch (IOException e) {
            Timber.e(e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            loadComunidadesAutonomas();
        } catch (IOException e) {
            Timber.e(e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            loadProvincias();
        } catch (IOException e) {
            Timber.e(e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            loadMunicipios();
        } catch (IOException e) {
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
        Timber.w("Upgrading database from version %d to %d%n", oldVersion, newVersion);

        if (mDataBase == null) {
            mDataBase = db;
        }

        dropAllTables();
        onCreate(mDataBase);
    }

//    ==============================================================================================
//    ................................. MUNICIPIOS .....................................
//    ==============================================================================================

    @SuppressWarnings("UnusedReturnValue")
    private int loadMunicipios() throws IOException
    {
        Timber.i("In loadMunicipios()");

        final Resources resources = mContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.municipio); // TODO: sustituir por referencia R de librería.
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
                    Timber.e("Unable to add municipio: %s  %s%n", strings[0].trim(), strings[1].trim());
                }
            }
        } finally {
            reader.close();
        }

        Timber.i("Done loading municipio file in DB.");
        mMunicipiosCounter = pkCounter;
        return pkCounter;
    }

    private long addMunicipio(int pk, short provinciaPk, short codMunicipioInProv, String nombre)
    {
        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(pr_id, provinciaPk);
        values.put(m_cd, codMunicipioInProv);
        values.put(mu_nombre, nombre);

        return mDataBase.insert(TB_MUNICIPIO, null, values);
    }

    @SuppressWarnings("unused")
    public List<Municipio> getMunicipioByProvincia(short provinciaId)
    {
        Timber.d("In getMunicipioByProvincia()");

        Cursor cursor = doMunicipiosByProvinciaCursor(provinciaId);
        if (cursor == null) {
            return new ArrayList<>(0);
        }

        int pkMunicipioIndex = cursor.getColumnIndex(_ID);
        int pkProvinciaIndex = cursor.getColumnIndex(pr_id);
        int pkMunicipioInProvinciaIndex = cursor.getColumnIndex(m_cd);
        int nombreIndex = cursor.getColumnIndex(mu_nombre);
        List<Municipio> municipios = new ArrayList<>(cursor.getCount());
        Municipio municipio;

        do {
            municipio = new Municipio(
                    (int) cursor.getLong(pkMunicipioIndex),
                    cursor.getShort(pkMunicipioInProvinciaIndex),
                    cursor.getString(nombreIndex),
                    new Provincia(cursor.getShort(pkProvinciaIndex)));
            municipios.add(municipio);
        } while (cursor.moveToNext());

        cursor.close();
        return municipios;
    }

    Cursor doMunicipiosByProvinciaCursor(short prId)
    {
        Timber.i("In doMunicipiosByProvinciaCursor()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        String[] columns = new String[]{_ID, pr_id, m_cd, mu_nombre};
        String whereClause = pr_id + " = ?";
        String[] wherClauseArgs = new String[]{String.valueOf(prId)};

        Cursor cursor = mDataBase.query(TB_MUNICIPIO, columns, whereClause, wherClauseArgs, null, null, null);

        if (checkNullCursor(cursor)) return null;
        return cursor;
    }

//    ==============================================================================================
//    ....................................... PROVINCIAS .......................................
//    ==============================================================================================

    @SuppressWarnings("UnusedReturnValue")
    private int loadProvincias() throws IOException
    {
        Timber.i("In loadProvincias()");

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
                    Timber.e("Unable to add provincia: %s%n", strings[0].trim());
                } else {
                    ++pkCounter;
                }
            }
        } finally {
            reader.close();
        }

        Timber.i("Done loading provincias file in DB.");
        mProvinciasCounter = pkCounter;
        return pkCounter;
    }

    private long addProvincia(short pk, short comunidadPk, String nombre)
    {
        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(ca_id, comunidadPk);
        values.put(pr_nombre, nombre);

        return mDataBase.insert(TB_PROVINCIA, null, values);
    }

    Cursor doProvinciasByCACursor(short caId)
    {
        Timber.d("doProvinciasByCACursor( - entering)");

        if (mDataBase == null || mProvinciasCounter == 0) {
            mDataBase = getReadableDatabase();
        }

        String[] columns = new String[]{_ID, pr_nombre, ca_id};
        String whereClause = ca_id + " = ?";
        String[] wherClauseArgs = new String[]{String.valueOf(caId)};

        Cursor cursor = mDataBase.query(TB_PROVINCIA, columns, whereClause, wherClauseArgs, null, null, null);

        if (checkNullCursor(cursor)) return null;

        Timber.d("doProvinciasByCACursor() - almost out");
        return cursor;
    }

    public List<Provincia> getProvinciasByCA(short caId)
    {
        Timber.d("In getProvinciasByCA() - entering, caId = %d%n", caId);

        Cursor cursor = doProvinciasByCACursor(caId);
        if (cursor == null) {
            return new ArrayList<>(0);
        }

        int pkIndex = cursor.getColumnIndex(_ID);
        int nombreIndex = cursor.getColumnIndex(pr_nombre);
        int comuAutonomaIndex = cursor.getColumnIndex(ca_id);
        Provincia provincia;
        List<Provincia> provincias = new ArrayList<>();

        do {
            provincia = new Provincia(new ComunidadAutonoma(cursor.getShort(comuAutonomaIndex)), cursor.getShort(pkIndex), cursor.getString(nombreIndex));
            provincias.add(provincia);
        } while (cursor.moveToNext());

        cursor.close();
        Timber.d("In getProvinciasByCA() - cursor.close(), caId = %d%n", caId);
        return provincias;
    }

//    ==============================================================================================
//    ................................ COMUNIDADES AUTÓNOMAS ..............................
//    ==============================================================================================

    @SuppressWarnings("UnusedReturnValue")
    private int loadComunidadesAutonomas() throws IOException
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

                long id = addComunidad(Short.parseShort(strings[0].trim()), strings[1].trim());

                if (id < 0) {
                    Timber.e("Unable to add comunidad: %s  %s%n", strings[0].trim(), strings[1].trim());
                } else {
                    ++pkCounter;
                }
            }
        } finally {
            reader.close();
        }

        Timber.i("Done loading comunidades file in DB.");
        mComunidadesCounter = pkCounter;
        return pkCounter;
    }

    private long addComunidad(short pk, String nombre)
    {
        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(cu_nombre, nombre);

        return mDataBase.insert(TB_C_AUTONOMA, null, values);
    }

    public List<ComunidadAutonoma> getComunidadesAu()
    {
        Timber.d("In getComunidadesAu()");

        Cursor cursor = doComunidadesCursor();

        int pkIndex = cursor != null ? cursor.getColumnIndex(_ID) : 0;
        assert cursor != null;
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

    private Cursor doComunidadesCursor()
    {
        Timber.d("In doComunidadesCursor()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        String[] tableColumns = new String[]{_ID, cu_nombre};
        Cursor cursor = mDataBase.query(TB_C_AUTONOMA, tableColumns, null, null, null, null, null);

        if (checkNullCursor(cursor)) return null;
        return cursor;
    }

//    ==============================================================================================
//    ................................... TIPOS DE VÍA ..................................
//    ==============================================================================================

    @SuppressWarnings("UnusedReturnValue")
    private int loadTipoVia() throws IOException
    {
        Timber.i("In loadTipoVia()");

        final Resources resources = mContext.getResources();
        int pkCounter = 0;

        try (InputStream inputStream = resources.openRawResource(R.raw.tipos_vias);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            ContentValues values;

            while ((line = reader.readLine()) != null) {

                values = new ContentValues();
                values.put(_ID, pkCounter);
                values.put(tipovia, lineToLowerCase(line));
                long id = mDataBase.insert(TB_TIPO_VIA, null, values);

                if (id < 0) {
                    Timber.e("Unable to add tipo de vía: %s%n", line.trim());
                } else {
                    ++pkCounter;
                }
            }
        }

        Timber.i("Done loading tipos de vía file in DB.");
        mTipoViaCounter = pkCounter;
        return pkCounter;
    }

    private Cursor doTipoViaCursor()
    {
        Timber.d("In doTipoViaCursor()");

        if (mDataBase == null) {
            mDataBase = getWritableDatabase();
        }

        String[] tableColumns = new String[]{_ID, tipovia};
        Cursor cursor = mDataBase.query(TB_TIPO_VIA, tableColumns, null, null, null, null, _ID);

        if (checkNullCursor(cursor)) return null;

        return cursor;
    }

    public List<TipoViaValueObj> getTiposVia()
    {
        Timber.d("In getTiposVia()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        Cursor cursor = doTipoViaCursor();
        if (cursor == null){
            return new ArrayList<>(0);
        }

        int pkIndex = cursor.getColumnIndex(_ID);
        int tipoViaIndexDesc = cursor.getColumnIndex(tipovia);
        List<TipoViaValueObj> tiposViaList = new ArrayList<>(cursor.getCount());
        TipoViaValueObj tipoViaObj;

        while (!cursor.isAfterLast()) {
            tipoViaObj = new TipoViaValueObj(cursor.getInt(pkIndex), cursor.getString(tipoViaIndexDesc));
            tiposViaList.add(tipoViaObj);
            cursor.moveToNext();
        }
        cursor.close();
        return tiposViaList;
    }

//    ...................................... UTILITIES .............................................

    void dropAllTables()
    {
        Timber.d("In dropAllTables()");

        if (mDataBase != null) {
            mDataBase.execSQL(DROP_MUNICIPIO);
            mMunicipiosCounter = 0;
            mDataBase.execSQL(DROP_PROVINCIA);
            mProvinciasCounter = 0;
            mDataBase.execSQL(DROP_C_AUTONOMA);
            mComunidadesCounter = 0;
            mDataBase.execSQL(DROP_TIPO_VIA);
            mTipoViaCounter = 0;
        }
    }

    private boolean checkNullCursor(Cursor cursor)
    {
        if (cursor == null) {
            return true;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }
}
