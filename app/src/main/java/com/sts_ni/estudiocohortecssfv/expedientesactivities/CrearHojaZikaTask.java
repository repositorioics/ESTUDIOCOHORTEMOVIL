package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.HojaZikaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.SeguimientoZikaWS;

import java.util.Calendar;

/**
 * Created by MiguelLopez
 * Created at 17/04/2017.
 */
class CrearHojaZikaTask extends UserTask<String, Void, ResultadoObjectWSDTO<HojaZikaDTO>> {

    private final Object mLock = new Object();
    private SeguimientoZikaActivity seguimientoZikaActivity;
    private ProgressDialog PD;
    private String mNombrePaciente;
    private String mEstudioPaciente;

    public CrearHojaZikaTask(SeguimientoZikaActivity seguimientoZikaActivity) {
        this.seguimientoZikaActivity = seguimientoZikaActivity;
    }

    @Override
    public void onPreExecute() {
        PD = new ProgressDialog(this.seguimientoZikaActivity);
        PD.setTitle(this.seguimientoZikaActivity.getResources().getString(R.string.title_obteniendo));
        PD.setMessage(this.seguimientoZikaActivity.getResources().getString(R.string.msj_espere_por_favor));
        PD.setCancelable(false);
        PD.setIndeterminate(true);
        PD.show();
    }

    public ResultadoObjectWSDTO<HojaZikaDTO> doInBackground(String... params) {
        ResultadoObjectWSDTO<HojaZikaDTO> resultado = new ResultadoObjectWSDTO<>();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.seguimientoZikaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            synchronized (mLock) {
                mNombrePaciente = params[1];
                mEstudioPaciente = params[2];
            }
            SeguimientoZikaWS zikaWS = new SeguimientoZikaWS(this.seguimientoZikaActivity.getResources());
            HojaZikaDTO hojaZika = new HojaZikaDTO();
            hojaZika.setCodExpediente(Integer.parseInt(params[0]));
            hojaZika.setFechaInicio(Calendar.getInstance());
            resultado = zikaWS.crearHojaSeguimiento(hojaZika);
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.seguimientoZikaActivity.getResources().getString(R.string.msj_no_tiene_conexion));
        }

        return resultado;
    }

    @Override
    public void onPostExecute(ResultadoObjectWSDTO<HojaZikaDTO> respuesta) {
        if ((PD != null) && PD.isShowing()) {
            PD.dismiss();
        }
        if (respuesta.getCodigoError().intValue() == 0){
            this.seguimientoZikaActivity.cargarDatosCrearHojaUI(mNombrePaciente, mEstudioPaciente, respuesta.getObjecRespuesta());
        }else if (respuesta.getCodigoError().intValue() != 999){
            MensajesHelper.mostrarMensajeInfo(this.seguimientoZikaActivity,
                    respuesta.getMensajeError(), this.seguimientoZikaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
            seguimientoZikaActivity.findViewById(R.id.edtxtNumSeguimiento).setEnabled(true);
        }else {
            MensajesHelper.mostrarMensajeError(this.seguimientoZikaActivity,
                    respuesta.getMensajeError(), this.seguimientoZikaActivity.getResources().getString(
                            R.string.title_estudio_sostenible), null);
            seguimientoZikaActivity.findViewById(R.id.edtxtNumSeguimiento).setEnabled(true);
        }
    }
}
