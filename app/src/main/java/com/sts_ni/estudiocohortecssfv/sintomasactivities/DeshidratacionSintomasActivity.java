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
import com.sts_ni.estudiocohortecssfv.dto.DeshidratacionSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

/**
 * Controlador de la UI Deshidratacion.
 */
public class DeshidratacionSintomasActivity extends ActionBarActivity {

    private Context CONTEXT;
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private DeshidratacionSintomasDTO mCorrienteDeshidratacionSintomas;
    private String mUsuarioLogiado;
    private TextView viewTxtvNDHSintoma;
    private TextView viewTxtvSDHSintoma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deshidratacion_sintoma);

        this.CONTEXT = this;

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        viewTxtvNDHSintoma = (TextView) findViewById(R.id.txtvNDHSintoma);
        viewTxtvSDHSintoma = (TextView) findViewById(R.id.txtvSDHSintoma);

        viewTxtvNDHSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSDHSintoma.setOnClickListener(new View.OnClickListener() {
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

    public void onChkboxClickedLMSDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbLMSSDHSintoma), findViewById(R.id.chkbLMSNDHSintoma), view);
    }

    public void onChkboxClickedPGCDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbPGCSDHSintoma), findViewById(R.id.chkbPGCNDHSintoma), view);
    }

    public void onChkboxClickedORRDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbORRSDHSintoma), findViewById(R.id.chkbORRNDHSintoma),
                findViewById(R.id.chkbORRDDHSintoma), view);
    }

    public void onChkboxClickedBASDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbBASSDHSintoma), findViewById(R.id.chkbBASNDHSintoma),
                view);
    }

    public void onChkboxClickedOJHDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbOJHSDHSintoma), findViewById(R.id.chkbOJHNDHSintoma), view);
    }

    public void onChkboxClickedFTHDH(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbFTHSDHSintoma), findViewById(R.id.chkbFTHNDHSintoma),
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
                            ((CheckBox) findViewById(R.id.chkbLMSNDHSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbPGCNDHSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbORRNDHSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbBASNDHSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbOJHNDHSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbFTHNDHSintoma)).setChecked(!valor);

                            ((CheckBox) findViewById(R.id.chkbLMSSDHSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbPGCSDHSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbORRSDHSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbBASSDHSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbOJHSDHSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbFTHSDHSintoma)).setChecked(valor);

                            ((CheckBox) findViewById(R.id.chkbORRDDHSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_deshid_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_deshid_sintomas)), getResources().getString(
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
                if(mCorrienteDeshidratacionSintomas != null) {
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
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbLMSSDHSintoma), findViewById(R.id.chkbLMSNDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbPGCSDHSintoma), findViewById(R.id.chkbPGCNDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbORRSDHSintoma), findViewById(R.id.chkbORRNDHSintoma), findViewById(R.id.chkbORRDDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbBASSDHSintoma), findViewById(R.id.chkbBASNDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbOJHSDHSintoma), findViewById(R.id.chkbOJHNDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbFTHSDHSintoma), findViewById(R.id.chkbFTHNDHSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setLenguaMucosasSecas(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbLMSSDHSintoma), findViewById(R.id.chkbLMSNDHSintoma)));

        hojaConsulta.setPliegueCutaneo(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbPGCSDHSintoma), findViewById(R.id.chkbPGCNDHSintoma)));

        hojaConsulta.setOrinaReducida(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbORRSDHSintoma), findViewById(R.id.chkbORRNDHSintoma),
                findViewById(R.id.chkbORRDDHSintoma)));

        hojaConsulta.setBebeConSed(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbBASSDHSintoma), findViewById(R.id.chkbBASNDHSintoma)));

        hojaConsulta.setOjosHundidos(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbOJHSDHSintoma), findViewById(R.id.chkbOJHNDHSintoma)).toString());

        hojaConsulta.setFontanelaHundida(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbFTHSDHSintoma), findViewById(R.id.chkbFTHNDHSintoma)));

        return hojaConsulta;
    }

    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if((mCorrienteDeshidratacionSintomas.getBebeConSed() == null && hojaConsulta.getBebeConSed() != null) ||
                (mCorrienteDeshidratacionSintomas.getFontanelaHundida() == null && hojaConsulta.getFontanelaHundida() != null) ||
                (mCorrienteDeshidratacionSintomas.getLenguaMucosasSecas() == null && hojaConsulta.getLenguaMucosasSecas()!= null) ||
                (mCorrienteDeshidratacionSintomas.getOjosHundidos() == null && hojaConsulta.getOjosHundidos() != null) ||
                (mCorrienteDeshidratacionSintomas.getOrinaReducida() == null && hojaConsulta.getOrinaReducida() != null) ||
                (mCorrienteDeshidratacionSintomas.getPliegueCutaneo() == null && hojaConsulta.getPliegueCutaneo() != null)) {
            return true;
        }

        if((mCorrienteDeshidratacionSintomas.getBebeConSed() != null && hojaConsulta.getBebeConSed() == null) ||
                (mCorrienteDeshidratacionSintomas.getFontanelaHundida() != null && hojaConsulta.getFontanelaHundida() == null) ||
                (mCorrienteDeshidratacionSintomas.getLenguaMucosasSecas() != null && hojaConsulta.getLenguaMucosasSecas() == null) ||
                (mCorrienteDeshidratacionSintomas.getOjosHundidos() != null && hojaConsulta.getOjosHundidos() == null) ||
                (mCorrienteDeshidratacionSintomas.getOrinaReducida() != null && hojaConsulta.getOrinaReducida() == null) ||
                (mCorrienteDeshidratacionSintomas.getPliegueCutaneo() != null && hojaConsulta.getPliegueCutaneo() == null)) {
            return true;
        }

        if(mCorrienteDeshidratacionSintomas.getBebeConSed().charValue() != hojaConsulta.getBebeConSed().charValue() ||
                mCorrienteDeshidratacionSintomas.getFontanelaHundida().charValue() != hojaConsulta.getFontanelaHundida().charValue() ||
                mCorrienteDeshidratacionSintomas.getLenguaMucosasSecas().charValue() != hojaConsulta.getLenguaMucosasSecas().charValue() ||
                mCorrienteDeshidratacionSintomas.getOjosHundidos().charValue() != hojaConsulta.getOjosHundidos().charAt(0) ||
                mCorrienteDeshidratacionSintomas.getOrinaReducida().charValue() != hojaConsulta.getOrinaReducida().charValue() ||
                mCorrienteDeshidratacionSintomas.getPliegueCutaneo().charValue() != hojaConsulta.getPliegueCutaneo().charValue()) {
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

                    RESPUESTA = SINTOMASWS.guardarDeshidraSintomas(mHojaConsulta, mUsuarioLogiado);
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
            private ResultadoObjectWSDTO<DeshidratacionSintomasDTO> RESPUESTA;
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
                RESPUESTA = new ResultadoObjectWSDTO<>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerDeshidratacionSintomas(hojaConsulta);
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
                    mCorrienteDeshidratacionSintomas = RESPUESTA.getObjecRespuesta();
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
                DeshidratacionSintomasDTO deshidratacionSintomas = RESPUESTA.getObjecRespuesta();

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbLMSSDHSintoma), findViewById(R.id.chkbLMSNDHSintoma),
                        ((deshidratacionSintomas.getLenguaMucosasSecas() != null)
                                ? deshidratacionSintomas.getLenguaMucosasSecas().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbPGCSDHSintoma), findViewById(R.id.chkbPGCNDHSintoma),
                        ((deshidratacionSintomas.getPliegueCutaneo() != null)
                                ? deshidratacionSintomas.getPliegueCutaneo().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbORRSDHSintoma), findViewById(R.id.chkbORRNDHSintoma),
                        findViewById(R.id.chkbORRDDHSintoma),
                        ((deshidratacionSintomas.getOrinaReducida() != null)
                                ? deshidratacionSintomas.getOrinaReducida().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbBASSDHSintoma), findViewById(R.id.chkbBASNDHSintoma),
                        ((deshidratacionSintomas.getBebeConSed() != null)
                                ? deshidratacionSintomas.getBebeConSed().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbOJHSDHSintoma),
                        findViewById(R.id.chkbOJHNDHSintoma),
                        ((deshidratacionSintomas.getOjosHundidos() != null)
                                ? deshidratacionSintomas.getOjosHundidos().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbFTHSDHSintoma), findViewById(R.id.chkbFTHNDHSintoma),
                        ((deshidratacionSintomas.getFontanelaHundida() != null)
                                ? deshidratacionSintomas.getFontanelaHundida().charValue() : '4'));

            }
        };
        lecturaTask.execute((Void[])null);
    }
}
