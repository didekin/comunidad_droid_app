package com.didekinaar.usuariocomunidad;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.comunidad.ComuSearchResultsListAdapter;

import timber.log.Timber;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 18:01
 */
public class SeeUserComuByUserAdapter extends ArrayAdapter<UsuarioComunidad> {

    SeeUserComuByUserAdapter(Context context)
    {
        super(context, R.layout.comu_usercomu_list_item, R.id.nombreComunidad_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Timber.d("getView(), position= %d%n", position);

        ComuAndUserComuViewHolder comuAnduserComuViewHolder;

        if (convertView == null) {

            Timber.d("getView(), convertView == null");

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

        private final ComuSearchResultsListAdapter.ComuViewHolder comuViewHolder;
        private final SeeUserComuByComuListAdapter.UserComuVwHolder userComuVwHolder;

        ComuAndUserComuViewHolder(View convertView, Resources resources)
        {
            comuViewHolder = new ComuSearchResultsListAdapter.ComuViewHolder(convertView);
            userComuVwHolder = new SeeUserComuByComuListAdapter.UserComuVwHolder(convertView, resources);
        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            Timber.d("initializeTextInViews()");

            comuViewHolder.initializeTextInViews(userComu.getComunidad());
            userComuVwHolder.initializeTextInViews(userComu);
        }
    }
}
