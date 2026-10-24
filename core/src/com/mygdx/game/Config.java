package com.mygdx.game;

/**
 * Tamanio del "mundo" del juego. Todo (bloques, paleta, pelotas, rebotes) se calcula
 * con estas medidas fijas y no con el tamanio real de la ventana; asi, en pantalla
 * completa el juego se escala entero con barras negras en vez de verse cortado.
 */
public final class Config {
    public static final int ANCHO = 800;
    public static final int ALTO = 480;

    private Config() { }
}
