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

            // Leer el registro inicial
            Mensaje registro = (Mensaje) in.readObject();
            String nombre = registro.getRemitente();

            jugador = new Jugador(nombre);
            gestor.agregarJugador(jugador);

            // Registrar la conexión
            conexiones.put(jugador, out);

            System.out.println("✓ Jugador conectado: " + nombre);

            // Notificar a todos los jugadores actualizados
            servidor.enviarATodos(
                    new Mensaje(
                            "ACTUALIZAR_JUGADORES",
                            "SERVIDOR",
                            java.util.Map.of("jugadores", gestor.obtenerNombresJugadores())
                    )
            );

            // Escuchar mensajes del cliente
            while (true) {
                Mensaje m = (Mensaje) in.readObject();
                servidor.procesarMensaje(m);
            }

        } catch (EOFException | java.net.SocketException e) {
            // Cliente se desconectó abruptamente
            System.out.println("⚠️  Cliente desconectado abruptamente: " +
                    (jugador != null ? jugador.getNombre() : "DESCONOCIDO"));
        } catch (Exception e) {
            System.err.println("Error en HiloCliente: " + e.getMessage());
        } finally {
            limpiarConexion();
        }
    }

    private void limpiarConexion() {
        if (jugador != null) {
            // Simular mensaje de desconexión si no se envió
            servidor.procesarMensaje(new Mensaje("DESCONEXION", jugador.getNombre(), null));

            // Remover de conexiones
            conexiones.remove(jugador);
        }

        // Cerrar recursos
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }

    public static synchronized void enviarATodos(Mensaje m) {
        // Crear una copia para evitar ConcurrentModificationException
        HashMap<Jugador, ObjectOutputStream> copia = new HashMap<>(conexiones);

        copia.forEach((jugador, out) -> {
            try {
                out.writeObject(m);
                out.flush();
            } catch (IOException e) {
                System.err.println("Error al enviar a " + jugador.getNombre() + ": " + e.getMessage());
                // Remover conexión muerta
                conexiones.remove(jugador);
            }
        });
    }

    public static synchronized void enviarAJugador(Mensaje m, Jugador j) {
        try {
            ObjectOutputStream out = conexiones.get(j);
            if (out != null) {
                out.writeObject(m);
                out.flush();
            }
        } catch (Exception e) {
            System.err.println("Error al enviar a jugador " + j.getNombre() + ": " + e.getMessage());
            // Remover conexión muerta
            conexiones.remove(j);
        }
    }
}