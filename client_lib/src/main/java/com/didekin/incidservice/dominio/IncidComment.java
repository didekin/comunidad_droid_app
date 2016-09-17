package com.didekin.incidservice.dominio;

import com.didekin.common.dominio.BeanBuilder;
import com.didekin.usuario.dominio.Usuario;

import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 03/02/16
 * Time: 10:40
 */
@SuppressWarnings({"PrivateMemberAccessBetweenOuterAndInnerClass"})
public final class IncidComment {

    private final long commentId;
    private final String descripcion;
    private final Incidencia incidencia;
    private final Usuario redactor;
    private final Timestamp fechaAlta;

    private IncidComment(IncidCommentBuilder builder)
    {
        commentId = builder.commentId;
        descripcion = builder.descripcion;
        incidencia = builder.incidencia;
        redactor = builder.redactor;
        fechaAlta = builder.fechaAlta;
    }

    public long getCommentId()
    {
        return commentId;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public Incidencia getIncidencia()
    {
        return incidencia;
    }

    public Usuario getRedactor()
    {
        return redactor;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta != null ? new Timestamp(fechaAlta.getTime()) : null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof IncidComment)) return false;

        IncidComment comment = (IncidComment) o;

        return this.commentId == comment.commentId ||
                (descripcion.equals(comment.descripcion)
                        && incidencia.equals(comment.incidencia)
                );
    }

    @Override
    public int hashCode()
    {
        int result;

        if (commentId > 0) {
            result = (int) (commentId ^ (commentId >>> 32));
        } else {
            result = descripcion.hashCode();
            result = 31 * result + incidencia.hashCode();
        }
        return result;
    }

    // ==================== BUILDER ====================

    @SuppressWarnings("PrivateMemberAccessBetweenOuterAndInnerClass")
    public final static class IncidCommentBuilder implements BeanBuilder<IncidComment> {

        private long commentId;
        private String descripcion;
        private Incidencia incidencia;
        private Usuario redactor;
        private Timestamp fechaAlta;

        public IncidCommentBuilder()
        {
        }

        public IncidCommentBuilder commentId(long initValue)
        {
            commentId = initValue;
            return this;
        }

        public IncidCommentBuilder descripcion(String initValue)
        {
            descripcion = initValue;
            return this;
        }

        public IncidCommentBuilder incidencia(Incidencia initValue)
        {
            incidencia = initValue;
            return this;
        }

        public IncidCommentBuilder redactor(Usuario initValue)
        {
            redactor = initValue;
            return this;
        }

        public IncidCommentBuilder fechaAlta(Timestamp initValue)
        {
            fechaAlta = initValue;
            return this;
        }

        public IncidCommentBuilder copyComment(IncidComment initValue)
        {
            commentId = initValue.commentId;
            descripcion = initValue.descripcion;
            incidencia = initValue.incidencia;
            redactor = initValue.redactor;
            fechaAlta = initValue.fechaAlta;
            return this;
        }

        @Override
        public IncidComment build()
        {
            IncidComment comment = new IncidComment(this);
            if (comment.commentId <= 0) {
                if (comment.descripcion == null || comment.incidencia == null) {
                    throw new IllegalStateException(INCIDENCIA_COMMENT_WRONG_INIT.toString());
                }
            }
            return comment;
        }
    }
}
