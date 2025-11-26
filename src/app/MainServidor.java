package app;

import modelo.GestorJugadores;
import modelo.Tablero; import red.ServidorImpl;
public class MainServidor {
    public static void main(String[] args) throws Exception {
        Tablero tablero = new Tablero();
        GestorJugadores gestor = new GestorJugadores();
        ServidorImpl servidor = new ServidorImpl(5000, tablero, gestor);         servidor.iniciar();     } }