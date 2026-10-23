package com.mygdx.game.managers;

/**
 * Tabla de dificultad segun el puntaje.
 *
 * Cada etapa dice, a partir de que puntaje empieza, cuantas pelotas hay en
 * juego y cuanta velocidad extra tienen. Para ajustar el juego basta con
 * cambiar esta tabla (por ejemplo, borrar la ultima fila deja un maximo de
 * 3 pelotas).
 */
public class Dificultad {
    // { puntaje minimo, pelotas en juego, velocidad extra de la pelota }
    private static final int[][] ETAPAS = {
        {    0, 1, 0 },   // inicio
        {  400, 2, 1 },   // mas rapida y una pelota mas
        { 1000, 3, 1 },   // tercera pelota
        { 2048, 4, 1 },   // cuarta pelota (maximo)
    };

    private Dificultad() {
        // clase de utilidad: no se instancia
    }

    private static int[] etapaPara(int puntaje) {
        int[] actual = ETAPAS[0];
        for (int[] etapa : ETAPAS) {
            if (puntaje >= etapa[0]) {
                actual = etapa;
            }
        }
        return actual;
    }

    /** Cuantas pelotas deben estar en juego con este puntaje. */
    public static int pelotasPara(int puntaje) {
        return etapaPara(puntaje)[1];
    }

    /** Cuanta velocidad extra (en pixeles por fotograma) tienen las pelotas con este puntaje. */
    public static int velocidadExtraPara(int puntaje) {
        return etapaPara(puntaje)[2];
    }
}