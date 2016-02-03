package com.didekin.incidservice.domain;

import com.didekin.common.BeanBuilder;
import com.didekin.serviceone.domain.UsuarioComunidad;

import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_COMMENT_ANSWER_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 03/02/16
 * Time: 10:52
 */
@SuppressWarnings("unused")
public class IncidCommentAnswer {   // TODO: serializable

    private final long answerId;
    private final IncidComment comment;
    private final String descripcion;
    private final UsuarioComunidad redactor;
    private final Timestamp fechaAlta;

    public IncidCommentAnswer(IncidCommentAnswerBuilder builder)
    {
        answerId = builder.commentAnswerId;
        comment = builder.comment;
        descripcion = builder.descripcion;
        redactor = builder.redactor;
        fechaAlta = builder.fechaAlta;
    }

    public long getAnswerId()
    {
        return answerId;
    }

    public IncidComment getComment()
    {
        return comment;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public UsuarioComunidad getRedactor()
    {
        return redactor;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }

    // ==================== BUILDER ====================

    public final static class IncidCommentAnswerBuilder implements BeanBuilder<IncidCommentAnswer> {

        private long commentAnswerId;
        private IncidComment comment;
        private String descripcion;
        private UsuarioComunidad redactor;
        private Timestamp fechaAlta;

        public IncidCommentAnswerBuilder()
        {
        }

        public IncidCommentAnswerBuilder answerId(long initValue)
        {
            commentAnswerId = initValue;
            return this;
        }

        public IncidCommentAnswerBuilder comment(IncidComment initValue)
        {
            comment = initValue;
            return this;
        }

        public IncidCommentAnswerBuilder descripcion(String initValue)
        {
            descripcion = initValue;
            return this;
        }

        public IncidCommentAnswerBuilder redactor(UsuarioComunidad initValue)
        {
            redactor = initValue;
            return this;
        }

        public IncidCommentAnswerBuilder fechaAlta(Timestamp initValue)
        {
            fechaAlta = initValue;
            return this;
        }

        @Override
        public IncidCommentAnswer build()
        {
            IncidCommentAnswer answer = new IncidCommentAnswer(this);
            if (answer.answerId <= 0) {
                if (answer.descripcion == null || answer.comment == null || answer.redactor == null) {
                    throw new IllegalStateException(INCIDENCIA_COMMENT_ANSWER_WRONG_INIT.toString());
                }
            }
            return answer;
        }
    }
}
