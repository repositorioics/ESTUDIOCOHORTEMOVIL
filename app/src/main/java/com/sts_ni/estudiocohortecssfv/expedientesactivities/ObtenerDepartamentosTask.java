package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.DepartamentosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.ExpedienteWS;

public class ObtenerDepartamentosTask extends UserTask<String, Void, ResultadoListWSDTO<DepartamentosDTO>> {
    private final Object mLock = new Object();
    private VigilanciaIntegradaActivity vigilanciaIntegradaActivity;
    private ProgressDialog PD;

    public ObtenerDepartamentosTask(VigilanciaIntegradaActivity vigilanciaIntegradaActivity) {
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

    public ResultadoListWSDTO<DepartamentosDTO> doInBackground(String... params) {
        ResultadoListWSDTO<DepartamentosDTO> resultado = new ResultadoListWSDTO<>();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.vigilanciaIntegradaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            ExpedienteWS expedienteWS = new ExpedienteWS(this.vigilanciaIntegradaActivity.getResources());
            resultado = expedienteWS.getListaDepartamento();
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.vigilanciaIntegradaActivity.getResources().getString(R.string.msj_no_tiene_conexion));
        }
        return resultado;
    }

    @Override
    public void onPostExecute(ResultadoListWSDTO<DepartamentosDTO> respuesta) {
        if ((PD != null) && PD.isShowing()) {
            PD.dismiss();
        }
        if (respuesta.getCodigoError().intValue() == 0) {
            this.vigilanciaIntegradaActivity.cargarListaDepartamento(respuesta.getLstResultado());
        } else if (respuesta.getCodigoError().intValue() != 999) {
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
