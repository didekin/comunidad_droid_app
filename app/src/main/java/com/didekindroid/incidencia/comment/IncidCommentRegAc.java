package com.didekindroid.incidencia.comment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.util.ConnectionUtils;
import com.didekindroid.util.UIutils;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.comment_should_be_registered;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

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
                Timber.d("onClickLinkToImportanciaUsers()");
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
            makeToast(this, errorMsg.toString());
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast);
        } else {
            new IncidCommentRegister().execute(comment);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    @SuppressWarnings("WeakerAccess")
    class IncidCommentRegister extends AsyncTask<IncidComment, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(IncidComment... comments)
        {
            Timber.d("doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = incidenciaDao.regIncidComment(comments[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return rowInserted;
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            if (checkPostExecute(IncidCommentRegAc.this)) return;

            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidCommentRegAc.this);
            } else if (!(isDestroyed() || isChangingConfigurations())) {
                assertTrue(rowInserted == 1, comment_should_be_registered);
                Intent intent = new Intent(IncidCommentRegAc.this, IncidCommentSeeAc.class);
                intent.putExtra(INCIDENCIA_OBJECT.key, mIncidencia);
                startActivity(intent);
            } else {
                Timber.i("onPostExcecute(): activity destroyed");
            }
        }
    }
}
