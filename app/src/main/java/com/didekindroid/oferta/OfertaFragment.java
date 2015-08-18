package com.didekindroid.oferta;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.didekindroid.R;

/**
 * User: pedro
 * Date: 04/02/15
 * Time: 13:39
 */
public class OfertaFragment extends Fragment {

    public static final String TAG = "OfertaFragment";
    public static final String ARG_DESCRIP = "description";
    private String mCurrentOfferDescrip = null;
    private TextView mOfferView;

    @Override
    public void onAttach(Activity activity)
    {
        Log.d(TAG, "onAttach()");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        Log.d(TAG, "onCreateView()");

        // If activity recreated (such as from screen rotate), restore
        // the previous offer selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentOfferDescrip = savedInstanceState.getString(ARG_DESCRIP);
        }

        // Inflate the layout for this fragment
        mOfferView = (TextView) inflater.inflate(R.layout.oferta_view, container, false);
        return mOfferView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "Enters onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        Log.d(TAG, "Enters onViewRestored()");
        super.onViewStateRestored(savedInstanceState);
    }


    @Override
    public void onStart()
    {
        Log.d(TAG, "Enters onStart()");
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            updateOfferView(args.getString(ARG_DESCRIP));
        } else if (mCurrentOfferDescrip != null) {
            updateOfferView(mCurrentOfferDescrip);
        }
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "Enters onResume()");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "Enters onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putString(ARG_DESCRIP, mCurrentOfferDescrip);
    }

    public void updateOfferView(String description)
    {
        Log.d(TAG, "Enters updateOfferView()");
        mOfferView.setText(description);
        mCurrentOfferDescrip = description;
    }


    /*
    This is usually where you should commit any changes that should be persisted beyond
    the current user session.
    */
    @Override
    public void onPause()
    {
        Log.d(TAG,"onPause()" );
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.d(TAG,"onStop()" );
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Log.d(TAG,"onDestroyView()" );
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Log.d(TAG,"onDetach()");
        super.onDetach();
    }
}
