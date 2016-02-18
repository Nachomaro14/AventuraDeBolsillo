package dam.aventuradebolsillo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class PrincipalActivity extends Activity{
    ImageButton btnCombatir, btnEquipo, btnInventario, btnTienda, btnPosada;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        btnCombatir = (ImageButton) findViewById(R.id.btnCombatir);
        btnEquipo = (ImageButton) findViewById(R.id.btnEquipo);
        btnInventario = (ImageButton) findViewById(R.id.btnInventario);
        btnTienda = (ImageButton) findViewById(R.id.btnTienda);
        btnPosada = (ImageButton) findViewById(R.id.btnPosada);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int alto = height / 5;

        btnCombatir.setLayoutParams(new LinearLayout.LayoutParams(width, alto));
        btnEquipo.setLayoutParams(new LinearLayout.LayoutParams(width, alto));
        btnInventario.setLayoutParams(new LinearLayout.LayoutParams(width, alto));
        btnTienda.setLayoutParams(new LinearLayout.LayoutParams(width, alto));
        btnPosada.setLayoutParams(new LinearLayout.LayoutParams(width, alto));
    }

    public void accederCombate(View view){
        Intent intent = new Intent(this, CombateActivity.class);
        startActivity(intent);
    }

    public void accederEquipo(View view){
        Intent intent = new Intent(this, EquipoActivity.class);
        startActivity(intent);
    }

    public void accederInventario(View view){
        Intent intent = new Intent(this, InventarioActivity.class);
        startActivity(intent);
    }

    public void accederTienda(View view){
        Intent intent = new Intent(this, TiendaActivity.class);
        startActivity(intent);
    }
}