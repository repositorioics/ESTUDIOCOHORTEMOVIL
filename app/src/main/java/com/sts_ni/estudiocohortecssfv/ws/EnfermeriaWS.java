package com.sts_ni.estudiocohortecssfv.ws;

import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.PacienteDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * Clase para realizar el consumo de los metodos del webservice relacionados a Enfermeria.
 */
public class EnfermeriaWS extends EstudioCohorteCssfvWS {

    private Resources RES;
    private int TIME_OUT = 80000;

    public EnfermeriaWS(Resources res) {

        this.RES = res;

        HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
    }

    public ResultadoListWSDTO<InicioDTO> getListaInicioEnfermeria(){

        ResultadoListWSDTO<InicioDTO> retorno = new ResultadoListWSDTO<InicioDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_LISTA_INICIO_ENFERMERIA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_LISTA_INICIO_ENFERMERIA, sobre, HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    ArrayList<InicioDTO> lstInicio = new ArrayList<InicioDTO>();

                    for (int i = 0; i < inicioArrayJson.length(); i++){
                        JSONObject inicioJson  = inicioArrayJson.getJSONObject(i);
                        InicioDTO inicio = new InicioDTO();

                        inicio.setIdObjeto(inicioJson.getInt("secHojaConsulta"));
                        inicio.setCodExpediente(inicioJson.getInt("codExpediente"));
                        inicio.setNomPaciente(inicioJson.getString("nombrePaciente"));
                        inicio.setEstado(inicioJson.getString("descripcion"));
                        inicio.setNumHojaConsulta(inicioJson.getString("numHojaConsulta"));
                        inicio.setSexo(inicioJson.getString("sexo"));
                        String fechaNac = inicioJson.getString("fechaNac");
                        String fechaConsulta = inicioJson.getString("fechaConsulta");
                        SimpleDateFormat sdfFC = new SimpleDateFormat("yyyyMMdd hh:mm:ss a", Locale.ENGLISH);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        Calendar cal = new GregorianCalendar();
                        Calendar calNac = new GregorianCalendar();
                        calNac.setTime(sdf.parse(fechaNac));
                        inicio.setFechaNac(calNac);

                        //Número de expediente fisico con la fecha de nacimiento
                        SimpleDateFormat numExpFis = new SimpleDateFormat("ddMMyy");
                        inicio.setExpedienteFisico(numExpFis.format(calNac.getTime()));

                        cal.setTime(sdfFC.parse(fechaConsulta));
                        inicio.setFechaConsulta(cal);

                        inicio.setNumHojaConsulta(inicioJson.getString("numHojaConsulta"));
                        inicio.setNumOrdenLlegada(inicioJson.getString("ordenLlegada"));
                        inicio.setCodigoEstado(inicioJson.getString("codigoEstado").charAt(0));
                        if(inicioJson.get("nombreMedico").toString() != "null") {
                            inicio.setNombreMedico(inicioJson.get("nombreMedico").toString());
                        }
                        inicio.setUsuarioEnfermeria(inicioJson.getString("usuarioEnfermeria"));

                        if(inicioJson.get("horasv").toString() != "null") {
                            inicio.setHorasv(inicioJson.getString("horasv"));
                        }

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

    //Funcion para guardar los datos preclinicos
    public ErrorDTO enviarDatosPreclinicos(HojaConsultaDTO hojaConsulta){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_ENVIAR_DATOS_PRECLINICOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("codExpediente", hojaConsulta.getCodExpediente());
            objenvio.put("fechaConsulta", hojaConsulta.getFechaConsulta());
            objenvio.put("numHojaConsulta", hojaConsulta.getNumHojaConsulta());
            objenvio.put("pesoKg", hojaConsulta.getPesoKg());
            objenvio.put("tallaCm", hojaConsulta.getTallaCm());
            objenvio.put("temperaturac", hojaConsulta.getTemperaturac());
            objenvio.put("usuarioEnfermeria", hojaConsulta.getUsuarioEnfermeria());
            objenvio.put("expedienteFisico", hojaConsulta.getExpedienteFisico());
            if(hojaConsulta.getUsuarioMedico() != null && hojaConsulta.getUsuarioMedico().intValue() > 0) {
                objenvio.put("usuarioMedico", hojaConsulta.getUsuarioMedico());
            }

            objenvio.put("horasv", hojaConsulta.getHorasv());

            objenvio.put("consultaRespiratoria", hojaConsulta.getConsultaRespiratorio());

            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_ENVIAR_DATOS_PRECLINICOS, sobre, HEADER_PROPERTY);
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

/*    Funcion para guardar los datos preclinicos
    de pacientes que fueron cancelados*/

    public ErrorDTO enviarDatosPreclinicosCancelados(HojaConsultaDTO hojaConsulta, String motivo){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_CANCELAR_DATOS_PRECLINICOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("codExpediente", hojaConsulta.getCodExpediente());
            objenvio.put("fechaConsulta", hojaConsulta.getFechaConsulta());
            objenvio.put("numHojaConsulta", hojaConsulta.getNumHojaConsulta());
            objenvio.put("pesoKg", hojaConsulta.getPesoKg());
            objenvio.put("tallaCm", hojaConsulta.getTallaCm());
            objenvio.put("temperaturac", hojaConsulta.getTemperaturac());
            objenvio.put("usuarioEnfermeria", hojaConsulta.getUsuarioEnfermeria());
            objenvio.put("expedienteFisico", hojaConsulta.getExpedienteFisico());
            objenvio.put("motivo", motivo);
            objenvio.put("horasv", hojaConsulta.getHorasv());
            objenvio.put("consultaRespiratoria", hojaConsulta.getConsultaRespiratorio());

            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_CANCELAR_DATOS_PRECLINICOS, sobre, HEADER_PROPERTY);
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

    /*  Funcion para guardar los datos preclinicos de pacientes
    que no atienden llamado*/

    public ErrorDTO noAtiendeLlamadoDatosPreclinicos(HojaConsultaDTO hojaConsulta){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_NO_ATIENDE_LLAMADO_DATOS_PRECLINICOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("codExpediente", hojaConsulta.getCodExpediente());
            objenvio.put("fechaConsulta", hojaConsulta.getFechaConsulta());
            objenvio.put("numHojaConsulta", hojaConsulta.getNumHojaConsulta());
/*            objenvio.put("pesoKg", hojaConsulta.getPesoKg());
            objenvio.put("tallaCm", hojaConsulta.getTallaCm());
            objenvio.put("temperaturac", hojaConsulta.getTemperaturac());*/
            objenvio.put("usuarioEnfermeria", hojaConsulta.getUsuarioEnfermeria());

            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_NO_ATIENDE_LLAMADO_DATOS_PRECLINICOS, sobre, HEADER_PROPERTY);
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

    //Funcion de busqueda de paciente por el codigo expediente
    public ErrorDTO buscarPacientes(PacienteDTO paciente){

        ErrorDTO retorno = new ErrorDTO();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_BUSCAR_PACIENTE);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(paciente.getCodExpediente());
            paramEviar.setName("paramPaciente");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_BUSCAR_PACIENTE, sobre, HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                   JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    paciente.setNomPaciente(((JSONObject)resultadoJson.get(0)).getString("nombrePaciente"));

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
            public ErrorDTO guardarPacienteEmergencia(HojaConsultaDTO hojaConsulta){

            ErrorDTO retorno = new ErrorDTO();

            try {
                SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDA_PACIENTE_EMERGENGIA);
                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = false;

                JSONObject objenvio = new JSONObject();
                objenvio.put("codExpediente", hojaConsulta.getCodExpediente());
                objenvio.put("usuarioEnfermeria", hojaConsulta.getUsuarioEnfermeria());
                //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GUARDA_PACIENTE_EMERGENCIA, sobre, HEADER_PROPERTY);
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

    public ResultadoObjectWSDTO<CabeceraSintomaDTO> obtenerDatosPreclinicos(Integer secHojaConsulta){

        ResultadoObjectWSDTO<CabeceraSintomaDTO> retorno = new ResultadoObjectWSDTO<>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_DATOS_PRECLINICOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramId = new PropertyInfo();
            paramId.setValue(secHojaConsulta);
            paramId.setName("paramHojaConsultaId");
            paramId.setNamespace("");
            paramId.setType(Integer.class);

            request.addProperty(paramId);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_DATOS_PRECLINICOS, sobre, HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    CabeceraSintomaDTO datosPreclinicos = gson.fromJson(resultadoJson.get(0).toString(),
                            CabeceraSintomaDTO.class);

                    retorno.setObjecRespuesta(datosPreclinicos);

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
    /*Guarda el usuario de enfermeria en la hoja de consulta
    *
    */
    public ErrorDTO guardarUsuarioEnfermeria(HojaConsultaDTO hojaConsulta){

        ErrorDTO retorno = new ErrorDTO();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_ACTUALIZAR_USUARIO_ENFERMERIA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("usuarioEnfermeria", hojaConsulta.getUsuarioEnfermeria());
            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GUARDA_PACIENTE_EMERGENCIA, sobre, HEADER_PROPERTY);
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
