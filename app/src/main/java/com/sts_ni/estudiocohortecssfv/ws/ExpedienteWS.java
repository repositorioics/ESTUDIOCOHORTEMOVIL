package com.sts_ni.estudiocohortecssfv.ws;

import android.content.res.Resources;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.DepartamentosDTO;
import com.sts_ni.estudiocohortecssfv.dto.DiagnosticoDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.EscuelaPacienteDTO;
import com.sts_ni.estudiocohortecssfv.dto.ExpedienteDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.MunicipiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;

import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Clase para realizar el consumo de los metodos del webservice relacionados a Expediente.
 */
public class ExpedienteWS extends EstudioCohorteCssfvWS {

    private Resources RES;
    private int TIME_OUT = 40000;
    private static int TIME_OUT_PDF = 120000;
    public ArrayList<HeaderProperty> HEADER_PROPERTY;
    ArrayList<ExpedienteDTO> lstHojaConsulta;

    public ExpedienteWS(Resources res) {
        this.HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        this.HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
        this.RES = res;
    }

    public ResultadoListWSDTO<ExpedienteDTO> getListaHojaConsulta(int codExpediente) {

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

    // Metodo para obtener los departamentos
    public ResultadoListWSDTO<DepartamentosDTO> getListaDepartamento(){

        ResultadoListWSDTO<DepartamentosDTO> retorno = new ResultadoListWSDTO<>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_DEPARTAMENTOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);

            transporte.call(ACCIOSOAP_METODO_GET_DEPARTAMENTOS, sobre);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    ArrayList<DepartamentosDTO> lstInicio = new ArrayList<DepartamentosDTO>();

                    for (int i = 0; i < inicioArrayJson.length(); i++){
                        JSONObject inicioJson  = inicioArrayJson.getJSONObject(i);
                        DepartamentosDTO inicio = new DepartamentosDTO();

                        inicio.setDivisionpoliticaId(inicioJson.getInt("divisionpoliticaId"));
                        inicio.setNombre(inicioJson.getString("nombre"));
                        inicio.setCodigoNacional(inicioJson.getInt("codigoNacional"));
                        inicio.setCodigoIso(inicioJson.getString("codigoIso"));

                        lstInicio.add(inicio);
                    }

                    /*Gson gson = new GsonBuilder().create();

                    DepartamentosDTO[] arrDepartamentos = gson.fromJson(inicioArrayJson.toString(), DepartamentosDTO[].class);

                    ArrayList<DepartamentosDTO> lstDepartamento = new ArrayList<>(Arrays.asList(arrDepartamentos));*/

                    retorno.setLstResultado(lstInicio);
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

    // Metodo para obtener los municipios que pertenecen al departamento seleccionado
    public ResultadoListWSDTO<MunicipiosDTO> getListaMunicipio(int divisionpoliticaId){

        ResultadoListWSDTO<MunicipiosDTO> retorno = new ResultadoListWSDTO<>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_MUNICIPIOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(divisionpoliticaId);
            paramEviar.setName("divisionpoliticaId");
            paramEviar.setNamespace("");
            paramEviar.setType(Integer.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);

            transporte.call(ACCIOSOAP_METODO_GET_MUNICIPIOS, sobre);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    ArrayList<MunicipiosDTO> lstInicio = new ArrayList<MunicipiosDTO>();

                    for (int i = 0; i < inicioArrayJson.length(); i++){
                        JSONObject inicioJson  = inicioArrayJson.getJSONObject(i);
                        MunicipiosDTO inicio = new MunicipiosDTO();

                        inicio.setDivisionpoliticaId(inicioJson.getInt("divisionpoliticaId"));
                        inicio.setNombre(inicioJson.getString("nombre"));
                        inicio.setCodigoNacional(inicioJson.getInt("codigoNacional"));
                        inicio.setDependencia(inicioJson.getInt("dependencia"));

                        lstInicio.add(inicio);
                    }

                    /*Gson gson = new GsonBuilder().create();

                    DepartamentosDTO[] arrDepartamentos = gson.fromJson(inicioArrayJson.toString(), DepartamentosDTO[].class);

                    ArrayList<DepartamentosDTO> lstDepartamento = new ArrayList<>(Arrays.asList(arrDepartamentos));*/

                    retorno.setLstResultado(lstInicio);
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

    public byte[] getFichaPdf(int secVigilanciaIntegrada){

        byte[] retorno=null;

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_VISUALIZAR_FICHA_PDF);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(secVigilanciaIntegrada);
            paramWS.setName("secVigilanciaIntegrada");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT_PDF);
            transporte.call(ACCIOSOAP_METODO_VISUALIZAR_FICHA_PDF, sobre, this.HEADER_PROPERTY);
            retorno =  Base64.decode(sobre.getResponse().toString(), Base64.DEFAULT);
            String resultado=sobre.getResponse().toString();


        }catch (ConnectException ce){
            ce.printStackTrace();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }

        return retorno;
    }

    public void ImprimirFichaPdf(int secVigilanciaIntegrada){

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_IMPRIMIR_FICHA_PDF);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(secVigilanciaIntegrada);
            paramWS.setName("secVigilanciaIntegrada");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_IMPRIMIR_FICHA_PDF, sobre, this.HEADER_PROPERTY);

        }catch (ConnectException ce){
            ce.printStackTrace();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*Modificacion 6/12/2019 -- SC
    * */
    public byte[] getFichaEpiPdf(int numHojaConsulta){

        byte[] retorno=null;

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_VISUALIZAR_FICHA_EPI_PDF);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(numHojaConsulta);
            paramWS.setName("numHojaConsulta");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT_PDF);
            transporte.call(ACCIOSOAP_METODO_VISUALIZAR_FICHA_EPI_PDF, sobre, this.HEADER_PROPERTY);
            retorno =  Base64.decode(sobre.getResponse().toString(), Base64.DEFAULT);
            String resultado=sobre.getResponse().toString();


        }catch (ConnectException ce){
            ce.printStackTrace();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }

        return retorno;
    }

    public void ImprimirFichaEpiPdf(int numHojaConsulta){

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_IMPRIMIR_FICHA_EPI_PDF);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(numHojaConsulta);
            paramWS.setName("numHojaConsulta");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_IMPRIMIR_FICHA_EPI_PDF, sobre, this.HEADER_PROPERTY);

        }catch (ConnectException ce){
            ce.printStackTrace();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


