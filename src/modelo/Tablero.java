package modelo;

import java.io.Serializable;
import java.util.*;

public class Tablero implements Serializable {

    private Map<ColorFicha, List<Ficha>> fichasPorColor;
    private Map<ColorFicha, Integer> posicionesIniciales;

    public Tablero() {
        fichasPorColor = new HashMap<>();
        posicionesIniciales = new HashMap<>();
    }

    public void inicializarPara(int numJugadores, List<ColorFicha> colores) {
        posicionesIniciales.put(ColorFicha.ROJO, 0);
        posicionesIniciales.put(ColorFicha.AZUL, 17);
        posicionesIniciales.put(ColorFicha.VERDE, 34);
        posicionesIniciales.put(ColorFicha.AMARILLO, 51);

        for (ColorFicha color : colores) {
            List<Ficha> fichas = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                fichas.add(new Ficha(i, color));
            }
            fichasPorColor.put(color, fichas);
        }

        System.out.println("Tablero inicializado para " + numJugadores + " jugadores con 7 fichas cada uno.");
    }

    public List<Ficha> obtenerFichas(ColorFicha color) {
        return fichasPorColor.getOrDefault(color, new ArrayList<>());
    }

    public Ficha obtenerFicha(ColorFicha color, int idFicha) {
        return obtenerFichas(color).stream()
                .filter(f -> f.getId() == idFicha)
                .findFirst()
                .orElse(null);
    }

    public boolean puedeMoverse(Ficha ficha, int pasos) {
        if (ficha.isEnMeta()) return false;

        if (ficha.isEnCasa()) {
            return pasos == 5;
        }

        return (ficha.getPosicion() + pasos) < 68;
    }

    public void moverFicha(Ficha ficha, int pasos) {
        if (ficha.isEnCasa() && pasos == 5) {
            int posInicial = posicionesIniciales.get(ficha.getColor());
            ficha.setPosicion(posInicial);
        } else if (!ficha.isEnCasa() && !ficha.isEnMeta()) {
            ficha.mover(pasos);

            if (ficha.getPosicion() >= 68) {
                ficha.setEnMeta(true);
            }

            verificarCaptura(ficha);
        }
    }

    private void verificarCaptura(Ficha fichaMovida) {
        for (ColorFicha color : fichasPorColor.keySet()) {
            if (color == fichaMovida.getColor()) continue;

            for (Ficha otraFicha : obtenerFichas(color)) {
                if (!otraFicha.isEnCasa() && !otraFicha.isEnMeta() &&
                        otraFicha.getPosicion() == fichaMovida.getPosicion()) {
                    otraFicha.regresarACasa();
                }
            }
        }
    }

    public boolean jugadorGano(ColorFicha color) {
        return obtenerFichas(color).stream().allMatch(Ficha::isEnMeta);
    }

    public Map<String, Object> obtenerEstadoTablero() {
        Map<String, Object> estado = new HashMap<>();

        for (ColorFicha color : fichasPorColor.keySet()) {
            List<Map<String, Object>> fichasInfo = new ArrayList<>();
            for (Ficha ficha : obtenerFichas(color)) {
                Map<String, Object> fichaInfo = new HashMap<>();
                fichaInfo.put("id", ficha.getId());
                fichaInfo.put("posicion", ficha.getPosicion());
                fichaInfo.put("enMeta", ficha.isEnMeta());
                fichasInfo.add(fichaInfo);
            }
            estado.put(color.toString(), fichasInfo);
        }

        return estado;
    }
}