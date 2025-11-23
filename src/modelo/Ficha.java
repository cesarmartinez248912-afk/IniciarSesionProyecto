package modelo;

import java.io.Serializable;

public class Ficha implements Serializable {
    private int id;
    private ColorFicha color;
    private int posicion; // -1 = casa, 0-67 = tablero
    private boolean enMeta;

    public Ficha(int id, ColorFicha color) {
        this.id = id;
        this.color = color;
        this.posicion = -1; // inicia en casa
        this.enMeta = false;
    }

    public int getId() { return id; }
    public ColorFicha getColor() { return color; }
    public int getPosicion() { return posicion; }
    public boolean isEnMeta() { return enMeta; }
    public boolean isEnCasa() { return posicion == -1; }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public void setEnMeta(boolean enMeta) {
        this.enMeta = enMeta;
    }

    public void mover(int pasos) {
        if (!isEnCasa() && !enMeta) {
            this.posicion += pasos;
        }
    }

    public void regresarACasa() {
        this.posicion = -1;
        this.enMeta = false;
    }
}