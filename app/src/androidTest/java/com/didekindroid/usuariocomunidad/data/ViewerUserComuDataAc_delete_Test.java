package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlags;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 07/06/17
 * Time: 12:29
 */
@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
@RunWith(AndroidJUnit4.class)
public class ViewerUserComuDataAc_delete_Test {

    private UserComuDataAc activity;

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            UsuarioComunidad userComu = null;
            try {
                userComu = new UsuarioComunidad.UserComuBuilder(signUpGetComu(COMU_TRAV_PLAZUELA_PEPE), USER_PEPE)
                        .planta("One")
                        .roles(PROPIETARIO.function)
                        .build();
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(USERCOMU_LIST_OBJECT.key, userComu);
        }
    };

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        waitAtMost(4, SECONDS).until(() -> activity.viewer, notNullValue());
    }

    // .............................. LISTENERS ..................................

    @Test
    public void test_DeleteButtonListener()
    {
        // Before.
        assertThat(activity.viewer.getController().getTkCacher().isUserRegistered(), is(true));
        // Exec.
        onView(withId(R.id.usercomu_data_ac_delete_button)).perform(click());
        // Check.
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(comuSearchAcLayout));
        intended(hasFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK));
    }
}