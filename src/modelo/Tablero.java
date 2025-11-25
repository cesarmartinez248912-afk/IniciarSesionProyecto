package modelo;

import java.io.Serializable;
import java.util.*;

public class Tablero implements Serializable {

    private Map<ColorFicha, List<Ficha>> fichasPorColor;
    private Map<ColorFicha, Integer> posicionesIniciales;
    private Map<ColorFicha, Integer> posicionesEntradaPasillo;

    // El tablero tiene 68 casillas en total en el camino circular
    private static final int CASILLAS_TABLERO = 68;
    private static final int CASILLAS_PASILLO = 7; // Pasillo de color hasta meta

    public Tablero() {
        fichasPorColor = new HashMap<>();
        posicionesIniciales = new HashMap<>();
        posicionesEntradaPasillo = new HashMap<>();
    }

    public void inicializarPara(int numJugadores, List<ColorFicha> colores) {
        // POSICIONES CORREGIDAS - CASILLAS DE SALIDA REALES DEL PARCHÍS
        posicionesIniciales.put(ColorFicha.ROJO, 5);      // Salida Rojo - posición correcta
        posicionesIniciales.put(ColorFicha.AZUL, 22);     // Salida Azul - posición correcta
        posicionesIniciales.put(ColorFicha.VERDE, 39);    // Salida Verde - posición correcta
        posicionesIniciales.put(ColorFicha.AMARILLO, 56); // Salida Amarillo - posición correcta

        // Posiciones donde se entra al pasillo final (CORREGIDAS)
        posicionesEntradaPasillo.put(ColorFicha.ROJO, 68);
        posicionesEntradaPasillo.put(ColorFicha.AZUL, 17);
        posicionesEntradaPasillo.put(ColorFicha.VERDE, 34);
        posicionesEntradaPasillo.put(ColorFicha.AMARILLO, 51);

        for (ColorFicha color : colores) {
            List<Ficha> fichas = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                fichas.add(new Ficha(i, color));
            }
            fichasPorColor.put(color, fichas);
        }

        System.out.println("Tablero inicializado para " + numJugadores + " jugadores con 4 fichas cada uno.");
        System.out.println("Posiciones de salida: ROJO=5, AZUL=22, VERDE=39, AMARILLO=56");
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

        // Si está en casa, solo sale con 5
        if (ficha.isEnCasa()) {
            return pasos == 5;
        }

        // Si está en el pasillo de color
        if (ficha.isEnPasillo()) {
            int nuevaPosicionPasillo = ficha.getPosicionPasillo() + pasos;
            return nuevaPosicionPasillo <= CASILLAS_PASILLO;
        }

        // Si está en el tablero circular
        int posActual = ficha.getPosicion();
        int posEntrada = posicionesEntradaPasillo.get(ficha.getColor());

        // Calcular cuántas casillas faltan para la entrada del pasillo
        int casillasHastaEntrada;
        if (posActual <= posEntrada) {
            casillasHastaEntrada = posEntrada - posActual;
        } else {
            casillasHastaEntrada = (CASILLAS_TABLERO - posActual) + posEntrada;
        }

        // Si el movimiento lo lleva a o más allá de la entrada del pasillo
        if (pasos > casillasHastaEntrada) {
            int casillasEnPasillo = pasos - casillasHastaEntrada - 1;
            return casillasEnPasillo <= CASILLAS_PASILLO;
        }

        return true;
    }

    // MÉTODO NUEVO PARA SALIR DE CASA
    public void salirDeCasa(Ficha ficha) {
        if (!ficha.isEnCasa()) return;

        int posSalida = posicionesIniciales.get(ficha.getColor());
        ficha.setPosicion(posSalida);
        ficha.setEnCasa(false);
        System.out.println("¡Ficha " + ficha.getId() + " de " + ficha.getColor() + " SALE de casa a posición " + posSalida + "!");

        // Verificar captura inmediata al salir
        verificarCaptura(ficha);
    }

    public void moverFicha(Ficha ficha, int pasos) {
        // Salir de casa con 5 - USAR MÉTODO CORREGIDO
        if (ficha.isEnCasa() && pasos == 5) {
            salirDeCasa(ficha);
            return;
        }

        // Movimiento en el pasillo
        if (ficha.isEnPasillo()) {
            int nuevaPosicionPasillo = ficha.getPosicionPasillo() + pasos;
            ficha.setPosicionPasillo(nuevaPosicionPasillo);

            if (nuevaPosicionPasillo >= CASILLAS_PASILLO) {
                ficha.setEnMeta(true);
                System.out.println("¡Ficha " + ficha.getId() + " de " + ficha.getColor() + " llegó a META!");
            }
            return;
        }

        // Movimiento en el tablero circular
        int posActual = ficha.getPosicion();
        int posEntrada = posicionesEntradaPasillo.get(ficha.getColor());

        // Calcular casillas hasta la entrada del pasillo
        int casillasHastaEntrada;
        if (posActual <= posEntrada) {
            casillasHastaEntrada = posEntrada - posActual;
        } else {
            casillasHastaEntrada = (CASILLAS_TABLERO - posActual) + posEntrada;
        }

        // Si llega o pasa la entrada del pasillo
        if (pasos > casillasHastaEntrada) {
            ficha.setEnPasillo(true);
            int casillasEnPasillo = pasos - casillasHastaEntrada - 1;
            ficha.setPosicionPasillo(casillasEnPasillo);

            if (casillasEnPasillo >= CASILLAS_PASILLO) {
                ficha.setEnMeta(true);
                System.out.println("¡Ficha " + ficha.getId() + " de " + ficha.getColor() + " llegó a META!");
            } else {
                System.out.println("Ficha " + ficha.getId() + " de " + ficha.getColor() + " entra al pasillo en posición " + casillasEnPasillo);
            }
        } else {
            // Movimiento normal en el tablero circular
            int nuevaPos = (posActual + pasos) % CASILLAS_TABLERO;
            // Ajustar para que no sea 0
            if (nuevaPos == 0) nuevaPos = CASILLAS_TABLERO;
            ficha.setPosicion(nuevaPos);
            System.out.println("Ficha " + ficha.getId() + " de " + ficha.getColor() + " se mueve a posición " + nuevaPos);

            // Verificar capturas
            verificarCaptura(ficha);
        }
    }

    private void verificarCaptura(Ficha fichaMovida) {
        if (fichaMovida.isEnPasillo() || fichaMovida.isEnMeta()) return;

        for (ColorFicha color : fichasPorColor.keySet()) {
            if (color == fichaMovida.getColor()) continue;

            for (Ficha otraFicha : obtenerFichas(color)) {
                if (!otraFicha.isEnCasa() && !otraFicha.isEnMeta() && !otraFicha.isEnPasillo() &&
                        otraFicha.getPosicion() == fichaMovida.getPosicion()) {
                    System.out.println("¡CAPTURA! Ficha " + otraFicha.getId() + " de " + otraFicha.getColor() + " regresa a casa");
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
                fichaInfo.put("enCasa", ficha.isEnCasa());
                fichaInfo.put("enPasillo", ficha.isEnPasillo());
                fichaInfo.put("posicionPasillo", ficha.getPosicionPasillo());
                fichasInfo.add(fichaInfo);
            }
            estado.put(color.toString(), fichasInfo);
        }

        return estado;
    }

    // MÉTODO PARA OBTENER POSICIÓN DE SALIDA (útil para debugging)
    public int getPosicionSalida(ColorFicha color) {
        return posicionesIniciales.getOrDefault(color, -1);
    }
}