package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekin.incidservice.dominio.IncidComment;
import com.didekindroid.R;

import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 05/02/16
 * Time: 23:58
 */
public class IncidCommentSeeAdapter extends ArrayAdapter<IncidComment> {

    private static final String TAG = IncidCommentSeeAdapter.class.getCanonicalName();

    public IncidCommentSeeAdapter(Context activity)
    {
        super(activity, R.layout.incid_comments_see_list_item, R.id.incid_comment_fecha_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView()");
        IncidCommentViewHolder viewHolder;

        if (convertView == null){
            Log.d(TAG, "getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_comments_see_list_item, parent,false);
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

        public IncidCommentViewHolder(View convertView)
        {
            mFechaAltaView = (TextView) convertView.findViewById(R.id.incid_comment_fecha_view);
            mRedactorView = (TextView) convertView.findViewById(R.id.incid_comment_redactor_view);
            mDescripcionView = (TextView) convertView.findViewById(R.id.incid_comment_descripcion_view);
        }

        void initializeTextInViews(IncidComment comment)
        {
            Log.d(TAG, "initializeTextInViews()");
            mFechaAltaView.setText(formatTimeStampToString(comment.getFechaAlta()));
            mRedactorView.setText(comment.getRedactor().getAlias());
            mDescripcionView.setText(comment.getDescripcion());
        }
    }
}
