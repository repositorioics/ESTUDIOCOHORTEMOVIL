package com.sts_ni.estudiocohortecssfv.sintomasactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.RenalSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

/**
 * Controlador de la UI Renal.
 */
public class RenalSintomasActivity extends ActionBarActivity {

    private Context CONTEXT;
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private RenalSintomasDTO mCorrienteRenalSintomas;
    private String mUsuarioLogiado;
    private TextView viewTxtvNRNSintoma;
    private TextView viewTxtvSRNSintoma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renal_sintoma);

        this.CONTEXT = this;

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        viewTxtvNRNSintoma = (TextView) findViewById(R.id.txtvNRNSintoma);
        viewTxtvSRNSintoma = (TextView) findViewById(R.id.txtvSRNSintoma);

        viewTxtvNRNSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSRNSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, true);
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

    public void onChkboxClickedSNURN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbSNUSRNSintoma), findViewById(R.id.chkbSNUNRNSintoma),
                findViewById(R.id.chkbSNUDRNSintoma), view);
    }

    public void onChkboxClickedLEURN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbLEUSRNSintoma), findViewById(R.id.chkbLEUNRNSintoma),
                findViewById(R.id.chkbLEUDRNSintoma), view);
    }

    public void onChkboxClickedNITRN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbNITSRNSintoma), findViewById(R.id.chkbNITNRNSintoma),
                findViewById(R.id.chkbNITDRNSintoma), view);
    }

    public void onChkboxClickedERTRN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbERTSRNSintoma), findViewById(R.id.chkbERTNRNSintoma),
                findViewById(R.id.chkbERTDRNSintoma), view);
    }

    public void onChkboxClickedBLRRN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbBLRSRNSintoma), findViewById(R.id.chkbBLRNRNSintoma),
                findViewById(R.id.chkbBLRDRNSintoma), view);
    }

    /***
     * Metodo que es ejecutado en el evento onClick de la eqitueta no o si
     * @param view
     */
    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) findViewById(R.id.chkbSNUNRNSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbLEUNRNSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbNITNRNSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbERTNRNSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbBLRNRNSintoma)).setChecked(!valor);

                            ((CheckBox) findViewById(R.id.chkbSNUSRNSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbLEUSRNSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbNITSRNSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbERTSRNSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbBLRSRNSintoma)).setChecked(valor);

                            ((CheckBox) findViewById(R.id.chkbSNUDRNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbLEUDRNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbNITDRNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbERTDRNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbBLRDRNSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_renal_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_renal_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void onClick_btnRegresar(View view) {
        try{
            validarCampos();
            mHojaConsulta = cargarHojaConsulta();
            if (mPacienteSeleccionado.getCodigoEstado() == '7') {
                if(mCorrienteRenalSintomas != null) {
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
        }
    }

    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbSNUSRNSintoma), findViewById(R.id.chkbSNUNRNSintoma),
                findViewById(R.id.chkbSNUDRNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbLEUSRNSintoma), findViewById(R.id.chkbLEUNRNSintoma),
                findViewById(R.id.chkbLEUDRNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbNITSRNSintoma), findViewById(R.id.chkbNITNRNSintoma),
                findViewById(R.id.chkbNITDRNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbERTSRNSintoma), findViewById(R.id.chkbERTNRNSintoma),
                findViewById(R.id.chkbERTDRNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbBLRSRNSintoma), findViewById(R.id.chkbBLRNRNSintoma),
                findViewById(R.id.chkbBLRDRNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setSintomasUrinarios(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbSNUSRNSintoma),
                findViewById(R.id.chkbSNUNRNSintoma), findViewById(R.id.chkbSNUDRNSintoma)));

        hojaConsulta.setLeucocituria(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbLEUSRNSintoma),
                findViewById(R.id.chkbLEUNRNSintoma), findViewById(R.id.chkbLEUNRNSintoma)));

        hojaConsulta.setNitritos(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbNITSRNSintoma),
                findViewById(R.id.chkbNITNRNSintoma), findViewById(R.id.chkbNITDRNSintoma)));

        hojaConsulta.setEritrocitos(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbERTSRNSintoma),
                findViewById(R.id.chkbERTNRNSintoma), findViewById(R.id.chkbERTDRNSintoma)));

        hojaConsulta.setBilirrubinuria(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbBLRSRNSintoma),
                findViewById(R.id.chkbBLRNRNSintoma), findViewById(R.id.chkbBLRDRNSintoma)));

        return hojaConsulta;
    }

    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if((mCorrienteRenalSintomas.getBilirrubinuria() == null && hojaConsulta.getBilirrubinuria() != null) ||
                (mCorrienteRenalSintomas.getEritrocitos() == null && hojaConsulta.getEritrocitos() != null) ||
                (mCorrienteRenalSintomas.getLeucocituria() == null && hojaConsulta.getLeucocituria()!= null) ||
                (mCorrienteRenalSintomas.getNitritos() == null && hojaConsulta.getNitritos() != null) ||
                (mCorrienteRenalSintomas.getSintomasUrinarios() == null && hojaConsulta.getSintomasUrinarios() != null)) {
            return true;
        }

        if((mCorrienteRenalSintomas.getBilirrubinuria() != null && hojaConsulta.getBilirrubinuria() == null) ||
                (mCorrienteRenalSintomas.getEritrocitos() != null && hojaConsulta.getEritrocitos() == null) ||
                (mCorrienteRenalSintomas.getLeucocituria() != null && hojaConsulta.getLeucocituria() == null) ||
                (mCorrienteRenalSintomas.getNitritos() != null && hojaConsulta.getNitritos() == null) ||
                (mCorrienteRenalSintomas.getSintomasUrinarios() != null && hojaConsulta.getSintomasUrinarios() == null)) {
            return true;
        }

        if(mCorrienteRenalSintomas.getBilirrubinuria().charValue() != hojaConsulta.getBilirrubinuria().charValue() ||
                mCorrienteRenalSintomas.getEritrocitos().charValue() != hojaConsulta.getEritrocitos().charValue() ||
                mCorrienteRenalSintomas.getLeucocituria().charValue() != hojaConsulta.getLeucocituria().charValue() ||
                mCorrienteRenalSintomas.getNitritos().charValue() != hojaConsulta.getNitritos().charValue() ||
                mCorrienteRenalSintomas.getSintomasUrinarios().charValue() != hojaConsulta.getSintomasUrinarios().charValue()) {
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

                    RESPUESTA = SINTOMASWS.guardarRenalSintomas(mHojaConsulta, mUsuarioLogiado);
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
            private ResultadoObjectWSDTO<RenalSintomasDTO> RESPUESTA;
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
                RESPUESTA = new ResultadoObjectWSDTO<RenalSintomasDTO>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerRenalSintomas(hojaConsulta);
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
                    mCorrienteRenalSintomas = RESPUESTA.getObjecRespuesta();
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
                RenalSintomasDTO renalSintomas = RESPUESTA.getObjecRespuesta();

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbSNUSRNSintoma),
                        findViewById(R.id.chkbSNUNRNSintoma), findViewById(R.id.chkbSNUDRNSintoma),
                        ((renalSintomas.getSintomasUrinarios() != null) 
                                ? renalSintomas.getSintomasUrinarios().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbLEUSRNSintoma),
                        findViewById(R.id.chkbLEUNRNSintoma), findViewById(R.id.chkbLEUDRNSintoma),
                        ((renalSintomas.getLeucocituria() != null)
                                ? renalSintomas.getLeucocituria().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbNITSRNSintoma),
                        findViewById(R.id.chkbNITNRNSintoma), findViewById(R.id.chkbNITDRNSintoma),
                        ((renalSintomas.getNitritos() != null)
                                ? renalSintomas.getNitritos().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbERTSRNSintoma),
                        findViewById(R.id.chkbERTNRNSintoma), findViewById(R.id.chkbERTDRNSintoma),
                        ((renalSintomas.getEritrocitos() != null)
                                ? renalSintomas.getEritrocitos().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbBLRSRNSintoma),
                        findViewById(R.id.chkbBLRNRNSintoma), findViewById(R.id.chkbBLRDRNSintoma),
                        ((renalSintomas.getBilirrubinuria() != null)
                                ? renalSintomas.getBilirrubinuria().charValue() : '4'));
            }
        };
        lecturaTask.execute((Void[])null);
    }
}
