package filtros;

import modelo.GestorJugadores;
import modelo.Jugador;
import red.Mensaje;
import red.Servidor;

public class FiltroNotificarInicio implements Filtro {

    private Servidor servidor;

    public FiltroNotificarInicio(Servidor servidor, GestorJugadores gestor) {
        this.servidor = servidor;
    }

    @Override
    public Mensaje procesar(Mensaje m) {
        m.setTipo("INICIO_PARTIDA");
        servidor.enviarATodos(m);  // ✔ CORRECTO
        return m;
    }
}
