package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.NavigationDrawerFragment;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.ExpedienteDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.CancelacionDialog;
import com.sts_ni.estudiocohortecssfv.tools.ImpresionPdfDialog;
import com.sts_ni.estudiocohortecssfv.tools.LstViewGenericaExp;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.CierreWS;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.ExpedienteWS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Controlador de la UI Expediente.
 */
public class ExpedienteActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ImpresionPdfDialog.DialogImpresionPdfListener{

    private NavigationDrawerFragment mNavigationDrawerFragment;
    ArrayList<ExpedienteDTO> ARRAY_DATOS_EXPEDIENTE;
    String CODIGO_EXPEDIENTE;
    private LstViewGenericaExp LST_ADAPTER_EXP;
    private int SEC_HOJA_CONSULTA;
    private Activity ACTIVITY;
    public byte[] RESPUESTA = null;

    public  AsyncTask<Void, Void, Void> mReimpresionHojConsultaTask;
    private DialogFragment mFragmentoAcciones;

    private static Context CONTEXT;
    public static int consultorioMedico = 0;
    public static int consultorioResp = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expediente_busqueda);

        this.CONTEXT = this;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView) actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.title_expedientes));
        ((TextView) actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp) getApplication()).getInfoSessionWSDTO().getUser());

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.dwlExpediente));

        if (savedInstanceState != null) {
            this.ARRAY_DATOS_EXPEDIENTE = (ArrayList<ExpedienteDTO>) savedInstanceState.getSerializable("listaExpediente");
            this.CODIGO_EXPEDIENTE = savedInstanceState.getString("codExpediente");
        }

        this.ACTIVITY = this;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if ((((ListView) findViewById(R.id.lstvHojaConsultaExp)).getAdapter()) != null) {
            outState.putSerializable("listaExpediente", ((LstViewGenericaExp) ((ListView) findViewById(R.id.lstvHojaConsultaExp)).getAdapter()).getResultado());
        }
        outState.putString("codExpediente", ((EditText) findViewById(R.id.edtxBuscarExpediente)).getText().toString());
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelarReimpresionHojaConsulta();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fmlExpediente, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.pantalla_inicio, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            MenuItem itemLupa = (MenuItem) menu.findItem(R.id.action_search);
            itemLupa.setVisible(false);
            MenuItem itemRecargar = (MenuItem) menu.findItem(R.id.action_reload);
            itemRecargar.setVisible(false);
            return true;
        }
        return super.onPrepareOptionsMenu(menu);
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

    public void onItemClick(int mPosition)
    {
        ExpedienteDTO inicioDto =  LST_ADAPTER_EXP.getItem(mPosition);
        SEC_HOJA_CONSULTA=inicioDto.getSecHojaConsulta();
        //obtenerHojaConsultaPdf();
        ImpresionPdfDialog DlogBuscar=new ImpresionPdfDialog();
        DlogBuscar.show(getSupportFragmentManager(), "Seleccionar");
    }

    /*Metodo para hacer la seleccion de la impresora*/
    private void showAlertDialogReimpresionHC() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CONTEXT);
        alertDialog.setTitle("Seleccioné la Impresora");
        String[] items = {"Consultorio Médico","Consultorio Respiratorio"};
        int checkedItem = 2;
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(CONTEXT, "Impresión enviada al Consultorio Médico", Toast.LENGTH_LONG).show();
                        llamarProcesoReimpresionHojaConsulta(consultorioMedico);
                        dialog.cancel();
                        break;
                    case 1:
                        Toast.makeText(CONTEXT, "Impresión enviada al Consultorio Respiratorio", Toast.LENGTH_LONG).show();
                        llamarProcesoReimpresionHojaConsulta(consultorioResp);
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    /***
     * Metodo para realizar la reimpresion de la hoja de consulta.
     */
    private void llamarProcesoReimpresionHojaConsulta(final int consultorio) {
        if (mReimpresionHojConsultaTask == null ||
                mReimpresionHojConsultaTask.getStatus() == AsyncTask.Status.FINISHED) {
            mReimpresionHojConsultaTask = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) ACTIVITY.getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private ErrorDTO RESPUESTA = new ErrorDTO();
                private ExpedienteWS EXPEDIENTEWS;

                @Override
                protected void onPreExecute() {
                    try {
                        EXPEDIENTEWS = new ExpedienteWS(getResources());
                        PD = new ProgressDialog(ACTIVITY);
                        PD.setTitle(getResources().getString(R.string.title_procesando));
                        PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                        PD.setCancelable(false);
                        PD.setIndeterminate(true);
                        PD.show();
                    } catch (IllegalArgumentException iae) {
                        iae.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        if (NET_INFO != null && NET_INFO.isConnected()) {

                            RESPUESTA = EXPEDIENTEWS.ejecutarProcesoReimpresion(SEC_HOJA_CONSULTA, consultorio);
                        } else {
                            RESPUESTA.setCodigoError(Long.parseLong("3"));
                            RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                        }
                    } catch (IllegalArgumentException iae) {
                        iae.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    try {
                        PD.dismiss();
                        if (RESPUESTA.getCodigoError().intValue() == 0) {
                            MensajesHelper.mostrarMensajeOk(ACTIVITY,
                                    getResources().getString(R.string.msj_se_ejecuto_la_impresion),
                                    getResources().getString(
                                            R.string.title_estudio_sostenible), null);
                        } else if (RESPUESTA.getCodigoError().intValue() == 1) {
                            MensajesHelper.mostrarMensajeOk(ACTIVITY,
                                    getResources().getString(R.string.msj_se_ejecuto_la_impresion),
                                    getResources().getString(
                                            R.string.title_estudio_sostenible), null);

                        } else {
                            MensajesHelper.mostrarMensajeError(ACTIVITY,
                                    RESPUESTA.getMensajeError(), getResources().getString(
                                            R.string.title_estudio_sostenible), null);
                        }
                    } catch (IllegalArgumentException iae) {
                        iae.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }
            };
            mReimpresionHojConsultaTask.execute((Void[]) null);
        }
    }

    /***
     *Metodo para cancelar el proceso cuando se gira el dispositivo
     */
    private void cancelarReimpresionHojaConsulta() {
        if (mReimpresionHojConsultaTask != null && mReimpresionHojConsultaTask.getStatus() == AsyncTask.Status.RUNNING) {
            mReimpresionHojConsultaTask.cancel(true);
            mReimpresionHojConsultaTask = null;
        }
    }

    private void obtenerHojaConsultaPdf(){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> hojaconsultapdf = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager)ACTIVITY.getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ConsultaWS CONSULTAWS = new ConsultaWS(getResources());

            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(ACTIVITY);
                PD.setTitle(getResources().getString(R.string.title_procesando));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected())
                {
                    RESPUESTA = CONSULTAWS.getHojaConsultaPdf(SEC_HOJA_CONSULTA);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                PD.dismiss();

                try {

                    File file = new File("/sdcard/HojaConsulta.pdf");
                    FileOutputStream fos = new FileOutputStream(file);
                    String filepath = "/sdcard/HojaConsulta.pdf";
                    OutputStream pdffos = new FileOutputStream(filepath);
                    pdffos.write(RESPUESTA);
                    pdffos.flush();
                    pdffos.close();
                    if (file.exists()) {
                        Uri path = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(path, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
        hojaconsultapdf.execute((Void[])null);

    }

    @Override
    public void onClickImpresion() {
        //llamarProcesoReimpresionHojaConsulta();
        showAlertDialogReimpresionHC();

    }

    @Override
    public void onClickPDF() {
        obtenerHojaConsultaPdf();
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

        private ListView LSTV_LISTA_Expediente;
        ArrayList<ExpedienteDTO> arrDatosExp = new ArrayList<ExpedienteDTO>();

        public int codExpediente;


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
            View rootView = inflater.inflate(R.layout.expediente_busqueda_layout, container, false);

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);


            ImageButton btnBuscarExp = (ImageButton) getActivity().findViewById(R.id.ibtBuscarExp);
            btnBuscarExp.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    EditText edtxBuscarExpediente= (EditText)getActivity().findViewById(R.id.edtxBuscarExpediente);
                    Toast.makeText(getActivity(), "Buscando código de expediente: " + edtxBuscarExpediente.getText(), Toast.LENGTH_LONG).show();
                    if (StringUtils.isNumeric(edtxBuscarExpediente.getText().toString())) {

                        codExpediente = Integer.parseInt(edtxBuscarExpediente.getText().toString());

                        cargarExpediente();
                    }
                    else {

                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                "Ingrese un código de expediente valido", getResources().getString(
                                        R.string.app_name), null);

                    }

                }
            });


            ((EditText)getActivity().findViewById(R.id.edtxBuscarExpediente)).setText(((ExpedienteActivity)getActivity()).CODIGO_EXPEDIENTE);

            if (((ExpedienteActivity)getActivity()).ARRAY_DATOS_EXPEDIENTE != null &&
                    ((ExpedienteActivity)getActivity()).ARRAY_DATOS_EXPEDIENTE.size() > 0) {
                LSTV_LISTA_Expediente = (ListView) getActivity().findViewById(R.id.lstvHojaConsultaExp);
                ((ExpedienteActivity) getActivity()).LST_ADAPTER_EXP = new LstViewGenericaExp(getActivity(), getActivity(), ((ExpedienteActivity) getActivity()).ARRAY_DATOS_EXPEDIENTE, getResources());
                LSTV_LISTA_Expediente.setAdapter(((ExpedienteActivity) getActivity()).LST_ADAPTER_EXP);
            }



        }


        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        void cargarExpediente() {

  /*Creando una tarea asincrona*/
            AsyncTask<Void, Void, Void> listaInicioServicio = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private ResultadoListWSDTO<ExpedienteDTO> RESPUESTA = new ResultadoListWSDTO<ExpedienteDTO>();
                private ExpedienteWS EXPWS = new ExpedienteWS(getResources());

                @Override
                protected void onPreExecute() {
                    PD = new ProgressDialog(getActivity());
                    PD.setTitle(getResources().getString(R.string.title_obteniendo));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    if (NET_INFO != null && NET_INFO.isConnected()){
                        RESPUESTA = EXPWS.getListaHojaConsulta(codExpediente);

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

                        LSTV_LISTA_Expediente = (ListView)getActivity().findViewById(R.id.lstvHojaConsultaExp);
                        ((ExpedienteActivity) getActivity()).LST_ADAPTER_EXP = new LstViewGenericaExp(getActivity(), getActivity(), RESPUESTA.getLstResultado(), getResources());
                        LSTV_LISTA_Expediente.setAdapter(((ExpedienteActivity) getActivity()).LST_ADAPTER_EXP);

                    }else if (RESPUESTA.getCodigoError().intValue() != 999){
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.app_name), null);

                    }else{
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.app_name), null);
                    }
                }
            };
            listaInicioServicio.execute((Void[])null);

        /*ExpedienteDTO exp1 = new ExpedienteDTO();

        exp1.setNumHojaConsulta(1025);
        exp1.setFechaConsulta("10/03/2015");
        exp1.setHoraConsulta("10:05:50");
        exp1.setNomMedico("Leandro Vanegas");
        exp1.setEstado("L");

        ExpedienteDTO exp2 = new ExpedienteDTO();

        exp2.setNumHojaConsulta(1056);
        exp2.setFechaConsulta("19/03/2015");
        exp2.setHoraConsulta("08:05:50");
        exp2.setNomMedico("Hilbert Méndez");
        exp2.setEstado("E");

        arrDatosExp = new ArrayList<ExpedienteDTO>();
        arrDatosExp.add(exp1);
        arrDatosExp.add(exp2);


        LSTV_LISTA_Expediente = (ListView) findViewById(R.id.lstvHojaConsulta);
        LSTV_LISTA_Expediente.setClickable(false);
        LSTV_LISTA_Expediente.setFocusable(false);

        LST_ADAPTER_EXP = new LstViewGenericaExp(this, this, arrDatosExp, getResources());

        LSTV_LISTA_Expediente.setAdapter(LST_ADAPTER_EXP);

      */
        }
    }
}
