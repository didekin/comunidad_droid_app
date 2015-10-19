package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.security.UiException;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PRESIDENTE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PROPIETARIO;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.dominio.DomainDataUtils.*;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.containsString;

/**
 * User: pedro@didekin
 * Date: 01/10/15
 * Time: 18:57
 */
@RunWith(AndroidJUnit4.class)
public class UserComuDataAcTest_2 {

    private UserComuDataAc mActivity;
    private UsuarioComunidad mUsuarioComunidad;
    CleanEnum whatToClean = CLEAN_JUAN_AND_PEPE;

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            UsuarioComunidad userComu = makeUsuarioComunidad(mUsuarioComunidad.getComunidad(), USER_PEPE,
                    "portalB", null, "planta1", null, PROPIETARIO.function.concat(",").concat(PRESIDENTE.function));
            ServOne.regUserAndUserComu(userComu);
            updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
        }

        @Override
        protected Intent getActivityIntent()
        {
            signUpAndUpdateTk(COMU_REAL_JUAN);
            List<UsuarioComunidad> comunidadesUserOne = null;
            try {
                comunidadesUserOne = ServOne.seeUserComusByUser();
            } catch (UiException e) {
            }
            mUsuarioComunidad = comunidadesUserOne.get(0);

            // We use that comunidad as the one to associate to the present user.
            Intent intent = new Intent();
            intent.putExtra(USERCOMU_LIST_OBJECT.extra, mUsuarioComunidad);

            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return intent;
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
    public void testOnCreate()
    {
        onView(withId(R.id.reg_usercomu_portal_ed)).check(matches(withText(containsString(mUsuarioComunidad.getPortal()))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_escalera_ed)).check(matches(withText(containsString(mUsuarioComunidad.getEscalera()))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_planta_ed)).check(matches(withText(containsString(mUsuarioComunidad.getPlanta()))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_puerta_ed)).check(matches(withText(containsString(mUsuarioComunidad.getPuerta()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testComuDataMn_withToken_2() throws InterruptedException
    {
        // Two users. The second one has not visible the option 'comu_data_ac_mn'.
        onView(withText(R.string.comu_data_ac_mn)).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(mActivity);
        onView(withText(R.string.comu_data_ac_mn)).check(doesNotExist());
        onView(withText(R.string.see_usercomu_by_comu_ac_mn)).check(matches(isDisplayed()));
    }
}
