package com.didekindroid.router;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.lib_one.api.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.confidencialidadLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.router.MnRouterAction.confidencialidad_mn;
import static com.didekindroid.router.MnRouterAction.incid_see_closed_by_comu_mn;
import static com.didekindroid.router.MnRouterAction.incid_see_open_by_comu_mn;
import static com.didekindroid.router.MnRouterAction.see_usercomu_by_user_mn;
import static com.didekindroid.router.MnRouterAction.user_data_mn;
import static com.didekindroid.testutil.ActivityTestUtil.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_alias;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.userDataAcRsId;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 16/09/17
 * Time: 13:21
 */
@RunWith(AndroidJUnit4.class)
public class ViewerDrawerMain_Reg_Test extends ViewerDrawerMain_Test_abs {

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
        viewerDrawer = intentRule.getActivity().getViewerDrawer();
        assertThat(viewerDrawer.getController().isRegisteredUser(), is(true));
        waitAtMost(4, SECONDS).until(() -> viewerDrawer.drawerHeaderRot != null);
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOneUser(USER_PEPE);
    }

    //    ============================ TESTS ==============================

    @Test
    public void testDoViewInViewer()
    {
        clickNavigateUp();
        waitAtMost(6, SECONDS).until(isViewDisplayed(
                allOf(
                        withText(USER_PEPE.getAlias()),
                        withId(drawer_header_view)
                )
        ));
    }

    @Test
    public void testDoViewForRegUser() throws InterruptedException
    {
        // Preconditions.
        final Bundle bundleState = new Bundle(1);
        bundleState.putString(user_alias.key, "MOCK_ALIAS");

        // Exec.
        intentRule.getActivity().runOnUiThread(() -> viewerDrawer.doViewForRegUser(bundleState));
        // Check.
        clickNavigateUp();
        waitAtMost(6, SECONDS).until(isViewDisplayed(
                allOf(
                        withText("MOCK_ALIAS"),
                        withId(drawer_header_view)
                )
        ));
    }

    @Test
    public void testSaveState()
    {
        Bundle bundleState = new Bundle(1);
        viewerDrawer.saveState(bundleState);
        assertThat(bundleState.getString(user_alias.key), is(USER_PEPE.getAlias()));
    }

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
        checkMainDrawerMenu(confidencialidad_mn.getMnItemRsId(), confidencialidadLayout, comuSearchAcLayout);
        checkMainDrawerMenu(see_usercomu_by_user_mn.getMnItemRsId(), seeUserComuByUserFrRsId, comuSearchAcLayout);
        checkMainDrawerMenu(user_data_mn.getMnItemRsId(), userDataAcRsId, comuSearchAcLayout);
        checkMainDrawerMenu(incid_see_open_by_comu_mn.getMnItemRsId(), incidSeeByComuAcLayout, comuSearchAcLayout);
        checkMainDrawerMenu(incid_see_closed_by_comu_mn.getMnItemRsId(), incidSeeByComuAcLayout, comuSearchAcLayout);
    }
}