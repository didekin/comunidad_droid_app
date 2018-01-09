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

    public boolean validateUserName(Resources resources, StringBuilder errorBuilder)
    {
        boolean isValid = checkUserName(resources.getText(R.string.email_hint), errorBuilder);

        if (isValid) {
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .build();
        }
        return isValid;
    }

    boolean validateUserNameAlias(Resources resources, StringBuilder errorBuilder)
    {
        boolean isValide = checkAlias(resources.getText(R.string.alias), errorBuilder)
                & checkUserName(resources.getText(R.string.email_hint), errorBuilder);

        if (isValide) {
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .alias(alias)
                    .build();
        }
        return isValide;
    }

    public boolean validateUserNamePswd(Resources resources, StringBuilder errorMsg)
    {
        boolean isValide = checkDoublePassword(resources, errorMsg)
                & checkUserName(resources.getText(R.string.email_hint), errorMsg);

        if (isValide) {
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .password(password)
                    .build();
        }
        return isValide;
    }

    public boolean validateUserNameAliasPswd(Resources resources, StringBuilder errorMsg)
    {
        boolean isValid = checkAlias(resources.getText(R.string.alias), errorMsg)
                & checkUserName(resources.getText(R.string.email_hint), errorMsg)
                & checkSinglePassword(resources, errorMsg);

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
        boolean isValid = checkUserName(resources.getText(R.string.email_hint), errorBuilder)
                & checkSinglePassword(resources, errorBuilder);

        if (isValid) {
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .password(password)
                    .build();
        }
        return isValid;
    }

    boolean checkAlias(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = ALIAS.isPatternOk(alias);
        if (!isValid) {
            errorMsg.append(text).append(LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    boolean checkSinglePassword(Resources resources, StringBuilder errorMsg)
    {
        boolean isValid = PASSWORD.isPatternOk(password);
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.password)).append(LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    boolean checkDoublePassword(Resources resources, StringBuilder errorMsg)
    {
        if (!password.trim().equals(verificaPassword)) {
            errorMsg.append(resources.getText(R.string.password))
                    .append(resources.getText(R.string.password_different))
                    .append(LINE_BREAK.getRegexp());
            return false;
        }
        return checkSinglePassword(resources, errorMsg);
    }

    boolean checkUserName(CharSequence text, StringBuilder errorMsg)
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


