package com.sts_ni.estudiocohortecssfv.sintomasactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.CSintomasTabFragment;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeccionesSintomasDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.SintomasWS;

/**
 * Created by MiguelLopez
 * Created at 18/10/2015.
 */
public class SeccionesSintomasTask extends UserTask<String, Void, ResultadoObjectWSDTO<SeccionesSintomasDTO>> {

    private final Object mLock = new Object();
    private CSintomasTabFragment cSintomasTabFragment;
    private ProgressDialog PD;

    public SeccionesSintomasTask(CSintomasTabFragment cSintomasTabFragment) {
        this.cSintomasTabFragment = cSintomasTabFragment;
    }

    @Override
    public void onPreExecute() {
        synchronized (mLock) {
            PD = new ProgressDialog(this.cSintomasTabFragment.getActivity());
            PD.setTitle(this.cSintomasTabFragment.getActivity().getResources().getString(R.string.title_obteniendo));
            PD.setMessage(this.cSintomasTabFragment.getActivity().getResources().getString(R.string.msj_espere_por_favor));
            PD.setCancelable(false);
            PD.setIndeterminate(true);
            PD.show();
        }
    }

    public ResultadoObjectWSDTO<SeccionesSintomasDTO> doInBackground(String... params) {
        ResultadoObjectWSDTO<SeccionesSintomasDTO> resultado = new ResultadoObjectWSDTO<>();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.cSintomasTabFragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            SintomasWS sintomasWS = new SintomasWS(this.cSintomasTabFragment.getActivity().getResources());
            HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
            hojaConsulta.setSecHojaConsulta(Integer.valueOf(params[0]));
            resultado = sintomasWS.obtenerSeccionSintomasCompletadas(hojaConsulta);
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.cSintomasTabFragment.getActivity().getResources().getString(R.string.msj_no_tiene_conexion));
        }

        return resultado;
    }

    @Override
    public void onPostExecute(ResultadoObjectWSDTO<SeccionesSintomasDTO> respuesta) {
        synchronized (mLock) {
            if ((PD != null) && PD.isShowing()) {
                PD.dismiss();
            }
        }

        if (respuesta.getCodigoError().intValue() == 0) {
            this.cSintomasTabFragment.marcarSeccionesCompletadas(respuesta.getObjecRespuesta());
        } else if (respuesta.getCodigoError().intValue() != 999) {
            MensajesHelper.mostrarMensajeInfo(this.cSintomasTabFragment.getActivity(),
                    respuesta.getMensajeError(), this.cSintomasTabFragment.getActivity().getResources().getString(
                            R.string.title_estudio_sostenible), null);
        } else {
            MensajesHelper.mostrarMensajeError(this.cSintomasTabFragment.getActivity(),
                    respuesta.getMensajeError(), this.cSintomasTabFragment.getActivity().getResources().getString(
                            R.string.title_estudio_sostenible), null);
        }
    }
}
