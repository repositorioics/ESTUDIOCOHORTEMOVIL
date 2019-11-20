package com.sts_ni.estudiocohortecssfv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.GrillaCierreDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.tools.CancelacionDialog;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.CierreWS;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/***
 * Controlador Tab Cierre Consulta
 */
public class CCierreTabFragment extends Fragment implements CancelacionDialog.DialogListener {

    public HojaConsultaDTO HOJA_CONSULTA;

    private ProgressDialog PD_CIEERE;

    private InicioDTO mPacienteSeleccionado;
    private Fragment mCorrienteFragmento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.consulta_cierre_tab_layout, container, false);

        InicioDTO pacienteSeleccionado = (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado");
        this.mPacienteSeleccionado = pacienteSeleccionado;
        this.mCorrienteFragmento = this;

        ((EditText) rootView.findViewById(R.id.edtxNombrePaciente)).setText(pacienteSeleccionado.getNomPaciente());
        ((EditText) rootView.findViewById(R.id.edtxCodigoSintomaC)).setText(new StringBuffer().append(pacienteSeleccionado.getCodExpediente()).toString());

        inicializarControles(rootView);

        llamadoServicioCargaGrilla(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarCabezera();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(this.PD_CIEERE != null && this.PD_CIEERE.isShowing() )
            this.PD_CIEERE.dismiss();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        EditText edtxtMotivo= (EditText)dialog.getDialog().findViewById(R.id.edtxtMotivo);
        if(!StringUtils.isNullOrEmpty(edtxtMotivo.getText().toString())) {
            this.llamarProcesoCancelar(edtxtMotivo.getText().toString());
        } else {
            MensajesHelper.mostrarMensajeError(getActivity(), getResources().getString(
                    R.string.msj_aviso_requerido_motivo), getResources().getString(
                    R.string.title_estudio_sostenible), null);
        }
    }

    /***
     * Metodo para cargar la informaicon de la cabezara del TAB.
     */
    private void cargarCabezera() {
        if(getActivity() != null &&
          ((ConsultaActivity) getActivity()).CABECERA != null) {
            ((EditText) getActivity().findViewById(R.id.edtxEstudioParticipanteC)).setText(((ConsultaActivity) getActivity()).CABECERA.getCodConsentimeinto());
            ((EditText) getActivity().findViewById(R.id.edtxtExpedienteSintomaC)).setText(((ConsultaActivity) getActivity()).CABECERA.getExpedienteFisico());
            ((EditText) getActivity().findViewById(R.id.edtxtEdadSintomaC)).setText(DateUtils.obtenerEdad(((ConsultaActivity) getActivity()).CABECERA.getFechaNacimiento()));
            ((EditText) getActivity().findViewById(R.id.edtxtSexoSintomaC)).setText(((ConsultaActivity) getActivity()).CABECERA.getSexo());
            ((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintomaC)).setText(((ConsultaActivity) getActivity()).CABECERA.getPesoKg().toString());
            ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintomaC)).setText(((ConsultaActivity) getActivity()).CABECERA.getTallaCm().toString());
            ((EditText) getActivity().findViewById(R.id.edtxtTempCSintomaC)).setText(((ConsultaActivity) getActivity()).CABECERA.getTemperaturac().toString());
            ((EditText) getActivity().findViewById(R.id.edtxtFechaSintomaC)).setText(new SimpleDateFormat("dd/MM/yyyy").format(((ConsultaActivity) getActivity()).CABECERA.getFechaConsulta().getTime()));
            ((EditText) getActivity().findViewById(R.id.edtxtHoraSintomaC)).setText(((ConsultaActivity) getActivity()).CABECERA.getHoraConsulta());
        }
    }

    /***
     * Metodo para inicializar los controles UI del tab.
     * @param rootView
     */
    private void inicializarControles(final View rootView) {
        rootView.findViewById(R.id.imgBtnCerrarHoja).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //llamarProcesoAgregarHoja();
                                /*rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
                                rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
                                rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setEnabled(false);
                                rootView.findViewById(R.id.imgBtnCambioTurno).setEnabled(false);*/
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                rootView.findViewById(R.id.imgBtnCambioTurno).setEnabled(false);
                                rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
                                rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setEnabled(false);
                                llamarProcesoCierre(rootView);
                                break;
                        }
                    }
                };
                MensajesHelper.mostrarMensajeYesNo(getActivity(),
                        getResources().getString(
                                R.string.msj_esta_seguro_cerrar_hoja), getResources().getString(
                                R.string.title_estudio_sostenible), preguntaDialogClickListener);
            }




                //Desabilitar el boton cambio turno cuando se cerro la hoja de consulta

            //}
        });

        rootView.findViewById(R.id.btnSalirCierre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarSalirHojaServicio();

            }
        });

        rootView.findViewById(R.id.imgBtnCambioTurno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
                rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
                rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setEnabled(false);
                llamarProcesoCambioTurno();
                //Desbilitar el boton para cerrar la hoja de consulta cuando se dio click en cambio turno

                //rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
            }
        });

        rootView.findViewById(R.id.imgBtnAgregarHoja).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener preguntaDialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                llamarProcesoAgregarHoja();
                                /*rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
                                rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
                                rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setEnabled(false);
                                rootView.findViewById(R.id.imgBtnCambioTurno).setEnabled(false);*/
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                MensajesHelper.mostrarMensajeYesNo(getActivity(),
                        getResources().getString(
                                R.string.msj_esta_seguro_agregar_hoja), getResources().getString(
                                R.string.title_estudio_sostenible), preguntaDialogClickListener);
            }
        });

        rootView.findViewById(R.id.btnCancelarCierre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener preguntaCancelarDialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                CancelacionDialog dlogCancelacion = new CancelacionDialog();
                                dlogCancelacion.setTargetFragment(mCorrienteFragmento, 0);
                                dlogCancelacion.show(fm, "Cancelación");
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                MensajesHelper.mostrarMensajeYesNo(getActivity(),
                        getResources().getString(
                                R.string.msj_esta_seguro_de_cancelar), getResources().getString(
                                R.string.title_estudio_sostenible), preguntaCancelarDialogClickListener);
            }
        });

        rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarNoAtiendeLlamado();
                /*rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
                rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
                rootView.findViewById(R.id.imgBtnCambioTurno).setEnabled(false);*/
            }
        });

        if(mPacienteSeleccionado.getCodigoEstado() == '7') {
            rootView.findViewById(R.id.imgBtnAgregarHoja).setEnabled(false);
            rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
            rootView.findViewById(R.id.imgBtnCambioTurno).setEnabled(false);
            rootView.findViewById(R.id.btnCancelarCierre).setEnabled(false);
            rootView.findViewById(R.id.btnNoAtiendeLlamadoCierre).setEnabled(false);
        }
    }

    /***
     * Metodo para llamar el servicio que carga la grilla de los medicos y enfermeria.
     * @param rootView, Contenedor principal.
     */
    private void llamadoServicioCargaGrilla(final View rootView) {
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> cargarGrillaTask = new AsyncTask<Void, Void, Void>() {
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ResultadoObjectWSDTO<GrillaCierreDTO> RESPUESTA_GRILLA = new ResultadoObjectWSDTO<GrillaCierreDTO>();
            private CierreWS CIERREWS = new CierreWS(getResources());

            @Override
            protected void onPreExecute() {
                PD_CIEERE = new ProgressDialog(getActivity());
                PD_CIEERE.setTitle(getResources().getString(R.string.title_obteniendo));
                PD_CIEERE.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD_CIEERE.setCancelable(false);
                PD_CIEERE.setIndeterminate(true);
                PD_CIEERE.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()) {

                    RESPUESTA_GRILLA = CIERREWS.cargarGrillaCierre(((InicioDTO) getActivity().getIntent()
                                                                    .getSerializableExtra("pacienteSeleccionado")).getIdObjeto(),
                                                                    ((CssfvApp) getActivity().getApplication()).getInfoSessionWSDTO()
                                                                    .getUserId());

                } else {
                    RESPUESTA_GRILLA.setCodigoError(Long.parseLong("3"));
                    RESPUESTA_GRILLA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if ((PD_CIEERE != null) && PD_CIEERE.isShowing()) {
                    PD_CIEERE.dismiss();
                }

                if (RESPUESTA_GRILLA.getCodigoError().intValue() == 0) {
                    Calendar hoy = Calendar.getInstance();

                    HOJA_CONSULTA = new HojaConsultaDTO();

                    if(getActivity() != null && getActivity().getIntent() != null
                            && getActivity().getIntent().getSerializableExtra("pacienteSeleccionado") != null) {
                        HOJA_CONSULTA.setSecHojaConsulta(((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                        HOJA_CONSULTA.setUsuarioMedico((short) ((CssfvApp) getActivity().getApplication()).getInfoSessionWSDTO().getUserId());
                        HOJA_CONSULTA.setFechaCierre(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(hoy.getTime()));
                    }

                    ((TextView) rootView.findViewById(R.id.txtvCargoMedLog)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getCargoUsuarioLog());
                    ((TextView) rootView.findViewById(R.id.txtvNumeroMedLog)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getNumeroPersonalLog());
                    ((TextView) rootView.findViewById(R.id.txtvNombreMedLog)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getNombreUsuarioLog());
                    ((TextView) rootView.findViewById(R.id.txtvFechaMedLog)).setText(new SimpleDateFormat("dd/MM/yyyy").format(hoy.getTime()));
                    ((TextView) rootView.findViewById(R.id.txtvHoraMedLog)).setText(new SimpleDateFormat("KK:mm a").format(hoy.getTime()));

                    if (!StringUtils.isNullOrEmpty(RESPUESTA_GRILLA.getObjecRespuesta().getCargoUsuarioMedico())) {
                        ((TextView) rootView.findViewById(R.id.txtvCargoMedTurno)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getCargoUsuarioMedico());
                        ((TextView) rootView.findViewById(R.id.txtvNumeroMedTurno)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getNumeroPersonalMedico());
                        ((TextView) rootView.findViewById(R.id.txtvNombreMedTurno)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getNombreUsuarioMedico());

                        ((TextView) rootView.findViewById(R.id.txtvCargoEnferm)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getCargoEnfermeria());
                        ((TextView) rootView.findViewById(R.id.txtvNumeroEnferm)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getNumeroPersonalEnfermeria());
                        ((TextView) rootView.findViewById(R.id.txtvNombreEnferm)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getNombreEnfermeria());
                    } else {
                        ((TextView) rootView.findViewById(R.id.txtvCargoMedTurno)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getCargoEnfermeria());
                        ((TextView) rootView.findViewById(R.id.txtvNumeroMedTurno)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getNumeroPersonalEnfermeria());
                        ((TextView) rootView.findViewById(R.id.txtvNombreMedTurno)).setText(RESPUESTA_GRILLA.getObjecRespuesta().getNombreEnfermeria());
                    }
                } else if (RESPUESTA_GRILLA.getCodigoError().intValue() != 999) {
                    MensajesHelper.mostrarMensajeInfo(getActivity(),
                            RESPUESTA_GRILLA.getMensajeError(), getResources().getString(
                                    R.string.title_estudio_sostenible), null);

                } else {
                    MensajesHelper.mostrarMensajeError(getActivity(),
                            RESPUESTA_GRILLA.getMensajeError(), getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }
            }

        };
        cargarGrillaTask.execute((Void[]) null);
    }

    /***
     * Metodo para llamar servicio que realiza el proceso de cierra de Hoja de consulta.
     */
    private void llamarProcesoCierre(final View rootView){
        AsyncTask<Void, Void, Void> procesoCierreTask = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private CierreWS CIERREWS;

            @Override
            protected void onPreExecute() {
                try {
                    CIERREWS = new CierreWS(getResources());
                    PD = new ProgressDialog(getActivity());
                    PD.setTitle(getResources().getString(R.string.tittle_actualizando));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (NET_INFO != null && NET_INFO.isConnected()){

                        RESPUESTA = CIERREWS.ejecutarProcesoCierre(HOJA_CONSULTA);
                    }else{
                        RESPUESTA.setCodigoError(Long.parseLong("3"));
                        RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                try {
                    PD.dismiss();
                    if (RESPUESTA.getCodigoError().intValue() == 0){
                        MensajesHelper.mostrarMensajeOk(getActivity(),
                                getResources().getString(R.string.msj_cerrado_exitosamente).concat(" ").concat(
                                        getResources().getString(R.string.msj_se_ejecuto_la_impresion)),
                                getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                        rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
                        mPacienteSeleccionado.setCodigoEstado('7');




                    } else if (RESPUESTA.getCodigoError().intValue() == 1){
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                               new StringBuffer().append(getResources().getString(R.string.msj_cerrado_exitosamente).concat(
                                       " ").concat(getResources().getString(R.string.msj_se_ejecuto_la_impresion))).
                                       append(" ").append(RESPUESTA.getMensajeError()).toString(),getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                        rootView.findViewById(R.id.imgBtnCerrarHoja).setEnabled(false);
                        mPacienteSeleccionado.setCodigoEstado('7');

                        //*--------------------NUEVO CAMBIO 10/10/2019----------------------*/
                    } else if (RESPUESTA.getCodigoError().intValue() == 4) {
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                RESPUESTA.getMensajeError(),getResources().getString(
                                        R.string.title_estudio_sostenible), null);
                    } else {
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                RESPUESTA.getMensajeError(),getResources().getString(
                                        R.string.title_estudio_sostenible), null);
                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        };
        procesoCierreTask.execute((Void[])null);
    }

    /***
     * Metodo para llamar servicio que realiza el proceso de cambio de turno.
     */
    private void llamarProcesoCambioTurno(){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private CierreWS CIERREWS;

            @Override
            protected void onPreExecute() {
                try {
                    CIERREWS = new CierreWS(getResources());
                    PD = new ProgressDialog(getActivity());
                    PD.setTitle(getResources().getString(R.string.tittle_actualizando));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                }  catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (NET_INFO != null && NET_INFO.isConnected()){

                        RESPUESTA = CIERREWS.ejecutarProcesoCambioTurno(HOJA_CONSULTA);
                    }else{
                        RESPUESTA.setCodigoError(Long.parseLong("3"));
                        RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                try {
                    PD.dismiss();
                    if (RESPUESTA.getCodigoError().intValue() == 0){
                        MensajesHelper.mostrarMensajeOk(getActivity(),
                                getResources().getString(R.string.msj_exitosamente),
                                getResources().getString(
                                        R.string.title_estudio_sostenible), null);
                    } else if (RESPUESTA.getCodigoError().intValue() == 1){
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    } else {
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                RESPUESTA.getMensajeError(),getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        };
        task.execute((Void[])null);
    }

    /***
     * Metodo para llamar servicio que realiza el proceso de agregar nueva hoja de consulta.
     */
    private void llamarProcesoAgregarHoja(){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private CierreWS CIERREWS;

            @Override
            protected void onPreExecute() {
                try {
                    CIERREWS = new CierreWS(getResources());
                    PD = new ProgressDialog(getActivity());
                    PD.setTitle(getResources().getString(R.string.tittle_actualizando));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (NET_INFO != null && NET_INFO.isConnected()){

                        RESPUESTA = CIERREWS.ejecutarProcesoAgregarHoja(HOJA_CONSULTA);
                    }else{
                        RESPUESTA.setCodigoError(Long.parseLong("3"));
                        RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                try {
                    if(PD != null
                            && PD.isShowing()) {
                        PD.dismiss();
                    }
                    if (RESPUESTA.getCodigoError().intValue() == 0){
                        MensajesHelper.mostrarMensajeOk(getActivity(),
                                getResources().getString(R.string.msj_exitosamente),
                                getResources().getString(
                                        R.string.title_estudio_sostenible), null);
                    } else if (RESPUESTA.getCodigoError().intValue() == 1){
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    } else {
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                RESPUESTA.getMensajeError(),getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        };
        task.execute((Void[])null);
    }

    /***
     * Metodo para llamar servicio que realiza proceso de cancelacion de la hoja de consulta.
     * @param motivo, Motivo de cancelacion.
     */
    private void llamarProcesoCancelar(String motivo){
        AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private CierreWS CIERREWS;

            @Override
            protected void onPreExecute() {
                try {
                    CIERREWS = new CierreWS(getResources());
                    PD = new ProgressDialog(getActivity());
                    PD.setTitle(getResources().getString(R.string.tittle_actualizando));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(String... params) {
                try {
                    if (NET_INFO != null && NET_INFO.isConnected()){

                        RESPUESTA = CIERREWS.ejecutarProcesoCancelar(HOJA_CONSULTA, params[0]);
                    }else{
                        RESPUESTA.setCodigoError(Long.parseLong("3"));
                        RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                try {
                    PD.dismiss();
                    if (RESPUESTA.getCodigoError().intValue() == 0){
                        Intent intent = new Intent(getActivity(), InicioConsultaActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (RESPUESTA.getCodigoError().intValue() == 1){
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    } else {
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                RESPUESTA.getMensajeError(),getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        };
        task.execute(motivo);
    }

    /***
     * Metodo para llamar servicio que realiza proceso de no atiende llamado.
     */
    private void llamarNoAtiendeLlamado(){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private CierreWS CIERREWS;

            @Override
            protected void onPreExecute() {
                try {
                    CIERREWS = new CierreWS(getResources());
                    PD = new ProgressDialog(getActivity());
                    PD.setTitle(getResources().getString(R.string.tittle_actualizando));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (NET_INFO != null && NET_INFO.isConnected()){

                        RESPUESTA = CIERREWS.ejecutarNoAtiendeLlamdo(HOJA_CONSULTA);
                    }else{
                        RESPUESTA.setCodigoError(Long.parseLong("3"));
                        RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                try {
                    PD.dismiss();
                    if (RESPUESTA.getCodigoError().intValue() == 0){
                        Intent intent = new Intent(getActivity(), InicioConsultaActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (RESPUESTA.getCodigoError().intValue() == 1){
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    } else {
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                RESPUESTA.getMensajeError(),getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        };
        task.execute((Void[])null);
    }

    private void validarSalirHojaServicio(){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private CierreWS CIERREWS;

            @Override
            protected void onPreExecute() {
                try {
                    CIERREWS = new CierreWS(getResources());
                    PD = new ProgressDialog(getActivity());
                    PD.setTitle(getResources().getString(R.string.tittle_actualizando));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (NET_INFO != null && NET_INFO.isConnected()){

                        RESPUESTA = CIERREWS.validarSalirHojaConsulta(HOJA_CONSULTA);
                    }else{
                        RESPUESTA.setCodigoError(Long.parseLong("3"));
                        RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                try {
                    PD.dismiss();
                    if (RESPUESTA.getCodigoError().intValue() == 0){
                        Intent intent = new Intent(getActivity(), InicioConsultaActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (RESPUESTA.getCodigoError().intValue() == 1){
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    } else {
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                RESPUESTA.getMensajeError(),getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    }
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        };
        task.execute((Void[])null);
    }


}
