package com.didekindroid.incidencia.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidComment;

import timber.log.Timber;

import static com.didekindroid.lib_one.util.UiUtil.formatTimeStampToString;

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

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        Timber.d("getViewInViewer()");

        IncidCommentViewHolder viewHolder;

        if (convertView == null) {
            Timber.d("getViewInViewer(), convertView == null");
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
            mFechaAltaView = convertView.findViewById(R.id.incid_comment_fecha_view);
            mRedactorView = convertView.findViewById(R.id.incid_comment_redactor_view);
            mDescripcionView = convertView.findViewById(R.id.incid_comment_descripcion_view);
        }

        void initializeTextInViews(IncidComment comment)
        {
            mFechaAltaView.setText(formatTimeStampToString(comment.getFechaAlta()));
            mRedactorView.setText(comment.getRedactor().getAlias());
            mDescripcionView.setText(comment.getDescripcion());
        }
    }
}
