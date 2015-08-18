package com.didekindroid.oferta;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.didekindroid.R;
import com.didekindroid.common.ConnectionUtils;
import com.didekindroid.common.IoHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: pedro
 * Date: 04/02/15
 * Time: 13:39
 */
public class OfertasSummaryFragment extends ListFragment {

    private static final String TAG = "OfertasSummaryFragment";
    OnOfferSelectedListener mListener;

    public interface OnOfferSelectedListener {
        void onOfferSelected(OfertaBean ofertaBean);
    }

    @Override
    public void onAttach(Activity activity)
    {
        Log.d(TAG, "onAttach()");
        super.onAttach(activity);

        try {
            mListener = (OnOfferSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOfferSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    /*
    If your fragment is a subclass of ListFragment, the default implementation returns a ListView
    from onCreateView(), so you don't need to implement it.
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "Enters onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        // TODO: es necesario filtrar las ofertas por el estado "Aceptadas y en vigor".

        int layout = android.R.layout.simple_list_item_activated_1;
        ListAdapter adapter = new ArrayAdapter<>(
                getActivity(), layout, Ipsum.testOffers);
        setListAdapter(adapter);

        //TODO: habría que utilizar aquí getAdapterData().
        //getAdapterData();
    }

    @Override
    public void onStart()
    {
        Log.d(TAG, "onStart()");
        super.onStart();

        if (getFragmentManager().findFragmentById(R.id.oferta_fragment) != null) {
            Log.d(TAG, "onStart(), large layout.");
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Log.d(TAG, "Enters onListItemClick()");

        getListView().setItemChecked(position, true);

        OfertaBean ofertaBean = Ipsum.testOffers[position];
        /*Cursor cursor = (Cursor) l.getItemAtPosition(position);
        OfertaBean ofertaBean = new OfertaBean(
                cursor.getString(OfferTable.ServOne.indexColumnsAll[1])
                , cursor.getString(OfferTable.ServOne.indexColumnsAll[2])
        );*/
        mListener.onOfferSelected(ofertaBean);
    }

    public void getAdapterData(String url)
    {

        //TODO: hacer el método private e insertar el código que controla el máximo de
        // de ofertas a mostrar.
        /*SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String maxOffers = preferences.getString(getString(R.string.maxOffers_keyPref),"");
        int maOffersInt = Integer.parseInt(maxOffers);*/


        Log.d(TAG, "getAdapterData()");

        /*if (ConnectionUtils.isMobileConnected(getActivity()) == ConnectivityManager.TYPE_MOBILE) {
            new DownloadTask().execute(url);
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_conn_toast),
                    Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls)
        {
            Log.d(TAG, "doInBackground()");

            try {
                InputStream stream = null;
                String result = "";

                try {
                    stream = IoHelper.downloadUrl(urls[0]);
                    result = IoHelper.readIt(stream, 500);
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
                return result;
            } catch (IOException e) {
                return e.getLocalizedMessage();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            Log.d(TAG, "onPostExecute()");
            Log.i(TAG, result);
        }
    }
}
