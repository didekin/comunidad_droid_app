package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekin.incidservice.dominio.ImportanciaUser;
import com.didekindroid.R;

/**
 * User: pedro@didekin
 * Date: 27/03/16
 * Time: 13:54
 */
public class IncidImportanciaSeeAdapter extends ArrayAdapter<ImportanciaUser> {

    private static final String TAG = IncidImportanciaSeeAdapter.class.getCanonicalName();

    public IncidImportanciaSeeAdapter(Context activity)
    {
        super(activity, R.layout.incid_importancia_see_list_item, R.id.incid_importancia_alias_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView()");
        ImportanciaUserHolder viewHolder;

        if (convertView == null) {
            Log.d(TAG, "getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_importancia_see_list_item, parent, false);
            viewHolder = new ImportanciaUserHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ImportanciaUserHolder) convertView.getTag();
        final ImportanciaUser importanciaUser = getItem(position);
        viewHolder.initializeTextInViews(importanciaUser);
        return convertView;
    }

    private class ImportanciaUserHolder {

        final TextView aliasView;
        final TextView ratingView;

        public ImportanciaUserHolder(View convertView)
        {
            aliasView = (TextView) convertView.findViewById(R.id.incid_importancia_alias_view);
            ratingView = (TextView) convertView.findViewById(R.id.incid_importancia_rating_view);
        }

        public void initializeTextInViews(ImportanciaUser importanciaUser)
        {
            aliasView.setText(importanciaUser.getUserAlias());
            Resources resources = getContext().getResources();
            String importancia = importanciaUser.getImportancia() == 0 ? resources.getString(R.string.no_sabe) : resources.getStringArray(R.array.IncidImportanciaArray)[importanciaUser.getImportancia()];
            ratingView.setText(importancia);
        }
    }
}