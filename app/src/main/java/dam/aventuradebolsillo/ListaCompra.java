package dam.aventuradebolsillo;

public class ListaCompra {
    private String nombre;
    private int efecto;
    private String tipo;
    private int precio;

    public ListaCompra (String nombre, int efecto, String tipo, int precio) {
        this.nombre = nombre;
        this.efecto = efecto;
        this.tipo = tipo;
        this.precio = precio;
    }

    public String get_nombre() {
        return nombre;
    }

    public int get_efecto() {
        return efecto;
    }

    public String get_tipo() {
        return tipo;
    }

    public int get_precio() {
        return precio;
    }
}