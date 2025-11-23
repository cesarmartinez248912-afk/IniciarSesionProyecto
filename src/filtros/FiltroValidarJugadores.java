package filtros;

import modelo.GestorJugadores;
import red.Mensaje;
import java.util.Map;

public class FiltroValidarJugadores implements Filtro {

    private Filtro siguiente;
    private GestorJugadores gestor;

    public FiltroValidarJugadores(Filtro siguiente, GestorJugadores gestor) {
        this.siguiente = siguiente;
        this.gestor = gestor;
    }

    @Override
    public Mensaje procesar(Mensaje m) {
        System.out.println("[FiltroValidarJugadores] Validando cantidad de jugadores...");

        int n = gestor.cantidadJugadores();
        if (n < 2 || n > 4) {
            m.setTipo("ERROR");
            m.setDatos(Map.of("mensaje", "Número inválido de jugadores"));
            return m;
        }

        System.out.println("[FiltroValidarJugadores] OK: " + n + " jugadores");
        return siguiente.procesar(m);
    }

}
