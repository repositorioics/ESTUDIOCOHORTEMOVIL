package com.sts_ni.estudiocohortecssfv.ws;

import android.content.res.Resources;

import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.EghDTO;
import com.sts_ni.estudiocohortecssfv.dto.EgoDTO;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.GeneralesControlCambiosDTO;
import com.sts_ni.estudiocohortecssfv.dto.HojaConsultaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InfluenzaDTO;
import com.sts_ni.estudiocohortecssfv.dto.MalariaResultadoDTO;
import com.sts_ni.estudiocohortecssfv.dto.PerifericoResultadoDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadosExamenesDTO;
import com.sts_ni.estudiocohortecssfv.dto.SerologiaChickDTO;
import com.sts_ni.estudiocohortecssfv.dto.SerologiaDengueDTO;
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
 * Clase para realizar el consumo de los metodos del webservice relacionados al TAB Examenes.
 */
public class ExamenesWS extends EstudioCohorteCssfvWS{

    private Resources RES;

    private int TIME_OUT = 80000;

    public ExamenesWS(Resources res) {

        this.RES = res;

        HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
    }

    public ResultadoObjectWSDTO<CabeceraSintomaDTO> obtenerDatosCabecera(Integer codExpediente, Integer secHojaConsulta){

        ResultadoObjectWSDTO<CabeceraSintomaDTO> retorno = new ResultadoObjectWSDTO<CabeceraSintomaDTO>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_DATOS_CABECERA_EXAMENES);
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
            transporte.call(ACCIOSOAP_GET_DATOS_CABECERA_EXAMENES, sobre, HEADER_PROPERTY);
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
                    /*Nuevo Campo consulta agregado 06/01/2020*/
                    if(StringUtils.isNullOrEmpty(((JSONObject)resultadoJson.get(0)).getString("consulta")) ||
                            ((JSONObject)resultadoJson.get(0)).getString("consulta").compareTo("null") == 0) {
                        retorno.getObjecRespuesta().setConsulta(((JSONObject)resultadoJson.get(0)).getString("consulta"));
                    } else {
                        retorno.getObjecRespuesta().setConsulta(((JSONObject)resultadoJson.get(0)).getString("consulta"));
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

    //=========================================================================

    public ErrorDTO guardarExamenes(HojaConsultaDTO hojaConsulta, GeneralesControlCambiosDTO genCtrlCambios){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_EXAMENES);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("fechaOrdenLaboratorio", hojaConsulta.getFechaOrdenLaboratorio());
            objenvio.put("bhc", hojaConsulta.getBhc());
            objenvio.put("serologiaDengue", hojaConsulta.getSerologiaDengue());
            objenvio.put("serologiaChik", hojaConsulta.getSerologiaChik());
            objenvio.put("gotaGruesa", hojaConsulta.getGotaGruesa());
            objenvio.put("extendidoPeriferico", hojaConsulta.getExtendidoPeriferico());
            objenvio.put("ego", hojaConsulta.getEgo());
            objenvio.put("egh", hojaConsulta.getEgh());
            objenvio.put("citologiaFecal", hojaConsulta.getCitologiaFecal());
            objenvio.put("factorReumatoideo", hojaConsulta.getFactorReumatoideo());
            objenvio.put("albumina", hojaConsulta.getAlbumina());
            objenvio.put("astAlt", hojaConsulta.getAstAlt());
            objenvio.put("bilirrubinas", hojaConsulta.getBilirrubinas());
            objenvio.put("cpk", hojaConsulta.getCpk());
            objenvio.put("colesterol", hojaConsulta.getColesterol());
            objenvio.put("influenza", hojaConsulta.getInfluenza());
            objenvio.put("usuarioMedico", hojaConsulta.getUsuarioMedico());
            objenvio.put("oel", hojaConsulta.getOel());

            if(genCtrlCambios != null) {
                objenvio.put("usuario", genCtrlCambios.getUsuario());
                objenvio.put("controlador", genCtrlCambios.getControlador());
            }else{
                objenvio.put("usuario", "");
                objenvio.put("controlador", "");
            }
            //objenvio.put("otroExamenLab", hojaConsulta.getOtroExamenLab());

            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GUARDAR_EXAMENES, sobre, HEADER_PROPERTY);

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

    //Retornando lista de examenes------------------------------------------------------------------

    public ResultadoObjectWSDTO<ResultadosExamenesDTO> getListaExamenes(Integer secHojaConsulta){

        ResultadoObjectWSDTO<ResultadosExamenesDTO> retorno = new ResultadoObjectWSDTO<ResultadosExamenesDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_EXAMENES);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(secHojaConsulta);
            paramEviar.setName("paramSecHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(Integer.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_EXAMENES, sobre, HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){

                    ResultadosExamenesDTO resultadosExamenes = new ResultadosExamenesDTO();

                    JSONObject resultadoJSON = (JSONObject) jObject.get("resultado");

                    //JSONArray arregloJSON = (JSONArray)resultadoJSON.get("hojaConsulta");
                    JSONArray arregloJSON = (!resultadoJSON.isNull("hojaConsulta")) ? (JSONArray)resultadoJSON.get("hojaConsulta") : new JSONArray();

                    //-------Lista de Examenes------------------------------------------------------
                    for (int i = 0; i < arregloJSON.length(); i++){
                        JSONObject inicioJson  = arregloJSON.getJSONObject(i);
                        HojaConsultaDTO inicio = new HojaConsultaDTO();

                       /* inicio.setGotaGruesa((inicioJson.getString("gotaGruesa")).charAt(0));
                        inicio.setSerologiaDengue((inicioJson.getString("serologiaDengue")).charAt(0));
                        inicio.setSerologiaChik((inicioJson.getString("serologiaChik")).charAt(0));
                        inicio.setExtendidoPeriferico((inicioJson.getString("extendidoPeriferico")).charAt(0));
                        inicio.setEgo((inicioJson.getString("ego")).charAt(0));
                        inicio.setEgh((inicioJson.getString("egh")).charAt(0));
                        inicio.setInfluenza((inicioJson.getString("influenza")).charAt(0));
                        inicio.setNumOrdenLaboratorio(inicioJson.getInt("numOrdenLaboratorio"));*/

                        if(!inicioJson.isNull("gotaGruesa")){
                            inicio.setGotaGruesa((inicioJson.getString("gotaGruesa")).charAt(0));
                        }
                        if(!inicioJson.isNull("serologiaDengue")){
                            inicio.setSerologiaDengue((inicioJson.getString("serologiaDengue")).charAt(0));
                        }
                        if(!inicioJson.isNull("serologiaChik")){
                            inicio.setSerologiaChik((inicioJson.getString("serologiaChik")).charAt(0));
                        }
                        if(!inicioJson.isNull("extendidoPeriferico")){
                            inicio.setExtendidoPeriferico((inicioJson.getString("extendidoPeriferico")).charAt(0));
                        }
                        if(!inicioJson.isNull("ego")){
                            inicio.setEgo((inicioJson.getString("ego")).charAt(0));
                        }
                        if(!inicioJson.isNull("egh")){
                            inicio.setEgh((inicioJson.getString("egh")).charAt(0));
                        }
                        if(!inicioJson.isNull("influenza")){
                            inicio.setInfluenza((inicioJson.getString("influenza")).charAt(0));
                        }
                        if(!inicioJson.isNull("numOrdenLaboratorio")){
                            inicio.setNumOrdenLaboratorio(inicioJson.getInt("numOrdenLaboratorio"));
                        }

                        resultadosExamenes.setHojaConsulta(inicio);

                    }

                    //--------Lista resultados Malaria Gota Gruesa----------------------------------
                    JSONArray arregloMalariaJSON = (!resultadoJSON.isNull("malariaResultado")) ? (JSONArray)resultadoJSON.get("malariaResultado") : new JSONArray();

                    for (int i = 0; i < arregloMalariaJSON.length(); i++) {
                        JSONObject inicioJson = arregloMalariaJSON.getJSONObject(i);
                        MalariaResultadoDTO malariaResultado = new MalariaResultadoDTO();

                        malariaResultado.setSecMalariaResultado(inicioJson.getInt("secMalariaResultado"));
                        malariaResultado.setSecOrdenLaboratorio(inicioJson.getInt("secOrdenLaboratorio"));
                        malariaResultado.setPFalciparum(inicioJson.getString("PFalciparum"));
                        malariaResultado.setPVivax(inicioJson.getString("PVivax"));
                        malariaResultado.setNegativo(inicioJson.getString("negativo"));
                        malariaResultado.setCodigoMuestra(inicioJson.getString("codigoMuestra"));
                        malariaResultado.setUsuarioBioanalista((short)inicioJson.getInt("usuarioBioanalista"));
                        malariaResultado.setHoraReporte((inicioJson.getString("horaReporte")));
                        malariaResultado.setEstado((inicioJson.getString("estado")).charAt(0));

                        resultadosExamenes.setMalariaResultado(malariaResultado);

                    }
                    //---------Lista resultados Serologia Dengue------------------------------------

                    JSONArray arregloDengueJSON = (!resultadoJSON.isNull("serologiaDengue")) ? (JSONArray)resultadoJSON.get("serologiaDengue") : new JSONArray();

                    for (int i = 0; i < arregloDengueJSON.length(); i++) {
                        JSONObject inicioJson = arregloDengueJSON.getJSONObject(i);
                        SerologiaDengueDTO serologiaDengue = new SerologiaDengueDTO();

                        serologiaDengue.setSecSerologiaDengue(inicioJson.getInt("secSerologiaDengue"));
                        serologiaDengue.setSecOrdenLaboratorio(inicioJson.getInt("ordenLaboratorio"));
                        serologiaDengue.setCodigoMuestra(inicioJson.getString("codigoMuestra"));
                        serologiaDengue.setUsuarioBioanalista((short)inicioJson.getInt("usuarioBioanalista"));
                        serologiaDengue.setHoraReporte((inicioJson.getString("horaReporte")));
                        serologiaDengue.setEstado(inicioJson.getString("estado").charAt(0));

                        resultadosExamenes.setSerologiaDengue(serologiaDengue);
                    }
                    //----------Lista resultados Serologia Chick------------------------------------
                    JSONArray arregloChickJSON = (!resultadoJSON.isNull("serologiaChick")) ? (JSONArray)resultadoJSON.get("serologiaChick") : new JSONArray();

                    for (int i = 0; i < arregloChickJSON.length(); i++) {
                        JSONObject inicioJson = arregloChickJSON.getJSONObject(i);
                        SerologiaChickDTO serologiaChick = new SerologiaChickDTO();

                        serologiaChick.setSecChikMuestra(inicioJson.getInt("secChikMuestra"));
                        serologiaChick.setSecOrdenLaboratorio(inicioJson.getInt("ordenLaboratorio"));
                        serologiaChick.setCodigoMuestra(inicioJson.getString("codigoMuestra"));
                        serologiaChick.setUsuarioBioanalista((short)inicioJson.getInt("usuarioBioanalista"));
                        serologiaChick.setHoraReporte((inicioJson.getString("horaReporte")));
                        serologiaChick.setEstado(inicioJson.getString("estado").charAt(0));

                        resultadosExamenes.setSerologiaChick(serologiaChick);

                    }
                    //----------Lista resultados Periferico Resultados------------------------------
                    JSONArray arregloPerifericoJSON = (!resultadoJSON.isNull("perifericoResultado")) ? (JSONArray)resultadoJSON.get("perifericoResultado") : new JSONArray();

                    for (int i = 0; i < arregloPerifericoJSON.length(); i++) {
                        JSONObject inicioJson = arregloPerifericoJSON.getJSONObject(i);
                        PerifericoResultadoDTO perifericoResultado = new PerifericoResultadoDTO();

                        perifericoResultado.setSecPerifericoResultado(inicioJson.getInt("secPerifericoResultado"));
                        perifericoResultado.setSecOrdenLaboratorio(inicioJson.getInt("secOrdenLaboratorio"));
                        perifericoResultado.setAnisocitosis(inicioJson.getString("anisocitosis"));
                        perifericoResultado.setAnisocromia(inicioJson.getString("anisocromia"));
                        perifericoResultado.setPoiquilocitosis(inicioJson.getString("poiquilocitosis"));
                        perifericoResultado.setLinfocitosAtipicos(inicioJson.getString("linfocitosAtipicos"));
                        perifericoResultado.setObservacionSblanca(inicioJson.getString("observacionSblanca"));
                        perifericoResultado.setObservacionPlaqueta(inicioJson.getString("observacionPlaqueta"));
                        perifericoResultado.setCodigoMuestra(inicioJson.getString("codigoMuestra"));
                        perifericoResultado.setUsuarioBioanalista((short)inicioJson.getInt("usuarioBioanalista"));
                        perifericoResultado.setHoraReporte((inicioJson.getString("horaReporte")));
                        perifericoResultado.setEstado(inicioJson.getString("estado").charAt(0));

                        resultadosExamenes.setPerifericoResultado(perifericoResultado);
                    }
                    //----------Lista resultados EGO------------------------------------------------
                    JSONArray arregloEgoJSON = (!resultadoJSON.isNull("egoResultado")) ? (JSONArray)resultadoJSON.get("egoResultado") : new JSONArray();

                    for (int i = 0; i < arregloEgoJSON.length(); i++) {
                        JSONObject inicioJson = arregloEgoJSON.getJSONObject(i);
                        EgoDTO ego = new EgoDTO();

                        ego.setSecEgoResultado(inicioJson.getInt("secEgoResultado"));
                        ego.setSecOrdenLaboratorio(inicioJson.getInt("ordenLaboratorio"));
                        ego.setColor(inicioJson.getString("color"));
                        ego.setAspecto(inicioJson.getString("aspecto"));
                        ego.setSedimento(inicioJson.getString("sedimento"));
                        ego.setDensidad(inicioJson.getString("densidad"));
                        ego.setProteinas(inicioJson.getString("proteinas"));
                        ego.setHomoglobinas(inicioJson.getString("homoglobinas"));
                        ego.setCuerpoCetonico(inicioJson.getString("cuerpoCetonico"));
                        ego.setPh(inicioJson.getString("ph"));
                        ego.setUrobilinogeno(inicioJson.getString("urobilinogeno"));
                        ego.setGlucosa(inicioJson.getString("glucosa"));
                        ego.setBilirrubinas(inicioJson.getString("bilirrubinas"));
                        ego.setNitritos(inicioJson.getString("nitritos"));
                        ego.setCelulasEpiteliales(inicioJson.getString("celulasEpiteliales"));
                        ego.setLeucositos(inicioJson.getString("leucositos"));
                        ego.setEritrocitos(inicioJson.getString("eritrocitos"));
                        ego.setCilindros(inicioJson.getString("cilindros"));
                        ego.setCristales(inicioJson.getString("cristales"));
                        ego.setHilosMucosos(inicioJson.getString("hilosMucosos"));
                        ego.setBacterias(inicioJson.getString("bacterias"));
                        ego.setLevaduras(inicioJson.getString("levaduras"));
                        ego.setObservaciones(inicioJson.getString("observaciones"));
                        ego.setCodigoMuestra(inicioJson.getString("codigoMuestra"));
                        ego.setUsuarioBioanalista((short)inicioJson.getInt("usuarioBioanalista"));
                        ego.setHoraReporte((inicioJson.getString("horaReporte")));
                        ego.setEstado(inicioJson.getString("estado").charAt(0));

                        resultadosExamenes.setEgo(ego);
                    }
                    //-----------Lista Resultados EGH y Citologia Fecal-----------------------------
                    JSONArray arregloEghJSON = (!resultadoJSON.isNull("eghResultado")) ? (JSONArray)resultadoJSON.get("eghResultado") : new JSONArray();

                    for (int i = 0; i < arregloEghJSON.length(); i++) {
                        JSONObject inicioJson = arregloEghJSON.getJSONObject(i);
                        EghDTO egh = new EghDTO();

                        egh.setSecEghResultado(inicioJson.getInt("secEghResultado"));
                        egh.setSecOrdenLaboratorio(inicioJson.getInt("secOrdenLaboratorio"));
                        egh.setColor(inicioJson.getString("color"));
                        egh.setConsistencia(inicioJson.getString("consistencia"));
                        egh.setRestosAlimenticios(inicioJson.getString("restosAlimenticios"));
                        egh.setMucus(inicioJson.getString("mucus"));
                        egh.setPh(inicioJson.getString("ph"));
                        egh.setSangreOculta(inicioJson.getString("sangreOculta"));
                        egh.setBacterias(inicioJson.getString("bacterias"));
                        egh.setLevaduras(inicioJson.getString("levaduras"));
                        egh.setLeucocitos(inicioJson.getString("leucocitos"));
                        egh.setEritrocitos(inicioJson.getString("eritrocitos"));
                        egh.setFilamentosMucosos(inicioJson.getString("filamentosMucosos"));
                        egh.setEColi(inicioJson.getString("EColi"));
                        egh.setEndolimaxNana(inicioJson.getString("endolimaxNana"));
                        egh.setEHistolytica(inicioJson.getString("EHistolytica"));
                        egh.setGardiaAmblia(inicioJson.getString("gardiaAmblia"));
                        egh.setTrichuris(inicioJson.getString("trichuris"));
                        egh.setHymenolepisNana(inicioJson.getString("hymenolepisNana"));
                        egh.setStrongyloideStercolaris(inicioJson.getString("strongyloideStercolaris"));
                        egh.setUnicinarias(inicioJson.getString("unicinarias"));
                        egh.setEnterovirus(inicioJson.getString("enterovirus"));
                        egh.setObservaciones(inicioJson.getString("observaciones"));
                        egh.setCodigoMuestra(inicioJson.getString("codigoMuestra"));
                        egh.setUsuarioBionalista((short)inicioJson.getInt("usuarioBionalista"));
                        egh.setHoraReporte((inicioJson.getString("horaReporte")));
                        egh.setEstado(inicioJson.getString("estado").charAt(0));

                        resultadosExamenes.setEgh(egh);
                    }
                    //---------Lista resultados Influenza---------------------------------------------------------------------

                    JSONArray arregloInfluenzaJSON = (!resultadoJSON.isNull("influenzaMuestra")) ? (JSONArray)resultadoJSON.get("influenzaMuestra") : new JSONArray();

                    for (int i = 0; i < arregloInfluenzaJSON.length(); i++) {
                        JSONObject inicioJson = arregloInfluenzaJSON.getJSONObject(i);
                        InfluenzaDTO influenza = new InfluenzaDTO();

                        influenza.setSecInfluenzaMuestra(inicioJson.getInt("secInfluenzaMuestra"));
                        influenza.setSecOrdenLaboratorio(inicioJson.getInt("ordenLaboratorio"));
                        influenza.setCodigoMuestra(inicioJson.getString("codigoMuestra"));
                        influenza.setUsuarioBioanalista((short) inicioJson.getInt("usuarioBioanalista"));
                        influenza.setHoraReporte((inicioJson.getString("horaReporte")));
                        influenza.setEstado(inicioJson.getString("estado").charAt(0));

                        resultadosExamenes.setInfluenza(influenza);
                    }

                    retorno.setObjecRespuesta(resultadosExamenes);
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

    //Retornando lista de examenes que fueron Chekeados---------------------------------------------

    public ResultadoObjectWSDTO<HojaConsultaDTO> getListaExamenesChekeados(Integer secHojaConsulta){

        ResultadoObjectWSDTO<HojaConsultaDTO> retorno = new ResultadoObjectWSDTO<HojaConsultaDTO>();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GET_EXAMENES_CHEKEADOS);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(secHojaConsulta);
            paramEviar.setName("paramSecHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(Integer.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GET_EXAMENES_CHEKEADOS, sobre, HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject  jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJSON = (JSONArray) jObject.get("resultado");
                    JSONObject resultadoExamen = ((JSONObject)resultadoJSON.get(0));

                    retorno.setObjecRespuesta(new HojaConsultaDTO());
                    if(!resultadoExamen.isNull("bhc")) {
                        retorno.getObjecRespuesta().setBhc(resultadoExamen.getString("bhc").charAt(0));
                    }
                    if(!resultadoExamen.isNull("serologiaDengue")) {
                        retorno.getObjecRespuesta().setSerologiaDengue(resultadoExamen.getString("serologiaDengue").charAt(0));
                    }
                    if(!resultadoExamen.isNull("serologiaChik")){
                        retorno.getObjecRespuesta().setSerologiaChik(resultadoExamen.getString("serologiaChik").charAt(0));
                    }
                    if(!resultadoExamen.isNull("gotaGruesa")){
                        retorno.getObjecRespuesta().setGotaGruesa(resultadoExamen.getString("gotaGruesa").charAt(0));
                    }
                    if(!resultadoExamen.isNull("extendidoPeriferico")){
                        retorno.getObjecRespuesta().setExtendidoPeriferico(resultadoExamen.getString("extendidoPeriferico").charAt(0));
                    }
                    if(!resultadoExamen.isNull("ego")){
                        retorno.getObjecRespuesta().setEgo(resultadoExamen.getString("ego").charAt(0));
                    }
                    if(!resultadoExamen.isNull("egh")){
                        retorno.getObjecRespuesta().setEgh(resultadoExamen.getString("egh").charAt(0));
                    }
                    if(!resultadoExamen.isNull("citologiaFecal")){
                        retorno.getObjecRespuesta().setCitologiaFecal(resultadoExamen.getString("citologiaFecal").charAt(0));
                    }
                    if(!resultadoExamen.isNull("factorReumatoideo")){
                        retorno.getObjecRespuesta().setFactorReumatoideo(resultadoExamen.getString("factorReumatoideo").charAt(0));
                    }
                    if(!resultadoExamen.isNull("albumina")){
                        retorno.getObjecRespuesta().setAlbumina(resultadoExamen.getString("albumina").charAt(0));
                    }
                    if(!resultadoExamen.isNull("astAlt")){
                        retorno.getObjecRespuesta().setAstAlt(resultadoExamen.getString("astAlt").charAt(0));
                    }
                    if(!resultadoExamen.isNull("bilirrubinas")){
                        retorno.getObjecRespuesta().setBilirrubinas(resultadoExamen.getString("bilirrubinas").charAt(0));
                    }
                    if(!resultadoExamen.isNull("cpk")){
                        retorno.getObjecRespuesta().setCpk(resultadoExamen.getString("cpk").charAt(0));
                    }
                    if(!resultadoExamen.isNull("colesterol")){
                        retorno.getObjecRespuesta().setColesterol(resultadoExamen.getString("colesterol").charAt(0));
                    }
                    if(!resultadoExamen.isNull("influenza")){
                        retorno.getObjecRespuesta().setInfluenza(resultadoExamen.getString("influenza").charAt(0));
                    }
                    if(!resultadoExamen.isNull("otroExamenLab")){
                        retorno.getObjecRespuesta().setOtroExamenLab(resultadoExamen.getString("otroExamenLab").toString());
                    }
                    if(!resultadoExamen.isNull("oel")){
                        retorno.getObjecRespuesta().setOel(resultadoExamen.getString("oel").charAt(0));
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
            retorno.setMensajeError(RES.getString(R.string.msj_error_no_controlado));
        }

        return retorno;
    }

    //Funcion para Guardar el Campo Otro Examen
    public ErrorDTO guardarOTroExamen(HojaConsultaDTO hojaConsulta){

        ErrorDTO retorno = new ErrorDTO();

        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_OTRO_EXAMEN);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secHojaConsulta", hojaConsulta.getSecHojaConsulta());
            objenvio.put("otroExamenLab", hojaConsulta.getOtroExamenLab().toString());

            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramHojaConsulta");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_GUARDAR_OTRO_EXAMEN, sobre, HEADER_PROPERTY);

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
