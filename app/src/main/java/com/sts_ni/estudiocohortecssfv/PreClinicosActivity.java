package com.sts_ni.estudiocohortecssfv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ComplementoCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.MedicosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.enfermeriatask.ObtenerDatosPreclinicosTask;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.BuscarDialogGenerico;
import com.sts_ni.estudiocohortecssfv.tools.BuscarDialogMedico;
import com.sts_ni.estudiocohortecssfv.tools.CancelacionDialog;
import com.sts_ni.estudiocohortecssfv.tools.DecimalDigitsInputFilter;
import com.sts_ni.estudiocohortecssfv.tools.LstViewGenericaInicio;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.ControlCambiosWS;
import com.sts_ni.estudiocohortecssfv.ws.EnfermeriaWS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Controlador de la pantalla Datos Preclinicos, Enfermeria.
 */
public class PreClinicosActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, CancelacionDialog.DialogListener, BuscarDialogMedico.DialogListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment M_NAVIGATION_DRAWER_FRAGMENT;

    public Context CONTEXT;

    public  Activity ACTIVITY;

    public AsyncTask<Void, Void, Void> mObtenerMedicosTask;

    public ObtenerDatosPreclinicosTask mObtenerDatosPreclinicosTask;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private static final String EDIT_TEXT_PESO_VALUE = "pesoKg";
    private static final String EDIT_TEXT_TALLA_VALUE = "tallaCm";
    private static final String EDIT_TEXT_TEMP_VALUE = "temperaturac";
    private static final String EDIT_TEXT_EXP_FISICO_VALUE = "expedienteFisico";
    private String VALOR_PESO_KG = new String();
    private String VALOR_TALLA = new String();
    private String VALOR_TEMP = new String();
    private String VALOR_EXP_FISICO = new String();
    public String nMedico;
    public boolean buscaMedico;

    GeneralesControlCambiosDTO genControlCambios = null;
    private String vFueraRango = new String();

    private ResultadoListWSDTO<MedicosDTO> RESPUESTADIAG;
    ArrayAdapter<MedicosDTO> adapter;

    static ComplementoCambiosDTO cCtrC = new ComplementoCambiosDTO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CONTEXT = this;
        ACTIVITY = this;
        setContentView(R.layout.activity_preclinicos);

        M_NAVIGATION_DRAWER_FRAGMENT = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        M_NAVIGATION_DRAWER_FRAGMENT.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.dwlPreClinicos));

        if (savedInstanceState != null){
           this.VALOR_PESO_KG = savedInstanceState.getString(this.EDIT_TEXT_PESO_VALUE);
           this.VALOR_TALLA = savedInstanceState.getString(this.EDIT_TEXT_TALLA_VALUE);
           this.VALOR_TEMP = savedInstanceState.getString(this.EDIT_TEXT_TEMP_VALUE);
           this.VALOR_EXP_FISICO = savedInstanceState.getString(this.EDIT_TEXT_EXP_FISICO_VALUE);
        }

        //Carga todos los médicos
        buscaMedico = false;
        llamadoListaMedicoServicio();

        InicioDTO pacienteSeleccionado = (InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado");

        if (pacienteSeleccionado.getCodigoEstado() != '1') {
            llamarObtenerDatosPreclinicosTask(pacienteSeleccionado.getIdObjeto());
        }
    }

    @Override
    protected void onStart () {
        super.onStart();
        //se carga el spinner
        MedicosDTO diagTO = new MedicosDTO();
        ArrayList<MedicosDTO>  diagMedico= new ArrayList<MedicosDTO>();
        diagTO.setIdMedico((short) 0);
        diagTO.setNombreMedico("Seleccione Medico");
        diagMedico.add(diagTO);
        Spinner spnMedico = (Spinner) findViewById(R.id.spnMedico);
        adapter = new ArrayAdapter<MedicosDTO>(CONTEXT, R.layout.simple_spinner_dropdown_item,  diagMedico);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMedico.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fmlPreClinicos, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(this.EDIT_TEXT_PESO_VALUE, ((EditText) findViewById(R.id.edtxtPeso)).getText().toString());
        outState.putString(this.EDIT_TEXT_TALLA_VALUE, ((EditText) findViewById(R.id.edtxtTalla)).getText().toString());
        outState.putString(this.EDIT_TEXT_TEMP_VALUE, ((EditText) findViewById(R.id.edtxtTemp)).getText().toString());
        outState.putString(this.EDIT_TEXT_EXP_FISICO_VALUE, ((EditText) findViewById(R.id.edtxtExpediente)).getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!M_NAVIGATION_DRAWER_FRAGMENT.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
           // getMenuInflater().inflate(R.menu.pantalla_inicio, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        EditText edtxtMotivo= (EditText)dialog.getDialog().findViewById(R.id.edtxtMotivo);

        if(!StringUtils.isNullOrEmpty(edtxtMotivo.getText().toString())) {
            this.enviandoDatosPreclinicosCanceladosServicio(edtxtMotivo.getText().toString());
        } else {
            MensajesHelper.mostrarMensajeError(CONTEXT, getResources().getString(
                    R.string.msj_aviso_requerido_motivo), getResources().getString(
                    R.string.title_estudio_sostenible), null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cancelarObtenerDatosPreclinicosTask();
        cancelarObtenerMedicoTask();
    }

    /***
     * Metodo que realiza el evento de click del botón Enviar.
     * @param view, Objeto
     */
    public void onClick_btnEnviar(View view) {
        ArrayList<ControlCambiosDTO> controlCambios = new ArrayList<ControlCambiosDTO>();
        genControlCambios = null;

        vFueraRango = "";
        if (validarCamposRequeridos(controlCambios)) {

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
                                mensajePrevioGuardar();
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
            }else{
                mensajePrevioGuardar();
            }

        }
    }

    /***
     * Metodo para ejecutar el mensaje de si esta seguro de enviar la hoja de consulta.
     */
    private void mensajePrevioGuardar(){
        DialogInterface.OnClickListener preguntaEnviarDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        enviandoDatosPreclinicosServicio();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        MensajesHelper.mostrarMensajeYesNo(this,
                getResources().getString(
                        R.string.msj_enviar_hoja_consulta),getResources().getString(
                        R.string.title_estudio_sostenible),preguntaEnviarDialogClickListener);
    }

    /***
     * Metodo que realiza el evento del onClick del botón Cancelar
     * @param view, Objeto.
     */
    public void onClick_btnCancel(View view){

        DialogInterface.OnClickListener preguntaCancelarDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        CancelacionDialog dlogCancelacion = new CancelacionDialog();
                        dlogCancelacion.show(getSupportFragmentManager(), "Cancelación");
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        MensajesHelper.mostrarMensajeYesNo(this,
                getResources().getString(
                        R.string.msj_esta_seguro_de_cancelar), getResources().getString(
                        R.string.title_estudio_sostenible), preguntaCancelarDialogClickListener);

    }

    /***
     * Metodo que ejecuta el evento onClick del botón No atiende llamado.
     * @param view, Objeto
     */
    public void onClick_btnNoAtiendeLlamado(View view){
        DialogInterface.OnClickListener noAtiendeLlamadoDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        DatosPreclinicosNoALLamadoServicio();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        MensajesHelper.mostrarMensajeYesNo(this,
                getResources().getString(
                        R.string.msj_paciente_no_atiende_llamado), getResources().getString(
                        R.string.title_estudio_sostenible), noAtiendeLlamadoDialogClickListener);
    }

    /***
     * Metodo para ejecutar el mensaje que se encuentra fuera de campo
     */
    private void mensajeFueraRango(){

    }

    /***
     * Metodo que valida los campos requeridos y fuera de Rango
     * @param controlCambios, Lista para almacenar los campos que se encuentran fuera de rango.
     * @return Falso si faltan campos requeridos, True si los datos se encuentran correcto.
     */
    private boolean validarCamposRequeridos(ArrayList<ControlCambiosDTO> controlCambios) {
        EditText edtxExpediente = (EditText) findViewById(R.id.edtxtExpediente);
        EditText edtxtPeso = (EditText) findViewById(R.id.edtxtPeso);
        EditText edtxtTalla = (EditText) findViewById(R.id.edtxtTalla);
        EditText edtxtTemp = (EditText) findViewById(R.id.edtxtTemp);
        EditText edtxtExpedienteFisico = (EditText) findViewById(R.id.edtxtExpediente);

        if (StringUtils.isNullOrEmpty(edtxExpediente.getText().toString())) {

            MensajesHelper.mostrarMensajeInfo(this,
                    getResources().getString(
                            R.string.msj_completar_informacion), getResources().getString(
                            R.string.title_estudio_sostenible), null);

            return false;
        }else if (StringUtils.isNullOrEmpty(edtxtPeso.getText().toString())) {

            MensajesHelper.mostrarMensajeInfo(this,
                    getResources().getString(
                            R.string.msj_completar_informacion),getResources().getString(
                            R.string.title_estudio_sostenible), null);
            return false;
        }  else if (StringUtils.isNullOrEmpty(edtxtTalla.getText().toString())) {

            MensajesHelper.mostrarMensajeInfo(this,
                    getResources().getString(
                            R.string.msj_completar_informacion), getResources().getString(
                            R.string.title_estudio_sostenible), null);

            return  false;
        } else if (StringUtils.isNullOrEmpty(edtxtTemp.getText().toString())) {

            MensajesHelper.mostrarMensajeInfo(this,
                    getResources().getString(
                            R.string.msj_completar_informacion), getResources().getString(
                            R.string.title_estudio_sostenible), null);
            return  false;
        } else if (StringUtils.isNullOrEmpty(edtxtExpedienteFisico.getText().toString())){
            MensajesHelper.mostrarMensajeInfo(this,
                    getResources().getString(
                                    R.string.msj_completar_informacion), getResources().getString(
                            R.string.title_estudio_sostenible), null);
            return false;
        }

        int cont = 0;
        //if (!estaEnRango(1, 100, edtxtPeso.getText().toString())) { // Rangos anteriores
        if (!estaEnRango(1, 200, edtxtPeso.getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_peso));
            cont++;
            /*ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_peso));
            ctrCambios.setValorCampo(edtxtPeso.getText().toString());
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/

        }

        //if (!estaEnRango(20, 200, edtxtTalla.getText().toString())) { // Rangos Anteriores
        if (!estaEnRango(20, 220, edtxtTalla.getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_talla));
            cont++;
           /* ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_talla));
            ctrCambios.setValorCampo(edtxtTalla.getText().toString());
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/
        }

        // if (!estaEnRango(34, 42, edtxtTemp.getText().toString())) { // Rangos Anteriores
        if (!estaEnRango(35.5, 41, edtxtTemp.getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_temp));
            cont++;
            /*ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_temp));
            ctrCambios.setValorCampo(edtxtTemp.getText().toString());
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/
        }

        if (cont >0){
            MensajesHelper.mostrarMensajeInfo(this, String.format(
                    getResources().getString(
                            R.string.msj_aviso_control_cambios1), vFueraRango), getResources().getString(
                    R.string.title_estudio_sostenible),null);
            return false;
        }

        return true;
    }

    private boolean estaEnRango(double min, double max, String valor) {
        double valorComparar = Double.parseDouble(valor);
        return (valorComparar >= min && valorComparar <= max) ? true : false;
    }

    /***
     * Metodo para llamar el servicio del enviar la hoja de consulta.
     */
    private void enviandoDatosPreclinicosServicio(){

        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
        //hojaConsulta.setCodExpediente(((InicioDTO) ACTIVITY.getIntent().getSerializableExtra("pacienteSeleccionado")).getCodExpediente());
        //hojaConsulta.setNumHojaConsulta(Integer.parseInt(((InicioDTO)ACTIVITY.getIntent().getSerializableExtra("pacienteSeleccionado")).getNumHojaConsulta()));
        hojaConsulta.setPesoKg(Double.parseDouble(((EditText) this.findViewById(R.id.edtxtPeso)).getText().toString()));
        hojaConsulta.setTallaCm(Double.parseDouble(((EditText) this.findViewById(R.id.edtxtTalla)).getText().toString()));
        hojaConsulta.setTemperaturac(Double.parseDouble(((EditText) this.findViewById(R.id.edtxtTemp)).getText().toString()));
        hojaConsulta.setUsuarioEnfermeria(((CssfvApp)this.getApplication()).getInfoSessionWSDTO().getUserId());
        hojaConsulta.setExpedienteFisico(((EditText) this.findViewById(R.id.edtxtExpediente)).getText().toString());
        hojaConsulta.setHorasv(((EditText) this.findViewById(R.id.edtxtHora)).getText().toString());

        short idMedico =  ((MedicosDTO) ((Spinner)findViewById(R.id.spnMedico)).getSelectedItem()).getIdMedico();
        if(idMedico > 0){
            hojaConsulta.setUsuarioMedico(idMedico);
        }

        /*Creando una tarea asincrona*/
        AsyncTask<Object, Void, Void> datosPreclinicos = new AsyncTask<Object, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private EnfermeriaWS ENFERMERIAWS = new EnfermeriaWS(getResources());
            private ControlCambiosWS CTRCAMBIOSWS = new ControlCambiosWS(getResources());

            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(CONTEXT);
                PD.setTitle(getResources().getString(R.string.tittle_enviando));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Object... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    if(genControlCambios != null) {
                        genControlCambios.setCodExpediente(cCtrC.getCodExpediente());
                        genControlCambios.setNumHojaConsulta(cCtrC.getNumHojaConsulta());
                        CTRCAMBIOSWS.guardarControlCambios(genControlCambios);
                    }
                    RESPUESTA = ENFERMERIAWS.enviarDatosPreclinicos((HojaConsultaDTO)params[0]);
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
                    Intent intent = new Intent(CONTEXT, InicioEnfermeriaActivity.class);
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
        datosPreclinicos.execute(hojaConsulta);
    }

    /***
     * Metodo para llamar el servicio de cancelar Hoja de consulta.
     * @param motivoCancelacion, Motivo de cancelación.
     */
    private void enviandoDatosPreclinicosCanceladosServicio(String motivoCancelacion){

        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        short idMedico =  ((MedicosDTO) ((Spinner)findViewById(R.id.spnMedico)).getSelectedItem()).getIdMedico();
        if(idMedico > 0){
            hojaConsulta.setUsuarioMedico(idMedico);
        }

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        double pesoKg = (((EditText) this.findViewById(R.id.edtxtPeso)).getText().toString().compareTo("") == 0) ? 0.00 : Double.parseDouble(((EditText) ACTIVITY.findViewById(R.id.edtxtPeso)).getText().toString());
        hojaConsulta.setPesoKg(pesoKg);
        double tallaCm = (((EditText) this.findViewById(R.id.edtxtTalla)).getText().toString().compareTo("") == 0) ? 0.00 : Double.parseDouble(((EditText) ACTIVITY.findViewById(R.id.edtxtTalla)).getText().toString());
        hojaConsulta.setTallaCm(tallaCm);
        double temperaturaC = (((EditText) this.findViewById(R.id.edtxtTemp)).getText().toString().compareTo("") == 0) ? 0.00 : Double.parseDouble(((EditText) ACTIVITY.findViewById(R.id.edtxtTemp)).getText().toString());
        hojaConsulta.setTemperaturac(temperaturaC);
        hojaConsulta.setUsuarioEnfermeria(((CssfvApp)this.getApplication()).getInfoSessionWSDTO().getUserId());
        hojaConsulta.setExpedienteFisico(((EditText)this.findViewById(R.id.edtxtExpediente)).getText().toString());
        hojaConsulta.setHorasv(((EditText)this.findViewById(R.id.edtxtHora)).getText().toString());

        /*Creando una tarea asincrona*/
        AsyncTask<Object, Void, Void> datosPreclinicos = new AsyncTask<Object, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private EnfermeriaWS ENFERMERIAWS = new EnfermeriaWS(getResources());

            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(CONTEXT);
                PD.setTitle(getResources().getString(R.string.tittle_cancelando));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Object... param) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    String motivo = param[1].toString();
                    RESPUESTA = ENFERMERIAWS.enviarDatosPreclinicosCancelados((HojaConsultaDTO)param[0], motivo);
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
                    Intent intent = new Intent(CONTEXT, InicioEnfermeriaActivity.class);
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
        datosPreclinicos.execute(hojaConsulta, motivoCancelacion);
    }

    /***
     * Metodo para realizar el llamado al servicio de No atiende llamado.
     */
    private void DatosPreclinicosNoALLamadoServicio(){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> datosPreclinicos = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private EnfermeriaWS ENFERMERIAWS = new EnfermeriaWS(getResources());

            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(CONTEXT);
                PD.setTitle(getResources().getString(R.string.tittle_cancelando));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
                    hojaConsulta.setSecHojaConsulta(((InicioDTO) ACTIVITY.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    hojaConsulta.setUsuarioEnfermeria(((CssfvApp)ACTIVITY.getApplication()).getInfoSessionWSDTO().getUserId());
                    RESPUESTA = ENFERMERIAWS.noAtiendeLlamadoDatosPreclinicos(hojaConsulta);
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
                    Intent intent = new Intent(CONTEXT, InicioEnfermeriaActivity.class);
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
        datosPreclinicos.execute((Void[])null);
    }

    /***
     * Metodo para llamar el servicio de cargar los datos preclinicos de Hoja de consultas
     * que no se encuentran en estado Enfermeria.
     * @param secHojaConsulta, Id Hoja Consulta
     */
    private void llamarObtenerDatosPreclinicosTask(Integer secHojaConsulta) {
        if (mObtenerDatosPreclinicosTask == null ||
                mObtenerDatosPreclinicosTask.getStatus() == ObtenerDatosPreclinicosTask.Status.FINISHED) {
            mObtenerDatosPreclinicosTask = (ObtenerDatosPreclinicosTask) new
                    ObtenerDatosPreclinicosTask(this).execute(secHojaConsulta);
        }
    }

    /***
     * Metodo para cargar la informaicion obtenida por el servicio en la UI.
     * @param datosPreclinicos, Datos de la Hoja de consulta.
     */
    public void cargarDatosUI(CabeceraSintomaDTO datosPreclinicos) {
        ((EditText)findViewById(R.id.edtxtPeso)).setText(datosPreclinicos.getPesoKg().toString());
        ((EditText)findViewById(R.id.edtxtTalla)).setText(datosPreclinicos.getTallaCm().toString());
        ((EditText)findViewById(R.id.edtxtTemp)).setText(datosPreclinicos.getTemperaturac().toString());
        ((EditText)findViewById(R.id.edtxtExpediente)).setText(datosPreclinicos.getExpedienteFisico());

        if(datosPreclinicos.getUsuarioMedico() != null) {
            if(adapter != null) {
                MedicosDTO medicoEncontrado = new MedicosDTO();
                for (int i = 0; i < adapter.getCount(); i++) {
                    medicoEncontrado = adapter.getItem(i);
                    if (medicoEncontrado.getIdMedico().intValue() == datosPreclinicos.getUsuarioMedico().intValue()) {
                        break;
                    }
                }
                int posicion = adapter.getPosition(medicoEncontrado);
                ((Spinner) findViewById(R.id.spnMedico)).setSelection(posicion);
            }
        }

        findViewById(R.id.btnEnviar).setEnabled(false);
        findViewById(R.id.btnCancel).setEnabled(false);
        findViewById(R.id.btnNoAtiendeLlamado).setEnabled(false);

        findViewById(R.id.edtxtPeso).setEnabled(false);
        findViewById(R.id.edtxtTalla).setEnabled(false);
        findViewById(R.id.edtxtTemp).setEnabled(false);
        findViewById(R.id.edtxtNumOrdenLlegada).setEnabled(false);
        findViewById(R.id.spnMedico).setEnabled(false);
        findViewById(R.id.imgBusquedaMedico).setEnabled(false);
    }

    /***
     * Metodo para llamar el servicio de cargar la lista de medicos.
     */
    private void llamadoListaMedicoServicio(){
        if (mObtenerMedicosTask == null ||
                mObtenerMedicosTask.getStatus() == AsyncTask.Status.FINISHED) {
            mObtenerMedicosTask = new AsyncTask<Void, Void, Void>() {
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
                        try
                        {
                            if(buscaMedico){
                                buscaMedico = false;
                                RESPUESTADIAG = CONSULTAWS.getListaMedicos(nMedico, false);
                            }else {
                                RESPUESTADIAG = CONSULTAWS.getListaMedicos(false);
                            }
                        }
                        catch (Exception e){
                            MensajesHelper.mostrarMensajeError(CONTEXT,
                                    e.getMessage(), getResources().getString(
                                            R.string.app_name), null);

                        }

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
                        MedicosDTO diagTO = new MedicosDTO();
                        ArrayList<MedicosDTO>  diagMedico= new ArrayList<MedicosDTO>();
                        diagTO.setIdMedico((short) 0);
                        diagTO.setNombreMedico("Seleccione Medico");
                        diagMedico.add(diagTO);
                        diagMedico.addAll(RESPUESTADIAG.getLstResultado());
                        Spinner spnMedico = (Spinner) findViewById(R.id.spnMedico);
                        adapter = new ArrayAdapter<MedicosDTO>(CONTEXT, R.layout.simple_spinner_dropdown_item,  diagMedico);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnMedico.setAdapter(adapter);


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
            mObtenerMedicosTask.execute((Void[])null);
        }
    }

    private void cancelarObtenerDatosPreclinicosTask() {
        if (mObtenerDatosPreclinicosTask != null && mObtenerDatosPreclinicosTask.getStatus() == UserTask.Status.RUNNING) {
            mObtenerDatosPreclinicosTask.cancel(true);
            mObtenerDatosPreclinicosTask = null;
        }
    }

    private void cancelarObtenerMedicoTask() {
        if (mObtenerMedicosTask != null && mObtenerMedicosTask.getStatus() == AsyncTask.Status.RUNNING) {
            mObtenerMedicosTask.cancel(true);
            mObtenerMedicosTask = null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.preclinicos_layout, container, false);

            ((EditText) rootView.findViewById(R.id.edtxtPeso)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4,2)});
            ((EditText) rootView.findViewById(R.id.edtxtTalla)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4,2)});
            ((EditText) rootView.findViewById(R.id.edtxtTemp)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3,2)});

            return rootView;


        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((PreClinicosActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }



        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

   /*         LocalDate birthdate = new LocalDate(1970, 1, 20);
            LocalDate now = new LocalDate();
            Years age = years.yearsBetween(birthdate, now);*/

            EditText edtxtFecha = (EditText)getActivity().findViewById(R.id.edtxtFecha);
            EditText edtxtHora = (EditText)getActivity().findViewById(R.id.edtxtHora);
            EditText edtxtNombreApellido = (EditText)getActivity().findViewById(R.id.edtxtNombreApellido);
            EditText edtxtExpediente = (EditText)getActivity().findViewById(R.id.edtxtExpediente);
            EditText edtxtCodigo = (EditText)getActivity().findViewById(R.id.edtxtCodigo);
            EditText edtxtSexo = (EditText)getActivity().findViewById(R.id.edtxtSexo);
            EditText edtxtEdad = (EditText)getActivity().findViewById(R.id.edtxtEdad);

            EditText edtxtNHojaConsulta = (EditText)getActivity().findViewById(R.id.edtxtNHojaConsulta);
            EditText edtxtNumOrdenLlegada = (EditText)getActivity().findViewById(R.id.edtxtNumOrdenLlegada);

            InicioDTO pacienteSeleccionado = (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado");
            Calendar fechaNac = new GregorianCalendar();

            ((EditText) getActivity().findViewById(R.id.edtxtPeso)).setText(((PreClinicosActivity)getActivity()).VALOR_PESO_KG);
            ((EditText) getActivity().findViewById(R.id.edtxtTalla)).setText(((PreClinicosActivity)getActivity()).VALOR_TALLA);
            ((EditText) getActivity().findViewById(R.id.edtxtTemp)).setText(((PreClinicosActivity)getActivity()).VALOR_TEMP);
            ((EditText) getActivity().findViewById(R.id.edtxtExpediente)).setText(((PreClinicosActivity)getActivity()).VALOR_EXP_FISICO);


            if(pacienteSeleccionado != null){
                edtxtNombreApellido.setText(pacienteSeleccionado.getNomPaciente());
                edtxtCodigo.setText(String.valueOf(pacienteSeleccionado.getCodExpediente()));
                edtxtExpediente.setText(pacienteSeleccionado.getExpedienteFisico());
                edtxtSexo.setText(String.valueOf(pacienteSeleccionado.getSexo()));
                edtxtNHojaConsulta.setText(String.valueOf(pacienteSeleccionado.getNumHojaConsulta()));
                edtxtNumOrdenLlegada.setText(String.valueOf(pacienteSeleccionado.getNumOrdenLlegada()));
                fechaNac = (((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getFechaNac());
                edtxtFecha.setText(df.format(((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getFechaConsulta().getTime()));

                df = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                Calendar hora = Calendar.getInstance();
                //edtxtHora.setText(df.format(((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getFechaConsulta().getTime()));
                if(!StringUtils.isNullOrEmpty(((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getHorasv())) {
                    edtxtHora.setText(((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getHorasv());
                }else{
                    edtxtHora.setText(df.format(hora.getTime()));
                }
                cCtrC.setCodExpediente(pacienteSeleccionado.getCodExpediente());
                cCtrC.setNumHojaConsulta(Integer.parseInt(pacienteSeleccionado.getNumHojaConsulta()));
            }

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
            int month = (age)*12 + today.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH);

            if(today.get(Calendar.DAY_OF_MONTH) < fechaNac.get(Calendar.DAY_OF_MONTH)){
                month = month - 1;
            }

            if(month == 0) {
                Long tDias = (today.getTimeInMillis() - fechaNac.getTimeInMillis())  / (1000 * 60 * 60 * 24);
                edtxtEdad.setText(tDias + " dias");
                //return new StringBuffer().append(tDias).append(" dias").toString();

            }
            else if(age == 0) {
                age = today.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH);
                if(age == 0) {
                    age = today.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH);
                    edtxtEdad.setText(age + " dias");
                    //return new StringBuffer().append(age).append(" dias").toString();
                }else {
                    edtxtEdad.setText(age + " meses");
                    //return new StringBuffer().append(age).append(" meses").toString();
                }
            } else if (month > 0 && month < 12) {
                edtxtEdad.setText(month + " meses");
                //return new StringBuffer().append(month).append(" meses").toString();

            }else {
                if (today.get(Calendar.MONTH) < fechaNac.get(Calendar.MONTH)) {
                    age--;
                } else if (today.get(Calendar.MONTH) == fechaNac.get(Calendar.MONTH)
                        && today.get(Calendar.DAY_OF_MONTH) < fechaNac.get(Calendar.DAY_OF_MONTH)) {
                    age--;
                }
                edtxtEdad.setText(age + " años");
                //return new StringBuffer().append(age).append(" años").toString();
            }

           /* Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);

            if (age == 0){
                age =  (today.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH));

                if (age == 0) {
                    age = (today.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH));
                    edtxtEdad.setText(age + " dias");
                } else {
                    edtxtEdad.setText(age + " meses");
                }
            }else {
                if (today.get(Calendar.MONTH) < fechaNac.get(Calendar.MONTH)) {
                    age--;
                } else if (today.get(Calendar.MONTH) == fechaNac.get(Calendar.MONTH)
                        && today.get(Calendar.DAY_OF_MONTH) < fechaNac.get(Calendar.DAY_OF_MONTH)) {
                    age--;
                }
                edtxtEdad.setText(age + " años");
            }*/

        }

    }

    /***
     * Metodo que se ejecuta en el evento onClick del boton de buscar médico.
     * @param view
     */
    public  void onClick_imgBusquedaMedico(View view){
        buscaMedico = true;
        BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
        DlogBuscar.show(getSupportFragmentManager(), "Buscar");

    }

    /****
     * Metodo que controla el click del boton buscar del Dialog de busqueda.
     * @param dialog, Objeto
     */
    @Override
    public void onDialogAceptClick(DialogFragment dialog) {

            // User touched the dialog's positive button
            EditText etxtBuscar = (EditText) dialog.getDialog().findViewById(R.id.etxtBuscar);
            Toast.makeText(dialog.getActivity(), "Buscando al médico: " + etxtBuscar.getText(), Toast.LENGTH_LONG).show();

            if (!StringUtils.isNullOrEmpty(etxtBuscar.getText().toString())) {
                nMedico = etxtBuscar.getText().toString();
                llamadoListaMedicoServicio();

            } else {
                MensajesHelper.mostrarMensajeInfo(CONTEXT,
                        "Ingrese un texto valido", getResources().getString(
                                R.string.app_name), null);

                buscaMedico = false;
            }

    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
        buscaMedico = false;
    }


}
