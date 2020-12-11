package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.InicioConsultaActivity;
import com.sts_ni.estudiocohortecssfv.NavigationDrawerFragment;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ConsultaRespiratorioDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.LstViewGenericaInicio;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;

public class ListaConsultaRespiratorioActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private Context CONTEXT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.CONTEXT = this;
        setContentView(R.layout.activity_lista_consulta_respiratorio);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView) actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.label_consulta_respiratorio));
        ((TextView) actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp) getApplication()).getInfoSessionWSDTO().getUser());

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.dwlListaConsultaRespiratorio));

        llamadoListaConsultaRespiratorioServicio();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fmlConsultaRespiratorio, ListaConsultaRespiratorioActivity.PlaceholderFragment.newInstance(position + 1))
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
            MenuItem itemLupa = menu.findItem(R.id.action_search);
            itemLupa.setVisible(false);
            MenuItem itemRecargar = menu.findItem(R.id.action_reload);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        public static ListaConsultaRespiratorioActivity.PlaceholderFragment newInstance(int sectionNumber) {
            ListaConsultaRespiratorioActivity.PlaceholderFragment fragment = new ListaConsultaRespiratorioActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }


        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View consultaRespiratoria = inflater.inflate(R.layout.lista_respiratorio_layout, container, false);
            return consultaRespiratoria;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
    }

    /***
     * Metodo que realiza el llamado del servicio que obtiene la lista de consultas en respiratorio.
     */
    private void llamadoListaConsultaRespiratorioServicio(){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> listaInicioServicio = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ResultadoListWSDTO<ConsultaRespiratorioDTO> RESPUESTA = new ResultadoListWSDTO<>();
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
                    RESPUESTA = CONSULTAWS.getListaConsultasRespitatorio();
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
                if (RESPUESTA.getCodigoError().intValue() == 0) {
                    init(RESPUESTA);

                    /*LSTV_LISTA_CONSULTA = (ListView)ACTIVITY.findViewById(R.id.lstvListaConsulta);
                    LST_ADAPTER_INICIO = new LstViewGenericaInicio(CONTEXT, ACTIVITY, RESPUESTA.getLstResultado(), getResources());
                    LSTV_LISTA_CONSULTA.setAdapter(LST_ADAPTER_INICIO);*/

                } else if (RESPUESTA.getCodigoError().intValue() != 999) {
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.app_name), null);

                    /*Creando una tabla sin datos*/
                    TableLayout tbL = (TableLayout) findViewById(R.id.tabla_consulta_respiratorio);
                    TableRow tbrowNoData = new TableRow(CONTEXT);
                    tbrowNoData.setBackgroundResource(R.color.bg_button_no_atiende_llamado_normal_color);
                    TextView tvNoData = new TextView(CONTEXT);
                    tvNoData.setText("No hay consultas en respiratorio");
                    tvNoData.setTextSize(18);
                    tvNoData.setTextColor(Color.WHITE);
                    tbrowNoData.addView(tvNoData);
                    tbL.addView(tbrowNoData);

                }else{
                    MensajesHelper.mostrarMensajeError(CONTEXT,
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.app_name), null);
                }
            }
        };
        listaInicioServicio.execute((Void[])null);

    }

    public void init(ResultadoListWSDTO<ConsultaRespiratorioDTO> result) {
        EditText cantidadConsultaEnEspera = (EditText) findViewById(R.id.edtxtConsultasEnEspera);
        EditText cantidadEnConsultas = (EditText) findViewById(R.id.edtxtEnConsulta);
        EditText cantidadConsultasEnLab = (EditText) findViewById(R.id.edtxtConsultasEnLab);
        EditText cantidadConsultaCerradas = (EditText) findViewById(R.id.edtxtConsultasCerrada);

        TableLayout stk = (TableLayout) findViewById(R.id.tabla_consulta_respiratorio);

        TableRow tbrowTitle = new TableRow(this);
        tbrowTitle.setBackgroundResource(R.color.bg_button_no_atiende_llamado_normal_color);
        TextView tvTitle = new TextView(this);
        tvTitle.setText("Consultas en respiratorio");
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(Color.WHITE);
        tbrowTitle.addView(tvTitle);

        //stk.addView(tbrowTitle);

        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundResource(R.color.bg_text_estado_color);

        TextView tv0 = new TextView(this);
        tv0.setText(" Medico ");
        tv0.setTextSize(14);
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setPadding(5, 15, 0, 15);
        tv0.setTextColor(Color.BLACK);
        tbrow0.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText(" Código Expediente ");
        tv1.setTextSize(14);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setPadding(50, 15, 0, 15);
        tv1.setTextColor(Color.BLACK);
        tbrow0.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(" Número Hoja Consulta ");
        tv2.setTextSize(14);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setPadding(50, 15, 0, 15);
        tv2.setTextColor(Color.BLACK);
        tbrow0.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText(" Estado ");
        tv3.setTextSize(14);
        tv3.setTypeface(null, Typeface.BOLD);
        tv3.setPadding(50, 15, 0, 15);
        tv3.setTextColor(Color.BLACK);
        tbrow0.addView(tv3);

        stk.addView(tbrow0);

        int enEspera = 0;
        int enConsulta = 0;
        int enLab = 0;
        int cerrado = 0;
        //int abandono = 0;

        for (int i = 0; i < result.getLstResultado().size(); i++) {
            ConsultaRespiratorioDTO consultaRepiratoria = result.getLstResultado().get(i);
            TableRow tbrow = new TableRow(this);
            String medico = "";
            medico = consultaRepiratoria.getUsuarioMedico();
            TextView t1v = new TextView(this);
            //t1v.setText(hojaConsultaDTO.getUsuarioMedico().equals("null") ? "" : String.valueOf(hojaConsultaDTO.getUsuarioMedico()));
            t1v.setText(medico != null ? medico : "");
            t1v.setTextColor(Color.BLACK);
            t1v.setTextSize(12);
            t1v.setPadding(5, 8, 0, 15);
            t1v.setGravity(Gravity.LEFT);
            tbrow.addView(t1v);

            TextView t2v = new TextView(this);
            t2v.setText(String.valueOf(consultaRepiratoria.getCodigoExpedinte()));
            t2v.setTextColor(Color.BLACK);
            t2v.setTextSize(12);
            t2v.setPadding(50, 8, 0, 15);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);

            TextView t3v = new TextView(this);
            t3v.setText(String.valueOf(consultaRepiratoria.getNumHojaConsulta()));
            t3v.setTextColor(Color.BLACK);
            t3v.setTextSize(12);
            t3v.setPadding(50, 8, 0, 15);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);

            TextView t4v = new TextView(this);
            t4v.setText(String.valueOf(consultaRepiratoria.getEstado()));
            t4v.setTextColor(Color.BLACK);
            t4v.setTextSize(12);
            t4v.setPadding(50, 8, 0, 15);
            t4v.setGravity(Gravity.LEFT);
            tbrow.addView(t4v);

            stk.addView(tbrow);
            if (consultaRepiratoria.getEstado().trim().equals("En Espera")) {
                enEspera++;
            }
            if (consultaRepiratoria.getEstado().trim().equals("En Consulta")) {
                enConsulta++;
            }
            if (consultaRepiratoria.getEstado().trim().equals("Laboratorio")) {
                enLab++;
            }
            if (consultaRepiratoria.getEstado().trim().equals("Cerrado")) {
                cerrado++;
            }
            /*if (consultaRepiratoria.getEstado().trim().equals("Abandono")) {
                abandono++;
            }*/
        }
        cantidadConsultaEnEspera.setText(String.valueOf(enEspera));
        cantidadEnConsultas.setText(String.valueOf(enConsulta));
        cantidadConsultasEnLab.setText(String.valueOf(enLab));
        cantidadConsultaCerradas.setText(String.valueOf(cerrado));
    }
}
