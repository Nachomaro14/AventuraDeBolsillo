package dam.aventuradebolsillo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TiendaActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);
    }

    public void accederComprar(View view){
        Intent intent = new Intent(this, ComprarActivity.class);
        startActivity(intent);
    }

    public void accederVender(View view){
        Intent intent = new Intent(this, VenderActivity.class);
        startActivity(intent);
    }
}