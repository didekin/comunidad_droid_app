package com.didekindroid.comunidad;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.comunidad.ComunidadAutonoma;
import com.didekin.comunidad.Provincia;
import com.didekindroid.comunidad.repository.ComunidadDataDb;
import com.didekindroid.comunidad.repository.ComunidadDbHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.comunidad.repository.ComunidadDataDb.ComunidadAutonoma.NUMBER_RECORDS;
import static com.didekindroid.comunidad.repository.ComunidadDbHelper.DB_NAME;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 13/06/15
 * Time: 13:14
 */
@RunWith(AndroidJUnit4.class)
public class ComunidadDbHelperTest {

    private ComunidadDbHelper dbHelper;
    Context context;
    SQLiteDatabase database;
    File dbFile;

    @Before
    public void getFixture() throws Exception
    {
        context = creator.get().getContext();
        // Borro la base de datos para evitar interferencias con tests encadenados que fallen y no borren la base de datos.
        context.deleteDatabase(DB_NAME);
        dbHelper = new ComunidadDbHelper(context);
    }

    @After
    public void clearTables()
    {
        dbHelper.dropAllTables();
        dbHelper.close();
        context.deleteDatabase(DB_NAME);
    }

    @Test
    public void testSetUp()
    {
        assertThat(context, notNullValue());
        assertThat(dbHelper, notNullValue());
        assertThat(database, nullValue());
        checkNoRecords();
    }

    @Test
    public void testDropTables() throws Exception
    {
        database = dbHelper.getWritableDatabase();
        checkRecords();

        dbHelper.dropAllTables();
        checkNoRecords();
    }

    @Test
    public void testGetTiposVia()
    {
        database = dbHelper.getReadableDatabase();
        checkRecords();

        List<String> tiposVia = dbHelper.getTiposVia();
        assertThat(tiposVia.size() > 1, is(true));
        assertThat(tiposVia, hasItems("tipo de vía", "Avenida", "Calle", "Ronda", "Carretera"));
    }

    @Test
    public void testGetComunidadesAu()
    {
        database = dbHelper.getReadableDatabase();
        checkRecords();

        List<ComunidadAutonoma> comunidades = dbHelper.getComunidadesAu();
        assertThat(comunidades.size(), is(NUMBER_RECORDS));
        ComunidadAutonoma comunidad1 = new ComunidadAutonoma((short) 8, "Castilla - La Mancha");
        ComunidadAutonoma comunidad2 = new ComunidadAutonoma((short) 4, "Balears, Illes");
        ComunidadAutonoma comunidad3 = new ComunidadAutonoma((short) 7, "Castilla y León");
        ComunidadAutonoma comunidad4 = new ComunidadAutonoma((short) 17, "Rioja, La");
        assertThat(comunidades, hasItems(comunidad1, comunidad2, comunidad3, comunidad4));
    }

    @Test
    public void testGetProvincias()
    {
        database = dbHelper.getReadableDatabase();
        checkRecords();

        List<Provincia> provincias = dbHelper.getProvincias();
        assertThat(provincias.size(), is(ComunidadDataDb.Provincia.NUMBER_RECORDS));
        Provincia provincia1 = new Provincia((short) 12, "Castellón/Castelló");
        Provincia provincia2 = new Provincia((short) 46, "Valencia/València");
        Provincia provincia3 = new Provincia((short) 38, "Santa Cruz de Tenerife");
        Provincia provincia4 = new Provincia((short) 35, "Palmas, Las");
        assertThat(provincias, hasItems(provincia1, provincia2, provincia3, provincia4));
    }

    @Test
    public void testGetProvinciasByCA()
    {
        database = dbHelper.getReadableDatabase();
        checkRecords();

        SQLiteCursor cursor = (SQLiteCursor) dbHelper.getProvinciasByCA((short) 2);
        assertThat(cursor.getCount(), is(3));

        SQLiteCursor cursorP = (SQLiteCursor) dbHelper.getProvinciasByCA((short) 1);
        assertThat(cursorP.getCount(), is(8));
        cursorP.close();
    }

    @Test
    public void testGetMunicipiosByPrId()
    {
        database = dbHelper.getReadableDatabase();
        checkRecords();

        Cursor cursorM = dbHelper.getMunicipiosByPrId((short) 1);
        assertThat(cursorM.getCount(), is(51));
        assertThat(cursorM.getColumnCount(), is(4));
        cursorM.close();

        Cursor cursorP = dbHelper.getMunicipiosByPrId((short) 33);
        assertThat(cursorP.getCount(), is(78));
        cursorP.close();
    }

//    ================================ Private methods ====================================

    private void checkRecords()
    {
        assertThat(dbHelper.mTipoViaCounter > 1, is(true));
        assertThat(dbHelper.mMunicipiosCounter > 1, is(true));
        assertThat(dbHelper.mComunidadesCounter, is(NUMBER_RECORDS));
        assertThat(dbHelper.mProvinciasCounter, is(ComunidadDataDb.Provincia.NUMBER_RECORDS));
    }

    private void checkNoRecords()
    {
        assertThat(dbHelper.mTipoViaCounter, is(0));
        assertThat(dbHelper.mMunicipiosCounter, is(0));
        assertThat(dbHelper.mComunidadesCounter, is(0));
        assertThat(dbHelper.mProvinciasCounter, is(0));
    }
}