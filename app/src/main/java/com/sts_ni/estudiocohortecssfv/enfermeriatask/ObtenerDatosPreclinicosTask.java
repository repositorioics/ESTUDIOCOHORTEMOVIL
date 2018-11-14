package com.sts_ni.estudiocohortecssfv.enfermeriatask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.PreClinicosActivity;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.EnfermeriaWS;

/**
 * Created by Miguel Lopez
 * Created At 26/10/2015.
 */
public class ObtenerDatosPreclinicosTask extends UserTask<Integer, Void, ResultadoObjectWSDTO<CabeceraSintomaDTO>> {

    private final Object mLock = new Object();
    private PreClinicosActivity preClinicosActivity;
    private ProgressDialog PD;

    public ObtenerDatosPreclinicosTask(PreClinicosActivity preClinicosActivity) {
        this.preClinicosActivity = preClinicosActivity;
    }

    @Override
    public void onPreExecute() {
        synchronized (mLock) {
            PD = new ProgressDialog(this.preClinicosActivity);
            PD.setTitle(this.preClinicosActivity.getResources().getString(R.string.title_obteniendo));
            PD.setMessage(this.preClinicosActivity.getResources().getString(R.string.msj_espere_por_favor));
            PD.setCancelable(false);
            PD.setIndeterminate(true);
            PD.show();
        }
    }

    public ResultadoObjectWSDTO<CabeceraSintomaDTO> doInBackground(Integer... params) {
        ResultadoObjectWSDTO<CabeceraSintomaDTO> resultado = new ResultadoObjectWSDTO<>();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.preClinicosActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {

            EnfermeriaWS enfermeriaWS = new EnfermeriaWS(this.preClinicosActivity.getResources());
            resultado = enfermeriaWS.obtenerDatosPreclinicos(params[0]);
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.preClinicosActivity.getResources().getString(R.string.msj_no_tiene_conexion));
        }

        return resultado;
    }

    @Override
    public void onPostExecute(ResultadoObjectWSDTO<CabeceraSintomaDTO> respuesta) {
        if ((PD != null) && PD.isShowing()) {
            PD.dismiss();
        }
        if (respuesta.getCodigoError().intValue() == 0) {
            this.preClinicosActivity.cargarDatosUI(respuesta.getObjecRespuesta());
        } else if (respuesta.getCodigoError().intValue() != 999) {
            MensajesHelper.mostrarMensajeInfo(this.preClinicosActivity,
                    respuesta.getMensajeError(), this.preClinicosActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
        } else {
            MensajesHelper.mostrarMensajeError(this.preClinicosActivity,
                    respuesta.getMensajeError(), this.preClinicosActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
        }
    }

}

