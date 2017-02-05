package com.didekindroid.usuario.password;

import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.password.PswdChangeReactor.isPasswordChanged;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 03/02/17
 * Time: 20:31
 */
public class PswdChangeReactorTest {

    @Before
    public void doFixture() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
    }
    @After
    public void unDoFixture() throws UiException
    {
        cleanOptions(CLEAN_PEPE);
    }

    //  =======================================================================================
    // ............................ OBSERVABLES ..................................
    //  =======================================================================================

    @Test
    public void testPasswordChangeRemote() throws Exception
    {
        isPasswordChanged("new_password").test().assertResult(1);

        String newPswd = usuarioDao.getUserData().getPassword();
        assertThat(newPswd, allOf(
                is("new_password"),
                not(is(USER_PEPE.getPassword()))
        ));
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

}