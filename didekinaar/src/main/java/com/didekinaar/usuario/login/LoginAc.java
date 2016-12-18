package com.didekinaar.usuario.login;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekinaar.R;
import com.didekinaar.utils.UIutils;

import timber.log.Timber;

import static com.didekinaar.usuario.login.LoginAc.PasswordMailDialog.newInstance;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.makeToast;

/**
 * User: pedro
 * Date: 15/12/14
 * Time: 10:04
 */

/**
 * Preconditions:
 * 1. The user is not necessarily registered: she might have erased the security app data.
 * Results:
 * 1a. If successful, the activity ComuSearchAc is presented and the security data are updated.
 * 1b. If the userName doesn't exist, the user is invited to register.
 * 1c. If the userName exists, but the passowrd is not correct, after three failed intents,  a new passord is sent
 * by mail, after her confirmation.
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class LoginAc extends AppCompatActivity implements LoginViewIf {

    View mAcView;
    volatile short counterWrong;
    LoginController controller;
    protected Class<? extends Activity> defaultActivityClassToGo;

    // Template method to be overwritten in the apps.
    protected abstract void setDefaultActivityClassToGo(Class<? extends Activity> activityClassToGo);
    // Template method to be overwritten in the apps.
    protected abstract int getDialogThemeId();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.i("Entered onCreate()");
        super.onCreate(savedInstanceState);

        mAcView = getLayoutInflater().inflate(R.layout.login_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);
        controller = new LoginController(this);

        Button mLoginButton = (Button) findViewById(R.id.login_ac_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                controller.doLoginValidate();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
        if (controller.subscriptions != null && controller.subscriptions.hasSubscriptions()) {
            controller.subscriptions.unsubscribe();
        }
    }

    @Override
    public void showDialog(String userName)
    {
        Timber.d("showDialog()");
        DialogFragment newFragment = newInstance(userName);
        newFragment.show(getFragmentManager(), "passwordMailDialog");
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
                UIutils.doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //  =====================================================================================================
    //    .................................... INNER CLASSES .................................
    //  =====================================================================================================

    public static class PasswordMailDialog extends DialogFragment {

        public static PasswordMailDialog newInstance(String emailUser)
        {
            Timber.d("newInstance()");

            PasswordMailDialog mailDialog = new PasswordMailDialog();
            Bundle args = new Bundle();
            args.putString(EMAIL_DIALOG_ARG, emailUser);
            mailDialog.setArguments(args);
            return mailDialog;
        }

        @Override
        public AppCompatDialog onCreateDialog(Bundle savedInstanceState)
        {
            Timber.d("onCreateDialog()");

            int message = R.string.send_password_by_mail_dialog;
            Builder builder = new Builder(getActivity(), ((LoginAc) getActivity()).getDialogThemeId());

            builder.setMessage(message)
                    .setPositiveButton(R.string.send_password_by_mail_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dismiss();
                            ((LoginAc) getActivity()).controller.doDialogPositiveClick(getArguments().getString("email"));
                        }
                    })
                    .setNegativeButton(R.string.send_password_by_mail_NO, new DialogInterface.OnClickListener() {
                        @SuppressWarnings("unchecked")
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dismiss();
                            makeToast(getActivity(), R.string.login_wrong_no_mail);
                            ((LoginAc) getActivity()).controller.doDialogNegativeClick();
                        }
                    });
            return builder.create();
        }
    }
}
