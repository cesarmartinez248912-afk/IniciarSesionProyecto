package red;

import modelo.Jugador;

public interface Servidor {
    void enviarATodos(Mensaje m);
    void enviarA(Mensaje m, Jugador jugador);
    void procesarMensaje(Mensaje mensaje);

}
