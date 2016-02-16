package dam.aventuradebolsillo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class InicioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
    }

    public void accederRegistro(View view){
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }

    public void accederPrincipal(View view){
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }
}