package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.didekin.incidencia.dominio.IncidComment;
import com.didekin.incidencia.dominio.Incidencia;
import com.didekindroid.incidencia.exception.UiAppException;
import com.didekindroid.R;
import com.didekinaar.utils.ConnectionUtils;
import com.didekinaar.utils.UIutils;
import com.didekindroid.incidencia.dominio.IncidCommentBean;

import java.util.Objects;

import timber.log.Timber;

import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekinaar.usuario.UserMenu.doUpMenu;

/**
 * Preconditions:
 * 1. An intent key is received with an IncidenciaUser instance.
 * Postconditions:
 * 1. An intent key is passed with an IncidenciaUser instance.
 * 2. A comment is persisted, associated the usuarioComunidad and incidencia implicits in the
 * incidenciaUser in the received intent.
 */
public class IncidCommentRegAc extends AppCompatActivity {

    Incidencia mIncidencia;
    Button mComentarButton;
    View mAcView;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
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
                Timber.d("onClick()");
                registerComment();
            }
        });
    }

// ============================================================
//    ..... ACTION BAR ....
/* ============================================================*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    ============================================================
//              .......... HELPER METHDOS .......
//    ============================================================

    void registerComment()
    {
        Timber.d("registerComment()");
        StringBuilder errorMsg = getErrorMsgBuilder(this);
        IncidCommentBean commentBean = new IncidCommentBean(mIncidencia);
        IncidComment comment = commentBean.makeComment(mAcView, errorMsg, getResources());

        if (comment == null) {
            Timber.d("registerComment(); comment == null");
            makeToast(this, errorMsg.toString(), com.didekinaar.R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast);
        } else {
            new IncidCommentRegister().execute(comment);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    // TODO: to persist the task during restarts and properly cancel the task when the activity is destroyed. (Example in Shelves)
    class IncidCommentRegister extends AsyncTask<IncidComment, Void, Integer> {

        UiAppException uiException;

        @Override
        protected Integer doInBackground(IncidComment... comments)
        {
            Timber.d("doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = IncidenciaServ.regIncidComment(comments[0]);
            } catch (UiAppException e) {
                uiException = e;
            }
            return rowInserted;
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidCommentRegAc.this, new Intent());
            } else {
                Objects.equals(rowInserted == 1, true);
                Intent intent = new Intent(IncidCommentRegAc.this, IncidCommentSeeAc.class);
                intent.putExtra(INCIDENCIA_OBJECT.key, mIncidencia);
                startActivity(intent);
            }
        }
    }
}
