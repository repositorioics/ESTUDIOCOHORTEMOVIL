package com.sts_ni.estudiocohortecssfv.sintomasactivities;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.CategoriaSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.ControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.DatePickerFragment;
import com.sts_ni.estudiocohortecssfv.tools.DecimalDigitsInputFilter;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.ControlCambiosWS;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Controlador de la UI Categoria.
 */
public class CategoriaSintomaActivity extends ActionBarActivity {

    private Context CONTEXT;
    private boolean CATEGORIA_A_B;
    private String CATEGORIA_SELECCIONADA;
    public ProgressDialog PD_CREATE;
    GeneralesControlCambiosDTO genControlCambios = null;
    private String vFueraRango = new String();
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private CategoriaSintomasDTO mCorrienteCategoriaSintomas;
    private String mUsuarioLogiado;
    private TextView viewTxtvNSintomaCat1;
    private TextView viewTxtvSSintomaCat1;
    private TextView viewTxtvNSintomaCat2;
    private TextView viewTxtvSSintomaCat2;
    private TextView viewTxtvNSintomaCat3;
    private TextView viewTxtvSSintomaCat3;

    public AsyncTask<Void, Void, Void> mGuardarSaturacionTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_sintoma);

        this.CONTEXT = this;

        this.CATEGORIA_A_B = false;

        this.CATEGORIA_SELECCIONADA = null;

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        View catPart1 = (View)findViewById(R.id.incCategoriaPart1);

        viewTxtvNSintomaCat1 = (TextView) catPart1.findViewById(R.id.txtvNSintoma);
        viewTxtvSSintomaCat1 = (TextView) catPart1.findViewById(R.id.txtvSSintoma);


        viewTxtvNSintomaCat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 2);
            }
        });

        viewTxtvSSintomaCat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 1);
            }
        });

        View catPart2 = (View)findViewById(R.id.incCategoriaPart2);

        viewTxtvNSintomaCat2 = (TextView) catPart2.findViewById(R.id.txtvNSintoma);
        viewTxtvSSintomaCat2 = (TextView) catPart2.findViewById(R.id.txtvSSintoma);


        viewTxtvNSintomaCat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado2(view, 2);
            }
        });

        viewTxtvSSintomaCat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado2(view, 1);
            }
        });

        View catPart3 = (View)findViewById(R.id.incCategoriaPart3);

        viewTxtvNSintomaCat3 = (TextView) catPart3.findViewById(R.id.txtvNSintoma);
        viewTxtvSSintomaCat3 = (TextView) catPart3.findViewById(R.id.txtvSSintoma);


        viewTxtvNSintomaCat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado3(view, 2);
            }
        });

        viewTxtvSSintomaCat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado3(view, 1);
            }
        });

        inicializarComponentes();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelarLlamarServicioTask();
    }

    /***
     * Metodo que es ejecutado en el evento onClick de la eqitueta no o si
     * @param view
     */
    public void SintomaMarcado(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) findViewById(R.id.chkbMAHSSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPTPSSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPTQSSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPTTSSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPEFSSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPLESSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) findViewById(R.id.chkbMAHSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbPTPSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbPTQSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbPTTSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbPEFSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbPLESSintoma)).setChecked(true);



                            ((CheckBox) findViewById(R.id.chkbMAHNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPTPNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPTQNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPTTNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPEFNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPLENSintoma)).setChecked(false);

                            if(valor==2) ((CheckBox) findViewById(R.id.chkbMAHNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbPTPNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbPTQNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbPTTNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbPEFNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbPLENSintoma)).setChecked(true);


                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.label_sintoma));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.label_sintoma));


            MensajesHelper.mostrarMensajeYesNo(this,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    /***
     * Metodo que es ejecutado en el evento onClick de la eqitueta no o si
     * @param view
     */
    public void SintomaMarcado2(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) findViewById(R.id.chkbEPISSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbGNGSSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPTSSSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbLLCSSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbCAISSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) findViewById(R.id.chkbEPISSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbGNGSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbPTSSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbLLCSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbCAISSintoma)).setChecked(true);

                            ((CheckBox) findViewById(R.id.chkbEPINSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbGNGNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbPTSNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbLLCNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbCAINSintoma)).setChecked(false);

                            if(valor==2) ((CheckBox) findViewById(R.id.chkbEPINSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbGNGNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbPTSNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbLLCNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbCAINSintoma)).setChecked(true);


                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.label_sintoma));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.label_sintoma));


            MensajesHelper.mostrarMensajeYesNo(this,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    /***
     * Metodo que es ejecutado en el evento onClick de la eqitueta no o si
     * @param view
     */
    public void SintomaMarcado3(View view, final int valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            ((CheckBox) findViewById(R.id.chkbHPESSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbHMTSSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbMLNSSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbHMCSSintoma)).setChecked(false);

                            if(valor==1) ((CheckBox) findViewById(R.id.chkbHPESSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbHMTSSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbMLNSSintoma)).setChecked(true);
                            if(valor==1) {
                                ((CheckBox) findViewById(R.id.chkbHMCSSintoma)).setChecked(true);
                                findViewById(R.id.edtxtHMC).setEnabled(true);
                            }

                            ((CheckBox) findViewById(R.id.chkbHPENSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbHMTNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbMLNNSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbHMCNSintoma)).setChecked(false);

                            if(valor==2) ((CheckBox) findViewById(R.id.chkbHPENSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbHMTNSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbMLNNSintoma)).setChecked(true);
                            if(valor==2) {
                                ((CheckBox) findViewById(R.id.chkbHMCNSintoma)).setChecked(true);
                                ((EditText)findViewById(R.id.edtxtHMC)).setText("");
                                findViewById(R.id.edtxtHMC).setEnabled(false);
                            }


                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.label_sintoma));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.label_sintoma));


            MensajesHelper.mostrarMensajeYesNo(this,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void onChkboxClickedCategoria(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkbCategoriaA:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbCategoriaB)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaC)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaD)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaNA)).setChecked(false);
                    this.CATEGORIA_A_B = true;
                    this.CATEGORIA_SELECCIONADA = "A";
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbCategoriaB:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbCategoriaA)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaC)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaD)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaNA)).setChecked(false);
                    this.CATEGORIA_A_B = true;
                    this.CATEGORIA_SELECCIONADA = "B";
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkbCategoriaC:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbCategoriaA)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaB)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaD)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaNA)).setChecked(false);
                    this.CATEGORIA_A_B = false;
                    this.CATEGORIA_SELECCIONADA = "C";
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkbCategoriaD:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbCategoriaA)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaB)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaC)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaNA)).setChecked(false);
                    this.CATEGORIA_A_B = false;
                    this.CATEGORIA_SELECCIONADA = "D";
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkbCategoriaNA:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkbCategoriaA)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaB)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaC)).setChecked(false);
                    ((CheckBox) findViewById(R.id.chkbCategoriaD)).setChecked(false);
                    this.CATEGORIA_A_B = false;
                    this.CATEGORIA_SELECCIONADA = "NA";
                }
                else
                    ((CheckBox) view).setChecked(true);
                break;
        }
    }

    public void onClick_btnRegresar(View view) {
        try {
            ArrayList<ControlCambiosDTO> controlCambios = new ArrayList<ControlCambiosDTO>();
            genControlCambios = null;

            vFueraRango = "";
            if (validarCampos(controlCambios)) {
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
                                    enviarHojaConsulta();
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
                }  else {
                    enviarHojaConsulta();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void enviarHojaConsulta() {
        mHojaConsulta = cargarHojaConsulta();
        if (mPacienteSeleccionado.getCodigoEstado() == '7') {
            if(mCorrienteCategoriaSintomas != null) {
                if(tieneCambiosHojaConsulta(mHojaConsulta)) {
                    DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    llamarGuardadoServicio();
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
            llamarGuardadoServicio();
        }
    }

    public void onChkboxClickedCCSiNo(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbCambioCategoriaSi), findViewById(R.id.chkbCambioCategoriaNo),
                view);
    }

    /************************* Categoria Part 1 ***********************************************************************************/

    public void onChkboxClickedMAH(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbMAHSSintoma), findViewById(R.id.chkbMAHNSintoma),
                view);
    }

    public void onChkboxClickedPTP(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbPTPSSintoma), findViewById(R.id.chkbPTPNSintoma),
                view);
    }

    public void onChkboxClickedPTQ(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbPTQSSintoma), findViewById(R.id.chkbPTQNSintoma),
                view);
    }

    public void onChkboxClickedPTT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbPTTSSintoma), findViewById(R.id.chkbPTTNSintoma),
                view);
    }

    public void onChkboxClickedPEF(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbPEFSSintoma), findViewById(R.id.chkbPEFNSintoma),
                view);
    }

    public void onChkboxClickedPLE(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbPLESSintoma), findViewById(R.id.chkbPLENSintoma),
                view);
    }

    /************************* Categoria Part 2 ***********************************************************************************/

    public void onChkboxClickedEPI(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbEPISSintoma), findViewById(R.id.chkbEPINSintoma),
                view);
    }

    public void onChkboxClickedGNG(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbGNGSSintoma), findViewById(R.id.chkbGNGNSintoma),
                view);
    }

    public void onChkboxClickedPTS(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbPTSSSintoma), findViewById(R.id.chkbPTSNSintoma),
                view);
    }

    public void onChkboxClickedLLC(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbLLCSSintoma), findViewById(R.id.chkbLLCNSintoma),
                view);
    }

    public void onChkboxClickedCAI(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbCAISSintoma), findViewById(R.id.chkbCAINSintoma),
                view);
    }

    /************************* Categoria Part 3 ***********************************************************************************/

    public void onChkboxClickedHPE(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbHPESSintoma), findViewById(R.id.chkbHPENSintoma),
                findViewById(R.id.chkbHPEDSintoma), view);
    }

    public void onChkboxClickedHMT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbHMTSSintoma), findViewById(R.id.chkbHMTNSintoma),
                view);
    }

    public void onChkboxClickedMLN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbMLNSSintoma), findViewById(R.id.chkbMLNNSintoma),
                view);
    }

    public void onChkboxClickedHMC(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbHMCSSintoma), findViewById(R.id.chkbHMCNSintoma),
                findViewById(R.id.chkbHMCDSintoma), view);
        if(view.getId() == R.id.chkbHMCSSintoma){
            findViewById(R.id.edtxtHMC).setEnabled(true);
        } else{
            ((EditText)findViewById(R.id.edtxtHMC)).setText("");
            findViewById(R.id.edtxtHMC).setEnabled(false);
        }
    }

    /************************* Categoria Part 4 ***********************************************************************************/

    public void onChkboxClickedHSH(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbHSHSSintoma), findViewById(R.id.chkbHSHNSintoma),
                view);
        if(view.getId() == R.id.chkbHSHSSintoma) {
            findViewById(R.id.edtxtSIH).setEnabled(true);
        } else {
            findViewById(R.id.edtxtSIH).setEnabled(false);
            ((EditText)findViewById(R.id.edtxtSIH)).setText("");
        }
    }

    public void onChkboxClickedRTS(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRTSSSintoma), findViewById(R.id.chkbRTSNSintoma),
                view);
        if(view.getId() == R.id.chkbRTSSSintoma) {
            findViewById(R.id.edtxtSIR).setEnabled(true);
        } else {
            findViewById(R.id.edtxtSIR).setEnabled(false);
            ((EditText)findViewById(R.id.edtxtSIR)).setText("");
        }
    }

    public void onChkboxClickedTMM(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbTMMSSintoma), findViewById(R.id.chkbTMMNSintoma),
                view);
        if(view.getId() == R.id.chkbTMMSSintoma) {
            findViewById(R.id.edtxtSIT).setEnabled(true);
        } else {
            findViewById(R.id.edtxtSIT).setEnabled(false);
            ((EditText)findViewById(R.id.edtxtSIT)).setText("");
        }
    }

    public void onChkboxClickedTMD(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbTMDSSintoma), findViewById(R.id.chkbTMDNSintoma),
                view);
        if(view.getId() == R.id.chkbTMDSSintoma) {
            findViewById(R.id.edtxtSIU).setEnabled(true);
        } else {
            findViewById(R.id.edtxtSIU).setEnabled(false);
            ((EditText)findViewById(R.id.edtxtSIU)).setText("");
        }
    }

    public void inicializarComponentes() {
        double pesoKg = Double.parseDouble(getIntent().getStringExtra("pesoKgPaciente"));
        double tallaCmPaciente = Double.parseDouble(getIntent().getStringExtra("tallaCmPaciente"));

        double imc = pesoKg / (tallaCmPaciente / 100.00) * 2.00;

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.00", otherSymbols);

        findViewById(R.id.dpFECC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogFCC(v);
            }
        });

        ((EditText) findViewById(R.id.dpFECC)).setKeyListener(null);

        ((EditText) findViewById(R.id.edtxtLNF)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        /*((EditText)findViewById(R.id.edtxtimc)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5,2)});

        ((EditText)findViewById(R.id.edtxtimc)).setText(df.format(imc));*/

        llamarValidacionConsenDengueServicio(((InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado")).getCodExpediente());
    }

    public void showDatePickerDialogFCC(View view) {
        DialogFragment newFragment = new DatePickerFragment(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)getActivity().findViewById(R.id.dpFECC)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    ((EditText) getActivity().findViewById(R.id.dpFECC)).setError(null);
                    ((EditText) getActivity().findViewById(R.id.dpFECC)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        };
        newFragment.show(getSupportFragmentManager(), getString(R.string.title_date_picker));
    }

    public boolean validarCampos(ArrayList<ControlCambiosDTO> controlCambios) throws Exception {

        /*if(StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.edtxtimc)).getText().toString())){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else */
        /*if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbCategoriaA), findViewById(R.id.chkbCategoriaB)
                , findViewById(R.id.chkbCategoriaC), findViewById(R.id.chkbCategoriaD), findViewById(R.id.chkbCategoriaNA))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
            //el cambio de categoría ya no es obligatorio
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbCambioCategoriaSi), findViewById(R.id.chkbCambioCategoriaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbHSHSSintoma), findViewById(R.id.chkbHSHNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRTSSSintoma), findViewById(R.id.chkbRTSNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbTMMSSintoma), findViewById(R.id.chkbTMMNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbTMDSSintoma), findViewById(R.id.chkbTMDNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }*/

        //Validar Casillas Si especifique no requerido
        if(((CheckBox)findViewById(R.id.chkbHSHSSintoma)).isChecked()){
            if(StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.edtxtSIH)).getText().toString())){
                throw new Exception(getString(R.string.msj_completar_si_especifique));
            }
        }

        if(((CheckBox)findViewById(R.id.chkbRTSSSintoma)).isChecked()){
            if(StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.edtxtSIR)).getText().toString())){
                throw new Exception(getString(R.string.msj_completar_si_especifique));
            }
        }

        if(((CheckBox)findViewById(R.id.chkbTMMSSintoma)).isChecked()){
            if(StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.edtxtSIT)).getText().toString())){
                throw new Exception(getString(R.string.msj_completar_si_especifique));
            }
        }

        if(((CheckBox)findViewById(R.id.chkbTMDSSintoma)).isChecked()){
            if(StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.edtxtSIU)).getText().toString())){
                throw new Exception(getString(R.string.msj_completar_si_especifique));
            }
        }

        //Validar si Categoria A o B estan seleccionados
        //if(isCategoriaAB()) {
        //validar sin importar la categoría
        if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbMAHSSintoma), findViewById(R.id.chkbMAHNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbPTPSSintoma), findViewById(R.id.chkbPTPNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbPTQSSintoma), findViewById(R.id.chkbPTQNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbPTTSSintoma), findViewById(R.id.chkbPTTNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbPEFSSintoma), findViewById(R.id.chkbPEFNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbPLESSintoma), findViewById(R.id.chkbPLENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbEPISSintoma), findViewById(R.id.chkbEPINSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbGNGSSintoma), findViewById(R.id.chkbGNGNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbPTSSSintoma), findViewById(R.id.chkbPTSNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbLLCSSintoma), findViewById(R.id.chkbLLCNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbCAISSintoma), findViewById(R.id.chkbCAINSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } /* else if(StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.edtxtLNF)).getText().toString())) {
                throw new Exception(getString(R.string.msj_completar_informacion));
            } */ /*else if(StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.dpFECC)).getText().toString())) {
                throw new Exception(getString(R.string.msj_completar_informacion));
            }*/ else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbHPESSintoma), findViewById(R.id.chkbHPENSintoma),
                findViewById(R.id.chkbHPEDSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbHMTSSintoma), findViewById(R.id.chkbHMTNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbMLNSSintoma), findViewById(R.id.chkbMLNNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));

        }else if (((CheckBox) findViewById(R.id.chkbHMCSSintoma)).isChecked() &&
                StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtHMC)).getText().toString())) {
            throw new Exception(getString(R.string.msj_debe_ingresar_hemoconcentracion));

        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbCategoriaA), findViewById(R.id.chkbCategoriaB),
                findViewById(R.id.chkbCategoriaC), findViewById(R.id.chkbCategoriaD), findViewById(R.id.chkbCategoriaNA))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbHSHSSintoma), findViewById(R.id.chkbHSHNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRTSSSintoma), findViewById(R.id.chkbRTSNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbTMMSSintoma), findViewById(R.id.chkbTMMNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbTMDSSintoma), findViewById(R.id.chkbTMDNSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
        // }

        if(!TextUtils.isEmpty(((EditText) findViewById(R.id.dpFECC)).getText())) {
            Calendar fechaCategoria = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            fechaCategoria.setTime(sdf.parse(((EditText) findViewById(R.id.dpFECC)).getText().toString()));
            InicioDTO pacienteSeleccionado = ((InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado"));

            if (fechaCategoria.before(pacienteSeleccionado.getFechaNac())) {
                throw new Exception(getString(R.string.msj_fecha_menor_fecha_nacimiento));
            }
        }

        int cont = 0;
        if(!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtSaturacion)).getText().toString())) {
            if (!estaEnRango(0, 100, ((EditText) findViewById(R.id.edtxtSaturacion)).getText().toString())) {
                vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_saturacion_o2));
                cont++;
                /*ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
                ctrCambios.setNombreCampo(getResources().getString(R.string.label_saturacion_o2));
                ctrCambios.setValorCampo(((EditText) findViewById(R.id.edtxtSaturacion)).getText().toString());
                ctrCambios.setTipoControl(ctrCambios.discrepancia);
                controlCambios.add(ctrCambios);*/
            }
        }

        if(!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtHMC)).getText().toString())) {
            if (!estaEnRango(0, 75, ((EditText) findViewById(R.id.edtxtHMC)).getText().toString())) {
                vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_hemoconcentracion));
               cont++;
                /*ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
                ctrCambios.setNombreCampo(getResources().getString(R.string.label_hemoconcentracion));
                ctrCambios.setValorCampo(((EditText) findViewById(R.id.edtxtHMC)).getText().toString());
                ctrCambios.setTipoControl(ctrCambios.discrepancia);
                controlCambios.add(ctrCambios);*/
            }
        }

        if (cont > 0){
            throw new Exception(getString(R.string.msj_aviso_control_cambios1,vFueraRango));

        }
        //PRUEBA
        /*String test = (((EditText)findViewById(R.id.edtxtSIU)).getText().toString());
        int longitud = test.length();
        if(((CheckBox)findViewById(R.id.chkbTMDSSintoma)).isChecked()){
            if (longitud > 32) {
                throw new Exception(getString(R.string.tiene_mas_caracteres));
            }
        }*/
        return true;
    }

    private boolean estaEnRango(double min, double max, String valor) {
        double valorComparar = Double.parseDouble(valor);
        return (valorComparar >= min && valorComparar <= max) ? true : false;
    }

    private void llamarValidacionConsenDengueServicio(final Integer codExpediente){
        AsyncTask<Void, Void, Void> validarTask = new AsyncTask<Void, Void, Void>() {
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private boolean RESPUESTA = false;
            private SintomasWS SINTOMASWS;
            private ResultadoObjectWSDTO<CategoriaSintomasDTO> RESPUESTA_CARGA;

            @Override
            protected void onPreExecute() {
                PD_CREATE = new ProgressDialog(CONTEXT);
                PD_CREATE.setTitle(getResources().getString(R.string.title_obteniendo));
                PD_CREATE.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD_CREATE.setCancelable(false);
                PD_CREATE.setIndeterminate(true);
                PD_CREATE.show();
                this.RESPUESTA_CARGA = new ResultadoObjectWSDTO<CategoriaSintomasDTO>();
                this.SINTOMASWS = new SintomasWS(getResources());
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (this.NET_INFO != null && this.NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    this.RESPUESTA = this.SINTOMASWS.tieneConsetimientoDengue(codExpediente);
                    this.RESPUESTA_CARGA = this.SINTOMASWS.obtenerCategoriasSintomas(hojaConsulta);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                PD_CREATE.dismiss();
                if(this.RESPUESTA_CARGA != null) {
                    CategoriaSintomasDTO categoriaSintomasDTO = this.RESPUESTA_CARGA.getObjecRespuesta();
                    cargarCabezeraPiesDatos(categoriaSintomasDTO);
                    cargarCategoiraAB(categoriaSintomasDTO);
                    mCorrienteCategoriaSintomas = this.RESPUESTA_CARGA.getObjecRespuesta();
                }
            }

            protected void cargarCabezeraPiesDatos(CategoriaSintomasDTO categoriaSintomasDTO) {
                ((EditText)findViewById(R.id.edtxtSaturacion)).setText(((categoriaSintomasDTO.getSaturaciono() != null)
                        ? categoriaSintomasDTO.getSaturaciono().toString() : null));

                /*if(categoriaSintomasDTO.getImc() != null) {
                    ((EditText) findViewById(R.id.edtxtimc)).setText(categoriaSintomasDTO.getImc().toString());
                }*/

                if(!StringUtils.isNullOrEmpty(categoriaSintomasDTO.getCategoria())) {
                    if(categoriaSintomasDTO.getCategoria().trim().compareTo("A") == 0) {
                        ((CheckBox) findViewById(R.id.chkbCategoriaA)).setChecked(true);
                        CATEGORIA_A_B = true;
                        CATEGORIA_SELECCIONADA = "A";
                    } else if(categoriaSintomasDTO.getCategoria().trim().compareTo("B") == 0) {
                        ((CheckBox) findViewById(R.id.chkbCategoriaB)).setChecked(true);
                        CATEGORIA_A_B = true;
                        CATEGORIA_SELECCIONADA = "B";
                    } else if(categoriaSintomasDTO.getCategoria().trim().compareTo("C") == 0) {
                        ((CheckBox) findViewById(R.id.chkbCategoriaC)).setChecked(true);
                        CATEGORIA_A_B = false;
                        CATEGORIA_SELECCIONADA = "C";
                    } else if(categoriaSintomasDTO.getCategoria().trim().compareTo("D") == 0) {
                        ((CheckBox) findViewById(R.id.chkbCategoriaD)).setChecked(true);
                        CATEGORIA_A_B = false;
                        CATEGORIA_SELECCIONADA = "D";
                    } else if(categoriaSintomasDTO.getCategoria().trim().compareTo("NA") == 0) {
                        ((CheckBox) findViewById(R.id.chkbCategoriaNA)).setChecked(true);
                        CATEGORIA_A_B = false;
                        CATEGORIA_SELECCIONADA = "NA";
                    }
                }

                if(categoriaSintomasDTO.getCambioCategoria() != null && !categoriaSintomasDTO.getCambioCategoria().toString().isEmpty()) {
                    AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbCambioCategoriaSi),
                            findViewById(R.id.chkbCambioCategoriaNo), ((categoriaSintomasDTO.getCambioCategoria() != null)
                                    ? categoriaSintomasDTO.getCambioCategoria().charValue() : '4'));
                }

                if(categoriaSintomasDTO.getHospitalizado() != null && !categoriaSintomasDTO.getHospitalizado().toString().isEmpty()) {
                    AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbHSHSSintoma),
                            findViewById(R.id.chkbHSHNSintoma), ((categoriaSintomasDTO.getHospitalizado() != null)
                                    ? categoriaSintomasDTO.getHospitalizado().charValue() : '4'));
                }

                if(categoriaSintomasDTO.getHospitalizado() != null
                        && categoriaSintomasDTO.getHospitalizado().charValue() == '0') {
                    findViewById(R.id.edtxtSIH).setEnabled(true);
                    ((EditText) findViewById(R.id.edtxtSIH)).setText(categoriaSintomasDTO.getHospitalizadoEspecificar());
                }

                if(categoriaSintomasDTO.getTransfucionSangre() != null && !categoriaSintomasDTO.getTransfucionSangre().toString().isEmpty()) {
                    AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRTSSSintoma),
                            findViewById(R.id.chkbRTSNSintoma), ((categoriaSintomasDTO.getTransfucionSangre() != null)
                                    ? categoriaSintomasDTO.getTransfucionSangre().charValue() : '4'));
                }

                if(categoriaSintomasDTO.getTransfucionSangre() != null
                        && categoriaSintomasDTO.getTransfucionSangre().charValue() == '0') {
                    findViewById(R.id.edtxtSIR).setEnabled(true);
                    ((EditText) findViewById(R.id.edtxtSIR)).setText(categoriaSintomasDTO.getTransfuncionEspecificar());
                }

                if(categoriaSintomasDTO.getTomandoMedicamento() != null && !categoriaSintomasDTO.getTomandoMedicamento().toString().isEmpty()) {
                    AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbTMMSSintoma),
                            findViewById(R.id.chkbTMMNSintoma), ((categoriaSintomasDTO.getTomandoMedicamento() != null)
                                    ? categoriaSintomasDTO.getTomandoMedicamento().charValue() : '4'));
                }

                if(categoriaSintomasDTO.getTomandoMedicamento() != null
                        && categoriaSintomasDTO.getTomandoMedicamento().charValue() == '0') {
                    findViewById(R.id.edtxtSIT).setEnabled(true);
                    ((EditText) findViewById(R.id.edtxtSIT)).setText(categoriaSintomasDTO.getTomandoMedicamentoEspecificar());
                }

                if(categoriaSintomasDTO.getMedicamentoDistinto() != null && !categoriaSintomasDTO.getMedicamentoDistinto().toString().isEmpty()) {
                    AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbTMDSSintoma),
                            findViewById(R.id.chkbTMDNSintoma), ((categoriaSintomasDTO.getMedicamentoDistinto() != null)
                                    ? categoriaSintomasDTO.getMedicamentoDistinto().charValue() : '4'));
                }

                if(categoriaSintomasDTO.getMedicamentoDistinto() != null
                        && categoriaSintomasDTO.getMedicamentoDistinto().charValue() == '0') {
                    findViewById(R.id.edtxtSIU).setEnabled(true);
                    ((EditText) findViewById(R.id.edtxtSIU)).setText(categoriaSintomasDTO.getMedicamentoDistintoEspecificar());
                }
            }

            protected void cargarCategoiraAB(CategoriaSintomasDTO categoriaSintomasDTO) {
                /*if(!StringUtils.isNullOrEmpty(categoriaSintomasDTO.getCategoria())
                    &&(categoriaSintomasDTO.getCategoria().trim().compareTo("A") == 0
                        || categoriaSintomasDTO.getCategoria().trim().compareTo("B") == 0)) {*/
                //llenar sin importar la categoría
                //Part 1
                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbMAHSSintoma),
                        findViewById(R.id.chkbMAHNSintoma), ((categoriaSintomasDTO.getManifestacionHemorragica() != null)
                                ? categoriaSintomasDTO.getManifestacionHemorragica().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbPTPSSintoma),
                        findViewById(R.id.chkbPTPNSintoma), ((categoriaSintomasDTO.getPruebaTorniquetePositiva() != null)
                                ? categoriaSintomasDTO.getPruebaTorniquetePositiva().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbPTQSSintoma),
                        findViewById(R.id.chkbPTQNSintoma), ((categoriaSintomasDTO.getPetequiaPt() != null)
                                ? categoriaSintomasDTO.getPetequiaPt().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbPTTSSintoma),
                        findViewById(R.id.chkbPTTNSintoma), ((categoriaSintomasDTO.getPetequiasPt() != null)
                                ? categoriaSintomasDTO.getPetequiasPt().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbPEFSSintoma),
                        findViewById(R.id.chkbPEFNSintoma), ((categoriaSintomasDTO.getPielExtremidadesFrias() != null)
                                ? categoriaSintomasDTO.getPielExtremidadesFrias().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbPLESSintoma),
                        findViewById(R.id.chkbPLENSintoma), ((categoriaSintomasDTO.getPalidezEnExtremidades() != null)
                                ? categoriaSintomasDTO.getPalidezEnExtremidades().charValue() : '4'));
                //Part 2
                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbEPISSintoma),
                        findViewById(R.id.chkbEPINSintoma), ((categoriaSintomasDTO.getEpitaxis() != null)
                                ? categoriaSintomasDTO.getEpitaxis().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbGNGSSintoma),
                        findViewById(R.id.chkbGNGNSintoma), ((categoriaSintomasDTO.getGingivorragia() != null)
                                ? categoriaSintomasDTO.getGingivorragia().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbPTSSSintoma),
                        findViewById(R.id.chkbPTSNSintoma), ((categoriaSintomasDTO.getPeteqiasEspontaneas() != null)
                                ? categoriaSintomasDTO.getPeteqiasEspontaneas().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbLLCSSintoma),
                        findViewById(R.id.chkbLLCNSintoma), ((categoriaSintomasDTO.getLlenadoCapilarseg() != null)
                                ? categoriaSintomasDTO.getLlenadoCapilarseg().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbCAISSintoma),
                        findViewById(R.id.chkbCAINSintoma), ((categoriaSintomasDTO.getCianosis() != null)
                                ? categoriaSintomasDTO.getCianosis().charValue() : '4'));

                ((EditText) findViewById(R.id.edtxtLNF)).setText(((categoriaSintomasDTO.getLinfocitosAtipicos() != null)
                        ? categoriaSintomasDTO.getLinfocitosAtipicos().toString() : null));

                ((EditText) findViewById(R.id.dpFECC)).setText(categoriaSintomasDTO.getFecha());

                //Part 3
                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbHPESSintoma),
                        findViewById(R.id.chkbHPENSintoma), findViewById(R.id.chkbHPEDSintoma),
                        ((categoriaSintomasDTO.getHipermenorrea() != null)
                                ? categoriaSintomasDTO.getHipermenorrea().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbHMTSSintoma),
                        findViewById(R.id.chkbHMTNSintoma), ((categoriaSintomasDTO.getHematemesis() != null)
                                ? categoriaSintomasDTO.getHematemesis().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbMLNSSintoma),
                        findViewById(R.id.chkbMLNNSintoma), ((categoriaSintomasDTO.getMelena() != null)
                                ? categoriaSintomasDTO.getMelena().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbHMCSSintoma),
                        findViewById(R.id.chkbHMCNSintoma), findViewById(R.id.chkbHMCDSintoma),
                        ((categoriaSintomasDTO.getHemoconc() != null)
                                ? categoriaSintomasDTO.getHemoconc().charValue() : '4'));

                ((EditText) findViewById(R.id.edtxtHMC)).setText(((categoriaSintomasDTO.getHemoconcentracion() != null)
                        ? categoriaSintomasDTO.getHemoconcentracion().toString() : null));
            }
        };
        validarTask.execute((Void[])null);
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setSaturaciono2((!StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.edtxtSaturacion)).getText().toString()))
                ? Integer.parseInt(((EditText)findViewById(R.id.edtxtSaturacion)).getText().toString()) : -1);

        //hojaConsulta.setImc(Double.parseDouble(((EditText)findViewById(R.id.edtxtimc)).getText().toString()));

        hojaConsulta.setCategoria((getCategoriaSeleccionada() == null && ((CheckBox)findViewById(R.id.chkbCategoriaNA)).isChecked())
        ? "NA" : getCategoriaSeleccionada());

        if(((CheckBox)findViewById(R.id.chkbCambioCategoriaSi)).isChecked() || ((CheckBox)findViewById(R.id.chkbCambioCategoriaNo)).isChecked()) {
            hojaConsulta.setCambioCategoria(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbCambioCategoriaSi),
                    findViewById(R.id.chkbCambioCategoriaNo)));
        }

        if(((CheckBox)findViewById(R.id.chkbHSHSSintoma)).isChecked() || ((CheckBox)findViewById(R.id.chkbHSHNSintoma)).isChecked()) {
            hojaConsulta.setHospitalizado(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbHSHSSintoma),
                    findViewById(R.id.chkbHSHNSintoma)));
        }

        hojaConsulta.setHospitalizadoEspecificar(((CheckBox)findViewById(R.id.chkbHSHSSintoma)).isChecked()
        ? ((EditText)findViewById(R.id.edtxtSIH)).getText().toString() : "");

        if(((CheckBox)findViewById(R.id.chkbRTSSSintoma)).isChecked() || ((CheckBox)findViewById(R.id.chkbRTSNSintoma)).isChecked()) {
            hojaConsulta.setTransfusionSangre(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRTSSSintoma),
                    findViewById(R.id.chkbRTSNSintoma)));
        }

        hojaConsulta.setTransfusionEspecificar(((CheckBox) findViewById(R.id.chkbRTSSSintoma)).isChecked()
                ? ((EditText) findViewById(R.id.edtxtSIR)).getText().toString() : "");

        if(((CheckBox)findViewById(R.id.chkbTMMSSintoma)).isChecked() || ((CheckBox)findViewById(R.id.chkbTMMNSintoma)).isChecked()) {
            hojaConsulta.setTomandoMedicamento(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbTMMSSintoma),
                    findViewById(R.id.chkbTMMNSintoma)));
        }

        hojaConsulta.setMedicamentoEspecificar(((CheckBox) findViewById(R.id.chkbTMMSSintoma)).isChecked()
                ? ((EditText) findViewById(R.id.edtxtSIT)).getText().toString() : "");

        if(((CheckBox)findViewById(R.id.chkbTMDSSintoma)).isChecked() || ((CheckBox)findViewById(R.id.chkbTMDNSintoma)).isChecked()) {
            hojaConsulta.setMedicamentoDistinto(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbTMDSSintoma),
                    findViewById(R.id.chkbTMDNSintoma)));
        }

        hojaConsulta.setMedicamentoDistEspecificar(((CheckBox) findViewById(R.id.chkbTMDSSintoma)).isChecked()
                ? ((EditText) findViewById(R.id.edtxtSIU)).getText().toString() : "");

        //if(isCategoriaAB()){
        //no importa la categoría
            //Part 1
            hojaConsulta.setManifestacionHemorragica(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbMAHSSintoma),
                    findViewById(R.id.chkbMAHNSintoma)));

            hojaConsulta.setPruebaTorniquetePositiva(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbPTPSSintoma),
                    findViewById(R.id.chkbPTPNSintoma)));

            hojaConsulta.setPetequia10Pt(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbPTQSSintoma),
                    findViewById(R.id.chkbPTQNSintoma)));

            hojaConsulta.setPetequia20Pt(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbPTTSSintoma),
                    findViewById(R.id.chkbPTTNSintoma)));

            hojaConsulta.setPielExtremidadesFrias(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbPEFSSintoma),
                    findViewById(R.id.chkbPEFNSintoma)));

            hojaConsulta.setPalidezEnExtremidades(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbPLESSintoma),
                    findViewById(R.id.chkbPLENSintoma)));
            //Part 2
            hojaConsulta.setEpistaxis(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbEPISSintoma),
                    findViewById(R.id.chkbEPINSintoma)));

            hojaConsulta.setGingivorragia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbGNGSSintoma),
                    findViewById(R.id.chkbGNGNSintoma)));

            hojaConsulta.setPetequiasEspontaneas(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbPTSSSintoma),
                    findViewById(R.id.chkbPTSNSintoma)));

            hojaConsulta.setLlenadoCapilar2seg(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbLLCSSintoma),
                    findViewById(R.id.chkbLLCNSintoma)));

            hojaConsulta.setCianosis(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbCAISSintoma),
                    findViewById(R.id.chkbCAINSintoma)));

            if(!StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.edtxtLNF)).getText().toString())){
                hojaConsulta.setLinfocitosaAtipicos(Double.parseDouble(((EditText) findViewById(R.id.edtxtLNF)).getText().toString()));
            }

            hojaConsulta.setFechaLinfocitos(((EditText)findViewById(R.id.dpFECC)).getText().toString());
            //Part 3
            hojaConsulta.setHipermenorrea(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbHPESSintoma),
                    findViewById(R.id.chkbHPENSintoma), findViewById(R.id.chkbHPEDSintoma)));

            hojaConsulta.setHematemesis(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbHMTSSintoma),
                    findViewById(R.id.chkbHMTNSintoma)));

            hojaConsulta.setMelena(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbMLNSSintoma),
                    findViewById(R.id.chkbMLNNSintoma)));

            hojaConsulta.setHemoconc(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbHMCSSintoma),
                    findViewById(R.id.chkbHMCNSintoma), findViewById(R.id.chkbHMCDSintoma)));

        if(!StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtHMC)).getText().toString())) {
            hojaConsulta.setHemoconcentracion(Short.parseShort(((EditText)findViewById(R.id.edtxtHMC)).getText().toString()));
        }
        //}

        return hojaConsulta;
    }

    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if ((mCorrienteCategoriaSintomas.getSaturaciono() == null && hojaConsulta.getSaturaciono2() != null) ||
                (mCorrienteCategoriaSintomas.getCategoria() == null && hojaConsulta.getCategoria() != null) ||
                (mCorrienteCategoriaSintomas.getCambioCategoria() == null && hojaConsulta.getCambioCategoria() != null) ||
                (mCorrienteCategoriaSintomas.getManifestacionHemorragica() == null && hojaConsulta.getManifestacionHemorragica() != null) ||
                (mCorrienteCategoriaSintomas.getPruebaTorniquetePositiva() == null && hojaConsulta.getPruebaTorniquetePositiva() != null) ||
                (mCorrienteCategoriaSintomas.getPetequiaPt() == null && hojaConsulta.getPetequia10Pt() != null) ||
                (mCorrienteCategoriaSintomas.getPetequiasPt() == null && hojaConsulta.getPetequia20Pt() != null) ||
                (mCorrienteCategoriaSintomas.getPielExtremidadesFrias() == null && hojaConsulta.getPielExtremidadesFrias() != null) ||
                (mCorrienteCategoriaSintomas.getPalidezEnExtremidades() == null && hojaConsulta.getPalidezEnExtremidades() != null) ||
                (mCorrienteCategoriaSintomas.getEpitaxis() == null && hojaConsulta.getEpistaxis() != null) ||
                (mCorrienteCategoriaSintomas.getGingivorragia() == null && hojaConsulta.getGingivorragia() != null) ||
                (mCorrienteCategoriaSintomas.getPeteqiasEspontaneas() == null && hojaConsulta.getPetequiasEspontaneas() != null) ||
                (mCorrienteCategoriaSintomas.getLlenadoCapilarseg() == null && hojaConsulta.getLlenadoCapilar2seg() != null) ||
                (mCorrienteCategoriaSintomas.getCianosis() == null && hojaConsulta.getCianosis() != null) ||
                (mCorrienteCategoriaSintomas.getLinfocitosAtipicos() == null && hojaConsulta.getLinfocitosaAtipicos() != null) ||
                (mCorrienteCategoriaSintomas.getFecha() == null && hojaConsulta.getFechaLinfocitos() != null) ||
                (mCorrienteCategoriaSintomas.getHipermenorrea() == null && hojaConsulta.getHipermenorrea() != null) ||
                (mCorrienteCategoriaSintomas.getHematemesis() == null && hojaConsulta.getHematemesis() != null) ||
                (mCorrienteCategoriaSintomas.getMelena() == null && hojaConsulta.getMelena() != null) ||
                (mCorrienteCategoriaSintomas.getHemoconc() == null && hojaConsulta.getHemoconc() != null) ||
                (mCorrienteCategoriaSintomas.getHemoconcentracion() == null && hojaConsulta.getHemoconcentracion() != null) ||
                (mCorrienteCategoriaSintomas.getHospitalizado() == null && hojaConsulta.getHospitalizado() != null) ||
                (mCorrienteCategoriaSintomas.getHospitalizadoEspecificar() == null && hojaConsulta.getHospitalizadoEspecificar() != null) ||
                (mCorrienteCategoriaSintomas.getTransfucionSangre() == null && hojaConsulta.getTransfusionSangre() != null) ||
                (mCorrienteCategoriaSintomas.getTransfuncionEspecificar() == null && hojaConsulta.getTransfusionEspecificar() != null) ||
                (mCorrienteCategoriaSintomas.getTomandoMedicamento() == null && hojaConsulta.getTomandoMedicamento() != null) ||
                (mCorrienteCategoriaSintomas.getTomandoMedicamentoEspecificar() == null && hojaConsulta.getMedicamentoEspecificar() != null) ||
                (mCorrienteCategoriaSintomas.getMedicamentoDistinto() == null && hojaConsulta.getMedicamentoDistinto() != null) ||
                (mCorrienteCategoriaSintomas.getMedicamentoDistintoEspecificar() == null && hojaConsulta.getMedicamentoDistEspecificar() != null)) {
            return true;
        }

        if ((mCorrienteCategoriaSintomas.getSaturaciono() != null && hojaConsulta.getSaturaciono2() == null) ||
                (mCorrienteCategoriaSintomas.getCategoria() != null && hojaConsulta.getCategoria() == null) ||
                (mCorrienteCategoriaSintomas.getCambioCategoria() != null && hojaConsulta.getCambioCategoria() == null) ||
                (mCorrienteCategoriaSintomas.getManifestacionHemorragica() != null && hojaConsulta.getManifestacionHemorragica() == null) ||
                (mCorrienteCategoriaSintomas.getPruebaTorniquetePositiva() != null && hojaConsulta.getPruebaTorniquetePositiva() == null) ||
                (mCorrienteCategoriaSintomas.getPetequiaPt() != null && hojaConsulta.getPetequia10Pt() == null) ||
                (mCorrienteCategoriaSintomas.getPetequiasPt() != null && hojaConsulta.getPetequia20Pt() == null) ||
                (mCorrienteCategoriaSintomas.getPielExtremidadesFrias() != null && hojaConsulta.getPielExtremidadesFrias() == null) ||
                (mCorrienteCategoriaSintomas.getPalidezEnExtremidades() != null && hojaConsulta.getPalidezEnExtremidades() == null) ||
                (mCorrienteCategoriaSintomas.getEpitaxis() != null && hojaConsulta.getEpistaxis() == null) ||
                (mCorrienteCategoriaSintomas.getGingivorragia() != null && hojaConsulta.getGingivorragia() == null) ||
                (mCorrienteCategoriaSintomas.getPeteqiasEspontaneas() != null && hojaConsulta.getPetequiasEspontaneas() == null) ||
                (mCorrienteCategoriaSintomas.getLlenadoCapilarseg() != null && hojaConsulta.getLlenadoCapilar2seg() == null) ||
                (mCorrienteCategoriaSintomas.getCianosis() != null && hojaConsulta.getCianosis() == null) ||
                (mCorrienteCategoriaSintomas.getLinfocitosAtipicos() != null && hojaConsulta.getLinfocitosaAtipicos() == null) ||
                (mCorrienteCategoriaSintomas.getFecha() != null && hojaConsulta.getFechaLinfocitos() == null) ||
                (mCorrienteCategoriaSintomas.getHipermenorrea() != null && hojaConsulta.getHipermenorrea() == null) ||
                (mCorrienteCategoriaSintomas.getHematemesis() != null && hojaConsulta.getHematemesis() == null) ||
                (mCorrienteCategoriaSintomas.getMelena() != null && hojaConsulta.getMelena() == null) ||
                (mCorrienteCategoriaSintomas.getHemoconc() != null && hojaConsulta.getHemoconc() == null) ||
                (mCorrienteCategoriaSintomas.getHemoconcentracion() != null && hojaConsulta.getHemoconcentracion() == null) ||
                (mCorrienteCategoriaSintomas.getHospitalizado() != null && hojaConsulta.getHospitalizado() == null) ||
                (mCorrienteCategoriaSintomas.getHospitalizadoEspecificar() != null && hojaConsulta.getHospitalizadoEspecificar() == null) ||
                (mCorrienteCategoriaSintomas.getTransfucionSangre() != null && hojaConsulta.getTransfusionSangre() == null) ||
                (mCorrienteCategoriaSintomas.getTransfuncionEspecificar() != null && hojaConsulta.getTransfusionEspecificar() == null) ||
                (mCorrienteCategoriaSintomas.getTomandoMedicamento() != null && hojaConsulta.getTomandoMedicamento() == null) ||
                (mCorrienteCategoriaSintomas.getTomandoMedicamentoEspecificar() != null && hojaConsulta.getMedicamentoEspecificar() == null) ||
                (mCorrienteCategoriaSintomas.getMedicamentoDistinto() != null && hojaConsulta.getMedicamentoDistinto() == null) ||
                (mCorrienteCategoriaSintomas.getMedicamentoDistintoEspecificar() != null && hojaConsulta.getMedicamentoDistEspecificar() == null)) {
            return true;
        }

        if ((mCorrienteCategoriaSintomas.getSaturaciono().intValue() != hojaConsulta.getSaturaciono2().intValue()) ||
                (mCorrienteCategoriaSintomas.getCategoria().compareTo(hojaConsulta.getCategoria()) != 0) ||
                (mCorrienteCategoriaSintomas.getCambioCategoria().charValue() != hojaConsulta.getCambioCategoria().charValue()) ||
                (mCorrienteCategoriaSintomas.getManifestacionHemorragica().charValue() != hojaConsulta.getManifestacionHemorragica().charValue()) ||
                (mCorrienteCategoriaSintomas.getPruebaTorniquetePositiva().charValue() != hojaConsulta.getPruebaTorniquetePositiva().charValue()) ||
                (mCorrienteCategoriaSintomas.getPetequiaPt().charValue() != hojaConsulta.getPetequia10Pt().charValue()) ||
                (mCorrienteCategoriaSintomas.getPetequiasPt().charValue() != hojaConsulta.getPetequia20Pt().charValue()) ||
                (mCorrienteCategoriaSintomas.getPielExtremidadesFrias().charValue() != hojaConsulta.getPielExtremidadesFrias().charValue()) ||
                (mCorrienteCategoriaSintomas.getPalidezEnExtremidades().charValue() != hojaConsulta.getPalidezEnExtremidades().charValue()) ||
                (mCorrienteCategoriaSintomas.getEpitaxis().charValue() != hojaConsulta.getEpistaxis().charValue()) ||
                (mCorrienteCategoriaSintomas.getGingivorragia().charValue() != hojaConsulta.getGingivorragia().charValue()) ||
                (mCorrienteCategoriaSintomas.getPeteqiasEspontaneas().charValue() != hojaConsulta.getPetequiasEspontaneas().charValue()) ||
                (mCorrienteCategoriaSintomas.getLlenadoCapilarseg().charValue() != hojaConsulta.getLlenadoCapilar2seg().charValue()) ||
                (mCorrienteCategoriaSintomas.getCianosis().charValue() != hojaConsulta.getCianosis().charValue()) ||
                (mCorrienteCategoriaSintomas.getLinfocitosAtipicos().compareTo(hojaConsulta.getLinfocitosaAtipicos()) != 0) ||
                (mCorrienteCategoriaSintomas.getFecha().compareTo(hojaConsulta.getFechaLinfocitos()) != 0) ||
                (mCorrienteCategoriaSintomas.getHipermenorrea().charValue() != hojaConsulta.getHipermenorrea().charValue()) ||
                (mCorrienteCategoriaSintomas.getHematemesis().charValue() != hojaConsulta.getHematemesis().charValue()) ||
                (mCorrienteCategoriaSintomas.getMelena().charValue() != hojaConsulta.getMelena().charValue()) ||
                (mCorrienteCategoriaSintomas.getHemoconc().charValue() != hojaConsulta.getHemoconc().charValue()) ||
                (mCorrienteCategoriaSintomas.getHemoconcentracion().compareTo(hojaConsulta.getHemoconcentracion()) != 0) ||
                (mCorrienteCategoriaSintomas.getHospitalizado().charValue() != hojaConsulta.getHospitalizado().charValue()) ||
                (mCorrienteCategoriaSintomas.getHospitalizadoEspecificar().compareTo(hojaConsulta.getHospitalizadoEspecificar()) != 0) ||
                (mCorrienteCategoriaSintomas.getTransfucionSangre().charValue() != hojaConsulta.getTransfusionSangre().charValue()) ||
                (mCorrienteCategoriaSintomas.getTransfuncionEspecificar().compareTo(hojaConsulta.getTransfusionEspecificar()) != 0) ||
                (mCorrienteCategoriaSintomas.getTomandoMedicamento().charValue() != hojaConsulta.getTomandoMedicamento().charValue()) ||
                (mCorrienteCategoriaSintomas.getTomandoMedicamentoEspecificar().compareTo(hojaConsulta.getMedicamentoEspecificar()) != 0) ||
                (mCorrienteCategoriaSintomas.getMedicamentoDistinto().charValue() != hojaConsulta.getMedicamentoDistinto().charValue()) ||
                (mCorrienteCategoriaSintomas.getMedicamentoDistintoEspecificar().compareTo(hojaConsulta.getMedicamentoDistEspecificar()) != 0)) {
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

    private void llamarGuardadoServicio() {
        if (mGuardarSaturacionTask == null ||
                mGuardarSaturacionTask.getStatus() == AsyncTask.Status.FINISHED) {
            mGuardarSaturacionTask = new AsyncTask<Void, Void, Void>() {
                /*               @Override
                               protected Void doInBackground(Void... params) {
                                   return null;
                               }
                           }
                       }*/
                //AsyncTask<Void, Void, Void> guardarTask = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private ErrorDTO RESPUESTA = new ErrorDTO();
                private SintomasWS SINTOMASWS = new SintomasWS(getResources());
                private String MENSAJE_MATRIZ = null;
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
                    if (NET_INFO != null && NET_INFO.isConnected()) {

                        if (genControlCambios != null) {
                            genControlCambios.setCodExpediente(mPacienteSeleccionado.getCodExpediente());
                            genControlCambios.setNumHojaConsulta(Integer.parseInt(mPacienteSeleccionado.getNumHojaConsulta()));
                            CTRCAMBIOSWS.guardarControlCambios(genControlCambios);
                        }

                        RESPUESTA = SINTOMASWS.guardarCategoriaSintomas(mHojaConsulta, isCategoriaAB(), mUsuarioLogiado);

                        if (RESPUESTA.getCodigoError().intValue() == 0) {
                            MENSAJE_MATRIZ = SINTOMASWS.validacionMatrizSintoma(mHojaConsulta);
                        }
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

                        if (!StringUtils.isNullOrEmpty(MENSAJE_MATRIZ)
                                && !MENSAJE_MATRIZ.startsWith("any")) {
                            MensajesHelper.mostrarMensajeInfo(CONTEXT, MENSAJE_MATRIZ,
                                    getResources().getString(R.string.title_estudio_sostenible), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            cerrarActivity();
                                        }
                                    });
                        } else {
                            cerrarActivity();
                        }

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

                protected void cerrarActivity() {
                    InicioDTO pacienteSeleccionado = (InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado");

                    Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
                    intent.putExtra("indice", 0);
                    intent.putExtra("pacienteSeleccionado", pacienteSeleccionado);

                    startActivity(intent);
                    finish();
                }
            };
            mGuardarSaturacionTask.execute((Void[]) null);
        }
    }

    private void cancelarLlamarServicioTask() {
        if (mGuardarSaturacionTask != null && mGuardarSaturacionTask.getStatus() == AsyncTask.Status.RUNNING) {
            mGuardarSaturacionTask.cancel(true);
            mGuardarSaturacionTask = null;
        }
    }

    public String getCategoriaSeleccionada() {
        return CATEGORIA_SELECCIONADA;
    }

    public boolean isCategoriaAB() {
        return CATEGORIA_A_B;
    }
}
