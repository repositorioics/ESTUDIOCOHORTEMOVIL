package com.sts_ni.estudiocohortecssfv.diagnostiscoactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.EscuelaPacienteDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeguimientoInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.DiagnosticoWS;
import com.sts_ni.estudiocohortecssfv.ws.SeguimientoInfluenzaWS;

/**
 * Created by MiguelLopez on 17/10/2015.
 */
public class ObtenerEscuelaTask extends UserTask<String, Void, ResultadoListWSDTO<EscuelaPacienteDTO>> {

    private final Object mLock = new Object();
    private DiagnosticoProximaCitaActivity diagnosticoProximaCitaActivity;
    private ProgressDialog PD;

    public ObtenerEscuelaTask(DiagnosticoProximaCitaActivity diagnosticoProximaCitaActivity) {
        this.diagnosticoProximaCitaActivity = diagnosticoProximaCitaActivity;
    }

    @Override
    public void onPreExecute() {
        synchronized (mLock) {
            PD = new ProgressDialog(this.diagnosticoProximaCitaActivity);
            PD.setTitle(this.diagnosticoProximaCitaActivity.getResources().getString(R.string.title_obteniendo));
            PD.setMessage(this.diagnosticoProximaCitaActivity.getResources().getString(R.string.msj_espere_por_favor));
            PD.setCancelable(false);
            PD.setIndeterminate(true);
            PD.show();
        }
    }

    public ResultadoListWSDTO<EscuelaPacienteDTO> doInBackground(String... params) {
        ResultadoListWSDTO<EscuelaPacienteDTO> resultado = new ResultadoListWSDTO<>();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.diagnosticoProximaCitaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            DiagnosticoWS diagnosticoWS = new DiagnosticoWS(this.diagnosticoProximaCitaActivity.getResources());
            resultado = diagnosticoWS.getListaEscuela();
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.diagnosticoProximaCitaActivity.getResources().getString(R.string.msj_no_tiene_conexion));
        }

        return resultado;
    }

    @Override
    public void onPostExecute(ResultadoListWSDTO<EscuelaPacienteDTO> respuesta) {
        if ((PD != null) && PD.isShowing()) {
            PD.dismiss();
        }
        if (respuesta.getCodigoError().intValue() == 0) {
            this.diagnosticoProximaCitaActivity.cargarListaEscuela(respuesta.getLstResultado());
        } else if (respuesta.getCodigoError().intValue() != 999) {
            MensajesHelper.mostrarMensajeInfo(this.diagnosticoProximaCitaActivity,
                    respuesta.getMensajeError(), this.diagnosticoProximaCitaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
        } else {
            MensajesHelper.mostrarMensajeError(this.diagnosticoProximaCitaActivity,
                    respuesta.getMensajeError(), this.diagnosticoProximaCitaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
        }
    }
}
