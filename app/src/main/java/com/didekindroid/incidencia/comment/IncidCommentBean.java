package com.didekindroid.incidencia.comment;

import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekinlib.model.incidencia.dominio.IncidDataPatterns.INCID_COMMENT_DESC;

/**
 * User: pedro@didekin
 * Date: 03/02/16
 * Time: 18:47
 */
class IncidCommentBean {

    private final Incidencia incidencia;
    private String commentDesc;

    IncidCommentBean(Incidencia incidencia)
    {
        this.incidencia = incidencia;
    }

    private void setCommentDesc(String commentDesc)
    {
        this.commentDesc = commentDesc;
    }

    private boolean validateDescripcion(StringBuilder errorMsg, Resources resources)
    {
        if (!INCID_COMMENT_DESC.isPatternOk(commentDesc)) {
            errorMsg.append(resources.getString(R.string.incid_comment_label)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    IncidComment makeComment(View mAcView, StringBuilder errorMsg, Resources resources)
    {
        setCommentDesc(((EditText) mAcView.findViewById(R.id.incid_comment_ed)).getText().toString());

        if (validateDescripcion(errorMsg, resources)) {
            return new IncidComment.IncidCommentBuilder()
                    .incidencia(new Incidencia.IncidenciaBuilder()
                            .incidenciaId(incidencia.getIncidenciaId())
                            .comunidad(new Comunidad.ComunidadBuilder()
                                    .c_id(incidencia.getComunidad().getC_Id())
                                    .build())
                            .build())
                    .descripcion(commentDesc)
                    .build();
        } else {
            return null;
        }
    }
}
