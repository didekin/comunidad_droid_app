package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_TRAV_PLAZUELA_11;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.data.ViewerUserComuDataAc.newViewerUserComuDataAc;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static io.reactivex.Single.just;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
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
        Intent intent = new Intent(getTargetContext(), UserComuDataAc.class);
        intent.putExtra(USERCOMU_LIST_OBJECT.key, userComuIntent);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        TKhandler.updateIsRegistered(true);
        activity = (AppCompatActivity) getInstrumentation().startActivitySync(intent);

        cleanWithTkhandler();
        viewer = newViewerUserComuDataAc((UserComuDataAc) activity);
        viewer.userComuIntent = userComuIntent;
    }

    @Test
    public void test_NewViewerUserComuDataAc() throws Exception
    {
        assertThat(viewer.getController(), isA(CtrlerUsuarioComunidad.class));
    }

    @Test
    public void test_UpdateActivityMenu() throws Exception
    {
        // Preconditions.
        Menu myMenu = doMenuPreconditions();
        // Mock controller.
        viewer.setController(new CtrlerUsuarioComunidadTest());
        // Mock menu in viewer.
        viewer.setAcMenu(myMenu);
        // Exec.
        viewer.updateActivityMenu();
        // Check.
        MenuItem comuDataItem = myMenu.findItem(R.id.comu_data_ac_mn);
        assertThat(comuDataItem.isEnabled(), is(true));
        assertThat(comuDataItem.isVisible(), is(true));
    }

    // .............................. SUBSCRIBERS ..................................

    @Test
    public void test_AcMenuObserver_1()
    {
        // Preconditions.
        boolean isOldest = false;
        Menu myMenu = doMenuPreconditions();
        // Exec and check.
        execCheck(myMenu, isOldest);
    }

    @Test
    public void test_AcMenuObserver_2()
    {
        // Preconditions.
        boolean isOldest = true;
        Menu myMenu = doMenuPreconditions();
        // Mock menu in viewer.
        viewer.setAcMenu(myMenu);
        // Exec and check.
        execCheck(myMenu, isOldest);
    }

    //  =========================  HELPERS  ===========================

    Menu doMenuPreconditions()
    {
        PopupMenu popupMenu = new PopupMenu(getTargetContext(), null);
        Menu menu = popupMenu.getMenu();
        activity.getMenuInflater().inflate(R.menu.menu_mock_one, menu);
        MenuItem menuItem = menu.findItem(R.id.comu_data_ac_mn);
        assertThat(menuItem.isEnabled(), is(false));
        assertThat(menuItem.isVisible(), is(false));
        return menu;
    }

    void execCheck(Menu myMenu, boolean isOldest)
    {
        // Exec.
        ViewerUserComuDataAc.AcMenuObserver observer = viewer.new AcMenuObserver();
        just(isOldest).subscribeWith(observer);
        // Check: no change in menu
        MenuItem comuDataItem = myMenu.findItem(R.id.comu_data_ac_mn);
        assertThat(comuDataItem.isEnabled(), is(isOldest));
        assertThat(comuDataItem.isVisible(), is(isOldest));
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