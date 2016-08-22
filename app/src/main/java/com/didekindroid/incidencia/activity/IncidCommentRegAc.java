package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.didekin.incidservice.dominio.IncidComment;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.incidencia.dominio.IncidCommentBean;

import static com.didekindroid.common.activity.BundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. An intent key is received with an IncidenciaUser instance.
 * Postconditions:
 * 1. An intent key is passed with an IncidenciaUser instance.
 * 2. A comment is persisted, associated the usuarioComunidad and incidencia implicits in the
 *    incidenciaUser in the received intent.
 */
public class IncidCommentRegAc extends AppCompatActivity {

    private static final String TAG = IncidCommentRegAc.class.getCanonicalName();
    Incidencia mIncidencia;
    Button mComentarButton;
    View mAcView;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        mAcView = getLayoutInflater().inflate(R.layout.incid_comment_reg_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        mIncidencia = (Incidencia) getIntent().getExtras().getSerializable(INCIDENCIA_OBJECT.key);
        ((TextView) findViewById(R.id.incid_reg_desc_txt)).setText(mIncidencia.getDescripcion());
        mComentarButton = (Button) findViewById(R.id.incid_comment_reg_button);
        mComentarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "onClick()");
                registerComment();
            }
        });
    }

//    ============================================================
//              .......... HELPER METHDOS .......
//    ============================================================

    private void registerComment()
    {
        Log.d(TAG, "registerComment()");
        StringBuilder errorMsg = getErrorMsgBuilder(this);
        IncidCommentBean commentBean = new IncidCommentBean(mIncidencia);
        IncidComment comment = commentBean.makeComment(mAcView ,errorMsg, getResources());

        if (comment == null) {
            Log.d(TAG, "registerComment(); comment == null");
            makeToast(this, errorMsg.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
        } else {
            new IncidCommentRegister().execute(comment);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

// TODO: to persist the task during restarts and properly cancel the task when the activity is destroyed. (Example in Shelves)
    class IncidCommentRegister extends AsyncTask<IncidComment, Void, Integer> {

        private final String TAG = IncidCommentRegister.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(IncidComment... comments)
        {
            Log.d(TAG, "doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = IncidenciaServ.regIncidComment(comments[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return rowInserted;
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            Log.d(TAG, "onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidCommentRegAc.this, new Intent());
            } else {
                checkState(rowInserted == 1);
                Intent intent = new Intent(IncidCommentRegAc.this, IncidCommentSeeAc.class);
                intent.putExtra(INCIDENCIA_OBJECT.key, mIncidencia);
                startActivity(intent);
            }
        }
    }
}
