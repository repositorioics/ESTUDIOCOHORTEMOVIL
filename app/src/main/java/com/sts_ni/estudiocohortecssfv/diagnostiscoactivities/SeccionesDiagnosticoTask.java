package com.sts_ni.estudiocohortecssfv.diagnostiscoactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.CDiagnosticoTabFragment;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeccionesDiagnosticoDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.DiagnosticoWS;

/**
 * Created by MiguelLopez on 18/10/2015.
 */
public class SeccionesDiagnosticoTask extends UserTask<String, Void, ResultadoObjectWSDTO<SeccionesDiagnosticoDTO>> {

    private final Object mLock = new Object();
    private CDiagnosticoTabFragment cDiagnosticoTabFragment;
    private ProgressDialog PD;

    public SeccionesDiagnosticoTask(CDiagnosticoTabFragment cDiagnosticoTabFragment) {
        this.cDiagnosticoTabFragment = cDiagnosticoTabFragment;
    }

    @Override
    public void onPreExecute() {
        synchronized (mLock) {
            PD = new ProgressDialog(this.cDiagnosticoTabFragment.getActivity());
            PD.setTitle(this.cDiagnosticoTabFragment.getActivity().getResources().getString(R.string.title_obteniendo));
            PD.setMessage(this.cDiagnosticoTabFragment.getActivity().getResources().getString(R.string.msj_espere_por_favor));
            PD.setCancelable(false);
            PD.setIndeterminate(true);
            PD.show();
        }
    }

    public ResultadoObjectWSDTO<SeccionesDiagnosticoDTO> doInBackground(String... params) {
        ResultadoObjectWSDTO<SeccionesDiagnosticoDTO> resultado = new ResultadoObjectWSDTO<>();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.cDiagnosticoTabFragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            DiagnosticoWS diagnosticoWS = new DiagnosticoWS(this.cDiagnosticoTabFragment.getActivity().getResources());
            HojaConsultaDTO hojaConsulta = new HojaConsultaDTO();
            hojaConsulta.setSecHojaConsulta(Integer.valueOf(params[0]));
            resultado = diagnosticoWS.obtenerSeccionDiagnosticoCompletadas(hojaConsulta);
        } else {
            resultado.setCodigoError(Long.parseLong("3"));
            resultado.setMensajeError(this.cDiagnosticoTabFragment.getActivity().getResources().getString(R.string.msj_no_tiene_conexion));
        }

        return resultado;
    }

    @Override
    public void onPostExecute(ResultadoObjectWSDTO<SeccionesDiagnosticoDTO> respuesta) {
        if ((PD != null) && PD.isShowing()) {
            PD.dismiss();
        }
        if (respuesta.getCodigoError().intValue() == 0) {
            this.cDiagnosticoTabFragment.marcarSeccionesCompletadas(respuesta.getObjecRespuesta());
        } else if (respuesta.getCodigoError().intValue() != 999) {
            MensajesHelper.mostrarMensajeInfo(this.cDiagnosticoTabFragment.getActivity(),
                    respuesta.getMensajeError(), this.cDiagnosticoTabFragment.getActivity().getResources().getString(
                            R.string.title_estudio_sostenible), null);
        } else {
            MensajesHelper.mostrarMensajeError(this.cDiagnosticoTabFragment.getActivity(),
                    respuesta.getMensajeError(), this.cDiagnosticoTabFragment.getActivity().getResources().getString(
                            R.string.title_estudio_sostenible), null);
        }
    }
}
