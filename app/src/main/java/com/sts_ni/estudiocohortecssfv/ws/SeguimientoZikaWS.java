package com.sts_ni.estudiocohortecssfv.ws;

import android.content.res.Resources;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaZikaDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoListWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.SeguimientoZikaDTO;
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
import java.util.List;


/**
 * Clase para gestionar el cosumo del webservice a los metodos relacionados a Siguimiento Zika.
 */
public class SeguimientoZikaWS extends EstudioCohorteCssfvWS {
    private int TIME_OUT = 40000;
    private static int TIME_OUT_PDF = 120000;
    public ArrayList<HeaderProperty> HEADER_PROPERTY;
    private Resources RES;

    public SeguimientoZikaWS(Resources res) {
        this.HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        this.HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
        this.RES = res;
    }

    public ResultadoObjectWSDTO<HojaZikaDTO> buscarPacientes(String codigoExpediente){

        ResultadoObjectWSDTO<HojaZikaDTO> retorno = new ResultadoObjectWSDTO<HojaZikaDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, BUSCAR_PACIENTE_SEGUIMIENTO_ZIKA);
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
            transporte.call(ACCIOSOAP_BUSCAR_PACIENTE_SEGUIMIENTO_ZIKA, sobre,this.HEADER_PROPERTY);
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

    public ResultadoObjectWSDTO<HojaZikaDTO> buscarPacienteCrearHoja(String codExpediente){

        ResultadoObjectWSDTO<HojaZikaDTO> retorno = new ResultadoObjectWSDTO<>();
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

                    HojaZikaDTO hojaZika = gson.fromJson(resultadoJson.get(0).toString(), HojaZikaDTO.class);

                    retorno.setObjecRespuesta(hojaZika);

                    retorno.setObjecRespuesta(hojaZika);

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

    //Funcion de busqueda de paciente con hoja de seguimiento zika por el codigo expediente
    public ResultadoObjectWSDTO<HojaZikaDTO> buscarPaciente(String codExpediente){

        ResultadoObjectWSDTO<HojaZikaDTO> retorno = new ResultadoObjectWSDTO<>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, BUSCAR_PACIENTE_SEGUIMIENTO_ZIKA);
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
            transporte.call(ACCIOSOAP_BUSCAR_PACIENTE_SEGUIMIENTO_ZIKA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    HojaZikaDTO hojaZika = gson.fromJson(resultadoJson.get(0).toString(), HojaZikaDTO.class);

                    retorno.setObjecRespuesta(hojaZika);
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

    //Funcion de busqueda de seguimiento de zika por numero de seguimiento
    public ResultadoObjectWSDTO<HojaZikaDTO> buscarSeguimientoZika(String numSeguimiento){

        ResultadoObjectWSDTO<HojaZikaDTO> retorno = new ResultadoObjectWSDTO<>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_BUSCAR_HOJA_SEGUIMIENTO_ZIKA);
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
            transporte.call(ACCIOSOAP_METODO_BUSCAR_HOJA_SEGUIMIENTO_ZIKA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                   JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    HojaZikaDTO hojaZika = gson.fromJson(resultadoJson.get(0).toString(), HojaZikaDTO.class);

                    retorno.setObjecRespuesta(hojaZika);

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
    public ResultadoObjectWSDTO<HojaZikaDTO> crearHojaSeguimiento(HojaZikaDTO hojaZika){

        ResultadoObjectWSDTO<HojaZikaDTO> retorno = new ResultadoObjectWSDTO<>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_CREAR_HOJA_SEGUIMIENTO_ZIKA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            Gson gson = new GsonBuilder().create();
            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(gson.toJson(hojaZika));
            paramEviar.setName("paramCrearHoja");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_CREAR_HOJA_SEGUIMIENTO_ZIKA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if (resultado != null && !resultado.isEmpty()) {

                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    HojaZikaDTO hojaZikaResultado = gson.fromJson(resultadoJson.get(0).toString(), HojaZikaDTO.class);

                    retorno.setObjecRespuesta(hojaZikaResultado);

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
    public ErrorDTO guardarHojaSeguimiento(HojaZikaDTO hojaZika, List<SeguimientoZikaDTO> seguimiento){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_HOJA_SEGUIMIENTO_ZIKA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;


            JSONObject objenvio = new JSONObject();
            objenvio.put("numHojaSeguimiento", hojaZika.getNumHojaSeguimiento());
            objenvio.put("codExpediente", hojaZika.getCodExpediente());
            objenvio.put("fif", hojaZika.getFif());
            objenvio.put("fis", hojaZika.getFis());
            objenvio.put("categoria", hojaZika.getCategoria());
            objenvio.put("sintomaInicial1", hojaZika.getSintomaInicial1());
            objenvio.put("sintomaInicial2", hojaZika.getSintomaInicial2());
            objenvio.put("sintomaInicial3", hojaZika.getSintomaInicial3());
            objenvio.put("sintomaInicial4", hojaZika.getSintomaInicial4());
            if (hojaZika.getFechaCierre() != null)
                objenvio.put("fechaCierre",new SimpleDateFormat("dd/MM/yyyy").format(hojaZika.getFechaCierre().getTime()).toString());
            objenvio.put("cerrado",String.valueOf(hojaZika.getCerrado()));


            String paramJsonArray="";
            if (seguimiento != null) {
                // creando el json array para el seguimiento zika
                JSONArray jsonArray = new JSONArray();

                for (SeguimientoZikaDTO seg : seguimiento) {
                    JSONObject objenvioSeg = new JSONObject();

                    objenvioSeg.put("controlDia", String.valueOf(seg.getControlDia()));
                    objenvioSeg.put("fechaSeguimiento", seg.getFechaSeguimiento());
                    objenvioSeg.put("usuarioMedico", seg.getUsuarioMedico());
                    objenvioSeg.put("supervisor", seg.getSupervisor());
                    objenvioSeg.put("consultaInicial", String.valueOf(seg.getConsultaInicial()));
                    objenvioSeg.put("fiebre", String.valueOf(seg.getFiebre()));
                    objenvioSeg.put("astenia", String.valueOf(seg.getAstenia()));
                    objenvioSeg.put("malEstadoGral", String.valueOf(seg.getMalEstadoGral()));
                    objenvioSeg.put("escalosfrios", String.valueOf(seg.getEscalosfrios()));
                    objenvioSeg.put("convulsiones", String.valueOf(seg.getConvulsiones()));
                    objenvioSeg.put("cefalea", String.valueOf(seg.getCefalea()));
                    objenvioSeg.put("rigidezCuello", String.valueOf(seg.getRigidezCuello()));
                    objenvioSeg.put("dolorRetroocular", String.valueOf(seg.getDolorRetroocular()));
                    objenvioSeg.put("pocoApetito", String.valueOf(seg.getPocoApetito()));
                    objenvioSeg.put("nauseas", String.valueOf(seg.getNauseas()));
                    objenvioSeg.put("vomitos", String.valueOf(seg.getVomitos()));
                    objenvioSeg.put("diarrea", String.valueOf(seg.getDiarrea()));
                    objenvioSeg.put("dolorAbdominalContinuo", String.valueOf(seg.getDolorAbdominalContinuo()));
                    objenvioSeg.put("artralgiaProximal", String.valueOf(seg.getArtralgiaProximal()));
                    objenvioSeg.put("artralgiaDistal", String.valueOf(seg.getArtralgiaDistal()));
                    objenvioSeg.put("mialgia", String.valueOf(seg.getMialgia()));
                    objenvioSeg.put("conjuntivitisNoPurulenta", String.valueOf(seg.getConjuntivitisNoPurulenta()));
                    objenvioSeg.put("edemaArtProxMS", String.valueOf(seg.getEdemaArtProxMS()));
                    objenvioSeg.put("edemaArtDistMS", String.valueOf(seg.getEdemaArtDistMS()));
                    objenvioSeg.put("edemaArtProxMI", String.valueOf(seg.getEdemaArtProxMI()));
                    objenvioSeg.put("edemaArtDistMI", String.valueOf(seg.getEdemaArtDistMI()));
                    objenvioSeg.put("edemaPeriauricular", String.valueOf(seg.getEdemaPeriauricular()));
                    objenvioSeg.put("adenopatiaCervAnt", String.valueOf(seg.getAdenopatiaCervAnt()));
                    objenvioSeg.put("adenopatiaCervPost", String.valueOf(seg.getAdenopatiaCervPost()));
                    objenvioSeg.put("adenopatiaRetroAuricular", String.valueOf(seg.getAdenopatiaRetroAuricular()));
                    objenvioSeg.put("rash", String.valueOf(seg.getRash()));
                    objenvioSeg.put("equimosis", String.valueOf(seg.getEquimosis()));
                    objenvioSeg.put("pruebaTorniquetePos", String.valueOf(seg.getPruebaTorniquetePos()));
                    objenvioSeg.put("epistaxis", String.valueOf(seg.getEpistaxis()));
                    objenvioSeg.put("gingivorragia", String.valueOf(seg.getGingivorragia()));
                    objenvioSeg.put("petequiasEspontaneas", String.valueOf(seg.getPetequiasEspontaneas()));
                    objenvioSeg.put("hematemesis", String.valueOf(seg.getHematemesis()));
                    objenvioSeg.put("melena", String.valueOf(seg.getMelena()));
                    objenvioSeg.put("oftalmoplejia", String.valueOf(seg.getOftalmoplejia()));
                    objenvioSeg.put("dificultadRespiratoria", String.valueOf(seg.getDificultadResp()));
                    objenvioSeg.put("debilidadMuscMS", String.valueOf(seg.getDebilidadMuscMS()));
                    objenvioSeg.put("debilidadMuscMI", String.valueOf(seg.getDebilidadMuscMI()));
                    objenvioSeg.put("parestesiaMS", String.valueOf(seg.getParestesiaMS()));
                    objenvioSeg.put("parestesiaMI", String.valueOf(seg.getParestesiaMI()));
                    objenvioSeg.put("paralisisMuscMS", String.valueOf(seg.getParalisisMuscMS()));
                    objenvioSeg.put("paralisisMuscMI", String.valueOf(seg.getParalisisMuscMI()));
                    objenvioSeg.put("tos", String.valueOf(seg.getTos()));
                    objenvioSeg.put("rinorrea", String.valueOf(seg.getRinorrea()));
                    objenvioSeg.put("dolorGarganta", String.valueOf(seg.getDolorGarganta()));
                    objenvioSeg.put("prurito", String.valueOf(seg.getPrurito()));


                    jsonArray.put(objenvioSeg.toString());
                }

                paramJsonArray=jsonArray.toString();
            }
            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaZika");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            PropertyInfo paramEviar2 = new PropertyInfo();
            paramEviar2.setValue(paramJsonArray);
            paramEviar2.setName("paramSeguimientoZika");
            paramEviar2.setNamespace("");
            paramEviar2.setType(String.class);

            request.addProperty(paramEviar2);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_GUARDAR_HOJA_SEGUIMIENTO_ZIKA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if (resultado != null && !resultado.isEmpty()) {

                JSONObject jObj = new JSONObject(resultado);

                JSONObject mensaje = (JSONObject) jObj.get("mensaje");

                retorno.setCodigoError((long) mensaje.getInt("codigo"));
                retorno.setMensajeError(mensaje.getString("texto"));

                if(mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObj.get("resultado");
                    hojaZika.setNumHojaSeguimiento(((JSONObject) resultadoJson.get(0)).getInt("numHojaSeguimiento"));

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


    public ResultadoListWSDTO<SeguimientoZikaDTO> getListaSeguimientoZika(int numHojaSeguimiento){

        ResultadoListWSDTO<SeguimientoZikaDTO> retorno = new ResultadoListWSDTO<SeguimientoZikaDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_LISTA_SEGUIMIENTO_ZIKA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setOutputSoapObject(request);

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(numHojaSeguimiento);
            paramWS.setName("paramSecHojaZika");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_GET_LISTA_SEGUIMIENTO_ZIKA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray inicioArrayJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    SeguimientoZikaDTO[] arrSeguimientosZika = gson.fromJson(inicioArrayJson.toString(), SeguimientoZikaDTO[].class);

                    ArrayList<SeguimientoZikaDTO> lstSeguimientoZika = new ArrayList<>(Arrays.asList(arrSeguimientosZika));
                    retorno.setLstResultado(lstSeguimientoZika);
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


    public void ImprimirHojaSeguimientoPdf(int numHojaSeguimiento){

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_IMPRIMIROJASEGUIMIENTOZ_PDF);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramWS = new PropertyInfo();
            paramWS.setValue(numHojaSeguimiento);
            paramWS.setName("paramNumHojaSeguimiento");
            paramWS.setNamespace("");
            paramWS.setType(Integer.class);
            request.addProperty(paramWS);
            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_IMPRIMIRHOJASEGUIMIENTOZ_PDF, sobre, this.HEADER_PROPERTY);

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

    public byte[] getSeguimientoZikaPdf(int numHojaSeguimiento){

        byte[] retorno=null;

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_VISUALIZARHOJASEGUIMIENTOZ_PDF);
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
            transporte.call(ACCIOSOAP_VISUALIZARHOJASEGUIMIENTOZ_PDF, sobre, this.HEADER_PROPERTY);
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
