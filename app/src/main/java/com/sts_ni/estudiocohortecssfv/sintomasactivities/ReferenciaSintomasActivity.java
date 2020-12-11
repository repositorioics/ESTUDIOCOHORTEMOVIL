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
import com.sts_ni.estudiocohortecssfv.dto.ReferenciaSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

/**
 * Controlador de la UI Referencia.
 */
public class ReferenciaSintomasActivity extends ActionBarActivity {

    private Context CONTEXT;
    HojaConsultaDTO hcRef;
    HojaConsultaDTO hojaConsulta;
    private String mUsuarioLogiado;
    private TextView viewTxtvNENSintoma;
    private TextView viewTxtvSENSintoma;

    public AsyncTask<Void, Void, Void> mMensajesAlertas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referencia_sintoma);

        this.CONTEXT = this;

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();
        viewTxtvNENSintoma = (TextView) findViewById(R.id.txtvNENSintoma);
        viewTxtvSENSintoma = (TextView) findViewById(R.id.txtvSENSintoma);

        viewTxtvNENSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSENSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, true);
            }
        });
        obtenerMensajesAlertaCriteriosEti();

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

    public void onChkboxClickedINPEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbINPSENSintoma), findViewById(R.id.chkbINPNENSintoma), view);
    }

    public void onChkboxClickedRFHEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRFHSENSintoma), findViewById(R.id.chkbRFHNENSintoma),
                view);
    }

    public void onChkboxClickedRFDEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRFDSENSintoma), findViewById(R.id.chkbRFDNENSintoma),
                view);
        boolean referenciaDengue = ((CheckBox) findViewById(R.id.chkbRFDSENSintoma)).isChecked();
        if (referenciaDengue) {
            String mensaje = "Recuerde marcar las casillas que son de gravedad";
            MensajesHelper.mostrarMensajeInfo(this.CONTEXT,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    null);
        }
    }

    public void onChkboxClickedRFIEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRFISENSintoma), findViewById(R.id.chkbRFINENSintoma),
                view);
    }

    public void onChkboxClickedRFCEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRFCSENSintoma), findViewById(R.id.chkbRFCNENSintoma), view);
    }

    public void onChkboxClickedETIEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbETISENSintoma), findViewById(R.id.chkbETINENSintoma),
                view);
    }

    public void onChkboxClickedIRAEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbIRASENSintoma), findViewById(R.id.chkbIRANENSintoma),
                view);
    }


    public void onChkboxClickedNEUEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbNEUSENSintoma), findViewById(R.id.chkbNEUNENSintoma),
                view);
    }

    public void onChkcoxClickedCV(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbCOVIDSSintoma), findViewById(R.id.chkbCOVIDNSintoma),
                view);
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
                            ((CheckBox) findViewById(R.id.chkbINPNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbRFHNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbRFDNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbRFINENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbRFCNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbETINENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbIRANENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbNEUNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbCOVIDNSintoma)).setChecked(!valor);

                            ((CheckBox) findViewById(R.id.chkbINPSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbRFHSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbRFDSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbRFISENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbRFCSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbETISENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbIRASENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbNEUSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbCOVIDSSintoma)).setChecked(valor);

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_referencia_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_referencia_sintomas)), getResources().getString(
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
            hojaConsulta = cargarHojaConsulta();

            InicioDTO pacienteSeleccionado = (InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado");
            if (pacienteSeleccionado.getCodigoEstado() == '7') {
                if(hcRef != null) {
                    if (tieneCambios(hojaConsulta)) {
                        DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        llamarGuardadoServicio(hojaConsulta);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        regresarPantallaAnterior();
                                        break;
                                }
                            }
                        };
                        MensajesHelper.mostrarMensajeYesNo(this,
                                getResources().getString(
                                        R.string.msj_cambio_hoja_consulta), getResources().getString(
                                        R.string.title_estudio_sostenible),
                                preguntaDialogClickListener);
                    }else{
                        regresarPantallaAnterior();
                    }
                }else{
                    llamarGuardadoServicio(hojaConsulta);
                }
            }else {
                llamarGuardadoServicio(hojaConsulta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    private void regresarPantallaAnterior() {
        InicioDTO pacienteSeleccionado = (InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado");

        Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
        intent.putExtra("indice",0);
        intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);

        startActivity(intent);
        finish();
    }

    private boolean tieneCambios(HojaConsultaDTO hojaconsulta) {
        if( hcRef.getInterconsultaPediatrica().charValue() != '4' && hcRef.getInterconsultaPediatrica().charValue() != hojaconsulta.getInterconsultaPediatrica().charValue() ){
            return true;
        }

        if( hcRef.getReferenciaHospital().charValue() != '4' && hcRef.getReferenciaHospital().charValue() != hojaconsulta.getReferenciaHospital().charValue() ){
            return true;
        }

        if( hcRef.getReferenciaDengue().charValue() != '4' && hcRef.getReferenciaDengue().charValue() != hojaconsulta.getReferenciaDengue().charValue() ){
            return true;
        }

        if( hcRef.getReferenciaIrag().charValue() != '4' && hcRef.getReferenciaIrag().charValue() != hojaconsulta.getReferenciaIrag().charValue() ){
            return true;
        }

        if( hcRef.getReferenciaChik().charValue() != '4' && hcRef.getReferenciaChik().charValue() != hojaconsulta.getReferenciaChik().charValue() ){
            return true;
        }

        if( hcRef.getEti().charValue() != '4' && hcRef.getEti().charValue() != hojaconsulta.getEti().charValue() ){
            return true;
        }

        if( hcRef.getIrag().charValue() != '4' && hcRef.getIrag().charValue() != hojaconsulta.getIrag().charValue() ){
            return true;
        }

        if( hcRef.getNeumonia().charValue() != '4' && hcRef.getNeumonia().charValue() != hojaconsulta.getNeumonia().charValue() ){
            return true;
        }
        if( hcRef.getcV().charValue() != '4' && hcRef.getcV().charValue() != hojaconsulta.getcV().charValue() ){
            return true;
        }

        return false;
    }

    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbINPSENSintoma), findViewById(R.id.chkbINPNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRFHSENSintoma), findViewById(R.id.chkbRFHNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRFDSENSintoma), findViewById(R.id.chkbRFDNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRFISENSintoma), findViewById(R.id.chkbRFINENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRFCSENSintoma), findViewById(R.id.chkbRFCNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbETISENSintoma), findViewById(R.id.chkbETINENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbIRASENSintoma), findViewById(R.id.chkbIRANENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbNEUSENSintoma), findViewById(R.id.chkbNEUNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbCOVIDSSintoma), findViewById(R.id.chkbCOVIDNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setInterconsultaPediatrica(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbINPSENSintoma), findViewById(R.id.chkbINPNENSintoma)));

        hojaConsulta.setReferenciaHospital(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRFHSENSintoma), findViewById(R.id.chkbRFHNENSintoma)));

        hojaConsulta.setReferenciaDengue(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRFDSENSintoma), findViewById(R.id.chkbRFDNENSintoma)));

        hojaConsulta.setReferenciaIrag(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRFISENSintoma), findViewById(R.id.chkbRFINENSintoma)));

        hojaConsulta.setReferenciaChik(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRFCSENSintoma), findViewById(R.id.chkbRFCNENSintoma)));

        hojaConsulta.setEti(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbETISENSintoma), findViewById(R.id.chkbETINENSintoma)));

        hojaConsulta.setIrag(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbIRASENSintoma), findViewById(R.id.chkbIRANENSintoma)));

        hojaConsulta.setNeumonia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbNEUSENSintoma), findViewById(R.id.chkbNEUNENSintoma)));

        hojaConsulta.setcV(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbCOVIDSSintoma), findViewById(R.id.chkbCOVIDNSintoma)));

        return hojaConsulta;
    }

    private void llamarGuardadoServicio(final HojaConsultaDTO hojaConsulta){
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

                    RESPUESTA = SINTOMASWS.guardarReferenciaSintomas(hojaConsulta, mUsuarioLogiado);
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
            private ResultadoObjectWSDTO<ReferenciaSintomasDTO> RESPUESTA;
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
                RESPUESTA = new ResultadoObjectWSDTO<ReferenciaSintomasDTO>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerReferenciaSintomas(hojaConsulta);
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
                ReferenciaSintomasDTO referenciaSintomas = RESPUESTA.getObjecRespuesta();
                hcRef = new HojaConsultaDTO();

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbINPSENSintoma), findViewById(R.id.chkbINPNENSintoma),
                        ((referenciaSintomas.getInterconsultaPediatrica() != null)
                                ? referenciaSintomas.getInterconsultaPediatrica().charValue() : '4'));
                hcRef.setInterconsultaPediatrica(((referenciaSintomas.getInterconsultaPediatrica() != null)
                        ? referenciaSintomas.getInterconsultaPediatrica().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRFHSENSintoma), findViewById(R.id.chkbRFHNENSintoma),
                        ((referenciaSintomas.getReferenciaHospital() != null)
                                ? referenciaSintomas.getReferenciaHospital().charValue() : '4'));
                hcRef.setReferenciaHospital(((referenciaSintomas.getReferenciaHospital() != null)
                        ? referenciaSintomas.getReferenciaHospital().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRFDSENSintoma), findViewById(R.id.chkbRFDNENSintoma),
                        ((referenciaSintomas.getReferenciaDengue() != null)
                                ? referenciaSintomas.getReferenciaDengue().charValue() : '4'));
                hcRef.setReferenciaDengue(((referenciaSintomas.getReferenciaDengue() != null)
                        ? referenciaSintomas.getReferenciaDengue().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRFISENSintoma), findViewById(R.id.chkbRFINENSintoma),
                        ((referenciaSintomas.getReferenciaIrag() != null)
                                ? referenciaSintomas.getReferenciaIrag().charValue() : '4'));
                hcRef.setReferenciaIrag(((referenciaSintomas.getReferenciaIrag() != null)
                        ? referenciaSintomas.getReferenciaIrag().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRFCSENSintoma), findViewById(R.id.chkbRFCNENSintoma),
                        ((referenciaSintomas.getReferenciaChik() != null)
                                ? referenciaSintomas.getReferenciaChik().charValue() : '4'));
                hcRef.setReferenciaChik(((referenciaSintomas.getReferenciaChik() != null)
                        ? referenciaSintomas.getReferenciaChik().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbETISENSintoma), findViewById(R.id.chkbETINENSintoma),
                        ((referenciaSintomas.getEti() != null) ? referenciaSintomas.getEti().charValue() : '4'));
                hcRef.setEti(((referenciaSintomas.getEti() != null) ? referenciaSintomas.getEti().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbIRASENSintoma), findViewById(R.id.chkbIRANENSintoma),
                        ((referenciaSintomas.getIrag() != null) ? referenciaSintomas.getIrag().charValue() : '4'));
                hcRef.setIrag(((referenciaSintomas.getIrag() != null) ? referenciaSintomas.getIrag().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbNEUSENSintoma), findViewById(R.id.chkbNEUNENSintoma),
                        ((referenciaSintomas.getNeumonia() != null) ? referenciaSintomas.getNeumonia().charValue() : '4'));
                hcRef.setNeumonia(((referenciaSintomas.getNeumonia() != null) ? referenciaSintomas.getNeumonia().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbCOVIDSSintoma), findViewById(R.id.chkbCOVIDNSintoma),
                        ((referenciaSintomas.getcV() != null) ? referenciaSintomas.getcV().charValue() : '4'));
                hcRef.setcV(((referenciaSintomas.getcV() != null) ? referenciaSintomas.getcV().charValue() : '4'));
            }
        };
        lecturaTask.execute((Void[])null);
    }

    /***
     * Metodo que realiza el llamado del servicio de alertas para verificar
     * si reunen criterios Eti
     * Fecha creacion 24/11/2020 - SC
     */
    private void obtenerMensajesAlertaCriteriosEti() {
        if (mMensajesAlertas == null ||
                mMensajesAlertas.getStatus() == AsyncTask.Status.FINISHED) {
            mMensajesAlertas = new AsyncTask<Void, Void, Void>() {

                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private SintomasWS SINTOMASWS = new SintomasWS(getResources());
                private String MENSAJE_MATRIZ = null;

                @Override
                protected void onPreExecute() {
                    PD = new ProgressDialog(CONTEXT);
                    PD.setTitle(getResources().getString(R.string.title_obteniendo));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    if (NET_INFO != null && NET_INFO.isConnected()) {
                        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
                        hojaConsulta.setSecHojaConsulta(((InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                        MENSAJE_MATRIZ = SINTOMASWS.validacionMatrizEti(hojaConsulta);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    PD.dismiss();
                    try {
                        if (!StringUtils.isNullOrEmpty(MENSAJE_MATRIZ)
                                && !MENSAJE_MATRIZ.startsWith("any")) {
                            MensajesHelper.mostrarMensajeInfo(CONTEXT, MENSAJE_MATRIZ,
                                    getResources().getString(R.string.title_estudio_sostenible), null);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        MensajesHelper.mostrarMensajeError(CONTEXT,
                                new StringBuffer().append(getResources().getString(
                                        R.string.msj_error_no_controlado)).append(e.getMessage()).toString(),
                                getResources().getString(R.string.title_estudio_sostenible), null);
                    }
                }
            };
            mMensajesAlertas.execute((Void[]) null);
        }
    }
}
