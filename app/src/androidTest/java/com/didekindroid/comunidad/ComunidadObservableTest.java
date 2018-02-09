package com.didekindroid.comunidad;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.comunidad.ComunidadObservable.comunidad;
import static com.didekindroid.comunidad.ComunidadObservable.comunidadesFound;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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

    @Test
    public void test_ComunidadesFound() throws Exception
    {
        comunidadesFound(comunidad).test().assertOf(new Consumer<TestObserver<List<Comunidad>>>() {
            @Override
            public void accept(@NonNull TestObserver<List<Comunidad>> listTestObserver) throws Exception
            {
                assertThat(listTestObserver.values().get(0).get(0), is(comunidad));
            }
        });
    }
}