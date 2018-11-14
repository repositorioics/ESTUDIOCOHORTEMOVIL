package com.sts_ni.estudiocohortecssfv.expedientesactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.HojaZikaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeguimientoZikaDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.UserTask;
import com.sts_ni.estudiocohortecssfv.ws.SeguimientoZikaWS;

/**
 * Created by ics
 * Created at 17/04/2017.
 */
public class BuscarSeguimientoZikaTask extends UserTask<String, Void, ResultadoObjectWSDTO<HojaZikaDTO>> {

    private final Object mLock = new Object();
    private SeguimientoZikaActivity seguimientoZikaActivity;
    private ProgressDialog PD;
    private String mOpcionBusqueda;
    private String mCodigoBusqueda;

    public BuscarSeguimientoZikaTask(SeguimientoZikaActivity seguimientoZikaActivity) {
        this.seguimientoZikaActivity = seguimientoZikaActivity;
    }

    @Override
    public void onPreExecute() {
        synchronized (mLock) {
            PD = new ProgressDialog(this.seguimientoZikaActivity);
            PD.setTitle(this.seguimientoZikaActivity.getResources().getString(R.string.title_obteniendo));
            PD.setMessage(this.seguimientoZikaActivity.getResources().getString(R.string.msj_espere_por_favor));
            PD.setCancelable(false);
            PD.setIndeterminate(true);
            PD.show();
        }
    }

    public ResultadoObjectWSDTO<HojaZikaDTO> doInBackground(String... params) {
        ResultadoObjectWSDTO<HojaZikaDTO> resultado = new ResultadoObjectWSDTO<>();
        ConnectivityManager cm;
        NetworkInfo netInfo;
        cm = (ConnectivityManager) this.seguimientoZikaActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            synchronized (mLock) {
                mOpcionBusqueda = params[0];
                mCodigoBusqueda = params[1];
            }
            SeguimientoZikaWS zikaWS = new SeguimientoZikaWS(this.seguimientoZikaActivity.getResources());
            if (params[0].contains("0")) {
                resultado = zikaWS.buscarPacienteCrearHoja(params[1]);
            } else if (params[0].contains("1")) {
                resultado = zikaWS.buscarPaciente(params[1]);
                if (resultado.getCodigoError().intValue() == 0) {
                    ResultadoListWSDTO<SeguimientoZikaDTO> seguimientosZika = zikaWS.getListaSeguimientoZika(
                            resultado.getObjecRespuesta().getSecHojaZika());
                    if (seguimientosZika.getCodigoError().intValue() == 0) {
                        resultado.getObjecRespuesta().setLstSeguimientoZika(seguimientosZika.getLstResultado());
                    }
                }
            } else {
                resultado = zikaWS.buscarSeguimientoZika(params[1]);
                if (resultado.getCodigoError().intValue() == 0) {
                    ResultadoListWSDTO<SeguimientoZikaDTO> seguimientosZika = zikaWS.getListaSeguimientoZika(
                            resultado.getObjecRespuesta().getSecHojaZika());
                    if (seguimientosZika.getCodigoError().intValue() == 0) {
                        resultado.getObjecRespuesta().setLstSeguimientoZika(seguimientosZika.getLstResultado());
                    }
                }
            }
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
        if (respuesta.getCodigoError().intValue() == 0) {
            final ResultadoObjectWSDTO<HojaZikaDTO> fRespuesta = respuesta;
            if (mOpcionBusqueda.contains("0")) {
                DialogInterface.OnClickListener crearNuevaHojaDcl = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                new CrearHojaZikaTask(seguimientoZikaActivity).execute(mCodigoBusqueda, fRespuesta.
                                        getObjecRespuesta().getNomPaciente(), fRespuesta.getObjecRespuesta().getEstudioPaciente());
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                MensajesHelper.mostrarMensajeYesNo(this.seguimientoZikaActivity,
                        this.seguimientoZikaActivity.getResources().getString(R.string.msj_crear_nueva_hoja_seguimiento),
                        this.seguimientoZikaActivity.getResources().getString(R.string.title_estudio_sostenible),
                        crearNuevaHojaDcl);
            } else {
                this.seguimientoZikaActivity.cargarDatosZikaUI(respuesta.getObjecRespuesta());
            }
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
