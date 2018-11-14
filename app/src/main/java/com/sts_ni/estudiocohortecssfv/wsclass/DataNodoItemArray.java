package com.sts_ni.estudiocohortecssfv.wsclass;

import com.sts_ni.estudiocohortecssfv.dto.ErrorDTO;
import com.sts_ni.estudiocohortecssfv.dto.NodoItemDTO;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Autor: Ing. Miguel Alejandro Lopez Detrinidad
 * Fecha: 17 Feb 2015
 * Descripcion: Clase para obtener respuesta del metodo que nos envia el menu.
 */
public class DataNodoItemArray extends Vector<NodoItemDTO> implements KvmSerializable {

    private static final long serialVersionUID = -1166006770093411055L;

    private ErrorDTO respuestaError;

    @Override
    public Object getProperty(int index) {
        return this.get(index);
    }

    @Override
    public int getPropertyCount() {
        return this.size();
        //return dataArray.length;
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        info.name = "nodoMenuItems";
        info.type = new NodoItemDTO().getClass();
    }

    @Override
    public String getInnerText() {
        return null;
    }

    @Override
    public void setInnerText(String s) {

    }

    @Override
    public void setProperty(int index, Object value) {
        SoapObject soapObject = new SoapObject();
        soapObject = (SoapObject) value;

        NodoItemDTO nodoItemDTO = new NodoItemDTO();
        nodoItemDTO.setAutorizado(Boolean.valueOf(((SoapPrimitive)soapObject.getProperty("autorizado")).toString()).booleanValue());
        nodoItemDTO.setEtiqueta(((SoapPrimitive) soapObject.getProperty("etiqueta")).toString());
        nodoItemDTO.setOrden(Integer.valueOf(((SoapPrimitive) soapObject.getProperty("orden")).toString()));
        nodoItemDTO.setUrl(((SoapPrimitive)soapObject.getProperty("url")).toString());
        this.add(nodoItemDTO);
    }

    public ErrorDTO getRespuestaError() {
        return respuestaError;
    }

    public void setRespuestaError(ErrorDTO respuestaError) {
        this.respuestaError = respuestaError;
    }
}
