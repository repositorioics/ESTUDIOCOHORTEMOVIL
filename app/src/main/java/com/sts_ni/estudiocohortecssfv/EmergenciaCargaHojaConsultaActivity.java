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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.PacienteDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.EnfermeriaWS;

/**
 * Controlador de la pantalla Cargar Hoja de Consulta para emergencia.
 */
public class EmergenciaCargaHojaConsultaActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment M_NAVIGATION_DRAWER_FRAGMENT;

    public Context CONTEXT;

    public  Activity ACTIVITY;

    public int COD_EXPEDIENTE = 0;


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static final String EDIT_TEXT_COD_EXPEDIENTE_VALUE = "codExpediente";
    private String VALOR_COD_EXPEDIENTE = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia_carga_hoja_consulta);

        M_NAVIGATION_DRAWER_FRAGMENT = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        M_NAVIGATION_DRAWER_FRAGMENT.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.dwlEmergenciaCargaHojaConsulta));

        if (savedInstanceState != null){
            this.VALOR_COD_EXPEDIENTE = savedInstanceState.getString(this.EDIT_TEXT_COD_EXPEDIENTE_VALUE);

        }

        this.CONTEXT = this;

        this.ACTIVITY = this;
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
                .replace(R.id.fmlEmergenciaCargaHojaConsulta, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(this.EDIT_TEXT_COD_EXPEDIENTE_VALUE, ((EditText) findViewById(R.id.edtxtCodigoExpediente)).getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!M_NAVIGATION_DRAWER_FRAGMENT.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.pantalla_inicio, menu);
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

    /***
     * Metodo que se ejecuta en el evento onClick del boton guardar.
     * @param view
     */
    public void onClick_btnGuardarHojaConsulta( View view){
        if (this.COD_EXPEDIENTE != 0) {
            DialogInterface.OnClickListener preguntaGuardarDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            emergenciaEnfermeriaGuardarServicio();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            MensajesHelper.mostrarMensajeYesNo(this, getResources().getString(R.string.msj_aviso_crear_hoja_consulta),
                    getResources().getString(R.string.title_estudio_sostenible), preguntaGuardarDialogClickListener);
        }else{
            MensajesHelper.mostrarMensajeInfo(this,
                    getResources().getString(
                            R.string.msj_realizar_busqueda_para_guardar), getResources().getString(
                            R.string.title_estudio_sostenible), null);
        }

    }

    /***
     * Metodo que se ejecuta en el evento onClick del boton de buscar.
     * @param view
     */
    public  void onClick_imgBotonBusquedaLupa (View view){
        if (verificarCodigoPaciente()) {
            buscarPacienteLupaServicio();
        }

    }

    private boolean verificarCodigoPaciente () {
        EditText edtxCodigoExpediente = (EditText) findViewById(R.id.edtxtCodigoExpediente);

        if (StringUtils.isNullOrEmpty(edtxCodigoExpediente.getText().toString())) {

            MensajesHelper.mostrarMensajeInfo(this,
                    getResources().getString(
                            R.string.msj_no_se_encontro_codigo_paciente), getResources().getString(
                            R.string.title_estudio_sostenible), null);

            return false;
        }
        return true;
    }

    /***
     * Metedo que realiza el llamado del servicio que busca el paciente.
     */
    private void buscarPacienteLupaServicio(){
        /*Creando una tarea asincrona*/
            AsyncTask<Void, Void, Void> emergenciaCargaHojaConsulta = new AsyncTask<Void, Void, Void>() {
                private PacienteDTO PACIENTE = new PacienteDTO();
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private ErrorDTO RESPUESTA = new ErrorDTO();
                private EnfermeriaWS ENFERMERIAWS = new EnfermeriaWS(getResources());

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
                        PACIENTE.setCodExpediente (Integer.parseInt(((EditText) ACTIVITY.findViewById(R.id.edtxtCodigoExpediente)).getText().toString()));
                        RESPUESTA = ENFERMERIAWS.buscarPacientes(PACIENTE);
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
                        ((EditText) ACTIVITY.findViewById(R.id.edtxtNombrePaciente)).setText(PACIENTE.getNomPaciente());
                        COD_EXPEDIENTE = PACIENTE.getCodExpediente();

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
        emergenciaCargaHojaConsulta.execute((Void[])null);

    }

    /***
     * Metodo que realiza el llamdo del servicio que guardar la Hoja de consulta en emergencia.
     * @param view
     */
    private void emergenciaEnfermeriaGuardarServicio(){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> emergenciaEnfermeria = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private EnfermeriaWS ENFERMERIAWS = new EnfermeriaWS(getResources());

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
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
                    hojaConsulta.setCodExpediente(COD_EXPEDIENTE);
                    hojaConsulta.setUsuarioEnfermeria(((CssfvApp)ACTIVITY.getApplication()).getInfoSessionWSDTO().getUserId());
                    RESPUESTA = ENFERMERIAWS.guardarPacienteEmergencia(hojaConsulta);
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
        emergenciaEnfermeria.execute((Void[])null);
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
            View rootView = inflater.inflate(R.layout.emergencia_carga_hoja_consulta_layout, container, false);
            return rootView;

        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((EmergenciaCargaHojaConsultaActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ((EditText) getActivity().findViewById(R.id.edtxtCodigoExpediente)).setText(((EmergenciaCargaHojaConsultaActivity)getActivity()).VALOR_COD_EXPEDIENTE);



        }

    }
}
