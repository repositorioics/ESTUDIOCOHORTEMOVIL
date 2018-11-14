package com.sts_ni.estudiocohortecssfv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.EgoDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;

/**
 * Clase para controlar la interfaz que muestra los resultados del examen EGO
 */
public class EgoResultadoActivity extends Activity {

    private Context CONTEXT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ego_resultado_layout);

        this.CONTEXT = this;
        //rest of the code
        EgoDTO ego = (EgoDTO) getIntent().getSerializableExtra("egoResultado");

        EditText edtxtColorEGO = (EditText)findViewById(R.id.edtxtColorEGO);
        EditText edtxtAspecto = (EditText)findViewById(R.id.edtxtAspecto);
        EditText edtxtSedimento = (EditText)findViewById(R.id.edtxtSedimento);
        EditText edtxtDensidad = (EditText)findViewById(R.id.edtxtDensidad);
        EditText edtxtProteinas = (EditText)findViewById(R.id.edtxtProteinas);
        EditText edtxtHemoglobinas = (EditText)findViewById(R.id.edtxtHemoglobinas);
        EditText edtxtCuerpoCetonico = (EditText)findViewById(R.id.edtxtCuerpoCetonico);
        EditText edtxtPHego = (EditText)findViewById(R.id.edtxtPHego);
        EditText edtxtUrobilinogeno = (EditText)findViewById(R.id.edtxtUrobilinogeno);
        EditText edtxtGlucosa = (EditText)findViewById(R.id.edtxtGlucosa);
        EditText edtxtBilirrubinasEGO = (EditText)findViewById(R.id.edtxtBilirrubinasEGO);
        EditText edtxtNitritos = (EditText)findViewById(R.id.edtxtNitritos);
        EditText edtxtCelulasEpiteliales = (EditText)findViewById(R.id.edtxtCelulasEpiteliales);
        EditText edtxtLeucocitosEGO = (EditText)findViewById(R.id.edtxtLeucocitosEGO);
        EditText edtxtEritrocitosEGO = (EditText)findViewById(R.id.edtxtEritrocitosEGO);
        EditText edtxtCilindros = (EditText)findViewById(R.id.edtxtCilindros);
        EditText edtxtCristales = (EditText)findViewById(R.id.edtxtCristales);
        EditText edtxtHilosMucosos = (EditText)findViewById(R.id.edtxtHilosMucosos);
        EditText edtxtBacteriasEGO = (EditText)findViewById(R.id.edtxtBacteriasEGO);
        EditText edtxtLevadurasEGO = (EditText)findViewById(R.id.edtxtLevadurasEGO);
        EditText edtxtObservacionesEGO = (EditText)findViewById(R.id.edtxtObservacionesEGO);

        edtxtColorEGO.setText(String.valueOf(ego.getColor()));
        edtxtAspecto.setText(String .valueOf(ego.getAspecto()));
        edtxtSedimento.setText(String.valueOf(ego.getSedimento()));
        edtxtDensidad.setText(String.valueOf(ego.getDensidad()));
        edtxtProteinas.setText(String.valueOf(ego.getProteinas()));
        edtxtHemoglobinas.setText(String.valueOf(ego.getHomoglobinas()));
        edtxtCuerpoCetonico.setText(String.valueOf(ego.getCuerpoCetonico()));
        edtxtPHego.setText(String.valueOf(ego.getPh()));
        edtxtUrobilinogeno.setText(String.valueOf(ego.getUrobilinogeno()));
        edtxtGlucosa.setText(String.valueOf(ego.getGlucosa()));
        edtxtBilirrubinasEGO.setText(String.valueOf(ego.getBilirrubinas()));
        edtxtNitritos.setText(String.valueOf(ego.getNitritos()));
        edtxtCelulasEpiteliales.setText(String.valueOf(ego.getCelulasEpiteliales()));
        edtxtLeucocitosEGO.setText(String.valueOf(ego.getLeucositos()));
        edtxtEritrocitosEGO.setText(String.valueOf(ego.getEritrocitos()));
        edtxtCilindros.setText(String.valueOf(ego.getCilindros()));
        edtxtCristales.setText(String.valueOf(ego.getCristales()));
        edtxtHilosMucosos.setText(String.valueOf(ego.getHilosMucosos()));
        edtxtBacteriasEGO.setText(String .valueOf(ego.getBacterias()));
        edtxtLevadurasEGO.setText(String.valueOf(ego.getLevaduras()));
        edtxtObservacionesEGO.setText(String.valueOf(ego.getObservaciones()));

    }

    public void onClick_btnRegreso(View view) {
        try{
            EgoDTO ego = (EgoDTO) getIntent().getSerializableExtra("egoResultado");
            InicioDTO pacienteSeleccionado = (InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado");
            CabeceraSintomaDTO cabSintoma = (CabeceraSintomaDTO) getIntent().getSerializableExtra("cabeceraSintoma");

            Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
            intent.putExtra("indice",1);
            intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);
            intent.putExtra("cabeceraSintoma", cabSintoma);
            intent.putExtra("egoResultado", ego);
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
