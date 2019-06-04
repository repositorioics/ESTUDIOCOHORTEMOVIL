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
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.CatalogoDTO;
import com.sts_ni.estudiocohortecssfv.dto.ControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.EstadoNutricionalSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.DecimalDigitsInputFilter;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.ControlCambiosWS;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Controlador de la UI Estado Nutricional.
 */
public class EstadoNutriSintomasAcitvity extends ActionBarActivity {

    private Context CONTEXT;
    public boolean enTerreno = false;
    GeneralesControlCambiosDTO genControlCambios = null;
    private String vFueraRango = new String();
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private EstadoNutricionalSintomasDTO mCorrienteEstadoNutricionalSintomas;
    private String mUsuarioLogiado;
    ArrayAdapter<CatalogoDTO> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_nutri_sintoma);

        this.CONTEXT = this;

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        double pesoKg = Double.parseDouble(getIntent().getStringExtra("pesoKgPaciente"));
        double tallaCmPaciente = Double.parseDouble(getIntent().getStringExtra("tallaCmPaciente"));

        double imc = (pesoKg > 0 && tallaCmPaciente > 0) ? pesoKg / Math.pow((tallaCmPaciente/100.00), 2) : 00.00;

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.00", otherSymbols);

        ((EditText)findViewById(R.id.edtxtimc)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5,2)});
        ((EditText)findViewById(R.id.edtxtimc)).setText((imc > 0) ? df.format(imc) : "0.00");

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();
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

    private void llenarSpinnerEstdoNutricional() {
        ArrayList<CatalogoDTO>  estadosNutriconales = new ArrayList<CatalogoDTO>();
        estadosNutriconales.add(new CatalogoDTO(0, "Seleccione"));
        estadosNutriconales.add(new CatalogoDTO(1, "Obeso"));
        estadosNutriconales.add(new CatalogoDTO(2, "Sobrepeso"));
        estadosNutriconales.add(new CatalogoDTO(3, "Sospecha de Problema"));
        estadosNutriconales.add(new CatalogoDTO(4, "Normal"));
        estadosNutriconales.add(new CatalogoDTO(5, "Bajo Peso"));
        estadosNutriconales.add(new CatalogoDTO(6, "Bajo Peso Severo"));
        estadosNutriconales.add(new CatalogoDTO(7, "Desconocido"));
        Spinner spnEstadoNutri = (Spinner) findViewById(R.id.spnEstadoNutri);
        adapter = new ArrayAdapter<CatalogoDTO>(CONTEXT, R.layout.simple_spinner_dropdown_item,  estadosNutriconales);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEstadoNutri.setAdapter(adapter);
    }

    /*public void todosSUnCheck(){
        ((CheckBox) findViewById(R.id.chkbOBSSENSintoma)).setChecked(false);
        ((CheckBox) findViewById(R.id.chkbSBPSENSintoma)).setChecked(false);
        ((CheckBox) findViewById(R.id.chkbSDPSENSintoma)).setChecked(false);
        ((CheckBox) findViewById(R.id.chkbNRMSENSintoma)).setChecked(false);
        ((CheckBox) findViewById(R.id.chkbBJPSENSintoma)).setChecked(false);
        ((CheckBox) findViewById(R.id.chkbBPSSENSintoma)).setChecked(false);
    }

    public void todosN(boolean seleccionado){
        ((CheckBox) findViewById(R.id.chkbOBSNENSintoma)).setChecked(seleccionado);
        ((CheckBox) findViewById(R.id.chkbSBPNENSintoma)).setChecked(seleccionado);
        ((CheckBox) findViewById(R.id.chkbSDPNENSintoma)).setChecked(seleccionado);
        ((CheckBox) findViewById(R.id.chkbNRMNENSintoma)).setChecked(seleccionado);
        ((CheckBox) findViewById(R.id.chkbBJPNENSintoma)).setChecked(seleccionado);
        ((CheckBox) findViewById(R.id.chkbBPSNENSintoma)).setChecked(seleccionado);
        if(!seleccionado) {
            todosD(false);
        }
        todosSUnCheck();
    }

    public void todosD(boolean seleccionado){
        ((CheckBox) findViewById(R.id.chkbOBSDENSintoma)).setChecked(seleccionado);
        ((CheckBox) findViewById(R.id.chkbSBPDENSintoma)).setChecked(seleccionado);
        ((CheckBox) findViewById(R.id.chkbSDPDENSintoma)).setChecked(seleccionado);
        ((CheckBox) findViewById(R.id.chkbNRMDENSintoma)).setChecked(seleccionado);
        ((CheckBox) findViewById(R.id.chkbBJPDENSintoma)).setChecked(seleccionado);
        ((CheckBox) findViewById(R.id.chkbBPSDENSintoma)).setChecked(seleccionado);
        if(!seleccionado) {
            todosN(false);
        }
        todosSUnCheck();
    }

    public void onChkboxClickedOBSEN(View view) {
        if(view.getId() == R.id.chkbOBSSENSintoma) {
            if(enTerreno){
                todosD(true);
            }else {
                todosN(true);
            }
            ((CheckBox) view).setChecked(true);
        }
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbOBSSENSintoma), findViewById(R.id.chkbOBSNENSintoma),
                findViewById(R.id.chkbOBSDENSintoma), view);
    }

    public void onChkboxClickedSBPEN(View view) {
        if(view.getId() == R.id.chkbSBPSENSintoma) {
            if(enTerreno){
                todosD(true);
            }else {
                todosN(true);
            }
            ((CheckBox) view).setChecked(true);
        }
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbSBPSENSintoma), findViewById(R.id.chkbSBPNENSintoma),
                findViewById(R.id.chkbSBPDENSintoma), view);
    }

    public void onChkboxClickedSDPEN(View view) {
        if(view.getId() == R.id.chkbSDPSENSintoma) {
            if(enTerreno){
                todosD(true);
            }else {
                todosN(true);
            }
            ((CheckBox) view).setChecked(true);
        }
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbSDPSENSintoma), findViewById(R.id.chkbSDPNENSintoma),
                findViewById(R.id.chkbSDPDENSintoma), view);
    }

    public void onChkboxClickedNRMEN(View view) {
        if(view.getId() == R.id.chkbNRMSENSintoma) {
            if(enTerreno){
                todosD(true);
            }else {
                todosN(true);
            }
            ((CheckBox) view).setChecked(true);
        }
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbNRMSENSintoma), findViewById(R.id.chkbNRMNENSintoma),
                findViewById(R.id.chkbNRMDENSintoma), view);
    }

    public void onChkboxClickedBJPEN(View view) {
        if(view.getId() == R.id.chkbBJPSENSintoma) {
            if(enTerreno){
                todosD(true);
            }else {
                todosN(true);
            }
            ((CheckBox) view).setChecked(true);
        }
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbBJPSENSintoma), findViewById(R.id.chkbBJPNENSintoma),
                findViewById(R.id.chkbBJPDENSintoma), view);
    }

    public void onChkboxClickedBPSEN(View view) {
        if(view.getId() == R.id.chkbBPSSENSintoma) {
            if(enTerreno){
                todosD(true);
            }else {
                todosN(true);
            }
            ((CheckBox) view).setChecked(true);
        }
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbBPSSENSintoma), findViewById(R.id.chkbBPSNENSintoma),
                findViewById(R.id.chkbBPSDENSintoma), view);
    }*/

    public void onClick_btnRegresar(View view) {
        try{
            ArrayList<ControlCambiosDTO> controlCambios = new ArrayList<>();
            genControlCambios = null;

            vFueraRango = "";
            if (validarCampos(controlCambios)) {
                if( controlCambios.size() > 0){
                    genControlCambios = new GeneralesControlCambiosDTO();
                    genControlCambios.setUsuario(((CssfvApp) this.getApplication()).getInfoSessionWSDTO().getUser());
                    genControlCambios.setControlador(this.getLocalClassName());
                    genControlCambios.setControlCambios(controlCambios);

                    DialogInterface.OnClickListener preguntaEnviarDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    enviarHojaConsulta();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    MensajesHelper.mostrarMensajeYesNo(this, String.format(
                            getResources().getString(
                                    R.string.msj_aviso_control_cambios), vFueraRango), getResources().getString(
                            R.string.title_estudio_sostenible),preguntaEnviarDialogClickListener);
                } else {
                    enviarHojaConsulta();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void enviarHojaConsulta() {
        mHojaConsulta = cargarHojaConsulta();
        if (mPacienteSeleccionado.getCodigoEstado() == '7') {
            if(mCorrienteEstadoNutricionalSintomas != null) {
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
    }

    public boolean validarCampos(ArrayList<ControlCambiosDTO> controlCambios) throws Exception {

       /* if(StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtimc)).getText().toString())){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbOBSSENSintoma), findViewById(R.id.chkbOBSNENSintoma),
                findViewById(R.id.chkbOBSDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbSBPSENSintoma), findViewById(R.id.chkbSBPNENSintoma),
                findViewById(R.id.chkbSBPDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbSDPSENSintoma), findViewById(R.id.chkbSDPNENSintoma),
                findViewById(R.id.chkbSDPDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbNRMSENSintoma), findViewById(R.id.chkbNRMNENSintoma),
                findViewById(R.id.chkbNRMDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbBJPSENSintoma), findViewById(R.id.chkbBJPNENSintoma),
                findViewById(R.id.chkbBJPDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbBPSSENSintoma), findViewById(R.id.chkbBPSNENSintoma),
                findViewById(R.id.chkbBPSDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }*/

        if(((Spinner)findViewById(R.id.spnEstadoNutri)).getSelectedItem() == null ||
                ((CatalogoDTO)((Spinner)findViewById(R.id.spnEstadoNutri)).getSelectedItem()).getId() == 0) {
            throw new Exception(getString(R.string.msj_completar_informacion) + ", debe seleccionar un estado nutricional.");
        }

        // Aumento en el rango imc valorAnterior = 50, valorActual = 70
        if (!estaEnRango(5, 70, ((EditText) findViewById(R.id.edtxtimc)).getText().toString())) {
            throw new Exception(getString(R.string.msj_aviso_control_cambios2, getString(R.string.label_imc)));
           /* vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_imc));
            ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_imc));
            ctrCambios.setValorCampo(((EditText) findViewById(R.id.edtxtimc)).getText().toString());
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/
        }

        return true;
    }

    private boolean estaEnRango(double min, double max, String valor) {
        double valorComparar = Double.parseDouble(valor);
        return (valorComparar >= min && valorComparar <= max) ? true : false;
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setImc(Double.parseDouble(((EditText)findViewById(R.id.edtxtimc)).getText().toString()));

        hojaConsulta.setObeso(obtenerValor(1));

        hojaConsulta.setSobrepeso(obtenerValor(2));

        hojaConsulta.setSospechaProblema(obtenerValor(3));

        hojaConsulta.setNormal(obtenerValor(4));

        hojaConsulta.setBajoPeso(obtenerValor(5));

        hojaConsulta.setBajoPesoSevero(obtenerValor(6));

        return hojaConsulta;
    }

    private char obtenerValor(int id) {
        return  (((CatalogoDTO)((Spinner)findViewById(R.id.spnEstadoNutri)).getSelectedItem()).getId() == id) ? '0' :
                (((CatalogoDTO)((Spinner)findViewById(R.id.spnEstadoNutri)).getSelectedItem()).getId() != 7) ? '1' :'2';
    }

    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if((mCorrienteEstadoNutricionalSintomas.getBajoPeso() == null && hojaConsulta.getBajoPeso() != null) ||
                (mCorrienteEstadoNutricionalSintomas.getBajoPesoSevero() == null && hojaConsulta.getBajoPesoSevero() != null) ||
                (mCorrienteEstadoNutricionalSintomas.getImc() == null && hojaConsulta.getImc()!= null) ||
                (mCorrienteEstadoNutricionalSintomas.getNormal() == null && hojaConsulta.getNormal() != null) ||
                (mCorrienteEstadoNutricionalSintomas.getObeso() == null && hojaConsulta.getObeso() != null) ||
                (mCorrienteEstadoNutricionalSintomas.getSobrepeso() == null && hojaConsulta.getSobrepeso() != null) ||
                (mCorrienteEstadoNutricionalSintomas.getSospechaProblema() == null && hojaConsulta.getSospechaProblema() != null)) {
            return true;
        }

        if((mCorrienteEstadoNutricionalSintomas.getBajoPeso() != null && hojaConsulta.getBajoPeso() == null) ||
                (mCorrienteEstadoNutricionalSintomas.getBajoPesoSevero() != null && hojaConsulta.getBajoPesoSevero() == null) ||
                (mCorrienteEstadoNutricionalSintomas.getImc() != null && hojaConsulta.getImc() == null) ||
                (mCorrienteEstadoNutricionalSintomas.getNormal() != null && hojaConsulta.getNormal() == null) ||
                (mCorrienteEstadoNutricionalSintomas.getObeso() != null && hojaConsulta.getObeso() == null) ||
                (mCorrienteEstadoNutricionalSintomas.getSobrepeso() != null && hojaConsulta.getSobrepeso() == null) ||
                (mCorrienteEstadoNutricionalSintomas.getSospechaProblema() != null && hojaConsulta.getSospechaProblema() == null)) {
            return true;
        }

        if(mCorrienteEstadoNutricionalSintomas.getBajoPeso().charValue() != hojaConsulta.getBajoPeso().charValue() ||
                mCorrienteEstadoNutricionalSintomas.getBajoPesoSevero().charValue() != hojaConsulta.getBajoPesoSevero().charValue() ||
                mCorrienteEstadoNutricionalSintomas.getImc() != hojaConsulta.getImc() ||
                mCorrienteEstadoNutricionalSintomas.getNormal().charValue() != hojaConsulta.getNormal().charValue() ||
                mCorrienteEstadoNutricionalSintomas.getObeso().charValue() != hojaConsulta.getObeso().charValue() ||
                mCorrienteEstadoNutricionalSintomas.getSobrepeso().charValue() != hojaConsulta.getSobrepeso().charValue() ||
                mCorrienteEstadoNutricionalSintomas.getSospechaProblema().charValue() != hojaConsulta.getSospechaProblema().charValue()) {
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
            private ControlCambiosWS CTRCAMBIOSWS = new ControlCambiosWS(getResources());

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
                    if(genControlCambios != null) {
                        genControlCambios.setCodExpediente(mPacienteSeleccionado.getCodExpediente());
                        genControlCambios.setNumHojaConsulta(Integer.parseInt(mPacienteSeleccionado.getNumHojaConsulta()));
                        CTRCAMBIOSWS.guardarControlCambios(genControlCambios);
                    }
                    RESPUESTA = SINTOMASWS.guardarEstadoNutriSintomas(mHojaConsulta, mUsuarioLogiado);
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
            private ResultadoObjectWSDTO<EstadoNutricionalSintomasDTO> RESPUESTA;
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
                RESPUESTA = new ResultadoObjectWSDTO<EstadoNutricionalSintomasDTO>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerEstadoNutricionalSintomas(hojaConsulta);
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
                    mCorrienteEstadoNutricionalSintomas = RESPUESTA.getObjecRespuesta();
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
                EstadoNutricionalSintomasDTO estadoNutricionalSintomas = RESPUESTA.getObjecRespuesta();

                llenarSpinnerEstdoNutricional();

                if(!StringUtils.isNullOrEmpty(estadoNutricionalSintomas.getLugarAtencion()) && estadoNutricionalSintomas.getLugarAtencion().equalsIgnoreCase("Terreno")){
                    enTerreno = true;
                }

                if(estadoNutricionalSintomas.getImc() != null) {
                    ((EditText) findViewById(R.id.edtxtimc)).setText(estadoNutricionalSintomas.getImc().toString());
                }

                if(estadoNutricionalSintomas.getObeso() != null &&
                        estadoNutricionalSintomas.getObeso().compareTo('0') == 0) {
                    ((Spinner)findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(1)));
                } else if(estadoNutricionalSintomas.getSobrepeso() != null &&
                        estadoNutricionalSintomas.getSobrepeso().compareTo('0') == 0) {
                    ((Spinner)findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(2)));
                } else if(estadoNutricionalSintomas.getSospechaProblema() != null &&
                        estadoNutricionalSintomas.getSospechaProblema().compareTo('0') == 0) {
                    ((Spinner)findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(3)));
                } else if(estadoNutricionalSintomas.getNormal() != null &&
                        estadoNutricionalSintomas.getNormal().compareTo('0') == 0) {
                    ((Spinner)findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(4)));
                } else if(estadoNutricionalSintomas.getBajoPeso() != null &&
                        estadoNutricionalSintomas.getBajoPeso().compareTo('0') == 0) {
                    ((Spinner)findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(5)));
                } else if(estadoNutricionalSintomas.getBajoPesoSevero() != null &&
                        estadoNutricionalSintomas.getBajoPesoSevero().compareTo('0') == 0) {
                    ((Spinner)findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(6)));
                } else if(estadoNutricionalSintomas.getObeso() != null &&
                        estadoNutricionalSintomas.getObeso().compareTo('2') == 0) {
                    ((Spinner)findViewById(R.id.spnEstadoNutri)).setSelection(adapter.getPosition(adapter.getItem(7)));
                }


             /*   AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbSBPSENSintoma),
                        findViewById(R.id.chkbSBPNENSintoma), findViewById(R.id.chkbSBPNENSintoma),
                        ((estadoNutricionalSintomas.() != null)
                                ? estadoNutricionalSintomas.getSobrepeso().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbSDPSENSintoma),
                        findViewById(R.id.chkbSDPNENSintoma), findViewById(R.id.chkbSDPDENSintoma),
                        ((estadoNutricionalSintomas.getSospechaProblema() != null)
                                ? estadoNutricionalSintomas.getSospechaProblema().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbNRMSENSintoma),
                        findViewById(R.id.chkbNRMNENSintoma), findViewById(R.id.chkbNRMDENSintoma),
                        ((estadoNutricionalSintomas.getNormal() != null)
                                ? estadoNutricionalSintomas.getNormal().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbBJPSENSintoma),
                        findViewById(R.id.chkbBJPNENSintoma), findViewById(R.id.chkbBJPDENSintoma),
                        ((estadoNutricionalSintomas.getBajoPeso() != null)
                                ? estadoNutricionalSintomas.getBajoPeso().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbBPSSENSintoma),
                        findViewById(R.id.chkbBPSNENSintoma), findViewById(R.id.chkbBPSDENSintoma),
                        ((estadoNutricionalSintomas.getBajoPesoSevero() != null)
                                ? estadoNutricionalSintomas.getBajoPesoSevero().charValue() : '4'));*/

            }
        };
        lecturaTask.execute((Void[])null);
    }
}
