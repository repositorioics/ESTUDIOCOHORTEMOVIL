package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaZikaDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.SeguimientoZikaWS;

/**
 * Created by ics on 17/04/2017.
 */
public class GuardarSeguimientoZikaTask extends UserTask<Object, Void, ErrorDTO> {

    private final Object mLock = new Object();
    private SeguimientoZikaActivity seguimientoZikaActivity;
    private ProgressDialog PD;

    public GuardarSeguimientoZikaTask(SeguimientoZikaActivity seguimientoZikaActivity) {
        this.seguimientoZikaActivity = seguimientoZikaActivity;
    }

    @Override
    public void onPreExecute() {
        synchronized (mLock) {
            PD = new ProgressDialog(this.seguimientoZikaActivity);
            PD.setTitle(this.seguimientoZikaActivity.getResources().getString(R.string.msj_mensaje_enviado));
            PD.setMessage(this.seguimientoZikaActivity.getResources().getString(R.string.msj_espere_por_favor));
            PD.setCancelable(false);
            PD.setIndeterminate(true);
            PD.show();
        }
    }

    public ErrorDTO doInBackground(Object... params) {
        ErrorDTO resultado = new ErrorDTO();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.seguimientoZikaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            HojaZikaDTO hojaZika = (HojaZikaDTO) params[0];
            String user = "";
            String consultorio = "";
            SeguimientoZikaWS zikaWS = new SeguimientoZikaWS(this.seguimientoZikaActivity.getResources());
            resultado = zikaWS.guardarHojaSeguimiento(hojaZika, hojaZika.getLstSeguimientoZika(), user, consultorio);
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.seguimientoZikaActivity.getResources().getString(R.string.msj_no_tiene_conexion));
        }

        return resultado;
    }

    @Override
    public void onPostExecute(ErrorDTO respuesta) {
        if ((PD != null) && PD.isShowing()) {
            PD.dismiss();
        }
        if (respuesta.getCodigoError().intValue() == 0) {
            MensajesHelper.mostrarMensajeInfo(this.seguimientoZikaActivity,
                    "Hoja de Seguimiento Zika Guardada.", this.seguimientoZikaActivity.getResources().getString(
                            R.string.app_name), null);
            this.seguimientoZikaActivity.actualizarObjetos();
        } else if (respuesta.getCodigoError().intValue() != 999) {
            MensajesHelper.mostrarMensajeInfo(this.seguimientoZikaActivity,
                    respuesta.getMensajeError(), this.seguimientoZikaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
        } else {
            MensajesHelper.mostrarMensajeError(this.seguimientoZikaActivity,
                    respuesta.getMensajeError(), this.seguimientoZikaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
        }
    }
}
