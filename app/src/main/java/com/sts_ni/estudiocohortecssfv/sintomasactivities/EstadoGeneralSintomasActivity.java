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
import android.widget.EditText;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.EstadoGeneralesSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.CancelacionDialog;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

/**
 * Controlador de la UI donde se ingresa la informacion de Estado General.
 */
public class EstadoGeneralSintomasActivity extends ActionBarActivity {

    private Context CONTEXT;
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private EstadoGeneralesSintomasDTO mCorrienteEstadoGenerales;
    private String mUsuarioLogiado;
    private TextView viewTxtvNEGSintoma;
    private TextView viewTxtvSEGSintoma;
    private String vFueraRango = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_general_sintoma);

        this.CONTEXT = this;

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());
        viewTxtvNEGSintoma = (TextView) findViewById(R.id.txtvNEGSintoma);
        viewTxtvSEGSintoma = (TextView) findViewById(R.id.txtvSEGSintoma);

        viewTxtvNEGSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSEGSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, true);
            }
        });


        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();
    }

    @Override
    protected void onStart () {
        super.onStart();
        obtenerEstadoGeneralServicio();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void onChkboxClickedFiebreEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbFiebreSEGSintoma), findViewById(R.id.chkbFiebreNEGSintoma),
                findViewById(R.id.chkbFiebreDEGSintoma), view);
    }

    public void onChkboxClickedAstnEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbAstnSEGSintoma), findViewById(R.id.chkbAstnNEGSintoma),
                findViewById(R.id.chkbAstnDEGSintoma), view);
    }

    public void onChkboxClickedAnormSomEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbAnormSomnSEGSintoma), findViewById(R.id.chkbAnormSomnNEGSintoma),
                view);
    }

    public void onChkboxClickedMalEstGEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbMalEstSEGSintoma), findViewById(R.id.chkbMalEstNEGSintoma),
                view);
    }

    public void onChkboxClickedPerdConsEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbPerdConsSEGSintoma), findViewById(R.id.chkbPerdConsNEGSintoma),
                view);
    }

    public void onChkboxClickedInqIrriEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbInqIrriSEGSintoma), findViewById(R.id.chkbInqIrriNEGSintoma),
                view);
    }

    public void onChkboxClickedConvulEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbConvulSEGSintoma), findViewById(R.id.chkbConvulNEGSintoma),
                view);
    }

    public void onChkboxClickedHipoEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbHipoSEGSintoma), findViewById(R.id.chkbHipoNEGSintoma),
                view);
    }

    public void onChkboxClickedLetarEG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbLetarSEGSintoma), findViewById(R.id.chkbLetarNEGSintoma),
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
                            ((CheckBox) findViewById(R.id.chkbFiebreNEGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbAstnNEGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbAnormSomnNEGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbMalEstNEGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbPerdConsNEGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbInqIrriNEGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbConvulNEGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbHipoNEGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbLetarNEGSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbFiebreSEGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbAstnSEGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbAnormSomnSEGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbMalEstSEGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbPerdConsSEGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbInqIrriSEGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbConvulSEGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbHipoSEGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbLetarSEGSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbFiebreDEGSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbAstnDEGSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_estado_general_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_estado_general_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    /***
     * Metodo que es ejecutado en el evento onClick de guardar la informacion.
     * @param view
     */
    public void onClick_btnEstadoGeneral(View view) {
        try{
            validarCampos();
            mHojaConsulta = cargarHojaConsulta();
            if (mPacienteSeleccionado.getCodigoEstado() == '7') {
                if(mCorrienteEstadoGenerales != null) {
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
                                getResources().getString(
                                        R.string.msj_cambio_hoja_consulta), getResources().getString(
                                        R.string.title_estudio_sostenible),
                                preguntaDialogClickListener);
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

    /***
     * Metodo para validar los campos requeridos
     * @throws Exception
     */
    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbFiebreSEGSintoma), findViewById(R.id.chkbFiebreNEGSintoma),
                findViewById(R.id.chkbFiebreDEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbAstnSEGSintoma), findViewById(R.id.chkbAstnNEGSintoma),
                findViewById(R.id.chkbAstnDEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbAnormSomnSEGSintoma), findViewById(R.id.chkbAnormSomnNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbMalEstSEGSintoma), findViewById(R.id.chkbMalEstNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbPerdConsSEGSintoma), findViewById(R.id.chkbPerdConsNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbInqIrriSEGSintoma), findViewById(R.id.chkbInqIrriNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbConvulSEGSintoma), findViewById(R.id.chkbConvulNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbHipoSEGSintoma), findViewById(R.id.chkbHipoNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbLetarSEGSintoma), findViewById(R.id.chkbLetarNEGSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
    }
    /***
     * Metodo que carga la informacion de Estado general cuando ya se habia guardado anteriormente.
     * @return
     */
    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setFiebre(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbFiebreSEGSintoma), findViewById(R.id.chkbFiebreNEGSintoma),
                findViewById(R.id.chkbFiebreDEGSintoma)));

        hojaConsulta.setAstenia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbAstnSEGSintoma), findViewById(R.id.chkbAstnNEGSintoma),
                findViewById(R.id.chkbAstnDEGSintoma)));

        hojaConsulta.setAsomnoliento(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbAnormSomnSEGSintoma), findViewById(R.id.chkbAnormSomnNEGSintoma)));

        hojaConsulta.setMalEstado(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbMalEstSEGSintoma), findViewById(R.id.chkbMalEstNEGSintoma)));

        hojaConsulta.setPerdidaConsciencia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbPerdConsSEGSintoma), findViewById(R.id.chkbPerdConsNEGSintoma)));

        hojaConsulta.setInquieto(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbInqIrriSEGSintoma), findViewById(R.id.chkbInqIrriNEGSintoma)));

        hojaConsulta.setConvulsiones(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbConvulSEGSintoma), findViewById(R.id.chkbConvulNEGSintoma)));

        hojaConsulta.setHipotermia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbHipoSEGSintoma), findViewById(R.id.chkbHipoNEGSintoma)));

        hojaConsulta.setLetargia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbLetarSEGSintoma), findViewById(R.id.chkbLetarNEGSintoma)));

        return hojaConsulta;
    }

    /***
     * Metodo para validar si los datos han sufrido cambios.
     * @param hojaConsulta
     * @return
     */
    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if((mCorrienteEstadoGenerales.getFiebre() == null && hojaConsulta.getFiebre() != null) ||
                (mCorrienteEstadoGenerales.getAsomnoliento() == null && hojaConsulta.getAsomnoliento() != null) ||
                (mCorrienteEstadoGenerales.getAstenia() == null && hojaConsulta.getAstenia()!= null) ||
                (mCorrienteEstadoGenerales.getConvulsiones() == null && hojaConsulta.getConvulsiones() != null) ||
                (mCorrienteEstadoGenerales.getHipotermia() == null && hojaConsulta.getHipotermia() != null) ||
                (mCorrienteEstadoGenerales.getInquieto() == null && hojaConsulta.getInquieto() != null) ||
                (mCorrienteEstadoGenerales.getLetargia() == null && hojaConsulta.getLetargia() != null) ||
                (mCorrienteEstadoGenerales.getMalEstado() == null && hojaConsulta.getMalEstado() != null) ||
                (mCorrienteEstadoGenerales.getPerdidaConsciencia() == null && hojaConsulta.getPerdidaConsciencia() != null)) {
            return true;
        }

        if((mCorrienteEstadoGenerales.getFiebre() != null && hojaConsulta.getFiebre() == null) ||
                (mCorrienteEstadoGenerales.getAsomnoliento() != null && hojaConsulta.getAsomnoliento() == null) ||
                (mCorrienteEstadoGenerales.getAstenia() != null && hojaConsulta.getAstenia() == null) ||
                (mCorrienteEstadoGenerales.getConvulsiones() != null && hojaConsulta.getConvulsiones() == null) ||
                (mCorrienteEstadoGenerales.getHipotermia() != null && hojaConsulta.getHipotermia() == null) ||
                (mCorrienteEstadoGenerales.getInquieto() != null && hojaConsulta.getInquieto() == null) ||
                (mCorrienteEstadoGenerales.getLetargia() != null && hojaConsulta.getLetargia() == null) ||
                (mCorrienteEstadoGenerales.getMalEstado() != null && hojaConsulta.getMalEstado() == null) ||
                (mCorrienteEstadoGenerales.getPerdidaConsciencia() != null && hojaConsulta.getPerdidaConsciencia() == null)) {
            return true;
        }

        if(mCorrienteEstadoGenerales.getFiebre().charValue() != hojaConsulta.getFiebre().charValue() ||
                mCorrienteEstadoGenerales.getAsomnoliento().charValue() != hojaConsulta.getAsomnoliento().charValue() ||
                mCorrienteEstadoGenerales.getAstenia().charValue() != hojaConsulta.getAstenia().charValue() ||
                mCorrienteEstadoGenerales.getConvulsiones().charValue() != hojaConsulta.getConvulsiones().charValue() ||
                mCorrienteEstadoGenerales.getHipotermia().charValue() != hojaConsulta.getHipotermia().charValue() ||
                mCorrienteEstadoGenerales.getInquieto().charValue() != hojaConsulta.getInquieto().charValue() ||
                mCorrienteEstadoGenerales.getLetargia().charValue() != hojaConsulta.getLetargia().charValue() ||
                mCorrienteEstadoGenerales.getMalEstado().charValue() != hojaConsulta.getMalEstado().charValue() ||
                mCorrienteEstadoGenerales.getPerdidaConsciencia().charValue() != hojaConsulta.getPerdidaConsciencia().charValue()) {
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

    /***
     * Metodo que realiza el llamdo del servicio que guarda la Hoja de consulta para la informacion Estado General.
     */
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

                    RESPUESTA = SINTOMASWS.guardarEstadoGeneralSintomas(mHojaConsulta, mUsuarioLogiado);
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

    private void obtenerEstadoGeneralServicio(){
        AsyncTask<Void, Void, Void> lecturaTask = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ResultadoObjectWSDTO<EstadoGeneralesSintomasDTO> RESPUESTA;
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
                    RESPUESTA = SINTOMASWS.obtenerEstadoGeneralSintomas(hojaConsulta);
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
                    mCorrienteEstadoGenerales = RESPUESTA.getObjecRespuesta();
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
                EstadoGeneralesSintomasDTO estadoGeneralesSintomas = RESPUESTA.getObjecRespuesta();

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbFiebreSEGSintoma), findViewById(R.id.chkbFiebreNEGSintoma),
                        findViewById(R.id.chkbFiebreDEGSintoma), ((estadoGeneralesSintomas.getFiebre() != null)
                                ? estadoGeneralesSintomas.getFiebre().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbAstnSEGSintoma), findViewById(R.id.chkbAstnNEGSintoma),
                        findViewById(R.id.chkbAstnDEGSintoma), ((estadoGeneralesSintomas.getAstenia() != null)
                                ? estadoGeneralesSintomas.getAstenia().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbAnormSomnSEGSintoma),
                        findViewById(R.id.chkbAnormSomnNEGSintoma), ((estadoGeneralesSintomas.getAsomnoliento() != null)
                                ? estadoGeneralesSintomas.getAsomnoliento().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbMalEstSEGSintoma),
                         findViewById(R.id.chkbMalEstNEGSintoma), ((estadoGeneralesSintomas.getMalEstado() != null)
                                ? estadoGeneralesSintomas.getMalEstado().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbPerdConsSEGSintoma),
                         findViewById(R.id.chkbPerdConsNEGSintoma), ((estadoGeneralesSintomas.getPerdidaConsciencia() != null)
                                ? estadoGeneralesSintomas.getPerdidaConsciencia().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbInqIrriSEGSintoma),
                         findViewById(R.id.chkbInqIrriNEGSintoma), ((estadoGeneralesSintomas.getInquieto() != null)
                                ? estadoGeneralesSintomas.getInquieto().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbConvulSEGSintoma),
                         findViewById(R.id.chkbConvulNEGSintoma), ((estadoGeneralesSintomas.getConvulsiones() != null)
                                ? estadoGeneralesSintomas.getInquieto().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbHipoSEGSintoma),
                        findViewById(R.id.chkbHipoNEGSintoma), ((estadoGeneralesSintomas.getHipotermia() != null)
                                ? estadoGeneralesSintomas.getHipotermia().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbLetarSEGSintoma),
                         findViewById(R.id.chkbLetarNEGSintoma), ((estadoGeneralesSintomas.getLetargia() != null)
                                ? estadoGeneralesSintomas.getLetargia().charValue() : '4'));
            }
        };
        lecturaTask.execute((Void[])null);
    }
}
