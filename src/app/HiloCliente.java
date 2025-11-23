package app;

import modelo.GestorJugadores;
import modelo.Jugador;
import red.Mensaje;
import red.Servidor;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class HiloCliente extends Thread {

    private static final HashMap<Jugador, ObjectOutputStream> conexiones = new HashMap<>();

    private Socket socket;
    private Servidor servidor;
    private GestorJugadores gestor;
    private Jugador jugador;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public HiloCliente(Socket socket, Servidor servidor, GestorJugadores gestor) {
        this.socket = socket;
        this.servidor = servidor;
        this.gestor = gestor;
    }

    @Override
    public void run() {
        try {

            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());


            Mensaje registro = (Mensaje) in.readObject();

            String nombre = registro.getRemitente();

            jugador = new Jugador(nombre);
            gestor.agregarJugador(jugador);


            conexiones.put(jugador, out);

            System.out.println("Jugador conectado: " + nombre);


            servidor.enviarATodos(
                    new Mensaje(
                            "ACTUALIZAR_JUGADORES",
                            "SERVIDOR",
                            java.util.Map.of("jugadores", gestor.obtenerNombresJugadores())
                    )
            );


            while (true) {
                Mensaje m = (Mensaje) in.readObject();
                servidor.procesarMensaje(m);
            }

        } catch (Exception e) {
            System.out.println("Cliente desconectado: " +
                    (jugador != null ? jugador.getNombre() : "DESCONOCIDO")
            );
        }
    }


    public static synchronized void enviarATodos(Mensaje m) {
        conexiones.values().forEach(out -> {
            try { out.writeObject(m); }
            catch (IOException ignored) {}
        });
    }


    public static synchronized void enviarAJugador(Mensaje m, Jugador j) {
        try { conexiones.get(j).writeObject(m); }
        catch (Exception ignored) {}
    }
}
