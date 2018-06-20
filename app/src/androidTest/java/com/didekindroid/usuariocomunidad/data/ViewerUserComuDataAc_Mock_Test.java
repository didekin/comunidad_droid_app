package com.didekindroid.usuariocomunidad.data;

import android.app.Activity;
import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.view.Menu;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.usuariocomunidad.repository.CtrlerUsuarioComunidad;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.observers.DisposableSingleObserver;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.testutil.UiTestUtil.doMockMenu;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/06/17
 * Time: 12:29
 */
@RunWith(AndroidJUnit4.class)
public class ViewerUserComuDataAc_Mock_Test {

    private Activity activity;
    private ViewerUserComuDataAc viewer;

    @Before
    public void setUp() throws Exception
    {
        secInitializer.get().getTkCacher().updateIsRegistered(true);
        activity = getInstrumentation()
                .startActivitySync(
                        new Intent(getTargetContext(), ActivityMock.class)
                                .putExtra(USERCOMU_LIST_OBJECT.key, comu_real_rodrigo)
                                .setFlags(FLAG_ACTIVITY_NEW_TASK)
                );

        viewer = new ViewerUserComuDataAc(null, activity);
        viewer.userComuIntent = comu_real_rodrigo;
    }

    @After
    public void cleanUp()
    {
        cleanWithTkhandler();
    }

    // .............................. VIEWER ..................................

    @Test
    public void test_SetAcMenu_1()
    {
        // Preconditions.
        Menu mockMenu = doMockMenu(activity, R.menu.menu_mock_one);
        viewer.setController(new CtrlerUsuarioComunidadTest(true));
        // Exec.
        viewer.setAcMenu(mockMenu);
        // Check.
        assertThat(viewer.acMenu, is(mockMenu));
        assertThat(viewer.showComuDataMn.get(), is(true));
    }

    @Test
    public void test_SetAcMenu_2()
    {
        // Preconditions.
        Menu mockMenu = doMockMenu(activity, R.menu.menu_mock_one);
        viewer.setController(new CtrlerUsuarioComunidadTest(false));
        // Exec.
        viewer.setAcMenu(mockMenu);
        // Check.
        assertThat(viewer.acMenu, is(mockMenu));
        assertThat(viewer.showComuDataMn.get(), is(false));
    }

    //  =========================  HELPERS  ===========================

    class CtrlerUsuarioComunidadTest extends CtrlerUsuarioComunidad {

        final boolean hasPower;

        CtrlerUsuarioComunidadTest(boolean hasPowerIn)
        {
            super();
            hasPower = hasPowerIn;
        }

        @Override
        public boolean isOldestOrAdmonUserComu(DisposableSingleObserver<Boolean> observer, Comunidad comunidad)
        {
            return hasPower;
        }
    }
}