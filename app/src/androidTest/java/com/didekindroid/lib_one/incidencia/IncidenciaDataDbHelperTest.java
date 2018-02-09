package com.didekindroid.lib_one.incidencia;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.incidencia.spinner.AmbitoIncidValueObj;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.incidencia.IncidenciaDataDb.AmbitoIncidencia.AMBITO_INCID_COUNT;
import static com.didekindroid.lib_one.incidencia.IncidenciaDataDb.AmbitoIncidencia.CREATE_AMBITO_INCIDENCIA;
import static com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper.DB_NAME;
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
public class IncidenciaDataDbHelperTest {

    private IncidenciaDataDbHelper dbHelper;
    Context context;
    SQLiteDatabase database;

    @Before
    public void getFixture() throws Exception
    {
        context = httpInitializer.get().getContext();
        dbHelper = new IncidenciaDataDbHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    @Test
    public void testSetUp()
    {
        assertThat(context, notNullValue());
        assertThat(dbHelper, notNullValue());
        assertThat(dbHelper.mAmbitoIncidenciaCounter, is(AMBITO_INCID_COUNT));
    }

    @Test
    public void testDropTipoIncidencia() throws Exception
    {
        dbHelper.dropAmbitoIncidencia();
        assertThat(dbHelper.mAmbitoIncidenciaCounter, is(0));
    }

    @Test
    public void testDropAllTables() throws Exception
    {
        dbHelper.dropAllTables();
        assertThat(dbHelper.mAmbitoIncidenciaCounter, is(0));
    }

    @Test
    public void testDoTipoIncidenciaCursor()
    {
        Cursor cursor = dbHelper.doAmbitoIncidenciaCursor();
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(AMBITO_INCID_COUNT));
        assertThat(cursor.getColumnCount(), is(2));
        cursor.moveToFirst();
        assertThat(cursor.getShort(0), is((short) 0));
        assertThat(cursor.getString(1), is("ámbito de incidencia"));
        cursor.moveToLast();
        assertThat(cursor.getShort(0), is((short) (AMBITO_INCID_COUNT - 1)));
        assertThat(cursor.getString(1), is("Otros"));
        cursor.close();
    }

    @Test
    public void testGetAmbitoIncidList() throws Exception
    {
        final Cursor cursor = dbHelper.doAmbitoIncidenciaCursor();
        List<AmbitoIncidValueObj> list = dbHelper.getAmbitoIncidList(cursor);
        assertThat(list.size(), is(AMBITO_INCID_COUNT));
        assertThat(list.get(0).id, is((short)0));
        assertThat(list.get(0).ambitoStr, is("ámbito de incidencia"));
        assertThat(list.get(AMBITO_INCID_COUNT - 1).id, is((short)(AMBITO_INCID_COUNT - 1)));
        assertThat(list.get(AMBITO_INCID_COUNT - 1).ambitoStr, is("Otros"));
        assertThat(cursor.isClosed(), is(true));
    }

    @Test
    public void testGetAmbitoDescByPk()
    {
        assertThat(dbHelper.getAmbitoDescByPk((short) 9), is("Buzones"));
        // No hay registro 55.
        assertThat(dbHelper.getAmbitoDescByPk((short) 55), nullValue());
    }

    @Test
    public void testLoadTipoIncidencia() throws IOException
    {
        dbHelper.dropAmbitoIncidencia();
        assertThat(dbHelper.mAmbitoIncidenciaCounter, is(0));
        database.execSQL(CREATE_AMBITO_INCIDENCIA);
        assertThat(dbHelper.loadAmbitoIncidencia(), is(AMBITO_INCID_COUNT));
        assertThat(dbHelper.mAmbitoIncidenciaCounter, is(AMBITO_INCID_COUNT));
    }

    @After
    public void clearTables()
    {
        dbHelper.dropAllTables();
        dbHelper.close();
        context.deleteDatabase(DB_NAME);
    }
}