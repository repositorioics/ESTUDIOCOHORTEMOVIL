package com.sts_ni.estudiocohortecssfv.ws;

import android.content.res.Resources;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.ExpedienteDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;

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

/**
 * Clase para realizar el consumo de los metodos del webservice relacionados a Expediente.
 */
public class ExpedienteWS extends EstudioCohorteCssfvWS {

    private Resources RES;
    private int TIME_OUT = 40000;
    public ArrayList<HeaderProperty> HEADER_PROPERTY;
    ArrayList<ExpedienteDTO> lstHojaConsulta;

    public ExpedienteWS(Resources res) {
        this.HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        this.HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
        this.RES = res;
    }

    public ResultadoListWSDTO<ExpedienteDTO> getListaHojaConsulta(int codExpediente){

        ResultadoListWSDTO<ExpedienteDTO> retorno = new ResultadoListWSDTO<ExpedienteDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_HOJA_CONSULTA_EXP);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(codExpediente);
            paramWS.setName("paramCodExpediente");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_HOJA_CONSULTA_EXP, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    lstHojaConsulta = new ArrayList<ExpedienteDTO>();

                    for (int i = 0; i < inicioArrayJson.length(); i++){
                        JSONObject inicioJson  = inicioArrayJson.getJSONObject(i);
                        ExpedienteDTO exp = new ExpedienteDTO();

                        exp.setNumHojaConsulta(inicioJson.getInt("numHojaConsulta"));
                        exp.setEstado(inicioJson.getString("estado"));
                        exp.setNomMedico(inicioJson.getString("medicoCierre"));
                        exp.setFechaConsulta(inicioJson.getString("fechaCierre"));
                        exp.setHoraConsulta(inicioJson.getString("horaCierre"));
                        exp.setSecHojaConsulta(inicioJson.getInt("secHojaConsulta"));
                        lstHojaConsulta.add(exp);

                    }

                    retorno.setLstResultado(lstHojaConsulta);
                    retorno.setCodigoError(Long.parseLong("0"));
                    retorno.setMensajeError("");

                }else {
                    retorno.setCodigoError((long)mensaje.getInt("codigo"));
                    retorno.setMensajeError(mensaje.getString("texto"));
                }

            }else {
                retorno.setCodigoError(Long.parseLong("1"));
                retorno.setMensajeError(RES.getString(R.string.msj_servicio_no_envia_respuesta));
            }

        }catch (ConnectException ce){
            ce.printStackTrace();
            retorno.setCodigoError(Long.parseLong("2"));
            retorno.setMensajeError(RES.getString(R.string.msj_servicio_no_dispon));
        }catch (SocketTimeoutException et){
            et.printStackTrace();
            retorno.setCodigoError(Long.parseLong("2"));
            retorno.setMensajeError(RES.getString(R.string.msj_servicio_no_dispon));
        } catch (Exception e) {
            e.printStackTrace();
            retorno.setCodigoError(Long.parseLong("999"));
            retorno.setMensajeError(RES.getString(R.string.msj_error_no_controlado));
    }

        return retorno;
    }

    /*Metodo para reimprimir la hoja de consulta*/
    public ErrorDTO ejecutarProcesoReimpresion(Integer secHojaConsulta){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_REIMPRESION_HOJA_CONSULTA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

         /*   JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());*/

            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(secHojaConsulta);
            paramEviar.setName("paramsecHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(Integer.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_REIMPRESION_HOJA_CONSULTA, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if (resultado != null && !resultado.isEmpty()) {

                JSONObject jObj = new JSONObject(resultado);

                JSONObject mensaje = (JSONObject) jObj.get("mensaje");

                retorno.setCodigoError((long) mensaje.getInt("codigo"));
                retorno.setMensajeError(mensaje.getString("texto"));

            } else {
                retorno.setCodigoError(Long.parseLong("3"));
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


