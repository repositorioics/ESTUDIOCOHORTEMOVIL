package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.HojaInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeguimientoInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.SeguimientoInfluenzaWS;

/**
 * Created by MiguelLopez
 * Created at 09/10/2015.
 */
public class BuscarSeguimientoTask extends UserTask<String, Void, ResultadoObjectWSDTO<HojaInfluenzaDTO>> {

    private final Object mLock = new Object();
    private SeguimientoInfluenzaActivity seguimientoInfluenzaActivity;
    private ProgressDialog PD;
    private String mOpcionBusqueda;
    private String mCodigoBusqueda;

    public BuscarSeguimientoTask(SeguimientoInfluenzaActivity seguimientoInfluenzaActivity) {
        this.seguimientoInfluenzaActivity = seguimientoInfluenzaActivity;
    }

    @Override
    public void onPreExecute() {
        synchronized (mLock) {
            PD = new ProgressDialog(this.seguimientoInfluenzaActivity);
            PD.setTitle(this.seguimientoInfluenzaActivity.getResources().getString(R.string.title_obteniendo));
            PD.setMessage(this.seguimientoInfluenzaActivity.getResources().getString(R.string.msj_espere_por_favor));
            PD.setCancelable(false);
            PD.setIndeterminate(true);
            PD.show();
        }
    }

    public ResultadoObjectWSDTO<HojaInfluenzaDTO> doInBackground(String... params) {
        ResultadoObjectWSDTO<HojaInfluenzaDTO> resultado = new ResultadoObjectWSDTO<>();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.seguimientoInfluenzaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            synchronized (mLock) {
                mOpcionBusqueda = params[0];
                mCodigoBusqueda = params[1];
            }
            SeguimientoInfluenzaWS influenzaWS = new SeguimientoInfluenzaWS(this.seguimientoInfluenzaActivity.getResources());
            if (params[0].contains("0")) {
                resultado = influenzaWS.buscarPacienteCrearHoja(params[1]);
            } else if (params[0].contains("1")) {
                resultado = influenzaWS.buscarPaciente(params[1]);
                if (resultado.getCodigoError().intValue() == 0) {
                    ResultadoListWSDTO<SeguimientoInfluenzaDTO> seguimientosInfluenza = influenzaWS.getListaSeguimientoInfluenza(
                            resultado.getObjecRespuesta().getSecHojaInfluenza());
                    if (seguimientosInfluenza.getCodigoError().intValue() == 0) {
                        resultado.getObjecRespuesta().setLstSeguimientoInfluenza(seguimientosInfluenza.getLstResultado());
                    }
                }
            } else {
                resultado = influenzaWS.buscarSeguimientoInfluenza(params[1]);
                if (resultado.getCodigoError().intValue() == 0) {
                    ResultadoListWSDTO<SeguimientoInfluenzaDTO> seguimientosInfluenza = influenzaWS.getListaSeguimientoInfluenza(
                            resultado.getObjecRespuesta().getSecHojaInfluenza());
                    if (seguimientosInfluenza.getCodigoError().intValue() == 0) {
                        resultado.getObjecRespuesta().setLstSeguimientoInfluenza(seguimientosInfluenza.getLstResultado());
                    }
                }
            }
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.seguimientoInfluenzaActivity.getResources().getString(R.string.msj_no_tiene_conexion));
        }

        return resultado;
    }

    @Override
    public void onPostExecute(ResultadoObjectWSDTO<HojaInfluenzaDTO> respuesta) {
        if ((PD != null) && PD.isShowing()) {
            PD.dismiss();
        }
        if (respuesta.getCodigoError().intValue() == 0) {
            final ResultadoObjectWSDTO<HojaInfluenzaDTO> fRespuesta = respuesta;
            if (mOpcionBusqueda.contains("0")) {
                DialogInterface.OnClickListener crearNuevaHojaDcl = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                new CrearHojaTask(seguimientoInfluenzaActivity).execute(mCodigoBusqueda, fRespuesta.
                                        getObjecRespuesta().getNomPaciente(), fRespuesta.getObjecRespuesta().getEstudioPaciente());
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                MensajesHelper.mostrarMensajeYesNo(this.seguimientoInfluenzaActivity,
                        this.seguimientoInfluenzaActivity.getResources().getString(R.string.msj_crear_nueva_hoja_seguimiento),
                        this.seguimientoInfluenzaActivity.getResources().getString(R.string.title_estudio_sostenible),
                        crearNuevaHojaDcl);
            } else {
                this.seguimientoInfluenzaActivity.cargarDatosInfluenzaUI(respuesta.getObjecRespuesta());
            }
        } else if (respuesta.getCodigoError().intValue() != 999) {
            MensajesHelper.mostrarMensajeInfo(this.seguimientoInfluenzaActivity,
                    respuesta.getMensajeError(), this.seguimientoInfluenzaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
        } else {
            MensajesHelper.mostrarMensajeError(this.seguimientoInfluenzaActivity,
                    respuesta.getMensajeError(), this.seguimientoInfluenzaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
        }
    }
}
