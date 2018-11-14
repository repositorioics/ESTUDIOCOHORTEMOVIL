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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.BuscarDialogGenerico;
import com.sts_ni.estudiocohortecssfv.tools.CancelacionDialog;
import com.sts_ni.estudiocohortecssfv.tools.LstViewGenericaInicio;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;

import java.util.ArrayList;

/**
 * Controlador para establecer los valores de los items de las listas de inicio de consulta.
 */
public class InicioConsultaActivity extends ActionBarActivity implements BuscarDialogGenerico.DialogListener
,NavigationDrawerFragment.NavigationDrawerCallbacks
 {

    private LstViewGenericaInicio LST_ADAPTER_INICIO;

    private ListView LSTV_LISTA_CONSULTA;
    ArrayList<InicioDTO> arrDatosEjemlos = new ArrayList<>();
     public Context CONTEXT;

     public Activity ACTIVITY;

     public boolean  blnExpediente=false;
     public boolean  CAMBIO_TURNO=false;
     public int codExpediente;
     InicioDTO pacienteSeleccionado;
     public int  M_POSITION;
     int SEC_HOJA_CONSULTA;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment M_NAVIGATION_DRAWER_FRAGMENT;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_consulta);

    M_NAVIGATION_DRAWER_FRAGMENT = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        M_NAVIGATION_DRAWER_FRAGMENT.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.dwlInicioConsulta));

        this.CONTEXT = this;

        this.ACTIVITY = this;
    }

   @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fmlInicioConsulta, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
      // cargarConsultas();
        blnExpediente = false;
        codExpediente = 0;
       llamadoListaInicioServicio();

    }

     /***
      * Metodo que realiza el llamado del servicio que obtiene la lista de pacientes.
      */
     private void llamadoListaInicioServicio(){


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
                         RESPUESTA = CONSULTAWS.getListaInicioConsulta();
                   //  RESPUESTA = CONSULTAWS.getListaInicioConsultaPorExpediente(1);

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

                     LSTV_LISTA_CONSULTA = (ListView)ACTIVITY.findViewById(R.id.lstvListaConsulta);
                     LST_ADAPTER_INICIO = new LstViewGenericaInicio(CONTEXT, ACTIVITY, RESPUESTA.getLstResultado(), getResources());
                     LSTV_LISTA_CONSULTA.setAdapter(LST_ADAPTER_INICIO);

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
                     RESPUESTA = CONSULTAWS.getListaInicioConsultaPorExpediente(codExpediente, false);

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

                     LSTV_LISTA_CONSULTA = (ListView)ACTIVITY.findViewById(R.id.lstvListaConsulta);
                     LST_ADAPTER_INICIO = new LstViewGenericaInicio(CONTEXT, ACTIVITY, RESPUESTA.getLstResultado(), getResources());
                     LSTV_LISTA_CONSULTA.setAdapter(LST_ADAPTER_INICIO);

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

    /*void cargarConsultas(){
        InicioDTO primerPaciente = new InicioDTO();
        InicioDTO segundoPaciente = new InicioDTO();
        InicioDTO tercerPaciente = new InicioDTO();
        InicioDTO cuartoPaciente = new InicioDTO();
        primerPaciente.setNumHojaConsulta("2204");
        primerPaciente.setNomPaciente("Juan Carlos Lopez Sanchez");
        primerPaciente.setEstado("Enfermeria");
        segundoPaciente.setNumHojaConsulta("6709");
        segundoPaciente.setNomPaciente("Carmen Maria Gutierrez Vilchez");
        segundoPaciente.setEstado("Enfermeria");
        tercerPaciente.setNumHojaConsulta("5508");
        tercerPaciente.setNomPaciente("Hugo Alberto Huete Ruiz");
        tercerPaciente.setEstado("Enfermeria");
        cuartoPaciente.setNumHojaConsulta("3535");
        cuartoPaciente.setNomPaciente("Xiomara del Carmen Valle Lopez");
        cuartoPaciente.setEstado("Enfermeria");

        arrDatosEjemlos = new ArrayList<InicioDTO>();
        arrDatosEjemlos.add(primerPaciente);
        arrDatosEjemlos.add(segundoPaciente);
        arrDatosEjemlos.add(tercerPaciente);
        arrDatosEjemlos.add(cuartoPaciente);

        LSTV_LISTA_CONSULTA = (ListView)findViewById(R.id.lstvListaConsulta);
        LSTV_LISTA_CONSULTA.setClickable(false );
        LSTV_LISTA_CONSULTA.setFocusable(false);

        LST_ADAPTER_INICIO = new LstViewGenericaInicio(this, this, arrDatosEjemlos, getResources());

        LSTV_LISTA_CONSULTA.setAdapter(LST_ADAPTER_INICIO);



    }*/

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_pantalla_inicio);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
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
            getMenuInflater().inflate(R.menu.pantalla_inicio, menu);
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
            Intent intent = new Intent(this,InicioConsultaActivity.class);
            startActivity(intent);
            return true;

        }
        if (id == R.id.action_search) {
            BuscarDialogGenerico DlogBuscar=new BuscarDialogGenerico();
            DlogBuscar.show(getSupportFragmentManager(), "Buscar");

            return true;
        }
        if (id == R.id.action_reload) {
            blnExpediente = false;
            codExpediente = 0;
            llamadoListaInicioServicio();  // cargarConsultas();
            //noinspection SimplifiableIfStatement
        }
        return super.onOptionsItemSelected(item);
    }

     /***
      * Este Metodo asigna al paciente al medico que va a atenderlo, ademas envia un mensaje
      * de alerta preguntandole al medico si desea atender al paciente que esta seleccionando
      */
   public void enviarPacienteConsulta(int mPosition)
    {
        Intent intent = new Intent(ACTIVITY, ConsultaActivity.class);

        this.M_POSITION = mPosition;
        InicioDTO inicioDto = LST_ADAPTER_INICIO.getItem(M_POSITION);

        intent.putExtra("pacienteSeleccionado", inicioDto);
        SEC_HOJA_CONSULTA = inicioDto.getIdObjeto();

        if(inicioDto.getCodigoEstado() == '2' || inicioDto.getCodigoEstado()=='6') {
            CAMBIO_TURNO = false;
            ActualizarEstadoHojaConsulta(cargarHojaConsulta());
        } else {

            Integer IdUsuarioMedico = new Integer(inicioDto.getUsuarioMedico());
            Integer IdMedicoCambioTurno = new Integer(inicioDto.getMedicoCambioTurno());
            int IdUsuarioLista;

            if (IdMedicoCambioTurno != null && IdMedicoCambioTurno.intValue() > 0)
                IdUsuarioLista = IdMedicoCambioTurno.intValue();
            else
                IdUsuarioLista = IdUsuarioMedico.intValue();

            int IdUsuarioLogeado = ((CssfvApp)ACTIVITY.getApplication()).getInfoSessionWSDTO().getUserId();
            if ((inicioDto.getCodigoEstado() == '5') )
            {
                CAMBIO_TURNO = true;
                ActualizarEstadoHojaConsulta(cargarHojaConsulta());

            }
            else if(IdUsuarioLista != IdUsuarioLogeado){

                MensajesHelper.mostrarMensajeInfo(this, String.format(
                        getResources().getString(R.string.msj_el_paciente_seleccionado_se_encuentra_en), inicioDto.getEstado()),
                        getResources().getString(R.string.app_name), null);

            }
            else{

                startActivity(intent);
                finish();

            }
        }
    }
     /***
      * Metodo para controlar el click en los items de la lista.
      * @param mPosition, Posicion del item que se le realiza el click.
      */
   public void onItemClick(int mPosition)
    {
        this.M_POSITION = mPosition;
        InicioDTO inicioDto = LST_ADAPTER_INICIO.getItem(M_POSITION);
        DialogInterface.OnClickListener confirmacionAtencionPacienteDialogClickListener = new
                DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        enviarPacienteConsulta(M_POSITION);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        MensajesHelper.mostrarMensajeYesNo(this,String.format(
                getResources().getString(R.string.msj_confirmacion_atencion_consulta_paciente), inicioDto.getNomPaciente()),
                getResources().getString(
                        R.string.title_estudio_sostenible), confirmacionAtencionPacienteDialogClickListener);
    }

     /***
      * Metodo para realizar las validaciones cuando solo un medico puede abrir la hoja de consulta.
      * @return
      */
     public HojaConsultaDTO cargarHojaConsulta() {

         HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
         hojaConsulta.setSecHojaConsulta(SEC_HOJA_CONSULTA);
         hojaConsulta.setEstado('3');
         if (!CAMBIO_TURNO) {
             hojaConsulta.setUsuarioMedico((short) ((CssfvApp) ACTIVITY.getApplication()).getInfoSessionWSDTO().getUserId());
         }
         else {
             hojaConsulta.setEstado('3');
         } hojaConsulta.setMedicoCambioTurno((short) ((CssfvApp) ACTIVITY.getApplication()).getInfoSessionWSDTO().getUserId());

         return hojaConsulta;
     }

     /***
      * Metodo para actualizar de estado la hoja de consulta.
      * @param hojaconsulta, Hoja de consulta.
      */
     private void ActualizarEstadoHojaConsulta(final HojaConsultaDTO hojaconsulta){
        /*Creando una tarea asincrona*/
         AsyncTask<Void, Void, Void> HojaConsulta = new AsyncTask<Void, Void, Void>() {
             private ProgressDialog PD;
             private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
             private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
             private ErrorDTO RESPUESTA = new ErrorDTO();
             private ConsultaWS consultaWS = new ConsultaWS(getResources());


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

                     RESPUESTA = consultaWS.ActualizarEstadoHojaConsulta(hojaconsulta,CAMBIO_TURNO);
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
                     Intent intent = new Intent(ACTIVITY, ConsultaActivity.class);

                     InicioDTO inicioDto = LST_ADAPTER_INICIO.getItem(M_POSITION);
                     intent.putExtra("pacienteSeleccionado", inicioDto);
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
            View rootView = inflater.inflate(R.layout.inicio_consulta_layout, container, false);
            return rootView;

        }


        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((InicioConsultaActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

        }


    }

}
