package com.didekindroid.usuario.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.didekin.usuario.dominio.ComunidadAutonoma;
import com.didekin.usuario.dominio.Municipio;
import com.didekin.usuario.dominio.Provincia;
import com.didekindroid.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.provider.BaseColumns._ID;
import static com.didekindroid.common.utils.IoHelper.lineToLowerCase;
import static com.didekindroid.usuario.repository.UsuarioDataDb.ComunidadAutonoma.CREATE_C_AUTONOMA;
import static com.didekindroid.usuario.repository.UsuarioDataDb.ComunidadAutonoma.DROP_C_AUTONOMA;
import static com.didekindroid.usuario.repository.UsuarioDataDb.ComunidadAutonoma.TB_C_AUTONOMA;
import static com.didekindroid.usuario.repository.UsuarioDataDb.ComunidadAutonoma.cu_nombre;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Municipio.CREATE_INDEX_PROV_FK;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Municipio.CREATE_MUNICIPIO;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Municipio.DROP_MUNICIPIO;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Municipio.TB_MUNICIPIO;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Municipio.m_cd;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Municipio.mu_nombre;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Municipio.pr_id;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Provincia.CREATE_INDEX_CA_FK;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Provincia.CREATE_PROVINCIA;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Provincia.DROP_PROVINCIA;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Provincia.TB_PROVINCIA;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Provincia.ca_id;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Provincia.pr_nombre;
import static com.didekindroid.usuario.repository.UsuarioDataDb.SQL_ENABLE_FK;
import static com.didekindroid.usuario.repository.UsuarioDataDb.TipoVia.CREATE_TIPO_VIA;
import static com.didekindroid.usuario.repository.UsuarioDataDb.TipoVia.DROP_TIPO_VIA;
import static com.didekindroid.usuario.repository.UsuarioDataDb.TipoVia.TB_TIPO_VIA;
import static com.didekindroid.usuario.repository.UsuarioDataDb.TipoVia.tipovia;
import static java.lang.String.valueOf;

/**
 * User: pedro
 * Date: 16/12/14
 * Time: 18:30
 */
public class UsuarioDataDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "userComu.db";
    /*This number has to be changed in future versions, to get executed onUpgrade() method.*/
    public static final int DB_VERSION = 1;

    private final Context mContext;
    private SQLiteDatabase mDataBase;
    int mMunicipiosCounter;
    int mComunidadesCounter;
    int mProvinciasCounter;
    int mTipoViaCounter;

    public UsuarioDataDbHelper(Context context)
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

//    ======================================
//    ........... MUNICIPIOS ...............
//    ======================================

    private int loadMunicipios() throws IOException
    {
        Timber.i("In loadMunicipios()");

        final Resources resources = mContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.municipio);
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

    protected long addMunicipio(int pk, short provinciaPk, short codMunicipioInProv, String nombre)
    {

//        Timber.d("In addMunicipio()");

        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(pr_id, provinciaPk);
        values.put(m_cd, codMunicipioInProv);
        values.put(mu_nombre, nombre);

        return mDataBase.insert(TB_MUNICIPIO, null, values);
    }

    @SuppressWarnings("unused")
    protected Municipio getMunicipioFromDb(short codMunicipio, short provinciaId)
    {
        Timber.d("In getMunicipioFromDb()");

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
        Municipio municipio = new Municipio((int) cursor.getLong(0), codMunicipio, cursor.getString(nombreIndex), new
                Provincia(provinciaId));
        cursor.close();
        return municipio;
    }

    public Cursor getMunicipiosByPrId(short prId)
    {
        Timber.i("In getMunicipiosByPrId()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        String[] columns = new String[]{_ID, pr_id, m_cd, mu_nombre};
        String whereClause = pr_id + " = ?";
        String[] wherClauseArgs = new String[]{String.valueOf(prId)};

        return mDataBase.query(TB_MUNICIPIO, columns, whereClause, wherClauseArgs, null, null, null);
    }

//    ======================================
//    ............. PROVINCIAS .............
//    ======================================

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
        Timber.d("En addProvincia()");

        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(ca_id, comunidadPk);
        values.put(pr_nombre, nombre);

        return mDataBase.insert(TB_PROVINCIA, null, values);
    }

    public List<Provincia> getProvincias()
    {
        Timber.d("In getProvincias()");

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
        Timber.d("In getProvinciasByCA(), caId = %d%n", caId);

        if (mDataBase == null || mProvinciasCounter == 0) {
            mDataBase = getReadableDatabase();
        }

        String[] columns = new String[]{_ID, pr_nombre};
        String whereClause = UsuarioDataDb.Provincia.ca_id + " = ?";
        String[] wherClauseArgs = new String[]{String.valueOf(caId)};

        return mDataBase.query(TB_PROVINCIA, columns, whereClause, wherClauseArgs, null, null, null);

    }

//    =====================================================
//    ............. COMUNIDADES AUTÓNOMAS .................
//    =====================================================

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
        Timber.d("En addComunidad()");

        ContentValues values = new ContentValues();
        values.put(_ID, pk);
        values.put(cu_nombre, nombre);

        return mDataBase.insert(TB_C_AUTONOMA, null, values);
    }

    public List<ComunidadAutonoma> getComunidadesAu()
    {
        Timber.d("In getComunidadesAu()");

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
        Timber.d("In doComunidadesCursor()");

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

//    =====================================================
//    .................... TIPOS DE VÍA ...................
//    =====================================================

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

    public Cursor doTipoViaCursor()
    {
        Timber.d("In doTipoViaCursor()");

        if (mDataBase == null) {
            mDataBase = getWritableDatabase();
        }

        String[] tableColumns = new String[]{_ID, tipovia};
        String sortOrder = _ID;
        Cursor cursor = mDataBase.query(TB_TIPO_VIA, tableColumns, null, null, null, null, sortOrder);

        if (cursor == null) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        return cursor;
    }

    public List<String> getTiposVia()
    {
        Timber.d("In getTiposVia()");

        if (mDataBase == null) {
            mDataBase = getReadableDatabase();
        }

        Cursor cursor = doTipoViaCursor();

        int pkIndex = cursor.getColumnIndex(_ID);
        int tipoViaIndex = cursor.getColumnIndex(tipovia);
        List<String> tiposViaList = new ArrayList<>();

        do {
            tiposViaList.add(cursor.getString(tipoViaIndex));
        } while (cursor.moveToNext());

        cursor.close();
        return tiposViaList;
    }

//    ................ UTILITIES ...............

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
}
