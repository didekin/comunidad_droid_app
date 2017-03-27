package com.didekindroid.usuariocomunidad.dao;

import android.support.test.rule.ActivityTestRule;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_EL_ESCORIAL;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.dao.UserComuObservable.comunidadesByUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
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

    @Before
    public void doFixture() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
    }

    @After
    public void unDoFixture() throws UiException
    {
       cleanOneUser(USER_PEPE);
    }

    @Test
    public void testComunidadesByUser() throws Exception
    {
        comunidadesByUser().test().assertOf(new Consumer<TestObserver<List<Comunidad>>>() {
            @Override
            public void accept(TestObserver<List<Comunidad>> listTestObserver) throws Exception
            {
                List<Comunidad> list = listTestObserver.values().get(0);
                assertThat(list.size(), is(1));
                assertThat(list.get(0), is(COMU_EL_ESCORIAL));
            }
        });
    }
}