package com.sts_ni.estudiocohortecssfv.ws;

import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sts_ni.estudiocohortecssfv.R;
import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.ResultadoObjectWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.VigilanciaIntegradaIragEtiDTO;
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
import java.util.ArrayList;
import java.util.List;

public class VigilanciaIntegradaWS extends EstudioCohorteCssfvWS{
    private int TIME_OUT = 40000;
    private static int TIME_OUT_PDF = 120000;
    public ArrayList<HeaderProperty> HEADER_PROPERTY;
    private Resources RES;

    public  VigilanciaIntegradaWS(Resources res){
        this.HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        this.HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));
        this.RES = res;
    }

    //Funcion de busqueda de paciente con ficha de vigilancia integrada por el codigo expediente
    public ResultadoObjectWSDTO<VigilanciaIntegradaIragEtiDTO> buscarFichaByCodExpediente(String codExpediente, String numHojaConsulta) {

        ResultadoObjectWSDTO<VigilanciaIntegradaIragEtiDTO> retorno = new ResultadoObjectWSDTO<>();
        try {
            SoapObject request = new SoapObject(NAMESPACE, BUSCAR_FICHA_VIGILANCIA_INTEGRADA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(codExpediente);
            paramEviar.setName("codExpediente");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            PropertyInfo paramEviar2 = new PropertyInfo();
            paramEviar2.setValue(numHojaConsulta);
            paramEviar2.setName("numHojaConsulta");
            paramEviar2.setNamespace("");
            paramEviar2.setType(String.class);

            request.addProperty(paramEviar2);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_BUSCAR_FICHA_VIGILANCIA_INTEGRADA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if(!StringUtils.isNullOrEmpty(resultado)){
                JSONObject jObject = new JSONObject(resultado);
                JSONObject mensaje = (JSONObject) jObject.get("mensaje");
                if(mensaje.getInt("codigo") == 0){
                    JSONArray resultadoJson = (JSONArray) jObject.get("resultado");

                    Gson gson = new GsonBuilder().create();

                    VigilanciaIntegradaIragEtiDTO vigilanciaIntegrada = gson.fromJson(resultadoJson.get(0).toString(), VigilanciaIntegradaIragEtiDTO.class);

                    retorno.setObjecRespuesta(vigilanciaIntegrada);

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

    //Funcion para guardar los datos de la ficha vigilancia integrada
    public ErrorDTO guardarFichaVigilanciaIntegrada(VigilanciaIntegradaIragEtiDTO vigilanciaIntegrada){
        ErrorDTO retorno = new ErrorDTO();
        try {
            SoapObject request = new SoapObject(NAMESPACE, METODO_GUARDAR_FICHA_VIGILANCIA_INTEGRADA);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            JSONObject objenvio = new JSONObject();
            objenvio.put("secVigilanciaIntegrada", vigilanciaIntegrada.getSecVigilanciaIntegrada());
            objenvio.put("secHojaConsulta", vigilanciaIntegrada.getSecHojaConsulta());
            objenvio.put("codExpediente", vigilanciaIntegrada.getCodExpediente());
            objenvio.put("departamento", vigilanciaIntegrada.getDepartamento());
            objenvio.put("municipio", vigilanciaIntegrada.getMunicipio());
            objenvio.put("barrio", vigilanciaIntegrada.getBarrio());
            objenvio.put("direccion", vigilanciaIntegrada.getDireccion());
            objenvio.put("telefono", vigilanciaIntegrada.getTelefono());
            objenvio.put("urbano", vigilanciaIntegrada.getUrbano());
            objenvio.put("rural", vigilanciaIntegrada.getRural());
            objenvio.put("emergencia", vigilanciaIntegrada.getEmergencia());
            objenvio.put("emergAmbulatorio", vigilanciaIntegrada.getEmergAmbulatorio());
            objenvio.put("sala", vigilanciaIntegrada.getSala());
            objenvio.put("uci", vigilanciaIntegrada.getUci());
            objenvio.put("diagnostico", vigilanciaIntegrada.getDiagnostico());
            objenvio.put("presentTarjVacuna", vigilanciaIntegrada.getPresentTarjVacuna());
            objenvio.put("antiHib", vigilanciaIntegrada.getAntiHib());
            objenvio.put("antiMeningococica", vigilanciaIntegrada.getAntiMeningococica());
            objenvio.put("antiNeumococica", vigilanciaIntegrada.getAntiNeumococica());
            objenvio.put("antiInfluenza", vigilanciaIntegrada.getAntiInfluenza());
            objenvio.put("pentavalente", vigilanciaIntegrada.getPentavalente());
            objenvio.put("conjugada", vigilanciaIntegrada.getConjugada());
            objenvio.put("polisacarida", vigilanciaIntegrada.getPolisacarida());
            objenvio.put("heptavalente", vigilanciaIntegrada.getHeptavalente());
            objenvio.put("polisacarida23", vigilanciaIntegrada.getPolisacarida23());
            objenvio.put("valente13", vigilanciaIntegrada.getValente13());
            objenvio.put("estacional", vigilanciaIntegrada.getEstacional());
            objenvio.put("h1n1p", vigilanciaIntegrada.getH1n1p());
            objenvio.put("otraVacuna", vigilanciaIntegrada.getOtraVacuna());
            objenvio.put("noDosisAntiHib", vigilanciaIntegrada.getNoDosisAntiHib());
            objenvio.put("noDosisAntiMening", vigilanciaIntegrada.getNoDosisAntiMening());
            objenvio.put("noDosisAntiNeumo", vigilanciaIntegrada.getNoDosisAntiNeumo());
            objenvio.put("noDosisAntiInflu", vigilanciaIntegrada.getNoDosisAntiInflu());
            objenvio.put("fechaUltDosisAntiHib", vigilanciaIntegrada.getFechaUltDosisAntiHib());
            objenvio.put("fechaUltDosisAntiMening", vigilanciaIntegrada.getFechaUltDosisAntiMening());
            objenvio.put("fechaUltDosisAntiNeumo", vigilanciaIntegrada.getFechaUltDosisAntiNeumo());
            objenvio.put("fechaUltDosisAntiInflu", vigilanciaIntegrada.getFechaUltDosisAntiInflu());
            objenvio.put("cancer", vigilanciaIntegrada.getCancer());
            objenvio.put("diabetes", vigilanciaIntegrada.getDiabetes());
            objenvio.put("vih", vigilanciaIntegrada.getVih());
            objenvio.put("otraInmunodeficiencia", vigilanciaIntegrada.getOtraInmunodeficiencia());
            objenvio.put("enfNeurologicaCronica", vigilanciaIntegrada.getEnfNeurologicaCronica());
            objenvio.put("enfCardiaca", vigilanciaIntegrada.getEnfCardiaca());
            objenvio.put("asma", vigilanciaIntegrada.getAsma());
            objenvio.put("epoc", vigilanciaIntegrada.getEpoc());
            objenvio.put("otraEnfPulmonar", vigilanciaIntegrada.getOtraEnfPulmonar());
            objenvio.put("insufRenalCronica", vigilanciaIntegrada.getInsufRenalCronica());
            objenvio.put("desnutricion", vigilanciaIntegrada.getDesnutricion());
            objenvio.put("obesidad", vigilanciaIntegrada.getObesidad());
            objenvio.put("embarazo", vigilanciaIntegrada.getEmbarazo());
            objenvio.put("embarazoSemanas", vigilanciaIntegrada.getEmbarazoSemanas());
            objenvio.put("txCorticosteroide", vigilanciaIntegrada.getTxCorticosteroide());
            objenvio.put("otraCondicion", vigilanciaIntegrada.getOtraCondicion());
            //objenvio.put("sintomaInicial", vigilanciaIntegrada.getSintomaInicial());
            objenvio.put("usoAntibioticoUltimaSemana", vigilanciaIntegrada.getUsoAntibioticoUltimaSemana());
            objenvio.put("cuantosAntibioticosLeDio", vigilanciaIntegrada.getCuantosAntibioticosLeDio());
            objenvio.put("cualesAntibioticosLeDio", vigilanciaIntegrada.getCualesAntibioticosLeDio());
            objenvio.put("cuantosDiasLeDioElUltimoAntibiotico", vigilanciaIntegrada.getCuantosDiasLeDioElUltimoAntibiotico());
            objenvio.put("viaOral", vigilanciaIntegrada.getViaOral());
            objenvio.put("viaParenteral", vigilanciaIntegrada.getViaParenteral());
            objenvio.put("viaAmbas", vigilanciaIntegrada.getViaAmbas());
            objenvio.put("antecedentesUsoAntivirales", vigilanciaIntegrada.getAntecedentesUsoAntivirales());
            objenvio.put("nombreAntiviral", vigilanciaIntegrada.getNombreAntiviral());
            objenvio.put("fecha1raDosis", vigilanciaIntegrada.getFecha1raDosis());
            objenvio.put("fechaUltimaDosis", vigilanciaIntegrada.getFechaUltimaDosis());
            objenvio.put("noDosisAdministrada", vigilanciaIntegrada.getNoDosisAdministrada());
            objenvio.put("nombrePaciente", vigilanciaIntegrada.getNombrePaciente());
            objenvio.put("tutor", vigilanciaIntegrada.getTutor());
            objenvio.put("otraCondPreexistente", vigilanciaIntegrada.getOtraCondPreexistente());
            objenvio.put("numHojaConsulta", vigilanciaIntegrada.getNumHojaConsulta());
            objenvio.put("estornudos", vigilanciaIntegrada.getEstornudos());
            objenvio.put("otraManifestacionClinica", vigilanciaIntegrada.getOtraManifestacionClinica());
            objenvio.put("cualManifestacionClinica", vigilanciaIntegrada.getCualManifestacionClinica());

            //Seteando parametros

            PropertyInfo paramEviar = new PropertyInfo();
            paramEviar.setValue(objenvio.toString());
            paramEviar.setName("paramVigilanciaIntegrada");
            paramEviar.setNamespace("");
            paramEviar.setType(String.class);

            request.addProperty(paramEviar);

            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIOSOAP_METODO_GUARDAR_FICHA_VIGILANCIA_INTEGRADA, sobre,this.HEADER_PROPERTY);
            String resultado = sobre.getResponse().toString();

            if (resultado != null && !resultado.isEmpty()) {

                JSONObject jObj = new JSONObject(resultado);

                JSONObject mensaje = (JSONObject) jObj.get("mensaje");

                retorno.setCodigoError((long) mensaje.getInt("codigo"));
                retorno.setMensajeError(mensaje.getString("texto"));

                if(mensaje.getInt("codigo") == 0) {
                    JSONArray resultadoJson = (JSONArray) jObj.get("resultado");
                    vigilanciaIntegrada.setSecVigilanciaIntegrada(((JSONObject) resultadoJson.get(0)).getInt("secVigilanciaIntegrada"));

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
