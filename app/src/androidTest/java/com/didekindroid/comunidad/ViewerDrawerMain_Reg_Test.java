package com.didekindroid.comunidad;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.confidencialidad;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.incid_closed;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.incid_open;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.user_comus;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.user_data;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.confidencialidadLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeCloseAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeOpenAcLayout;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.userDataAcRsId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 16/09/17
 * Time: 13:21
 */
@RunWith(AndroidJUnit4.class)
public class ViewerDrawerMain_Reg_Test extends ViewerDrawerMainTest {

    @Rule
    public IntentsTestRule<ComuSearchAc> intentRule = new IntentsTestRule<ComuSearchAc>(ComuSearchAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            // Precondition: user registered.
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_PEPE);
            } catch (IOException | UiException e) {
                fail();
            }
            return new Intent();
        }
    };

    @Before
    public void setUp()
    {
        activity = intentRule.getActivity();
        viewerDrawer = activity.viewerDrawer;
        assertThat(viewerDrawer.getController().isRegisteredUser(), is(true));
        navView = viewerDrawer.getViewInViewer().findViewById(drawer_nav_view);
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOneUser(USER_PEPE);
    }

    //    ============================ TESTS ==============================

    /**
     * It tests implicitly ViewerDrawerMain.doViewInViewer() and  explicitly ViewerDrawerMain.buildMenu().
     */
    @Test
    public void test_BuildMenu() throws Exception
    {
        checkMenuItems(true);
    }

    /**
     * It tests implicitly ViewerDrawerMain.doViewInViewer() and explicitly DrawerMainMnItemSelListener.onNavigationItemSelected().
     */
    @Test
    public void test_OnNavigationItemSelected_2() throws IOException, UiException, InterruptedException
    {
        // Exec and check. All come UP to ComuSearch because there is not previous navigation.
        checkMainDrawerMenu(confidencialidad.resourceId, confidencialidadLayout, comuSearchAcLayout);
        checkMainDrawerMenu(user_comus.resourceId, seeUserComuByUserFrRsId, comuSearchAcLayout);
        checkMainDrawerMenu(user_data.resourceId, userDataAcRsId, comuSearchAcLayout);
        checkMainDrawerMenu(incid_open.resourceId, incidSeeOpenAcLayout, comuSearchAcLayout);
        checkMainDrawerMenu(incid_closed.resourceId, incidSeeCloseAcLayout, comuSearchAcLayout);
    }
}