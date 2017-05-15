package com.didekindroid.comunidad;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.comunidad.ComunidadObservable.comunidad;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 16:38
 */
@RunWith(AndroidJUnit4.class)
public class ComunidadObservableTest {

    Comunidad comunidad;

    @Before
    public void setUp() throws Exception
    {
        try {
            comunidad = signUpWithTkGetComu(COMU_ESCORIAL_PEPE);
        } catch (IOException | UiException e) {
            fail();
        }
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_Comunidad() throws Exception
    {
        comunidad(comunidad.getC_Id()).test().assertResult(comunidad);
    }
}