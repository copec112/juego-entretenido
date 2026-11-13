package com.mygdx.game.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.mygdx.game.entities.BlockGrid;

/**
 * Patron Builder: arma la matriz de bloques de un nivel paso a paso.
 *
 * Cada caracteristica del nivel se configura con un metodo con nombre propio
 * (todos devuelven el mismo builder, asi se encadenan) y al final build()
 * valida los datos, crea la grilla vacia y va poniendo los bloques iniciales
 * en celdas al azar:
 *
 *   BlockGrid grilla = new LevelBuilder()
 *           .conTamanio(7, 10)
 *           .conBloquesIniciales(8)
 *           .conFilasIniciales(5)
 *           .build();
 *
 * Los valores por defecto sirven para el nivel 1. Quien sabe que configuracion
 * lleva cada nivel es DirectorNiveles.
 */
public class LevelBuilder {
    // valores por defecto (nivel 1)
    private static final int FILAS_POR_DEFECTO = 7;
    private static final int COLUMNAS_POR_DEFECTO = 10;
    private static final int BLOQUES_POR_DEFECTO = 11;
    private static final int FILAS_LIBRES = 2; // filas de abajo que parten vacias si no se indica otra cosa

    private int filas = FILAS_POR_DEFECTO;
    private int columnas = COLUMNAS_POR_DEFECTO;
    private int bloquesIniciales = BLOQUES_POR_DEFECTO;
    private int filasIniciales = 0; // 0 = "las que corresponden al tamanio" (todas menos las 2 de abajo)
    private final Random random = new Random();

    /** Tamanio de la matriz. */
    public LevelBuilder conTamanio(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        return this;
    }

    /** Cuantos bloques (2 o 4) parten en la matriz, en celdas al azar. */
    public LevelBuilder conBloquesIniciales(int cantidad) {
        this.bloquesIniciales = cantidad;
        return this;
    }

    /** En cuantas filas (desde arriba) pueden aparecer los bloques iniciales. */
    public LevelBuilder conFilasIniciales(int filasIniciales) {
        this.filasIniciales = filasIniciales;
        return this;
    }

    /**
     * Valida lo configurado, crea la grilla vacia y coloca los bloques iniciales
     * (2 o 4) en celdas elegidas al azar dentro de las primeras filas de arriba,
     * para que abajo quede espacio libre por donde moverlos.
     */
    public BlockGrid build() {
        if (filas <= 0 || columnas <= 0) {
            throw new IllegalStateException("La matriz necesita al menos 1 fila y 1 columna");
        }
        int filasConBloques = (filasIniciales > 0) ? filasIniciales : Math.max(1, filas - FILAS_LIBRES);
        if (filasConBloques > filas) {
            throw new IllegalStateException("filasIniciales debe estar entre 1 y " + filas);
        }
        if (bloquesIniciales < 0 || bloquesIniciales > filasConBloques * columnas) {
            throw new IllegalStateException("bloquesIniciales no cabe en las filas iniciales (maximo "
                    + filasConBloques * columnas + ")");
        }

        BlockGrid grilla = new BlockGrid(filas, columnas);

        // celdas candidatas: las de las primeras filas, en orden al azar
        List<int[]> candidatas = new ArrayList<int[]>();
        for (int f = 0; f < filasConBloques; f++) {
            for (int c = 0; c < columnas; c++) {
                candidatas.add(new int[] { f, c });
            }
        }
        Collections.shuffle(candidatas, random);

        for (int i = 0; i < bloquesIniciales; i++) {
            int[] celda = candidatas.get(i);
            grilla.colocarBloque(celda[0], celda[1], random.nextBoolean() ? 2 : 4);
        }
        return grilla;
    }
}
