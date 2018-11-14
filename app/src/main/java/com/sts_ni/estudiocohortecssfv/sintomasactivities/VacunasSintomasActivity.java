package com.sts_ni.estudiocohortecssfv.sintomasactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.VacunasSintomasDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.DatePickerFragment;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Controlador de la UI Vacunas.
 */
public class VacunasSintomasActivity extends ActionBarActivity {

    private Context CONTEXT;
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private VacunasSintomasDTO mCorrienteVacunasSintomas;
    private String mUsuarioLogiado;
    private TextView viewTxtvNENSintoma;
    private TextView viewTxtvSENSintoma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacuna_sintoma);

        this.CONTEXT = this;

        /*((EditText)findViewById(R.id.dpFCV)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    showDatePickerDialog(v);
            }
        });*/

        findViewById(R.id.dpFCV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        ((EditText)findViewById(R.id.dpFCV)).setKeyListener(null);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        viewTxtvNENSintoma = (TextView) findViewById(R.id.txtvNENSintoma);
        viewTxtvSENSintoma = (TextView) findViewById(R.id.txtvSENSintoma);


        viewTxtvNENSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 2);
            }
        });

        viewTxtvSENSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onStart () {
        super.onStart();
        obtenerValorGuardadoServicio();
    }

    /***
     * Controla el evento check de lactancia materna.
     * @param view
     */
    public void onChkboxClickedLTMEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbLTMSENSintoma), findViewById(R.id.chkbLTMNENSintoma),
                findViewById(R.id.chkbLTMDENSintoma), view);
    }

    /***
     * Controla el evento check de Vacuna completa
     * @param view
     */
    public void onChkboxClickedVCCEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbVCCSENSintoma), findViewById(R.id.chkbVCCNENSintoma),
                findViewById(R.id.chkbVCCDENSintoma), view);
    }

    /***
     * Controla el evento check de Vacuna Influenza.
     * @param view
     */
    public void onChkboxClickedVCIEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbVCISENSintoma), findViewById(R.id.chkbVCINENSintoma),
                findViewById(R.id.chkbVCIDENSintoma), view);

        if(((CheckBox)findViewById(R.id.chkbVCISENSintoma)).isChecked()) {
            findViewById(R.id.txtvFCVENSintoma).setVisibility(View.VISIBLE);
            findViewById(R.id.dpFCV).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.txtvFCVENSintoma).setVisibility(View.INVISIBLE);
            ((EditText)findViewById(R.id.dpFCV)).setText("");
            findViewById(R.id.dpFCV).setVisibility(View.INVISIBLE);
        }
    }

    /***
     * Metodo para mostrar el Date Picker de Fecha Vacuna.
     * @param view
     */
    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)getActivity().findViewById(R.id.dpFCV)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    ((EditText) getActivity().findViewById(R.id.dpFCV)).setError(null);
                    ((EditText) getActivity().findViewById(R.id.dpFCV)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                    ((EditText) getActivity().findViewById(R.id.dpFCV)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        };
        newFragment.show(getSupportFragmentManager(), getString(R.string.title_date_picker));
    }

    /***
     * Metodo que es ejecutado en el evento onClick de la eqitueta no o si
     * @param view
     */
    public void SintomaMarcado(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) findViewById(R.id.chkbLTMSENSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbVCCSENSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbVCISENSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) findViewById(R.id.chkbLTMSENSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbVCCSENSintoma)).setChecked(true);
                            if(valor==1) {
                                ((CheckBox) findViewById(R.id.chkbVCISENSintoma)).setChecked(true);
                                findViewById(R.id.txtvFCVENSintoma).setVisibility(View.VISIBLE);
                                findViewById(R.id.dpFCV).setVisibility(View.VISIBLE);
                            }



                            ((CheckBox) findViewById(R.id.chkbLTMNENSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbVCCNENSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbVCINENSintoma)).setChecked(false);

                            if(valor==2) ((CheckBox) findViewById(R.id.chkbLTMNENSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbVCCNENSintoma)).setChecked(true);
                            if(valor==2) {
                                ((CheckBox) findViewById(R.id.chkbVCINENSintoma)).setChecked(true);
                                findViewById(R.id.txtvFCVENSintoma).setVisibility(View.INVISIBLE);
                                ((EditText)findViewById(R.id.dpFCV)).setText("");
                                findViewById(R.id.dpFCV).setVisibility(View.INVISIBLE);
                            }


                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_vacunas_sintomas));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.boton_vacunas_sintomas));


            MensajesHelper.mostrarMensajeYesNo(this,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    /***
     * Metodo que realiza el guardado de la informacion registrada y regresa a la pantalla Consulta.
     * @param view
     */
    public void onClick_btnRegresar(View view) {
        try{
            validarCampos();
            mHojaConsulta = cargarHojaConsulta();
            if (mPacienteSeleccionado.getCodigoEstado() == '7') {
                if(mCorrienteVacunasSintomas != null) {
                    if(tieneCambiosHojaConsulta(mHojaConsulta)) {
                        DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        llamarGuardadoServicio();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        regresarPantallaAnterior();
                                        break;
                                }
                            }
                        };
                        MensajesHelper.mostrarMensajeYesNo(this,
                                getResources().getString(R.string.msj_cambio_hoja_consulta),
                                getResources().getString(R.string.title_estudio_sostenible), preguntaDialogClickListener);
                    } else {
                        regresarPantallaAnterior();
                    }
                } else {
                    regresarPantallaAnterior();
                }
            } else {
                llamarGuardadoServicio();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        } finally {
        }
    }

    public void validarCampos() throws Exception {
        EditText dpFCV = (EditText) findViewById(R.id.dpFCV);
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbLTMSENSintoma), findViewById(R.id.chkbLTMNENSintoma),
                findViewById(R.id.chkbLTMDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbVCCSENSintoma), findViewById(R.id.chkbVCCNENSintoma),
                findViewById(R.id.chkbVCCDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbVCISENSintoma), findViewById(R.id.chkbVCINENSintoma),
                findViewById(R.id.chkbVCIDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if (((CheckBox) findViewById(R.id.chkbVCISENSintoma)).isChecked() && StringUtils.isNullOrEmpty(dpFCV.getText().toString())) {
            throw new Exception(getString(R.string.msj_debe_ingresar_fecha_vacuna));
        }
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setLactanciaMaterna(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbLTMSENSintoma),
                findViewById(R.id.chkbLTMNENSintoma), findViewById(R.id.chkbLTMDENSintoma)));

        hojaConsulta.setVacunasCompletas(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbVCCSENSintoma),
                findViewById(R.id.chkbVCCNENSintoma), findViewById(R.id.chkbVCCNENSintoma)));

        hojaConsulta.setVacunaInfluenza(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbVCISENSintoma),
                findViewById(R.id.chkbVCINENSintoma), findViewById(R.id.chkbVCIDENSintoma)));

        if( StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.dpFCV)).getText().toString()) ){
            hojaConsulta.setFechaVacuna(null);
        }else {
            hojaConsulta.setFechaVacuna(((EditText) findViewById(R.id.dpFCV)).getText().toString());
        }

        return hojaConsulta;
    }

    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if((mCorrienteVacunasSintomas.getLactanciaMaterna() == null && hojaConsulta.getLactanciaMaterna() != null) ||
                (mCorrienteVacunasSintomas.getVacunaInfluenza() == null && hojaConsulta.getVacunaInfluenza() != null) ||
                (mCorrienteVacunasSintomas.getVacunasCompletas() == null && hojaConsulta.getVacunasCompletas()!= null)) {
            return true;
        }

        if((mCorrienteVacunasSintomas.getLactanciaMaterna() != null && hojaConsulta.getLactanciaMaterna() == null) ||
                (mCorrienteVacunasSintomas.getVacunaInfluenza() != null && hojaConsulta.getVacunaInfluenza() == null) ||
                (mCorrienteVacunasSintomas.getVacunasCompletas() != null && hojaConsulta.getVacunasCompletas() == null)) {
            return true;
        }

        if(mCorrienteVacunasSintomas.getLactanciaMaterna().charValue() != hojaConsulta.getLactanciaMaterna().charValue() ||
                mCorrienteVacunasSintomas.getVacunaInfluenza().charValue() != hojaConsulta.getVacunaInfluenza().charValue() ||
                mCorrienteVacunasSintomas.getVacunasCompletas().charValue() != hojaConsulta.getVacunasCompletas().charValue()) {
            return true;
        }

        return false;
    }

    private void regresarPantallaAnterior() {
        Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
        intent.putExtra("indice",0);
        intent.putExtra("pacienteSeleccionado", mPacienteSeleccionado);

        startActivity(intent);
        finish();
    }

    private void llamarGuardadoServicio(){
        AsyncTask<Void, Void, Void> guardarTask = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private SintomasWS SINTOMASWS = new SintomasWS(getResources());

            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(CONTEXT);
                PD.setTitle(getResources().getString(R.string.tittle_actualizando));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){

                    RESPUESTA = SINTOMASWS.guardarVacunasSintomas(mHojaConsulta, mUsuarioLogiado);
                }else{
                    RESPUESTA.setCodigoError(Long.parseLong("3"));
                    RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                PD.dismiss();
                if (RESPUESTA.getCodigoError().intValue() == 0){
                    InicioDTO pacienteSeleccionado = (InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado");

                    Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
                    intent.putExtra("indice",0);
                    intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);

                    startActivity(intent);
                    finish();

                }else if (RESPUESTA.getCodigoError().intValue() != 999){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            RESPUESTA.getMensajeError(),getResources().getString(
                                    R.string.title_estudio_sostenible), null);

                }else{
                    MensajesHelper.mostrarMensajeError(CONTEXT,
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }
            }
        };
        guardarTask.execute((Void[])null);
    }

    private void obtenerValorGuardadoServicio(){
        AsyncTask<Void, Void, Void> lecturaTask = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ResultadoObjectWSDTO<VacunasSintomasDTO> RESPUESTA;
            private SintomasWS SINTOMASWS;

            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(CONTEXT);
                PD.setTitle(getResources().getString(R.string.title_obteniendo));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
                SINTOMASWS = new SintomasWS(getResources());
                RESPUESTA = new ResultadoObjectWSDTO<VacunasSintomasDTO>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerVacunaSintomas(hojaConsulta);
                }else{
                    RESPUESTA.setCodigoError(Long.parseLong("3"));
                    RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                PD.dismiss();
                if (RESPUESTA.getCodigoError().intValue() == 0){
                    cargarValores();
                    mCorrienteVacunasSintomas = RESPUESTA.getObjecRespuesta();
                }else if (RESPUESTA.getCodigoError().intValue() != 999){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            RESPUESTA.getMensajeError(),getResources().getString(
                                    R.string.title_estudio_sostenible), null);

                }else{
                    MensajesHelper.mostrarMensajeError(CONTEXT,
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }
            }

            protected void cargarValores() {
                VacunasSintomasDTO vacunasSintomas = RESPUESTA.getObjecRespuesta();

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbLTMSENSintoma),
                        findViewById(R.id.chkbLTMNENSintoma), findViewById(R.id.chkbLTMDENSintoma), 
                        ((vacunasSintomas.getLactanciaMaterna() != null) ? vacunasSintomas.getLactanciaMaterna().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbVCCSENSintoma),
                        findViewById(R.id.chkbVCCNENSintoma), findViewById(R.id.chkbVCCNENSintoma),
                        ((vacunasSintomas.getVacunasCompletas() != null) ? vacunasSintomas.getVacunasCompletas().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbVCISENSintoma),
                        findViewById(R.id.chkbVCINENSintoma), findViewById(R.id.chkbVCIDENSintoma), 
                        ((vacunasSintomas.getVacunaInfluenza() != null) ? vacunasSintomas.getVacunaInfluenza().charValue() : '4'));

                if(!StringUtils.isNullOrEmpty(vacunasSintomas.getFechaVacuna())) {
                    ((EditText) findViewById(R.id.dpFCV)).setText((vacunasSintomas.getFechaVacuna()));
                }

                if(!((CheckBox)findViewById(R.id.chkbVCISENSintoma)).isChecked()) {
                    findViewById(R.id.txtvFCVENSintoma).setVisibility(View.INVISIBLE);
                    findViewById(R.id.dpFCV).setVisibility(View.INVISIBLE);
                }
            }
        };
        lecturaTask.execute((Void[])null);
    }
}
