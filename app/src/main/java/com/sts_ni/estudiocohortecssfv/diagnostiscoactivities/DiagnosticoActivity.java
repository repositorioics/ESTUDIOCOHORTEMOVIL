package com.sts_ni.estudiocohortecssfv.diagnostiscoactivities;

import android.app.Activity;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.DiagnosticoDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.LstViewGenericaInicio;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.DiagnosticoWS;

import java.util.ArrayList;

/**
 * Controlador de la UI Diagnostico.
 */
public class DiagnosticoActivity extends ActionBarActivity
{

    private ListView LSTV_LISTA_DIAGNOSTICO;

    private  ResultadoListWSDTO<DiagnosticoDTO> ARR_DIAGNOSTICO;

    public Context CONTEXT;
    private ResultadoListWSDTO<DiagnosticoDTO> RESPUESTADIAG;
    private ResultadoListWSDTO<HojaConsultaDTO> RESPUESTA;

    public Activity ACTIVITY;
    public Integer  SEC_HOJA_CONSULTA ;
    InicioDTO pacienteSeleccionado;
    ArrayAdapter<DiagnosticoDTO> adapter;

    private HojaConsultaDTO hojaconsulta;
    private String otroDiagnostico;
    private String mUsuarioLogiado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostico);



        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());
        this.CONTEXT = this;

        this.ACTIVITY = this;

        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        pacienteSeleccionado = (InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado");
        SEC_HOJA_CONSULTA=pacienteSeleccionado.getIdObjeto();

        llamadoListaDiagnosticoServicio();
        cargarDatosDiagnostico();
    }

    /***
     * Metodo para cargar la informacion en la pantalla.
     * @return
     */
    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
        Integer diagnosticoId =  ((DiagnosticoDTO) ((Spinner)findViewById(R.id.spnDialogo1)).getSelectedItem()).getSecDiagnostico();
        hojaConsulta.setDiagnostico1( Short.parseShort( diagnosticoId.toString()));
         diagnosticoId =  ((DiagnosticoDTO) ((Spinner)findViewById(R.id.spnDialogo2)).getSelectedItem()).getSecDiagnostico();
        hojaConsulta.setDiagnostico2(Short.parseShort(diagnosticoId.toString()));
        diagnosticoId =  ((DiagnosticoDTO) ((Spinner)findViewById(R.id.spnDialogo3)).getSelectedItem()).getSecDiagnostico();
        hojaConsulta.setDiagnostico3(Short.parseShort(diagnosticoId.toString()));
        diagnosticoId =  ((DiagnosticoDTO) ((Spinner)findViewById(R.id.spnDialogo4)).getSelectedItem()).getSecDiagnostico();
        hojaConsulta.setDiagnostico4(Short.parseShort(diagnosticoId.toString()));
        hojaConsulta.setOtroDiagnostico(( (EditText) findViewById(R.id.edtxtOtrosDiagnostico)).getText().toString());
        return hojaConsulta;
    }


    /***
     * Metodo para cargar información guardado en Diagnostico.
     */
    private void cargarDatosDiagnostico(){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> listaInicioServicio = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();

            private ConsultaWS CONSULTAWS = new ConsultaWS(getResources());

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
                if (NET_INFO != null && NET_INFO.isConnected()){
                    RESPUESTA = CONSULTAWS.getHojaConsultaPorNumero(SEC_HOJA_CONSULTA);


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
                    try {

                        for (HojaConsultaDTO HojaConsultadto : RESPUESTA.getLstResultado()) {

                            ((EditText) ACTIVITY.findViewById(R.id.edtxtOtrosDiagnostico)).setText((HojaConsultadto.getOtroDiagnostico() == "null") ? "" : HojaConsultadto.getOtroDiagnostico());
                            otroDiagnostico = (HojaConsultadto.getOtroDiagnostico() == "null") ? "" : HojaConsultadto.getOtroDiagnostico();

                            for (DiagnosticoDTO DiagDto : RESPUESTADIAG.getLstResultado()) {
                                if (DiagDto.getSecDiagnostico() == HojaConsultadto.getDiagnostico1()) {
                                    ((Spinner) ACTIVITY.findViewById(R.id.spnDialogo1)).setSelection(adapter.getPosition(DiagDto));

                                }
                                if (DiagDto.getSecDiagnostico() == HojaConsultadto.getDiagnostico2()) {
                                    ((Spinner) ACTIVITY.findViewById(R.id.spnDialogo2)).setSelection(adapter.getPosition(DiagDto));

                                }
                                if (DiagDto.getSecDiagnostico() == HojaConsultadto.getDiagnostico3()) {
                                    ((Spinner) ACTIVITY.findViewById(R.id.spnDialogo3)).setSelection(adapter.getPosition(DiagDto));

                                }
                                if (DiagDto.getSecDiagnostico() == HojaConsultadto.getDiagnostico4()) {
                                    ((Spinner) ACTIVITY.findViewById(R.id.spnDialogo4)).setSelection(adapter.getPosition(DiagDto));

                                }


                            }


                        }

                    }
                   catch (Exception e)
                   {

                       MensajesHelper.mostrarMensajeInfo(CONTEXT,
                               e.getMessage(), getResources().getString(
                                       R.string.app_name), null);
                   }

                }else if (RESPUESTA.getCodigoError().intValue() != 999){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.app_name), null);

                }else{
                    MensajesHelper.mostrarMensajeError(CONTEXT,
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.app_name), null);
                }
            }
        };
        listaInicioServicio.execute((Void[])null);

    }

    /***
     * Metodo que realiza el llamdo de los datos para poblar el spinner de Diagnosticos
     */
    private void llamadoListaDiagnosticoServicio(){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> listaInicioServicio = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();

            private DiagnosticoWS DIAGNOSTICOWS = new DiagnosticoWS(getResources());

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
                if (NET_INFO != null && NET_INFO.isConnected()){
                    try
                    {

                        RESPUESTADIAG = DIAGNOSTICOWS.getListaDiagnostico();
                    }
                    catch (Exception e){
                        MensajesHelper.mostrarMensajeError(CONTEXT,
                                e.getMessage(), getResources().getString(
                                        R.string.app_name), null);

                    }

                    //  RESPUESTA = CONSULTAWS.getListaInicioConsultaPorExpediente(1);

                }else{
                    RESPUESTADIAG.setCodigoError(Long.parseLong("3"));
                    RESPUESTADIAG.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                PD.dismiss();
                if (RESPUESTADIAG.getCodigoError().intValue() == 0){
                      DiagnosticoDTO diagTO=new DiagnosticoDTO();
                     ArrayList<DiagnosticoDTO>  diag1= new  ArrayList<DiagnosticoDTO>();
                     diagTO.setSecDiagnostico(0);
                     diagTO.setDiagnostico("Seleccione Diagnostico");
                     diag1.add(diagTO);
                    diag1.addAll(RESPUESTADIAG.getLstResultado());
                    Spinner spnDialogo1 = (Spinner) findViewById(R.id.spnDialogo1);
                    Spinner spnDialogo2 = (Spinner) findViewById(R.id.spnDialogo2);
                    Spinner spnDialogo3 = (Spinner) findViewById(R.id.spnDialogo3);
                    Spinner spnDialogo4 = (Spinner) findViewById(R.id.spnDialogo4);
                    adapter = new ArrayAdapter<DiagnosticoDTO>(CONTEXT, R.layout.simple_spinner_dropdown_item,  diag1) {
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View v = super.getDropDownView(position, convertView,parent);
                            ((TextView) v).setGravity(Gravity.LEFT);
                            return v;
                        }
                    };
                    //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnDialogo1.setAdapter(adapter);
                    spnDialogo3.setAdapter(adapter);
                    spnDialogo2.setAdapter(adapter);
                    spnDialogo4.setAdapter(adapter);


                }else if (RESPUESTADIAG.getCodigoError().intValue() != 999){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            RESPUESTADIAG.getMensajeError(), getResources().getString(
                                    R.string.app_name), null);

                }else{
                    MensajesHelper.mostrarMensajeError(CONTEXT,
                            RESPUESTADIAG.getMensajeError(), getResources().getString(
                                    R.string.app_name), null);
                }
            }
        };
        listaInicioServicio.execute((Void[])null);

    }


    @Override
    public void onBackPressed() {
        return;
    }

    /***
     * Metodo para controlar el evento onClick del boton regresar.
     * @param view
     */
    public void onClick_btnDiagnostico( View view) {

        try {
            pacienteSeleccionado = (InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado");
            SEC_HOJA_CONSULTA = pacienteSeleccionado.getIdObjeto();
            hojaconsulta = cargarHojaConsulta();
            validarCampos();
            if (pacienteSeleccionado.getCodigoEstado() == '7') {
                if (tieneCambios()) {
                    DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    guardarDiagnostico(hojaconsulta);
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
                    guardarDiagnostico(hojaconsulta);
                }
            }else{
                guardarDiagnostico(hojaconsulta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String mensajeError = (StringUtils.isNullOrEmpty(e.getMessage()) ? getString(R.string.msj_error_no_controlado) :
                                    e.getMessage());
            MensajesHelper.mostrarMensajeError(this, mensajeError, getString(R.string.app_name), null);
        }
    }

    private void regresarPantallaAnterior() {
        Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
        intent.putExtra("indice", 2);
        intent.putExtra("pacienteSeleccionado", pacienteSeleccionado);
        intent.putExtra("cabeceraSintoma", ((CabeceraSintomaDTO) getIntent().getSerializableExtra("cabeceraSintoma")));
        startActivity(intent);
        finish();
    }

    private boolean tieneCambios(){
        String valor = ((EditText) findViewById(R.id.edtxtOtrosDiagnostico)).getText().toString();
        if((StringUtils.isNullOrEmpty(valor) && !StringUtils.isNullOrEmpty(otroDiagnostico)) ||
                (!StringUtils.isNullOrEmpty(valor) && StringUtils.isNullOrEmpty(otroDiagnostico)) ||
                !valor.equalsIgnoreCase(otroDiagnostico)){
            return true;
        }

        return false;
    }

    public void validarCampos() throws Exception {

        if(((Spinner)findViewById(R.id.spnDialogo1)).getSelectedItem() == null ||
                ((DiagnosticoDTO)((Spinner)findViewById(R.id.spnDialogo1)).getSelectedItem()).getSecDiagnostico() == 0) {
            throw new Exception(getString(R.string.msj_completar_informacion) + ", debe seleccionar un valor en el primer Diagnostico.");
        }
    }

    /***
     * Metodo que llama el servicio para guardar Diagnostico.
     */
    private void guardarDiagnostico(final HojaConsultaDTO hojaconsulta){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> HojaConsulta = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private DiagnosticoWS diagnosticoWS = new DiagnosticoWS(getResources());


            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(CONTEXT);
                PD.setTitle(getResources().getString(R.string.msj_mensaje_enviado));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){



                    RESPUESTA = diagnosticoWS.guardarDiagnostico(hojaconsulta, mUsuarioLogiado);
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


                    Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
                    intent.putExtra("indice",2);
                    intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);
                    intent.putExtra("cabeceraSintoma", ((CabeceraSintomaDTO)getIntent().getSerializableExtra("cabeceraSintoma")));
                    startActivity(intent);
                    finish();

                }else if (RESPUESTA.getCodigoError().intValue() != 999){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.app_name), null);

                }else{
                    MensajesHelper.mostrarMensajeError(CONTEXT,
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.app_name), null);
                }
            }
        };
        HojaConsulta.execute((Void[])null);
    }

}