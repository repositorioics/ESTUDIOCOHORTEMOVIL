package com.sts_ni.estudiocohortecssfv.ws;

import android.content.res.Resources;
import android.util.Base64;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.MedicosDTO;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Clase para realizar el consumo de los metodos del webservice relacionados a Consulta.
 */
public class ConsultaWS extends EstudioCohorteCssfvWS {

    private Resources RES;
    private static int TIME_OUT = 40000;
    private static int TIME_OUT_PDF = 120000;
    public ArrayList<HeaderProperty> HEADER_PROPERTY;
    public ConsultaWS(Resources res) {
        this.HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        this.HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
        this.RES = res;
    }

    public ResultadoListWSDTO<InicioDTO> getListaInicioConsulta(){

        ResultadoListWSDTO<InicioDTO> retorno = new ResultadoListWSDTO<InicioDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_LISTA_INICIO_CONSULTA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_LISTA_INICIO_CONSULTA, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
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
                        inicio.setCodigoEstado(inicioJson.get("codigoEstado").toString().charAt(0));
                        String fechaNac = inicioJson.getString("fechaNac");
                        Calendar calFechaNac = new GregorianCalendar();
                        calFechaNac.setTime(new SimpleDateFormat("yyyyMMdd").parse(fechaNac));
                        inicio.setFechaNac(calFechaNac);


                        if(inicioJson.get("usuarioMedico").toString()!="null")
                            inicio.setUsuarioMedico(inicioJson.getInt("usuarioMedico"));

                        if(inicioJson.get("medicoCambioTurno").toString()!="null")
                            inicio.setMedicoCambioTurno(inicioJson.getInt("medicoCambioTurno"));
                        if(inicioJson.get("nombreMedico").toString() !="null" )
                            inicio.setNombreMedico(inicioJson.get("nombreMedico").toString());
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

    public ResultadoListWSDTO<InicioDTO> getListaInicioConsultaPorExpediente(int codExpediente, boolean esEnfermeria){

        ResultadoListWSDTO<InicioDTO> retorno = new ResultadoListWSDTO<InicioDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_LISTA_INICIO_CONSULTAPOREXPEDIENTE);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(codExpediente);
            paramWS.setName("paramCodExpediente");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);

            PropertyInfo paramEsEnfermeriaWS = new PropertyInfo();
            paramEsEnfermeriaWS.setValue(esEnfermeria);
            paramEsEnfermeriaWS.setName("paramEsEnfermeria");
            paramEsEnfermeriaWS.setNamespace("");
            paramEsEnfermeriaWS.setType(Boolean.class);


            request.addProperty(paramWS);
            request.addProperty(paramEsEnfermeriaWS);
            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_LISTA_INICIO_CONSULTAPOREXPEDIENTE, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");
                    ArrayList<InicioDTO> lstInicio = new ArrayList<InicioDTO>();

                    if(esEnfermeria) {
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
                    } else {
                        for (int i = 0; i < inicioArrayJson.length(); i++){
                            JSONObject inicioJson  = inicioArrayJson.getJSONObject(i);
                            InicioDTO inicio = new InicioDTO();
                            inicio.setIdObjeto(inicioJson.getInt("secHojaConsulta"));
                            inicio.setCodExpediente(inicioJson.getInt("codExpediente"));
                            inicio.setNomPaciente(inicioJson.getString("nombrePaciente"));
                            inicio.setEstado(inicioJson.getString("descripcion"));
                            inicio.setNumHojaConsulta(inicioJson.getString("numHojaConsulta"));
                            inicio.setCodigoEstado(inicioJson.get("codigoEstado").toString().charAt(0));


                            if(inicioJson.get("usuarioMedico").toString()!="null")
                                inicio.setUsuarioMedico(inicioJson.getInt("usuarioMedico"));

                            if(inicioJson.get("medicoCambioTurno").toString()!="null")
                                inicio.setMedicoCambioTurno(inicioJson.getInt("medicoCambioTurno"));
                            if(inicioJson.get("nombreMedico").toString() !="null" )
                                inicio.setNombreMedico(inicioJson.get("nombreMedico").toString());
                            lstInicio.add(inicio);
                        }
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

    public ResultadoListWSDTO<HojaConsultaDTO> getHojaConsultaPorNumero(int codExpediente){

        ResultadoListWSDTO<HojaConsultaDTO> retorno = new ResultadoListWSDTO<>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_CONSULTAPORNUMERO);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(codExpediente);
            paramWS.setName("paramSecHojaConsulta");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_CONSULTAPORNUMERO, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    ArrayList<HojaConsultaDTO> lstInicio = new ArrayList<HojaConsultaDTO>();

                    for (int i = 0; i < inicioArrayJson.length(); i++){
                        JSONObject inicioJson  = inicioArrayJson.getJSONObject(i);
                        HojaConsultaDTO inicio = new HojaConsultaDTO();

                        inicio.setSecHojaConsulta(inicioJson.getInt("secHojaConsulta"));
                        inicio.setHistoriaExamenFisico(inicioJson.getString("historiaExamenFisico"));
                        //PLANES
                        inicio.setPlanes(inicioJson.getString("planes"));
                        //TRATAMIENTO
                        inicio.setAcetaminofen(inicioJson.get("acetaminofen").toString().charAt(0));
                        inicio.setAsa(inicioJson.get("asa") .toString().charAt(0));
                        inicio.setIbuprofen(inicioJson.get("ibuprofen").toString().charAt(0));
                        inicio.setPenicilina(inicioJson.get("penicilina").toString().charAt(0));
                        inicio.setAmoxicilina(inicioJson.get("amoxicilina").toString().charAt(0));
                        inicio.setDicloxacilina(inicioJson.get("dicloxacilina").toString().charAt(0));
                        inicio.setOtro(inicioJson.get("otro").toString().charAt(0));
                        inicio.setOtroAntibiotico(inicioJson.getString("otroAntibiotico"));
                        inicio.setFurazolidona(inicioJson.get("furazolidona").toString().charAt(0));
                        inicio.setMetronidazolTinidazol(inicioJson.get("metronidazolTinidazol").toString().charAt(0));
                        inicio.setAlbendazolMebendazol(inicioJson.get("albendazolMebendazol").toString().charAt(0));
                        inicio.setSulfatoFerroso(inicioJson.get("sulfatoFerroso").toString().charAt(0));
                        inicio.setSueroOral(inicioJson.get("sueroOral").toString().charAt(0));
                        inicio.setSulfatoZinc(inicioJson.get("sulfatoZinc").toString().charAt(0));
                        inicio.setLiquidosIv(inicioJson.get("liquidosIv").toString().charAt(0));
                        inicio.setPrednisona(inicioJson.get("prednisona").toString().charAt(0));
                        inicio.setHidrocortisonaIv(inicioJson.get("hidrocortisonaIv").toString().charAt(0));
                        inicio.setSalbutamol(inicioJson.get("salbutamol").toString().charAt(0));
                        inicio.setOseltamivir(inicioJson.get("oseltamivir").toString().charAt(0));
                        //DIAGNOSTICO
                        if(inicioJson.get("diagnostico1").toString()!="null")
                             inicio.setDiagnostico1((short) inicioJson.getInt("diagnostico1"));
                        if(inicioJson.get("diagnostico2").toString()!="null")
                          inicio.setDiagnostico2((short) inicioJson.getInt("diagnostico2"));
                        if(inicioJson.get("diagnostico3").toString()!="null")
                             inicio.setDiagnostico3((short) inicioJson.getInt("diagnostico3"));
                        if(inicioJson.get("diagnostico4").toString()!="null")
                          inicio.setDiagnostico4((short) inicioJson.getInt("diagnostico4"));
                        inicio.setOtroDiagnostico( inicioJson.getString("otroDiagnostico"));
                        lstInicio.add(inicio);

                        //PROXIMACITA
                        if(inicioJson.get("telef").toString() != "null") {
                            inicio.setTelef((Long) inicioJson.getLong("telef"));
                        }

                        inicio.setProximaCita(inicioJson.getString("proximaCita"));
                        if(inicioJson.get("colegio").toString() != "null") {
                            inicio.setColegio(inicioJson.get("colegio").toString());
                        }

                        inicio.setHorarioClases(inicioJson.getString("horarioClases"));


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



    public byte[] getHojaConsultaPdf(int secHojaConsulta){

        byte[] retorno=null;

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_VISUALIZARHOJACONSULTA_PDF);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(secHojaConsulta);
            paramWS.setName("secHojaConsulta");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT_PDF);
            transporte.call(ACCIOSOAP_VISUALIZARHOJACONSULTA_PDF, sobre, this.HEADER_PROPERTY);
            retorno=  Base64.decode(sobre.getResponse().toString(), Base64.DEFAULT);
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

    //Actualizar Hoja de Consulta
    public ErrorDTO ActualizarEstadoHojaConsulta(HojaConsultaDTO hojaConsulta,boolean cambioTurno){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_ACTUALIZARESTADOENCONSULTA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();

            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            if (cambioTurno) {
                objenvio.put("medicoCambioTurno", hojaConsulta.getMedicoCambioTurno());
            } else {
                objenvio.put("usuarioMedico", hojaConsulta.getUsuarioMedico());
                objenvio.put("horaConsulta", new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
            }

            //Seteando parametros
            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);


            PropertyInfo paramCambioTurno = new PropertyInfo();
            paramCambioTurno.setValue(cambioTurno);
            paramCambioTurno.setName("paramCambioTurno");
            paramCambioTurno.setNamespace("");
            paramCambioTurno.setType(Boolean.class);

            request.addProperty(paramEviar);
            request.addProperty(paramCambioTurno);
            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_ACTUALIZAESTADORENCONSULTA, sobre);
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

    public ResultadoListWSDTO<MedicosDTO> getListaMedicos(boolean presentaCodigo){

        ResultadoListWSDTO<MedicosDTO> retorno = new ResultadoListWSDTO<MedicosDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_LISTA_MEDICOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_LISTA_MEDICOS, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    ArrayList<MedicosDTO> lstMedico = new ArrayList<MedicosDTO>();

                    for (int i = 0; i < inicioArrayJson.length(); i++){
                        JSONObject medicoJson  = inicioArrayJson.getJSONObject(i);
                        MedicosDTO medico = new MedicosDTO();
                        medico.setPresentaCodigoPersonal(presentaCodigo);
                        medico.setIdMedico((short) medicoJson.getInt("idMedico"));
                        medico.setNombreMedico(medicoJson.getString("nombreMedico"));
                        medico.setCodigoPersonal(medicoJson.getString("codigoPersonal"));

                        lstMedico.add(medico);
                    }

                    retorno.setLstResultado(lstMedico);
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

    public ResultadoListWSDTO<MedicosDTO> getListaMedicos(String nombre, boolean presentaCodigo){

        ResultadoListWSDTO<MedicosDTO> retorno = new ResultadoListWSDTO<MedicosDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_LISTA_MEDICOS_POR_NOMBRE);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("nombre", nombre);

            //Seteando parametros
            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramNombre");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);
            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_LISTA_MEDICOS_POR_NOMBRE, sobre, this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    ArrayList<MedicosDTO> lstMedico = new ArrayList<MedicosDTO>();

                    for (int i = 0; i < inicioArrayJson.length(); i++){
                        JSONObject medicoJson  = inicioArrayJson.getJSONObject(i);
                        MedicosDTO medico = new MedicosDTO();
                        medico.setPresentaCodigoPersonal(presentaCodigo);
                        medico.setIdMedico((short) medicoJson.getInt("idMedico"));
                        medico.setNombreMedico(medicoJson.getString("nombreMedico"));
                        medico.setCodigoPersonal(medicoJson.getString("codigoPersonal"));

                        lstMedico.add(medico);
                    }

                    retorno.setLstResultado(lstMedico);
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


