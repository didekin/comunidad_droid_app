package com.didekindroid.incidencia.utils;

import com.didekindroid.incidencia.comment.IncidCommentSeeListFr;
import com.didekindroid.incidencia.core.IncidEditAc;
import com.didekindroid.incidencia.list.close.IncidSeeCloseByComuFr;
import com.didekindroid.incidencia.resolucion.IncidResolucionRegEditSeeAc;
import com.didekindroid.incidencia.resolucion.IncidResolucionSeeFr;

/**
 * User: pedro@didekin
 * Date: 16/03/16
 * Time: 19:40
 */
public final class IncidFragmentTags {

    public static final String incid_comments_see_list_fr_tag = IncidCommentSeeListFr.class.getCanonicalName();
    public static final String incid_edit_ac_frgs_tag = IncidEditAc.class.getCanonicalName();
    public static final String incid_resolucion_ac_frgs_tag = IncidResolucionRegEditSeeAc.class.getCanonicalName();
    public static final String incid_resolucion_see_fr_tag = IncidResolucionSeeFr.class.getCanonicalName();
    public static final String incid_see_by_comu_list_fr_tag = IncidSeeCloseByComuFr.class.getCanonicalName();

    private IncidFragmentTags()
    {
    }


}
