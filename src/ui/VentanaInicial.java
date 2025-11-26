package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class VentanaInicial extends JFrame {

    private JTextField campoIP;
    private JTextField campoNombre;
    private JButton botonConectar;

    public VentanaInicial() {
        setTitle("Parchís Online");
        setSize(450, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);


        Color colorPrimario = new Color(52, 152, 219);
        Color colorSecundario = new Color(41, 128, 185);
        Color colorFondo = new Color(236, 240, 241);
        Color colorTexto = new Color(44, 62, 80);
        Color colorBorde = new Color(189, 195, 199);


        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, colorFondo, 0, h, new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        panelPrincipal.setLayout(null);

        // Título con icono de dados
        JLabel titulo = new JLabel("PARCHÍS ONLINE", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titulo.setForeground(colorPrimario);
        titulo.setBounds(0, 40, 450, 50);

        JLabel subtitulo = new JLabel("Conecta y juega con tus amigos", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(colorTexto.brighter());
        subtitulo.setBounds(0, 90, 450, 25);


        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(null);
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBounds(50, 150, 350, 260);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(colorBorde, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Efecto de sombra (simulado con paneles)
        JPanel sombra = new JPanel();
        sombra.setBackground(new Color(0, 0, 0, 20));
        sombra.setBounds(55, 155, 350, 260);

        // Icono de servidor
        JLabel iconoServidor = new JLabel("", SwingConstants.CENTER);
        iconoServidor.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        iconoServidor.setBounds(145, 10, 60, 50);

        // Label IP
        JLabel labelIP = new JLabel("Dirección del Servidor");
        labelIP.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelIP.setForeground(colorTexto);
        labelIP.setBounds(20, 80, 310, 20);

        // Campo IP estilizado
        campoIP = new JTextField("localhost");
        campoIP.setBounds(20, 105, 310, 40);
        campoIP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoIP.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(colorBorde, 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        campoIP.setBackground(colorFondo);

        // Label Nombre
        JLabel labelNombre = new JLabel("Tu nickname");
        labelNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelNombre.setForeground(colorTexto);
        labelNombre.setBounds(20, 155, 310, 20);

        // Campo Nombre estilizado
        campoNombre = new JTextField();
        campoNombre.setBounds(20, 180, 310, 40);
        campoNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoNombre.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(colorBorde, 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        campoNombre.setBackground(colorFondo);

        // Agregar placeholders
        agregarPlaceholder(campoNombre, "Ingresa tu nombre...");

        // Agregar componentes al formulario
        panelFormulario.add(iconoServidor);
        panelFormulario.add(labelIP);
        panelFormulario.add(campoIP);
        panelFormulario.add(labelNombre);
        panelFormulario.add(campoNombre);

        // Botón conectar con estilo moderno
        botonConectar = new JButton("CONECTAR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(colorSecundario.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(colorSecundario);
                } else {
                    g2d.setColor(colorPrimario);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        botonConectar.setBounds(100, 440, 250, 50);
        botonConectar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botonConectar.setForeground(Color.WHITE);
        botonConectar.setFocusPainted(false);
        botonConectar.setBorderPainted(false);
        botonConectar.setContentAreaFilled(false);
        botonConectar.setCursor(new Cursor(Cursor.HAND_CURSOR));


        panelPrincipal.add(sombra);
        panelPrincipal.add(titulo);
        panelPrincipal.add(subtitulo);
        panelPrincipal.add(panelFormulario);
        panelPrincipal.add(botonConectar);

        add(panelPrincipal);

        // Efectos hover en campos
        agregarEfectoHover(campoIP, colorBorde, colorPrimario);
        agregarEfectoHover(campoNombre, colorBorde, colorPrimario);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void agregarPlaceholder(JTextField campo, String placeholder) {
        campo.setForeground(Color.GRAY);
        campo.setText(placeholder);

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (campo.getText().isEmpty()) {
                    campo.setForeground(Color.GRAY);
                    campo.setText(placeholder);
                }
            }
        });
    }

    private void agregarEfectoHover(JTextField campo, Color colorNormal, Color colorHover) {
        campo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(colorHover, 2, true),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!campo.hasFocus()) {
                    campo.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(colorNormal, 1, true),
                            BorderFactory.createEmptyBorder(5, 15, 5, 15)
                    ));
                }
            }
        });

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(colorHover, 2, true),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(colorNormal, 1, true),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });
    }

    public JButton getBotonConectar() { return botonConectar; }

    public String getIP() {
        String text = campoIP.getText();
        return text;
    }

    public String getNombre() {
        String text = campoNombre.getText();
        return (text.equals("Ingresa tu nombre...") || text.isEmpty()) ? "" : text;
    }
}