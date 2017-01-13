package com.didekindroid.usuario;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.Usuario;
import com.didekindroid.exception.UiException;
import com.didekindroid.testutil.IdlingResourceForIntentServ;
import com.didekindroid.testutil.MockActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 */
@RunWith(AndroidJUnit4.class)
public class RegGcmIntentService_Test {

    IdlingResourceForIntentServ idlingResource;
    Context context = creator.get().getContext();
    Usuario pepe;
    MockActivity mActivity;

    @Rule
    public ActivityTestRule<MockActivity> intentRule =
            new ActivityTestRule<MockActivity>(MockActivity.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                pepe = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                // Preconditions for the test before launching activity.
                assertThat(usuarioDaoRemote.getGcmToken() == null, is(true));
                assertThat(TKhandler.isRegisteredUser(), is(true));
                assertThat(TKhandler.isGcmTokenSentServer(), is(false));
            } catch (IOException | UiException e) {
                e.printStackTrace();
            }
        }
    };

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        idlingResource = new IdlingResourceForIntentServ(mActivity, new RegGcmIntentService());
        registerIdlingResources(idlingResource);
    }

    @After
    public void tearDown() throws Exception
    {
        if (idlingResource != null){
            unregisterIdlingResources(idlingResource);
        }
        cleanOneUser(USER_PEPE);
    }

    //  ===========================================================================

    /**
     * Test para RegGcmIntentService methods: user not registered.
     */
    @Test
    public void testOnHandleIntent() throws Exception
    {
        mActivity.launchRegService();
//        Thread.sleep(2000);
        assertThat(TKhandler.isGcmTokenSentServer(), is(true));
        assertThat(usuarioDaoRemote.getGcmToken() != null, is(true));
    }
}