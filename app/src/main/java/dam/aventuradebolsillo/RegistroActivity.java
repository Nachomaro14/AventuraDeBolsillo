package dam.aventuradebolsillo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class RegistroActivity extends Activity{

    ImageButton registrar;
    RadioButton guerrero, mago, picaro;
    EditText usuario, contrasena;
    HttpClient httpclient = new DefaultHttpClient();
    List<NameValuePair> nameValuePairs;
    HttpPost httppost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        registrar = (ImageButton) findViewById(R.id.btnNuevoUsuario);
        guerrero = (RadioButton) findViewById(R.id.checkGuerrero);
        mago = (RadioButton) findViewById(R.id.checkMago);
        picaro = (RadioButton) findViewById(R.id.checkPicaro);

        usuario = (EditText) findViewById(R.id.txtNuevoUsuario);
        contrasena = (EditText) findViewById(R.id.txtNuevaContraseña);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Registrar(RegistroActivity.this).execute();
            }
        });
    }

    public boolean insertarNuevoUsuario(){
        String u = usuario.getText().toString();
        String p = contrasena.getText().toString();
        String c = "";
        if(guerrero.isChecked()){
            c = "Guerrero";
        }else if(mago.isChecked()){
            c = "Mago";
        }else{
            c = "Picaro";
        }
        if(!u.equals("") && !p.equals("") && !c.equals("")){
            if(c.equals("Guerrero")){
                httppost = new HttpPost("http://aventuradebolsillo.esy.es/insertarNuevoGuerrero.php");//PV: 10, PE: 4, Ataque: 8
            }else if(c.equals("Mago")){
                httppost = new HttpPost("http://aventuradebolsillo.esy.es/insertarNuevoMago.php");//PV: 5, PE: 5, Ataque: 13
            }else{
                httppost = new HttpPost("http://aventuradebolsillo.esy.es/insertarNuevoPicaro.php");//PV: 7, PE: 4, Ataque: 11
            }

            nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("usuario", u));
            nameValuePairs.add(new BasicNameValuePair("contrasena", p));

            try{
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpclient.execute(httppost);
                return true;
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }catch (ClientProtocolException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public class Registrar extends AsyncTask<String, String, String>{

        private Activity ctx;

        Registrar(Activity ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            if(insertarNuevoUsuario()){
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Creado un nuevo usuario", Toast.LENGTH_LONG).show();
                        usuario.setText("");
                        contrasena.setText("");
                    }
                });
            }else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "No ha sido posible crear el nuevo usuario", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }
}