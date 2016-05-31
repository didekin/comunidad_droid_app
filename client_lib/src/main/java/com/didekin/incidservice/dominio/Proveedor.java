package com.didekin.incidservice.dominio;

import com.didekin.common.dominio.BeanBuilder;
import com.didekin.usuario.dominio.Address;
import com.didekin.usuario.dominio.Usuario;

import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 17:16
 */

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Proveedor {

    private final long proveedorId;
    private final String nif;
    private final Usuario usuario;
    private final Address address;
    private final TipoServicio[] tiposServicio;
    private final Timestamp fechaAlta;

    private Proveedor(ProveedorBuilder builder)
    {
        proveedorId = builder.proveedorId;
        nif = builder.nif;
        usuario = builder.usuario;
        address = builder.address;
        tiposServicio = builder.tiposServicio;
        fechaAlta = builder.fechaAlta;
    }

    public static class ProveedorBuilder implements BeanBuilder<Proveedor> {

        private long proveedorId;
        private String nif;
        private Usuario usuario;
        private Address address;
        private TipoServicio[] tiposServicio;
        private Timestamp fechaAlta;

        public ProveedorBuilder()
        {
        }

        public ProveedorBuilder proveedorId(long initValue)
        {
            proveedorId = initValue;
            return this;
        }

        public ProveedorBuilder nif(String initValue)
        {
            nif = initValue;
            return this;
        }

        public ProveedorBuilder usuario(Usuario initValue)
        {
            usuario = initValue;
            return this;
        }

        public ProveedorBuilder address(Address initValue)
        {
            address = initValue;
            return this;
        }

        public ProveedorBuilder tiposServicio(TipoServicio[] initValue)
        {
            tiposServicio = initValue;
            return this;
        }

        public ProveedorBuilder fechaAlta(Timestamp initValue){
            fechaAlta = initValue;
            return this;
        }

        @Override
        public Proveedor build()
        {
            return new Proveedor(this);
        }
    }
}
