package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.DomainDataUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.didekindroid.common.utils.AppIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.COMU_SEARCH_AC;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.USER_DATA_AC;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * User: pedro@didekin
 * Date: 27/08/15
 * Time: 11:38
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByComuAcTest_slow {

    SeeUserComuByComuAc mActivity;
    SeeUserComuByComuFr mFragment;
    long comunidadId;
    Intent intent;

    @Rule
    public ActivityTestRule<SeeUserComuByComuAc> mActivityRule =
            new ActivityTestRule<>(SeeUserComuByComuAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(6000);
    }

    @Before
    public void setUp() throws Exception
    {
        Thread.sleep(5000);
        // User is registered, with a comunidad in the intent.
        signUpAndUpdateTk(DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE);
        // We insert a second user with the same comunidad.
        signUpAndUpdateTk(DomainDataUtils.COMU_PLAZUELA5_JUAN);

        // We get the id of the comunidad we will put in the intent.
        List<UsuarioComunidad> usuariosComu = ServOne.seeUserComusByUser();
        comunidadId = usuariosComu.get(0).getComunidad().getC_Id(); // COMU_PLAZUELA5_JUAN.
        // We put the comunidad in the intent.
        intent = new Intent();
        intent.putExtra(COMUNIDAD_ID.extra, comunidadId);

        mActivity = mActivityRule.launchActivity(intent);
        mFragment = (SeeUserComuByComuFr) mActivity.getFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

//    =================================================================================================================

    @Test
    public void testUserComuByUserMn_withToken() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testUserDataMn_withToken() throws InterruptedException
    {
        USER_DATA_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testComuSearchMn_withToken() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(mActivity);
    }
}