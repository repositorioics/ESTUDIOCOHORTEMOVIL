package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.SeguimientoInfluenzaWS;

/**
 * Created by MiguelLopez on 12/10/2015.
 */
public class GuardarSeguimientoTask extends UserTask<Object, Void, ErrorDTO> {

    private final Object mLock = new Object();
    private SeguimientoInfluenzaActivity seguimientoInfluenzaActivity;
    private ProgressDialog PD;

    public GuardarSeguimientoTask(SeguimientoInfluenzaActivity seguimientoInfluenzaActivity) {
        this.seguimientoInfluenzaActivity = seguimientoInfluenzaActivity;
    }

    @Override
    public void onPreExecute() {
        synchronized (mLock) {
            PD = new ProgressDialog(this.seguimientoInfluenzaActivity);
            PD.setTitle(this.seguimientoInfluenzaActivity.getResources().getString(R.string.msj_mensaje_enviado));
            PD.setMessage(this.seguimientoInfluenzaActivity.getResources().getString(R.string.msj_espere_por_favor));
            PD.setCancelable(false);
            PD.setIndeterminate(true);
            PD.show();
        }
    }

    public ErrorDTO doInBackground(Object... params) {
        ErrorDTO resultado = new ErrorDTO();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.seguimientoInfluenzaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            HojaInfluenzaDTO hojaInfluenza = (HojaInfluenzaDTO) params[0];
            SeguimientoInfluenzaWS influenzaWS = new SeguimientoInfluenzaWS(this.seguimientoInfluenzaActivity.getResources());
            String user = "";
            resultado = influenzaWS.guardarHojaSeguimiento(hojaInfluenza, hojaInfluenza.getLstSeguimientoInfluenza(), user);
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.seguimientoInfluenzaActivity.getResources().getString(R.string.msj_no_tiene_conexion));
        }

        return resultado;
    }

    @Override
    public void onPostExecute(ErrorDTO respuesta) {
        if ((PD != null) && PD.isShowing()) {
            PD.dismiss();
        }
        if (respuesta.getCodigoError().intValue() == 0) {
            MensajesHelper.mostrarMensajeInfo(this.seguimientoInfluenzaActivity,
                    "Hoja de Seguimiento Influenza Guardada.", this.seguimientoInfluenzaActivity.getResources().getString(
                            R.string.app_name), null);
            this.seguimientoInfluenzaActivity.actualizarObjetos();
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
