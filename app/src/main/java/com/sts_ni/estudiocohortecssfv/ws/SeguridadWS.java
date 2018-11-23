package com.sts_ni.estudiocohortecssfv.ws;

import android.content.Context;

import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.InfoResultadoWSDTO;
import com.sts_ni.estudiocohortecssfv.dto.InfoSessionWSDTO;
import com.sts_ni.estudiocohortecssfv.wsclass.ArbaloMenuResponse;
import com.sts_ni.estudiocohortecssfv.wsclass.DataNodoItemArray;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Clase para consumir metodos del WebService del modulo de Seguridad.
 */
public class SeguridadWS {

  //  private static String URL = "http://54.183.17.90:8080/seguridadWS/SeguridadWS?wsdl";
    private static String URL = "http://192.168.1.97:8080/seguridadWS/SeguridadWS?wsdl";
 // private static String URL = "http://192.168.1.95:8080/seguridadWS/SeguridadWS?wsdl";
    private static String CODIGO_SISTEMA;

    static {
        try {
            Properties props = new Properties();
            InputStream inputStream = new FileInputStream("/sdcard/cssfv/config/config.properties");
            props.load(inputStream);
            URL = props.getProperty("SEGURIDAD.URLWS");
            CODIGO_SISTEMA = props.getProperty("CODIGO_SISTEMA");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static final String NAMESPACE = "http://webservices.seguridadWS.sts.com.ni/";

    public ArrayList<HeaderProperty> HEADER_PROPERTY;

    private int TIME_OUT;

    private static final String METODO_VERIFICAR_CREDENCIALES = "verificarCredenciales";
    private static final String ACCIONSOAP_VERIFICAR_CREDENCIALES = NAMESPACE + METODO_VERIFICAR_CREDENCIALES;

    private static final String METODO_OBTENER_ARBOL_MENU = "obtenerArbolMenuAutorizado";
    private static final String ACCIONSOAP_OBTENER_ARBOL_MENU = NAMESPACE + METODO_OBTENER_ARBOL_MENU;

    private Context CONTEXT;

    public SeguridadWS(Context context){

        this.CONTEXT = context;

        this.HEADER_PROPERTY = new ArrayList<HeaderProperty>();

        this.HEADER_PROPERTY.add(new HeaderProperty("Connection", "close"));

        this.TIME_OUT = 40000;
    }

    /***
     * Metodo para validar credenciales en el modulo de seguridad
     *
     * @param usuario
     * @param clave
     * @return objeto ErrorDTO
     */
    public ErrorDTO validarCredenciales(String usuario, String  clave, InfoSessionWSDTO infoSession){
        ErrorDTO respuesta = new ErrorDTO();
        try {

            // Setear parámetros de envio WS
            PropertyInfo paramCodigoSistema = new PropertyInfo();
            paramCodigoSistema.setValue(CODIGO_SISTEMA);
            paramCodigoSistema.setName("pCodigoSistema");
            paramCodigoSistema.setNamespace("");
            paramCodigoSistema.setType(String.class);

            PropertyInfo paramUsuario = new PropertyInfo();
            paramUsuario.setValue(usuario);
            paramUsuario.setName("pUsername");
            paramUsuario.setNamespace("");
            paramUsuario.setType(String.class);

            PropertyInfo paramClave = new PropertyInfo();
            paramClave.setValue(clave);
            paramClave.setName("pPass");
            paramClave.setNamespace("");
            paramClave.setType(String.class);

            // Setear datos para el WS
            SoapObject request = new SoapObject(NAMESPACE, METODO_VERIFICAR_CREDENCIALES);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            request.addProperty(paramCodigoSistema);
            request.addProperty(paramUsuario);
            request.addProperty(paramClave);

            sobre.setOutputSoapObject(request);


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIONSOAP_VERIFICAR_CREDENCIALES, sobre, this.HEADER_PROPERTY);


            SoapObject resultadoSoap = (SoapObject) sobre.getResponse();

            if (resultadoSoap != null) {

                InfoResultadoWSDTO resultado = new InfoResultadoWSDTO();

                resultado.setOk((Boolean.valueOf(((SoapPrimitive)resultadoSoap.getProperty("ok")).toString())).booleanValue());
                resultado.setNumeroError(Integer.valueOf(((SoapPrimitive)resultadoSoap.getProperty("numeroError")).toString()));
                resultado.setMensaje(((SoapPrimitive)resultadoSoap.getProperty("mensaje")).toString());
                resultado.setGravedad(Integer.valueOf(((SoapPrimitive)resultadoSoap.getProperty("gravedad")).toString()));

                if(resultado.isOk()){
                    respuesta.setCodigoError((long) 0);
                    respuesta.setMensajeError("");
                    SoapObject objInfo = (SoapObject) resultadoSoap.getProperty("objeto");
                    infoSession.setUserId(Integer.valueOf(((SoapPrimitive)objInfo.getProperty("usuarioId")).toString()));
                    infoSession.setNameUser(((SoapPrimitive) objInfo.getProperty("nombre")).toString());
                    infoSession.setUser(usuario);
                }else{
                    if(resultado.getGravedad() <=2)
                        respuesta.setCodigoError((long) 1);
                    else
                        respuesta.setCodigoError((long) -1);
                    respuesta.setMensajeError(resultado.getMensaje());
                }

            } else {

                respuesta.setCodigoError((long)2);
                respuesta.setMensajeError("No se obtuvo respuesta");

            }

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.setCodigoError(Long.parseLong("2"));
            respuesta.setMensajeError("Servicio no disponible");
        }
        return respuesta;
    }

    public DataNodoItemArray obterMenuRol(int usuarioId){
        DataNodoItemArray nodoItemArray = new DataNodoItemArray();
        ErrorDTO respuestaError = new ErrorDTO();
        try {

            // Setear parámetros de envio WS
            PropertyInfo paramUsuarioId = new PropertyInfo();
            paramUsuarioId.setValue(usuarioId);
            paramUsuarioId.setName("pUsuarioId");
            paramUsuarioId.setNamespace("");
            paramUsuarioId.setType(Integer.class);

            PropertyInfo paramCodigoSistema = new PropertyInfo();
            paramCodigoSistema.setValue(CODIGO_SISTEMA);
            paramCodigoSistema.setName("pCodigoSistema");
            paramCodigoSistema.setNamespace("");
            paramCodigoSistema.setType(String.class);

            // Setear datos para el WS
            SoapObject request = new SoapObject(NAMESPACE, METODO_OBTENER_ARBOL_MENU);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;

            request.addProperty(paramUsuarioId);
            request.addProperty(paramCodigoSistema);

            sobre.setOutputSoapObject(request);
            sobre.addMapping(NAMESPACE, "obtenerArbolMenuAutorizadoResponse", new ArbaloMenuResponse().getClass());


            HttpTransportSE transporte = new HttpTransportSE(URL, this.TIME_OUT);
            transporte.call(ACCIONSOAP_OBTENER_ARBOL_MENU, sobre, this.HEADER_PROPERTY);

            ArbaloMenuResponse resultadoWS = new ArbaloMenuResponse();
            resultadoWS = (ArbaloMenuResponse) sobre.bodyIn;

            if (resultadoWS != null) {

                nodoItemArray = (DataNodoItemArray) resultadoWS.getProperty(0);
                respuestaError.setCodigoError((long) 0);
                respuestaError.setMensajeError("");

            } else {

                respuestaError.setCodigoError((long)2);
                respuestaError.setMensajeError("No se obtuvo respuesta");

            }

        } catch (Exception e) {
            e.printStackTrace();
            respuestaError.setCodigoError(Long.parseLong("2"));
            respuestaError.setMensajeError("Servicio no disponible");
        }
        nodoItemArray.setRespuestaError(respuestaError);
        return nodoItemArray;
    }

}
