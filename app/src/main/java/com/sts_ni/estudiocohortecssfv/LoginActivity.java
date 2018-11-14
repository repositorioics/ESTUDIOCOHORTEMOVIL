package com.sts_ni.estudiocohortecssfv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.InfoSessionWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.ws.SeguridadWS;
import com.sts_ni.estudiocohortecssfv.wsclass.DataNodoItemArray;

import java.util.ArrayList;
import java.util.List;


/**
 * Controlador de la UI del Login.
 */
public class LoginActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    public SeguridadWS mSeguridadWS = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            System.exit(0);
        }
        setContentView(R.layout.activity_login);

        //Inicializando Seguridad WS
        mSeguridadWS = new SeguridadWS(this);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(user)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(user, password, this);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Clase para controlar tarea asyncrona para realizar el llamado al servicio de la
     * autenticación del usuario.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUser;
        private final String mPassword;
        private final Context mContext;
        private ErrorDTO mRespuestaValidacion;
        private final ConnectivityManager mCm;
        private final NetworkInfo mNetInfo;
        private InfoSessionWSDTO mInfoSession;
        private DataNodoItemArray NODO_ITEM_ARRAY;

        UserLoginTask(String user, String password, Context context) {
            mUser = user;
            mPassword = password;
            mContext = context;
            mRespuestaValidacion = new ErrorDTO();
            mCm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            mNetInfo = mCm.getActiveNetworkInfo();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            if (mNetInfo != null && mNetInfo.isConnected()) {

                mInfoSession = new InfoSessionWSDTO();
                mRespuestaValidacion = mSeguridadWS.validarCredenciales(mUser, mPassword, mInfoSession);
                if(mRespuestaValidacion.getCodigoError().intValue() == 0){
                    NODO_ITEM_ARRAY = mSeguridadWS.obterMenuRol(mInfoSession.getUserId());
                }

            } else {
                mRespuestaValidacion.setCodigoError(Long.parseLong("3"));
                mRespuestaValidacion.setMensajeError("");
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                if(mRespuestaValidacion.getCodigoError().intValue() == 0){ // Sincronizacion correcta con el servicio web
                    try {
                        ((CssfvApp)getApplication()).setInfoSessionWSDTO(mInfoSession);
                        if (NODO_ITEM_ARRAY.getRespuestaError().getCodigoError().intValue() == 0) { // Sincronizacion correcta con el servicio web
                            try {
                                ((CssfvApp)getApplication()).setMenuArray(NODO_ITEM_ARRAY);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Intent intentPantallaInicio = new Intent(mContext,
                            PantallaInicioActivity.class);
                    startActivity(intentPantallaInicio);
                    finish();


                }else if(mRespuestaValidacion.getCodigoError().intValue() == 2
                        || mRespuestaValidacion.getCodigoError().intValue() == 3){ // El servicio no se encuentra disponible o Sin disponibilidad a internet

                    MensajesHelper.mostrarMensajeError(mContext, getResources().getString(R.string.msj_servicio_no_dispon),
                            getResources().getString(R.string.app_name), null);


                }else if(mRespuestaValidacion.getCodigoError().intValue() < 0) { // Error en el servicio web

                    MensajesHelper.mostrarMensajeError(mContext, "Respuesta del servicio: " + mRespuestaValidacion.getMensajeError(),
                            getResources().getString(R.string.app_name), null);

                } else { // Respuesta de validacion

                    MensajesHelper.mostrarMensajeError(mContext, mRespuestaValidacion.getMensajeError(),
                            getResources().getString(R.string.app_name), null);

                }
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



