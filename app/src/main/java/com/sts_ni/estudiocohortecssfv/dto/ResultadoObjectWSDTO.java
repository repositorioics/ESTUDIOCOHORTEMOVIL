package com.sts_ni.estudiocohortecssfv.dto;

/**
 * Created by user on 01/03/2015.
 */
public class ResultadoObjectWSDTO<T> extends ErrorDTO {
    private T objecRespuesta;

    public T getObjecRespuesta() {
        return objecRespuesta;
    }

    public void setObjecRespuesta(T objecRespuesta) {
        this.objecRespuesta = objecRespuesta;
    }
}
