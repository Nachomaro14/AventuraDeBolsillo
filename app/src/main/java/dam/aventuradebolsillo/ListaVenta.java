package dam.aventuradebolsillo;

public class ListaVenta {
    private String nombre;
    private int efecto;
    private String tipo;
    private int precio;
    private int id;

    public ListaVenta (int id, String nombre, int efecto, String tipo, int precio) {
        this.id = id;
        this.nombre = nombre;
        this.efecto = efecto;
        this.tipo = tipo;
        this.precio = precio;
    }
    public int get_id(){
        return id;
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