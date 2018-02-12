package com.didekindroid.incidencia.comment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.exception.UiExceptionIf;
import com.didekindroid.lib_one.util.ConnectionUtils;
import com.didekindroid.lib_one.util.UIutils;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.comment_should_be_registered;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static com.didekindroid.lib_one.util.UIutils.checkPostExecute;
import static com.didekindroid.lib_one.util.UIutils.doToolBar;
import static com.didekindroid.lib_one.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UIutils.makeToast;
import static com.didekindroid.router.LeadRouter.afterRegComment;
import static com.didekindroid.router.MnRouterAction.resourceIdToMnItem;
import static com.didekindroid.router.UiExceptionRouter.uiException_router;

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
        mComentarButton = findViewById(R.id.incid_comment_reg_button);
        mComentarButton.setOnClickListener(v -> registerComment());
    }

// ============================================================
//    ..... ACTION BAR ....
// ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                resourceIdToMnItem.get(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    ============================================================
//              .......... HELPERS .......
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

        UiExceptionIf uiException;

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
                uiException_router.getActionFromMsg(uiException.getErrorHtppMsg()).initActivity(IncidCommentRegAc.this);
            } else if (!(isDestroyed() || isChangingConfigurations())) {
                assertTrue(rowInserted == 1, comment_should_be_registered);
                Bundle bundle = new Bundle(1);
                bundle.putSerializable(INCIDENCIA_OBJECT.key, mIncidencia);
                afterRegComment.initActivity(IncidCommentRegAc.this, bundle);
            } else {
                Timber.i("onPostExcecute(): activity destroyed");
            }
        }
    }
}
