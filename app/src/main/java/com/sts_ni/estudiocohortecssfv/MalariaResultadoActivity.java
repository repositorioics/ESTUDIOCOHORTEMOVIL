package com.sts_ni.estudiocohortecssfv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.sts_ni.estudiocohortecssfv.dto.CabeceraSintomaDTO;
import com.sts_ni.estudiocohortecssfv.dto.InicioDTO;
import com.sts_ni.estudiocohortecssfv.dto.MalariaResultadoDTO;
import com.sts_ni.estudiocohortecssfv.helper.MensajesHelper;
import com.sts_ni.estudiocohortecssfv.utils.StringUtils;

/**
 * Clase para controlar la interfaz que muestra los resultados del examen Malaria.
 */
public class MalariaResultadoActivity extends Activity{

    private Context CONTEXT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.malaria_resultado_layout);

        this.CONTEXT = this;
        //rest of the code
        MalariaResultadoDTO malariaResultado = (MalariaResultadoDTO) getIntent().getSerializableExtra("malariaResultado");
        CheckBox chkbPFalciparum = (CheckBox)findViewById(R.id.chkbPFalciparum);
        CheckBox chkbPVivax = (CheckBox)findViewById(R.id.chkbPVivax);
        CheckBox chkbNegativo = (CheckBox)findViewById(R.id.chkbNegativoMalaria);

        chkbPFalciparum.setChecked(!StringUtils.isNullOrEmpty(malariaResultado.getPFalciparum()) ?
                Boolean.valueOf(malariaResultado.getPFalciparum()) : false);
        chkbPVivax.setChecked(!StringUtils.isNullOrEmpty(malariaResultado.getPVivax()) ?
                Boolean.valueOf(malariaResultado.getPVivax()) : false);
        chkbNegativo.setChecked(!StringUtils.isNullOrEmpty(malariaResultado.getNegativo()) ?
                Boolean.valueOf(malariaResultado.getNegativo()) : false);
    }

    public void onClick_btnRegreso(View view) {
        try{
            MalariaResultadoDTO malariaResultado = (MalariaResultadoDTO) getIntent().getSerializableExtra("malariaResultado");
            InicioDTO pacienteSeleccionado = (InicioDTO) getIntent().getSerializableExtra("pacienteSeleccionado");
            CabeceraSintomaDTO cabSintoma = (CabeceraSintomaDTO) getIntent().getSerializableExtra("cabeceraSintoma");

            Intent intent = new Intent(CONTEXT, ConsultaActivity.class);
            intent.putExtra("indice",1);
            intent.putExtra("pacienteSeleccionado",pacienteSeleccionado);
            intent.putExtra("cabeceraSintoma", cabSintoma);
            intent.putExtra("malariaResultado", malariaResultado);
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
