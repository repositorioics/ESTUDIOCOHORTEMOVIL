package com.sts_ni.estudiocohortecssfv.dto;

public class ConsultaRespiratorioDTO {
    private String codigoExpedinte;
    private String numHojaConsulta;
    private String estado;
    private String usuarioMedico;
    private String usuarioEnfermeria;
    private String medicoCambioTurno;
    private String secHojaConsulta;

    public String getCodigoExpedinte() {
        return codigoExpedinte;
    }

    public void setCodigoExpedinte(String codigoExpedinte) {
        this.codigoExpedinte = codigoExpedinte;
    }

    public String getNumHojaConsulta() {
        return numHojaConsulta;
    }

    public void setNumHojaConsulta(String numHojaConsulta) {
        this.numHojaConsulta = numHojaConsulta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuarioMedico() {
        return usuarioMedico;
    }

    public void setUsuarioMedico(String usuarioMedico) {
        this.usuarioMedico = usuarioMedico;
    }

    public String getUsuarioEnfermeria() {
        return usuarioEnfermeria;
    }

    public void setUsuarioEnfermeria(String usuarioEnfermeria) {
        this.usuarioEnfermeria = usuarioEnfermeria;
    }

    public String getMedicoCambioTurno() {
        return medicoCambioTurno;
    }

    public void setMedicoCambioTurno(String medicoCambioTurno) {
        this.medicoCambioTurno = medicoCambioTurno;
    }

    public String getSecHojaConsulta() {
        return secHojaConsulta;
    }

    public void setSecHojaConsulta(String secHojaConsulta) {
        this.secHojaConsulta = secHojaConsulta;
    }
}
