package filtros;

import modelo.ColorFicha;
import modelo.GestorJugadores;
import modelo.Jugador;
import red.Mensaje;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FiltroAsignarTurnos implements Filtro {

    private Filtro siguiente;
    private GestorJugadores gestor;

    public FiltroAsignarTurnos(Filtro siguiente, GestorJugadores gestor) {
        this.siguiente = siguiente;
        this.gestor = gestor;
    }

    @Override
    public Mensaje procesar(Mensaje m) {
        System.out.println("[FiltroAsignarTurnos] Asignando turnos...");

        List<Jugador> jugadores = gestor.obtenerJugadores();
        Collections.shuffle(jugadores);

        @SuppressWarnings("unchecked")
        List<ColorFicha> colores = (List<ColorFicha>) m.getDatos().get("coloresAsignados");

        for (int i = 0; i < jugadores.size(); i++) {
            jugadores.get(i).setColor(colores.get(i));
        }

        gestor.asignarOrdenTurnos(jugadores);

        m.setDatos(Map.of(
                "ordenTurnos", jugadores.stream().map(Jugador::getNombre).toList(),
                "colores", colores));

        System.out.println("[FiltroAsignarTurnos] Turnos: " + m.getDatos());

        return siguiente.procesar(m);
    }

}
