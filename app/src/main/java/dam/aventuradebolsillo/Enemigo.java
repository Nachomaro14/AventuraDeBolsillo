package dam.aventuradebolsillo;

public class Enemigo {



    String[] raza = {"Goblin", "Gnomo", "Trasgo", "Orco", "Troll", "Gigante", "Cieno", "Demonio", "Elemental", "Constructo", "Dragón", "Fantasma", "No-Muerto"};
    String[] mote = {"Sanguinario", "Enclenque", "Dormilón" , "de las Sombras" , "Anciano", "Mata-Patos", "Trambólico", "de las Montañas", "Real", "Infernal", "Celestial", "Frenético"};

    String nombre;
    String razaE;
    int vida;
    int nivel;
    int exp;
    int daño;
    int armadura;
    int oro;


    public Enemigo(int nivelJugador) {

        this.nombre = obtenerNombre();
        this.vida = 12 * nivelJugador;
        this.nivel = nivelJugador;
        this.exp = obtenerExp(nivelJugador);
        this.daño = 2 * nivelJugador;
        this.armadura = 3 + nivelJugador;
        this.oro = obtenerOro(nivelJugador);


    }


    public String obtenerNombre() {

        String nombre = "";
        String mote = "";
        String raza = "";
        int numero = (int) (Math.random() * 13 + 1);
        if (numero >= 1 || numero <= 13) {
            raza = this.raza[numero - 1];
            this.razaE = raza;
        }
        int numero2 = (int) (Math.random() * 12 + 1);
        if (numero2 >= 1 || numero <= 12) {
            mote = this.mote[numero2 - 1];
        }
        nombre = raza + " " + mote;

        return nombre;
    }

    public int obtenerExp(int nivelJugador) {
        int expAux = 25;
        if (nivelJugador > 1) {
            for (int i = 1; i < nivelJugador; i++) {
                expAux = expAux + 25;
            }
        }
        return expAux;
    }

    public int obtenerOro(int nivelJugador) {
        int oroAux = 8;
        if (nivelJugador > 1) {
            for (int i = 1; i < nivelJugador; i++) {
                oroAux = oroAux + 8;
            }
        }
        return oroAux;
    }

    //GETTER

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExp() {
        return exp;
    }

    public int getDaño() {
        return daño;
    }

    public int getArmadura() {
        return armadura;
    }

    public int getOro() {
        return oro;
    }

    public String getRaza() {
        return razaE;
    }

    //SETTER

    public void setVida(int vida) {
        this.vida = vida;
    }
}
