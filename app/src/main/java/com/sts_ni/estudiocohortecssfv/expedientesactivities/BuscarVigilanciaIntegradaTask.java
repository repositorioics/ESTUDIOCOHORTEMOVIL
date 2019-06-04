package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.HojaInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.VigilanciaIntegradaIragEtiDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.VigilanciaIntegradaWS;

public class BuscarVigilanciaIntegradaTask extends UserTask<String, Void, ResultadoObjectWSDTO<VigilanciaIntegradaIragEtiDTO>> {
    private final Object mLock = new Object();
    private VigilanciaIntegradaActivity vigilanciaIntegradaActivity;
    private ProgressDialog PD;
    private String mOpcionBusqueda;
    private String mCodigoBusqueda;

    public BuscarVigilanciaIntegradaTask(VigilanciaIntegradaActivity vigilanciaIntegradaActivity) {
        this.vigilanciaIntegradaActivity = vigilanciaIntegradaActivity;
    }

    @Override
    public void onPreExecute() {
        synchronized (mLock) {
            PD = new ProgressDialog(this.vigilanciaIntegradaActivity);
            PD.setTitle(this.vigilanciaIntegradaActivity.getResources().getString(R.string.title_obteniendo));
            PD.setMessage(this.vigilanciaIntegradaActivity.getResources().getString(R.string.msj_espere_por_favor));
            PD.setCancelable(false);
            PD.setIndeterminate(true);
            PD.show();
        }
    }

    public ResultadoObjectWSDTO<VigilanciaIntegradaIragEtiDTO> doInBackground(String... params) {
        ResultadoObjectWSDTO<VigilanciaIntegradaIragEtiDTO> resultado = new ResultadoObjectWSDTO<>();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.vigilanciaIntegradaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            synchronized (mLock) {
                mCodigoBusqueda = params[0];
            }
            VigilanciaIntegradaWS vigilanciaIntegradaWS = new VigilanciaIntegradaWS(this.vigilanciaIntegradaActivity.getResources());
            if (params[0].contains("1")) {
                resultado = vigilanciaIntegradaWS.buscarFichaByCodExpediente(params[1], params[2]);
            } /*else if (params[0].contains("1")) {
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
            }*/
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.vigilanciaIntegradaActivity.getResources().getString(R.string.msj_no_tiene_conexion));
        }
        return resultado;
    }

    @Override
    public void onPostExecute(ResultadoObjectWSDTO<VigilanciaIntegradaIragEtiDTO> respuesta) {
        if ((PD != null) && PD.isShowing()) {
            PD.dismiss();
        }
        if (respuesta.getCodigoError().intValue() == 0) {
            this.vigilanciaIntegradaActivity.cargarDatosObtenidos(respuesta.getObjecRespuesta());
        }else if (respuesta.getCodigoError().intValue() != 999) {
            MensajesHelper.mostrarMensajeInfo(this.vigilanciaIntegradaActivity,
                    respuesta.getMensajeError(), this.vigilanciaIntegradaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
        } else {
            MensajesHelper.mostrarMensajeError(this.vigilanciaIntegradaActivity,
                    respuesta.getMensajeError(), this.vigilanciaIntegradaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
        }
    }
}
