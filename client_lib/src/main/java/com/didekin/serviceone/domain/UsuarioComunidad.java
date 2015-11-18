package com.didekin.serviceone.domain;


import com.didekin.common.BeanBuilder;

import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.USERCOMU_WRONG_INIT;

/**
 * User: pedro
 * Date: 29/03/15
 * Time: 12:02
 */
public final class UsuarioComunidad implements Comparable<UsuarioComunidad> {

    private final String portal;
    private final String escalera;
    private final String planta;
    private final String puerta;
    private final String roles;

    private final Timestamp fechaAlta;
    private final Timestamp fechaMod;

    private final Usuario usuario;
    private final Comunidad comunidad;

    private UsuarioComunidad(UserComuBuilder userComuBuilder)
    {
        portal = userComuBuilder.portal;
        escalera = userComuBuilder.escalera;
        planta = userComuBuilder.planta;
        puerta = userComuBuilder.puerta;
        roles = userComuBuilder.roles;
        fechaAlta = userComuBuilder.fechaAlta;
        fechaMod = userComuBuilder.fechaMod;
        usuario = userComuBuilder.usuario;
        comunidad = userComuBuilder.comunidad;
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
        return fechaAlta != null ? new Timestamp(fechaAlta.getTime()) : null;
    }


    public Timestamp getFechaMod()
    {
        return fechaMod != null ? new Timestamp(fechaMod.getTime()) : null;
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

    @Override
    public boolean equals(Object o)
    {
        if (o != null && this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UsuarioComunidad userComu = (UsuarioComunidad) o;

        return usuario.equals(userComu.usuario)
                && comunidad.equals(userComu.comunidad)
                && !(portal != null ? !portal.equals(userComu.portal) : userComu.portal != null)
                && !(escalera != null ? !escalera.equals(userComu.escalera) : userComu.escalera != null)
                && !(planta != null ? !planta.equals(userComu.planta) : userComu.planta != null)
                && !(puerta != null ? !puerta.equals(userComu.puerta) : userComu.puerta != null);
    }

    @Override
    public int hashCode()
    {
        int result = portal != null ? portal.hashCode() : 0;
        result = 31 * result + (escalera != null ? escalera.hashCode() : 0);
        result = 31 * result + (planta != null ? planta.hashCode() : 0);
        result = 31 * result + (puerta != null ? puerta.hashCode() : 0);
        result = 31 * result + usuario.hashCode();
        result = 31 * result + comunidad.hashCode();
        return result;
    }

    @Override
    public int compareTo(UsuarioComunidad o)
    {
        int result;

        if ((result = comunidad.compareTo(o.getComunidad())) != 0) {
            return result;
        }

        if ((result = usuario.compareTo(o.getUsuario())) != 0) {
            return result;
        }

        if (portal != null && o.getPortal() != null && (result = portal.compareToIgnoreCase(o.getPortal())) != 0) {
            return result;
        }
        if (escalera != null && o.getEscalera() != null && (result = escalera.compareToIgnoreCase(o.getEscalera())) != 0) {
            return result;
        }
        if (planta != null && o.getPlanta() != null && (result = planta.compareToIgnoreCase(o.getPlanta())) != 0) {
            return result;
        }
        if (puerta != null && o.getPuerta() != null && (result = puerta.compareToIgnoreCase(o.getPuerta())) != 0) {
            return result;
        }
        return 0;
    }

    public static class UserComuBuilder implements BeanBuilder<UsuarioComunidad> {

        private String portal;
        private String escalera;
        private String planta;
        private String puerta;
        private String roles;

        private Timestamp fechaAlta;
        private Timestamp fechaMod;

        // Required parameters.
        private Usuario usuario; // It can be null in beans coming from the android client.
        private Comunidad comunidad;

        public UserComuBuilder(Comunidad comunidad, Usuario usuario)
        {
            this.comunidad = comunidad;
            this.usuario = usuario;
        }

        @SuppressWarnings("unused")
        public UserComuBuilder userComuRest(UsuarioComunidad initValue)
        {
            portal = initValue.getPortal();
            escalera = initValue.getEscalera();
            planta = initValue.getPlanta();
            puerta = initValue.getPuerta();
            roles = initValue.getRoles();
            fechaAlta = initValue.getFechaAlta();
            fechaMod = initValue.getFechaMod();
            return this;
        }

        public UserComuBuilder portal(String initValue)
        {
            portal = initValue;
            return this;
        }

        public UserComuBuilder escalera(String initValue)
        {
            escalera = initValue;
            return this;
        }

        public UserComuBuilder planta(String initValue)
        {
            planta = initValue;
            return this;
        }

        public UserComuBuilder puerta(String initValue)
        {
            puerta = initValue;
            return this;
        }

        public UserComuBuilder roles(String initValue)
        {
            roles = initValue;
            return this;
        }

        @Override
        public UsuarioComunidad build()
        {
            UsuarioComunidad userComu = new UsuarioComunidad(this);
            if (userComu.getComunidad() == null) {
                throw new IllegalStateException(USERCOMU_WRONG_INIT.toString());
            }
            return userComu;
        }
    }
}
