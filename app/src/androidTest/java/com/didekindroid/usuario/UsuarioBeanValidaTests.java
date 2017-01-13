package com.didekindroid.usuario;

import android.content.res.Resources;

import com.didekindroid.R;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 01/06/15
 * Time: 18:47
 */
public abstract class UsuarioBeanValidaTests {

    private StringBuilder errors;
    private Resources resources;

    protected abstract Resources getResources();

    @Before
    public void doBefore()
    {
        resources = getResources();
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
    }

    @Test
    public void checkPreconditions()
    {
        assertThat(errors, notNullValue());
        assertThat(resources, notNullValue());
    }

    @Test
    public void testValidate() throws Exception
    {
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", "alias1", "password1", "password1");
        assertThat(usuarioBean.validate(resources, errors), is(true));
        usuarioBean = new UsuarioBean("", "alias1", "password1", "password1");
        assertThat(usuarioBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.email_hint).toString()));
        usuarioBean = new UsuarioBean("", "alias1", "password1", "");
        assertThat(usuarioBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), allOf(containsString(resources.getText(R.string.password_different).toString())
                , containsString(resources.getText(R.string.password_different).toString())));
    }

    @Test
    public void testValidateModified_1()
    {
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", "alias1", "", "");
        assertThat(usuarioBean.validateWithOnePassword(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.password).toString()));

        usuarioBean = new UsuarioBean("user@name.com", "alias1", "password", "");
        assertThat(usuarioBean.validateWithOnePassword(resources, errors), is(true));
        assertThat(usuarioBean.getUsuario().getUserName(), is("user@name.com"));
        assertThat(usuarioBean.getUsuario().getAlias(), is("alias1"));
        assertThat(usuarioBean.getUsuario().getPassword(), is("password"));

        usuarioBean = new UsuarioBean("user@name.com", "alias1", "password", "hola");
        assertThat(usuarioBean.validateWithOnePassword(resources, errors), is(true));
    }

    @Test
    public void testValidatePassword() throws Exception
    {
        /*PASSWORD("[0-9a-zA-Z_]{6,60}")*/
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", "alias1", "23pas_sword1", "23pas_sword1");
        assertThat(usuarioBean.validate(resources, errors), is(true));
        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23AB__s_word1", "23AB__s_word1");
        assertThat(usuarioBean.validate(resources, errors), is(true));
        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23AB*_s_word1", "23AB*_s_word1");
        assertThat(usuarioBean.validate(resources, errors), is(false));
        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23AB_s_word1", "23AB*_s_word1");
        assertThat(usuarioBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.password_different).toString()));
    }

    @Test
    public void testValidateUserName() throws Exception
    {
        /*EMAIL("[\\w\\._\\-]{1,48}@[\\w\\-_]{1,40}\\.[\\w&&[^0-9]]{1,10}")*/
        UsuarioBean usuarioBean = new UsuarioBean("user_@name.com", "alias1", "23pas_sword1", "23pas_sword1");
        assertThat(usuarioBean.validate(resources, errors), is(true));
    }
}