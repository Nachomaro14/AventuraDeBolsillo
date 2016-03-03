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

public class VenderActivity extends Activity {

    ListView lista;
    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;
    ArrayList<ListaVenta> datos = new ArrayList<ListaVenta>();
    ListaVenta venta;
    Activity ctx = this;

    ArrayList<Object> info = new ArrayList<>();

    int precio;
    int ide;
    int oro;
    int precioV;

    String usuario;

    TextView o;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String u = b.getString("Usuario");
            usuario = u;
            System.out.println(usuario);
        }

        o = (TextView) findViewById(R.id.textView38);

        new Lista(VenderActivity.this).execute();
        new OroC(VenderActivity.this).execute();

        lista = (ListView) findViewById(R.id.listView2);

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ListaVenta venta = (ListaVenta) lista.getAdapter().getItem(position);

                final String[] items = {"Vender"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                builder.setTitle(venta.get_nombre())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        try {
                                            ide = venta.get_id();
                                            precio = venta.get_precio();
                                            precioV = venta.get_precio() * 75 / 100;
                                            new Venta(VenderActivity.this).execute();
                                            new Lista(VenderActivity.this).execute();
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
                    venta = new ListaVenta(Integer.parseInt(jsonArrayChild.optString("IdObjeto")),
                            jsonArrayChild.optString("Nombre"),
                            Integer.parseInt(jsonArrayChild.optString("Efecto")),
                            jsonArrayChild.optString("Tipo"),
                            Integer.parseInt(jsonArrayChild.optString("Precio")));
                    datos.add(venta);
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
                        lista.setAdapter(new AdaptadorVenta(VenderActivity.this, R.layout.fila_venta, datos) {
                            @Override
                            public void onEntrada(Object entrada, View view) {
                                if (entrada != null) {
                                    TextView nombre = (TextView) view.findViewById(R.id.nombreObjeto);
                                    if (nombre != null)
                                        nombre.setText(((ListaVenta) entrada).get_nombre());

                                    TextView efecto = (TextView) view.findViewById(R.id.efectoObjeto);
                                    if (efecto != null)
                                        efecto.setText("" + ((ListaVenta) entrada).get_efecto());

                                    TextView tipo = (TextView) view.findViewById(R.id.tipoEfecto);
                                    if (tipo != null)
                                        tipo.setText(((ListaVenta) entrada).get_tipo());

                                    ImageView imagen = (ImageView) view.findViewById(R.id.imagenObjeto);
                                    if (((ListaVenta) entrada).get_tipo().equals("PV")) {
                                        imagen.setImageResource(R.drawable.pocionroja);
                                    } else if (((ListaVenta) entrada).get_tipo().equals("PE")) {
                                        imagen.setImageResource(R.drawable.pocionazul);
                                    }

                                    TextView precio = (TextView) view.findViewById(R.id.precioObjeto);
                                    if (precio != null)
                                        precio.setText("" + ((ListaVenta) entrada).get_precio() * 75 / 100);
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

    public class Venta extends AsyncTask<String, Float, String> {

        private Activity ctx;

        Venta(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(venderObjeto()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        oro = oro + precioV;
                        o.setText(String.valueOf(oro));
                        Toast.makeText(getBaseContext(), "El objeto ha sido vendido,\n" + precioV + " monedas de oro obtenidas", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
    }

    private boolean venderObjeto(){
        httppost = new HttpPost("http://aventuradebolsillo.esy.es/venderObjeto.php");
        nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
        nameValuePairs.add(new BasicNameValuePair("precioV", String.valueOf(precioV)));
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
}