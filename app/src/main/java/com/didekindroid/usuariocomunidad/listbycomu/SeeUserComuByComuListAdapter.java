package com.didekindroid.usuariocomunidad.listbycomu;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.usuariocomunidad.UserComuVwHolder;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 17:50
 */
class SeeUserComuByComuListAdapter extends ArrayAdapter<UsuarioComunidad> {

    SeeUserComuByComuListAdapter(Context context)
    {
        super(context, R.layout.user_usercomu_list_item, R.id.user_item_alias_txt);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Timber.d("getViewInViewer(), position= %d%n", position);

        UserAndUserComuVwHolder viewHolder;

        if (convertView == null) {
            Timber.d("getViewInViewer(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_usercomu_list_item, parent, false);
            viewHolder = new UserAndUserComuVwHolder(convertView, getContext().getResources());
            convertView.setTag(viewHolder);
        }

        viewHolder = (UserAndUserComuVwHolder) convertView.getTag();
        final UsuarioComunidad userComu = getItem(position);
        viewHolder.initializeTextInViews(userComu);

        return convertView;
    }

    // ......... Inner classes .................

    private static class UserVwHolder {

        final TextView mUserName;
        final TextView mUserAlias;

        UserVwHolder(View convertView)
        {
            mUserName = convertView.findViewById(R.id.user_item_username_txt);
            mUserAlias = convertView.findViewById(R.id.user_item_alias_txt);
        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            Timber.d("initializeTextInViews()");

            mUserName.setText(userComu.getUsuario().getUserName());
            mUserAlias.setText(userComu.getUsuario().getAlias());
        }
    }

    private static class UserAndUserComuVwHolder {

        final UserVwHolder userVwHolder;
        final UserComuVwHolder userComuVwHolder;

        UserAndUserComuVwHolder(View convertView, Resources resources)
        {
            userVwHolder = new UserVwHolder(convertView);
            userComuVwHolder = new UserComuVwHolder(convertView, resources);
        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            Timber.d("initializeTextInViews()");

            userVwHolder.initializeTextInViews(userComu);
            userComuVwHolder.initializeTextInViews(userComu);
        }
    }
}
