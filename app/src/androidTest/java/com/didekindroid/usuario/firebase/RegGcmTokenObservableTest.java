package com.didekindroid.usuario.firebase;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.testutil.MockActivity;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import io.reactivex.observers.TestObserver;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.firebase.FirebaseTokenReactor.regGcmTokenSingle;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 18/01/17
 * Time: 11:15
 */
@RunWith(AndroidJUnit4.class)
public class RegGcmTokenObservableTest {

    TestObserver<Integer> testSubscriber;
    MockActivity activity;

    @Rule
    public IntentsTestRule<MockActivity> intentRule = new IntentsTestRule<MockActivity>(MockActivity.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                UserComuDataTestUtil.signUpAndUpdateTk(COMU_REAL_JUAN);
            } catch (IOException | UiException e) {
                e.printStackTrace();
            }
        }
    };

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        assertThat(activity, notNullValue());
    }

    @After
    public void tearDown() throws Exception
    {
        testSubscriber.cancel();
        cleanOptions(CLEAN_JUAN);
    }

    /** We test directly the observable. */
    @Test
    public void testRegGcmTokenCompletable_1() throws Exception
    {
        testSubscriber = new TestObserver<>();
        regGcmTokenSingle().subscribe(testSubscriber);
        testSubscriber.assertComplete();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValue(1);

        assertThat(TKhandler.isGcmTokenSentServer(), is(false));
    }

    @Test
    public void testRegGcmTokenCompletable_2() throws Exception
    {
//        testSubscriber = new TestSubscriber<Integer>();

    }
}