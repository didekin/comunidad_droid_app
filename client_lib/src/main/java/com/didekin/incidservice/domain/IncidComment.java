package com.didekin.incidservice.domain;

import com.didekin.common.BeanBuilder;
import com.didekin.serviceone.domain.UsuarioComunidad;

import java.sql.Timestamp;
import java.util.List;

import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 03/02/16
 * Time: 10:40
 */
@SuppressWarnings("unused")
public class IncidComment { // TODO: serializable

    private final long commentId;
    private final String descripcion;
    private final Incidencia incidencia;
    private final UsuarioComunidad redactor;
    private final Timestamp fechaAlta;
    private final Timestamp fechaModificacion;
    private final List<IncidCommentAnswer> answers;

    public IncidComment(IncidCommentBuilder builder)
    {
        commentId = builder.commentId;
        descripcion = builder.descripcion;
        incidencia = builder.incidencia;
        redactor = builder.redactor;
        fechaAlta = builder.fechaAlta;
        fechaModificacion = builder.fechaModificacion;
        answers = builder.answers;
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

    public UsuarioComunidad getRedactor()
    {
        return redactor;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }

    public Timestamp getFechaModificacion()
    {
        return fechaModificacion;
    }

    public List<IncidCommentAnswer> getAnswers()
    {
        return answers;
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
                        && redactor.equals(comment.redactor)
                );
    }

    @Override
    public int hashCode()
    {
        int result;

        if (commentId > 0){
           result = (int) (commentId ^ (commentId >>> 32));
        } else {
            result = descripcion.hashCode();
            result = 31 * result + incidencia.hashCode();
            result = 31 * result + redactor.hashCode();
        }
        return result;
    }

    // ==================== BUILDER ====================

    public final static class IncidCommentBuilder implements BeanBuilder<IncidComment> {

        private long commentId;
        private String descripcion;
        private Incidencia incidencia;
        private UsuarioComunidad redactor;
        private Timestamp fechaAlta;
        private Timestamp fechaModificacion;
        private List<IncidCommentAnswer> answers;

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

        public IncidCommentBuilder redactor(UsuarioComunidad initValue)
        {
            redactor = initValue;
            return this;
        }

        public IncidCommentBuilder fechaAlta(Timestamp initValue)
        {
            fechaAlta = initValue;
            return this;
        }

        public IncidCommentBuilder fechaModificacion(Timestamp initValue)
        {
            fechaModificacion = initValue;
            return this;
        }

        public IncidCommentBuilder answers(List<IncidCommentAnswer> initValue){
            answers = initValue;
            return this;
        }

        @Override
        public IncidComment build()
        {
            IncidComment comment = new IncidComment(this);
            if (comment.commentId <= 0) {
                if (comment.descripcion == null || comment.incidencia == null || comment.redactor == null) {
                    throw new IllegalStateException(INCIDENCIA_COMMENT_WRONG_INIT.toString());
                }
            }
            return comment;
        }
    }
}
