package com.didekindroid.usuariocomunidad.repository;

import android.support.test.rule.ActivityTestRule;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_EL_ESCORIAL;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.repository.UserComuObservable.comunidadByUserAndComu;
import static com.didekindroid.usuariocomunidad.repository.UserComuObservable.comunidadModificada;
import static com.didekindroid.usuariocomunidad.repository.UserComuObservable.comunidadesByUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 18:00
 */
public class UserComuObservableTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);
    Comunidad comunidad;

    @Before
    public void doFixture() throws IOException, UiException
    {
        comunidad = signUpWithTkGetComu(COMU_ESCORIAL_PEPE);
    }

    @After
    public void unDoFixture() throws UiException
    {
        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testComunidadesByUser() throws Exception
    {
        comunidadesByUser().test().assertOf(listTestObserver -> {
            List<Comunidad> list = listTestObserver.values().get(0);
            assertThat(list.size(), is(1));
            assertThat(list.get(0), is(COMU_EL_ESCORIAL));
        });
    }

    @Test
    public void test_ComunidadModificada() throws Exception
    {
        Comunidad newComunidad = new Comunidad.ComunidadBuilder().copyComunidadNonNullValues(comunidad).nombreVia("nuevo_nombre_via").build();
        comunidadModificada(newComunidad).test().assertResult(1);
    }

    @Test
    public void test_ComunidadByUserAndComu() throws Exception
    {
        UsuarioComunidad userComuBack = new UsuarioComunidad.UserComuBuilder(comunidad, USER_PEPE).userComuRest(COMU_ESCORIAL_PEPE).build();
        comunidadByUserAndComu(comunidad).test().assertResult(userComuBack);
    }
}