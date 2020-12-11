package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.NavigationDrawerFragment;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.diagnostiscoactivities.ObtenerEscuelaTask;
import com.sts_ni.estudiocohortecssfv.dto.DepartamentosDTO;
import com.sts_ni.estudiocohortecssfv.dto.MunicipiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.VigilanciaIntegradaIragEtiDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.DatePickerFragment;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.ExpedienteWS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class VigilanciaIntegradaActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public ArrayAdapter<CharSequence> adapterSintomas;
    private static VigilanciaIntegradaActivity mVigilanciaIntegradaActivity;
    public BuscarVigilanciaIntegradaTask mBuscarVigilanciaIntegradaTask;
    public GuardarVigilanciaIntegradaTask mGuardarVigilanciaIntegradaTask;
    private ObtenerDepartamentosTask mObtenerDepartamentosTask;
    public int mSecVigilanciaIntegrada = 0;
    public int mSecHojaConsulta = 0;
    public int mCodExpediente = 0;
    public int mNumHojaConsulta = 0;
    public static int mMunicipio = 0;
    public Character mIrag = null;
    public Character mEti = null;
    public Character mIragInusitada = null;
    public String mNombrePaciente = null;
    public String mTutor = null;
    public static boolean isEdit = false;
    public static boolean existeDetalle = false;
    private static Context CONTEXT;
    private ArrayList<DepartamentosDTO> mDepartamentos;
    ArrayAdapter<DepartamentosDTO> adapter;
    private static ArrayAdapter<MunicipiosDTO> adapterMunicipios;
    private static ResultadoListWSDTO<MunicipiosDTO> RESPUESTAMUNICIP;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    public static int consultorioMedico = 0;
    public static int consultorioResp = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_vigilancia_integrada_irag_eti);

        this.CONTEXT = this;

        mVigilanciaIntegradaActivity = this;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView) actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.label_vigilancia_integrada));
        ((TextView) actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp) getApplication()).getInfoSessionWSDTO().getUser());

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.dwlVigilanciaIntegrada));

        llamarServicioObtenerDepartamentos();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fmlVigilanciaIntegrada, PlaceholderFragment.newInstance(position + 1))
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
        cancelarGuardarTask();
        cancelarObtenerDepartamentosTask();
    }

    /***
     * Cancela tarea busqueda al destruir el activity.
     */
    private void cancelarBusquedaTask() {
        if (mBuscarVigilanciaIntegradaTask != null && mBuscarVigilanciaIntegradaTask.getStatus() == UserTask.Status.RUNNING) {
            mBuscarVigilanciaIntegradaTask.cancel(true);
            mBuscarVigilanciaIntegradaTask = null;
        }
    }

    /***
     * Cancela tarea guardar ficha vigilancia integrada al destruir el activity.
     */
    private void cancelarGuardarTask() {
        if (mGuardarVigilanciaIntegradaTask != null && mGuardarVigilanciaIntegradaTask.getStatus() == UserTask.Status.RUNNING) {
            mGuardarVigilanciaIntegradaTask.cancel(true);
            mGuardarVigilanciaIntegradaTask = null;
        }
    }

    private void cancelarObtenerDepartamentosTask() {
        if (mObtenerDepartamentosTask != null && mObtenerDepartamentosTask.getStatus() == UserTask.Status.RUNNING) {
            mObtenerDepartamentosTask.cancel(true);
            mObtenerDepartamentosTask = null;
        }
    }

    public void llamarServicioObtenerDepartamentos() {
        if (mObtenerDepartamentosTask == null || mObtenerDepartamentosTask.getStatus() == ObtenerDepartamentosTask.Status.FINISHED) {
            mObtenerDepartamentosTask = (ObtenerDepartamentosTask) new
                    ObtenerDepartamentosTask(this).execute(null);
        }
    }

    /***
     * Carga todos los departamentos obtenidos.
     */
    public void cargarListaDepartamento(ArrayList<DepartamentosDTO> paramLstDepartamento) {

        DepartamentosDTO departamentos = new DepartamentosDTO();
        ArrayList<DepartamentosDTO> lstDepartamentos = new ArrayList<DepartamentosDTO>();
        departamentos.setDivisionpoliticaId(0);
        departamentos.setNombre("Seleccione el Departamento");
        lstDepartamentos.add(departamentos);
        lstDepartamentos.addAll(paramLstDepartamento);
        Spinner spnDepartamentos = (Spinner) findViewById(R.id.spnDepartamentos);
        adapter = new ArrayAdapter<DepartamentosDTO>(this, R.layout.simple_spinner_dropdown_item, lstDepartamentos) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.LEFT);
                return v;
            }
        };
        spnDepartamentos.setAdapter(adapter);
        this.mDepartamentos = lstDepartamentos;
    }

    /***
     * Carga los datos obtenidos atravez de buscar con la informacion guardad cuando se creo la ficha.
     */
    public void cargarDatosObtenidos(VigilanciaIntegradaIragEtiDTO vigilanciaIntegradaIragEtiDTO) {
        this.mSecVigilanciaIntegrada = vigilanciaIntegradaIragEtiDTO.getSecVigilanciaIntegrada();
        this.mSecHojaConsulta = vigilanciaIntegradaIragEtiDTO.getSecHojaConsulta();
        ((EditText) this.findViewById(R.id.edtxtNombreApellido)).setText(vigilanciaIntegradaIragEtiDTO.getNombrePaciente());
        ((EditText) this.findViewById(R.id.edtxtTutor)).setText(vigilanciaIntegradaIragEtiDTO.getTutor());
        this.mIrag = vigilanciaIntegradaIragEtiDTO.getIrag();
        this.mEti = vigilanciaIntegradaIragEtiDTO.getEti();
        this.mIragInusitada = vigilanciaIntegradaIragEtiDTO.getIragInusitada();
        this.mCodExpediente = vigilanciaIntegradaIragEtiDTO.getCodExpediente();
        this.mNombrePaciente = vigilanciaIntegradaIragEtiDTO.getNombrePaciente();
        this.mTutor = vigilanciaIntegradaIragEtiDTO.getTutor();
        this.mNumHojaConsulta = vigilanciaIntegradaIragEtiDTO.getNumHojaConsulta();
        if (mIrag.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkIrag)).setChecked(true);
            ((CheckBox) this.findViewById(R.id.chkEti)).setChecked(false);
            ((CheckBox) this.findViewById(R.id.chkIragInusitada)).setChecked(false);
        }
        if (mEti.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkIrag)).setChecked(false);
            ((CheckBox) this.findViewById(R.id.chkEti)).setChecked(true);
            ((CheckBox) this.findViewById(R.id.chkIragInusitada)).setChecked(false);
        }
        if (mIragInusitada.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkIrag)).setChecked(false);
            ((CheckBox) this.findViewById(R.id.chkEti)).setChecked(false);
            ((CheckBox) this.findViewById(R.id.chkIragInusitada)).setChecked(true);
        }
        findViewById(R.id.btnGuardarFicha).setEnabled(true);
        /* Si el departamento y el municipio son distintos a null indica que ya se guardaron todos los datos
        por lo tanto se cargaran todos los datos de la ficha */
        mMunicipio = 0;
        isEdit = false;
        existeDetalle = false;
        findViewById(R.id.ibtBuscarFichaExp).setEnabled(false);
        if (vigilanciaIntegradaIragEtiDTO.getDepartamento() != null && vigilanciaIntegradaIragEtiDTO.getMunicipio() != null) {
            this.cargarDatosCompletos(vigilanciaIntegradaIragEtiDTO);
        }
    }

    /***
     * Carga los datos obtenidos atravez de buscar con toda la informacion guardada desde la pantalla de
     * vigilancia integrada.
     */
    public void cargarDatosCompletos(VigilanciaIntegradaIragEtiDTO vigilanciaIntegradaIragEtiDTO) {
        isEdit = true;
        existeDetalle = true;
        //((EditText) this.findViewById(R.id.edtxtDepartamento)).setText(vigilanciaIntegradaIragEtiDTO.getDepartamento());
        //((EditText) this.findViewById(R.id.edtxtMunicipio)).setText(vigilanciaIntegradaIragEtiDTO.getMunicipio());
        int departamento = Integer.parseInt(vigilanciaIntegradaIragEtiDTO.getDepartamento());
        mMunicipio = Integer.parseInt(vigilanciaIntegradaIragEtiDTO.getMunicipio());
        DepartamentosDTO departamentosDTO = new DepartamentosDTO();
        ArrayList<DepartamentosDTO> dep = this.mDepartamentos;
        for (int i = 0; i < dep.size(); i++) {
            if (dep.get(i).getCodigoNacional() == departamento) {
                departamentosDTO.setDivisionpoliticaId(dep.get(i).getDivisionpoliticaId());
                departamentosDTO.setCodigoNacional(dep.get(i).getCodigoNacional());
                departamentosDTO.setNombre(dep.get(i).getNombre());
                departamentosDTO.setCodigoIso(dep.get(i).getCodigoIso());
            }
        }

        ((Spinner) this.findViewById(R.id.spnDepartamentos)).setSelection(departamentosDTO.getDivisionpoliticaId());
        //((Spinner) this.findViewById(R.id.spnMunicipios)).setSelection(2);
        ((EditText) this.findViewById(R.id.edtxtBarrio)).setText(vigilanciaIntegradaIragEtiDTO.getBarrio());
        ((EditText) this.findViewById(R.id.edtxtDireccion)).setText(vigilanciaIntegradaIragEtiDTO.getDireccion());
        ((EditText) this.findViewById(R.id.edtxtTelefono)).setText(vigilanciaIntegradaIragEtiDTO.getTelefono());
        ((EditText) this.findViewById(R.id.dpFechaUltimaDosisAntiHib)).setKeyListener(null);
        ((EditText) this.findViewById(R.id.dpFechaUltimaDosisAntiMening)).setKeyListener(null);
        ((EditText) this.findViewById(R.id.dpFechaUltimaDosisAntiNeumococica)).setKeyListener(null);
        ((EditText) this.findViewById(R.id.dpFechaUltimaDosisAntiInfluenza)).setKeyListener(null);
        ((EditText) this.findViewById(R.id.dpFecha1raDosisAntiViral)).setKeyListener(null);
        ((EditText) this.findViewById(R.id.dpFechaUltDosisAntiViral)).setKeyListener(null);
        Character chkUrbano;
        Character chkRural;
        Character chkEmergencia;
        Character chkSala;
        Character chkUci;
        Character chkAmbulatorio;
        Character chkPresentTarjVacuna;
        Character chkAntiHib;
        Character chkAntiMeningococica;
        Character chkAntiNeumococica;
        Character chkAntiInfluenza;
        Character chkPentavalente;
        Character chkConjugada;
        Character chkPolisacarida;
        Character chkHeptavalente;
        Character chkPolisacarida23;
        Character chkValente13;
        Character chkEstacional;
        Character chkH1n1p;
        Character chkOtraVacuna;
        Short noDosisAntiHib;
        Short noDosisAntiMening;
        Short noDosisAntiNeumo;
        Short noDosisAntiInflu;
        String fechaUltDosisAntiHib;
        String fechaUltDosisAntiMening;
        String fechaUltDosisAntiNeumo;
        String fechaUltDosisAntiInflu;
        Character chkCancer;
        Character chkDiabetes;
        Character chkVih;
        Character chkOtraInmunodeficiencia;
        Character chkEnfNeurologicaCronica;
        Character chkEnfCardiaca;
        Character chkAsma;
        Character chkEpoc;
        Character chkOtraEnfPulmonar;
        Character chkInsufRenalCronica;
        Character chkDesnutricion;
        Character chkObesidad;
        Character chkEmbarazo;
        Short edtxtEmbarazoSemanas;
        Character chkTxCorticosteroide;
        Character chkOtraCondicion;
        Character chkEstornudos;
        String edtxtOtraCondPreexistente;
        Character chkUsoAntibioticoUltimaSemana;
        Short edtxtCuantosAntibioticosLeDio;
        String edtxtCualesAntibioticosLeDio;
        Short edtxtCuantosDiasLeDioElUltimoAntibiotico;
        Character chkViaOral;
        Character chkViaParenteral;
        Character chkViaAmbas;
        Character chkAntecedentesUsoAntivirales;
        String edtxtNombreAntiviral;
        String edtxtFecha1raDosis;
        String edtxtFechaUltimaDosis;
        Short edtxtNoDosisAdministrada;
        Character chkOtraManifestacionClinica;
        String edtxtOtraManifestacionClinica;

        chkUrbano = vigilanciaIntegradaIragEtiDTO.getUrbano();
        if (chkUrbano.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkUrbano)).setChecked(true);
        }
        chkRural = vigilanciaIntegradaIragEtiDTO.getRural();
        if (chkRural.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkRural)).setChecked(true);
        }
        chkEmergencia = vigilanciaIntegradaIragEtiDTO.getEmergencia();
        if (chkEmergencia.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkCaptacionEmergencia)).setChecked(true);
        }
        chkSala = vigilanciaIntegradaIragEtiDTO.getSala();
        if (chkSala.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkCaptacionSala)).setChecked(true);
        }
        chkUci = vigilanciaIntegradaIragEtiDTO.getUci();
        if (chkUci.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkCaptacionUci)).setChecked(true);
        }
        chkAmbulatorio = vigilanciaIntegradaIragEtiDTO.getEmergAmbulatorio();
        if (chkAmbulatorio.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkCaptacionAmbulatorio)).setChecked(true);
        }
        ((EditText) this.findViewById(R.id.edtxtDiagnostico)).setText(vigilanciaIntegradaIragEtiDTO.getDiagnostico());
        // Tarjeta Vacuna
        chkPresentTarjVacuna = vigilanciaIntegradaIragEtiDTO.getPresentTarjVacuna();
        if (chkPresentTarjVacuna.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkTarjetaVacunaSi)).setChecked(true);
        } else {
            ((CheckBox) this.findViewById(R.id.chkTarjetaVacunaNo)).setChecked(true);
            this.findViewById(R.id.chkAntiHibSi).setEnabled(false);
            this.findViewById(R.id.chkAntiHibNo).setEnabled(false);
            this.findViewById(R.id.chkAntiMenigSi).setEnabled(false);
            this.findViewById(R.id.chkAntiMenigNo).setEnabled(false);
            this.findViewById(R.id.chkAntiNeumococicaSi).setEnabled(false);
            this.findViewById(R.id.chkAntiNeumococicaNo).setEnabled(false);
            this.findViewById(R.id.chkAntiInfluenzaSi).setEnabled(false);
            this.findViewById(R.id.chkAntiInfluenzaNo).setEnabled(false);
            this.findViewById(R.id.chkPentavalente).setEnabled(false);
            this.findViewById(R.id.chkConjugada).setEnabled(false);
            this.findViewById(R.id.chkPolisacarida).setEnabled(false);
            this.findViewById(R.id.chkHeptavalente).setEnabled(false);
            this.findViewById(R.id.chk23Polisacarida).setEnabled(false);
            this.findViewById(R.id.chk13Valente).setEnabled(false);
            this.findViewById(R.id.chkEstacional).setEnabled(false);
            this.findViewById(R.id.chkH1N1p).setEnabled(false);
            this.findViewById(R.id.chkOtraVacunaAntiInfluenza).setEnabled(false);
            this.findViewById(R.id.chkAntiHibNoSabe).setEnabled(true);
            this.findViewById(R.id.chkAntiMenigNoSabe).setEnabled(true);
            this.findViewById(R.id.chkAntiNeumococicaNoSabe).setEnabled(true);
            this.findViewById(R.id.chkAntiInfluenzaNoSabe).setEnabled(true);
            this.findViewById(R.id.edtxtNoDosisPentavalente).setEnabled(false);
            this.findViewById(R.id.dpFechaUltimaDosisAntiHib).setEnabled(false);
            this.findViewById(R.id.edtxtNoDosisAntiMening).setEnabled(false);
            this.findViewById(R.id.dpFechaUltimaDosisAntiMening).setEnabled(false);
            this.findViewById(R.id.edtxtNoDosisAntiNeumococica).setEnabled(false);
            this.findViewById(R.id.dpFechaUltimaDosisAntiNeumococica).setEnabled(false);
            this.findViewById(R.id.edtxtNoDosisAntiInfluenza).setEnabled(false);
            this.findViewById(R.id.dpFechaUltimaDosisAntiInfluenza).setEnabled(false);
        }
        // Anti Hib
        chkAntiHib = vigilanciaIntegradaIragEtiDTO.getAntiHib();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkAntiHibSi),
                findViewById(R.id.chkAntiHibNo), findViewById(R.id.chkAntiHibNoSabe),
                ((chkAntiHib != null)
                        ? chkAntiHib.charValue() : '4'));
        // Anti Meningococica
        chkAntiMeningococica = vigilanciaIntegradaIragEtiDTO.getAntiMeningococica();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkAntiMenigSi),
                findViewById(R.id.chkAntiMenigNo), findViewById(R.id.chkAntiMenigNoSabe),
                ((chkAntiMeningococica != null)
                        ? chkAntiMeningococica.charValue() : '4'));
        // Anti Neumococica
        chkAntiNeumococica = vigilanciaIntegradaIragEtiDTO.getAntiNeumococica();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkAntiNeumococicaSi),
                findViewById(R.id.chkAntiNeumococicaNo), findViewById(R.id.chkAntiNeumococicaNoSabe),
                ((chkAntiNeumococica != null)
                        ? chkAntiNeumococica.charValue() : '4'));
        // Anti Influenza
        chkAntiInfluenza = vigilanciaIntegradaIragEtiDTO.getAntiInfluenza();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkAntiInfluenzaSi),
                findViewById(R.id.chkAntiInfluenzaNo), findViewById(R.id.chkAntiInfluenzaNoSabe),
                ((chkAntiInfluenza != null)
                        ? chkAntiInfluenza.charValue() : '4'));
        // Pentavalente
        chkPentavalente = vigilanciaIntegradaIragEtiDTO.getPentavalente();
        if (chkPentavalente.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkPentavalente)).setChecked(true);
            this.findViewById(R.id.edtxtNoDosisPentavalente).setEnabled(true);
            this.findViewById(R.id.dpFechaUltimaDosisAntiHib).setEnabled(true);
        }
        // Conjugada
        chkConjugada = vigilanciaIntegradaIragEtiDTO.getConjugada();
        if (chkConjugada.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkConjugada)).setChecked(true);
            this.findViewById(R.id.edtxtNoDosisAntiMening).setEnabled(true);
            this.findViewById(R.id.dpFechaUltimaDosisAntiMening).setEnabled(true);
        }
        // Polisacarida
        chkPolisacarida = vigilanciaIntegradaIragEtiDTO.getPolisacarida();
        if (chkPolisacarida.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkPolisacarida)).setChecked(true);
            this.findViewById(R.id.edtxtNoDosisAntiMening).setEnabled(true);
            this.findViewById(R.id.dpFechaUltimaDosisAntiMening).setEnabled(true);
        }
        // Heptavalente
        chkHeptavalente = vigilanciaIntegradaIragEtiDTO.getHeptavalente();
        if (chkHeptavalente.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkHeptavalente)).setChecked(true);
            this.findViewById(R.id.edtxtNoDosisAntiNeumococica).setEnabled(true);
            this.findViewById(R.id.dpFechaUltimaDosisAntiNeumococica).setEnabled(true);
        }
        // 23Polisacarida
        chkPolisacarida23 = vigilanciaIntegradaIragEtiDTO.getPolisacarida23();
        if (chkPolisacarida23.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chk23Polisacarida)).setChecked(true);
            this.findViewById(R.id.edtxtNoDosisAntiNeumococica).setEnabled(true);
            this.findViewById(R.id.dpFechaUltimaDosisAntiNeumococica).setEnabled(true);
        }
        // 13Valente
        chkValente13 = vigilanciaIntegradaIragEtiDTO.getValente13();
        if (chkValente13.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chk13Valente)).setChecked(true);
            this.findViewById(R.id.edtxtNoDosisAntiNeumococica).setEnabled(true);
            this.findViewById(R.id.dpFechaUltimaDosisAntiNeumococica).setEnabled(true);
        }
        // Estacional
        chkEstacional = vigilanciaIntegradaIragEtiDTO.getEstacional();
        if (chkEstacional.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkEstacional)).setChecked(true);
            this.findViewById(R.id.edtxtNoDosisAntiInfluenza).setEnabled(true);
            this.findViewById(R.id.dpFechaUltimaDosisAntiInfluenza).setEnabled(true);
        }
        // H1n1p
        chkH1n1p = vigilanciaIntegradaIragEtiDTO.getH1n1p();
        if (chkH1n1p.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkH1N1p)).setChecked(true);
            this.findViewById(R.id.edtxtNoDosisAntiInfluenza).setEnabled(true);
            this.findViewById(R.id.dpFechaUltimaDosisAntiInfluenza).setEnabled(true);
        }
        // OtraVacuna
        chkOtraVacuna = vigilanciaIntegradaIragEtiDTO.getOtraVacuna();
        if (chkOtraVacuna.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkOtraVacunaAntiInfluenza)).setChecked(true);
            this.findViewById(R.id.edtxtNoDosisAntiInfluenza).setEnabled(true);
            this.findViewById(R.id.dpFechaUltimaDosisAntiInfluenza).setEnabled(true);
        }
        // No de dosis Anti Hib
        noDosisAntiHib = vigilanciaIntegradaIragEtiDTO.getNoDosisAntiHib();
        //vigilanciaIntegrada.setNoDosisAntiHib(Short.valueOf(((EditText) getActivity().findViewById(R.id.edtxtNoDosisPentavalente)).getText().toString()));
        if (noDosisAntiHib != null) {
            ((EditText) this.findViewById(R.id.edtxtNoDosisPentavalente)).setText(String.valueOf(noDosisAntiHib));
        }
        // No de dosis Anti Meningococica
        noDosisAntiMening = vigilanciaIntegradaIragEtiDTO.getNoDosisAntiMening();
        if (noDosisAntiMening != null) {
            ((EditText) this.findViewById(R.id.edtxtNoDosisAntiMening)).setText(String.valueOf(noDosisAntiMening));
        }
        // No de dosis Anti Neumococica
        noDosisAntiNeumo = vigilanciaIntegradaIragEtiDTO.getNoDosisAntiNeumo();
        if (noDosisAntiNeumo != null) {
            ((EditText) this.findViewById(R.id.edtxtNoDosisAntiNeumococica)).setText(String.valueOf(noDosisAntiNeumo));
        }
        // No de dosis Anti Influenza
        noDosisAntiInflu = vigilanciaIntegradaIragEtiDTO.getNoDosisAntiInflu();
        if (noDosisAntiInflu != null) {
            ((EditText) this.findViewById(R.id.edtxtNoDosisAntiInfluenza)).setText(String.valueOf(noDosisAntiInflu));
        }
        // Fecha ultima dosis Anti Hib
        fechaUltDosisAntiHib = vigilanciaIntegradaIragEtiDTO.getFechaUltDosisAntiHib();
        if (!StringUtils.isNullOrEmpty(fechaUltDosisAntiHib)) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = inputFormat.parse(fechaUltDosisAntiHib);
                String outputDateStr = outputFormat.format(date);
                ((EditText) this.findViewById(R.id.dpFechaUltimaDosisAntiHib)).setText(outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Fecha ultima dosis Anti Meningococica
        fechaUltDosisAntiMening = vigilanciaIntegradaIragEtiDTO.getFechaUltDosisAntiMening();
        if (!StringUtils.isNullOrEmpty(fechaUltDosisAntiMening)) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = inputFormat.parse(fechaUltDosisAntiMening);
                String outputDateStr = outputFormat.format(date);
                ((EditText) this.findViewById(R.id.dpFechaUltimaDosisAntiMening)).setText(outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Fecha ultima dosis Anti Neumococica
        fechaUltDosisAntiNeumo = vigilanciaIntegradaIragEtiDTO.getFechaUltDosisAntiNeumo();
        if (!StringUtils.isNullOrEmpty(fechaUltDosisAntiNeumo)) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = inputFormat.parse(fechaUltDosisAntiNeumo);
                String outputDateStr = outputFormat.format(date);
                ((EditText) this.findViewById(R.id.dpFechaUltimaDosisAntiNeumococica)).setText(outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Fecha ultima dosis Anti Influenza
        fechaUltDosisAntiInflu = vigilanciaIntegradaIragEtiDTO.getFechaUltDosisAntiInflu();
        if (!StringUtils.isNullOrEmpty(fechaUltDosisAntiInflu)) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = inputFormat.parse(fechaUltDosisAntiInflu);
                String outputDateStr = outputFormat.format(date);
                ((EditText) this.findViewById(R.id.dpFechaUltimaDosisAntiInfluenza)).setText(outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Cancer
        chkCancer = vigilanciaIntegradaIragEtiDTO.getCancer();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkCancerSi),
                findViewById(R.id.chkCancerNo), findViewById(R.id.chkCancerNs),
                ((chkCancer != null)
                        ? chkCancer.charValue() : '4'));
        // Diabetes
        chkDiabetes = vigilanciaIntegradaIragEtiDTO.getDiabetes();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkDiabetesSi),
                findViewById(R.id.chkDiabetesNo), findViewById(R.id.chkDiabetesNs),
                ((chkDiabetes != null)
                        ? chkDiabetes.charValue() : '4'));
        // VIH
        chkVih = vigilanciaIntegradaIragEtiDTO.getVih();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkVihSi),
                findViewById(R.id.chkVihNo), findViewById(R.id.chkVihNs),
                ((chkVih != null)
                        ? chkVih.charValue() : '4'));
        // Otra Inmunodeficiencia
        chkOtraInmunodeficiencia = vigilanciaIntegradaIragEtiDTO.getOtraInmunodeficiencia();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkOtraInmunodeficienciaSi),
                findViewById(R.id.chkOtraInmunodeficienciaNo), findViewById(R.id.chkOtraInmunodeficienciaNs),
                ((chkOtraInmunodeficiencia != null)
                        ? chkOtraInmunodeficiencia.charValue() : '4'));
        // Enfermedad nurologica cronica
        chkEnfNeurologicaCronica = vigilanciaIntegradaIragEtiDTO.getEnfNeurologicaCronica();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkEnfNeurologicaCronicaSi),
                findViewById(R.id.chkEnfNeurologicaCronicaNo), findViewById(R.id.chkEnfNeurologicaCronicaNs),
                ((chkEnfNeurologicaCronica != null)
                        ? chkEnfNeurologicaCronica.charValue() : '4'));
        // Enfermedad cardiaca
        chkEnfCardiaca = vigilanciaIntegradaIragEtiDTO.getEnfCardiaca();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkEnfermedadCardiacaSi),
                findViewById(R.id.chkEnfermedadCardiacaNo), findViewById(R.id.chkEnfermedadCardiacaNs),
                ((chkEnfCardiaca != null)
                        ? chkEnfCardiaca.charValue() : '4'));
        // Asma
        chkAsma = vigilanciaIntegradaIragEtiDTO.getAsma();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkAsmaSi),
                findViewById(R.id.chkAsmaNo), findViewById(R.id.chkAsmaNs),
                ((chkAsma != null)
                        ? chkAsma.charValue() : '4'));
        // Epoc
        chkEpoc = vigilanciaIntegradaIragEtiDTO.getEpoc();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkEpocSi),
                findViewById(R.id.chkEpocNo), findViewById(R.id.chkEpocNs),
                ((chkEpoc != null)
                        ? chkEpoc.charValue() : '4'));
        // Otra enfermedad pulmonar
        chkOtraEnfPulmonar = vigilanciaIntegradaIragEtiDTO.getOtraEnfPulmonar();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkOtraEnfPulmonarSi),
                findViewById(R.id.chkOtraEnfPulmonarNo), findViewById(R.id.chkOtraEnfPulmonarNs),
                ((chkOtraEnfPulmonar != null)
                        ? chkOtraEnfPulmonar.charValue() : '4'));
        // Insuficiencia renal cronica
        chkInsufRenalCronica = vigilanciaIntegradaIragEtiDTO.getInsufRenalCronica();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkInsufRenalCronicaSi),
                findViewById(R.id.chkInsufRenalCronicaNo), findViewById(R.id.chkInsufRenalCronicaNs),
                ((chkInsufRenalCronica != null)
                        ? chkInsufRenalCronica.charValue() : '4'));
        // Desnutricion
        chkDesnutricion = vigilanciaIntegradaIragEtiDTO.getDesnutricion();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkDesnutricionSi),
                findViewById(R.id.chkDesnutricionNo), findViewById(R.id.chkDesnutricionNs),
                ((chkDesnutricion != null)
                        ? chkDesnutricion.charValue() : '4'));
        // Obesidad
        chkObesidad = vigilanciaIntegradaIragEtiDTO.getObesidad();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkObesidadSi),
                findViewById(R.id.chkObesidadNo), findViewById(R.id.chkObesidadNs),
                ((chkObesidad != null)
                        ? chkObesidad.charValue() : '4'));
        // Embarazo
        chkEmbarazo = vigilanciaIntegradaIragEtiDTO.getEmbarazo();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkEmbarazoSi),
                findViewById(R.id.chkEmbarazoNo), findViewById(R.id.chkEmbarazoNs),
                ((chkEmbarazo != null)
                        ? chkEmbarazo.charValue() : '4'));
        // Semanas embarazo
        edtxtEmbarazoSemanas = vigilanciaIntegradaIragEtiDTO.getEmbarazoSemanas();
        if (edtxtEmbarazoSemanas != null) {
            ((EditText) this.findViewById(R.id.edtxtSemanasEmbarazo)).setText(String.valueOf(edtxtEmbarazoSemanas));
            this.findViewById(R.id.edtxtSemanasEmbarazo).setEnabled(true);
        }
        // Tx con corticosteroide
        chkTxCorticosteroide = vigilanciaIntegradaIragEtiDTO.getTxCorticosteroide();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkTxCorticosteroideSi),
                findViewById(R.id.chkTxCorticosteroideNo), findViewById(R.id.chkTxCorticosteroideNs),
                ((chkTxCorticosteroide != null)
                        ? chkTxCorticosteroide.charValue() : '4'));
        // Otra condicion
        chkOtraCondicion = vigilanciaIntegradaIragEtiDTO.getOtraCondicion();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkOtraSi),
                findViewById(R.id.chkOtraNo), findViewById(R.id.chkOtraNs),
                ((chkOtraCondicion != null)
                        ? chkOtraCondicion.charValue() : '4'));
        // Otra condicion preexistente
        edtxtOtraCondPreexistente = vigilanciaIntegradaIragEtiDTO.getOtraCondPreexistente();
        if (edtxtOtraCondPreexistente != null) {
            ((EditText) this.findViewById(R.id.edtxtOtra)).setText(edtxtOtraCondPreexistente);
            this.findViewById(R.id.edtxtOtra).setEnabled(true);
        }

        // Estornudos
        chkEstornudos = vigilanciaIntegradaIragEtiDTO.getEstornudos();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkEstornudosSi),
                findViewById(R.id.chkEstornudosNo), findViewById(R.id.chkEstornudosNs),
                ((chkEstornudos != null)
                        ? chkEstornudos.charValue() : '4'));

        // Otra Manifestacion Clinica
        chkOtraManifestacionClinica = vigilanciaIntegradaIragEtiDTO.getOtraManifestacionClinica();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkOtraManifestacionClinicaSi),
                findViewById(R.id.chkOtraManifestacionClinicaNo), findViewById(R.id.chkOtraManifestacionClinicaNs),
                ((chkOtraManifestacionClinica != null)
                        ? chkOtraManifestacionClinica.charValue() : '4'));

        edtxtOtraManifestacionClinica = vigilanciaIntegradaIragEtiDTO.getCualManifestacionClinica();
        if (edtxtOtraManifestacionClinica != null) {
            ((EditText) this.findViewById(R.id.edtxtOtraManifestacionClinica)).setText(edtxtOtraManifestacionClinica);
            this.findViewById(R.id.edtxtOtraManifestacionClinica).setEnabled(true);
        }
        // Uso de antibiotico durante la ultima semana
        chkUsoAntibioticoUltimaSemana = vigilanciaIntegradaIragEtiDTO.getUsoAntibioticoUltimaSemana();
        AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkUsoAntibioticoSi),
                findViewById(R.id.chkUsoAntibioticoNo), findViewById(R.id.chkUsoAntibioticoNoSabe),
                ((chkUsoAntibioticoUltimaSemana != null)
                        ? chkUsoAntibioticoUltimaSemana.charValue() : '4'));
        // Cuantos antibioticos le dio
        edtxtCuantosAntibioticosLeDio = vigilanciaIntegradaIragEtiDTO.getCuantosAntibioticosLeDio();
        if (edtxtCuantosAntibioticosLeDio != null) {
            ((EditText) this.findViewById(R.id.edtxtCuantosAntibioticos)).setText(String.valueOf(edtxtCuantosAntibioticosLeDio));
            this.findViewById(R.id.edtxtCuantosAntibioticos).setEnabled(true);
        }
        // Cuales antibioticos le dio
        edtxtCualesAntibioticosLeDio = vigilanciaIntegradaIragEtiDTO.getCualesAntibioticosLeDio();
        if (edtxtCualesAntibioticosLeDio != null) {
            ((EditText) this.findViewById(R.id.edtxtCualesAntibioticosDio)).setText(edtxtCualesAntibioticosLeDio);
            this.findViewById(R.id.edtxtCualesAntibioticosDio).setEnabled(true);
        }
        // Cuantos dias le dio el utlimo antibiotico
        edtxtCuantosDiasLeDioElUltimoAntibiotico = vigilanciaIntegradaIragEtiDTO.getCuantosDiasLeDioElUltimoAntibiotico();
        if (edtxtCuantosDiasLeDioElUltimoAntibiotico != null) {
            ((EditText) this.findViewById(R.id.edtxtDiasUltimoAntibiotico)).setText(String.valueOf(edtxtCuantosDiasLeDioElUltimoAntibiotico));
            this.findViewById(R.id.edtxtDiasUltimoAntibiotico).setEnabled(true);
        }
        // Via oral
        chkViaOral = vigilanciaIntegradaIragEtiDTO.getViaOral();
        if (chkViaOral.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkViaOral)).setChecked(true);
            this.findViewById(R.id.chkViaOral).setEnabled(true);
            this.findViewById(R.id.chkViaParenteral).setEnabled(true);
            this.findViewById(R.id.chkViaAmbas).setEnabled(true);
        }
        // Via parenteral
        chkViaParenteral = vigilanciaIntegradaIragEtiDTO.getViaParenteral();
        if (chkViaParenteral.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkViaParenteral)).setChecked(true);
            this.findViewById(R.id.chkViaOral).setEnabled(true);
            this.findViewById(R.id.chkViaParenteral).setEnabled(true);
            this.findViewById(R.id.chkViaAmbas).setEnabled(true);
        }
        // Via ambas
        chkViaAmbas = vigilanciaIntegradaIragEtiDTO.getViaAmbas();
        if (chkViaAmbas.compareTo('0') == '0') {
            ((CheckBox) this.findViewById(R.id.chkViaAmbas)).setChecked(true);
            this.findViewById(R.id.chkViaOral).setEnabled(true);
            this.findViewById(R.id.chkViaParenteral).setEnabled(true);
            this.findViewById(R.id.chkViaAmbas).setEnabled(true);
        }
        // Antecedentes uso antivirales
        chkAntecedentesUsoAntivirales = vigilanciaIntegradaIragEtiDTO.getAntecedentesUsoAntivirales();
        if (chkAntecedentesUsoAntivirales.compareTo('0') == 0) {
            ((CheckBox) this.findViewById(R.id.chkUsoAntiViralSi)).setChecked(true);
        } else {
            ((CheckBox) this.findViewById(R.id.chkUsoAntiViralNo)).setChecked(true);
        }
        // Nombre antiviral
        edtxtNombreAntiviral = vigilanciaIntegradaIragEtiDTO.getNombreAntiviral();
        if (edtxtNombreAntiviral != null) {
            ((EditText) this.findViewById(R.id.edtxtNombreAntiviral)).setText(edtxtNombreAntiviral);
            this.findViewById(R.id.edtxtNombreAntiviral).setEnabled(true);
        }
        // Fecha 1ra dosis
        edtxtFecha1raDosis = vigilanciaIntegradaIragEtiDTO.getFecha1raDosis();
        if (edtxtFecha1raDosis != null) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = inputFormat.parse(edtxtFecha1raDosis);
                String outputDateStr = outputFormat.format(date);
                ((EditText) this.findViewById(R.id.dpFecha1raDosisAntiViral)).setText(outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.findViewById(R.id.dpFecha1raDosisAntiViral).setEnabled(true);
        }
        // Fecha ultima dosis
        edtxtFechaUltimaDosis = vigilanciaIntegradaIragEtiDTO.getFechaUltimaDosis();
        if (edtxtFechaUltimaDosis != null) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = inputFormat.parse(edtxtFechaUltimaDosis);
                String outputDateStr = outputFormat.format(date);
                ((EditText) this.findViewById(R.id.dpFechaUltDosisAntiViral)).setText(outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.findViewById(R.id.dpFechaUltDosisAntiViral).setEnabled(true);
        }
        // No dosis administrada
        edtxtNoDosisAdministrada = vigilanciaIntegradaIragEtiDTO.getNoDosisAdministrada();
        if (edtxtNoDosisAdministrada != null) {
            ((EditText) this.findViewById(R.id.edtxtNoDosisAdministrada)).setText(String.valueOf(edtxtNoDosisAdministrada));
            this.findViewById(R.id.edtxtNoDosisAdministrada).setEnabled(true);
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public byte[] RESPUESTA = null;

        public EditText edtxtNoDosisPentavalente;
        public EditText dpFechaUltimaDosisAntiHib;
        public EditText edtxtNoDosisAntiMening;
        public EditText dpFechaUltimaDosisAntiMening;
        public EditText edtxtNoDosisAntiNeumococica;
        public EditText dpFechaUltimaDosisAntiNeumococica;
        public EditText edtxtNoDosisAntiInfluenza;
        public EditText dpFechaUltimaDosisAntiInfluenza;
        public EditText edtxtSemanasEmbarazo;
        public EditText edtxtOtra;
        public EditText edtxtCuantosAntibioticos;
        public EditText edtxtCualesAntibioticosDio;
        public EditText edtxtDiasUltimoAntibiotico;
        public EditText edtxtNombreAntiviral;
        public EditText dpFecha1raDosisAntiViral;
        public EditText dpFechaUltDosisAntiViral;
        public EditText edtxtNoDosisAdministrada;
        public EditText edtxtOtraManifestacionClinica;
        View focusView = null;
        String mensaje = "Debe seleccionar al menos una opcion";
        String strDepartamento = "Debe seleccionar el departamento";
        String strMunicipio = "Debe seleccionar el municipio";
        String strNombreApellido = "Primero debe de realizar la busqueda";

        public static VigilanciaIntegradaActivity.PlaceholderFragment newInstance(int sectionNumber) {
            VigilanciaIntegradaActivity.PlaceholderFragment fragment = new VigilanciaIntegradaActivity.PlaceholderFragment();
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
            View vigilanciaIntegrada = inflater.inflate(R.layout.formulario_vigilancia_integrada_irag_eti_layout, container, false);
            if (savedInstanceState != null) {
                mVigilanciaIntegradaActivity.mSecVigilanciaIntegrada = savedInstanceState.getInt("secVigilanciaIntegrada");
                mVigilanciaIntegradaActivity.mSecHojaConsulta = savedInstanceState.getInt("secHojaConsulta");
                mVigilanciaIntegradaActivity.mCodExpediente = savedInstanceState.getInt("codExpediente");
                mVigilanciaIntegradaActivity.mIrag = savedInstanceState.getChar("irag");
                mVigilanciaIntegradaActivity.mEti = savedInstanceState.getChar("eti");
                mVigilanciaIntegradaActivity.mIragInusitada = savedInstanceState.getChar("iragInusitada");
                mVigilanciaIntegradaActivity.mNombrePaciente = savedInstanceState.getString("nombrePaciente");
                mVigilanciaIntegradaActivity.mTutor = savedInstanceState.getString("tutor");
                mVigilanciaIntegradaActivity.mNumHojaConsulta = savedInstanceState.getInt("mNumHojaConsulta");
            }

            inicializarControles(vigilanciaIntegrada);
            return vigilanciaIntegrada;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("secVigilanciaIntegrada", mVigilanciaIntegradaActivity.mSecVigilanciaIntegrada);
            outState.putInt("secHojaConsulta", mVigilanciaIntegradaActivity.mSecHojaConsulta);
            outState.putInt("codExpediente", mVigilanciaIntegradaActivity.mCodExpediente);
            outState.putString("irag", ((CheckBox) getActivity().findViewById(R.id.chkIrag)).getText().toString());
            outState.putString("eti", ((CheckBox) getActivity().findViewById(R.id.chkEti)).getText().toString());
            outState.putString("iragInusitada", ((CheckBox) getActivity().findViewById(R.id.chkIragInusitada)).getText().toString());
            outState.putSerializable("nombrePaciente", mVigilanciaIntegradaActivity.mNombrePaciente);
            outState.putSerializable("tutor", mVigilanciaIntegradaActivity.mTutor);
            outState.putInt("mNumHojaConsulta", mVigilanciaIntegradaActivity.mNumHojaConsulta);
        }

        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getActivity().findViewById(R.id.btnGuardarFicha).setEnabled(false);
            final Spinner spnDepartamentos = (Spinner) getActivity().findViewById(R.id.spnDepartamentos);
            if (spnDepartamentos != null) {
                spnDepartamentos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        //Spinner sp;
                        //sp = (Spinner) spnDepartamentos.getSelectedItem();
                        DepartamentosDTO departamentosDTO = (DepartamentosDTO) ((Spinner) getActivity().findViewById(R.id.spnDepartamentos)).getSelectedItem();

                        if (departamentosDTO.getDivisionpoliticaId() > 0) {
                            llamadoServicioObtenerMunicipios(departamentosDTO.getDivisionpoliticaId());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
            establecerMetodosBotonesAtrasSiguiente();
            this.establecerMetodosBotones();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public void onStart() {
            super.onStart();
            if (mVigilanciaIntegradaActivity.mSecVigilanciaIntegrada > 0 && mVigilanciaIntegradaActivity.mSecHojaConsulta > 0 &&
                    mVigilanciaIntegradaActivity.mCodExpediente > 0 && mVigilanciaIntegradaActivity.mNombrePaciente != null
                    && mVigilanciaIntegradaActivity.mTutor != null) {
                ((EditText) getActivity().findViewById(R.id.edtxtNombreApellido)).setText(String.valueOf(mVigilanciaIntegradaActivity.mNombrePaciente));
                ((EditText) getActivity().findViewById(R.id.edtxtTutor)).setText(String.valueOf(mVigilanciaIntegradaActivity.mTutor));
                if (mVigilanciaIntegradaActivity.mIrag == 0) {
                    ((CheckBox) getActivity().findViewById(R.id.chkIrag)).setChecked(true);
                    ((CheckBox) getActivity().findViewById(R.id.chkEti)).setChecked(false);
                    ((CheckBox) getActivity().findViewById(R.id.chkIragInusitada)).setChecked(false);
                }
                if (mVigilanciaIntegradaActivity.mEti == 0) {
                    ((CheckBox) getActivity().findViewById(R.id.chkIrag)).setChecked(false);
                    ((CheckBox) getActivity().findViewById(R.id.chkEti)).setChecked(true);
                    ((CheckBox) getActivity().findViewById(R.id.chkIragInusitada)).setChecked(false);
                }
                if (mVigilanciaIntegradaActivity.mIragInusitada == 0) {
                    ((CheckBox) getActivity().findViewById(R.id.chkIrag)).setChecked(false);
                    ((CheckBox) getActivity().findViewById(R.id.chkEti)).setChecked(false);
                    ((CheckBox) getActivity().findViewById(R.id.chkIragInusitada)).setChecked(true);
                }
            }
        }

        /***
         * Establece los metodos que seran ejecutados en los eventos onClick.
         */
        protected void establecerMetodosBotones() {
            Button btnBuscarFichaExp = (Button) getActivity().findViewById(R.id.ibtBuscarFichaExp);
            btnBuscarFichaExp.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    buscarPaciente();
                }
            });

            Button btnGuardar = (Button) getActivity().findViewById(R.id.btnGuardarFicha);
            btnGuardar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    DialogInterface.OnClickListener preguntaGuardarDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    guardarFichaVigilanciaIntegrada();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    MensajesHelper.mostrarMensajeYesNo(getActivity(), getResources().getString(
                            R.string.msj_aviso_guardar_ficha_vigilancia), getResources().getString(
                            R.string.title_estudio_sostenible), preguntaGuardarDialogClickListener);
                }
            });

            Button btnLimpiarFicha = (Button) getActivity().findViewById(R.id.btnlimpiarFicha);
            btnLimpiarFicha.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    existeDetalle = false;
                    getActivity().findViewById(R.id.ibtBuscarFichaExp).setEnabled(true);
                    Intent intent = new Intent();
                    intent.setAction("com.sts_ni.estudiocohortecssfv.vigilancia_integrada_action");
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            //botón para presentar el pdf de la ficha vigilancia de infecciones respiratorias
            ImageButton ibtSeguimientoPDF = (ImageButton) getActivity().findViewById(R.id.ibtFichaPDF);
            ibtSeguimientoPDF.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (existeDetalle) {
                        obtenerFichaPdf();
                    }/*else{
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                getResources().getString(R.string.msj_sin_detalle_ficha),
                                getResources().getString(R.string.app_name), null);
                    }*/
                }
            });

            //boton para imprimir la ficha vigilancia de infecciones respiratorias
            ImageButton btnImprimirFicha = (ImageButton) getActivity().findViewById(R.id.ibtImprimirFicha);
            btnImprimirFicha.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (existeDetalle) {
                        //ImprimirFichaPdf();
                        showAlertDialogFicha();
                    }/*else{
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                getResources().getString(R.string.msj_sin_detalle_seg_influenza),
                                getResources().getString(R.string.app_name), null);
                    }*/
                }
            });
        }

        /***
         * Metodo para llamar el servicio que realiza la busqueda por codigo expediente o
         * Numero de Hoja de Consulta.
         */
        private void buscarPaciente() {
            String mensajeBusqueda = "No puede buscar por los dos campos a la vez";
            /*Si el codigo de Expediente y el Numero de Hoja de Consulta tienen datos entonces se enviara un mensaje de error
             * Indicando que para realizar la busqueda solo necesita ingresar uno
             * */
            if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtCodExpediente)).getText().toString()) &&
                    !StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNumHojaConsulta)).getText().toString())) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        mensajeBusqueda, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return;
            }
            if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtCodExpediente)).getText().toString()) ||
                    !StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNumHojaConsulta)).getText().toString())) {
                if (mVigilanciaIntegradaActivity.mBuscarVigilanciaIntegradaTask == null ||
                        mVigilanciaIntegradaActivity.mBuscarVigilanciaIntegradaTask.getStatus() == BuscarVigilanciaIntegradaTask.Status.FINISHED) {
                    mVigilanciaIntegradaActivity.mBuscarVigilanciaIntegradaTask = (BuscarVigilanciaIntegradaTask) new
                            BuscarVigilanciaIntegradaTask(mVigilanciaIntegradaActivity).execute("1", ((EditText) getActivity()
                            .findViewById(R.id.edtxtCodExpediente)).getText().toString(), ((EditText) getActivity()
                            .findViewById(R.id.edtxtNumHojaConsulta)).getText().toString());
                }
            }
        }

        /***
         * Metodo que llama servicio para guardar
         */
        public void guardarFichaVigilanciaIntegrada() {
            if (mVigilanciaIntegradaActivity.mSecVigilanciaIntegrada > 0 || mVigilanciaIntegradaActivity.mSecHojaConsulta > 0) {
                if (mVigilanciaIntegradaActivity.mGuardarVigilanciaIntegradaTask == null ||
                        mVigilanciaIntegradaActivity.mGuardarVigilanciaIntegradaTask.getStatus() == GuardarVigilanciaIntegradaTask.Status.FINISHED) {

                    if (validarDatosPaciente() && validarAntecedentesVacunacion()
                            && validarCondicionesPreexistentes() && validarUsoAntibioticos()) {
                        VigilanciaIntegradaIragEtiDTO vigilanciaIntegrada = new VigilanciaIntegradaIragEtiDTO();

                        vigilanciaIntegrada.setSecVigilanciaIntegrada(mVigilanciaIntegradaActivity.mSecVigilanciaIntegrada);
                        vigilanciaIntegrada.setSecHojaConsulta(mVigilanciaIntegradaActivity.mSecHojaConsulta);
                        vigilanciaIntegrada.setCodExpediente(mVigilanciaIntegradaActivity.mCodExpediente);
                        vigilanciaIntegrada.setNombrePaciente(mVigilanciaIntegradaActivity.mNombrePaciente);
                        vigilanciaIntegrada.setTutor(mVigilanciaIntegradaActivity.mTutor);
                        vigilanciaIntegrada.setNumHojaConsulta(mVigilanciaIntegradaActivity.mNumHojaConsulta);

                        DepartamentosDTO departamentosDTO = (DepartamentosDTO) ((Spinner) getActivity().findViewById(R.id.spnDepartamentos)).getSelectedItem();
                        MunicipiosDTO municipiosDTO = (MunicipiosDTO) ((Spinner) getActivity().findViewById(R.id.spnMunicipios)).getSelectedItem();
                        vigilanciaIntegrada.setDepartamento(String.valueOf(departamentosDTO.getCodigoNacional()));
                        vigilanciaIntegrada.setMunicipio(String.valueOf(municipiosDTO.getCodigoNacional()));
                        //vigilanciaIntegrada.setDepartamento(String.valueOf(((Spinner) getActivity().findViewById(R.id.spnDepartamentos)).getSelectedItemId()));
                        //vigilanciaIntegrada.setMunicipio(String.valueOf(((Spinner) getActivity().findViewById(R.id.spnMunicipios)).getSelectedItemId()));
                        vigilanciaIntegrada.setBarrio(((EditText) getActivity().findViewById(R.id.edtxtBarrio)).getText().toString());
                        vigilanciaIntegrada.setDireccion(((EditText) getActivity().findViewById(R.id.edtxtDireccion)).getText().toString());
                        vigilanciaIntegrada.setTelefono(((EditText) getActivity().findViewById(R.id.edtxtTelefono)).getText().toString());
                        vigilanciaIntegrada.setUrbano(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkUrbano)));
                        vigilanciaIntegrada.setRural(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkRural)));
                        vigilanciaIntegrada.setEmergencia(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkCaptacionEmergencia)));
                        vigilanciaIntegrada.setSala(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkCaptacionSala)));
                        vigilanciaIntegrada.setUci(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkCaptacionUci)));
                        vigilanciaIntegrada.setEmergAmbulatorio(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkCaptacionAmbulatorio)));
                        vigilanciaIntegrada.setDiagnostico(((EditText) getActivity().findViewById(R.id.edtxtDiagnostico)).getText().toString());

                        vigilanciaIntegrada.setPresentTarjVacuna(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkTarjetaVacunaSi), getActivity().findViewById(R.id.chkTarjetaVacunaNo)));
                        vigilanciaIntegrada.setAntiHib(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkAntiHibSi), getActivity().findViewById(R.id.chkAntiHibNo), getActivity().findViewById(R.id.chkAntiHibNoSabe)));
                        vigilanciaIntegrada.setAntiMeningococica(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkAntiMenigSi), getActivity().findViewById(R.id.chkAntiMenigNo), getActivity().findViewById(R.id.chkAntiMenigNoSabe)));
                        vigilanciaIntegrada.setAntiNeumococica(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkAntiNeumococicaSi), getActivity().findViewById(R.id.chkAntiNeumococicaNo), getActivity().findViewById(R.id.chkAntiNeumococicaNoSabe)));
                        vigilanciaIntegrada.setAntiInfluenza(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkAntiInfluenzaSi), getActivity().findViewById(R.id.chkAntiInfluenzaNo), getActivity().findViewById(R.id.chkAntiInfluenzaNoSabe)));

                        vigilanciaIntegrada.setPentavalente(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkPentavalente)));
                        vigilanciaIntegrada.setConjugada(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkConjugada)));
                        vigilanciaIntegrada.setPolisacarida(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkPolisacarida)));
                        vigilanciaIntegrada.setHeptavalente(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkHeptavalente)));
                        vigilanciaIntegrada.setPolisacarida23(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chk23Polisacarida)));
                        vigilanciaIntegrada.setValente13(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chk13Valente)));
                        vigilanciaIntegrada.setEstacional(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkEstacional)));
                        vigilanciaIntegrada.setH1n1p(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkH1N1p)));
                        vigilanciaIntegrada.setOtraVacuna(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza)));

                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNoDosisPentavalente)).getText().toString())) {
                            vigilanciaIntegrada.setNoDosisAntiHib(Short.valueOf(((EditText) getActivity().findViewById(R.id.edtxtNoDosisPentavalente)).getText().toString()));
                        } else {
                            vigilanciaIntegrada.setNoDosisAntiHib(null);
                        }
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiMening)).getText().toString())) {
                            vigilanciaIntegrada.setNoDosisAntiMening(Short.valueOf(((EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiMening)).getText().toString()));
                        } else {
                            vigilanciaIntegrada.setNoDosisAntiMening(null);
                        }
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiNeumococica)).getText().toString())) {
                            vigilanciaIntegrada.setNoDosisAntiNeumo(Short.valueOf(((EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiNeumococica)).getText().toString()));
                        } else {
                            vigilanciaIntegrada.setNoDosisAntiNeumo(null);
                        }
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiInfluenza)).getText().toString())) {
                            vigilanciaIntegrada.setNoDosisAntiInflu(Short.valueOf(((EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiInfluenza)).getText().toString()));
                        } else {
                            vigilanciaIntegrada.setNoDosisAntiInflu(null);
                        }
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib)).getText().toString())) {
                            vigilanciaIntegrada.setFechaUltDosisAntiHib(((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib)).getText().toString());
                        } else {
                            vigilanciaIntegrada.setFechaUltDosisAntiHib(null);
                        }
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening)).getText().toString())) {
                            vigilanciaIntegrada.setFechaUltDosisAntiMening(((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening)).getText().toString());
                        } else {
                            vigilanciaIntegrada.setFechaUltDosisAntiMening(null);
                        }
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica)).getText().toString())) {
                            vigilanciaIntegrada.setFechaUltDosisAntiNeumo(((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica)).getText().toString());
                        }
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza)).getText().toString())) {
                            vigilanciaIntegrada.setFechaUltDosisAntiInflu(((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza)).getText().toString());
                        } else {
                            vigilanciaIntegrada.setFechaUltDosisAntiInflu(null);
                        }
                        vigilanciaIntegrada.setCancer(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkCancerSi), getActivity().findViewById(R.id.chkCancerNo), getActivity().findViewById(R.id.chkCancerNs)));
                        vigilanciaIntegrada.setDiabetes(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkDiabetesSi), getActivity().findViewById(R.id.chkDiabetesNo), getActivity().findViewById(R.id.chkDiabetesNs)));
                        vigilanciaIntegrada.setVih(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkVihSi), getActivity().findViewById(R.id.chkVihNo), getActivity().findViewById(R.id.chkVihNs)));
                        vigilanciaIntegrada.setOtraInmunodeficiencia(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkOtraInmunodeficienciaSi), getActivity().findViewById(R.id.chkOtraInmunodeficienciaNo), getActivity().findViewById(R.id.chkOtraInmunodeficienciaNs)));
                        vigilanciaIntegrada.setEnfNeurologicaCronica(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkEnfNeurologicaCronicaSi), getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNo), getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNs)));
                        vigilanciaIntegrada.setEnfCardiaca(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkEnfermedadCardiacaSi), getActivity().findViewById(R.id.chkEnfermedadCardiacaNo), getActivity().findViewById(R.id.chkEnfermedadCardiacaNs)));
                        vigilanciaIntegrada.setAsma(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkAsmaSi), getActivity().findViewById(R.id.chkAsmaNo), getActivity().findViewById(R.id.chkAsmaNs)));
                        vigilanciaIntegrada.setEpoc(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkEpocSi), getActivity().findViewById(R.id.chkEpocNo), getActivity().findViewById(R.id.chkEpocNs)));
                        vigilanciaIntegrada.setOtraEnfPulmonar(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkOtraEnfPulmonarSi), getActivity().findViewById(R.id.chkOtraEnfPulmonarNo), getActivity().findViewById(R.id.chkOtraEnfPulmonarNs)));
                        vigilanciaIntegrada.setInsufRenalCronica(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkInsufRenalCronicaSi), getActivity().findViewById(R.id.chkInsufRenalCronicaNo), getActivity().findViewById(R.id.chkInsufRenalCronicaNs)));
                        vigilanciaIntegrada.setDesnutricion(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkDesnutricionSi), getActivity().findViewById(R.id.chkDesnutricionNo), getActivity().findViewById(R.id.chkDesnutricionNs)));
                        vigilanciaIntegrada.setObesidad(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkObesidadSi), getActivity().findViewById(R.id.chkObesidadNo), getActivity().findViewById(R.id.chkObesidadNs)));
                        vigilanciaIntegrada.setEmbarazo(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkEmbarazoSi), getActivity().findViewById(R.id.chkEmbarazoNo), getActivity().findViewById(R.id.chkEmbarazoNs)));
                        vigilanciaIntegrada.setTxCorticosteroide(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkTxCorticosteroideSi), getActivity().findViewById(R.id.chkTxCorticosteroideNo), getActivity().findViewById(R.id.chkTxCorticosteroideNs)));
                        vigilanciaIntegrada.setOtraCondicion(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkOtraSi), getActivity().findViewById(R.id.chkOtraNo), getActivity().findViewById(R.id.chkOtraNs)));
                        vigilanciaIntegrada.setEstornudos(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkEstornudosSi), getActivity().findViewById(R.id.chkEstornudosNo), getActivity().findViewById(R.id.chkEstornudosNs)));
                        vigilanciaIntegrada.setOtraManifestacionClinica(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkOtraManifestacionClinicaSi), getActivity().findViewById(R.id.chkOtraManifestacionClinicaNo), getActivity().findViewById(R.id.chkOtraManifestacionClinicaNs)));

                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtSemanasEmbarazo)).getText().toString())) {
                            vigilanciaIntegrada.setEmbarazoSemanas(Short.valueOf(((EditText) getActivity().findViewById(R.id.edtxtSemanasEmbarazo)).getText().toString()));
                        } else {
                            vigilanciaIntegrada.setEmbarazoSemanas(null);
                        }
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtOtra)).getText().toString())) {
                            vigilanciaIntegrada.setOtraCondPreexistente(((EditText) getActivity().findViewById(R.id.edtxtOtra)).getText().toString());
                        }

                        vigilanciaIntegrada.setUsoAntibioticoUltimaSemana(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkUsoAntibioticoSi), getActivity().findViewById(R.id.chkUsoAntibioticoNo), getActivity().findViewById(R.id.chkUsoAntibioticoNoSabe)));
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtCuantosAntibioticos)).getText().toString())) {
                            vigilanciaIntegrada.setCuantosAntibioticosLeDio(Short.valueOf(((EditText) getActivity().findViewById(R.id.edtxtCuantosAntibioticos)).getText().toString()));
                        } else {
                            vigilanciaIntegrada.setCuantosAntibioticosLeDio(null);
                        }
                        if (!StringUtils.isNullOrEmpty((((EditText) getActivity().findViewById(R.id.edtxtCualesAntibioticosDio)).getText().toString()))) {
                            vigilanciaIntegrada.setCualesAntibioticosLeDio((((EditText) getActivity().findViewById(R.id.edtxtCualesAntibioticosDio)).getText().toString()));
                        } else {
                            vigilanciaIntegrada.setCualesAntibioticosLeDio(null);
                        }
                        if (!StringUtils.isNullOrEmpty((((EditText) getActivity().findViewById(R.id.edtxtDiasUltimoAntibiotico)).getText().toString()))) {
                            vigilanciaIntegrada.setCuantosDiasLeDioElUltimoAntibiotico(Short.valueOf((((EditText) getActivity().findViewById(R.id.edtxtDiasUltimoAntibiotico)).getText().toString())));
                        } else {
                            vigilanciaIntegrada.setCuantosDiasLeDioElUltimoAntibiotico(null);
                        }


                        vigilanciaIntegrada.setViaOral(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkViaOral)));
                        vigilanciaIntegrada.setViaParenteral(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkViaParenteral)));
                        vigilanciaIntegrada.setViaAmbas(AndroidUtils.resultadoGenericoChk(getActivity().findViewById(R.id.chkViaAmbas)));

                        vigilanciaIntegrada.setAntecedentesUsoAntivirales(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkUsoAntiViralSi), getActivity().findViewById(R.id.chkUsoAntiViralNo)));
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNombreAntiviral)).getText().toString())) {
                            vigilanciaIntegrada.setNombreAntiviral(((EditText) getActivity().findViewById(R.id.edtxtNombreAntiviral)).getText().toString());
                        } else {
                            vigilanciaIntegrada.setNombreAntiviral(null);
                        }

                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.dpFecha1raDosisAntiViral)).getText().toString())) {
                            vigilanciaIntegrada.setFecha1raDosis(((EditText) getActivity().findViewById(R.id.dpFecha1raDosisAntiViral)).getText().toString());
                        } else {
                            vigilanciaIntegrada.setFecha1raDosis(null);
                        }
                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.dpFechaUltDosisAntiViral)).getText().toString())) {
                            vigilanciaIntegrada.setFechaUltimaDosis(((EditText) getActivity().findViewById(R.id.dpFechaUltDosisAntiViral)).getText().toString());
                        } else {
                            vigilanciaIntegrada.setFechaUltimaDosis(null);
                        }

                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtNoDosisAdministrada)).getText().toString())) {
                            vigilanciaIntegrada.setNoDosisAdministrada(Short.valueOf(((EditText) getActivity().findViewById(R.id.edtxtNoDosisAdministrada)).getText().toString()));
                        } else {
                            vigilanciaIntegrada.setNoDosisAdministrada(null);
                        }

                        if (!StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtOtraManifestacionClinica)).getText().toString())) {
                            vigilanciaIntegrada.setCualManifestacionClinica(((EditText) getActivity().findViewById(R.id.edtxtOtraManifestacionClinica)).getText().toString());
                        } else {
                            vigilanciaIntegrada.setCualManifestacionClinica(null);
                        }
                        mVigilanciaIntegradaActivity.mGuardarVigilanciaIntegradaTask = (GuardarVigilanciaIntegradaTask) new
                                GuardarVigilanciaIntegradaTask(mVigilanciaIntegradaActivity).execute(vigilanciaIntegrada);
                        existeDetalle = true;
                    }
                }
            }
        }

        public void inicializarControles(View rootView) {
            // chkUrbano
            View.OnClickListener onClikUrbano = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkUrbano(view);
                }
            };
            rootView.findViewById(R.id.chkUrbano).setOnClickListener(onClikUrbano);

            // chkRural
            View.OnClickListener onClikRural = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkRural(view);
                }
            };
            rootView.findViewById(R.id.chkRural).setOnClickListener(onClikRural);

            // chkCaptacionEmergencia
            View.OnClickListener onClikEmergencia = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEmergencia(view);
                }
            };
            rootView.findViewById(R.id.chkCaptacionEmergencia).setOnClickListener(onClikEmergencia);

            // chkCaptacionSala
            View.OnClickListener onClikSala = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkSala(view);
                }
            };
            rootView.findViewById(R.id.chkCaptacionSala).setOnClickListener(onClikSala);

            // chkCaptacionUci
            View.OnClickListener onClikUci = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkUci(view);
                }
            };
            rootView.findViewById(R.id.chkCaptacionUci).setOnClickListener(onClikUci);

            // chkCaptacionAmbulatorio
            View.OnClickListener onClikAmbulatorio = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAmbulatorio(view);
                }
            };
            rootView.findViewById(R.id.chkCaptacionAmbulatorio).setOnClickListener(onClikAmbulatorio);

            // Presenta tarjeta de vacunacion Si
            View.OnClickListener onClikTarjetaVacunaSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkTarjetaVacunaSi(view);
                }
            };
            rootView.findViewById(R.id.chkTarjetaVacunaSi).setOnClickListener(onClikTarjetaVacunaSi);

            // Presenta tarjeta de vacunacion No
            View.OnClickListener onClikTarjetaVacunaNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkTarjetaVacunaNo(view);
                }
            };
            rootView.findViewById(R.id.chkTarjetaVacunaNo).setOnClickListener(onClikTarjetaVacunaNo);

            // Aplicada chkAntiHibSi
            View.OnClickListener chkAntiHibSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAntiHibSi(view);
                }
            };
            rootView.findViewById(R.id.chkAntiHibSi).setOnClickListener(chkAntiHibSi);

            // Aplicada chkAntiHibNo
            View.OnClickListener chkAntiHibNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAntiHibNo(view);
                }
            };
            rootView.findViewById(R.id.chkAntiHibNo).setOnClickListener(chkAntiHibNo);

            // Vacuna Pentavalente
            View.OnClickListener chkPentavalente = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkPentavalente(view);
                }
            };
            rootView.findViewById(R.id.chkPentavalente).setOnClickListener(chkPentavalente);

            // Aplicada Anti Meningococica Si
            View.OnClickListener chkAntiMeningococicaSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAntiMeningococicaSi(view);
                }
            };
            rootView.findViewById(R.id.chkAntiMenigSi).setOnClickListener(chkAntiMeningococicaSi);

            // Aplicada Anti Meningococica No
            View.OnClickListener chkAntiMeningococicaNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAntiMeningococicaNo(view);
                }
            };
            rootView.findViewById(R.id.chkAntiMenigNo).setOnClickListener(chkAntiMeningococicaNo);

            // Vacuna Conjugada
            View.OnClickListener chkConjugada = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkConjugada(view);
                }
            };
            rootView.findViewById(R.id.chkConjugada).setOnClickListener(chkConjugada);

            // Vacuna Polisacarida
            View.OnClickListener chkPolisacarida = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkPolisacarida(view);
                }
            };
            rootView.findViewById(R.id.chkPolisacarida).setOnClickListener(chkPolisacarida);

            // Aplicada Anti Neumomocica Si
            View.OnClickListener chkAntiNeumococicaSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAntiNeumococicaSi(view);
                }
            };
            rootView.findViewById(R.id.chkAntiNeumococicaSi).setOnClickListener(chkAntiNeumococicaSi);

            // Aplicada Anti Neumococica No
            View.OnClickListener chkAntiNeumococicaNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAntiNeumococicaNo(view);
                }
            };
            rootView.findViewById(R.id.chkAntiNeumococicaNo).setOnClickListener(chkAntiNeumococicaNo);

            // Vacuna Heptavalente
            View.OnClickListener chkHeptaValente = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkHeptavalente(view);
                }
            };
            rootView.findViewById(R.id.chkHeptavalente).setOnClickListener(chkHeptaValente);

            // Vacuna 23Polisacarida
            View.OnClickListener chk23Polisacarida = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChk23Polisacarida(view);
                }
            };
            rootView.findViewById(R.id.chk23Polisacarida).setOnClickListener(chk23Polisacarida);

            // Vacuna 13Valente
            View.OnClickListener chk13Valente = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChk13Valente(view);
                }
            };
            rootView.findViewById(R.id.chk13Valente).setOnClickListener(chk13Valente);

            // Aplicada Anti Influensa Si
            View.OnClickListener chkAntiInfluenzaSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAntiInfluenzaSi(view);
                }
            };
            rootView.findViewById(R.id.chkAntiInfluenzaSi).setOnClickListener(chkAntiInfluenzaSi);

            // Aplicada Anti Influenza No
            View.OnClickListener chkAntiInfluenzaNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAntiInfluenzaNo(view);
                }
            };
            rootView.findViewById(R.id.chkAntiInfluenzaNo).setOnClickListener(chkAntiInfluenzaNo);

            // Vacuna Estacional
            View.OnClickListener chkEstacional = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEstacional(view);
                }
            };
            rootView.findViewById(R.id.chkEstacional).setOnClickListener(chkEstacional);

            // Vacuna H1N1P
            View.OnClickListener chkH1n1p = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkH1n1p(view);
                }
            };
            rootView.findViewById(R.id.chkH1N1p).setOnClickListener(chkH1n1p);

            // Vacuna Influenza Otra
            View.OnClickListener chkOtraInfluenza = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkInfluenzaOtra(view);
                }
            };
            rootView.findViewById(R.id.chkOtraVacunaAntiInfluenza).setOnClickListener(chkOtraInfluenza);

            // Fecha ultima dosis Anti Hib
            rootView.findViewById(R.id.dpFechaUltimaDosisAntiHib).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialogFechaUltimaDosisAntiHib(view);
                }
            });

            // Fecha ultima dosis Anti Meningcocica
            rootView.findViewById(R.id.dpFechaUltimaDosisAntiMening).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialogFechaUltimaDosisAntiMening(view);
                }
            });

            // Fecha ultima dodis Anti Neumococica
            rootView.findViewById(R.id.dpFechaUltimaDosisAntiNeumococica).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialogFechaUltimaDosisAntiNeumococica(view);
                }
            });

            // Fecha ultima dodis Anti Influenza
            rootView.findViewById(R.id.dpFechaUltimaDosisAntiInfluenza).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialogFechaUltimaDosisAntiInfluenza(view);
                }
            });

            // -------------------------------Listado de Checkbox -----------------------
            // ChkCancer Si, No, Ns
            View.OnClickListener chkCancerSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkCancerSi(view);
                }
            };
            rootView.findViewById(R.id.chkCancerSi).setOnClickListener(chkCancerSi);

            View.OnClickListener chkCancerNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkCancerNo(view);
                }
            };
            rootView.findViewById(R.id.chkCancerNo).setOnClickListener(chkCancerNo);

            View.OnClickListener chkCancerNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkCancerNs(view);
                }
            };
            rootView.findViewById(R.id.chkCancerNs).setOnClickListener(chkCancerNs);

            // ChkDiabetes Si, No, Ns
            View.OnClickListener chkDiabetesSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkDiabetesSi(view);
                }
            };
            rootView.findViewById(R.id.chkDiabetesSi).setOnClickListener(chkDiabetesSi);

            View.OnClickListener chkDiabetesNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkDiabetesNo(view);
                }
            };
            rootView.findViewById(R.id.chkDiabetesNo).setOnClickListener(chkDiabetesNo);

            View.OnClickListener chkDiabetesNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkDiabetesNs(view);
                }
            };
            rootView.findViewById(R.id.chkDiabetesNs).setOnClickListener(chkDiabetesNs);

            // ChkVIH Si, No, Ns
            View.OnClickListener chkVihSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkVihSi(view);
                }
            };
            rootView.findViewById(R.id.chkVihSi).setOnClickListener(chkVihSi);

            View.OnClickListener chkVihNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkVihNo(view);
                }
            };
            rootView.findViewById(R.id.chkVihNo).setOnClickListener(chkVihNo);

            View.OnClickListener chkVihNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkVihNs(view);
                }
            };
            rootView.findViewById(R.id.chkVihNs).setOnClickListener(chkVihNs);

            // ChkOtraInmunudeficiencia Si, No, Ns
            View.OnClickListener chkOtraInmunodeficienciaSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkOtraInmunodeficienciaSi(view);
                }
            };
            rootView.findViewById(R.id.chkOtraInmunodeficienciaSi).setOnClickListener(chkOtraInmunodeficienciaSi);

            View.OnClickListener chkOtraInmunodeficienciaNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkOtraInmunodeficienciaNo(view);
                }
            };
            rootView.findViewById(R.id.chkOtraInmunodeficienciaNo).setOnClickListener(chkOtraInmunodeficienciaNo);

            View.OnClickListener chkOtraInmunodeficienciaNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkOtraInmunodeficienciaNs(view);
                }
            };
            rootView.findViewById(R.id.chkOtraInmunodeficienciaNs).setOnClickListener(chkOtraInmunodeficienciaNs);

            // ChkEnfNeurologicaCronica Si, No, Ns
            View.OnClickListener chkEnfNeurologicaCronicaSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEnfNeurologicaCronicaSi(view);
                }
            };
            rootView.findViewById(R.id.chkEnfNeurologicaCronicaSi).setOnClickListener(chkEnfNeurologicaCronicaSi);

            View.OnClickListener chkEnfNeurologicaCronicaNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEnfNeurologicaCronicaNo(view);
                }
            };
            rootView.findViewById(R.id.chkEnfNeurologicaCronicaNo).setOnClickListener(chkEnfNeurologicaCronicaNo);

            View.OnClickListener chkEnfNeurologicaCronicaNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEnfNeurologicaCronicaNs(view);
                }
            };
            rootView.findViewById(R.id.chkEnfNeurologicaCronicaNs).setOnClickListener(chkEnfNeurologicaCronicaNs);

            // ChkEnfermedadCardiaca Si, No, Ns
            View.OnClickListener chkEnfermedadCardiacaSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEnfermedadCardiacaSi(view);
                }
            };
            rootView.findViewById(R.id.chkEnfermedadCardiacaSi).setOnClickListener(chkEnfermedadCardiacaSi);

            View.OnClickListener chkEnfermedadCardiacaNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEnfermedadCardiacaNo(view);
                }
            };
            rootView.findViewById(R.id.chkEnfermedadCardiacaNo).setOnClickListener(chkEnfermedadCardiacaNo);

            View.OnClickListener chkEnfermedadCardiacaNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEnfermedadCardiacaNs(view);
                }
            };
            rootView.findViewById(R.id.chkEnfermedadCardiacaNs).setOnClickListener(chkEnfermedadCardiacaNs);

            // ChkAsma Si, No, Ns
            View.OnClickListener chkAsmaSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAsmaSi(view);
                }
            };
            rootView.findViewById(R.id.chkAsmaSi).setOnClickListener(chkAsmaSi);

            View.OnClickListener chkAsmaNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAsmaNo(view);
                }
            };
            rootView.findViewById(R.id.chkAsmaNo).setOnClickListener(chkAsmaNo);

            View.OnClickListener chkAsmaNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkAsmaNs(view);
                }
            };
            rootView.findViewById(R.id.chkAsmaNs).setOnClickListener(chkAsmaNs);

            // ChkEpoc Si, No, Ns
            View.OnClickListener chkEpocSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEpocSi(view);
                }
            };
            rootView.findViewById(R.id.chkEpocSi).setOnClickListener(chkEpocSi);

            View.OnClickListener chkEpocNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEpocNo(view);
                }
            };
            rootView.findViewById(R.id.chkEpocNo).setOnClickListener(chkEpocNo);

            View.OnClickListener chkEpocNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEpocNs(view);
                }
            };
            rootView.findViewById(R.id.chkEpocNs).setOnClickListener(chkEpocNs);

            // ChkOtraEnfPulmonar Si, No, Ns
            View.OnClickListener chkOtraEnfPulmonarSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkOtraEnfPulmonarSi(view);
                }
            };
            rootView.findViewById(R.id.chkOtraEnfPulmonarSi).setOnClickListener(chkOtraEnfPulmonarSi);

            View.OnClickListener chkOtraEnfPulmonarNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkOtraEnfPulmonarNo(view);
                }
            };
            rootView.findViewById(R.id.chkOtraEnfPulmonarNo).setOnClickListener(chkOtraEnfPulmonarNo);

            View.OnClickListener chkOtraEnfPulmonarNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkOtraEnfPulmonarNs(view);
                }
            };
            rootView.findViewById(R.id.chkOtraEnfPulmonarNs).setOnClickListener(chkOtraEnfPulmonarNs);

            // ChkInuficienciaRenal Si, No, Ns
            View.OnClickListener chkInsufRenalCronicaSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkInsufRenalCronicaSi(view);
                }
            };
            rootView.findViewById(R.id.chkInsufRenalCronicaSi).setOnClickListener(chkInsufRenalCronicaSi);

            View.OnClickListener chkInsufRenalCronicaNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkInsufRenalCronicaNo(view);
                }
            };
            rootView.findViewById(R.id.chkInsufRenalCronicaNo).setOnClickListener(chkInsufRenalCronicaNo);

            View.OnClickListener chkInsufRenalCronicaNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkInsufRenalCronicaNs(view);
                }
            };
            rootView.findViewById(R.id.chkInsufRenalCronicaNs).setOnClickListener(chkInsufRenalCronicaNs);


            // ChkDesnutricion Si, No, Ns
            View.OnClickListener chkDesnutricionSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkDesnutricionSi(view);
                }
            };
            rootView.findViewById(R.id.chkDesnutricionSi).setOnClickListener(chkDesnutricionSi);

            View.OnClickListener chkDesnutricionNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkDesnutricionNo(view);
                }
            };
            rootView.findViewById(R.id.chkDesnutricionNo).setOnClickListener(chkDesnutricionNo);

            View.OnClickListener chkDesnutricionNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkDesnutricionNs(view);
                }
            };
            rootView.findViewById(R.id.chkDesnutricionNs).setOnClickListener(chkDesnutricionNs);

            // ChkObesidad Si, No, Ns
            View.OnClickListener chkObesidadSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkObesidadSi(view);
                }
            };
            rootView.findViewById(R.id.chkObesidadSi).setOnClickListener(chkObesidadSi);

            View.OnClickListener chkObesidadNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkObesidadNo(view);
                }
            };
            rootView.findViewById(R.id.chkObesidadNo).setOnClickListener(chkObesidadNo);

            View.OnClickListener chkObesidadNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkObesidadNs(view);
                }
            };
            rootView.findViewById(R.id.chkObesidadNs).setOnClickListener(chkObesidadNs);

            // ChkEmbarazo Si, No, Ns
            View.OnClickListener chkEmbarzoSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEmbarazoSi(view);
                }
            };
            rootView.findViewById(R.id.chkEmbarazoSi).setOnClickListener(chkEmbarzoSi);

            View.OnClickListener chkEmbarzoNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEmbarazoNo(view);
                }
            };
            rootView.findViewById(R.id.chkEmbarazoNo).setOnClickListener(chkEmbarzoNo);

            View.OnClickListener chkEmbarzoNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEmbarazoNs(view);
                }
            };
            rootView.findViewById(R.id.chkEmbarazoNs).setOnClickListener(chkEmbarzoNs);

            // ChkTxCorticosteroide Si, No, Ns
            View.OnClickListener chkTxCorticosteroideSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkTxCorticosteroideSi(view);
                }
            };
            rootView.findViewById(R.id.chkTxCorticosteroideSi).setOnClickListener(chkTxCorticosteroideSi);

            View.OnClickListener chkTxCorticosteroideNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkTxCorticosteroideNo(view);
                }
            };
            rootView.findViewById(R.id.chkTxCorticosteroideNo).setOnClickListener(chkTxCorticosteroideNo);

            View.OnClickListener chkTxCorticosteroideNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkTxCorticosteroideNs(view);
                }
            };
            rootView.findViewById(R.id.chkTxCorticosteroideNs).setOnClickListener(chkTxCorticosteroideNs);

            // ChkOtra Si, No, Ns
            View.OnClickListener chkOtraSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkOtraSi(view);
                }
            };
            rootView.findViewById(R.id.chkOtraSi).setOnClickListener(chkOtraSi);

            View.OnClickListener chkOtraNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkOtraNo(view);
                }
            };
            rootView.findViewById(R.id.chkOtraNo).setOnClickListener(chkOtraNo);

            View.OnClickListener chkOtraNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkOtraNs(view);
                }
            };
            rootView.findViewById(R.id.chkOtraNs).setOnClickListener(chkOtraNs);

            // ChkEstornudos Si, No, Ns
            View.OnClickListener chkEstornudosSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEstornudosSi(view);
                }
            };
            rootView.findViewById(R.id.chkEstornudosSi).setOnClickListener(chkEstornudosSi);

            View.OnClickListener chkEstornudosNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEstornudosNo(view);
                }
            };
            rootView.findViewById(R.id.chkEstornudosNo).setOnClickListener(chkEstornudosNo);

            View.OnClickListener chkEstornudosNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkEstornudosNs(view);
                }
            };
            rootView.findViewById(R.id.chkEstornudosNs).setOnClickListener(chkEstornudosNs);

            // ChkOtraManifestacionClinica Si, No, Ns
            View.OnClickListener chkOtraManifestacionClinicaSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOtraManifestacionClinicaSi(view);
                }
            };
            rootView.findViewById(R.id.chkOtraManifestacionClinicaSi).setOnClickListener(chkOtraManifestacionClinicaSi);

            View.OnClickListener chkOtraManifestacionClinicaNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOtraManifestacionClinicaNo(view);
                }
            };
            rootView.findViewById(R.id.chkOtraManifestacionClinicaNo).setOnClickListener(chkOtraManifestacionClinicaNo);

            View.OnClickListener chkOtraManifestacionClinicaNs = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOtraManifestacionClinicaNs(view);
                }
            };
            rootView.findViewById(R.id.chkOtraManifestacionClinicaNs).setOnClickListener(chkOtraManifestacionClinicaNs);


            /**********************************************************************/
            // Todo Marcado Si
            rootView.findViewById(R.id.txtvCondicionSi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    condicionMarcada(view, 0);
                }
            });

            // Todo Marcado No
            rootView.findViewById(R.id.txtvCondicionNo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    condicionMarcada(view, 1);
                }
            });

            // Todo Marcado Ns
            rootView.findViewById(R.id.txtvCondicionNS).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    condicionMarcada(view, 2);
                }
            });

            // Uso de Antibiotico

            // chkUsoAntibioticoSi
            View.OnClickListener chkUsoAntibioticoSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkUsoAntibioticoSi(view);
                }
            };
            rootView.findViewById(R.id.chkUsoAntibioticoSi).setOnClickListener(chkUsoAntibioticoSi);

            // chkUsoAntibioticoNo
            View.OnClickListener chkUsoAntibioticoNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkUsoAntibioticoNo(view);
                }
            };
            rootView.findViewById(R.id.chkUsoAntibioticoNo).setOnClickListener(chkUsoAntibioticoNo);

            // chkUsoAntibioticoNoSabe
            View.OnClickListener chkUsoAntibioticoNoSabe = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkUsoAntibioticoNoSabe(view);
                }
            };
            rootView.findViewById(R.id.chkUsoAntibioticoNoSabe).setOnClickListener(chkUsoAntibioticoNoSabe);

            // chkViaOral
            View.OnClickListener chkViaOral = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkViaOral(view);
                }
            };
            rootView.findViewById(R.id.chkViaOral).setOnClickListener(chkViaOral);

            // chkViaParenteral
            View.OnClickListener chkViaParenteral = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkViaParenteral(view);
                }
            };
            rootView.findViewById(R.id.chkViaParenteral).setOnClickListener(chkViaParenteral);

            // chkViaOral
            View.OnClickListener chkViaAmbas = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkViaAmbas(view);
                }
            };
            rootView.findViewById(R.id.chkViaAmbas).setOnClickListener(chkViaAmbas);

            // Antecedentes uso de Antivirales

            // chkUsoAntiViralSi
            View.OnClickListener chkUsoAntiViralSi = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkUsoAntiViralSi(view);
                }
            };
            rootView.findViewById(R.id.chkUsoAntiViralSi).setOnClickListener(chkUsoAntiViralSi);

            // chkUsoAntiViralNo
            View.OnClickListener chkUsoAntiViralNo = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChkUsoAntiViralNo(view);
                }
            };
            rootView.findViewById(R.id.chkUsoAntiViralNo).setOnClickListener(chkUsoAntiViralNo);

            // Fecha 1ra Dosis Antiviral
            rootView.findViewById(R.id.dpFecha1raDosisAntiViral).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialogFecha1raDosisAntiViral(view);
                }
            });

            // Fecha ultima Dosis Antiviral
            rootView.findViewById(R.id.dpFechaUltDosisAntiViral).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialogFechaUltDosisAntiViral(view);
                }
            });

        }

        // Urbano
        public void onChkUrbano(View view) {
            boolean chkUrbano = ((CheckBox) getActivity().findViewById(R.id.chkUrbano)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkUrbano)).setChecked(chkUrbano);
            if (chkUrbano) {
                ((CheckBox) getActivity().findViewById(R.id.chkRural)).setChecked(!chkUrbano);
            }
        }

        // Rural
        public void onChkRural(View view) {
            boolean chkRural = ((CheckBox) getActivity().findViewById(R.id.chkRural)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkRural)).setChecked(chkRural);
            if (chkRural) {
                ((CheckBox) getActivity().findViewById(R.id.chkUrbano)).setChecked(!chkRural);
            }
        }

        // Emergencia
        public void onChkEmergencia(View view) {
            boolean chkEmergencia = ((CheckBox) getActivity().findViewById(R.id.chkCaptacionEmergencia)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkCaptacionEmergencia)).setChecked(chkEmergencia);
            if (chkEmergencia) {
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionSala)).setChecked(!chkEmergencia);
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionUci)).setChecked(!chkEmergencia);
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionAmbulatorio)).setChecked(!chkEmergencia);
            }
        }

        // Sala
        public void onChkSala(View view) {
            boolean chkSala = ((CheckBox) getActivity().findViewById(R.id.chkCaptacionSala)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkCaptacionSala)).setChecked(chkSala);
            if (chkSala) {
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionEmergencia)).setChecked(!chkSala);
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionUci)).setChecked(!chkSala);
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionAmbulatorio)).setChecked(!chkSala);
            }
        }

        // UCI
        public void onChkUci(View view) {
            boolean chkUci = ((CheckBox) getActivity().findViewById(R.id.chkCaptacionUci)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkCaptacionUci)).setChecked(chkUci);
            if (chkUci) {
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionEmergencia)).setChecked(!chkUci);
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionSala)).setChecked(!chkUci);
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionAmbulatorio)).setChecked(!chkUci);
            }
        }

        // Ambulatorio
        public void onChkAmbulatorio(View view) {
            boolean chkAmbulatorio = ((CheckBox) getActivity().findViewById(R.id.chkCaptacionAmbulatorio)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkCaptacionAmbulatorio)).setChecked(chkAmbulatorio);
            if (chkAmbulatorio) {
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionEmergencia)).setChecked(!chkAmbulatorio);
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionSala)).setChecked(!chkAmbulatorio);
                ((CheckBox) getActivity().findViewById(R.id.chkCaptacionUci)).setChecked(!chkAmbulatorio);
            }
        }

        // Tarjeta Vacuna SI
        public void onChkTarjetaVacunaSi(View view) {
            boolean chkTarjetaVacunaSi = ((CheckBox) getActivity().findViewById(R.id.chkTarjetaVacunaSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkTarjetaVacunaSi)).setChecked(chkTarjetaVacunaSi);
            if (chkTarjetaVacunaSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkTarjetaVacunaNo)).setChecked(!chkTarjetaVacunaSi);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiHibNoSabe)).setChecked(!chkTarjetaVacunaSi);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigNoSabe)).setChecked(!chkTarjetaVacunaSi);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaNoSabe)).setChecked(!chkTarjetaVacunaSi);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaNoSabe)).setChecked(!chkTarjetaVacunaSi);
                getActivity().findViewById(R.id.chkAntiHibSi).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiHibNo).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiMenigSi).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiMenigNo).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiNeumococicaSi).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiNeumococicaNo).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiInfluenzaSi).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiInfluenzaNo).setEnabled(true);
                getActivity().findViewById(R.id.chkPentavalente).setEnabled(true);
                getActivity().findViewById(R.id.chkConjugada).setEnabled(true);
                getActivity().findViewById(R.id.chkPolisacarida).setEnabled(true);
                getActivity().findViewById(R.id.chkHeptavalente).setEnabled(true);
                getActivity().findViewById(R.id.chk23Polisacarida).setEnabled(true);
                getActivity().findViewById(R.id.chk13Valente).setEnabled(true);
                getActivity().findViewById(R.id.chkEstacional).setEnabled(true);
                getActivity().findViewById(R.id.chkH1N1p).setEnabled(true);
                getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiHibNoSabe).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiMenigNoSabe).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiNeumococicaNoSabe).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiInfluenzaNoSabe).setEnabled(false);
                ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib)).setKeyListener(null);
                ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening)).setKeyListener(null);
                ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica)).setKeyListener(null);
                ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza)).setKeyListener(null);

            } else {
                getActivity().findViewById(R.id.chkAntiHibNoSabe).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiMenigNoSabe).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiNeumococicaNoSabe).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiInfluenzaNoSabe).setEnabled(true);
            }
        }

        // Tarjeta Vacuna NO
        public void onChkTarjetaVacunaNo(View view) {
            boolean chkTarjetaVacunaNo = ((CheckBox) getActivity().findViewById(R.id.chkTarjetaVacunaNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkTarjetaVacunaNo)).setChecked(chkTarjetaVacunaNo);
            if (chkTarjetaVacunaNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkTarjetaVacunaSi)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiHibNoSabe)).setChecked(chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigNoSabe)).setChecked(chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaNoSabe)).setChecked(chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaNoSabe)).setChecked(chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiHibSi)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiHibNo)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkPentavalente)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigSi)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigNo)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaSi)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaNo)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaSi)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaNo)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkConjugada)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkPolisacarida)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkHeptavalente)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chk23Polisacarida)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chk13Valente)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkEstacional)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkH1N1p)).setChecked(!chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza)).setChecked(!chkTarjetaVacunaNo);


                getActivity().findViewById(R.id.chkAntiHibSi).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiHibNo).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiMenigSi).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiMenigNo).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiNeumococicaSi).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiNeumococicaNo).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiInfluenzaSi).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiInfluenzaNo).setEnabled(false);
                getActivity().findViewById(R.id.chkPentavalente).setEnabled(false);
                getActivity().findViewById(R.id.chkConjugada).setEnabled(false);
                getActivity().findViewById(R.id.chkPolisacarida).setEnabled(false);
                getActivity().findViewById(R.id.chkHeptavalente).setEnabled(false);
                getActivity().findViewById(R.id.chk23Polisacarida).setEnabled(false);
                getActivity().findViewById(R.id.chk13Valente).setEnabled(false);
                getActivity().findViewById(R.id.chkEstacional).setEnabled(false);
                getActivity().findViewById(R.id.chkH1N1p).setEnabled(false);
                getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza).setEnabled(false);
                getActivity().findViewById(R.id.chkAntiHibNoSabe).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiMenigNoSabe).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiNeumococicaNoSabe).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiInfluenzaNoSabe).setEnabled(true);
                getActivity().findViewById(R.id.edtxtNoDosisPentavalente).setEnabled(false);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib).setEnabled(false);
                getActivity().findViewById(R.id.edtxtNoDosisAntiMening).setEnabled(false);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening).setEnabled(false);
                getActivity().findViewById(R.id.edtxtNoDosisAntiNeumococica).setEnabled(false);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica).setEnabled(false);
                getActivity().findViewById(R.id.edtxtNoDosisAntiInfluenza).setEnabled(false);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza).setEnabled(false);

                edtxtNoDosisPentavalente = (EditText) getActivity().findViewById(R.id.edtxtNoDosisPentavalente);
                edtxtNoDosisPentavalente.setText("");
                dpFechaUltimaDosisAntiHib = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib);
                dpFechaUltimaDosisAntiHib.setText("");
                edtxtNoDosisAntiMening = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiMening);
                edtxtNoDosisAntiMening.setText("");
                dpFechaUltimaDosisAntiMening = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening);
                dpFechaUltimaDosisAntiMening.setText("");
                edtxtNoDosisAntiNeumococica = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiNeumococica);
                edtxtNoDosisAntiNeumococica.setText("");
                dpFechaUltimaDosisAntiNeumococica = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica);
                dpFechaUltimaDosisAntiNeumococica.setText("");
                edtxtNoDosisAntiInfluenza = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiInfluenza);
                edtxtNoDosisAntiInfluenza.setText("");
                dpFechaUltimaDosisAntiInfluenza = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza);
                dpFechaUltimaDosisAntiInfluenza.setText("");

            } else {
                ((CheckBox) getActivity().findViewById(R.id.chkAntiHibNoSabe)).setChecked(chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigNoSabe)).setChecked(chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaNoSabe)).setChecked(chkTarjetaVacunaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaNoSabe)).setChecked(chkTarjetaVacunaNo);
                getActivity().findViewById(R.id.chkAntiHibSi).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiHibNo).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiMenigSi).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiMenigNo).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiNeumococicaSi).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiNeumococicaNo).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiInfluenzaSi).setEnabled(true);
                getActivity().findViewById(R.id.chkAntiInfluenzaNo).setEnabled(true);
                getActivity().findViewById(R.id.chkPentavalente).setEnabled(true);
                getActivity().findViewById(R.id.chkConjugada).setEnabled(true);
                getActivity().findViewById(R.id.chkPolisacarida).setEnabled(true);
                getActivity().findViewById(R.id.chkHeptavalente).setEnabled(true);
                getActivity().findViewById(R.id.chk23Polisacarida).setEnabled(true);
                getActivity().findViewById(R.id.chk13Valente).setEnabled(true);
                getActivity().findViewById(R.id.chkEstacional).setEnabled(true);
                getActivity().findViewById(R.id.chkH1N1p).setEnabled(true);
                getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza).setEnabled(true);
            }
        }

        // Aplicada AntiHib si
        public void onChkAntiHibSi(View view) {
            boolean chkAntiHibSi = ((CheckBox) getActivity().findViewById(R.id.chkAntiHibSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAntiHibSi)).setChecked(chkAntiHibSi);
            if (chkAntiHibSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkAntiHibNo)).setChecked(!chkAntiHibSi);
                getActivity().findViewById(R.id.chkPentavalente).setEnabled(true);
            }
        }

        // Aplicada AntiHib No
        public void onChkAntiHibNo(View view) {
            boolean chkAntiHibNo = ((CheckBox) getActivity().findViewById(R.id.chkAntiHibNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAntiHibNo)).setChecked(chkAntiHibNo);
            if (chkAntiHibNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkAntiHibSi)).setChecked(!chkAntiHibNo);
                getActivity().findViewById(R.id.chkPentavalente).setEnabled(false);
                ((CheckBox) getActivity().findViewById(R.id.chkPentavalente)).setChecked(!chkAntiHibNo);
                getActivity().findViewById(R.id.edtxtNoDosisPentavalente).setEnabled(false);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib).setEnabled(false);
                edtxtNoDosisPentavalente = (EditText) getActivity().findViewById(R.id.edtxtNoDosisPentavalente);
                edtxtNoDosisPentavalente.setText("");
                dpFechaUltimaDosisAntiHib = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib);
                dpFechaUltimaDosisAntiHib.setText("");
            }
        }

        // Pentavalente
        public void onChkPentavalente(View view) {
            boolean chkPentavalente = ((CheckBox) getActivity().findViewById(R.id.chkPentavalente)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkPentavalente)).setChecked(chkPentavalente);
            if (chkPentavalente) {
                getActivity().findViewById(R.id.edtxtNoDosisPentavalente).setEnabled(true);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib).setEnabled(true);
            } else {
                getActivity().findViewById(R.id.edtxtNoDosisPentavalente).setEnabled(false);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib).setEnabled(false);
                edtxtNoDosisPentavalente = (EditText) getActivity().findViewById(R.id.edtxtNoDosisPentavalente);
                edtxtNoDosisPentavalente.setText("");
                dpFechaUltimaDosisAntiHib = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib);
                dpFechaUltimaDosisAntiHib.setText("");
            }
        }

        // Aplicada ChkAntiMeningococica Si
        public void onChkAntiMeningococicaSi(View view) {
            boolean chkAntiMenigSi = ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigSi)).setChecked(chkAntiMenigSi);
            if (chkAntiMenigSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigNo)).setChecked(!chkAntiMenigSi);
                getActivity().findViewById(R.id.chkConjugada).setEnabled(true);
                getActivity().findViewById(R.id.chkPolisacarida).setEnabled(true);
            }
        }

        // Aplicada ChkAntiMeningococica No
        public void onChkAntiMeningococicaNo(View view) {
            boolean chkAntiMenigNo = ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigNo)).setChecked(chkAntiMenigNo);
            if (chkAntiMenigNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkAntiMenigSi)).setChecked(!chkAntiMenigNo);
                ((CheckBox) getActivity().findViewById(R.id.chkConjugada)).setChecked(!chkAntiMenigNo);
                ((CheckBox) getActivity().findViewById(R.id.chkPolisacarida)).setChecked(!chkAntiMenigNo);
                getActivity().findViewById(R.id.chkConjugada).setEnabled(false);
                getActivity().findViewById(R.id.chkPolisacarida).setEnabled(false);
                edtxtNoDosisAntiMening = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiMening);
                edtxtNoDosisAntiMening.setEnabled(false);
                edtxtNoDosisAntiMening.setText("");
                dpFechaUltimaDosisAntiMening = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening);
                dpFechaUltimaDosisAntiMening.setEnabled(false);
                dpFechaUltimaDosisAntiMening.setText("");
            } else {
                ((CheckBox) getActivity().findViewById(R.id.chkConjugada)).setChecked(chkAntiMenigNo);
                ((CheckBox) getActivity().findViewById(R.id.chkPolisacarida)).setChecked(chkAntiMenigNo);
            }
        }

        // Conjugada
        public void onChkConjugada(View view) {
            boolean chkConjugada = ((CheckBox) getActivity().findViewById(R.id.chkConjugada)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkConjugada)).setChecked(chkConjugada);
            if (chkConjugada) {
                ((CheckBox) getActivity().findViewById(R.id.chkPolisacarida)).setChecked(!chkConjugada);
                edtxtNoDosisAntiMening = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiMening);
                edtxtNoDosisAntiMening.setEnabled(true);
                dpFechaUltimaDosisAntiMening = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening);
                dpFechaUltimaDosisAntiMening.setEnabled(true);
            }
        }

        // Polisacarida
        public void onChkPolisacarida(View view) {
            boolean chkPolisacarida = ((CheckBox) getActivity().findViewById(R.id.chkPolisacarida)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkPolisacarida)).setChecked(chkPolisacarida);
            if (chkPolisacarida) {
                ((CheckBox) getActivity().findViewById(R.id.chkConjugada)).setChecked(!chkPolisacarida);
                edtxtNoDosisAntiMening = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiMening);
                edtxtNoDosisAntiMening.setEnabled(true);
                dpFechaUltimaDosisAntiMening = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening);
                dpFechaUltimaDosisAntiMening.setEnabled(true);
            }
        }

        // Aplicada Anti Neumococica Si
        public void onChkAntiNeumococicaSi(View view) {
            boolean chkAntiNeumococicaSi = ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaSi)).setChecked(chkAntiNeumococicaSi);
            if (chkAntiNeumococicaSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaNo)).setChecked(!chkAntiNeumococicaSi);
                getActivity().findViewById(R.id.chkHeptavalente).setEnabled(true);
                getActivity().findViewById(R.id.chk23Polisacarida).setEnabled(true);
                getActivity().findViewById(R.id.chk13Valente).setEnabled(true);
            }
        }

        // Aplicada Anti neumocicica No
        public void onChkAntiNeumococicaNo(View view) {
            boolean chkAntiNeumococicaNo = ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaNo)).setChecked(chkAntiNeumococicaNo);
            if (chkAntiNeumococicaNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaSi)).setChecked(!chkAntiNeumococicaNo);
                getActivity().findViewById(R.id.chkHeptavalente).setEnabled(false);
                getActivity().findViewById(R.id.chk23Polisacarida).setEnabled(false);
                getActivity().findViewById(R.id.chk13Valente).setEnabled(false);
                ((CheckBox) getActivity().findViewById(R.id.chkHeptavalente)).setChecked(!chkAntiNeumococicaNo);
                ((CheckBox) getActivity().findViewById(R.id.chk23Polisacarida)).setChecked(!chkAntiNeumococicaNo);
                ((CheckBox) getActivity().findViewById(R.id.chk13Valente)).setChecked(!chkAntiNeumococicaNo);
                getActivity().findViewById(R.id.edtxtNoDosisAntiNeumococica).setEnabled(false);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica).setEnabled(false);
                edtxtNoDosisAntiNeumococica = ((EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiNeumococica));
                edtxtNoDosisAntiNeumococica.setText("");
                dpFechaUltimaDosisAntiNeumococica = ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica));
                dpFechaUltimaDosisAntiNeumococica.setText("");
            }
        }

        // Heptavalente
        public void onChkHeptavalente(View view) {
            boolean chkHeptavalente = ((CheckBox) getActivity().findViewById(R.id.chkHeptavalente)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkHeptavalente)).setChecked(chkHeptavalente);
            if (chkHeptavalente) {
                ((CheckBox) getActivity().findViewById(R.id.chk23Polisacarida)).setChecked(!chkHeptavalente);
                ((CheckBox) getActivity().findViewById(R.id.chk13Valente)).setChecked(!chkHeptavalente);
                getActivity().findViewById(R.id.edtxtNoDosisAntiNeumococica).setEnabled(true);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica).setEnabled(true);
            }
        }

        // 23Polisacarida
        public void onChk23Polisacarida(View view) {
            boolean chk23Polisacarida = ((CheckBox) getActivity().findViewById(R.id.chk23Polisacarida)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chk23Polisacarida)).setChecked(chk23Polisacarida);
            if (chk23Polisacarida) {
                ((CheckBox) getActivity().findViewById(R.id.chkHeptavalente)).setChecked(!chk23Polisacarida);
                ((CheckBox) getActivity().findViewById(R.id.chk13Valente)).setChecked(!chk23Polisacarida);
                getActivity().findViewById(R.id.edtxtNoDosisAntiNeumococica).setEnabled(true);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica).setEnabled(true);
            }
        }

        // 13Valente
        public void onChk13Valente(View view) {
            boolean chk13Valente = ((CheckBox) getActivity().findViewById(R.id.chk13Valente)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chk13Valente)).setChecked(chk13Valente);
            if (chk13Valente) {
                ((CheckBox) getActivity().findViewById(R.id.chkHeptavalente)).setChecked(!chk13Valente);
                ((CheckBox) getActivity().findViewById(R.id.chk23Polisacarida)).setChecked(!chk13Valente);
                getActivity().findViewById(R.id.edtxtNoDosisAntiNeumococica).setEnabled(true);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica).setEnabled(true);
            }
        }

        // Aplicada Anti Influenza Si
        public void onChkAntiInfluenzaSi(View view) {
            boolean chkAntiInfluenzaSi = ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaSi)).setChecked(chkAntiInfluenzaSi);
            if (chkAntiInfluenzaSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaNo)).setChecked(!chkAntiInfluenzaSi);
                getActivity().findViewById(R.id.chkEstacional).setEnabled(true);
                getActivity().findViewById(R.id.chkH1N1p).setEnabled(true);
                getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza).setEnabled(true);
            }
        }

        // Aplicada Anti Influenza No
        public void onChkAntiInfluenzaNo(View view) {
            boolean chkAntiInfluenzaNo = ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaNo)).setChecked(chkAntiInfluenzaNo);
            if (chkAntiInfluenzaNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaSi)).setChecked(!chkAntiInfluenzaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkEstacional)).setChecked(!chkAntiInfluenzaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkH1N1p)).setChecked(!chkAntiInfluenzaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza)).setChecked(!chkAntiInfluenzaNo);
                getActivity().findViewById(R.id.chkEstacional).setEnabled(false);
                getActivity().findViewById(R.id.chkH1N1p).setEnabled(false);
                getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza).setEnabled(false);
                getActivity().findViewById(R.id.edtxtNoDosisAntiInfluenza).setEnabled(false);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza).setEnabled(false);
                edtxtNoDosisAntiInfluenza = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiInfluenza);
                edtxtNoDosisAntiInfluenza.setText("");
                dpFechaUltimaDosisAntiInfluenza = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza);
                dpFechaUltimaDosisAntiInfluenza.setText("");
            }
        }

        // Estacional
        public void onChkEstacional(View view) {
            boolean chkEstacional = ((CheckBox) getActivity().findViewById(R.id.chkEstacional)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEstacional)).setChecked(chkEstacional);
            if (chkEstacional) {
                ((CheckBox) getActivity().findViewById(R.id.chkH1N1p)).setChecked(!chkEstacional);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza)).setChecked(!chkEstacional);
                getActivity().findViewById(R.id.edtxtNoDosisAntiInfluenza).setEnabled(true);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza).setEnabled(true);
            }
        }

        // H1N1p
        public void onChkH1n1p(View view) {
            boolean chkH1n1p = ((CheckBox) getActivity().findViewById(R.id.chkH1N1p)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkH1N1p)).setChecked(chkH1n1p);
            if (chkH1n1p) {
                ((CheckBox) getActivity().findViewById(R.id.chkEstacional)).setChecked(!chkH1n1p);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza)).setChecked(!chkH1n1p);
                getActivity().findViewById(R.id.edtxtNoDosisAntiInfluenza).setEnabled(true);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza).setEnabled(true);
            }
        }

        // Otra Vacuna Anti Influenza
        public void onChkInfluenzaOtra(View view) {
            boolean chkAntiInfluenzaOtra = ((CheckBox) getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza)).setChecked(chkAntiInfluenzaOtra);
            if (chkAntiInfluenzaOtra) {
                ((CheckBox) getActivity().findViewById(R.id.chkEstacional)).setChecked(!chkAntiInfluenzaOtra);
                ((CheckBox) getActivity().findViewById(R.id.chkH1N1p)).setChecked(!chkAntiInfluenzaOtra);
                getActivity().findViewById(R.id.edtxtNoDosisAntiInfluenza).setEnabled(true);
                getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza).setEnabled(true);
            }
        }

        // Fecha Ultima Dosis Anti Hib
        public void showDatePickerDialogFechaUltimaDosisAntiHib(View view) {
            DialogFragment newFragment = new DatePickerFragment() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    if (DateUtils.esMayorFechaHoy(calendar)) {
                        ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib)).setError(getString(R.string.msj_fecha_mayor_hoy));
                    } else {
                        if (((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib)).getError() != null) {
                            ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib)).setError(null);
                            ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                        }
                        ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                    }
                }
            };
            newFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.title_date_picker));
        }

        // Fecha Ultima Dosis Anti Meningcocica
        public void showDatePickerDialogFechaUltimaDosisAntiMening(View view) {
            DialogFragment newFragment = new DatePickerFragment() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    if (DateUtils.esMayorFechaHoy(calendar)) {
                        ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening)).setError(getString(R.string.msj_fecha_mayor_hoy));
                    } else {
                        if (((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening)).getError() != null) {
                            ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening)).setError(null);
                            ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                        }
                        ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                    }
                }
            };
            newFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.title_date_picker));
        }

        // Fecha Ultima Dosis Anti Neumococica
        public void showDatePickerDialogFechaUltimaDosisAntiNeumococica(View view) {
            DialogFragment newFragment = new DatePickerFragment() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    if (DateUtils.esMayorFechaHoy(calendar)) {
                        ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica)).setError(getString(R.string.msj_fecha_mayor_hoy));
                    } else {
                        if (((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica)).getError() != null) {
                            ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica)).setError(null);
                            ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                        }
                        ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                    }
                }
            };
            newFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.title_date_picker));
        }

        // Fecha Ultima Dosis Anti Neumococica
        public void showDatePickerDialogFechaUltimaDosisAntiInfluenza(View view) {
            DialogFragment newFragment = new DatePickerFragment() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    if (DateUtils.esMayorFechaHoy(calendar)) {
                        ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza)).setError(getString(R.string.msj_fecha_mayor_hoy));
                    } else {
                        if (((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza)).getError() != null) {
                            ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza)).setError(null);
                            ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                        }
                        ((EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                    }
                }
            };
            newFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.title_date_picker));
        }

        // ChkCancerSi
        public void onChkCancerSi(View view) {
            boolean chkCancerSi = ((CheckBox) getActivity().findViewById(R.id.chkCancerSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkCancerSi)).setChecked(chkCancerSi);
            if (chkCancerSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkCancerNo)).setChecked(!chkCancerSi);
                ((CheckBox) getActivity().findViewById(R.id.chkCancerNs)).setChecked(!chkCancerSi);
            }
        }

        // ChkCancerNo
        public void onChkCancerNo(View view) {
            boolean chkCancerNo = ((CheckBox) getActivity().findViewById(R.id.chkCancerNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkCancerNo)).setChecked(chkCancerNo);
            if (chkCancerNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkCancerSi)).setChecked(!chkCancerNo);
                ((CheckBox) getActivity().findViewById(R.id.chkCancerNs)).setChecked(!chkCancerNo);
            }
        }

        // ChkCancerNS
        public void onChkCancerNs(View view) {
            boolean chkCancerNs = ((CheckBox) getActivity().findViewById(R.id.chkCancerNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkCancerNs)).setChecked(chkCancerNs);
            if (chkCancerNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkCancerSi)).setChecked(!chkCancerNs);
                ((CheckBox) getActivity().findViewById(R.id.chkCancerNo)).setChecked(!chkCancerNs);
            }
        }

        // chkDiabetesSi
        public void onChkDiabetesSi(View view) {
            boolean chkDiabetesSi = ((CheckBox) getActivity().findViewById(R.id.chkDiabetesSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkDiabetesSi)).setChecked(chkDiabetesSi);
            if (chkDiabetesSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkDiabetesNo)).setChecked(!chkDiabetesSi);
                ((CheckBox) getActivity().findViewById(R.id.chkDiabetesNs)).setChecked(!chkDiabetesSi);
            }
        }

        // chkDiabetesNo
        public void onChkDiabetesNo(View view) {
            boolean chkDiabetesNo = ((CheckBox) getActivity().findViewById(R.id.chkDiabetesNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkDiabetesNo)).setChecked(chkDiabetesNo);
            if (chkDiabetesNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkDiabetesSi)).setChecked(!chkDiabetesNo);
                ((CheckBox) getActivity().findViewById(R.id.chkDiabetesNs)).setChecked(!chkDiabetesNo);
            }
        }

        // chkDiabetesNs
        public void onChkDiabetesNs(View view) {
            boolean chkDiabetesNs = ((CheckBox) getActivity().findViewById(R.id.chkDiabetesNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkDiabetesNs)).setChecked(chkDiabetesNs);
            if (chkDiabetesNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkDiabetesSi)).setChecked(!chkDiabetesNs);
                ((CheckBox) getActivity().findViewById(R.id.chkDiabetesNo)).setChecked(!chkDiabetesNs);
            }
        }


        // chkVihSi
        public void onChkVihSi(View view) {
            boolean chkVihSi = ((CheckBox) getActivity().findViewById(R.id.chkVihSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkVihSi)).setChecked(chkVihSi);
            if (chkVihSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkVihNo)).setChecked(!chkVihSi);
                ((CheckBox) getActivity().findViewById(R.id.chkVihNs)).setChecked(!chkVihSi);
            }
        }

        // chkVihNo
        public void onChkVihNo(View view) {
            boolean chkVihNo = ((CheckBox) getActivity().findViewById(R.id.chkVihNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkVihNo)).setChecked(chkVihNo);
            if (chkVihNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkVihSi)).setChecked(!chkVihNo);
                ((CheckBox) getActivity().findViewById(R.id.chkVihNs)).setChecked(!chkVihNo);
            }
        }

        // chkVihNs
        public void onChkVihNs(View view) {
            boolean chkVihNs = ((CheckBox) getActivity().findViewById(R.id.chkVihNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkVihNs)).setChecked(chkVihNs);
            if (chkVihNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkVihSi)).setChecked(!chkVihNs);
                ((CheckBox) getActivity().findViewById(R.id.chkVihNo)).setChecked(!chkVihNs);
            }
        }

        // chkOtraInmunodeficienciaSi
        public void onChkOtraInmunodeficienciaSi(View view) {
            boolean chkOtraInmunodeficienciaSi = ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaSi)).setChecked(chkOtraInmunodeficienciaSi);
            if (chkOtraInmunodeficienciaSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaNo)).setChecked(!chkOtraInmunodeficienciaSi);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaNs)).setChecked(!chkOtraInmunodeficienciaSi);
            }
        }

        // chkOtraInmunodeficienciaNo
        public void onChkOtraInmunodeficienciaNo(View view) {
            boolean chkOtraInmunodeficienciaNo = ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaNo)).setChecked(chkOtraInmunodeficienciaNo);
            if (chkOtraInmunodeficienciaNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaSi)).setChecked(!chkOtraInmunodeficienciaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaNs)).setChecked(!chkOtraInmunodeficienciaNo);
            }
        }

        // chkOtraInmunodeficienciaNs
        public void onChkOtraInmunodeficienciaNs(View view) {
            boolean chkOtraInmunodeficienciaNs = ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaNs)).setChecked(chkOtraInmunodeficienciaNs);
            if (chkOtraInmunodeficienciaNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaNo)).setChecked(!chkOtraInmunodeficienciaNs);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaSi)).setChecked(!chkOtraInmunodeficienciaNs);
            }
        }

        // chkEnfNeurologicaCronicaSi
        public void onChkEnfNeurologicaCronicaSi(View view) {
            boolean chkEnfNeurologicaCronicaSi = ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaSi)).setChecked(chkEnfNeurologicaCronicaSi);
            if (chkEnfNeurologicaCronicaSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNo)).setChecked(!chkEnfNeurologicaCronicaSi);
                ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNs)).setChecked(!chkEnfNeurologicaCronicaSi);
            }
        }

        // chkEnfNeurologicaCronicaNo
        public void onChkEnfNeurologicaCronicaNo(View view) {
            boolean chkEnfNeurologicaCronicaNo = ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNo)).setChecked(chkEnfNeurologicaCronicaNo);
            if (chkEnfNeurologicaCronicaNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaSi)).setChecked(!chkEnfNeurologicaCronicaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNs)).setChecked(!chkEnfNeurologicaCronicaNo);
            }
        }

        // chkEnfNeurologicaCronicaNs
        public void onChkEnfNeurologicaCronicaNs(View view) {
            boolean chkEnfNeurologicaCronicaNs = ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNs)).setChecked(chkEnfNeurologicaCronicaNs);
            if (chkEnfNeurologicaCronicaNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNo)).setChecked(!chkEnfNeurologicaCronicaNs);
                ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaSi)).setChecked(!chkEnfNeurologicaCronicaNs);
            }
        }

        // chkEnfermedadCardiacaSi
        public void onChkEnfermedadCardiacaSi(View view) {
            boolean chkEnfermedadCardiacaSi = ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaSi)).setChecked(chkEnfermedadCardiacaSi);
            if (chkEnfermedadCardiacaSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaNo)).setChecked(!chkEnfermedadCardiacaSi);
                ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaNs)).setChecked(!chkEnfermedadCardiacaSi);
            }
        }

        // chkEnfermedadCardiacaNo
        public void onChkEnfermedadCardiacaNo(View view) {
            boolean chkEnfermedadCardiacaNo = ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaNo)).setChecked(chkEnfermedadCardiacaNo);
            if (chkEnfermedadCardiacaNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaSi)).setChecked(!chkEnfermedadCardiacaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaNs)).setChecked(!chkEnfermedadCardiacaNo);
            }
        }

        // chkEnfermedadCardiacaNs
        public void onChkEnfermedadCardiacaNs(View view) {
            boolean chkEnfermedadCardiacaNs = ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaNs)).setChecked(chkEnfermedadCardiacaNs);
            if (chkEnfermedadCardiacaNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaNo)).setChecked(!chkEnfermedadCardiacaNs);
                ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaSi)).setChecked(!chkEnfermedadCardiacaNs);
            }
        }

        // chkAsmaSi
        public void onChkAsmaSi(View view) {
            boolean chkAsmaSi = ((CheckBox) getActivity().findViewById(R.id.chkAsmaSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAsmaSi)).setChecked(chkAsmaSi);
            if (chkAsmaSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkAsmaNo)).setChecked(!chkAsmaSi);
                ((CheckBox) getActivity().findViewById(R.id.chkAsmaNs)).setChecked(!chkAsmaSi);
            }
        }

        // chkAsmaNo
        public void onChkAsmaNo(View view) {
            boolean chkAsmaNo = ((CheckBox) getActivity().findViewById(R.id.chkAsmaNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAsmaNo)).setChecked(chkAsmaNo);
            if (chkAsmaNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkAsmaSi)).setChecked(!chkAsmaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkAsmaNs)).setChecked(!chkAsmaNo);
            }
        }

        // chkAsmaNs
        public void onChkAsmaNs(View view) {
            boolean chkAsmaNs = ((CheckBox) getActivity().findViewById(R.id.chkAsmaNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkAsmaNs)).setChecked(chkAsmaNs);
            if (chkAsmaNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkAsmaNo)).setChecked(!chkAsmaNs);
                ((CheckBox) getActivity().findViewById(R.id.chkAsmaSi)).setChecked(!chkAsmaNs);
            }
        }

        // chkEpocSi
        public void onChkEpocSi(View view) {
            boolean chkEpocSi = ((CheckBox) getActivity().findViewById(R.id.chkEpocSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEpocSi)).setChecked(chkEpocSi);
            if (chkEpocSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkEpocNo)).setChecked(!chkEpocSi);
                ((CheckBox) getActivity().findViewById(R.id.chkEpocNs)).setChecked(!chkEpocSi);
            }
        }

        // chkEpocNo
        public void onChkEpocNo(View view) {
            boolean chkEpocNo = ((CheckBox) getActivity().findViewById(R.id.chkEpocNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEpocNo)).setChecked(chkEpocNo);
            if (chkEpocNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkEpocSi)).setChecked(!chkEpocNo);
                ((CheckBox) getActivity().findViewById(R.id.chkEpocNs)).setChecked(!chkEpocNo);
            }
        }

        // chkEpocNs
        public void onChkEpocNs(View view) {
            boolean chkEpocNs = ((CheckBox) getActivity().findViewById(R.id.chkEpocNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEpocNs)).setChecked(chkEpocNs);
            if (chkEpocNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkEpocNo)).setChecked(!chkEpocNs);
                ((CheckBox) getActivity().findViewById(R.id.chkEpocSi)).setChecked(!chkEpocNs);
            }
        }

        // chkOtraEnfPulmonarSi
        public void onChkOtraEnfPulmonarSi(View view) {
            boolean chkOtraEnfPulmonarSi = ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarSi)).setChecked(chkOtraEnfPulmonarSi);
            if (chkOtraEnfPulmonarSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarNo)).setChecked(!chkOtraEnfPulmonarSi);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarNs)).setChecked(!chkOtraEnfPulmonarSi);
            }
        }

        // chkOtraEnfPulmonarNo
        public void onChkOtraEnfPulmonarNo(View view) {
            boolean chkOtraEnfPulmonarNo = ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarNo)).setChecked(chkOtraEnfPulmonarNo);
            if (chkOtraEnfPulmonarNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarSi)).setChecked(!chkOtraEnfPulmonarNo);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarNs)).setChecked(!chkOtraEnfPulmonarNo);
            }
        }

        // chkOtraEnfPulmonarNs
        public void onChkOtraEnfPulmonarNs(View view) {
            boolean chkOtraEnfPulmonarNs = ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarNs)).setChecked(chkOtraEnfPulmonarNs);
            if (chkOtraEnfPulmonarNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarNo)).setChecked(!chkOtraEnfPulmonarNs);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarSi)).setChecked(!chkOtraEnfPulmonarNs);
            }
        }

        // chkInsufRenalCronicaSi
        public void onChkInsufRenalCronicaSi(View view) {
            boolean chkInsufRenalCronicaSi = ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaSi)).setChecked(chkInsufRenalCronicaSi);
            if (chkInsufRenalCronicaSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaNo)).setChecked(!chkInsufRenalCronicaSi);
                ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaNs)).setChecked(!chkInsufRenalCronicaSi);
            }
        }

        // chkInsufRenalCronicaSi
        public void onChkInsufRenalCronicaNo(View view) {
            boolean chkInsufRenalCronicaNo = ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaNo)).setChecked(chkInsufRenalCronicaNo);
            if (chkInsufRenalCronicaNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaSi)).setChecked(!chkInsufRenalCronicaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaNs)).setChecked(!chkInsufRenalCronicaNo);
            }
        }

        // chkInsufRenalCronicaSi
        public void onChkInsufRenalCronicaNs(View view) {
            boolean chkInsufRenalCronicaNs = ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaNs)).setChecked(chkInsufRenalCronicaNs);
            if (chkInsufRenalCronicaNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaNo)).setChecked(!chkInsufRenalCronicaNs);
                ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaSi)).setChecked(!chkInsufRenalCronicaNs);
            }
        }

        // chkDesnutricionSi
        public void onChkDesnutricionSi(View view) {
            boolean chkDesnutricionSi = ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionSi)).setChecked(chkDesnutricionSi);
            if (chkDesnutricionSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionNo)).setChecked(!chkDesnutricionSi);
                ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionNs)).setChecked(!chkDesnutricionSi);
            }
        }

        // chkDesnutricionNo
        public void onChkDesnutricionNo(View view) {
            boolean chkDesnutricionNo = ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionNo)).setChecked(chkDesnutricionNo);
            if (chkDesnutricionNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionSi)).setChecked(!chkDesnutricionNo);
                ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionNs)).setChecked(!chkDesnutricionNo);
            }
        }

        // chkDesnutricionNs
        public void onChkDesnutricionNs(View view) {
            boolean chkDesnutricionNs = ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionNs)).setChecked(chkDesnutricionNs);
            if (chkDesnutricionNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionNo)).setChecked(!chkDesnutricionNs);
                ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionSi)).setChecked(!chkDesnutricionNs);
            }
        }

        // chkObesidadSi
        public void onChkObesidadSi(View view) {
            boolean chkObesidadSi = ((CheckBox) getActivity().findViewById(R.id.chkObesidadSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkObesidadSi)).setChecked(chkObesidadSi);
            if (chkObesidadSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkObesidadNo)).setChecked(!chkObesidadSi);
                ((CheckBox) getActivity().findViewById(R.id.chkObesidadNs)).setChecked(!chkObesidadSi);
            }
        }

        // chkObesidadNo
        public void onChkObesidadNo(View view) {
            boolean chkObesidadNo = ((CheckBox) getActivity().findViewById(R.id.chkObesidadNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkObesidadNo)).setChecked(chkObesidadNo);
            if (chkObesidadNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkObesidadSi)).setChecked(!chkObesidadNo);
                ((CheckBox) getActivity().findViewById(R.id.chkObesidadNs)).setChecked(!chkObesidadNo);
            }
        }

        // chkObesidadNs
        public void onChkObesidadNs(View view) {
            boolean chkObesidadNs = ((CheckBox) getActivity().findViewById(R.id.chkObesidadNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkObesidadNs)).setChecked(chkObesidadNs);
            if (chkObesidadNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkObesidadNo)).setChecked(!chkObesidadNs);
                ((CheckBox) getActivity().findViewById(R.id.chkObesidadSi)).setChecked(!chkObesidadNs);
            }
        }

        // chkEmbarazoSi
        public void onChkEmbarazoSi(View view) {
            boolean chkEmbarazoSi = ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoSi)).setChecked(chkEmbarazoSi);
            if (chkEmbarazoSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoNo)).setChecked(!chkEmbarazoSi);
                ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoNs)).setChecked(!chkEmbarazoSi);
                getActivity().findViewById(R.id.edtxtSemanasEmbarazo).setEnabled(true);
            }
        }

        // chkEmbarazoNo
        public void onChkEmbarazoNo(View view) {
            boolean chkEmbarazoNo = ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoNo)).setChecked(chkEmbarazoNo);
            if (chkEmbarazoNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoSi)).setChecked(!chkEmbarazoNo);
                ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoNs)).setChecked(!chkEmbarazoNo);
                getActivity().findViewById(R.id.edtxtSemanasEmbarazo).setEnabled(false);
                edtxtSemanasEmbarazo = (EditText) getActivity().findViewById(R.id.edtxtSemanasEmbarazo);
                edtxtSemanasEmbarazo.setError(null);
                edtxtSemanasEmbarazo.setText("");
            }
        }

        // chkEmbarazoNs
        public void onChkEmbarazoNs(View view) {
            boolean chkEmbarazoNs = ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoNs)).setChecked(chkEmbarazoNs);
            if (chkEmbarazoNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoNo)).setChecked(!chkEmbarazoNs);
                ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoSi)).setChecked(!chkEmbarazoNs);
                getActivity().findViewById(R.id.edtxtSemanasEmbarazo).setEnabled(false);
                edtxtSemanasEmbarazo = (EditText) getActivity().findViewById(R.id.edtxtSemanasEmbarazo);
                edtxtSemanasEmbarazo.setError(null);
                edtxtSemanasEmbarazo.setText("");
            }
        }

        // chkTxCorticosteroideSi
        public void onChkTxCorticosteroideSi(View view) {
            boolean chkTxCorticosteroideSi = ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideSi)).setChecked(chkTxCorticosteroideSi);
            if (chkTxCorticosteroideSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideNo)).setChecked(!chkTxCorticosteroideSi);
                ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideNs)).setChecked(!chkTxCorticosteroideSi);
            }
        }

        // chkTxCorticosteroideNo
        public void onChkTxCorticosteroideNo(View view) {
            boolean chkTxCorticosteroideNo = ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideNo)).setChecked(chkTxCorticosteroideNo);
            if (chkTxCorticosteroideNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideSi)).setChecked(!chkTxCorticosteroideNo);
                ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideNs)).setChecked(!chkTxCorticosteroideNo);
            }
        }

        // chkTxCorticosteroideNs
        public void onChkTxCorticosteroideNs(View view) {
            boolean chkTxCorticosteroideNs = ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideNs)).setChecked(chkTxCorticosteroideNs);
            if (chkTxCorticosteroideNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideNo)).setChecked(!chkTxCorticosteroideNs);
                ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideSi)).setChecked(!chkTxCorticosteroideNs);
            }
        }

        // chkOtraSi
        public void onChkOtraSi(View view) {
            boolean chkOtraSi = ((CheckBox) getActivity().findViewById(R.id.chkOtraSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkOtraSi)).setChecked(chkOtraSi);
            if (chkOtraSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraNo)).setChecked(!chkOtraSi);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraNs)).setChecked(!chkOtraSi);
                getActivity().findViewById(R.id.edtxtOtra).setEnabled(true);
            }
        }

        // chkOtraNo
        public void onChkOtraNo(View view) {
            boolean chkOtraNo = ((CheckBox) getActivity().findViewById(R.id.chkOtraNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkOtraNo)).setChecked(chkOtraNo);
            if (chkOtraNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraSi)).setChecked(!chkOtraNo);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraNs)).setChecked(!chkOtraNo);
                getActivity().findViewById(R.id.edtxtOtra).setEnabled(false);
                edtxtOtra = (EditText) getActivity().findViewById(R.id.edtxtOtra);
                edtxtOtra.setError(null);
                edtxtOtra.setText("");
            }
        }

        // chkOtraNs
        public void onChkOtraNs(View view) {
            boolean chkOtraNs = ((CheckBox) getActivity().findViewById(R.id.chkOtraNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkOtraNs)).setChecked(chkOtraNs);
            if (chkOtraNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraNo)).setChecked(!chkOtraNs);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraSi)).setChecked(!chkOtraNs);
                getActivity().findViewById(R.id.edtxtOtra).setEnabled(false);
                edtxtOtra = (EditText) getActivity().findViewById(R.id.edtxtOtra);
                edtxtOtra.setError(null);
                edtxtOtra.setText("");
            }
        }

        // chkEstornudosSi
        public void onChkEstornudosSi(View view) {
            boolean chkEstornudosSi = ((CheckBox) getActivity().findViewById(R.id.chkEstornudosSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEstornudosSi)).setChecked(chkEstornudosSi);
            if (chkEstornudosSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkEstornudosNo)).setChecked(!chkEstornudosSi);
                ((CheckBox) getActivity().findViewById(R.id.chkEstornudosNs)).setChecked(!chkEstornudosSi);
            }
        }

        // chkEstornudosNo
        public void onChkEstornudosNo(View view) {
            boolean chkEstornudosNo = ((CheckBox) getActivity().findViewById(R.id.chkEstornudosNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEstornudosNo)).setChecked(chkEstornudosNo);
            if (chkEstornudosNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkEstornudosSi)).setChecked(!chkEstornudosNo);
                ((CheckBox) getActivity().findViewById(R.id.chkEstornudosNs)).setChecked(!chkEstornudosNo);
            }
        }

        // chkEstornudosNs
        public void onChkEstornudosNs(View view) {
            boolean chkEstornudosNs = ((CheckBox) getActivity().findViewById(R.id.chkEstornudosNs)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkEstornudosNs)).setChecked(chkEstornudosNs);
            if (chkEstornudosNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkEstornudosSi)).setChecked(!chkEstornudosNs);
                ((CheckBox) getActivity().findViewById(R.id.chkEstornudosNo)).setChecked(!chkEstornudosNs);
            }
        }

        // chkOtraManifestacionClinicaSi
        public void onOtraManifestacionClinicaSi(View view) {
            boolean chkOtraManifestacionClinicaSi = ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaSi)).isChecked();
            if (chkOtraManifestacionClinicaSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaNo)).setChecked(!chkOtraManifestacionClinicaSi);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaNs)).setChecked(!chkOtraManifestacionClinicaSi);
                getActivity().findViewById(R.id.edtxtOtraManifestacionClinica).setEnabled(true);
            }
        }

        // chkOtraManifestacionClinicaNo
        public void onOtraManifestacionClinicaNo(View view) {
            boolean chkOtraManifestacionClinicaNo = ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaNo)).isChecked();
            if (chkOtraManifestacionClinicaNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaSi)).setChecked(!chkOtraManifestacionClinicaNo);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaNs)).setChecked(!chkOtraManifestacionClinicaNo);
                getActivity().findViewById(R.id.edtxtOtraManifestacionClinica).setEnabled(false);
                edtxtOtraManifestacionClinica = (EditText) getActivity().findViewById(R.id.edtxtOtraManifestacionClinica);
                edtxtOtraManifestacionClinica.setError(null);
                edtxtOtraManifestacionClinica.setText("");
            }
        }

        // chkOtraManifestacionClinicaNs
        public void onOtraManifestacionClinicaNs(View view) {
            boolean chkOtraManifestacionClinicaNs = ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaNs)).isChecked();
            if (chkOtraManifestacionClinicaNs) {
                ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaSi)).setChecked(!chkOtraManifestacionClinicaNs);
                ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaNo)).setChecked(!chkOtraManifestacionClinicaNs);
                getActivity().findViewById(R.id.edtxtOtraManifestacionClinica).setEnabled(false);
                edtxtOtraManifestacionClinica = (EditText) getActivity().findViewById(R.id.edtxtOtraManifestacionClinica);
                edtxtOtraManifestacionClinica.setError(null);
                edtxtOtraManifestacionClinica.setText("");
            }
        }

        // Todo Marcado
        public void condicionMarcada(View view, final int valor) {
            try {
                DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (valor == 0) {
                                    condicionesPreexistentesSi(true);
                                    condicionesPreexistentesNo(false);
                                    condicionesPreexistentesNs(false);
                                    getActivity().findViewById(R.id.edtxtSemanasEmbarazo).setEnabled(true);
                                    getActivity().findViewById(R.id.edtxtOtra).setEnabled(true);
                                    getActivity().findViewById(R.id.edtxtOtraManifestacionClinica).setEnabled(true);
                                } else if (valor == 1) {
                                    condicionesPreexistentesSi(false);
                                    condicionesPreexistentesNo(true);
                                    condicionesPreexistentesNs(false);
                                    getActivity().findViewById(R.id.edtxtSemanasEmbarazo).setEnabled(false);
                                    getActivity().findViewById(R.id.edtxtOtra).setEnabled(false);
                                    edtxtSemanasEmbarazo = (EditText) getActivity().findViewById(R.id.edtxtSemanasEmbarazo);
                                    edtxtSemanasEmbarazo.setText("");
                                    edtxtOtra = (EditText) getActivity().findViewById(R.id.edtxtOtra);
                                    edtxtOtra.setText("");
                                    edtxtOtraManifestacionClinica = (EditText) getActivity().findViewById(R.id.edtxtOtraManifestacionClinica);
                                    edtxtOtraManifestacionClinica.setText("");
                                    getActivity().findViewById(R.id.edtxtOtraManifestacionClinica).setEnabled(false);
                                } else {
                                    condicionesPreexistentesSi(false);
                                    condicionesPreexistentesNo(false);
                                    condicionesPreexistentesNs(true);
                                    getActivity().findViewById(R.id.edtxtSemanasEmbarazo).setEnabled(false);
                                    getActivity().findViewById(R.id.edtxtOtra).setEnabled(false);
                                    edtxtSemanasEmbarazo = (EditText) getActivity().findViewById(R.id.edtxtSemanasEmbarazo);
                                    edtxtSemanasEmbarazo.setText("");
                                    edtxtOtra = (EditText) getActivity().findViewById(R.id.edtxtOtra);
                                    edtxtOtra.setText("");
                                    edtxtOtraManifestacionClinica = (EditText) getActivity().findViewById(R.id.edtxtOtraManifestacionClinica);
                                    edtxtOtraManifestacionClinica.setText("");
                                    getActivity().findViewById(R.id.edtxtOtraManifestacionClinica).setEnabled(false);
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }

                    }
                };
                if (valor == 0) {
                    MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                            String.format(getResources().getString(R.string.msg_change_yes_conditions), getResources().getString(R.string.msg_change_yes_conditions_2)), getResources().getString(
                                    R.string.title_estudio_sostenible),
                            preguntaDialogClickListener);
                }
                if (valor == 1) {
                    MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                            String.format(getResources().getString(R.string.msg_change_no_conditions), getResources().getString(R.string.msg_change_yes_conditions_2)), getResources().getString(
                                    R.string.title_estudio_sostenible),
                            preguntaDialogClickListener);
                }
                if (valor == 2) {
                    MensajesHelper.mostrarMensajeYesNo(CONTEXT,
                            String.format(getResources().getString(R.string.msg_change_ns_conditions), getResources().getString(R.string.msg_change_yes_conditions_2)), getResources().getString(
                                    R.string.title_estudio_sostenible),
                            preguntaDialogClickListener);
                }
            } catch (Exception e) {
                e.printStackTrace();
                MensajesHelper.mostrarMensajeError(CONTEXT, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
            }
        }

        public void condicionesPreexistentesSi(boolean value) {
            ((CheckBox) getActivity().findViewById(R.id.chkCancerSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkDiabetesSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkVihSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkAsmaSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEpocSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkObesidadSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEstornudosSi)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaSi)).setChecked(value);
        }

        public void condicionesPreexistentesNo(boolean value) {
            ((CheckBox) getActivity().findViewById(R.id.chkCancerNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkDiabetesNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkVihNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkAsmaNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEpocNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkObesidadNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEstornudosNo)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaNo)).setChecked(value);
        }

        public void condicionesPreexistentesNs(boolean value) {
            ((CheckBox) getActivity().findViewById(R.id.chkCancerNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkDiabetesNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkVihNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraInmunodeficienciaNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEnfermedadCardiacaNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkAsmaNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEpocNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraEnfPulmonarNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkInsufRenalCronicaNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkDesnutricionNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkObesidadNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEmbarazoNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkTxCorticosteroideNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkEstornudosNs)).setChecked(value);
            ((CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaNs)).setChecked(value);
        }

        // chkAntibioticoSi
        public void onChkUsoAntibioticoSi(View view) {
            boolean chkUsoAntibioticoSi = ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoSi)).setChecked(chkUsoAntibioticoSi);
            if (chkUsoAntibioticoSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoNo)).setChecked(!chkUsoAntibioticoSi);
                ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoNoSabe)).setChecked(!chkUsoAntibioticoSi);
                getActivity().findViewById(R.id.edtxtCuantosAntibioticos).setEnabled(true);
                getActivity().findViewById(R.id.edtxtCualesAntibioticosDio).setEnabled(true);
                getActivity().findViewById(R.id.edtxtDiasUltimoAntibiotico).setEnabled(true);
                getActivity().findViewById(R.id.chkViaOral).setEnabled(true);
                getActivity().findViewById(R.id.chkViaParenteral).setEnabled(true);
                getActivity().findViewById(R.id.chkViaAmbas).setEnabled(true);
            }
        }

        // chkAntibioticoNo
        public void onChkUsoAntibioticoNo(View view) {
            boolean chkUsoAntibioticoNo = ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoNo)).setChecked(chkUsoAntibioticoNo);
            if (chkUsoAntibioticoNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoSi)).setChecked(!chkUsoAntibioticoNo);
                ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoNoSabe)).setChecked(!chkUsoAntibioticoNo);
                ((CheckBox) getActivity().findViewById(R.id.chkViaOral)).setChecked(!chkUsoAntibioticoNo);
                ((CheckBox) getActivity().findViewById(R.id.chkViaParenteral)).setChecked(!chkUsoAntibioticoNo);
                ((CheckBox) getActivity().findViewById(R.id.chkViaAmbas)).setChecked(!chkUsoAntibioticoNo);
                getActivity().findViewById(R.id.edtxtCuantosAntibioticos).setEnabled(false);
                getActivity().findViewById(R.id.edtxtCualesAntibioticosDio).setEnabled(false);
                getActivity().findViewById(R.id.edtxtDiasUltimoAntibiotico).setEnabled(false);
                getActivity().findViewById(R.id.chkViaOral).setEnabled(false);
                getActivity().findViewById(R.id.chkViaParenteral).setEnabled(false);
                getActivity().findViewById(R.id.chkViaAmbas).setEnabled(false);
                edtxtCuantosAntibioticos = (EditText) getActivity().findViewById(R.id.edtxtCuantosAntibioticos);
                edtxtCuantosAntibioticos.setText("");
                edtxtCuantosAntibioticos.setError(null);
                edtxtCualesAntibioticosDio = (EditText) getActivity().findViewById(R.id.edtxtCualesAntibioticosDio);
                edtxtCualesAntibioticosDio.setText("");
                edtxtCualesAntibioticosDio.setError(null);
                edtxtDiasUltimoAntibiotico = (EditText) getActivity().findViewById(R.id.edtxtDiasUltimoAntibiotico);
                edtxtDiasUltimoAntibiotico.setText("");
                edtxtDiasUltimoAntibiotico.setError(null);
            }
        }

        // chkAntibioticoNoSabe
        public void onChkUsoAntibioticoNoSabe(View view) {
            boolean chkUsoAntibioticoNoSabe = ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoNoSabe)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoNoSabe)).setChecked(chkUsoAntibioticoNoSabe);
            if (chkUsoAntibioticoNoSabe) {
                ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoNo)).setChecked(!chkUsoAntibioticoNoSabe);
                ((CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoSi)).setChecked(!chkUsoAntibioticoNoSabe);
                ((CheckBox) getActivity().findViewById(R.id.chkViaOral)).setChecked(!chkUsoAntibioticoNoSabe);
                ((CheckBox) getActivity().findViewById(R.id.chkViaParenteral)).setChecked(!chkUsoAntibioticoNoSabe);
                ((CheckBox) getActivity().findViewById(R.id.chkViaAmbas)).setChecked(!chkUsoAntibioticoNoSabe);
                getActivity().findViewById(R.id.edtxtCuantosAntibioticos).setEnabled(false);
                getActivity().findViewById(R.id.edtxtCualesAntibioticosDio).setEnabled(false);
                getActivity().findViewById(R.id.edtxtDiasUltimoAntibiotico).setEnabled(false);
                getActivity().findViewById(R.id.chkViaOral).setEnabled(false);
                getActivity().findViewById(R.id.chkViaParenteral).setEnabled(false);
                getActivity().findViewById(R.id.chkViaAmbas).setEnabled(false);
                edtxtCuantosAntibioticos = (EditText) getActivity().findViewById(R.id.edtxtCuantosAntibioticos);
                edtxtCuantosAntibioticos.setText("");
                edtxtCuantosAntibioticos.setError(null);
                edtxtCualesAntibioticosDio = (EditText) getActivity().findViewById(R.id.edtxtCualesAntibioticosDio);
                edtxtCualesAntibioticosDio.setText("");
                edtxtCualesAntibioticosDio.setError(null);
                edtxtDiasUltimoAntibiotico = (EditText) getActivity().findViewById(R.id.edtxtDiasUltimoAntibiotico);
                edtxtDiasUltimoAntibiotico.setText("");
                edtxtDiasUltimoAntibiotico.setError(null);
            }
        }

        // chkViaOral
        public void onChkViaOral(View view) {
            boolean chkViaOral = ((CheckBox) getActivity().findViewById(R.id.chkViaOral)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkViaOral)).setChecked(chkViaOral);
            if (chkViaOral) {
                ((CheckBox) getActivity().findViewById(R.id.chkViaParenteral)).setChecked(!chkViaOral);
                ((CheckBox) getActivity().findViewById(R.id.chkViaAmbas)).setChecked(!chkViaOral);
            }
        }

        // chkViaOral
        public void onChkViaParenteral(View view) {
            boolean chkViaParenteral = ((CheckBox) getActivity().findViewById(R.id.chkViaParenteral)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkViaParenteral)).setChecked(chkViaParenteral);
            if (chkViaParenteral) {
                ((CheckBox) getActivity().findViewById(R.id.chkViaOral)).setChecked(!chkViaParenteral);
                ((CheckBox) getActivity().findViewById(R.id.chkViaAmbas)).setChecked(!chkViaParenteral);
            }
        }

        // chkViaOral
        public void onChkViaAmbas(View view) {
            boolean chkViaAmbas = ((CheckBox) getActivity().findViewById(R.id.chkViaAmbas)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkViaAmbas)).setChecked(chkViaAmbas);
            if (chkViaAmbas) {
                ((CheckBox) getActivity().findViewById(R.id.chkViaParenteral)).setChecked(!chkViaAmbas);
                ((CheckBox) getActivity().findViewById(R.id.chkViaOral)).setChecked(!chkViaAmbas);
            }
        }

        // chkUsoAntiViralSi
        public void onChkUsoAntiViralSi(View view) {
            boolean chkUsoAntiViralSi = ((CheckBox) getActivity().findViewById(R.id.chkUsoAntiViralSi)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkUsoAntiViralSi)).setChecked(chkUsoAntiViralSi);
            if (chkUsoAntiViralSi) {
                ((CheckBox) getActivity().findViewById(R.id.chkUsoAntiViralNo)).setChecked(!chkUsoAntiViralSi);
                getActivity().findViewById(R.id.edtxtNombreAntiviral).setEnabled(true);
                getActivity().findViewById(R.id.dpFecha1raDosisAntiViral).setEnabled(true);
                getActivity().findViewById(R.id.dpFechaUltDosisAntiViral).setEnabled(true);
                getActivity().findViewById(R.id.edtxtNoDosisAdministrada).setEnabled(true);
                ((EditText) getActivity().findViewById(R.id.dpFecha1raDosisAntiViral)).setKeyListener(null);
                ((EditText) getActivity().findViewById(R.id.dpFechaUltDosisAntiViral)).setKeyListener(null);
            }
        }

        // chkUsoAntiViralNo
        public void onChkUsoAntiViralNo(View view) {
            boolean chkUsoAntiViralNo = ((CheckBox) getActivity().findViewById(R.id.chkUsoAntiViralNo)).isChecked();
            ((CheckBox) getActivity().findViewById(R.id.chkUsoAntiViralNo)).setChecked(chkUsoAntiViralNo);
            if (chkUsoAntiViralNo) {
                ((CheckBox) getActivity().findViewById(R.id.chkUsoAntiViralSi)).setChecked(!chkUsoAntiViralNo);
                getActivity().findViewById(R.id.edtxtNombreAntiviral).setEnabled(false);
                getActivity().findViewById(R.id.dpFecha1raDosisAntiViral).setEnabled(false);
                getActivity().findViewById(R.id.dpFechaUltDosisAntiViral).setEnabled(false);
                getActivity().findViewById(R.id.edtxtNoDosisAdministrada).setEnabled(false);
                edtxtNombreAntiviral = (EditText) getActivity().findViewById(R.id.edtxtNombreAntiviral);
                edtxtNombreAntiviral.setText("");
                edtxtNombreAntiviral.setError(null);
                dpFecha1raDosisAntiViral = (EditText) getActivity().findViewById(R.id.dpFecha1raDosisAntiViral);
                dpFecha1raDosisAntiViral.setText("");
                dpFechaUltDosisAntiViral = (EditText) getActivity().findViewById(R.id.dpFechaUltDosisAntiViral);
                dpFechaUltDosisAntiViral.setText("");
                dpFechaUltDosisAntiViral.setError(null);
                edtxtNoDosisAdministrada = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAdministrada);
                edtxtNoDosisAdministrada.setText("");
                edtxtNoDosisAdministrada.setError(null);
            }
        }

        // Fecha 1ra Dosis Antiviral
        public void showDatePickerDialogFecha1raDosisAntiViral(View view) {
            DialogFragment newFragment = new DatePickerFragment() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    if (DateUtils.esMayorFechaHoy(calendar)) {
                        ((EditText) getActivity().findViewById(R.id.dpFecha1raDosisAntiViral)).setError(getString(R.string.msj_fecha_mayor_hoy));
                    } else {
                        if (((EditText) getActivity().findViewById(R.id.dpFecha1raDosisAntiViral)).getError() != null) {
                            ((EditText) getActivity().findViewById(R.id.dpFecha1raDosisAntiViral)).setError(null);
                            ((EditText) getActivity().findViewById(R.id.dpFecha1raDosisAntiViral)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                        }
                        ((EditText) getActivity().findViewById(R.id.dpFecha1raDosisAntiViral)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                    }
                }
            };
            newFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.title_date_picker));
        }

        // Fecha Ultima Dosis Antiviral
        public void showDatePickerDialogFechaUltDosisAntiViral(View view) {
            DialogFragment newFragment = new DatePickerFragment() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    if (DateUtils.esMayorFechaHoy(calendar)) {
                        ((EditText) getActivity().findViewById(R.id.dpFechaUltDosisAntiViral)).setError(getString(R.string.msj_fecha_mayor_hoy));
                    } else {
                        if (((EditText) getActivity().findViewById(R.id.dpFechaUltDosisAntiViral)).getError() != null) {
                            ((EditText) getActivity().findViewById(R.id.dpFechaUltDosisAntiViral)).setError(null);
                            ((EditText) getActivity().findViewById(R.id.dpFechaUltDosisAntiViral)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                        }
                        ((EditText) getActivity().findViewById(R.id.dpFechaUltDosisAntiViral)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                    }
                }
            };
            newFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.title_date_picker));
        }

        /* Verificar que todos los datos requeridos no vallan vacios, si existe un dato requerido
         vacio no se podra pasar a la siguiente pantalla */
        public boolean validarDatosPaciente() {
            //EditText tutor = (EditText) getActivity().findViewById(R.id.edtxtTutor);
            //EditText departamento = (EditText) getActivity().findViewById(R.id.edtxtDepartamento);
            //EditText municipio = (EditText) getActivity().findViewById(R.id.edtxtMunicipio);
            Spinner spnDepartamentos = (Spinner) getActivity().findViewById(R.id.spnDepartamentos);
            Spinner spnMunicipios = (Spinner) getActivity().findViewById(R.id.spnMunicipios);
            int depatamento = (int) spnDepartamentos.getSelectedItemId();
            int municipio = (int) spnMunicipios.getSelectedItemId();


            EditText barrio = (EditText) getActivity().findViewById(R.id.edtxtBarrio);
            EditText direccion = (EditText) getActivity().findViewById(R.id.edtxtDireccion);
            //EditText telefono = (EditText) getActivity().findViewById(R.id.edtxtTelefono);
            CheckBox chkUrbano = (CheckBox) getActivity().findViewById(R.id.chkUrbano);
            CheckBox chkRural = (CheckBox) getActivity().findViewById(R.id.chkRural);
            CheckBox chkCaptacionEmergencia = (CheckBox) getActivity().findViewById(R.id.chkCaptacionEmergencia);
            CheckBox chkCaptacionSala = (CheckBox) getActivity().findViewById(R.id.chkCaptacionSala);
            CheckBox chkCaptacionUci = (CheckBox) getActivity().findViewById(R.id.chkCaptacionUci);
            CheckBox chkCaptacionAmbulatorio = (CheckBox) getActivity().findViewById(R.id.chkCaptacionAmbulatorio);
            EditText diagnostico = (EditText) getActivity().findViewById(R.id.edtxtDiagnostico);

            EditText edtxtNombreApellido = (EditText) getActivity().findViewById(R.id.edtxtNombreApellido);
            EditText edtxtTutor = (EditText) getActivity().findViewById(R.id.edtxtTutor);

            focusView = null;
            //tutor.setError(null);
            //departamento.setError(null);
            //municipio.setError(null);
            barrio.setError(null);
            direccion.setError(null);
            //telefono.setError(null);
            diagnostico.setError(null);

            if (StringUtils.isNullOrEmpty(edtxtNombreApellido.getText().toString()) &&
                    StringUtils.isNullOrEmpty(edtxtTutor.getText().toString())) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        strNombreApellido, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }

            if (depatamento <= 0) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        strDepartamento, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (municipio <= 0) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        strMunicipio, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            /*if (departamento == null || TextUtils.isEmpty(departamento.getText().toString())
                    || departamento.length() == 0 || departamento.equals("")) {
                departamento.setError(getString(R.string.label_campo_requerido));
                departamento.requestFocus();
                focusView = departamento;
                return false;
            }*/
            /*if (municipio == null || TextUtils.isEmpty(municipio.getText().toString())
                    || municipio.length() == 0 || municipio.equals("")) {
                municipio.setError(getString(R.string.label_campo_requerido));
                municipio.requestFocus();
                focusView = municipio;
                return false;
            }*/
            if (barrio == null || TextUtils.isEmpty(barrio.getText().toString())
                    || barrio.length() == 0 || barrio.equals("")) {
                barrio.setError(getString(R.string.label_campo_requerido));
                barrio.requestFocus();
                focusView = barrio;
                return false;
            }
            if (direccion == null || TextUtils.isEmpty(direccion.getText().toString())
                    || direccion.length() == 0 || direccion.equals("")) {
                direccion.setError(getString(R.string.label_campo_requerido));
                focusView = direccion;
                return false;
            }
           /* if (telefono == null || TextUtils.isEmpty(telefono.getText().toString())
                    || telefono.length() == 0 || telefono.equals("")) {
                telefono.setError(getString(R.string.label_campo_requerido));
                focusView = telefono;
                return false;
            }*/
            if (!chkUrbano.isChecked() && !chkRural.isChecked()) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        mensaje, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (!chkCaptacionEmergencia.isChecked() && !chkCaptacionSala.isChecked() &&
                    !chkCaptacionUci.isChecked() && !chkCaptacionAmbulatorio.isChecked()) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        mensaje, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (diagnostico == null || TextUtils.isEmpty(diagnostico.getText().toString())
                    || diagnostico.length() == 0 || diagnostico.equals("")) {
                diagnostico.setError(getString(R.string.label_campo_requerido));
                diagnostico.requestFocus();
                focusView = diagnostico;
                return false;
            }
            return true;
        }

        /* Verificar que todos los datos requeridos no vallan vacios, si existe un dato requerido
         vacio no se podra pasar a la siguiente pantalla */
        public boolean validarAntecedentesVacunacion() {
            focusView = null;

            String mensajeVacuna = "Debe de indicar si presenta tarjeta de vacunación";
            String mensajeVacunaAntiHub = "Debe de indicar si la vacuna Anti Hib fue aplicada";
            String mensajeVacunaAntiMening = "Debe de indicar si la vacuna Anti Meningococica fue aplicada";
            String mensajeVacunaAntiNeumococica = "Debe de indicar si la vacuna Anti Neumococica fue aplicada";
            String mensajeVacunaAntiInfluenza = "Debe de indicar si la vacuna Anti Influenza fue aplicada";
            String mensajeCualVacuna = "Debe de indicar ¿Cúal? vacuna";

            // Presenta Tatjeta Vacuna
            CheckBox chkTarjetaVacunaSi = (CheckBox) getActivity().findViewById(R.id.chkTarjetaVacunaSi);
            CheckBox chkTarjetaVacunaNo = (CheckBox) getActivity().findViewById(R.id.chkTarjetaVacunaNo);
            // Vacuna AntiHib
            CheckBox chkAntiHibSi = (CheckBox) getActivity().findViewById(R.id.chkAntiHibSi);
            CheckBox chkAntiHibNo = (CheckBox) getActivity().findViewById(R.id.chkAntiHibNo);
            CheckBox chkAntiHibNoSabe = (CheckBox) getActivity().findViewById(R.id.chkAntiHibNoSabe);
            CheckBox chkPentavalente = (CheckBox) getActivity().findViewById(R.id.chkPentavalente);
            EditText edtxtNoDosisPentavalente = (EditText) getActivity().findViewById(R.id.edtxtNoDosisPentavalente);
            EditText dpFechaUltimaDosisAntiHib = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiHib);
            // Vacuna AntiMeningococica
            CheckBox chkAntiMenigSi = (CheckBox) getActivity().findViewById(R.id.chkAntiMenigSi);
            CheckBox chkAntiMenigNo = (CheckBox) getActivity().findViewById(R.id.chkAntiMenigNo);
            CheckBox chkAntiMenigNoSabe = (CheckBox) getActivity().findViewById(R.id.chkAntiMenigNoSabe);
            CheckBox chkConjugada = (CheckBox) getActivity().findViewById(R.id.chkConjugada);
            CheckBox chkPolisacarida = (CheckBox) getActivity().findViewById(R.id.chkPolisacarida);
            EditText edtxtNoDosisAntiMening = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiMening);
            EditText dpFechaUltimaDosisAntiMening = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiMening);
            // Vacuna Anti Neumococica
            CheckBox chkAntiNeumococicaSi = (CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaSi);
            CheckBox chkAntiNeumococicaNo = (CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaNo);
            CheckBox chkAntiNeumococicaNoSabe = (CheckBox) getActivity().findViewById(R.id.chkAntiNeumococicaNoSabe);
            CheckBox chkHeptavalente = (CheckBox) getActivity().findViewById(R.id.chkHeptavalente);
            CheckBox chk23Polisacarida = (CheckBox) getActivity().findViewById(R.id.chk23Polisacarida);
            CheckBox chk13Valente = (CheckBox) getActivity().findViewById(R.id.chk13Valente);
            EditText edtxtNoDosisAntiNeumococica = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiNeumococica);
            EditText dpFechaUltimaDosisAntiNeumococica = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiNeumococica);
            // Vacuna Anti Influenza
            CheckBox chkAntiInfluenzaSi = (CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaSi);
            CheckBox chkAntiInfluenzaNo = (CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaNo);
            CheckBox chkAntiInfluenzaNoSabe = (CheckBox) getActivity().findViewById(R.id.chkAntiInfluenzaNoSabe);
            CheckBox chkEstacional = (CheckBox) getActivity().findViewById(R.id.chkEstacional);
            CheckBox chkH1N1p = (CheckBox) getActivity().findViewById(R.id.chkH1N1p);
            CheckBox chkOtraVacunaAntiInfluenza = (CheckBox) getActivity().findViewById(R.id.chkOtraVacunaAntiInfluenza);
            EditText edtxtNoDosisAntiInfluenza = (EditText) getActivity().findViewById(R.id.edtxtNoDosisAntiInfluenza);
            EditText dpFechaUltimaDosisAntiInfluenza = (EditText) getActivity().findViewById(R.id.dpFechaUltimaDosisAntiInfluenza);

            edtxtNoDosisPentavalente.setError(null);
            dpFechaUltimaDosisAntiHib.setError(null);
            edtxtNoDosisAntiMening.setError(null);
            dpFechaUltimaDosisAntiMening.setError(null);
            edtxtNoDosisAntiNeumococica.setError(null);
            dpFechaUltimaDosisAntiNeumococica.setError(null);
            edtxtNoDosisAntiInfluenza.setError(null);
            dpFechaUltimaDosisAntiInfluenza.setError(null);

            // Verificamos si presenta tarjeta de vacuna
            if (!chkTarjetaVacunaSi.isChecked() && !chkTarjetaVacunaNo.isChecked()) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        mensajeVacuna, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            // Verificamos que se seleccione una opcion de vacuna AntiHib
            if (!chkAntiHibSi.isChecked() && !chkAntiHibNo.isChecked() && !chkAntiHibNoSabe.isChecked()) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        mensajeVacunaAntiHub, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            // Validamos si la vacuna AntiHib es Si para llenar todos sus datos
            if (chkAntiHibSi.isChecked()) {
                // Verificamos que se seleccione la vacuna pentavalente
                if (!chkPentavalente.isChecked()) {
                    MensajesHelper.mostrarMensajeError(getActivity(),
                            mensajeCualVacuna, getResources().getString(
                                    R.string.title_estudio_sostenible),
                            null);
                    return false;
                } else { // Si esta chekeado
                    // Verificamos que se si ingrese el No. de dosis
                    if (edtxtNoDosisPentavalente == null || TextUtils.isEmpty(edtxtNoDosisPentavalente.getText().toString())
                            || edtxtNoDosisPentavalente.length() == 0 || edtxtNoDosisPentavalente.equals("")) {
                        edtxtNoDosisPentavalente.setError(getString(R.string.label_campo_requerido));
                        edtxtNoDosisPentavalente.requestFocus();
                        focusView = edtxtNoDosisPentavalente;
                        return false;
                    }
                    // Verificamos que se ingrese la fecha de la ultima dosis
                    if (dpFechaUltimaDosisAntiHib == null || TextUtils.isEmpty(dpFechaUltimaDosisAntiHib.getText().toString())
                            || dpFechaUltimaDosisAntiHib.length() == 0 || dpFechaUltimaDosisAntiHib.equals("")) {
                        dpFechaUltimaDosisAntiHib.setError(getString(R.string.label_fecha_no_dosis));
                        dpFechaUltimaDosisAntiHib.requestFocus();
                        focusView = dpFechaUltimaDosisAntiHib;
                        return false;
                    }
                }
            }
            // Verificamos que se seleccione una opcion de vacuna Anti Meningococica
            if (!chkAntiMenigSi.isChecked() && !chkAntiMenigNo.isChecked() && !chkAntiMenigNoSabe.isChecked()) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        mensajeVacunaAntiMening, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            // Validamos si la vacuna Anti Meningococica es Si para llenar todos sus datos
            if (chkAntiMenigSi.isChecked()) {
                // Verificamos que se seleccione la vacuna Conjugada o Polisacarida
                if (!chkConjugada.isChecked() && !chkPolisacarida.isChecked()) {
                    MensajesHelper.mostrarMensajeError(getActivity(),
                            mensajeCualVacuna, getResources().getString(
                                    R.string.title_estudio_sostenible),
                            null);
                    return false;
                } else { // Si esta chekeado
                    // Verificamos que se si ingrese el No. de dosis
                    if (edtxtNoDosisAntiMening == null || TextUtils.isEmpty(edtxtNoDosisAntiMening.getText().toString())
                            || edtxtNoDosisAntiMening.length() == 0 || edtxtNoDosisAntiMening.equals("")) {
                        edtxtNoDosisAntiMening.setError(getString(R.string.label_campo_requerido));
                        edtxtNoDosisAntiMening.requestFocus();
                        focusView = edtxtNoDosisAntiMening;
                        return false;
                    }
                    // Verificamos que se ingrese la fecha de la ultima dosis
                    if (dpFechaUltimaDosisAntiMening == null || TextUtils.isEmpty(dpFechaUltimaDosisAntiMening.getText().toString())
                            || dpFechaUltimaDosisAntiMening.length() == 0 || dpFechaUltimaDosisAntiMening.equals("")) {
                        dpFechaUltimaDosisAntiMening.setError(getString(R.string.label_fecha_no_dosis));
                        dpFechaUltimaDosisAntiMening.requestFocus();
                        focusView = dpFechaUltimaDosisAntiMening;
                        return false;
                    }
                }
            }
            // Verificamos que se seleccione una opcion de vacuna Anti Neumococica
            if (!chkAntiNeumococicaSi.isChecked() && !chkAntiNeumococicaNo.isChecked() && !chkAntiNeumococicaNoSabe.isChecked()) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        mensajeVacunaAntiNeumococica, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            // Validamos si la vacuna Anti Neumococica es Si para llenar todos sus datos
            if (chkAntiNeumococicaSi.isChecked()) {
                // Verificamos que se seleccione la vacuna Heptavalenta, 23Polisacarida o 13Valente
                if (!chkHeptavalente.isChecked() && !chk23Polisacarida.isChecked() && !chk13Valente.isChecked()) {
                    MensajesHelper.mostrarMensajeError(getActivity(),
                            mensajeCualVacuna, getResources().getString(
                                    R.string.title_estudio_sostenible),
                            null);
                    return false;
                } else { // Si esta chekeado
                    // Verificamos que se si ingrese el No. de dosis
                    if (edtxtNoDosisAntiNeumococica == null || TextUtils.isEmpty(edtxtNoDosisAntiNeumococica.getText().toString())
                            || edtxtNoDosisAntiNeumococica.length() == 0 || edtxtNoDosisAntiNeumococica.equals("")) {
                        edtxtNoDosisAntiNeumococica.setError(getString(R.string.label_campo_requerido));
                        edtxtNoDosisAntiNeumococica.requestFocus();
                        focusView = edtxtNoDosisAntiNeumococica;
                        return false;
                    }
                    // Verificamos que se ingrese la fecha de la ultima dosis
                    if (dpFechaUltimaDosisAntiNeumococica == null || TextUtils.isEmpty(dpFechaUltimaDosisAntiNeumococica.getText().toString())
                            || dpFechaUltimaDosisAntiNeumococica.length() == 0 || dpFechaUltimaDosisAntiNeumococica.equals("")) {
                        dpFechaUltimaDosisAntiNeumococica.setError(getString(R.string.label_fecha_no_dosis));
                        dpFechaUltimaDosisAntiNeumococica.requestFocus();
                        focusView = dpFechaUltimaDosisAntiNeumococica;
                        return false;
                    }
                }
            }
            // Verificamos que se seleccione una opcion de vacuna Anti Influenza
            if (!chkAntiInfluenzaSi.isChecked() && !chkAntiInfluenzaNo.isChecked() && !chkAntiInfluenzaNoSabe.isChecked()) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        mensajeVacunaAntiInfluenza, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            // Validamos si la vacuna Anti Influenza es Si para llenar todos sus datos
            if (chkAntiInfluenzaSi.isChecked()) {
                // Verificamos que se seleccione la vacuna Estacional, H1n1p o Otra
                if (!chkEstacional.isChecked() && !chkH1N1p.isChecked() && !chkOtraVacunaAntiInfluenza.isChecked()) {
                    MensajesHelper.mostrarMensajeError(getActivity(),
                            mensajeCualVacuna, getResources().getString(
                                    R.string.title_estudio_sostenible),
                            null);
                    return false;
                } else { // Si esta chekeado
                    // Verificamos que se si ingrese el No. de dosis
                    if (edtxtNoDosisAntiInfluenza == null || TextUtils.isEmpty(edtxtNoDosisAntiInfluenza.getText().toString())
                            || edtxtNoDosisAntiInfluenza.length() == 0 || edtxtNoDosisAntiInfluenza.equals("")) {
                        edtxtNoDosisAntiInfluenza.setError(getString(R.string.label_campo_requerido));
                        edtxtNoDosisAntiInfluenza.requestFocus();
                        focusView = edtxtNoDosisAntiInfluenza;
                        return false;
                    }
                    // Verificamos que se ingrese la fecha de la ultima dosis
                    if (dpFechaUltimaDosisAntiInfluenza == null || TextUtils.isEmpty(dpFechaUltimaDosisAntiInfluenza.getText().toString())
                            || dpFechaUltimaDosisAntiInfluenza.length() == 0 || dpFechaUltimaDosisAntiInfluenza.equals("")) {
                        dpFechaUltimaDosisAntiInfluenza.setError(getString(R.string.label_fecha_no_dosis));
                        dpFechaUltimaDosisAntiInfluenza.requestFocus();
                        focusView = dpFechaUltimaDosisAntiInfluenza;
                        return false;
                    }
                }
            }
            return true;
        }

        /* Verificar que todos los datos requeridos no vallan vacios, si existe un dato requerido
         vacio no se podra pasar a la siguiente pantalla */
        public boolean validarCondicionesPreexistentes() {
            focusView = null;
            CheckBox chkEmbarazoSi = (CheckBox) getActivity().findViewById(R.id.chkEmbarazoSi);
            CheckBox chkOtraSi = (CheckBox) getActivity().findViewById(R.id.chkOtraSi);
            CheckBox chkOtraManifestacionClinicaSi = (CheckBox) getActivity().findViewById(R.id.chkOtraManifestacionClinicaSi);
            EditText edtxtSemanasEmbarazo = (EditText) getActivity().findViewById(R.id.edtxtSemanasEmbarazo);
            EditText edtxtOtra = (EditText) getActivity().findViewById(R.id.edtxtOtra);
            EditText edtxtOtraManifestacionClinica = (EditText) getActivity().findViewById(R.id.edtxtOtraManifestacionClinica);
            edtxtSemanasEmbarazo.setError(null);
            edtxtOtra.setError(null);
            edtxtOtraManifestacionClinica.setError(null);
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkCancerSi), getActivity().findViewById(R.id.chkCancerNo),
                    getActivity().findViewById(R.id.chkCancerNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkDiabetesSi), getActivity().findViewById(R.id.chkDiabetesNo),
                    getActivity().findViewById(R.id.chkDiabetesNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkVihSi), getActivity().findViewById(R.id.chkVihNo),
                    getActivity().findViewById(R.id.chkVihNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkOtraInmunodeficienciaSi),
                    getActivity().findViewById(R.id.chkOtraInmunodeficienciaNo), getActivity().findViewById(R.id.chkOtraInmunodeficienciaNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkEnfNeurologicaCronicaSi),
                    getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNo), getActivity().findViewById(R.id.chkEnfNeurologicaCronicaNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkEnfermedadCardiacaSi),
                    getActivity().findViewById(R.id.chkEnfermedadCardiacaNo), getActivity().findViewById(R.id.chkEnfermedadCardiacaNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkAsmaSi), getActivity().findViewById(R.id.chkAsmaNo),
                    getActivity().findViewById(R.id.chkAsmaNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkEpocSi), getActivity().findViewById(R.id.chkEpocNo),
                    getActivity().findViewById(R.id.chkEpocNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkOtraEnfPulmonarSi), getActivity().findViewById(R.id.chkOtraEnfPulmonarNo),
                    getActivity().findViewById(R.id.chkOtraEnfPulmonarNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkInsufRenalCronicaSi), getActivity().findViewById(R.id.chkInsufRenalCronicaNo),
                    getActivity().findViewById(R.id.chkInsufRenalCronicaNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkDesnutricionSi), getActivity().findViewById(R.id.chkDesnutricionNo),
                    getActivity().findViewById(R.id.chkDesnutricionNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkObesidadSi), getActivity().findViewById(R.id.chkObesidadNo),
                    getActivity().findViewById(R.id.chkObesidadNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkEmbarazoSi), getActivity().findViewById(R.id.chkEmbarazoNo),
                    getActivity().findViewById(R.id.chkEmbarazoNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (chkEmbarazoSi.isChecked()) {
                if (edtxtSemanasEmbarazo == null || TextUtils.isEmpty(edtxtSemanasEmbarazo.getText().toString())
                        || edtxtSemanasEmbarazo.length() == 0 || edtxtSemanasEmbarazo.equals("")) {
                    edtxtSemanasEmbarazo.setError(getString(R.string.label_campo_requerido));
                    edtxtSemanasEmbarazo.requestFocus();
                    focusView = edtxtSemanasEmbarazo;
                    return false;
                }
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkTxCorticosteroideSi), getActivity().findViewById(R.id.chkTxCorticosteroideNo),
                    getActivity().findViewById(R.id.chkTxCorticosteroideNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkOtraSi), getActivity().findViewById(R.id.chkOtraNo),
                    getActivity().findViewById(R.id.chkOtraNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (chkOtraSi.isChecked()) {
                if (edtxtOtra == null || TextUtils.isEmpty(edtxtOtra.getText().toString())
                        || edtxtOtra.length() == 0 || edtxtOtra.equals("")) {
                    edtxtOtra.setError(getString(R.string.label_campo_requerido));
                    edtxtOtra.requestFocus();
                    focusView = edtxtOtra;
                    return false;
                }
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkEstornudosSi), getActivity().findViewById(R.id.chkEstornudosNo),
                    getActivity().findViewById(R.id.chkEstornudosNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkOtraManifestacionClinicaSi), getActivity().findViewById(R.id.chkOtraManifestacionClinicaNo),
                    getActivity().findViewById(R.id.chkOtraManifestacionClinicaNs))) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        (getString(R.string.msj_completar_informacion)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            if (chkOtraManifestacionClinicaSi.isChecked()) {
                if (edtxtOtraManifestacionClinica == null || TextUtils.isEmpty(edtxtOtraManifestacionClinica.getText().toString())
                        || edtxtOtraManifestacionClinica.length() == 0 || edtxtOtraManifestacionClinica.equals("")) {
                    edtxtOtraManifestacionClinica.setError(getString(R.string.label_campo_requerido));
                    edtxtOtraManifestacionClinica.requestFocus();
                    focusView = edtxtOtraManifestacionClinica;
                    return false;
                }
            }
            return true;
        }

        /* Verificar que todos los datos requeridos no vallan vacios, si existe un dato requerido
        vacio no se podra guardar la informacion */
        public boolean validarUsoAntibioticos() {
            focusView = null;
            String mensajeUsoAntibiotico = "Debe de indicar si uso antibiotico durante la ultima semana";
            String mensajeUsoAntiviral = "Debe de indicar si tiene antecedentes de uso antivirales";
            String mensajeVia = "Debe de indicar la via de administracion del antibiotico";
            edtxtCuantosAntibioticos = ((EditText) getActivity().findViewById(R.id.edtxtCuantosAntibioticos));
            edtxtCualesAntibioticosDio = ((EditText) getActivity().findViewById(R.id.edtxtCualesAntibioticosDio));
            edtxtDiasUltimoAntibiotico = ((EditText) getActivity().findViewById(R.id.edtxtDiasUltimoAntibiotico));
            edtxtNombreAntiviral = ((EditText) getActivity().findViewById(R.id.edtxtNombreAntiviral));
            dpFecha1raDosisAntiViral = ((EditText) getActivity().findViewById(R.id.dpFecha1raDosisAntiViral));
            dpFechaUltDosisAntiViral = ((EditText) getActivity().findViewById(R.id.dpFechaUltDosisAntiViral));
            CheckBox chkUsoAntibioticoSi = (CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoSi);
            CheckBox chkUsoAntibioticoNo = (CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoNo);
            CheckBox chkUsoAntibioticoNoSabe = (CheckBox) getActivity().findViewById(R.id.chkUsoAntibioticoNoSabe);
            CheckBox chkViaOral = (CheckBox) getActivity().findViewById(R.id.chkViaOral);
            CheckBox chkViaParenteral = (CheckBox) getActivity().findViewById(R.id.chkViaParenteral);
            CheckBox chkViaAmbas = (CheckBox) getActivity().findViewById(R.id.chkViaAmbas);
            edtxtCuantosAntibioticos.setError(null);
            edtxtCualesAntibioticosDio.setError(null);
            edtxtDiasUltimoAntibiotico.setError(null);
            edtxtNombreAntiviral.setError(null);
            dpFecha1raDosisAntiViral.setError(null);
            dpFechaUltDosisAntiViral.setError(null);
            // Verificamos que se halla marcado el uso de antibiotico durante la ultima semana
            if (!chkUsoAntibioticoSi.isChecked() && !chkUsoAntibioticoNo.isChecked()
                    && !chkUsoAntibioticoNoSabe.isChecked()) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        mensajeUsoAntibiotico, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            // Verificamos si hay uso de antibiotico
            if (chkUsoAntibioticoSi.isChecked()) {

                getActivity().findViewById(R.id.edtxtCuantosAntibioticos).setEnabled(true);
                getActivity().findViewById(R.id.edtxtCualesAntibioticosDio).setEnabled(true);
                getActivity().findViewById(R.id.edtxtDiasUltimoAntibiotico).setEnabled(true);

                // Verificamos que se ingrese la cantidad de antibioticos que le dio
                if (edtxtCuantosAntibioticos == null || TextUtils.isEmpty(edtxtCuantosAntibioticos.getText().toString())
                        || edtxtCuantosAntibioticos.length() == 0 || edtxtCuantosAntibioticos.equals("")) {
                    edtxtCuantosAntibioticos.setError(getString(R.string.label_campo_requerido));
                    edtxtCuantosAntibioticos.requestFocus();
                    focusView = edtxtCuantosAntibioticos;
                    return false;
                }

                // Verificamos que se indique cuales antibioticos le dio
                if (edtxtCualesAntibioticosDio == null || TextUtils.isEmpty(edtxtCualesAntibioticosDio.getText().toString())
                        || edtxtCualesAntibioticosDio.length() == 0 || edtxtCualesAntibioticosDio.equals("")) {
                    edtxtCualesAntibioticosDio.setError(getString(R.string.label_campo_requerido));
                    edtxtCualesAntibioticosDio.requestFocus();
                    focusView = edtxtCualesAntibioticosDio;
                    return false;
                }

                // Verificamos que se indique cuantos dias le dio el ultimo antibiotico
                if (edtxtDiasUltimoAntibiotico == null || TextUtils.isEmpty(edtxtDiasUltimoAntibiotico.getText().toString())
                        || edtxtDiasUltimoAntibiotico.length() == 0 || edtxtDiasUltimoAntibiotico.equals("")) {
                    edtxtDiasUltimoAntibiotico.setError(getString(R.string.label_campo_requerido));
                    edtxtDiasUltimoAntibiotico.requestFocus();
                    focusView = edtxtDiasUltimoAntibiotico;
                    return false;
                }
                // Verificamos que se halla marcado el tipo de via de administracion del antibiotico
                if (!chkViaOral.isChecked() && !chkViaParenteral.isChecked()
                        && !chkViaAmbas.isChecked()) {
                    MensajesHelper.mostrarMensajeError(getActivity(),
                            mensajeVia, getResources().getString(
                                    R.string.title_estudio_sostenible),
                            null);
                    return false;
                }
            }

            // Verifica que se halla marcado el uso de antecedentes antivirales
            CheckBox chkUsoAntiViralSi = (CheckBox) getActivity().findViewById(R.id.chkUsoAntiViralSi);
            CheckBox chkUsoAntiViralNo = (CheckBox) getActivity().findViewById(R.id.chkUsoAntiViralNo);
            if (!chkUsoAntiViralSi.isChecked() && !chkUsoAntiViralNo.isChecked()) {
                MensajesHelper.mostrarMensajeError(getActivity(),
                        mensajeUsoAntiviral, getResources().getString(
                                R.string.title_estudio_sostenible),
                        null);
                return false;
            }
            // Verificamos si se marco el uso de antiviral como si
            if (chkUsoAntiViralSi.isChecked()) {
                getActivity().findViewById(R.id.edtxtNombreAntiviral).setEnabled(true);
                getActivity().findViewById(R.id.dpFecha1raDosisAntiViral).setEnabled(true);
                getActivity().findViewById(R.id.dpFechaUltDosisAntiViral).setEnabled(true);
                getActivity().findViewById(R.id.edtxtNoDosisAdministrada).setEnabled(true);
                if (edtxtNombreAntiviral == null || TextUtils.isEmpty(edtxtNombreAntiviral.getText().toString())
                        || edtxtNombreAntiviral.length() == 0 || edtxtNombreAntiviral.equals("")) {
                    edtxtNombreAntiviral.setError(getString(R.string.label_campo_requerido));
                    edtxtNombreAntiviral.requestFocus();
                    focusView = edtxtNombreAntiviral;
                    return false;
                }
                if (dpFecha1raDosisAntiViral == null || TextUtils.isEmpty(dpFecha1raDosisAntiViral.getText().toString())
                        || dpFecha1raDosisAntiViral.length() == 0 || dpFecha1raDosisAntiViral.equals("")) {
                    dpFecha1raDosisAntiViral.setError(getString(R.string.label_campo_requerido));
                    dpFecha1raDosisAntiViral.requestFocus();
                    focusView = dpFecha1raDosisAntiViral;
                    return false;
                }
                if (dpFechaUltDosisAntiViral == null || TextUtils.isEmpty(dpFechaUltDosisAntiViral.getText().toString())
                        || dpFechaUltDosisAntiViral.length() == 0 || dpFechaUltDosisAntiViral.equals("")) {
                    dpFechaUltDosisAntiViral.setError(getString(R.string.label_campo_requerido));
                    dpFechaUltDosisAntiViral.requestFocus();
                    focusView = dpFechaUltDosisAntiViral;
                    return false;
                }

            }
            return true;
        }

        /***
         * Metodo que realiza la carga de los municipios que pertenecen al departamento seleccionado
         */
        private void llamadoServicioObtenerMunicipios(final int divisionpoliticaId) {
            /*Creando una tarea asincrona*/
            AsyncTask<Void, Void, Void> listaInicioServicio = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) CONTEXT.getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();

                private ExpedienteWS EXPEDIENTEWS = new ExpedienteWS(getResources());

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
                        try {
                            RESPUESTAMUNICIP = EXPEDIENTEWS.getListaMunicipio(divisionpoliticaId);
                        } catch (Exception e) {
                            MensajesHelper.mostrarMensajeError(CONTEXT,
                                    e.getMessage(), getResources().getString(
                                            R.string.app_name), null);

                        }
                        //  RESPUESTA = CONSULTAWS.getListaInicioConsultaPorExpediente(1);
                    } else {
                        RESPUESTAMUNICIP.setCodigoError(Long.parseLong("3"));
                        RESPUESTAMUNICIP.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    PD.dismiss();
                    if (RESPUESTAMUNICIP.getCodigoError().intValue() == 0) {
                        MunicipiosDTO municipiosDTO = new MunicipiosDTO();
                        ArrayList<MunicipiosDTO> municipios = new ArrayList<MunicipiosDTO>();
                        municipiosDTO.setDivisionpoliticaId(0);
                        municipiosDTO.setNombre("Seleccione el Municipio");
                        municipiosDTO.setDependencia(0);
                        municipiosDTO.setCodigoNacional(0);
                        municipios.add(municipiosDTO);
                        municipios.addAll(RESPUESTAMUNICIP.getLstResultado());
                        Spinner spnMunicipios = (Spinner) getActivity().findViewById(R.id.spnMunicipios);
                        adapterMunicipios = new ArrayAdapter<MunicipiosDTO>(CONTEXT, R.layout.simple_spinner_dropdown_item, municipios) {
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View v = super.getDropDownView(position, convertView, parent);
                                ((TextView) v).setGravity(Gravity.LEFT);
                                return v;
                            }
                        };

                        spnMunicipios.setAdapter(adapterMunicipios);
                        if (isEdit && mMunicipio > 0) {
                            MunicipiosDTO municip = new MunicipiosDTO();
                            int mId = 0;
                            for (int i = 0; i < municipios.size(); i++) {
                                if (municipios.get(i).getCodigoNacional() == mMunicipio) {
                                    mId = i;
                                    municip.setDivisionpoliticaId(municipios.get(i).getDivisionpoliticaId());
                                    municip.setCodigoNacional(municipios.get(i).getCodigoNacional());
                                    municip.setNombre(municipios.get(i).getNombre());
                                    municip.setDependencia(municipios.get(i).getDependencia());
                                }
                            }
                            ((Spinner) getActivity().findViewById(R.id.spnMunicipios)).setSelection(mId);
                            isEdit = false;
                            mMunicipio = 0;
                        }
                    } else if (RESPUESTAMUNICIP.getCodigoError().intValue() != 999) {
                        MensajesHelper.mostrarMensajeInfo(CONTEXT,
                                RESPUESTAMUNICIP.getMensajeError(), getResources().getString(
                                        R.string.app_name), null);

                    } else {
                        MensajesHelper.mostrarMensajeError(CONTEXT,
                                RESPUESTAMUNICIP.getMensajeError(), getResources().getString(
                                        R.string.app_name), null);
                    }
                }
            };
            listaInicioServicio.execute((Void[]) null);
        }

        /***
         * Metodo para llamar el servicio que obtiene el seguimiento zika PDF.
         */
        private void obtenerFichaPdf() {
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
                        RESPUESTA = ESPEDIENTEWS.getFichaPdf(mVigilanciaIntegradaActivity.mSecVigilanciaIntegrada);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    PD.dismiss();

                    try {

                        File file = new File("/sdcard/VigilanciaInfeccionesRespiratorias.pdf");
                        FileOutputStream fos = new FileOutputStream(file);
                        String filepath = "/sdcard/VigilanciaInfeccionesRespiratorias.pdf";
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

        /*Metodo para hacer la seleccion de la impresora*/
        private void showAlertDialogFicha() {
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
                            ImprimirFichaPdf(consultorioMedico);
                            dialog.cancel();
                            break;
                        case 1:
                            Toast.makeText(CONTEXT, "Impresión enviada al Consultorio Respiratorio", Toast.LENGTH_LONG).show();
                            ImprimirFichaPdf(consultorioResp);
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
         * Metodo para llamar servicio que imprime la ficha.
         */
        private void ImprimirFichaPdf(final int consultorio) {
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
                        EXPEDIENTE.ImprimirFichaPdf(mVigilanciaIntegradaActivity.mSecVigilanciaIntegrada, consultorio);
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

        /***
         * Metodos Botones Next y Back
         * Establece los metodos que seran ejecutados en los eventos onClick de los botones atras y siguiente
         */
        public void establecerMetodosBotonesAtrasSiguiente() {
            ImageButton imgNext1 = (ImageButton) getActivity().findViewById(R.id.imgNext2VigilanciaIntegrada);
            ImageButton imgNext2 = (ImageButton) getActivity().findViewById(R.id.imgNext3VigilanciaIntegrada);
            ImageButton imgNext3 = (ImageButton) getActivity().findViewById(R.id.imgNext4VigilanciaIntegrada);

            ImageButton imgBack1 = (ImageButton) getActivity().findViewById(R.id.imgBack1VigilanciaIntegrada);
            ImageButton imgBack2 = (ImageButton) getActivity().findViewById(R.id.imgBack2VigilanciaIntegrada);
            ImageButton imgBack3 = (ImageButton) getActivity().findViewById(R.id.imgBack3VigilanciaIntegrada);

            final LinearLayout page1 = (LinearLayout) getActivity().findViewById(R.id.page1VigilanciaIntegrada);
            final LinearLayout page2 = (LinearLayout) getActivity().findViewById(R.id.page2VigilanciaIntegrada);
            final LinearLayout page3 = (LinearLayout) getActivity().findViewById(R.id.page3VigilanciaIntegrada);
            final LinearLayout page4 = (LinearLayout) getActivity().findViewById(R.id.page4VigilanciaIntegrada);

            imgNext1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarDatosPaciente()) {
                        page1.setVisibility(View.GONE);
                        page2.setVisibility(View.VISIBLE);
                    }
                }
            });
            imgNext2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarAntecedentesVacunacion()) {
                        page2.setVisibility(View.GONE);
                        page3.setVisibility(View.VISIBLE);
                    }
                }
            });
            imgNext3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCondicionesPreexistentes()) {
                        page3.setVisibility(View.GONE);
                        page4.setVisibility(View.VISIBLE);
                    }
                }
            });
            imgBack1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page1.setVisibility(View.VISIBLE);
                    page2.setVisibility(View.GONE);
                }
            });
            imgBack2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page2.setVisibility(View.VISIBLE);
                    page3.setVisibility(View.GONE);
                }
            });
            imgBack3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page3.setVisibility(View.VISIBLE);
                    page4.setVisibility(View.GONE);
                }
            });
        }
    }
}