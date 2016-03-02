package dam.aventuradebolsillo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class PrincipalActivity extends Activity{
    ImageButton btnCombatir, btnEquipo, btnInventario, btnTienda, btnPosada;

    String usuario;

    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;

    int oro;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        btnCombatir = (ImageButton) findViewById(R.id.btnCombatir);
        btnEquipo = (ImageButton) findViewById(R.id.btnEquipo);
        btnInventario = (ImageButton) findViewById(R.id.btnInventario);
        btnTienda = (ImageButton) findViewById(R.id.btnTienda);
        btnPosada = (ImageButton) findViewById(R.id.btnPosada);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String u = b.getString("Usuario");
            usuario = u;
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int alto = height / 5;

        btnCombatir.setLayoutParams(new LinearLayout.LayoutParams(width, alto));
        btnEquipo.setLayoutParams(new LinearLayout.LayoutParams(width, alto));
        btnInventario.setLayoutParams(new LinearLayout.LayoutParams(width, alto));
        btnTienda.setLayoutParams(new LinearLayout.LayoutParams(width, alto));
        btnPosada.setLayoutParams(new LinearLayout.LayoutParams(width, alto));

        btnPosada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder d = new AlertDialog.Builder(PrincipalActivity.this);
                d.setTitle("Posada");
                d.setMessage("Bienvenido, aventurero.\n¿Estás seguro de que quieres pasar la noche en la posada?\n(El coste es de 25 monedas de oro)");
                d.setCancelable(false);
                d.setPositiveButton("Sí, gracias", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Posada(PrincipalActivity.this).execute();
                    }
                });
                d.setNegativeButton("No, gracias", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                d.show();

            }
        });
    }

    public void accederCombate(View view){
        Intent intent = new Intent(this, CombateActivity.class);
        Bundle b = new Bundle();
        b.putString("Usuario", usuario);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void accederEquipo(View view){
        Intent intent = new Intent(this, EquipoActivity.class);
        Bundle b = new Bundle();
        b.putString("Usuario", usuario);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void accederInventario(View view){
        Intent intent = new Intent(this, InventarioActivity.class);
        startActivity(intent);
    }

    public void accederTienda(View view){
        Intent intent = new Intent(this, TiendaActivity.class);
        startActivity(intent);
    }

    public String phpComprobarOro() {
        httppost = new HttpPost("http://aventuradebolsillo.esy.es/obtenerOro.php");
        nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
        HttpResponse response;
        String resultado = "";

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            resultado = convertStreamToString(instream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    private boolean filtrarDatos(){
        String data = phpComprobarOro();
        System.out.println("Devuelve: " + data);
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try{
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    oro = jsonArrayChild.optInt("Oro");
                    if(oro >= 25){
                        return true;
                    }else {
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public class Posada extends AsyncTask<String, Float, String> {

        private Activity ctx;

        Posada(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(filtrarDatos()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new Iniciar(PrincipalActivity.this).execute();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "No tienes oro suficiente", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }

    public String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        }else{
            return "";
        }
    }

    public boolean phpPosada() {
        httppost = new HttpPost("http://aventuradebolsillo.esy.es/posada.php");
        nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public class Iniciar extends AsyncTask<String, Float, String> {

        private Activity ctx;

        Iniciar(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(phpPosada()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(ctx, "¡Gracias!\n¡Vuelva pronto!\n(Tu vida y energía han sido restablecidas)", Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
            return null;
        }
    }
}