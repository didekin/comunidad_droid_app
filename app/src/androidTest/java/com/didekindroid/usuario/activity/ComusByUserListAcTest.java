package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.masterdata.dominio.Municipio;
import com.didekindroid.masterdata.dominio.Provincia;
import com.didekindroid.usuario.dominio.Comunidad;
import com.didekindroid.usuario.dominio.UsuarioComunidad;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.usuario.common.UserIntentExtras.USUARIO_COMUNIDAD_REG;
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
        Comunidad comunidad = new Comunidad("Calle", "Real", (short) 5, "Bis",
                new Municipio(new Provincia((short) 3), (short) 13));
        UsuarioComunidad usuarioComunidad = new UsuarioComunidad(comunidad, null, "portal", "esc", "plantaX",
                "door", "pro");
        Intent intent = new Intent();
        intent.putExtra(USUARIO_COMUNIDAD_REG.toString(), usuarioComunidad);
        mActivity = mActivityRule.launchActivity(intent);
    }

    @Test
    public void testFixture(){
        assertThat(mActivity,notNullValue());
    }
}