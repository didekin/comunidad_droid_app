package com.didekindroid.usuariocomunidad;

import android.support.test.rule.ActivityTestRule;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.CtrlerUserReg.userAndComuRegistered;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_LA_FUENTE_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/05/17
 * Time: 09:44
 */
public class CtrlerUserRegTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);
    CtrlerUserReg controller;
    ActivityMock activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        controller = new CtrlerUserReg(new ViewerMock<>(activity));
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        cleanOptions(CLEAN_PEPE);
    }

    //  =======================================================================================
    // ............................ OBSERVABLES ..................................
    //  =======================================================================================

    @Test
    public void test_UserAndComuRegistered() throws Exception
    {
        checkNoInitCache();

        userAndComuRegistered(COMU_ESCORIAL_PEPE).test().assertComplete();
        checkInitTokenCache();
        assertThat(userComuDaoRemote.seeUserComusByUser().get(0), is(COMU_ESCORIAL_PEPE));
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Test
    public void test_RegisterComuAndUser() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            controller.registerComuAndUser(, COMU_LA_FUENTE_PEPE);
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }
}