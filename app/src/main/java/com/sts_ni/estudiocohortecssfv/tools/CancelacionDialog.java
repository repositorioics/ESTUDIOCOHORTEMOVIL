package com.sts_ni.estudiocohortecssfv.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.sts_ni.estudiocohortecssfv.R;

/**
 * Created by MiguelLopez on 01/10/2015.
 */
public class CancelacionDialog extends DialogFragment {

    public interface DialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    DialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.activity_dialog_cancelacion, null))
                .setPositiveButton(R.string.boton_aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        if(getTargetFragment() != null) {
                            ((DialogListener)getTargetFragment()).onDialogPositiveClick(CancelacionDialog.this);
                        } else {
                            ((DialogListener)getActivity()).onDialogPositiveClick(CancelacionDialog.this);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Motivo de Cancelación");

        return alertDialog;
    }

}
