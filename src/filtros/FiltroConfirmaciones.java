package filtros;

import modelo.GestorJugadores;
import red.Mensaje;
import java.util.Map;

public class FiltroConfirmaciones implements Filtro {

    private Filtro siguiente;
    private GestorJugadores gestor;

    public FiltroConfirmaciones(Filtro siguiente, GestorJugadores gestor) {
        this.siguiente = siguiente;
        this.gestor = gestor;
    }

    @Override
    public Mensaje procesar(Mensaje m) {
        System.out.println("[FiltroConfirmaciones] Verificando confirmaciones...");

        if (gestor.cantidadJugadores() < 4) {
            boolean ok = gestor.esperarConfirmaciones(5000);
            if (!ok) {
                System.out.println("[FiltroConfirmaciones] FALLO: No todos confirmaron");
                m.setTipo("ERROR");
                m.setDatos(Map.of("mensaje", "No todos confirmaron"));
                return m;
            }
        }

        System.out.println("[FiltroConfirmaciones] Todos confirmaron.");
        return siguiente.procesar(m);
    }

}
