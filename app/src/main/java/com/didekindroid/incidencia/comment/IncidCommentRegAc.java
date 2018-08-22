package com.didekindroid.incidencia.comment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.IncidContextualName.new_incid_comment_just_registered;
import static com.didekindroid.incidencia.comment.CtrlerIncidComment.doErrorInCtrler;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;

/**
 * Preconditions:
 * 1. An intent key is received with an IncidenciaUser instance.
 * Postconditions:
 * 1. An intent key is passed with an IncidenciaUser instance.
 * 2. A comment is persisted, associated the usuarioComunidad and incidencia implicits in the
 * incidenciaUser in the received intent.
 */
public class IncidCommentRegAc extends AppCompatActivity {

    Incidencia incidencia;
    Button button;
    View acView;
    CtrlerIncidComment controller;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.incid_comment_reg_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        incidencia = (Incidencia) getIntent().getExtras().getSerializable(INCIDENCIA_OBJECT.key);
        ((TextView) findViewById(R.id.incid_reg_desc_txt)).setText(incidencia.getDescripcion());

        button = findViewById(R.id.incid_comment_reg_button);
        button.setOnClickListener(v -> registerComment());
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        if (controller != null) {
            controller.clearSubscriptions();
        }
        super.onStop();
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
                routerInitializer.get().getMnRouter().getActionFromMnItemId(resourceId).initActivity(this);
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
        IncidCommentBean commentBean = new IncidCommentBean(incidencia);
        IncidComment comment = commentBean.makeComment(acView, errorMsg, getResources());

        if (comment == null) {
            Timber.d("registerComment(); comment == null");
            makeToast(this, errorMsg.toString());
        } else if (!isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            controller = new CtrlerIncidComment();
            controller.regIncidComment(new DisposableSingleObserver<Integer>() {
                @Override
                public void onSuccess(Integer integer)
                {
                    Timber.d("onSuccess()");
                    routerInitializer.get()
                            .getContextRouter()
                            .getActionFromContextNm(new_incid_comment_just_registered)
                            .initActivity(IncidCommentRegAc.this, INCIDENCIA_OBJECT.getBundleForKey(incidencia));
                }

                @Override
                public void onError(Throwable e)
                {
                    Timber.d("onError()");
                    doErrorInCtrler(e, IncidCommentRegAc.this);
                }
            }, comment);
        }
    }
}
