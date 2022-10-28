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
import android.support.v4.app.DialogFragment;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.MedicosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.BuscarDialogGenerico;
import com.sts_ni.estudiocohortecssfv.tools.LstViewGenericaInicio;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.EnfermeriaWS;

import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * Controlador de la Pantalla Lista de Inicio Enfermería.
 *
 */
public class InicioEnfermeriaActivity extends ActionBarActivity implements BuscarDialogGenerico.DialogListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    private LstViewGenericaInicio LST_ADAPTER_INICIO;

    private ListView LSTV_LISTA_ENFERMERIA;

    private ArrayList<InicioDTO> ARR_DATOS_EJEMPLOS;

    private InicioDTO mInicioDto;

    public Context CONTEXT;

    public Activity ACTIVITY;
    public AsyncTask<HojaConsultaDTO, Void, ErrorDTO> mAsignarUusarioEnfermeriaTask;
    public  AsyncTask<Void, Void, Void> mCancelaListaInicioServicioTask;
    private ResultadoListWSDTO<InicioDTO> RESPUESTADIAG;

    public int M_POSITION;

    public boolean blnExpediente = false;
    public int codExpediente;

    private NavigationDrawerFragment M_NAVIGATION_DRAWER_FRAGMENT;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_enfermeria);

        M_NAVIGATION_DRAWER_FRAGMENT = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        M_NAVIGATION_DRAWER_FRAGMENT.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.dwlInicioEnfermeria));

        this.CONTEXT = this;

        this.ACTIVITY = this;

    }
/*
    @Override
    public void onStop() {
        super.onStop();

        if(this.PD != null && this.PD.isShowing() )
            this.PD.dismiss();
    }*/

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fmlInicioEnfermeria, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        blnExpediente = false;
        codExpediente = 0;

        llamadoListaInicioServicio();

        /*InicioDTO inicioDto = ARR_DATOS_EJEMPLOS.get(mPosition);
        intent.putExtra("pacienteSeleccionado", inicioDto);*/

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelarUsuarioEnfermeriaTask();
        cancelarListaInicioServicioTask();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!M_NAVIGATION_DRAWER_FRAGMENT.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.inicio_enfermeria_menu, menu);
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
        if (id == R.id.action_emergencia) {
            Intent intent = new Intent(this, EmergenciaCargaHojaConsultaActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == R.id.action_reload) {
            blnExpediente = false;
            codExpediente = 0;
            llamadoListaInicioServicio();  // Cargar Lista Enfermeria();
            //noinspection SimplifiableIfStatement
        }

        if (id == R.id.action_search) {
            BuscarDialogGenerico DlogBuscar=new BuscarDialogGenerico();
            DlogBuscar.show(getSupportFragmentManager(), "Buscar");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /****
     * Metodo que controla el click del boton buscar del Dialog de busqueda.
     * @param dialog, Objeto
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        EditText etxtBuscar= (EditText)dialog.getDialog().findViewById(R.id.etxtBuscar);
        Toast.makeText(dialog.getActivity(), "Buscando número de hoja: " + etxtBuscar.getText(), Toast.LENGTH_LONG).show();
        if (isNumeric(etxtBuscar.getText().toString())) {
            blnExpediente = true;
            codExpediente = Integer.parseInt(etxtBuscar.getText().toString());

            llamadoListaInicioServicioPorExpediente();
        }
        else {
            blnExpediente = false;
            MensajesHelper.mostrarMensajeInfo(CONTEXT,
                    "Ingrese un código de expediente valido", getResources().getString(
                            R.string.app_name), null);

        }

    }

    private static boolean isNumeric(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    /***
     * Metodo que veririfica si el paciente ya esta siendo atendido
     * en el area de enfermeria
     */
    public void enviarPacienteEnfermeria(int mPosition) {
        this.M_POSITION = mPosition;
        mInicioDto = LST_ADAPTER_INICIO.getItem(M_POSITION);

        int IdUsuarioLogeado = ((CssfvApp) ACTIVITY.getApplication()).getInfoSessionWSDTO().getUserId();
        Integer IdUsuarioEnfermeria = (StringUtils.isNullOrEmpty(mInicioDto.getUsuarioEnfermeria()) ||
                mInicioDto.getUsuarioEnfermeria().compareTo("null") == 0) ? null :
        new Integer(mInicioDto.getUsuarioEnfermeria());

        if (IdUsuarioEnfermeria != null && IdUsuarioEnfermeria.intValue() != IdUsuarioLogeado) {
            MensajesHelper.mostrarMensajeInfo(this,
                    getResources().getString(R.string.msj_paciente_ya_atendido),
                    getResources().getString(R.string.app_name), null);

        } else {
            asignarUsuarioEnfermeria();
        }
    }

    /***
     * Esta funcion es utilizada por el adpatador del listview
     *
     * @param mPosition, Posición del item de la lista
     */
    public void onItemClick(int mPosition) {
        this.M_POSITION = mPosition;
        InicioDTO inicioDto = LST_ADAPTER_INICIO.getItem(M_POSITION);
        DialogInterface.OnClickListener confirmacionAtencionPacienteDialogClickListener = new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                enviarPacienteEnfermeria(M_POSITION);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
        MensajesHelper.mostrarMensajeYesNo(this, String.format(getResources().getString(
                        R.string.msj_confirmacion_atencion_consulta_paciente), inicioDto.getNomPaciente()),
                getResources().getString(R.string.title_estudio_sostenible), confirmacionAtencionPacienteDialogClickListener);
    }

    /***
     * Metodo para realizar el llamado al servicio que carga la lista de pacientes
     */
    private void llamadoListaInicioServicio() {
        /*Creando una tarea asincrona*/
        if (mCancelaListaInicioServicioTask == null ||
                mCancelaListaInicioServicioTask.getStatus() == AsyncTask.Status.FINISHED) {
            mCancelaListaInicioServicioTask = new AsyncTask<Void, Void, Void>() {
                //AsyncTask<Void, Void, Void> listaInicioServicio = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private ResultadoListWSDTO<InicioDTO> RESPUESTA = new ResultadoListWSDTO<InicioDTO>();
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
                    if (NET_INFO != null && NET_INFO.isConnected()) {
                        RESPUESTA = ENFERMERIAWS.getListaInicioEnfermeria();
                    } else {
                        RESPUESTA.setCodigoError(Long.parseLong("3"));
                        RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    PD.dismiss();
                    if (RESPUESTA.getCodigoError().intValue() == 0) {

                        LSTV_LISTA_ENFERMERIA = (ListView) ACTIVITY.findViewById(R.id.lstvListaEnfermeria);
                        LST_ADAPTER_INICIO = new LstViewGenericaInicio(CONTEXT, ACTIVITY, RESPUESTA.getLstResultado(), getResources());
                        LSTV_LISTA_ENFERMERIA.setAdapter(LST_ADAPTER_INICIO);

                    } else if (RESPUESTA.getCodigoError().intValue() != 999) {
                        MensajesHelper.mostrarMensajeInfo(CONTEXT,
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    } else {
                        MensajesHelper.mostrarMensajeError(CONTEXT,
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);
                    }
                }
            };
            mCancelaListaInicioServicioTask.execute();
        }
    }

    /***
     * Metodo que realiza el llamado al servicio de busquedad de un expediente especifico.
     */
    private void llamadoListaInicioServicioPorExpediente(){


        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> listaInicioServicio = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ResultadoListWSDTO<InicioDTO> RESPUESTA = new ResultadoListWSDTO<>();
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
                    RESPUESTA = CONSULTAWS.getListaInicioConsultaPorExpediente(codExpediente, true);

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

                    LSTV_LISTA_ENFERMERIA = (ListView)ACTIVITY.findViewById(R.id.lstvListaEnfermeria);
                    LST_ADAPTER_INICIO = new LstViewGenericaInicio(CONTEXT, ACTIVITY, RESPUESTA.getLstResultado(), getResources());
                    LSTV_LISTA_ENFERMERIA.setAdapter(LST_ADAPTER_INICIO);

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
     *
     */
    private void cancelarListaInicioServicioTask() {
        if (mCancelaListaInicioServicioTask != null && mCancelaListaInicioServicioTask.getStatus() == AsyncTask.Status.RUNNING) {
            mCancelaListaInicioServicioTask.cancel(true);
            mCancelaListaInicioServicioTask = null;
        }
    }

    /***
     * Metodo para asignar el usuario enfermeria a la hoja deconsulta.
     *
     */
    private void asignarUsuarioEnfermeria() {
        if (mAsignarUusarioEnfermeriaTask == null ||
                mAsignarUusarioEnfermeriaTask.getStatus() == AsyncTask.Status.FINISHED) {
            mAsignarUusarioEnfermeriaTask = new AsyncTask<HojaConsultaDTO, Void, ErrorDTO>() {
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();

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
                protected ErrorDTO doInBackground(HojaConsultaDTO... params) {
                    ErrorDTO resultado = new ErrorDTO();
                    if (NET_INFO != null && NET_INFO.isConnected()){
                        try
                        {

                            resultado = ENFERMERIAWS.guardarUsuarioEnfermeria(params[0]);
                        }
                        catch (Exception e){
                            resultado.setCodigoError(Long.parseLong("999"));
                            resultado.setMensajeError(getResources().getString(R.string.msj_error_no_controlado));
                        }

                    }else{
                        resultado.setCodigoError(Long.parseLong("3"));
                        resultado.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));
                    }

                    return resultado;
                }

                @Override
                protected void onPostExecute(ErrorDTO resultado){
                    PD.dismiss();
                    if (resultado.getCodigoError().intValue() == 0){

                        Intent intent = new Intent(CONTEXT, PreClinicosActivity.class);
                        intent.putExtra("pacienteSeleccionado", mInicioDto);
                        startActivity(intent);
                        finish();

                    }else if (resultado.getCodigoError().intValue() != 999){
                        MensajesHelper.mostrarMensajeInfo(CONTEXT,
                                resultado.getMensajeError(), getResources().getString(
                                        R.string.app_name), null);

                    }else{
                        MensajesHelper.mostrarMensajeError(CONTEXT,
                                resultado.getMensajeError(), getResources().getString(
                                        R.string.app_name), null);
                    }
                }
            };
            HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
            hojaConsulta.setSecHojaConsulta(mInicioDto.getIdObjeto());
            hojaConsulta.setUsuarioEnfermeria(((CssfvApp)ACTIVITY.getApplication()).getInfoSessionWSDTO().getUserId());
            mAsignarUusarioEnfermeriaTask.execute(hojaConsulta);
        }
    }

    /***
     *
     */
    private void cancelarUsuarioEnfermeriaTask() {
        if (mAsignarUusarioEnfermeriaTask != null && mAsignarUusarioEnfermeriaTask.getStatus() == AsyncTask.Status.RUNNING) {
            mAsignarUusarioEnfermeriaTask.cancel(true);
            mAsignarUusarioEnfermeriaTask = null;
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
            View rootView = inflater.inflate(R.layout.inicio_enfermeria_layout, container, false);
            return rootView;

        }


        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((InicioEnfermeriaActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

        }


    }
}
