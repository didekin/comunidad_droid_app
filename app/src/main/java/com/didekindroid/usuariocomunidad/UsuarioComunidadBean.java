package com.didekindroid.usuariocomunidad;

import android.content.res.Resources;

import com.didekindroid.R;
import com.didekinlib.model.common.dominio.ValidDataPatterns;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import static com.didekinlib.model.common.dominio.ValidDataPatterns.PORTAL;


/**
 * User: pedro@didekin
 * Date: 01/06/15
 * Time: 16:59
 */
@SuppressWarnings("unused")
public final class UsuarioComunidadBean {


    private final boolean isPresidente;
    private final boolean isAdministrador;
    private final boolean isPropietario;
    private final boolean isInquilino;
    private final String portal;
    private final String escalera;
    private final String planta;
    private final String puerta;
    private final Usuario usuario;
    private final Comunidad comunidad;

    private UsuarioComunidad usuarioComunidad;

    public UsuarioComunidadBean(Comunidad comunidad, Usuario usuario,
                                String portal, String escalera, String planta, String puerta,
                                boolean isPresidente, boolean isAdministrador, boolean isPropietario,
                                boolean isInquilino)
    {
        this.comunidad = comunidad;
        this.usuario = usuario;
        this.portal = portal;
        this.escalera = escalera;
        this.planta = planta;
        this.puerta = puerta;
        this.isPresidente = isPresidente;
        this.isAdministrador = isAdministrador;
        this.isPropietario = isPropietario;
        this.isInquilino = isInquilino;
    }

    // .......................... Auxiliary role methods ...........................

    String rolesInBean()
    {
        StringBuilder rolesBuilder = new StringBuilder();

        if (isAdministrador) {
            rolesBuilder.append(RolUi.ADM.function).append(",");
        }
        if (isPresidente) {
            rolesBuilder.append(RolUi.PRE.function).append(",");
        }
        if (isPropietario) {
            rolesBuilder.append(RolUi.PRO.function).append(",");
        }
        if (isInquilino) {
            rolesBuilder.append(RolUi.INQ.function);
        }

        if (rolesBuilder.charAt(rolesBuilder.length() - 1) == ',') {
            rolesBuilder.deleteCharAt(rolesBuilder.length() - 1);
        }

        return rolesBuilder.toString();
    }

    private int countRoles(boolean... isRole)
    {
        int count = 0;
        for (boolean value : isRole) {
            if (value) {
                count++;
            }
        }
        return count;
    }

    // ................................................................................

    public boolean validate(Resources resources, StringBuilder errorMsg)
    {
        if (checkUserComuData(resources, errorMsg)) {
            usuarioComunidad = makeUsuarioComunidad();
            return true;
        }
        return false;
    }

    /*  [\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}  */
    private boolean validatePortal(Resources resources, StringBuilder errorMsg)
    {
        if (portal.trim().isEmpty()) return true;

        boolean isValid = PORTAL.isPatternOk(portal);
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.reg_usercomu_portal_rot)).append(ValidDataPatterns.LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    /*  [\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}  */
    private boolean validateEscalera(Resources resources, StringBuilder errorMsg)
    {
        if (escalera.trim().isEmpty()) return true;

        boolean isValid = ValidDataPatterns.ESCALERA.isPatternOk(escalera);
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.reg_usercomu_escalera_rot)).append(ValidDataPatterns.LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    /*  [\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}  */
    private boolean validatePlanta(Resources resources, StringBuilder errorMsg)
    {
        if (planta.trim().isEmpty()) return true;

        boolean isValid = ValidDataPatterns.PLANTA.isPatternOk(planta);
        if (!isValid)
            errorMsg.append(resources.getText(R.string.reg_usercomu_planta_rot)).append(ValidDataPatterns.LINE_BREAK.getRegexp());
        return isValid;
    }

    /*  [\\w_ñÑáéíóúüÜ\\.\\-]{1,10}  */
    private boolean validatePuerta(Resources resources, StringBuilder errorMsg)
    {
        if (puerta.trim().isEmpty()) return true;

        boolean isValid = ValidDataPatterns.PUERTA.isPatternOk(puerta);
        if (!isValid) {
            errorMsg.append(resources.getText(R.string.reg_usercomu_puerta_rot)).append(ValidDataPatterns.LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    private boolean validateRoles(Resources resources, StringBuilder errorMsg)
    {
        int rolesSize = countRoles(isAdministrador, isPropietario, isPresidente, isInquilino);
        boolean isValid = false;

        /*No son compatibles los rolesInBean de propietario e inquilino en una misma comunidad y vivienda.*/
        if (rolesSize > 0 && !(isPropietario && isInquilino)) {
            isValid = true;
        } else {
            errorMsg.append(resources.getText(R.string.reg_usercomu_role_rot)).append(ValidDataPatterns.LINE_BREAK.getRegexp());
        }
        return isValid;
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
        return usuario.equals(that.usuario) && comunidad.equals(that.comunidad);
    }

    @Override
    public int hashCode()
    {
        int result = usuario.hashCode();
        result = 31 * result + comunidad.hashCode();
        return result;
    }

    // ............................. HELPERS ....................................

    private boolean checkUserComuData(Resources resources, StringBuilder errorMsg)
    {
        return validatePortal(resources, errorMsg)
                & validateEscalera(resources, errorMsg)
                & validatePlanta(resources, errorMsg)
                & validatePuerta(resources, errorMsg)
                & validateRoles(resources, errorMsg);
    }

    private UsuarioComunidad makeUsuarioComunidad()
    {
        return new UsuarioComunidad
                .UserComuBuilder(comunidad, usuario)
                .portal(portal)
                .escalera(escalera)
                .planta(planta)
                .puerta(puerta)
                .roles(rolesInBean())
                .build();
    }
}
