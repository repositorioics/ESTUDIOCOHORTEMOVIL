package com.sts_ni.estudiocohortecssfv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.EghDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;

/**
 * Clase para controlar la interfaz que muestra los resultados del examen EGH
 */
public class EghResultadoActivity extends Activity {

    private Context CONTEXT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.egh_resultado_layout);

        this.CONTEXT = this;
        //rest of the code
        EghDTO egh = (EghDTO) getIntent().getSerializableExtra("eghResultado");

        EditText edtxtColorEGH = (EditText)findViewById(R.id.edtxtColorEGH);
        EditText edtxtConsistencia = (EditText)findViewById(R.id.edtxtConsistencia);
        EditText edtxtRestosAlimenticios = (EditText)findViewById(R.id.edtxtRestosAlimenticios);
        EditText edtxtMucus = (EditText)findViewById(R.id.edtxtMucus);
        EditText edtxtPHegh = (EditText)findViewById(R.id.edtxtPHegh);
        EditText edtxtSangreOculta = (EditText)findViewById(R.id.edtxtSangreOculta);
        EditText edtxtBacterias = (EditText)findViewById(R.id.edtxtBacterias);
        EditText edtxtLevaduras = (EditText)findViewById(R.id.edtxtLevaduras);
        EditText edtxtLeucocitos = (EditText)findViewById(R.id.edtxtLeucocitos);
        EditText edtxtEritrocitos = (EditText)findViewById(R.id.edtxtEritrocitos);
        EditText edtxtFilamentosMucosos = (EditText)findViewById(R.id.edtxtFilamentosMucosos);
        EditText edtxtEColi = (EditText)findViewById(R.id.edtxtEColi);
        EditText edtxtEndolimaxNana = (EditText)findViewById(R.id.edtxtEndolimaxNana);
        EditText edtxtEHistolytica = (EditText)findViewById(R.id.edtxtEHistolytica);
        EditText edtxtGardiaLambia = (EditText)findViewById(R.id.edtxtGardiaLambia);
        EditText edtxtTrichuris = (EditText)findViewById(R.id.edtxtTrichuris);
        EditText edtxtHymenolepis = (EditText)findViewById(R.id.edtxtHymenolepis);
        EditText edtxtStrongyloide = (EditText)findViewById(R.id.edtxtStrongyloide);
        EditText edtxtUnicinarias = (EditText)findViewById(R.id.edtxtUnicinarias);
        EditText edtxtEnterovirus = (EditText)findViewById(R.id.edtxtEnterovirus);
        EditText edtxtObservacionesEGH = (EditText)findViewById(R.id.edtxtObservacionesEGH);

        edtxtColorEGH.setText(String.valueOf(egh.getColor()));
        edtxtConsistencia.setText(String.valueOf(egh.getConsistencia()));
        edtxtRestosAlimenticios.setText(String.valueOf(egh.getRestosAlimenticios()));
        edtxtMucus.setText(String.valueOf(egh.getMucus()));
        edtxtPHegh.setText(String.valueOf(egh.getPh()));
        edtxtSangreOculta.setText(String.valueOf(egh.getSangreOculta()));
        edtxtBacterias.setText(String.valueOf(egh.getBacterias()));
        edtxtLevaduras.setText(String.valueOf(egh.getLevaduras()));
        edtxtLeucocitos.setText(String.valueOf(egh.getLeucocitos()));
        edtxtEritrocitos.setText(String.valueOf(egh.getEritrocitos()));
        edtxtFilamentosMucosos.setText(String.valueOf(egh.getFilamentosMucosos()));
        edtxtEColi.setText(String.valueOf(egh.getEColi()));
        edtxtEndolimaxNana.setText(String.valueOf(egh.getEndolimaxNana()));
        edtxtEHistolytica.setText(String.valueOf(egh.getEHistolytica()));
        edtxtGardiaLambia.setText(String.valueOf(egh.getGardiaAmblia()));
        edtxtTrichuris.setText(String.valueOf(egh.getTrichuris()));
        edtxtHymenolepis.setText(String.valueOf(egh.getHymenolepisNana()));
        edtxtStrongyloide.setText(String.valueOf(egh.getStrongyloideStercolaris()));
        edtxtUnicinarias.setText(String.valueOf(egh.getUnicinarias()));
        edtxtEnterovirus.setText(String.valueOf(egh.getEnterovirus()));
        edtxtObservacionesEGH.setText(String.valueOf(egh.getObservaciones()));
    }

    public void onClick_btnRegreso(View view) {
        try{
            EghDTO egh = (EghDTO) getIntent().getSerializableExtra("eghResultado");
            InicioDTO pacienteSeleccionado = (InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado");
            CabeceraSintomaDTO cabSintoma = (CabeceraSintomaDTO) getIntent().getSerializableExtra("cabeceraSintoma");

            Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
            intent.putExtra("indice",1);
            intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);
            intent.putExtra("cabeceraSintoma", cabSintoma);
            intent.putExtra("eghResultado", egh);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            MensajesHelper.mostrarMensajeError(this, e.getMessage(), getString(R.string.app_name), null);
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
