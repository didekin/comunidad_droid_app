package com.didekindroid.incidencia.dominio;

import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;

import com.didekin.incidencia.dominio.IncidComment;
import com.didekin.incidencia.dominio.Incidencia;
import com.didekin.comunidad.Comunidad;
import com.didekindroid.R;


import static com.didekin.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekin.incidencia.dominio.IncidDataPatterns.INCID_COMMENT_DESC;

/**
 * User: pedro@didekin
 * Date: 03/02/16
 * Time: 18:47
 */
public class IncidCommentBean {

    private String commentDesc;
    private final Incidencia incidencia;

    public IncidCommentBean(Incidencia incidencia)
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

    public IncidComment makeComment(View mAcView, StringBuilder errorMsg, Resources resources)
    {
        setCommentDesc(((EditText) mAcView.findViewById(R.id.incid_comment_ed)).getText().toString());

        if (validateDescripcion(errorMsg, resources)){
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
