package com.didekindroid.usuario;

import android.content.res.Resources;

import com.didekindroid.R;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;

import static com.didekinlib.model.common.dominio.ValidDataPatterns.ALIAS;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.EMAIL;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.PASSWORD;

/**
 * User: pedro@didekin
 * Date: 01/06/15
 * Time: 17:09
 */
public final class UsuarioBean implements Serializable {

    private final String userName;
    private final String alias;
    private final String password;
    private final String verificaPassword;
    private Usuario usuario;

    public UsuarioBean(String userName, String alias, String password, String verificaPassword)
    {
        this.userName = userName;
        this.password = password;
        this.alias = alias;
        this.verificaPassword = verificaPassword;
    }

    boolean validateRegUser(Resources resources, StringBuilder errorBuilder)
    {
        boolean isValide = validateAlias(resources.getText(R.string.alias), errorBuilder)
                & validateUserName(resources.getText(R.string.email_hint), errorBuilder);

        if (isValide) {
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .alias(alias)
                    .build();
        }
        return isValide;
    }

    public boolean validateWithoutAlias(Resources resources, StringBuilder errorMsg)
    {
        boolean isValide = validateDoublePassword(resources, errorMsg)
                & validateUserName(resources.getText(R.string.email_hint), errorMsg);

        if (isValide) {
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .password(password)
                    .build();
        }
        return isValide;
    }

    public boolean validateWithOnePassword(Resources resources, StringBuilder errorMsg)
    {
        boolean isValid = validateAlias(resources.getText(R.string.alias), errorMsg)
                & validateUserName(resources.getText(R.string.email_hint), errorMsg)
                & validateSinglePassword(resources, errorMsg);

        if (isValid) {
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .alias(alias)
                    .password(password)
                    .build();
        }
        return isValid;
    }

    public boolean validateLoginData(Resources resources, StringBuilder errorBuilder)
    {
        boolean isValid = validateUserName(resources.getText(R.string.email_hint), errorBuilder)
                & validateSinglePassword(resources, errorBuilder);

        if (isValid) {
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .password(password)
                    .build();
        }
        return isValid;
    }

    boolean validateAlias(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = ALIAS.isPatternOk(alias);
        if (!isValid) {
            errorMsg.append(text).append(LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    boolean validateSinglePassword(Resources resources, StringBuilder errorMsg)
    {
        boolean isValid = PASSWORD.isPatternOk(password);
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.password)).append(LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    boolean validateDoublePassword(Resources resources, StringBuilder errorMsg)
    {
        if (!password.trim().equals(verificaPassword)) {
            errorMsg.append(resources.getText(R.string.password))
                    .append(resources.getText(R.string.password_different))
                    .append(LINE_BREAK.getRegexp());
            return false;
        }
        return validateSinglePassword(resources, errorMsg);
    }

    boolean validateUserName(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = EMAIL.isPatternOk(userName);
        if (!isValid) {
            errorMsg.append(text).append(LINE_BREAK.getRegexp());
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

    public Usuario getUsuario()
    {
        return usuario;
    }
}


