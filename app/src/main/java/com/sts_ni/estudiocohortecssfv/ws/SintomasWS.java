package com.sts_ni.estudiocohortecssfv.ws;

import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.CabezaSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.CategoriaSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.CutaneoSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.DeshidratacionSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.EstadoGeneralesSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.EstadoNutricionalSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.GargantaSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.GastroinstestinalSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.HorarioAtencionDTO;
import com.sts_ni.estudiocohortecssfv.dto.OsteomuscularSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.PacienteDTO;
import com.sts_ni.estudiocohortecssfv.dto.ReferenciaSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.RenalSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.RespiratorioSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeccionesSintomasDTO;
import com.sts_ni.estudiocohortecssfv.dto.VacunasSintomasDTO;
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
import java.util.List;
import java.util.Locale;

/**
 * Clase para consumir los metodos webservice relacionados al tab
 * de sintomas.
 */
public class SintomasWS extends EstudioCohorteCssfvWS {

    private Resources RES;

    private int TIME_OUT = 80000;

    public SintomasWS(Resources res) {

        this.RES = res;

        HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
    }

    /***
     * Metodo para obtener los datos de la cabecera del Tab Sintomas
     * @param codExpediente, Codigo Expediente
     * @return ResultadoObjectWSDTO
     */
    public ResultadoObjectWSDTO<CabeceraSintomaDTO> obtenerDatosCabecera(Integer codExpediente, Integer secHojaConsulta){

        ResultadoObjectWSDTO<CabeceraSintomaDTO> retorno = new ResultadoObjectWSDTO<CabeceraSintomaDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_DATOS_CABECERA_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(codExpediente);
            paramWS.setName("paramExpediente");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);

            PropertyInfo paramId = new PropertyInfo();
            paramId.setValue(secHojaConsulta);
            paramId.setName("paramHojaConsultaId");
            paramId.setNamespace("");
            paramId.setType(Integer.class);

            request.addProperty(paramWS);
            request.addProperty(paramId);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_DATOS_CABECERA_SINTOMAS, sobre, HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    retorno.setObjecRespuesta(new CabeceraSintomaDTO());
                    retorno.getObjecRespuesta().setCodConsentimeinto(((JSONObject)resultadoJson.get(0)).getString("estudiosParticipante"));
                    retorno.getObjecRespuesta().setExpedienteFisico(((JSONObject)resultadoJson.get(0)).getString("expedienteFisico"));
                    String fechaNac = ((JSONObject)resultadoJson.get(0)).getString("fechaNacimiento");
                    SimpleDateFormat sdfFechaNac = new SimpleDateFormat("yyyyMMdd");
                    Calendar calNac = new GregorianCalendar();
                    calNac.setTime(sdfFechaNac.parse(fechaNac));
                    retorno.getObjecRespuesta().setFechaNacimiento(calNac);
                    retorno.getObjecRespuesta().setSexo(((JSONObject)resultadoJson.get(0)).getString("sexo"));
                    retorno.getObjecRespuesta().setPesoKg(((JSONObject)resultadoJson.get(0)).getDouble("pesoKg"));
                    retorno.getObjecRespuesta().setTallaCm(((JSONObject)resultadoJson.get(0)).getDouble("tallaCm"));
                    retorno.getObjecRespuesta().setTemperaturac(((JSONObject)resultadoJson.get(0)).getDouble("temperaturac"));
                    String fechaConsulta = ((JSONObject)resultadoJson.get(0)).getString("fechaConsulta");
                    SimpleDateFormat sdfFechaConsulta = new SimpleDateFormat("yyyyMMdd hh:mm:ss a", Locale.ENGLISH);
                    Calendar calFechaConsulta = new GregorianCalendar();
                    calFechaConsulta.setTime(sdfFechaConsulta.parse(fechaConsulta));
                    retorno.getObjecRespuesta().setFechaConsulta(calFechaConsulta);
                    if(StringUtils.isNullOrEmpty(((JSONObject)resultadoJson.get(0)).getString("horaConsulta")) ||
                            ((JSONObject)resultadoJson.get(0)).getString("horaConsulta").compareTo("null") == 0) {
                        retorno.getObjecRespuesta().setHoraConsulta(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
                    } else {
                        retorno.getObjecRespuesta().setHoraConsulta(((JSONObject)resultadoJson.get(0)).getString("horaConsulta"));
                    }

                    /*Nuevo Campo categoria agregado */
                    if(StringUtils.isNullOrEmpty(((JSONObject)resultadoJson.get(0)).getString("categoria")) ||
                            ((JSONObject)resultadoJson.get(0)).getString("categoria").compareTo("null") == 0) {
                        retorno.getObjecRespuesta().setCategoria(((JSONObject)resultadoJson.get(0)).getString("categoria"));
                    } else {
                        retorno.getObjecRespuesta().setCategoria(((JSONObject)resultadoJson.get(0)).getString("categoria"));
                    }
                    /*Nuevo Campo fif agregado */
                    if(StringUtils.isNullOrEmpty(((JSONObject)resultadoJson.get(0)).getString("fif")) ||
                            ((JSONObject)resultadoJson.get(0)).getString("fif").compareTo("null") == 0) {
                        retorno.getObjecRespuesta().setFif(((JSONObject)resultadoJson.get(0)).getString("fif"));
                    } else {
                        retorno.getObjecRespuesta().setFif(((JSONObject)resultadoJson.get(0)).getString("fif"));
                    }
                    /*Nuevo Campo consulta agregado */
                    if(StringUtils.isNullOrEmpty(((JSONObject)resultadoJson.get(0)).getString("consulta")) ||
                            ((JSONObject)resultadoJson.get(0)).getString("consulta").compareTo("null") == 0) {
                        retorno.getObjecRespuesta().setConsulta(((JSONObject)resultadoJson.get(0)).getString("consulta"));
                    } else {
                        retorno.getObjecRespuesta().setConsulta(((JSONObject)resultadoJson.get(0)).getString("consulta"));
                    }
                    /*Nuevo Campo temperatura medico agregado */
                    if(StringUtils.isNullOrEmpty(((JSONObject)resultadoJson.get(0)).getString("temMedc")) ||
                            ((JSONObject)resultadoJson.get(0)).getString("temMedc").compareTo("null") == 0) {
                        retorno.getObjecRespuesta().setTemMedc(((JSONObject)resultadoJson.get(0)).getString("temMedc"));
                    } else {
                        retorno.getObjecRespuesta().setTemMedc(((JSONObject)resultadoJson.get(0)).getString("temMedc"));
                    }
                    /*Nuevo Campo eritrocitis agregado */
                    if(StringUtils.isNullOrEmpty(((JSONObject)resultadoJson.get(0)).getString("eritrocitos")) ||
                            ((JSONObject)resultadoJson.get(0)).getString("eritrocitos").compareTo("null") == 0) {
                        retorno.getObjecRespuesta().setEritrocitos(((JSONObject)resultadoJson.get(0)).getString("eritrocitos"));
                    } else {
                        retorno.getObjecRespuesta().setEritrocitos(((JSONObject)resultadoJson.get(0)).getString("eritrocitos"));
                    }
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

    public ResultadoListWSDTO<HorarioAtencionDTO> obtenerHorarioAtencion(Integer diaSemana){

        ResultadoListWSDTO<HorarioAtencionDTO> retorno = new ResultadoListWSDTO<HorarioAtencionDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_HORARIO_TURNO);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(diaSemana);
            paramWS.setName("paramDiaSemana");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);

            request.addProperty(paramWS);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_HORARIO_TURNO, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){

                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");
                    ArrayList<HorarioAtencionDTO>  lstHorarioAtencion = new ArrayList<HorarioAtencionDTO>();

                    for (int i = 0; i < resultadoJson.length() ; i++) {
                        JSONObject horarioAtencionJson = resultadoJson.getJSONObject(i);
                        HorarioAtencionDTO horarioAtencionDTO = new HorarioAtencionDTO(horarioAtencionJson.getInt("secHorarioAtencion"),
                                horarioAtencionJson.getString("turno"), horarioAtencionJson.getString("dia"), horarioAtencionJson.getString("horaInicio"),
                                horarioAtencionJson.getString("horaFin"));
                        lstHorarioAtencion.add(horarioAtencionDTO);
                    }

                    retorno.setLstResultado(lstHorarioAtencion);

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

    /***
     * Metodo para guardar Generales de sintomas
     *
     * @param hojaConsulta, Hoja consulta Object
     * @return ErrorDTO
     */
    public ErrorDTO guardarGeneralesSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_GENEREALES_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            //objenvio.put("presion", hojaConsulta.getPresion());
            objenvio.put("pas", hojaConsulta.getPas());
            objenvio.put("pad", hojaConsulta.getPad());
            objenvio.put("fciaResp", hojaConsulta.getFciaResp());
            objenvio.put("fciaCard", hojaConsulta.getFciaCard());
            objenvio.put("lugarAtencion", hojaConsulta.getLugarAtencion());
            objenvio.put("consulta", hojaConsulta.getConsulta());
            if(hojaConsulta.getSegChick() != null){
                objenvio.put("segChick", hojaConsulta.getSegChick());
            }
            objenvio.put("turno", hojaConsulta.getTurno());
            objenvio.put("temMedc", hojaConsulta.getTemMedc());
            if(hojaConsulta.getFis() != null) {
                objenvio.put("fis", hojaConsulta.getFis());
            }
            if(hojaConsulta.getFif() != null) {
                objenvio.put("fif", hojaConsulta.getFif());
            }
            if(hojaConsulta.getUltDiaFiebre() != null) {
                objenvio.put("ultDiaFiebre", hojaConsulta.getUltDiaFiebre());
            }
            if(hojaConsulta.getUltDosisAntipiretico() != null) {
                objenvio.put("ultDosisAntipiretico", hojaConsulta.getUltDosisAntipiretico());
            }
            if(hojaConsulta.getAmPmUltDiaFiebre() != null) {
                objenvio.put("amPmUltDiaFiebre", hojaConsulta.getAmPmUltDiaFiebre());
            }
            if(hojaConsulta.getHoraUltDosisAntipiretico() != null) {
                objenvio.put("horaUltDosisAntipiretico", hojaConsulta.getHoraUltDosisAntipiretico());
            }
            if(hojaConsulta.getAmPmUltDosisAntipiretico() != null) {
                objenvio.put("amPmUltDosisAntipiretico", hojaConsulta.getAmPmUltDosisAntipiretico());
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
            transporte.call(ACCIOSOAP_GUARDAR_GENEREALES_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarEstadoGeneralSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_ESTADO_GENEREAL_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("fiebre", hojaConsulta.getFiebre());
            objenvio.put("astenia", hojaConsulta.getAstenia());
            objenvio.put("asomnoliento", hojaConsulta.getAsomnoliento());
            objenvio.put("malEstado", hojaConsulta.getMalEstado());
            objenvio.put("perdidaConsciencia", hojaConsulta.getPerdidaConsciencia());
            objenvio.put("inquieto", hojaConsulta.getInquieto());
            objenvio.put("convulsiones", hojaConsulta.getConvulsiones());
            objenvio.put("hipotermia", hojaConsulta.getHipotermia());
            objenvio.put("letargia", hojaConsulta.getLetargia());
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
            transporte.call(ACCIOSOAP_GUARDAR_ESTADO_GENEREAL_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarGastroinstSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_GASTROIN_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("pocoApetito", hojaConsulta.getPocoApetito());
            objenvio.put("nausea", hojaConsulta.getNausea());
            objenvio.put("dificultadAlimentarse", hojaConsulta.getDificultadAlimentarse());
            objenvio.put("vomito12horas", hojaConsulta.getVomito12horas());
            objenvio.put("vomito12h", hojaConsulta.getVomito12h());
            objenvio.put("diarrea", hojaConsulta.getDiarrea());
            objenvio.put("diarreaSangre", hojaConsulta.getDiarreaSangre());
            objenvio.put("estrenimiento", hojaConsulta.getEstrenimiento());
            objenvio.put("dolorAbIntermitente", hojaConsulta.getDolorAbIntermitente());
            objenvio.put("dolorAbContinuo", hojaConsulta.getDolorAbContinuo());
            objenvio.put("epigastralgia", hojaConsulta.getEpigastralgia());
            objenvio.put("intoleranciaOral", hojaConsulta.getIntoleranciaOral());
            objenvio.put("distensionAbdominal", hojaConsulta.getDistensionAbdominal());
            objenvio.put("hepatomegalia", hojaConsulta.getHepatomegalia());
            objenvio.put("hepatomegaliaCM", (hojaConsulta.getHepatomegaliaCm() != null) ? hojaConsulta.getHepatomegaliaCm() : JSONObject.NULL);

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
            transporte.call(ACCIOSOAP_GUARDAR_GASTROIN_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarOsteomuscularSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_OSTEOMUSCULAR_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("altralgia", hojaConsulta.getAltralgia());
            objenvio.put("mialgia", hojaConsulta.getMialgia());
            objenvio.put("lumbalgia", hojaConsulta.getLumbalgia());
            objenvio.put("dolorCuello", hojaConsulta.getDolorCuello());
            objenvio.put("tenosinovitis", hojaConsulta.getTenosinovitis());
            objenvio.put("artralgiaProximal", hojaConsulta.getArtralgiaProximal());
            objenvio.put("artralgiaDistal", hojaConsulta.getArtralgiaDistal());
            objenvio.put("conjuntivitis", hojaConsulta.getConjuntivitis());
            objenvio.put("edemaMunecas", hojaConsulta.getEdemaMunecas());
            objenvio.put("edemaCodos", hojaConsulta.getEdemaCodos());
            objenvio.put("edemaHombros", hojaConsulta.getEdemaHombros());
            objenvio.put("edemaRodillas", hojaConsulta.getEdemaRodillas());
            objenvio.put("edemaTobillos", hojaConsulta.getEdemaTobillos());
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
            transporte.call(ACCIOSOAP_GUARDAR_OSTEOMUSCULAR_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarCabezaSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_CABEZA_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("cefalea", hojaConsulta.getCefalea());
            objenvio.put("rigidezCuello", hojaConsulta.getRigidezCuello());
            objenvio.put("inyeccionConjuntival", hojaConsulta.getInyeccionConjuntival());
            objenvio.put("hemorragiaSuconjuntival", hojaConsulta.getHemorragiaSuconjuntival());
            objenvio.put("dolorRetroocular", hojaConsulta.getDolorRetroocular());
            objenvio.put("fontanelaAbombada", hojaConsulta.getFontanelaAbombada());
            objenvio.put("ictericiaConuntival", hojaConsulta.getIctericiaConuntival());
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
            transporte.call(ACCIOSOAP_GUARDAR_CABEZA_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarDeshidraSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_DESHIDRA_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("lenguaMucosasSecas", hojaConsulta.getLenguaMucosasSecas());
            objenvio.put("pliegueCutaneo", hojaConsulta.getPliegueCutaneo());
            objenvio.put("orinaReducida", hojaConsulta.getOrinaReducida());
            objenvio.put("bebeConSed", hojaConsulta.getBebeConSed());
            objenvio.put("ojosHundidos", hojaConsulta.getOjosHundidos());
            objenvio.put("fontanelaHundida", hojaConsulta.getFontanelaHundida());
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
            transporte.call(ACCIOSOAP_GUARDAR_DESHIDRA_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarCutaneoSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_CUTANEO_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("rahsLocalizado", hojaConsulta.getRahsLocalizado());
            objenvio.put("rahsGeneralizado", hojaConsulta.getRahsGeneralizado());
            objenvio.put("rashEritematoso", hojaConsulta.getRashEritematoso());
            objenvio.put("rahsMacular", hojaConsulta.getRahsMacular());
            objenvio.put("rashPapular", hojaConsulta.getRashPapular());
            objenvio.put("rahsMoteada", hojaConsulta.getRahsMoteada());
            objenvio.put("ruborFacial", hojaConsulta.getRuborFacial());
            objenvio.put("equimosis", hojaConsulta.getEquimosis());
            objenvio.put("cianosisCentral", hojaConsulta.getCianosisCentral());
            objenvio.put("ictericia", hojaConsulta.getIctericia());
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
            transporte.call(ACCIOSOAP_GUARDAR_CUTANEO_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarGargantaSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_GARGANTA_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("eritema", hojaConsulta.getEritema());
            objenvio.put("dolorGarganta", hojaConsulta.getDolorGarganta());
            objenvio.put("adenopatiasCervicales", hojaConsulta.getAdenopatiasCervicales());
            objenvio.put("exudado", hojaConsulta.getExudado());
            objenvio.put("petequiasMucosa", hojaConsulta.getPetequiasMucosa());
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
            transporte.call(ACCIOSOAP_GUARDAR_GARGANTA_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarRenalSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_RENAL_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("sintomasUrinarios", hojaConsulta.getSintomasUrinarios());
            objenvio.put("leucocituria", hojaConsulta.getLeucocituria());
            objenvio.put("nitritos", hojaConsulta.getNitritos());
            objenvio.put("eritrocitos", hojaConsulta.getEritrocitos());
            objenvio.put("bilirrubinuria", hojaConsulta.getBilirrubinuria());
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
            transporte.call(ACCIOSOAP_GUARDAR_RENAL_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarEstadoNutriSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_ESTADO_NUTRI_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("imc", hojaConsulta.getImc());
            objenvio.put("obeso", hojaConsulta.getObeso());
            objenvio.put("sobrepeso", hojaConsulta.getSobrepeso());
            objenvio.put("sospechaProblema", hojaConsulta.getSospechaProblema());
            objenvio.put("normal", hojaConsulta.getNormal());
            objenvio.put("bajoPeso", hojaConsulta.getBajoPeso());
            objenvio.put("bajoPesoSevero", hojaConsulta.getBajoPesoSevero());
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
            transporte.call(ACCIOSOAP_GUARDAR_ESTADO_NUTRI_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarRespiratorioSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_RESPIRATORIO_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("tos", hojaConsulta.getTos());
            objenvio.put("rinorrea", hojaConsulta.getRinorrea());
            objenvio.put("congestionNasal", hojaConsulta.getCongestionNasal());
            objenvio.put("otalgia", hojaConsulta.getOtalgia());
            objenvio.put("aleteoNasal", hojaConsulta.getAleteoNasal());
            objenvio.put("apnea", hojaConsulta.getApnea());
            objenvio.put("respiracionRapida", hojaConsulta.getRespiracionRapida());
            objenvio.put("quejidoEspiratorio", hojaConsulta.getQuejidoEspiratorio());
            objenvio.put("estiradorReposo", hojaConsulta.getEstiradorReposo());
            objenvio.put("tirajeSubcostal", hojaConsulta.getTirajeSubcostal());
            objenvio.put("sibilancias", hojaConsulta.getSibilancias());
            objenvio.put("crepitos", hojaConsulta.getCrepitos());
            objenvio.put("roncos", hojaConsulta.getRoncos());
            objenvio.put("otraFif", hojaConsulta.getOtraFif());
            if(!StringUtils.isNullOrEmpty(hojaConsulta.getNuevaFif())) {
                objenvio.put("nuevaFif", hojaConsulta.getNuevaFif());
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
            transporte.call(ACCIOSOAP_GUARDAR_RESPIRATORIO_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarReferenciaSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_REFERENCIA_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("interconsultaPediatrica", hojaConsulta.getInterconsultaPediatrica());
            objenvio.put("referenciaHospital", hojaConsulta.getReferenciaHospital());
            objenvio.put("referenciaDengue", hojaConsulta.getReferenciaDengue());
            objenvio.put("referenciaIrag", hojaConsulta.getReferenciaIrag());
            objenvio.put("referenciaChik", hojaConsulta.getReferenciaChik());
            objenvio.put("eti", hojaConsulta.getEti());
            objenvio.put("irag", hojaConsulta.getIrag());
            objenvio.put("neumonia", hojaConsulta.getNeumonia());
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
            transporte.call(ACCIOSOAP_GUARDAR_REFERENCIA_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public ErrorDTO guardarVacunasSintomas(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_VACUNAS_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("lactanciaMaterna", hojaConsulta.getLactanciaMaterna());
            objenvio.put("vacunasCompletas", hojaConsulta.getVacunasCompletas());
            objenvio.put("vacunaInfluenza", hojaConsulta.getVacunaInfluenza());
            if(!StringUtils.isNullOrEmpty(hojaConsulta.getFechaVacuna())) {
                objenvio.put("fechaVacuna", hojaConsulta.getFechaVacuna());
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
            transporte.call(ACCIOSOAP_GUARDAR_VACUNAS_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public boolean tieneConsetimientoDengue(Integer codExpediente){

        boolean retorno = false;
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_VALIDAR_CONSENTIMIENTO_DENGUE);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(codExpediente);
            paramWS.setName("paramExpediente");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);

            request.addProperty(paramWS);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_VALIDAR_CONSENTIMIENTO_DENGUE, sobre, HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    retorno = ((JSONObject)resultadoJson.get(0)).getBoolean("consentimiento");

                }else {
                   throw new Exception(mensaje.getString("texto"));
                }

            }else {
                throw new Exception(RES.getString(R.string.msj_servicio_no_envia_respuesta));
            }

        }catch (ConnectException ce){
            ce.printStackTrace();
        }catch (SocketTimeoutException et){
            et.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retorno;
    }

    public ErrorDTO guardarCategoriaSintomas(HojaConsultaDTO hojaConsulta, boolean categoriaAB, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_CATEGORIA_SINTOMAS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            if(hojaConsulta.getSaturaciono2() != -1) objenvio.put("saturaciono2", hojaConsulta.getSaturaciono2());
            //objenvio.put("imc", hojaConsulta.getImc());
            if(hojaConsulta.getCategoria() != null) {
                objenvio.put("categoria", hojaConsulta.getCategoria());
            }

            if(hojaConsulta.getCambioCategoria() != null) {
                objenvio.put("cambioCategoria", hojaConsulta.getCambioCategoria().toString());
            }

            //if(categoriaAB) {
            objenvio.put("manifestacionHemorragica", hojaConsulta.getManifestacionHemorragica().toString());
            objenvio.put("pruebaTorniquetePositiva", hojaConsulta.getPruebaTorniquetePositiva().toString());
            objenvio.put("petequia10Pt", hojaConsulta.getPetequia10Pt().toString());
            objenvio.put("petequia20Pt", hojaConsulta.getPetequia20Pt().toString());
            objenvio.put("pielExtremidadesFrias", hojaConsulta.getPielExtremidadesFrias().toString());
            objenvio.put("palidezEnExtremidades", hojaConsulta.getPalidezEnExtremidades().toString());

            objenvio.put("epistaxis", hojaConsulta.getEpistaxis().toString());
            objenvio.put("gingivorragia", hojaConsulta.getGingivorragia().toString());
            objenvio.put("petequiasEspontaneas", hojaConsulta.getPetequiasEspontaneas().toString());
            objenvio.put("llenadoCapilar2seg", hojaConsulta.getLlenadoCapilar2seg().toString());
            objenvio.put("cianosis", hojaConsulta.getCianosis().toString());
            objenvio.put("linfocitosaAtipicos", hojaConsulta.getLinfocitosaAtipicos());
            objenvio.put("fechaLinfocitos", hojaConsulta.getFechaLinfocitos());

            objenvio.put("hipermenorrea", hojaConsulta.getHipermenorrea().toString());
            objenvio.put("hematemesis", hojaConsulta.getHematemesis().toString());
            objenvio.put("melena", hojaConsulta.getMelena().toString());
            objenvio.put("hemoconc", hojaConsulta.getHemoconc().toString());
            objenvio.put("hemoconcentracion", hojaConsulta.getHemoconcentracion());
            //}

            if(hojaConsulta.getHospitalizado() != null) {
                objenvio.put("hospitalizado", hojaConsulta.getHospitalizado().toString());
            }

            if(!StringUtils.isNullOrEmpty(hojaConsulta.getHospitalizadoEspecificar())) {
                objenvio.put("hospitalizadoEspecificar", hojaConsulta.getHospitalizadoEspecificar());
            }

            if(hojaConsulta.getTransfusionSangre() != null) {
                objenvio.put("transfusionSangre", hojaConsulta.getTransfusionSangre().toString());
            }
            if(!StringUtils.isNullOrEmpty(hojaConsulta.getTransfusionEspecificar())) {
                objenvio.put("transfusionEspecificar", hojaConsulta.getTransfusionEspecificar());
            }

            if(hojaConsulta.getTomandoMedicamento() != null) {
                objenvio.put("tomandoMedicamento", hojaConsulta.getTomandoMedicamento().toString());
            }
            if(!StringUtils.isNullOrEmpty(hojaConsulta.getMedicamentoEspecificar())) {
                objenvio.put("medicamentoEspecificar", hojaConsulta.getMedicamentoEspecificar());
            }

            if(hojaConsulta.getMedicamentoDistinto() != null) {
                objenvio.put("medicamentoDistinto", hojaConsulta.getMedicamentoDistinto().toString());
            }
            if(!StringUtils.isNullOrEmpty(hojaConsulta.getMedicamentoDistEspecificar())) {
                objenvio.put("medicamentoDistEspecificar", hojaConsulta.getMedicamentoDistEspecificar());
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
            transporte.call(ACCIOSOAP_GUARDAR_CATEGORIA_SINTOMAS, sobre, HEADER_PROPERTY);

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

    public String validacionMatrizSintoma(HojaConsultaDTO hojaConsulta){

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_VALIDACION_MATRIZ_SINTOMA);
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
            transporte.call(ACCIOSOAP_VALIDACION_MATRIZ_SINTOMA, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if (resultado != null && !resultado.isEmpty()) {
                return resultado;
            }

        }catch (ConnectException ce){
            ce.printStackTrace();
        }catch (SocketTimeoutException et) {
            et.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ResultadoObjectWSDTO<GeneralesSintomasDTO> obtenerGeneralesSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<GeneralesSintomasDTO> retorno = new ResultadoObjectWSDTO<GeneralesSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_GENERALES_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_GENERALES_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    GeneralesSintomasDTO generalesSintomas = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            GeneralesSintomasDTO.class);

                    retorno.setObjecRespuesta(generalesSintomas);
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

    public ResultadoObjectWSDTO<EstadoGeneralesSintomasDTO> obtenerEstadoGeneralSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<EstadoGeneralesSintomasDTO> retorno = new ResultadoObjectWSDTO<EstadoGeneralesSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_ESTADO_GENERAL_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_ESTADO_GENERAL_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    EstadoGeneralesSintomasDTO estadoGeneralesSintomas = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                                                            EstadoGeneralesSintomasDTO.class);

                    retorno.setObjecRespuesta(estadoGeneralesSintomas);
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

    public ResultadoObjectWSDTO<GastroinstestinalSintomasDTO> obtenerGastroinstestinalSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<GastroinstestinalSintomasDTO> retorno = new ResultadoObjectWSDTO<GastroinstestinalSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_GASTROINTESTINAL_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_GASTROINTESTINAL_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    GastroinstestinalSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            GastroinstestinalSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<OsteomuscularSintomasDTO> obtenerOsteomuscularSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<OsteomuscularSintomasDTO> retorno = new ResultadoObjectWSDTO<OsteomuscularSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_OSTEOMUSCULAR_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_OSTEOMUSCULAR_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    OsteomuscularSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            OsteomuscularSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<CabezaSintomasDTO> obtenerCabezaSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<CabezaSintomasDTO> retorno = new ResultadoObjectWSDTO<CabezaSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_CABEZA_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_CABEZA_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    CabezaSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            CabezaSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<DeshidratacionSintomasDTO> obtenerDeshidratacionSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<DeshidratacionSintomasDTO> retorno = new ResultadoObjectWSDTO<DeshidratacionSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_DESHIDRATACION_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_DESHIDRATACION_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    DeshidratacionSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            DeshidratacionSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<CutaneoSintomasDTO> obtenerCutaneoSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<CutaneoSintomasDTO> retorno = new ResultadoObjectWSDTO<CutaneoSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_CUTANEO_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_CUTANEO_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    CutaneoSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            CutaneoSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<GargantaSintomasDTO> obtenerGargantaSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<GargantaSintomasDTO> retorno = new ResultadoObjectWSDTO<GargantaSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_GARGANTA_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_GARGANTA_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    GargantaSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            GargantaSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<RenalSintomasDTO> obtenerRenalSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<RenalSintomasDTO> retorno = new ResultadoObjectWSDTO<RenalSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_RENAL_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_RENAL_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    RenalSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            RenalSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<EstadoNutricionalSintomasDTO> obtenerEstadoNutricionalSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<EstadoNutricionalSintomasDTO> retorno = new ResultadoObjectWSDTO<EstadoNutricionalSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_ESTADO_NUTRICIONAL_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_ESTADO_NUTRICIONAL_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    EstadoNutricionalSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            EstadoNutricionalSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<RespiratorioSintomasDTO> obtenerRespiratorioSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<RespiratorioSintomasDTO> retorno = new ResultadoObjectWSDTO<RespiratorioSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_RESPIRATORIO_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_RESPIRATORIO_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    RespiratorioSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            RespiratorioSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<ReferenciaSintomasDTO> obtenerReferenciaSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<ReferenciaSintomasDTO> retorno = new ResultadoObjectWSDTO<ReferenciaSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_REFERENCIA_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_REFERENCIA_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    ReferenciaSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            ReferenciaSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<VacunasSintomasDTO> obtenerVacunaSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<VacunasSintomasDTO> retorno = new ResultadoObjectWSDTO<VacunasSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_VACUNAS_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_VACUNAS_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    VacunasSintomasDTO pojoRetorno = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            VacunasSintomasDTO.class);

                    retorno.setObjecRespuesta(pojoRetorno);
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

    public ResultadoObjectWSDTO<CategoriaSintomasDTO> obtenerCategoriasSintomas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<CategoriaSintomasDTO> retorno = new ResultadoObjectWSDTO<CategoriaSintomasDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_CATEGORIAS_SINTOMAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_CATEGORIAS_SINTOMAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    CategoriaSintomasDTO categoriaSintomas = gson.fromJson(((JSONObject) resultadoJson.get(0)).toString(),
                            CategoriaSintomasDTO.class);

                    retorno.setObjecRespuesta(categoriaSintomas);
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

    //Funcion para guardar los datos preclinicos
    public ErrorDTO editarDatosPreclinicos(HojaConsultaDTO hojaConsulta, String usuarioLogiado){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_EDITAR_DATOS_PRECLINICOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("pesoKg", hojaConsulta.getPesoKg());
            objenvio.put("tallaCm", hojaConsulta.getTallaCm());
            objenvio.put("temperaturac", hojaConsulta.getTemperaturac());
            objenvio.put("expedienteFisico", hojaConsulta.getExpedienteFisico());
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
            transporte.call(ACCIOSOAP_EDITAR_DATOS_PRECLINICOS, sobre, HEADER_PROPERTY);
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

    public ResultadoObjectWSDTO<SeccionesSintomasDTO> obtenerSeccionSintomasCompletadas(HojaConsultaDTO hojaConsulta){
        ResultadoObjectWSDTO<SeccionesSintomasDTO> retorno = new ResultadoObjectWSDTO<>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_SECCIONES_SINTOMAS_COMPLETADAS);
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
            transporte.call(ACCIOSOAP_METODO_GET_SECCIONES_SINTOMAS_COMPLETADAS, sobre, HEADER_PROPERTY);

            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)) {
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if (mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();
                    SeccionesSintomasDTO seccionesSintomas = gson.fromJson(resultadoJson.get(0).toString(),
                            SeccionesSintomasDTO.class);

                    retorno.setObjecRespuesta(seccionesSintomas);
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

    //Retornando la fis y la fif cuando la consulta es de seguimiento y convaleciente

    public ResultadoObjectWSDTO<HojaConsultaDTO> getFisFifConsultaInicial(Integer codExpediente){

        ResultadoObjectWSDTO<HojaConsultaDTO> retorno = new ResultadoObjectWSDTO<HojaConsultaDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_OBTENER_FIS_FIF);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(codExpediente);
            paramEviar.setName("codExpediente");
            paramEviar.setNamespace("");
            paramEviar.setType(Integer.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_OBTENER_FIS_FIF, sobre, HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJSON = (JSONArray) jObject.get("resultado");
                    //JSONObject resultadoJson = ((JSONObject)resultadoJSON.get(0));

                    retorno.setObjecRespuesta(new HojaConsultaDTO());

                    retorno.getObjecRespuesta().setFis(((JSONObject)resultadoJSON.get(0)).getString("fis"));
                    retorno.getObjecRespuesta().setFif(((JSONObject)resultadoJSON.get(0)).getString("fif"));

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
}
