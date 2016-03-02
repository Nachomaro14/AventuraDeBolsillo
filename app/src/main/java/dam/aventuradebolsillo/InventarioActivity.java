package dam.aventuradebolsillo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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

public class InventarioActivity extends Activity{

    ListView lista;
    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;
    ArrayList<ListaInventario> datos = new ArrayList<ListaInventario>();
    ListaInventario inventario;
    Activity ctx = this;

    ArrayList<Object> info = new ArrayList<>();

    int efecto;
    String tipo;
    int ide;

    String usuario;

    int vida, energia, vidaMax, energiaMax;
    String clase;

    ImageView jugador;
    TextView v, e;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String u = b.getString("Usuario");
            usuario = u;
            System.out.println(usuario);
        }

        jugador = (ImageView) findViewById(R.id.imageView14);
        v = (TextView) findViewById(R.id.textView34);
        e = (TextView) findViewById(R.id.textView36);

        new Estadisticas(InventarioActivity.this).execute();
        new Lista(InventarioActivity.this).execute();

        lista = (ListView) findViewById(R.id.listView);

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ListaInventario inventario = (ListaInventario) lista.getAdapter().getItem(position);

                final String[] items = {"Usar"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                builder.setTitle(inventario.get_nombre())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        try {
                                            efecto = inventario.get_efecto();
                                            tipo = inventario.get_tipo();
                                            ide = inventario.get_id();
                                            new Uso(InventarioActivity.this).execute();
                                            new Lista(InventarioActivity.this).execute();
                                        } catch (SecurityException e) {
                                            Toast.makeText(getBaseContext(), "Error al usar objeto.", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                }
                            }
                        });

                builder.create().show();
                return false;
            }
        });
    }

    public boolean usar() {
        if(tipo.equals("PV")){
            if(vida != vidaMax){
                if(efecto + vida > vidaMax){
                    httppost = new HttpPost("http://aventuradebolsillo.esy.es/rellenarPV.php");
                    vida = vidaMax;
                }else{
                    httppost = new HttpPost("http://aventuradebolsillo.esy.es/usarObjetoPV.php");
                    vida = vida + efecto;
                }
            }else{
                return false;
            }
        }else{
            if(energia != energiaMax){
                if(efecto + energia > energiaMax){
                    httppost = new HttpPost("http://aventuradebolsillo.esy.es/rellenarPE.php");
                    energia = energiaMax;
                }else{
                    httppost = new HttpPost("http://aventuradebolsillo.esy.es/usarObjetoPE.php");
                    energia = energia + efecto;
                }
            }else{
                return false;
            }
        }
        nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
        nameValuePairs.add(new BasicNameValuePair("efecto", String.valueOf(efecto)));
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(ide)));

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

    public class Uso extends AsyncTask<String, Float, String> {

        private Activity ctx;

        Uso(Activity ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if (usar()) {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        v.setText(String.valueOf(vida));
                        e.setText(String.valueOf(energia));
                        Toast.makeText(getBaseContext(), "Objeto usado.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "No has podido usar el objeto", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;

        }
    }

    public boolean obtenerListaObjetos(){
        datos.clear();
        String data = log();
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try{
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    inventario = new ListaInventario(Integer.parseInt(jsonArrayChild.optString("IdObjeto")),
                            jsonArrayChild.optString("Nombre"),
                            Integer.parseInt(jsonArrayChild.optString("Efecto")),
                            jsonArrayChild.optString("Tipo"),
                            Integer.parseInt(jsonArrayChild.optString("Precio")));
                    datos.add(inventario);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public class Lista extends AsyncTask<String, Float, String> {

        private Activity ctx;

        Lista(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(obtenerListaObjetos()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lista.setAdapter(new AdaptadorInventario(InventarioActivity.this, R.layout.fila_inventario, datos) {
                            @Override
                            public void onEntrada(Object entrada, View view) {
                                if (entrada != null) {
                                    TextView nombre = (TextView) view.findViewById(R.id.nombreObjeto);
                                    if (nombre != null)
                                        nombre.setText(((ListaInventario) entrada).get_nombre());

                                    TextView efecto = (TextView) view.findViewById(R.id.efectoObjeto);
                                    if (efecto != null)
                                        efecto.setText("" + ((ListaInventario) entrada).get_efecto());

                                    TextView tipo = (TextView) view.findViewById(R.id.tipoEfecto);
                                    if (tipo != null)
                                        tipo.setText(((ListaInventario) entrada).get_tipo());

                                    ImageView imagen = (ImageView) view.findViewById(R.id.imagenObjeto);
                                    if (((ListaInventario) entrada).get_tipo().equals("PV")) {
                                        imagen.setImageResource(R.drawable.pocionroja);
                                    } else if (((ListaInventario) entrada).get_tipo().equals("PE")) {
                                        imagen.setImageResource(R.drawable.pocionazul);
                                    }

                                    TextView precio = (TextView) view.findViewById(R.id.precioObjeto);
                                    if (precio != null)
                                        precio.setText("" + ((ListaInventario) entrada).get_precio());
                                }
                            }
                        });
                    }
                });
            }
            return null;
        }
    }

    public String log() {
        httppost = new HttpPost("http://aventuradebolsillo.esy.es/obtenerObjetosInventario.php");
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

    public String logInfo() {
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
        String data = logInfo();
        System.out.println("Devuelve: " + data);
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try{
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    info.add(jsonArrayChild.optString("Clase"));
                    info.add(jsonArrayChild.optInt("Experiencia"));
                    info.add(jsonArrayChild.optInt("ExpMax"));
                    info.add(jsonArrayChild.optInt("Nivel"));
                    info.add(jsonArrayChild.optInt("Oro"));
                    info.add(jsonArrayChild.optInt("PV"));
                    info.add(jsonArrayChild.optInt("PVMaximo"));
                    info.add(jsonArrayChild.optInt("PE"));
                    info.add(jsonArrayChild.optInt("PEMaximo"));
                    info.add(jsonArrayChild.optInt("Ataque"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public class Estadisticas extends AsyncTask<String, Float, String> {

        private Activity ctx;

        Estadisticas(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(filtrarDatos()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vida = (Integer) info.get(5);
                        vidaMax = (Integer) info.get(6);
                        energia = (Integer) info.get(7);
                        energiaMax = (Integer) info.get(8);
                        clase = (String) info.get(0);
                        v.setText(String.valueOf(vida));
                        e.setText(String.valueOf(energia));

                        if(clase.equals("Guerrero")){
                            jugador.setImageResource(R.drawable.guerrero);
                        }else if(clase.equals("Mago")){
                            jugador.setImageResource(R.drawable.mago);
                        }else if (clase.equals("Picaro")){
                            jugador.setImageResource(R.drawable.picaro);
                        }
                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
    }
}