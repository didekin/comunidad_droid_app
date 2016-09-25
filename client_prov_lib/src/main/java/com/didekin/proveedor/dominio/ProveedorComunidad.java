package com.didekin.proveedor.dominio;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Usuario;

import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 20:34
 */
public final class ProveedorComunidad {

    private final Proveedor proveedor;
    private final Comunidad comunidad;
    private final Usuario usuarioRelacion;
    private final Timestamp fechaRelacion;

    public ProveedorComunidad(Proveedor proveedor, Comunidad comunidad, Usuario usuarioRelacion, Timestamp fechaRelacion)
    {
        this.proveedor = proveedor;
        this.comunidad = comunidad;
        this.usuarioRelacion = usuarioRelacion;
        this.fechaRelacion = fechaRelacion;
    }

    public Proveedor getProveedor()
    {
        return proveedor;
    }

    public Comunidad getComunidad()
    {
        return comunidad;
    }

    public Usuario getUsuarioRelacion()
    {
        return usuarioRelacion;
    }

    public Timestamp getFechaRelacion()
    {
        return fechaRelacion;
    }
}
