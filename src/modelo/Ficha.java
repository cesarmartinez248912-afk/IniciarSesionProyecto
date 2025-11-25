package modelo;

import java.io.Serializable;

public class Ficha implements Serializable {
    private int id;
    private ColorFicha color;
    private int posicion; // -1 = casa, 0-67 = tablero circular
    private boolean enCasa;
    private boolean enPasillo;
    private int posicionPasillo; // 0-7 en el pasillo de color
    private boolean enMeta;

    public Ficha(int id, ColorFicha color) {
        this.id = id;
        this.color = color;
        this.posicion = -1;
        this.enCasa = true;
        this.enPasillo = false;
        this.posicionPasillo = 0;
        this.enMeta = false;
    }

    public int getId() { return id; }
    public ColorFicha getColor() { return color; }
    public int getPosicion() { return posicion; }
    public boolean isEnMeta() { return enMeta; }
    public boolean isEnCasa() { return enCasa; }
    public boolean isEnPasillo() { return enPasillo; }
    public int getPosicionPasillo() { return posicionPasillo; }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public void setEnCasa(boolean enCasa) {
        this.enCasa = enCasa;
    }

    public void setEnPasillo(boolean enPasillo) {
        this.enPasillo = enPasillo;
    }

    public void setPosicionPasillo(int posicionPasillo) {
        this.posicionPasillo = posicionPasillo;
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
        this.enCasa = true;
        this.enPasillo = false;
        this.posicionPasillo = 0;
        this.enMeta = false;
    }
}