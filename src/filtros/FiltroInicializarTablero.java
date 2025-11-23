package filtros;

import modelo.ColorFicha;
import modelo.GestorJugadores;
import modelo.Tablero;
import red.Mensaje;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FiltroInicializarTablero implements Filtro {

    private Filtro siguiente;
    private Tablero tablero;
    private GestorJugadores gestor;

    public FiltroInicializarTablero(Filtro siguiente, Tablero tablero, GestorJugadores gestor) {
        this.siguiente = siguiente;
        this.tablero = tablero;
        this.gestor = gestor;
    }

    @Override
    public Mensaje procesar(Mensaje m) {
        System.out.println("[FiltroInicializarTablero] Inicializando tablero...");

        int jugadores = gestor.cantidadJugadores();

        List<ColorFicha> colores = List.of(ColorFicha.ROJO, ColorFicha.AZUL,
                        ColorFicha.VERDE, ColorFicha.AMARILLO)
                .subList(0, jugadores);

        tablero.inicializarPara(jugadores, colores);

        System.out.println("[FiltroInicializarTablero] Colores: " + colores);

        m.setDatos(Map.of("coloresAsignados", colores));
        return siguiente.procesar(m);
    }

}
