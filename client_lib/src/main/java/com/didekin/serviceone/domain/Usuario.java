package com.didekin.serviceone.domain;

import com.didekin.common.BeanBuilder;
import com.didekin.common.exception.DidekinExceptionMsg;

import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_COMPARABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_HASHABLE;

/**
 * User: pedro
 * Date: 29/03/15
 * Time: 12:02
 */
public final class Usuario implements Comparable<Usuario> {

    private final long uId;
    private final String userName;  //email of the user.
    private final String alias;
    private final String password;

    private Usuario(UsuarioBuilder builder)
    {
        uId = builder.uId;
        userName = builder.userName;
        alias = builder.alias;
        password = builder.password;
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

    public long getuId()
    {
        return uId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Usuario usuario = (Usuario) o;

        if (uId > 0 && usuario.getuId() > 0) {
            return uId == usuario.getuId();
        }

        return userName.equals(usuario.userName);

    }

    @Override
    public int hashCode()
    {
        if (userName == null) {
            throw new UnsupportedOperationException(USER_NOT_HASHABLE.toString());
        }
        return userName.hashCode();
    }

    @Override
    public int compareTo(Usuario o)
    {
        if (userName == null || o == null || o.getUserName() == null) {
            throw new UnsupportedOperationException(USER_NOT_COMPARABLE.toString());
        }

        return userName.compareToIgnoreCase(o.getUserName());
    }

    public static class UsuarioBuilder implements BeanBuilder<Usuario> {

        //Parameters; all optional.
        private long uId = 0L;
        private String userName = null;  //email of the user.
        private String alias = null;
        private String password = null;

        public UsuarioBuilder()
        {
        }

        public UsuarioBuilder uId(long uId)
        {
            this.uId = uId;
            return this;
        }

        public UsuarioBuilder userName(String userName)
        {
            this.userName = userName;
            return this;
        }

        public UsuarioBuilder alias(String alias)
        {
            this.alias = alias;
            return this;
        }

        public UsuarioBuilder password(String password)
        {
            this.password = password;
            return this;
        }

        @Override
        public Usuario build()
        {
            Usuario usuario = new Usuario(this);

            if (usuario.uId == 0 && usuario.userName == null) {
                throw new IllegalStateException(DidekinExceptionMsg.USER_WRONG_INIT.toString());
            }
            return usuario;
        }

        @SuppressWarnings("unused")
        public UsuarioBuilder copyUsuario(Usuario usuario)
        {
            uId = usuario.getuId();
            password = usuario.getPassword();
            userName = usuario.getUserName();
            alias = usuario.getAlias();
            return this;
        }
    }
}
