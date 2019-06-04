package com.sts_ni.estudiocohortecssfv.sintomasactivities;

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
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.InicioEnfermeriaActivity;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ComplementoCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.HorarioAtencionDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.DatePickerFragment;
import com.sts_ni.estudiocohortecssfv.tools.DecimalDigitsInputFilter;
import com.sts_ni.estudiocohortecssfv.tools.MaskedWatcher;
import com.sts_ni.estudiocohortecssfv.tools.TimePickerFragment;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.ControlCambiosWS;
import com.sts_ni.estudiocohortecssfv.ws.EnfermeriaWS;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Cotnrolador de pantalla Generales Sintomas.
 */
public class GeneralesSintomasActivity extends ActionBarActivity {

    private Context CONTEXT;
    public ProgressDialog PD_CREATE;

    GeneralesControlCambiosDTO genControlCambios = null;
    private String vFueraRango = new String();
    ComplementoCambiosDTO cCtrC = new ComplementoCambiosDTO();
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private GeneralesSintomasDTO mCorrienteGeneralesSintomas;
    private String mUsuarioLogiado;
    private Boolean mAmPm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generales_sintoma);

        this.CONTEXT = this;

        inicializarContorles();

        HorarioAtencionTask task = new HorarioAtencionTask(this, this);
        task.execute((Void) null);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(this.PD_CREATE != null && this.PD_CREATE.isShowing() )
            this.PD_CREATE.dismiss();
    }

    /***
     * Metodo para inicializar todos los controles de interfaz de la pantalla.
     */
    public void inicializarContorles() {
        //((EditText)findViewById(R.id.edtxtPAGeneralesSint)).addTextChangedListener( new MaskedWatcher("###/###"));
        ((EditText)findViewById(R.id.edtxtTempMedGeneralesSint)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3,2)});

        findViewById(R.id.dpFis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogFis(v);
            }
        });

        ((EditText)findViewById(R.id.dpFis)).setKeyListener(null);

        findViewById(R.id.dpFif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showDatePickerDialogFif(v);
            }
        });

        ((EditText)findViewById(R.id.dpFif)).setKeyListener(null);

        findViewById(R.id.dpUltmFiebGeneralesSint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogFib(v);
            }
        });

        ((EditText)findViewById(R.id.dpUltmFiebGeneralesSint)).setKeyListener(null);

        findViewById(R.id.dpUltmDosGeneralesSint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogDosis(v);
            }
        });

        ((EditText)findViewById(R.id.dpUltmDosGeneralesSint)).setKeyListener(null);

        findViewById(R.id.edtxtHoraGeneralesSint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialogHora(v);
            }
        });

        ((EditText)findViewById(R.id.edtxtHoraGeneralesSint)).setKeyListener(null);
    }

    public void onChkboxClickedLugarAtent(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkbLugarAtenCSSFVGeneralesSint:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkbLugarAtenTerrenoGeneralesSint)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbLugarAtenTerrenoGeneralesSint:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkbLugarAtenCSSFVGeneralesSint)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
        }
    }

    public void onChkboxClickedConsulta(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkbConsultaInicialGeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbConsultaConvGeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbConsultaSeguimGeneralesSint)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbConsultaConvGeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbConsultaInicialGeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbConsultaSeguimGeneralesSint)).setChecked(false);
                } else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbConsultaSeguimGeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbConsultaInicialGeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbConsultaConvGeneralesSint)).setChecked(false);
                } else
                    ((CheckBox) view).setChecked(true);
                break;
        }
    }

    public void onChkboxClickedSegChick(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkbSegChk1GeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbSegChk2GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk3GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk4GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk5GeneralesSint)).setChecked(false);
                }
                /*else
                    ((CheckBox) view).setChecked(true);*/
                break;
            case R.id.chkbSegChk2GeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbSegChk1GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk3GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk4GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk5GeneralesSint)).setChecked(false);
                }
                /*else
                    ((CheckBox) view).setChecked(true);*/
                break;
            case R.id.chkbSegChk3GeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbSegChk1GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk2GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk4GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk5GeneralesSint)).setChecked(false);
                }
                /*else
                    ((CheckBox) view).setChecked(true);*/
                break;
            case R.id.chkbSegChk4GeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbSegChk1GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk2GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk3GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk5GeneralesSint)).setChecked(false);
                }
                /*else
                    ((CheckBox) view).setChecked(true);*/
                break;
            case R.id.chkbSegChk5GeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbSegChk1GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk2GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk3GeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbSegChk4GeneralesSint)).setChecked(false);
                }
                /*else
                    ((CheckBox) view).setChecked(true);*/
                break;
        }
    }

    public void onChkboxClickedTurno(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkbRegularGeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbNocheGeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbFindeGeneralesSint)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbNocheGeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbRegularGeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbFindeGeneralesSint)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbFindeGeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbRegularGeneralesSint)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbNocheGeneralesSint)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
        }
    }

    public void onChkboxClickedAMPMFb(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkbAMUltFGeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbPMUltFGeneralesSint)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbPMUltFGeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbAMUltFGeneralesSint)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
        }
    }

    /*public void onChkboxClickedAMPMUltD(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkbAMUltDGeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbPMUltDGeneralesSint)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbPMUltDGeneralesSint:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbAMUltDGeneralesSint)).setChecked(false);
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
        }
    }*/

    public void showDatePickerDialogFis(View view) {
        DialogFragment newFragment = new DatePickerFragment(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)getActivity().findViewById(R.id.dpFis)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) getActivity().findViewById(R.id.dpFis)).getError() != null) {
                        ((EditText) getActivity().findViewById(R.id.dpFis)).setError(null);
                        ((EditText) getActivity().findViewById(R.id.dpFis)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                    }
                    ((EditText) getActivity().findViewById(R.id.dpFis)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        };
        newFragment.show(getSupportFragmentManager(), getString(R.string.title_date_picker));
    }

    public void showDatePickerDialogFif(View view) {
        DialogFragment newFragment = new DatePickerFragment(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)getActivity().findViewById(R.id.dpFif)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) getActivity().findViewById(R.id.dpFif)).getError() != null) {
                        ((EditText) getActivity().findViewById(R.id.dpFif)).setError(null);
                        ((EditText) getActivity().findViewById(R.id.dpFif)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                    }
                    ((EditText) getActivity().findViewById(R.id.dpFif)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        };
        newFragment.show(getSupportFragmentManager(), getString(R.string.title_date_picker));
    }

    public void showDatePickerDialogFib(View view) {
        DialogFragment newFragment = new DatePickerFragment(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)getActivity().findViewById(R.id.dpUltmFiebGeneralesSint)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) getActivity().findViewById(R.id.dpUltmFiebGeneralesSint)).getError() != null) {
                        ((EditText) getActivity().findViewById(R.id.dpUltmFiebGeneralesSint)).setError(null);
                        ((EditText) getActivity().findViewById(R.id.dpUltmFiebGeneralesSint)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                    }
                    ((EditText) getActivity().findViewById(R.id.dpUltmFiebGeneralesSint)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        };
        newFragment.show(getSupportFragmentManager(), getString(R.string.title_date_picker));
    }

    public void showDatePickerDialogDosis(View view) {
        DialogFragment newFragment = new DatePickerFragment(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)getActivity().findViewById(R.id.dpUltmDosGeneralesSint)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    if(((EditText) getActivity().findViewById(R.id.dpUltmDosGeneralesSint)).getError() != null) {
                        ((EditText) getActivity().findViewById(R.id.dpUltmDosGeneralesSint)).setError(null);
                        ((EditText) getActivity().findViewById(R.id.dpUltmDosGeneralesSint)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                    }
                    ((EditText) getActivity().findViewById(R.id.dpUltmDosGeneralesSint)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        };
        newFragment.show(getSupportFragmentManager(), getString(R.string.title_date_picker));
    }

    /***
     * Metodo que realiza el llamdo del Time Picker.
     * @param view
     */
    public void showTimePickerDialogHora(View view) {
        DialogFragment newFragment = new TimePickerFragment(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                mAmPm = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? true :
                        (calendar.get(Calendar.AM_PM) == Calendar.PM) ? false : null;
                if(((EditText) getActivity().findViewById(R.id.edtxtHoraGeneralesSint)).getError() != null) {
                    ((EditText) getActivity().findViewById(R.id.edtxtHoraGeneralesSint)).setError(null);
                    ((EditText) getActivity().findViewById(R.id.edtxtHoraGeneralesSint)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.timer, 0);
                }

                ((EditText) getActivity().findViewById(R.id.edtxtHoraGeneralesSint)).setText(new SimpleDateFormat("KK:mm a").format(calendar.getTime()));
                //String a = ((EditText) findViewById(R.id.edtxtHoraGeneralesSint)).getText().toString();

                /*if(DateUtils.esMayorHoraActual(hourOfDay, minute)) {
                    ((EditText)getActivity().findViewById(R.id.edtxtHoraGeneralesSint)).setError(getString(R.string.msj_hora_mayor_actual));
                    ((EditText) getActivity().findViewById(R.id.edtxtHoraGeneralesSint)).setText("");
                }*/ /*else {
                    mAmPm = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? true :
                            (calendar.get(Calendar.AM_PM) == Calendar.PM) ? false : null;
                    if(((EditText) getActivity().findViewById(R.id.edtxtHoraGeneralesSint)).getError() != null) {
                        ((EditText) getActivity().findViewById(R.id.edtxtHoraGeneralesSint)).setError(null);
                        ((EditText) getActivity().findViewById(R.id.edtxtHoraGeneralesSint)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.timer, 0);
                    }
                    ((EditText) getActivity().findViewById(R.id.edtxtHoraGeneralesSint)).setText(new SimpleDateFormat("KK:mm a").format(calendar.getTime()));
                }*/
            }
        };
        newFragment.show(getSupportFragmentManager(), getString(R.string.title_date_picker));
    }

    /***
     * Metodo que controla el evento del onClick del botón de guardar.
     * @param view
     */
    public void onClick_btnGenerales( View view){
        ArrayList<ControlCambiosDTO> controlCambios = new ArrayList<>();
        genControlCambios = null;
        vFueraRango = "";

        try {
            validarCampoRequerido(controlCambios);
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
                                hojaConsultaSave();
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
            }else {
                hojaConsultaSave();
            }
        } catch (Exception e){
            if(e.getMessage() != null && !e.getMessage().isEmpty()) {
                MensajesHelper.mostrarMensajeInfo(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
            } else {
                MensajesHelper.mostrarMensajeInfo(this, getString(R.string.msj_error_no_controlado), getString(R.string.title_estudio_sostenible), null);
            }
            e.printStackTrace();
        }

    }

    /***
     * Metodo para cargar la información de interfaz a la Hoja consulta.
     */
    public void hojaConsultaSave(){
        mHojaConsulta = cargarHojaConsulta();
        if (mPacienteSeleccionado.getCodigoEstado() == '7') {
            if(mCorrienteGeneralesSintomas != null) {
                if(tieneCambiosHojaConsulta(mHojaConsulta)) {
                    DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    guardarGeneralesServicio();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    regresarPantallaAnterior();
                                    break;
                            }
                        }
                    };
                    MensajesHelper.mostrarMensajeYesNo(this,
                            getResources().getString(R.string.msj_cambio_hoja_consulta),
                            getResources().getString(R.string.title_estudio_sostenible), preguntaDialogClickListener);
                } else {
                    regresarPantallaAnterior();
                }
            } else {
                regresarPantallaAnterior();
            }
        } else {
            guardarGeneralesServicio();
        }
    }

    public void validarCampoRequerido(ArrayList<ControlCambiosDTO> controlCambios) throws Exception {
        //String[] pa = ((EditText) findViewById(R.id.edtxtPAGeneralesSint)).getText().toString().split("/");
        String pas = ((EditText) findViewById(R.id.edtxtPASint)).getText().toString();
        String pad = ((EditText) findViewById(R.id.edtxtPADint)).getText().toString();

        if (StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtPASint)).getText().toString())) {
            throw new Exception(getString(R.string.label_pas) + ", " + getString(R.string.msj_completar_informacion));
        } else if (StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtPADint)).getText().toString())) {
            throw new Exception(getString(R.string.label_pad) + ", " + getString(R.string.msj_completar_informacion));
        }  else if (StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtFciaCardGeneralesSint)).getText().toString())) {
            throw new Exception(getString(R.string.label_fcia_card) + ", " + getString(R.string.msj_completar_informacion));
        } else if (StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtFciaRespGeneralesSint)).getText().toString())) {
            throw new Exception(getString(R.string.label_fcia_resp) + ", " + getString(R.string.msj_completar_informacion));
        } else if (!((CheckBox) findViewById(R.id.chkbLugarAtenCSSFVGeneralesSint)).isChecked() && !((CheckBox) findViewById(R.id.chkbLugarAtenTerrenoGeneralesSint)).isChecked()) {
            throw new Exception(getString(R.string.label_lugar_atencion) + ", " + getString(R.string.msj_completar_informacion));
        } else if (!((CheckBox) findViewById(R.id.chkbConsultaInicialGeneralesSint)).isChecked() && !((CheckBox) findViewById(R.id.chkbConsultaSeguimGeneralesSint)).isChecked()
                && !((CheckBox) findViewById(R.id.chkbConsultaConvGeneralesSint)).isChecked()) {
            throw new Exception(getString(R.string.label_consulta) + ", " + getString(R.string.msj_completar_informacion));
        } /*else if (!((CheckBox) findViewById(R.id.chkbSegChk1GeneralesSint)).isChecked() && !((CheckBox) findViewById(R.id.chkbSegChk2GeneralesSint)).isChecked()
                && !((CheckBox) findViewById(R.id.chkbSegChk3GeneralesSint)).isChecked() && !((CheckBox) findViewById(R.id.chkbSegChk4GeneralesSint)).isChecked()
                && !((CheckBox) findViewById(R.id.chkbSegChk5GeneralesSint)).isChecked()) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } */ else if (!((CheckBox) findViewById(R.id.chkbRegularGeneralesSint)).isChecked() && !((CheckBox) findViewById(R.id.chkbNocheGeneralesSint)).isChecked()
                && !((CheckBox) findViewById(R.id.chkbFindeGeneralesSint)).isChecked()) {
            throw new Exception(getString(R.string.label_turno) + ", " + getString(R.string.msj_completar_informacion));
        } else if (StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtTempMedGeneralesSint)).getText().toString())) {
            throw new Exception(getString(R.string.label_temp_med_c) + ", " + getString(R.string.msj_completar_informacion));
        }/* else if ((!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.dpUltmDosGeneralesSint)).getText().toString())
                && StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtHoraGeneralesSint)).getText().toString()))
                || (StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.dpUltmDosGeneralesSint)).getText().toString())
                && !StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtHoraGeneralesSint)).getText().toString()))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtHoraGeneralesSint)).getText().toString()) &&
                !((CheckBox) findViewById(R.id.chkbAMUltDGeneralesSint)).isChecked() && !((CheckBox) findViewById(R.id.chkbPMUltDGeneralesSint)).isChecked()) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }*/
        else if(!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.dpUltmDosGeneralesSint)).getText().toString())) {
            if (StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtHoraGeneralesSint)).getText().toString())) {
                throw new Exception(getString(R.string.msj_hora_vacia));
            }
        }
        int cont = 0;
        //if (!estaEnRango(55, 135, pas)) { // rangos anteriores
        if (!estaEnRango(55, 220, pas)) {
           vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_pas));
            cont++;
           /* ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_pas));
            ctrCambios.setValorCampo(pas);
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/
        }

        //if (!estaEnRango(35, 100, pad)) { // rangos anteriores
        if (!estaEnRango(35, 160, pad)) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_pad));
            cont++;
          /*  ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_pad));
            ctrCambios.setValorCampo(pad);
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/
        }
        if (!estaEnRango(11, 80, ((EditText) findViewById(R.id.edtxtFciaRespGeneralesSint)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_fcia_resp));
            cont++;
           /* ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_fcia_resp));
            ctrCambios.setValorCampo(((EditText) findViewById(R.id.edtxtFciaRespGeneralesSint)).getText().toString());
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/
        }

        if (!estaEnRango(45, 200, ((EditText) findViewById(R.id.edtxtFciaCardGeneralesSint)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_fcia_card));
            cont++;
           /* ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_fcia_card));
            ctrCambios.setValorCampo(((EditText) findViewById(R.id.edtxtFciaCardGeneralesSint)).getText().toString());
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/
        }
        if (!estaEnRango(35.5, 41, ((EditText) findViewById(R.id.edtxtTempMedGeneralesSint)).getText().toString())) {
           vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_temp_med_c));
            cont++;
           /* ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_temp_med_c));
            ctrCambios.setValorCampo(((EditText) findViewById(R.id.edtxtTempMedGeneralesSint)).getText().toString());
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/
        }

        if (cont > 0){
            throw new Exception(getString(R.string.msj_aviso_control_cambios1, vFueraRango));
        }

    }

    private boolean estaEnRango(double min, double max, String valor) {
        double valorComparar = Double.parseDouble(valor);
        return (valorComparar >= min && valorComparar <= max) ? true : false;
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
        //String[] pa = ((EditText)findViewById(R.id.edtxtPAGeneralesSint)).getText().toString().split("/");
        String pas = ((EditText) findViewById(R.id.edtxtPASint)).getText().toString();
        String pad = ((EditText) findViewById(R.id.edtxtPADint)).getText().toString();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        //hojaConsulta.setPresion(Integer.parseInt(pa[0] + pa[1]));
        hojaConsulta.setPas(Short.parseShort(pas));
        hojaConsulta.setPad(Short.parseShort(pad));

        hojaConsulta.setFciaCard(Integer.parseInt(((EditText) findViewById(R.id.edtxtFciaCardGeneralesSint)).getText().toString()));
        hojaConsulta.setFciaResp(Integer.parseInt(((EditText) findViewById(R.id.edtxtFciaRespGeneralesSint)).getText().toString()));

        if (((CheckBox) findViewById(R.id.chkbLugarAtenCSSFVGeneralesSint)).isChecked())
            hojaConsulta.setLugarAtencion("CS SFV");
        else if (((CheckBox) findViewById(R.id.chkbLugarAtenTerrenoGeneralesSint)).isChecked())
            hojaConsulta.setLugarAtencion("Terreno");

        if (((CheckBox) findViewById(R.id.chkbConsultaInicialGeneralesSint)).isChecked())
            hojaConsulta.setConsulta("Inicial");
        else if (((CheckBox) findViewById(R.id.chkbConsultaConvGeneralesSint)).isChecked())
            hojaConsulta.setConsulta("Convaleciente");
        else if (((CheckBox) findViewById(R.id.chkbConsultaSeguimGeneralesSint)).isChecked())
            hojaConsulta.setConsulta("Seguimiento");

        if (((CheckBox) findViewById(R.id.chkbSegChk1GeneralesSint)).isChecked()) {
            hojaConsulta.setSegChick("1");
        } else if (((CheckBox) findViewById(R.id.chkbSegChk2GeneralesSint)).isChecked()) {
            hojaConsulta.setSegChick("2");
        } else if (((CheckBox) findViewById(R.id.chkbSegChk3GeneralesSint)).isChecked()) {
            hojaConsulta.setSegChick("3");
        } else if (((CheckBox) findViewById(R.id.chkbSegChk4GeneralesSint)).isChecked()) {
            hojaConsulta.setSegChick("4");
        } else if (((CheckBox) findViewById(R.id.chkbSegChk5GeneralesSint)).isChecked()) {
            hojaConsulta.setSegChick("5");
        } else{
            hojaConsulta.setSegChick(null);
        }

        if (((CheckBox) findViewById(R.id.chkbRegularGeneralesSint)).isChecked()) {
            hojaConsulta.setTurno("1");
        } else if (((CheckBox) findViewById(R.id.chkbNocheGeneralesSint)).isChecked()) {
            hojaConsulta.setTurno("2");
        } else if (((CheckBox) findViewById(R.id.chkbFindeGeneralesSint)).isChecked()) {
            hojaConsulta.setTurno("3");
        }

        hojaConsulta.setTemMedc(Double.parseDouble(((EditText) findViewById(R.id.edtxtTempMedGeneralesSint)).getText().toString()));

        if(!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.dpFif)).getText().toString())){
            hojaConsulta.setFif(((EditText) findViewById(R.id.dpFif)).getText().toString());
        }

        if(!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.dpFis)).getText().toString())) {
            hojaConsulta.setFis(((EditText) findViewById(R.id.dpFis)).getText().toString());
        }

        if (!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.dpUltmFiebGeneralesSint)).getText().toString())) {
            hojaConsulta.setUltDiaFiebre(((EditText) findViewById(R.id.dpUltmFiebGeneralesSint)).getText().toString());
        }

        if(((CheckBox)findViewById(R.id.chkbAMUltFGeneralesSint)).isChecked()) {
            hojaConsulta.setAmPmUltDiaFiebre("AM");
        } else if(((CheckBox)findViewById(R.id.chkbPMUltFGeneralesSint)).isChecked()) {
            hojaConsulta.setAmPmUltDiaFiebre("PM");
        }

        if(!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.dpUltmDosGeneralesSint)).getText().toString())) {
            hojaConsulta.setUltDosisAntipiretico(((EditText) findViewById(R.id.dpUltmDosGeneralesSint)).getText().toString());
        }

        if(!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtHoraGeneralesSint)).getText().toString())) {
            hojaConsulta.setHoraUltDosisAntipiretico(((EditText) findViewById(R.id.edtxtHoraGeneralesSint)).getText().toString());
        }

        /*if(((CheckBox)findViewById(R.id.chkbAMUltDGeneralesSint)).isChecked()) {
            hojaConsulta.setAmPmUltDosisAntipiretico("AM");
        } else if(((CheckBox)findViewById(R.id.chkbPMUltDGeneralesSint)).isChecked()) {
            hojaConsulta.setAmPmUltDosisAntipiretico("PM");
        }*/


        if(mAmPm != null && mAmPm.booleanValue() == true) {
            hojaConsulta.setAmPmUltDosisAntipiretico("AM");
        } else if(mAmPm != null && mAmPm.booleanValue() == false) {
            hojaConsulta.setAmPmUltDosisAntipiretico("PM");
        }

        return hojaConsulta;
    }

    /***
     * Metodo para Validar si la hoja de consulta a sufrido cambios.
     * @param hojaConsulta, Hoja de consulta.
     * @return si tiene cambio regresa True en caso contrario false.
     */
    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if ((mCorrienteGeneralesSintomas.getPas() == null && hojaConsulta.getPas() != null) ||
                (mCorrienteGeneralesSintomas.getPad() == null && hojaConsulta.getPad() != null) ||
                (mCorrienteGeneralesSintomas.getLugarAtencion() == null && hojaConsulta.getLugarAtencion() != null) ||
                (mCorrienteGeneralesSintomas.getConsulta() == null && hojaConsulta.getConsulta() != null) ||
                (mCorrienteGeneralesSintomas.getSegChick() == null && hojaConsulta.getSegChick() != null) ||
                (mCorrienteGeneralesSintomas.getTurno() == null && hojaConsulta.getTurno() != null) ||
                (mCorrienteGeneralesSintomas.getTemMedc() == null && hojaConsulta.getTemMedc() != null) ||
                (mCorrienteGeneralesSintomas.getFis() == null && hojaConsulta.getFis() != null) ||
                (mCorrienteGeneralesSintomas.getFif() == null && hojaConsulta.getFif() != null) ||
                (mCorrienteGeneralesSintomas.getUltDiaFiebre() == null && hojaConsulta.getUltDiaFiebre() != null) ||
                (mCorrienteGeneralesSintomas.getUltDosisAntipiretico() == null && hojaConsulta.getUltDosisAntipiretico() != null) ||
                (mCorrienteGeneralesSintomas.getAmPmUltDiaFiebre() == null && hojaConsulta.getAmPmUltDiaFiebre() != null) ||
                (mCorrienteGeneralesSintomas.getHoraUltDosisAntipiretico() == null && hojaConsulta.getHoraUltDosisAntipiretico() != null) ||
                (mCorrienteGeneralesSintomas.getAmPmUltDosisAntipiretico() == null && hojaConsulta.getAmPmUltDosisAntipiretico() != null)) {
            return true;
        }

        if ((mCorrienteGeneralesSintomas.getPas() != null && hojaConsulta.getPas() == null) ||
                (mCorrienteGeneralesSintomas.getPad() != null && hojaConsulta.getPad() == null) ||
                (mCorrienteGeneralesSintomas.getLugarAtencion() != null && hojaConsulta.getLugarAtencion() == null) ||
                (mCorrienteGeneralesSintomas.getConsulta() != null && hojaConsulta.getConsulta() == null) ||
                (mCorrienteGeneralesSintomas.getSegChick() != null && hojaConsulta.getSegChick() == null) ||
                (mCorrienteGeneralesSintomas.getTurno() != null && hojaConsulta.getTurno() == null) ||
                (mCorrienteGeneralesSintomas.getTemMedc() != null && hojaConsulta.getTemMedc() == null) ||
                (mCorrienteGeneralesSintomas.getFis() != null && hojaConsulta.getFis() == null) ||
                (mCorrienteGeneralesSintomas.getFif() != null && hojaConsulta.getFif() == null) ||
                (mCorrienteGeneralesSintomas.getUltDiaFiebre() != null && hojaConsulta.getUltDiaFiebre() == null) ||
                (mCorrienteGeneralesSintomas.getUltDosisAntipiretico() != null && hojaConsulta.getUltDosisAntipiretico() == null) ||
                (mCorrienteGeneralesSintomas.getAmPmUltDiaFiebre() != null && hojaConsulta.getAmPmUltDiaFiebre() == null) ||
                (mCorrienteGeneralesSintomas.getHoraUltDosisAntipiretico() != null && hojaConsulta.getHoraUltDosisAntipiretico() == null) ||
                (mCorrienteGeneralesSintomas.getAmPmUltDosisAntipiretico() != null && hojaConsulta.getAmPmUltDosisAntipiretico() == null)) {
            return true;
        }

        if ((mCorrienteGeneralesSintomas.getPas().compareTo(hojaConsulta.getPas()) != 0) ||
                (mCorrienteGeneralesSintomas.getPad().compareTo(hojaConsulta.getPad()) != 0) ||
                (mCorrienteGeneralesSintomas.getFciaResp().intValue() != hojaConsulta.getFciaResp()) ||
                (mCorrienteGeneralesSintomas.getFciaCard().intValue() != hojaConsulta.getFciaCard()) ||
                (mCorrienteGeneralesSintomas.getLugarAtencion().compareTo(hojaConsulta.getLugarAtencion()) != 0) ||
                (mCorrienteGeneralesSintomas.getConsulta().compareTo(hojaConsulta.getConsulta()) != 0) ||
                (mCorrienteGeneralesSintomas.getSegChick().charValue() != hojaConsulta.getSegChick().charAt(0)) ||
                (mCorrienteGeneralesSintomas.getTurno().charValue() != hojaConsulta.getTurno().charAt(0)) ||
                (mCorrienteGeneralesSintomas.getTemMedc().compareTo(hojaConsulta.getTemMedc()) != 0) ||
                (mCorrienteGeneralesSintomas.getFis().compareTo(hojaConsulta.getFis()) != 0) ||
                (mCorrienteGeneralesSintomas.getFif().compareTo(hojaConsulta.getFif()) != 0) ||
                (mCorrienteGeneralesSintomas.getUltDiaFiebre().compareTo(hojaConsulta.getUltDiaFiebre()) != 0) ||
                (mCorrienteGeneralesSintomas.getUltDosisAntipiretico().compareTo(hojaConsulta.getUltDosisAntipiretico()) != 0) ||
                (mCorrienteGeneralesSintomas.getHoraUltDosisAntipiretico().compareTo(hojaConsulta.getHoraUltDosisAntipiretico()) != 0) ||
                (mCorrienteGeneralesSintomas.getAmPmUltDosisAntipiretico().compareTo(hojaConsulta.getAmPmUltDosisAntipiretico()) != 0)) {
            return true;
        }

        return false;
    }

    private void regresarPantallaAnterior() {
        Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
        intent.putExtra("indice",0);
        intent.putExtra("pacienteSeleccionado", mPacienteSeleccionado);

        startActivity(intent);
        finish();
    }

    public class HorarioAtencionTask extends AsyncTask<Void, Void, Boolean> {

        private final Context CONTEXT;
        private final Activity ACTIVITY;
        private ResultadoListWSDTO<HorarioAtencionDTO> RESPUESTA;
        private ResultadoObjectWSDTO<GeneralesSintomasDTO> RESULTADO_CARGA_DATOS;
        private final ConnectivityManager CM;
        private final NetworkInfo NET_INFO;


        HorarioAtencionTask(Context context, Activity activity) {
            this.CONTEXT = context;
            this.ACTIVITY = activity;
            this.RESPUESTA = new ResultadoListWSDTO<HorarioAtencionDTO>();
            this.RESULTADO_CARGA_DATOS = new ResultadoObjectWSDTO<GeneralesSintomasDTO>();
            this.CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            this.NET_INFO = this.CM.getActiveNetworkInfo();
        }

        @Override
        protected void onPreExecute() {
            PD_CREATE = new ProgressDialog(CONTEXT);
            PD_CREATE.setTitle(getResources().getString(R.string.title_obteniendo));
            PD_CREATE.setMessage(getResources().getString(R.string.msj_espere_por_favor));
            PD_CREATE.setCancelable(false);
            PD_CREATE.setIndeterminate(true);
            PD_CREATE.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (this.NET_INFO != null && this.NET_INFO.isConnected()) {

                SintomasWS sintomasWS = new SintomasWS(getResources());

                Calendar hoy = Calendar.getInstance();

                this.RESPUESTA = sintomasWS.obtenerHorarioAtencion(hoy.get(Calendar.DAY_OF_WEEK));

                if(this.RESPUESTA.getCodigoError().intValue() == 1
                        || this.RESPUESTA.getCodigoError().intValue() == 0){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    this.RESULTADO_CARGA_DATOS = sintomasWS.obtenerGeneralesSintomas(hojaConsulta);
                }

            } else {
                this.RESPUESTA.setCodigoError(Long.parseLong("3"));
                this.RESPUESTA.setMensajeError("");
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                PD_CREATE.dismiss();
                if(this.RESPUESTA.getCodigoError().intValue() == 0
                        || this.RESPUESTA.getCodigoError().intValue() == 1){ // Sin errores
                    cargarValores();
                }else {
                    System.out.println(new StringBuffer().append(this.RESPUESTA.getCodigoError()).
                            append(" --- ").append(this.RESPUESTA.getMensajeError()).toString());
                }
            }
        }

        protected void cargarValores() {
            try {
                GeneralesSintomasDTO generalesSintomas = RESULTADO_CARGA_DATOS.getObjecRespuesta();
                mCorrienteGeneralesSintomas = RESULTADO_CARGA_DATOS.getObjecRespuesta();
                /*if(generalesSintomas.getPresion() != null) {
                    if(generalesSintomas.getPresion().toString().length() < 6) {
                        ((EditText)findViewById(R.id.edtxtPAGeneralesSint)).setText("0"+generalesSintomas.getPresion().toString());
                    } else {
                        ((EditText)findViewById(R.id.edtxtPAGeneralesSint)).setText(generalesSintomas.getPresion().toString());
                    }
                }*/
                if(generalesSintomas == null) {
                    generalesSintomas = new GeneralesSintomasDTO();
                }

                cCtrC.setCodExpediente(generalesSintomas.getCodExpediente());
                cCtrC.setNumHojaConsulta(generalesSintomas.getNumHojaConsulta());

                if(generalesSintomas.getPas() != null) {
                    ((EditText) findViewById(R.id.edtxtPASint)).setText(generalesSintomas.getPas().toString());
                }

                if(generalesSintomas.getPad() != null) {
                    ((EditText) findViewById(R.id.edtxtPADint)).setText(generalesSintomas.getPad().toString());
                }

                if (generalesSintomas.getFciaResp() != null) {
                    ((EditText) findViewById(R.id.edtxtFciaRespGeneralesSint)).setText(generalesSintomas.getFciaResp().toString());
                }

                if (generalesSintomas.getFciaCard() != null) {
                    ((EditText) findViewById(R.id.edtxtFciaCardGeneralesSint)).setText(generalesSintomas.getFciaCard().toString());
                }

                if (generalesSintomas.getTurno() != null) {
                    if (generalesSintomas.getTurno().charValue() == '1') {
                        ((CheckBox) findViewById(R.id.chkbRegularGeneralesSint)).setChecked(true);
                    } else if (generalesSintomas.getTurno().charValue() == '2') {
                        ((CheckBox) findViewById(R.id.chkbNocheGeneralesSint)).setChecked(true);
                    } else {
                        ((CheckBox) findViewById(R.id.chkbFindeGeneralesSint)).setChecked(true);
                    }
                } else {
                    Calendar hoy = Calendar.getInstance();
                    for (HorarioAtencionDTO horarioAtencionDTO : this.RESPUESTA.getLstResultado()) {
                        Calendar fechaInicio = Calendar.getInstance();
                        Calendar fechaFin = Calendar.getInstance();
                        SimpleDateFormat sdfHoy = new SimpleDateFormat("yyyyMMdd");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd KK:mm a",
                                Locale.ENGLISH);
                        fechaInicio.setTime(sdf.parse(sdfHoy.format(hoy.getTime()) + " " + horarioAtencionDTO.getHoraInicio()));
                        fechaFin.setTime(sdf.parse(sdfHoy.format(hoy.getTime()) + " " + horarioAtencionDTO.getHoraFin()));

                        if ((hoy.after(fechaInicio) || hoy.equals(fechaInicio)) && (hoy.before(fechaFin) || hoy.equals(fechaFin))) {
                            if (horarioAtencionDTO.getTurno().compareTo("1") == 0)
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkbRegularGeneralesSint)).setChecked(true);
                            else if (horarioAtencionDTO.getTurno().compareTo("2") == 0)
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkbNocheGeneralesSint)).setChecked(true);
                            else
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkbFindeGeneralesSint)).setChecked(true);
                            break;
                        }

                    }
                }

                cargarCheckBox(generalesSintomas);

                if (generalesSintomas.getTemMedc() != null) {
                    ((EditText) findViewById(R.id.edtxtTempMedGeneralesSint)).setText(generalesSintomas.getTemMedc().toString());
                }

                if (!StringUtils.isNullOrEmpty(generalesSintomas.getFif())) {
                    ((EditText) findViewById(R.id.dpFif)).setText(generalesSintomas.getFif());
                }

                if(!StringUtils.isNullOrEmpty(generalesSintomas.getFis())) {
                    ((EditText) findViewById(R.id.dpFis)).setText(generalesSintomas.getFis());
                }

                if (!StringUtils.isNullOrEmpty(generalesSintomas.getUltDiaFiebre())){
                    ((EditText) findViewById(R.id.dpUltmFiebGeneralesSint)).setText(generalesSintomas.getUltDiaFiebre());
                }

                if(!StringUtils.isNullOrEmpty(generalesSintomas.getUltDosisAntipiretico()) ) {
                    ((EditText) findViewById(R.id.dpUltmDosGeneralesSint)).setText(generalesSintomas.getUltDosisAntipiretico());
                }

                if(!StringUtils.isNullOrEmpty(generalesSintomas.getHoraUltDosisAntipiretico())) {
                    ((EditText) findViewById(R.id.edtxtHoraGeneralesSint)).setText(generalesSintomas.getHoraUltDosisAntipiretico());
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        protected void cargarCheckBox(GeneralesSintomasDTO generalesSintomas) {
            if(!StringUtils.isNullOrEmpty(generalesSintomas.getLugarAtencion())) {
                if (generalesSintomas.getLugarAtencion().compareTo("Terreno") == 0) {
                    ((CheckBox) findViewById(R.id.chkbLugarAtenTerrenoGeneralesSint)).setChecked(true);
                } else {
                    ((CheckBox) findViewById(R.id.chkbLugarAtenCSSFVGeneralesSint)).setChecked(true);
                }
            }

            if(!StringUtils.isNullOrEmpty(generalesSintomas.getConsulta())) {
                if(generalesSintomas.getConsulta().compareTo("Inicial") == 0) {
                    ((CheckBox)findViewById(R.id.chkbConsultaInicialGeneralesSint)).setChecked(true);
                } else if(generalesSintomas.getConsulta().compareTo("Convaleciente") == 0) {
                    ((CheckBox)findViewById(R.id.chkbConsultaConvGeneralesSint)).setChecked(true);
                } else {
                    ((CheckBox)findViewById(R.id.chkbConsultaSeguimGeneralesSint)).setChecked(true);
                }
            }

            if(generalesSintomas.getSegChick() != null) {
                if(generalesSintomas.getSegChick().charValue() == '1') {
                    ((CheckBox)findViewById(R.id.chkbSegChk1GeneralesSint)).setChecked(true);
                } else if(generalesSintomas.getSegChick().charValue() == '2') {
                    ((CheckBox)findViewById(R.id.chkbSegChk2GeneralesSint)).setChecked(true);
                } else if(generalesSintomas.getSegChick().charValue() == '3') {
                    ((CheckBox)findViewById(R.id.chkbSegChk3GeneralesSint)).setChecked(true);
                } else if(generalesSintomas.getSegChick().charValue() == '4') {
                    ((CheckBox)findViewById(R.id.chkbSegChk4GeneralesSint)).setChecked(true);
                } else {
                    ((CheckBox)findViewById(R.id.chkbSegChk5GeneralesSint)).setChecked(true);
                }
            }

            if(!StringUtils.isNullOrEmpty(generalesSintomas.getAmPmUltDiaFiebre())) {
                if(generalesSintomas.getAmPmUltDiaFiebre().compareTo("AM") == 0) {
                    ((CheckBox) findViewById(R.id.chkbAMUltFGeneralesSint)).setChecked(true);
                } else {
                    ((CheckBox) findViewById(R.id.chkbPMUltFGeneralesSint)).setChecked(true);
                }
            }

            /*if(!StringUtils.isNullOrEmpty(generalesSintomas.getAmPmUltDosisAntipiretico())) {
                if(generalesSintomas.getAmPmUltDosisAntipiretico().compareTo("AM") == 0) {
                    ((CheckBox) findViewById(R.id.chkbAMUltDGeneralesSint)).setChecked(true);
                } else {
                    ((CheckBox) findViewById(R.id.chkbPMUltDGeneralesSint)).setChecked(true);
                }
            }*/
        }

        @Override
        protected void onCancelled() {
        }
    }

    private void guardarGeneralesServicio(){
        AsyncTask<Void, Void, Void> guardarGeneralesTask = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private SintomasWS SINTOMASWS = new SintomasWS(getResources());
            private ControlCambiosWS CTRCAMBIOSWS = new ControlCambiosWS(getResources());

            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(CONTEXT);
                PD.setTitle(getResources().getString(R.string.tittle_actualizando));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    if(genControlCambios != null) {
                        genControlCambios.setCodExpediente(cCtrC.getCodExpediente());
                        genControlCambios.setNumHojaConsulta(cCtrC.getNumHojaConsulta());
                        CTRCAMBIOSWS.guardarControlCambios(genControlCambios);
                    }
                    RESPUESTA = SINTOMASWS.guardarGeneralesSintomas(mHojaConsulta, mUsuarioLogiado);
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
                    InicioDTO pacienteSeleccionado = (InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado");

                    Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
                    intent.putExtra("indice",0);
                    intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);

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
        guardarGeneralesTask.execute((Void[])null);
    }
}
