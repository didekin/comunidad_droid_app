package com.didekindroid.usuario.dominio;

import android.content.res.Resources;
import com.didekin.serviceone.domain.SerialNumbers;
import com.didekin.serviceone.domain.Usuario;
import com.didekindroid.R;

import java.io.Serializable;

import static com.didekindroid.uiutils.CommonPatterns.LINE_BREAK;
import static com.didekindroid.uiutils.CommonPatterns.SELECT;
import static com.didekindroid.usuario.dominio.UserPatterns.*;

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
    private final String prefixTf;
    private final String numeroTf;
    private Usuario usuario;

    public UsuarioBean(String userName, String alias, String password, String verificaPassword
            , String prefixTf, String numeroTf)
    {
        this.userName = userName;
        this.password = password;
        this.alias = alias;
        this.verificaPassword = verificaPassword;
        this.prefixTf = prefixTf;
        this.numeroTf = numeroTf;
    }

    public boolean validate(Resources resources, StringBuilder errorMsg)
    {
        boolean isValide = validateAlias(resources.getText(R.string.alias), errorMsg)
                & validatePassword(resources, errorMsg)
                & validatePrefixTf(resources.getText(R.string.telefono_prefix_rotulo), errorMsg)
                & validateNumeroTf(resources.getText(R.string.telefono_numero), errorMsg)
                & validateUserName(resources.getText(R.string.email_hint), errorMsg);

        if (isValide){
            usuario = new Usuario.UsuarioBuilder()
                    .userName(userName)
                    .alias(alias)
                    .password(password)
                    .prefixTf(Short.parseShort(prefixTf))
                    .numeroTf(Integer.parseInt(numeroTf))
                    .build();
        }

        return isValide;
    }

    private boolean validateAlias(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = ALIAS.pattern.matcher(usuario.getAlias()).matches()
                && !SELECT.pattern.matcher(usuario.getAlias()).find();
        if (!isValid) {
            errorMsg.append(text + LINE_BREAK.literal);
        }
        return isValid;
    }

    protected boolean validatePassword(Resources resources, StringBuilder errorMsg)
    {
        String password = usuario.getPassword();

        if (!password.trim().equals(verificaPassword.toString())) {
            errorMsg.append(resources.getText(R.string.password).toString() + resources.getText(R.string
                    .password_different).toString() + LINE_BREAK.literal);
            return false;
        }

        boolean isValid = PASSWORD.pattern.matcher(password).matches()
                && !SELECT.pattern.matcher(password).find();
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.password).toString() + LINE_BREAK.literal);
        }
        return isValid;
    }

    protected boolean validatePrefixTf(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = PREFIX.pattern.matcher(prefixTf).matches()
                && !SELECT.pattern.matcher(prefixTf).find();
        if (!isValid) {
            errorMsg.append(text + LINE_BREAK.literal);
        }
        return isValid;
    }

    protected boolean validateNumeroTf(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = TELEFONO.pattern.matcher(numeroTf).matches()
                && !SELECT.pattern.matcher(numeroTf).find();
        if (!isValid) {
            errorMsg.append(text + LINE_BREAK.literal);
        }
        return isValid;
    }


    protected boolean validateUserName(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = EMAIL.pattern.matcher(usuario.getUserName()).matches()
                && !SELECT.pattern.matcher(usuario.getUserName()).find();
        if (!isValid) {
            errorMsg.append(text + LINE_BREAK.literal);
        }
        return isValid;
    }

    public String getUserName()
    {
        return usuario.getUserName();
    }

    public String getAlias()
    {
        return usuario.getAlias();
    }

    public String getPassword()
    {
        return usuario.getPassword();
    }

    public String getVerificaPassword()
    {
        return verificaPassword;
    }

    public String getPrefixTf()
    {
        return prefixTf;
    }

    public String getNumeroTf()
    {
        return numeroTf;
    }

    public Usuario getUsuario()
    {
        return usuario;
    }
}


