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
import android.view.View;

import android.widget.EditText;
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

import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.DiagnosticoWS;



import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Controlador de la UI Examenes y historial.
 */
public class DiagnosticoExamenHistoriaActivity extends ActionBarActivity
        {

            public Context CONTEXT;

            public  Activity ACTIVITY;

            public Integer  SEC_HOJA_CONSULTA;

            InicioDTO pacienteSeleccionado;

            private String hisExaFisico;
            private String mUsuarioLogiado;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_diagnostico_historiaexamen);
                final ActionBar actionBar = getSupportActionBar();
                 pacienteSeleccionado = (InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado");
                SEC_HOJA_CONSULTA=pacienteSeleccionado.getIdObjeto();
                cargarExamenHistoriaTask task = new cargarExamenHistoriaTask(this, this);
                task.execute((Void) null);

                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(R.layout.custom_action_bar_title_center);
                ((TextView)actionBar.getCustomView().findViewById(R.id.myTitleBar)).setText(getResources().getString(R.string.tilte_hoja_consulta));
                ((TextView)actionBar.getCustomView().findViewById(R.id.myUserBar)).setText(((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser());
                this.CONTEXT = this;

                this.ACTIVITY = this;

                this.mUsuarioLogiado = ((CssfvApp)getApplication()).getInfoSessionWSDTO().getUser();
            }

            @Override
            public void onBackPressed() {
                return;
            }

           public void onClick_btnExamenHistoria( View view){

                pacienteSeleccionado = (InicioDTO) this.getIntent().getSerializableExtra("pacienteSeleccionado");
               SEC_HOJA_CONSULTA=  pacienteSeleccionado.getIdObjeto();

               if (pacienteSeleccionado.getCodigoEstado() == '7') {
                   if(tieneCambios()) {
                       DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               switch (which){
                                   case DialogInterface.BUTTON_POSITIVE:
                                       guardarExamenHistoria();
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
                   guardarExamenHistoria();
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

            private boolean tieneCambios(){
               String valor = ((EditText) findViewById(R.id.edtxtHistoricoExamen)).getText().toString();
               if((StringUtils.isNullOrEmpty(valor) && !StringUtils.isNullOrEmpty(hisExaFisico)) ||
                       (!StringUtils.isNullOrEmpty(valor) && StringUtils.isNullOrEmpty(hisExaFisico)) ||
                       !valor.equalsIgnoreCase(hisExaFisico)){
                    return true;
               }

                return false;
            }

            //funcion para guardar desde la pantalla de examenHistoria
            private void guardarExamenHistoria(){
        /*Creando una tarea asincrona*/
                AsyncTask<Void, Void, Void> HojaConsulta = new AsyncTask<Void, Void, Void>() {
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
                    protected Void doInBackground(Void... params) {
                        if (NET_INFO != null && NET_INFO.isConnected()){
                            HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
                            hojaConsulta.setSecHojaConsulta(SEC_HOJA_CONSULTA);
                            hojaConsulta.setHistoriaExamenFisico( ((EditText) findViewById(R.id.edtxtHistoricoExamen)).getText().toString());
                            RESPUESTA = diagnosticoWS.guardarExamenHistoria(hojaConsulta, mUsuarioLogiado);
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
                                    RESPUESTA.getMensajeError(),getResources().getString(
                                            R.string.app_name), null);

                        }else{
                            MensajesHelper.mostrarMensajeError(CONTEXT,
                                    RESPUESTA.getMensajeError(), getResources().getString(
                                            R.string.app_name), null);
                        }
                    }
                };
                HojaConsulta.execute((Void[])null);
            }






            public class cargarExamenHistoriaTask extends AsyncTask<Void, Void, Boolean> {

                private final Context CONTEXT;
                private final Activity ACTIVITY;
                private ResultadoListWSDTO<HojaConsultaDTO> RESPUESTA;
                private final ConnectivityManager CM;
                private final NetworkInfo NET_INFO;


                cargarExamenHistoriaTask(Context context, Activity activity) {
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


                                for (HojaConsultaDTO HojaConsultadto: this.RESPUESTA.getLstResultado()) {
                                    ((EditText)this.ACTIVITY.findViewById(R.id.edtxtHistoricoExamen)).setText((HojaConsultadto.getHistoriaExamenFisico()=="null")?"":HojaConsultadto.getHistoriaExamenFisico());
                                    hisExaFisico = (HojaConsultadto.getHistoriaExamenFisico()=="null")?"":HojaConsultadto.getHistoriaExamenFisico();
                                }
                            } catch (Exception e){
                                e.printStackTrace();
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






