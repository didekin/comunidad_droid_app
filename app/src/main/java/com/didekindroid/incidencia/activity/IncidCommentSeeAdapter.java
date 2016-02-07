package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.didekin.incidservice.domain.IncidComment;
import com.didekindroid.R;

/**
 * User: pedro@didekin
 * Date: 05/02/16
 * Time: 23:58
 */
public class IncidCommentSeeAdapter extends ArrayAdapter<IncidComment> {

    private static final String TAG = IncidCommentSeeAdapter.class.getCanonicalName();

    public IncidCommentSeeAdapter(Context activity)
    {
        super(activity, R.layout.incid_comments_see_list_item, R.id.nombreComunidad_view);
    }
}
