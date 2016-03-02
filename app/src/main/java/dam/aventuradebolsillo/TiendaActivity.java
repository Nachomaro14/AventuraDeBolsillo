package dam.aventuradebolsillo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TiendaActivity extends Activity {

    String usuario;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);

        if(getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            String u = b.getString("Usuario");
            usuario = u;
        }
    }

    public void accederComprar(View view){
        Intent intent = new Intent(this, ComprarActivity.class);
        Bundle b = new Bundle();
        b.putString("Usuario", usuario);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void accederVender(View view){
        Intent intent = new Intent(this, VenderActivity.class);
        Bundle b = new Bundle();
        b.putString("Usuario", usuario);
        intent.putExtras(b);
        startActivity(intent);
    }
}