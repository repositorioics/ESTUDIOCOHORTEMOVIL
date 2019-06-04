package com.sts_ni.estudiocohortecssfv.diagnostiscoactivities;

import android.app.Activity;
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
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.ConsultaActivity;
import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.DiagnosticoWS;

/**
 * Controlador de la UI Tratamiento.
 */
public class DiagnosticoTratamientoActivity extends ActionBarActivity
{
    public Context CONTEXT;

    public  Activity ACTIVITY;
    public Integer  SEC_HOJA_CONSULTA ;
    InicioDTO pacienteSeleccionado;
    HojaConsultaDTO hcTratamiento;
    private HojaConsultaDTO hojaConsulta;
    private String mUsuarioLogiado;
    private TextView viewTxtvNo;
    private TextView viewTxtvSi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostico_tratamiento);

        final ActionBar actionBar = getSupportActionBar();
        pacienteSeleccionado = (InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado");
        SEC_HOJA_CONSULTA=pacienteSeleccionado.getIdObjeto();
        cargarTratamientoTask task = new cargarTratamientoTask(this, this);
        task.execute((Void) null);

       actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_title_center);
        ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
        ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());

        this.CONTEXT = this;

        this.ACTIVITY = this;

        this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();

        viewTxtvNo = (TextView) findViewById(R.id.txtNo);
        viewTxtvSi = (TextView) findViewById(R.id.txtvSi);

        viewTxtvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, false);
            }
        });

        viewTxtvSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, true);
            }
        });

        // Setiando el peso y la temperatura para visualizarlos en la pantalla
        CabeceraSintomaDTO CabeceraSintomaDTO = ((CabeceraSintomaDTO) getIntent().getSerializableExtra("cabeceraSintoma"));

        EditText edtxtTratamientoPeso = (EditText)findViewById(R.id.edtxtTratamientoPeso);
        EditText edtxtTratamientoTalla = (EditText)findViewById(R.id.edtxtTratamientoTalla);
        edtxtTratamientoPeso.setText(String.valueOf(CabeceraSintomaDTO.getPesoKg()));
        edtxtTratamientoTalla.setText(String.valueOf(CabeceraSintomaDTO.getTallaCm()));
        // ---------------------------------------------------------------------

        // funcion para hacer el texto escroleable
        EditText edtxtPlanes = (EditText)findViewById(R.id.edtxtPlanes);
        /*edtxtPlanes.setVerticalScrollBarEnabled(true);
        edtxtPlanes.setMovementMethod(new ScrollingMovementMethod());*/
        edtxtPlanes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view.getId() == R.id.edtxtPlanes) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        //----------------------------------------------------------------------
    }

    @Override
    public void onBackPressed() {
        return;
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
                            ((CheckBox) findViewById(R.id.chkIbAcetamenofenNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkASANo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkIbuprofenNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkPenicilinaNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkAmoxicilinaNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkDicloxacilinaNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkOtrosNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkFurazolidonaNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkMetroTinidazolNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkAlbendazolMebendazolNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkSulfatoFerrosoNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkSueroOralNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkSulfatoZincNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkLiquidosIVNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkPrednisonaNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkHidrocortisonaNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkSalutamolNo)).setChecked(!valor);
                            ((CheckBox) findViewById(R.id.chkOseltamivirNo)).setChecked(!valor);

                            ((CheckBox) findViewById(R.id.chkIbAcetamenofenSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkASASi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkIbuProfenSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkPenicilinaSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkAmoxicilinaSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkDicloxacilinaSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkOtrosSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkFurazolidonaSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkMetroTinidazolSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkAlbendazolMebendazolSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkSulfatoFerrosoSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkSueroOralSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkSulfatoZincSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkLiquidosIVSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkPrednisonaSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkHidrocortisonaSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkSalutamolSi)).setChecked(valor);
                            ((CheckBox) findViewById(R.id.chkOseltamivirSi)).setChecked(valor);

                            if(((CheckBox) findViewById(R.id.chkOtrosSi)).isChecked()){
                                findViewById(R.id.edtxtOtro).setEnabled(true);
                            }
                            else {
                                ((EditText) findViewById(R.id.edtxtOtro)).setText("");
                                findViewById(R.id.edtxtOtro).setEnabled(false);
                            }

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            if (valor) {
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_yes), getResources().getString(R.string.boton_diagnostico_Tratamiento)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
            else{
                MensajesHelper.mostrarMensajeYesNo(this,
                        String.format(getResources().getString(R.string.msg_change_no),getResources().getString(R.string.boton_diagnostico_Tratamiento)), getResources().getString(
                                R.string.title_estudio_sostenible),
                        preguntaDialogClickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    public void onClick_btnTratamiento( View view){
        try{

            pacienteSeleccionado = (InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado");
            SEC_HOJA_CONSULTA=  pacienteSeleccionado.getIdObjeto();
            validarCampos();
            hojaConsulta = cargarHojaConsulta();

            if (pacienteSeleccionado.getCodigoEstado() == '7') {
                if(hcTratamiento != null) {
                    if (tieneCambios(hojaConsulta)) {
                        DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        guardarTratamiento(hojaConsulta);
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

                    }else{
                        regresarPantallaAnterior();
                    }
                }else{
                    guardarTratamiento(hojaConsulta);
                }
            }else{
                guardarTratamiento(hojaConsulta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.app_name), null);
        }

    }

    private void regresarPantallaAnterior() {
        Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
        intent.putExtra("indice", 2);
        intent.putExtra("pacienteSeleccionado", pacienteSeleccionado);
        intent.putExtra("cabeceraSintoma", ((CabeceraSintomaDTO) getIntent().getSerializableExtra("cabeceraSintoma")));
        startActivity(intent);
        finish();
    }

    private boolean tieneCambios(HojaConsultaDTO hojaconsulta) {
        if(hojaconsulta.getAcetaminofen().charValue() != hcTratamiento.getAcetaminofen().charValue()){
            return true;
        }

        if(hojaconsulta.getAsa().charValue() != hcTratamiento.getAsa().charValue()){
            return true;
        }

        if(hojaconsulta.getIbuprofen().charValue() != hcTratamiento.getIbuprofen().charValue()){
            return true;
        }

        if(hojaconsulta.getPenicilina().charValue() != hcTratamiento.getPenicilina().charValue()){
            return true;
        }

        if(hojaconsulta.getAmoxicilina().charValue() != hcTratamiento.getAmoxicilina().charValue()){
            return true;
        }

        if(hojaconsulta.getDicloxacilina().charValue() != hcTratamiento.getDicloxacilina().charValue()){
            return true;
        }

        if(!hojaconsulta.getOtroAntibiotico().equalsIgnoreCase(hcTratamiento.getOtroAntibiotico())){
            return true;
        }

        if(hojaconsulta.getOtro().charValue() != hcTratamiento.getOtro().charValue()){
            return true;
        }

        if(hojaconsulta.getFurazolidona().charValue() != hcTratamiento.getFurazolidona().charValue()){
            return true;
        }

        if(hojaconsulta.getMetronidazolTinidazol().charValue() != hcTratamiento.getMetronidazolTinidazol().charValue()){
            return true;
        }

        if(hojaconsulta.getAlbendazolMebendazol().charValue() != hcTratamiento.getAlbendazolMebendazol().charValue()){
            return true;
        }

        if(hojaconsulta.getSulfatoFerroso().charValue() != hcTratamiento.getSulfatoFerroso().charValue()){
            return true;
        }

        if(hojaconsulta.getSueroOral().charValue() != hcTratamiento.getSueroOral().charValue()){
            return true;
        }

        if(hojaconsulta.getSulfatoZinc().charValue() != hcTratamiento.getSulfatoZinc().charValue()){
            return true;
        }

        if(hojaconsulta.getLiquidosIv().charValue() != hcTratamiento.getLiquidosIv().charValue()){
            return true;
        }

        if(hojaconsulta.getPrednisona().charValue() != hcTratamiento.getPrednisona().charValue()){
            return true;
        }

        if(hojaconsulta.getHidrocortisonaIv().charValue() != hcTratamiento.getHidrocortisonaIv().charValue()){
            return true;
        }

        if(hojaconsulta.getSalbutamol().charValue() != hcTratamiento.getSalbutamol().charValue()){
            return true;
        }

        if(hojaconsulta.getOseltamivir().charValue() != hcTratamiento.getOseltamivir().charValue()){
            return true;
        }

        if(!hojaconsulta.getPlanes().equalsIgnoreCase(hcTratamiento.getPlanes())){
            return true;
        }

        return false;
    }

    public void validarCampos() throws Exception {
        if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkIbAcetamenofenSi), findViewById(R.id.chkIbAcetamenofenNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkASASi), findViewById(R.id.chkASANo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkIbuProfenSi), findViewById(R.id.chkIbuprofenNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkPenicilinaSi), findViewById(R.id.chkPenicilinaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkAmoxicilinaSi), findViewById(R.id.chkAmoxicilinaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkDicloxacilinaSi), findViewById(R.id.chkDicloxacilinaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkOtrosSi), findViewById(R.id.chkOtrosNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkFurazolidonaSi), findViewById(R.id.chkFurazolidonaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkMetroTinidazolSi), findViewById(R.id.chkMetroTinidazolNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkAlbendazolMebendazolSi), findViewById(R.id.chkAlbendazolMebendazolNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        } else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkSulfatoFerrosoSi), findViewById(R.id.chkSulfatoFerrosoNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkSueroOralSi), findViewById(R.id.chkSueroOralNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkSulfatoZincSi), findViewById(R.id.chkSulfatoZincNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkLiquidosIVSi), findViewById(R.id.chkLiquidosIVNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkPrednisonaSi), findViewById(R.id.chkPrednisonaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkHidrocortisonaSi), findViewById(R.id.chkHidrocortisonaNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkSalutamolSi), findViewById(R.id.chkSalutamolNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(AndroidUtils.esChkboxsFalse(findViewById(R.id.chkOseltamivirSi), findViewById(R.id.chkOseltamivirNo))) {
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(((CheckBox) findViewById(R.id.chkOtrosSi)).isChecked() && StringUtils.isNullOrEmpty( ((EditText) findViewById(R.id.edtxtOtro)).getText().toString()) ){
            throw new Exception(getString(R.string.msj_completar_informacion));
        }else if(StringUtils.isNullOrEmpty(((EditText) findViewById(R.id.edtxtPlanes)).getText().toString())) {
            throw new Exception(getString(R.string.msj_completar_los_planes));
        }
    }

    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setAcetaminofen(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkIbAcetamenofenSi), findViewById(R.id.chkIbAcetamenofenNo)));
        hojaConsulta.setAsa(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkASASi), findViewById(R.id.chkASANo)));
        hojaConsulta.setIbuprofen(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkIbuProfenSi), findViewById(R.id.chkIbuprofenNo)));
        hojaConsulta.setPenicilina(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkPenicilinaSi), findViewById(R.id.chkPenicilinaNo)));
        hojaConsulta.setAmoxicilina(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkAmoxicilinaSi), findViewById(R.id.chkAmoxicilinaNo)));
        hojaConsulta.setDicloxacilina( AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkDicloxacilinaSi), findViewById(R.id.chkDicloxacilinaNo)));
        hojaConsulta.setOtro( AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkOtrosSi), findViewById(R.id.chkOtrosNo)));
        hojaConsulta.setOtroAntibiotico(((EditText) findViewById(R.id.edtxtOtro)).getText().toString()) ;
        hojaConsulta.setFurazolidona(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkFurazolidonaSi), findViewById(R.id.chkFurazolidonaNo)));
        hojaConsulta.setMetronidazolTinidazol(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkMetroTinidazolSi), findViewById(R.id.chkMetroTinidazolNo)) );
        hojaConsulta.setAlbendazolMebendazol(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkAlbendazolMebendazolSi), findViewById(R.id.chkAlbendazolMebendazolNo)) );
        hojaConsulta.setSulfatoFerroso(AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkSulfatoFerrosoSi), findViewById(R.id.chkSulfatoFerrosoNo)) );
        hojaConsulta.setSueroOral( AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkSueroOralSi), findViewById(R.id.chkSueroOralNo)) );
        hojaConsulta.setSulfatoZinc( AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkSulfatoZincSi), findViewById(R.id.chkSulfatoZincNo))  );
        hojaConsulta.setLiquidosIv(  AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkLiquidosIVSi), findViewById(R.id.chkLiquidosIVNo))  );
        hojaConsulta.setPrednisona( AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkPrednisonaSi), findViewById(R.id.chkPrednisonaNo)) );
        hojaConsulta.setHidrocortisonaIv( AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkHidrocortisonaSi), findViewById(R.id.chkHidrocortisonaNo)));
        hojaConsulta.setSalbutamol( AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkSalutamolSi), findViewById(R.id.chkSalutamolNo)) );
        hojaConsulta.setOseltamivir( AndroidUtils.resultadoGenericoChkbSND(findViewById(R.id.chkOseltamivirSi), findViewById(R.id.chkOseltamivirNo))  );
        hojaConsulta.setPlanes(((EditText) findViewById(R.id.edtxtPlanes)).getText().toString());
        return hojaConsulta;
    }

    //funcion para guardar desde la pantalla de tratamiento
    private void guardarTratamiento(HojaConsultaDTO hojaconsulta){
        /*Creando una tarea asincrona*/
        AsyncTask<Object, Void, Void> HojaConsulta = new AsyncTask<Object, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private DiagnosticoWS diagnosticoWS = new DiagnosticoWS(getResources());


            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(CONTEXT);
                PD.setTitle(getResources().getString(R.string.msj_mensaje_enviado));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Object... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){

                    RESPUESTA = diagnosticoWS.guardarTratamiento((HojaConsultaDTO)params[0], mUsuarioLogiado);
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


                    Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
                    intent.putExtra("indice",2);
                    intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);
                    intent.putExtra("cabeceraSintoma", ((CabeceraSintomaDTO)getIntent().getSerializableExtra("cabeceraSintoma")));
                    startActivity(intent);
                    finish();

                }else if (RESPUESTA.getCodigoError().intValue() != 999){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.app_name), null);

                }else{
                    MensajesHelper.mostrarMensajeError(CONTEXT,
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.app_name), null);
                }
            }
        };
        HojaConsulta.execute(hojaconsulta);
    }

    public void onChkboxClickedTratamiento(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkIbAcetamenofenSi:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkIbAcetamenofenNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkASASi:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkASANo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkASANo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkASASi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkIbAcetamenofenNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkIbAcetamenofenSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkIbuProfenSi:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkIbuprofenNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkIbuprofenNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkIbuProfenSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkPenicilinaSi:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkPenicilinaNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkPenicilinaNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkPenicilinaSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkAmoxicilinaSi:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkAmoxicilinaNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkAmoxicilinaNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkAmoxicilinaSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkDicloxacilinaSi:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkDicloxacilinaNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkDicloxacilinaNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkDicloxacilinaSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkOtrosSi:
                if (checked){
                    ((CheckBox) findViewById(R.id.chkOtrosNo)).setChecked(false);
                    findViewById(R.id.edtxtOtro).setEnabled(true);
                } else {
                    ((CheckBox) view).setChecked(true);
                }
                break;
            case R.id.chkOtrosNo:
                if (checked) {
                    ((CheckBox) findViewById(R.id.chkOtrosSi)).setChecked(false);
                    ((EditText) findViewById(R.id.edtxtOtro)).setText("");
                    findViewById(R.id.edtxtOtro).setEnabled(false);
                }else {
                    ((CheckBox) view).setChecked(true);
                }
                break;

            case R.id.chkFurazolidonaSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkFurazolidonaNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkFurazolidonaNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkFurazolidonaSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkMetroTinidazolSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkMetroTinidazolNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkMetroTinidazolNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkMetroTinidazolSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkAlbendazolMebendazolSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkAlbendazolMebendazolNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkAlbendazolMebendazolNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkAlbendazolMebendazolSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkSulfatoFerrosoSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkSulfatoFerrosoNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkSulfatoFerrosoNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkSulfatoFerrosoSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkSueroOralSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkSueroOralNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkSueroOralNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkSueroOralSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkSulfatoZincSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkSulfatoZincNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkSulfatoZincNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkSulfatoZincSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkLiquidosIVSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkLiquidosIVNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkLiquidosIVNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkLiquidosIVSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkPrednisonaSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkPrednisonaNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkPrednisonaNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkPrednisonaSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkHidrocortisonaSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkHidrocortisonaNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkHidrocortisonaNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkHidrocortisonaSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;

            case R.id.chkSalutamolSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkSalutamolNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkSalutamolNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkSalutamolSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkOseltamivirSi :
                if (checked)
                    ((CheckBox) findViewById(R.id.chkOseltamivirNo)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
            case R.id.chkOseltamivirNo:
                if (checked)
                    ((CheckBox) findViewById(R.id.chkOseltamivirSi)).setChecked(false);
                else
                    ((CheckBox) view).setChecked(true);
                break;
        }
    }


    public class cargarTratamientoTask extends AsyncTask<Void, Void, Boolean> {

        private final Context CONTEXT;
        private final Activity ACTIVITY;
        private ResultadoListWSDTO<HojaConsultaDTO> RESPUESTA;
        private final ConnectivityManager CM;
        private final NetworkInfo NET_INFO;
        cargarTratamientoTask(Context context, Activity activity) {
            this.CONTEXT = context;
            this.ACTIVITY = activity;
            this.RESPUESTA = new ResultadoListWSDTO<HojaConsultaDTO>();
            this.CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            this.NET_INFO = this.CM.getActiveNetworkInfo();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (this.NET_INFO != null && this.NET_INFO.isConnected()) {

                ConsultaWS consultaWS = new ConsultaWS(getResources());
                this.RESPUESTA = consultaWS.getHojaConsultaPorNumero(SEC_HOJA_CONSULTA);

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

                if(this.RESPUESTA.getCodigoError().intValue() == 0){ // Sin errores

                    try {
                        hcTratamiento = new HojaConsultaDTO();
                        for (HojaConsultaDTO HojaConsultadto: this.RESPUESTA.getLstResultado()) {

                            if (HojaConsultadto.getAcetaminofen()=='1' || HojaConsultadto.getAcetaminofen()=='0' ){
                                ((CheckBox)this.ACTIVITY.findViewById(R.id.chkIbAcetamenofenSi)).setChecked( ( ( HojaConsultadto.getAcetaminofen())=='0'?true:false) ) ;
                                ((CheckBox)this.ACTIVITY.findViewById(R.id.chkIbAcetamenofenNo)).setChecked( ( ( HojaConsultadto.getAcetaminofen()  )=='0'?false:true) ) ;
                                hcTratamiento.setAcetaminofen(HojaConsultadto.getAcetaminofen());
                            }
                            if (HojaConsultadto.getAsa()=='1' || HojaConsultadto.getAsa()=='0' ){
                                ((CheckBox)this.ACTIVITY.findViewById(R.id.chkASASi)).setChecked( ( (HojaConsultadto.getAsa().charValue()  )=='0'?true:false) ) ;
                                ((CheckBox)this.ACTIVITY.findViewById(R.id.chkASANo)).setChecked( ( ( HojaConsultadto.getAsa().charValue() )=='0'?false:true) ) ;
                                hcTratamiento.setAsa(HojaConsultadto.getAsa());
                            }
                            if (HojaConsultadto.getIbuprofen()=='1' || HojaConsultadto.getIbuprofen()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkIbuProfenSi)).setChecked(((HojaConsultadto.getIbuprofen())) == '0' ? true : false);
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkIbuprofenNo)).setChecked(((HojaConsultadto.getIbuprofen()) == '0' ? false : true));
                                hcTratamiento.setIbuprofen(HojaConsultadto.getIbuprofen());
                            }
                            if (HojaConsultadto.getPenicilina()=='1' || HojaConsultadto.getPenicilina()=='0' ) {
                                ((CheckBox)this.ACTIVITY.findViewById(R.id.chkPenicilinaSi)).setChecked( ( (HojaConsultadto.getPenicilina()  )=='0'?true:false) ) ;
                                ((CheckBox)this.ACTIVITY.findViewById(R.id.chkPenicilinaNo)).setChecked( ( (HojaConsultadto.getPenicilina()  )=='0'?false:true) ) ;
                                hcTratamiento.setPenicilina(HojaConsultadto.getPenicilina());
                            }
                            if (HojaConsultadto.getAmoxicilina()=='1' || HojaConsultadto.getAmoxicilina()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkAmoxicilinaSi)).setChecked(((HojaConsultadto.getAmoxicilina()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkAmoxicilinaNo)).setChecked(((HojaConsultadto.getAmoxicilina()) == '0' ? false : true));
                                hcTratamiento.setAmoxicilina(HojaConsultadto.getAmoxicilina());
                            }
                            if (HojaConsultadto.getDicloxacilina()=='1' || HojaConsultadto.getDicloxacilina()=='0' ) {
                                ((CheckBox)this.ACTIVITY.findViewById(R.id.chkDicloxacilinaSi)).setChecked( ( (HojaConsultadto.getDicloxacilina()  )=='0'?true:false) ) ;
                                ((CheckBox)this.ACTIVITY.findViewById(R.id.chkDicloxacilinaNo)).setChecked( ( (HojaConsultadto.getDicloxacilina()  )=='0'?false:true) ) ;
                                hcTratamiento.setDicloxacilina(HojaConsultadto.getDicloxacilina());
                            }
                            if (HojaConsultadto.getOtro()=='1' || HojaConsultadto.getOtro()=='0' ) {
                                //OTRO ANTIBIOTICO
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkOtrosSi)).setChecked(((HojaConsultadto.getOtro()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkOtrosNo)).setChecked(((HojaConsultadto.getOtro()) == '0' ? false : true));
                                hcTratamiento.setOtro(HojaConsultadto.getOtro());
                            }
                            if (HojaConsultadto.getFurazolidona()=='1' || HojaConsultadto.getFurazolidona()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkFurazolidonaSi)).setChecked(((HojaConsultadto.getFurazolidona()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkFurazolidonaNo)).setChecked(((HojaConsultadto.getFurazolidona()) == '0' ? false : true));
                                hcTratamiento.setFurazolidona(HojaConsultadto.getFurazolidona());
                            }
                            if (HojaConsultadto.getMetronidazolTinidazol()=='1' || HojaConsultadto.getMetronidazolTinidazol()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkMetroTinidazolSi)).setChecked(((HojaConsultadto.getMetronidazolTinidazol()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkMetroTinidazolNo)).setChecked(((HojaConsultadto.getMetronidazolTinidazol()) == '0' ? false : true));
                                hcTratamiento.setMetronidazolTinidazol(HojaConsultadto.getMetronidazolTinidazol());
                            }
                            if (HojaConsultadto.getAlbendazolMebendazol()=='1' || HojaConsultadto.getAlbendazolMebendazol()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkAlbendazolMebendazolSi)).setChecked(((HojaConsultadto.getAlbendazolMebendazol()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkAlbendazolMebendazolNo)).setChecked(((HojaConsultadto.getAlbendazolMebendazol()) == '0' ? false : true));
                                hcTratamiento.setAlbendazolMebendazol(HojaConsultadto.getAlbendazolMebendazol());
                            }
                            if (HojaConsultadto.getSueroOral()=='1' || HojaConsultadto.getSueroOral()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkSueroOralSi)).setChecked(((HojaConsultadto.getSueroOral()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkSueroOralNo)).setChecked(((HojaConsultadto.getSueroOral()) == '0' ? false : true));
                                hcTratamiento.setSueroOral(HojaConsultadto.getSueroOral());
                            }
                            if (HojaConsultadto.getSulfatoFerroso()=='1' || HojaConsultadto.getSulfatoFerroso()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkSulfatoFerrosoSi)).setChecked(((HojaConsultadto.getSulfatoFerroso()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkSulfatoFerrosoNo)).setChecked(((HojaConsultadto.getSulfatoFerroso()) == '0' ? false : true));
                                hcTratamiento.setSulfatoFerroso(HojaConsultadto.getSulfatoFerroso());
                            }
                            if (HojaConsultadto.getSulfatoZinc()=='1' || HojaConsultadto.getSulfatoZinc()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkSulfatoZincSi)).setChecked(((HojaConsultadto.getSulfatoZinc()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkSulfatoZincNo)).setChecked(((HojaConsultadto.getSulfatoZinc()) == '0' ? false : true));
                                hcTratamiento.setSulfatoZinc(HojaConsultadto.getSulfatoZinc());
                            }
                            if (HojaConsultadto.getLiquidosIv()=='1' || HojaConsultadto.getLiquidosIv()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkLiquidosIVSi)).setChecked(((HojaConsultadto.getLiquidosIv()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkLiquidosIVNo)).setChecked(((HojaConsultadto.getLiquidosIv()) == '0' ? false : true));
                                hcTratamiento.setLiquidosIv(HojaConsultadto.getLiquidosIv());
                            }
                            if (HojaConsultadto.getPrednisona()=='1' || HojaConsultadto.getPrednisona()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkPrednisonaSi)).setChecked(((HojaConsultadto.getPrednisona()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkPrednisonaNo)).setChecked(((HojaConsultadto.getPrednisona()) == '0' ? false : true));
                                hcTratamiento.setPrednisona(HojaConsultadto.getPrednisona());
                            }
                            if (HojaConsultadto.getHidrocortisonaIv()=='1' || HojaConsultadto.getHidrocortisonaIv()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkHidrocortisonaSi)).setChecked(((HojaConsultadto.getHidrocortisonaIv()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkHidrocortisonaNo)).setChecked(((HojaConsultadto.getHidrocortisonaIv()) == '0' ? false : true));
                                hcTratamiento.setHidrocortisonaIv(HojaConsultadto.getHidrocortisonaIv());
                            }
                            if (HojaConsultadto.getSalbutamol()=='1' || HojaConsultadto.getSalbutamol()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkSalutamolSi)).setChecked(((HojaConsultadto.getSalbutamol()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkSalutamolNo)).setChecked(((HojaConsultadto.getSalbutamol()) == '0' ? false : true));
                                hcTratamiento.setSalbutamol(HojaConsultadto.getSalbutamol());
                            }
                            if (HojaConsultadto.getOseltamivir()=='1' || HojaConsultadto.getOseltamivir()=='0' ) {
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkOseltamivirSi)).setChecked(((HojaConsultadto.getOseltamivir()) == '0' ? true : false));
                                ((CheckBox) this.ACTIVITY.findViewById(R.id.chkOseltamivirNo)).setChecked(((HojaConsultadto.getOseltamivir()) == '0' ? false : true));
                                hcTratamiento.setOseltamivir(HojaConsultadto.getOseltamivir());
                            }

                            if(((CheckBox) this.ACTIVITY.findViewById(R.id.chkOtrosNo)).isChecked()) {
                                ((EditText) this.ACTIVITY.findViewById(R.id.edtxtOtro)).setText("");
                                this.ACTIVITY.findViewById(R.id.edtxtOtro).setEnabled(false);
                            }else {
                                ((EditText) this.ACTIVITY.findViewById(R.id.edtxtOtro)).setText((HojaConsultadto.getOtroAntibiotico() == "null") ? "" : HojaConsultadto.getOtroAntibiotico());
                            }
                            hcTratamiento.setOtroAntibiotico((HojaConsultadto.getOtroAntibiotico()=="null")?"":HojaConsultadto.getOtroAntibiotico());
                            ((EditText)this.ACTIVITY.findViewById(R.id.edtxtPlanes)).setText((HojaConsultadto.getPlanes()=="null")?"":HojaConsultadto.getPlanes());
                            hcTratamiento.setPlanes((HojaConsultadto.getPlanes()=="null")?"":HojaConsultadto.getPlanes());
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        MensajesHelper.mostrarMensajeInfo(CONTEXT,
                               e.getMessage(), getResources().getString(
                                        R.string.app_name), null);
                    }

                }else {
                    System.out.println(new StringBuffer().append(this.RESPUESTA.getCodigoError()).
                            append(" --- ").append(this.RESPUESTA.getMensajeError()).toString());
                }
            }
        }

        @Override
        protected void onCancelled() {
        }
    }


   }