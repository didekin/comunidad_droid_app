package com.didekindroid.usuario.login;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.ManagerIf;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.util.ConnectionUtils;
import com.didekindroid.util.MenuRouter;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.usuario.UsuarioBundleKey.login_counter_atomic_int;
import static com.didekindroid.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.usuario.login.LoginAc.PasswordMailDialog.newInstance;
import static com.didekindroid.util.DefaultNextAcRouter.routerMap;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;

/**
 * User: pedro
 * Date: 15/12/14
 * Time: 10:04
 * <p>
 * Preconditions:
 * 1. The user is not necessarily registered: she might have erased the security app data.
 * Results:
 * 1a. If successful, the activity ComuSearchAc is presented and the security data are updated.
 * 1b. If the userName doesn't exist, the user is invited to register.
 * 1c. If the userName exists, but the passowrd is not correct, after three failed intents,  a new passord is sent
 * by mail, after her confirmation.
 */
public class LoginAc extends AppCompatActivity implements ViewerLoginIf<View, Object>, ManagerIf<Object> {

    View acView;
    AtomicInteger counterWrong;
    UsuarioBean usuarioBean;
    ControllerLoginIf controller;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.i("Entered onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.login_ac, null);
        setContentView(acView);
        doToolBar(this, true);
        controller = new ControllerLogin(this);
        counterWrong = new AtomicInteger(0);

        Button mLoginButton = (Button) findViewById(R.id.login_ac_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                if (checkLoginData()) {
                    controller.validateLoginRemote(usuarioBean.getUsuario());
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle saveState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(saveState);
        saveState.putInt(login_counter_atomic_int.key, counterWrong.get());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            counterWrong.set(savedInstanceState.getInt(login_counter_atomic_int.key));
        }
    }

    @Override
    protected void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        clearControllerSubscriptions();
    }

    // ============================================================
    //    ........... ManagerIf .........
    // ============================================================

    @Override
    public Activity getActivity()
    {
        return this;
    }

    @Override
    public ActionForUiExceptionIf processViewerError(UiException ui)
    {
        Timber.d("processViewerError()");
        return ui.processMe(this, new Intent());
    }

    @Override
    public void replaceRootView(Object initParamsForView)
    {
        Timber.d("replaceRootView()");
        Intent intent = new Intent(this, routerMap.get(this.getClass()));
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // ============================================================
    //    ........... VIEWER IMPLEMENTATION .........
    // ============================================================

    @Override
    public ManagerIf<Object> getManager()
    {
        Timber.d("getContext()");
        return this;
    }

    @Override
    public ActionForUiExceptionIf processControllerError(UiException e)
    {
        Timber.d("processControllerError()");
        ActionForUiExceptionIf action = null;
        if (e.getErrorBean().getMessage().equals(USER_NAME_NOT_FOUND.getHttpMessage())) {
            makeToast(this, R.string.username_wrong_in_login);
        } else {
            action = processViewerError(e);
        }
        return action;
    }

    @Override
    public int clearControllerSubscriptions()
    {
        Timber.d("clearControllerSubscriptions()");
        return controller.clearSubscriptions();
    }

    @Override
    public View getViewInViewer()
    {
        Timber.d("getViewInViewer()");
        return acView;
    }

    // ============================================================
    //    ........... VIEWER LOGIN IMPLEMENTATION .........
    // ============================================================

    @Override
    public boolean checkLoginData()
    {
        Timber.i("checkUserData()");
        usuarioBean = new UsuarioBean(getLoginDataFromView()[0], null, getLoginDataFromView()[1], null);

        StringBuilder errorBuilder = getErrorMsgBuilder(this);
        if (!usuarioBean.validateLoginData(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString());
            return false;
        }
        if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
            return false;
        }
        return true;
    }

    @Override
    public void showDialog(String userName)
    {
        Timber.d("showDialog()");
        DialogFragment newFragment = newInstance(usuarioBean);
        newFragment.show(getFragmentManager(), "passwordMailDialog");
    }

    @Override
    public String[] getLoginDataFromView()
    {
        Timber.d("getLoginDataFromView()");
        return new String[]{
                ((EditText) acView.findViewById(R.id.reg_usuario_email_editT)).getText().toString(),
                ((EditText) acView.findViewById(R.id.reg_usuario_password_ediT)).getText().toString()
        };
    }

    @Override
    public void processLoginBackInView(boolean isLoginOk)
    {
        Timber.d("processLoginBackInView()");

        if (isLoginOk) {
            Timber.d("login OK");
            replaceRootView(null);
        } else {
            int counter = counterWrong.addAndGet(1);
            Timber.d("Password wrong, counterWrong = %d%n", counter - 1);
            if (counter > 3) { /* Password wrong*/
                showDialog(usuarioBean.getUserName());
            } else {
                makeToast(this, R.string.password_wrong);
            }
        }
    }

    @Override
    public void processBackSendPswdInView(boolean isSendPassword)
    {
        Timber.d("processBackSendPswdInView()");
        if (isSendPassword) {
            makeToast(this, R.string.password_new_in_login);
            recreate();
        }
    }

    @Override
    public void doDialogNegativeClick()
    {
        Timber.d("doDialogNegativeClick()");
        Intent intent = new Intent(this, routerMap.get(this.getClass()));
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
                MenuRouter.doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //  =====================================================================================================
    //    .................................... INNER CLASSES .................................
    //  =====================================================================================================

    // ............................... DIALOG ................................

    public static class PasswordMailDialog extends DialogFragment {

        public static PasswordMailDialog newInstance(UsuarioBean usuarioBean)
        {
            Timber.d("newInstance()");
            PasswordMailDialog dialog = new PasswordMailDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable(usuario_object.key, usuarioBean.getUsuario());
            dialog.setArguments(bundle);
            return dialog;
        }

        @Override
        public AppCompatDialog onCreateDialog(Bundle savedInstanceState)
        {
            Timber.d("onCreateDialog()");

            int message = R.string.send_password_by_mail_dialog;
            Builder builder = new Builder(getActivity(), R.style.alertDialogTheme);

            builder.setMessage(message)
                    .setPositiveButton(R.string.send_password_by_mail_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dismiss();
                            ((LoginAc) getActivity()).controller.doDialogPositiveClick(
                                    (Usuario) getArguments().getSerializable(usuario_object.key)
                            );
                        }
                    })
                    .setNegativeButton(R.string.send_password_by_mail_NO, new DialogInterface.OnClickListener() {
                        @SuppressWarnings("unchecked")
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dismiss();
                            makeToast(getActivity(), R.string.login_wrong_no_mail);
                            ((LoginAc) getActivity()).doDialogNegativeClick();
                        }
                    });
            return builder.create();
        }
    }
}
