package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.HojaInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.SeguimientoInfluenzaWS;

import java.util.Calendar;

/**
 * Created by MiguelLopez
 * Created at 08/10/2015.
 */
class CrearHojaTask extends UserTask<String, Void, ResultadoObjectWSDTO<HojaInfluenzaDTO>> {

    private final Object mLock = new Object();
    private SeguimientoInfluenzaActivity seguimientoInfluenzaActivity;
    private ProgressDialog PD;
    private String mNombrePaciente;
    private String mEstudioPaciente;

    public CrearHojaTask(SeguimientoInfluenzaActivity seguimientoInfluenzaActivity) {
        this.seguimientoInfluenzaActivity = seguimientoInfluenzaActivity;
    }

    @Override
    public void onPreExecute() {
        PD = new ProgressDialog(this.seguimientoInfluenzaActivity);
        PD.setTitle(this.seguimientoInfluenzaActivity.getResources().getString(R.string.title_obteniendo));
        PD.setMessage(this.seguimientoInfluenzaActivity.getResources().getString(R.string.msj_espere_por_favor));
        PD.setCancelable(false);
        PD.setIndeterminate(true);
        PD.show();
    }

    public ResultadoObjectWSDTO<HojaInfluenzaDTO> doInBackground(String... params) {
        ResultadoObjectWSDTO<HojaInfluenzaDTO> resultado = new ResultadoObjectWSDTO<>();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.seguimientoInfluenzaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            synchronized (mLock) {
                mNombrePaciente = params[1];
                mEstudioPaciente = params[2];
            }
            SeguimientoInfluenzaWS influenzaWS = new SeguimientoInfluenzaWS(this.seguimientoInfluenzaActivity.getResources());
            HojaInfluenzaDTO hojaInfluenza = new HojaInfluenzaDTO();
            hojaInfluenza.setCodExpediente(Integer.parseInt(params[0]));
            hojaInfluenza.setFechaInicio(Calendar.getInstance());
            resultado = influenzaWS.crearHojaSeguimiento(hojaInfluenza);
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
        if (respuesta.getCodigoError().intValue() == 0){
            this.seguimientoInfluenzaActivity.cargarDatosCrearHojaUI(mNombrePaciente, mEstudioPaciente, respuesta.getObjecRespuesta());
        }else if (respuesta.getCodigoError().intValue() != 999){
            MensajesHelper.mostrarMensajeInfo(this.seguimientoInfluenzaActivity,
                    respuesta.getMensajeError(), this.seguimientoInfluenzaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
            seguimientoInfluenzaActivity.findViewById(R.id.edtxtNumSeguimiento).setEnabled(true);
        }else {
            MensajesHelper.mostrarMensajeError(this.seguimientoInfluenzaActivity,
                    respuesta.getMensajeError(), this.seguimientoInfluenzaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
            seguimientoInfluenzaActivity.findViewById(R.id.edtxtNumSeguimiento).setEnabled(true);
        }
    }
}
