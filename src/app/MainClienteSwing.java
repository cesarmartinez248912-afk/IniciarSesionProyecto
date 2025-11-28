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
    private static volatile boolean conectado = false;
    private static Thread hiloEscucha;

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
                    conectar(inicio.getIP(), inicio.getPuerto(), nombre);
                    inicio.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al conectar: " + ex.getMessage());
                }
            });
        });
    }

    private static void conectar(String ip, int puerto, String nombre) throws Exception {
        miNombre = nombre;
        socket = new Socket(ip, puerto);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        conectado = true;

        out.writeObject(new Mensaje("REGISTRO_NOMBRE", nombre, null));

        VentanaJuego juego = new VentanaJuego();
        juego.imprimir("Conectado como: " + nombre);
        juego.imprimir("Servidor: " + ip + ":" + puerto);

        // Manejar cierre de ventana
        juego.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                desconectar(juego);
                System.exit(0);
            }
        });

        // Botón de desconectar
        juego.getBotonDesconectar().addActionListener(ev -> {
            desconectar(juego);
            juego.dispose();

            // Volver a la ventana inicial
            SwingUtilities.invokeLater(() -> {
                VentanaInicial nuevaVentana = new VentanaInicial();
                nuevaVentana.getBotonConectar().addActionListener(e2 -> {
                    try {
                        String nuevoNombre = nuevaVentana.getNombre().trim();
                        if (nuevoNombre.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar un nombre");
                            return;
                        }
                        conectar(nuevaVentana.getIP(), nuevaVentana.getPuerto(), nuevoNombre);
                        nuevaVentana.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error al conectar: " + ex.getMessage());
                    }
                });
            });
        });

        // Hilo para escuchar mensajes del servidor
        hiloEscucha = new Thread(() -> {
            while (conectado) {
                try {
                    Mensaje m = (Mensaje) in.readObject();
                    procesarMensaje(m, juego);
                } catch (Exception ignored) {
                    if (conectado) {
                        juego.imprimir("Desconectado del servidor");
                    }
                    break;
                }
            }
        });
        hiloEscucha.start();

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

    private static void desconectar(VentanaJuego juego) {
        if (!conectado) return;

        conectado = false;

        try {
            // Notificar al servidor que nos vamos
            if (out != null) {
                enviar(new Mensaje("DESCONEXION", miNombre, null));
            }

            juego.imprimir("Desconectándose...");

            // Cerrar recursos
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();

            // Interrumpir hilo de escucha
            if (hiloEscucha != null) {
                hiloEscucha.interrupt();
            }

        } catch (Exception e) {
            System.err.println("Error al desconectar: " + e.getMessage());
        }
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

            case "JUGADOR_DESCONECTADO":
                String nombreDesconectado = (String) m.getDatos().get("jugador");
                juego.imprimir("⚠️ Jugador '" + nombreDesconectado + "' se ha desconectado");
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
                    juego.imprimir("2. Si puedes mover, elige una ficha (1-4)");
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
                    juego.imprimir("Ahora elige qué ficha mover (1-4) y presiona 'Mover'");
                } else {
                    juego.imprimir(jugadorDado + " sacó: " + resultado);
                }
                break;

            case "INFO_TURNO":
                String mensaje = (String) m.getDatos().get("mensaje");
                juego.imprimir("");
                juego.imprimir("⏭️  " + mensaje);
                juego.imprimir("");
                break;

            case "OPCION_CENTRO":
                String mensajeCentro = (String) m.getDatos().get("mensaje");
                int idFichaCentro = (int) m.getDatos().get("idFicha");

                int opcion = JOptionPane.showConfirmDialog(
                        juego,
                        mensajeCentro + "\nFicha: " + idFichaCentro,
                        "Centro Seguro",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                Map<String, Object> respuesta = new HashMap<>();
                respuesta.put("elegirCentro", opcion == JOptionPane.YES_OPTION);
                enviar(new Mensaje("RESPUESTA_CENTRO", miNombre, respuesta));
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
                juego.imprimir("🏆 GANADOR: " + ganador + " 🏆");
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
            if (out != null && conectado) {
                out.writeObject(m);
            }
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
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
                    boolean centro = (boolean) f.getOrDefault("enCentro", false);

                    String estado;
                    if (meta) {
                        estado = "META ✓";
                    } else if (centro) {
                        estado = "CENTRO 🛡️";
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