package com.didekindroid.usuario.activity;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.ComuSearchResultsListAdapter.ComuViewHolder;
import com.didekindroid.usuario.activity.SeeUserComutByComuListAdapter.UserComuVwHolder;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 18:01
 */
public class SeeUserComuByUserAdapter extends ArrayAdapter<UsuarioComunidad> {

    private static final String TAG = SeeUserComuByUserAdapter.class.getCanonicalName();

    public SeeUserComuByUserAdapter(Context context)
    {
        super(context, R.layout.comu_usercomu_list_item, R.id.nombreComunidad_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView(), position= " + position);

        ComuAndUserComuViewHolder comuAnduserComuViewHolder;

        if (convertView == null) {

            Log.d(TAG, "getView(), convertView == null");

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

        public ComuAndUserComuViewHolder(View convertView, Resources resources)
        {
            comuViewHolder = new ComuViewHolder(convertView);
            userComuVwHolder = new UserComuVwHolder(convertView, resources);
        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            Log.d(TAG, "initializeTextInViews()");

            comuViewHolder.initializeTextInViews(userComu.getComunidad());
            userComuVwHolder.initializeTextInViews(userComu);
        }
    }
}
