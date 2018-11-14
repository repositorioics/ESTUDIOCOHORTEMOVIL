package com.sts_ni.estudiocohortecssfv.dto;



/**
 * Informaci�n del resultado de una transacci�n el cual se usar� para progagar 
 * dicho resultado a trav�s de las diferentes capas de la aplicaci�n.
 * que dicho resultado sea propagado a las diferentes capas de la aplicaci�n.
 * <p>
 * @author F�lix Medina
 * @author <a href=mailto:felix.medina@simmo.cl>felix.medina@simmo.cl</a>
 * @version 1.0, &nbsp; 08/05/2013
 */

public class InfoResultadoWSDTO implements java.io.Serializable {
	
	private static final long serialVersionUID = 7301833333255795271L;
	
	/**
	 * Nivel de gravedad del mensaje indicando un mensaje informativo en lugar de un error
	 */
	public static final int INFO=1;
	/**
	 * Nivel de gravedad del mensaje indicando que un error pudo haber ocurrido.
	 */
	public static final int WARN=2;
	/**
	 * Nivel de gravedad del mensaje indicando que se ha producido un error.
	 */
	public static final int ERROR=3;
	/**
	 * Nivel de gravedad del mensaje indicando que se ha producido un error muy grave.
	 */
	public static final int FATAL=4;

	private Integer numeroError;
	private String mensaje;
	private String mensajeDetalle;
	private Object objeto;
	private String fuenteError;
	private Integer filasAfectadas;
	private boolean ok;
	private boolean excepcion;
	private int gravedad;
	
	public InfoResultadoWSDTO() {
		
		this.numeroError=0;
		this.mensaje="";
		this.mensajeDetalle="";
		this.objeto=null;
		this.fuenteError="";
		this.ok=true;
		this.excepcion=false;
		this.gravedad=1;
		
	}
	
	/**
	 * Establece el n�mero de error, el cual puede ser una numeraci�n propia
	 * para su manipulaci�n final o como n�mero de referencia.
	 */
	public void setNumeroError(Integer numeroError) {
		this.numeroError = numeroError;
	}

	/**
	 * Obtiene el n�mero de error, el cual puede ser una numeraci�n propia para
	 * su manipulaci�n final o como n�mero de referencia.
	 */
	public Integer getNumeroError() {
		return numeroError;
	}

	/**
	 * Establece un mensaje descriptivo que se gener�.
	 */
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	/**
	 * Obtiene el mensaje descriptivo que se gener�.
	 */
	public String getMensaje() {
		return mensaje;
	}

	/**
	 * Encapsula como objeto el resultado propiamente dicho de la operaci�n.
	 * Debe ser <code>null</code> si se ha generado un error en dicha operaci�n.
	 */
	public void setObjeto(Object objeto) {
		this.objeto = objeto;
	}
	
	/**
	 * Obtiene un objeto de tipo <code>Object</code> al cual debe aplicarse
	 * el tipo correspondiente para su manipulaci�n.
	 */
	public Object getObjeto() {
		return objeto;
	}

	/**
	 * Establece la fuente u origen donde se produjo el error, a fin de ser
	 * un elemento orientativo para los errores graves o fatales.
	 */
	public void setFuenteError(String fuenteError) {
		this.fuenteError = fuenteError;
	}

	/**
	 * Obtiene la fuente u origen donde se produjo el error, a fin de ser
	 * un elemento orientativo para los erroes graves o fatales.
	 */
	public String getFuenteError() {
		return fuenteError;
	}

	/**
	 * Establece el estado del resultado, indicando si se produjo o no un
	 * error en la operaci�n.  <br>
	 * <code>true</code> si no se produjo error, 
	 * <code>false</code> si se ha producido un error. 
	 */
	public void setOk(boolean ok) {
		this.ok = ok;
	}

	/**
	 * Obtiene el estado del resultado, indicando si se produjo o no un
	 * error en la operaci�n solicitada. <br>
	 * Retorna <code>true</code> si no se produjo error, 
	 * <code>false</code> si se ha producido un error. 
	 */
	public boolean isOk() {
		return ok;
	}

	/**
	 * Establece si el error que se produjo fue generado por 
	 * una excepci�n.<br>
	 * <code>true</code> si no se produjo una excepci�n, 
	 * <code>false</code> si se ha producido una excepci�n. 
	 */
	public void setExcepcion(boolean excepcion) {
		this.excepcion = excepcion;
	}

	/**
	 * Obtiene <code>true</code> si el error que se produjo fue generado por 
	 * una excepci�n o <code>false</code> caso contrario.<br>
	 */ 
	public Boolean isExcepcion() {
		return excepcion;
	}

	/**
	 * Establece un texto descriptivo con el detalle del error que se produjo.
	 */
	public void setMensajeDetalle(String mensajeDetalle) {
		this.mensajeDetalle = mensajeDetalle;
	}

	/**
	 * Obtiene un texto descriptivo con el detalle del error que se produjo.
	 */
	public String getMensajeDetalle() {
		return mensajeDetalle;
	}

	/**
	 * Establece la gravedad del error utilizando los mismos
	 * valores de niveles de severidad del <code>FacesMessages</code><br>
	 * 1=Info<br>
	 * 2=Warning<br>
	 * 3=Error<br>
	 * 4=Fatal
	 */
	public void setGravedad(int gravedad) {
		this.gravedad = gravedad;
	}

	/**
	 * Obtiene la gravedad del error utilizando los mismos valores de 
	 * niveles de severidad del <code>FacesMessages</code><br>
	 * 1=Info<br>
	 * 2=Warning<br>
	 * 3=Error<br>
	 * 4=Fatal<p>
	 * No es posible, con la versi�n de Mojarra (2.1.6), utilizar la
	 * subclase Severity del FacesMessages, ya que esta es no serializable.
	 */
	public int getGravedad() {
		return gravedad;
	}

	/**
	 * Establece el n�mero de filas afectadas en la transacci�n u operaci�n
	 * realizada y que di� lugar al objeto InfoResultado
	 */
	public void setFilasAfectadas(Integer filasAfectadas) {
		this.filasAfectadas = filasAfectadas;
	}

	/**
	 * Obtiene el n�mero de filas afectadas en la transacci�n u operaci�n
	 * realizada y que di� lugar al objeto InfoResultado
	 */
	public Integer getFilasAfectadas() {
		return filasAfectadas;
	}
	
}
