package red;

import app.HiloCliente;
import modelo.*;
import filtros.PipelineIniciarPartida;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServidorImpl implements Servidor {

    private ServerSocket server;
    private GestorJugadores gestor;
    private Tablero tablero;
    private GestorTurnos gestorTurnos;
    private PipelineIniciarPartida pipeline;
    private Random random;
    private String jugadorSolicitante = null;
    private Map<String, Integer> ultimosDados = new HashMap<>();

    public ServidorImpl(int puerto, Tablero tablero, GestorJugadores gestor) throws IOException {
        this.server = new ServerSocket(puerto);
        this.gestor = gestor;
        this.tablero = tablero;
        this.gestorTurnos = new GestorTurnos();
        this.pipeline = new PipelineIniciarPartida(tablero, gestor, this);
        this.pipeline.setTimeoutConfirmaciones(30000);
        this.random = new Random();
    }

    public void iniciar() throws IOException {
        System.out.println("Servidor listo en puerto " + server.getLocalPort());
        while (true) {
            Socket cliente = server.accept();
            new HiloCliente(cliente, this, gestor).start();
        }
    }

    public synchronized void procesarMensaje(Mensaje m) {
        String tipo = m.getTipo();
        String remitente = m.getRemitente();

        switch (tipo) {
            case "REGISTRO_NOMBRE":
                manejarRegistro(m);
                break;

            case "SOLICITUD_INICIO":
                manejarSolicitudInicio(m);
                break;

            case "CONFIRMACION":
                manejarConfirmacion(m);
                break;

            case "roll":
                manejarTirarDado(m);
                break;

            case "move":
                manejarMovimiento(m);
                break;

            default:
                System.out.println("Mensaje: " + tipo);
        }
    }

    private void manejarRegistro(Mensaje m) {
        String nombre = m.getRemitente();
        Jugador nuevo = new Jugador(nombre);
        gestor.agregarJugador(nuevo);

        enviarATodos(new Mensaje(
                "ACTUALIZAR_JUGADORES",
                "SERVIDOR",
                Map.of("jugadores", gestor.obtenerNombresJugadores())
        ));
    }

    private void manejarSolicitudInicio(Mensaje m) {
        jugadorSolicitante = m.getRemitente();

        Jugador solicitante = gestor.buscarPorNombre(jugadorSolicitante);
        if (solicitante != null) {
            solicitante.setConfirmado(true);
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("solicitante", jugadorSolicitante);
        datos.put("mensaje", "El jugador " + jugadorSolicitante + " solicita iniciar partida");

        List<Jugador> jugadores = gestor.obtenerJugadores();
        for (Jugador j : jugadores) {
            if (!j.getNombre().equals(jugadorSolicitante)) {
                enviarA(new Mensaje("SOLICITUD_CONFIRMAR", "SERVIDOR", datos), j);
            }
        }

        new Thread(() -> {
            try {
                pipeline.procesar(m);

                List<Jugador> jugadoresList = gestor.obtenerJugadores();
                gestorTurnos.inicializar(jugadoresList);

                notificarTurno();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                jugadorSolicitante = null;
            }
        }).start();
    }

    private void manejarConfirmacion(Mensaje m) {
        gestor.confirmarJugador(m.getRemitente());
        System.out.println("[Confirmado] " + m.getRemitente());

        long confirmados = gestor.obtenerJugadores().stream().filter(j -> j.haConfirmado()).count();
        long total = gestor.cantidadJugadores();

        Map<String, Object> datos = new HashMap<>();
        datos.put("confirmados", confirmados);
        datos.put("total", total);
        datos.put("mensaje", confirmados + " de " + total + " jugadores confirmaron");

        enviarATodos(new Mensaje("ESTADO_CONFIRMACIONES", "SERVIDOR", datos));
    }

    private void manejarTirarDado(Mensaje m) {
        if (!gestorTurnos.isPartidaIniciada()) {
            enviarError(m.getRemitente(), "La partida no ha iniciado");
            return;
        }

        if (!gestorTurnos.esTurnoDe(m.getRemitente())) {
            enviarError(m.getRemitente(), "No es tu turno");
            return;
        }

        int resultado = random.nextInt(6) + 1;
        ultimosDados.put(m.getRemitente(), resultado);

        Jugador jugador = gestor.buscarPorNombre(m.getRemitente());

        Map<String, Object> datos = new HashMap<>();
        datos.put("player", jugador.getNombre());
        datos.put("resultado", resultado);

        enviarATodos(new Mensaje("RESULTADO_DADO", "SERVIDOR", datos));

        // Verificar si el jugador puede mover alguna ficha
        ColorFicha color = jugador.getColor();
        boolean puedeJugar = verificarSiPuedeJugar(color, resultado);

        if (!puedeJugar) {
            // Esperar 2 segundos y pasar turno automáticamente
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    synchronized (this) {
                        Map<String, Object> datosInfo = new HashMap<>();
                        datosInfo.put("mensaje", jugador.getNombre() + " no puede mover. Pasa el turno.");
                        enviarATodos(new Mensaje("INFO_TURNO", "SERVIDOR", datosInfo));

                        gestorTurnos.avanzarTurno();
                        ultimosDados.remove(m.getRemitente());
                        notificarTurno();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private boolean verificarSiPuedeJugar(ColorFicha color, int resultado) {
        List<Ficha> fichas = tablero.obtenerFichas(color);

        for (Ficha ficha : fichas) {
            if (tablero.puedeMoverse(ficha, resultado)) {
                return true;
            }
        }

        return false;
    }

    private void manejarMovimiento(Mensaje m) {
        if (!gestorTurnos.isPartidaIniciada()) {
            enviarError(m.getRemitente(), "La partida no ha iniciado");
            return;
        }

        if (!gestorTurnos.esTurnoDe(m.getRemitente())) {
            enviarError(m.getRemitente(), "No es tu turno");
            return;
        }

        try {
            int idFicha = (int) m.getDatos().get("piece");
            int pasos = (int) m.getDatos().get("steps");

            // Verificar que use el dado que tiró
            Integer dadoTirado = ultimosDados.get(m.getRemitente());
            if (dadoTirado == null || dadoTirado != pasos) {
                enviarError(m.getRemitente(), "Debes usar el resultado del dado que tiraste");
                return;
            }

            Jugador jugador = gestor.buscarPorNombre(m.getRemitente());
            ColorFicha color = jugador.getColor();

            Ficha ficha = tablero.obtenerFicha(color, idFicha);

            if (ficha == null) {
                enviarError(m.getRemitente(), "Ficha no encontrada");
                return;
            }

            if (!tablero.puedeMoverse(ficha, pasos)) {
                enviarError(m.getRemitente(), "Movimiento inválido");
                return;
            }

            tablero.moverFicha(ficha, pasos);

            Map<String, Object> datos = new HashMap<>();
            datos.put("board", tablero.obtenerEstadoTablero());
            enviarATodos(new Mensaje("state", "SERVIDOR", datos));

            if (tablero.jugadorGano(color)) {
                Map<String, Object> datosGanador = new HashMap<>();
                datosGanador.put("ganador", m.getRemitente());
                enviarATodos(new Mensaje("FIN_PARTIDA", "SERVIDOR", datosGanador));
                gestorTurnos.finalizarPartida();
                return;
            }

            gestorTurnos.avanzarTurno();
            ultimosDados.remove(m.getRemitente());
            notificarTurno();

        } catch (Exception e) {
            e.printStackTrace();
            enviarError(m.getRemitente(), "Error al procesar movimiento");
        }
    }

    private void notificarTurno() {
        Jugador actual = gestorTurnos.obtenerJugadorActual();
        if (actual == null) return;

        Map<String, Object> datos = new HashMap<>();
        datos.put("jugador", actual.getNombre());
        datos.put("color", actual.getColor().toString());

        enviarATodos(new Mensaje("TURNO", "SERVIDOR", datos));
    }

    private void enviarError(String destinatario, String textoError) {
        Jugador j = gestor.buscarPorNombre(destinatario);
        if (j != null) {
            Map<String, Object> datos = Map.of("mensaje", textoError);
            enviarA(new Mensaje("ERROR", "SERVIDOR", datos), j);
        }
    }

    @Override
    public void enviarATodos(Mensaje m) {
        HiloCliente.enviarATodos(m);
    }

    @Override
    public void enviarA(Mensaje m, Jugador jugador) {
        HiloCliente.enviarAJugador(m, jugador);
    }
}