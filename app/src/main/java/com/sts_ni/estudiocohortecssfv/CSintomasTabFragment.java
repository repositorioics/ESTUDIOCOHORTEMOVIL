package com.sts_ni.estudiocohortecssfv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeccionesSintomasDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.CabezaSintomaActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.CategoriaSintomaActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.CutaneoSintomaActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.DeshidratacionSintomasActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.EstadoGeneralSintomasActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.EstadoNutriSintomasAcitvity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.GargantaSintomasActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.GastrointestinalSintomasActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.GeneralesSintomasActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.OsteomuscularSintomasActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.ReferenciaSintomasActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.RenalSintomasActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.RespiratorioSintomasActivity;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.SeccionesSintomasTask;
import com.sts_ni.estudiocohortecssfv.sintomasactivities.VacunasSintomasActivity;
import com.sts_ni.estudiocohortecssfv.tools.DecimalDigitsInputFilter;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.ControlCambiosWS;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Controlador del Tab de Sintomas.
 */
public class CSintomasTabFragment extends Fragment {

    private ProgressDialog PD;
    public Integer  SEC_HOJA_CONSULTA ;
    public byte[] RESPUESTA =null;
    public View mRootView;
    public SeccionesSintomasTask mSeccionesSintomasTask;
    AsyncTask<String, Void, Void> mEdicionTask;
    GeneralesControlCambiosDTO genControlCambios = null;
    private String vFueraRango = new String();

    public AsyncTask<Void, Void, Void> mMensajesAlertas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.consulta_sintomas_tab_layout, container, false);

        this.mRootView = rootView;
        InicioDTO pacienteSeleccionado = (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado");
        SEC_HOJA_CONSULTA=pacienteSeleccionado.getIdObjeto();

        ((EditText)rootView.findViewById(R.id.edtxNombrePaciente)).setText(pacienteSeleccionado.getNomPaciente());
        ((EditText)rootView.findViewById(R.id.edtxCodigoSintoma)).setText(new StringBuffer().append(pacienteSeleccionado.getCodExpediente()).toString());

        llamadoServicioObtenerDatosCab(rootView);

        inicializarControles(rootView);

        llamarServicioSeccionesCompletadas();

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();

        if(this.PD != null && this.PD.isShowing() )
            this.PD.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelarObtenerSeccionesCompletadasTask();
        cancelarEdicionTask();
        cancelarAlertasServicioTask();
    }

    /***
     * Metodo para inicializar los controles UI.
     * @param rootView, Contenedor principal.
     */
    private void inicializarControles(View rootView) {

        try {
            inicializarPrimerSegmento(rootView);
            inicializarSegundoSegmento(rootView);
            inicializarTerceroSegmento(rootView);

            rootView.findViewById(R.id.btnCategoriaSatImcSintomas)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CategoriaSintomaActivity.class);
                            intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));
                            intent.putExtra("pesoKgPaciente", ((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintoma)).getText().toString());
                            intent.putExtra("tallaCmPaciente", ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintoma)).getText().toString());

                            startActivity(intent);
                            getActivity().finish();
                        }
                    });

            rootView.findViewById(R.id.ibtEditPreclinicos).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarDatosPreclinicos();
                }
            });

            //visor de hoja de consulta
            rootView.findViewById(R.id.ibtVisorDiagnostico).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    obtenerHojaConsultaPdf();
                }
            });

            //visualizar las elertas para los examenes
            rootView.findViewById(R.id.ibtMensajeAlerta).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    obtenerMensajesAlertas();
                }
            });
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

    }

    /***
     * Metodo para asociar los eventos con los metodos correspondientes, Primera Parte.
     * @param rootView, Contenedor principal.
     */
    private void inicializarPrimerSegmento(View rootView) {
        rootView.findViewById(R.id.btnGeneralesSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), GeneralesSintomasActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        rootView.findViewById(R.id.btnEstadoGeneralSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), EstadoGeneralSintomasActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        rootView.findViewById(R.id.btnGastroInstesSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), GastrointestinalSintomasActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        rootView.findViewById(R.id.btnOsteomuscularSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), OsteomuscularSintomasActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        rootView.findViewById(R.id.btnCabezaSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), CabezaSintomaActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        rootView.findViewById(R.id.btnDeshidartacionSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DeshidratacionSintomasActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });
    }

    /***
     * Metodo para asociar los eventos con los metodos correspondientes, Segunda Parte.
     * @param rootView, Contenedor principal.
     */
    private void inicializarSegundoSegmento(View rootView) {
        rootView.findViewById(R.id.btnCutaneoSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), CutaneoSintomaActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        rootView.findViewById(R.id.btnGargantaSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), GargantaSintomasActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        rootView.findViewById(R.id.btnRenalSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), RenalSintomasActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });
    }

    /***
     * Metodo para asociar los eventos con los metodos correspondientes, Tercera Parte.
     * @param rootView, Contenedor principal.
     */
    private void inicializarTerceroSegmento(View rootView) {
        rootView.findViewById(R.id.btnEstadoNutriSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), EstadoNutriSintomasAcitvity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));
                        intent.putExtra("pesoKgPaciente", ((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintoma)).getText().toString());
                        intent.putExtra("tallaCmPaciente", ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintoma)).getText().toString());

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        rootView.findViewById(R.id.btnRespiratorioSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), RespiratorioSintomasActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        rootView.findViewById(R.id.btnReferenciaSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ReferenciaSintomasActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        rootView.findViewById(R.id.btnVacunasSintomas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), VacunasSintomasActivity.class);
                        intent.putExtra("pacienteSeleccionado", (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado"));

                        startActivity(intent);
                        getActivity().finish();
                    }
                });
    }

    /***
     * Metodo que controla el evento de edición de datos preclinicos.
     */
    private void editarDatosPreclinicos() {
        //((EditText) getActivity().findViewById(R.id.edtxtExpedienteSintoma)).setEnabled(true);
        getActivity().findViewById(R.id.edtxtPesoKgSintoma).setEnabled(true);
        ((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintoma)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        getActivity().findViewById(R.id.edtxtTallaCmSintoma).setEnabled(true);
        ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintoma)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        getActivity().findViewById(R.id.edtxtTempCSintoma).setEnabled(true);
        ((EditText) getActivity().findViewById(R.id.edtxtTempCSintoma)).setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    ArrayList<ControlCambiosDTO> controlCambios = new ArrayList<ControlCambiosDTO>();
                    genControlCambios = null;

                    vFueraRango = "";
                    if (validarCamposFueraRango(controlCambios)) {

                        if (controlCambios.size() > 0) {
                            genControlCambios = new GeneralesControlCambiosDTO();
                            genControlCambios.setUsuario(((CssfvApp) getActivity().getApplication()).getInfoSessionWSDTO().getUser());
                            genControlCambios.setControlador(getActivity().getLocalClassName());
                            genControlCambios.setControlCambios(controlCambios);

                            DialogInterface.OnClickListener preguntaEnviarDialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            llamarServicioEdicionPreclinico();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };
                            MensajesHelper.mostrarMensajeYesNo(getActivity(), String.format(
                                    getResources().getString(
                                            R.string.msj_aviso_control_cambios), vFueraRango), getResources().getString(
                                    R.string.title_estudio_sostenible), preguntaEnviarDialogClickListener);
                        } else {
                            llamarServicioEdicionPreclinico();
                        }

                    }
                    return true;
                }
                return false;
            }
        };

        //((EditText) getActivity().findViewById(R.id.edtxtExpedienteSintoma)).setOnEditorActionListener(onEditorActionListener);
        ((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintoma)).setOnEditorActionListener(onEditorActionListener);
        ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintoma)).setOnEditorActionListener(onEditorActionListener);
        ((EditText) getActivity().findViewById(R.id.edtxtTempCSintoma)).setOnEditorActionListener(onEditorActionListener);
    }

    /***
     * Metodo para validar campos fuera de rango en edición preclinicos.
     * @param controlCambios, Lista para guardar los campos fuera de rango.
     * @return
     */
    private boolean validarCamposFueraRango(ArrayList<ControlCambiosDTO> controlCambios) {
        int cont = 0;
        if (!estaEnRango(1, 200, ((EditText)getActivity().findViewById(R.id.edtxtPesoKgSintoma)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_peso));
            cont++;
           /* ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_peso));
            ctrCambios.setValorCampo(((EditText)getActivity().findViewById(R.id.edtxtPesoKgSintoma)).getText().toString());
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/

        }

        if (!estaEnRango(20, 200, ((EditText)getActivity().findViewById(R.id.edtxtTallaCmSintoma)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_talla));
            cont++;
            /*ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_talla));
            ctrCambios.setValorCampo(((EditText)getActivity().findViewById(R.id.edtxtTallaCmSintoma)).getText().toString());
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/
        }

        if (!estaEnRango(34, 42, ((EditText)getActivity().findViewById(R.id.edtxtTempCSintoma)).getText().toString())) {
            vFueraRango = StringUtils.concatenar(vFueraRango, getResources().getString(R.string.label_temp));
            cont++;
           /* ControlCambiosDTO ctrCambios = new ControlCambiosDTO();
            ctrCambios.setNombreCampo(getResources().getString(R.string.label_temp));
            ctrCambios.setValorCampo(((EditText)getActivity().findViewById(R.id.edtxtTempCSintoma)).getText().toString());
            ctrCambios.setTipoControl(ctrCambios.discrepancia);
            controlCambios.add(ctrCambios);*/
        }

        if (cont >0){
            MensajesHelper.mostrarMensajeInfo(getActivity(), String.format(
                    getResources().getString(
                            R.string.msj_aviso_control_cambios1), vFueraRango), getResources().getString(
                    R.string.title_estudio_sostenible),null);
            return false;
        }

        return true;
    }

    private boolean estaEnRango(double min, double max, String valor) {
        double valorComparar = Double.parseDouble(valor);
        return (valorComparar >= min && valorComparar <= max) ? true : false;
    }

    /***
     * Metodo que realiza el llamado del servicio de alertas(Algoritmos).
     *
     */
    private void obtenerMensajesAlertas(){
        if (mMensajesAlertas == null ||
                mMensajesAlertas.getStatus() == AsyncTask.Status.FINISHED) {
            mMensajesAlertas = new AsyncTask<Void, Void, Void>() {

                private ProgressDialog PD;
                private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private SintomasWS SINTOMASWS = new SintomasWS(getResources());
                private String MENSAJE_MATRIZ = null;

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
                        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
                        hojaConsulta.setSecHojaConsulta(((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                        MENSAJE_MATRIZ = SINTOMASWS.validacionMatrizSintoma(hojaConsulta);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    PD.dismiss();

                    try {
                        if (!StringUtils.isNullOrEmpty(MENSAJE_MATRIZ)
                                && !MENSAJE_MATRIZ.startsWith("any")) {
                            MensajesHelper.mostrarMensajeInfo(getActivity(), MENSAJE_MATRIZ,
                                    getResources().getString(R.string.title_estudio_sostenible), null);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                new StringBuffer().append(getResources().getString(
                                        R.string.msj_error_no_controlado)).append(e.getMessage()).toString(),
                                getResources().getString(R.string.title_estudio_sostenible), null);
                    }

                }
            };
            mMensajesAlertas.execute((Void[]) null);
        }
    }

    /***
     * Metodo que realiza el llamado del servicio de cargar los datos de cabecera.
     * @param rootView, Contenedor Principal.
     */
    private void llamadoServicioObtenerDatosCab(final View rootView){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> ObenterDatosCabServicio = new AsyncTask<Void, Void, Void>() {
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ResultadoObjectWSDTO<CabeceraSintomaDTO> RESPUESTA = new ResultadoObjectWSDTO<CabeceraSintomaDTO>();
            private SintomasWS SINTOMASWS = new SintomasWS(getResources());

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
                if (NET_INFO != null && NET_INFO.isConnected()){

                    RESPUESTA = SINTOMASWS.obtenerDatosCabecera(((InicioDTO) getActivity().getIntent().
                                                                    getSerializableExtra("pacienteSeleccionado")).getCodExpediente(),
                                                                ((InicioDTO) getActivity().getIntent().
                                                                    getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                }else{
                    RESPUESTA.setCodigoError(Long.parseLong("3"));
                    RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                if ((PD != null) && PD.isShowing()) {
                    PD.dismiss();
                }
                if (RESPUESTA.getCodigoError().intValue() == 0){

                    if(getActivity() != null && getActivity().getIntent() != null)
                        ((ConsultaActivity)getActivity()).CABECERA = RESPUESTA.getObjecRespuesta();
                    ((EditText)rootView.findViewById(R.id.edtxEstudioParticipante)).setText(RESPUESTA.getObjecRespuesta().getCodConsentimeinto());
                    ((EditText)rootView.findViewById(R.id.edtxtExpedienteSintoma)).setText(RESPUESTA.getObjecRespuesta().getExpedienteFisico());
                    ((EditText)rootView.findViewById(R.id.edtxtEdadSintoma)).setText(DateUtils.obtenerEdad(RESPUESTA.getObjecRespuesta().getFechaNacimiento()));
                    ((EditText)rootView.findViewById(R.id.edtxtSexoSintoma)).setText(RESPUESTA.getObjecRespuesta().getSexo());
                    ((EditText)rootView.findViewById(R.id.edtxtPesoKgSintoma)).setText(RESPUESTA.getObjecRespuesta().getPesoKg().toString());
                    ((EditText)rootView.findViewById(R.id.edtxtTallaCmSintoma)).setText(RESPUESTA.getObjecRespuesta().getTallaCm().toString());
                    ((EditText)rootView.findViewById(R.id.edtxtTempCSintoma)).setText(RESPUESTA.getObjecRespuesta().getTemperaturac().toString());
                    ((EditText)rootView.findViewById(R.id.edtxtFechaSintoma)).setText(new SimpleDateFormat("dd/MM/yyyy").format(RESPUESTA.getObjecRespuesta().getFechaConsulta().getTime()));
                    ((EditText)rootView.findViewById(R.id.edtxtHoraSintoma)).setText(RESPUESTA.getObjecRespuesta().getHoraConsulta());


                }else if (RESPUESTA.getCodigoError().intValue() != 999){
                    MensajesHelper.mostrarMensajeInfo(getActivity(),
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.title_estudio_sostenible), null);

                }else{
                    MensajesHelper.mostrarMensajeError(getActivity(),
                            RESPUESTA.getMensajeError(), getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }
            }
        };
        ObenterDatosCabServicio.execute((Void[])null);
    }

    /***
     * Metodo que realiza el llamado del servcio que obtiene la Hoja de consulta PDF.
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
                    MensajesHelper.mostrarMensajeError(getActivity(),
                            new StringBuffer().append(getResources().getString(
                                    R.string.msj_error_no_controlado)).append(e.getMessage()).toString(),
                                    getResources().getString(R.string.title_estudio_sostenible), null);
                }

            }
        };
        hojaconsultapdf.execute((Void[])null);
    }

    /***
     * Metodo para realizar el llamado del servicio de edición datos preclinicos.
     */
    private void llamarServicioEdicionPreclinico(){
        if (mEdicionTask == null || mEdicionTask.getStatus() == AsyncTask.Status.FINISHED) {
            mEdicionTask = new AsyncTask<String, Void, Void>() {
                private ProgressDialog PD_EDITAR;
                private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private ErrorDTO RESPUESTA = new ErrorDTO();
                private SintomasWS SINTOMAWS;
                private ControlCambiosWS CTRCAMBIOSWS;

                @Override
                protected void onPreExecute() {
                    try {
                        SINTOMAWS = new SintomasWS(getResources());
                        CTRCAMBIOSWS = new ControlCambiosWS(getResources());
                        PD_EDITAR = new ProgressDialog(getActivity());
                        PD_EDITAR.setTitle(getResources().getString(R.string.tittle_actualizando));
                        PD_EDITAR.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                        PD_EDITAR.setCancelable(false);
                        PD_EDITAR.setIndeterminate(true);
                        PD_EDITAR.show();
                    } catch (IllegalArgumentException iae) {
                        iae.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }

                @Override
                protected Void doInBackground(String... params) {
                    try {
                        if (NET_INFO != null && NET_INFO.isConnected()) {
                            InicioDTO pacienteSeleccionado = ((InicioDTO) getActivity().getIntent().
                                    getSerializableExtra("pacienteSeleccionado"));
                            if(genControlCambios != null) {
                                genControlCambios.setCodExpediente(pacienteSeleccionado.getCodExpediente());
                                genControlCambios.setNumHojaConsulta(Integer.parseInt(pacienteSeleccionado.getNumHojaConsulta()));
                                CTRCAMBIOSWS.guardarControlCambios(genControlCambios);
                            }
                            HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
                            hojaConsulta.setSecHojaConsulta(pacienteSeleccionado.getIdObjeto());
                            hojaConsulta.setPesoKg(Double.parseDouble(params[0]));
                            hojaConsulta.setTallaCm(Double.parseDouble(params[1]));
                            hojaConsulta.setTemperaturac(Double.parseDouble(params[2]));
                            hojaConsulta.setExpedienteFisico(params[3]);
                            RESPUESTA = SINTOMAWS.editarDatosPreclinicos(hojaConsulta, ((CssfvApp)getActivity().getApplication()).
                                    getInfoSessionWSDTO().getUser());
                        } else {
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
                protected void onPostExecute(Void result) {
                    try {
                        PD_EDITAR.dismiss();
                        if (RESPUESTA.getCodigoError().intValue() == 0) {
                            MensajesHelper.mostrarMensajeOk(getActivity(),
                                    getResources().getString(R.string.msj_exitosamente),
                                    getResources().getString(R.string.title_estudio_sostenible), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                cargarCabecera();
                                            } catch (NumberFormatException nfe) {
                                                nfe.printStackTrace();
                                            } catch (NullPointerException nfe) {
                                                nfe.printStackTrace();
                                            }
                                        }
                                    });
                        } else if (RESPUESTA.getCodigoError().intValue() == 1) {
                            MensajesHelper.mostrarMensajeInfo(getActivity(),
                                    RESPUESTA.getMensajeError(), getResources().
                                            getString(R.string.title_estudio_sostenible), null);
                        } else {
                            MensajesHelper.mostrarMensajeError(getActivity(),
                                    RESPUESTA.getMensajeError(), getResources().
                                            getString(R.string.title_estudio_sostenible), null);
                        }
                    } catch (IllegalArgumentException iae) {
                        iae.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                }

                private void cargarCabecera() {
                    ((ConsultaActivity) getActivity()).CABECERA.setExpedienteFisico(((EditText) getActivity()
                            .findViewById(R.id.edtxtExpedienteSintoma)).getText().toString());
                    ((ConsultaActivity) getActivity()).CABECERA.setPesoKg(Double.parseDouble(
                            ((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintoma)).getText().
                                    toString()));
                    ((ConsultaActivity) getActivity()).CABECERA.setTallaCm(Double.parseDouble(
                            ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintoma)).getText().
                                    toString()));
                    ((ConsultaActivity) getActivity()).CABECERA.setTemperaturac(Double.parseDouble(
                            ((EditText) getActivity().findViewById(R.id.edtxtTempCSintoma)).getText().
                                    toString()));

                    //cabecera Examenes
                    if (getActivity() != null) {
                        ((EditText) getActivity().findViewById(R.id.edtxEstudioParticipante)).setText(((ConsultaActivity) getActivity()).CABECERA.getCodConsentimeinto());
                        ((EditText) getActivity().findViewById(R.id.edtxtExpedienteExamen)).setText(((ConsultaActivity) getActivity()).CABECERA.getExpedienteFisico());
                        ((EditText) getActivity().findViewById(R.id.edtxtEdadExamen)).setText(DateUtils.obtenerEdad(((ConsultaActivity) getActivity()).CABECERA.getFechaNacimiento()));
                        ((EditText) getActivity().findViewById(R.id.edtxtSexoExamen)).setText(((ConsultaActivity) getActivity()).CABECERA.getSexo());
                        ((EditText) getActivity().findViewById(R.id.edtxtPesoKgExamen)).setText(((ConsultaActivity) getActivity()).CABECERA.getPesoKg().toString());
                        ((EditText) getActivity().findViewById(R.id.edtxtTallaCmExamen)).setText(((ConsultaActivity) getActivity()).CABECERA.getTallaCm().toString());
                        ((EditText) getActivity().findViewById(R.id.edtxtTempCExamen)).setText(((ConsultaActivity) getActivity()).CABECERA.getTemperaturac().toString());
                        ((EditText) getActivity().findViewById(R.id.edtxtFechaExamen)).setText(new SimpleDateFormat("dd/MM/yyyy").format(((ConsultaActivity) getActivity()).CABECERA.getFechaConsulta().getTime()));
                        ((EditText) getActivity().findViewById(R.id.edtxtHoraExamen)).setText(new SimpleDateFormat("KK:mm a").format(((ConsultaActivity) getActivity()).CABECERA.getFechaConsulta().getTime()));
                    }

                    ((EditText) getActivity().findViewById(R.id.edtxtExpedienteSintoma)).setEnabled(false);
                    ((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintoma)).setEnabled(false);
                    ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintoma)).setEnabled(false);
                    ((EditText) getActivity().findViewById(R.id.edtxtTempCSintoma)).setEnabled(false);
                }
            };
            mEdicionTask.execute(((EditText) getActivity().findViewById(R.id.edtxtPesoKgSintoma)).getText().toString(),
                    ((EditText) getActivity().findViewById(R.id.edtxtTallaCmSintoma)).getText().toString(),
                    ((EditText) getActivity().findViewById(R.id.edtxtTempCSintoma)).getText().toString(),
                    ((EditText) getActivity().findViewById(R.id.edtxtExpedienteSintoma)).getText().toString());
        }
    }

    /***
     * Metodo que llama el servicio que consulta las secciones que han sido ya guardadas.
     */
    public void llamarServicioSeccionesCompletadas() {
        if (mSeccionesSintomasTask == null || mSeccionesSintomasTask.getStatus() == SeccionesSintomasTask.Status.FINISHED) {
            mSeccionesSintomasTask = (SeccionesSintomasTask) new
                    SeccionesSintomasTask(this).execute(this.SEC_HOJA_CONSULTA.toString());
        }
    }

    /***
     * Metodo para cambiar el color de fondo de los botones de las secciones.
     * @param seccionesSintomas
     */
    public void marcarSeccionesCompletadas(SeccionesSintomasDTO seccionesSintomas) {

        if(seccionesSintomas.isEsGeneralesCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnGeneralesSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnGeneralesSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));

        }

        if(seccionesSintomas.isEsEstadoGeneralesCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnEstadoGeneralSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnEstadoGeneralSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsGastroinstetinalCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnGastroInstesSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnGastroInstesSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsOsteomuscularCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnOsteomuscularSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnOsteomuscularSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsCabezaCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnCabezaSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnCabezaSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsDeshidratacionCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnDeshidartacionSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnDeshidartacionSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsCutaneoCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnCutaneoSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnCutaneoSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsGargantaCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnGargantaSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnGargantaSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsRenalCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnRenalSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnRenalSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsEstadoNutricionalCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnEstadoNutriSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnEstadoNutriSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsRespiratorioCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnRespiratorioSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnRespiratorioSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsReferenciaCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnReferenciaSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnReferenciaSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsVacunaCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnVacunasSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnVacunasSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }

        if(seccionesSintomas.isEsCategoriaCompletada()) {
            cambiarColorBotonesSecciones(R.id.btnCategoriaSatImcSintomas);
            Button button = (Button)getActivity().findViewById(R.id.btnCategoriaSatImcSintomas);
            button.setTextColor(getResources().getColor(R.color.color_bg_button_texto_color_completo));
        }
    }

    private void cambiarColorBotonesSecciones(int idView) {
        mRootView.findViewById(idView).setBackgroundColor(getActivity().getResources().
                getColor(R.color.color_bg_button_verde_completado));
    }

    /***
     * Metodo para cancelar tarea obtener secciones completadas al ser destruido el activity.
     */
    private void cancelarObtenerSeccionesCompletadasTask() {
        if (mSeccionesSintomasTask != null && mSeccionesSintomasTask.getStatus() == UserTask.Status.RUNNING) {
            mSeccionesSintomasTask.cancel(true);
            mSeccionesSintomasTask = null;
        }
    }

    /***
     * Metodo para cancelar tarea Edicion datos preclinicos al ser destruido el activity.
     */
    private void cancelarEdicionTask() {
        if (mEdicionTask != null && mEdicionTask.getStatus() == AsyncTask.Status.RUNNING) {
            mEdicionTask.cancel(true);
            mEdicionTask = null;
        }
    }

    /***
     * Metodo para cancelar tarea obtener las alertas al ser destruido el activity.
     */
    private void cancelarAlertasServicioTask() {
        if (mMensajesAlertas != null && mMensajesAlertas.getStatus() == AsyncTask.Status.RUNNING) {
            mMensajesAlertas.cancel(true);
            mMensajesAlertas = null;
        }
    }
}
