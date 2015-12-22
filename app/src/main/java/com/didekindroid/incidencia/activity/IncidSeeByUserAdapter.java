package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.didekin.incidservice.domain.IncidUserComu;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;

/**
 * User: pedro@didekin
 * Date: 18/12/15
 * Time: 13:21
 */
public class IncidSeeByUserAdapter extends ArrayAdapter<IncidUserComu> {

    public IncidSeeByUserAdapter(Context context)
    {
        super(context, R.layout.comu_usercomu_list_item, R.id.nombreComunidad_view);
    }


}
