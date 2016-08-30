package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.activity.BundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.testutils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.usuario.activity.utils.RolUi.PRO;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 01/10/15
 * Time: 18:57
 */
@SuppressWarnings("EmptyCatchBlock")
@RunWith(AndroidJUnit4.class)
public class UserComuDataAc_2_Test {

    private UserComuDataAc mActivity;
    private UsuarioComunidad mUsuarioComunidad;
    CleanUserEnum whatToClean = CLEAN_JUAN_AND_PEPE;

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            // Segundo usuario: newest, no ADMON.
            UsuarioComunidad userComu = makeUsuarioComunidad(mUsuarioComunidad.getComunidad(), USER_PEPE,
                    "portalB", null, "planta1", null, PRO.function);
            try {
                ServOne.regUserAndUserComu(userComu).execute();
                updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                // Primer usuario: oldest, no ADMON.
                assertThat(COMU_REAL_JUAN.hasAdministradorAuthority(), is(false));
                signUpAndUpdateTk(COMU_REAL_JUAN);
                List<UsuarioComunidad> comunidadesUserOne = ServOne.seeUserComusByUser();
                mUsuarioComunidad = comunidadesUserOne != null ? comunidadesUserOne.get(0) : null;
                Intent intent = new Intent();
                intent.putExtra(USERCOMU_LIST_OBJECT.key, mUsuarioComunidad);
                return intent;
            } catch (UiException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    //  ===========================================================================

    @Test
    public void testComuDataMn_withToken_2() throws InterruptedException
    {
        // Two users. The second one has not visible the option 'comu_data_ac_mn'.
        onView(withText(R.string.comu_data_ac_mn)).check(doesNotExist());
        // Deploy menu options.
        openActionBarOverflowOrOptionsMenu(mActivity);
        onView(withText(R.string.comu_data_ac_mn)).check(doesNotExist());
        onView(withText(R.string.see_usercomu_by_comu_ac_mn)).check(matches(isDisplayed()));
    }
}
