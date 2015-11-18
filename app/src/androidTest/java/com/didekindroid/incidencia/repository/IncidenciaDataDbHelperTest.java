package com.didekindroid.incidencia.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.serviceone.domain.ComunidadAutonoma;
import com.didekin.serviceone.domain.Provincia;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.usuario.repository.UsuarioDataDb;
import com.didekindroid.usuario.repository.UsuarioDataDbHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.deleteDatabase;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.TipoIncidencia.CREATE_TIPOINCIDENCIA;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.TipoIncidencia.TIPOINCID_COUNT;
import static com.didekindroid.usuario.repository.UsuarioDataDb.ComunidadAutonoma.NUMBER_RECORDS;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
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
        context = DidekindroidApp.getContext();
        dbHelper = new IncidenciaDataDbHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    @Test
    public void testSetUp()
    {
        assertThat(context, notNullValue());
        assertThat(dbHelper, notNullValue());
        assertThat(dbHelper.mTipoIncidenciaCounter, is(TIPOINCID_COUNT));
    }

    @Test
    public void testDropTipoIncidencia() throws Exception
    {
        dbHelper.dropTipoIncidencia();
        assertThat(dbHelper.mTipoIncidenciaCounter, is(0));
    }

    @Test
    public void testDropAllTables() throws Exception
    {
        dbHelper.dropAllTables();
        assertThat(dbHelper.mTipoIncidenciaCounter, is(0));
    }

    @Test
    public void testLoadTipoIncidencia() throws IOException
    {
        dbHelper.dropTipoIncidencia();
        assertThat(dbHelper.mTipoIncidenciaCounter, is(0));
        database.execSQL(CREATE_TIPOINCIDENCIA);
        assertThat(dbHelper.loadTipoIncidencia(), is(TIPOINCID_COUNT));
    }

    @Test
    public void testDoTipoIncidenciaCursor()
    {
       Cursor cursor = dbHelper.doTipoIncidenciaCursor();
        assertThat(cursor,notNullValue());
        assertThat(cursor.getCount(),is(TIPOINCID_COUNT));
        assertThat(cursor.getColumnCount(),is(2));
        cursor.moveToFirst();
        assertThat(cursor.getShort(0),is((short) 0));
        assertThat(cursor.getString(1), is("tipo de incidencia"));
        cursor.moveToLast();
        assertThat(cursor.getShort(0), is((short) (TIPOINCID_COUNT - 1)));
        assertThat(cursor.getString(1), is("Otros"));
    }

    @After
    public void clearTables()
    {
        dbHelper.dropAllTables();
        dbHelper.close();
        String dBFileName = "data/data/com.didekindroid.debug/databases/".concat(IncidenciaDataDbHelper.DB_NAME);
        deleteDatabase(new File(dBFileName));
    }
}