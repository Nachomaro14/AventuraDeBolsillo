package dam.aventuradebolsillo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class AdaptadorInventario extends BaseAdapter {

    private ArrayList<?> inventarios;
    private int R_layout_IdView;
    private Context contexto;

    public AdaptadorInventario(Context contexto, int R_layout_IdView, ArrayList<?> inventarios) {
        super();
        this.contexto = contexto;
        this.inventarios = inventarios;
        this.R_layout_IdView = R_layout_IdView;
    }

    @Override
    public View getView(int posicion, View view, ViewGroup pariente) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView, null);
        }
        onEntrada (inventarios.get(posicion), view);
        return view;
    }

    @Override
    public int getCount() {
        return inventarios.size();
    }

    @Override
    public Object getItem(int posicion) {
        return inventarios.get(posicion);
    }

    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public abstract void onEntrada (Object entrada, View view);
}