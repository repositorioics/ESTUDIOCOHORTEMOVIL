package com.sts_ni.estudiocohortecssfv.dto;

import java.util.ArrayList;

/**
 * Created by mmoreno on 08/10/2015.
 */
public class GeneralesControlCambiosDTO {

    private int numHojaConsulta;
    private int codExpediente;
    private String usuario;
    private String controlador;
    ArrayList<ControlCambiosDTO> controlCambios;

    public int getNumHojaConsulta() {
        return numHojaConsulta;
    }

    public void setNumHojaConsulta(int numHojaConsulta) {
        this.numHojaConsulta = numHojaConsulta;
    }

    public int getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(int codExpediente) {
        this.codExpediente = codExpediente;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getControlador() {
        return controlador;
    }

    public void setControlador(String controlador) {
        this.controlador = controlador;
    }

    public ArrayList<ControlCambiosDTO> getControlCambios() {
        return controlCambios;
    }

    public void setControlCambios(ArrayList<ControlCambiosDTO> controlCambios) {
        this.controlCambios = controlCambios;
    }
}
