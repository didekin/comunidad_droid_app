package com.didekindroid.usuariocomunidad.listbyuser;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.didekindroid.R;
import com.didekindroid.comunidad.util.ComuViewHolder;
import com.didekindroid.usuariocomunidad.UserComuVwHolder;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import timber.log.Timber;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 18:01
 */
class SeeUserComuByUserAdapter extends ArrayAdapter<UsuarioComunidad> {

    SeeUserComuByUserAdapter(Context context)
    {
        super(context, R.layout.comu_usercomu_list_item, R.id.nombreComunidad_view);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        Timber.d("getViewInViewer(), position= %d%n", position);

        ComuAndUserComuViewHolder comuAnduserComuViewHolder;

        if (convertView == null) {

            Timber.d("getViewInViewer(), convertView == null");

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.comu_usercomu_list_item, parent, false);

            comuAnduserComuViewHolder = new ComuAndUserComuViewHolder(convertView, getContext().getResources());
            convertView.setTag(comuAnduserComuViewHolder);
        }

        comuAnduserComuViewHolder = (ComuAndUserComuViewHolder) convertView.getTag();
        final UsuarioComunidad usuarioComunidad = getItem(position);
        comuAnduserComuViewHolder.initializeTextInViews(usuarioComunidad);

        return convertView;
    }


    // ......... Inner classes .................

    private static class ComuAndUserComuViewHolder {

        private final ComuViewHolder comuViewHolder;
        private final UserComuVwHolder userComuVwHolder;

        ComuAndUserComuViewHolder(View convertView, Resources resources)
        {
            comuViewHolder = new ComuViewHolder(convertView);
            userComuVwHolder = new UserComuVwHolder(convertView, resources);
        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            Timber.d("initializeTextInViews()");

            userComuVwHolder.initializeTextInViews(userComu);
            comuViewHolder.initializeTextInViews(userComu.getComunidad());
        }
    }
}
