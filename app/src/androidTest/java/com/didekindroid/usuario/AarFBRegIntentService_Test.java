package com.didekindroid.usuario;

import android.app.NotificationManager;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.testutil.MockActivity;
import com.didekinaar.testutil.IdlingResourceForIntentServ;
import com.didekinaar.usuario.AarFBRegIntentService;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static com.didekinaar.security.TokenIdentityCacher.isGcmTokenSentServer;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler.isRegisteredUser;
import static com.didekinaar.security.TokenIdentityCacher.updateIsGcmTokenSentServer;
import static com.didekinaar.security.TokenIdentityCacher.updateIsRegistered;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 */
@RunWith(AndroidJUnit4.class)
public class AarFBRegIntentService_Test {

    private MockActivity mActivity;
    IdlingResourceForIntentServ idlingResource;
    NotificationManager mNotifyManager;
    Context context = InstrumentationRegistry.getTargetContext();

    @Rule
    public IntentsTestRule<MockActivity> intentRule = new IntentsTestRule<MockActivity>(MockActivity.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            updateIsGcmTokenSentServer(false, context);
            updateIsRegistered(false, context);
        }
    };

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        mNotifyManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        idlingResource = new IdlingResourceForIntentServ(mActivity, new AarFBRegIntentService());
        registerIdlingResources(idlingResource);
    }

    @After
    public void tearDown() throws Exception
    {
        updateIsGcmTokenSentServer(false, mActivity);
        mNotifyManager.cancelAll();
        unregisterIdlingResources(idlingResource);
        updateIsRegistered(false, context);
    }

    //  ===========================================================================

    /**
     * Test para AarFBRegIntentService methods: user not registered.
     */
    @Test
    public void testRegistrationGcmToken() throws Exception
    {
        // Preconditions for the test.
        assertThat(TKhandler.isRegisteredUser(mActivity), is(false));

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        assertThat(refreshedToken, notNullValue());
        assertThat(isGcmTokenSentServer(mActivity), is(false));
    }
}