package com.didekindroid.usuariocomunidad.listbycomu;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;
import android.widget.TextView;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_PLAZUELA_5;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.listbycomu.CtrlerUserComuByComuList.comunidad;
import static com.didekindroid.usuariocomunidad.listbycomu.CtrlerUserComuByComuList.listByEntityId;
import static com.didekindroid.usuariocomunidad.listbycomu.ViewerSeeUserComuByComu.newViewerUserComuByComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/03/17
 * Time: 14:53
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerUserComuByComuTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerUserComuByComuList controller;
    Comunidad comunidad;
    Usuario usuario;

    @Before
    public void setUp() throws IOException, UiException
    {
        Usuario usuario = signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        comunidad = userComuDaoRemote.seeUserComusByUser().get(0).getComunidad();
        controller = new CtrlerUserComuByComuList(newViewerUserComuByComu(null, activityRule.getActivity()));
    }

    @After
    public void clearUp() throws UiException
    {
        cleanOneUser(USER_JUAN);
    }

    // .................................... OBSERVABLES .................................

    public void testListByEntityId() throws IOException, UiException
    {
        listByEntityId(usuario.getuId()).test().assertOf(new Consumer<TestObserver<List<UsuarioComunidad>>>() {
            @Override
            public void accept(TestObserver<List<UsuarioComunidad>> listTestObserver) throws Exception
            {
                UsuarioComunidad usuarioComunidad = listTestObserver.values().get(0).get(0);
                assertThat(usuarioComunidad.getUsuario(), is(USER_JUAN));
            }
        });
    }

    public void testComunidad() throws IOException, UiException
    {
        comunidad(comunidad.getC_Id()).test().assertResult(COMU_LA_PLAZUELA_5);
    }

    // .................................... INSTANCE METHODS .................................

    public void testLoadItemsByEntitiyId() throws IOException, UiException
    {
        assertThat(controller.loadItemsByEntitiyId(comunidad.getC_Id()), is(true));
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    public void testOnSuccessLoadItemsById()
    {
        List<UsuarioComunidad> userComus = new ArrayList<>();
        controller.onSuccessLoadItemsInList(userComus);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    public void testComunidadData()
    {
        assertThat(controller.comunidadData(comunidad.getC_Id()), is(true));
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    public void testOnSuccessComunidadData(){
        controller.onSuccessComunidadData(comunidad);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }

    // .................................... HELPERS .................................

    static final class ViewerUserComuByComuForTest extends ViewerSeeUserComuByComu {

        private ViewerUserComuByComuForTest(ListView view, TextView nombreComuView, Activity activity)
        {
            super(view, nombreComuView, activity);
        }

        @Override
        void processLoadedItemsinView(List<UsuarioComunidad> itemList)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
        }

        @Override
        void setNombreComuViewText(String text)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
        }
    }
}
