package com.sts_ni.estudiocohortecssfv.ws;

import org.ksoap2.HeaderProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Super Clase para controlar el llamado a los metodos webservice del Estudio Cohorte CSSFV.
 */
public class EstudioCohorteCssfvWS {
    //public static final String URL = "http://192.168.43.220:8080/estudioCohorteCSSFVMovilWS/EstudioCohorteCSSFVMovilWSService?wsdl";
    //public static String URL = "http://54.183.17.90:8080/estudioCohorteCSSFVMovilWS/EstudioCohorteCSSFVMovilWSService?wsdl";
  public static String URL = "http://192.168.1.97:8080/estudioCohorteCSSFVMovilWS/EstudioCohorteCSSFVMovilWSService?wsdl";
  // public static String URL = "http://192.168.1.95:8080/estudioCohorteCSSFVMovilWS/EstudioCohorteCSSFVMovilWSService?wsdl";


    static {
        try {
            Properties props = new Properties();
            InputStream inputStream = new FileInputStream("/sdcard/cssfv/config/config.properties");
            props.load(inputStream);
            URL = props.getProperty("CSSFV.URLWS");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static final String NAMESPACE = "http://webservice.estudiocohortecssfv.sts_ni.com/";

    public ArrayList<HeaderProperty> HEADER_PROPERTY;

    public static final String METODO_GET_HOJA_CONSULTA_TEST = "getHojaConsultaTest";
    public static final String ACCIONSOAP_GET_HOJA_CONSULTA_TEST = NAMESPACE + METODO_GET_HOJA_CONSULTA_TEST;

    public static final String METODO_GET_LISTA_INICIO_ENFERMERIA = "getListaInicioEnfermeria";
    public static final String ACCIOSOAP_GET_LISTA_INICIO_ENFERMERIA = NAMESPACE + METODO_GET_LISTA_INICIO_ENFERMERIA;

    public static final String METODO_ENVIAR_DATOS_PRECLINICOS = "enviarDatosPreclinicos";
    public static final String ACCIOSOAP_ENVIAR_DATOS_PRECLINICOS = NAMESPACE + METODO_ENVIAR_DATOS_PRECLINICOS;

    public static final String METODO_CANCELAR_DATOS_PRECLINICOS = "cancelarDatosPreclinicos";
    public static final String ACCIOSOAP_CANCELAR_DATOS_PRECLINICOS = NAMESPACE + METODO_CANCELAR_DATOS_PRECLINICOS;

    public static final String METODO_NO_ATIENDE_LLAMADO_DATOS_PRECLINICOS = "noAtiendeLlamadoDatosPreclinicos";
    public static final String ACCIOSOAP_NO_ATIENDE_LLAMADO_DATOS_PRECLINICOS = NAMESPACE + METODO_NO_ATIENDE_LLAMADO_DATOS_PRECLINICOS;

    public static final String METODO_BUSCAR_PACIENTE = "buscarPaciente";
    public static final String ACCIOSOAP_BUSCAR_PACIENTE = NAMESPACE + METODO_BUSCAR_PACIENTE;

    public static final String METODO_GUARDA_PACIENTE_EMERGENGIA = "guardarPacienteEmergencia";
    public static final String ACCIOSOAP_GUARDA_PACIENTE_EMERGENCIA = NAMESPACE + METODO_GUARDA_PACIENTE_EMERGENGIA;

    public static final String METODO_GET_LISTA_INICIO_CONSULTA = "getListaInicioHojaConsulta";
    public static final String ACCIOSOAP_GET_LISTA_INICIO_CONSULTA = NAMESPACE + METODO_GET_LISTA_INICIO_CONSULTA;

    public static final String METODO_GET_LISTA_INICIO_CONSULTAPOREXPEDIENTE = "getListaHojaConsultaPorExpediente";
    public static final String ACCIOSOAP_GET_LISTA_INICIO_CONSULTAPOREXPEDIENTE = NAMESPACE + METODO_GET_LISTA_INICIO_CONSULTAPOREXPEDIENTE;

    public static final String METODO_GET_DATOS_CABECERA_SINTOMAS = "getDatosCabeceraSintomas";
    public static final String ACCIOSOAP_GET_DATOS_CABECERA_SINTOMAS = NAMESPACE + METODO_GET_DATOS_CABECERA_SINTOMAS;

    public static final String METODO_GET_HORARIO_TURNO = "getHorarioTurno";
    public static final String ACCIOSOAP_GET_HORARIO_TURNO = NAMESPACE + METODO_GET_HORARIO_TURNO;

    public static final String METODO_GUARDA_EXAMENHISTORIA_HOJACONSULTA = "guardarExamenHistoricoHojaConsulta";
    public static final String ACCIOSOAP_GUARDA_EXAMENHISTORIA_HOJACONSULTA = NAMESPACE + METODO_GUARDA_EXAMENHISTORIA_HOJACONSULTA;

    public static final String METODO_GET_CONSULTAPORNUMERO = "getHojaConsultaPorNumero";
    public static final String ACCIOSOAP_GET_CONSULTAPORNUMERO =  NAMESPACE + METODO_GET_CONSULTAPORNUMERO;

    public static final String METODO_GUARDA_PLANES_HOJACONSULTA = "guardarPlanesHojaConsulta";
    public static final String ACCIOSOAP_GUARDA_PLANES_HOJACONSULTA = NAMESPACE + METODO_GUARDA_PLANES_HOJACONSULTA;

    public static final String METODO_GUARDA_TRATAMIENTO_HOJACONSULTA = "guardarTratamientoHojaConsulta";
    public static final String ACCIOSOAP_GUARDA_TRATAMIENTO_HOJACONSULTA = NAMESPACE + METODO_GUARDA_TRATAMIENTO_HOJACONSULTA;

    public static final String METODO_GUARDA_PROXIMACITA_HOJACONSULTA = "guardarProximaCitaHojaConsulta";
    public static final String ACCIOSOAP_GUARDA_PROXIMACITA__HOJACONSULTA = NAMESPACE + METODO_GUARDA_PROXIMACITA_HOJACONSULTA;

    public static final String METODO_LISTADIAGNOSTICO = "getListaDiagnostico";
    public static final String ACCIOSOAP_LISTADIAGNOSTICO= NAMESPACE + METODO_LISTADIAGNOSTICO;

    public static final String METODO_LISTA_ESCUELA = "getTodasEscuela";
    public static final String ACCIOSOAP_LISTA_ESCUELA = NAMESPACE + METODO_LISTA_ESCUELA;

    public static final String METODO_GUARDAR_GENEREALES_SINTOMAS = "guardarGeneralesSintomas";
    public static final String ACCIOSOAP_GUARDAR_GENEREALES_SINTOMAS = NAMESPACE + METODO_GUARDAR_GENEREALES_SINTOMAS;

    public static final String METODO_GUARDAR_ESTADO_GENEREAL_SINTOMAS = "guardarEstadoGeneralSintomas";
    public static final String ACCIOSOAP_GUARDAR_ESTADO_GENEREAL_SINTOMAS = NAMESPACE + METODO_GUARDAR_ESTADO_GENEREAL_SINTOMAS;

    public static final String METODO_GUARDAR_GASTROIN_SINTOMAS = "guardarGastroInSintomas";
    public static final String ACCIOSOAP_GUARDAR_GASTROIN_SINTOMAS = NAMESPACE + METODO_GUARDAR_GASTROIN_SINTOMAS;

    public static final String METODO_GUARDAR_OSTEOMUSCULAR_SINTOMAS = "guardarOsteomuscularSintomas";
    public static final String ACCIOSOAP_GUARDAR_OSTEOMUSCULAR_SINTOMAS = NAMESPACE + METODO_GUARDAR_OSTEOMUSCULAR_SINTOMAS;

    public static final String METODO_GUARDAR_CABEZA_SINTOMAS = "guardarCabezaSintomas";
    public static final String ACCIOSOAP_GUARDAR_CABEZA_SINTOMAS = NAMESPACE + METODO_GUARDAR_CABEZA_SINTOMAS;

    public static final String METODO_GUARDAR_DESHIDRA_SINTOMAS = "guardarDeshidraSintomas";
    public static final String ACCIOSOAP_GUARDAR_DESHIDRA_SINTOMAS = NAMESPACE + METODO_GUARDAR_DESHIDRA_SINTOMAS;

    public static final String METODO_GUARDAR_CUTANEO_SINTOMAS = "guardarCutaneoSintomas";
    public static final String ACCIOSOAP_GUARDAR_CUTANEO_SINTOMAS = NAMESPACE + METODO_GUARDAR_CUTANEO_SINTOMAS;

    public static final String METODO_GUARDAR_GARGANTA_SINTOMAS = "guardarGargantaSintomas";
    public static final String ACCIOSOAP_GUARDAR_GARGANTA_SINTOMAS = NAMESPACE + METODO_GUARDAR_GARGANTA_SINTOMAS;

    public static final String METODO_GUARDAR_RENAL_SINTOMAS = "guardarRenalSintomas";
    public static final String ACCIOSOAP_GUARDAR_RENAL_SINTOMAS = NAMESPACE + METODO_GUARDAR_RENAL_SINTOMAS;

    public static final String METODO_GUARDAR_ESTADO_NUTRI_SINTOMAS = "guardarEstadoNutriSintomas";
    public static final String ACCIOSOAP_GUARDAR_ESTADO_NUTRI_SINTOMAS = NAMESPACE + METODO_GUARDAR_ESTADO_NUTRI_SINTOMAS;

    public static final String METODO_GUARDAR_RESPIRATORIO_SINTOMAS = "guardarRespiratorioSintomas";
    public static final String ACCIOSOAP_GUARDAR_RESPIRATORIO_SINTOMAS = NAMESPACE + METODO_GUARDAR_RESPIRATORIO_SINTOMAS;

    public static final String METODO_GUARDAR_REFERENCIA_SINTOMAS = "guardarReferenciaSintomas";
    public static final String ACCIOSOAP_GUARDAR_REFERENCIA_SINTOMAS = NAMESPACE + METODO_GUARDAR_REFERENCIA_SINTOMAS;

    public static final String METODO_GUARDAR_VACUNAS_SINTOMAS = "guardarVacunaSintomas";
    public static final String ACCIOSOAP_GUARDAR_VACUNAS_SINTOMAS = NAMESPACE + METODO_GUARDAR_VACUNAS_SINTOMAS;

    //examenes
    public static final String METODO_GET_DATOS_CABECERA_EXAMENES = "getDatosCabeceraExamenes";
    public static final String ACCIOSOAP_GET_DATOS_CABECERA_EXAMENES = NAMESPACE + METODO_GET_DATOS_CABECERA_SINTOMAS;

    public static final String METODO_GUARDAR_EXAMENES = "guardarExamenes";
    public static final String ACCIOSOAP_GUARDAR_EXAMENES = NAMESPACE + METODO_GUARDAR_EXAMENES;

    public static final String METODO_GET_EXAMENES = "buscarExamenes";
    public static final String ACCIOSOAP_GET_EXAMENES = NAMESPACE + METODO_GET_EXAMENES;

    public static final String METODO_GET_EXAMENES_CHEKEADOS = "buscarExamenesChekeados";
    public static final String ACCIOSOAP_GET_EXAMENES_CHEKEADOS = NAMESPACE + METODO_GET_EXAMENES_CHEKEADOS;

    public static final String METODO_GUARDAR_OTRO_EXAMEN = "guardarOtroExamen";
    public static final String ACCIOSOAP_GUARDAR_OTRO_EXAMEN = NAMESPACE + METODO_GUARDAR_OTRO_EXAMEN;

    public static final String METODO_GUARDARDIAGNOSTICO_DIAGNOSTICO = "guardarDiagnosticoHojaConsulta";
    public static final String ACCIOSOAP_GUARDARDIAGNOSTICO_DIAGNOSTICO = NAMESPACE + METODO_GUARDARDIAGNOSTICO_DIAGNOSTICO;

    public static final String METODO_VALIDAR_CONSENTIMIENTO_DENGUE = "validarNoConsentimientoDengue";
    public static final String ACCIOSOAP_VALIDAR_CONSENTIMIENTO_DENGUE = NAMESPACE + METODO_VALIDAR_CONSENTIMIENTO_DENGUE;

    public static final String METODO_GUARDAR_CATEGORIA_SINTOMAS = "guardarCategoriaSintomas";
    public static final String ACCIOSOAP_GUARDAR_CATEGORIA_SINTOMAS = NAMESPACE + METODO_GUARDAR_CATEGORIA_SINTOMAS;

    public static final String METODO_VALIDACION_MATRIZ_SINTOMA = "validacionMatrizSintoma";
    public static final String ACCIOSOAP_VALIDACION_MATRIZ_SINTOMA = NAMESPACE + METODO_VALIDACION_MATRIZ_SINTOMA;

    //Expediente
    public static final String METODO_HOJA_CONSULTA_EXP = "getListaHojaConsultaExp";
    public static final String ACCIOSOAP_GET_HOJA_CONSULTA_EXP= NAMESPACE + METODO_HOJA_CONSULTA_EXP;

    public static final String METODO_CARGAR_GRILLA_CIERRE = "cargarGrillaCierre";
    public static final String ACCIOSOAP_CARGAR_GRILLA_CIERRE = NAMESPACE + METODO_CARGAR_GRILLA_CIERRE;

    //Seguimiento Influenza
    public static final String BUSCAR_PACIENTE_CREAR_SEGUIMIENTO_INFLUENZA = "buscarPacienteCrearHoja";
    public static final String ACCIOSOAP_BUSCAR_PACIENTE_CREAR_SEGUIMIENTO_INFLUENZA = NAMESPACE + BUSCAR_PACIENTE_CREAR_SEGUIMIENTO_INFLUENZA;

    public static final String BUSCAR_PACIENTE_SEGUIMIENTO_INFLUENZA = "buscarPacienteSeguimientoInfluenza";
    public static final String ACCIOSOAP_BUSCAR_PACIENTE_SEGUIMIENTO_INFLUENZA = NAMESPACE + BUSCAR_PACIENTE_SEGUIMIENTO_INFLUENZA;

    public static final String METODO_BUSCAR_HOJA_SEGUIMIENTO_INFLUENA = "buscarHojaSeguimientoInfluenza";
    public static final String ACCIOSOAP_METODO_BUSCAR_HOJA_SEGUIMIENTO_INFLUENA = NAMESPACE + METODO_BUSCAR_HOJA_SEGUIMIENTO_INFLUENA;

    public static final String METODO_CREAR_HOJA_SEGUIMIENTO_INFLUENA = "crearSeguimientoInfluenza";
    public static final String ACCIOSOAP_METODO_CREAR_HOJA_SEGUIMIENTO_INFLUENA = NAMESPACE + METODO_CREAR_HOJA_SEGUIMIENTO_INFLUENA;

    public static final String METODO_GUARDAR_HOJA_SEGUIMIENTO_INFLUENA = "guardarSeguimientoInfluenza";
    public static final String ACCIOSOAP_METODO_GUARDAR_HOJA_SEGUIMIENTO_INFLUENA = NAMESPACE + METODO_GUARDAR_HOJA_SEGUIMIENTO_INFLUENA;

    public static final String METODO_GET_LISTA_SEGUIMIENTO_INFLUENA = "getListaSeguimientoInfluenza";
    public static final String ACCIOSOAP_METODO_GET_LISTA_SEGUIMIENTO_INFLUENA = NAMESPACE + METODO_GET_LISTA_SEGUIMIENTO_INFLUENA;

    public static final String METODO_PROCESO_CIERRE = "procesoCierre";
    public static final String ACCIOSOAP_METODO_PROCESO_CIERRE = NAMESPACE + METODO_PROCESO_CIERRE;

    public static final String METODO_PROCESO_CAMBIO_TURNO = "procesoCambioTurno";
    public static final String ACCIOSOAP_METODO_PROCESO_CAMBIO_TURNO = NAMESPACE + METODO_PROCESO_CAMBIO_TURNO;

    public static final String METODO_PROCESO_AGREGAR_HOJA = "procesoAgregarHojaConsulta";
    public static final String ACCIOSOAP_METODO_PROCESO_AGREGAR_HOJA = NAMESPACE + METODO_PROCESO_AGREGAR_HOJA;

    public static final String METODO_PROCESO_CANCELAR = "procesoCancelar";
    public static final String ACCIOSOAP_METODO_PROCESO_CANCELAR = NAMESPACE + METODO_PROCESO_CANCELAR;

    public static final String METODO_NO_ATIENDE_LLAMADO_CIERRE = "noAtiendeLlamadoCierre";
    public static final String ACCIOSOAP_METODO_NO_ATIENDE_LLAMADO_CIERRE = NAMESPACE + METODO_NO_ATIENDE_LLAMADO_CIERRE;

    public static final String METODO_VALIDAR_SALIR_HOJA_CONSULTA = "validarSalirHojaConsulta";
    public static final String ACCIOSOAP_METODO_VALIDAR_SALIR_HOJA_CONSULTA = NAMESPACE + METODO_VALIDAR_SALIR_HOJA_CONSULTA;

    public static final String METODO_VISUALIZARHOJACONSULTA_PDF = "getHojaConsultaPdf";
    public static final String ACCIOSOAP_VISUALIZARHOJACONSULTA_PDF = NAMESPACE + METODO_VISUALIZARHOJACONSULTA_PDF;

    public static final String METODO_VISUALIZARHOJASEGUIMIENTO_PDF = "getSeguimientoInfluenzaPdf";
    public static final String ACCIOSOAP_VISUALIZARHOJASEGUIMIENTO_PDF = NAMESPACE + METODO_VISUALIZARHOJASEGUIMIENTO_PDF;

    public static final String METODO_ACTUALIZARESTADOENCONSULTA= "actualizarEstadoEnConsulta";
    public static final String ACCIOSOAP_METODO_ACTUALIZAESTADORENCONSULTA = NAMESPACE + METODO_ACTUALIZARESTADOENCONSULTA;

    public static final String METODO_GET_GENERALES_SINTOMAS = "getGeneralesSintomas";
    public static final String ACCIOSOAP_METODO_GET_GENERALES_SINTOMAS = NAMESPACE + METODO_GET_GENERALES_SINTOMAS;

    public static final String METODO_GET_ESTADO_GENERAL_SINTOMAS = "getEstadoGeneralSintomas";
    public static final String ACCIOSOAP_METODO_GET_ESTADO_GENERAL_SINTOMAS = NAMESPACE + METODO_GET_ESTADO_GENERAL_SINTOMAS;

    public static final String METODO_GET_GASTROINTESTINAL_SINTOMAS = "getGastroinstestinalSintomas";
    public static final String ACCIOSOAP_METODO_GET_GASTROINTESTINAL_SINTOMAS = NAMESPACE + METODO_GET_GASTROINTESTINAL_SINTOMAS;

    public static final String METODO_GET_OSTEOMUSCULAR_SINTOMAS = "getOsteomuscularSintomas";
    public static final String ACCIOSOAP_METODO_GET_OSTEOMUSCULAR_SINTOMAS = NAMESPACE + METODO_GET_OSTEOMUSCULAR_SINTOMAS;

    public static final String METODO_GET_CABEZA_SINTOMAS = "getCabezaSintomas";
    public static final String ACCIOSOAP_METODO_GET_CABEZA_SINTOMAS = NAMESPACE + METODO_GET_CABEZA_SINTOMAS;

    public static final String METODO_GET_DESHIDRATACION_SINTOMAS = "getDeshidratacionSintomas";
    public static final String ACCIOSOAP_METODO_GET_DESHIDRATACION_SINTOMAS = NAMESPACE + METODO_GET_DESHIDRATACION_SINTOMAS;

    public static final String METODO_GET_CUTANEO_SINTOMAS = "getCutaneoSintomas";
    public static final String ACCIOSOAP_METODO_GET_CUTANEO_SINTOMAS = NAMESPACE + METODO_GET_CUTANEO_SINTOMAS;

    public static final String METODO_GET_GARGANTA_SINTOMAS = "getGargantaSintomas";
    public static final String ACCIOSOAP_METODO_GET_GARGANTA_SINTOMAS = NAMESPACE + METODO_GET_GARGANTA_SINTOMAS;

    public static final String METODO_GET_RENAL_SINTOMAS = "getRenalSintomas";
    public static final String ACCIOSOAP_METODO_GET_RENAL_SINTOMAS = NAMESPACE + METODO_GET_RENAL_SINTOMAS;

    public static final String METODO_GET_ESTADO_NUTRICIONAL_SINTOMAS = "getEstadoNutricionalSintomas";
    public static final String ACCIOSOAP_METODO_GET_ESTADO_NUTRICIONAL_SINTOMAS = NAMESPACE + METODO_GET_ESTADO_NUTRICIONAL_SINTOMAS;

    public static final String METODO_GET_RESPIRATORIO_SINTOMAS = "getRespiratorioSintomas";
    public static final String ACCIOSOAP_METODO_GET_RESPIRATORIO_SINTOMAS = NAMESPACE + METODO_GET_RESPIRATORIO_SINTOMAS;

    public static final String METODO_GET_REFERENCIA_SINTOMAS = "getReferenciaSintomas";
    public static final String ACCIOSOAP_METODO_GET_REFERENCIA_SINTOMAS = NAMESPACE + METODO_GET_REFERENCIA_SINTOMAS;

    public static final String METODO_GET_VACUNAS_SINTOMAS = "getVacunasSintomas";
    public static final String ACCIOSOAP_METODO_GET_VACUNAS_SINTOMAS = NAMESPACE + METODO_GET_VACUNAS_SINTOMAS;

    public static final String METODO_GET_CATEGORIAS_SINTOMAS = "getCategoriasSintomas";
    public static final String ACCIOSOAP_METODO_GET_CATEGORIAS_SINTOMAS = NAMESPACE + METODO_GET_CATEGORIAS_SINTOMAS;


    public static final String METODO_OBTENERDATOSPACIENTES = "obtenerDatosPaciente";
    public static final String ACCIOSOAP_METODO_OBTENERDATOSPACIENTES = NAMESPACE + METODO_GET_CATEGORIAS_SINTOMAS;

    public static final String METODO_IMPRIMIROJASEGUIMIENTO_PDF = "imprimirSeguimientoInfluenciaPdf";
    public static final String ACCIOSOAP_IMPRIMIRHOJASEGUIMIENTO_PDF = NAMESPACE + METODO_IMPRIMIROJASEGUIMIENTO_PDF;

    public static final String METODO_EDITAR_DATOS_PRECLINICOS = "editarDatosPreclinicos";
    public static final String ACCIOSOAP_EDITAR_DATOS_PRECLINICOS = NAMESPACE + METODO_EDITAR_DATOS_PRECLINICOS;

    public static final String METODO_GUARDAR_CONTROL_CAMBIOS = "guardarControlCambios";
    public static final String ACCIOSOAP_GUARDAR_CONTROL_CAMBIOS = NAMESPACE + METODO_GUARDAR_CONTROL_CAMBIOS;

    public static final String METODO_GET_LISTA_MEDICOS = "getListaMedicos";
    public static final String ACCIOSOAP_GET_LISTA_MEDICOS = NAMESPACE + METODO_GET_LISTA_MEDICOS;

    public static final String METODO_GET_LISTA_MEDICOS_POR_NOMBRE = "getListaMedicosPorNombre";
    public static final String ACCIOSOAP_GET_LISTA_MEDICOS_POR_NOMBRE = NAMESPACE + METODO_GET_LISTA_MEDICOS_POR_NOMBRE;

    public static final String METODO_GET_SECCIONES_SINTOMAS_COMPLETADAS = "getSeccionesSintomasCompletadas";
    public static final String ACCIOSOAP_METODO_GET_SECCIONES_SINTOMAS_COMPLETADAS = NAMESPACE + METODO_GET_SECCIONES_SINTOMAS_COMPLETADAS;

    public static final String METODO_GET_SECCIONES_DIAGNOSTICO_COMPLETADAS = "getSeccionesDiagnosticoCompletadas";
    public static final String ACCIOSOAP_METODO_GET_SECCIONES_DIAGNOSTICO_COMPLETADAS = NAMESPACE + METODO_GET_SECCIONES_DIAGNOSTICO_COMPLETADAS;

    public static final String METODO_GET_DATOS_PRECLINICOS = "getDatosPreclinicos";
    public static final String ACCIOSOAP_GET_DATOS_PRECLINICOS = NAMESPACE + METODO_GET_DATOS_PRECLINICOS;

    public static final String METODO_ACTUALIZAR_USUARIO_ENFERMERIA = "actualizarEstadoEnfermeria";
    public static final String ACCIOSOAP_GET_DATO_USUARIO_ENFERMERIA = NAMESPACE + METODO_ACTUALIZAR_USUARIO_ENFERMERIA;

    public static final String METODO_REIMPRESION_HOJA_CONSULTA = "reimpresionHojaConsulta";
    public static final String ACCIOSOAP_REIMPRESION_HOJA_CONSULTA = NAMESPACE + METODO_REIMPRESION_HOJA_CONSULTA;

    //Seguimiento Zika
    public static final String METODO_GET_LISTA_SEGUIMIENTO_ZIKA = "getListaSeguimientoZika";
    public static final String ACCIOSOAP_METODO_GET_LISTA_SEGUIMIENTO_ZIKA = NAMESPACE + METODO_GET_LISTA_SEGUIMIENTO_ZIKA;

    public static final String BUSCAR_PACIENTE_SEGUIMIENTO_ZIKA = "buscarPacienteSeguimientoZika";
    public static final String ACCIOSOAP_BUSCAR_PACIENTE_SEGUIMIENTO_ZIKA = NAMESPACE + BUSCAR_PACIENTE_SEGUIMIENTO_ZIKA;

    public static final String METODO_BUSCAR_HOJA_SEGUIMIENTO_ZIKA = "buscarHojaSeguimientoZika";
    public static final String ACCIOSOAP_METODO_BUSCAR_HOJA_SEGUIMIENTO_ZIKA = NAMESPACE + METODO_BUSCAR_HOJA_SEGUIMIENTO_ZIKA;

    public static final String METODO_CREAR_HOJA_SEGUIMIENTO_ZIKA = "crearSeguimientoZika";
    public static final String ACCIOSOAP_METODO_CREAR_HOJA_SEGUIMIENTO_ZIKA = NAMESPACE + METODO_CREAR_HOJA_SEGUIMIENTO_ZIKA;

    public static final String METODO_GUARDAR_HOJA_SEGUIMIENTO_ZIKA = "guardarSeguimientoZika";
    public static final String ACCIOSOAP_METODO_GUARDAR_HOJA_SEGUIMIENTO_ZIKA = NAMESPACE + METODO_GUARDAR_HOJA_SEGUIMIENTO_ZIKA;

    public static final String METODO_VISUALIZARHOJASEGUIMIENTOZ_PDF = "getSeguimientoZikaPdf";
    public static final String ACCIOSOAP_VISUALIZARHOJASEGUIMIENTOZ_PDF = NAMESPACE + METODO_VISUALIZARHOJASEGUIMIENTOZ_PDF;

    public static final String METODO_IMPRIMIROJASEGUIMIENTOZ_PDF = "imprimirSeguimientoZikaPdf";
    public static final String ACCIOSOAP_IMPRIMIRHOJASEGUIMIENTOZ_PDF = NAMESPACE + METODO_IMPRIMIROJASEGUIMIENTOZ_PDF;

    // Vigilancia Integrada
    public static final String BUSCAR_FICHA_VIGILANCIA_INTEGRADA = "buscarFichaVigilanciaIntegrada";
    public static final String ACCIOSOAP_BUSCAR_FICHA_VIGILANCIA_INTEGRADA = NAMESPACE + BUSCAR_FICHA_VIGILANCIA_INTEGRADA;

    public static final String METODO_GUARDAR_FICHA_VIGILANCIA_INTEGRADA  = "guardarFichaVigilanciaIntegrada";
    public static final String ACCIOSOAP_METODO_GUARDAR_FICHA_VIGILANCIA_INTEGRADA = NAMESPACE + METODO_GUARDAR_FICHA_VIGILANCIA_INTEGRADA;

    // Departamentos
    public static final String METODO_GET_DEPARTAMENTOS  = "getDepartamentos";
    public static final String ACCIOSOAP_METODO_GET_DEPARTAMENTOS = NAMESPACE + METODO_GET_DEPARTAMENTOS;

    // Municipios
    public static final String METODO_GET_MUNICIPIOS  = "getMunicipios";
    public static final String ACCIOSOAP_METODO_GET_MUNICIPIOS = NAMESPACE + METODO_GET_MUNICIPIOS;

    public static final String METODO_VISUALIZAR_FICHA_PDF = "getFichaPdf";
    public static final String ACCIOSOAP_METODO_VISUALIZAR_FICHA_PDF = NAMESPACE + METODO_VISUALIZAR_FICHA_PDF;

    public static final String METODO_IMPRIMIR_FICHA_PDF = "imprimirFichaPdf";
    public static final String ACCIOSOAP_METODO_IMPRIMIR_FICHA_PDF = NAMESPACE + METODO_IMPRIMIR_FICHA_PDF;

    //
    public static final String METODO_ACTIVAR_DIAGNOSTICO = "procesoActivarDiagnostico";
    public static final String ACCIOSOAP_METODO_ACTIVAR_DIAGNOSTICO = NAMESPACE + METODO_ACTIVAR_DIAGNOSTICO;

    /*Fecha Creacion 6/12/2019 -- SC*/
    public static final String METODO_VISUALIZAR_FICHA_EPI_PDF = "getFichaEpiSindromesFebrilesPdf";
    public static final String ACCIOSOAP_METODO_VISUALIZAR_FICHA_EPI_PDF = NAMESPACE + METODO_VISUALIZAR_FICHA_EPI_PDF;

    public static final String METODO_IMPRIMIR_FICHA_EPI_PDF = "imprimirFichaEpiSindromesFebrilesPdf";
    public static final String ACCIOSOAP_METODO_IMPRIMIR_FICHA_EPI_PDF = NAMESPACE + METODO_IMPRIMIR_FICHA_EPI_PDF;

    public static final String METODO_OBTENER_FIS_FIF = "getFisAndFifByCodExp";
    public static final String ACCIOSOAP_METODO_OBTENER_FIS_FIF = NAMESPACE + METODO_OBTENER_FIS_FIF;

  public static final String METODO_CAMBIAR_VALOR_VARIABLE_UAF = "updateUafValue";
  public static final String ACCIOSOAP_METODO_CAMBIAR_VALOR_VARIABLE_UAF = NAMESPACE + METODO_CAMBIAR_VALOR_VARIABLE_UAF;

}
