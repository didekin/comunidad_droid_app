package com.didekindroid.usuario.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.ComuSearchResultsListAdapter.ComuViewHolder;
import com.didekindroid.usuario.activity.SeeUserComutByComuListAdapter.UserComuVwHolder;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 18:01
 */
public class ComuAndUserComuListByUserAdapter extends ArrayAdapter<UsuarioComunidad> {

    private static final String TAG = ComuAndUserComuListByUserAdapter.class.getCanonicalName();

    public ComuAndUserComuListByUserAdapter(Context context)
    {
        super(context, R.layout.comu_and_usercomu_list_item_view, R.id.nombreComunidad_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView(), position= " + position);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        UsuarioComunidadViewHolder usuarioComuViewHolder;

        if (convertView == null) {

            Log.d(TAG, "getView(), convertView == null");

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.comu_and_usercomu_list_item_view, parent, false);

            usuarioComuViewHolder = new UsuarioComunidadViewHolder();
            usuarioComuViewHolder.comuViewHolder = new ComuViewHolder(convertView);
            usuarioComuViewHolder.userComuVwHolder = new UserComuVwHolder(convertView, getContext().getResources());

            convertView.setTag(usuarioComuViewHolder);
        }

        usuarioComuViewHolder = (UsuarioComunidadViewHolder) convertView.getTag();

        final UsuarioComunidad usuarioComunidad = getItem(position);
        final Comunidad comunidad = usuarioComunidad.getComunidad();
        ComuViewHolder comuViewHolder = usuarioComuViewHolder.comuViewHolder;
        UserComuVwHolder userComuVwHolder = usuarioComuViewHolder.userComuVwHolder;

        comuViewHolder.initializeTextInViews(comunidad);
        userComuVwHolder.initializeTextInViews(usuarioComunidad);

        return convertView;
    }


    // ......... Inner classes .................

    private static class UsuarioComunidadViewHolder {

        private ComuViewHolder comuViewHolder;
        private UserComuVwHolder userComuVwHolder;
    }
}
