package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;

/**
 * User: pedro@didekin
 * Date: 01/10/15
 * Time: 09:41
 */
@RunWith(AndroidJUnit4.class)
public class ComuDataAcTest {

    private ComuDataAc mActivity;
    CleanEnum whatToClean = CLEAN_JUAN;
    Comunidad mComunidad;

    @Rule
    public IntentsTestRule<ComuDataAc> intentRule = new IntentsTestRule<ComuDataAc>(ComuDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
        }

        @Override
        protected Intent getActivityIntent()
        {
            signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
            mComunidad = ServOne.getComusByUser().get(0);
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.extra, mComunidad.getC_Id());
            return intent;
        }
    };

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

//    =============================================================================================

    @Test
    public void testOnCreate() throws Exception
    {

    }
}