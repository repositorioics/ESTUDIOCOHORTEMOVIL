package com.sts_ni.estudiocohortecssfv.sintomasactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.OsteomuscularSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

/**
 * Controlador de la UI Osteomuscular.
 */
public class OsteomuscularSintomasActivity extends ActionBarActivity {

    private Context CONTEXT;
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private OsteomuscularSintomasDTO mCorrienteOsteomuscularSintomas;
    private String mUsuarioLogiado;
    private TextView viewTxtvNSintoma;
    private TextView viewTxtvSSintoma;
    private TextView viewTxtvDSintoma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osteomuscular_sintoma);

        this.CONTEXT = this;

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        viewTxtvNSintoma = (TextView) findViewById(R.id.txtvNSintoma);
        viewTxtvSSintoma = (TextView) findViewById(R.id.txtvSSintoma);
        viewTxtvDSintoma = (TextView) findViewById(R.id.txtvDSintoma);

        viewTxtvNSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 2);
            }
        });

        viewTxtvSSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 1);
            }
        });

        viewTxtvDSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 3);
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onStart () {
        super.onStart();
        obtenerValorGuardadoServicio();
    }

    public void onChkboxClickedARTOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbARTSOTSintoma), findViewById(R.id.chkbARTNOTSintoma),
                findViewById(R.id.chkbARTDOTSintoma), view);
    }

    public void onChkboxClickedMIAOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbMIASOTSintoma), findViewById(R.id.chkbMIANOTSintoma),
                findViewById(R.id.chkbMIADOTSintoma), view);
    }

    public void onChkboxClickedLUMOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbLUMSOTSintoma), findViewById(R.id.chkbLUMNOTSintoma),
                findViewById(R.id.chkbLUMDOTSintoma), view);
    }

    public void onChkboxClickedDOCOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbDOCSOTSintoma), findViewById(R.id.chkbDOCNOTSintoma),
                findViewById(R.id.chkbDOCDOTSintoma), view);
    }

    public void onChkboxClickedTNSOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbTNSSOTSintoma), findViewById(R.id.chkbTNSNOTSintoma),
                findViewById(R.id.chkbTNSDOTSintoma), view);
    }

    public void onChkboxClickedARPOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbARPSOTSintoma), findViewById(R.id.chkbARPNOTSintoma),
                findViewById(R.id.chkbARPDOTSintoma), view);
    }

    public void onChkboxClickedARDOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbARDSOTSintoma), findViewById(R.id.chkbARDNOTSintoma),
                findViewById(R.id.chkbARDDOTSintoma), view);
    }

    public void onChkboxClickedCNJOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbCNJSOTSintoma), findViewById(R.id.chkbCNJNOTSintoma),
                findViewById(R.id.chkbCNJDOTSintoma), view);
    }

    public void onChkboxClickedEDMOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbEDMSOTSintoma), findViewById(R.id.chkbEDMNOTSintoma),
                findViewById(R.id.chkbEDMDOTSintoma), view);
    }

    public void onChkboxClickedEDCOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbEDCSOTSintoma), findViewById(R.id.chkbEDCNOTSintoma),
                findViewById(R.id.chkbEDCDOTSintoma), view);
    }

    public void onChkboxClickedEDHOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbEDHSOTSintoma), findViewById(R.id.chkbEDHNOTSintoma),
                findViewById(R.id.chkbEDHDOTSintoma), view);
    }

    public void onChkboxClickedEDROT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbEDRSOTSintoma), findViewById(R.id.chkbEDRNOTSintoma),
                findViewById(R.id.chkbEDRDOTSintoma), view);
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

                            ((CheckBox) findViewById(R.id.chkbARTSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbARTNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbARTDOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbMIASOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbMIANOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbMIADOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbLUMSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbLUMNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbLUMDOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbDOCSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbDOCNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbDOCDOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbTNSSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbTNSNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbTNSDOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbARPSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbARPNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbARPDOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbARDSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbARDNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbARDDOTSintoma)).setChecked(false);

                            ((CheckBox) findViewById(R.id.chkbCNJSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDMSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDCSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDHSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDRSOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDTSOTSintoma)).setChecked(false);

                            ((CheckBox) findViewById(R.id.chkbCNJNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDMNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDCNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDHNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDRNOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDTNOTSintoma)).setChecked(false);

                            ((CheckBox) findViewById(R.id.chkbCNJDOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDMDOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDCDOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDHDOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDRDOTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEDTDOTSintoma)).setChecked(false);


                            if(valor==1) ((CheckBox) findViewById(R.id.chkbARTSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbMIASOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbLUMSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbDOCSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbTNSSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbARPSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbARDSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbCNJSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbEDMSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbEDCSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbEDHSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbEDRSOTSintoma)).setChecked(true);
                            if(valor==1) ((CheckBox) findViewById(R.id.chkbEDTSOTSintoma)).setChecked(true);

                            if(valor==2) ((CheckBox) findViewById(R.id.chkbARTNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbMIANOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbLUMNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbDOCNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbTNSNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbARPNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbARDNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbCNJNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbEDMNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbEDCNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbEDHNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbEDRNOTSintoma)).setChecked(true);
                            if(valor==2) ((CheckBox) findViewById(R.id.chkbEDTNOTSintoma)).setChecked(true);

                            if(valor==3) ((CheckBox) findViewById(R.id.chkbARTDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbMIADOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbLUMDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbDOCDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbTNSDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbARPDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbARDDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbCNJDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbEDMDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbEDCDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbEDHDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbEDRDOTSintoma)).setChecked(true);
                            if(valor==3) ((CheckBox) findViewById(R.id.chkbEDTDOTSintoma)).setChecked(true);

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            String mensaje = "Confirmacion";
            if(valor==1) mensaje = String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_osteomuscular_sintomas));
            if(valor==2) mensaje = String.format(getResources().getString(R.string.msg_change_no), getResources().getString(R.string.boton_osteomuscular_sintomas));
            if(valor==3) mensaje = String.format(getResources().getString(R.string.msg_change_desc), getResources().getString(R.string.boton_osteomuscular_sintomas));


            MensajesHelper.mostrarMensajeYesNo(this,
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void onChkboxClickedEDTOT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbEDTSOTSintoma), findViewById(R.id.chkbEDTNOTSintoma),
                findViewById(R.id.chkbEDTDOTSintoma), view);
    }

    public void onClick_btnOsteomuscular(View view) {
        try{
            validarCampos();
            mHojaConsulta = cargarHojaConsulta();
            if (mPacienteSeleccionado.getCodigoEstado() == '7') {
                if(mCorrienteOsteomuscularSintomas != null) {
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
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbARTSOTSintoma), findViewById(R.id.chkbARTNOTSintoma),
                findViewById(R.id.chkbARTDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbMIASOTSintoma), findViewById(R.id.chkbMIANOTSintoma),
                findViewById(R.id.chkbMIADOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbLUMSOTSintoma), findViewById(R.id.chkbLUMNOTSintoma),
                findViewById(R.id.chkbLUMDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbDOCSOTSintoma), findViewById(R.id.chkbDOCNOTSintoma),
                findViewById(R.id.chkbDOCDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbTNSSOTSintoma), findViewById(R.id.chkbTNSNOTSintoma),
                findViewById(R.id.chkbTNSDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbARPSOTSintoma), findViewById(R.id.chkbARPNOTSintoma),
                findViewById(R.id.chkbARPDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbARDSOTSintoma), findViewById(R.id.chkbARDNOTSintoma),
                findViewById(R.id.chkbARDDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbCNJSOTSintoma), findViewById(R.id.chkbCNJNOTSintoma),
                findViewById(R.id.chkbCNJDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbEDMSOTSintoma), findViewById(R.id.chkbEDMNOTSintoma),
                findViewById(R.id.chkbEDMDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbEDCSOTSintoma), findViewById(R.id.chkbEDCNOTSintoma),
                findViewById(R.id.chkbEDCDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbEDHSOTSintoma), findViewById(R.id.chkbEDHNOTSintoma),
                findViewById(R.id.chkbEDHDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbEDRSOTSintoma), findViewById(R.id.chkbEDRNOTSintoma),
                findViewById(R.id.chkbEDRDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbEDTSOTSintoma), findViewById(R.id.chkbEDTNOTSintoma),
                findViewById(R.id.chkbEDTDOTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setAltralgia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbARTSOTSintoma), findViewById(R.id.chkbARTNOTSintoma),
                findViewById(R.id.chkbARTDOTSintoma)));

        hojaConsulta.setMialgia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbMIASOTSintoma), findViewById(R.id.chkbMIANOTSintoma),
                findViewById(R.id.chkbMIADOTSintoma)));

        hojaConsulta.setLumbalgia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbLUMSOTSintoma), findViewById(R.id.chkbLUMNOTSintoma),
                findViewById(R.id.chkbLUMDOTSintoma)));
            
        hojaConsulta.setDolorCuello(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbDOCSOTSintoma), findViewById(R.id.chkbDOCNOTSintoma),
                findViewById(R.id.chkbDOCDOTSintoma)));
            
        hojaConsulta.setTenosinovitis(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbTNSSOTSintoma), findViewById(R.id.chkbTNSNOTSintoma),
                findViewById(R.id.chkbTNSDOTSintoma)));
            
        hojaConsulta.setArtralgiaProximal(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbARPSOTSintoma), findViewById(R.id.chkbARPNOTSintoma),
                findViewById(R.id.chkbARPDOTSintoma)));
            
        hojaConsulta.setArtralgiaDistal(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbARDSOTSintoma), findViewById(R.id.chkbARDNOTSintoma),
                findViewById(R.id.chkbARDDOTSintoma)));
            
        hojaConsulta.setConjuntivitis(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbCNJSOTSintoma), findViewById(R.id.chkbCNJNOTSintoma),
                findViewById(R.id.chkbCNJDOTSintoma)));
            
        hojaConsulta.setEdemaMunecas(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbEDMSOTSintoma), findViewById(R.id.chkbEDMNOTSintoma),
                findViewById(R.id.chkbEDMDOTSintoma)));
            
        hojaConsulta.setEdemaCodos(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbEDCSOTSintoma), findViewById(R.id.chkbEDCNOTSintoma),
                findViewById(R.id.chkbEDCDOTSintoma)));
            
        hojaConsulta.setEdemaHombros(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbEDHSOTSintoma), findViewById(R.id.chkbEDHNOTSintoma),
                findViewById(R.id.chkbEDHDOTSintoma)));
            
        hojaConsulta.setEdemaRodillas(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbEDRSOTSintoma), findViewById(R.id.chkbEDRNOTSintoma),
                findViewById(R.id.chkbEDRDOTSintoma)));
            
        hojaConsulta.setEdemaTobillos(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbEDTSOTSintoma), findViewById(R.id.chkbEDTNOTSintoma),
                findViewById(R.id.chkbEDTDOTSintoma)));

        return hojaConsulta;
    }

    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if((mCorrienteOsteomuscularSintomas.getAltralgia() == null && hojaConsulta.getAltralgia() != null) ||
                (mCorrienteOsteomuscularSintomas.getArtralgiaDistal() == null && hojaConsulta.getArtralgiaDistal() != null) ||
                (mCorrienteOsteomuscularSintomas.getArtralgiaProximal() == null && hojaConsulta.getArtralgiaProximal()!= null) ||
                (mCorrienteOsteomuscularSintomas.getConjuntivitis() == null && hojaConsulta.getConjuntivitis() != null) ||
                (mCorrienteOsteomuscularSintomas.getDolorCuello() == null && hojaConsulta.getDolorCuello() != null) ||
                (mCorrienteOsteomuscularSintomas.getEdemaCodos() == null && hojaConsulta.getEdemaCodos() != null) ||
                (mCorrienteOsteomuscularSintomas.getEdemaHombros() == null && hojaConsulta.getEdemaHombros() != null) ||
                (mCorrienteOsteomuscularSintomas.getEdemaMunecas() == null && hojaConsulta.getEdemaMunecas() != null) ||
                (mCorrienteOsteomuscularSintomas.getEdemaRodillas() == null && hojaConsulta.getEdemaRodillas() != null) ||
                (mCorrienteOsteomuscularSintomas.getEdemaTobillos() == null && hojaConsulta.getEdemaTobillos() != null) ||
                (mCorrienteOsteomuscularSintomas.getLumbalgia() == null && hojaConsulta.getLumbalgia() != null) ||
                (mCorrienteOsteomuscularSintomas.getMialgia() == null && hojaConsulta.getMialgia() != null) ||
                (mCorrienteOsteomuscularSintomas.getTenosinovitis() == null && hojaConsulta.getTenosinovitis() != null)) {
            return true;
        }

        if((mCorrienteOsteomuscularSintomas.getAltralgia() != null && hojaConsulta.getAltralgia() == null) ||
                (mCorrienteOsteomuscularSintomas.getArtralgiaDistal() != null && hojaConsulta.getArtralgiaDistal() == null) ||
                (mCorrienteOsteomuscularSintomas.getArtralgiaProximal() != null && hojaConsulta.getArtralgiaProximal() == null) ||
                (mCorrienteOsteomuscularSintomas.getConjuntivitis() != null && hojaConsulta.getConjuntivitis() == null) ||
                (mCorrienteOsteomuscularSintomas.getDolorCuello() != null && hojaConsulta.getDolorCuello() == null) ||
                (mCorrienteOsteomuscularSintomas.getEdemaCodos() != null && hojaConsulta.getEdemaCodos() == null) ||
                (mCorrienteOsteomuscularSintomas.getEdemaHombros() != null && hojaConsulta.getEdemaHombros() == null) ||
                (mCorrienteOsteomuscularSintomas.getEdemaMunecas() != null && hojaConsulta.getEdemaMunecas() == null) ||
                (mCorrienteOsteomuscularSintomas.getEdemaRodillas() != null && hojaConsulta.getEdemaRodillas() == null) ||
                (mCorrienteOsteomuscularSintomas.getEdemaTobillos() != null && hojaConsulta.getEdemaTobillos() == null) ||
                (mCorrienteOsteomuscularSintomas.getLumbalgia() != null && hojaConsulta.getLumbalgia() == null) ||
                (mCorrienteOsteomuscularSintomas.getMialgia() != null && hojaConsulta.getMialgia() == null) ||
                (mCorrienteOsteomuscularSintomas.getTenosinovitis() != null && hojaConsulta.getTenosinovitis() == null)) {
            return true;
        }

        if(mCorrienteOsteomuscularSintomas.getAltralgia().charValue() != hojaConsulta.getAltralgia().charValue() ||
                mCorrienteOsteomuscularSintomas.getArtralgiaDistal().charValue() != hojaConsulta.getArtralgiaDistal().charValue() ||
                mCorrienteOsteomuscularSintomas.getArtralgiaProximal().charValue() != hojaConsulta.getArtralgiaProximal().charValue() ||
                mCorrienteOsteomuscularSintomas.getConjuntivitis().charValue() != hojaConsulta.getConjuntivitis().charValue() ||
                mCorrienteOsteomuscularSintomas.getDolorCuello().charValue() != hojaConsulta.getDolorCuello().charValue() ||
                mCorrienteOsteomuscularSintomas.getEdemaCodos().charValue() != hojaConsulta.getEdemaCodos().charValue() ||
                mCorrienteOsteomuscularSintomas.getEdemaHombros().charValue() != hojaConsulta.getEdemaHombros().charValue() ||
                mCorrienteOsteomuscularSintomas.getEdemaMunecas().charValue() != hojaConsulta.getEdemaMunecas().charValue() ||
                mCorrienteOsteomuscularSintomas.getEdemaRodillas().charValue() != hojaConsulta.getEdemaRodillas().charValue() ||
                mCorrienteOsteomuscularSintomas.getEdemaTobillos().charValue() != hojaConsulta.getEdemaTobillos().charValue() ||
                mCorrienteOsteomuscularSintomas.getLumbalgia().charValue() != hojaConsulta.getLumbalgia().charValue() ||
                mCorrienteOsteomuscularSintomas.getMialgia().charValue() != hojaConsulta.getMialgia().charValue() ||
                mCorrienteOsteomuscularSintomas.getTenosinovitis().charValue() != hojaConsulta.getTenosinovitis().charValue()) {
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

    private void llamarGuardadoServicio(){
        AsyncTask<Void, Void, Void> guardarTask = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private SintomasWS SINTOMASWS = new SintomasWS(getResources());

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

                    RESPUESTA = SINTOMASWS.guardarOsteomuscularSintomas(mHojaConsulta, mUsuarioLogiado);
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
        guardarTask.execute((Void[])null);
    }

    private void obtenerValorGuardadoServicio(){
        AsyncTask<Void, Void, Void> lecturaTask = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ResultadoObjectWSDTO<OsteomuscularSintomasDTO> RESPUESTA;
            private SintomasWS SINTOMASWS;

            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(CONTEXT);
                PD.setTitle(getResources().getString(R.string.title_obteniendo));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
                SINTOMASWS = new SintomasWS(getResources());
                RESPUESTA = new ResultadoObjectWSDTO<>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerOsteomuscularSintomas(hojaConsulta);
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
                    cargarValores();
                    mCorrienteOsteomuscularSintomas = RESPUESTA.getObjecRespuesta();
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

            protected void cargarValores() {
                OsteomuscularSintomasDTO osteomuscularSintomas = RESPUESTA.getObjecRespuesta();

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbARTSOTSintoma), findViewById(R.id.chkbARTNOTSintoma),
                        findViewById(R.id.chkbARTDOTSintoma), ((osteomuscularSintomas.getAltralgia() != null)  
                                ? osteomuscularSintomas.getAltralgia().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbMIASOTSintoma), findViewById(R.id.chkbMIANOTSintoma),
                        findViewById(R.id.chkbMIADOTSintoma), ((osteomuscularSintomas.getMialgia() != null)
                                ? osteomuscularSintomas.getMialgia().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbLUMSOTSintoma), findViewById(R.id.chkbLUMNOTSintoma),
                        findViewById(R.id.chkbLUMDOTSintoma), ((osteomuscularSintomas.getLumbalgia() != null)
                                ? osteomuscularSintomas.getLumbalgia().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbDOCSOTSintoma), findViewById(R.id.chkbDOCNOTSintoma),
                        findViewById(R.id.chkbDOCDOTSintoma), ((osteomuscularSintomas.getDolorCuello() != null)
                                ? osteomuscularSintomas.getDolorCuello().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbTNSSOTSintoma), findViewById(R.id.chkbTNSNOTSintoma),
                        findViewById(R.id.chkbTNSDOTSintoma), ((osteomuscularSintomas.getTenosinovitis() != null)
                                ? osteomuscularSintomas.getTenosinovitis().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbARPSOTSintoma), findViewById(R.id.chkbARPNOTSintoma),
                        findViewById(R.id.chkbARPDOTSintoma), ((osteomuscularSintomas.getArtralgiaProximal() != null)
                                ? osteomuscularSintomas.getArtralgiaProximal().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbARDSOTSintoma), findViewById(R.id.chkbARDNOTSintoma),
                        findViewById(R.id.chkbARDDOTSintoma), ((osteomuscularSintomas.getArtralgiaDistal() != null)
                                ? osteomuscularSintomas.getArtralgiaDistal().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbCNJSOTSintoma), findViewById(R.id.chkbCNJNOTSintoma),
                        findViewById(R.id.chkbCNJDOTSintoma), ((osteomuscularSintomas.getConjuntivitis() != null)
                                ? osteomuscularSintomas.getConjuntivitis().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbEDMSOTSintoma), findViewById(R.id.chkbEDMNOTSintoma),
                        findViewById(R.id.chkbEDMDOTSintoma), ((osteomuscularSintomas.getEdemaMunecas() != null)
                                ? osteomuscularSintomas.getEdemaMunecas().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbEDCSOTSintoma), findViewById(R.id.chkbEDCNOTSintoma),
                        findViewById(R.id.chkbEDCDOTSintoma), ((osteomuscularSintomas.getEdemaCodos() != null)
                                ? osteomuscularSintomas.getEdemaCodos().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbEDHSOTSintoma), findViewById(R.id.chkbEDHNOTSintoma),
                        findViewById(R.id.chkbEDHDOTSintoma), ((osteomuscularSintomas.getEdemaHombros() != null)
                                ? osteomuscularSintomas.getEdemaHombros().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbEDRSOTSintoma), findViewById(R.id.chkbEDRNOTSintoma),
                        findViewById(R.id.chkbEDRDOTSintoma), ((osteomuscularSintomas.getEdemaRodillas() != null)
                                ? osteomuscularSintomas.getEdemaRodillas().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbEDTSOTSintoma), findViewById(R.id.chkbEDTNOTSintoma),
                        findViewById(R.id.chkbEDTDOTSintoma), ((osteomuscularSintomas.getEdemaTobillos() != null)
                                ? osteomuscularSintomas.getEdemaTobillos().charValue() : '4'));
                
            }
        };
        lecturaTask.execute((Void[])null);
    }
}
