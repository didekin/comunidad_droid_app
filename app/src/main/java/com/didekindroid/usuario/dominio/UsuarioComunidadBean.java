package com.didekindroid.usuario.dominio;

import android.content.res.Resources;

import com.didekin.common.dominio.DataPatterns;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.RolCheckBox;
import com.google.common.primitives.Booleans;

import static com.didekin.common.dominio.DataPatterns.PORTAL;

/**
 * User: pedro@didekin
 * Date: 01/06/15
 * Time: 16:59
 */
public final class UsuarioComunidadBean {


    private final boolean isPresidente;
    private final boolean isAdministrador;
    private final boolean isPropietario;
    private final boolean isInquilino;
    private final String portal;
    private final String escalera;
    private final String planta;
    private final String puerta;
    private final UsuarioBean usuarioBean;
    private final ComunidadBean comunidadBean;

    private UsuarioComunidad usuarioComunidad;

    public UsuarioComunidadBean(ComunidadBean comunidadBean, UsuarioBean usuarioBean,
                                String portal, String escalera, String planta, String puerta,
                                boolean isPresidente, boolean isAdministrador, boolean isPropietario,
                                boolean isInquilino)
    {
        this.comunidadBean = comunidadBean;
        this.usuarioBean = usuarioBean;
        this.portal = portal;
        this.escalera = escalera;
        this.planta = planta;
        this.puerta = puerta;
        this.isPresidente = isPresidente;
        this.isAdministrador = isAdministrador;
        this.isPropietario = isPropietario;
        this.isInquilino = isInquilino;
    }

    String rolesInBean()
    {
        StringBuilder rolesBuilder = new StringBuilder();

        if (isAdministrador) {
            rolesBuilder.append(RolCheckBox.ADM.function).append(",");
        }
        if (isPresidente) {
            rolesBuilder.append(RolCheckBox.PRE.function).append(",");
        }
        if (isPropietario) {
            rolesBuilder.append(RolCheckBox.PRO.function).append(",");
        }
        if (isInquilino) {
            rolesBuilder.append(RolCheckBox.INQ.function);
        }

        if (rolesBuilder.charAt(rolesBuilder.length() - 1) == ',') {
            rolesBuilder.deleteCharAt(rolesBuilder.length() - 1);
        }

        return rolesBuilder.toString();
    }

    public boolean validate(Resources resources, StringBuilder errorMsg)
    {
        boolean isValid = validatePortal(resources, errorMsg)
                & validateEscalera(resources, errorMsg)
                & validatePlanta(resources, errorMsg)
                & validatePuerta(resources, errorMsg)
                & validateRoles(resources, errorMsg)
                & validateUsuario(resources, errorMsg)
                & validateComunidad(resources, errorMsg);

        if (isValid) {

            Usuario usuario = usuarioBean != null ? usuarioBean.getUsuario() : null;

            usuarioComunidad = new UsuarioComunidad
                    .UserComuBuilder(comunidadBean.getComunidad(), usuario)
                    .portal(portal)
                    .escalera(escalera)
                    .planta(planta)
                    .puerta(puerta)
                    .roles(rolesInBean())
                    .build();
        }

        return isValid;
    }

    /*  [\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}  */
    boolean validatePortal(Resources resources, StringBuilder errorMsg)
    {
        if (portal.trim().isEmpty()) return true;

        boolean isValid = PORTAL.isPatternOk(portal);
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.reg_usercomu_portal_hint)).append(DataPatterns.LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    /*  [\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}  */
    boolean validateEscalera(Resources resources, StringBuilder errorMsg)
    {
        if (escalera.trim().isEmpty()) return true;

        boolean isValid = DataPatterns.ESCALERA.isPatternOk(escalera);
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.reg_usercomu_escalera_hint)).append(DataPatterns.LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    /*  [\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}  */
    boolean validatePlanta(Resources resources, StringBuilder errorMsg)
    {
        if (planta.trim().isEmpty()) return true;

        boolean isValid = DataPatterns.PLANTA.isPatternOk(planta);
        if (!isValid)
            errorMsg.append(resources.getText(R.string.reg_usercomu_planta_hint)).append(DataPatterns.LINE_BREAK.getRegexp());
        return isValid;
    }

    /*  [\\w_ñÑáéíóúüÜ\\.\\-]{1,10}  */
    boolean validatePuerta(Resources resources, StringBuilder errorMsg)
    {
        if (puerta.trim().isEmpty()) return true;

        boolean isValid = DataPatterns.PUERTA.isPatternOk(puerta);
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.reg_usercomu_puerta_hint)).append(DataPatterns.LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    boolean validateRoles(Resources resources, StringBuilder errorMsg)
    {
        int rolesSize = Booleans.countTrue(isAdministrador, isPropietario, isPresidente, isInquilino);
        boolean isValid = false;

        /*No son compatibles los rolesInBean de propietario e inquilino en una misma comunidad y vivienda.*/
        if (rolesSize > 0 && !(isPropietario && isInquilino)) {
            isValid = true;
        } else {
            errorMsg.append(resources.getText(R.string.reg_usercomu_role_rot)).append(DataPatterns.LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    boolean validateUsuario(Resources resources, StringBuilder errorMsg)
    {
        // The user is authenticated by an accessToken. usuarioBean may be null.
        if (usuarioBean == null) {
            return true;
        }
        // In this point the instance of usuario in usuarioBean is created.
        return usuarioBean.validate(resources, errorMsg);
    }

    boolean validateComunidad(Resources resources, StringBuilder errorMsg)
    {
        if (comunidadBean == null) {
            errorMsg.append(resources.getText(R.string.reg_usercomu_comunidad_null)).append(DataPatterns.LINE_BREAK.getRegexp());
            return false;
        }
        // In this point the instance of comunidad in usuarioBean is created.
        return comunidadBean.validate(resources, errorMsg);
    }

    public String getEscalera()
    {
        return escalera;
    }

    public String getPlanta()
    {
        return planta;
    }

    public String getPortal()
    {
        return portal;
    }

    public String getPuerta()
    {
        return puerta;
    }

    public boolean isAdministrador()
    {
        return isAdministrador;
    }

    public boolean isInquilino()
    {
        return isInquilino;
    }

    public boolean isPresidente()
    {
        return isPresidente;
    }

    public boolean isPropietario()
    {
        return isPropietario;
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

        return usuarioBean.equals(that.usuarioBean) && comunidadBean.equals(that.comunidadBean);

    }

    @Override
    public int hashCode()
    {
        int result = usuarioBean.hashCode();
        result = 31 * result + comunidadBean.hashCode();
        return result;
    }
}
