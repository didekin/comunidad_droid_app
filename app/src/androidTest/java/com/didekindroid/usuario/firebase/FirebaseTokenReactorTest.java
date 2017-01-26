package com.didekindroid.usuario.firebase;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.usuario.firebase.FirebaseTokenReactor.updatedGcmTkSingle;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;

/**
 * User: pedro@didekin
 * Date: 26/01/17
 * Time: 09:48
 */
@RunWith(AndroidJUnit4.class)
public class FirebaseTokenReactorTest {

    @Before
    public void getFixture() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
    }

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_JUAN);
    }

    //  ====================================================================================
    //    .................................... OBSERVABLES .................................
    //  ====================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testUpdatedGcmTkSingle() throws Exception
    {
        updatedGcmTkSingle().test().assertResult(1).assertComplete().assertNoErrors();
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Test
    public void testCheckGcmToken() throws Exception
    {

    }

    @Test
    public void testCheckGcmTokenSync() throws Exception
    {

    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

}