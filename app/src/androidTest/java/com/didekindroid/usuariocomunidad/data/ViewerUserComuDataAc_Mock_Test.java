package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_TRAV_PLAZUELA_11;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.doMockMenu;
import static com.didekindroid.lib_one.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static io.reactivex.Single.just;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/06/17
 * Time: 12:29
 */
@RunWith(AndroidJUnit4.class)
public class ViewerUserComuDataAc_Mock_Test {

    final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    AppCompatActivity activity;
    ViewerUserComuDataAc viewer;
    UsuarioComunidad userComuIntent = new UsuarioComunidad.UserComuBuilder(
            new Comunidad.ComunidadBuilder()
                    .c_id(233L)
                    .copyComunidadNonNullValues(COMU_TRAV_PLAZUELA_11)
                    .build(),
            USER_PEPE)
            .userComuRest(COMU_TRAV_PLAZUELA_PEPE).build();

    @Before
    public void setUp() throws Exception
    {
        Intent intent = new Intent(getTargetContext(), ActivityMock.class);
        intent.putExtra(USERCOMU_LIST_OBJECT.key, userComuIntent);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        TKhandler.updateIsRegistered(true);
        activity = (AppCompatActivity) getInstrumentation().startActivitySync(intent);

        viewer = new ViewerUserComuDataAc(null, activity);
        viewer.userComuIntent = userComuIntent;
    }

    @After
    public void cleanUp()
    {
        cleanWithTkhandler();
    }

    // .............................. VIEWER ..................................

    @Test
    public void test_SetAcMenu() throws Exception
    {
        // Preconditions.
        Menu mockMenu = doMockMenu(activity, R.menu.menu_mock_one);
        viewer.setController(new CtrlerUsuarioComunidadTest());
        // Exec.
        viewer.setAcMenu(mockMenu);
        // Check.
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        assertThat(viewer.acMenu, is(mockMenu));
    }

    // .............................. SUBSCRIBERS ..................................

    @Test
    public void test_AcMenuObserver_1()
    {
        execCheckPower(false);
    }

    @Test
    public void test_AcMenuObserver_2()
    {
        execCheckPower(true);
    }

    //  =========================  HELPERS  ===========================

    void execCheckPower(boolean hasComunidadPower)
    {
        // Exec.
        ViewerUserComuDataAc.AcMenuObserver observer = viewer.new AcMenuObserver();
        just(hasComunidadPower).subscribeWith(observer);
        // Check
        assertThat(viewer.showComuDataMn.get(), is(hasComunidadPower));
    }

    class CtrlerUsuarioComunidadTest extends CtrlerUsuarioComunidad {

        @Override
        public boolean checkIsOldestAdmonUser(DisposableSingleObserver<Boolean> observer, Comunidad comunidad)
        {
            assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            return true;
        }
    }
}