package com.didekindroid.masterdata.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.masterdata.dominio.ComunidadAutonoma;
import com.didekindroid.masterdata.dominio.Provincia;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static com.didekindroid.masterdata.repository.MasterDataDb.ComunidadAutonoma.NUMBER_RECORDS;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 13/06/15
 * Time: 13:14
 */
@RunWith(AndroidJUnit4.class)
public class MasterDataDbHelperTest {

    private static final String TAG = MasterDataDbHelperTest.class.getCanonicalName();

    private MasterDataDbHelper dbHelper;
    Context context;
    SQLiteDatabase database;

    @Before
    public void getFixture() throws Exception
    {
        context = DidekindroidApp.getContext();
        dbHelper = new MasterDataDbHelper(context);
        Thread.sleep(2500);
        database = dbHelper.getWritableDatabase();
    }

    @Test
    public void testSetUp()
    {
        assertThat(context, notNullValue());
        assertThat(dbHelper, notNullValue());
    }

    @Test
    public void testDropTables() throws Exception
    {
        dbHelper.dropAllTables();
        assertThat(dbHelper.mMunicipiosCounter, is(0));
        assertThat(dbHelper.mComunidadesCounter, is(0));
        assertThat(dbHelper.mProvinciasCounter, is(0));
    }

    @Test
    public void testGetComunidadesAu()
    {
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
        List<Provincia> provincias = dbHelper.getProvincias();
        assertThat(provincias.size(), is(MasterDataDb.Provincia.NUMBER_RECORDS));
        Provincia provincia1 = new Provincia((short) 12, "Castellón/Castelló");
        Provincia provincia2 = new Provincia((short) 46, "Valencia/València");
        Provincia provincia3 = new Provincia((short) 38, "Santa Cruz de Tenerife");
        Provincia provincia4 = new Provincia((short) 35, "Palmas, Las");
        assertThat(provincias, hasItems(provincia1, provincia2, provincia3, provincia4));
    }

    @Test
    public void testGetProvinciasByCA()
    {
        SQLiteCursor cursor = (SQLiteCursor) dbHelper.getProvinciasByCA((short) 2);
        assertThat(cursor.getCount(), is(3));
        cursor = (SQLiteCursor) dbHelper.getProvinciasByCA((short) 1);
        assertThat(cursor.getCount(), is(8));
    }

    @Test
    public void testGetMunicipiosByPrId()
    {
        Cursor cursor = dbHelper.getMunicipiosByPrId((short) 1);
        assertThat(cursor.getCount(), is(51));
        assertThat(cursor.getColumnCount(), is(4));

        cursor = dbHelper.getMunicipiosByPrId((short) 33);
        assertThat(cursor.getCount(), is(78));
    }

    @After
    public void clearTables()
    {
        dbHelper.dropAllTables();
        dbHelper.close();
        database.deleteDatabase(new File("data/data/com.didekindroid.debug/databases/masterdata.db"));
    }
}