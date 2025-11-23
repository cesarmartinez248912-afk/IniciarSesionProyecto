package modelo;

import java.io.Serializable;

public class Jugador implements Serializable {
    private String nombre;
    private ColorFicha color;
    private boolean confirmo;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.confirmo = false;
    }

    public String getNombre() { return nombre; }
    public ColorFicha getColor() { return color; }
    public void setColor(ColorFicha color) { this.color = color; }
    public boolean haConfirmado() { return confirmo; }
    public void setConfirmado(boolean c) { confirmo = c; }

    @Override
    public String toString() {
        return nombre + "(" + color + ")";
    }
}
