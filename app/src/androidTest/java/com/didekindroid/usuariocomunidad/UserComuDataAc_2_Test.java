package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.security.TokenIdentityCacher;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarTestUtil.updateSecurityData;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.usuariocomunidad.RolUi.PRO;
import static com.didekinaar.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

/**
 * User: pedro@didekin
 * Date: 01/10/15
 * Time: 18:57
 */
@SuppressWarnings("EmptyCatchBlock")
@RunWith(AndroidJUnit4.class)
public class UserComuDataAc_2_Test {

    private UserComuDataAc mActivity;
    UsuarioComunidad mUsuarioComunidad;
    UsuarioDataTestUtils.CleanUserEnum whatToClean = CLEAN_JUAN_AND_PEPE;

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            // Segundo usuario: newest, no ADMON.
            UsuarioComunidad userComu = UserComuTestUtil.makeUsuarioComunidad(mUsuarioComunidad.getComunidad(), UsuarioDataTestUtils.USER_PEPE,
                    "portalB", null, "planta1", null, PRO.function);
            try {
                AarUserComuServ.regUserAndUserComu(userComu).execute();
                updateSecurityData(UsuarioDataTestUtils.USER_PEPE.getUserName(), UsuarioDataTestUtils.USER_PEPE.getPassword());
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                // Primer usuario: oldest, no ADMON.
                assertThat(UserComuTestUtil.COMU_REAL_JUAN.hasAdministradorAuthority(), is(false));
                UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_REAL_JUAN);
                List<UsuarioComunidad> comunidadesUserOne = AarUserComuServ.seeUserComusByUser();
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
        onView(ViewMatchers.withText(R.string.comu_data_ac_mn)).check(doesNotExist());
        // Deploy menu options.
        openActionBarOverflowOrOptionsMenu(mActivity);
        onView(ViewMatchers.withText(R.string.comu_data_ac_mn)).check(doesNotExist());
        onView(ViewMatchers.withText(R.string.see_usercomu_by_comu_ac_mn)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteUserComu_2() throws UiException
    {
        whatToClean = CLEAN_JUAN;

        /* Borramos un usuario y verificamos datos de seguridad y navegación.*/
        onView(ViewMatchers.withId(R.id.usercomu_data_ac_delete_button)).perform(click());
        onView(ViewMatchers.withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));

        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TokenIdentityCacher.TKhandler.isRegisteredUser(), is(false));
    }
}
