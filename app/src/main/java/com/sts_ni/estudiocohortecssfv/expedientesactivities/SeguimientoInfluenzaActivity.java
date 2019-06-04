package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.Activity;
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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.NavigationDrawerFragment;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.HojaInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.dto.MedicosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeguimientoInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.BuscarDialogMedico;
import com.sts_ni.estudiocohortecssfv.tools.DatePickerFragment;
import com.sts_ni.estudiocohortecssfv.tools.DecimalDigitsInputFilter;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.SeguimientoInfluenzaWS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Controlador de la UI de Seguimiento Influenza
 */
public class SeguimientoInfluenzaActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    public   ArrayAdapter<CharSequence> adapter;
    public BuscarSeguimientoTask mBuscarSeguimientoTask;
    public GuardarSeguimientoTask mGuardarSeguimientoTask;
    public CerrarHojaSeguimientoTask mCerrarHojaSeguimientoTask;
    public AsyncTask<Void, Void, Void> mObtenerMedicosTask;
    public int mCodExp = 0;
    public int mNumSeg = 0;
    private static SeguimientoInfluenzaActivity mSeguimientoInfluenzaActivity;
    private static boolean existeDetalle;
    private static boolean esCargaInicial;
    private static String textoBusquedaMedico;
    private static ResultadoListWSDTO<MedicosDTO> RESPUESTAMEDICO;
    private static ArrayAdapter<MedicosDTO> adapterMedico1;
    private static ArrayAdapter<MedicosDTO> adapterMedico2;
    private static ArrayAdapter<MedicosDTO> adapterMedico3;
    private static ArrayAdapter<MedicosDTO> adapterMedico4;
    private static ArrayAdapter<MedicosDTO> adapterMedico5;
    private static ArrayAdapter<MedicosDTO> adapterMedico6;
    private static ArrayAdapter<MedicosDTO> adapterMedico7;
    private static ArrayAdapter<MedicosDTO> adapterMedico8;
    private static ArrayAdapter<MedicosDTO> adapterMedico9;
    private static ArrayAdapter<MedicosDTO> adapterMedico10;
    private static ArrayAdapter<MedicosDTO> adapterMedico11;
    private static ArrayAdapter<MedicosDTO> adapterMedico12;

    public static Context CONTEXT;
    public String mNombrePaciente = null;
    public String mEstudio = null;
    public String mFis = null;
    public String mFif = null;
    public ArrayList<SeguimientoInfluenzaDTO> listaSegInfluenza;
    public ArrayList<SeguimientoInfluenzaDTO> nuevaListaSegInfluenza;
    public static boolean isEmptyFIF = false;
    //public static int posicionMedico = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento_influenza);

        mSeguimientoInfluenzaActivity = this;
        CONTEXT = this;
        listaSegInfluenza = new ArrayList<>();
        nuevaListaSegInfluenza = new ArrayList<>();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView) actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.label_seguimiento_influenza));
        ((TextView) actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp) getApplication()).getInfoSessionWSDTO().getUser());
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.dwlSeguimientoInfluenza));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fmlSeguimientoInfluenza, PlaceholderFragment.newInstance(position + 1))
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

        cancelarBusquedaTask();
        cancelarGuardarSeguimientoTask();
        cancelarCerrarSeguimientoTask();
        cancelarObtenerMedicoTask();
    }

    /***
     * Cancela tarea busqueda al destruir el activity.
     */
    private void cancelarBusquedaTask() {
        if (mBuscarSeguimientoTask != null && mBuscarSeguimientoTask.getStatus() == UserTask.Status.RUNNING) {
            mBuscarSeguimientoTask.cancel(true);
            mBuscarSeguimientoTask = null;
        }
    }

    /***
     * Cancela tarea guardar seguimiento al destruir el activity.
     */
    private void cancelarGuardarSeguimientoTask() {
        if (mGuardarSeguimientoTask != null && mGuardarSeguimientoTask.getStatus() == UserTask.Status.RUNNING) {
            mGuardarSeguimientoTask.cancel(true);
            mGuardarSeguimientoTask = null;
        }
    }

    /***
     * Cancela tarea cerrar seguimiento al destruir el activity.
     */
    private void cancelarCerrarSeguimientoTask() {
        if (mCerrarHojaSeguimientoTask != null && mCerrarHojaSeguimientoTask.getStatus() == UserTask.Status.RUNNING) {
            mCerrarHojaSeguimientoTask.cancel(true);
            mCerrarHojaSeguimientoTask = null;
        }
    }

    private void cancelarObtenerMedicoTask() {
        if (mObtenerMedicosTask != null && mObtenerMedicosTask.getStatus() == AsyncTask.Status.RUNNING) {
            mObtenerMedicosTask.cancel(true);
            mObtenerMedicosTask = null;
        }
    }

    /***
     * Carga los datos obtenidos al crear hoja influenza.
     * @param nombrePaciente, Nombre del paciente
     * @param estudioPaciente, Estudios del paciente
     * @param hojaInfluenza, Hoja Influenza
     */
    public void cargarDatosCrearHojaUI(String nombrePaciente, String estudioPaciente, HojaInfluenzaDTO hojaInfluenza) {

        this.mNumSeg = hojaInfluenza.getNumHojaSeguimiento();
        this.mCodExp = hojaInfluenza.getCodExpediente();
        this.findViewById(R.id.ibtCrearHoja).setEnabled(false);
        this.findViewById(R.id.ibtBuscarExp).setEnabled(false);

        ((EditText) this.findViewById(R.id.edtxtNombreApellido)).
                setText(nombrePaciente);
        ((EditText) this.findViewById(R.id.edtxtEstudioPacienteSeguimiento)).
                setText(estudioPaciente);
        ((EditText) this.findViewById(R.id.edtxtNumSeguimiento)).
                setText(String.valueOf(hojaInfluenza.getNumHojaSeguimiento()));

        ((EditText) this.findViewById(R.id.edtxtFIS)).setText(hojaInfluenza.getFis());

        ((EditText) this.findViewById(R.id.edtxtFIF)).setText(hojaInfluenza.getFif());

        ViewPager viewPager = (ViewPager) this.findViewById(R.id.vpDias);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(PlaceholderFragment.mPagerAdapter);

        viewPager.setVisibility(View.VISIBLE);

        findViewById(R.id.btnGuardar).setEnabled(true);
        findViewById(R.id.ibtImprimir).setEnabled(true);
        findViewById(R.id.btnCerrarSeguimiento).setEnabled(true);
    }

    /***
     * Carga los datos obtenidos atravez de buscar Hoja Influenza.
     * @param hojaInfluenza, Hoja Influenza
     */
    public void cargarDatosInfluenzaUI(HojaInfluenzaDTO hojaInfluenza) {

        this.mCodExp = hojaInfluenza.getCodExpediente();
        this.mNumSeg = hojaInfluenza.getNumHojaSeguimiento();
        this.findViewById(R.id.ibtCrearHoja).setEnabled(false);
        this.findViewById(R.id.ibtBuscarExp).setEnabled(false);

        ((EditText) this.findViewById(R.id.edtxtNombreApellido)).setText(hojaInfluenza.getNomPaciente());

        ((EditText) this.findViewById(R.id.edtxtEstudioPacienteSeguimiento)).setText(hojaInfluenza.getEstudioPaciente());

        ((EditText) this.findViewById(R.id.edtxtCodExpediente)).setText(String.valueOf(hojaInfluenza.getCodExpediente()));

        ((EditText) this.findViewById(R.id.edtxtNumSeguimiento)).setText(String.valueOf(hojaInfluenza.getNumHojaSeguimiento()));

        ((EditText) this.findViewById(R.id.edtxtFIS)).setText(hojaInfluenza.getFis());

        ((EditText) this.findViewById(R.id.edtxtFIF)).setText(hojaInfluenza.getFif());

        /*Si la FIF es null o vacia y existe detalle en el seguimiento inlfuenza
           habilitamos el (EditText = edtxtFIF )para poder ingresar la FIF*/
        if (StringUtils.isNullOrEmpty(hojaInfluenza.getFif())
                && (hojaInfluenza.getLstSeguimientoInfluenza() != null && hojaInfluenza.getLstSeguimientoInfluenza().size() > 0)) {
            this.findViewById(R.id.edtxtFIF).setEnabled(true);
            isEmptyFIF = true;
            ((EditText) this.findViewById(R.id.edtxtFIF)).setKeyListener(null);
        }

        this.findViewById(R.id.ibtImprimir).setEnabled(true);

        if (hojaInfluenza.getCerrado() == 'S') {
            this.findViewById(R.id.btnGuardar).setEnabled(false);
            this.findViewById(R.id.btnCerrarSeguimiento).setEnabled(false);
        }
        else {
            this.findViewById(R.id.btnGuardar).setEnabled(true);
            this.findViewById(R.id.btnCerrarSeguimiento).setEnabled(true);
        }

        if(hojaInfluenza.getLstSeguimientoInfluenza() != null && hojaInfluenza.getLstSeguimientoInfluenza().size() > 0){
            existeDetalle = true;
            //bandera para presentar el aviso de la fecha de seguimiento
            boolean presentaAviso = false;
            String mensajeAviso = "";

            for(SeguimientoInfluenzaDTO seg : hojaInfluenza.getLstSeguimientoInfluenza()) {
                //cargnado los datos de seguimientos
                listaSegInfluenza.add(seg);

                if(hojaInfluenza.getCerrado() == 'N' && seg.getControlDia() == 1 && !StringUtils.isNullOrEmpty(seg.getFechaSeguimiento())){
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date fSeg = sdf.parse(seg.getFechaSeguimiento());
                        String hoy = sdf.format(new Date());
                        Date dNow = sdf.parse(hoy);

                        long diferenciaEn_ms = dNow.getTime() - fSeg.getTime();
                        long dias = diferenciaEn_ms / (1000 * 60 * 60 * 24);

                        if(dias >= 14){ /* dias >= 5 / Ahora son 14 dias */
                            presentaAviso = true;
                            mensajeAviso = String.format(getResources().getString(R.string.msj_aviso_fecha_cierre_seguimiento), dias);
                        }

                    }catch (ParseException e) {
                        e.printStackTrace();
                        MensajesHelper.mostrarMensajeError(CONTEXT,
                                getResources().getString(R.string.msj_error_no_controlado), getResources().getString(R.string.app_name), null);
                    }
                }
            }

            if(presentaAviso) {
                MensajesHelper.mostrarMensajeInfo(CONTEXT, mensajeAviso, getResources().getString(R.string.app_name), null);
            }
        }

        ViewPager viewPager = (ViewPager) this.findViewById(R.id.vpDias);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(PlaceholderFragment.mPagerAdapter);

        viewPager.setVisibility(View.VISIBLE);
    }

    public void actualizarObjetos() {
        if(!nuevaListaSegInfluenza.isEmpty()) {
            for(SeguimientoInfluenzaDTO seguimientoInfluenza : nuevaListaSegInfluenza) {
                if (!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFechaSeguimiento())) {
                    if((seguimientoInfluenza.getUsuarioMedico() > 0) &&
                            !StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela()) &&
                            !StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama()) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getCongestionNasa())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getConsultaInicial())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDificultadRespirar())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorArticular())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorCabeza())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorGarganta())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorMuscular())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorOido())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getFaltaApetito())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getFiebre())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getRespiracionRapida())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getSecrecionNasal())) &&
                            !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getTos()))) {
                        listaSegInfluenza.add(seguimientoInfluenza);
                    }
                }
            }
            if(!listaSegInfluenza.isEmpty()) {
                PlaceholderFragment.mPagerAdapter.notifyDataSetChanged();
                existeDetalle = true;
            }
        }
    }
    /***
     * Metodo que limpia los datos que se encuentra en cargado en la UI.
     */
    public void limpiarControles() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle(getResources().getString(R.string.title_limpiando));
        pd.setMessage(getResources().getString(R.string.msj_espere_por_favor));
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
        this.mCodExp = 0;
        this.mNumSeg = 0;
        //this.posicionMedico = 0;
        this.findViewById(R.id.ibtCrearHoja).setEnabled(true);
        this.findViewById(R.id.ibtBuscarExp).setEnabled(true);
        this.findViewById(R.id.edtxtNumSeguimiento).setEnabled(true);

        ((EditText) this.findViewById(R.id.edtxtNombreApellido)).setText("");

        ((EditText) this.findViewById(R.id.edtxtEstudioPacienteSeguimiento)).setText("");

        ((EditText) this.findViewById(R.id.edtxtCodExpediente)).setText("");

        ((EditText) this.findViewById(R.id.edtxtNumSeguimiento)).setText("");

        ((EditText) this.findViewById(R.id.edtxtFIS)).setText("");

        ((EditText) this.findViewById(R.id.edtxtFIF)).setText("");

        this.findViewById(R.id.ibtImprimir).setEnabled(false);
        this.findViewById(R.id.btnGuardar).setEnabled(false);
        this.findViewById(R.id.btnCerrarSeguimiento).setEnabled(false);

        listaSegInfluenza = new ArrayList<>();
        nuevaListaSegInfluenza = new ArrayList<>();
        esCargaInicial = true;
        ViewPager viewPager = (ViewPager) this.findViewById(R.id.vpDias);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return false;
            }
        });
        viewPager.setVisibility(View.INVISIBLE);
        PlaceholderFragment.llamadoListaInicialMedicoServicio();

        pd.dismiss();
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

        public byte[] RESPUESTA = null;

        public static SegumientoPagerAdapter mPagerAdapter;

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
            View seguiminetoLayout = inflater.inflate(R.layout.seguimiento_influenza_layout, container, false);

            mPagerAdapter = new SegumientoPagerAdapter(getChildFragmentManager());

            if (savedInstanceState != null){

                mSeguimientoInfluenzaActivity.mCodExp = savedInstanceState.getInt("codExp");
                mSeguimientoInfluenzaActivity.mNumSeg = savedInstanceState.getInt("NumSeg");

                mSeguimientoInfluenzaActivity.mNombrePaciente = savedInstanceState.getString("nombreApellido");
                mSeguimientoInfluenzaActivity.mEstudio = savedInstanceState.getString("estudios");
                mSeguimientoInfluenzaActivity.mFis = savedInstanceState.getString("fis");
                mSeguimientoInfluenzaActivity.mFif = savedInstanceState.getString("fif");

                mSeguimientoInfluenzaActivity.listaSegInfluenza = (ArrayList<SeguimientoInfluenzaDTO>) savedInstanceState.getSerializable("listaSeguimiento");
                mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza = (ArrayList<SeguimientoInfluenzaDTO>) savedInstanceState.getSerializable("nuevaListaSeguimiento");
            }
            if(mSeguimientoInfluenzaActivity.mNumSeg > 0 && mSeguimientoInfluenzaActivity.mCodExp > 0) {
                ViewPager viewPager = (ViewPager) seguiminetoLayout.findViewById(R.id.vpDias);
                viewPager.setOffscreenPageLimit(2);
                viewPager.setAdapter(mPagerAdapter);
            } else {
                ViewPager viewPager = (ViewPager) seguiminetoLayout.findViewById(R.id.vpDias);
                viewPager.setVisibility(View.INVISIBLE);
            }
            inicializarControlFif(seguiminetoLayout);
            return seguiminetoLayout;
        }

        @Override
        public void onSaveInstanceState (Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("codExp", mSeguimientoInfluenzaActivity.mCodExp);
            outState.putInt("NumSeg", mSeguimientoInfluenzaActivity.mNumSeg);
            outState.putString("nombreApellido", ((EditText) getActivity().findViewById(R.id.edtxtNombreApellido)).getText().toString());
            outState.putString("estudios", ((EditText) getActivity().findViewById(R.id.edtxtEstudioPacienteSeguimiento)).getText().toString());
            outState.putString("fis", ((EditText) getActivity().findViewById(R.id.edtxtFIS)).getText().toString());
            outState.putString("fif", ((EditText) getActivity().findViewById(R.id.edtxtFIF)).getText().toString());
            outState.putSerializable("listaSeguimiento", mSeguimientoInfluenzaActivity.listaSegInfluenza);
            outState.putSerializable("nuevaListaSeguimiento", mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            //--------------------------------------------------------------------------------------

            /*if(mSeguimientoInfluenzaActivity.mNumSeg > 0 && mSeguimientoInfluenzaActivity.mCodExp > 0) {
                ViewPager viewPager = (ViewPager) mSeguimientoInfluenzaActivity.findViewById(R.id.vpDias);
                viewPager.setVisibility(View.INVISIBLE);

            } else {
                ViewPager viewPager = (ViewPager) mSeguimientoInfluenzaActivity.findViewById(R.id.vpDias);
                viewPager.setOffscreenPageLimit(2);
                viewPager.setAdapter(mPagerAdapter);
                mSeguimientoInfluenzaActivity.listaSegInfluenza = (ArrayList<SeguimientoInfluenzaDTO>) savedInstanceState.getSerializable("listaSeguimiento");
                mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza = (ArrayList<SeguimientoInfluenzaDTO>) savedInstanceState.getSerializable("nuevaListaSeguimiento");
                esCargaInicial = true;
            }*/
            //--------------------------------------------------------------------------------------

            //cargando los spinner con todos los médicos
            esCargaInicial = true;
            llamadoListaInicialMedicoServicio();

            getActivity().findViewById(R.id.edtxtNombreApellido).setEnabled(false);

            getActivity().findViewById(R.id.btnGuardar).setEnabled(false);

            getActivity().findViewById(R.id.ibtImprimir).setEnabled(false);

            getActivity().findViewById(R.id.btnCerrarSeguimiento).setEnabled(false);

            this.establecerMetodosBotones();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public void onStart() {
            super.onStart();

            if (mSeguimientoInfluenzaActivity.mNombrePaciente != null && mSeguimientoInfluenzaActivity.mEstudio != null &&
                    mSeguimientoInfluenzaActivity.mFif != null && mSeguimientoInfluenzaActivity.mFif != null
                    && mSeguimientoInfluenzaActivity.mCodExp > 0 && mSeguimientoInfluenzaActivity.mNumSeg > 0) {
                ((EditText) getActivity().findViewById(R.id.edtxtCodExpediente)).setText(String.valueOf(mSeguimientoInfluenzaActivity.mCodExp));
                ((EditText) getActivity().findViewById(R.id.edtxtNumSeguimiento)).setText(String.valueOf(mSeguimientoInfluenzaActivity.mNumSeg));
                ((EditText) getActivity().findViewById(R.id.edtxtNombreApellido)).setText(mSeguimientoInfluenzaActivity.mNombrePaciente);
                ((EditText) getActivity().findViewById(R.id.edtxtEstudioPacienteSeguimiento)).setText(mSeguimientoInfluenzaActivity.mEstudio);
                ((EditText) getActivity().findViewById(R.id.edtxtFIS)).setText(mSeguimientoInfluenzaActivity.mFis);
                ((EditText) getActivity().findViewById(R.id.edtxtFIF)).setText(mSeguimientoInfluenzaActivity.mFif);
            }
        }

        /*
        * Metodo para inicializar el ingreso de la fecha FIF
        * */
        public void inicializarControlFif(View rootView) {
            rootView.findViewById(R.id.edtxtFIF).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialogFIF(view);
                }
            });
        }

        /*Ingreso fif*/
        public void showDatePickerDialogFIF(View view) {
            DialogFragment newFragment = new DatePickerFragment(){
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    ((EditText) getActivity().findViewById(R.id.edtxtFIF)).setError(null);
                    //((EditText) getActivity().findViewById(R.id.edtxtFIF)).setText(null);
                    if(DateUtils.esMayorFechaHoy(calendar)){
                        ((EditText)getActivity().findViewById(R.id.edtxtFIF)).setError(getString(R.string.msj_fecha_fif_mayor_hoy));
                    }
                    ((EditText) getActivity().findViewById(R.id.edtxtFIF)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            };
            newFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.title_date_picker));
        }

        /***
         * Establece los metodos que seran ejecutados en los eventos onClick.
         */
        protected void establecerMetodosBotones() {
            Button btnCrearHoja = (Button) getActivity().findViewById(R.id.ibtCrearHoja);
            btnCrearHoja.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    existeDetalle = false;
                    onCrearHoja();
                }
            });

            Button btnBuscarExp = (Button) getActivity().findViewById(R.id.ibtBuscarExp);
            btnBuscarExp.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    existeDetalle = false;
                    buscarPacienteLupaServicio();
                }
            });

            Button btnGuardar = (Button) getActivity().findViewById(R.id.btnGuardar);
            btnGuardar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    DialogInterface.OnClickListener preguntaGuardarDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    guardarHojaInfluenza();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    MensajesHelper.mostrarMensajeYesNo(getActivity(), getResources().getString(
                            R.string.msj_aviso_guardar_hoja_influenza), getResources().getString(
                            R.string.title_estudio_sostenible),preguntaGuardarDialogClickListener);
                }
            });


            ImageButton btnImprimir = (ImageButton) getActivity().findViewById(R.id.ibtImprimir);
            btnImprimir.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(existeDetalle) {
                        ImprimirHojaSeguimientoaPdf();
                    }else{
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                getResources().getString(R.string.msj_sin_detalle_seg_influenza),
                                getResources().getString(R.string.app_name), null);
                    }
                }
            });

            //botón para presentar el pdf de la hoja de influenza
            //Quitar Comentario MartinMoreno
            ImageButton ibtSeguimientoPDF = (ImageButton) getActivity().findViewById(R.id.ibtSeguimientoPDF);
            ibtSeguimientoPDF.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(existeDetalle) {
                        obtenerSeguimientoInflunzaPdf();
                    } else {
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                getResources().getString(R.string.msj_sin_detalle_seg_influenza),
                                getResources().getString(R.string.app_name), null);
                    }
                }
            });

            Button btnCerrarSeg = (Button) getActivity().findViewById(R.id.btnCerrarSeguimiento);
            btnCerrarSeg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //pregunta si quiere realizar cierre
                    DialogInterface.OnClickListener realizarCierreDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    cerrarHojaInfluenza();

                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_cierre_seguimiento_influenza), getResources().getString(
                                    R.string.title_estudio_sostenible), realizarCierreDialogClickListener);
                }
            });

            Button btnLimpiarSeguimiento = (Button) getActivity().findViewById(R.id.btnlimpiarSeguimiento);
            btnLimpiarSeguimiento.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    /*existeDetalle = false;
                    mPagerAdapter = new SegumientoPagerAdapter(getChildFragmentManager());
                    ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.vpDias);
                    mSeguimientoInfluenzaActivity.limpiarControles();
                    mPagerAdapter.notifyDataSetChanged();
                    viewPager.setVisibility(View.INVISIBLE);*/
                    Intent intent = new Intent();
                    intent.setAction("com.sts_ni.estudiocohortecssfv.seguimiento_influenza_action");
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }

        /***
         * Metodo para llamar el servicio que crea Hoja de seguimiento.
         */
        private void onCrearHoja() {
            if (StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtCodExpediente)).getText().toString())) {
                MensajesHelper.mostrarMensajeError(getActivity(), getResources().getString(R.string.msj_ingresar_codigo_paciente),
                        getResources().getString(R.string.title_estudio_sostenible), null);
                return;
            }
            getActivity().findViewById(R.id.edtxtNumSeguimiento).setEnabled(false);
            if (mSeguimientoInfluenzaActivity.mBuscarSeguimientoTask == null ||
                    mSeguimientoInfluenzaActivity.mBuscarSeguimientoTask.getStatus() == BuscarSeguimientoTask.Status.FINISHED) {
                mSeguimientoInfluenzaActivity.mBuscarSeguimientoTask = (BuscarSeguimientoTask) new
                        BuscarSeguimientoTask(mSeguimientoInfluenzaActivity).execute("0", ((EditText) getActivity().
                        findViewById(R.id.edtxtCodExpediente)).getText().toString());
            }
        }

        /***
         * Metodo para llamar el servicio que realiza la busqueda por codigo expediente o numero de seguimiento.
         */
        private void buscarPacienteLupaServicio() {
            if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtCodExpediente)).getText().toString())) {

                if (mSeguimientoInfluenzaActivity.mBuscarSeguimientoTask == null ||
                        mSeguimientoInfluenzaActivity.mBuscarSeguimientoTask.getStatus() == BuscarSeguimientoTask.Status.FINISHED) {
                    mSeguimientoInfluenzaActivity.mBuscarSeguimientoTask = (BuscarSeguimientoTask) new
                            BuscarSeguimientoTask(mSeguimientoInfluenzaActivity).execute("1", ((EditText) getActivity().
                            findViewById(R.id.edtxtCodExpediente)).getText().toString());
                }

            } else if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNumSeguimiento)).getText().toString())) {

                if (mSeguimientoInfluenzaActivity.mBuscarSeguimientoTask == null ||
                        mSeguimientoInfluenzaActivity.mBuscarSeguimientoTask.getStatus() == BuscarSeguimientoTask.Status.FINISHED) {
                    mSeguimientoInfluenzaActivity.mBuscarSeguimientoTask = (BuscarSeguimientoTask) new
                            BuscarSeguimientoTask(mSeguimientoInfluenzaActivity).execute("2", ((EditText) getActivity().
                            findViewById(R.id.edtxtNumSeguimiento)).getText().toString());
                }
            }
            else{
                MensajesHelper.mostrarMensajeError(getActivity(), getResources().getString(R.string.msj_param_seguimiento_invalido),
                        getResources().getString(R.string.title_estudio_sostenible), null);
            }
        }

        /***
         * Metodo que llama servicio para guardar desde la pantalla seguimiento de influenza
         */
        private void guardarHojaInfluenza() {

            if(mSeguimientoInfluenzaActivity.mCodExp > 0 || mSeguimientoInfluenzaActivity.mNumSeg > 0) {
                if (mSeguimientoInfluenzaActivity.mGuardarSeguimientoTask == null ||
                        mSeguimientoInfluenzaActivity.mGuardarSeguimientoTask.getStatus() == GuardarSeguimientoTask.Status.FINISHED) {

                    /*Verificamos que la FIF no sea vacia o null y isEmptyFIF = true*/
                    if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtFIF)).getText().toString())
                            && isEmptyFIF) {

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            ((EditText) getActivity().findViewById(R.id.edtxtFIF)).setError(null);
                            Calendar fif = Calendar.getInstance();
                            Calendar currentDate = Calendar.getInstance();
                            fif.setTime(sdf.parse(((EditText) getActivity().findViewById(R.id.edtxtFIF)).getText().toString()));
                            Date dateFIS = sdf.parse(((EditText) getActivity().findViewById(R.id.edtxtFIS)).getText().toString());
                            Date dateFIF = sdf.parse(((EditText) getActivity().findViewById(R.id.edtxtFIF)).getText().toString());
                            /*Si la FIF es menor que la FIS retornamos y enviamos mensaje*/
                            if (dateFIF.compareTo(dateFIS) < 0) {
                                ((EditText) getActivity().findViewById(R.id.edtxtFIF)).setError(getString(R.string.msj_fif_menor_que_fis));
                                return;
                            }
                            /*Si la FIF es mayor a la fecha actual retornamos y enviamos mensaje*/
                            if(fif.after(currentDate)) {
                                ((EditText)getActivity().findViewById(R.id.edtxtFIF)).setError(getString(R.string.msj_fecha_fif_mayor_hoy));
                                return;
                            }
                            isEmptyFIF = false;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    HojaInfluenzaDTO hojaInfluenza = new HojaInfluenzaDTO();

                    hojaInfluenza.setCodExpediente(mSeguimientoInfluenzaActivity.mCodExp);
                    hojaInfluenza.setNumHojaSeguimiento(mSeguimientoInfluenzaActivity.mNumSeg);
                    hojaInfluenza.setFif(((EditText) getActivity().findViewById(R.id.edtxtFIF)).getText().toString());
                    hojaInfluenza.setFis(((EditText) getActivity().findViewById(R.id.edtxtFIS)).getText().toString());

                    hojaInfluenza.setCerrado('N');

                    getActivity().findViewById(R.id.edtxtFIF).setEnabled(false);
                    ArrayList<SeguimientoInfluenzaDTO> lstSeg = new ArrayList<>();

                    for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza) {
                        if (!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFechaSeguimiento())) {
                            if((seguimientoInfluenza.getUsuarioMedico() > 0) &&
                                    !StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela()) &&
                                    !StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama()) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getCongestionNasa())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getConsultaInicial())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDificultadRespirar())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorArticular())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorCabeza())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorGarganta())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorMuscular())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorOido())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getFaltaApetito())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getFiebre())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getRespiracionRapida())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getSecrecionNasal())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getTos()))) {
                                lstSeg.add(seguimientoInfluenza);
                            } else {
                                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento),
                                        String.valueOf(seguimientoInfluenza.getControlDia())),
                                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                                return;
                            }
                        }
                    }
                    if (verificarDiasContinuos(lstSeg)) {
                        hojaInfluenza.setLstSeguimientoInfluenza(lstSeg);
                        mSeguimientoInfluenzaActivity.mGuardarSeguimientoTask = (GuardarSeguimientoTask) new
                                GuardarSeguimientoTask(mSeguimientoInfluenzaActivity).execute(hojaInfluenza);
                    }
                }
            }
        }

        /**
         * Metodo para verificar que no se salten el llenado de dias en la hoja de influenza,
         * para no dejar el dia anterior sin datos
         */
        public boolean verificarDiasContinuos(ArrayList<SeguimientoInfluenzaDTO> lstSeg) {
            SeguimientoInfluenzaDTO seguimientoInfluenzaByDias = new SeguimientoInfluenzaDTO();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia1 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia2 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia3 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia4 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia5 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia6 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia7 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia8 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia9 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia10 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia11 = new ArrayList<>();
            ArrayList<SeguimientoInfluenzaDTO> lstSegDia12 = new ArrayList<>();
            for (int i = 0; i < lstSeg.size(); i++) {
                seguimientoInfluenzaByDias = lstSeg.get(i);
                if (seguimientoInfluenzaByDias.getControlDia() == 1) {
                    lstSegDia1.add(seguimientoInfluenzaByDias);
                }
                if(seguimientoInfluenzaByDias.getControlDia() == 2) {
                    lstSegDia2.add(seguimientoInfluenzaByDias);
                }
                if (seguimientoInfluenzaByDias.getControlDia() == 3) {
                    lstSegDia3.add(seguimientoInfluenzaByDias);
                }
                if(seguimientoInfluenzaByDias.getControlDia() == 4) {
                    lstSegDia4.add(seguimientoInfluenzaByDias);
                }
                if(seguimientoInfluenzaByDias.getControlDia() == 5) {
                    lstSegDia5.add(seguimientoInfluenzaByDias);
                }
                if(seguimientoInfluenzaByDias.getControlDia() == 6) {
                    lstSegDia6.add(seguimientoInfluenzaByDias);
                }
                if(seguimientoInfluenzaByDias.getControlDia() == 7) {
                    lstSegDia7.add(seguimientoInfluenzaByDias);
                }
                if(seguimientoInfluenzaByDias.getControlDia() == 8) {
                    lstSegDia8.add(seguimientoInfluenzaByDias);
                }
                if(seguimientoInfluenzaByDias.getControlDia() == 9) {
                    lstSegDia9.add(seguimientoInfluenzaByDias);
                }
                if(seguimientoInfluenzaByDias.getControlDia() == 10) {
                    lstSegDia10.add(seguimientoInfluenzaByDias);
                }
                if(seguimientoInfluenzaByDias.getControlDia() == 11) {
                    lstSegDia11.add(seguimientoInfluenzaByDias);
                }
                if(seguimientoInfluenzaByDias.getControlDia() == 12) {
                    lstSegDia12.add(seguimientoInfluenzaByDias);
                }
            }

            if(lstSegDia2.size() > 0 && lstSegDia1.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("1")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            if(lstSegDia3.size() > 0 && lstSegDia2.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("2")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            if(lstSegDia4.size() > 0 && lstSegDia3.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("3")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            if(lstSegDia5.size() > 0 && lstSegDia4.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("4")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            if(lstSegDia6.size() > 0 && lstSegDia5.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("5")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            if(lstSegDia7.size() > 0 && lstSegDia6.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("6")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            if(lstSegDia8.size() > 0 && lstSegDia7.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("7")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            if(lstSegDia9.size() > 0 && lstSegDia8.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("8")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            if(lstSegDia10.size() > 0 && lstSegDia9.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("9")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            if(lstSegDia11.size() > 0 && lstSegDia10.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("10")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            if(lstSegDia12.size() > 0 && lstSegDia11.size() == 0) {
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento_sin_completar),
                        String.valueOf("11")),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return false;
            }
            return  true;
        }

        /**
         * Metodo que llama servicio para cerrar seguimiento de influenza
         */
        private void cerrarHojaInfluenza() {

            if (mSeguimientoInfluenzaActivity.mCodExp > 0 || mSeguimientoInfluenzaActivity.mNumSeg > 0) {
                if (mSeguimientoInfluenzaActivity.mCerrarHojaSeguimientoTask == null ||
                        mSeguimientoInfluenzaActivity.mCerrarHojaSeguimientoTask.getStatus() == CerrarHojaSeguimientoTask.Status.FINISHED) {
                    HojaInfluenzaDTO hojaInfluenza = new HojaInfluenzaDTO();

                    hojaInfluenza.setCodExpediente(mSeguimientoInfluenzaActivity.mCodExp);
                    hojaInfluenza.setNumHojaSeguimiento(mSeguimientoInfluenzaActivity.mNumSeg);
                    hojaInfluenza.setFif(((EditText) getActivity().findViewById(R.id.edtxtFIF)).getText().toString());
                    hojaInfluenza.setFis(((EditText) getActivity().findViewById(R.id.edtxtFIS)).getText().toString());
                    hojaInfluenza.setFechaCierre(Calendar.getInstance());
                    hojaInfluenza.setCerrado('S');

                    ArrayList<SeguimientoInfluenzaDTO> lstSeg = new ArrayList<>();
                    int cont = 0;
                    for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza) {
                        if (!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFechaSeguimiento())) {
                            cont ++;
                            if((seguimientoInfluenza.getUsuarioMedico() > 0) &&
                                    !StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela()) &&
                                    !StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama()) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getCongestionNasa())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getConsultaInicial())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDificultadRespirar())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorArticular())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorCabeza())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorGarganta())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorMuscular())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getDolorOido())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getFaltaApetito())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getFiebre())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getRespiracionRapida())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getSecrecionNasal())) &&
                                    !StringUtils.isNullOrEmpty(String.valueOf(seguimientoInfluenza.getTos()))) {
                                lstSeg.add(seguimientoInfluenza);
                            } else {
                                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                                        getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento),
                                        String.valueOf(seguimientoInfluenza.getControlDia())),
                                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                                return;
                            }
                        }

                        if(cont == 0){
                            MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                                    getResources().getString(R.string.msj_aviso_requerido_dia_seguimiento),
                                    String.valueOf(seguimientoInfluenza.getControlDia())),
                                    getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                            return;
                        }
                    }

                    hojaInfluenza.setLstSeguimientoInfluenza(lstSeg);
                    mSeguimientoInfluenzaActivity.mCerrarHojaSeguimientoTask = (CerrarHojaSeguimientoTask) new
                            CerrarHojaSeguimientoTask(mSeguimientoInfluenzaActivity).execute(hojaInfluenza);


                }
            }else{
                MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                        getResources().getString(R.string.msj_cierre_campos_requeridos)),
                        getActivity().getResources().getString(R.string.title_estudio_sostenible), null);
                return;
            }
        }

        /***
         * Metodo para llamar servicio que imprime el seguimiento influenza.
         */
        private void ImprimirHojaSeguimientoaPdf(){
        /*Creando una tarea asincrona*/
            AsyncTask<Void, Void, Void> ImprimirHojaSeguimientoaPdf = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private SeguimientoInfluenzaWS SEGUIMIENTO  = new SeguimientoInfluenzaWS(getResources());

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
                    if (NET_INFO != null && NET_INFO.isConnected())
                    {
                        SEGUIMIENTO.ImprimirHojaSeguimientoPdf(mSeguimientoInfluenzaActivity.mNumSeg);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result){
                    PD.dismiss();
                    try {
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                "Se envio la Hoja de seguimiento a impresión", getResources().getString(
                                        R.string.app_name), null);

                    }catch(Exception e){
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                "Ocurrio un problema al intentar imprimir", getResources().getString(
                                        R.string.app_name), null);
                    }

                }
            };
            ImprimirHojaSeguimientoaPdf.execute((Void[])null);
        }


        /***
         * Metodo para llamar el servicio que obtiene el seguimiento influenza PDF.
         */
        private void obtenerSeguimientoInflunzaPdf(){
        /*Creando una tarea asincrona*/
            AsyncTask<Void, Void, Void> seginfluenzapdf = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private SeguimientoInfluenzaWS SEGINFWS = new SeguimientoInfluenzaWS(getResources());

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
                    if (NET_INFO != null && NET_INFO.isConnected())
                    {
                        RESPUESTA = SEGINFWS.getSeguimientoInfluenzaPdf(mSeguimientoInfluenzaActivity.mNumSeg);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result){
                    PD.dismiss();

                    try {

                        File file = new File("/sdcard/SeguimientoInfluenza.pdf");
                        FileOutputStream fos = new FileOutputStream(file);
                        String filepath = "/sdcard/SeguimientoInfluenza.pdf";
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
            seginfluenzapdf.execute((Void[])null);

        }

        public static void llamadoListaInicialMedicoServicio(){

            if (mSeguimientoInfluenzaActivity.mObtenerMedicosTask == null ||
                    mSeguimientoInfluenzaActivity.mObtenerMedicosTask.getStatus() == AsyncTask.Status.FINISHED) {
                mSeguimientoInfluenzaActivity.mObtenerMedicosTask = new AsyncTask<Void, Void, Void>() {
                    private ProgressDialog PD;
                    private ConnectivityManager CM = (ConnectivityManager) CONTEXT.getSystemService(Context.CONNECTIVITY_SERVICE);

                    private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();

                    private ConsultaWS CONSULTAWS = new ConsultaWS(CONTEXT.getResources());

                    @Override
                    protected void onPreExecute() {
                        PD = new ProgressDialog(CONTEXT);
                        PD.setTitle(CONTEXT.getResources().getString(R.string.title_obteniendo));
                        PD.setMessage(CONTEXT.getResources().getString(R.string.msj_espere_por_favor));
                        PD.setCancelable(false);
                        PD.setIndeterminate(true);
                        PD.show();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        if (NET_INFO != null && NET_INFO.isConnected()) {
                            try {
                                if (!esCargaInicial) {
                                    RESPUESTAMEDICO = CONSULTAWS.getListaMedicos(textoBusquedaMedico, true);
                                } else {
                                    RESPUESTAMEDICO = CONSULTAWS.getListaMedicos(true);
                                }
                            } catch (Exception e) {
                                MensajesHelper.mostrarMensajeError(CONTEXT,
                                        e.getMessage(), CONTEXT.getResources().getString(
                                                R.string.app_name), null);

                            }

                        } else {
                            RESPUESTAMEDICO.setCodigoError(Long.parseLong("3"));
                            RESPUESTAMEDICO.setMensajeError(CONTEXT.getResources().getString(R.string.msj_no_tiene_conexion));
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        PD.dismiss();
                        if (RESPUESTAMEDICO.getCodigoError().intValue() == 0) {
                            MedicosDTO diagTO = new MedicosDTO();
                            ArrayList<MedicosDTO> diagMedico = new ArrayList<>();
                            diagTO.setPresentaCodigoPersonal(true);
                            diagTO.setIdMedico((short) 0);
                            diagTO.setNombreMedico("Seleccione Medico");
                            diagTO.setCodigoPersonal("Seleccione Medico");
                            diagMedico.add(diagTO);
                            diagMedico.addAll(RESPUESTAMEDICO.getLstResultado());

                            adapterMedico1 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico2 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico3 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico4 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico5 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico6 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico7 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico8 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico9 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico10 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico11 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            adapterMedico12 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                            adapterMedico12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            if(mPagerAdapter != null && mSeguimientoInfluenzaActivity.mCodExp > 0 &&
                                    mSeguimientoInfluenzaActivity.mNumSeg > 0) {
                                mPagerAdapter.notifyDataSetChanged();
                            }

                        } else if (RESPUESTAMEDICO.getCodigoError().intValue() != 999) {
                            MensajesHelper.mostrarMensajeInfo(CONTEXT,
                                    RESPUESTAMEDICO.getMensajeError(), CONTEXT.getResources().getString(
                                            R.string.app_name), null);

                        } else {
                            MensajesHelper.mostrarMensajeError(CONTEXT,
                                    RESPUESTAMEDICO.getMensajeError(), CONTEXT.getResources().getString(
                                            R.string.app_name), null);
                        }
                    }
                };
                mSeguimientoInfluenzaActivity.mObtenerMedicosTask.execute((Void[]) null);
            }
        }

        public class SegumientoPagerAdapter extends FragmentPagerAdapter {

            public SegumientoPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public int getCount() {
                return 12;
            }

            @Override
            public Fragment getItem(int position) {
                return DiaFragment.create(position +1);
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                DiaFragment fragment = (DiaFragment) super.instantiateItem(container, position);
                return fragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        // The first section of the app is the most interesting -- it offers
                        // a launchpad into the other demonstrations in this example application.
                        return getResources().getString(R.string.label_dia1);

                    case 1:
                        return getResources().getString(R.string.label_dia2);
                    case 2:
                        return getResources().getString(R.string.label_dia3);
                    case 3:
                        return getResources().getString(R.string.label_dia4);
                    case 4:
                        return getResources().getString(R.string.label_dia5);
                    case 5:
                        return getResources().getString(R.string.label_dia6);
                    case 6:
                        return getResources().getString(R.string.label_dia7);
                    case 7:
                        return getResources().getString(R.string.label_dia8);
                    case 8:
                        return getResources().getString(R.string.label_dia9);
                    case 9:
                        return getResources().getString(R.string.label_dia10);
                    case 10:
                        return getResources().getString(R.string.label_dia11);
                    case 11:
                        return getResources().getString(R.string.label_dia12);

                }
                return "Section " + (position + 1);
            }
        }

        public static class DiaFragment extends Fragment implements BuscarDialogMedico.DialogListener {
            public static final String ARG_PAGE = "ARG_PAGE";

            private int mPage;
            private Fragment mCorrienteFragmento;

            public static DiaFragment create(int page) {
                Bundle args = new Bundle();
                args.putInt(ARG_PAGE, page);
                DiaFragment fragment = new DiaFragment();
                fragment.setArguments(args);
                return fragment;
            }

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                mPage = getArguments().getInt(ARG_PAGE) ;
                mCorrienteFragmento = this;
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {

                mSeguimientoInfluenzaActivity.adapter = ArrayAdapter.createFromResource(getActivity(),
                        R.array.seguimiento_array, android.R.layout.simple_spinner_item);

                mSeguimientoInfluenzaActivity.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                View view = inflater.inflate(R.layout.fragment_seguimiento_dia_1, container, false);

                if(mPage == 2) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_2, container, false);
                } else if(mPage == 3) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_3, container, false);
                } else if(mPage == 4) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_4, container, false);
                } else if(mPage == 5) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_5, container, false);
                } else if(mPage == 6) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_6, container, false);
                } else if(mPage == 7) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_7, container, false);
                } else if(mPage == 8) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_8, container, false);
                } else if(mPage == 9) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_9, container, false);
                } else if(mPage == 10) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_10, container, false);
                } else if(mPage == 11) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_11, container, false);
                } else if(mPage == 12) {
                    view = inflater.inflate(R.layout.fragment_seguimiento_dia_12, container, false);
                }

                return view;
            }

            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                establecerBotones();
                establecerBotonesCleanerFechaSeg();
                establerOnClikFechasSeguimientos();

                if(mPage == 1) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia1Spn();
                    }
                } else if(mPage == 2) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia2Spn();
                    }
                } else if(mPage == 3) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia3Spn();
                    }
                } else if(mPage == 4) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia4Spn();
                    }
                } else if(mPage == 5) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia5Spn();
                    }
                } else if(mPage == 6) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia6Spn();
                    }
                } else if(mPage == 7) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia7Spn();
                    }
                } else if(mPage == 8) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia8Spn();
                    }
                }else if(mPage == 9) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia9Spn();
                    }
                }else if(mPage == 10) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia10Spn();
                    }
                }else if(mPage == 11) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia11Spn();
                    }
                }else if(mPage == 12) {
                    cargaSpinerMedicos();
                    if(mSeguimientoInfluenzaActivity.adapter != null) {
                        llenarDia12Spn();
                    }
                }

                establerCargaIU();
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
                    textoBusquedaMedico = etxtBuscar.getText().toString();
                    llamadoListaMedicoServicio();

                } else {
                    MensajesHelper.mostrarMensajeInfo(getActivity(),
                            "Ingrese un texto valido", getResources().getString(
                                    R.string.app_name), null);
                }

            }

            @Override
            public void onDialogCancelClick(DialogFragment dialog) {

            }

            public void establecerBotonesCleanerFechaSeg() {
                //botones para limpiar la fecha de seguimiento
                switch (mPage){
                    case 1:
                        ImageButton imgLimpiarSeguimiento1 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento1);
                        imgLimpiarSeguimiento1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 2:
                        ImageButton imgLimpiarSeguimiento2 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento2);
                        imgLimpiarSeguimiento2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 3:
                        ImageButton imgLimpiarSeguimiento3 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento3);
                        imgLimpiarSeguimiento3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 4:
                        ImageButton imgLimpiarSeguimiento4 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento4);
                        imgLimpiarSeguimiento4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 5:
                        ImageButton imgLimpiarSeguimiento5 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento5);
                        imgLimpiarSeguimiento5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 6:
                        ImageButton imgLimpiarSeguimiento6 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento6);
                        imgLimpiarSeguimiento6.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 7:
                        ImageButton imgLimpiarSeguimiento7 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento7);
                        imgLimpiarSeguimiento7.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 8:
                        ImageButton imgLimpiarSeguimiento8 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento8);
                        imgLimpiarSeguimiento8.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 9:
                        ImageButton imgLimpiarSeguimiento9 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento9);
                        imgLimpiarSeguimiento9.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 10:
                        ImageButton imgLimpiarSeguimiento10 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento10);
                        imgLimpiarSeguimiento10.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 11:
                        ImageButton imgLimpiarSeguimiento11 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento11);
                        imgLimpiarSeguimiento11.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });
                        break;
                    case 12:
                        ImageButton imgLimpiarSeguimiento12 = (ImageButton) getActivity().findViewById(R.id.imgLimpiarSeguimiento12);
                        imgLimpiarSeguimiento12.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                limpiarControlesTab();
                            }
                        });

                }
            }

            public void establecerBotones() {
                //botones para la busqueda de los médicos
                switch (mPage) {
                    case 1:
                        ImageButton imgBusquedaMedico1 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico1);
                        imgBusquedaMedico1.setEnabled(false);
                        imgBusquedaMedico1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;
                    case 2:
                        ImageButton imgBusquedaMedico2 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico2);
                        imgBusquedaMedico2.setEnabled(false);
                        imgBusquedaMedico2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;
                    case 3:
                        ImageButton imgBusquedaMedico3 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico3);
                        imgBusquedaMedico3.setEnabled(false);
                        imgBusquedaMedico3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;
                    case 4:
                        ImageButton imgBusquedaMedico4 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico4);
                        imgBusquedaMedico4.setEnabled(false);
                        imgBusquedaMedico4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;
                    case 5:
                        ImageButton imgBusquedaMedico5 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico5);
                        imgBusquedaMedico5.setEnabled(false);
                        imgBusquedaMedico5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;
                    case 6:
                        ImageButton imgBusquedaMedico6 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico6);
                        imgBusquedaMedico6.setEnabled(false);
                        imgBusquedaMedico6.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;
                    case 7:
                        ImageButton imgBusquedaMedico7 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico7);
                        imgBusquedaMedico7.setEnabled(false);
                        imgBusquedaMedico7.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;
                    case 8:
                        ImageButton imgBusquedaMedico8 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico8);
                        imgBusquedaMedico8.setEnabled(false);
                        imgBusquedaMedico8.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;

                    case 9:
                        ImageButton imgBusquedaMedico9 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico9);
                        imgBusquedaMedico9.setEnabled(false);
                        imgBusquedaMedico9.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;

                    case 10:
                        ImageButton imgBusquedaMedico10 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico10);
                        imgBusquedaMedico10.setEnabled(false);
                        imgBusquedaMedico10.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;
                    case 11:
                        ImageButton imgBusquedaMedico11 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico11);
                        imgBusquedaMedico11.setEnabled(false);
                        imgBusquedaMedico11.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;

                    case 12:
                        ImageButton imgBusquedaMedico12 = (ImageButton) getActivity().findViewById(R.id.imgBusquedaMedico12);
                        imgBusquedaMedico12.setEnabled(false);
                        imgBusquedaMedico12.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                esCargaInicial = false;
                                BuscarDialogMedico DlogBuscar=new BuscarDialogMedico();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                DlogBuscar.setTargetFragment(mCorrienteFragmento, 0);
                                DlogBuscar.show(fm, "Buscar");
                            }
                        });
                        break;
                }
            }

            /***
             * Establece el metodo que sera ejecutado al dar click sobre las fechas de seguimientos.
             */
            protected void establerOnClikFechasSeguimientos() {
                switch (mPage) {
                    case 1:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento1);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento1)).setKeyListener(null);
                        break;
                    case 2:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento2).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento2);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento2)).setKeyListener(null);
                        break;
                    case 3:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento3).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento3);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento3)).setKeyListener(null);
                        break;
                    case 4:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento4).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento4);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento4)).setKeyListener(null);
                        break;
                    case 5:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento5).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento5);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento5)).setKeyListener(null);
                        break;
                    case 6:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento6).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento6);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento6)).setKeyListener(null);
                        break;
                    case 7:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento7).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento7);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento7)).setKeyListener(null);
                        break;
                    case 8:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento8).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento8);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento8)).setKeyListener(null);
                        break;
                    case 9:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento9).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento9);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento9)).setKeyListener(null);
                        break;
                    case 10:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento10).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento10);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento10)).setKeyListener(null);
                        break;
                    case 11:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento11).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento11);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento11)).setKeyListener(null);
                        break;
                    case 12:
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento12).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogFechaSeguimiento(v, R.id.edtxtFechaSeguimiento12);
                            }
                        });

                        ((EditText)getActivity().findViewById(R.id.edtxtFechaSeguimiento12)).setKeyListener(null);
                        break;
                }
            }

            public void llenarDia1Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia1();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 1){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }
                }
            }

            public void llenarDia2Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia2();


                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 2){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }

                }
            }

            public void llenarDia3Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia3();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 3){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }

                }
            }

            public void llenarDia4Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia4();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 4){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }

                }
            }

            public void llenarDia5Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia5();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 5){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }

                }
            }

            public void llenarDia6Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia6();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 6){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }

                }
            }

            private void llenarDia7Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia7();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 7){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }

                }
            }

            private void llenarDia8Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia8();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 8){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }

                }
            }

            private void llenarDia9Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia9();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 9){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }

                }
            }

            private void llenarDia10Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia10();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 10){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }

                }
            }

            private void llenarDia11Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia11();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 11){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());
                    }

                }
            }

            private void llenarDia12Spn() {
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila1)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila2)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila3)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila4)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila5)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila6)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila7)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila8)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila9)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila10)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila11)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila12)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila13)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila14)).setAdapter(mSeguimientoInfluenzaActivity.adapter);
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila15)).setAdapter(mSeguimientoInfluenzaActivity.adapter);

                cargarDatosSintomasDia12();

                for(SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if (seguimientoInfluenza.getControlDia() == 12){
                        cargarFechaSeguimiento(seguimientoInfluenza.getFechaSeguimiento());

                    }

                }
            }

            public void establerCargaIU() {
                if(obtenerSeguimientoPorDia(mPage) != null) {
                    cargarIU(obtenerSeguimientoPorDia(mPage), false);
                } else if(obtenerNuevoSeguimientoPorDia(mPage) != null) {
                    cargarIU(obtenerNuevoSeguimientoPorDia(mPage), true);
                } else {
                    setMedico(((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId());
                }
            }

            public void cargarDatosSintomasDia1() {

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia1Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia2() {
                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia2Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia3() {
                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia3Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia4() {
                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia4Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia5() {
                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia5Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia6() {
                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia6Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia7() {
                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia7Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia8() {
                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia8Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia9() {
                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia9Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia10() {
                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia10Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia11() {
                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia11Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarDatosSintomasDia12() {
                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarConsultaInicial(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFiebre(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarTos(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarSecrecionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila5)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorGarganta(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila6)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarCongestionNasal(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila7)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorCabeza(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila8)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaApetito(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila9)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorMuscular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila10)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorArticular(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila11)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDolorOido(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila12)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRespiracionRapida(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila13)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarDificultadRespirar(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila14)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarFaltaEscuela(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner) getActivity().findViewById(R.id.spnDia12Fila15)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarQuedoEnCama(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            public void cargarConsultaInicial(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setConsultaInicial((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setConsultaInicial(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarFiebre(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setFiebre((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setFiebre(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarTos(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setTos((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setTos(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarSecrecionNasal(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setSecrecionNasal((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setSecrecionNasal(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarDolorGarganta(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setDolorGarganta((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setDolorGarganta(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarCongestionNasal(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setCongestionNasa((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setCongestionNasa(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarDolorCabeza(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setDolorCabeza((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setDolorCabeza(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString()
                    );
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarFaltaApetito(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setFaltaApetito((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setFaltaApetito(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarDolorMuscular(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setDolorMuscular((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setDolorMuscular(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarDolorArticular(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setDolorArticular((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setDolorArticular(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarDolorOido(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setDolorOido((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setDolorOido(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarRespiracionRapida(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setRespiracionRapida((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setRespiracionRapida(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarDificultadRespirar(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setDificultadRespirar((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setDificultadRespirar(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarFaltaEscuela(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setFaltaEscuela((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if(mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setFaltaEscuela(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarQuedoEnCama(int position) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (mSeguimientoInfluenzaActivity.adapter != null && seguimientoInfluenza != null) {
                    seguimientoInfluenza.setQuedoEnCama((mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) ?
                            mSeguimientoInfluenzaActivity.adapter.getItem(position).toString() : null);
                } else if (mSeguimientoInfluenzaActivity.adapter != null && mSeguimientoInfluenzaActivity.adapter.getItem(position).toString().compareTo("Seleccione") != 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setQuedoEnCama(mSeguimientoInfluenzaActivity.adapter.getItem(position).toString());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            /***
             * Metodo para llamar el servicio de cargar la lista de medicos.
             */
            public void llamadoListaMedicoServicio(){

                if (mSeguimientoInfluenzaActivity.mObtenerMedicosTask == null ||
                        mSeguimientoInfluenzaActivity.mObtenerMedicosTask.getStatus() == AsyncTask.Status.FINISHED) {
                    mSeguimientoInfluenzaActivity.mObtenerMedicosTask = new AsyncTask<Void, Void, Void>() {
                        private ProgressDialog PD;
                        private ConnectivityManager CM = (ConnectivityManager) CONTEXT.getSystemService(Context.CONNECTIVITY_SERVICE);

                        private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();

                        private ConsultaWS CONSULTAWS = new ConsultaWS(CONTEXT.getResources());

                        @Override
                        protected void onPreExecute() {
                            PD = new ProgressDialog(CONTEXT);
                            PD.setTitle(CONTEXT.getResources().getString(R.string.title_obteniendo));
                            PD.setMessage(CONTEXT.getResources().getString(R.string.msj_espere_por_favor));
                            PD.setCancelable(false);
                            PD.setIndeterminate(true);
                            PD.show();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            if (NET_INFO != null && NET_INFO.isConnected()) {
                                try {
                                    if (!esCargaInicial) {
                                        RESPUESTAMEDICO = CONSULTAWS.getListaMedicos(textoBusquedaMedico, true);
                                    } else {
                                        RESPUESTAMEDICO = CONSULTAWS.getListaMedicos(true);
                                    }
                                } catch (Exception e) {
                                    MensajesHelper.mostrarMensajeError(CONTEXT,
                                            e.getMessage(), CONTEXT.getResources().getString(
                                                    R.string.app_name), null);

                                }

                            } else {
                                RESPUESTAMEDICO.setCodigoError(Long.parseLong("3"));
                                RESPUESTAMEDICO.setMensajeError(CONTEXT.getResources().getString(R.string.msj_no_tiene_conexion));
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            PD.dismiss();
                            if (RESPUESTAMEDICO.getCodigoError().intValue() == 0) {
                                MedicosDTO diagTO = new MedicosDTO();
                                ArrayList<MedicosDTO> diagMedico = new ArrayList<>();
                                diagTO.setPresentaCodigoPersonal(true);
                                diagTO.setIdMedico((short) 0);
                                diagTO.setNombreMedico("Seleccione Medico");
                                diagTO.setCodigoPersonal("Seleccione Medico");
                                diagMedico.add(diagTO);
                                diagMedico.addAll(RESPUESTAMEDICO.getLstResultado());

                                switch (mPage) {
                                    case 1:
                                        adapterMedico1 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;
                                    case 2:
                                        adapterMedico2 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;
                                    case 3:
                                        adapterMedico3 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;
                                    case 4:
                                        adapterMedico4 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;
                                    case 5:
                                        adapterMedico5 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;
                                    case 6:
                                        adapterMedico6 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;
                                    case 7:
                                        adapterMedico7 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;
                                    case 8:
                                        adapterMedico8 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;

                                    case 9:
                                        adapterMedico9 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;

                                    case 10:
                                        adapterMedico10 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;
                                    case 11:
                                        adapterMedico11 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;
                                    case 12:
                                        adapterMedico12 = new ArrayAdapter<>(CONTEXT, R.layout.simple_spinner_dropdown_item, diagMedico);
                                        adapterMedico12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        break;
                                }

                                //carga todos los spinner o según el dia de seguimiento
                                cargaSpinerMedicos();

                            } else if (RESPUESTAMEDICO.getCodigoError().intValue() != 999) {
                                MensajesHelper.mostrarMensajeInfo(CONTEXT,
                                        RESPUESTAMEDICO.getMensajeError(), CONTEXT.getResources().getString(
                                                R.string.app_name), null);

                            } else {
                                MensajesHelper.mostrarMensajeError(CONTEXT,
                                        RESPUESTAMEDICO.getMensajeError(), CONTEXT.getResources().getString(
                                                R.string.app_name), null);
                            }
                        }
                    };
                    mSeguimientoInfluenzaActivity.mObtenerMedicosTask.execute((Void[]) null);
                }
            }

            //carga todos los spinner o según el dia de seguimiento
            public  void cargaSpinerMedicos(){
                if(esCargaInicial) {
                    switch (mPage) {
                        case 1:
                            Spinner spnMedico1 = (Spinner) getActivity().findViewById(R.id.spnMedico1);
                            spnMedico1.setEnabled(false);
                            spnMedico1.setAdapter(adapterMedico1);
                            /*for (int i = 0; i < adapterMedico1.getCount(); i++) {
                                if (adapterMedico1.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico1);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 2:
                            Spinner spnMedico2 = (Spinner) getActivity().findViewById(R.id.spnMedico2);
                            spnMedico2.setEnabled(false);
                            spnMedico2.setAdapter(adapterMedico2);
                            /*for (int i = 0; i < adapterMedico2.getCount(); i++) {
                                if (adapterMedico2.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico2);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 3:
                            Spinner spnMedico3 = (Spinner) getActivity().findViewById(R.id.spnMedico3);
                            spnMedico3.setEnabled(false);
                            spnMedico3.setAdapter(adapterMedico3);
                            /*for (int i = 0; i < adapterMedico3.getCount(); i++) {
                                if (adapterMedico3.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico3);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 4:
                            Spinner spnMedico4 = (Spinner) getActivity().findViewById(R.id.spnMedico4);
                            spnMedico4.setEnabled(false);
                            spnMedico4.setAdapter(adapterMedico4);
                            /*for (int i = 0; i < adapterMedico4.getCount(); i++) {
                                if (adapterMedico4.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico4);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 5:
                            Spinner spnMedico5 = (Spinner) getActivity().findViewById(R.id.spnMedico5);
                            spnMedico5.setEnabled(false);
                            spnMedico5.setAdapter(adapterMedico5);
                            /*for (int i = 0; i < adapterMedico5.getCount(); i++) {
                                if (adapterMedico5.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico5);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 6:
                            Spinner spnMedico6 = (Spinner) getActivity().findViewById(R.id.spnMedico6);
                            spnMedico6.setEnabled(false);
                            spnMedico6.setAdapter(adapterMedico6);
                            /*for (int i = 0; i < adapterMedico6.getCount(); i++) {
                                if (adapterMedico6.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico6);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 7:
                            Spinner spnMedico7 = (Spinner) getActivity().findViewById(R.id.spnMedico7);
                            spnMedico7.setEnabled(false);
                            spnMedico7.setAdapter(adapterMedico7);
                            /*for (int i = 0; i < adapterMedico7.getCount(); i++) {
                                if (adapterMedico7.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico7);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 8:
                            Spinner spnMedico8 = (Spinner) getActivity().findViewById(R.id.spnMedico8);
                            spnMedico8.setEnabled(false);
                            spnMedico8.setAdapter(adapterMedico8);
                            /*for (int i = 0; i < adapterMedico8.getCount(); i++) {
                                if (adapterMedico8.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico8);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 9:
                            Spinner spnMedico9 = (Spinner) getActivity().findViewById(R.id.spnMedico9);
                            spnMedico9.setEnabled(false);
                            spnMedico9.setAdapter(adapterMedico9);
                            /*for (int i = 0; i < adapterMedico9.getCount(); i++) {
                                if (adapterMedico9.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico9.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico9);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 10:
                            Spinner spnMedico10 = (Spinner) getActivity().findViewById(R.id.spnMedico10);
                            spnMedico10.setEnabled(false);
                            spnMedico10.setAdapter(adapterMedico10);
                            /*for (int i = 0; i < adapterMedico10.getCount(); i++) {
                                if (adapterMedico10.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico10.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico10);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 11:
                            Spinner spnMedico11 = (Spinner) getActivity().findViewById(R.id.spnMedico11);
                            spnMedico11.setEnabled(false);
                            spnMedico11.setAdapter(adapterMedico11);
                            /*for (int i = 0; i < adapterMedico11.getCount(); i++) {
                                if (adapterMedico11.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico11.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico11);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 12:
                            Spinner spnMedico12 = (Spinner) getActivity().findViewById(R.id.spnMedico12);
                            spnMedico12.setEnabled(false);
                            spnMedico12.setAdapter(adapterMedico12);
                            /*for (int i = 0; i < adapterMedico12.getCount(); i++) {
                                if (adapterMedico12.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico12.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico12);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                    }
                    esCargaInicial = false;
                    //posicionMedico = 0;
                }else{
                    switch (mPage){
                        case 1:
                            Spinner spnMedico1 = (Spinner) getActivity().findViewById(R.id.spnMedico1);
                            spnMedico1.setEnabled(false);
                            spnMedico1.setAdapter(adapterMedico1);
                            /*for (int i = 0; i < adapterMedico1.getCount(); i++) {
                                if (adapterMedico1.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico1);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 2:
                            Spinner spnMedico2 = (Spinner) getActivity().findViewById(R.id.spnMedico2);
                            spnMedico2.setEnabled(false);
                            spnMedico2.setAdapter(adapterMedico2);
                            /*for (int i = 0; i < adapterMedico2.getCount(); i++) {
                                if (adapterMedico2.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico2);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 3:
                            Spinner spnMedico3 = (Spinner) getActivity().findViewById(R.id.spnMedico3);
                            spnMedico3.setEnabled(false);
                            spnMedico3.setAdapter(adapterMedico3);
                            /*for (int i = 0; i < adapterMedico3.getCount(); i++) {
                                if (adapterMedico3.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico3);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 4:
                            Spinner spnMedico4 = (Spinner) getActivity().findViewById(R.id.spnMedico4);
                            spnMedico4.setEnabled(false);
                            spnMedico4.setAdapter(adapterMedico4);
                            /*for (int i = 0; i < adapterMedico4.getCount(); i++) {
                                if (adapterMedico4.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico4);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 5:
                            Spinner spnMedico5 = (Spinner) getActivity().findViewById(R.id.spnMedico5);
                            spnMedico5.setEnabled(false);
                            spnMedico5.setAdapter(adapterMedico5);
                            /*for (int i = 0; i < adapterMedico5.getCount(); i++) {
                                if (adapterMedico5.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico5);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 6:
                            Spinner spnMedico6 = (Spinner) getActivity().findViewById(R.id.spnMedico6);
                            spnMedico6.setEnabled(false);
                            spnMedico6.setAdapter(adapterMedico6);
                           /* for (int i = 0; i < adapterMedico6.getCount(); i++) {
                                if (adapterMedico6.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico6);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 7:
                            Spinner spnMedico7 = (Spinner) getActivity().findViewById(R.id.spnMedico7);
                            spnMedico7.setEnabled(false);
                            spnMedico7.setAdapter(adapterMedico7);
                            /*for (int i = 0; i < adapterMedico7.getCount(); i++) {
                                if (adapterMedico7.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico7);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        case 8:
                            Spinner spnMedico8 = (Spinner) getActivity().findViewById(R.id.spnMedico8);
                            spnMedico8.setEnabled(false);
                            spnMedico8.setAdapter(adapterMedico8);
                            /*for (int i = 0; i < adapterMedico8.getCount(); i++) {
                                if (adapterMedico8.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico8);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 9:
                            Spinner spnMedico9 = (Spinner) getActivity().findViewById(R.id.spnMedico9);
                            spnMedico9.setEnabled(false);
                            spnMedico9.setAdapter(adapterMedico9);
                            /*for (int i = 0; i < adapterMedico9.getCount(); i++) {
                                if (adapterMedico9.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico9.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico9);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 10:
                            Spinner spnMedico10 = (Spinner) getActivity().findViewById(R.id.spnMedico10);
                            spnMedico10.setEnabled(false);
                            spnMedico10.setAdapter(adapterMedico10);
                            /*for (int i = 0; i < adapterMedico10.getCount(); i++) {
                                if (adapterMedico10.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico10.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico10);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 11:
                            Spinner spnMedico11 = (Spinner) getActivity().findViewById(R.id.spnMedico11);
                            spnMedico11.setEnabled(false);
                            spnMedico11.setAdapter(adapterMedico11);
                            /*for (int i = 0; i < adapterMedico11.getCount(); i++) {
                                if (adapterMedico11.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico11.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico11);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;

                        case 12:
                            Spinner spnMedico12 = (Spinner) getActivity().findViewById(R.id.spnMedico12);
                            spnMedico12.setEnabled(false);
                            spnMedico12.setAdapter(adapterMedico12);
                            /*for (int i = 0; i < adapterMedico12.getCount(); i++) {
                                if (adapterMedico12.getItem(i).getIdMedico().intValue() == ((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId()) {
                                    posicionMedico = i;
                                    //medicoEncontrado = adapterMedico1.getItem(i);
                                }
                            }*/
                            spnMedico12.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cargarMedico(position, adapterMedico12);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                    }
                    //posicionMedico = 0;
                }
            }

            public void cargarMedico(int position, ArrayAdapter<MedicosDTO> medicoAdapter) {
                SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                if (seguimientoInfluenza != null) {
                    seguimientoInfluenza.setUsuarioMedico((medicoAdapter.getItem(position).getIdMedico().intValue() > 0) ?
                            medicoAdapter.getItem(position).getIdMedico() : 0);
                } else if (medicoAdapter.getItem(position).getIdMedico().intValue() > 0) {
                    seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                    seguimientoInfluenza.setControlDia(mPage);
                    seguimientoInfluenza.setUsuarioMedico(medicoAdapter.getItem(position).getIdMedico());
                    mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                }
            }

            public void cargarIU(SeguimientoInfluenzaDTO seguimientoInfluenza, boolean habilitar) {
                switch (mPage) {
                    case 1:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento1)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia1Fila15).setEnabled(habilitar);

                        /*((Spinner) getActivity().findViewById(R.id.spnDia1Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition(( seguimientoInfluenza.getFaltaEscuela() != null) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : ""));
                        getActivity().findViewById(R.id.spnDia1Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getQuedoEnCama() != null) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : ""));*/
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico1).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico1).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento1).setEnabled(habilitar);
                        break;
                    case 2:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento2)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia2Fila15).setEnabled(habilitar);
                        /*((Spinner) getActivity().findViewById(R.id.spnDia2Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getFaltaEscuela() != null) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : ""));
                        getActivity().findViewById(R.id.spnDia2Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getQuedoEnCama() != null) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : ""));*/
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico2).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico2).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento2).setEnabled(habilitar);
                        break;
                    case 3:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento3)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia3Fila15).setEnabled(habilitar);
                     /*   ((Spinner) getActivity().findViewById(R.id.spnDia3Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getFaltaEscuela() != null) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : ""));
                        getActivity().findViewById(R.id.spnDia3Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getQuedoEnCama() != null) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : ""));*/
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico3).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico3).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento3).setEnabled(habilitar);
                        break;
                    case 4:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento4)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia4Fila15).setEnabled(habilitar);
                        /*((Spinner) getActivity().findViewById(R.id.spnDia4Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition(seguimientoInfluenza.getFaltaEscuela().trim()));
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getFaltaEscuela() != null) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : ""));
                        getActivity().findViewById(R.id.spnDia4Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getQuedoEnCama() != null) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : ""));*/
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico4).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico4).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento4).setEnabled(habilitar);
                        break;
                    case 5:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento5)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia5Fila15).setEnabled(habilitar);
                        /*((Spinner) getActivity().findViewById(R.id.spnDia5Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition(seguimientoInfluenza.getFaltaEscuela().trim()));
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getFaltaEscuela() != null) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : ""));
                        getActivity().findViewById(R.id.spnDia5Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getQuedoEnCama() != null) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : ""));*/
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico5).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico5).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento5).setEnabled(habilitar);
                        break;
                    case 6:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento6)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia6Fila15).setEnabled(habilitar);
                        /*((Spinner) getActivity().findViewById(R.id.spnDia6Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition(seguimientoInfluenza.getFaltaEscuela().trim()));
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getFaltaEscuela() != null) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : ""));
                        getActivity().findViewById(R.id.spnDia6Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getQuedoEnCama() != null) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : ""));*/
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico6).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico6).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento6).setEnabled(habilitar);
                        break;
                    case 7:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento7)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia7Fila15).setEnabled(habilitar);
                        /*((Spinner) getActivity().findViewById(R.id.spnDia7Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition(seguimientoInfluenza.getFaltaEscuela().trim()));
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getFaltaEscuela() != null) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : ""));
                        getActivity().findViewById(R.id.spnDia7Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((seguimientoInfluenza.getQuedoEnCama() != null) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : ""));;*/
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico7).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico7).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento7).setEnabled(habilitar);
                        break;

                    case 8:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento8)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia8Fila15).setEnabled(habilitar);
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico8).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico8).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento8).setEnabled(habilitar);
                        break;

                    case 9:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento9)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia9Fila15).setEnabled(habilitar);
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico9).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico9).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento9).setEnabled(habilitar);
                        break;

                    case 10:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento10)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia10Fila15).setEnabled(habilitar);
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico10).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico10).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento10).setEnabled(habilitar);
                        break;

                    case 11:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento11)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia11Fila15).setEnabled(habilitar);
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico11).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico11).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento11).setEnabled(habilitar);
                        break;

                    case 12:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento12)).setText(seguimientoInfluenza.getFechaSeguimiento());
                        getActivity().findViewById(R.id.edtxtFechaSeguimiento12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila1)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getConsultaInicial())) ?
                                seguimientoInfluenza.getConsultaInicial().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila1).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila2)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFiebre())) ?
                                seguimientoInfluenza.getFiebre().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila2).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila3)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getTos())) ?
                                seguimientoInfluenza.getTos().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila3).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila4)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getSecrecionNasal())) ?
                                seguimientoInfluenza.getSecrecionNasal().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila4).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila5)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorGarganta())) ?
                                seguimientoInfluenza.getDolorGarganta().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila5).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila6)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getCongestionNasa())) ?
                                seguimientoInfluenza.getCongestionNasa().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila6).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila7)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorCabeza())) ?
                                seguimientoInfluenza.getDolorCabeza().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila7).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila8)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaApetito())) ?
                                seguimientoInfluenza.getFaltaApetito().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila8).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila9)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorMuscular())) ?
                                seguimientoInfluenza.getDolorMuscular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila9).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila10)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorArticular())) ?
                                seguimientoInfluenza.getDolorArticular().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila10).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila11)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDolorOido())) ?
                                seguimientoInfluenza.getDolorOido().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila11).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila12)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getRespiracionRapida())) ?
                                seguimientoInfluenza.getRespiracionRapida().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila12).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila13)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getDificultadRespirar())) ?
                                seguimientoInfluenza.getDificultadRespirar().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila13).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila14)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getFaltaEscuela())) ?
                                seguimientoInfluenza.getFaltaEscuela().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila14).setEnabled(habilitar);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila15)).setSelection(mSeguimientoInfluenzaActivity.adapter.getPosition((!StringUtils.isNullOrEmpty(seguimientoInfluenza.getQuedoEnCama())) ?
                                seguimientoInfluenza.getQuedoEnCama().trim() : "Seleccione"));
                        getActivity().findViewById(R.id.spnDia12Fila15).setEnabled(habilitar);
                        setMedico(seguimientoInfluenza.getUsuarioMedico());
                        getActivity().findViewById(R.id.spnMedico12).setEnabled(false);
                        getActivity().findViewById(R.id.imgBusquedaMedico12).setEnabled(false);
                        getActivity().findViewById(R.id.imgLimpiarSeguimiento12).setEnabled(habilitar);
                        break;
                }
            }

            /***
             * Metodo para seleccionar la fecha seguimiento del dia especifico.
             * @param view, Edittext de fecha seguimiento.
             */
            public void showDatePickerDialogFechaSeguimiento(View view, final int viewId) {
                DialogFragment newFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        if (esMayorFechaAnterior(calendar)) {
                            ((EditText) getActivity().findViewById(viewId)).setError( (mPage != 1) ?
                                    getString(R.string.msj_fecha_seguimiento_menor_anterior) :
                                    getString(R.string.msj_fecha_seguimiento_menor_fis));
                            ((EditText) getActivity().findViewById(viewId)).setText("");
                        } else if (DateUtils.esMayorFechaHoy(calendar)) {
                            ((EditText) getActivity().findViewById(viewId)).setError(getString(R.string.msj_fecha_mayor_hoy));
                            ((EditText) getActivity().findViewById(viewId)).setText("");
                        } else if (!esUnicaFechaSeguimiento(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()), viewId)) {
                            ((EditText) getActivity().findViewById(viewId)).setError(getString(R.string.msj_no_unica_fecha_seguimiento));
                            ((EditText) getActivity().findViewById(viewId)).setText("");
                        } else {
                            if(((EditText) getActivity().findViewById(viewId)).getError() != null) {
                                ((EditText) getActivity().findViewById(viewId)).setError(null);
                                ((EditText) getActivity().findViewById(viewId)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                            }
                            ((EditText) getActivity().findViewById(viewId)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                            cargarFechaSeguimiento(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                        }
                    }
                };
                newFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.title_date_picker));
            }

            public void cargarFechaSeguimiento(String fecha) {
                if(!StringUtils.isNullOrEmpty(fecha)) {
                    SeguimientoInfluenzaDTO seguimientoInfluenza = obtenerNuevoSeguimientoPorDia(mPage);
                    if (seguimientoInfluenza != null) {
                        seguimientoInfluenza.setFechaSeguimiento(fecha);
                    } else {
                        seguimientoInfluenza = new SeguimientoInfluenzaDTO();
                        seguimientoInfluenza.setControlDia(mPage);
                        seguimientoInfluenza.setFechaSeguimiento(fecha);
                        mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.add(seguimientoInfluenza);
                    }
                }
            }

            /***
             * Metodo que limpia los datos al utilizar el boton cleaner.
             */
            public void limpiarControlesTab() {
                switch (mPage) {
                    case 1:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento1)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia1Fila15)).setSelection(0);
                        break;
                    case 2:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento2)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia2Fila15)).setSelection(0);
                        break;
                    case 3:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento3)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia3Fila15)).setSelection(0);
                        break;
                    case 4:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento4)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia4Fila15)).setSelection(0);
                        break;
                    case 5:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento5)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia5Fila15)).setSelection(0);
                        break;
                    case 6:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento6)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia6Fila15)).setSelection(0);
                        break;
                    case 7:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento7)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia7Fila15)).setSelection(0);
                        break;
                    case 8:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento8)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia8Fila15)).setSelection(0);
                        break;
                    case 9:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento9)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia9Fila15)).setSelection(0);
                        break;
                    case 10:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento10)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia10Fila15)).setSelection(0);
                        break;
                    case 11:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento11)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia11Fila15)).setSelection(0);
                        break;
                    case 12:
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaSeguimiento12)).setText("");
                        //setMedico(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila1)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila2)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila3)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila4)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila5)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila6)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila7)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila8)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila9)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila10)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila11)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila12)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila13)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila14)).setSelection(0);
                        ((Spinner) getActivity().findViewById(R.id.spnDia12Fila15)).setSelection(0);
                        break;
                }
                removerNuevoSeguimientoPorDia(mPage);
            }

            /***
             * Metodo para validar que la fecha seguimeinto ingresada, no se encuentra repetida.
             * @param fecha, Fecha seleccionada
             * @param viewId, Edittext al que se ingresa la informacion.
             * @return True si no existe la fecha.
             */
            private boolean esUnicaFechaSeguimiento(String fecha, int viewId) {
                if(viewId != R.id.edtxtFechaSeguimiento1) {

                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(1) != null) ? obtenerSeguimientoPorDia(1) :
                            obtenerNuevoSeguimientoPorDia(1);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento2) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(2) != null) ? obtenerSeguimientoPorDia(2) :
                            obtenerNuevoSeguimientoPorDia(2);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento3) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(3) != null) ? obtenerSeguimientoPorDia(3) :
                            obtenerNuevoSeguimientoPorDia(3);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento4) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(4) != null) ? obtenerSeguimientoPorDia(4) :
                            obtenerNuevoSeguimientoPorDia(4);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento5) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(5) != null) ? obtenerSeguimientoPorDia(5) :
                            obtenerNuevoSeguimientoPorDia(5);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento6) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(6) != null) ? obtenerSeguimientoPorDia(6) :
                            obtenerNuevoSeguimientoPorDia(6);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento7) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(7) != null) ? obtenerSeguimientoPorDia(7) :
                            obtenerNuevoSeguimientoPorDia(7);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento8) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(8) != null) ? obtenerSeguimientoPorDia(8) :
                            obtenerNuevoSeguimientoPorDia(8);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento9) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(9) != null) ? obtenerSeguimientoPorDia(9) :
                            obtenerNuevoSeguimientoPorDia(9);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento10) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(10) != null) ? obtenerSeguimientoPorDia(10) :
                            obtenerNuevoSeguimientoPorDia(10);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento11) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(11) != null) ? obtenerSeguimientoPorDia(11) :
                            obtenerNuevoSeguimientoPorDia(11);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }

                if(viewId != R.id.edtxtFechaSeguimiento12) {
                    SeguimientoInfluenzaDTO seguimiento = (obtenerSeguimientoPorDia(12) != null) ? obtenerSeguimientoPorDia(12) :
                            obtenerNuevoSeguimientoPorDia(12);

                    if(seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento()) &&
                            seguimiento.getFechaSeguimiento().compareTo(fecha) == 0) {
                        return false;
                    }
                }
                return true;
            }

            /***
             * Metodo para validar que la fecha de seguimiento ingresada no se menor a la anterior
             * @param fecha, Fecha Ingresada
             * @return True si anterior es mayor.
             */
            private boolean esMayorFechaAnterior(Calendar fecha) {
                try {
                    SeguimientoInfluenzaDTO seguimiento = null;
                    switch (mPage) {
                        case 1:
                            if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtFIS)).getText().toString())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(((EditText) getActivity().findViewById(R.id.edtxtFIS)).getText().toString()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;
                        case 2:
                            seguimiento = (obtenerSeguimientoPorDia(1) != null) ? obtenerSeguimientoPorDia(1) :
                                    obtenerNuevoSeguimientoPorDia(1);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;
                        case 3:
                            seguimiento = (obtenerSeguimientoPorDia(2) != null) ? obtenerSeguimientoPorDia(2) :
                                    obtenerNuevoSeguimientoPorDia(2);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;
                        case 4:
                            seguimiento = (obtenerSeguimientoPorDia(3) != null) ? obtenerSeguimientoPorDia(3) :
                                    obtenerNuevoSeguimientoPorDia(3);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;
                        case 5:
                            seguimiento = (obtenerSeguimientoPorDia(4) != null) ? obtenerSeguimientoPorDia(4) :
                                    obtenerNuevoSeguimientoPorDia(4);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;
                        case 6:
                            seguimiento = (obtenerSeguimientoPorDia(5) != null) ? obtenerSeguimientoPorDia(5) :
                                    obtenerNuevoSeguimientoPorDia(5);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;
                        case 7:
                            seguimiento = (obtenerSeguimientoPorDia(6) != null) ? obtenerSeguimientoPorDia(6) :
                                    obtenerNuevoSeguimientoPorDia(6);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;

                        case 8:
                            seguimiento = (obtenerSeguimientoPorDia(7) != null) ? obtenerSeguimientoPorDia(7) :
                                    obtenerNuevoSeguimientoPorDia(7);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;

                        case 9:
                            seguimiento = (obtenerSeguimientoPorDia(8) != null) ? obtenerSeguimientoPorDia(8) :
                                    obtenerNuevoSeguimientoPorDia(8);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;

                        case 10:
                            seguimiento = (obtenerSeguimientoPorDia(9) != null) ? obtenerSeguimientoPorDia(9) :
                                    obtenerNuevoSeguimientoPorDia(9);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;

                        case 11:
                            seguimiento = (obtenerSeguimientoPorDia(10) != null) ? obtenerSeguimientoPorDia(10) :
                                    obtenerNuevoSeguimientoPorDia(10);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;

                        case 12:
                            seguimiento = (obtenerSeguimientoPorDia(11) != null) ? obtenerSeguimientoPorDia(11) :
                                    obtenerNuevoSeguimientoPorDia(11);

                            if (seguimiento != null && !StringUtils.isNullOrEmpty(seguimiento.getFechaSeguimiento())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar fechaAnterior = Calendar.getInstance();
                                fechaAnterior.setTime(sdf.parse(seguimiento.getFechaSeguimiento()));

                                if(fechaAnterior.after(fecha)) {
                                    return true;
                                }
                            }
                            break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return false;
            }

        /*    // Metodo para verificar que no se salten de dias en la hoja de influenza
            public boolean verificarDias() {
                SeguimientoInfluenzaDTO seguimiento1 = null;
                SeguimientoInfluenzaDTO seguimiento2 = null;
                SeguimientoInfluenzaDTO seguimiento3 = null;
                switch (mPage){
                    case 1:
                        seguimiento1 = (obtenerSeguimientoPorDia(1) != null) ? obtenerSeguimientoPorDia(1) : obtenerSeguimientoPorDia(1);
                        if(seguimiento1 == null && seguimiento2 != null) {
                            return false;
                        }
                        break;
                    case 2:
                        seguimiento2 = (obtenerSeguimientoPorDia(2) != null) ? obtenerSeguimientoPorDia(2) : obtenerSeguimientoPorDia(2);
                        if(seguimiento2 == null && seguimiento3 != null) {
                            return false;
                        }
                        break;
                    case 3:
                        seguimiento3 = (obtenerSeguimientoPorDia(3) != null) ? obtenerSeguimientoPorDia(3) : obtenerSeguimientoPorDia(3);
                        if(seguimiento3 == null && seguimiento2 != null) {
                            return false;
                        }
                        break;
                }
                return  true;
            }*/

            public void setMedico(int usuarioMedico){
                MedicosDTO medicoEncontrado = new MedicosDTO();
                int posicion;

                switch (mPage){
                    case 1:
                        for (int i = 0; i < adapterMedico1.getCount(); i++) {
                            if (adapterMedico1.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico1.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico1.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico1)).setSelection(posicion);
                        break;

                    case 2:
                        for (int i = 0; i < adapterMedico2.getCount(); i++) {
                            if (adapterMedico2.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico2.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico2.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico2)).setSelection(posicion);
                        break;

                    case 3:
                        for (int i = 0; i < adapterMedico3.getCount(); i++) {
                            if (adapterMedico3.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico3.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico3.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico3)).setSelection(posicion);
                        break;

                    case 4:
                        for (int i = 0; i < adapterMedico4.getCount(); i++) {
                            if (adapterMedico4.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico1.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico4.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico4)).setSelection(posicion);
                        break;

                    case 5:
                        for (int i = 0; i < adapterMedico5.getCount(); i++) {
                            if (adapterMedico5.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico1.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico5.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico5)).setSelection(posicion);
                        break;

                    case 6:
                        for (int i = 0; i < adapterMedico6.getCount(); i++) {
                            if (adapterMedico6.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico1.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico6.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico6)).setSelection(posicion);
                        break;

                    case 7:
                        for (int i = 0; i < adapterMedico7.getCount(); i++) {
                            if (adapterMedico7.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico1.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico7.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico7)).setSelection(posicion);
                        break;

                    case 8:
                        for (int i = 0; i < adapterMedico8.getCount(); i++) {
                            if (adapterMedico8.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico1.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico8.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico8)).setSelection(posicion);
                        break;

                    case 9:
                        for (int i = 0; i < adapterMedico9.getCount(); i++) {
                            if (adapterMedico9.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico1.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico9.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico9)).setSelection(posicion);
                        break;

                    case 10:
                        for (int i = 0; i < adapterMedico10.getCount(); i++) {
                            if (adapterMedico10.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico1.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico10.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico10)).setSelection(posicion);
                        break;

                    case 11:
                        for (int i = 0; i < adapterMedico11.getCount(); i++) {
                            if (adapterMedico11.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico1.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico11.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico11)).setSelection(posicion);
                        break;

                    case 12:
                        for (int i = 0; i < adapterMedico12.getCount(); i++) {
                            if (adapterMedico12.getItem(i).getIdMedico().intValue() == usuarioMedico) {
                                medicoEncontrado = adapterMedico1.getItem(i);
                                break;
                            }
                        }
                        posicion = adapterMedico12.getPosition(medicoEncontrado);
                        ((Spinner) getActivity().findViewById(R.id.spnMedico12)).setSelection(posicion);
                        break;
                }

            }

            public SeguimientoInfluenzaDTO obtenerSeguimientoPorDia(int dia) {
                for (SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.listaSegInfluenza) {
                    if(seguimientoInfluenza.getControlDia() != ' ' &&
                            Integer.parseInt(""+seguimientoInfluenza.getControlDia()) == dia) {
                        return seguimientoInfluenza;
                    }
                }
                return null;
            }

            public SeguimientoInfluenzaDTO obtenerNuevoSeguimientoPorDia(int dia) {
                if(mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza != null) {
                    for (SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza) {
                        if (seguimientoInfluenza.getControlDia() != ' ' &&
                                Integer.parseInt("" + seguimientoInfluenza.getControlDia()) == dia) {
                            return seguimientoInfluenza;
                        }
                    }
                }
                return null;
            }

            public void removerNuevoSeguimientoPorDia(int dia) {
                if(mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza != null) {
                    for (SeguimientoInfluenzaDTO seguimientoInfluenza : mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza) {
                        if (seguimientoInfluenza.getControlDia() != ' ' &&
                                Integer.parseInt("" + seguimientoInfluenza.getControlDia()) == dia) {
                            mSeguimientoInfluenzaActivity.nuevaListaSegInfluenza.remove(seguimientoInfluenza);
                            return;
                        }
                    }
                }
            }
        }

    }
}