package dam.aventuradebolsillo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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

public class EquipoActivity extends Activity {

    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;
    String usuario;
    TextView nombre, nivel, experiencia, oro, vida, energia, ataque;
    ImageView imagen;
    ArrayList<Object> info = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo);

        nombre = (TextView) findViewById(R.id.textView9);
        nivel = (TextView) findViewById(R.id.textView10);
        experiencia = (TextView) findViewById(R.id.textView11);
        oro = (TextView) findViewById(R.id.textView21);
        vida = (TextView) findViewById(R.id.textView30);
        energia = (TextView) findViewById(R.id.textView15);
        ataque = (TextView) findViewById(R.id.textView13);
        imagen = (ImageView) findViewById(R.id.imageView5);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String u = b.getString("Usuario");
            usuario = u;
        }

        new Equipo(EquipoActivity.this).execute();
    }

    public String log() {
        httppost = new HttpPost("http://aventuradebolsillo.esy.es/obtenerInfoUsuario.php");
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
        String data = log();
        System.out.println("Devuelve: " + data);
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try{
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    info.add(jsonArrayChild.optString("Nombre"));
                    info.add(jsonArrayChild.optString("Clase"));
                    info.add(jsonArrayChild.optInt("Experiencia") + "/" + jsonArrayChild.optInt("ExpMax"));
                    info.add(jsonArrayChild.optInt("Nivel"));
                    info.add(jsonArrayChild.optInt("Oro"));
                    info.add(jsonArrayChild.optInt("PV") + "/" + jsonArrayChild.optInt("PVMaximo"));
                    info.add(jsonArrayChild.optInt("PE") + "/" + jsonArrayChild.optInt("PEMaximo"));
                    info.add(jsonArrayChild.optInt("Ataque"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public class Equipo extends AsyncTask<String, Float, String> {

        private Activity ctx;

        Equipo(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(filtrarDatos()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nombre.setText(info.get(0).toString());
                        experiencia.setText(info.get(2).toString());
                        nivel.setText(info.get(3).toString());
                        oro.setText(info.get(4).toString());
                        vida.setText(info.get(5).toString());
                        energia.setText(info.get(6).toString());
                        ataque.setText(info.get(7).toString());
                        if(info.get(1).toString().equals("Guerrero")){
                            imagen.setImageResource(R.drawable.guerrero);
                        }else if(info.get(1).toString().equals("Mago")){
                            imagen.setImageResource(R.drawable.mago);
                        }else if (info.get(1).toString().equals("Picaro")){
                            imagen.setImageResource(R.drawable.picaro);
                        }
                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Credenciales inválidas", Toast.LENGTH_LONG).show();
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
}