package com.sts_ni.estudiocohortecssfv;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.sts_ni.estudiocohortecssfv.dto.PerifericoResultadoDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DescargarApkActivity extends Activity {
    public ProgressDialog PD_CREATE;
    private Context CONTEXT;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.CONTEXT = this;
        //setContentView(R.layout.periferico_resultado_layout);
        Intent i;
        PackageManager manager = getPackageManager();
        try {
            i = manager.getLaunchIntentForPackage("CSSFV.hoja_consulta");
            if (i == null)
                throw new PackageManager.NameNotFoundException();
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            UpdateApk downloadAndInstall = new UpdateApk();
            //progress.setCancelable(false);
            //progress.setMessage("Downloading...");
            downloadAndInstall.setContext(getApplicationContext());
            downloadAndInstall.execute(getResources().getString(R.string.descargar_apk));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class UpdateApk extends AsyncTask<String,Void,Void> {
        //ProgressDialog progressDialog;
        AlertDialog alertDialog;
        int status = 0;

        private Context context;
        public void setContext(Context context){
            this.context = context;
            //this.progressDialog = progress;
        }

        public void onPreExecute() {
            PD_CREATE = new ProgressDialog(CONTEXT);
            PD_CREATE.setTitle(getResources().getString(R.string.title_obteniendo));
            PD_CREATE.setMessage(getResources().getString(R.string.msj_espere_por_favor));
            PD_CREATE.setCancelable(false);
            PD_CREATE.setIndeterminate(true);
            PD_CREATE.show();
            //progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                //File sdcard = Environment.getExternalStorageDirectory();
                String sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                String fileName = "estudioCohorte_CSSFV.apk";
                sdcard += fileName;
                final Uri uri = Uri.parse("file://" + sdcard);

                File myDir = new File(sdcard);
                myDir.mkdirs();
                //File outputFile = new File(myDir, "estudioCohorte_CSSFV.apk");
                if(myDir.exists()){
                    myDir.delete();
                }
                FileOutputStream fos = new FileOutputStream(myDir);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.flush();
                fos.close();
                is.close();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                context.startActivity(intent);


            } catch (FileNotFoundException fnfe) {
                status = 1;
                Log.e("File", "FileNotFoundException! " + fnfe);
            }

            catch(Exception e)
            {
                Log.e("UpdateAPP", "Exception " + e);
            }
            return null;

        }

        public void onPostExecute(Void unused) {
            //progressDialog.dismiss();
            PD_CREATE.dismiss();
            if(status == 1)
                Toast.makeText(context,"Apk Not Available",Toast.LENGTH_LONG).show();
        }
    }
}
