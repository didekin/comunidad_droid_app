package com.didekindroid.usuario.comunidad.dominio;

import android.content.res.Resources;
import com.didekindroid.R;
import com.didekindroid.common.dominio.SerialNumbers;

import java.io.Serializable;
import java.util.List;

import static com.didekindroid.common.ui.CommonPatterns.LINE_BREAK;
import static com.didekindroid.common.ui.CommonPatterns.SELECT;
import static com.didekindroid.usuario.comunidad.dominio.UserPatterns.*;

/**
 * User: pedro@didekin
 * Date: 01/06/15
 * Time: 17:09
 */
/* Decorator of class Usuario, with the String version of certain fields of Usuario.*/
public class UsuarioBean implements Serializable {

    private static final long serialVersionUID = SerialNumbers.USUARIO_BEAN.number;

    private String verificaPassword;

    private String prefixTf;

    private String numeroTf;

    private Usuario usuario;

    public UsuarioBean()
    {
    }

    public UsuarioBean(String userName, String alias, String password, short prefixTf, int numeroTf)
        {
            Usuario usuario = new Usuario(userName,alias,password,prefixTf,numeroTf);
        }

    public UsuarioBean(String userName,String alias,String password,String verificaPassword
            ,String prefixTf,String numeroTf)
    {
        usuario = new Usuario(userName,alias,password);
        this.verificaPassword = verificaPassword;
        this.prefixTf = prefixTf;
        this.numeroTf = numeroTf;
    }

    public boolean validate(Resources resources, StringBuilder errorMsg)
    {
        return validateAlias(resources.getText(R.string.alias), errorMsg)
                & validatePassword(resources,errorMsg)
                & validatePrefixTf(resources.getText(R.string.telefono_prefix_rotulo), errorMsg)
                & validateNumeroTf(resources.getText(R.string.telefono_numero), errorMsg)
                & validateUserName(resources.getText(R.string.email_hint), errorMsg);
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
            usuario.setPassword(null);
        }
        return isValid;
    }

    protected boolean validatePrefixTf(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = PREFIX.pattern.matcher(prefixTf).matches()
                && !SELECT.pattern.matcher(prefixTf).find();
        if (!isValid) {
            errorMsg.append(text + LINE_BREAK.literal);
        }else{
            usuario.setPrefixTf(Short.parseShort(prefixTf));
        }
        return isValid;
    }

    protected boolean validateNumeroTf(CharSequence text, StringBuilder errorMsg)
    {
        boolean isValid = TELEFONO.pattern.matcher(numeroTf).matches()
                && !SELECT.pattern.matcher(numeroTf).find();
        if (!isValid) {
            errorMsg.append(text + LINE_BREAK.literal);
        }else{
            usuario.setNumeroTf(Integer.parseInt(numeroTf));
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

    public void addUsuarioComunidad(UsuarioComunidad usuarioCom)
    {
        usuario.addUsuarioComunidad(usuarioCom);
    }

    public void setUsuariosComunidad(List<UsuarioComunidad> usuariosComunidad)
    {
        usuario.setUsuariosComunidad(usuariosComunidad);
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

    public List<UsuarioComunidad> getUsuariosComunidad()
    {
        return usuario.getUsuariosComunidad();
    }

    public Usuario getUsuario()
    {
        return usuario;
    }
}


