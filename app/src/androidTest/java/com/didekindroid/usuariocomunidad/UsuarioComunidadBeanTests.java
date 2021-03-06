package com.didekindroid.usuariocomunidad;

import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 02/06/15
 * Time: 13:19
 */
@RunWith(AndroidJUnit4.class)
public class UsuarioComunidadBeanTests {

    private StringBuilder errors;
    private Resources resources;

    @Before
    public void doBefore()
    {
        resources = getTargetContext().getResources();
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
    }

    @Test
    public void testPreconditions()
    {
        assertThat(errors, notNullValue());
        assertThat(resources, notNullValue());
    }

    @Test
    public void testValidate()
    {
        UsuarioComunidadBean usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "portal_ 1", "escalera-2",
                "planta.B-Ñ", "puerta.J", true, true, true, false);
        assertThat(usuarioComunidadBean.validate(resources, errors), is(true));

        usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "portal_*", "escalera/2",
                "planta.B-Ñ", "puerta.J", true, true, true, false);
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), allOf(containsString(resources.getText(R.string.reg_usercomu_portal_rot).toString())
                , containsString(resources.getText(R.string.reg_usercomu_escalera_rot).toString())));
    }

    @Test
    public void testValidatePortal()
    {
        /*PORTAL("[\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}")*/
        UsuarioComunidadBean usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "por123456tal_1",
                "escalera-2", "planta.B-Ñ", "puerta.J", true, true, true, false);
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.reg_usercomu_portal_rot).toString()));
    }

    @Test
    public void testValidateEscalera()
    {
        UsuarioComunidadBean usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "esca le ra-2", "planta.B-Ñ", "puerta.J", true, true, true, false);
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.reg_usercomu_escalera_rot).toString()));
    }

    @Test
    public void testValidatePlanta()
    {
        UsuarioComunidadBean usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "plantaB_Ñ", "puerta.J", true, true, true, false);
        assertThat(usuarioComunidadBean.validate(resources, errors), is(true));
        assertThat(errors.toString(),
                not(containsString(resources.getText(R.string.reg_usercomu_planta_hint).toString())));

        usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "planta.B+Ñ", "puerta.J", true, true, true, false);
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.reg_usercomu_planta_rot).toString()));
    }

    @Test
    public void testValidatePuerta()
    {
        UsuarioComunidadBean usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "plantaB_Ñ", "puerta12", true, true, true, false);
        assertThat(usuarioComunidadBean.validate(resources, errors), is(true));
        assertThat(errors.toString(),
                not(containsString(resources.getText(R.string.reg_usercomu_puerta_hint).toString())));

        usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "planta.BÑ", "puer ta.J", true, true, true, false);
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.reg_usercomu_puerta_rot).toString()));
    }

    @Test
    public void testValidateRoles()
    {
        UsuarioComunidadBean usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "plantaB_Ñ", "puerta12", true, true, true, true);
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));

        usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "plantaB_Ñ", "puerta12", false, false, false, false);
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.reg_usercomu_role_rot).toString()));

        usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "plantaB_Ñ", "puerta12", false, false, true, true);
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.reg_usercomu_role_rot).toString()));
    }

    @Test
    public void testSetRoles()
    {
        UsuarioComunidadBean usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "plantaB_Ñ", "puerta12", true, false, false, false);
        usuarioComunidadBean.rolesInBean();
        assertThat(usuarioComunidadBean.rolesInBean(), is("pre"));

        usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "plantaB_Ñ", "puerta12", true, false, true, false);
        usuarioComunidadBean.rolesInBean();
        assertThat(usuarioComunidadBean.rolesInBean(), is("pre,pro"));

        usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "plantaB_Ñ", "puerta12", true, true, true, false);
        usuarioComunidadBean.rolesInBean();
        assertThat(usuarioComunidadBean.rolesInBean(), is("adm,pre,pro"));

        usuarioComunidadBean = new UsuarioComunidadBean(comu_real, USER_JUAN, "poÑr6ta_1",
                "escalera-2", "plantaB_Ñ", "puerta12", true, true, true, true);
        usuarioComunidadBean.rolesInBean();
        assertThat(usuarioComunidadBean.rolesInBean(), is("adm,pre,pro,inq"));
    }
}