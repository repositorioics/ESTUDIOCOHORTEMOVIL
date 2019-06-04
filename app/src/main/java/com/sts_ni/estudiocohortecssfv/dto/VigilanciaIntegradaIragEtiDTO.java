package com.sts_ni.estudiocohortecssfv.dto;

import java.util.Calendar;

public class VigilanciaIntegradaIragEtiDTO {
    private int secVigilanciaIntegrada;
    private int secHojaConsulta;
    private int codExpediente;
    private char irag;
    private char eti;
    private char iragInusitada;
    private String departamento;
    private String municipio;
    private String barrio;
    private String direccion;
    private String telefono;
    private Character urbano;
    private Character rural;
    private Character emergAmbulatorio;
    private Character sala;
    private Character uci;
    private String diagnostico;
    private Character presentTarjVacuna;
    private Character antiHib;
    private Character antiMeningococica;
    private Character antiNeumococica;
    private Character antiInfluenza;
    private Character pentavalente;
    private Character conjugada;
    private Character polisacarida;
    private Character heptavalente;
    private Character polisacarida23;
    private Character valente13;
    private Character estacional;
    private Character h1n1p;
    private Character otraVacuna;
    private Short noDosisAntiHib;
    private Short noDosisAntiMening;
    private Short noDosisAntiNeumo;
    private Short noDosisAntiInflu;
    private String fechaUltDosisAntiHib;
    private String fechaUltDosisAntiMening;
    private String fechaUltDosisAntiNeumo;
    private String fechaUltDosisAntiInflu;
    private Character cancer;
    private Character diabetes;
    private Character vih;
    private Character otraInmunodeficiencia;
    private Character enfNeurologicaCronica;
    private Character enfCardiaca;
    private Character asma;
    private Character epoc;
    private Character otraEnfPulmonar;
    private Character insufRenalCronica;
    private Character desnutricion;
    private Character obesidad;
    private Character embarazo;
    private Short embarazoSemanas;
    private Character txCorticosteroide;
    private Character otraCondicion;
    //private String sintomaInicial;
    private Character usoAntibioticoUltimaSemana;
    private Short cuantosAntibioticosLeDio;
    private String cualesAntibioticosLeDio;
    private Short cuantosDiasLeDioElUltimoAntibiotico;
    private Character viaOral;
    private Character viaParenteral;
    private Character viaAmbas;
    private Character antecedentesUsoAntivirales;
    private String nombreAntiviral;
    private String fecha1raDosis;
    private String fechaUltimaDosis;
    private Short noDosisAdministrada;
    private Character emergencia;
    private String otraCondPreexistente;
    private String fechaCreacion;
    private String nombrePaciente;
    private String tutor;
    private int numHojaConsulta;
    private Character estornudos;
    private Character otraManifestacionClinica;
    private String cualManifestacionClinica;
    private Short usuarioMedico;

    public int getSecVigilanciaIntegrada() {
        return secVigilanciaIntegrada;
    }

    public void setSecVigilanciaIntegrada(int secVigilanciaIntegrada) {
        this.secVigilanciaIntegrada = secVigilanciaIntegrada;
    }

    public int getSecHojaConsulta() {
        return secHojaConsulta;
    }

    public void setSecHojaConsulta(int secHojaConsulta) {
        this.secHojaConsulta = secHojaConsulta;
    }

    public int getCodExpediente() {
        return codExpediente;
    }

    public void setCodExpediente(int codExpediente) {
        this.codExpediente = codExpediente;
    }

    public char getIrag() {
        return irag;
    }

    public void setIrag(char irag) {
        this.irag = irag;
    }

    public char getEti() {
        return eti;
    }

    public void setEti(char eti) {
        this.eti = eti;
    }

    public char getIragInusitada() {
        return iragInusitada;
    }

    public void setIragInusitada(char iragInusitada) {
        this.iragInusitada = iragInusitada;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Character getUrbano() {
        return urbano;
    }

    public void setUrbano(Character urbano) {
        this.urbano = urbano;
    }

    public Character getRural() {
        return rural;
    }

    public void setRural(Character rural) {
        this.rural = rural;
    }

    public Character getEmergAmbulatorio() {
        return emergAmbulatorio;
    }

    public void setEmergAmbulatorio(Character emergAmbulatorio) {
        this.emergAmbulatorio = emergAmbulatorio;
    }

    public Character getSala() {
        return sala;
    }

    public void setSala(Character sala) {
        this.sala = sala;
    }

    public Character getUci() {
        return uci;
    }

    public void setUci(Character uci) {
        this.uci = uci;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public Character getPresentTarjVacuna() {
        return presentTarjVacuna;
    }

    public void setPresentTarjVacuna(Character presentTarjVacuna) {
        this.presentTarjVacuna = presentTarjVacuna;
    }

    public Character getAntiHib() {
        return antiHib;
    }

    public void setAntiHib(Character antiHib) {
        this.antiHib = antiHib;
    }

    public Character getAntiMeningococica() {
        return antiMeningococica;
    }

    public void setAntiMeningococica(Character antiMeningococica) {
        this.antiMeningococica = antiMeningococica;
    }

    public Character getAntiNeumococica() {
        return antiNeumococica;
    }

    public void setAntiNeumococica(Character antiNeumococica) {
        this.antiNeumococica = antiNeumococica;
    }

    public Character getAntiInfluenza() {
        return antiInfluenza;
    }

    public void setAntiInfluenza(Character antiInfluenza) {
        this.antiInfluenza = antiInfluenza;
    }

    public Character getPentavalente() {
        return pentavalente;
    }

    public void setPentavalente(Character pentavalente) {
        this.pentavalente = pentavalente;
    }

    public Character getConjugada() {
        return conjugada;
    }

    public void setConjugada(Character conjugada) {
        this.conjugada = conjugada;
    }

    public Character getPolisacarida() {
        return polisacarida;
    }

    public void setPolisacarida(Character polisacarida) {
        this.polisacarida = polisacarida;
    }

    public Character getHeptavalente() {
        return heptavalente;
    }

    public void setHeptavalente(Character heptavalente) {
        this.heptavalente = heptavalente;
    }

    public Character getPolisacarida23() {
        return polisacarida23;
    }

    public void setPolisacarida23(Character polisacarida23) {
        this.polisacarida23 = polisacarida23;
    }

    public Character getValente13() {
        return valente13;
    }

    public void setValente13(Character valente13) {
        this.valente13 = valente13;
    }

    public Character getEstacional() {
        return estacional;
    }

    public void setEstacional(Character estacional) {
        this.estacional = estacional;
    }

    public Character getH1n1p() {
        return h1n1p;
    }

    public void setH1n1p(Character h1n1p) {
        this.h1n1p = h1n1p;
    }

    public Character getOtraVacuna() {
        return otraVacuna;
    }

    public void setOtraVacuna(Character otraVacuna) {
        this.otraVacuna = otraVacuna;
    }

    public Short getNoDosisAntiHib() {
        return noDosisAntiHib;
    }

    public void setNoDosisAntiHib(Short noDosisAntiHib) {
        this.noDosisAntiHib = noDosisAntiHib;
    }

    public Short getNoDosisAntiMening() {
        return noDosisAntiMening;
    }

    public void setNoDosisAntiMening(Short noDosisAntiMening) {
        this.noDosisAntiMening = noDosisAntiMening;
    }

    public Short getNoDosisAntiNeumo() {
        return noDosisAntiNeumo;
    }

    public void setNoDosisAntiNeumo(Short noDosisAntiNeumo) {
        this.noDosisAntiNeumo = noDosisAntiNeumo;
    }

    public Short getNoDosisAntiInflu() {
        return noDosisAntiInflu;
    }

    public void setNoDosisAntiInflu(Short noDosisAntiInflu) {
        this.noDosisAntiInflu = noDosisAntiInflu;
    }

    public String getFechaUltDosisAntiHib() {
        return fechaUltDosisAntiHib;
    }

    public void setFechaUltDosisAntiHib(String fechaUltDosisAntiHib) {
        this.fechaUltDosisAntiHib = fechaUltDosisAntiHib;
    }

    public String getFechaUltDosisAntiMening() {
        return fechaUltDosisAntiMening;
    }

    public void setFechaUltDosisAntiMening(String fechaUltDosisAntiMening) {
        this.fechaUltDosisAntiMening = fechaUltDosisAntiMening;
    }

    public String getFechaUltDosisAntiNeumo() {
        return fechaUltDosisAntiNeumo;
    }

    public void setFechaUltDosisAntiNeumo(String fechaUltDosisAntiNeumo) {
        this.fechaUltDosisAntiNeumo = fechaUltDosisAntiNeumo;
    }

    public String getFechaUltDosisAntiInflu() {
        return fechaUltDosisAntiInflu;
    }

    public void setFechaUltDosisAntiInflu(String fechaUltDosisAntiInflu) {
        this.fechaUltDosisAntiInflu = fechaUltDosisAntiInflu;
    }

    public Character getCancer() {
        return cancer;
    }

    public void setCancer(Character cancer) {
        this.cancer = cancer;
    }

    public Character getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(Character diabetes) {
        this.diabetes = diabetes;
    }

    public Character getVih() {
        return vih;
    }

    public void setVih(Character vih) {
        this.vih = vih;
    }

    public Character getOtraInmunodeficiencia() {
        return otraInmunodeficiencia;
    }

    public void setOtraInmunodeficiencia(Character otraInmunodeficiencia) {
        this.otraInmunodeficiencia = otraInmunodeficiencia;
    }

    public Character getEnfNeurologicaCronica() {
        return enfNeurologicaCronica;
    }

    public void setEnfNeurologicaCronica(Character enfNeurologicaCronica) {
        this.enfNeurologicaCronica = enfNeurologicaCronica;
    }

    public Character getEnfCardiaca() {
        return enfCardiaca;
    }

    public void setEnfCardiaca(Character enfCardiaca) {
        this.enfCardiaca = enfCardiaca;
    }

    public Character getAsma() {
        return asma;
    }

    public void setAsma(Character asma) {
        this.asma = asma;
    }

    public Character getEpoc() {
        return epoc;
    }

    public void setEpoc(Character epoc) {
        this.epoc = epoc;
    }

    public Character getOtraEnfPulmonar() {
        return otraEnfPulmonar;
    }

    public void setOtraEnfPulmonar(Character otraEnfPulmonar) {
        this.otraEnfPulmonar = otraEnfPulmonar;
    }

    public Character getInsufRenalCronica() {
        return insufRenalCronica;
    }

    public void setInsufRenalCronica(Character insufRenalCronica) {
        this.insufRenalCronica = insufRenalCronica;
    }

    public Character getDesnutricion() {
        return desnutricion;
    }

    public void setDesnutricion(Character desnutricion) {
        this.desnutricion = desnutricion;
    }

    public Character getObesidad() {
        return obesidad;
    }

    public void setObesidad(Character obesidad) {
        this.obesidad = obesidad;
    }

    public Character getEmbarazo() {
        return embarazo;
    }

    public void setEmbarazo(Character embarazo) {
        this.embarazo = embarazo;
    }

    public Short getEmbarazoSemanas() {
        return embarazoSemanas;
    }

    public void setEmbarazoSemanas(Short embarazoSemanas) {
        this.embarazoSemanas = embarazoSemanas;
    }

    public Character getTxCorticosteroide() {
        return txCorticosteroide;
    }

    public void setTxCorticosteroide(Character txCorticosteroide) {
        this.txCorticosteroide = txCorticosteroide;
    }

    public Character getOtraCondicion() {
        return otraCondicion;
    }

    public void setOtraCondicion(Character otraCondicion) {
        this.otraCondicion = otraCondicion;
    }

    /*public String getSintomaInicial() {
        return sintomaInicial;
    }

    public void setSintomaInicial(String sintomaInicial) {
        this.sintomaInicial = sintomaInicial;
    }
*/
    public Character getUsoAntibioticoUltimaSemana() {
        return usoAntibioticoUltimaSemana;
    }

    public void setUsoAntibioticoUltimaSemana(Character usoAntibioticoUltimaSemana) {
        this.usoAntibioticoUltimaSemana = usoAntibioticoUltimaSemana;
    }

    public Short getCuantosAntibioticosLeDio() {
        return cuantosAntibioticosLeDio;
    }

    public void setCuantosAntibioticosLeDio(Short cuantosAntibioticosLeDio) {
        this.cuantosAntibioticosLeDio = cuantosAntibioticosLeDio;
    }

    public String getCualesAntibioticosLeDio() {
        return cualesAntibioticosLeDio;
    }

    public void setCualesAntibioticosLeDio(String cualesAntibioticosLeDio) {
        this.cualesAntibioticosLeDio = cualesAntibioticosLeDio;
    }

    public Short getCuantosDiasLeDioElUltimoAntibiotico() {
        return cuantosDiasLeDioElUltimoAntibiotico;
    }

    public void setCuantosDiasLeDioElUltimoAntibiotico(Short cuantosDiasLeDioElUltimoAntibiotico) {
        this.cuantosDiasLeDioElUltimoAntibiotico = cuantosDiasLeDioElUltimoAntibiotico;
    }

    public Character getViaOral() {
        return viaOral;
    }

    public void setViaOral(Character viaOral) {
        this.viaOral = viaOral;
    }

    public Character getViaParenteral() {
        return viaParenteral;
    }

    public void setViaParenteral(Character viaParenteral) {
        this.viaParenteral = viaParenteral;
    }

    public Character getViaAmbas() {
        return viaAmbas;
    }

    public void setViaAmbas(Character viaAmbas) {
        this.viaAmbas = viaAmbas;
    }

    public Character getAntecedentesUsoAntivirales() {
        return antecedentesUsoAntivirales;
    }

    public void setAntecedentesUsoAntivirales(Character antecedentesUsoAntivirales) {
        this.antecedentesUsoAntivirales = antecedentesUsoAntivirales;
    }

    public String getNombreAntiviral() {
        return nombreAntiviral;
    }

    public void setNombreAntiviral(String nombreAntiviral) {
        this.nombreAntiviral = nombreAntiviral;
    }

    public String getFecha1raDosis() {
        return fecha1raDosis;
    }

    public void setFecha1raDosis(String fecha1raDosis) {
        this.fecha1raDosis = fecha1raDosis;
    }

    public String getFechaUltimaDosis() {
        return fechaUltimaDosis;
    }

    public void setFechaUltimaDosis(String fechaUltimaDosis) {
        this.fechaUltimaDosis = fechaUltimaDosis;
    }

    public Short getNoDosisAdministrada() {
        return noDosisAdministrada;
    }

    public void setNoDosisAdministrada(Short noDosisAdministrada) {
        this.noDosisAdministrada = noDosisAdministrada;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public Character getEmergencia() {
        return emergencia;
    }

    public void setEmergencia(Character emergencia) {
        this.emergencia = emergencia;
    }

    public String getOtraCondPreexistente() {
        return otraCondPreexistente;
    }

    public void setOtraCondPreexistente(String otraCondPreexistente) {
        this.otraCondPreexistente = otraCondPreexistente;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getNumHojaConsulta() {
        return numHojaConsulta;
    }

    public void setNumHojaConsulta(int numHojaConsulta) {
        this.numHojaConsulta = numHojaConsulta;
    }

    public Character getEstornudos() {
        return estornudos;
    }

    public void setEstornudos(Character estornudos) {
        this.estornudos = estornudos;
    }

    public Character getOtraManifestacionClinica() {
        return otraManifestacionClinica;
    }

    public void setOtraManifestacionClinica(Character otraManifestacionClinica) {
        this.otraManifestacionClinica = otraManifestacionClinica;
    }

    public String getCualManifestacionClinica() {
        return cualManifestacionClinica;
    }

    public void setCualManifestacionClinica(String cualManifestacionClinica) {
        this.cualManifestacionClinica = cualManifestacionClinica;
    }

    public Short getUsuarioMedico() {
        return usuarioMedico;
    }

    public void setUsuarioMedico(Short usuarioMedico) {
        this.usuarioMedico = usuarioMedico;
    }
}
