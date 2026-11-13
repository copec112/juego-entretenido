package com.mygdx.game.builder;

import com.mygdx.game.entities.BlockGrid;

/**
 * Director del patron Builder: sabe QUE configuracion lleva cada nivel y se la
 * pide al LevelBuilder paso a paso. El juego solo le dice "quiero el nivel n".
 *
 * Nivel 1: 11 bloques; cada nivel suma 2 mas, hasta un maximo de 30.
 * Siempre quedan libres las 2 filas de abajo para poder mover los bloques.
 */
public class DirectorNiveles {
    private static final int BLOQUES_NIVEL_1 = 11;
    private static final int BLOQUES_POR_NIVEL = 2;
    private static final int MAX_BLOQUES = 30;
    private static final int FILAS_LIBRES = 2;

    /** Cuantos bloques parten en el nivel dado. */
    public int bloquesDelNivel(int nivel) {
        return Math.min(BLOQUES_NIVEL_1 + BLOQUES_POR_NIVEL * (nivel - 1), MAX_BLOQUES);
    }

    /** Construye la grilla del nivel, de "filas" x "columnas". */
    public BlockGrid construirNivel(int nivel, int filas, int columnas) {
        return new LevelBuilder()
                .conTamanio(filas, columnas)
                .conBloquesIniciales(bloquesDelNivel(nivel))
                .conFilasIniciales(Math.max(1, filas - FILAS_LIBRES))
                .build();
    }
}