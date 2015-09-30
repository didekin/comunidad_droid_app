package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;
import com.didekin.serviceone.domain.UsuarioComunidad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.usuario.activity.utils.UserIntentExtras.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeComunidad;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeUsuarioComunidad;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 11/07/15
 * Time: 16:51
 */
@RunWith(AndroidJUnit4.class)
public class ComusByUserListAcTest {

    private ComusByUserListAc mActivity;

    @Rule
    public ActivityTestRule<ComusByUserListAc> mActivityRule = new ActivityTestRule<>(
            ComusByUserListAc.class, true, false);

    @Before
    public void setUp() throws Exception
    {
        Comunidad comunidad = makeComunidad("Calle", "Real", (short) 5, "Bis",
                new Municipio( (short) 13,new Provincia((short) 3)));
        UsuarioComunidad usuarioComunidad = makeUsuarioComunidad(comunidad, null, "portal", "esc", "plantaX",
                "door", "pro");
        Intent intent = new Intent();
        intent.putExtra(USERCOMU_LIST_OBJECT.toString(), usuarioComunidad);
        mActivity = mActivityRule.launchActivity(intent);
    }

    @Test
    public void testFixture(){
        assertThat(mActivity,notNullValue());
    }
}