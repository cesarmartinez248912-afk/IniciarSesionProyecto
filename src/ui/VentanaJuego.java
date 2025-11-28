package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Map;

public class VentanaJuego extends JFrame {

    private JTextArea consola;
    private JButton btnSolicitarInicio;
    private JButton btnConfirmar;
    private JButton btnTirarDado;
    private JButton btnMoverFicha;
    private JButton btnDesconectar;
    private JTextField campoIdFicha;
    private JLabel labelTurno;
    private JLabel labelUltimoDado;
    private int ultimoDado = 0;

    private DefaultListModel<String> modeloJugadores;
    private JList<String> listaJugadores;

    private PanelTablero panelTablero;

    public VentanaJuego() {
        setTitle("Parchís Online");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        Color colorFondo = new Color(236, 240, 241);
        Color colorPrimario = new Color(52, 152, 219);
        Color colorExito = new Color(46, 204, 113);
        Color colorAdvertencia = new Color(241, 196, 15);
        Color colorPeligro = new Color(231, 76, 60);

        getContentPane().setBackground(colorFondo);

        // PANEL IZQUIERDO - Jugadores e Info
        JPanel panelIzq = new JPanel(new BorderLayout(10, 10));
        panelIzq.setBackground(colorFondo);
        panelIzq.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        // Lista de jugadores
        JPanel panelJugadores = new JPanel(new BorderLayout());
        panelJugadores.setBackground(Color.WHITE);
        panelJugadores.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel tituloJugadores = new JLabel("Jugadores", SwingConstants.CENTER);
        tituloJugadores.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloJugadores.setForeground(new Color(44, 62, 80));
        tituloJugadores.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        modeloJugadores = new DefaultListModel<>();
        listaJugadores = new JList<>(modeloJugadores);
        listaJugadores.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaJugadores.setBackground(new Color(250, 250, 250));
        listaJugadores.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JScrollPane scrollLista = new JScrollPane(listaJugadores);
        scrollLista.setBorder(null);
        scrollLista.setPreferredSize(new Dimension(200, 0));

        panelJugadores.add(tituloJugadores, BorderLayout.NORTH);
        panelJugadores.add(scrollLista, BorderLayout.CENTER);

        // Panel de info
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel tituloInfo = new JLabel("Información");
        tituloInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloInfo.setForeground(new Color(44, 62, 80));
        tituloInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelTurno = new JLabel("Turno: Esperando...");
        labelTurno.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelTurno.setForeground(new Color(52, 73, 94));
        labelTurno.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        labelTurno.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelUltimoDado = new JLabel("Dado: ");
        labelUltimoDado.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelUltimoDado.setForeground(colorPrimario);
        labelUltimoDado.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        labelUltimoDado.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelInfo.add(tituloInfo);
        panelInfo.add(labelTurno);
        panelInfo.add(labelUltimoDado);

        panelIzq.add(panelJugadores, BorderLayout.CENTER);
        panelIzq.add(panelInfo, BorderLayout.SOUTH);

        add(panelIzq, BorderLayout.WEST);


        panelTablero = new PanelTablero();
        add(panelTablero, BorderLayout.CENTER);

        // PANEL DERECHO - Consola
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBackground(colorFondo);
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        JPanel panelConsolaHeader = new JPanel(new BorderLayout());
        panelConsolaHeader.setBackground(new Color(44, 62, 80));
        panelConsolaHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel tituloConsola = new JLabel("Consola");
        tituloConsola.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloConsola.setForeground(Color.WHITE);
        panelConsolaHeader.add(tituloConsola);

        consola = new JTextArea();
        consola.setEditable(false);
        consola.setFont(new Font("Consolas", Font.PLAIN, 12));
        consola.setBackground(new Color(250, 250, 250));
        consola.setForeground(new Color(44, 62, 80));
        consola.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollConsola = new JScrollPane(consola);
        scrollConsola.setBorder(new LineBorder(new Color(189, 195, 199), 1));
        scrollConsola.setPreferredSize(new Dimension(300, 0));

        JPanel wrapperConsola = new JPanel(new BorderLayout());
        wrapperConsola.add(panelConsolaHeader, BorderLayout.NORTH);
        wrapperConsola.add(scrollConsola, BorderLayout.CENTER);

        panelDerecho.add(wrapperConsola);
        add(panelDerecho, BorderLayout.EAST);


        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelControles.setBackground(Color.WHITE);
        panelControles.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        btnSolicitarInicio = crearBoton("Solicitar Inicio", colorExito);
        btnConfirmar = crearBoton("Confirmar", colorPrimario);
        btnTirarDado = crearBoton("Tirar Dado", colorAdvertencia);
        btnMoverFicha = crearBoton("Mover", colorPeligro);
        btnDesconectar = crearBoton("Desconectar", new Color(231, 76, 60));
        JLabel labelFicha = new JLabel("Ficha:");
        labelFicha.setFont(new Font("Segoe UI", Font.BOLD, 13));

        campoIdFicha = new JTextField("1", 3);
        campoIdFicha.setFont(new Font("Segoe UI", Font.BOLD, 16));
        campoIdFicha.setHorizontalAlignment(JTextField.CENTER);
        campoIdFicha.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        panelControles.add(btnSolicitarInicio);
        panelControles.add(btnConfirmar);
        panelControles.add(btnTirarDado);
        panelControles.add(labelFicha);
        panelControles.add(campoIdFicha);
        panelControles.add(btnMoverFicha);
        panelControles.add(btnDesconectar);

        add(panelControles, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 40));

        return btn;
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
                jugadores.forEach(j -> modeloJugadores.addElement("🎮 " + j));
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
            ultimoDado = resultado;
            labelUltimoDado.setText("Dado: " + resultado);
        });
    }

    public void actualizarTablero(Map<String, Object> estadoTablero) {
        SwingUtilities.invokeLater(() -> {
            panelTablero.actualizarEstado(estadoTablero);
        });
    }

    public void notificar(String titulo, String mensaje) {
        imprimir("[" + titulo + "] " + mensaje);
    }

    public JButton getSolicitarInicio() { return btnSolicitarInicio; }
    public JButton getConfirmar() { return btnConfirmar; }
    public JButton getTirarDado() { return btnTirarDado; }
    public JButton getMoverFicha() { return btnMoverFicha; }
    public JButton getBotonDesconectar() { return btnDesconectar; }
    public int getIdFicha() {
        try { return Integer.parseInt(campoIdFicha.getText()); }
        catch (NumberFormatException e) { return 1; }
    }

    public int getPasos() {
        return ultimoDado;
    }
}

class PanelTablero extends JPanel {
    private Map<String, Object> estadoTablero;

    public PanelTablero() {
        setBackground(new Color(245, 245, 240));
        setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
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
        dibujarCasa(g2d, inicioX + margenCasa, inicioY + largoBrazo + anchoBrazo + margenCasa, tamCasa, tamCasilla, new Color(50, 180, 50));
        dibujarCasa(g2d, inicioX + largoBrazo + anchoBrazo + margenCasa, inicioY + largoBrazo + anchoBrazo + margenCasa, tamCasa, tamCasilla, new Color(240, 200, 50));

        dibujarBrazoVerticalArriba(g2d, inicioX + largoBrazo, inicioY, anchoBrazo, largoBrazo, tamCasilla, Color.RED);
        dibujarBrazoHorizontalDerecha(g2d, inicioX + largoBrazo + anchoBrazo, inicioY + largoBrazo, largoBrazo, anchoBrazo, tamCasilla, Color.BLUE);
        dibujarBrazoVerticalAbajo(g2d, inicioX + largoBrazo, inicioY + largoBrazo + anchoBrazo, anchoBrazo, largoBrazo, tamCasilla, new Color(240, 200, 50));
        dibujarBrazoHorizontalIzquierda(g2d, inicioX, inicioY + largoBrazo, largoBrazo, anchoBrazo, tamCasilla, new Color(50, 180, 50));

        int centroX = inicioX + largoBrazo;
        int centroY = inicioY + largoBrazo;
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillRect(centroX, centroY, anchoBrazo, anchoBrazo);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(centroX, centroY, anchoBrazo, anchoBrazo);

        dibujarEstrella(g2d, centroX + anchoBrazo/2, centroY + anchoBrazo/2, tamCasilla);

        if (estadoTablero != null) {
            dibujarFichas(g2d, tamCasilla, inicioX, inicioY, largoBrazo, anchoBrazo, tamCasa, margenCasa);
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                int px = x + j * (ancho / 3);
                int py = y + i * tamCasilla;
                int pw = ancho / 3;
                int ph = tamCasilla;
                g2d.setColor((j == 1 && i >= 1 && i <= 7) ? colorJugador.brighter() : Color.WHITE);
                g2d.fillRect(px, py, pw, ph);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(px, py, pw, ph);
            }
        }
    }

    private void dibujarBrazoHorizontalDerecha(Graphics2D g2d, int x, int y, int ancho, int alto, int tamCasilla, Color colorJugador) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                int px = x + i * tamCasilla;
                int py = y + j * (alto / 3);
                int pw = tamCasilla;
                int ph = alto / 3;
                g2d.setColor((j == 1 && i >= 0 && i <= 6) ? colorJugador.brighter() : Color.WHITE);
                g2d.fillRect(px, py, pw, ph);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(px, py, pw, ph);
            }
        }
    }

    private void dibujarBrazoVerticalAbajo(Graphics2D g2d, int x, int y, int ancho, int alto, int tamCasilla, Color colorJugador) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                int px = x + j * (ancho / 3);
                int py = y + i * tamCasilla;
                int pw = ancho / 3;
                int ph = tamCasilla;
                g2d.setColor((j == 1 && i >= 0 && i <= 6) ? colorJugador.brighter() : Color.WHITE);
                g2d.fillRect(px, py, pw, ph);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(px, py, pw, ph);
            }
        }
    }

    private void dibujarBrazoHorizontalIzquierda(Graphics2D g2d, int x, int y, int ancho, int alto, int tamCasilla, Color colorJugador) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                int px = x + i * tamCasilla;
                int py = y + j * (alto / 3);
                int pw = tamCasilla;
                int ph = alto / 3;
                g2d.setColor((j == 1 && i >= 1 && i <= 7) ? colorJugador.brighter() : Color.WHITE);
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

    private void dibujarFichas(Graphics2D g2d, int tamCasilla, int inicioX, int inicioY, int largoBrazo, int anchoBrazo, int tamCasa, int margenCasa) {
        for (String colorStr : estadoTablero.keySet()) {
            Color color = getColor(colorStr);
            Object obj = estadoTablero.get(colorStr);
            if (obj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Map<String, Object>> fichas = (java.util.List<Map<String, Object>>) obj;
                for (Map<String, Object> f : fichas) {
                    int id = (int) f.get("id");
                    boolean enMeta = (boolean) f.get("enMeta");
                    boolean enCasa = (boolean) f.get("enCasa");
                    boolean enPasillo = (boolean) f.get("enPasillo");
                    Point pos;
                    if (enMeta) {
                        pos = getPosicionMeta(colorStr, inicioX, inicioY, largoBrazo, anchoBrazo);
                    } else if (enCasa) {
                        pos = getPosicionCasa(colorStr, id, inicioX, inicioY, largoBrazo, anchoBrazo, tamCasa, margenCasa, tamCasilla);
                    } else if (enPasillo) {
                        int posicionPasillo = (int) f.get("posicionPasillo");
                        pos = getPosicionPasillo(colorStr, posicionPasillo, inicioX, inicioY, largoBrazo, anchoBrazo, tamCasilla);
                    } else {
                        if (f.containsKey("fila") && f.containsKey("columna")) {
                            int fila = (int) f.get("fila");
                            int columna = (int) f.get("columna");
                            pos = getPosicionTablero(fila, columna, inicioX, inicioY, largoBrazo, anchoBrazo, tamCasilla);
                        } else {
                            pos = getPosicionCasa(colorStr, id, inicioX, inicioY, largoBrazo, anchoBrazo, tamCasa, margenCasa, tamCasilla);
                        }
                    }
                    dibujarFicha(g2d, pos.x, pos.y, color, id);
                }
            }
        }
    }

    private Point getPosicionTablero(int fila, int columna, int inicioX, int inicioY, int largoBrazo, int anchoBrazo, int tam) {
        int x, y;
        if (columna <= 7) {
            x = inicioX + (columna * tam);
        } else if (columna <= 10) {
            x = inicioX + largoBrazo + ((columna - 8) * (anchoBrazo / 3));
        } else {
            x = inicioX + largoBrazo + anchoBrazo + ((columna - 11) * tam);
        }
        if (fila <= 7) {
            y = inicioY + (fila * tam);
        } else if (fila <= 10) {
            y = inicioY + largoBrazo + ((fila - 8) * (anchoBrazo / 3));
        } else {
            y = inicioY + largoBrazo + anchoBrazo + ((fila - 11) * tam);
        }
        return new Point(x + tam/2, y + tam/2);
    }

    private Point getPosicionPasillo(String color, int posPasillo, int inicioX, int inicioY, int largoBrazo, int anchoBrazo, int tam) {
        int x, y;
        switch (color.toUpperCase()) {
            case "ROJO":
                x = inicioX + largoBrazo + anchoBrazo / 2;
                y = inicioY + largoBrazo - ((posPasillo + 1) * tam);
                break;
            case "AZUL":
                x = inicioX + largoBrazo + anchoBrazo + (posPasillo * tam);
                y = inicioY + largoBrazo + anchoBrazo / 2;
                break;
            case "VERDE":
                x = inicioX + largoBrazo + anchoBrazo / 2;
                y = inicioY + largoBrazo + anchoBrazo + (posPasillo * tam);
                break;
            case "AMARILLO":
            default:
                x = inicioX + largoBrazo - ((posPasillo + 1) * tam);
                y = inicioY + largoBrazo + anchoBrazo / 2;
                break;
        }
        return new Point(x + tam/2, y + tam/2);
    }

    private Point getPosicionMeta(String color, int inicioX, int inicioY, int largoBrazo, int anchoBrazo) {
        return new Point(inicioX + largoBrazo + anchoBrazo / 2, inicioY + largoBrazo + anchoBrazo / 2);
    }

    private Point getPosicionCasa(String color, int id, int inicioX, int inicioY, int largoBrazo, int anchoBrazo, int tamCasa, int margenCasa, int tam) {
        int baseX, baseY;
        switch (color.toUpperCase()) {
            case "ROJO":
                baseX = inicioX + margenCasa + tamCasa / 2;
                baseY = inicioY + margenCasa + tamCasa / 2;
                break;
            case "AZUL":
                baseX = inicioX + largoBrazo + anchoBrazo + margenCasa + tamCasa / 2;
                baseY = inicioY + margenCasa + tamCasa / 2;
                break;
            case "VERDE":
                baseX = inicioX + margenCasa + tamCasa / 2;
                baseY = inicioY + largoBrazo + anchoBrazo + margenCasa + tamCasa / 2;
                break;
            case "AMARILLO":
            default:
                baseX = inicioX + largoBrazo + anchoBrazo + margenCasa + tamCasa / 2;
                baseY = inicioY + largoBrazo + anchoBrazo + margenCasa + tamCasa / 2;
                break;
        }
        int offsetX = ((id - 1) % 2) * tam - tam / 2;
        int offsetY = ((id - 1) / 2) * tam - tam / 2;
        return new Point(baseX + offsetX, baseY + offsetY);
    }

    private void dibujarFicha(Graphics2D g2d, int x, int y, Color color, int numero) {
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
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String numStr = String.valueOf(numero);
        int numWidth = fm.stringWidth(numStr);
        int numHeight = fm.getAscent();
        g2d.drawString(numStr, x - numWidth/2, y + numHeight/3);
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
