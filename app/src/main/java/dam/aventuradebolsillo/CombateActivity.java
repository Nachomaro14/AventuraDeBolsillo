package dam.aventuradebolsillo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

public class CombateActivity extends Activity{

    Activity ctx = this;

    ImageButton atacar, especial, huir;

    ImageView enemigo, jugador;

    HttpClient httpclient = new DefaultHttpClient();
    ArrayList<NameValuePair> nameValuePairs;
    HttpPost httppost;

    String usuario;

    TextView vidaE, vidaJ, energiaJ, nombreE;

    Enemigo ene;

    int vida, energia, vidaMaxima, energiaMaxima, nivel, ataque, experiencia, expMax, oro;
    String clase;

    ArrayList<Object> info = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combate);

        atacar = (ImageButton)findViewById(R.id.btnAtacar);
        especial = (ImageButton)findViewById(R.id.btnEspecial);
        huir = (ImageButton)findViewById(R.id.btnHuir);

        vidaE = (TextView) findViewById(R.id.textView19);
        vidaJ = (TextView) findViewById(R.id.textView26);
        energiaJ = (TextView) findViewById(R.id.textView28);
        nombreE = (TextView) findViewById(R.id.nombreEnemigo);

        enemigo = (ImageView) findViewById(R.id.imagenEnemigo);
        jugador = (ImageView) findViewById(R.id.imagenJugador);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String u = b.getString("Usuario");
            usuario = u;
        }

        atacar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ataque();
            }
        });
        especial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ataqueEspecial();
            }
        });
        huir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                huir();
            }
        });

        new Estadisticas(CombateActivity.this).execute();
    }

    public void ataque() {

        //JUGADOR
        //TURNO Jugador
        int turnoJugador = (int) (Math.random() * 4 + 1);
        if (turnoJugador < 4) {
            int daño = ataque - ene.getArmadura();
            int vidaEnemigo =  Integer.parseInt(vidaE.getText().toString()) - daño;

            if(vidaEnemigo <= 0 ){
                ganasCombate(ene.getExp());
            }else{
                vidaE.setText(String.valueOf(vidaEnemigo));
                turnoEnemigo();
            }


        } else {
            Toast.makeText(ctx, "Has fallado", Toast.LENGTH_SHORT).show();
            turnoEnemigo();
        }
    }
    public void ataqueEspecial(){
        int ener = Integer.parseInt(energiaJ.getText().toString());
        if(ener >= 2){
            ener = ener - 2;
            energiaJ.setText(String.valueOf(ener));
            //JUGADOR
            //TURNO Jugador
            int turnoJugador = (int) (Math.random() * 4 + 1);
            if(turnoJugador < 4){
                int daño = (ataque * 2) - ene.getArmadura();
                int vidaEnemigo =  Integer.parseInt(vidaE.getText().toString()) - daño;

                if(vidaEnemigo <= 0 ){
                    ganasCombate(ene.getExp());
                }else{
                    vidaE.setText(String.valueOf(vidaEnemigo));
                    turnoEnemigo();
                }
            }else{
                Toast.makeText(ctx, "Has fallado", Toast.LENGTH_SHORT).show();
                turnoEnemigo();
            }
        }else{
            Toast.makeText(ctx, "No tienes suficiente energía", Toast.LENGTH_SHORT).show();
        }

    }
    public void huir(){
        int huir = (int) (Math.random() * 4 + 1);
        if(huir < 4){
            vida = Integer.parseInt(vidaJ.getText().toString());
            energia = Integer.parseInt(energiaJ.getText().toString());
            Toast.makeText(ctx, "Has huido", Toast.LENGTH_SHORT).show();
            new Actualizacion(CombateActivity.this).execute();
            super.onBackPressed();
        }else{
            Toast.makeText(ctx, "No es tu día de suerte", Toast.LENGTH_SHORT).show();
            turnoEnemigo();
        }
    }

    public void turnoEnemigo(){
        //ENEMIGO
        //TURNO ENEMIGO
        int turnoEnemigo = (int) (Math.random() * 4 + 1);
        if(turnoEnemigo < 4){
            int vidaJugador  =  Integer.parseInt(vidaJ.getText().toString())  - ene.getDaño();
            if(vidaJugador <= 0 ){
                vidaJugador = 0;
                vidaJ.setText("" + vidaJugador);
                pierdesCombate(ene.getExp());
            }else{
                vidaJ.setText("" + vidaJugador);
            }
        }else{
            Toast.makeText(ctx, "Has evadido el ataque del enemigo", Toast.LENGTH_SHORT).show();
        }

    }

    public void pierdesCombate(int expEnemigo){
        experiencia = experiencia - expEnemigo;
        if(experiencia < 0){
            bajarNivel(nivel);
        }
        Toast.makeText(ctx, "Has perdido", Toast.LENGTH_SHORT).show();
        resetearParam();
        vida = Integer.parseInt(vidaJ.getText().toString());
        energia = Integer.parseInt(energiaJ.getText().toString());
        new Actualizacion(CombateActivity.this).execute();
        super.onBackPressed();
    }
    public void ganasCombate(int expEnemigo){
        experiencia = experiencia + expEnemigo;
        oro = oro + ene.getOro();
        if(experiencia >= expMax){
            subirNivel(nivel);
        }
        Toast.makeText(ctx, "Has ganado", Toast.LENGTH_SHORT).show();
        vida = Integer.parseInt(vidaJ.getText().toString());
        energia = Integer.parseInt(energiaJ.getText().toString());
        new Actualizacion(CombateActivity.this).execute();
        super.onBackPressed();
    }
    public void bajarNivel(int nivelJugador){
        if(nivelJugador == 1){
            experiencia = 0;
            resetearParam();
        }else{
            nivel = nivel - 1;
            experiencia = expMax/2;
            resetearParam();
        }

    }
    public void subirNivel(int expExtra){
        nivel = nivel + 1;
        experiencia = 0;
        experiencia = experiencia + expExtra;

        resetearParam();
    }
    public void resetearParam(){
        vidaJ.setText("" + vidaMaxima);
        energiaJ.setText("" + energiaMaxima);
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
                        energia = (Integer) info.get(7);
                        vidaMaxima = (Integer) info.get(6);
                        energiaMaxima = (Integer) info.get(8);
                        nivel = (Integer) info.get(3);
                        ataque = (Integer) info.get(9);
                        experiencia = (Integer) info.get(1);
                        expMax = (Integer) info.get(2);
                        oro = (Integer) info.get(4);
                        clase = (String) info.get(0);

                        if(clase.equals("Guerrero")){
                            jugador.setImageResource(R.drawable.guerrero);
                        }else if(clase.equals("Mago")){
                            jugador.setImageResource(R.drawable.mago);
                        }else if (clase.equals("Picaro")){
                            jugador.setImageResource(R.drawable.picaro);
                        }

                        int nivelEnemigo;
                        if(nivel > 1){
                            int i = (int) (Math.random() * 3);
                            nivelEnemigo = nivel + (i - 1);
                        }else{
                            int i = (int) (Math.random() * 2);
                            nivelEnemigo = nivel + i;
                        }
                        ene = new Enemigo(nivelEnemigo);

                        String raza = ene.getRaza();

                        switch (raza){
                            case "Goblin":
                                enemigo.setImageResource(R.drawable.goblin);
                                break;
                            case "Gnomo":
                                enemigo.setImageResource(R.drawable.gnomo);
                                break;
                            case "Trasgo":
                                enemigo.setImageResource(R.drawable.trasgo);
                                break;
                            case "Orco":
                                enemigo.setImageResource(R.drawable.orco);
                                break;
                            case "Troll":
                                enemigo.setImageResource(R.drawable.troll);
                                break;
                            case "Gigante":
                                enemigo.setImageResource(R.drawable.gigante);
                                break;
                            case "Cieno":
                                enemigo.setImageResource(R.drawable.cieno);
                                break;
                            case "Constructo":
                                enemigo.setImageResource(R.drawable.constructo);
                                break;
                            case "Demonio":
                                enemigo.setImageResource(R.drawable.demonio);
                                break;
                            case "Dragón":
                                enemigo.setImageResource(R.drawable.dragon);
                                break;
                            case "Elemental":
                                enemigo.setImageResource(R.drawable.elemental);
                                break;
                            case "Fantasma":
                                enemigo.setImageResource(R.drawable.fantasma);
                                break;
                            case "No-Muerto":
                                enemigo.setImageResource(R.drawable.nomuerto);
                                break;
                        }
                        nombreE.setText(String.valueOf(ene.getNombre()));
                        vidaE.setText(String.valueOf(ene.getVida()));
                        vidaJ.setText(String.valueOf(vida));
                        energiaJ.setText(String.valueOf(energia));
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

    public boolean up() {
        httppost = new HttpPost("http://aventuradebolsillo.esy.es/actualizarInfoUsuario.php");
        nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
        nameValuePairs.add(new BasicNameValuePair("experiencia", "" + experiencia));
        nameValuePairs.add(new BasicNameValuePair("expMax", "" + expMax));
        nameValuePairs.add(new BasicNameValuePair("nivel", "" + nivel));
        nameValuePairs.add(new BasicNameValuePair("oro", "" + oro));
        nameValuePairs.add(new BasicNameValuePair("vida", "" + vida));
        nameValuePairs.add(new BasicNameValuePair("vidaMax", "" + vidaMaxima));
        nameValuePairs.add(new BasicNameValuePair("energia", "" + energia));
        nameValuePairs.add(new BasicNameValuePair("energiaMax", "" + energiaMaxima));
        nameValuePairs.add(new BasicNameValuePair("ataque", "" + ataque));

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
                        Toast.makeText(ctx, "BD actualizada", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "BD no actualizada", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
    }
}