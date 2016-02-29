package dam.aventuradebolsillo;

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

public class MetodosBD {

    HttpClient httpclient = new DefaultHttpClient();
    List<NameValuePair> nameValuePairs;
    HttpPost httppost;

    public boolean insertarNuevoUsuario(String usuario, String contraseña, String clase){
        if(clase.equals("Guerrero")){
            httppost = new HttpPost("http://aventuradebolsillo.esy.es/php/insertarNuevoGuerrero.php");
        }else if(clase.equals("Mago")){
            httppost = new HttpPost("http://aventuradebolsillo.esy.es/php/insertarNuevoMago.php");
        }else{
            httppost = new HttpPost("http://aventuradebolsillo.esy.es/php/insertarNuevoPicaro.php");
        }
        //A partir de aquí comenzamos a recoger los datos
        nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
        nameValuePairs.add(new BasicNameValuePair("contraseña", contraseña));

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
        return false;
    }
}