package com.didekindroid.usuario.dominio;

import com.didekindroid.common.dominio.SerialNumbers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: pedro@didekin
 * Date: 09/06/15
 * Time: 18:32
 */
public final class Usuario implements Serializable {

    private static final long serialVersionUID = SerialNumbers.USUARIO.number;

    private long uId;
    private String alias;
    private String password;
    private short prefixTf;
    private int numeroTf;
    private String userName;

    private List<UsuarioComunidad> usuariosComunidad;

    public Usuario()
    {
    }

    public Usuario(String userName)
    {
        this.userName = userName;
    }

    public Usuario(String userName, String alias, String password)
    {
        this.userName = userName;
        this.alias = alias;
        this.password = password;
    }

    public Usuario(String userName, String alias, String password, short prefixTf, int numeroTf)
    {
        this(userName, alias, password);
        this.prefixTf = prefixTf;
        this.numeroTf = numeroTf;
    }

    public Usuario(long idUsuario, String userName, String alias, String password, short prefixTf, int numeroTf)
    {
        this(userName, alias, password, prefixTf, numeroTf);
        this.uId = idUsuario;
    }


    public void addUsuarioComunidad(UsuarioComunidad usuarioCom)
    {
        if (usuariosComunidad == null) {
            usuariosComunidad = new ArrayList<>();
        }
        usuariosComunidad.add(usuarioCom);
    }

    public void setUsuariosComunidad(List<UsuarioComunidad> usuariosComunidad)
    {
        this.usuariosComunidad = usuariosComunidad;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getAlias()
    {
        return alias;
    }

    public String getPassword()
    {
        return password;
    }

    public short getPrefixTf()
    {
        return prefixTf;
    }

    public int getNumeroTf()
    {
        return numeroTf;
    }

    public long getuId()
    {
        return uId;
    }

    public List<UsuarioComunidad> getUsuariosComunidad()
    {
        return usuariosComunidad;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setPrefixTf(short prefixTf)
    {
        this.prefixTf = prefixTf;
    }

    public void setNumeroTf(int numeroTf)
    {
        this.numeroTf = numeroTf;
    }

    public void setuId(long uId)
    {
        this.uId = uId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;

        if (!userName.equals(usuario.userName)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return userName.hashCode();
    }
}
