package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 30/09/15
 * Time: 11:32
 */
@RunWith(AndroidJUnit4.class)
public class UserComuDataAc_Delete_Test {

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            UsuarioComunidad usuarioComunidad = null;
            try {
                usuarioComunidad = new UsuarioComunidad.UserComuBuilder(signUpGetComu(COMU_TRAV_PLAZUELA_PEPE), USER_PEPE)
                        .planta("One")
                        .roles(PROPIETARIO.function)
                        .build();
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(USERCOMU_LIST_OBJECT.key, usuarioComunidad);
        }
    };

    /*  ===========================================================================*/

    @Test
    public void testDeleteUserComu_1()
    {
        onView(withId(R.id.usercomu_data_ac_delete_button)).perform(click());
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(comuSearchAcLayout));
        // Sale de la aplicación.
        try {
            checkBack(onView(withId(comuSearchAcLayout)));
        } catch (NoActivityResumedException e) {
            assertThat(e, isA(NoActivityResumedException.class));
        }
    }
}