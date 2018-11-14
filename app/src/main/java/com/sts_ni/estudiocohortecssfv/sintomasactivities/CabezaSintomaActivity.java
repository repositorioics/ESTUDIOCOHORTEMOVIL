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
import com.sts_ni.estudiocohortecssfv.dto.CabezaSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

/**
 * Controlador de la UI donde se ingresa la informacion de Cabeza.
 */
public class CabezaSintomaActivity extends ActionBarActivity {

    private Context CONTEXT;
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private CabezaSintomasDTO mCorrienteCabezaSintomas;
    private String mUsuarioLogiado;
    private TextView viewTxtvNCBSintoma;
    private TextView viewTxtvSCBSintoma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabeza_sintoma);

        this.CONTEXT = this;

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();
        viewTxtvNCBSintoma = (TextView) findViewById(R.id.txtvNCBSintoma);
        viewTxtvSCBSintoma = (TextView) findViewById(R.id.txtvSCBSintoma);

        viewTxtvNCBSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSCBSintoma.setOnClickListener(new View.OnClickListener() {
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

    public void onChkboxClickedCAFCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbCAFSCBSintoma), findViewById(R.id.chkbCAFNCBSintoma),
                findViewById(R.id.chkbCAFDCBSintoma), view);
    }

    public void onChkboxClickedRGCCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRGCSCBSintoma), findViewById(R.id.chkbRGCNCBSintoma), view);
    }

    public void onChkboxClickedINCCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbINCSCBSintoma), findViewById(R.id.chkbINCNCBSintoma),
                view);
    }

    public void onChkboxClickedHSCCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbHSCSCBSintoma), findViewById(R.id.chkbHSCNCBSintoma),
                view);
    }

    public void onChkboxClickedDROCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbDROSCBSintoma), findViewById(R.id.chkbDRONCBSintoma),
                findViewById(R.id.chkbDRODCBSintoma), view);
    }

    public void onChkboxClickedFTACB(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbFTASCBSintoma), findViewById(R.id.chkbFTANCBSintoma),
                view);
    }

    public void onChkboxClickedITCCB(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbITCSCBSintoma), findViewById(R.id.chkbITCNCBSintoma),
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
                            ((CheckBox) findViewById(R.id.chkbCAFNCBSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbRGCNCBSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbINCNCBSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbHSCNCBSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbDRONCBSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbFTANCBSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbITCNCBSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbCAFSCBSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbRGCSCBSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbINCSCBSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbHSCSCBSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbDROSCBSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbFTASCBSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbITCSCBSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbCAFDCBSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbDRODCBSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_cabez_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_cabez_sintomas)), getResources().getString(
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
    public void onClick_btnCabeza(View view) {
        try{
            validarCampos();
            mHojaConsulta = cargarHojaConsulta();
            if (mPacienteSeleccionado.getCodigoEstado() == '7') {
                if(mCorrienteCabezaSintomas != null) {
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
                                        R.string.title_estudio_sostenible), preguntaDialogClickListener);
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

    /***
     * Metodo para validar los campos requeridos
     * @throws Exception
     */
    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbCAFSCBSintoma), findViewById(R.id.chkbCAFNCBSintoma),
                findViewById(R.id.chkbCAFDCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRGCSCBSintoma), findViewById(R.id.chkbRGCNCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbINCSCBSintoma), findViewById(R.id.chkbINCNCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbHSCSCBSintoma), findViewById(R.id.chkbHSCNCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbDROSCBSintoma), findViewById(R.id.chkbDRONCBSintoma),
                findViewById(R.id.chkbDRODCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbFTASCBSintoma), findViewById(R.id.chkbFTANCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbITCSCBSintoma), findViewById(R.id.chkbITCNCBSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
    }

    /***
     * Metodo que carga la informacion de seccion cabeza cuando ya se habia guardado anteriormente.
     * @return
     */
    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setCefalea(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbCAFSCBSintoma), findViewById(R.id.chkbCAFNCBSintoma),
                findViewById(R.id.chkbCAFDCBSintoma)));

        hojaConsulta.setRigidezCuello(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRGCSCBSintoma), findViewById(R.id.chkbRGCNCBSintoma)));

        hojaConsulta.setInyeccionConjuntival(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbINCSCBSintoma), findViewById(R.id.chkbINCNCBSintoma)));

        hojaConsulta.setHemorragiaSuconjuntival(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbHSCSCBSintoma), findViewById(R.id.chkbHSCNCBSintoma)));

        hojaConsulta.setDolorRetroocular(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbDROSCBSintoma), findViewById(R.id.chkbDRONCBSintoma),
                findViewById(R.id.chkbDRODCBSintoma)));

        hojaConsulta.setFontanelaAbombada(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbFTASCBSintoma), findViewById(R.id.chkbFTANCBSintoma)));

        hojaConsulta.setIctericiaConuntival(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbITCSCBSintoma), findViewById(R.id.chkbITCNCBSintoma)));

        return hojaConsulta;
    }

    /***
     * Metodo para validar si los datos han sufrido cambios.
     * @param hojaConsulta
     * @return
     */
    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if((mCorrienteCabezaSintomas.getCefalea() == null && hojaConsulta.getCefalea() != null) ||
                (mCorrienteCabezaSintomas.getDolorRetroocular() == null && hojaConsulta.getDolorRetroocular() != null) ||
                (mCorrienteCabezaSintomas.getFontanelaAbombada() == null && hojaConsulta.getFontanelaAbombada()!= null) ||
                (mCorrienteCabezaSintomas.getHemorragiaSuconjuntival() == null && hojaConsulta.getHemorragiaSuconjuntival() != null) ||
                (mCorrienteCabezaSintomas.getIctericiaConuntival() == null && hojaConsulta.getIctericiaConuntival() != null) ||
                (mCorrienteCabezaSintomas.getInyeccionConjuntival() == null && hojaConsulta.getInyeccionConjuntival() != null) ||
                (mCorrienteCabezaSintomas.getRigidezCuello() == null && hojaConsulta.getRigidezCuello() != null)) {
            return true;
        }

        if((mCorrienteCabezaSintomas.getCefalea() != null && hojaConsulta.getCefalea() == null) ||
                (mCorrienteCabezaSintomas.getDolorRetroocular() != null && hojaConsulta.getDolorRetroocular() == null) ||
                (mCorrienteCabezaSintomas.getFontanelaAbombada() != null && hojaConsulta.getFontanelaAbombada() == null) ||
                (mCorrienteCabezaSintomas.getHemorragiaSuconjuntival() != null && hojaConsulta.getHemorragiaSuconjuntival() == null) ||
                (mCorrienteCabezaSintomas.getIctericiaConuntival() != null && hojaConsulta.getIctericiaConuntival() == null) ||
                (mCorrienteCabezaSintomas.getInyeccionConjuntival() != null && hojaConsulta.getInyeccionConjuntival() == null) ||
                (mCorrienteCabezaSintomas.getRigidezCuello() != null && hojaConsulta.getRigidezCuello() == null)) {
            return true;
        }

        if(mCorrienteCabezaSintomas.getCefalea().charValue() != hojaConsulta.getCefalea().charValue() ||
                mCorrienteCabezaSintomas.getDolorRetroocular().charValue() != hojaConsulta.getDolorRetroocular().charValue() ||
                mCorrienteCabezaSintomas.getFontanelaAbombada().charValue() != hojaConsulta.getFontanelaAbombada().charValue() ||
                mCorrienteCabezaSintomas.getHemorragiaSuconjuntival().charValue() != hojaConsulta.getHemorragiaSuconjuntival().charValue() ||
                mCorrienteCabezaSintomas.getIctericiaConuntival().charValue() != hojaConsulta.getIctericiaConuntival().charValue() ||
                mCorrienteCabezaSintomas.getInyeccionConjuntival().charValue() != hojaConsulta.getInyeccionConjuntival().charValue() ||
                mCorrienteCabezaSintomas.getRigidezCuello().charValue() != hojaConsulta.getRigidezCuello().charValue()) {
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
     * Metodo que realiza el llamdo del servicio que guarda la Hoja de consulta para la informacion Cabeza.
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

                    RESPUESTA = SINTOMASWS.guardarCabezaSintomas(mHojaConsulta, mUsuarioLogiado);
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
        guardarTask.execute((Void[]) null);
    }

    private void obtenerValorGuardadoServicio(){
        AsyncTask<Void, Void, Void> lecturaTask = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ResultadoObjectWSDTO<CabezaSintomasDTO> RESPUESTA;
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
                RESPUESTA = new ResultadoObjectWSDTO<CabezaSintomasDTO>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerCabezaSintomas(hojaConsulta);
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
                    mCorrienteCabezaSintomas = RESPUESTA.getObjecRespuesta();
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
                CabezaSintomasDTO cabezaSintomas = RESPUESTA.getObjecRespuesta();

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbCAFSCBSintoma), findViewById(R.id.chkbCAFNCBSintoma),
                        findViewById(R.id.chkbCAFDCBSintoma), ((cabezaSintomas.getCefalea() != null)
                                ? cabezaSintomas.getCefalea().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRGCSCBSintoma), findViewById(R.id.chkbRGCNCBSintoma),
                        ((cabezaSintomas.getRigidezCuello() != null)
                            ? cabezaSintomas.getRigidezCuello().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbINCSCBSintoma), findViewById(R.id.chkbINCNCBSintoma),
                        ((cabezaSintomas.getInyeccionConjuntival() != null)
                                ? cabezaSintomas.getInyeccionConjuntival().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbHSCSCBSintoma), findViewById(R.id.chkbHSCNCBSintoma),
                        ((cabezaSintomas.getHemorragiaSuconjuntival() != null)
                                ? cabezaSintomas.getHemorragiaSuconjuntival().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbDROSCBSintoma), findViewById(R.id.chkbDRONCBSintoma),
                        findViewById(R.id.chkbDRODCBSintoma), ((cabezaSintomas.getDolorRetroocular() != null)
                                ? cabezaSintomas.getDolorRetroocular().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbFTASCBSintoma), findViewById(R.id.chkbFTANCBSintoma),
                        ((cabezaSintomas.getFontanelaAbombada() != null)
                                ? cabezaSintomas.getFontanelaAbombada().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbITCSCBSintoma), findViewById(R.id.chkbITCNCBSintoma),
                        ((cabezaSintomas.getIctericiaConuntival() != null)
                                ? cabezaSintomas.getIctericiaConuntival().charValue() : '4'));
            }
        };
        lecturaTask.execute((Void[])null);
    }
}
