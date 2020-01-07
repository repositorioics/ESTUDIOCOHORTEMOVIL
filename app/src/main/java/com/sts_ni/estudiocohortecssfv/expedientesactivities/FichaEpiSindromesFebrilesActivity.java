package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.NavigationDrawerFragment;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.DepartamentosDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.ExpedienteWS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FichaEpiSindromesFebrilesActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static Context CONTEXT;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private static FichaEpiSindromesFebrilesActivity mFichaEpiSindromesFebrilesActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_epi_sindromes_febriles);

        this.CONTEXT = this;


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView) actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.label_ficha_epi_sindromes_febriles));
        ((TextView) actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp) getApplication()).getInfoSessionWSDTO().getUser());

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.dwlFichaEpiSindromesFebriles));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fmlFichaEpiSindromesFebriles, PlaceholderFragment.newInstance(position + 1))
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
    protected void onDestroy() {
        super.onDestroy();
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public byte[] RESPUESTA = null;

        public static FichaEpiSindromesFebrilesActivity.PlaceholderFragment newInstance(int sectionNumber) {
            FichaEpiSindromesFebrilesActivity.PlaceholderFragment fragment = new FichaEpiSindromesFebrilesActivity.PlaceholderFragment();
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
            View vigilanciaIntegrada = inflater.inflate(R.layout.formulario_ficha_epi_sindromes_febriles, container, false);
            if (savedInstanceState != null) {
            }
            return vigilanciaIntegrada;
        }

        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            this.establecerMetodosBotones();
        }

        /***
         * Establece los metodos que seran ejecutados en los eventos onClick.
         */
        protected void establecerMetodosBotones() {

            Button btnLimpiarFicha = (Button) getActivity().findViewById(R.id.btnlimpiarFichaEpi);
            btnLimpiarFicha.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction("com.sts_ni.estudiocohortecssfv.ficha_epi_sindromes_febriles_action");
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            //botón para presentar el pdf de la ficha vigilancia de infecciones respiratorias
            ImageButton ibtSeguimientoPDF = (ImageButton) getActivity().findViewById(R.id.ibtFichaEpiPDF);
            ibtSeguimientoPDF.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    String mensajeBusqueda = "Debe de ingresar el numero de hoja de consulta";
                    if (StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNumHojaConsulta)).getText().toString())) {
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                mensajeBusqueda, getResources().getString(
                                        R.string.title_estudio_sostenible),
                                null);
                        return;
                    } else {
                        String numHojaConsulta = (((EditText) getActivity().findViewById(R.id.edtxtNumHojaConsulta)).getText().toString());
                        obtenerFichaPdf(Integer.parseInt(numHojaConsulta));
                    }
                    /*else{
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                getResources().getString(R.string.msj_sin_detalle_ficha),
                                getResources().getString(R.string.app_name), null);
                    }*/
                }
            });

            //boton para imprimir la ficha vigilancia de infecciones respiratorias
            ImageButton btnImprimirFicha = (ImageButton) getActivity().findViewById(R.id.ibtImprimirFichaEpi);
            btnImprimirFicha.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    String mensajeBusqueda = "Debe de ingresar el numero de hoja de consulta";
                    if (StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNumHojaConsulta)).getText().toString())) {
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                mensajeBusqueda, getResources().getString(
                                        R.string.title_estudio_sostenible),
                                null);
                        return;
                    } else {
                        String numHojaConsulta = (((EditText) getActivity().findViewById(R.id.edtxtNumHojaConsulta)).getText().toString());
                        ImprimirFichaPdf(Integer.parseInt(numHojaConsulta));
                    }
                    /*else{
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                getResources().getString(R.string.msj_sin_detalle_seg_influenza),
                                getResources().getString(R.string.app_name), null);
                    }*/
                }
            });
        }

        /***
         * Metodo para llamar el servicio que obtiene el seguimiento zika PDF.
         */
        private void obtenerFichaPdf(final int numHojaConsulta) {
            /*Creando una tarea asincrona*/
            AsyncTask<Void, Void, Void> fichaPdf = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private ExpedienteWS ESPEDIENTEWS = new ExpedienteWS(getResources());

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
                    if (NET_INFO != null && NET_INFO.isConnected()) {
                        RESPUESTA = ESPEDIENTEWS.getFichaEpiPdf(numHojaConsulta);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    PD.dismiss();

                    try {

                        File file = new File("/sdcard/fichaSindFebriles.pdf");
                        FileOutputStream fos = new FileOutputStream(file);
                        String filepath = "/sdcard/fichaSindFebriles.pdf";
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            fichaPdf.execute((Void[]) null);
        }

        /***
         * Metodo para llamar servicio que imprime la ficha.
         */
        private void ImprimirFichaPdf(final int numHojaConsulta) {
            /*Creando una tarea asincrona*/
            AsyncTask<Void, Void, Void> ImprimirFichaPdf = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private ExpedienteWS EXPEDIENTE = new ExpedienteWS(getResources());

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
                    if (NET_INFO != null && NET_INFO.isConnected()) {
                        EXPEDIENTE.ImprimirFichaEpiPdf(numHojaConsulta);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    PD.dismiss();
                    try {
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                "Se envio la ficha a impresión", getResources().getString(
                                        R.string.app_name), null);

                    } catch (Exception e) {
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                "Ocurrio un problema al intentar imprimir", getResources().getString(
                                        R.string.app_name), null);
                    }

                }
            };
            ImprimirFichaPdf.execute((Void[]) null);
        }
    }
}
