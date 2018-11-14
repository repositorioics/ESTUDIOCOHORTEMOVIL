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
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.RespiratorioSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.DatePickerFragment;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Controlador de la UI Respiratorio.
 */
public class RespiratorioSintomasActivity extends ActionBarActivity {

    private Context CONTEXT;
    private InicioDTO mPacienteSeleccionado;
    private HojaConsultaDTO mHojaConsulta;
    private RespiratorioSintomasDTO mCorrienteRespiratorioSintomas;
    private String mUsuarioLogiado;
    private TextView viewTxtvNENSintoma;
    private TextView viewTxtvSENSintoma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiratorio_sintoma);

        this.CONTEXT = this;

        ((EditText)findViewById(R.id.dpNVF)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mPacienteSeleccionado = ((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado"));
        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        viewTxtvNENSintoma = (TextView) findViewById(R.id.txtvNENSintoma);
        viewTxtvSENSintoma = (TextView) findViewById(R.id.txtvSENSintoma);

        viewTxtvNENSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSENSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, true);
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

    public void onChkboxClickedTOSEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbTOSSENSintoma), findViewById(R.id.chkbTOSNENSintoma),
                findViewById(R.id.chkbTOSDENSintoma), view);
    }

    public void onChkboxClickedRNREN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRNRSENSintoma), findViewById(R.id.chkbRNRNENSintoma),
                findViewById(R.id.chkbRNRDENSintoma), view);
    }

    public void onChkboxClickedCGNEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbCGNSENSintoma), findViewById(R.id.chkbCGNNENSintoma),
                findViewById(R.id.chkbCGNDENSintoma), view);
    }

    public void onChkboxClickedOTGEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbOTGSENSintoma), findViewById(R.id.chkbOTGNENSintoma),
                findViewById(R.id.chkbOTGDENSintoma), view);
    }

    public void onChkboxClickedALNEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbALNSENSintoma), findViewById(R.id.chkbALNNENSintoma),
                view);
    }

    public void onChkboxClickedAPNEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbAPNSENSintoma), findViewById(R.id.chkbAPNNENSintoma),
                view);
    }

    public void onChkboxClickedRSREN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRSRSENSintoma), findViewById(R.id.chkbRSRNENSintoma),
                view);
    }

    public void onChkboxClickedQJEEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbQJESENSintoma), findViewById(R.id.chkbQJENENSintoma),
                view);
    }

    public void onChkboxClickedESREN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbESRSENSintoma), findViewById(R.id.chkbESRNENSintoma),
                view);
    }

    public void onChkboxClickedTISEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbTISSENSintoma), findViewById(R.id.chkbTISNENSintoma),
                view);
    }

    public void onChkboxClickedSIBEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbSIBSENSintoma), findViewById(R.id.chkbSIBNENSintoma),
                view);
    }

    public void onChkboxClickedCEPEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbCEPSENSintoma), findViewById(R.id.chkbCEPNENSintoma),
                view);
    }

    public void onChkboxClickedRNCEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbRNCSENSintoma), findViewById(R.id.chkbRNCNENSintoma),
                view);
    }

    public void onChkboxClickedOTFEN(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbOTFSENSintoma), findViewById(R.id.chkbOTFNENSintoma),
                view);

       if(((CheckBox) findViewById(R.id.chkbOTFSENSintoma)).isChecked()){
           findViewById(R.id.txtvNVFENSintoma).setVisibility(View.VISIBLE);
           findViewById(R.id.dpNVF).setVisibility(View.VISIBLE);

       }else{
           findViewById(R.id.txtvNVFENSintoma).setVisibility(View.INVISIBLE);
           EditText etNF = ((EditText) findViewById(R.id.dpNVF));
           etNF.setText(null);
           etNF.setVisibility(View.INVISIBLE);
       }
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if(DateUtils.esMayorFechaHoy(calendar)){
                    ((EditText)getActivity().findViewById(R.id.dpNVF)).setError(getString(R.string.msj_fecha_mayor_hoy));
                } else {
                    ((EditText) getActivity().findViewById(R.id.dpNVF)).setError(null);
                    ((EditText) getActivity().findViewById(R.id.dpNVF)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar, 0);
                    ((EditText) getActivity().findViewById(R.id.dpNVF)).setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                }
            }
        };
        newFragment.show(getSupportFragmentManager(), getString(R.string.title_date_picker));
    }

    /***
     * Metodo que es ejecutado en el evento onClick de la eqitueta no o si
     * @param view
     */
    public void SintomaMarcado(View view, final boolean valor) {
        try{
            DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ((CheckBox) findViewById(R.id.chkbTOSNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbRNRNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbCGNNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbOTGNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbALNNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbAPNNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbRSRNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbQJENENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbESRNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbTISNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbSIBNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbCEPNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbRNCNENSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbOTFNENSintoma)).setChecked(!valor);

                            ((CheckBox) findViewById(R.id.chkbTOSSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbRNRSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbCGNSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbOTGSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbALNSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbAPNSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbRSRSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbQJESENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbESRSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbTISSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbSIBSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbCEPSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbRNCSENSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbOTFSENSintoma)).setChecked(valor);

                            ((CheckBox) findViewById(R.id.chkbTOSDENSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbRNRDENSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbCGNDENSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbOTGDENSintoma)).setChecked(false);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_respiratorio_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_respiratorio_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void onClick_btnRegresar(View view) {
        try{
            validarCampos();
            mHojaConsulta = cargarHojaConsulta();
            if (mPacienteSeleccionado.getCodigoEstado() == '7') {
                if(mCorrienteRespiratorioSintomas != null) {
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
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.app_name), null);
        }
    }

    public void validarCampos() throws Exception {
        EditText dpNVF = (EditText) findViewById(R.id.dpNVF);
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbTOSSENSintoma), findViewById(R.id.chkbTOSNENSintoma),
                findViewById(R.id.chkbTOSDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRNRSENSintoma), findViewById(R.id.chkbRNRNENSintoma),
                findViewById(R.id.chkbRNRDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbCGNSENSintoma), findViewById(R.id.chkbCGNNENSintoma),
                findViewById(R.id.chkbCGNDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbOTGSENSintoma), findViewById(R.id.chkbOTGNENSintoma),
                findViewById(R.id.chkbOTGDENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbALNSENSintoma), findViewById(R.id.chkbALNNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbAPNSENSintoma), findViewById(R.id.chkbAPNNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRSRSENSintoma), findViewById(R.id.chkbRSRNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbQJESENSintoma), findViewById(R.id.chkbQJENENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbESRSENSintoma), findViewById(R.id.chkbESRNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbTISSENSintoma), findViewById(R.id.chkbTISNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbSIBSENSintoma), findViewById(R.id.chkbSIBNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbCEPSENSintoma), findViewById(R.id.chkbCEPNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }  else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbRNCSENSintoma), findViewById(R.id.chkbRNCNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (!((CheckBox) findViewById(R.id.chkbOTFSENSintoma)).isChecked() && !((CheckBox) findViewById(R.id.chkbOTFNENSintoma)).isChecked()) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if (((CheckBox) findViewById(R.id.chkbOTFSENSintoma)).isChecked() && StringUtils.isNullOrEmpty(dpNVF.getText().toString())) {
            throw new Exception(getString(R.string.msj_debe_ingresar_nueva_fif));
        }
        /*else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbOTFSENSintoma), findViewById(R.id.chkbOTFNENSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }*/
    }
    
    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setTos(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbTOSSENSintoma),
                findViewById(R.id.chkbTOSNENSintoma), findViewById(R.id.chkbTOSDENSintoma)));

        hojaConsulta.setRinorrea(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRNRSENSintoma),
                findViewById(R.id.chkbRNRNENSintoma), findViewById(R.id.chkbRNRNENSintoma)));

        hojaConsulta.setCongestionNasal(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbCGNSENSintoma),
                findViewById(R.id.chkbCGNNENSintoma), findViewById(R.id.chkbCGNDENSintoma)));

        hojaConsulta.setOtalgia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbOTGSENSintoma),
                findViewById(R.id.chkbOTGNENSintoma), findViewById(R.id.chkbOTGDENSintoma)));

        hojaConsulta.setAleteoNasal(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbALNSENSintoma),
                findViewById(R.id.chkbALNNENSintoma)));

        hojaConsulta.setApnea(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbAPNSENSintoma),
                findViewById(R.id.chkbAPNNENSintoma)));

        hojaConsulta.setRespiracionRapida(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRSRSENSintoma),
                findViewById(R.id.chkbRSRNENSintoma)));

        hojaConsulta.setQuejidoEspiratorio(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbQJESENSintoma),
                findViewById(R.id.chkbQJENENSintoma)));

        hojaConsulta.setEstiradorReposo(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbESRSENSintoma),
                findViewById(R.id.chkbESRNENSintoma)));

        hojaConsulta.setTirajeSubcostal(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbTISSENSintoma),
                findViewById(R.id.chkbTISNENSintoma)));

        hojaConsulta.setSibilancias(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbSIBSENSintoma),
                findViewById(R.id.chkbSIBNENSintoma)));

        hojaConsulta.setCrepitos(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbCEPSENSintoma),
                findViewById(R.id.chkbCEPNENSintoma)));

        hojaConsulta.setRoncos(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbRNCSENSintoma),
                findViewById(R.id.chkbRNCNENSintoma)));

        hojaConsulta.setOtraFif(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbOTFSENSintoma),
                findViewById(R.id.chkbOTFNENSintoma)));

        if(findViewById(R.id.dpNVF).getVisibility() == View.VISIBLE && !StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.dpNVF)).getText().toString())) {
            hojaConsulta.setNuevaFif(((EditText) findViewById(R.id.dpNVF)).getText().toString());
        }else{
            hojaConsulta.setNuevaFif(null);
        }

        return hojaConsulta;
    }

    private boolean tieneCambiosHojaConsulta(HojaConsultaDTO hojaConsulta) {

        if((mCorrienteRespiratorioSintomas.getAleteoNasal() == null && hojaConsulta.getAleteoNasal() != null) ||
                (mCorrienteRespiratorioSintomas.getApnea() == null && hojaConsulta.getApnea() != null) ||
                (mCorrienteRespiratorioSintomas.getCongestionNasal() == null && hojaConsulta.getCongestionNasal()!= null) ||
                (mCorrienteRespiratorioSintomas.getCrepitos() == null && hojaConsulta.getCrepitos() != null) ||
                (mCorrienteRespiratorioSintomas.getEstiradorReposo() == null && hojaConsulta.getEstiradorReposo() != null) ||
                (mCorrienteRespiratorioSintomas.getOtalgia() == null && hojaConsulta.getOtalgia() != null) ||
                (mCorrienteRespiratorioSintomas.getOtraFif() == null && hojaConsulta.getOtraFif() != null) ||
                (mCorrienteRespiratorioSintomas.getQuejidoEspiratorio() == null && hojaConsulta.getQuejidoEspiratorio() != null) ||
                (mCorrienteRespiratorioSintomas.getRespiracionRapida() == null && hojaConsulta.getRespiracionRapida() != null) ||
                (mCorrienteRespiratorioSintomas.getRinorrea() == null && hojaConsulta.getRinorrea() != null) ||
                (mCorrienteRespiratorioSintomas.getRoncos() == null && hojaConsulta.getRoncos() != null) ||
                (mCorrienteRespiratorioSintomas.getSibilancias() == null && hojaConsulta.getSibilancias() != null) ||
                (mCorrienteRespiratorioSintomas.getTirajeSubcostal() == null && hojaConsulta.getTirajeSubcostal() != null) ||
                (mCorrienteRespiratorioSintomas.getTos() == null && hojaConsulta.getTos() != null)) {
            return true;
        }

        if((mCorrienteRespiratorioSintomas.getAleteoNasal() != null && hojaConsulta.getAleteoNasal() == null) ||
                (mCorrienteRespiratorioSintomas.getApnea() != null && hojaConsulta.getApnea() == null) ||
                (mCorrienteRespiratorioSintomas.getCongestionNasal() != null && hojaConsulta.getCongestionNasal() == null) ||
                (mCorrienteRespiratorioSintomas.getCrepitos() != null && hojaConsulta.getCrepitos() == null) ||
                (mCorrienteRespiratorioSintomas.getEstiradorReposo() != null && hojaConsulta.getEstiradorReposo() == null) ||
                (mCorrienteRespiratorioSintomas.getOtalgia() != null && hojaConsulta.getOtalgia() == null) ||
                (mCorrienteRespiratorioSintomas.getOtraFif() != null && hojaConsulta.getOtraFif() == null) ||
                (mCorrienteRespiratorioSintomas.getQuejidoEspiratorio() != null && hojaConsulta.getQuejidoEspiratorio() == null) ||
                (mCorrienteRespiratorioSintomas.getRespiracionRapida() != null && hojaConsulta.getRespiracionRapida() == null) ||
                (mCorrienteRespiratorioSintomas.getRinorrea() != null && hojaConsulta.getRinorrea() == null) ||
                (mCorrienteRespiratorioSintomas.getRoncos() != null && hojaConsulta.getRoncos() == null) ||
                (mCorrienteRespiratorioSintomas.getSibilancias() != null && hojaConsulta.getSibilancias() == null) ||
                (mCorrienteRespiratorioSintomas.getTirajeSubcostal() != null && hojaConsulta.getTirajeSubcostal() == null) ||
                (mCorrienteRespiratorioSintomas.getTos() != null && hojaConsulta.getTos() == null)) {
            return true;
        }

        if(mCorrienteRespiratorioSintomas.getAleteoNasal().charValue() != hojaConsulta.getAleteoNasal().charValue() ||
                mCorrienteRespiratorioSintomas.getApnea().charValue() != hojaConsulta.getApnea().charValue() ||
                mCorrienteRespiratorioSintomas.getCongestionNasal().charValue() != hojaConsulta.getCongestionNasal().charValue() ||
                mCorrienteRespiratorioSintomas.getCrepitos().charValue() != hojaConsulta.getCrepitos().charValue() ||
                mCorrienteRespiratorioSintomas.getEstiradorReposo().charValue() != hojaConsulta.getEstiradorReposo().charValue() ||
                mCorrienteRespiratorioSintomas.getOtalgia().charValue() != hojaConsulta.getOtalgia().charValue() ||
                mCorrienteRespiratorioSintomas.getOtraFif().charValue() != hojaConsulta.getOtraFif().charValue() ||
                mCorrienteRespiratorioSintomas.getQuejidoEspiratorio().charValue() != hojaConsulta.getQuejidoEspiratorio().charValue() ||
                mCorrienteRespiratorioSintomas.getRespiracionRapida().charValue() != hojaConsulta.getRespiracionRapida().charValue() ||
                mCorrienteRespiratorioSintomas.getRinorrea().charValue() != hojaConsulta.getRinorrea().charValue() ||
                mCorrienteRespiratorioSintomas.getRoncos().charValue() != hojaConsulta.getRoncos().charValue() ||
                mCorrienteRespiratorioSintomas.getSibilancias().charValue() != hojaConsulta.getSibilancias().charValue() ||
                mCorrienteRespiratorioSintomas.getTirajeSubcostal().charValue() != hojaConsulta.getTirajeSubcostal().charValue() ||
                mCorrienteRespiratorioSintomas.getTos().charValue() != hojaConsulta.getTos().charValue()) {
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

                    RESPUESTA = SINTOMASWS.guardarRespiratorioSintomas(mHojaConsulta, mUsuarioLogiado);
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
            private ResultadoObjectWSDTO<RespiratorioSintomasDTO> RESPUESTA;
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
                RESPUESTA = new ResultadoObjectWSDTO<RespiratorioSintomasDTO>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerRespiratorioSintomas(hojaConsulta);
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
                    mCorrienteRespiratorioSintomas = RESPUESTA.getObjecRespuesta();
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
                RespiratorioSintomasDTO respiratorioSintomas = RESPUESTA.getObjecRespuesta();

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbTOSSENSintoma),
                        findViewById(R.id.chkbTOSNENSintoma), findViewById(R.id.chkbTOSDENSintoma),
                        ((respiratorioSintomas.getTos() != null) ? respiratorioSintomas.getTos().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRNRSENSintoma),
                        findViewById(R.id.chkbRNRNENSintoma), findViewById(R.id.chkbRNRNENSintoma),
                        ((respiratorioSintomas.getRinorrea() != null) ? respiratorioSintomas.getRinorrea().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbCGNSENSintoma),
                        findViewById(R.id.chkbCGNNENSintoma), findViewById(R.id.chkbCGNDENSintoma),
                        ((respiratorioSintomas.getCongestionNasal() != null) ? respiratorioSintomas.getCongestionNasal().charValue()
                                : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbOTGSENSintoma),
                        findViewById(R.id.chkbOTGNENSintoma), findViewById(R.id.chkbOTGDENSintoma),
                        ((respiratorioSintomas.getOtalgia() != null) ? respiratorioSintomas.getOtalgia().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbALNSENSintoma),
                        findViewById(R.id.chkbALNNENSintoma),
                        ((respiratorioSintomas.getAleteoNasal() != null) ? respiratorioSintomas.getAleteoNasal().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbAPNSENSintoma),
                        findViewById(R.id.chkbAPNNENSintoma), ((respiratorioSintomas.getApnea() != null)
                                ? respiratorioSintomas.getApnea().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRSRSENSintoma),
                        findViewById(R.id.chkbRSRNENSintoma), ((respiratorioSintomas.getRespiracionRapida() != null)
                                ? respiratorioSintomas.getRespiracionRapida().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbQJESENSintoma),
                        findViewById(R.id.chkbQJENENSintoma), ((respiratorioSintomas.getQuejidoEspiratorio() != null)
                                ? respiratorioSintomas.getQuejidoEspiratorio().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbESRSENSintoma),
                        findViewById(R.id.chkbESRNENSintoma), ((respiratorioSintomas.getEstiradorReposo() != null)
                                ? respiratorioSintomas.getEstiradorReposo().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbTISSENSintoma),
                        findViewById(R.id.chkbTISNENSintoma), ((respiratorioSintomas.getTirajeSubcostal() != null)
                                ? respiratorioSintomas.getTirajeSubcostal().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbSIBSENSintoma),
                        findViewById(R.id.chkbSIBNENSintoma), ((respiratorioSintomas.getSibilancias() != null)
                                ? respiratorioSintomas.getSibilancias().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbCEPSENSintoma),
                        findViewById(R.id.chkbCEPNENSintoma), ((respiratorioSintomas.getCrepitos() != null)
                                ? respiratorioSintomas.getCrepitos().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbRNCSENSintoma),
                        findViewById(R.id.chkbRNCNENSintoma), ((respiratorioSintomas.getRoncos() != null)
                                ? respiratorioSintomas.getRoncos().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbOTFSENSintoma),
                        findViewById(R.id.chkbOTFNENSintoma), ((respiratorioSintomas.getOtraFif() != null)
                                ? respiratorioSintomas.getOtraFif().charValue() : '4'));

                if( !StringUtils.isNullOrEmpty(respiratorioSintomas.getNuevaFif()) ) {
                    ((EditText) findViewById(R.id.dpNVF)).setText((respiratorioSintomas.getNuevaFif()));
                }

                if(!((CheckBox) findViewById(R.id.chkbOTFSENSintoma)).isChecked()){
                    findViewById(R.id.txtvNVFENSintoma).setVisibility(View.INVISIBLE);
                    findViewById(R.id.dpNVF).setVisibility(View.INVISIBLE);
                }
            }
        };
        lecturaTask.execute((Void[])null);
    }
}
