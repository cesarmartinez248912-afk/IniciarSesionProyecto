package modelo;

import java.io.Serializable;
import java.util.*;

public class Tablero implements Serializable {

    private Map<ColorFicha, List<Ficha>> fichasPorColor;
    private Map<ColorFicha, Integer> posicionesIniciales;
    private Map<ColorFicha, Integer> posicionesEntradaPasillo;

    private static final int CASILLAS_TABLERO = 68;
    private static final int CASILLAS_PASILLO = 7;
    private static final int POSICION_CENTRO = 34; // Posición que lleva al centro

    // Ficha que está actualmente en el centro
    private Ficha fichaEnCentro = null;

    public Tablero() {
        fichasPorColor = new HashMap<>();
        posicionesIniciales = new HashMap<>();
        posicionesEntradaPasillo = new HashMap<>();
    }

    public void inicializarPara(int numJugadores, List<ColorFicha> colores) {
        // Casillas de SALIDA
        posicionesIniciales.put(ColorFicha.ROJO, 5);
        posicionesIniciales.put(ColorFicha.AZUL, 22);
        posicionesIniciales.put(ColorFicha.VERDE, 39);
        posicionesIniciales.put(ColorFicha.AMARILLO, 56);

        // Casillas de entrada al PASILLO
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

        System.out.println("Tablero inicializado para " + numJugadores + " jugadores");
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

    /**
     * Verifica si una ficha puede entrar al centro con el número de pasos exacto
     */
    public boolean puedeEntrarAlCentro(Ficha ficha, int pasos) {
        if (ficha.isEnCasa() || ficha.isEnPasillo() || ficha.isEnMeta() || ficha.isEnCentro()) {
            return false;
        }

        int posActual = ficha.getPosicion();
        int posDestino = posActual + pasos;

        // Verificar si con esos pasos llega exactamente a la posición del centro
        return posDestino == POSICION_CENTRO;
    }

    /**
     * Mueve una ficha al centro
     */
    public void moverAlCentro(Ficha ficha) {
        // Si hay una ficha en el centro, la "come" (regresa a casa)
        if (fichaEnCentro != null && fichaEnCentro != ficha) {
            System.out.println("¡CAPTURA EN EL CENTRO! Ficha " + fichaEnCentro.getId() +
                    " de " + fichaEnCentro.getColor() + " regresa a casa");
            fichaEnCentro.regresarACasa();
        }

        ficha.setEnCentro(true);
        ficha.setPosicion(POSICION_CENTRO);
        fichaEnCentro = ficha;
        System.out.println("¡Ficha " + ficha.getId() + " de " + ficha.getColor() +
                " entró al CENTRO SEGURO!");
    }

    /**
     * Verifica si una ficha en el centro puede salir (solo con 1)
     */
    public boolean puedeSalirDelCentro(Ficha ficha, int pasos) {
        return ficha.isEnCentro() && pasos == 1;
    }

    /**
     * Saca una ficha del centro
     */
    public void salirDelCentro(Ficha ficha) {
        if (!ficha.isEnCentro()) return;

        ficha.setEnCentro(false);
        ficha.setPosicion(POSICION_CENTRO + 1); // Sale a la siguiente casilla

        if (fichaEnCentro == ficha) {
            fichaEnCentro = null;
        }

        System.out.println("Ficha " + ficha.getId() + " de " + ficha.getColor() +
                " salió del centro a posición " + (POSICION_CENTRO + 1));
    }

    public boolean puedeMoverse(Ficha ficha, int pasos) {
        if (ficha.isEnMeta()) return false;

        // Si está en el centro, solo puede salir con 1
        if (ficha.isEnCentro()) {
            return pasos == 1;
        }

        if (ficha.isEnCasa()) {
            // Puede salir con 5 o con 1
            return pasos == 5 || pasos == 1;
        }

        if (ficha.isEnPasillo()) {
            int nuevaPosicionPasillo = ficha.getPosicionPasillo() + pasos;
            return nuevaPosicionPasillo <= CASILLAS_PASILLO;
        }

        int posActual = ficha.getPosicion();
        int posEntrada = posicionesEntradaPasillo.get(ficha.getColor());

        int casillasHastaEntrada;
        if (posActual <= posEntrada) {
            casillasHastaEntrada = posEntrada - posActual;
        } else {
            casillasHastaEntrada = (CASILLAS_TABLERO - posActual) + posEntrada;
        }

        if (pasos > casillasHastaEntrada) {
            int casillasEnPasillo = pasos - casillasHastaEntrada - 1;
            return casillasEnPasillo <= CASILLAS_PASILLO;
        }

        return true;
    }

    public void salirDeCasa(Ficha ficha) {
        if (!ficha.isEnCasa()) return;

        int posSalida = posicionesIniciales.get(ficha.getColor());
        ficha.setPosicion(posSalida);
        ficha.setEnCasa(false);
        System.out.println("¡Ficha " + ficha.getId() + " de " + ficha.getColor() + " SALE a posición " + posSalida + "!");

        verificarCaptura(ficha);
    }

    public void moverFicha(Ficha ficha, int pasos, boolean elegirCentro) {
        // Si está en el centro y saca 1, sale
        if (ficha.isEnCentro() && pasos == 1) {
            salirDelCentro(ficha);
            return;
        }

        // Salir de casa con 5 o con 1
        if (ficha.isEnCasa() && (pasos == 5 || pasos == 1)) {
            salirDeCasa(ficha);
            return;
        }

        if (ficha.isEnPasillo()) {
            int nuevaPosicionPasillo = ficha.getPosicionPasillo() + pasos;
            ficha.setPosicionPasillo(nuevaPosicionPasillo);

            if (nuevaPosicionPasillo >= CASILLAS_PASILLO) {
                ficha.setEnMeta(true);
                System.out.println("¡Ficha " + ficha.getId() + " de " + ficha.getColor() + " llegó a META!");
            }
            return;
        }

        int posActual = ficha.getPosicion();
        int posDestino = posActual + pasos;

        // Verificar si puede/quiere entrar al centro
        if (elegirCentro && posDestino == POSICION_CENTRO) {
            moverAlCentro(ficha);
            return;
        }

        int posEntrada = posicionesEntradaPasillo.get(ficha.getColor());

        int casillasHastaEntrada;
        if (posActual <= posEntrada) {
            casillasHastaEntrada = posEntrada - posActual;
        } else {
            casillasHastaEntrada = (CASILLAS_TABLERO - posActual) + posEntrada;
        }

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
            int nuevaPos = posActual + pasos;

            if (nuevaPos > CASILLAS_TABLERO) {
                nuevaPos = nuevaPos - CASILLAS_TABLERO;
            }

            ficha.setPosicion(nuevaPos);
            System.out.println("Ficha " + ficha.getId() + " de " + ficha.getColor() + " se mueve de " + posActual + " a posición " + nuevaPos);

            verificarCaptura(ficha);
        }
    }

    // Sobrecarga para mantener compatibilidad
    public void moverFicha(Ficha ficha, int pasos) {
        moverFicha(ficha, pasos, false);
    }

    private void verificarCaptura(Ficha fichaMovida) {
        if (fichaMovida.isEnPasillo() || fichaMovida.isEnMeta() || fichaMovida.isEnCentro()) return;

        for (ColorFicha color : fichasPorColor.keySet()) {
            if (color == fichaMovida.getColor()) continue;

            for (Ficha otraFicha : obtenerFichas(color)) {
                // No capturar fichas en el centro
                if (otraFicha.isEnCentro()) continue;

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

    /**
     * Convierte posición lineal a coordenadas del tablero visual
     * ROJO se mueve hacia ABAJO después de salir
     * AZUL se mueve hacia la IZQUIERDA después de salir
     */
    private Map<String, Integer> convertirPosicionACoordenadasTablero(int posicion) {
        Map<String, Integer> coords = new HashMap<>();

        if (posicion == 1) {
            coords.put("fila", 0);
            coords.put("columna", 8);
        } else if (posicion == 2) {
            coords.put("fila", 1);
            coords.put("columna", 8);
        } else if (posicion == 3) {
            coords.put("fila", 2);
            coords.put("columna", 8);
        } else if (posicion == 4) {
            coords.put("fila", 3);
            coords.put("columna", 8);
        } else if (posicion == 5) {
            coords.put("fila", 2);
            coords.put("columna", 8);
        } else if (posicion == 6) {
            coords.put("fila", 3);
            coords.put("columna", 8);
        } else if (posicion == 7) {
            coords.put("fila", 4);
            coords.put("columna", 8);
        } else if (posicion == 8) {
            coords.put("fila", 5);
            coords.put("columna", 8);
        } else if (posicion == 9) {
            coords.put("fila", 6);
            coords.put("columna", 8);
        } else if (posicion == 10) {
            coords.put("fila", 7);
            coords.put("columna", 8);
        } else if (posicion == 11) {
            coords.put("fila", 8);
            coords.put("columna", 8);
        } else if (posicion == 12) {
            coords.put("fila", 9);
            coords.put("columna", 8);
        } else if (posicion == 13) {
            coords.put("fila", 10);
            coords.put("columna", 8);
        } else if (posicion == 14) {
            coords.put("fila", 11);
            coords.put("columna", 8);
        } else if (posicion == 15) {
            coords.put("fila", 11);
            coords.put("columna", 9);
        } else if (posicion == 16) {
            coords.put("fila", 11);
            coords.put("columna", 10);
        } else if (posicion == 17) {
            coords.put("fila", 11);
            coords.put("columna", 11);
        } else if (posicion == 18) {
            coords.put("fila", 11);
            coords.put("columna", 12);
        } else if (posicion == 19) {
            coords.put("fila", 11);
            coords.put("columna", 13);
        } else if (posicion == 20) {
            coords.put("fila", 11);
            coords.put("columna", 14);
        } else if (posicion == 21) {
            coords.put("fila", 11);
            coords.put("columna", 15);
        } else if (posicion == 22) {
            coords.put("fila", 8);
            coords.put("columna", 16);
        } else if (posicion == 23) {
            coords.put("fila", 8);
            coords.put("columna", 15);
        } else if (posicion == 24) {
            coords.put("fila", 8);
            coords.put("columna", 14);
        } else if (posicion == 25) {
            coords.put("fila", 8);
            coords.put("columna", 13);
        } else if (posicion == 26) {
            coords.put("fila", 8);
            coords.put("columna", 12);
        } else if (posicion == 27) {
            coords.put("fila", 8);
            coords.put("columna", 11);
        } else if (posicion == 28) {
            coords.put("fila", 8);
            coords.put("columna", 10);
        } else if (posicion == 29) {
            coords.put("fila", 8);
            coords.put("columna", 9);
        } else if (posicion == 30) {
            coords.put("fila", 8);
            coords.put("columna", 8);
        } else if (posicion == 31) {
            coords.put("fila", 8);
            coords.put("columna", 7);
        } else if (posicion == 32) {
            coords.put("fila", 8);
            coords.put("columna", 6);
        } else if (posicion == 33) {
            coords.put("fila", 8);
            coords.put("columna", 5);
        } else if (posicion == 34) {
            // POSICIÓN DEL CENTRO
            coords.put("fila", 8);
            coords.put("columna", 4);
        } else if (posicion == 35) {
            coords.put("fila", 8);
            coords.put("columna", 3);
        } else if (posicion == 36) {
            coords.put("fila", 8);
            coords.put("columna", 2);
        } else if (posicion == 37) {
            coords.put("fila", 7);
            coords.put("columna", 2);
        } else if (posicion == 38) {
            coords.put("fila", 6);
            coords.put("columna", 2);
        } else if (posicion == 39) {
            coords.put("fila", 8);
            coords.put("columna", 2);
        } else if (posicion == 40) {
            coords.put("fila", 5);
            coords.put("columna", 2);
        } else if (posicion == 41) {
            coords.put("fila", 4);
            coords.put("columna", 2);
        } else if (posicion == 42) {
            coords.put("fila", 3);
            coords.put("columna", 2);
        } else if (posicion == 43) {
            coords.put("fila", 2);
            coords.put("columna", 2);
        } else if (posicion == 44) {
            coords.put("fila", 1);
            coords.put("columna", 2);
        } else if (posicion == 45) {
            coords.put("fila", 0);
            coords.put("columna", 2);
        } else if (posicion == 46) {
            coords.put("fila", 0);
            coords.put("columna", 3);
        } else if (posicion == 47) {
            coords.put("fila", 0);
            coords.put("columna", 4);
        } else if (posicion == 48) {
            coords.put("fila", 0);
            coords.put("columna", 5);
        } else if (posicion == 49) {
            coords.put("fila", 0);
            coords.put("columna", 6);
        } else if (posicion == 50) {
            coords.put("fila", 0);
            coords.put("columna", 7);
        } else if (posicion == 51) {
            coords.put("fila", 0);
            coords.put("columna", 8);
        } else if (posicion == 52) {
            coords.put("fila", 0);
            coords.put("columna", 9);
        } else if (posicion == 53) {
            coords.put("fila", 0);
            coords.put("columna", 10);
        } else if (posicion == 54) {
            coords.put("fila", 1);
            coords.put("columna", 10);
        } else if (posicion == 55) {
            coords.put("fila", 2);
            coords.put("columna", 10);
        } else if (posicion == 56) {
            coords.put("fila", 14);
            coords.put("columna", 8);
        } else if (posicion == 57) {
            coords.put("fila", 3);
            coords.put("columna", 10);
        } else if (posicion == 58) {
            coords.put("fila", 4);
            coords.put("columna", 10);
        } else if (posicion == 59) {
            coords.put("fila", 5);
            coords.put("columna", 10);
        } else if (posicion == 60) {
            coords.put("fila", 6);
            coords.put("columna", 10);
        } else if (posicion == 61) {
            coords.put("fila", 7);
            coords.put("columna", 10);
        } else if (posicion == 62) {
            coords.put("fila", 8);
            coords.put("columna", 10);
        } else if (posicion == 63) {
            coords.put("fila", 9);
            coords.put("columna", 10);
        } else if (posicion == 64) {
            coords.put("fila", 10);
            coords.put("columna", 10);
        } else if (posicion == 65) {
            coords.put("fila", 11);
            coords.put("columna", 10);
        } else if (posicion == 66) {
            coords.put("fila", 11);
            coords.put("columna", 9);
        } else if (posicion == 67) {
            coords.put("fila", 11);
            coords.put("columna", 8);
        } else if (posicion == 68) {
            coords.put("fila", 11);
            coords.put("columna", 7);
        }

        return coords;
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
                fichaInfo.put("enCentro", ficha.isEnCentro());
                fichaInfo.put("posicionPasillo", ficha.getPosicionPasillo());

                if (!ficha.isEnCasa() && !ficha.isEnMeta() && !ficha.isEnPasillo()) {
                    if (ficha.isEnCentro()) {
                        // Centro en fila 8, columna 8
                        fichaInfo.put("fila", 8);
                        fichaInfo.put("columna", 8);
                    } else {
                        Map<String, Integer> coords = convertirPosicionACoordenadasTablero(ficha.getPosicion());
                        fichaInfo.put("fila", coords.get("fila"));
                        fichaInfo.put("columna", coords.get("columna"));
                    }
                }

                fichasInfo.add(fichaInfo);
            }
            estado.put(color.toString(), fichasInfo);
        }

        return estado;
    }

    public int getPosicionSalida(ColorFicha color) {
        return posicionesIniciales.getOrDefault(color, -1);
    }
}