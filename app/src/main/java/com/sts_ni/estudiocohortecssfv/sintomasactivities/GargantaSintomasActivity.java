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
import com.sts_ni.estudiocohortecssfv.dto.GargantaSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

/**
 * Controlador de la UI Garganta.
 */
public class GargantaSintomasActivity extends ActionBarActivity {

    private Context CONTEXT;
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private GargantaSintomasDTO mCorrienteGargantaSintomas;
    private String mUsuarioLogiado;
    private TextView viewTxtvNGGSintoma;
    private TextView viewTxtvSGGSintoma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garganta_sintoma);

        this.CONTEXT = this;

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        viewTxtvNGGSintoma = (TextView) findViewById(R.id.txtvNGGSintoma);
        viewTxtvSGGSintoma = (TextView) findViewById(R.id.txtvSGGSintoma);

        viewTxtvNGGSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSGGSintoma.setOnClickListener(new View.OnClickListener() {
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

    public void onChkboxClickedERTGG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbERTSGGSintoma), findViewById(R.id.chkbERTNGGSintoma),
                view);
    }

    public void onChkboxClickedDGGGG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbDGGSGGSintoma), findViewById(R.id.chkbDGGNGGSintoma),
                findViewById(R.id.chkbDGGDGGSintoma), view);
    }

    public void onChkboxClickedADCGG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbADCSGGSintoma), findViewById(R.id.chkbADCNGGSintoma),
                view);
    }

    public void onChkboxClickedEXDGG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbEXDSGGSintoma), findViewById(R.id.chkbEXDNGGSintoma),
                view);
    }

    public void onChkboxClickedPIMGG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbPIMSGGSintoma), findViewById(R.id.chkbPIMNGGSintoma),
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
                            ((CheckBox) findViewById(R.id.chkbERTNGGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbDGGNGGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbADCNGGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbEXDNGGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbPIMNGGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbERTSGGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbDGGSGGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbADCSGGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbEXDSGGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbPIMSGGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbDGGDGGSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_garganta_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_garganta_sintomas)), getResources().getString(
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
                if(mCorrienteGargantaSintomas != null) {
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
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbERTSGGSintoma), findViewById(R.id.chkbERTNGGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbDGGSGGSintoma), findViewById(R.id.chkbDGGNGGSintoma),
                findViewById(R.id.chkbDGGDGGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbADCSGGSintoma), findViewById(R.id.chkbADCNGGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbEXDSGGSintoma), findViewById(R.id.chkbEXDNGGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbPIMSGGSintoma), findViewById(R.id.chkbPIMNGGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setEritema(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbERTSGGSintoma),
                findViewById(R.id.chkbERTNGGSintoma)));

        hojaConsulta.setDolorGarganta(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbDGGSGGSintoma),
                findViewById(R.id.chkbDGGNGGSintoma), findViewById(R.id.chkbDGGNGGSintoma)));

        hojaConsulta.setAdenopatiasCervicales(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbADCSGGSintoma),
                findViewById(R.id.chkbADCNGGSintoma)));

        hojaConsulta.setExudado(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbEXDSGGSintoma),
                findViewById(R.id.chkbEXDNGGSintoma)));

        hojaConsulta.setPetequiasMucosa(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbPIMSGGSintoma),
                findViewById(R.id.chkbPIMNGGSintoma)));

        return hojaConsulta;
    }

    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if((mCorrienteGargantaSintomas.getAdenopatiasCervicales() == null && hojaConsulta.getAdenopatiasCervicales() != null) ||
                (mCorrienteGargantaSintomas.getDolorGarganta() == null && hojaConsulta.getDolorGarganta() != null) ||
                (mCorrienteGargantaSintomas.getEritema() == null && hojaConsulta.getEritema()!= null) ||
                (mCorrienteGargantaSintomas.getExudado() == null && hojaConsulta.getExudado() != null) ||
                (mCorrienteGargantaSintomas.getPetequiasMucosa() == null && hojaConsulta.getPetequiasMucosa() != null)) {
            return true;
        }

        if((mCorrienteGargantaSintomas.getAdenopatiasCervicales() != null && hojaConsulta.getAdenopatiasCervicales() == null) ||
                (mCorrienteGargantaSintomas.getDolorGarganta() != null && hojaConsulta.getDolorGarganta() == null) ||
                (mCorrienteGargantaSintomas.getEritema() != null && hojaConsulta.getEritema() == null) ||
                (mCorrienteGargantaSintomas.getExudado() != null && hojaConsulta.getExudado() == null) ||
                (mCorrienteGargantaSintomas.getPetequiasMucosa() != null && hojaConsulta.getPetequiasMucosa() == null)) {
            return true;
        }

        if(mCorrienteGargantaSintomas.getAdenopatiasCervicales().charValue() != hojaConsulta.getAdenopatiasCervicales().charValue() ||
                mCorrienteGargantaSintomas.getDolorGarganta().charValue() != hojaConsulta.getDolorGarganta().charValue() ||
                mCorrienteGargantaSintomas.getEritema().charValue() != hojaConsulta.getEritema().charValue() ||
                mCorrienteGargantaSintomas.getExudado().charValue() != hojaConsulta.getExudado().charValue() ||
                mCorrienteGargantaSintomas.getPetequiasMucosa().charValue() != hojaConsulta.getPetequiasMucosa().charValue()) {
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

                    RESPUESTA = SINTOMASWS.guardarGargantaSintomas(mHojaConsulta, mUsuarioLogiado);
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
            private ResultadoObjectWSDTO<GargantaSintomasDTO> RESPUESTA;
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
                RESPUESTA = new ResultadoObjectWSDTO<GargantaSintomasDTO>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerGargantaSintomas(hojaConsulta);
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
                    mCorrienteGargantaSintomas = RESPUESTA.getObjecRespuesta();
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
                GargantaSintomasDTO gargantaSintomas = RESPUESTA.getObjecRespuesta();

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbERTSGGSintoma),
                        findViewById(R.id.chkbERTNGGSintoma), ((gargantaSintomas.getEritema() != null)
                                ? gargantaSintomas.getEritema().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbDGGSGGSintoma),
                        findViewById(R.id.chkbDGGNGGSintoma), findViewById(R.id.chkbDGGNGGSintoma),
                        ((gargantaSintomas.getDolorGarganta() != null) ? gargantaSintomas.getDolorGarganta().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbADCSGGSintoma),
                        findViewById(R.id.chkbADCNGGSintoma), ((gargantaSintomas.getAdenopatiasCervicales() != null)
                                ? gargantaSintomas.getAdenopatiasCervicales().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbEXDSGGSintoma),
                        findViewById(R.id.chkbEXDNGGSintoma), ((gargantaSintomas.getExudado() != null)
                                ? gargantaSintomas.getExudado().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbPIMSGGSintoma),
                        findViewById(R.id.chkbPIMNGGSintoma), ((gargantaSintomas.getPetequiasMucosa() != null)
                                ? gargantaSintomas.getPetequiasMucosa().charValue() : '4'));

            }
        };
        lecturaTask.execute((Void[])null);
    }
}
