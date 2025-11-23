package ui;

import javax.swing.*;
import java.awt.*;

public class VentanaInicial extends JFrame {

    private JTextField campoIP;
    private JTextField campoNombre;
    private JButton botonConectar;

    public VentanaInicial() {
        setTitle("Conectar al Servidor");
        setSize(380, 230);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        Color azul = new Color(0, 120, 215);
        Color fondo = new Color(240, 240, 240);

        JPanel panel = new JPanel();
        panel.setBackground(fondo);
        panel.setBounds(0, 0, 380, 230);
        panel.setLayout(null);

        JLabel titulo = new JLabel("Cliente Parchís");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setBounds(110, 10, 200, 30);
        titulo.setForeground(Color.DARK_GRAY);

        JLabel labelIP = new JLabel("IP del servidor:");
        labelIP.setBounds(40, 60, 120, 20);
        campoIP = new JTextField("localhost");
        campoIP.setBounds(160, 60, 160, 24);

        JLabel labelNombre = new JLabel("Tu nombre:");
        labelNombre.setBounds(40, 100, 120, 20);
        campoNombre = new JTextField();
        campoNombre.setBounds(160, 100, 160, 24);

        botonConectar = new JButton("Conectar");
        botonConectar.setBounds(120, 150, 140, 32);
        botonConectar.setBackground(azul);
        botonConectar.setForeground(Color.white);
        botonConectar.setFocusPainted(false);

        panel.add(titulo);
        panel.add(labelIP);
        panel.add(campoIP);
        panel.add(labelNombre);
        panel.add(campoNombre);
        panel.add(botonConectar);

        add(panel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JButton getBotonConectar() { return botonConectar; }
    public String getIP() { return campoIP.getText(); }
    public String getNombre() { return campoNombre.getText(); }
}
