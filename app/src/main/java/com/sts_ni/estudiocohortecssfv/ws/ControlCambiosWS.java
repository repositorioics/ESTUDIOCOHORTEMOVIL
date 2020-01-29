package com.sts_ni.estudiocohortecssfv.ws;

import android.app.Application;
import android.content.res.Resources;

import com.sts_ni.estudiocohortecssfv.CssfvApp;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeguimientoInfluenzaDTO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by mmoreno on 08/10/2015.
 */
public class ControlCambiosWS extends EstudioCohorteCssfvWS {
    private int TIME_OUT = 80000;
    public ArrayList<HeaderProperty> HEADER_PROPERTY;
    private Resources RES;

    public ControlCambiosWS(Resources res) {
        this.HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        this.HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
        this.RES = res;
    }

    /**
     * Función para guardar el control de cambios de cada pantalla
     * @param genControlCambios datos generales que agrupan los campos y listado de campos
     * @return objeto de tipo ErrorDTO
     */
    public ErrorDTO guardarControlCambios(GeneralesControlCambiosDTO genControlCambios){

        ErrorDTO retorno = new ErrorDTO();

        if(genControlCambios == null || (genControlCambios != null && genControlCambios.getControlCambios() == null) ||
          (genControlCambios != null && genControlCambios.getControlCambios() != null && genControlCambios.getControlCambios().size() == 0) ){
            retorno.setCodigoError(Long.parseLong("998"));
            retorno.setMensajeError(RES.getString(R.string.msj_validacion_sin_datos));

            return retorno;
        }

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_CONTROL_CAMBIOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            // creando el json array para el control de cambios
            JSONArray jsonArray = new JSONArray();

            for (ControlCambiosDTO ctrCambios : genControlCambios.getControlCambios()) {
                JSONObject objenvioCtrCambios = new JSONObject();

                objenvioCtrCambios.put("codExpediente", String.valueOf(genControlCambios.getCodExpediente()));
                objenvioCtrCambios.put("fecha", new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()).toString());
                objenvioCtrCambios.put("numHojaConsulta", String.valueOf(genControlCambios.getNumHojaConsulta()));
                objenvioCtrCambios.put("controlador", genControlCambios.getControlador());
                objenvioCtrCambios.put("nombreCampo", ctrCambios.getNombreCampo().replace(":",""));
                objenvioCtrCambios.put("valorCampo", ctrCambios.getValorCampo());
                objenvioCtrCambios.put("usuario", genControlCambios.getUsuario());
                objenvioCtrCambios.put("tipoControl", ctrCambios.getTipoControl());

                jsonArray.put(objenvioCtrCambios.toString());
            }

            String paramJsonArray = jsonArray.toString();

            //Seteando parametros
            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(paramJsonArray);
            paramEviar.setName("paramControlCambios");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);
            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GUARDAR_CONTROL_CAMBIOS, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if (resultado != null && !resultado.isEmpty()) {

                JSONObject jObj = new JSONObject(resultado);

                JSONObject mensaje = (JSONObject) jObj.get("mensaje");

                retorno.setCodigoError((long) mensaje.getInt("codigo"));
                retorno.setMensajeError(mensaje.getString("texto"));

            } else {
                retorno.setCodigoError(Long.parseLong("1"));
                retorno.setMensajeError(RES.getString(R.string.msj_servicio_no_envia_respuesta));
            }

        }catch (ConnectException ce){
            ce.printStackTrace();
            retorno.setCodigoError(Long.parseLong("2"));
            retorno.setMensajeError(RES.getString(R.string.msj_servicio_no_dispon));
        }catch (SocketTimeoutException et) {
            et.printStackTrace();
            retorno.setCodigoError(Long.parseLong("2"));
            retorno.setMensajeError(RES.getString(R.string.msj_tiempo_espera_servicio_agotado));
        }catch (Exception e) {
            e.printStackTrace();
            retorno.setCodigoError(Long.parseLong("999"));
            retorno.setMensajeError(RES.getString(R.string.msj_error_no_controlado)+ " " + e.getMessage());
        }

        return retorno;
    }
}
