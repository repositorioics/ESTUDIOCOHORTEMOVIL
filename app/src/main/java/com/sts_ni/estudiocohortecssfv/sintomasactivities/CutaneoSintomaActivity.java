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
import com.sts_ni.estudiocohortecssfv.dto.CutaneoSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

/**
 * Controlador de la UI Cutaneo.
 */
public class CutaneoSintomaActivity extends ActionBarActivity {

    private Context CONTEXT;
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private CutaneoSintomasDTO mCorrienteCutaneoSintomas;
    private String mUsuarioLogiado;
    private TextView viewTxtvNCTSintoma;
    private TextView viewTxtvSCTSintoma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutaneo_sintoma);

        this.CONTEXT = this;

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        viewTxtvNCTSintoma = (TextView) findViewById(R.id.txtvNCTSintoma);
        viewTxtvSCTSintoma = (TextView) findViewById(R.id.txtvSCTSintoma);


        viewTxtvNCTSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 2);
            }
        });

        viewTxtvSCTSintoma.setOnClickListener(new View.OnClickListener() {
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

    public void onChkboxClickedRHLCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRHLSCTSintoma), findViewById(R.id.chkbRHLNCTSintoma), view);
    }

    public void onChkboxClickedRHGCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRHGSCTSintoma), findViewById(R.id.chkbRHGNCTSintoma),
                view);
    }

    public void onChkboxClickedRHECT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRHESCTSintoma), findViewById(R.id.chkbRHENCTSintoma),
                view);
    }

    public void onChkboxClickedRHMCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRHMSCTSintoma), findViewById(R.id.chkbRHMNCTSintoma),
                view);
    }

    public void onChkboxClickedRHPCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRHPSCTSintoma), findViewById(R.id.chkbRHPNCTSintoma), view);
    }

    public void onChkboxClickedRSMCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRSMSCTSintoma), findViewById(R.id.chkbRSMNCTSintoma),
                view);
    }

    public void onChkboxClickedRBFCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRBFSCTSintoma), findViewById(R.id.chkbRBFNCTSintoma),
                view);
    }


    public void onChkboxClickedEQMCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbEQMSCTSintoma), findViewById(R.id.chkbEQMNCTSintoma),
                view);
    }

    public void onChkboxClickedCNCCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbCNCSCTSintoma), findViewById(R.id.chkbCNCNCTSintoma),
                view);
    }

    public void onChkboxClickedITCCT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbITCSCTSintoma), findViewById(R.id.chkbITCNCTSintoma),
                view);
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

                            ((CheckBox) findViewById(R.id.chkbRHLSCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRHGSCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRHESCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRHMSCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRHPSCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRSMSCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRBFSCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEQMSCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbCNCSCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbITCSCTSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) findViewById(R.id.chkbRHLSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbRHGSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbRHESCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbRHMSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbRHPSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbRSMSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbRBFSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbEQMSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbCNCSCTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbITCSCTSintoma)).setChecked(true);

                            ((CheckBox) findViewById(R.id.chkbRHLNCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRHGNCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRHENCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRHMNCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRHPNCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRSMNCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRBFNCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEQMNCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbCNCNCTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbITCNCTSintoma)).setChecked(false);

                            if(valor==2) ((CheckBox) findViewById(R.id.chkbRHLNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbRHGNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbRHENCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbRHMNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbRHPNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbRSMNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbRBFNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbEQMNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbCNCNCTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbITCNCTSintoma)).setChecked(true);


                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_cutaneo_sintomas));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.boton_cutaneo_sintomas));
            if(valor==3) mensaje = String.format(getResources().getString(R.string.msg_change_desc), getResources().getString(R.string.boton_cutaneo_sintomas));


            MensajesHelper.mostrarMensajeYesNo(this,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

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
                if(mCorrienteCutaneoSintomas != null) {
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
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRHLSCTSintoma), findViewById(R.id.chkbRHLNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRHGSCTSintoma), findViewById(R.id.chkbRHGNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRHESCTSintoma), findViewById(R.id.chkbRHENCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRHMSCTSintoma), findViewById(R.id.chkbRHMNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRHPSCTSintoma), findViewById(R.id.chkbRHPNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRSMSCTSintoma), findViewById(R.id.chkbRSMNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRBFSCTSintoma), findViewById(R.id.chkbRBFNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbEQMSCTSintoma), findViewById(R.id.chkbEQMNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbCNCSCTSintoma), findViewById(R.id.chkbCNCNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbITCSCTSintoma), findViewById(R.id.chkbITCNCTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setRahsLocalizado(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRHLSCTSintoma), findViewById(R.id.chkbRHLNCTSintoma)));

        hojaConsulta.setRahsGeneralizado(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRHGSCTSintoma), findViewById(R.id.chkbRHGNCTSintoma)));

        hojaConsulta.setRashEritematoso(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRHESCTSintoma), findViewById(R.id.chkbRHENCTSintoma)));

        hojaConsulta.setRahsMacular(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRHMSCTSintoma), findViewById(R.id.chkbRHMNCTSintoma)));

        hojaConsulta.setRashPapular(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRHPSCTSintoma), findViewById(R.id.chkbRHPNCTSintoma)));

        hojaConsulta.setRahsMoteada(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRSMSCTSintoma), findViewById(R.id.chkbRSMNCTSintoma)));

        hojaConsulta.setRuborFacial(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRBFSCTSintoma), findViewById(R.id.chkbRBFNCTSintoma)));

        hojaConsulta.setEquimosis(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbEQMSCTSintoma), findViewById(R.id.chkbEQMNCTSintoma)));

        hojaConsulta.setCianosisCentral(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbCNCSCTSintoma), findViewById(R.id.chkbCNCNCTSintoma)));

        hojaConsulta.setIctericia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbITCSCTSintoma), findViewById(R.id.chkbITCNCTSintoma)));

        return hojaConsulta;
    }

    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if((mCorrienteCutaneoSintomas.getCianosisCentral() == null && hojaConsulta.getCianosisCentral() != null) ||
                (mCorrienteCutaneoSintomas.getEquimosis() == null && hojaConsulta.getEquimosis() != null) ||
                (mCorrienteCutaneoSintomas.getIctericia() == null && hojaConsulta.getIctericia()!= null) ||
                (mCorrienteCutaneoSintomas.getRahsGeneralizado() == null && hojaConsulta.getRahsGeneralizado() != null) ||
                (mCorrienteCutaneoSintomas.getRahsLocalizado() == null && hojaConsulta.getRahsLocalizado() != null) ||
                (mCorrienteCutaneoSintomas.getRahsMacular() == null && hojaConsulta.getRahsMacular() != null) ||
                (mCorrienteCutaneoSintomas.getRahsMoteada() == null && hojaConsulta.getRahsMoteada() != null) ||
                (mCorrienteCutaneoSintomas.getRashEritematoso() == null && hojaConsulta.getRashEritematoso() != null) ||
                (mCorrienteCutaneoSintomas.getRashPapular() == null && hojaConsulta.getRashPapular() != null) ||
                (mCorrienteCutaneoSintomas.getRuborFacial() == null && hojaConsulta.getRuborFacial() != null)) {
            return true;
        }

        if((mCorrienteCutaneoSintomas.getCianosisCentral() != null && hojaConsulta.getCianosisCentral() == null) ||
                (mCorrienteCutaneoSintomas.getEquimosis() != null && hojaConsulta.getEquimosis() == null) ||
                (mCorrienteCutaneoSintomas.getIctericia() != null && hojaConsulta.getIctericia() == null) ||
                (mCorrienteCutaneoSintomas.getRahsGeneralizado() != null && hojaConsulta.getRahsGeneralizado() == null) ||
                (mCorrienteCutaneoSintomas.getRahsLocalizado() != null && hojaConsulta.getRahsLocalizado() == null) ||
                (mCorrienteCutaneoSintomas.getRahsMacular() != null && hojaConsulta.getRahsMacular() == null) ||
                (mCorrienteCutaneoSintomas.getRahsMoteada() != null && hojaConsulta.getRahsMoteada() == null) ||
                (mCorrienteCutaneoSintomas.getRashEritematoso() != null && hojaConsulta.getRashEritematoso() == null) ||
                (mCorrienteCutaneoSintomas.getRashPapular() != null && hojaConsulta.getRashPapular() == null) ||
                (mCorrienteCutaneoSintomas.getRuborFacial() != null && hojaConsulta.getRuborFacial() == null)) {
            return true;
        }

        if(mCorrienteCutaneoSintomas.getCianosisCentral().charValue() != hojaConsulta.getCianosisCentral().charValue() ||
                mCorrienteCutaneoSintomas.getEquimosis().charValue() != hojaConsulta.getEquimosis().charValue() ||
                mCorrienteCutaneoSintomas.getIctericia().charValue() != hojaConsulta.getIctericia().charValue() ||
                mCorrienteCutaneoSintomas.getRahsGeneralizado().charValue() != hojaConsulta.getRahsGeneralizado().charValue() ||
                mCorrienteCutaneoSintomas.getRahsLocalizado().charValue() != hojaConsulta.getRahsLocalizado().charValue() ||
                mCorrienteCutaneoSintomas.getRahsMacular().charValue() != hojaConsulta.getRahsMacular().charValue() ||
                mCorrienteCutaneoSintomas.getRahsMoteada().charValue() != hojaConsulta.getRahsMoteada().charValue() ||
                mCorrienteCutaneoSintomas.getRashEritematoso().charValue() != hojaConsulta.getRashEritematoso().charValue() ||
                mCorrienteCutaneoSintomas.getRashPapular().charValue() != hojaConsulta.getRashPapular().charValue() ||
                mCorrienteCutaneoSintomas.getRuborFacial().charValue() != hojaConsulta.getRuborFacial().charValue()) {
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

                    RESPUESTA = SINTOMASWS.guardarCutaneoSintomas(mHojaConsulta, mUsuarioLogiado);
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
            private ResultadoObjectWSDTO<CutaneoSintomasDTO> RESPUESTA;
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
                RESPUESTA = new ResultadoObjectWSDTO<CutaneoSintomasDTO>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerCutaneoSintomas(hojaConsulta);
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
                    mCorrienteCutaneoSintomas = RESPUESTA.getObjecRespuesta();
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
                CutaneoSintomasDTO cutaneoSintomas = RESPUESTA.getObjecRespuesta();
                        
                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRHLSCTSintoma), findViewById(R.id.chkbRHLNCTSintoma), 
                        ((cutaneoSintomas.getRahsLocalizado() != null) ? cutaneoSintomas.getRahsLocalizado().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRHGSCTSintoma), findViewById(R.id.chkbRHGNCTSintoma),
                        ((cutaneoSintomas.getRahsGeneralizado() != null) ? cutaneoSintomas.getRahsGeneralizado().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRHESCTSintoma), findViewById(R.id.chkbRHENCTSintoma),
                        ((cutaneoSintomas.getRashEritematoso() != null) ? cutaneoSintomas.getRashEritematoso().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRHMSCTSintoma), findViewById(R.id.chkbRHMNCTSintoma),
                        ((cutaneoSintomas.getRahsMacular() != null) ? cutaneoSintomas.getRahsMacular().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRHPSCTSintoma), findViewById(R.id.chkbRHPNCTSintoma),
                        ((cutaneoSintomas.getRashPapular() != null) ? cutaneoSintomas.getRashPapular().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRSMSCTSintoma), findViewById(R.id.chkbRSMNCTSintoma),
                        ((cutaneoSintomas.getRahsMoteada() != null) ? cutaneoSintomas.getRahsMoteada().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRBFSCTSintoma), findViewById(R.id.chkbRBFNCTSintoma),
                        ((cutaneoSintomas.getRuborFacial() != null) ? cutaneoSintomas.getRuborFacial().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbEQMSCTSintoma), findViewById(R.id.chkbEQMNCTSintoma),
                        ((cutaneoSintomas.getEquimosis() != null) ? cutaneoSintomas.getEquimosis().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbCNCSCTSintoma), findViewById(R.id.chkbCNCNCTSintoma),
                        ((cutaneoSintomas.getCianosisCentral() != null) ? cutaneoSintomas.getCianosisCentral().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbITCSCTSintoma), findViewById(R.id.chkbITCNCTSintoma),
                        ((cutaneoSintomas.getIctericia() != null) ? cutaneoSintomas.getIctericia().charValue() : '4'));
            }
        };
        lecturaTask.execute((Void[])null);
    }
}
