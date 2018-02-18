package com.didekindroid.lib_one.usuario;

import android.content.res.Resources;

import com.didekindroid.R;

import org.junit.Before;
import org.junit.Test;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
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
public class UsuarioBeanTests {

    private StringBuilder errors;
    private Resources resources;

    @Before
    public void doBefore()
    {
        resources = httpInitializer.get().getContext().getResources();
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
    }

    @Test
    public void checkPreconditions()
    {
        assertThat(errors, notNullValue());
        assertThat(resources, notNullValue());
    }

    @Test
    public void test_ValidateRegUser() throws Exception
    {
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", "alias1", null, null);
        assertThat(usuarioBean.validateUserNameAlias(resources, errors), is(true));
        usuarioBean = new UsuarioBean("", "alias1", null, null);
        assertThat(usuarioBean.validateUserNameAlias(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.email_hint).toString()));
    }

    @Test
    public void test_ValidateWithoutAlias() throws Exception
    {
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", null, "23AB_s_word1", "23AB*_s_word1");
        assertThat(usuarioBean.validateUserNamePswd(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.password_different).toString()));
        usuarioBean = new UsuarioBean("user@name.com", null, "23AB__s_word1", "23AB__s_word1");
        assertThat(usuarioBean.validateUserNamePswd(resources, errors), is(true));
    }

    @Test
    public void test_ValidateWithOnePassword() throws Exception
    {
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", "alias1", "", null);
        assertThat(usuarioBean.validateUserNameAliasPswd(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.password).toString()));

        usuarioBean = new UsuarioBean("user@name.com", "alias1", "password", null);
        assertThat(usuarioBean.validateUserNameAliasPswd(resources, errors), is(true));
        assertThat(usuarioBean.getUsuario().getUserName(), is("user@name.com"));
        assertThat(usuarioBean.getUsuario().getAlias(), is("alias1"));
        assertThat(usuarioBean.getUsuario().getPassword(), is("password"));

        usuarioBean = new UsuarioBean("user@name.com", "alias1", "password", "hola");
        assertThat(usuarioBean.validateUserNameAliasPswd(resources, errors), is(true));
    }

    @Test
    public void test_ValidateLoginData() throws Exception
    {
        UsuarioBean usuarioBean = new UsuarioBean("user_@name.com", null, "23pas_sword1", null);
        assertThat(usuarioBean.validateLoginData(resources, errors), is(true));
        usuarioBean = new UsuarioBean("user_name.com", null, "23pas_sword1", null);
        assertThat(usuarioBean.validateLoginData(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.email_hint).toString()));
    }

    @Test
    public void test_ValidateAlias() throws Exception
    {
        UsuarioBean usuarioBean = new UsuarioBean(null, "alias_com", null, null);
        assertThat(usuarioBean.checkAlias(resources.getText(R.string.alias), errors), is(true));
        usuarioBean = new UsuarioBean(null, "alias+com", null, null);
        assertThat(usuarioBean.checkAlias(resources.getText(R.string.alias), errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.alias).toString()));
    }

    @Test
    public void test_ValidateSinglePassword() throws Exception
    {
        /*PASSWORD("[0-9a-zA-Z_]{6,60}")*/
        UsuarioBean usuarioBean = new UsuarioBean(null, null, "23AB_s_word1*", null);
        assertThat(usuarioBean.checkSinglePassword(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.password).toString()));
        usuarioBean = new UsuarioBean(null, null, "23AB_s_word1", null);
        assertThat(usuarioBean.checkSinglePassword(resources, errors), is(true));
    }

    @Test
    public void test_ValidateDoublePassword() throws Exception
    {
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", "alias1", "23AB_s_word1", "23AB*_s_word1");
        assertThat(usuarioBean.checkDoublePassword(resources, errors), is(false));
        assertThat(errors.toString(), allOf(
                containsString(resources.getText(R.string.password_different).toString()),
                containsString(resources.getText(R.string.password).toString())
        ));

        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23AB__s_word1", "23AB__s_word1");
        assertThat(usuarioBean.checkDoublePassword(resources, errors), is(true));
    }

    @Test
    public void test_ValidateUserName() throws Exception
    {
        /*EMAIL("[\\w\\._\\-]{1,48}@[\\w\\-_]{1,40}\\.[\\w&&[^0-9]]{1,10}")*/
        UsuarioBean usuarioBean = new UsuarioBean("user_@name.com", null, null, null);
        assertThat(usuarioBean.checkUserName(resources.getText(R.string.email_hint), errors), is(true));
        usuarioBean = new UsuarioBean("user_+name.com", "alias1", "23pas_sword1", "23pas_sword1");
        assertThat(usuarioBean.checkUserName(resources.getText(R.string.email_hint), errors), is(false));
    }
}