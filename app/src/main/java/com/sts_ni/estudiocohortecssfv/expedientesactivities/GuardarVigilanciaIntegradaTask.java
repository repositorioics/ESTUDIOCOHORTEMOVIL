package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.VigilanciaIntegradaIragEtiDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.VigilanciaIntegradaWS;

public class GuardarVigilanciaIntegradaTask extends UserTask<Object, Void, ErrorDTO> {
    private final Object mLock = new Object();
    private VigilanciaIntegradaActivity vigilanciaIntegradaActivity;
    private ProgressDialog PD;

    public GuardarVigilanciaIntegradaTask(VigilanciaIntegradaActivity vigilanciaIntegradaActivity) {
        this.vigilanciaIntegradaActivity = vigilanciaIntegradaActivity;
    }

    @Override
    public void onPreExecute() {
        synchronized (mLock) {
            PD = new ProgressDialog(this.vigilanciaIntegradaActivity);
            PD.setTitle(this.vigilanciaIntegradaActivity.getResources().getString(R.string.msj_mensaje_enviado));
            PD.setMessage(this.vigilanciaIntegradaActivity.getResources().getString(R.string.msj_espere_por_favor));
            PD.setCancelable(false);
            PD.setIndeterminate(true);
            PD.show();
        }
    }

    public ErrorDTO doInBackground(Object... params) {
        ErrorDTO resultado = new ErrorDTO();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.vigilanciaIntegradaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            VigilanciaIntegradaIragEtiDTO vigilanciaIntegrada = (VigilanciaIntegradaIragEtiDTO) params[0];
            VigilanciaIntegradaWS vigilanciaIntegradaWS = new VigilanciaIntegradaWS(this.vigilanciaIntegradaActivity.getResources());
            resultado = vigilanciaIntegradaWS.guardarFichaVigilanciaIntegrada(vigilanciaIntegrada);
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.vigilanciaIntegradaActivity.getResources().getString(R.string.msj_no_tiene_conexion));
        }

        return resultado;
    }

    @Override
    public void onPostExecute(ErrorDTO respuesta) {
        if ((PD != null) && PD.isShowing()) {
            PD.dismiss();
        }
        if (respuesta.getCodigoError().intValue() == 0) {
            MensajesHelper.mostrarMensajeInfo(this.vigilanciaIntegradaActivity,
                    "Ficha vigilancia de infecciones respiratorias guardada.", this.vigilanciaIntegradaActivity.getResources().getString(
                            R.string.app_name), null);
            //this.vigilanciaIntegradaActivity.actualizarObjetos();
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
