package app;

import red.Mensaje;
import ui.VentanaInicial;
import ui.VentanaJuego;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MainClienteSwing {

    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static String miNombre;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaInicial inicio = new VentanaInicial();

            inicio.getBotonConectar().addActionListener(e -> {
                try {
                    String nombre = inicio.getNombre().trim();
                    if (nombre.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Debe ingresar un nombre");
                        return;
                    }
                    conectar(inicio.getIP(), nombre);
                    inicio.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al conectar");
                }
            });
        });
    }

    private static void conectar(String ip, String nombre) throws Exception {
        miNombre = nombre;
        socket = new Socket(ip, 5000);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        out.writeObject(new Mensaje("REGISTRO_NOMBRE", nombre, null));

        VentanaJuego juego = new VentanaJuego();
        juego.imprimir("Conectado como: " + nombre);

        new Thread(() -> {
            while (true) {
                try {
                    Mensaje m = (Mensaje) in.readObject();
                    procesarMensaje(m, juego);
                } catch (Exception ignored) {
                    juego.imprimir("Desconectado del servidor");
                    break;
                }
            }
        }).start();

        juego.getSolicitarInicio().addActionListener(ev -> {
            juego.getSolicitarInicio().setEnabled(false);
            enviar(new Mensaje("SOLICITUD_INICIO", nombre, null));
        });

        juego.getConfirmar().addActionListener(ev -> {
            juego.getConfirmar().setEnabled(false);
            enviar(new Mensaje("CONFIRMACION", nombre, null));
        });

        juego.getTirarDado().addActionListener(ev -> {
            enviar(new Mensaje("roll", nombre, null));
        });

        juego.getMoverFicha().addActionListener(ev -> {
            int idFicha = juego.getIdFicha();
            int pasos = juego.getPasos();

            if (pasos == 0) {
                juego.imprimir("[ERROR] Debes tirar el dado primero");
                return;
            }

            juego.imprimir("→ Moviendo ficha " + idFicha + " con " + pasos + " pasos...");

            Map<String, Object> datos = new HashMap<>();
            datos.put("piece", idFicha);
            datos.put("steps", pasos);

            enviar(new Mensaje("move", nombre, datos));
        });
    }

    private static void procesarMensaje(Mensaje m, VentanaJuego juego) {
        String tipo = m.getTipo();

        switch (tipo) {
            case "ACTUALIZAR_JUGADORES":
                @SuppressWarnings("unchecked")
                java.util.List<String> lista = (java.util.List<String>) m.getDatos().get("jugadores");
                juego.actualizarListaJugadores(lista);
                juego.imprimir("Jugadores: " + lista);
                break;

            case "SOLICITUD_CONFIRMAR":
                String solicitante = (String) m.getDatos().get("solicitante");
                if (solicitante != null && !solicitante.equals("null")) {
                    juego.imprimir("[SERVIDOR] El jugador " + solicitante + " solicita iniciar partida");
                } else {
                    juego.imprimir("[SERVIDOR] Un jugador solicita iniciar partida");
                }
                juego.imprimir("[SERVIDOR] PRESIONA EL BOTÓN CONFIRMAR");
                juego.getConfirmar().setEnabled(true);
                break;

            case "ESTADO_CONFIRMACIONES":
                long confirmados = ((Number) m.getDatos().get("confirmados")).longValue();
                long total = ((Number) m.getDatos().get("total")).longValue();
                juego.imprimir("→ Confirmados: " + confirmados + "/" + total);
                break;

            case "INICIO_PARTIDA":
                @SuppressWarnings("unchecked")
                java.util.List<String> orden = (java.util.List<String>) m.getDatos().get("ordenTurnos");
                @SuppressWarnings("unchecked")
                java.util.List<?> colores = (java.util.List<?>) m.getDatos().get("colores");
                juego.imprimir("=== PARTIDA INICIADA ===");
                for (int i = 0; i < orden.size(); i++) {
                    String colorStr = colores.get(i).toString();
                    juego.imprimir((i + 1) + ". " + orden.get(i) + " - " + colorStr);
                }
                juego.getSolicitarInicio().setEnabled(false);
                juego.getConfirmar().setEnabled(false);
                break;

            case "TURNO":
                String jugador = (String) m.getDatos().get("jugador");
                String color = (String) m.getDatos().get("color");
                juego.actualizarTurno(jugador, color);

                if (jugador.equals(miNombre)) {
                    juego.imprimir("");
                    juego.imprimir("╔════════════════════════════════╗");
                    juego.imprimir("║      >>> ES TU TURNO <<<       ║");
                    juego.imprimir("╚════════════════════════════════╝");
                    juego.imprimir("1. Presiona 'Tirar dado'");
                    juego.imprimir("2. Escribe el número de ficha (1-7) que quieres mover");
                    juego.imprimir("3. Presiona 'Mover'");
                    juego.imprimir("");
                } else {
                    juego.imprimir("Turno de: " + jugador + " (" + color + ")");
                }
                break;

            case "RESULTADO_DADO":
                String jugadorDado = (String) m.getDatos().get("player");
                int resultado = (int) m.getDatos().get("resultado");
                juego.actualizarDado(resultado);

                if (jugadorDado.equals(miNombre)) {
                    juego.imprimir("");
                    juego.imprimir("🎲 Sacaste: " + resultado);
                    juego.imprimir("Ahora elige qué ficha mover (1-7) y presiona 'Mover'");
                } else {
                    juego.imprimir(jugadorDado + " sacó: " + resultado);
                }
                break;

            case "state":
                @SuppressWarnings("unchecked")
                Map<String, Object> board = (Map<String, Object>) m.getDatos().get("board");
                juego.actualizarTablero(board);
                juego.imprimir("✓ Movimiento realizado");
                mostrarEstadoFichas(board, miNombre, juego);
                break;

            case "FIN_PARTIDA":
                String ganador = (String) m.getDatos().get("ganador");
                juego.imprimir("=== FIN DE PARTIDA ===");
                juego.imprimir("GANADOR: " + ganador);
                break;

            case "ERROR":
                String error = (String) m.getDatos().get("mensaje");
                juego.imprimir("[ERROR] " + error);
                if (error.contains("confirmaron")) {
                    juego.getSolicitarInicio().setEnabled(true);
                    juego.getConfirmar().setEnabled(false);
                }
                break;

            default:
                juego.imprimir("Servidor: " + tipo);
        }
    }

    private static void enviar(Mensaje m) {
        try {
            out.writeObject(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void mostrarEstadoFichas(Map<String, Object> board, String nombreJugador, VentanaJuego juego) {
        juego.imprimir("");
        juego.imprimir("--- Estado de tus fichas ---");

        for (String colorStr : board.keySet()) {
            Object obj = board.get(colorStr);
            if (obj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Map<String, Object>> fichas = (java.util.List<Map<String, Object>>) obj;

                for (Map<String, Object> f : fichas) {
                    int id = (int) f.get("id");
                    int pos = (int) f.get("posicion");
                    boolean meta = (boolean) f.get("enMeta");

                    String estado;
                    if (meta) {
                        estado = "META ✓";
                    } else if (pos == -1) {
                        estado = "En casa";
                    } else {
                        estado = "Posición " + pos;
                    }

                    juego.imprimir("  Ficha " + id + ": " + estado);
                }
            }
        }
        juego.imprimir("");
    }
}