package com.sts_ni.estudiocohortecssfv.dto;

import java.util.Date;

/**
 * Created by flopezcarballo on 24/02/2015.
 */
public class HojaConsultaDTO {

    private int secHojaConsulta;
    private int codExpediente;
    private Date fechaConsulta;
    private int numHojaConsulta;
    private Double pesoKg;
    private Double tallaCm;
    private Double temperaturac;
    private int usuarioEnfermeria;
    private String expedienteFisico;

    private String historiaExamenFisico;
    private String planes;

    private int presion;
    private Short pas;
    private Short pad;
    private int fciaResp;
    private int fciaCard;
    private String lugarAtencion;
    private String consulta;
    private String segChick;
    private String turno;
    private Double temMedc;
    private String fis;
    private String fif;
    private String ultDiaFiebre;
    private String ultDosisAntipiretico;
    private String amPmUltDiaFiebre;
    private String horaUltDosisAntipiretico;
    private String amPmUltDosisAntipiretico;
    private Character fiebre;
    private Character astenia;
    private Character asomnoliento;
    private Character malEstado;
    private Character perdidaConsciencia;
    private Character inquieto;
    private Character convulsiones;
    private Character hipotermia;
    private Character letargia;
    private Character pocoApetito;
    private Character nausea;
    private Character dificultadAlimentarse;
    private Character vomito12horas;
    Short vomito12h;
    private Character diarrea;
    private Character diarreaSangre;
    private Character estrenimiento;
    private Character dolorAbIntermitente;
    private Character dolorAbContinuo;
    private Character epigastralgia;
    private Character intoleranciaOral;
    private Character distensionAbdominal;
    private Character hepatomegalia;
    private Double    hepatomegaliaCm;
    private Character altralgia;
    private Character mialgia;
    private Character lumbalgia;
    private Character dolorCuello;
    private Character tenosinovitis;
    private Character artralgiaProximal;
    private Character artralgiaDistal;
    private Character conjuntivitis;
    private Character edemaMunecas;
    private Character edemaCodos;
    private Character edemaHombros;
    private Character edemaRodillas;
    private Character edemaTobillos;
    private Character cefalea;
    private Character rigidezCuello;
    private Character inyeccionConjuntival;
    private Character hemorragiaSuconjuntival;
    private Character dolorRetroocular;
    private Character fontanelaAbombada;
    private Character ictericiaConuntival;
    private Character lenguaMucosasSecas;
    private Character pliegueCutaneo;
    private Character orinaReducida;
    private Character bebeConSed;
    private String ojosHundidos;
    private Character fontanelaHundida;
    private Character rahsLocalizado;
    private Character rahsGeneralizado;
    private Character rashEritematoso;
    private Character rahsMacular;
    private Character rashPapular;
    private Character rahsMoteada;
    private Character ruborFacial;
    private Character equimosis;
    private Character cianosisCentral;
    private Character ictericia;
    private Character eritema;
    private Character dolorGarganta;
    private Character adenopatiasCervicales;
    private Character exudado;
    private Character petequiasMucosa;
    private Character sintomasUrinarios;
    private Character leucocituria;
    private Character nitritos;
    private Character eritrocitos;
    private Character bilirrubinuria;
    private Character obeso;
    private Character sobrepeso;
    private Character sospechaProblema;
    private Character normal;
    private Character bajoPeso;
    private Character bajoPesoSevero;
    private Character tos;
    private Character rinorrea;
    private Character congestionNasal;
    private Character otalgia;
    private Character aleteoNasal;
    private Character apnea;
    private Character respiracionRapida;
    private Character quejidoEspiratorio;
    private Character estiradorReposo;
    private Character tirajeSubcostal;
    private Character sibilancias;
    private Character crepitos;
    private Character roncos;
    private Character otraFif;
    private String nuevaFif;
    private Character interconsultaPediatrica;
    private Character referenciaHospital;
    private Character referenciaDengue;
    private Character referenciaIrag;
    private Character referenciaChik;
    private Character eti;
    private Character irag;
    private Character neumonia;
    private Character lactanciaMaterna;
    private Character vacunasCompletas;
    private Character vacunaInfluenza;
    private String fechaVacuna;
    private Integer saturaciono2;
    private Double imc;
    private String categoria;
    private Character cambioCategoria;
    private Character manifestacionHemorragica;
    private Character pruebaTorniquetePositiva;
    private Character petequia10Pt;
    private Character petequia20Pt;
    private Character pielExtremidadesFrias;
    private Character palidezEnExtremidades;
    private Character epistaxis;
    private Character gingivorragia;
    private Character petequiasEspontaneas;
    private Character llenadoCapilar2seg;
    private Character cianosis;
    private Double linfocitosaAtipicos;
    private String fechaLinfocitos;
    private Character hipermenorrea;
    private Character hematemesis;
    private Character melena;
    private Character hemoconc;
    private Short hemoconcentracion;
    private Character hospitalizado;
    private String hospitalizadoEspecificar;
    private Character transfusionSangre;
    private String transfusionEspecificar;
    private Character tomandoMedicamento;
    private String medicamentoEspecificar;
    private Character medicamentoDistinto;
    private String medicamentoDistEspecificar;
    private String horasv;


    private  Character Acetaminofen;

    private  Character asa;
    private  Character ibuprofen;
    private  Character penicilina;
    private  Character amoxicilina;
    private  Character dicloxacilina;
    private  String otroAntibiotico;
    private  Character furazolidona;
    private  Character metronidazolTinidazol;
    private  Character albendazolMebendazol;
    private  Character sulfatoFerroso;
    private  Character sueroOral;
    private  Character sulfatoZinc;
    private  Character liquidosIv;
    private  Character prednisona;
    private  Character hidrocortisonaIv;
    private  Character salbutamol;
    private  Character oseltamivir;

    private Long telef;
    private String proximaCita;
    private String horarioClases;
    private String colegio;

    private Character bhc;
    private Character serologiaDengue;
    private Character serologiaChik;
    private Character gotaGruesa;
    private Character extendidoPeriferico;
    private Character ego;
    private Character egh;
    private Character citologiaFecal;
    private Character factorReumatoideo;
    private Character albumina;
    private Character astAlt;
    private Character bilirrubinas;
    private Character cpk;
    private Character colesterol;
    private Character influenza;
    private String otroExamenLab;
    private Character oel;
    private Integer numOrdenLaboratorio;
    private String fechaOrdenLaboratorio;

    private short diagnostico1;
    private short diagnostico2;
    private short diagnostico3;
    private short diagnostico4;
    private String otroDiagnostico;
    private String fechaCierre;
    private Short usuarioMedico;
    private Short medicoCambioTurno;

    private char estado;

    private Character otro;

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

    public Date getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(Date fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public int getNumHojaConsulta() {
        return numHojaConsulta;
    }

    public void setNumHojaConsulta(int numHojaConsulta) {
        this.numHojaConsulta = numHojaConsulta;
    }

    public Double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(Double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public Double getTallaCm() {
        return tallaCm;
    }

    public void setTallaCm(Double tallaCm) {
        this.tallaCm = tallaCm;
    }

    public Double getTemperaturac() {
        return temperaturac;
    }

    public void setTemperaturac(Double temperaturac) {
        this.temperaturac = temperaturac;
    }

    public int getUsuarioEnfermeria() {
        return usuarioEnfermeria;
    }

    public void setUsuarioEnfermeria(int usuarioEnfermeria) {
        this.usuarioEnfermeria = usuarioEnfermeria;
    }

    public String getExpedienteFisico() {
        return expedienteFisico;
    }

    public void setExpedienteFisico(String expedienteFisico) {
        this.expedienteFisico = expedienteFisico;
    }


    public String getHistoriaExamenFisico() {
        return historiaExamenFisico;
    }

    public void setHistoriaExamenFisico(String historiaExamenFisico) {
        this.historiaExamenFisico = historiaExamenFisico;
    }

    public String getPlanes() {
        return planes;
    }

    public void setPlanes(String planes) {
        this.planes = planes;
    }


    public Character getAcetaminofen() {
        return Acetaminofen;
    }

    public void setAcetaminofen(Character acetaminofen) {
        Acetaminofen = acetaminofen;
    }


    public Character getIbuprofen() {
        return ibuprofen;
    }

    public void setIbuprofen(Character ibuprofen) {
        this.ibuprofen = ibuprofen;
    }

    public Character getPenicilina() {
        return penicilina;
    }

    public void setPenicilina(Character penicilina) {
        this.penicilina = penicilina;
    }

    public Character getAmoxicilina() {
        return amoxicilina;
    }

    public void setAmoxicilina(Character amoxicilina) {
        this.amoxicilina = amoxicilina;
    }

    public Character getDicloxacilina() {
        return dicloxacilina;
    }

    public void setDicloxacilina(Character dicloxacilina) {
        this.dicloxacilina = dicloxacilina;
    }

    public String getOtroAntibiotico() {
        return otroAntibiotico;
    }

    public void setOtroAntibiotico(String otroAntibiotico) {
        this.otroAntibiotico = otroAntibiotico;
    }
    public Character getAsa() {
        return asa;
    }

    public void setAsa(Character asa) {
        this.asa = asa;
    }
    public Character getFurazolidona() {
        return furazolidona;
    }

    public void setFurazolidona(Character furazolidona) {
        this.furazolidona = furazolidona;
    }

    public Character getMetronidazolTinidazol() {
        return metronidazolTinidazol;
    }

    public void setMetronidazolTinidazol(Character metronidazolTinidazol) {
        this.metronidazolTinidazol = metronidazolTinidazol;
    }

    public Character getAlbendazolMebendazol() {
        return albendazolMebendazol;
    }

    public void setAlbendazolMebendazol(Character albendazolMebendazol) {
        this.albendazolMebendazol = albendazolMebendazol;
    }

    public Character getSulfatoFerroso() {
        return sulfatoFerroso;
    }

    public void setSulfatoFerroso(Character sulfatoFerroso) {
        this.sulfatoFerroso = sulfatoFerroso;
    }

    public Character getSueroOral() {
        return sueroOral;
    }

    public void setSueroOral(Character sueroOral) {
        this.sueroOral = sueroOral;
    }

    public Character getSulfatoZinc() {
        return sulfatoZinc;
    }

    public void setSulfatoZinc(Character sulfatoZinc) {
        this.sulfatoZinc = sulfatoZinc;
    }

    public Character getLiquidosIv() {
        return liquidosIv;
    }

    public void setLiquidosIv(Character liquidosIv) {
        this.liquidosIv = liquidosIv;
    }

    public Character getPrednisona() {
        return prednisona;
    }

    public void setPrednisona(Character prednisona) {
        this.prednisona = prednisona;
    }

    public Character getHidrocortisonaIv() {
        return hidrocortisonaIv;
    }

    public void setHidrocortisonaIv(Character hidrocortisonaIv) {
        this.hidrocortisonaIv = hidrocortisonaIv;
    }

    public Character getSalbutamol() {
        return salbutamol;
    }

    public void setSalbutamol(Character salbutamol) {
        this.salbutamol = salbutamol;
    }

    public Character getOseltamivir() {
        return oseltamivir;
    }

    public void setOseltamivir(Character oseltamivir) {
        this.oseltamivir = oseltamivir;
    }

    public Long getTelef() {
        return telef;
    }

    public void setTelef(Long telef) {
        this.telef = telef;
    }

    public String getProximaCita() {
        return proximaCita;
    }

    public void setProximaCita(String proximaCita) {
        this.proximaCita = proximaCita;
    }

    public String getHorarioClases() {
        return horarioClases;
    }

    public void setHorarioClases(String horarioClases) {
        this.horarioClases = horarioClases;
    }

    public String getColegio() {
        return colegio;
    }

    public void setColegio(String colegio) {
        this.colegio = colegio;
    }

    public int getPresion() {
        return presion;
    }

    public void setPresion(int presion) {
        this.presion = presion;
    }

    public Short getPas() {
        return pas;
    }

    public void setPas(Short pas) {
        this.pas = pas;
    }

    public Short getPad() {
        return pad;
    }

    public void setPad(Short pad) {
        this.pad = pad;
    }

    public int getFciaResp() {
        return fciaResp;
    }

    public void setFciaResp(int fciaResp) {
        this.fciaResp = fciaResp;
    }

    public int getFciaCard() {
        return fciaCard;
    }

    public void setFciaCard(int fciaCard) {
        this.fciaCard = fciaCard;
    }

    public String getLugarAtencion() {
        return lugarAtencion;
    }

    public void setLugarAtencion(String lugarAtencion) {
        this.lugarAtencion = lugarAtencion;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String getSegChick() {
        return segChick;
    }

    public void setSegChick(String segChick) {
        this.segChick = segChick;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public Double getTemMedc() {
        return temMedc;
    }

    public void setTemMedc(Double temMedc) {
        this.temMedc = temMedc;
    }

    public String getFis() {
        return fis;
    }

    public void setFis(String fis) {
        this.fis = fis;
    }

    public String getFif() {
        return fif;
    }

    public void setFif(String fif) {
        this.fif = fif;
    }

    public String getUltDiaFiebre() {
        return ultDiaFiebre;
    }

    public void setUltDiaFiebre(String ultDiaFiebre) {
        this.ultDiaFiebre = ultDiaFiebre;
    }

    public String getUltDosisAntipiretico() {
        return ultDosisAntipiretico;
    }

    public void setUltDosisAntipiretico(String ultDosisAntipiretico) {
        this.ultDosisAntipiretico = ultDosisAntipiretico;
    }

    public String getAmPmUltDiaFiebre() {
        return amPmUltDiaFiebre;
    }

    public void setAmPmUltDiaFiebre(String amPmUltDiaFiebre) {
        this.amPmUltDiaFiebre = amPmUltDiaFiebre;
    }

    public String getHoraUltDosisAntipiretico() {
        return horaUltDosisAntipiretico;
    }

    public void setHoraUltDosisAntipiretico(String horaUltDosisAntipiretico) {
        this.horaUltDosisAntipiretico = horaUltDosisAntipiretico;
    }

    public String getAmPmUltDosisAntipiretico() {
        return amPmUltDosisAntipiretico;
    }

    public void setAmPmUltDosisAntipiretico(String amPmUltDosisAntipiretico) {
        this.amPmUltDosisAntipiretico = amPmUltDosisAntipiretico;
    }

    public Character getFiebre() {
        return fiebre;
    }

    public void setFiebre(Character fiebre) {
        this.fiebre = fiebre;
    }

    public Character getAstenia() {
        return astenia;
    }

    public void setAstenia(Character astenia) {
        this.astenia = astenia;
    }

    public Character getAsomnoliento() {
        return asomnoliento;
    }

    public void setAsomnoliento(Character asomnoliento) {
        this.asomnoliento = asomnoliento;
    }

    public Character getMalEstado() {
        return malEstado;
    }

    public void setMalEstado(Character malEstado) {
        this.malEstado = malEstado;
    }

    public Character getPerdidaConsciencia() {
        return perdidaConsciencia;
    }

    public void setPerdidaConsciencia(Character perdidaConsciencia) {
        this.perdidaConsciencia = perdidaConsciencia;
    }

    public Character getInquieto() {
        return inquieto;
    }

    public void setInquieto(Character inquieto) {
        this.inquieto = inquieto;
    }

    public Character getConvulsiones() {
        return convulsiones;
    }

    public void setConvulsiones(Character convulsiones) {
        this.convulsiones = convulsiones;
    }

    public Character getHipotermia() {
        return hipotermia;
    }

    public void setHipotermia(Character hipotermia) {
        this.hipotermia = hipotermia;
    }

    public Character getLetargia() {
        return letargia;
    }

    public void setLetargia(Character letargia) {
        this.letargia = letargia;
    }

    public Character getPocoApetito() {
        return pocoApetito;
    }

    public void setPocoApetito(Character pocoApetito) {
        this.pocoApetito = pocoApetito;
    }

    public Character getNausea() {
        return nausea;
    }

    public void setNausea(Character nausea) {
        this.nausea = nausea;
    }

    public Character getDificultadAlimentarse() {
        return dificultadAlimentarse;
    }

    public void setDificultadAlimentarse(Character dificultadAlimentarse) {
        this.dificultadAlimentarse = dificultadAlimentarse;
    }

    public Character getVomito12horas() {
        return vomito12horas;
    }

    public void setVomito12horas(Character vomito12horas) {
        this.vomito12horas = vomito12horas;
    }

    public Short getVomito12h() {
        return vomito12h;
    }

    public void setVomito12h(Short vomito12h) {
        this.vomito12h = vomito12h;
    }

    public Character getDiarrea() {
        return diarrea;
    }

    public void setDiarrea(Character diarrea) {
        this.diarrea = diarrea;
    }

    public Character getDiarreaSangre() {
        return diarreaSangre;
    }

    public void setDiarreaSangre(Character diarreaSangre) {
        this.diarreaSangre = diarreaSangre;
    }

    public Character getEstrenimiento() {
        return estrenimiento;
    }

    public void setEstrenimiento(Character estrenimiento) {
        this.estrenimiento = estrenimiento;
    }

    public Character getDolorAbIntermitente() {
        return dolorAbIntermitente;
    }

    public void setDolorAbIntermitente(Character dolorAbIntermitente) {
        this.dolorAbIntermitente = dolorAbIntermitente;
    }

    public Character getDolorAbContinuo() {
        return dolorAbContinuo;
    }

    public void setDolorAbContinuo(Character dolorAbContinuo) {
        this.dolorAbContinuo = dolorAbContinuo;
    }

    public Character getEpigastralgia() {
        return epigastralgia;
    }

    public void setEpigastralgia(Character epigastralgia) {
        this.epigastralgia = epigastralgia;
    }

    public Character getIntoleranciaOral() {
        return intoleranciaOral;
    }

    public void setIntoleranciaOral(Character intoleranciaOral) {
        this.intoleranciaOral = intoleranciaOral;
    }

    public Character getDistensionAbdominal() {
        return distensionAbdominal;
    }

    public void setDistensionAbdominal(Character distensionAbdominal) {
        this.distensionAbdominal = distensionAbdominal;
    }

    public Character getHepatomegalia() {
        return hepatomegalia;
    }

    public void setHepatomegalia(Character hepatomegalia) {
        this.hepatomegalia = hepatomegalia;
    }

    public Double getHepatomegaliaCm() {
        return hepatomegaliaCm;
    }

    public void setHepatomegaliaCm(Double hepatomegaliaCm) {
        this.hepatomegaliaCm = hepatomegaliaCm;
    }

    public Character getAltralgia() {
        return altralgia;
    }

    public void setAltralgia(Character altralgia) {
        this.altralgia = altralgia;
    }

    public Character getMialgia() {
        return mialgia;
    }

    public void setMialgia(Character mialgia) {
        this.mialgia = mialgia;
    }

    public Character getLumbalgia() {
        return lumbalgia;
    }

    public void setLumbalgia(Character lumbalgia) {
        this.lumbalgia = lumbalgia;
    }

    public Character getDolorCuello() {
        return dolorCuello;
    }

    public void setDolorCuello(Character dolorCuello) {
        this.dolorCuello = dolorCuello;
    }

    public Character getTenosinovitis() {
        return tenosinovitis;
    }

    public void setTenosinovitis(Character tenosinovitis) {
        this.tenosinovitis = tenosinovitis;
    }

    public Character getArtralgiaProximal() {
        return artralgiaProximal;
    }

    public void setArtralgiaProximal(Character artralgiaProximal) {
        this.artralgiaProximal = artralgiaProximal;
    }

    public Character getArtralgiaDistal() {
        return artralgiaDistal;
    }

    public void setArtralgiaDistal(Character artralgiaDistal) {
        this.artralgiaDistal = artralgiaDistal;
    }

    public Character getConjuntivitis() {
        return conjuntivitis;
    }

    public void setConjuntivitis(Character conjuntivitis) {
        this.conjuntivitis = conjuntivitis;
    }

    public Character getEdemaMunecas() {
        return edemaMunecas;
    }

    public void setEdemaMunecas(Character edemaMunecas) {
        this.edemaMunecas = edemaMunecas;
    }

    public Character getEdemaCodos() {
        return edemaCodos;
    }

    public void setEdemaCodos(Character edemaCodos) {
        this.edemaCodos = edemaCodos;
    }

    public Character getEdemaHombros() {
        return edemaHombros;
    }

    public void setEdemaHombros(Character edemaHombros) {
        this.edemaHombros = edemaHombros;
    }

    public Character getEdemaRodillas() {
        return edemaRodillas;
    }

    public void setEdemaRodillas(Character edemaRodillas) {
        this.edemaRodillas = edemaRodillas;
    }

    public Character getEdemaTobillos() {
        return edemaTobillos;
    }

    public void setEdemaTobillos(Character edemaTobillos) {
        this.edemaTobillos = edemaTobillos;
    }

    public Character getCefalea() {
        return cefalea;
    }

    public void setCefalea(Character cefalea) {
        this.cefalea = cefalea;
    }

    public Character getRigidezCuello() {
        return rigidezCuello;
    }

    public void setRigidezCuello(Character rigidezCuello) {
        this.rigidezCuello = rigidezCuello;
    }

    public Character getInyeccionConjuntival() {
        return inyeccionConjuntival;
    }

    public void setInyeccionConjuntival(Character inyeccionConjuntival) {
        this.inyeccionConjuntival = inyeccionConjuntival;
    }

    public Character getHemorragiaSuconjuntival() {
        return hemorragiaSuconjuntival;
    }

    public void setHemorragiaSuconjuntival(Character hemorragiaSuconjuntival) {
        this.hemorragiaSuconjuntival = hemorragiaSuconjuntival;
    }

    public Character getDolorRetroocular() {
        return dolorRetroocular;
    }

    public void setDolorRetroocular(Character dolorRetroocular) {
        this.dolorRetroocular = dolorRetroocular;
    }

    public Character getFontanelaAbombada() {
        return fontanelaAbombada;
    }

    public void setFontanelaAbombada(Character fontanelaAbombada) {
        this.fontanelaAbombada = fontanelaAbombada;
    }

    public Character getIctericiaConuntival() {
        return ictericiaConuntival;
    }

    public void setIctericiaConuntival(Character ictericiaConuntival) {
        this.ictericiaConuntival = ictericiaConuntival;
    }

    public Character getLenguaMucosasSecas() {
        return lenguaMucosasSecas;
    }

    public void setLenguaMucosasSecas(Character lenguaMucosasSecas) {
        this.lenguaMucosasSecas = lenguaMucosasSecas;
    }

    public Character getPliegueCutaneo() {
        return pliegueCutaneo;
    }

    public void setPliegueCutaneo(Character pliegueCutaneo) {
        this.pliegueCutaneo = pliegueCutaneo;
    }

    public Character getOrinaReducida() {
        return orinaReducida;
    }

    public void setOrinaReducida(Character orinaReducida) {
        this.orinaReducida = orinaReducida;
    }

    public Character getBebeConSed() {
        return bebeConSed;
    }

    public void setBebeConSed(Character bebeConSed) {
        this.bebeConSed = bebeConSed;
    }

    public String getOjosHundidos() {
        return ojosHundidos;
    }

    public void setOjosHundidos(String ojosHundidos) {
        this.ojosHundidos = ojosHundidos;
    }

    public Character getFontanelaHundida() {
        return fontanelaHundida;
    }

    public void setFontanelaHundida(Character fontanelaHundida) {
        this.fontanelaHundida = fontanelaHundida;
    }

    public Character getRahsLocalizado() {
        return rahsLocalizado;
    }

    public void setRahsLocalizado(Character rahsLocalizado) {
        this.rahsLocalizado = rahsLocalizado;
    }

    public Character getRahsGeneralizado() {
        return rahsGeneralizado;
    }

    public void setRahsGeneralizado(Character rahsGeneralizado) {
        this.rahsGeneralizado = rahsGeneralizado;
    }

    public Character getRashEritematoso() {
        return rashEritematoso;
    }

    public void setRashEritematoso(Character rashEritematoso) {
        this.rashEritematoso = rashEritematoso;
    }

    public Character getRahsMacular() {
        return rahsMacular;
    }

    public void setRahsMacular(Character rahsMacular) {
        this.rahsMacular = rahsMacular;
    }

    public Character getRashPapular() {
        return rashPapular;
    }

    public void setRashPapular(Character rashPapular) {
        this.rashPapular = rashPapular;
    }

    public Character getRahsMoteada() {
        return rahsMoteada;
    }

    public void setRahsMoteada(Character rahsMoteada) {
        this.rahsMoteada = rahsMoteada;
    }

    public Character getRuborFacial() {
        return ruborFacial;
    }

    public void setRuborFacial(Character ruborFacial) {
        this.ruborFacial = ruborFacial;
    }

    public Character getEquimosis() {
        return equimosis;
    }

    public void setEquimosis(Character equimosis) {
        this.equimosis = equimosis;
    }

    public Character getCianosisCentral() {
        return cianosisCentral;
    }

    public void setCianosisCentral(Character cianosisCentral) {
        this.cianosisCentral = cianosisCentral;
    }

    public Character getIctericia() {
        return ictericia;
    }

    public void setIctericia(Character ictericia) {
        this.ictericia = ictericia;
    }

    public Character getEritema() {
        return eritema;
    }

    public void setEritema(Character eritema) {
        this.eritema = eritema;
    }

    public Character getDolorGarganta() {
        return dolorGarganta;
    }

    public void setDolorGarganta(Character dolorGarganta) {
        this.dolorGarganta = dolorGarganta;
    }

    public Character getAdenopatiasCervicales() {
        return adenopatiasCervicales;
    }

    public void setAdenopatiasCervicales(Character adenopatiasCervicales) {
        this.adenopatiasCervicales = adenopatiasCervicales;
    }

    public Character getExudado() {
        return exudado;
    }

    public void setExudado(Character exudado) {
        this.exudado = exudado;
    }

    public Character getPetequiasMucosa() {
        return petequiasMucosa;
    }

    public void setPetequiasMucosa(Character petequiasMucosa) {
        this.petequiasMucosa = petequiasMucosa;
    }

    public Character getSintomasUrinarios() {
        return sintomasUrinarios;
    }

    public void setSintomasUrinarios(Character sintomasUrinarios) {
        this.sintomasUrinarios = sintomasUrinarios;
    }

    public Character getLeucocituria() {
        return leucocituria;
    }

    public void setLeucocituria(Character leucocituria) {
        this.leucocituria = leucocituria;
    }

    public Character getEritrocitos() {
        return eritrocitos;
    }

    public void setEritrocitos(Character eritrocitos) {
        this.eritrocitos = eritrocitos;
    }

    public Character getNitritos() {
        return nitritos;
    }

    public void setNitritos(Character nitritos) {
        this.nitritos = nitritos;
    }

    public Character getBilirrubinuria() {
        return bilirrubinuria;
    }

    public void setBilirrubinuria(Character bilirrubinuria) {
        this.bilirrubinuria = bilirrubinuria;
    }

    public Character getObeso() {
        return obeso;
    }

    public void setObeso(Character obeso) {
        this.obeso = obeso;
    }

    public Character getSobrepeso() {
        return sobrepeso;
    }

    public void setSobrepeso(Character sobrepeso) {
        this.sobrepeso = sobrepeso;
    }

    public Character getSospechaProblema() {
        return sospechaProblema;
    }

    public void setSospechaProblema(Character sospechaProblema) {
        this.sospechaProblema = sospechaProblema;
    }

    public Character getNormal() {
        return normal;
    }

    public void setNormal(Character normal) {
        this.normal = normal;
    }

    public Character getBajoPeso() {
        return bajoPeso;
    }

    public void setBajoPeso(Character bajoPeso) {
        this.bajoPeso = bajoPeso;
    }

    public Character getBajoPesoSevero() {
        return bajoPesoSevero;
    }

    public void setBajoPesoSevero(Character bajoPesoSevero) {
        this.bajoPesoSevero = bajoPesoSevero;
    }

    public Character getTos() {
        return tos;
    }

    public void setTos(Character tos) {
        this.tos = tos;
    }

    public Character getRinorrea() {
        return rinorrea;
    }

    public void setRinorrea(Character rinorrea) {
        this.rinorrea = rinorrea;
    }

    public Character getCongestionNasal() {
        return congestionNasal;
    }

    public void setCongestionNasal(Character congestionNasal) {
        this.congestionNasal = congestionNasal;
    }

    public Character getOtalgia() {
        return otalgia;
    }

    public void setOtalgia(Character otalgia) {
        this.otalgia = otalgia;
    }

    public Character getAleteoNasal() {
        return aleteoNasal;
    }

    public void setAleteoNasal(Character aleteoNasal) {
        this.aleteoNasal = aleteoNasal;
    }

    public Character getApnea() {
        return apnea;
    }

    public void setApnea(Character apnea) {
        this.apnea = apnea;
    }

    public Character getRespiracionRapida() {
        return respiracionRapida;
    }

    public void setRespiracionRapida(Character respiracionRapida) {
        this.respiracionRapida = respiracionRapida;
    }

    public Character getQuejidoEspiratorio() {
        return quejidoEspiratorio;
    }

    public void setQuejidoEspiratorio(Character quejidoEspiratorio) {
        this.quejidoEspiratorio = quejidoEspiratorio;
    }

    public Character getEstiradorReposo() {
        return estiradorReposo;
    }

    public void setEstiradorReposo(Character estiradorReposo) {
        this.estiradorReposo = estiradorReposo;
    }

    public Character getTirajeSubcostal() {
        return tirajeSubcostal;
    }

    public void setTirajeSubcostal(Character tirajeSubcostal) {
        this.tirajeSubcostal = tirajeSubcostal;
    }

    public Character getSibilancias() {
        return sibilancias;
    }

    public void setSibilancias(Character sibilancias) {
        this.sibilancias = sibilancias;
    }

    public Character getCrepitos() {
        return crepitos;
    }

    public void setCrepitos(Character crepitos) {
        this.crepitos = crepitos;
    }

    public Character getRoncos() {
        return roncos;
    }

    public void setRoncos(Character roncos) {
        this.roncos = roncos;
    }

    public Character getOtraFif() {
        return otraFif;
    }

    public void setOtraFif(Character otraFif) {
        this.otraFif = otraFif;
    }

    public String getNuevaFif() {
        return nuevaFif;
    }

    public void setNuevaFif(String nuevaFif) {
        this.nuevaFif = nuevaFif;
    }

    public Character getInterconsultaPediatrica() {
        return interconsultaPediatrica;
    }

    public void setInterconsultaPediatrica(Character interconsultaPediatrica) {
        this.interconsultaPediatrica = interconsultaPediatrica;
    }

    public Character getReferenciaHospital() {
        return referenciaHospital;
    }

    public void setReferenciaHospital(Character referenciaHospital) {
        this.referenciaHospital = referenciaHospital;
    }

    public Character getReferenciaDengue() {
        return referenciaDengue;
    }

    public void setReferenciaDengue(Character referenciaDengue) {
        this.referenciaDengue = referenciaDengue;
    }

    public Character getReferenciaIrag() {
        return referenciaIrag;
    }

    public void setReferenciaIrag(Character referenciaIrag) {
        this.referenciaIrag = referenciaIrag;
    }

    public Character getReferenciaChik() {
        return referenciaChik;
    }

    public void setReferenciaChik(Character referenciaChik) {
        this.referenciaChik = referenciaChik;
    }

    public Character getEti() {
        return eti;
    }

    public void setEti(Character eti) {
        this.eti = eti;
    }

    public Character getIrag() {
        return irag;
    }

    public void setIrag(Character irag) {
        this.irag = irag;
    }

    public Character getNeumonia() {
        return neumonia;
    }

    public void setNeumonia(Character neumonia) {
        this.neumonia = neumonia;
    }

    public Character getLactanciaMaterna() {
        return lactanciaMaterna;
    }

    public void setLactanciaMaterna(Character lactanciaMaterna) {
        this.lactanciaMaterna = lactanciaMaterna;
    }

    public Character getVacunasCompletas() {
        return vacunasCompletas;
    }

    public void setVacunasCompletas(Character vacunasCompletas) {
        this.vacunasCompletas = vacunasCompletas;
    }

    public Character getVacunaInfluenza() {
        return vacunaInfluenza;
    }

    public void setVacunaInfluenza(Character vacunaInfluenza) {
        this.vacunaInfluenza = vacunaInfluenza;
    }

    public String getFechaVacuna() {
        return fechaVacuna;
    }

    public void setFechaVacuna(String fechaVacuna) {
        this.fechaVacuna = fechaVacuna;
    }

    public Integer getSaturaciono2() {
        return saturaciono2;
    }

    public void setSaturaciono2(Integer saturaciono2) {
        this.saturaciono2 = saturaciono2;
    }

    public Double getImc() {
        return imc;
    }

    public void setImc(Double imc) {
        this.imc = imc;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Character getCambioCategoria() {
        return cambioCategoria;
    }

    public void setCambioCategoria(Character cambioCategoria) {
        this.cambioCategoria = cambioCategoria;
    }

    public Character getManifestacionHemorragica() {
        return manifestacionHemorragica;
    }

    public void setManifestacionHemorragica(Character manifestacionHemorragica) {
        this.manifestacionHemorragica = manifestacionHemorragica;
    }

    public Character getPruebaTorniquetePositiva() {
        return pruebaTorniquetePositiva;
    }

    public void setPruebaTorniquetePositiva(Character pruebaTorniquetePositiva) {
        this.pruebaTorniquetePositiva = pruebaTorniquetePositiva;
    }

    public Character getPetequia10Pt() {
        return petequia10Pt;
    }

    public void setPetequia10Pt(Character petequia10Pt) {
        this.petequia10Pt = petequia10Pt;
    }

    public Character getPetequia20Pt() {
        return petequia20Pt;
    }

    public void setPetequia20Pt(Character petequia20Pt) {
        this.petequia20Pt = petequia20Pt;
    }

    public Character getPielExtremidadesFrias() {
        return pielExtremidadesFrias;
    }

    public void setPielExtremidadesFrias(Character pielExtremidadesFrias) {
        this.pielExtremidadesFrias = pielExtremidadesFrias;
    }

    public Character getPalidezEnExtremidades() {
        return palidezEnExtremidades;
    }

    public void setPalidezEnExtremidades(Character palidezEnExtremidades) {
        this.palidezEnExtremidades = palidezEnExtremidades;
    }

    public Character getEpistaxis() {
        return epistaxis;
    }

    public void setEpistaxis(Character epistaxis) {
        this.epistaxis = epistaxis;
    }

    public Character getGingivorragia() {
        return gingivorragia;
    }

    public void setGingivorragia(Character gingivorragia) {
        this.gingivorragia = gingivorragia;
    }

    public Character getPetequiasEspontaneas() {
        return petequiasEspontaneas;
    }

    public void setPetequiasEspontaneas(Character petequiasEspontaneas) {
        this.petequiasEspontaneas = petequiasEspontaneas;
    }

    public Character getLlenadoCapilar2seg() {
        return llenadoCapilar2seg;
    }

    public void setLlenadoCapilar2seg(Character llenadoCapilar2seg) {
        this.llenadoCapilar2seg = llenadoCapilar2seg;
    }

    public Character getCianosis() {
        return cianosis;
    }

    public void setCianosis(Character cianosis) {
        this.cianosis = cianosis;
    }

    public Double getLinfocitosaAtipicos() {
        return linfocitosaAtipicos;
    }

    public void setLinfocitosaAtipicos(Double linfocitosaAtipicos) {
        this.linfocitosaAtipicos = linfocitosaAtipicos;
    }

    public String getFechaLinfocitos() {
        return fechaLinfocitos;
    }

    public void setFechaLinfocitos(String fechaLinfocitos) {
        this.fechaLinfocitos = fechaLinfocitos;
    }

    public Character getHipermenorrea() {
        return hipermenorrea;
    }

    public void setHipermenorrea(Character hipermenorrea) {
        this.hipermenorrea = hipermenorrea;
    }

    public Character getHematemesis() {
        return hematemesis;
    }

    public void setHematemesis(Character hematemesis) {
        this.hematemesis = hematemesis;
    }

    public Character getMelena() {
        return melena;
    }

    public void setMelena(Character melena) {
        this.melena = melena;
    }

    public Character getHemoconc() {
        return hemoconc;
    }

    public void setHemoconc(Character hemoconc) {
        this.hemoconc = hemoconc;
    }

    public Short getHemoconcentracion() {
        return hemoconcentracion;
    }

    public void setHemoconcentracion(Short hemoconcentracion) {
        this.hemoconcentracion = hemoconcentracion;
    }

    public Character getHospitalizado() {
        return hospitalizado;
    }

    public void setHospitalizado(Character hospitalizado) {
        this.hospitalizado = hospitalizado;
    }

    public String getHospitalizadoEspecificar() {
        return hospitalizadoEspecificar;
    }

    public void setHospitalizadoEspecificar(String hospitalizadoEspecificar) {
        this.hospitalizadoEspecificar = hospitalizadoEspecificar;
    }

    public Character getTransfusionSangre() {
        return transfusionSangre;
    }

    public void setTransfusionSangre(Character transfusionSangre) {
        this.transfusionSangre = transfusionSangre;
    }

    public String getTransfusionEspecificar() {
        return transfusionEspecificar;
    }

    public void setTransfusionEspecificar(String transfusionEspecificar) {
        this.transfusionEspecificar = transfusionEspecificar;
    }

    public Character getTomandoMedicamento() {
        return tomandoMedicamento;
    }

    public void setTomandoMedicamento(Character tomandoMedicamento) {
        this.tomandoMedicamento = tomandoMedicamento;
    }

    public String getMedicamentoEspecificar() {
        return medicamentoEspecificar;
    }

    public void setMedicamentoEspecificar(String medicamentoEspecificar) {
        this.medicamentoEspecificar = medicamentoEspecificar;
    }

    public Character getMedicamentoDistinto() {
        return medicamentoDistinto;
    }

    public void setMedicamentoDistinto(Character medicamentoDistinto) {
        this.medicamentoDistinto = medicamentoDistinto;
    }

    public String getMedicamentoDistEspecificar() {
        return medicamentoDistEspecificar;
    }

    public void setMedicamentoDistEspecificar(String medicamentoDistEspecificar) {
        this.medicamentoDistEspecificar = medicamentoDistEspecificar;
    }

    public Character getBhc() {
        return bhc;
    }

    public void setBhc(Character bhc) {
        this.bhc = bhc;
    }

    public Character getSerologiaDengue() {
        return serologiaDengue;
    }

    public void setSerologiaDengue(Character serologiaDengue) {
        this.serologiaDengue = serologiaDengue;
    }

    public Character getSerologiaChik() {
        return serologiaChik;
    }

    public void setSerologiaChik(Character serologiaChik) {
        this.serologiaChik = serologiaChik;
    }

    public Character getGotaGruesa() {
        return gotaGruesa;
    }

    public void setGotaGruesa(Character gotaGruesa) {
        this.gotaGruesa = gotaGruesa;
    }

    public Character getExtendidoPeriferico() {
        return extendidoPeriferico;
    }

    public void setExtendidoPeriferico(Character extendidoPeriferico) {
        this.extendidoPeriferico = extendidoPeriferico;
    }

    public Character getEgo() {
        return ego;
    }

    public void setEgo(Character ego) {
        this.ego = ego;
    }

    public Character getEgh() {
        return egh;
    }

    public void setEgh(Character egh) {
        this.egh = egh;
    }

    public Character getCitologiaFecal() {
        return citologiaFecal;
    }

    public void setCitologiaFecal(Character citologiaFecal) {
        this.citologiaFecal = citologiaFecal;
    }

    public Character getFactorReumatoideo() {
        return factorReumatoideo;
    }

    public void setFactorReumatoideo(Character factorReumatoideo) {
        this.factorReumatoideo = factorReumatoideo;
    }

    public Character getAlbumina() {
        return albumina;
    }

    public void setAlbumina(Character albumina) {
        this.albumina = albumina;
    }

    public Character getAstAlt() {
        return astAlt;
    }

    public void setAstAlt(Character astAlt) {
        this.astAlt = astAlt;
    }

    public Character getBilirrubinas() {
        return bilirrubinas;
    }

    public void setBilirrubinas(Character bilirrubinas) {
        this.bilirrubinas = bilirrubinas;
    }

    public Character getCpk() {
        return cpk;
    }

    public void setCpk(Character cpk) {
        this.cpk = cpk;
    }

    public Character getColesterol() {
        return colesterol;
    }

    public void setColesterol(Character colesterol) {
        this.colesterol = colesterol;
    }

    public Character getInfluenza() {
        return influenza;
    }

    public void setInfluenza(Character influenza) {
        this.influenza = influenza;
    }

    public String getOtroExamenLab() {
        return otroExamenLab;
    }

    public void setOtroExamenLab(String otroExamenLab) {
        this.otroExamenLab = otroExamenLab;
    }

    public Character getOel() {
        return oel;
    }

    public void setOel(Character oel) {
        this.oel = oel;
    }

    public Integer getNumOrdenLaboratorio() {
        return numOrdenLaboratorio;
    }

    public void setNumOrdenLaboratorio(Integer numOrdenLaboratorio) {
        this.numOrdenLaboratorio = numOrdenLaboratorio;
    }

    public String getFechaOrdenLaboratorio() {
        return fechaOrdenLaboratorio;
    }

    public void setFechaOrdenLaboratorio(String fechaOrdenLaboratorio) {
        this.fechaOrdenLaboratorio = fechaOrdenLaboratorio;
    }


    public short getDiagnostico1() {
        return diagnostico1;
    }

    public void setDiagnostico1(short diagnostico1) {
        this.diagnostico1 = diagnostico1;
    }

    public short getDiagnostico2() {
        return diagnostico2;
    }

    public void setDiagnostico2(short diagnostico2) {
        this.diagnostico2 = diagnostico2;
    }

    public short getDiagnostico3() {
        return diagnostico3;
    }

    public void setDiagnostico3(short diagnostico3) {
        this.diagnostico3 = diagnostico3;
    }

    public short getDiagnostico4() {
        return diagnostico4;
    }

    public void setDiagnostico4(short diagnostico4) {
        this.diagnostico4 = diagnostico4;
    }

    public String getOtroDiagnostico() {
        return otroDiagnostico;
    }

    public void setOtroDiagnostico(String otrodiagnostico) {
        this.otroDiagnostico = otrodiagnostico;
    }

    public String getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(String fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public Short getUsuarioMedico() {
        return usuarioMedico;
    }

    public void setUsuarioMedico(Short usuarioMedico) {
        this.usuarioMedico = usuarioMedico;
    }

    public char getEstado() {
        return estado;
    }

    public void setEstado(char estado) {
        this.estado = estado;
    }

    public Character getOtro() {
        return otro;
    }

    public void setOtro(Character otro) {
        this.otro = otro;
    }

    public Short getMedicoCambioTurno() {
        return medicoCambioTurno;
    }

    public void setMedicoCambioTurno(Short medicoCambioTurno) {
        this.medicoCambioTurno = medicoCambioTurno;
    }

    public String getHorasv() {
        return horasv;
    }

    public void setHorasv(String horasv) {
        this.horasv = horasv;
    }
}
