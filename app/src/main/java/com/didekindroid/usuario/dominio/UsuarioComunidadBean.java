package com.didekindroid.usuario.dominio;

import android.content.res.Resources;
import com.didekindroid.R;
import com.didekindroid.common.dominio.SerialNumbers;
import com.google.common.primitives.Booleans;

import java.io.Serializable;

import static com.didekindroid.common.ui.CommonPatterns.LINE_BREAK;
import static com.didekindroid.common.ui.CommonPatterns.SELECT;
import static com.didekindroid.usuario.dominio.UserPatterns.*;

/**
 * User: pedro@didekin
 * Date: 01/06/15
 * Time: 16:59
 */
public class UsuarioComunidadBean implements Serializable {

    private static final long serialVersionUID = SerialNumbers.USUARIO_COMUNIDAD_BEAN.number;

    public transient final boolean isPresidente;
    public transient final boolean isAdministrador;
    public transient final boolean isPropietario;
    public transient final boolean isInquilino;

    private UsuarioComunidad usuarioComunidad;
    private final UsuarioBean usuarioBean;
    private final ComunidadBean comunidadBean;

    public UsuarioComunidadBean(ComunidadBean comunidadBean, UsuarioBean usuarioBean,
                                String portal, String escalera, String planta, String puerta,
                                boolean isPresidente, boolean isAdministrador, boolean isPropietario,
                                boolean isInquilino)
    {
        Usuario usuario = usuarioBean != null ? usuarioBean.getUsuario() : null;

        usuarioComunidad = new UsuarioComunidad(comunidadBean.getComunidad(), usuario,
                portal, escalera, planta, puerta);

        this.comunidadBean = comunidadBean;
        this.usuarioBean = usuarioBean;

        this.isPresidente = isPresidente;
        this.isAdministrador = isAdministrador;
        this.isPropietario = isPropietario;
        this.isInquilino = isInquilino;
    }

    public void setRoles()
    {
        StringBuilder rolesBuilder = new StringBuilder();

        if (isAdministrador) {
            rolesBuilder.append(Roles.ADMINISTRADOR.getFunction()).append(",");
        }
        if (isPresidente) {
            rolesBuilder.append(Roles.PRESIDENTE.getFunction()).append(",");
        }
        if (isPropietario) {
            rolesBuilder.append(Roles.PROPIETARIO.getFunction()).append(",");
        }
        if (isInquilino) {
            rolesBuilder.append(Roles.INQUILINO.getFunction());
        }

        if (rolesBuilder.charAt(rolesBuilder.length() - 1) == ',') {
            rolesBuilder.deleteCharAt(rolesBuilder.length() - 1);
        }

        usuarioComunidad.setRoles(rolesBuilder.toString());
    }

    public boolean validate(Resources resources, StringBuilder errorMsg)
    {
        return validatePortal(resources, errorMsg)
                & validateEscalera(resources, errorMsg)
                & validatePlanta(resources, errorMsg)
                & validatePuerta(resources, errorMsg)
                & validateRoles(resources, errorMsg)
                & validateUsuario(resources, errorMsg)
                & validateComunidad(resources, errorMsg);
    }

    protected boolean validatePortal(Resources resources, StringBuilder errorMsg)
    {
        if (usuarioComunidad.getPortal().trim().isEmpty()) return true;

        boolean isValid = PORTAL.pattern.matcher(usuarioComunidad.getPortal()).matches()
                && !SELECT.pattern.matcher(usuarioComunidad.getPortal()).find();
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.vivienda_portal_hint) + LINE_BREAK.literal);
            usuarioComunidad.setPortal(null);
        }
        return isValid;
    }

    protected boolean validateEscalera(Resources resources, StringBuilder errorMsg)
    {
        if (usuarioComunidad.getEscalera().trim().isEmpty()) return true;

        boolean isValid = ESCALERA.pattern.matcher(usuarioComunidad.getEscalera()).matches()
                && !SELECT.pattern.matcher(usuarioComunidad.getEscalera()).find();
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.vivienda_escalera_hint) + LINE_BREAK.literal);
            usuarioComunidad.setEscalera(null);
        }
        return isValid;
    }

    protected boolean validatePlanta(Resources resources, StringBuilder errorMsg)
    {
        if (usuarioComunidad.getPlanta().trim().isEmpty()) return true;

        boolean isValid = PLANTA.pattern.matcher(usuarioComunidad.getPlanta()).matches()
                && !SELECT.pattern.matcher(usuarioComunidad.getPlanta()).find();
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.vivienda_planta_hint) + LINE_BREAK.literal);
            usuarioComunidad.setPlanta(null);
        }
        return isValid;
    }

    protected boolean validatePuerta(Resources resources, StringBuilder errorMsg)
    {
        if (usuarioComunidad.getPuerta().trim().isEmpty()) return true;

        boolean isValid = PUERTA.pattern.matcher(usuarioComunidad.getPuerta()).matches()
                && !SELECT.pattern.matcher(usuarioComunidad.getPuerta()).find();
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.vivienda_puerta_hint) + LINE_BREAK.literal);
            usuarioComunidad.setPuerta(null);
        }
        return isValid;
    }

    protected boolean validateRoles(Resources resources, StringBuilder errorMsg)
    {
        int rolesSize = Booleans.countTrue(isAdministrador, isPropietario, isPresidente, isInquilino);
        boolean isValid = false;

        /*No son compatibles los roles de propietario e inquilino en una misma comunidad y vivienda.*/
        if (rolesSize > 0 && !(isPropietario && isInquilino)) {
            setRoles();
            isValid = true;
        } else {
            errorMsg.append(resources.getText(R.string.comunidad_role) + LINE_BREAK.literal);
        }
        return isValid;
    }

    protected boolean validateUsuario(Resources resources, StringBuilder errorMsg)
    {
        // The user is authenticated by an accessToken. usuarioBean may be null.
        if (usuarioBean == null) {
            return true;
        }
        return usuarioBean.validate(resources, errorMsg);
    }

    protected boolean validateComunidad(Resources resources, StringBuilder errorMsg)
    {
        if (comunidadBean == null) {
            errorMsg.append(resources.getText(R.string.comunidad_null) + LINE_BREAK.literal);
            return false;
        }

        return comunidadBean.validate(resources, errorMsg);
    }

    public ComunidadBean getComunidadBean()
    {
        return comunidadBean;
    }

    public String getEscalera()
    {
        return usuarioComunidad.getEscalera();
    }

    public String getPlanta()
    {
        return usuarioComunidad.getPlanta();
    }

    public String getPortal()
    {
        return usuarioComunidad.getPortal();
    }

    public String getPuerta()
    {
        return usuarioComunidad.getPuerta();
    }

    public String getRoles()
    {
        return usuarioComunidad.getRoles();
    }

    public UsuarioBean getUsuarioBean()
    {
        return usuarioBean;
    }

    public UsuarioComunidad getUsuarioComunidad()
    {
        return usuarioComunidad;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsuarioComunidadBean that = (UsuarioComunidadBean) o;

        if (!usuarioBean.equals(that.usuarioBean)) return false;
        if (!comunidadBean.equals(that.comunidadBean)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = usuarioBean.hashCode();
        result = 31 * result + comunidadBean.hashCode();
        return result;
    }
}
