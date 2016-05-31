package com.didekin.usuario.dominio;

import com.didekin.common.dominio.BeanBuilder;
import com.didekin.common.dominio.SerialNumber;
import com.didekin.common.exception.DidekinExceptionMsg;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_COMPARABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_EQUAL_ABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_HASHABLE;

/**
 * User: pedro
 * Date: 29/03/15
 * Time: 12:02
 */
@SuppressWarnings("unused")
public final class Usuario implements Comparable<Usuario>, Serializable {

    private final long uId;
    private final String userName;  //email of the user.
    private final String alias;
    private final String password;
    private final String gcmToken;

    private Usuario(UsuarioBuilder builder)
    {
        uId = builder.uId;
        userName = builder.userName;
        alias = builder.alias;
        password = builder.password;
        gcmToken = builder.gcmToken;
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

    public String getGcmToken()
    {
        return gcmToken;
    }

    // ............................ Serializable ...............................

    /**
     * Return an InnerSerial object that will replace the current Usuario object during serialization.
     * In the deserialization the readResolve() method of the InnerSerial object will be used.
     */
    private Object writeReplace()
    {
        return new InnerSerial(this);
    }

    private void readObject(ObjectInputStream inputStream) throws InvalidObjectException
    {
        throw new InvalidObjectException("Use innerSerial to serialize");
    }

    // ............................ Equals and hashCode ..........................

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

        if (usuario.userName != null && userName != null) {
            if (uId > 0 && usuario.getuId() > 0) {
                return uId == usuario.uId && userName.equals(usuario.userName);
            }
            return userName.equals(usuario.userName);
        } else {
            if (uId > 0 && usuario.getuId() > 0) {
                return uId == usuario.uId;
            }
            throw new UnsupportedOperationException(USER_NOT_EQUAL_ABLE.toString());
        }
    }

    @Override
    public int hashCode()
    {
        int hash;

        if (userName == null && uId <= 0L) {
            throw new UnsupportedOperationException(USER_NOT_HASHABLE.toString());
        } else {
            if (uId > 0 && userName != null) {
                hash = ((int) (uId ^ (uId >>> 32))) * 31 + userName.hashCode();
            } else if (uId > 0 && userName == null) {
                hash = ((int) (uId ^ (uId >>> 32))) * 31;
            } else {
                //noinspection ConstantConditions
                hash = userName.hashCode();
            }
        }
        return hash;
    }

    @Override
    public int compareTo(Usuario o)
    {
        if (userName == null || o == null || o.getUserName() == null) {
            throw new UnsupportedOperationException(USER_NOT_COMPARABLE.toString());
        }

        return userName.compareToIgnoreCase(o.getUserName());
    }

    //    ========================== BUILDER ===============================

    public static class UsuarioBuilder implements BeanBuilder<Usuario> {

        //Parameters; all optional.
        private long uId = 0L;
        private String userName = null;  //email of the user.
        private String alias = null;
        private String password = null;
        private String gcmToken;

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

        public UsuarioBuilder gcmToken(String gcmToken)
        {
            this.gcmToken = gcmToken;
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

        public UsuarioBuilder copyUsuario(Usuario usuario)
        {
            uId = usuario.uId;
            password = usuario.password;
            userName = usuario.userName;
            alias = usuario.alias;
            gcmToken = usuario.gcmToken;
            return this;
        }
    }

//    ============================= SERIALIZATION PROXY ==================================

    private static class InnerSerial implements Serializable {

        private static final long serialVersionUID = SerialNumber.USUARIO.number;

        private final long usuarioId;
        private final String userName;
        private final String userAlias;
        private final String password;
        private final String gcmToken;

        public InnerSerial(Usuario usuario)
        {
            usuarioId = usuario.uId;
            userName = usuario.userName;
            userAlias = usuario.alias;
            password = usuario.password;
            gcmToken = usuario.gcmToken;
        }

        private Object readResolve()
        {
            return new UsuarioBuilder()
                    .uId(usuarioId)
                    .userName(userName)
                    .alias(userAlias)
                    .password(password)
                    .gcmToken(gcmToken)
                    .build();
        }
    }
}
