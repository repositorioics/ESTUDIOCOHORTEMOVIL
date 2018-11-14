package com.sts_ni.estudiocohortecssfv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.PerifericoResultadoDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;

/**
 * Clase para controlar la interfaz que muestra los resultados del examen Periferico
 */
public class PerifericoResultadoActivity extends Activity {

    private Context CONTEXT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.periferico_resultado_layout);

        this.CONTEXT = this;
        //rest of the code
        PerifericoResultadoDTO perifericoResultado = (PerifericoResultadoDTO) getIntent().getSerializableExtra("perifericoResultado");

        EditText edtxtAnisocitosis = (EditText)findViewById(R.id.edtxtAnisocitosis);
        EditText edtxtAnisocromia = (EditText)findViewById(R.id.edtxtAnisocromia);
        EditText edtxtPoiquilicitosis = (EditText)findViewById(R.id.edtxtPoiquilicitosis);
        EditText edtxtLinfositosAtipicos = (EditText)findViewById(R.id.edtxtLinfositosAtipicos);
        EditText edtxtObservacionesSB = (EditText)findViewById(R.id.edtxtObservacionesSB);
        EditText edtxtObservacionesIP = (EditText)findViewById(R.id.edtxtObservacionesIP);

        edtxtAnisocitosis.setText(String.valueOf(perifericoResultado.getAnisocitosis()));
        edtxtAnisocromia.setText(String.valueOf(perifericoResultado.getAnisocromia()));
        edtxtPoiquilicitosis.setText(String.valueOf(perifericoResultado.getPoiquilocitosis()));
        edtxtLinfositosAtipicos.setText(String.valueOf(perifericoResultado.getLinfocitosAtipicos()));
        edtxtObservacionesSB.setText(String.valueOf(perifericoResultado.getObservacionSblanca()));
        edtxtObservacionesIP.setText(String.valueOf(perifericoResultado.getObservacionPlaqueta()));
    }

    public void onClick_btnRegreso(View view) {
        try{
            PerifericoResultadoDTO perifericoResultado = (PerifericoResultadoDTO) getIntent().getSerializableExtra("perifericoResultado");
            InicioDTO pacienteSeleccionado = (InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado");
            CabeceraSintomaDTO cabSintoma = (CabeceraSintomaDTO) getIntent().getSerializableExtra("cabeceraSintoma");


            Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
            intent.putExtra("indice",1);
            intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);
            intent.putExtra("cabeceraSintoma", cabSintoma);
            intent.putExtra("malariaResultado", perifericoResultado);
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
