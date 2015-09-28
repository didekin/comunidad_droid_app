package com.didekindroid.usuario.dominio;

import android.content.res.Resources;
import com.didekin.serviceone.domain.Usuario;
import com.didekindroid.R;

import static com.didekin.serviceone.domain.DataPatterns.*;
import static com.didekindroid.uiutils.CommonPatterns.LINE_BREAK;
import static com.didekindroid.uiutils.CommonPatterns.SELECT;

/**
 * User: pedro@didekin
 * Date: 01/06/15
 * Time: 17:09
 */
public final class UsuarioBean {

    private final String userName;
    private final String password;
    private final String alias;
    private final String verificaPassword;
    private Usuario usuario;

    public UsuarioBean(String userName, String alias, String password, String verificaPassword)
    {
        this.userName = userName;
        this.password = password;
        this.alias = alias;
        this.verificaPassword = verificaPassword;
    }

    public boolean validate(Resources resources, StringBuilder errorMsg)
    {
        boolean isValide = validateAlias(resources.getText(R.string.alias), errorMsg)
                & validateDoublePassword(resources, errorMsg)
                & validateUserName(resources.getText(R.string.email_hint), errorMsg);

        if (isValide) {
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .alias(alias)
                    .password(password)
                    .build();
        }
        return isValide;
    }

    public boolean validateWithOnePassword(Resources resources, StringBuilder errorMsg)
    {
        boolean isValid = validateAlias(resources.getText(R.string.alias), errorMsg)
                & validateUserName(resources.getText(R.string.email_hint), errorMsg)
                & validateSinglePassword(resources,errorMsg);

        if (isValid) {
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .alias(alias)
                    .password(password)
                    .build();
        }
        return isValid;
    }

    private boolean validateAlias(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = ALIAS.pattern.matcher(alias).matches()
                && !SELECT.pattern.matcher(alias).find();
        if (!isValid) {
            errorMsg.append(text + LINE_BREAK.literal);
        }
        return isValid;
    }

    boolean validateSinglePassword(Resources resources, StringBuilder errorMsg)
    {
        boolean isValid = PASSWORD.pattern.matcher(password).matches()
                && !SELECT.pattern.matcher(password).find();
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.password).toString() + LINE_BREAK.literal);
        }
        return isValid;
    }

    public boolean validateDoublePassword(Resources resources, StringBuilder errorMsg)
    {
        if (!password.trim().equals(verificaPassword.toString())) {
            errorMsg.append(resources.getText(R.string.password).toString() + resources.getText(R.string
                    .password_different).toString() + LINE_BREAK.literal);
            return false;
        }
        return validateSinglePassword(resources, errorMsg);
    }

    protected boolean validateUserName(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = EMAIL.pattern.matcher(userName).matches()
                && !SELECT.pattern.matcher(userName).find();
        if (!isValid) {
            errorMsg.append(text + LINE_BREAK.literal);
        }
        return isValid;
    }

    public String getAlias()
    {
        return alias;
    }

    public String getPassword()
    {
        return password;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getVerificaPassword()
    {
        return verificaPassword;
    }

    public Usuario getUsuario()
    {
        return usuario;
    }
}


