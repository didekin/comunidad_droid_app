package com.didekindroid.comunidad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.didekindroid.R;
import com.didekindroid.comunidad.util.ComuViewHolder;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 12/05/15
 * Time: 17:22
 */
class ComuSearchResultsListAdapter extends ArrayAdapter<Comunidad> {

    ComuSearchResultsListAdapter(Context context)
    {
        super(context, R.layout.comu_include, R.id.nombreComunidad_view);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        Timber.d("getView(), position= %d%n", position);

        ComuViewHolder viewHolder;

        if (convertView == null) {
            Timber.d("getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comu_include, parent, false);
            viewHolder = new ComuViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ComuViewHolder) convertView.getTag();
        final Comunidad comunidad = getItem(position);
        viewHolder.initializeTextInViews(comunidad);

        return convertView;
    }
}
