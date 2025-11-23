package filtros;

import modelo.GestorJugadores;
import modelo.Jugador;
import modelo.ColorFicha;
import modelo.Tablero;
import red.Mensaje;
import red.Servidor;

import java.util.*;
import java.util.stream.Collectors;

public class PipelineIniciarPartida {

    private Tablero tablero;
    private GestorJugadores gestor;
    private Servidor servidor;
    private long timeoutConfirmaciones = 8000;

    public PipelineIniciarPartida(Tablero tablero, GestorJugadores gestor, Servidor servidor) {
        this.tablero = tablero;
        this.gestor = gestor;
        this.servidor = servidor;
    }

    public void setTimeoutConfirmaciones(long millis) { this.timeoutConfirmaciones = millis; }

    public void procesar(Mensaje m) {
        try {
            log("[Pipeline] Iniciando pipeline para mensaje: " + m.getTipo());

            int n = gestor.cantidadJugadores();
            if (n < 2 || n > 4) {
                log("[FiltroValidarJugadores] Número inválido de jugadores: " + n);
                servidor.enviarATodos(new Mensaje("ERROR", "SERVIDOR", Map.of("mensaje", "Número inválido de jugadores")));
                gestor.resetearConfirmaciones();
                return;
            }
            log("[FiltroValidarJugadores] OK: " + n + " jugadores");

            log("[FiltroConfirmaciones] Verificando confirmaciones (timeout " + timeoutConfirmaciones + "ms) ...");

            boolean ok = esperarConfirmacionesExceptoSolicitante(timeoutConfirmaciones, m.getRemitente());

            if (!ok) {
                log("[FiltroConfirmaciones] FALLO: No todos confirmaron");
                servidor.enviarATodos(new Mensaje("ERROR", "SERVIDOR", Map.of("mensaje", "No todos confirmaron")));
                gestor.resetearConfirmaciones();
                return;
            }
            log("[FiltroConfirmaciones] Todos confirmaron.");

            List<ColorFicha> colores = List.of(ColorFicha.ROJO, ColorFicha.AZUL, ColorFicha.VERDE, ColorFicha.AMARILLO)
                    .subList(0, n);

            log("[FiltroInicializarTablero] Inicializando tablero...");
            tablero.inicializarPara(n, colores);
            log("[FiltroInicializarTablero] Colores: " + colores);

            List<Jugador> orden = new ArrayList<>(gestor.obtenerJugadores());
            Collections.shuffle(orden);

            log("[FiltroAsignarTurnos] Asignando colores a jugadores...");
            for (int i = 0; i < orden.size(); i++) {
                orden.get(i).setColor(colores.get(i));
                log("  - " + orden.get(i).getNombre() + " -> " + colores.get(i));
            }

            gestor.asignarOrdenTurnos(orden);
            List<String> ordenNombres = orden.stream().map(Jugador::getNombre).collect(Collectors.toList());
            log("[FiltroAsignarTurnos] Turnos: " + ordenNombres);

            Map<String,Object> datos = new HashMap<>();
            datos.put("ordenTurnos", ordenNombres);
            datos.put("colores", colores.stream().map(ColorFicha::toString).collect(Collectors.toList()));

            log("[FiltroNotificarInicio] Enviando INICIO_PARTIDA...");
            servidor.enviarATodos(new Mensaje("INICIO_PARTIDA", "SERVIDOR", datos));
            log("[FiltroNotificarInicio] INICIO_PARTIDA enviado correctamente.");

        } catch (Exception ex) {
            log("[Pipeline ERROR] Excepción capturada:");
            ex.printStackTrace();
            try {
                servidor.enviarATodos(new Mensaje("ERROR", "SERVIDOR", Map.of("mensaje", "Error al iniciar partida: " + ex.getMessage())));
            } catch (Exception e2) {
                log("[Pipeline ERROR] No se pudo enviar mensaje de error");
            }
        } finally {
            gestor.resetearConfirmaciones();
        }
    }

    private boolean esperarConfirmacionesExceptoSolicitante(long timeoutMillis, String solicitante) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            boolean todos = gestor.obtenerJugadores().stream()
                    .filter(j -> !j.getNombre().equals(solicitante))
                    .allMatch(Jugador::haConfirmado);
            if (todos) return true;
            try { Thread.sleep(150); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        return false;
    }

    private void log(String s) {
        System.out.println(s);
    }
}