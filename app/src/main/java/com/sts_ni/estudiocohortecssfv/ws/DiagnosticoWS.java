package com.sts_ni.estudiocohortecssfv.ws;

import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.DiagnosticoDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.EscuelaPacienteDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeccionesDiagnosticoDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeccionesSintomasDTO;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Clase para realizar el consumo de los metodos del webservice relacionados al TAB Diagnostico.
 */
public class DiagnosticoWS extends EstudioCohorteCssfvWS {

    private Resources RES;
    private int TIME_OUT = 40000;

    public ArrayList<HeaderProperty> HEADER_PROPERTY;

    public DiagnosticoWS(Resources res) {

        this.HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        this.HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));

        this.TIME_OUT = 40000;
        this.RES = res;
    }

    //Funcion para guardar Examen Historia
    public ErrorDTO guardarExamenHistoria(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDA_EXAMENHISTORIA_HOJACONSULTA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

         JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("historiaExamenFisico", hojaConsulta.getHistoriaExamenFisico());
            objenvio.put("usuarioLogiado", usuarioLogiado);

            //Seteando parametros
            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GUARDA_EXAMENHISTORIA_HOJACONSULTA, sobre);
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

    //Funcion para guardar Examen Historia
    public ErrorDTO guardarPlanes(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDA_PLANES_HOJACONSULTA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("planes", hojaConsulta.getPlanes());
            objenvio.put("usuarioLogiado", usuarioLogiado);

            //Seteando parametros
            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GUARDA_PLANES_HOJACONSULTA, sobre);
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


    //Funcion para guardar Tratamiento
    public ErrorDTO guardarTratamiento(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDA_TRATAMIENTO_HOJACONSULTA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();

            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("acetaminofen", hojaConsulta.getAcetaminofen());
            objenvio.put("asa", hojaConsulta.getAsa());
            objenvio.put("ibuprofen", hojaConsulta.getIbuprofen());
            objenvio.put("penicilina", hojaConsulta.getPenicilina());
            objenvio.put("amoxicilina", hojaConsulta.getAmoxicilina());
            objenvio.put("dicloxacilina", hojaConsulta.getDicloxacilina());
            objenvio.put("otro", hojaConsulta.getOtro());
            objenvio.put("otroAntibiotico", hojaConsulta.getOtroAntibiotico());
            objenvio.put("furazolidona", hojaConsulta.getFurazolidona());
            objenvio.put("metronidazolTinidazol", hojaConsulta.getMetronidazolTinidazol());
            objenvio.put("albendazolMebendazol", hojaConsulta.getAlbendazolMebendazol());
            objenvio.put("sulfatoFerroso", hojaConsulta.getSulfatoFerroso());
            objenvio.put("sueroOral", hojaConsulta.getSueroOral());
            objenvio.put("sulfatoZinc", hojaConsulta.getSulfatoZinc());
            objenvio.put("liquidosIv", hojaConsulta.getLiquidosIv());
            objenvio.put("prednisona", hojaConsulta.getPrednisona());
            objenvio.put("hidrocortisonaIv", hojaConsulta.getHidrocortisonaIv());
            objenvio.put("salbutamol", hojaConsulta.getSalbutamol());
            objenvio.put("oseltamivir", hojaConsulta.getOseltamivir());
            objenvio.put("planes", hojaConsulta.getPlanes());

            objenvio.put("usuarioLogiado", usuarioLogiado);
            //Seteando parametros
            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GUARDA_TRATAMIENTO_HOJACONSULTA, sobre);
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

    //Funcion para guardar Proxima Cita
    public ErrorDTO guardarProximaCita(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDA_PROXIMACITA_HOJACONSULTA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            if(hojaConsulta.getProximaCita() != null) {
                objenvio.put("proximaCita", hojaConsulta.getProximaCita());
            }
            if(hojaConsulta.getTelef() != null) {
                objenvio.put("telef", hojaConsulta.getTelef());
            }
            if(hojaConsulta.getColegio() != null) {
                objenvio.put("colegio", hojaConsulta.getColegio());
            }
            if(hojaConsulta.getHorarioClases() != null) {
                objenvio.put("horarioClases", hojaConsulta.getHorarioClases());
            }
            objenvio.put("usuarioLogiado", usuarioLogiado);

            //Seteando parametros
            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GUARDA_PROXIMACITA__HOJACONSULTA, sobre);
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

    //Funcion para guardar Proxima Cita
    public ErrorDTO guardarDiagnostico(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDARDIAGNOSTICO_DIAGNOSTICO);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();

            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("diagnostico1", hojaConsulta.getDiagnostico1());
            objenvio.put("diagnostico2", hojaConsulta.getDiagnostico2());
            objenvio.put("diagnostico3", hojaConsulta.getDiagnostico3());
            objenvio.put("diagnostico4", hojaConsulta.getDiagnostico4());
            objenvio.put("otroDiagnostico", hojaConsulta.getOtroDiagnostico());
            objenvio.put("usuarioLogiado", usuarioLogiado);

            //Seteando parametros
            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GUARDARDIAGNOSTICO_DIAGNOSTICO, sobre);
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


    public ResultadoListWSDTO<DiagnosticoDTO> getListaDiagnostico(){

        ResultadoListWSDTO<DiagnosticoDTO> retorno = new ResultadoListWSDTO<DiagnosticoDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_LISTADIAGNOSTICO);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);

            transporte.call(ACCIOSOAP_LISTADIAGNOSTICO, sobre);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    ArrayList<DiagnosticoDTO> lstInicio = new ArrayList<DiagnosticoDTO>();

                    for (int i = 0; i < inicioArrayJson.length(); i++){
                        JSONObject inicioJson  = inicioArrayJson.getJSONObject(i);
                        DiagnosticoDTO inicio = new DiagnosticoDTO();

                        inicio.setSecDiagnostico(inicioJson.getInt("secDiagnostico"));
                        inicio.setCodigoDignostico(inicioJson.getString("codigoDiagnostico"));
                        inicio.setDiagnostico(inicioJson.getString("diagnostico"));


                        lstInicio.add(inicio);

                    }

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


    public ResultadoObjectWSDTO<EscuelaPacienteDTO> obtenerDatosPaciente(Integer codExpediente){

        ResultadoObjectWSDTO<EscuelaPacienteDTO> retorno = new ResultadoObjectWSDTO<>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_OBTENERDATOSPACIENTES);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(codExpediente);
            paramWS.setName("paramCodExpediente");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_OBTENERDATOSPACIENTES, sobre, HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    retorno.setObjecRespuesta(new EscuelaPacienteDTO());
                    retorno.getObjecRespuesta().setSecPaciente(((JSONObject)resultadoJson.get(0)).getInt("secPaciente"));
                    retorno.getObjecRespuesta().setCodEscuela(((JSONObject)resultadoJson.get(0)).getInt("codEscuela"));
                    retorno.getObjecRespuesta().setDescripcion(((JSONObject)resultadoJson.get(0)).getString("descripcion"));

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
            retorno.setMensajeError(RES.getString(R.string.msj_error_no_controlado).concat(e.getMessage()));
        }

        return retorno;
    }

    public ResultadoListWSDTO<EscuelaPacienteDTO> getListaEscuela(){

        ResultadoListWSDTO<EscuelaPacienteDTO> retorno = new ResultadoListWSDTO<>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_LISTA_ESCUELA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);

            transporte.call(ACCIOSOAP_LISTA_ESCUELA, sobre);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    EscuelaPacienteDTO[] arrEscuelas = gson.fromJson(inicioArrayJson.toString(), EscuelaPacienteDTO[].class);

                    ArrayList<EscuelaPacienteDTO> lstEscuelas = new ArrayList<>(Arrays.asList(arrEscuelas));

                    retorno.setLstResultado(lstEscuelas);
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

    public ResultadoObjectWSDTO<SeccionesDiagnosticoDTO> obtenerSeccionDiagnosticoCompletadas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<SeccionesDiagnosticoDTO> retorno = new ResultadoObjectWSDTO<>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_SECCIONES_DIAGNOSTICO_COMPLETADAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());

            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_GET_SECCIONES_DIAGNOSTICO_COMPLETADAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    SeccionesDiagnosticoDTO seccionesDiagnostico = gson.fromJson(resultadoJson.get(0).toString(),
                            SeccionesDiagnosticoDTO.class);

                    retorno.setObjecRespuesta(seccionesDiagnostico);
                    retorno.setCodigoError(Long.parseLong("0"));
                    retorno.setMensajeError("");

                } else {
                    retorno.setCodigoError((long) mensaje.getInt("codigo"));
                    retorno.setMensajeError(mensaje.getString("texto"));
                }
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
