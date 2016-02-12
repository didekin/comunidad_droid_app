package com.didekindroid.usuario.dominio;

import android.content.res.Resources;

import com.didekin.usuario.dominio.Usuario;
import com.didekindroid.R;

import static com.didekin.common.domain.DataPatterns.ALIAS;
import static com.didekin.common.domain.DataPatterns.EMAIL;
import static com.didekin.common.domain.DataPatterns.LINE_BREAK;
import static com.didekin.common.domain.DataPatterns.PASSWORD;

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
        boolean isValide = validateAlias(resources.getText(
                R.string.alias), errorMsg)
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

    private boolean validateAlias(CharSequence text, StringBuilder errorMsg)
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
            errorMsg.append(resources.getText(R.string.password).toString()).append(LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    public boolean validateDoublePassword(Resources resources, StringBuilder errorMsg)
    {
        if (!password.trim().equals(verificaPassword)) {
            errorMsg.append(resources.getText(R.string.password).toString())
                    .append(resources.getText(R.string
                            .password_different).toString())
                    .append(LINE_BREAK.getRegexp());
            return false;
        }
        return validateSinglePassword(resources, errorMsg);
    }

    protected boolean validateUserName(CharSequence text, StringBuilder errorMsg)
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

    public String getVerificaPassword()
    {
        return verificaPassword;
    }

    public Usuario getUsuario()
    {
        return usuario;
    }
}


