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
import android.text.InputFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.GastroinstestinalSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.DecimalDigitsInputFilter;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

/**
 * Controlador de la UI Gastroinstetinal.
 */
public class GastrointestinalSintomasActivity extends ActionBarActivity {

    private Context CONTEXT;
    HojaConsultaDTO hojaConsulta;
    HojaConsultaDTO hcGastronIn;
    private String vFueraRango = new String();
    private String mUsuarioLogiado;
    private TextView viewTxtvNSintoma;
    private TextView viewTxtvSSintoma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastrointestinal_sintoma);

        this.CONTEXT = this;

        final ActionBar actionBar = getSupportActionBar();

        findViewById(R.id.edtxtHepaCmSintoma).setVisibility(View.INVISIBLE);
        ((EditText)findViewById(R.id.edtxtHepaCmSintoma)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
        findViewById(R.id.edtxtVm12h).setVisibility(View.INVISIBLE);

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        viewTxtvNSintoma = (TextView) findViewById(R.id.txtvNSintoma);
        viewTxtvSSintoma = (TextView) findViewById(R.id.txtvSSintoma);

        viewTxtvNSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSSintoma.setOnClickListener(new View.OnClickListener() {
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

    public void onChkboxClickedPocoAptGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbPocoAptSGTSintoma), findViewById(R.id.chkbPocoAptNGTSintoma),
                findViewById(R.id.chkbPocoAptDGTSintoma), view);
    }

    public void onChkboxClickedNauseaGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbNauseaSGTSintoma), findViewById(R.id.chkbNauseaNGTSintoma),
                findViewById(R.id.chkbNauseaDGTSintoma), view);
    }

    public void onChkboxClickedDAGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbDASGTSintoma), findViewById(R.id.chkbDANGTSintoma),
                findViewById(R.id.chkbDADGTSintoma), view);
    }

    public void onChkboxClickedVomGT(View view) {

        switch (view.getId()) {
            case R.id.chkbVomSGTSintoma :
                ((CheckBox)findViewById(R.id.chkbVomSGTSintoma)).setChecked(true);
                ((CheckBox)findViewById(R.id.chkbVomNGTSintoma)).setChecked(false);
                ((CheckBox)findViewById(R.id.chkbVomDGTSintoma)).setChecked(false);
                findViewById(R.id.edtxtVm12h).setVisibility(View.VISIBLE);
                break;
            case R.id.chkbVomNGTSintoma :
                ((CheckBox)findViewById(R.id.chkbVomSGTSintoma)).setChecked(false);
                ((CheckBox)findViewById(R.id.chkbVomNGTSintoma)).setChecked(true);
                ((CheckBox)findViewById(R.id.chkbVomDGTSintoma)).setChecked(false);
                findViewById(R.id.edtxtVm12h).setVisibility(View.INVISIBLE);
                ((EditText)findViewById(R.id.edtxtVm12h)).setText("");
                break;
            case R.id.chkbVomDGTSintoma :
                ((CheckBox)findViewById(R.id.chkbVomSGTSintoma)).setChecked(false);
                ((CheckBox)findViewById(R.id.chkbVomNGTSintoma)).setChecked(false);
                ((CheckBox)findViewById(R.id.chkbVomDGTSintoma)).setChecked(true);
                findViewById(R.id.edtxtVm12h).setVisibility(View.INVISIBLE);
                ((EditText)findViewById(R.id.edtxtVm12h)).setText("");
                break;
        }
        /*AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbVomSGTSintoma), findViewById(R.id.chkbVomNGTSintoma),
                findViewById(R.id.chkbVomDGTSintoma), view);*/

    }

    public void onChkboxClickedDiaGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbDiaSGTSintoma), findViewById(R.id.chkbDiaNGTSintoma),
                findViewById(R.id.chkbDiaDGTSintoma), view);
    }

    public void onChkboxClickedDiaSGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbDiaSSGTSintoma), findViewById(R.id.chkbDiaSNGTSintoma),
                findViewById(R.id.chkbDiaSDGTSintoma), view);
    }

    public void onChkboxClickedEstGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbEstSGTSintoma), findViewById(R.id.chkbEstNGTSintoma),
                findViewById(R.id.chkbEstDGTSintoma), view);
    }

    public void onChkboxClickedDaiGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbDaiSGTSintoma), findViewById(R.id.chkbDaiNGTSintoma),
                findViewById(R.id.chkbDaiDGTSintoma), view);
    }

    public void onChkboxClickedDacGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbDacSGTSintoma), findViewById(R.id.chkbDacNGTSintoma),
                findViewById(R.id.chkbDacDGTSintoma), view);
    }

    public void onChkboxClickedEpiGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbEpiSGTSintoma), findViewById(R.id.chkbEpiNGTSintoma),
                findViewById(R.id.chkbEpiDGTSintoma), view);
    }

    public void onChkboxClickedInvGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbInvSGTSintoma), findViewById(R.id.chkbInvNGTSintoma),
                findViewById(R.id.chkbInvDGTSintoma), view);
    }

    public void onChkboxClickedDABGT(View view) {
        AndroidUtils.controlarCheckBoxGroup(findViewById(R.id.chkbDABSGTSintoma), findViewById(R.id.chkbDABNGTSintoma),
                findViewById(R.id.chkbDABDGTSintoma), view);
    }

    public void onChkboxClickedHepaGT(View view) {

        switch (view.getId()) {
            case R.id.chkbHepaSGTSintoma :
                ((CheckBox)findViewById(R.id.chkbHepaNGTSintoma)).setChecked(false);
                ((CheckBox)findViewById(R.id.chkbHepaSGTSintoma)).setChecked(true);
                findViewById(R.id.edtxtHepaCmSintoma).setVisibility(View.VISIBLE);
                break;
            case R.id.chkbHepaNGTSintoma :
                ((CheckBox)findViewById(R.id.chkbHepaSGTSintoma)).setChecked(false);
                ((CheckBox)findViewById(R.id.chkbHepaNGTSintoma)).setChecked(true);
                findViewById(R.id.edtxtHepaCmSintoma).setVisibility(View.INVISIBLE);
                ((EditText)findViewById(R.id.edtxtHepaCmSintoma)).setText("");
                break;
        }
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
                            ((CheckBox) findViewById(R.id.chkbPocoAptNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbNauseaNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbDANGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbVomNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbDiaNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbDiaSNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbEstNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbDaiNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbDacNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbEpiNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbInvNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbDABNGTSintoma)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkbHepaNGTSintoma)).setChecked(!valor);

                            ((CheckBox) findViewById(R.id.chkbPocoAptSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbNauseaSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbDASGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbVomSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbDiaSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbDiaSSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbEstSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbDaiSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbDacSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbEpiSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbInvSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbDABSGTSintoma)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkbHepaSGTSintoma)).setChecked(valor);

                            ((CheckBox) findViewById(R.id.chkbPocoAptDGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbNauseaDGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbDADGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbVomDGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbDiaDGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbDiaSDGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEstDGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbDaiDGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbDacDGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbEpiDGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbInvDGTSintoma)).setChecked(false);
                            ((CheckBox) findViewById(R.id.chkbDABDGTSintoma)).setChecked(false);



                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_gastro_intestinal_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_gastro_intestinal_sintomas)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void onClick_btnGastrointestinal(View view) {
        try{
            vFueraRango = "";
            validarCampos();
            hojaConsulta = cargarHojaConsulta();

            InicioDTO pacienteSeleccionado = (InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado");

            if (pacienteSeleccionado.getCodigoEstado() == '7') {
                if(hcGastronIn != null) {
                    if (tieneCambios(hojaConsulta)) {
                        DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        decideGuardado();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        regresarPantallaAnterior();
                                        break;
                                }
                            }
                        };
                        MensajesHelper.mostrarMensajeYesNo(this,
                                getResources().getString(
                                        R.string.msj_cambio_hoja_consulta), getResources().getString(
                                        R.string.title_estudio_sostenible),
                                preguntaDialogClickListener);
                    }else {
                        decideGuardado();
                    }
                }else{
                    decideGuardado();
                }
            }else{
                decideGuardado();
            }


        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    private void regresarPantallaAnterior() {
        InicioDTO pacienteSeleccionado = (InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado");

        Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
        intent.putExtra("indice",0);
        intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);

        startActivity(intent);
        finish();
    }

    private boolean tieneCambios(HojaConsultaDTO hojaconsulta) {
        if( hcGastronIn.getPocoApetito().charValue() != '4' && hcGastronIn.getPocoApetito().charValue() != hojaconsulta.getPocoApetito().charValue() ){
            return true;
        }

        if( hcGastronIn.getNausea().charValue() != '4' && hcGastronIn.getNausea().charValue() != hojaconsulta.getNausea().charValue() ){
            return true;
        }

        if( hcGastronIn.getDificultadAlimentarse().charValue() != '4' && hcGastronIn.getDificultadAlimentarse().charValue() != hojaconsulta.getDificultadAlimentarse().charValue() ){
            return true;
        }

        if( hcGastronIn.getVomito12horas().charValue() != '4' && hcGastronIn.getVomito12horas().charValue() != hojaconsulta.getVomito12horas().charValue() ){
            return true;
        }

        if( hcGastronIn.getDiarrea().charValue() != '4' && hcGastronIn.getDiarrea().charValue() != hojaconsulta.getDiarrea().charValue() ){
            return true;
        }

        if( hcGastronIn.getDiarreaSangre().charValue() != '4' && hcGastronIn.getDiarreaSangre().charValue() != hojaconsulta.getDiarreaSangre().charValue() ){
            return true;
        }

        if( hcGastronIn.getEstrenimiento().charValue() != '4' && hcGastronIn.getEstrenimiento().charValue() != hojaconsulta.getEstrenimiento().charValue() ){
            return true;
        }

        if( hcGastronIn.getDolorAbIntermitente().charValue() != '4' && hcGastronIn.getDolorAbIntermitente().charValue() != hojaconsulta.getDolorAbIntermitente().charValue() ){
            return true;
        }

        if( hcGastronIn.getDolorAbContinuo().charValue() != '4' && hcGastronIn.getDolorAbContinuo().charValue() != hojaconsulta.getDolorAbContinuo().charValue() ){
            return true;
        }

        if( hcGastronIn.getEpigastralgia().charValue() != '4' && hcGastronIn.getEpigastralgia().charValue() != hojaconsulta.getEpigastralgia().charValue() ){
            return true;
        }

        if( hcGastronIn.getIntoleranciaOral().charValue() != '4' && hcGastronIn.getIntoleranciaOral().charValue() != hojaconsulta.getIntoleranciaOral().charValue() ){
            return true;
        }

        if( hcGastronIn.getDistensionAbdominal().charValue() != '4' && hcGastronIn.getDistensionAbdominal().charValue() != hojaconsulta.getDistensionAbdominal().charValue() ){
            return true;
        }

        if( hcGastronIn.getHepatomegalia().charValue() != '4' && hcGastronIn.getHepatomegalia().charValue() != hojaconsulta.getHepatomegalia().charValue() ){
            return true;
        }

        return false;
    }

    public void decideGuardado(){
        if(!StringUtils.isNullOrEmpty(vFueraRango)) {
            DialogInterface.OnClickListener preguntaEnviarDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            llamarGuardadoServicio(hojaConsulta);
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
                    R.string.title_estudio_sostenible), preguntaEnviarDialogClickListener);
        }else {
            llamarGuardadoServicio(hojaConsulta);
        }
    }

    public void validarCampos() throws Exception {
        EditText edtxtHepaCmSintoma = (EditText) findViewById(R.id.edtxtHepaCmSintoma);
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbPocoAptSGTSintoma), findViewById(R.id.chkbPocoAptNGTSintoma),
                    findViewById(R.id.chkbPocoAptDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbNauseaSGTSintoma), findViewById(R.id.chkbNauseaNGTSintoma),
                    findViewById(R.id.chkbNauseaDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));

        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbDASGTSintoma), findViewById(R.id.chkbDANGTSintoma),
                    findViewById(R.id.chkbDADGTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbVomSGTSintoma), findViewById(R.id.chkbVomNGTSintoma),
                    findViewById(R.id.chkbVomDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbDiaSGTSintoma), findViewById(R.id.chkbDiaNGTSintoma),
                    findViewById(R.id.chkbDiaDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbDiaSSGTSintoma), findViewById(R.id.chkbDiaSNGTSintoma),
                    findViewById(R.id.chkbDiaSDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbEstSGTSintoma), findViewById(R.id.chkbEstNGTSintoma),
                    findViewById(R.id.chkbEstDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbDaiSGTSintoma), findViewById(R.id.chkbDaiNGTSintoma),
                    findViewById(R.id.chkbDaiDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbDacSGTSintoma), findViewById(R.id.chkbDacNGTSintoma),
                    findViewById(R.id.chkbDacDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbEpiSGTSintoma), findViewById(R.id.chkbEpiNGTSintoma),
                    findViewById(R.id.chkbEpiDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbInvSGTSintoma), findViewById(R.id.chkbInvNGTSintoma),
                    findViewById(R.id.chkbInvDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbDABSGTSintoma), findViewById(R.id.chkbDABNGTSintoma),
                    findViewById(R.id.chkbDABDGTSintoma))){
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkbHepaSGTSintoma), findViewById(R.id.chkbHepaNGTSintoma))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (((CheckBox) findViewById(R.id.chkbHepaSGTSintoma)).isChecked() &&
                StringUtils.isNullOrEmpty(edtxtHepaCmSintoma.getText().toString())) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if (((CheckBox) findViewById(R.id.chkbVomSGTSintoma)).isChecked() &&
                StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtVm12h)).getText().toString())) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }


        //validando los rangos
        EditText edtVM12 = ((EditText)findViewById(R.id.edtxtVm12h));
        EditText edtHepa = ((EditText)findViewById(R.id.edtxtHepaCmSintoma));

        int cont = 0;
        if(edtVM12.getVisibility() == View.VISIBLE && !StringUtils.isNullOrEmpty(edtVM12.getText().toString())
                && !estaEnRango(0, 24, edtVM12.getText().toString())){
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_vomito_ultima_12_horas));
            cont++;
        }

        if(edtHepa.getVisibility() == View.VISIBLE && !StringUtils.isNullOrEmpty(edtHepa.getText().toString())
                && !estaEnRango(0, 5, edtHepa.getText().toString())){
           vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_hepatomegalia_cm));
            cont++;
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

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setPocoApetito(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbPocoAptSGTSintoma), findViewById(R.id.chkbPocoAptNGTSintoma),
                findViewById(R.id.chkbPocoAptDGTSintoma)));

        hojaConsulta.setNausea(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbNauseaSGTSintoma), findViewById(R.id.chkbNauseaNGTSintoma),
                findViewById(R.id.chkbNauseaDGTSintoma)));

        hojaConsulta.setDificultadAlimentarse(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbDASGTSintoma), findViewById(R.id.chkbDANGTSintoma),
                findViewById(R.id.chkbDADGTSintoma)));
            
        hojaConsulta.setVomito12horas(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbVomSGTSintoma), findViewById(R.id.chkbVomNGTSintoma),
                findViewById(R.id.chkbVomDGTSintoma)));

        if(((CheckBox)findViewById(R.id.chkbVomSGTSintoma)).isChecked() &&
                !StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.edtxtVm12h)).getText().toString())) {
            hojaConsulta.setVomito12h(Short.parseShort(((EditText)findViewById(R.id.edtxtVm12h)).getText().toString()));
        } else {
            hojaConsulta.setVomito12h(null);
        }

        hojaConsulta.setDiarrea(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbDiaSGTSintoma), findViewById(R.id.chkbDiaNGTSintoma),
                findViewById(R.id.chkbDiaDGTSintoma)));
            
        hojaConsulta.setDiarreaSangre(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbDiaSSGTSintoma), findViewById(R.id.chkbDiaSNGTSintoma),
                findViewById(R.id.chkbDiaSDGTSintoma)));
            
        hojaConsulta.setEstrenimiento(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbEstSGTSintoma), findViewById(R.id.chkbEstNGTSintoma),
                findViewById(R.id.chkbEstDGTSintoma)));
            
        hojaConsulta.setDolorAbIntermitente(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbDaiSGTSintoma), findViewById(R.id.chkbDaiNGTSintoma),
                findViewById(R.id.chkbDaiDGTSintoma)));
            
        hojaConsulta.setDolorAbContinuo(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbDacSGTSintoma), findViewById(R.id.chkbDacNGTSintoma),
                findViewById(R.id.chkbDacDGTSintoma)));

        hojaConsulta.setEpigastralgia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbEpiSGTSintoma), findViewById(R.id.chkbEpiNGTSintoma),
                findViewById(R.id.chkbEpiDGTSintoma)));

        hojaConsulta.setIntoleranciaOral(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbInvSGTSintoma), findViewById(R.id.chkbInvNGTSintoma),
                findViewById(R.id.chkbInvDGTSintoma)));

        hojaConsulta.setDistensionAbdominal(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbDABSGTSintoma), findViewById(R.id.chkbDABNGTSintoma),
                findViewById(R.id.chkbDABDGTSintoma)));

        hojaConsulta.setHepatomegalia(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkbHepaSGTSintoma), findViewById(R.id.chkbHepaNGTSintoma)));

        if(((CheckBox)findViewById(R.id.chkbHepaSGTSintoma)).isChecked() &&
                !StringUtils.isNullOrEmpty(((EditText)findViewById(R.id.edtxtHepaCmSintoma)).getText().toString())) {
            hojaConsulta.setHepatomegaliaCm(Double.parseDouble(((EditText)findViewById(R.id.edtxtHepaCmSintoma)).getText().toString()));
        } else
            hojaConsulta.setHepatomegaliaCm(null);
        
        return hojaConsulta;
    }

    private void llamarGuardadoServicio(final HojaConsultaDTO hojaConsulta){
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

                    RESPUESTA = SINTOMASWS.guardarGastroinstSintomas(hojaConsulta, mUsuarioLogiado);
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
            private ResultadoObjectWSDTO<GastroinstestinalSintomasDTO> RESPUESTA;
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
                RESPUESTA = new ResultadoObjectWSDTO<GastroinstestinalSintomasDTO>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){
                    HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

                    hojaConsulta.setSecHojaConsulta(((InicioDTO)getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    RESPUESTA = SINTOMASWS.obtenerGastroinstestinalSintomas(hojaConsulta);
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
                GastroinstestinalSintomasDTO gastroinstestinalSintomas = RESPUESTA.getObjecRespuesta();

                hcGastronIn = new HojaConsultaDTO();

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbPocoAptSGTSintoma), findViewById(R.id.chkbPocoAptNGTSintoma),
                        findViewById(R.id.chkbPocoAptDGTSintoma), ((gastroinstestinalSintomas.getPocoApetito() != null)
                                ? gastroinstestinalSintomas.getPocoApetito().charValue() : '4'));
                hcGastronIn.setPocoApetito(((gastroinstestinalSintomas.getPocoApetito() != null)
                        ? gastroinstestinalSintomas.getPocoApetito().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbNauseaSGTSintoma), findViewById(R.id.chkbNauseaNGTSintoma),
                        findViewById(R.id.chkbNauseaDGTSintoma), ((gastroinstestinalSintomas.getNausea() != null)
                                ? gastroinstestinalSintomas.getNausea().charValue() : '4'));
                hcGastronIn.setNausea(((gastroinstestinalSintomas.getNausea() != null)
                        ? gastroinstestinalSintomas.getNausea().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbDASGTSintoma), findViewById(R.id.chkbDANGTSintoma),
                        findViewById(R.id.chkbDADGTSintoma), ((gastroinstestinalSintomas.getDificultadAlimentarse() != null)
                                ? gastroinstestinalSintomas.getDificultadAlimentarse().charValue() : '4'));
                hcGastronIn.setDificultadAlimentarse(((gastroinstestinalSintomas.getDificultadAlimentarse() != null)
                        ? gastroinstestinalSintomas.getDificultadAlimentarse().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbVomSGTSintoma), findViewById(R.id.chkbVomNGTSintoma),
                        findViewById(R.id.chkbVomDGTSintoma), ((gastroinstestinalSintomas.getVomitoHoras() != null)
                                ? gastroinstestinalSintomas.getVomitoHoras().charValue() : '4'));
                hcGastronIn.setVomito12horas(((gastroinstestinalSintomas.getVomitoHoras() != null)
                        ? gastroinstestinalSintomas.getVomitoHoras().charValue() : '4'));

                if(gastroinstestinalSintomas.getVomitoHoras() != null &&
                        gastroinstestinalSintomas.getVomitoHoras().charValue() == '0') {
                    ((EditText)findViewById(R.id.edtxtVm12h)).setVisibility(View.VISIBLE);
                    ((EditText)findViewById(R.id.edtxtVm12h)).setText(((gastroinstestinalSintomas.getVomito12h() != null)
                            ? gastroinstestinalSintomas.getVomito12h().toString() : ""));
                }

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbDiaSGTSintoma), findViewById(R.id.chkbDiaNGTSintoma),
                        findViewById(R.id.chkbDiaDGTSintoma), ((gastroinstestinalSintomas.getDiarrea() != null)
                                ? gastroinstestinalSintomas.getDiarrea().charValue() : '4'));
                hcGastronIn.setDiarrea(((gastroinstestinalSintomas.getDiarrea() != null)
                        ? gastroinstestinalSintomas.getDiarrea().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbDiaSSGTSintoma), findViewById(R.id.chkbDiaSNGTSintoma),
                        findViewById(R.id.chkbDiaSDGTSintoma), ((gastroinstestinalSintomas.getDiarreaSangre() != null)
                                ? gastroinstestinalSintomas.getDiarreaSangre().charValue() : '4'));
                hcGastronIn.setDiarreaSangre(((gastroinstestinalSintomas.getDiarreaSangre() != null)
                        ? gastroinstestinalSintomas.getDiarreaSangre().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbEstSGTSintoma), findViewById(R.id.chkbEstNGTSintoma),
                        findViewById(R.id.chkbEstDGTSintoma), ((gastroinstestinalSintomas.getEstrenimiento() != null)
                                ? gastroinstestinalSintomas.getEstrenimiento().charValue() : '4'));
                hcGastronIn.setEstrenimiento(((gastroinstestinalSintomas.getEstrenimiento() != null)
                        ? gastroinstestinalSintomas.getEstrenimiento().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbDaiSGTSintoma), findViewById(R.id.chkbDaiNGTSintoma),
                        findViewById(R.id.chkbDaiDGTSintoma), ((gastroinstestinalSintomas.getDolorAbIntermitente() != null)
                                ? gastroinstestinalSintomas.getDolorAbIntermitente().charValue() : '4'));
                hcGastronIn.setDolorAbIntermitente(((gastroinstestinalSintomas.getDolorAbIntermitente() != null)
                        ? gastroinstestinalSintomas.getDolorAbIntermitente().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbDacSGTSintoma), findViewById(R.id.chkbDacNGTSintoma),
                        findViewById(R.id.chkbDacDGTSintoma), ((gastroinstestinalSintomas.getDolorAbContinuo() != null)
                                ? gastroinstestinalSintomas.getDolorAbContinuo().charValue() : '4'));
                hcGastronIn.setDolorAbContinuo(((gastroinstestinalSintomas.getDolorAbContinuo() != null)
                        ? gastroinstestinalSintomas.getDolorAbContinuo().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbEpiSGTSintoma), findViewById(R.id.chkbEpiNGTSintoma),
                        findViewById(R.id.chkbEpiDGTSintoma), ((gastroinstestinalSintomas.getEpigastralgia() != null)
                                ? gastroinstestinalSintomas.getEpigastralgia().charValue() : '4'));
                hcGastronIn.setEpigastralgia(((gastroinstestinalSintomas.getEpigastralgia() != null)
                        ? gastroinstestinalSintomas.getEpigastralgia().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbInvSGTSintoma), findViewById(R.id.chkbInvNGTSintoma),
                        findViewById(R.id.chkbInvDGTSintoma), ((gastroinstestinalSintomas.getIntoleranciaOral() != null)
                                ? gastroinstestinalSintomas.getIntoleranciaOral().charValue() : '4'));
                hcGastronIn.setIntoleranciaOral(((gastroinstestinalSintomas.getIntoleranciaOral() != null)
                        ? gastroinstestinalSintomas.getIntoleranciaOral().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbDABSGTSintoma), findViewById(R.id.chkbDABNGTSintoma),
                        findViewById(R.id.chkbDABDGTSintoma), ((gastroinstestinalSintomas.getDistensionAbdominal() != null)
                                ? gastroinstestinalSintomas.getDistensionAbdominal().charValue() : '4'));
                hcGastronIn.setDistensionAbdominal(((gastroinstestinalSintomas.getDistensionAbdominal() != null)
                        ? gastroinstestinalSintomas.getDistensionAbdominal().charValue() : '4'));

                AndroidUtils.establecerCheckboxGuardado(findViewById(R.id.chkbHepaSGTSintoma), findViewById(R.id.chkbHepaNGTSintoma),
                        ((gastroinstestinalSintomas.getHepatomegalia() != null)
                                ? gastroinstestinalSintomas.getHepatomegalia().charValue() : '4'));
                hcGastronIn.setHepatomegalia(((gastroinstestinalSintomas.getHepatomegalia() != null)
                        ? gastroinstestinalSintomas.getHepatomegalia().charValue() : '4'));

                if(gastroinstestinalSintomas.getHepatomegalia() != null &&
                        gastroinstestinalSintomas.getHepatomegalia().charValue() == '0') {
                    ((EditText)findViewById(R.id.edtxtHepaCmSintoma)).setVisibility(View.VISIBLE);
                    ((EditText)findViewById(R.id.edtxtHepaCmSintoma)).setText(((gastroinstestinalSintomas.getHepatomegaliaCm() != null)
                            ? gastroinstestinalSintomas.getHepatomegaliaCm().toString() : ""));
                }
            }
        };
        lecturaTask.execute((Void[])null);
    }

}
