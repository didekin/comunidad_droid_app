package com.didekindroid.router;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.router.ActivityRouter.acByDefaultNoRegUser;
import static com.didekindroid.router.ActivityRouter.acByDefaultRegUser;
import static com.didekindroid.router.ActivityRouter.acRouter;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 02/05/17
 * Time: 16:39
 */
@RunWith(AndroidJUnit4.class)
public class ActivityRouterTest {

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_TK_HANDLER);
    }

    @Test
    public void test_NextActivityFromMn() throws Exception
    {
        // No registered user.
        assertThat(TKhandler.isRegisteredUser(), is(false));
        assertThat(acRouter.nextActivityFromMn(-1).equals(acByDefaultNoRegUser), is(true));

        // Registered user.
        TKhandler.updateIsRegistered(true);
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(acRouter.nextActivityFromMn(-1).equals(acByDefaultRegUser), is(true));
    }
}