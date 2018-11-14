package com.sts_ni.estudiocohortecssfv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.EghDTO;
import com.sts_ni.estudiocohortecssfv.dto.EgoDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.MalariaResultadoDTO;
import com.sts_ni.estudiocohortecssfv.dto.PerifericoResultadoDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadosExamenesDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.AndroidUtils;
import com.sts_ni.estudiocohortecssfv.utils.DateUtils;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;
import com.sts_ni.estudiocohortecssfv.ws.ConsultaWS;
import com.sts_ni.estudiocohortecssfv.ws.ExamenesWS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by user on 12/02/2015.
 */
public class CExamenesTabFragment extends Fragment {

    private Context CONTEXT;
    public Activity ACTIVITY;
    public View mRootView;
    ResultadosExamenesDTO RESULTADO_EXAMEN;
    private ProgressDialog PD_INICIAL;

    public Integer  SEC_HOJA_CONSULTA ;
    public byte[] RESPUESTA =null;

    private TextView viewTxtvSExamen;
    private TextView viewTxtvNExamen;

    AsyncTask<Void, Void, Void> mBuscarExamenesChekeadosTask;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.consulta_examenes_tab_layout, container, false);

        this.CONTEXT = getActivity();
        this.ACTIVITY = getActivity();
        this.RESULTADO_EXAMEN = null;

        final InicioDTO pacienteSeleccionado = (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado");
        SEC_HOJA_CONSULTA=pacienteSeleccionado.getIdObjeto();

        View examPart1 = rootView.findViewById(R.id.incExamenParte1);

        viewTxtvSExamen = (TextView) examPart1.findViewById(R.id.txtvSExamen);
        viewTxtvNExamen = (TextView) examPart1.findViewById(R.id.txtvNExamen);

        viewTxtvSExamen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 1);
            }
        });

        viewTxtvNExamen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SintomaMarcado(view, 2);
            }
        });

        //Funcion onClick Boton Malaria Resultado
        View.OnClickListener onClickMalariaResultado = new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(RESULTADO_EXAMEN.getMalariaResultado() != null){
                    Intent intent = new Intent(CONTEXT, MalariaResultadoActivity.class);
                    InicioDTO pacienteSeleccionado = (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado");
                    CabeceraSintomaDTO cabSintoma = (CabeceraSintomaDTO) getActivity().getIntent().getSerializableExtra("cabeceraSintoma");

                    intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);
                    intent.putExtra("cabeceraSintoma", cabSintoma);

                    MalariaResultadoDTO malariaResultado = RESULTADO_EXAMEN.getMalariaResultado();
                    intent.putExtra("malariaResultado", malariaResultado);
                    startActivity(intent);
                    getActivity().finish();
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }

            }
        };
        rootView.findViewById(R.id.btnGotaGruesaResultado).setOnClickListener(onClickMalariaResultado);

        //Funcion onClick Boton Serologia Dengue
        View.OnClickListener onClickSerologiaDengue = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(RESULTADO_EXAMEN.getSerologiaDengue() != null){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_muestra_tomada),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }

            }
        };
        rootView.findViewById(R.id.btnSerologiaDengue).setOnClickListener(onClickSerologiaDengue);

        //Funcion onClick Boton Serologia Chick
        View.OnClickListener onClickSerologiaChick = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(RESULTADO_EXAMEN.getSerologiaChick() != null){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_muestra_tomada),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }

            }
        };
        rootView.findViewById(R.id.btnSerologiChick).setOnClickListener(onClickSerologiaChick);

        //Funcion onClick Boton EGO
        View.OnClickListener onClickEGO = new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(RESULTADO_EXAMEN.getEgo() != null){
                    Intent intent = new Intent(CONTEXT, EgoResultadoActivity.class);
                    InicioDTO pacienteSeleccionado = (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado");
                    CabeceraSintomaDTO cabSintoma = (CabeceraSintomaDTO) getActivity().getIntent().getSerializableExtra("cabeceraSintoma");

                    intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);
                    intent.putExtra("cabeceraSintoma", cabSintoma);

                    EgoDTO ego = RESULTADO_EXAMEN.getEgo();
                    intent.putExtra("egoResultado", ego);
                    startActivity(intent);
                    getActivity().finish();
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }
            }
        };
        rootView.findViewById(R.id.btnEGOResultado).setOnClickListener(onClickEGO);

        //Funcion onClick Boton EGH
        View.OnClickListener onClickEGH = new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(RESULTADO_EXAMEN.getEgh() != null){
                    Intent intent = new Intent(CONTEXT, EghResultadoActivity.class);
                    InicioDTO pacienteSeleccionado = (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado");
                    CabeceraSintomaDTO cabSintoma = (CabeceraSintomaDTO) getActivity().getIntent().getSerializableExtra("cabeceraSintoma");

                    intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);
                    intent.putExtra("cabeceraSintoma", cabSintoma);

                    EghDTO egh = RESULTADO_EXAMEN.getEgh();
                    intent.putExtra("eghResultado", egh);
                    startActivity(intent);
                    getActivity().finish();
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }
            }
        };
        rootView.findViewById(R.id.btnEGHResultado).setOnClickListener(onClickEGH);

        //Funcion onClick Boton Extendido Periferico
        View.OnClickListener onClicExtPeriferico = new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(RESULTADO_EXAMEN.getPerifericoResultado() != null){
                    Intent intent = new Intent(CONTEXT, PerifericoResultadoActivity.class);
                    InicioDTO pacienteSeleccionado = (InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado");
                    CabeceraSintomaDTO cabSintoma = (CabeceraSintomaDTO) getActivity().getIntent().getSerializableExtra("cabeceraSintoma");

                    intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);
                    intent.putExtra("cabeceraSintoma", cabSintoma);

                    PerifericoResultadoDTO perifericoResultado = RESULTADO_EXAMEN.getPerifericoResultado();
                    intent.putExtra("perifericoResultado", perifericoResultado);
                    startActivity(intent);
                    getActivity().finish();
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }
            }
        };
        rootView.findViewById(R.id.btnExtendidoPeriferico).setOnClickListener(onClicExtPeriferico);

        //Funcion para onClick Influenza
        View.OnClickListener onClicInfluenza = new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(RESULTADO_EXAMEN.getInfluenza() != null){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_muestra_tomada),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }
            }
        };
        rootView.findViewById(R.id.btnInfluenza).setOnClickListener(onClicInfluenza);
        //==========================================================================================
        //Funcion btn BHC
        /*View.OnClickListener onClickBhc = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(RESULTADO_EXAMEN.getHojaConsulta().getBhc() != null){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_muestra_tomada),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }

            }
        };
        rootView.findViewById(R.id.btnBHCesultado).setOnClickListener(onClickBhc);

        //Funcion btn Albumina
        View.OnClickListener onClickAlbumina = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(RESULTADO_EXAMEN.getHojaConsulta().getAlbumina() != null){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_muestra_tomada),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }

            }
        };
        rootView.findViewById(R.id.btnAlbuminaReuResultado).setOnClickListener(onClickAlbumina);

        //Funcion btn Ast/Alt
        View.OnClickListener onClickAstAlt = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(RESULTADO_EXAMEN.getHojaConsulta().getAstAlt() != null){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_muestra_tomada),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }

            }
        };
        rootView.findViewById(R.id.btnASTALTReuResultado).setOnClickListener(onClickAstAlt);

        //Function btn Bulirrubinas
        View.OnClickListener onClickBilirrubinas = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(RESULTADO_EXAMEN.getHojaConsulta().getBilirrubinas() != null){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_muestra_tomada),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }

            }
        };
        rootView.findViewById(R.id.btnBilirubinasReuResultado).setOnClickListener(onClickBilirrubinas);

        //Funcion btn Cpk
        View.OnClickListener onClickCpk = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(RESULTADO_EXAMEN.getHojaConsulta().getCpk() != null){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_muestra_tomada),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }

            }
        };
        rootView.findViewById(R.id.btnCPKReuResultado).setOnClickListener(onClickCpk);

        //Funcion btn Colesterom
        View.OnClickListener onClickColesterol = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(RESULTADO_EXAMEN.getHojaConsulta().getColesterol() != null){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_muestra_tomada),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }

            }
        };
        rootView.findViewById(R.id.btnColesterolReuResultado).setOnClickListener(onClickColesterol);

        //Funcion btn Factor Reumatoideo
        View.OnClickListener onClickFactorReuma = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(RESULTADO_EXAMEN.getHojaConsulta().getFactorReumatoideo() != null){
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_muestra_tomada),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }else{
                    MensajesHelper.mostrarMensajeInfo(CONTEXT,
                            getResources().getString(
                                    R.string.msj_resultado_examen__aun_no_realizado),getResources().getString(
                                    R.string.title_estudio_sostenible), null);
                }

            }
        };
        rootView.findViewById(R.id.btnFactoroReuResultado).setOnClickListener(onClickFactorReuma);*/

        //------------------Funcion onClick del Boton Actualizar Resultados--------------
        View.OnClickListener onClickedActualizarResultados = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtOrdenLab = (TextView)getActivity().findViewById(R.id.txtvNumOrdenLaboratorio);
                txtOrdenLab.setVisibility(View.VISIBLE);
                EditText edtxOrdenLab = (EditText)getActivity().findViewById(R.id.edtxNumOrenLaboratorio);
                edtxOrdenLab.setVisibility(View.VISIBLE);
                llamandoBuscarExamenes();
            }
        };
        rootView.findViewById(R.id.btnActualizarResultados).setOnClickListener(onClickedActualizarResultados);

        ((EditText)rootView.findViewById(R.id.edtxNombrePaciente)).setText(pacienteSeleccionado.getNomPaciente());
        ((EditText)rootView.findViewById(R.id.edtxCodigoExamenes)).setText(new StringBuffer().append(pacienteSeleccionado.getCodExpediente()).toString());

        /*Button botonSD = (Button)getActivity().findViewById(R.id.btnSerologiaDengue);
        Drawable imagenSD = getResources().getDrawable(R.drawable.check_resultado_exa);
        // imagenEgo.setBounds(0,0,60,60);
        botonSD.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenSD, null);*/
        llamadoServicioObtenerDatosCab(rootView);
        llamandoBuscarExamenesChekeados();
        inicializarControles(rootView);

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();

        if(this.PD_INICIAL != null && this.PD_INICIAL.isShowing() )
            this.PD_INICIAL.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelarBucarExamanesTask();
    }

    /***
     * Metodo para inicializar controles de UI del TAB.
     * @param rootView, Contenedor principal.
     */
    public void inicializarControles(View rootView){

        //Controlador para visualizar hoja de consulta
        rootView.findViewById(R.id.ibtVisorDiagnostico)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        obtenerHojaConsultaPdf();
                    }
                });

        View.OnClickListener onClickedBHC = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChkboxClickedBHC(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSBHCExamen)
                .setOnClickListener(onClickedBHC);

        rootView.findViewById(R.id.chkbNBHCExamen)
                .setOnClickListener(onClickedBHC);


        View.OnClickListener onClickedDengue = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedSDengue(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSSerologiaDengueExamen)
                .setOnClickListener(onClickedDengue);

        rootView.findViewById(R.id.chkbNSerologiaDengueExamen)
                .setOnClickListener(onClickedDengue);

        View.OnClickListener onClickedChick = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedSChick(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSSerologiaChickExamen)
                .setOnClickListener(onClickedChick);

        rootView.findViewById(R.id.chkbNSerologiaChickExamen)
                .setOnClickListener(onClickedChick);

        View.OnClickListener onClickedGotaGruesa = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedGotaGruesa(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSGotaGruesaExamen)
                .setOnClickListener(onClickedGotaGruesa);

        rootView.findViewById(R.id.chkbNGotaGruesaExamen)
                .setOnClickListener(onClickedGotaGruesa);

        View.OnClickListener onClickedExtendidoPeriferico = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedExtendidoPeriferico(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSExtendidoPerExamen)
                .setOnClickListener(onClickedExtendidoPeriferico);

        rootView.findViewById(R.id.chkbNExtendidoPerExamen)
                .setOnClickListener(onClickedExtendidoPeriferico);

        View.OnClickListener onClickedAST = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedAST(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSAST)
                .setOnClickListener(onClickedAST);

        rootView.findViewById(R.id.chkbNAST)
                .setOnClickListener(onClickedAST);

        View.OnClickListener onClickedEGO = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedEGO(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSEGOExamen)
                .setOnClickListener(onClickedEGO);

        rootView.findViewById(R.id.chkbNEGOExamen)
                .setOnClickListener(onClickedEGO);

        View.OnClickListener onClickedEGH = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedEGH(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSEGHExamen)
                .setOnClickListener(onClickedEGH);

        rootView.findViewById(R.id.chkbNEGHExamen)
                .setOnClickListener(onClickedEGH);

        View.OnClickListener onClickedCitologiaFecal = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedCitologiaFecal(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSCitologiaFecalExamen)
                .setOnClickListener(onClickedCitologiaFecal);

        rootView.findViewById(R.id.chkbNCitologiaFecalExamen)
                .setOnClickListener(onClickedCitologiaFecal);

        View.OnClickListener onClickedFactorReu = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedFactorReumatoide(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSFactorReuExamen)
                .setOnClickListener(onClickedFactorReu);

        rootView.findViewById(R.id.chkbNFactorReuExamen)
                .setOnClickListener(onClickedFactorReu);

        View.OnClickListener onClickedAlbumina = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedAlbumina(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSAlbuminaExamen)
                .setOnClickListener(onClickedAlbumina);

        rootView.findViewById(R.id.chkbNAlbuminaExamen)
                .setOnClickListener(onClickedAlbumina);

        /*View.OnClickListener onClickedASTALT = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedASTALT(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSASTALTExamen)
                .setOnClickListener(onClickedASTALT);

        rootView.findViewById(R.id.chkbNASTALTExamen)
                .setOnClickListener(onClickedASTALT);*/

        View.OnClickListener onClickedBilirrubinas = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedBilrirubinas(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSBilirrubinasExamen)
                .setOnClickListener(onClickedBilirrubinas);

        rootView.findViewById(R.id.chkbNBilirrubinasExamen)
                .setOnClickListener(onClickedBilirrubinas);

        View.OnClickListener onClickedCPK = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedCPK(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSCPKExamen)
                .setOnClickListener(onClickedCPK);

        rootView.findViewById(R.id.chkbNCPKExamen)
                .setOnClickListener(onClickedCPK);

        View.OnClickListener onClickedColesterol = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedColesterol(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSColesterolExamen)
                .setOnClickListener(onClickedColesterol);

        rootView.findViewById(R.id.chkbNColesterolExamen)
                .setOnClickListener(onClickedColesterol);

        View.OnClickListener onClickedInfluenza = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedInfluenza(view);
                //validacionCheckBoxExamenes();
            }
        };

        rootView.findViewById(R.id.chkbSInfluenzaExamen)
                .setOnClickListener(onClickedInfluenza);

        rootView.findViewById(R.id.chkbNInfluenzaExamen)
                .setOnClickListener(onClickedInfluenza);

       View.OnClickListener onClickedOtro = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onChkboxClickedOtro(view);
            }
        };

        rootView.findViewById(R.id.chkbSOtroExamen)
                .setOnClickListener(onClickedOtro);

        rootView.findViewById(R.id.chkbNOtroExamen)
                .setOnClickListener(onClickedOtro);

        View.OnClickListener onClickBtnEnvio = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onClick_btnEnviarOrdLab();
            }
        };

        rootView.findViewById(R.id.btnEnviarOrdLab).setOnClickListener(onClickBtnEnvio);
    }
    //------------------------- Primer Grupo de CheckBox---------------------------

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedBHC(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSBHCExamen), getActivity().findViewById(R.id.chkbNBHCExamen),
                view);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedSDengue(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSSerologiaDengueExamen), getActivity().findViewById(R.id.chkbNSerologiaDengueExamen),
                view);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedSChick(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSSerologiaChickExamen), getActivity().findViewById(R.id.chkbNSerologiaChickExamen),
                view);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedGotaGruesa(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSGotaGruesaExamen), getActivity().findViewById(R.id.chkbNGotaGruesaExamen),
                view);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedExtendidoPeriferico(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSExtendidoPerExamen), getActivity().findViewById(R.id.chkbNExtendidoPerExamen),
                view);
    }

    /***
     * Metodo que es ejecutado en el onclick del checkbox
     * @param view
     */
    public void onChkboxClickedAST(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSAST), getActivity().findViewById(R.id.chkbNAST),
                view);
    }

    //-------------------------Segundo Grupo de CheckBox---------------------------
    public void onChkboxClickedEGO(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSEGOExamen), getActivity().findViewById(R.id.chkbNEGOExamen),
                view);
    }

    public void onChkboxClickedEGH(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSEGHExamen), getActivity().findViewById(R.id.chkbNEGHExamen),
                view);
    }

    public void onChkboxClickedCitologiaFecal(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSCitologiaFecalExamen), getActivity().findViewById(R.id.chkbNCitologiaFecalExamen),
                view);
    }

    public void onChkboxClickedFactorReumatoide(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSFactorReuExamen), getActivity().findViewById(R.id.chkbNFactorReuExamen),
                view);
    }

    public void onChkboxClickedAlbumina(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSAlbuminaExamen), getActivity().findViewById(R.id.chkbNAlbuminaExamen),
                view);
    }

    /*public void onChkboxClickedASTALT(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSASTALTExamen), getActivity().findViewById(R.id.chkbNASTALTExamen),
                view);
    }*/

    //-------------------------Tercer Grupo de CheckBox---------------------------
    public void onChkboxClickedBilrirubinas(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSBilirrubinasExamen), getActivity().findViewById(R.id.chkbNBilirrubinasExamen),
                view);
    }

    public void onChkboxClickedCPK(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSCPKExamen), getActivity().findViewById(R.id.chkbNCPKExamen),
                view);
    }

    public void onChkboxClickedColesterol(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSColesterolExamen), getActivity().findViewById(R.id.chkbNColesterolExamen),
                view);
    }

    public void onChkboxClickedInfluenza(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSInfluenzaExamen), getActivity().findViewById(R.id.chkbNInfluenzaExamen),
                view);
    }

    public void onChkboxClickedOtro(View view) {
        AndroidUtils.controlarCheckBoxGroup(getActivity().findViewById(R.id.chkbSOtroExamen), getActivity().findViewById(R.id.chkbNOtroExamen),
                view);

        if(view.getId() == R.id.chkbSOtroExamen){
            getActivity().findViewById(R.id.edtxtOtroExamenLab).setVisibility(View.VISIBLE);
            /*EditText edtxtOtroExamenLab = (EditText)getActivity().findViewById(R.id.edtxtOtroExamenLab);
            edtxtOtroExamenLab.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_SEND){
                        guardarOtroExamenLab();
                        return true;
                    }
                    return false;
                }
            });*/

        }else{
            ((EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab)).setText("");
            getActivity().findViewById(R.id.edtxtOtroExamenLab).setVisibility(View.INVISIBLE);
        }
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

                            ((CheckBox) getActivity().findViewById(R.id.chkbSBHCExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSSerologiaDengueExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSSerologiaChickExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSGotaGruesaExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSExtendidoPerExamen)).setChecked(false);

                            ((CheckBox) getActivity().findViewById(R.id.chkbSEGOExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSEGHExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSCitologiaFecalExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSFactorReuExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSAlbuminaExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSAST)).setChecked(false);

                            ((CheckBox) getActivity().findViewById(R.id.chkbSBilirrubinasExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSCPKExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSColesterolExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSInfluenzaExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbSOtroExamen)).setChecked(false);

                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSBHCExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSSerologiaDengueExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSSerologiaChickExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSGotaGruesaExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSExtendidoPerExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSEGOExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSEGHExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSCitologiaFecalExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSFactorReuExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSAlbuminaExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSAST)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSBilirrubinasExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSCPKExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSColesterolExamen)).setChecked(true);
                            if(valor==1) ((CheckBox) getActivity().findViewById(R.id.chkbSInfluenzaExamen)).setChecked(true);
                            if(valor==1) {
                                ((CheckBox) getActivity().findViewById(R.id.chkbSOtroExamen)).setChecked(true);
                                getActivity().findViewById(R.id.edtxtOtroExamenLab).setVisibility(View.VISIBLE);
                            }

                            ((CheckBox) getActivity().findViewById(R.id.chkbNBHCExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNSerologiaDengueExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNSerologiaChickExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNGotaGruesaExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNExtendidoPerExamen)).setChecked(false);

                            ((CheckBox) getActivity().findViewById(R.id.chkbNEGOExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNEGHExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNCitologiaFecalExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNFactorReuExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNAlbuminaExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNAST)).setChecked(false);

                            ((CheckBox) getActivity().findViewById(R.id.chkbNBilirrubinasExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNCPKExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNColesterolExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNInfluenzaExamen)).setChecked(false);
                            ((CheckBox) getActivity().findViewById(R.id.chkbNOtroExamen)).setChecked(false);

                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNBHCExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNSerologiaDengueExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNSerologiaChickExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNGotaGruesaExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNExtendidoPerExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNEGOExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNEGHExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNCitologiaFecalExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNFactorReuExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNAlbuminaExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNAST)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNBilirrubinasExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNCPKExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNColesterolExamen)).setChecked(true);
                            if(valor==2) ((CheckBox) getActivity().findViewById(R.id.chkbNInfluenzaExamen)).setChecked(true);
                            if(valor==2) {
                                ((CheckBox) getActivity().findViewById(R.id.chkbNOtroExamen)).setChecked(true);
                                ((EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab)).setText("");
                                getActivity().findViewById(R.id.edtxtOtroExamenLab).setVisibility(View.INVISIBLE);
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


            MensajesHelper.mostrarMensajeYesNo(getActivity(),
                    mensaje, getResources().getString(
                            R.string.title_estudio_sostenible),
                    preguntaDialogClickListener);

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(getActivity(), e.getMessage(), getString(R.string.title_estudio_sostenible), null);
        }
    }

    /***
     * Metodo que se ejecuta en el evento onClick del boton Enviar examen.
     */
    public void onClick_btnEnviarOrdLab(){
        DialogInterface.OnClickListener preguntaEnviarOrdLab = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        validacionCheckBoxExamenes();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        MensajesHelper.mostrarMensajeYesNo(this.CONTEXT,
                getResources().getString(R.string.msj_esta_seguro_de_guardar_examenes),getResources().getString(
                    R.string.title_estudio_sostenible), preguntaEnviarOrdLab);
    }

    public void presentaMensaje(){
        MensajesHelper.mostrarMensajeInfo(this.CONTEXT,
                getResources().getString(
                        R.string.msj_casillas_sin_marcar),getResources().getString(
                        R.string.title_estudio_sostenible), null);
    }

    /***
     * Metodo para que realiza la validación del checkbox que deben estar marcados.
     */
    public void validacionCheckBoxExamenes() {
        if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSBHCExamen), getActivity().findViewById(R.id.chkbNBHCExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSSerologiaDengueExamen), getActivity().findViewById(R.id.chkbNSerologiaDengueExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSSerologiaChickExamen), getActivity().findViewById(R.id.chkbNSerologiaChickExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSGotaGruesaExamen), getActivity().findViewById(R.id.chkbNGotaGruesaExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSExtendidoPerExamen), getActivity().findViewById(R.id.chkbNExtendidoPerExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSAST), getActivity().findViewById(R.id.chkbNAST))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSEGOExamen), getActivity().findViewById(R.id.chkbNEGOExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSEGHExamen), getActivity().findViewById(R.id.chkbNEGHExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSCitologiaFecalExamen), getActivity().findViewById(R.id.chkbNCitologiaFecalExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSFactorReuExamen), getActivity().findViewById(R.id.chkbNFactorReuExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSAlbuminaExamen), getActivity().findViewById(R.id.chkbNAlbuminaExamen))) {
            presentaMensaje();
            return;
        }/*else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSASTALTExamen), getActivity().findViewById(R.id.chkbNASTALTExamen))) {
            presentaMensaje();
            return;
        }*/else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSBilirrubinasExamen), getActivity().findViewById(R.id.chkbNBilirrubinasExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSCPKExamen), getActivity().findViewById(R.id.chkbNCPKExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSColesterolExamen), getActivity().findViewById(R.id.chkbNColesterolExamen))) {
            presentaMensaje();
            return;
        }else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSInfluenzaExamen), getActivity().findViewById(R.id.chkbNInfluenzaExamen))) {
            presentaMensaje();
            return;
        }/*else if(AndroidUtils.esChkboxsFalse(getActivity().findViewById(R.id.chkbSOtroExamen), getActivity().findViewById(R.id.chkbNOtroExamen))) {
            return;
        }*/
        if(((CheckBox) getActivity().findViewById(R.id.chkbSOtroExamen)).isChecked()) {
            if(StringUtils.isNullOrEmpty(((EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab)).getText().toString())) {
                MensajesHelper.mostrarMensajeInfo(this.CONTEXT,
                        getResources().getString(
                                R.string.msj_otro_examen_sin_texto), getResources().getString(
                                R.string.title_estudio_sostenible), null);
                return;
            }
        }

        HojaConsultaDTO hojaConsulta = cargarHojaConsulta();
        GeneralesControlCambiosDTO genCtrlCambios = new GeneralesControlCambiosDTO();
        genCtrlCambios.setUsuario(((CssfvApp) this.ACTIVITY.getApplication()).getInfoSessionWSDTO().getUser());
        genCtrlCambios.setControlador(this.ACTIVITY.getLocalClassName());
        llamandoGuardarExamenesServicio(hojaConsulta, genCtrlCambios);

        if( ((CheckBox) getActivity().findViewById(R.id.chkbSOtroExamen)).isChecked() ) {
            guardarOtroExamenLab();
        }
    }

    /***
     * Metodo que realiza el llamado del servicio que carga la cabezera del TAB.
     * @param rootView
     */
    private void llamadoServicioObtenerDatosCab(final View rootView){
        /*Creando una tarea asincrona*/
        AsyncTask<Void, Void, Void> ObenterDatosCabServicio = new AsyncTask<Void, Void, Void>() {
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ResultadoObjectWSDTO<CabeceraSintomaDTO> RESPUESTA = new ResultadoObjectWSDTO<CabeceraSintomaDTO>();
            private ExamenesWS EXAMENESWS = new ExamenesWS(getResources());

            @Override
            protected void onPreExecute() {
                if(getActivity() != null) {
                    if(PD_INICIAL != null && PD_INICIAL.isShowing()) {
                        PD_INICIAL.dismiss();
                    }
                    PD_INICIAL = new ProgressDialog(getActivity());
                    PD_INICIAL.setTitle(getResources().getString(R.string.title_obteniendo));
                    PD_INICIAL.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD_INICIAL.setCancelable(false);
                    PD_INICIAL.setIndeterminate(true);
                    PD_INICIAL.show();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){

                    if(getActivity() != null
                            && getActivity().getIntent() != null
                            && getActivity().getIntent().getSerializableExtra("pacienteSeleccionado") != null) {
                        RESPUESTA = EXAMENESWS.obtenerDatosCabecera(((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getCodExpediente(),
                                ((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
                    }
                }else{
                    RESPUESTA.setCodigoError(Long.parseLong("3"));
                    RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                if(PD_INICIAL != null && PD_INICIAL.isShowing()){
                    PD_INICIAL.dismiss();
                }
                if(RESPUESTA != null
                        && RESPUESTA.getCodigoError() != null) {
                    if (RESPUESTA.getCodigoError().intValue() == 0) {

                        if(getActivity() != null && getActivity().getIntent() != null)
                            getActivity().getIntent().putExtra("cabeceraSintoma", RESPUESTA.getObjecRespuesta());
                        ((EditText) rootView.findViewById(R.id.edtxEstudioParticipante)).setText(RESPUESTA.getObjecRespuesta().getCodConsentimeinto());
                        ((EditText) rootView.findViewById(R.id.edtxtExpedienteExamen)).setText(RESPUESTA.getObjecRespuesta().getExpedienteFisico());
                        ((EditText) rootView.findViewById(R.id.edtxtEdadExamen)).setText(DateUtils.obtenerEdad(RESPUESTA.getObjecRespuesta().getFechaNacimiento()));
                        ((EditText) rootView.findViewById(R.id.edtxtSexoExamen)).setText(RESPUESTA.getObjecRespuesta().getSexo());
                        ((EditText) rootView.findViewById(R.id.edtxtPesoKgExamen)).setText(RESPUESTA.getObjecRespuesta().getPesoKg().toString());
                        ((EditText) rootView.findViewById(R.id.edtxtTallaCmExamen)).setText(RESPUESTA.getObjecRespuesta().getTallaCm().toString());
                        ((EditText) rootView.findViewById(R.id.edtxtTempCExamen)).setText(RESPUESTA.getObjecRespuesta().getTemperaturac().toString());
                        ((EditText) rootView.findViewById(R.id.edtxtFechaExamen)).setText(new SimpleDateFormat("dd/MM/yyyy").format(RESPUESTA.getObjecRespuesta().getFechaConsulta().getTime()));
                        ((EditText) rootView.findViewById(R.id.edtxtHoraExamen)).setText(new SimpleDateFormat("KK:mm a").format(RESPUESTA.getObjecRespuesta().getFechaConsulta().getTime()));
                        ((EditText) rootView.findViewById(R.id.edtxtHoraExamen)).setText(RESPUESTA.getObjecRespuesta().getHoraConsulta());

                    } else if (RESPUESTA.getCodigoError().intValue() != 999) {
                        MensajesHelper.mostrarMensajeInfo(getActivity(),
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);

                    } else {
                        MensajesHelper.mostrarMensajeError(getActivity(),
                                RESPUESTA.getMensajeError(), getResources().getString(
                                        R.string.title_estudio_sostenible), null);
                    }
                }
            }
        };
        ObenterDatosCabServicio.execute((Void[])null);
    }

    /***
     * Metodo que carga la hoja de consulta que se ingresado informacion.
     * @return
     */
    public HojaConsultaDTO cargarHojaConsulta() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        Calendar fechaOrdenLab = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss a", Locale.ENGLISH);

        hojaConsulta.setSecHojaConsulta(((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());
        hojaConsulta.setFechaOrdenLaboratorio(sdf.format(fechaOrdenLab.getTime()));
       //calFechaConsulta.setTime(sdfFechaConsulta.parse(fechaConsulta));
        hojaConsulta.setBhc(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSBHCExamen), getActivity().findViewById(R.id.chkbSBHCExamen)));
        hojaConsulta.setSerologiaDengue(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSSerologiaDengueExamen), getActivity().findViewById(R.id.chkbNSerologiaDengueExamen)));
        hojaConsulta.setSerologiaChik(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSSerologiaChickExamen), getActivity().findViewById(R.id.chkbNSerologiaChickExamen)));
        hojaConsulta.setGotaGruesa(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSGotaGruesaExamen), getActivity().findViewById(R.id.chkbNGotaGruesaExamen)));
        hojaConsulta.setExtendidoPeriferico(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSExtendidoPerExamen), getActivity().findViewById(R.id.chkbNExtendidoPerExamen)));
        hojaConsulta.setEgo(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSEGOExamen), getActivity().findViewById(R.id.chkbNEGOExamen)));
        hojaConsulta.setEgh(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSEGHExamen), getActivity().findViewById(R.id.chkbNEGHExamen)));
        hojaConsulta.setCitologiaFecal(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSCitologiaFecalExamen), getActivity().findViewById(R.id.chkbNCitologiaFecalExamen)));
        hojaConsulta.setFactorReumatoideo(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSFactorReuExamen), getActivity().findViewById(R.id.chkbNFactorReuExamen)));
        hojaConsulta.setAlbumina(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSAlbuminaExamen), getActivity().findViewById(R.id.chkbNAlbuminaExamen)));
        hojaConsulta.setAstAlt(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSAST), getActivity().findViewById(R.id.chkbNAST)));
        //hojaConsulta.setAstAlt(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSASTALTExamen), getActivity().findViewById(R.id.chkbNASTALTExamen)));
        hojaConsulta.setBilirrubinas(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSBilirrubinasExamen), getActivity().findViewById(R.id.chkbNBilirrubinasExamen)));
        hojaConsulta.setCpk(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSCPKExamen), getActivity().findViewById(R.id.chkbNCPKExamen)));
        hojaConsulta.setColesterol(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSColesterolExamen), getActivity().findViewById(R.id.chkbNColesterolExamen)));
        hojaConsulta.setInfluenza(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSInfluenzaExamen), getActivity().findViewById(R.id.chkbNInfluenzaExamen)));
        hojaConsulta.setUsuarioMedico((short)((CssfvApp)getActivity().getApplication()).getInfoSessionWSDTO().getUserId());
        hojaConsulta.setOel(AndroidUtils.resultadoGenericoChkbSND(getActivity().findViewById(R.id.chkbSOtroExamen), getActivity().findViewById(R.id.chkbNOtroExamen)));

        return hojaConsulta;

    }

    /***
     * Metodo que realiza el llamado del servicio que guarda las ordenes de examenes.
     * @param hojaConsulta, Hoja de consulta
     * @param genCtrlCambios, Lista de examenes que han sufrido cambios.
     */
    private void llamandoGuardarExamenesServicio (final HojaConsultaDTO hojaConsulta, final GeneralesControlCambiosDTO genCtrlCambios) {

        AsyncTask<Void, Void, Void> guardarExamenesTask = new AsyncTask<Void, Void, Void>() {

            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private ExamenesWS EXAMENESWS = new ExamenesWS(getResources());

            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(getActivity());
                PD.setTitle(getResources().getString(R.string.tittle_enviando));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){

                    RESPUESTA = EXAMENESWS.guardarExamenes(hojaConsulta, genCtrlCambios);
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
        guardarExamenesTask.execute((Void[])null);
    }

    /***
     * Este metodo valida si el examen viene con información ingresada por el usuario laboratorio.
     * @return True si vienen datos.
     */
    public boolean tieneDatosExamenEgo() {
        if (!StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getColor()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getAspecto()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getSedimento()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getDensidad()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getProteinas()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getHomoglobinas()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getCuerpoCetonico()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getPh()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getUrobilinogeno()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getGlucosa()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getBilirrubinas()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getNitritos()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getCelulasEpiteliales()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getLeucositos()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getEritrocitos()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getCilindros()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getCristales()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getHilosMucosos()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getBacterias()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgo().getLevaduras())) {

            return true;
        }
        return false;
    }

    /***
     * Este metodo valida si el examen viene con información ingresada por el usuario laboratorio.
     * @return True si vienen datos.
     */
    public boolean tieneDatosExamenEgh(){
        if(!StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getColor()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getConsistencia()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getRestosAlimenticios()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getMucus()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getPh()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getSangreOculta()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getBacterias()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getLevaduras()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getLeucocitos()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getEritrocitos()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getFilamentosMucosos()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getEHistolytica()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getGardiaAmblia()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getTrichuris()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getHymenolepisNana()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getStrongyloideStercolaris()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getUnicinarias()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getEgh().getEnterovirus())) {

            return true;
        }
        return false;
    }

    /***
     * Este metodo valida si el examen viene con información ingresada por el usuario laboratorio.
     * @return True si vienen datos.
     */
    public boolean tieneDatosExamenGotaGruesa(){
        if((!StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getMalariaResultado().getPFalciparum()) &&
                (RESULTADO_EXAMEN.getMalariaResultado().getPFalciparum().toLowerCase().trim().compareTo("false") != 0)) ||
                (!StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getMalariaResultado().getPVivax()) &&
                        (RESULTADO_EXAMEN.getMalariaResultado().getPVivax().toLowerCase().trim().compareTo("false") != 0)) ||
                (!StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getMalariaResultado().getNegativo()) &&
                        (RESULTADO_EXAMEN.getMalariaResultado().getNegativo().toLowerCase().trim().compareTo("false") != 0))) {
            return true;
        }
        return false;
    }

    /***
     * Este metodo valida si el examen viene con información ingresada por el usuario laboratorio.
     * @return True si vienen datos.
     */
    public boolean tieneDatosExamenExtendidoPeriferico(){
        if(!StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getPerifericoResultado().getAnisocitosis()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getPerifericoResultado().getAnisocromia()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getPerifericoResultado().getPoiquilocitosis()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getPerifericoResultado().getObservacionSblanca()) ||
                !StringUtils.isNullOrEmpty(RESULTADO_EXAMEN.getPerifericoResultado().getObservacionPlaqueta())) {
            return true;
        }
        return false;
    }

    /***
     * Metodo que realiza el llamado del servicio que busca los examenes que han sido llenados.
     */
    private void llamandoBuscarExamenes() {

        AsyncTask<Void, Void, Void> buscarExamenesTask = new AsyncTask<Void, Void, Void>() {
            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ResultadoObjectWSDTO<ResultadosExamenesDTO> RESPUESTA = new ResultadoObjectWSDTO<ResultadosExamenesDTO>();
            private ExamenesWS EXAMENESWS = new ExamenesWS(getResources());

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

                    RESPUESTA = EXAMENESWS.getListaExamenes((((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto()));
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

                    RESULTADO_EXAMEN = RESPUESTA.getObjecRespuesta();
                    //llenado de la lista cuando la respuesta es positiva

                    if(RESULTADO_EXAMEN.getHojaConsulta().getNumOrdenLaboratorio() != null){
                        ((EditText)getActivity().findViewById(R.id.edtxNumOrenLaboratorio)).setText(RESULTADO_EXAMEN.getHojaConsulta().getNumOrdenLaboratorio().toString());

                        if(RESULTADO_EXAMEN.getEgo() == null){
                            Button botonEgo = (Button)getActivity().findViewById(R.id.btnEGOResultado);
                            Drawable imagenEgo = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonEgo.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenEgo, null);

                        }else{
                            Button botonEgo = (Button)getActivity().findViewById(R.id.btnEGOResultado);
                            Drawable imagenEgo = getResources().getDrawable(R.drawable.check_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonEgo.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenEgo, null);

                            if (tieneDatosExamenEgo() == true) {
                                botonEgo.setBackgroundColor(getActivity().getResources().
                                        getColor(R.color.color_bg_button_celeste_examenes));
                            }
                        }
                        if(RESULTADO_EXAMEN.getEgh() == null){
                            Button botonEgh = (Button)getActivity().findViewById(R.id.btnEGHResultado);
                            Drawable imagenEgh = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonEgh.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenEgh, null);
                        }else{
                            Button botonEgh = (Button)getActivity().findViewById(R.id.btnEGHResultado);
                            Drawable imagenEgh = getResources().getDrawable(R.drawable.check_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonEgh.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenEgh, null);

                            if(tieneDatosExamenEgh() == true) {
                                botonEgh.setBackgroundColor(getActivity().getResources().
                                        getColor(R.color.color_bg_button_celeste_examenes));
                            }
                        }
                        if(RESULTADO_EXAMEN.getMalariaResultado() == null){
                            Button botonMResultado = (Button)getActivity().findViewById(R.id.btnGotaGruesaResultado);
                            Drawable imagenMR = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonMResultado.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenMR, null);

                        }else{
                            Button botonMResultado = (Button)getActivity().findViewById(R.id.btnGotaGruesaResultado);
                            Drawable imagenMR = getResources().getDrawable(R.drawable.check_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonMResultado.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenMR, null);

                            if(tieneDatosExamenGotaGruesa() == true) {
                                botonMResultado.setBackgroundColor(getActivity().getResources().
                                        getColor(R.color.color_bg_button_celeste_examenes));
                            }
                        }
                        if(RESULTADO_EXAMEN.getPerifericoResultado() == null){
                            Button botonPerResult = (Button)getActivity().findViewById(R.id.btnExtendidoPeriferico);
                            Drawable imagenPR = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonPerResult.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenPR, null);
                        }else{
                            Button botonPerResult = (Button)getActivity().findViewById(R.id.btnExtendidoPeriferico);
                            Drawable imagenPR = getResources().getDrawable(R.drawable.check_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonPerResult.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenPR, null);

                            if(tieneDatosExamenExtendidoPeriferico() == true) {
                                botonPerResult.setBackgroundColor(getActivity().getResources().
                                        getColor(R.color.color_bg_button_celeste_examenes));
                            }
                        }
                        if(RESULTADO_EXAMEN.getSerologiaDengue() == null){
                            Button botonSD = (Button)getActivity().findViewById(R.id.btnSerologiaDengue);
                            Drawable imagenSD = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonSD.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenSD, null);
                        }else{
                            Button botonSD = (Button)getActivity().findViewById(R.id.btnSerologiaDengue);
                            Drawable imagenSD = getResources().getDrawable(R.drawable.check_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonSD.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenSD, null);
                        }
                        if(RESULTADO_EXAMEN.getSerologiaChick() == null){
                            Button botonSCK = (Button)getActivity().findViewById(R.id.btnSerologiChick);
                            Drawable imagenSCK = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonSCK.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenSCK, null);
                        }else{
                            Button botonSCK = (Button)getActivity().findViewById(R.id.btnSerologiChick);
                            Drawable imagenSCK = getResources().getDrawable(R.drawable.check_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonSCK.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenSCK, null);
                        }
                        if(RESULTADO_EXAMEN.getInfluenza() == null){
                            Button botonInfluenza = (Button)getActivity().findViewById(R.id.btnInfluenza);
                            Drawable imagenInfluenza = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonInfluenza.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenInfluenza, null);
                        }else{
                            Button botonInfluenza = (Button)getActivity().findViewById(R.id.btnInfluenza);
                            Drawable imagenInfluenza = getResources().getDrawable(R.drawable.check_resultado_exa);
                            // imagenEgo.setBounds(0,0,60,60);
                            botonInfluenza.setCompoundDrawablesWithIntrinsicBounds(null, null, imagenInfluenza, null);
                        }

                        /*Nuevo Grupo de Examenes solicitados**/

                        /*if( RESULTADO_EXAMEN.getHojaConsulta().getBhc() != null) {
                            Button btnBhc = (Button) getActivity().findViewById(R.id.btnBHCesultado);
                            btnBhc.setEnabled(true);
                            Drawable imgBhc = getResources().getDrawable(R.drawable.check_resultado_exa);
                            btnBhc.setCompoundDrawablesWithIntrinsicBounds(null, null, imgBhc, null);
                        }else {
                            Button btnBhc = (Button) getActivity().findViewById(R.id.btnBHCesultado);
                            btnBhc.setEnabled(true);
                            Drawable imgBhc = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            btnBhc.setCompoundDrawablesWithIntrinsicBounds(null, null, imgBhc, null);
                        }
                        if(RESULTADO_EXAMEN.getHojaConsulta().getAlbumina() != null) {
                            Button btnAlbumina = (Button) getActivity().findViewById(R.id.btnAlbuminaReuResultado);
                            btnAlbumina.setEnabled(true);
                            Drawable imgAlbumina = getResources().getDrawable(R.drawable.check_resultado_exa);
                            btnAlbumina.setCompoundDrawablesWithIntrinsicBounds(null, null, imgAlbumina, null);
                        }else {
                            Button btnAlbumina = (Button) getActivity().findViewById(R.id.btnAlbuminaReuResultado);
                            btnAlbumina.setEnabled(true);
                            Drawable imgAlbumina = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            btnAlbumina.setCompoundDrawablesWithIntrinsicBounds(null, null, imgAlbumina, null);
                        }
                        if(RESULTADO_EXAMEN.getHojaConsulta().getAstAlt() != null) {
                            Button btnAstAlt = (Button) getActivity().findViewById(R.id.btnASTALTReuResultado);
                            btnAstAlt.setEnabled(true);
                            Drawable imgAstAlt = getResources().getDrawable(R.drawable.check_resultado_exa);
                            btnAstAlt.setCompoundDrawablesWithIntrinsicBounds(null, null, imgAstAlt, null);
                        }else {
                            Button btnAstAlt = (Button) getActivity().findViewById(R.id.btnASTALTReuResultado);
                            btnAstAlt.setEnabled(true);
                            Drawable imgAstAlt = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            btnAstAlt.setCompoundDrawablesWithIntrinsicBounds(null, null, imgAstAlt, null);
                        }
                        if(RESULTADO_EXAMEN.getHojaConsulta().getBilirrubinas() != null) {
                            Button btnBilirrubinas = (Button) getActivity().findViewById(R.id.btnBilirubinasReuResultado);
                            btnBilirrubinas.setEnabled(true);
                            Drawable imgBilirrubinas = getResources().getDrawable(R.drawable.check_resultado_exa);
                            btnBilirrubinas.setCompoundDrawablesWithIntrinsicBounds(null, null, imgBilirrubinas, null);
                        }else {
                            Button btnBilirrubinas = (Button) getActivity().findViewById(R.id.btnBilirubinasReuResultado);
                            btnBilirrubinas.setEnabled(true);
                            Drawable imgBilirrubinas = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            btnBilirrubinas.setCompoundDrawablesWithIntrinsicBounds(null, null, imgBilirrubinas, null);
                        }
                        if(RESULTADO_EXAMEN.getHojaConsulta().getCpk() != null) {
                            Button btnCpk = (Button) getActivity().findViewById(R.id.btnCPKReuResultado);
                            btnCpk.setEnabled(true);
                            Drawable imgCpk = getResources().getDrawable(R.drawable.check_resultado_exa);
                            btnCpk.setCompoundDrawablesWithIntrinsicBounds(null, null, imgCpk, null);
                        }else {
                            Button btnCpk = (Button) getActivity().findViewById(R.id.btnCPKReuResultado);
                            btnCpk.setEnabled(true);
                            Drawable imgCpk = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            btnCpk.setCompoundDrawablesWithIntrinsicBounds(null, null, imgCpk, null);
                        }
                        if(RESULTADO_EXAMEN.getHojaConsulta().getColesterol() != null) {
                            Button btnColesterol = (Button) getActivity().findViewById(R.id.btnColesterolReuResultado);
                            btnColesterol.setEnabled(true);
                            Drawable imgColesterol = getResources().getDrawable(R.drawable.check_resultado_exa);
                            btnColesterol.setCompoundDrawablesWithIntrinsicBounds(null, null, imgColesterol, null);
                        }else {
                            Button btnColesterol = (Button) getActivity().findViewById(R.id.btnColesterolReuResultado);
                            btnColesterol.setEnabled(true);
                            Drawable imgColesterol = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            btnColesterol.setCompoundDrawablesWithIntrinsicBounds(null, null, imgColesterol, null);
                        }
                        if(RESULTADO_EXAMEN.getHojaConsulta().getFactorReumatoideo() != null) {
                            Button btnFactorReuma = (Button) getActivity().findViewById(R.id.btnFactoroReuResultado);
                            btnFactorReuma.setEnabled(true);
                            Drawable imgFactorReuma = getResources().getDrawable(R.drawable.check_resultado_exa);
                            btnFactorReuma.setCompoundDrawablesWithIntrinsicBounds(null, null, imgFactorReuma, null);
                        }else {
                            Button btnFactorReuma = (Button) getActivity().findViewById(R.id.btnFactoroReuResultado);
                            btnFactorReuma.setEnabled(true);
                            Drawable imgFactorReuma = getResources().getDrawable(R.drawable.delete_resultado_exa);
                            btnFactorReuma.setCompoundDrawablesWithIntrinsicBounds(null, null, imgFactorReuma, null);
                        }*/

                    }


                    if(RESULTADO_EXAMEN.getHojaConsulta().getEgo() !=null){
                        if(RESULTADO_EXAMEN.getHojaConsulta().getEgo().compareTo('0') == 0){
                            Button btnEgo = (Button)getActivity().findViewById(R.id.btnEGOResultado);
                            btnEgo.setEnabled(true);
                        }else{
                            Button btnEgo = (Button)getActivity().findViewById(R.id.btnEGOResultado);
                            btnEgo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }
                    if(RESULTADO_EXAMEN.getHojaConsulta().getEgh() != null){
                        if(RESULTADO_EXAMEN.getHojaConsulta().getEgh().compareTo('0') == 0){
                            Button btnEgh = (Button) getActivity().findViewById(R.id.btnEGHResultado);
                            btnEgh.setEnabled(true);
                        }else{
                            Button btnEgh = (Button) getActivity().findViewById(R.id.btnEGHResultado);
                            btnEgh.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }
                    if(RESULTADO_EXAMEN.getHojaConsulta().getGotaGruesa() != null){
                        if(RESULTADO_EXAMEN.getHojaConsulta().getGotaGruesa().compareTo('0') == 0){
                            Button btnGotaGruesa = (Button)getActivity().findViewById(R.id.btnGotaGruesaResultado);
                            btnGotaGruesa.setEnabled(true);
                        }else {
                            Button btnGotaGruesa = (Button) getActivity().findViewById(R.id.btnGotaGruesaResultado);
                            btnGotaGruesa.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }
                    if(RESULTADO_EXAMEN.getHojaConsulta().getSerologiaChik() != null){
                        if(RESULTADO_EXAMEN.getHojaConsulta().getSerologiaChik().compareTo('0') == 0){
                            Button btnSerologiaChick = (Button)getActivity().findViewById(R.id.btnSerologiChick);
                            btnSerologiaChick.setEnabled(true);
                        }else{
                            Button btnSerologiaChick = (Button)getActivity().findViewById(R.id.btnSerologiChick);
                            btnSerologiaChick.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }
                    if(RESULTADO_EXAMEN.getHojaConsulta().getSerologiaDengue() != null){
                        if(RESULTADO_EXAMEN.getHojaConsulta().getSerologiaDengue().compareTo('0') == 0){
                            Button btnSerologiaDengue = (Button)getActivity().findViewById(R.id.btnSerologiaDengue);
                            btnSerologiaDengue.setEnabled(true);
                        }else {
                            Button btnSerologiaDengue = (Button)getActivity().findViewById(R.id.btnSerologiaDengue);
                            btnSerologiaDengue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }
                    if(RESULTADO_EXAMEN.getHojaConsulta().getInfluenza() != null){
                        if(RESULTADO_EXAMEN.getHojaConsulta().getInfluenza().compareTo('0') == 0){
                            Button btnInfluenza = (Button) getActivity().findViewById(R.id.btnInfluenza);
                            btnInfluenza.setEnabled(true);
                        }else{
                            Button btnInfluenza = (Button) getActivity().findViewById(R.id.btnInfluenza);
                            btnInfluenza.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }
                    if(RESULTADO_EXAMEN.getHojaConsulta().getExtendidoPeriferico() != null){
                        if(RESULTADO_EXAMEN.getHojaConsulta().getExtendidoPeriferico().compareTo('0') == 0){
                            Button btnExtPeriferico = (Button)getActivity().findViewById(R.id.btnExtendidoPeriferico);
                            btnExtPeriferico.setEnabled(true);
                        }else{
                            Button btnExtPeriferico = (Button)getActivity().findViewById(R.id.btnExtendidoPeriferico);
                            btnExtPeriferico.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }


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
        buscarExamenesTask.execute((Void[])null);
    }

    //llamando servicio buscarExamenesChekeados

    private void llamandoBuscarExamenesChekeados() {
        if (mBuscarExamenesChekeadosTask == null || mBuscarExamenesChekeadosTask.getStatus() == AsyncTask.Status.FINISHED) {
            mBuscarExamenesChekeadosTask = new AsyncTask<Void, Void, Void>() {
                private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
                private ResultadoObjectWSDTO<HojaConsultaDTO> RESPUESTA = new ResultadoObjectWSDTO<HojaConsultaDTO>();
                private ResultadoObjectWSDTO<ResultadosExamenesDTO> RESULTADO_EXAMENES = new ResultadoObjectWSDTO<ResultadosExamenesDTO>();
                private ExamenesWS EXAMENESWS = new ExamenesWS(getResources());

                @Override
                protected void onPreExecute() {
                    if (getActivity() != null) {
                        if (PD_INICIAL != null && PD_INICIAL.isShowing()) {
                            PD_INICIAL.dismiss();
                        }
                        PD_INICIAL = new ProgressDialog(getActivity());
                        PD_INICIAL.setTitle(getResources().getString(R.string.title_obteniendo));
                        PD_INICIAL.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                        PD_INICIAL.setCancelable(false);
                        PD_INICIAL.setIndeterminate(true);
                        PD_INICIAL.show();
                    }
                }

                @Override
                protected Void doInBackground(Void... params) {
                    if (NET_INFO != null && NET_INFO.isConnected()) {

                        try {
                            if (getActivity() != null
                                    && getActivity().getIntent() != null
                                    && getActivity().getIntent().getSerializableExtra("pacienteSeleccionado") != null) {
                                RESPUESTA = EXAMENESWS.getListaExamenesChekeados((((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto()));
                                RESULTADO_EXAMENES = EXAMENESWS.getListaExamenes((((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto()));
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    } else {
                        RESPUESTA.setCodigoError(Long.parseLong("3"));
                        RESPUESTA.setMensajeError(getResources().getString(R.string.msj_no_tiene_conexion));
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {

                    if (PD_INICIAL != null && PD_INICIAL.isShowing()) {
                        PD_INICIAL.dismiss();
                    }

                    if (RESPUESTA != null && RESPUESTA.getCodigoError() != null) {
                        if (RESPUESTA.getCodigoError().intValue() == 0) {
                            cargarCheckBox();
                            cargarBotones();
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
                }

                protected void cargarCheckBox() {
                    //llenado de la lista cuando la respuesta es positiva
                    try {
                        if (RESPUESTA != null && RESPUESTA.getObjecRespuesta() != null) {

                            if(getActivity().findViewById(R.id.chkbSBHCExamen) == null) {
                                return;
                            }

                            if (RESPUESTA.getObjecRespuesta().getBhc() != null) {
                                if (RESPUESTA.getObjecRespuesta().getBhc().compareTo('0') == 0) {
                                    CheckBox chkbSBHCExamen = (CheckBox) getActivity().findViewById(R.id.chkbSBHCExamen);
                                    chkbSBHCExamen.setChecked(true);
                                } else {
                                    CheckBox chkbSBHCExamen = (CheckBox) getActivity().findViewById(R.id.chkbNBHCExamen);
                                    chkbSBHCExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getSerologiaDengue() != null) {
                                if (RESPUESTA.getObjecRespuesta().getSerologiaDengue().compareTo('0') == 0) {
                                    CheckBox chkbSSerologiaDengueExamen = (CheckBox) getActivity().findViewById(R.id.chkbSSerologiaDengueExamen);
                                    chkbSSerologiaDengueExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNSerologiaDengueExamen = (CheckBox) getActivity().findViewById(R.id.chkbNSerologiaDengueExamen);
                                    chkbNSerologiaDengueExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getSerologiaChik() != null) {
                                if (RESPUESTA.getObjecRespuesta().getSerologiaChik().compareTo('0') == 0) {
                                    CheckBox chkbSSerologiaChickExamen = (CheckBox) getActivity().findViewById(R.id.chkbSSerologiaChickExamen);
                                    chkbSSerologiaChickExamen.setChecked(true);

                                } else {
                                    CheckBox chkbNSerologiaChickExamen = (CheckBox) getActivity().findViewById(R.id.chkbNSerologiaChickExamen);
                                    chkbNSerologiaChickExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getGotaGruesa() != null) {
                                if (RESPUESTA.getObjecRespuesta().getGotaGruesa().compareTo('0') == 0) {
                                    CheckBox chkbSGotaGruesaExamen = (CheckBox) getActivity().findViewById(R.id.chkbSGotaGruesaExamen);
                                    chkbSGotaGruesaExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNGotaGruesaExamen = (CheckBox) getActivity().findViewById(R.id.chkbNGotaGruesaExamen);
                                    chkbNGotaGruesaExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getExtendidoPeriferico() != null) {
                                if (RESPUESTA.getObjecRespuesta().getExtendidoPeriferico().compareTo('0') == 0) {
                                    CheckBox chkbSExtendidoPerExamen = (CheckBox) getActivity().findViewById(R.id.chkbSExtendidoPerExamen);
                                    chkbSExtendidoPerExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNExtendidoPerExamen = (CheckBox) getActivity().findViewById(R.id.chkbNExtendidoPerExamen);
                                    chkbNExtendidoPerExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getEgo() != null) {
                                if (RESPUESTA.getObjecRespuesta().getEgo().compareTo('0') == 0) {
                                    CheckBox chkbSEGOExamen = (CheckBox) getActivity().findViewById(R.id.chkbSEGOExamen);
                                    chkbSEGOExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNEGOExamen = (CheckBox) getActivity().findViewById(R.id.chkbNEGOExamen);
                                    chkbNEGOExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getEgh() != null) {
                                if (RESPUESTA.getObjecRespuesta().getEgh().compareTo('0') == 0) {
                                    CheckBox chkbSEGHExamen = (CheckBox) getActivity().findViewById(R.id.chkbSEGHExamen);
                                    chkbSEGHExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNEGHExamen = (CheckBox) getActivity().findViewById(R.id.chkbNEGHExamen);
                                    chkbNEGHExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getCitologiaFecal() != null) {
                                if (RESPUESTA.getObjecRespuesta().getCitologiaFecal().compareTo('0') == 0) {
                                    CheckBox chkbSCitologiaFecalExamen = (CheckBox) getActivity().findViewById(R.id.chkbSCitologiaFecalExamen);
                                    chkbSCitologiaFecalExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNCitologiaFecalExamen = (CheckBox) getActivity().findViewById(R.id.chkbNCitologiaFecalExamen);
                                    chkbNCitologiaFecalExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getFactorReumatoideo() != null) {
                                if (RESPUESTA.getObjecRespuesta().getFactorReumatoideo().compareTo('0') == 0) {
                                    CheckBox chkbSFactorReuExamen = (CheckBox) getActivity().findViewById(R.id.chkbSFactorReuExamen);
                                    chkbSFactorReuExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNFactorReuExamen = (CheckBox) getActivity().findViewById(R.id.chkbNFactorReuExamen);
                                    chkbNFactorReuExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getAlbumina() != null) {
                                if (RESPUESTA.getObjecRespuesta().getAlbumina().compareTo('0') == 0) {
                                    CheckBox chkbSAlbuminaExamen = (CheckBox) getActivity().findViewById(R.id.chkbSAlbuminaExamen);
                                    chkbSAlbuminaExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNAlbuminaExamen = (CheckBox) getActivity().findViewById(R.id.chkbNAlbuminaExamen);
                                    chkbNAlbuminaExamen.setChecked(true);
                                }
                            }
                            //===============
                        /*if (RESPUESTA.getObjecRespuesta().getAstAlt() != null) {
                            if (RESPUESTA.getObjecRespuesta().getAstAlt().compareTo('0') == 0) {
                                CheckBox chkbSASTALTExamen = (CheckBox) getActivity().findViewById(R.id.chkbSASTALTExamen);
                                chkbSASTALTExamen.setChecked(true);
                            } else {
                                CheckBox chkbNASTALTExamen = (CheckBox) getActivity().findViewById(R.id.chkbNASTALTExamen);
                                chkbNASTALTExamen.setChecked(true);
                            }
                        }*/
                            if (RESPUESTA.getObjecRespuesta().getAstAlt() != null) {
                                if (RESPUESTA.getObjecRespuesta().getAstAlt().compareTo('0') == 0) {
                                    CheckBox chkbSAST = (CheckBox) getActivity().findViewById(R.id.chkbSAST);
                                    chkbSAST.setChecked(true);
                                } else {
                                    CheckBox chkbNAST = (CheckBox) getActivity().findViewById(R.id.chkbNAST);
                                    chkbNAST.setChecked(true);
                                }
                            }
                            //================
                            if (RESPUESTA.getObjecRespuesta().getBilirrubinas() != null) {
                                if (RESPUESTA.getObjecRespuesta().getBilirrubinas().compareTo('0') == 0) {
                                    CheckBox chkbSBilirrubinasExamen = (CheckBox) getActivity().findViewById(R.id.chkbSBilirrubinasExamen);
                                    chkbSBilirrubinasExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNBilirrubinasExamen = (CheckBox) getActivity().findViewById(R.id.chkbNBilirrubinasExamen);
                                    chkbNBilirrubinasExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getCpk() != null) {
                                if (RESPUESTA.getObjecRespuesta().getCpk().compareTo('0') == 0) {
                                    CheckBox chkbSCPKExamen = (CheckBox) getActivity().findViewById(R.id.chkbSCPKExamen);
                                    chkbSCPKExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNCPKExamen = (CheckBox) getActivity().findViewById(R.id.chkbNCPKExamen);
                                    chkbNCPKExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getColesterol() != null) {
                                if (RESPUESTA.getObjecRespuesta().getColesterol().compareTo('0') == 0) {
                                    CheckBox chkbSColesterolExamen = (CheckBox) getActivity().findViewById(R.id.chkbSColesterolExamen);
                                    chkbSColesterolExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNColesterolExamen = (CheckBox) getActivity().findViewById(R.id.chkbNColesterolExamen);
                                    chkbNColesterolExamen.setChecked(true);
                                }
                            }
                            if (RESPUESTA.getObjecRespuesta().getInfluenza() != null) {
                                if (RESPUESTA.getObjecRespuesta().getInfluenza().compareTo('0') == 0) {
                                    CheckBox chkbSInfluenzaExamen = (CheckBox) getActivity().findViewById(R.id.chkbSInfluenzaExamen);
                                    chkbSInfluenzaExamen.setChecked(true);
                                } else {
                                    CheckBox chkbNInfluenzaExamen = (CheckBox) getActivity().findViewById(R.id.chkbNInfluenzaExamen);
                                    chkbNInfluenzaExamen.setChecked(true);
                                }
                            }

                            //Verificando si otra examen de laboratorio está marcado
                            if (RESPUESTA.getObjecRespuesta().getOel() != null) {
                                if (RESPUESTA.getObjecRespuesta().getOel().compareTo('0') == 0) {
                                    CheckBox chkbSOtroExamen = (CheckBox) getActivity().findViewById(R.id.chkbSOtroExamen);
                                    chkbSOtroExamen.setChecked(true);

                                    if ((!StringUtils.isNullOrEmpty(RESPUESTA.getObjecRespuesta().getOtroExamenLab()))) {
                                        EditText edtxtOtroExamenLab = (EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab);
                                        edtxtOtroExamenLab.setVisibility(View.VISIBLE);
                                        ((EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab)).setText(RESPUESTA.getObjecRespuesta().getOtroExamenLab());
                                    }

                                } else {
                                    CheckBox chkbNOtroExamen = (CheckBox) getActivity().findViewById(R.id.chkbNOtroExamen);
                                    chkbNOtroExamen.setChecked(true);
                                    EditText edtxtOtroExamenLab = (EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab);
                                    edtxtOtroExamenLab.setText("");
                                    edtxtOtroExamenLab.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                EditText edtxtOtroExamenLab = (EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab);
                                edtxtOtroExamenLab.setText("");
                                edtxtOtroExamenLab.setVisibility(View.INVISIBLE);
                            }

                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            MensajesHelper.mostrarMensajeError(getActivity(),
                                    new StringBuffer().append(getResources().getString(
                                            R.string.msj_error_no_controlado)).append(e.getMessage()).toString(),
                                    getResources().getString(R.string.title_estudio_sostenible), null);
                        }
                    }
                }

                protected void cargarBotones() {
                    try {
                        RESULTADO_EXAMEN = RESULTADO_EXAMENES.getObjecRespuesta();
                        if (RESULTADO_EXAMEN != null &&
                                RESULTADO_EXAMEN.getHojaConsulta() != null &&
                                RESULTADO_EXAMEN.getHojaConsulta().getNumOrdenLaboratorio() != null) {

                            if(getActivity().findViewById(R.id.txtvNumOrdenLaboratorio) == null) {
                                return;
                            }

                            getActivity().findViewById(R.id.txtvNumOrdenLaboratorio).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.edtxNumOrenLaboratorio).setVisibility(View.VISIBLE);
                            ((EditText) getActivity().findViewById(R.id.edtxNumOrenLaboratorio)).setText(
                                    RESULTADO_EXAMEN.getHojaConsulta().getNumOrdenLaboratorio().toString());

                            if (RESULTADO_EXAMEN.getHojaConsulta().getEgo().compareTo('0') == 0) {
                                if (RESULTADO_EXAMEN.getEgo() != null) {
                                    Button btnEgo = (Button) getActivity().findViewById(R.id.btnEGOResultado);
                                    btnEgo.setEnabled(true);
                                    Drawable imgEgo = getResources().getDrawable(R.drawable.check_resultado_exa);
                                    btnEgo.setCompoundDrawablesWithIntrinsicBounds(null, null, imgEgo, null);

                                    if (tieneDatosExamenEgo() == true) {
                                        btnEgo.setBackgroundColor(getActivity().getResources().
                                                getColor(R.color.color_bg_button_celeste_examenes));
                                    }
                                } else {
                                    Button btnEgo = (Button) getActivity().findViewById(R.id.btnEGOResultado);
                                    btnEgo.setEnabled(true);
                                    Drawable imgEgo = getResources().getDrawable(R.drawable.delete_resultado_exa);
                                    btnEgo.setCompoundDrawablesWithIntrinsicBounds(null, null, imgEgo, null);
                                }
                            }

                            if (RESULTADO_EXAMEN.getHojaConsulta().getEgh().compareTo('0') == 0) {
                                if (RESULTADO_EXAMEN.getEgh() != null) {
                                    Button btnEgh = (Button) getActivity().findViewById(R.id.btnEGHResultado);
                                    btnEgh.setEnabled(true);
                                    Drawable imgEgh = getResources().getDrawable(R.drawable.check_resultado_exa);
                                    btnEgh.setCompoundDrawablesWithIntrinsicBounds(null, null, imgEgh, null);

                                    if(tieneDatosExamenEgh() == true) {
                                        btnEgh.setBackgroundColor(getActivity().getResources().
                                                getColor(R.color.color_bg_button_celeste_examenes));
                                    }
                                } else {
                                    Button btnEgh = (Button) getActivity().findViewById(R.id.btnEGHResultado);
                                    btnEgh.setEnabled(true);
                                    Drawable imgEgh = getResources().getDrawable(R.drawable.delete_resultado_exa);
                                    btnEgh.setCompoundDrawablesWithIntrinsicBounds(null, null, imgEgh, null);
                                }
                            }

                            if (RESULTADO_EXAMEN.getHojaConsulta().getGotaGruesa().compareTo('0') == 0) {
                                if (RESULTADO_EXAMEN.getMalariaResultado() != null) {
                                    Button btnGotaGruesa = (Button) getActivity().findViewById(R.id.btnGotaGruesaResultado);
                                    btnGotaGruesa.setEnabled(true);
                                    Drawable imgGotaGruesa = getResources().getDrawable(R.drawable.check_resultado_exa);
                                    btnGotaGruesa.setCompoundDrawablesWithIntrinsicBounds(null, null, imgGotaGruesa, null);

                                    if(tieneDatosExamenGotaGruesa() == true) {
                                        btnGotaGruesa.setBackgroundColor(getActivity().getResources().
                                                getColor(R.color.color_bg_button_celeste_examenes));
                                    }
                                } else {
                                    Button btnGotaGruesa = (Button) getActivity().findViewById(R.id.btnGotaGruesaResultado);
                                    btnGotaGruesa.setEnabled(true);
                                    Drawable imgGotaGruesa = getResources().getDrawable(R.drawable.delete_resultado_exa);
                                    btnGotaGruesa.setCompoundDrawablesWithIntrinsicBounds(null, null, imgGotaGruesa, null);
                                }
                            }

                            if (RESULTADO_EXAMEN.getHojaConsulta().getSerologiaChik().compareTo('0') == 0) {
                                if (RESULTADO_EXAMEN.getSerologiaChick() != null) {
                                    Button btnSerologiaChick = (Button) getActivity().findViewById(R.id.btnSerologiChick);
                                    btnSerologiaChick.setEnabled(true);
                                    Drawable imgSerologiaChick = getResources().getDrawable(R.drawable.check_resultado_exa);
                                    btnSerologiaChick.setCompoundDrawablesWithIntrinsicBounds(null, null, imgSerologiaChick, null);
                                } else {
                                    Button btnSerologiaChick = (Button) getActivity().findViewById(R.id.btnSerologiChick);
                                    btnSerologiaChick.setEnabled(true);
                                    Drawable imgSerologiaChick = getResources().getDrawable(R.drawable.delete_resultado_exa);
                                    btnSerologiaChick.setCompoundDrawablesWithIntrinsicBounds(null, null, imgSerologiaChick, null);
                                }
                            }

                            if (RESULTADO_EXAMEN.getHojaConsulta().getSerologiaDengue().compareTo('0') == 0) {
                                if (RESULTADO_EXAMEN.getSerologiaDengue() != null) {
                                    Button btnSerologiaDengue = (Button) getActivity().findViewById(R.id.btnSerologiaDengue);
                                    btnSerologiaDengue.setEnabled(true);
                                    Drawable imgSerologiaDengue = getResources().getDrawable(R.drawable.check_resultado_exa);
                                    btnSerologiaDengue.setCompoundDrawablesWithIntrinsicBounds(null, null, imgSerologiaDengue, null);
                                } else {
                                    Button btnSerologiaDengue = (Button) getActivity().findViewById(R.id.btnSerologiaDengue);
                                    btnSerologiaDengue.setEnabled(true);
                                    Drawable imgSerologiaDengue = getResources().getDrawable(R.drawable.delete_resultado_exa);
                                    btnSerologiaDengue.setCompoundDrawablesWithIntrinsicBounds(null, null, imgSerologiaDengue, null);
                                }
                            }

                            if (RESULTADO_EXAMEN.getHojaConsulta().getInfluenza().compareTo('0') == 0) {
                                if (RESULTADO_EXAMEN.getInfluenza() != null) {
                                    Button btnInfluenza = (Button) getActivity().findViewById(R.id.btnInfluenza);
                                    btnInfluenza.setEnabled(true);
                                    Drawable imgInfluenza = getResources().getDrawable(R.drawable.check_resultado_exa);
                                    btnInfluenza.setCompoundDrawablesWithIntrinsicBounds(null, null, imgInfluenza, null);
                                } else {
                                    Button btnInfluenza = (Button) getActivity().findViewById(R.id.btnInfluenza);
                                    btnInfluenza.setEnabled(true);
                                    Drawable imgInfluenza = getResources().getDrawable(R.drawable.delete_resultado_exa);
                                    btnInfluenza.setCompoundDrawablesWithIntrinsicBounds(null, null, imgInfluenza, null);
                                }
                            }

                            if (RESULTADO_EXAMEN.getHojaConsulta().getExtendidoPeriferico().compareTo('0') == 0) {
                                if (RESULTADO_EXAMEN.getPerifericoResultado() != null) {
                                    Button btnExtPeriferico = (Button) getActivity().findViewById(R.id.btnExtendidoPeriferico);
                                    btnExtPeriferico.setEnabled(true);
                                    Drawable imgExtPeriferico = getResources().getDrawable(R.drawable.check_resultado_exa);
                                    btnExtPeriferico.setCompoundDrawablesWithIntrinsicBounds(null, null, imgExtPeriferico, null);

                                    if(tieneDatosExamenExtendidoPeriferico() == true) {
                                        btnExtPeriferico.setBackgroundColor(getActivity().getResources().
                                                getColor(R.color.color_bg_button_celeste_examenes));
                                    }
                                } else {
                                    Button btnExtPeriferico = (Button) getActivity().findViewById(R.id.btnExtendidoPeriferico);
                                    btnExtPeriferico.setEnabled(true);
                                    Drawable imgExtPeriferico = getResources().getDrawable(R.drawable.delete_resultado_exa);
                                    btnExtPeriferico.setCompoundDrawablesWithIntrinsicBounds(null, null, imgExtPeriferico, null);
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            MensajesHelper.mostrarMensajeError(getActivity(),
                                    new StringBuffer().append(getResources().getString(
                                            R.string.msj_error_no_controlado)).append(e.getMessage()).toString(),
                                    getResources().getString(R.string.title_estudio_sostenible), null);
                        }

                    }
                }

            };
            mBuscarExamenesChekeadosTask.execute((Void[]) null);
        }
    }

    //Llamando a la funcion que Guarda el Campo OTro Examen

    private void llamandoGuardarOtroExamenServicio (final HojaConsultaDTO hojaConsulta) {

        AsyncTask<Void, Void, Void> guardarOtroExamenTask = new AsyncTask<Void, Void, Void>() {

            private ProgressDialog PD;
            private ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            private NetworkInfo NET_INFO = CM.getActiveNetworkInfo();
            private ErrorDTO RESPUESTA = new ErrorDTO();
            private ExamenesWS EXAMENESWS = new ExamenesWS(getResources());

            @Override
            protected void onPreExecute() {
                PD = new ProgressDialog(getActivity());
                PD.setTitle(getResources().getString(R.string.tittle_enviando));
                PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                PD.setCancelable(false);
                PD.setIndeterminate(true);
                PD.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (NET_INFO != null && NET_INFO.isConnected()){

                    //RESPUESTA = EXAMENESWS.getListaExamenes((((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto()));

                    RESPUESTA = EXAMENESWS.guardarOTroExamen(hojaConsulta);

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
        guardarOtroExamenTask.execute((Void[])null);
    }

    //==============================================================================================
    public HojaConsultaDTO cargarHojaConsultaOtroExa() {
        HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();

        hojaConsulta.setSecHojaConsulta(((InicioDTO) getActivity().getIntent().getSerializableExtra("pacienteSeleccionado")).getIdObjeto());

        hojaConsulta.setOtroExamenLab(String.valueOf(((EditText) getActivity().findViewById(R.id.edtxtOtroExamenLab)).getText().toString()));

        return hojaConsulta;

    }

    private void guardarOtroExamenLab(){
       HojaConsultaDTO hojaConsulta = cargarHojaConsultaOtroExa();
       llamandoGuardarOtroExamenServicio(hojaConsulta);
    }

    /***
     * Metodo que llama el serivicio que obtiene la Hoja de consulta PDF.
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

                if(getActivity() != null) {
                    if (PD != null && PD.isShowing()) {
                        PD.dismiss();
                    }

                    PD = new ProgressDialog(getActivity());
                    PD.setTitle(getResources().getString(R.string.title_obteniendo));
                    PD.setMessage(getResources().getString(R.string.msj_espere_por_favor));
                    PD.setCancelable(false);
                    PD.setIndeterminate(true);
                    PD.show();
                }
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
                if(PD != null && PD.isShowing()) {
                    PD.dismiss();
                }

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
                    MensajesHelper.mostrarMensajeError(getActivity(),
                            new StringBuffer().append(getResources().getString(
                                    R.string.msj_error_no_controlado)).append(e.getMessage()).toString(),
                            getResources().getString(R.string.title_estudio_sostenible), null);
                }

            }
        };
        hojaconsultapdf.execute((Void[])null);
    }

    private void cancelarBucarExamanesTask() {
        if (mBuscarExamenesChekeadosTask != null && mBuscarExamenesChekeadosTask.getStatus() == AsyncTask.Status.RUNNING) {
            mBuscarExamenesChekeadosTask.cancel(true);
            mBuscarExamenesChekeadosTask = null;
        }
    }
}
