package dam.aventuradebolsillo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class AdaptadorVenta extends BaseAdapter {

    private ArrayList<?> ventas;
    private int R_layout_IdView;
    private Context contexto;

    public AdaptadorVenta(Context contexto, int R_layout_IdView, ArrayList<?> ventas) {
        super();
        this.contexto = contexto;
        this.ventas = ventas;
        this.R_layout_IdView = R_layout_IdView;
    }

    @Override
    public View getView(int posicion, View view, ViewGroup pariente) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView, null);
        }
        onEntrada (ventas.get(posicion), view);
        return view;
    }

    @Override
    public int getCount() {
        return ventas.size();
    }

    @Override
    public Object getItem(int posicion) {
        return ventas.get(posicion);
    }

    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public abstract void onEntrada (Object entrada, View view);
}