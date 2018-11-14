package com.sts_ni.estudiocohortecssfv.dto;

import java.io.Serializable;

/**
 * Created by flopezcarballo on 25/03/2015.
 */
public class ResultadosExamenesDTO implements Serializable {
    private HojaConsultaDTO hojaConsulta;
    private MalariaResultadoDTO malariaResultado;
    private SerologiaDengueDTO serologiaDengue;
    private SerologiaChickDTO serologiaChick;
    private PerifericoResultadoDTO perifericoResultado;
    private EghDTO egh;
    private EgoDTO ego;
    private InfluenzaDTO influenza;

    public HojaConsultaDTO getHojaConsulta() {
        return hojaConsulta;
    }

    public void setHojaConsulta(HojaConsultaDTO hojaConsulta) {
        this.hojaConsulta = hojaConsulta;
    }

    public MalariaResultadoDTO getMalariaResultado() {
        return malariaResultado;
    }

    public void setMalariaResultado(MalariaResultadoDTO malariaResultado) {
        this.malariaResultado = malariaResultado;
    }

    public SerologiaDengueDTO getSerologiaDengue() {
        return serologiaDengue;
    }

    public void setSerologiaDengue(SerologiaDengueDTO serologiaDengue) {
        this.serologiaDengue = serologiaDengue;
    }

    public SerologiaChickDTO getSerologiaChick() {
        return serologiaChick;
    }

    public void setSerologiaChick(SerologiaChickDTO serologiaChick) {
        this.serologiaChick = serologiaChick;
    }

    public PerifericoResultadoDTO getPerifericoResultado() {
        return perifericoResultado;
    }

    public void setPerifericoResultado(PerifericoResultadoDTO perifericoResultado) {
        this.perifericoResultado = perifericoResultado;
    }

    public EghDTO getEgh() {
        return egh;
    }

    public void setEgh(EghDTO egh) {
        this.egh = egh;
    }

    public EgoDTO getEgo() {
        return ego;
    }

    public void setEgo(EgoDTO ego) {
        this.ego = ego;
    }

    public InfluenzaDTO getInfluenza() {
        return influenza;
    }

    public void setInfluenza(InfluenzaDTO influenza) {
        this.influenza = influenza;
    }
}
