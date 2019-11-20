package com.sts_ni.estudiocohortecssfv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sts_ni.estudiocohortecssfv.diagnostiscoactivities.DiagnosticoActivity;
import com.sts_ni.estudiocohortecssfv.diagnostiscoactivities.DiagnosticoExamenHistoriaActivity;
import com.sts_ni.estudiocohortecssfv.diagnostiscoactivities.DiagnosticoPlanesActivity;
import com.sts_ni.estudiocohortecssfv.diagnostiscoactivities.DiagnosticoProximaCitaActivity;
import com.sts_ni.estudiocohortecssfv.diagnostiscoactivities.DiagnosticoTratamientoActivity;
import com.sts_ni.estudiocohortecssfv.diagnostiscoactivities.SeccionesDiagnosticoTask;
import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeccionesDiagnosticoDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.DiagnosticoWS;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * Controlador del TAB Diagnostico.
 */
public class CDiagnosticoTabFragment extends Fragment  {

    public Activity ACTIVITY;
    public Integer  SEC_HOJA_CONSULTA ;
    public byte[] RESPUESTA =null;
    public View mRootView;
    public SeccionesDiagnosticoTask mSeccionesDiagnosticoTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.consulta_diagnostico_tab_layout, container,false);

        this.mRootView = rootView;

        InicioDTO pacienteSeleccionado = (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado");

        SEC_HOJA_CONSULTA=pacienteSeleccionado.getIdObjeto();

        ((EditText)rootView.findViewById(R.id.edtxNombrePaciente)).setText(pacienteSeleccionado.getNomPaciente());
        ((EditText)rootView.findViewById(R.id.edtxCodigoSintomaD)).setText(new StringBuffer().append(pacienteSeleccionado.getCodExpediente()).toString());


        //Historia y Examen Físico
        rootView.findViewById(R.id.btndianosticoHistExamenFisico)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DiagnosticoExamenHistoriaActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));
                        intent.putExtra("cabeceraSintoma", ((ConsultaActivity)getActivity()).CABECERA);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        //Planes
        /*rootView.findViewById(R.id.btndianosticoplanes)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DiagnosticoPlanesActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));
                        intent.putExtra("cabeceraSintoma", ((ConsultaActivity)getActivity()).CABECERA);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });*/
        //Tratamiento
        rootView.findViewById(R.id.btndianosticotratamiento)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DiagnosticoTratamientoActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));
                        intent.putExtra("cabeceraSintoma", ((ConsultaActivity)getActivity()).CABECERA);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        //Proxima Cita
        rootView.findViewById(R.id.btndianosticoproxcita)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DiagnosticoProximaCitaActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));
                        intent.putExtra("cabeceraSintoma", ((ConsultaActivity)getActivity()).CABECERA);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
        //Diagnostico
        rootView.findViewById(R.id.btndianosticodiag)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DiagnosticoActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));
                        intent.putExtra("cabeceraSintoma", ((ConsultaActivity)getActivity()).CABECERA);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        //visor de hoja de consulta
        rootView.findViewById(R.id.ibtVisorDiagnostico)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        obtenerHojaConsultaPdf();
                    }
                });

        llamarServicioSeccionesCompletadas();
        llamarProcesoActivarDiagnostico();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarCabezera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelarObtenerSeccionesCompletadasTask();
    }

    /***
     * Metodo para cargar los datos de la cabecera del TAB.
     */
    private void cargarCabezera() {
        if(((ConsultaActivity)getActivity()).CABECERA == null
                || ((ConsultaActivity)getActivity()).CABECERA.getPesoKg() == null) {
            ((ConsultaActivity)getActivity()).CABECERA = (CabeceraSintomaDTO)getActivity().getIntent().getSerializableExtra("cabeceraSintoma");
        }

        if(getActivity() != null) {
            ((EditText) getActivity().findViewById(R.id.edtxEstudioParticipanteD)).setText(((ConsultaActivity) getActivity()).CABECERA.getCodConsentimeinto());
            ((EditText) getActivity().findViewById(R.id.edtxtExpedienteSintomaD)).setText(((ConsultaActivity) getActivity()).CABECERA.getExpedienteFisico());
            ((EditText) getActivity().findViewById(R.id.edtxtEdadSintomaD)).setText(DateUtils.obtenerEdad(((ConsultaActivity) getActivity()).CABECERA.getFechaNacimiento()));
            ((EditText) getActivity().findViewById(R.id.edtxtSexoSintomaD)).setText(((ConsultaActivity) getActivity()).CABECERA.getSexo());
            ((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintomaD)).setText(((ConsultaActivity) getActivity()).CABECERA.getPesoKg().toString());
            ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintomaD)).setText(((ConsultaActivity) getActivity()).CABECERA.getTallaCm().toString());
            ((EditText) getActivity().findViewById(R.id.edtxtTempCSintomaD)).setText(((ConsultaActivity) getActivity()).CABECERA.getTemperaturac().toString());
            ((EditText) getActivity().findViewById(R.id.edtxtFechaSintomaD)).setText(new SimpleDateFormat("dd/MM/yyyy").format(((ConsultaActivity) getActivity()).CABECERA.getFechaConsulta().getTime()));
            ((EditText) getActivity().findViewById(R.id.edtxtHoraSintomaD)).setText(((ConsultaActivity) getActivity()).CABECERA.getHoraConsulta());
        }
    }

    /***
     * Cambio 07/11/2019 SC
     * Metodo para activar el boton Diagnostico***/
    private void llamarProcesoActivarDiagnostico(){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> procesoActivarDiagnosticoTask = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private DiagnosticoWS DIAGNOSTICOWS = new DiagnosticoWS(getResources());

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
                    RESPUESTA = DIAGNOSTICOWS.activarDiagnostico(SEC_HOJA_CONSULTA);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                try {
                    PD.dismiss();
                    if (RESPUESTA.getCodigoError().intValue() == 0){
                        /*MensajesHelper.mostrarMensajeOk(getActivity(),
                                getResources().getString(R.string.msj_cerrado_exitosamente).concat(" ").concat(
                                        getResources().getString(R.string.msj_se_ejecuto_la_impresion)),
                                getResources().getString(
                                        R.string.title_estudio_sostenible), null);*/
                        getActivity().findViewById(R.id.btndianosticodiag).setEnabled(true);
                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        };
        procesoActivarDiagnosticoTask.execute((Void[])null);
    }
    /***
     * Metodo para llamar el servicio que obtiene la Hoja consulta PDF.
     */
    private void obtenerHojaConsultaPdf(){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> hojaconsultapdf = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ConsultaWS CONSULTAWS = new ConsultaWS(getResources());

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
                    RESPUESTA = CONSULTAWS.getHojaConsultaPdf(SEC_HOJA_CONSULTA);
                 }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                PD.dismiss();

                try {

                    File file = new File("/sdcard/HojaConsulta.pdf");
                    FileOutputStream fos = new FileOutputStream(file);
                    String filepath = "/sdcard/HojaConsulta.pdf";
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
        hojaconsultapdf.execute((Void[])null);

    }

    /***
     * Metodo para llamar el servico que informa que secciones han sido completadas en Diagnostico.
     */
    public void llamarServicioSeccionesCompletadas() {
        if (mSeccionesDiagnosticoTask == null || mSeccionesDiagnosticoTask.getStatus() == SeccionesDiagnosticoTask.Status.FINISHED) {
            mSeccionesDiagnosticoTask = (SeccionesDiagnosticoTask) new
                    SeccionesDiagnosticoTask(this).execute(this.SEC_HOJA_CONSULTA.toString());
        }
    }

    /***
     * Metodo para cambiar el color de las secciones.
     * @param seccionesDiagnostico, Secciones completadas.
     */
    public void marcarSeccionesCompletadas(SeccionesDiagnosticoDTO seccionesDiagnostico) {

        if(seccionesDiagnostico.isEsHistorialExamenesCompletada()) {
            cambiarColorBotonesSecciones(R.id.btndianosticoHistExamenFisico);
            Button button = (Button)getActivity().findViewById(R.id.btndianosticoHistExamenFisico);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesDiagnostico.isEsTratamientoPlanesCompletada()) {
            cambiarColorBotonesSecciones(R.id.btndianosticotratamiento);
            Button button = (Button)getActivity().findViewById(R.id.btndianosticotratamiento);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesDiagnostico.isEsDiagnosticoCompletada()) {
            cambiarColorBotonesSecciones(R.id.btndianosticodiag);
            Button button = (Button)getActivity().findViewById(R.id.btndianosticodiag);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesDiagnostico.isEsProximaCitaCompletada()) {
            cambiarColorBotonesSecciones(R.id.btndianosticoproxcita);
            Button button = (Button)getActivity().findViewById(R.id.btndianosticoproxcita);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

    }

    private void cambiarColorBotonesSecciones(int idView) {
        mRootView.findViewById(idView).setBackgroundColor(getActivity().getResources().
                getColor(R.color.color_bg_button_verde_completado));
    }

    /***
     * Metodo para cancelar tarea de obtener secciones completadas cuando el activity es destruido.
     */
    private void cancelarObtenerSeccionesCompletadasTask() {
        if (mSeccionesDiagnosticoTask != null && mSeccionesDiagnosticoTask.getStatus() == UserTask.Status.RUNNING) {
            mSeccionesDiagnosticoTask.cancel(true);
            mSeccionesDiagnosticoTask = null;
        }
    }
}