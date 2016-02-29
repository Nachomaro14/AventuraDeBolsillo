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
            httppost = new HttpPost("AQUÍ DEBEMOS INTRODUCIR LA URL DEL PHP DE 'insertarNuevoGuerrero' QUE ESTÁ EN EL SERVIDOR"); //Ejemplo: "http://192.168.0.11/picarcodigo/insert.php"
        }else if(clase.equals("Mago")){
            httppost = new HttpPost("AQUÍ DEBEMOS INTRODUCIR LA URL DEL PHP DE 'insertarNuevoMago' QUE ESTÁ EN EL SERVIDOR"); //Ejemplo: "http://192.168.0.11/picarcodigo/insert.php"
        }else{
            httppost = new HttpPost("AQUÍ DEBEMOS INTRODUCIR LA URL DEL PHP DE 'insertarNuevoPicaro' QUE ESTÁ EN EL SERVIDOR"); //Ejemplo: "http://192.168.0.11/picarcodigo/insert.php"
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