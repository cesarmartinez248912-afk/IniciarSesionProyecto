package ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class VentanaJuego extends JFrame {

    private JTextArea consola;
    private JButton btnSolicitarInicio;
    private JButton btnConfirmar;
    private JButton btnTirarDado;
    private JButton btnMoverFicha;
    private JTextField campoIdFicha;
    private JLabel labelTurno;
    private JLabel labelUltimoDado;
    private int ultimoDado = 0;

    private DefaultListModel<String> modeloJugadores;
    private JList<String> listaJugadores;

    private PanelTablero panelTablero;

    public VentanaJuego() {
        setTitle("Parchís");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel panelIzq = new JPanel(new BorderLayout());
        modeloJugadores = new DefaultListModel<>();
        listaJugadores = new JList<>(modeloJugadores);
        listaJugadores.setBorder(BorderFactory.createTitledBorder("Jugadores"));
        JScrollPane scrollLista = new JScrollPane(listaJugadores);
        scrollLista.setPreferredSize(new Dimension(180, 0));
        panelIzq.add(scrollLista, BorderLayout.CENTER);

        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Info"));
        labelTurno = new JLabel("Turno: -");
        labelUltimoDado = new JLabel("Dado: -");
        panelInfo.add(labelTurno);
        panelInfo.add(labelUltimoDado);
        panelIzq.add(panelInfo, BorderLayout.SOUTH);

        add(panelIzq, BorderLayout.WEST);

        panelTablero = new PanelTablero();
        add(panelTablero, BorderLayout.CENTER);

        consola = new JTextArea();
        consola.setEditable(false);
        consola.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollConsola = new JScrollPane(consola);
        scrollConsola.setPreferredSize(new Dimension(280, 0));
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Consola"));
        add(scrollConsola, BorderLayout.EAST);

        JPanel panelControles = new JPanel(new FlowLayout());

        btnSolicitarInicio = new JButton("Solicitar inicio");
        btnConfirmar = new JButton("Confirmar");
        btnTirarDado = new JButton("Tirar dado");

        campoIdFicha = new JTextField("1", 3);
        btnMoverFicha = new JButton("Mover");

        panelControles.add(btnSolicitarInicio);
        panelControles.add(btnConfirmar);
        panelControles.add(btnTirarDado);
        panelControles.add(new JLabel("Ficha (1-7):"));
        panelControles.add(campoIdFicha);
        panelControles.add(btnMoverFicha);

        add(panelControles, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void imprimir(String t) {
        SwingUtilities.invokeLater(() -> {
            consola.append(t + "\n");
            consola.setCaretPosition(consola.getDocument().getLength());
        });
    }

    public void actualizarListaJugadores(java.util.List<String> jugadores) {
        SwingUtilities.invokeLater(() -> {
            modeloJugadores.clear();
            if (jugadores != null) {
                jugadores.forEach(modeloJugadores::addElement);
            }
        });
    }

    public void actualizarTurno(String jugador, String color) {
        SwingUtilities.invokeLater(() -> {
            labelTurno.setText("Turno: " + jugador + " (" + color + ")");
        });
    }

    public void actualizarDado(int resultado) {
        SwingUtilities.invokeLater(() -> {
            labelUltimoDado.setText("Dado: " + resultado);
            campoPasos.setText(String.valueOf(resultado));
        });
    }

    public void actualizarTablero(Map<String, Object> estadoTablero) {
        SwingUtilities.invokeLater(() -> {
            panelTablero.actualizarEstado(estadoTablero);
        });
    }

    public void notificar(String titulo, String mensaje) {
        // Ya no usamos JOptionPane, solo consola
        imprimir("[" + titulo + "] " + mensaje);
    }

    public JButton getSolicitarInicio() { return btnSolicitarInicio; }
    public JButton getConfirmar() { return btnConfirmar; }
    public JButton getTirarDado() { return btnTirarDado; }
    public JButton getMoverFicha() { return btnMoverFicha; }

    public int getIdFicha() {
        try { return Integer.parseInt(campoIdFicha.getText()); }
        catch (NumberFormatException e) { return 1; }
    }

    public int getPasos() {
        try { return Integer.parseInt(campoPasos.getText()); }
        catch (NumberFormatException e) { return 0; }
    }
}

class PanelTablero extends JPanel {
    private Map<String, Object> estadoTablero;

    public PanelTablero() {
        setBackground(new Color(245, 245, 240));
        setBorder(BorderFactory.createTitledBorder("Tablero"));
    }

    public void actualizarEstado(Map<String, Object> estado) {
        this.estadoTablero = estado;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        int tamCasilla = Math.min(w, h) / 28;

        int anchoBrazo = tamCasilla * 3;
        int largoBrazo = tamCasilla * 8;
        int tamCasa = tamCasilla * 7;

        int totalAncho = largoBrazo + anchoBrazo + largoBrazo;
        int totalAlto = largoBrazo + anchoBrazo + largoBrazo;

        int inicioX = (w - totalAncho) / 2;
        int inicioY = (h - totalAlto) / 2;

        int margenCasa = (largoBrazo - tamCasa) / 2;

        dibujarCasa(g2d, inicioX + margenCasa, inicioY + margenCasa, tamCasa, tamCasilla, Color.RED);
        dibujarCasa(g2d, inicioX + largoBrazo + anchoBrazo + margenCasa, inicioY + margenCasa, tamCasa, tamCasilla, Color.BLUE);
        dibujarCasa(g2d, inicioX + margenCasa, inicioY + largoBrazo + anchoBrazo + margenCasa, tamCasa, tamCasilla, Color.GREEN);
        dibujarCasa(g2d, inicioX + largoBrazo + anchoBrazo + margenCasa, inicioY + largoBrazo + anchoBrazo + margenCasa, tamCasa, tamCasilla, Color.YELLOW);

        dibujarBrazoVerticalArriba(g2d, inicioX + largoBrazo, inicioY, anchoBrazo, largoBrazo, tamCasilla, Color.RED);

        dibujarBrazoHorizontalDerecha(g2d, inicioX + largoBrazo + anchoBrazo, inicioY + largoBrazo, largoBrazo, anchoBrazo, tamCasilla, Color.BLUE);

        dibujarBrazoVerticalAbajo(g2d, inicioX + largoBrazo, inicioY + largoBrazo + anchoBrazo, anchoBrazo, largoBrazo, tamCasilla, Color.YELLOW);

        dibujarBrazoHorizontalIzquierda(g2d, inicioX, inicioY + largoBrazo, largoBrazo, anchoBrazo, tamCasilla, Color.GREEN);

        int centroX = inicioX + largoBrazo;
        int centroY = inicioY + largoBrazo;
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillRect(centroX, centroY, anchoBrazo, anchoBrazo);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(centroX, centroY, anchoBrazo, anchoBrazo);

        dibujarEstrella(g2d, centroX + anchoBrazo/2, centroY + anchoBrazo/2, tamCasilla);

        if (estadoTablero != null) {
            dibujarFichas(g2d, cx, cy, tamCasilla, w, h, tamCasa, inicioX, inicioY);
        }
    }

    private void dibujarCasa(Graphics2D g2d, int x, int y, int tam, int casilla, Color color) {
        g2d.setColor(color.brighter());
        g2d.fillRoundRect(x, y, tam, tam, 20, 20);

        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(x, y, tam, tam, 20, 20);

        int circuloTam = casilla * 2;
        int circuloX = x + (tam - circuloTam) / 2;
        int circuloY = y + (tam - circuloTam) / 2;

        g2d.setColor(color);
        g2d.fillOval(circuloX, circuloY, circuloTam, circuloTam);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(circuloX, circuloY, circuloTam, circuloTam);
    }

    private void dibujarBrazoVerticalArriba(Graphics2D g2d, int x, int y, int ancho, int alto, int tamCasilla, Color colorJugador) {
        int numCasillas = 8;

        for (int i = 0; i < numCasillas; i++) {
            for (int j = 0; j < 3; j++) {
                int px = x + j * (ancho / 3);
                int py = y + i * tamCasilla;
                int pw = ancho / 3;
                int ph = tamCasilla;

                if (j == 1 && i >= 1 && i <= 7) {
                    g2d.setColor(colorJugador);
                } else {
                    g2d.setColor(Color.WHITE);
                }

                g2d.fillRect(px, py, pw, ph);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(px, py, pw, ph);
            }
        }
    }

    private void dibujarBrazoHorizontalDerecha(Graphics2D g2d, int x, int y, int ancho, int alto, int tamCasilla, Color colorJugador) {
        int numCasillas = 8;

        for (int i = 0; i < numCasillas; i++) {
            for (int j = 0; j < 3; j++) {
                int px = x + i * tamCasilla;
                int py = y + j * (alto / 3);
                int pw = tamCasilla;
                int ph = alto / 3;

                if (j == 1 && i >= 0 && i <= 6) {
                    g2d.setColor(colorJugador);
                } else {
                    g2d.setColor(Color.WHITE);
                }

                g2d.fillRect(px, py, pw, ph);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(px, py, pw, ph);
            }
        }
    }

    private void dibujarBrazoVerticalAbajo(Graphics2D g2d, int x, int y, int ancho, int alto, int tamCasilla, Color colorJugador) {
        int numCasillas = 8;

        for (int i = 0; i < numCasillas; i++) {
            for (int j = 0; j < 3; j++) {
                int px = x + j * (ancho / 3);
                int py = y + i * tamCasilla;
                int pw = ancho / 3;
                int ph = tamCasilla;

                if (j == 1 && i >= 0 && i <= 6) {
                    g2d.setColor(colorJugador);
                } else {
                    g2d.setColor(Color.WHITE);
                }

                g2d.fillRect(px, py, pw, ph);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(px, py, pw, ph);
            }
        }
    }

    private void dibujarBrazoHorizontalIzquierda(Graphics2D g2d, int x, int y, int ancho, int alto, int tamCasilla, Color colorJugador) {
        int numCasillas = 8;

        for (int i = 0; i < numCasillas; i++) {
            for (int j = 0; j < 3; j++) {
                int px = x + i * tamCasilla;
                int py = y + j * (alto / 3);
                int pw = tamCasilla;
                int ph = alto / 3;

                if (j == 1 && i >= 1 && i <= 7) {
                    g2d.setColor(colorJugador);
                } else {
                    g2d.setColor(Color.WHITE);
                }

                g2d.fillRect(px, py, pw, ph);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(px, py, pw, ph);
            }
        }
    }

    private void dibujarEstrella(Graphics2D g2d, int cx, int cy, int tam) {
        int[] xPoints = new int[8];
        int[] yPoints = new int[8];

        for (int i = 0; i < 8; i++) {
            double angle = Math.PI / 4 * i;
            int radius = (i % 2 == 0) ? tam / 2 : tam / 4;
            xPoints[i] = cx + (int) (radius * Math.cos(angle));
            yPoints[i] = cy + (int) (radius * Math.sin(angle));
        }

        g2d.setColor(new Color(255, 215, 0));
        g2d.fillPolygon(xPoints, yPoints, 8);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, 8);
    }

    private void dibujarFichas(Graphics2D g2d, int cx, int cy, int tam, int w, int h, int tamCasa, int margen, int margenV) {
        for (String colorStr : estadoTablero.keySet()) {
            Color color = getColor(colorStr);
            Object obj = estadoTablero.get(colorStr);

            if (obj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Map<String, Object>> fichas = (java.util.List<Map<String, Object>>) obj;

                for (Map<String, Object> f : fichas) {
                    int id = (int) f.get("id");
                    int pos = (int) f.get("posicion");
                    boolean meta = (boolean) f.get("enMeta");

                    if (meta) {
                        int offsetX = ((id - 2) * tam / 3);
                        int offsetY = ((id - 3) * tam / 3);
                        dibujarFicha(g2d, cx + offsetX, cy + offsetY, color);
                    } else if (pos == -1) {
                        Point casaPos = getCasaPos(colorStr, w, h, tam, tamCasa, id, margen, margenV);
                        dibujarFicha(g2d, casaPos.x, casaPos.y, color);
                    } else {
                        Point tableroPos = getPosicionEnTablero(pos, cx, cy, tam, colorStr, tamCasa, margen, margenV);
                        dibujarFicha(g2d, tableroPos.x, tableroPos.y, color);
                    }
                }
            }
        }
    }

    private void dibujarFicha(Graphics2D g2d, int x, int y, Color color) {
        int radio = 12;
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(x - radio + 2, y - radio + 2, radio * 2, radio * 2);

        g2d.setColor(color);
        g2d.fillOval(x - radio, y - radio, radio * 2, radio * 2);

        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - radio, y - radio, radio * 2, radio * 2);

        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(x - radio/2, y - radio/2, radio, radio);
    }

    private Point getCasaPos(String colorStr, int w, int h, int tam, int tamCasa, int id, int margen, int margenV) {
        int baseX = 0, baseY = 0;

        switch (colorStr.toUpperCase()) {
            case "ROJO":
                baseX = margen + tamCasa / 2;
                baseY = margenV + tamCasa / 2;
                break;
            case "AZUL":
                baseX = w - margen - tamCasa / 2;
                baseY = margenV + tamCasa / 2;
                break;
            case "VERDE":
                baseX = margen + tamCasa / 2;
                baseY = h - margenV - tamCasa / 2;
                break;
            case "AMARILLO":
                baseX = w - margen - tamCasa / 2;
                baseY = h - margenV - tamCasa / 2;
                break;
        }

        int offsetX = ((id - 1) % 2) * tam - tam / 2;
        int offsetY = ((id - 1) / 2) * tam - tam / 2;

        return new Point(baseX + offsetX, baseY + offsetY);
    }

    private Point getPosicionEnTablero(int pos, int cx, int cy, int tam, String colorStr, int tamCasa, int margen, int margenV) {
        int casillaPorBrazo = 17;

        int x = cx, y = cy;

        if (pos < casillaPorBrazo) {
            x = cx;
            y = margenV + tamCasa + (pos * tam);
        } else if (pos < casillaPorBrazo * 2) {
            x = cx + ((pos - casillaPorBrazo) * tam);
            y = cy;
        } else if (pos < casillaPorBrazo * 3) {
            x = cx;
            y = cy + ((pos - casillaPorBrazo * 2) * tam);
        } else {
            x = cx - ((pos - casillaPorBrazo * 3) * tam);
            y = cy;
        }

        return new Point(x, y);
    }

    private Color getColor(String c) {
        switch (c.toUpperCase()) {
            case "ROJO": return new Color(220, 50, 50);
            case "AZUL": return new Color(50, 100, 220);
            case "VERDE": return new Color(50, 180, 50);
            case "AMARILLO": return new Color(240, 200, 50);
            default: return Color.GRAY;
        }
    }
}