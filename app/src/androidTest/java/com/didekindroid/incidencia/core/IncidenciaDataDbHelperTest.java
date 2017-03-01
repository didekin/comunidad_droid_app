package com.didekindroid.incidencia.core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static android.database.sqlite.SQLiteDatabase.deleteDatabase;
import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.incidencia.core.IncidenciaDataDb.AmbitoIncidencia.AMBITO_INCID_COUNT;
import static com.didekindroid.incidencia.core.IncidenciaDataDb.AmbitoIncidencia.CREATE_AMBITO_INCIDENCIA;
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

    public static final String DB_PATH = "data/data/com.didekindroid/databases/";
    private IncidenciaDataDbHelper dbHelper;
    Context context;
    SQLiteDatabase database;

    @Before
    public void getFixture() throws Exception
    {
        context = creator.get().getContext();
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
        String dBFileName = DB_PATH.concat(IncidenciaDataDbHelper.DB_NAME);
        deleteDatabase(new File(dBFileName));
    }
}