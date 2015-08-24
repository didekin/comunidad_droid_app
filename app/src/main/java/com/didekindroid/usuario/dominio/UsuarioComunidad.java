package com.didekindroid.usuario.dominio;

import com.didekindroid.common.dominio.SerialNumbers;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 10/06/15
 * Time: 10:54
 */
public class UsuarioComunidad implements Serializable {

    private static final long serialVersionUID = SerialNumbers.USUARIO_COMUNIDAD.number;

    private String portal;
    private String escalera;
    private String planta;
    private String puerta;
    private String roles;

    private Timestamp fechaAlta;
    private Timestamp fechaMod;

    private Usuario usuario;
    private Comunidad comunidad;

    public UsuarioComunidad()
    {
    }

    public UsuarioComunidad(Comunidad comunidad)
    {
        this.comunidad = comunidad;
    }

    public UsuarioComunidad(final Usuario usuario, final Comunidad comunidad)
    {
        this(comunidad);
        this.usuario = usuario;
    }

    public UsuarioComunidad(final Comunidad comunidad, Usuario usuario, String portal, String escalera,
                            String planta, String puerta)
    {
        this(usuario, comunidad);
        this.escalera = escalera;
        this.planta = planta;
        this.portal = portal;
        this.puerta = puerta;
    }

    public UsuarioComunidad(final Comunidad comunidad, final Usuario usuario, final String portal
            , final String escalera, final String planta, final String puerta, final String roles)
    {
        this(comunidad, usuario, portal, escalera, planta, puerta);
        this.roles = roles;
    }

    public Comunidad getComunidad()
    {
        return comunidad;
    }

    public String getEscalera()
    {
        return escalera;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }

    public Timestamp getFechaMod()
    {
        return fechaMod;
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

    public String getRoles()
    {
        return roles;
    }

    public Usuario getUsuario()
    {
        return usuario;
    }

    public void setFechaAlta(Timestamp fechaAlta)
    {
        this.fechaAlta = fechaAlta;
    }

    public void setFechaMod(Timestamp fechaMod)
    {
        this.fechaMod = fechaMod;
    }

    public void setPortal(String portal)
    {
        this.portal = portal;
    }

    public void setEscalera(String escalera)
    {
        this.escalera = escalera;
    }

    public void setPlanta(String planta)
    {
        this.planta = planta;
    }

    public void setPuerta(String puerta)
    {
        this.puerta = puerta;
    }

    public void setUsuario(Usuario usuario)
    {
        this.usuario = usuario;
    }

    public void setComunidad(Comunidad comunidad)
    {
        this.comunidad = comunidad;
    }

    public void setRoles(String roles)
    {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsuarioComunidad that = (UsuarioComunidad) o;

        if (portal != null ? !portal.equals(that.portal) : that.portal != null) return false;
        if (escalera != null ? !escalera.equals(that.escalera) : that.escalera != null) return false;
        if (planta != null ? !planta.equals(that.planta) : that.planta != null) return false;
        if (puerta != null ? !puerta.equals(that.puerta) : that.puerta != null) return false;
        if (!roles.equals(that.roles)) return false;
        if (!usuario.equals(that.usuario)) return false;
        if (!comunidad.equals(that.comunidad)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = portal != null ? portal.hashCode() : 0;
        result = 31 * result + (escalera != null ? escalera.hashCode() : 0);
        result = 31 * result + (planta != null ? planta.hashCode() : 0);
        result = 31 * result + (puerta != null ? puerta.hashCode() : 0);
        result = 31 * result + roles.hashCode();
        result = 31 * result + usuario.hashCode();
        result = 31 * result + comunidad.hashCode();
        return result;
    }
}
