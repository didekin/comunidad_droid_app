package com.didekindroid.comunidad.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.comunidad.spinner.TipoViaValueObj;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.comunidad.repository.ComunidadDataDb.ComunidadAutonoma.NUMBER_RECORDS;
import static com.didekindroid.comunidad.repository.ComunidadDataDb.TipoVia;
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

    Context context;
    SQLiteDatabase database;
    File dbFile;
    private ComunidadDbHelper dbHelper;

    @Before
    public void getFixture() throws Exception
    {
        context = creator.get().getContext();
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

        List<TipoViaValueObj> tiposVia = dbHelper.getTiposVia();
        assertThat(tiposVia.size(), is(TipoVia.NUMBER_RECORDS));
        assertThat(tiposVia, hasItems(
                new TipoViaValueObj(0, "tipo de vía"),
                new TipoViaValueObj(2, "Acceso"),
                new TipoViaValueObj(4, "Acera")
        ));
    }

    @Test
    public void testGetComunidadesAu()
    {
        database = dbHelper.getReadableDatabase();
        checkRecords();

        List<ComunidadAutonoma> comunidades = dbHelper.getComunidadesAu();
        assertThat(comunidades.size(), is(NUMBER_RECORDS));
        ComunidadAutonoma comunidad0 = new ComunidadAutonoma((short) 0, "comunidad autónoma");
        ComunidadAutonoma comunidad1 = new ComunidadAutonoma((short) 8, "Castilla - La Mancha");
        ComunidadAutonoma comunidad2 = new ComunidadAutonoma((short) 4, "Balears, Illes");
        ComunidadAutonoma comunidad3 = new ComunidadAutonoma((short) 7, "Castilla y León");
        ComunidadAutonoma comunidad4 = new ComunidadAutonoma((short) 17, "Rioja, La");
        assertThat(comunidades, hasItems(comunidad0,comunidad1, comunidad2, comunidad3, comunidad4));
    }

    @Test
    public void test_DoProvinciasByCACursor() throws Exception
    {
        database = dbHelper.getReadableDatabase();
        checkRecords();

        SQLiteCursor cursor = (SQLiteCursor) dbHelper.doProvinciasByCACursor((short) 2);
        assertThat(cursor.getCount(), is(3));

        SQLiteCursor cursorP = (SQLiteCursor) dbHelper.doProvinciasByCACursor((short) 1);
        assertThat(cursorP.getCount(), is(8));
        cursorP.close();
    }

    @Test
    public void test_GetProvinciasByCA()
    {
        database = dbHelper.getReadableDatabase();
        checkRecords();

        List<Provincia> provincias = dbHelper.getProvinciasByCA((short) 11);
        assertThat(provincias.size(), is(2));
        Provincia provincia1 = new Provincia(new ComunidadAutonoma((short) 11),(short) 6, "Badajoz");
        Provincia provincia2 = new Provincia(new ComunidadAutonoma((short) 11),(short) 10, "Cáceres");
        assertThat(provincias, hasItems(provincia1, provincia2));
    }

    @Test
    public void test_GetProvinciasByCA_0()
    {
        database = dbHelper.getReadableDatabase();

        List<Provincia> provincias = dbHelper.getProvinciasByCA((short) 0);
        assertThat(provincias.size(), is(1));
        assertThat(provincias.get(0).getNombre(), is("provincia"));
    }

    @Test
    public void test_GetMunicipioByProvincia() throws Exception
    {
        database = dbHelper.getReadableDatabase();
        checkRecords();

        List<Municipio> municipios = dbHelper.getMunicipioByProvincia((short) 11);
        assertThat(municipios.size(), is(44));
        assertThat(municipios.get(0).getNombre(), is("Alcalá de los Gazules"));
        assertThat(municipios.get(2).getNombre(), is("Algar"));
    }

    @Test
    public void test_GetMunicipioByProvincia_0() throws Exception
    {
        database = dbHelper.getReadableDatabase();

        List<Municipio> municipios = dbHelper.getMunicipioByProvincia((short) 0);
        assertThat(municipios.size(), is(1));
        assertThat(municipios.get(0).getNombre(), is("municipio"));
    }

    @Test
    public void test_DoMunicipiosByProvinciaCursor() throws Exception
    {
        database = dbHelper.getReadableDatabase();
        checkRecords();

        Cursor cursorM = dbHelper.doMunicipiosByProvinciaCursor((short) 1);
        assertThat(cursorM.getCount(), is(51));
        assertThat(cursorM.getColumnCount(), is(4));
        cursorM.close();

        Cursor cursorP = dbHelper.doMunicipiosByProvinciaCursor((short) 33);
        assertThat(cursorP.getCount(), is(78));
        cursorP.close();
    }

//    ================================ Private methods ====================================

    private void checkRecords()
    {
        assertThat(dbHelper.mTipoViaCounter, is(TipoVia.NUMBER_RECORDS));
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