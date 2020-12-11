package com.sts_ni.estudiocohortecssfv.ws;

import android.content.res.Resources;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeguimientoInfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;

import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.Calendar;
import java.util.List;


/**
 * Clase para gestionar el cosumo del webservice a los metodos relacionados a Siguimiento Influenza.
 */
public class SeguimientoInfluenzaWS extends EstudioCohorteCssfvWS {
    private int TIME_OUT = 80000;
    private static int TIME_OUT_PDF = 180000;
    public ArrayList<HeaderProperty> HEADER_PROPERTY;
    private Resources RES;

    public SeguimientoInfluenzaWS(Resources res) {
        this.HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        this.HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
        this.RES = res;
    }

    public ResultadoObjectWSDTO<HojaInfluenzaDTO> buscarPacientes(String codigoExpediente){

        ResultadoObjectWSDTO<HojaInfluenzaDTO> retorno = new ResultadoObjectWSDTO<HojaInfluenzaDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, BUSCAR_PACIENTE_SEGUIMIENTO_INFLUENZA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(codigoExpediente);
            paramEviar.setName("paramCodExpediente");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_BUSCAR_PACIENTE_SEGUIMIENTO_INFLUENZA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

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

    public ResultadoObjectWSDTO<HojaInfluenzaDTO> buscarPacienteCrearHoja(String codExpediente){

        ResultadoObjectWSDTO<HojaInfluenzaDTO> retorno = new ResultadoObjectWSDTO<>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, BUSCAR_PACIENTE_CREAR_SEGUIMIENTO_INFLUENZA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(codExpediente);
            paramEviar.setName("paramCodExpediente");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_BUSCAR_PACIENTE_CREAR_SEGUIMIENTO_INFLUENZA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    HojaInfluenzaDTO hojaInfluenza = gson.fromJson(resultadoJson.get(0).toString(), HojaInfluenzaDTO.class);

                    retorno.setObjecRespuesta(hojaInfluenza);

                    retorno.setObjecRespuesta(hojaInfluenza);

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

    //Funcion de busqueda de paciente con hoja de seguimiento influenza por el codigo expediente
    public ResultadoObjectWSDTO<HojaInfluenzaDTO> buscarPaciente(String codExpediente){

        ResultadoObjectWSDTO<HojaInfluenzaDTO> retorno = new ResultadoObjectWSDTO<>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, BUSCAR_PACIENTE_SEGUIMIENTO_INFLUENZA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(codExpediente);
            paramEviar.setName("paramCodExpediente");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_BUSCAR_PACIENTE_SEGUIMIENTO_INFLUENZA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    HojaInfluenzaDTO hojaInfluenza = gson.fromJson(resultadoJson.get(0).toString(), HojaInfluenzaDTO.class);

                    retorno.setObjecRespuesta(hojaInfluenza);

                    /*paciente.setNomPaciente(((JSONObject)resultadoJson.get(0)).getString("nombrePaciente"));
                    paciente.setCodExpediente(((JSONObject)resultadoJson.get(0)).getInt("codExpediente"));

                    if (((JSONObject)resultadoJson.get(0)).getInt("Existe") == 1 ){
                        paciente.setSecHojaInfluenza(((JSONObject) resultadoJson.get(0)).getInt("secHojaInfluenza"));
                        paciente.setNumHojaSeguimiento(((JSONObject) resultadoJson.get(0)).getInt("numHojaSeguimiento"));
                        paciente.setFif(((JSONObject) resultadoJson.get(0)).getString("fif"));
                        paciente.setFis(((JSONObject) resultadoJson.get(0)).getString("fis"));
                        String fechaInicio = ((JSONObject) resultadoJson.get(0)).getString("fechaInicio");
                        paciente.setCerrado(((JSONObject) resultadoJson.get(0)).getString("cerrado").charAt(0));

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        Calendar cal = new GregorianCalendar();
                        cal.setTime(sdf.parse(fechaInicio));
                        paciente.setFechaInicio(cal);

                        if (((JSONObject) resultadoJson.get(0)).has("fechaCierre")) {
                            SimpleDateFormat sdfcierre = new SimpleDateFormat("yyyyMMdd");
                            Calendar calcierre = new GregorianCalendar();
                            String fechaCierre = ((JSONObject) resultadoJson.get(0)).getString("fechaCierre");
                            calcierre.setTime(sdfcierre.parse(fechaCierre));
                            paciente.setFechaCierre(calcierre);
                        }
                    }*/
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

    //Funcion de busqueda de seguimiento de influenza por numero de seguimiento
    public ResultadoObjectWSDTO<HojaInfluenzaDTO> buscarSeguimientoInfluenza(String numSeguimiento){

        ResultadoObjectWSDTO<HojaInfluenzaDTO> retorno = new ResultadoObjectWSDTO<>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_BUSCAR_HOJA_SEGUIMIENTO_INFLUENA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(numSeguimiento);
            paramEviar.setName("paramNumHojaSeguimiento");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_BUSCAR_HOJA_SEGUIMIENTO_INFLUENA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                   JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    HojaInfluenzaDTO hojaInfluenza = gson.fromJson(resultadoJson.get(0).toString(), HojaInfluenzaDTO.class);

                    retorno.setObjecRespuesta(hojaInfluenza);

                    /*hojaInfluenza.setCodExpediente(((JSONObject)resultadoJson.get(0)).getInt("codExpediente"));
                    hojaInfluenza.setNomPaciente(((JSONObject)resultadoJson.get(0)).getString("nombrePaciente"));
                    hojaInfluenza.setSecHojaInfluenza(((JSONObject) resultadoJson.get(0)).getInt("secHojaInfluenza"));
                    hojaInfluenza.setNumHojaSeguimiento(((JSONObject)resultadoJson.get(0)).getInt("numHojaSeguimiento"));
                    hojaInfluenza.setFif(((JSONObject)resultadoJson.get(0)).getString("fif"));
                    hojaInfluenza.setFis(((JSONObject) resultadoJson.get(0)).getString("fis"));
                    String fechaInicio = ((JSONObject) resultadoJson.get(0)).getString("fechaInicio");
                    hojaInfluenza.setCerrado(((JSONObject) resultadoJson.get(0)).getString("cerrado").charAt(0));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    Calendar cal = new GregorianCalendar();
                    cal.setTime(sdf.parse(fechaInicio));
                    hojaInfluenza.setFechaInicio(cal);

                    if (((JSONObject) resultadoJson.get(0)).has("fechaCierre")) {
                        SimpleDateFormat sdfcierre = new SimpleDateFormat("yyyyMMdd");
                        Calendar calcierre = new GregorianCalendar();
                        String fechaCierre = ((JSONObject) resultadoJson.get(0)).getString("fechaCierre");
                        calcierre.setTime(sdfcierre.parse(fechaCierre));
                        hojaInfluenza.setFechaCierre(calcierre);
                    }*/

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

    //Funcion para guardar los datos desde la pantalla Emergencia
    public ResultadoObjectWSDTO<HojaInfluenzaDTO> crearHojaSeguimiento(HojaInfluenzaDTO hojaInfluenza){

        ResultadoObjectWSDTO<HojaInfluenzaDTO> retorno = new ResultadoObjectWSDTO<>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_CREAR_HOJA_SEGUIMIENTO_INFLUENA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            Gson gson = new GsonBuilder().create();
            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(gson.toJson(hojaInfluenza));
            paramEviar.setName("paramCrearHoja");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_CREAR_HOJA_SEGUIMIENTO_INFLUENA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if (resultado != null && !resultado.isEmpty()) {

                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    HojaInfluenzaDTO hojaInfluenzaResultado = gson.fromJson(resultadoJson.get(0).toString(), HojaInfluenzaDTO.class);

                    retorno.setObjecRespuesta(hojaInfluenzaResultado);

                    retorno.setCodigoError(Long.parseLong("0"));
                    retorno.setMensajeError("");
                } else {
                    retorno.setCodigoError((long) mensaje.getInt("codigo"));
                    retorno.setMensajeError(mensaje.getString("texto"));
                }

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

    //Funcion para guardar los datos desde la pantalla Emergencia
    public ErrorDTO guardarHojaSeguimiento(HojaInfluenzaDTO hojaInfluenza, List<SeguimientoInfluenzaDTO> seguimiento, String user){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_HOJA_SEGUIMIENTO_INFLUENA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;


            JSONObject objenvio = new JSONObject();
            objenvio.put("numHojaSeguimiento", hojaInfluenza.getNumHojaSeguimiento());
            objenvio.put("codExpediente", hojaInfluenza.getCodExpediente());
            objenvio.put("fif", hojaInfluenza.getFif());
            objenvio.put("fis", hojaInfluenza.getFis());
            objenvio.put("secHojaConsulta", hojaInfluenza.getSecHojaConsulta());
            if (hojaInfluenza.getFechaCierre() != null)
                objenvio.put("fechaCierre",new SimpleDateFormat("dd/MM/yyyy").format(hojaInfluenza.getFechaCierre().getTime()).toString());
            objenvio.put("cerrado",String.valueOf(hojaInfluenza.getCerrado()));


            String paramJsonArray="";
            if (seguimiento != null) {
                // creando el json array para el seguimiento influenza
                JSONArray jsonArray = new JSONArray();

                for (SeguimientoInfluenzaDTO seg : seguimiento) {
                    JSONObject objenvioSeg = new JSONObject();

                    objenvioSeg.put("controlDia", String.valueOf(seg.getControlDia()));
                    objenvioSeg.put("fechaSeguimiento", seg.getFechaSeguimiento());
                    objenvioSeg.put("usuarioMedico", seg.getUsuarioMedico());
                    objenvioSeg.put("consultaInicial", String.valueOf(seg.getConsultaInicial()));
                    objenvioSeg.put("fiebre", String.valueOf(seg.getFiebre()));
                    objenvioSeg.put("tos", String.valueOf(seg.getTos()));
                    objenvioSeg.put("secrecionNasal", String.valueOf(seg.getSecrecionNasal()));
                    objenvioSeg.put("dolorGarganta", String.valueOf(seg.getDolorGarganta()));
                    objenvioSeg.put("congestionNasa", String.valueOf(seg.getCongestionNasa()));
                    objenvioSeg.put("dolorCabeza", String.valueOf(seg.getDolorCabeza()));
                    objenvioSeg.put("faltaApetito", String.valueOf(seg.getFaltaApetito()));
                    objenvioSeg.put("dolorMuscular", String.valueOf(seg.getDolorMuscular()));
                    objenvioSeg.put("dolorArticular", String.valueOf(seg.getDolorArticular()));
                    objenvioSeg.put("dolorOido", String.valueOf(seg.getDolorOido()));
                    objenvioSeg.put("respiracionRapida", String.valueOf(seg.getRespiracionRapida()));
                    objenvioSeg.put("dificultadRespirar", String.valueOf(seg.getDificultadRespirar()));
                    objenvioSeg.put("faltaEscuela", seg.getFaltaEscuela());
                    objenvioSeg.put("quedoEnCama", seg.getQuedoEnCama());
                    /*NUEVOS CAMPOS AGREGADOS*/
                    objenvioSeg.put("fiebreLeve", seg.getFiebreLeve());
                    objenvioSeg.put("fiebreModerada", seg.getFiebreModerada());
                    objenvioSeg.put("fiebreSevera", seg.getFiebreSevera());
                    objenvioSeg.put("tosLeve", seg.getTosLeve());
                    objenvioSeg.put("tosModerada", seg.getTosModerada());
                    objenvioSeg.put("tosSevera", seg.getTosSevera());
                    objenvioSeg.put("secrecionNasalLeve", seg.getSecrecionNasalLeve());
                    objenvioSeg.put("secrecionNasalModerada", seg.getSecrecionNasalModerada());
                    objenvioSeg.put("secrecionNasalSevera", seg.getSecrecionNasalSevera());
                    objenvioSeg.put("dolorGargantaLeve", seg.getDolorGargantaLeve());
                    objenvioSeg.put("dolorGargantaModerada", seg.getDolorGargantaModerada());
                    objenvioSeg.put("dolorGargantaSevera", seg.getDolorGargantaSevera());
                    objenvioSeg.put("dolorCabezaLeve", seg.getDolorCabezaLeve());
                    objenvioSeg.put("dolorCabezaModerada", seg.getDolorCabezaModerada());
                    objenvioSeg.put("dolorCabezaSevera", seg.getDolorCabezaSevera());
                    objenvioSeg.put("dolorMuscularLeve", seg.getDolorMuscularLeve());
                    objenvioSeg.put("dolorMuscularModerada", seg.getDolorMuscularModerada());
                    objenvioSeg.put("dolorMuscularSevera", seg.getDolorMuscularSevera());
                    objenvioSeg.put("dolorArticularLeve", seg.getDolorArticularLeve());
                    objenvioSeg.put("dolorArticularModerada", seg.getDolorArticularModerada());
                    objenvioSeg.put("dolorArticularSevera", seg.getDolorArticularSevera());
                    objenvioSeg.put("secHojaInfluenza", seg.getSecHojaInfluenza());
                    /*NUEVOS CAMPOS AGREGADOS*/
                    objenvioSeg.put("cuadroConfusional", seg.getCuadroConfusional());
                    objenvioSeg.put("cuadroNeurologico", seg.getCuadroNeurologico());
                    objenvioSeg.put("confusionMental", seg.getConfusionMental());
                    objenvioSeg.put("anosmia", seg.getAnosmia());
                    objenvioSeg.put("ageusia", seg.getAgeusia());
                    objenvioSeg.put("mareo", seg.getMareo());
                    objenvioSeg.put("ictus", seg.getIctus());
                    objenvioSeg.put("sincope", seg.getSincope());

                    jsonArray.put(objenvioSeg.toString());
                }

                paramJsonArray=jsonArray.toString();
            }
            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaInfluenza");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            PropertyInfo paramEviar2 = new PropertyInfo();
            paramEviar2.setValue(paramJsonArray);
            paramEviar2.setName("paramSeguimientoInfluenza");
            paramEviar2.setNamespace("");
            paramEviar2.setType(String.class);

            request.addProperty(paramEviar2);

            /* Nuevo parametro a enviar, contiene el usuario logeado
            * Fecha: 24/04/2020 */

            PropertyInfo paramEviar3 = new PropertyInfo();
            paramEviar3.setValue(user);
            paramEviar3.setName("user");
            paramEviar3.setNamespace("");
            paramEviar3.setType(String.class);

            request.addProperty(paramEviar3);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_GUARDAR_HOJA_SEGUIMIENTO_INFLUENA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if (resultado != null && !resultado.isEmpty()) {

                JSONObject jObj = new JSONObject(resultado);

                JSONObject mensaje = (JSONObject) jObj.get("mensaje");

                retorno.setCodigoError((long) mensaje.getInt("codigo"));
                retorno.setMensajeError(mensaje.getString("texto"));

                if(mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObj.get("resultado");
                    hojaInfluenza.setNumHojaSeguimiento(((JSONObject) resultadoJson.get(0)).getInt("numHojaSeguimiento"));

                    retorno.setCodigoError(Long.parseLong("0"));
                    retorno.setMensajeError("");
                } else {
                    retorno.setCodigoError((long) mensaje.getInt("codigo"));
                    retorno.setMensajeError(mensaje.getString("texto"));
                }

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


    public ResultadoListWSDTO<SeguimientoInfluenzaDTO> getListaSeguimientoInfluenza(int numHojaSeguimiento){

        ResultadoListWSDTO<SeguimientoInfluenzaDTO> retorno = new ResultadoListWSDTO<SeguimientoInfluenzaDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_LISTA_SEGUIMIENTO_INFLUENA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(numHojaSeguimiento);
            paramWS.setName("paramSecHojaInfluenza");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_GET_LISTA_SEGUIMIENTO_INFLUENA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    SeguimientoInfluenzaDTO[] arrSeguimientosInfluenza = gson.fromJson(inicioArrayJson.toString(), SeguimientoInfluenzaDTO[].class);

                    ArrayList<SeguimientoInfluenzaDTO> lstSeguimientoInfluenza = new ArrayList<>(Arrays.asList(arrSeguimientosInfluenza));

                   /* for (int i = 0; i < inicioArrayJson.length(); i++){
                        JSONObject inicioJson  = inicioArrayJson.getJSONObject(i);
                        SeguimientoInfluenzaDTO seg = new SeguimientoInfluenzaDTO();

                        String fechaSeguimiento =  inicioJson.getString("fechaSeguimiento");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Calendar cal = new GregorianCalendar();
                        cal.setTime(sdf.parse(fechaSeguimiento));
                        seg.setFechaSeguimiento(cal);
                        seg.setControlDia(inicioJson.getString("controlDia").charAt(0));
                        seg.setNombreMedico(inicioJson.getString("nombreMedico"));
                        seg.setConsultaInicial(inicioJson.getString("consultaInicial").charAt(0));
                        seg.setFiebre(inicioJson.getString("fiebre").charAt(0));
                        seg.setTos(inicioJson.getString("tos").charAt(0));
                        seg.setSecrecionNasal(inicioJson.getString("secrecionNasal").charAt(0));
                        seg.setDolorGarganta(inicioJson.getString("dolorGarganta").charAt(0));
                        seg.setCongestionNasa(inicioJson.getString("congestionNasa").charAt(0));
                        seg.setDolorCabeza(inicioJson.getString("dolorCabeza").charAt(0));
                        seg.setFaltaApetito(inicioJson.getString("faltaApetito").charAt(0));
                        seg.setDolorMuscular(inicioJson.getString("dolorMuscular").charAt(0));
                        seg.setDolorArticular(inicioJson.getString("dolorArticular").charAt(0));
                        seg.setDolorOido(inicioJson.getString("dolorOido").charAt(0));
                        seg.setRespiracionRapida(inicioJson.getString("respiracionRapida").charAt(0));
                        seg.setDificultadRespirar(inicioJson.getString("dificultadRespirar").charAt(0));
                        seg.setFaltaEscuela(inicioJson.getString("faltaEscuela").toString());
                        seg.setQuedoEnCama(inicioJson.getString("quedoEnCama").toString());
                        lstSeguimientoInfluenza.add(seg);

                    }*/

                    retorno.setLstResultado(lstSeguimientoInfluenza);
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


    public ErrorDTO ImprimirHojaSeguimientoPdf(int numHojaSeguimiento, int consultorio){

        ErrorDTO retorno = new ErrorDTO();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_IMPRIMIROJASEGUIMIENTO_PDF);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(numHojaSeguimiento);
            paramWS.setName("paramNumHojaSeguimiento");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);

            PropertyInfo paramImpresora = new PropertyInfo();
            paramImpresora.setValue(consultorio);
            paramImpresora.setName("paramImpresora");
            paramImpresora.setNamespace("");
            paramImpresora.setType(Integer.class);

            request.addProperty(paramWS);
            request.addProperty(paramImpresora);
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_IMPRIMIRHOJASEGUIMIENTO_PDF, sobre, this.HEADER_PROPERTY);

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

    public byte[] getSeguimientoInfluenzaPdf(int numHojaSeguimiento){

        byte[] retorno=null;

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_VISUALIZARHOJASEGUIMIENTO_PDF);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(numHojaSeguimiento);
            paramWS.setName("paramNumHojaSeguimiento");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT_PDF);
            transporte.call(ACCIOSOAP_VISUALIZARHOJASEGUIMIENTO_PDF, sobre, this.HEADER_PROPERTY);
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

}
