package com.didekindroid.usuario.repository;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.DidekindroidApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import timber.log.Timber;

import static android.database.sqlite.SQLiteDatabase.deleteDatabase;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/06/15
 * Time: 09:36
 */
@RunWith(AndroidJUnit4.class)
public class MockDbHelperTest {

    private MockDbHelper dbHelper;
    Context context;

    @Before
    public void setUp() throws Exception
    {
        Timber.i("In setUp()");
        context = DidekindroidApp.getContext();
        dbHelper = new MockDbHelper(context);
    }

    @Test
    public void testSetUp()
    {
        Timber.i("In testSetUP()");
        assertThat(context, notNullValue());
        assertThat(dbHelper, notNullValue());
    }

    @Test
    public void testWritableDataBase()
    {
        Timber.i("In testWritableDataBase()");
        dbHelper.getWritableDatabase();
        dbHelper.close();
        boolean isClosedDb = deleteDatabase(new File("data/data/com.didekindroid/databases/mock.db"));
        assertThat(isClosedDb, is(true));
    }

    @After
    public void tearDown() throws Exception
    {
        Timber.i("In tearDown()");
    }
}