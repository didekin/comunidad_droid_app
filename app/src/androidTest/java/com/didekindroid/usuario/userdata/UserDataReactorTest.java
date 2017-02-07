package com.didekindroid.usuario.userdata;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.userdata.UserDataReactor.userDataLoaded;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;

/**
 * User: pedro@didekin
 * Date: 07/02/17
 * Time: 14:13
 */
@RunWith(AndroidJUnit4.class)
public class UserDataReactorTest {

    @Before
    public void doFixture() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
    }

    @After
    public void unDoFixture() throws UiException
    {
        cleanOptions(CLEAN_PEPE);
    }

    //  =======================================================================================
    // ............................ OBSERVABLES ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testUserDataLoaded()
    {
        userDataLoaded().test().assertResult(USER_PEPE);
    }

    @Test
    public void testUserDataModified() throws Exception
    {

    }

    @Test
    public void testDeletedTokenInBd() throws Exception
    {

    }

    @Test
    public void testUserNameModified() throws Exception
    {

    }

    @Test
    public void testAliasModified() throws Exception
    {

    }

}