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
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.EscuelaPacienteDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.MedicosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.DatePickerFragment;
import com.sts_ni.estudiocohortecssfv.tools.DialogBusquedaGenerica;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.ControlCambiosWS;
import com.sts_ni.estudiocohortecssfv.ws.DiagnosticoWS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controlador de la UI Proxima cita.
 */
public class DiagnosticoProximaCitaActivity  extends ActionBarActivity implements DialogBusquedaGenerica.DialogListener
{
    public Context CONTEXT;

    public  Activity ACTIVITY;
    private ResultadoListWSDTO<HojaConsultaDTO> RESPUESTA;
    public Integer  SEC_HOJA_CONSULTA ;
    private ResultadoObjectWSDTO<EscuelaPacienteDTO> RESPUESTAESCUELAPACIENTE;
    InicioDTO pacienteSeleccionado;
    private ProgressDialog PD;
    private ArrayAdapter<EscuelaPacienteDTO> adapter;
    private ObtenerEscuelaTask mObtenerEscuelaTask;

    private HojaConsultaDTO hojaconsulta;
    private HojaConsultaDTO iniHCDTO;
    private String mUsuarioLogiado;
    GeneralesControlCambiosDTO genControlCambios = null;
    private String vFueraRango = new String();
    private ArrayList<EscuelaPacienteDTO> mEscuelasPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostico_proximacita);

        final ActionBar actionBar = getSupportActionBar();
        pacienteSeleccionado = (InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado");
        SEC_HOJA_CONSULTA=pacienteSeleccionado.getIdObjeto();

        /*cargarPriximaCitaTask task = new cargarPriximaCitaTask(this, this);
        task.execute((Void) null);*/

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());
        this.CONTEXT = this;

        this.ACTIVITY = this;

        //Calendar
        findViewById(R.id.ibtCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogProximaCita();
            }
        });
        ((EditText)findViewById(R.id.dpProximaCita)).setKeyListener(null);

        llamarServicioObtenerEscuelas();
        //llamadoServicioObtenerDatospaciente();
        //cargarDatosProximaCita();

        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelarObtenerEscuelasTask();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    /****
     * Metodo que controla el click del boton buscar del Dialog de busqueda.
     * @param dialog, Objeto
     */
    @Override
    public void onDialogAceptClick(DialogFragment dialog) {

        EditText edtxtBuscar = (EditText) dialog.getDialog().findViewById(R.id.etxtBuscar);
        Toast.makeText(dialog.getActivity(), "Buscando el colegio: " + edtxtBuscar.getText(), Toast.LENGTH_LONG).show();
        String busqueda = (edtxtBuscar.getText() != null) ? edtxtBuscar.getText().toString() : null;
        filtrarSpinnerEscuelaPaciente(busqueda);
    }

    public void onClick_btnProximaCita( View view){
        try {

            ArrayList<ControlCambiosDTO> controlCambios = new ArrayList<ControlCambiosDTO>();
            genControlCambios = null;

            vFueraRango = "";
            validarCampos(controlCambios);
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
            }  else {
                enviarHojaConsulta();
            }

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.app_name), null);
        }
    }

    public void onClick_btnBuscarEscuelaPaciente(View view) {
        DialogBusquedaGenerica DlogBuscar = DialogBusquedaGenerica.newInstance(getString(R.string.label_titulo_busqueda_escuela));
        DlogBuscar.show(getSupportFragmentManager(), "Buscar");
    }

    private void enviarHojaConsulta() {
        pacienteSeleccionado = (InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado");
        SEC_HOJA_CONSULTA = pacienteSeleccionado.getIdObjeto();
        hojaconsulta = cargarHojaConsulta();

        if (pacienteSeleccionado.getCodigoEstado() == '7') {
            if(iniHCDTO != null) {
                if (tieneCambios(hojaconsulta)) {
                    DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    guardarProximaCita(hojaconsulta);
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
                    guardarProximaCita(hojaconsulta);
                }
            }else{
                guardarProximaCita(hojaconsulta);
            }
        }else {
            guardarProximaCita(hojaconsulta);
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

    private boolean tieneCambios(HojaConsultaDTO hojaconsulta){
        if((iniHCDTO.getHorarioClases() != null && hojaconsulta.getHorarioClases() == null) ||
                (iniHCDTO.getTelef() != null && hojaconsulta.getTelef() == null) ||
                (iniHCDTO.getProximaCita() != null && hojaconsulta.getProximaCita() == null) )
        {
            return true;
        }

        if((iniHCDTO.getHorarioClases() == null && hojaconsulta.getHorarioClases() != null) ||
                (iniHCDTO.getTelef() == null && hojaconsulta.getTelef() != null) ||
                (iniHCDTO.getProximaCita() == null && hojaconsulta.getProximaCita() != null))
        {
            return true;
        }

        if(!iniHCDTO.getHorarioClases().equalsIgnoreCase(hojaconsulta.getHorarioClases()) ||
                iniHCDTO.getTelef() != hojaconsulta.getTelef() ||
                !iniHCDTO.getProximaCita().equalsIgnoreCase(hojaconsulta.getProximaCita()))
        {
            return true;
        }

        return false;
    }

    public void validarCampos(ArrayList<ControlCambiosDTO> controlCambios) throws Exception {

        /*if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkAM), findViewById(R.id.chkPm), findViewById(R.id.chkNA))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if( ( (EditText) findViewById(R.id.dpProximaCita) ).getText().toString().length()==0 ){
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
        else if( ( (EditText) findViewById(R.id.dpProximaCita) ).getText().toString().length()>0 ){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();

            if (!DateUtils.esMayorIgualFechaActual(( (EditText) findViewById(R.id.dpProximaCita) ).getText().toString()) ){
                throw new Exception("La fecha de la proxima cita debe ser mayor o igual a " + simpleDateFormat.format(c .getTime()));
            }
        }*/
        if (((EditText) findViewById(R.id.dpProximaCita)).getText().toString().length() == 0) {
            throw new Exception(getString(R.string.msj_debe_ingresar_proxima_cita));
        }else {
            Calendar fechaMesesDespues = GregorianCalendar.getInstance();
            fechaMesesDespues.add(Calendar.MONTH, 2);
            SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            EditText dpProximaCita = (EditText) findViewById(R.id.dpProximaCita);
            Date fechaProximaCita = formateador.parse(dpProximaCita.getText().toString());

            if (fechaProximaCita.after(fechaMesesDespues.getTime())) {
                throw new Exception(getString(R.string.msj_la_fecha_menor_dos_meses));
            }
        }

        if(!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtTelEme)).getText().toString())) {
            if (!validarTelefRegex(((EditText) findViewById(R.id.edtxtTelEme)).getText().toString())) {
                vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_TelefonoEmergencia));
                ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
                ctrCambios.setNombreCampo(getResources().getString(R.string.label_TelefonoEmergencia));
                ctrCambios.setValorCampo(((EditText) findViewById(R.id.edtxtTelEme)).getText().toString());
                ctrCambios.setTipoControl(ctrCambios.discrepancia);
                controlCambios.add(ctrCambios);
            }
        }

    }

    private boolean validarTelefRegex(String valor) {
        Pattern pattern = Pattern.compile("\\d{8}");
        Matcher matcher = pattern.matcher(valor);
        return matcher.matches();
    }

    public void cargarListaEscuela(ArrayList<EscuelaPacienteDTO> paramLstEscuela) {
        EscuelaPacienteDTO escuelaPaciente = new EscuelaPacienteDTO();
        ArrayList<EscuelaPacienteDTO> lstEscuelas= new ArrayList<>();
        escuelaPaciente.setCodEscuela((short) 0);
        escuelaPaciente.setDescripcion("Seleccione Colegio");
        lstEscuelas.add(escuelaPaciente);
        lstEscuelas.addAll(paramLstEscuela);
        Spinner spnEscuelas = (Spinner) findViewById(R.id.spnColegio);
        adapter = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item,  lstEscuelas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEscuelas.setAdapter(adapter);
        this.mEscuelasPaciente = lstEscuelas;
        cargarDatosProximaCita();
    }

    public void filtrarSpinnerEscuelaPaciente(final String busqueda) {
        ArrayList<EscuelaPacienteDTO> nuevaEscuelasPaciente = this.mEscuelasPaciente;
        if(!StringUtils.isNullOrEmpty(busqueda)) {
            nuevaEscuelasPaciente = new ArrayList<>(Collections2.filter(this.mEscuelasPaciente, new Predicate<EscuelaPacienteDTO>() {
                @Override
                public boolean apply(EscuelaPacienteDTO escuelaPaciente) {
                    return escuelaPaciente.getDescripcion().toLowerCase().contains(busqueda.toLowerCase());
                }
            }));
        }

        adapter = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item,  nuevaEscuelasPaciente);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.spnColegio)).setAdapter(adapter);
    }

    private void cancelarObtenerEscuelasTask() {
        if (mObtenerEscuelaTask != null && mObtenerEscuelaTask.getStatus() == UserTask.Status.RUNNING) {
            mObtenerEscuelaTask.cancel(true);
            mObtenerEscuelaTask = null;
        }
    }

    public void llamarServicioObtenerEscuelas() {
        if (mObtenerEscuelaTask == null || mObtenerEscuelaTask.getStatus() == ObtenerEscuelaTask.Status.FINISHED) {
            mObtenerEscuelaTask = (ObtenerEscuelaTask) new
                    ObtenerEscuelaTask(this).execute(null);
        }
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        String horario;
        if(((CheckBox) findViewById(R.id.chkAM)).isChecked())
            horario="M";
        else if (((CheckBox) findViewById(R.id.chkPm)).isChecked())
            horario="V";
        else
            horario="N";

        hojaConsulta.setHorarioClases(horario);
        if(!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtTelEme)).getText().toString())){
            hojaConsulta.setTelef(Long.parseLong(((EditText) findViewById(R.id.edtxtTelEme)).getText().toString()));
        }

        hojaConsulta.setProximaCita(((EditText) findViewById(R.id.dpProximaCita)).getText().toString());
        int codEscuela =  ((EscuelaPacienteDTO) ((Spinner)findViewById(R.id.spnColegio)).getSelectedItem()).getCodEscuela();
        if(codEscuela > 0)
            hojaConsulta.setColegio(String.valueOf(codEscuela));

        return hojaConsulta;
    }

    public void showDatePickerDialogProximaCita() {
        DialogFragment newFragment = new DatePickerFragment(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorIgualFechaActual(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()))){
                    ((EditText)getActivity().findViewById(R.id.dpProximaCita)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                    ((EditText) getActivity().findViewById(R.id.dpProximaCita)).setError(null);
                } else {
                    ((EditText)getActivity().findViewById(R.id.dpProximaCita)).setError(getString(R.string.msj_fecha_menor_hoy));
                    //MensajesHelper.mostrarMensajeError(getActivity(), getString(R.string.msj_fecha_mayor_hoy), getString(R.string.title_estudio_sostenible), null);
                    ((EditText) getActivity().findViewById(R.id.dpProximaCita)).setText("");
                    ((EditText) getActivity().findViewById(R.id.dpProximaCita)).requestFocus();
                }
            }
        };
        newFragment.show(getSupportFragmentManager(), getString(R.string.title_date_picker));
    }

    public void onChkboxClickedHorario(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkAM:
                if (checked) {
                    ((CheckBox) findViewById(R.id.chkPm)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkNA)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkPm:
                if (checked) {
                    ((CheckBox) findViewById(R.id.chkAM)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkNA)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkNA:
                if (checked) {
                    ((CheckBox) findViewById(R.id.chkAM)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkPm)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;

        }
    }

   /* private void llamadoServicioObtenerDatospaciente(){
        *//*Creando una tarea asincrona*//*
        AsyncTask<Void, Void, Void> obtenerDatosPaciente = new AsyncTask<Void, Void, Void>() {
            private ConnectivityManager CM = (ConnectivityManager) ACTIVITY.getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();

            private DiagnosticoWS DIAGNOSTICOWS = new DiagnosticoWS(getResources());

            @Override
            protected void onPreExecute() {
                if(ACTIVITY != null) {
                    if(PD != null && PD.isShowing()) {
                        PD.dismiss();
                    }
                    PD = new ProgressDialog(ACTIVITY);
                    PD.setTitle(getResources().getString(R.string.title_obteniendo));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){

                    RESPUESTAESCUELAPACIENTE = DIAGNOSTICOWS.obtenerDatosPaciente( ((InicioDTO) ACTIVITY.getIntent().getSerializableExtra("pacienteSeleccionado")).getCodExpediente());
                    *//*if(ACTIVITY != null
                            && ACTIVITY.getIntent() != null
                            && ACTIVITY.getIntent().getSerializableExtra("pacienteSeleccionado") != null) {
                    }*//*
                }else{
                    RESPUESTAESCUELAPACIENTE.setCodigoError(Long.parseLong("3"));
                    RESPUESTAESCUELAPACIENTE.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                if(PD != null && PD.isShowing()){
                    PD.dismiss();
                }
                if(RESPUESTAESCUELAPACIENTE != null
                        && RESPUESTAESCUELAPACIENTE.getCodigoError() != null) {
                    if (RESPUESTAESCUELAPACIENTE.getCodigoError().intValue() == 0) {
                        EscuelaPacienteDTO escuelaPaciente = new EscuelaPacienteDTO();
                        escuelaPaciente.setCodEscuela(RESPUESTAESCUELAPACIENTE.getObjecRespuesta().getCodEscuela());
                        escuelaPaciente.setDescripcion(RESPUESTAESCUELAPACIENTE.getObjecRespuesta().getDescripcion());
                        int posicion = adapter.getPosition(escuelaPaciente);
                        ((Spinner) findViewById(R.id.spnColegio)).setSelection(posicion);
                    } else if (RESPUESTAESCUELAPACIENTE.getCodigoError().intValue() != 999) {
                        MensajesHelper.mostrarMensajeInfo(ACTIVITY,
                                RESPUESTAESCUELAPACIENTE.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    } else {
                        MensajesHelper.mostrarMensajeError(ACTIVITY,
                                RESPUESTAESCUELAPACIENTE.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);
                    }
                }
            }
        };
        obtenerDatosPaciente.execute((Void[])null);
    }*/

    private void cargarDatosProximaCita(){
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

                    iniHCDTO = new HojaConsultaDTO();
                    for (HojaConsultaDTO HojaConsultadto: RESPUESTA.getLstResultado()) {

                        if(HojaConsultadto.getProximaCita() != null) {
                            ((EditText)ACTIVITY.findViewById(R.id.dpProximaCita)).setText((HojaConsultadto.getProximaCita()=="null")?"":HojaConsultadto.getProximaCita());
                            iniHCDTO.setProximaCita(((EditText)ACTIVITY.findViewById(R.id.dpProximaCita)).getText().toString());
                        }

                        if(HojaConsultadto.getTelef() != null){
                            ((EditText)ACTIVITY.findViewById(R.id.edtxtTelEme)).setText(HojaConsultadto.getTelef().toString());
                            iniHCDTO.setTelef(HojaConsultadto.getTelef());
                        }

                        if (HojaConsultadto.getHorarioClases() != null && HojaConsultadto.getHorarioClases().toString().trim().compareTo("M") == 0) {
                            ((CheckBox) ACTIVITY.findViewById(R.id.chkAM)).setChecked(true);
                            ((CheckBox) ACTIVITY.findViewById(R.id.chkPm)).setChecked(false);
                            ((CheckBox) ACTIVITY.findViewById(R.id.chkNA)).setChecked(false);
                            iniHCDTO.setHorarioClases("M");
                        }
                        else if (HojaConsultadto.getHorarioClases() != null && HojaConsultadto.getHorarioClases().toString().trim().compareTo("V") == 0)
                        {
                            ((CheckBox) ACTIVITY.findViewById(R.id.chkPm)).setChecked(true);
                            ((CheckBox) ACTIVITY.findViewById(R.id.chkAM)).setChecked(false);

                            ((CheckBox) ACTIVITY.findViewById(R.id.chkNA)).setChecked(false);
                            iniHCDTO.setHorarioClases("V");
                        }
                        else if(HojaConsultadto.getHorarioClases() != null && HojaConsultadto.getHorarioClases().toString().trim().compareTo("N") == 0)
                        {
                            ((CheckBox) ACTIVITY.findViewById(R.id.chkNA)).setChecked(true);
                            ((CheckBox) ACTIVITY.findViewById(R.id.chkPm)).setChecked(false);
                            ((CheckBox) ACTIVITY.findViewById(R.id.chkAM)).setChecked(false);
                            iniHCDTO.setHorarioClases("N");
                        }

                        if(!StringUtils.isNullOrEmpty(HojaConsultadto.getColegio()) &&
                                HojaConsultadto.getColegio().compareTo("null") != 0) {
                            EscuelaPacienteDTO colegioEncontrado = new EscuelaPacienteDTO();
                            for(int i = 0; i < adapter.getCount(); i++) {
                                colegioEncontrado = adapter.getItem(i);
                                if(colegioEncontrado.getCodEscuela() ==  Integer.parseInt(HojaConsultadto.getColegio().trim())) {
                                    break;
                                }
                            }
                            int posicion = adapter.getPosition(colegioEncontrado);
                            ((Spinner) findViewById(R.id.spnColegio)).setSelection(posicion);
                            iniHCDTO.setColegio(HojaConsultadto.getColegio());
                        }
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

    //funcion para guardar desde la pantalla de examenHistoria
    private void guardarProximaCita(final HojaConsultaDTO hojaconsulta){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> HojaConsulta = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private DiagnosticoWS diagnosticoWS = new DiagnosticoWS(getResources());
            private ControlCambiosWS CTRCAMBIOSWS = new ControlCambiosWS(getResources());

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

                    if(genControlCambios != null) {
                        genControlCambios.setCodExpediente(pacienteSeleccionado.getCodExpediente());
                        genControlCambios.setNumHojaConsulta(Integer.parseInt(pacienteSeleccionado.getNumHojaConsulta()));
                        CTRCAMBIOSWS.guardarControlCambios(genControlCambios);
                    }

                    RESPUESTA = diagnosticoWS.guardarProximaCita(hojaconsulta, mUsuarioLogiado);
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
        HojaConsulta.execute((Void[]) null);
    }
}