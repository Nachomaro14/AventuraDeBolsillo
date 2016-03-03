package dam.aventuradebolsillo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ComprarActivity extends Activity{

    ListView lista;
    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;
    ArrayList<ListaCompra> datos = new ArrayList<ListaCompra>();
    ListaCompra compra;
    Activity ctx = this;

    String usuario;

    int oro;

    String nombre;
    int efecto;
    String tipo;
    int precio;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String u = b.getString("Usuario");
            usuario = u;
            System.out.println(usuario);
        }

        new Lista(ComprarActivity.this).execute();
        new OroC(ComprarActivity.this).execute();

        lista = (ListView) findViewById(R.id.listView3);

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ListaCompra compra = (ListaCompra) lista.getAdapter().getItem(position);

                final String[] items = {"Comprar"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                builder.setTitle(compra.get_nombre())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        try {
                                            precio = compra.get_precio();
                                            nombre = compra.get_nombre();
                                            efecto = compra.get_efecto();
                                            tipo = compra.get_tipo();
                                            if(precio <= oro){
                                                new Compra(ComprarActivity.this).execute();
                                            }else{
                                                Toast.makeText(getBaseContext(), "No tienes suficiente oro", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (SecurityException e) {
                                            Toast.makeText(getBaseContext(), "Error al realizar compra.", Toast.LENGTH_SHORT).show();
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
                    compra = new ListaCompra(jsonArrayChild.optString("Nombre"),
                            Integer.parseInt(jsonArrayChild.optString("Efecto")),
                            jsonArrayChild.optString("Tipo"),
                            Integer.parseInt(jsonArrayChild.optString("Precio")));
                    datos.add(compra);
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
                        lista.setAdapter(new AdaptadorCompra(ComprarActivity.this, R.layout.fila_compra, datos) {
                            @Override
                            public void onEntrada(Object entrada, View view) {
                                if (entrada != null) {
                                    TextView nombre = (TextView) view.findViewById(R.id.nombreObjeto);
                                    if (nombre != null)
                                        nombre.setText(((ListaCompra) entrada).get_nombre());

                                    TextView efecto = (TextView) view.findViewById(R.id.efectoObjeto);
                                    if (efecto != null)
                                        efecto.setText("" + ((ListaCompra) entrada).get_efecto());

                                    TextView tipo = (TextView) view.findViewById(R.id.tipoEfecto);
                                    if (tipo != null)
                                        tipo.setText(((ListaCompra) entrada).get_tipo());

                                    ImageView imagen = (ImageView) view.findViewById(R.id.imagenObjeto);
                                    if (((ListaCompra) entrada).get_tipo().equals("PV")) {
                                        imagen.setImageResource(R.drawable.pocionroja);
                                    } else if (((ListaCompra) entrada).get_tipo().equals("PE")) {
                                        imagen.setImageResource(R.drawable.pocionazul);
                                    }

                                    TextView precio = (TextView) view.findViewById(R.id.precioObjeto);
                                    if (precio != null)
                                        precio.setText("" + ((ListaCompra) entrada).get_precio());
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
        httppost = new HttpPost("http://aventuradebolsillo.esy.es/obtenerObjetosTienda.php");
        HttpResponse response;
        String resultado = "";

        try {
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

    public class OroC extends AsyncTask<String, Float, String> {

        private Activity ctx;

        OroC(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(filtrarDatos()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TextView o = (TextView) findViewById(R.id.textView40);
                            o.setText(String.valueOf(oro));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "No tienes oro suficiente", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
    }

    private boolean filtrarDatos() {
        String data = phpComprobarOro();
        System.out.println("Devuelve: " + data);
        if (!data.equalsIgnoreCase("")) {
            JSONObject json;
            try {
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("info");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    oro = jsonArrayChild.optInt("Oro");
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
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

    public boolean logObjeto() {
        httppost = new HttpPost("http://aventuradebolsillo.esy.es/comprarObjeto.php");
        nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
        nameValuePairs.add(new BasicNameValuePair("nombre", nombre));
        nameValuePairs.add(new BasicNameValuePair("efecto", String.valueOf(efecto)));
        nameValuePairs.add(new BasicNameValuePair("tipo", tipo));
        nameValuePairs.add(new BasicNameValuePair("precio", String.valueOf(precio)));

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

    public class Compra extends AsyncTask<String, Float, String> {

        private Activity ctx;

        Compra(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(oro >= precio){
                if(logObjeto()){
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            oro = oro - precio;
                            TextView o = (TextView) findViewById(R.id.textView40);
                            o.setText(String.valueOf(oro));
                            Toast.makeText(ctx, nombre + " comprado", Toast.LENGTH_SHORT).show();
                            new Actualizacion(ComprarActivity.this).execute();
                        }
                    });
                }else{
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ctx, "No has podido comprar el objeto", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }else{
                Toast.makeText(ctx, "No tienes suficiente oro", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    public boolean up() {
        httppost = new HttpPost("http://aventuradebolsillo.esy.es/actualizarOroUsuario.php");
        nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
        nameValuePairs.add(new BasicNameValuePair("oro", "" + oro));

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

    public class Actualizacion extends AsyncTask<String, Float, String> {

        private Activity ctx;

        Actualizacion(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(up()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
            return null;
        }
    }
}