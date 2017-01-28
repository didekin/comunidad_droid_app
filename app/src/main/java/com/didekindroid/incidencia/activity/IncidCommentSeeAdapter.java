package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidComment;

import timber.log.Timber;

import static com.didekindroid.util.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 05/02/16
 * Time: 23:58
 */
class IncidCommentSeeAdapter extends ArrayAdapter<IncidComment> {

    IncidCommentSeeAdapter(Context activity)
    {
        super(activity, R.layout.incid_comments_see_list_item, R.id.incid_comment_fecha_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Timber.d("getView()");

        IncidCommentViewHolder viewHolder;

        if (convertView == null) {
            Timber.d("getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_comments_see_list_item, parent, false);
            viewHolder = new IncidCommentViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (IncidCommentViewHolder) convertView.getTag();
        final IncidComment comment = getItem(position);
        viewHolder.initializeTextInViews(comment);
        return convertView;
    }

    private class IncidCommentViewHolder {

        final TextView mFechaAltaView;
        final TextView mRedactorView;
        final TextView mDescripcionView;

        IncidCommentViewHolder(View convertView)
        {
            mFechaAltaView = (TextView) convertView.findViewById(R.id.incid_comment_fecha_view);
            mRedactorView = (TextView) convertView.findViewById(R.id.incid_comment_redactor_view);
            mDescripcionView = (TextView) convertView.findViewById(R.id.incid_comment_descripcion_view);
        }

        void initializeTextInViews(IncidComment comment)
        {
            mFechaAltaView.setText(formatTimeStampToString(comment.getFechaAlta()));
            mRedactorView.setText(comment.getRedactor().getAlias());
            mDescripcionView.setText(comment.getDescripcion());
        }
    }
}
