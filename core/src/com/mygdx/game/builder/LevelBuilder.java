package com.mygdx.game.builder;

import com.mygdx.game.entities.BlockGrid;

/**
 * Patron Builder: arma la matriz de bloques de un nivel paso a paso.
 *
 * En vez de llamar a un constructor con cuatro numeros sueltos, cada
 * caracteristica del nivel se configura con un metodo con nombre propio
 * (todos devuelven el mismo builder, asi se encadenan) y al final build()
 * valida los datos y entrega el BlockGrid listo:
 *
 *   BlockGrid grilla = new LevelBuilder()
 *           .conTamanio(7, 10)
 *           .conBloquesIniciales(8)
 *           .conFilasIniciales(5)
 *           .build();
 *
 * Los valores por defecto sirven para el nivel 1; paraNivel(n) configura
 * de una vez la dificultad de un nivel (mas bloques mientras mas alto).
 */
public class LevelBuilder {
    // valores por defecto (nivel 1)
    private static final int FILAS_POR_DEFECTO = 7;
    private static final int COLUMNAS_POR_DEFECTO = 10;
    private static final int BLOQUES_POR_DEFECTO = 8;
    private static final int BLOQUES_POR_NIVEL = 2;   // bloques extra por cada nivel
    private static final int MAX_BLOQUES = 30;
    private static final int FILAS_LIBRES = 2;        // filas de abajo que parten vacias

    private int filas = FILAS_POR_DEFECTO;
    private int columnas = COLUMNAS_POR_DEFECTO;
    private int bloquesIniciales = BLOQUES_POR_DEFECTO;
    private int filasIniciales = FILAS_POR_DEFECTO - FILAS_LIBRES;

    /** Tamanio de la matriz. Deja libres las 2 filas de abajo para poder mover los bloques. */
    public LevelBuilder conTamanio(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.filasIniciales = Math.max(1, filas - FILAS_LIBRES);
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

    /** Configura la cantidad de bloques segun el nivel: 8 en el 1, 2 mas por nivel, hasta 30. */
    public LevelBuilder paraNivel(int nivel) {
        this.bloquesIniciales = Math.min(BLOQUES_POR_DEFECTO + BLOQUES_POR_NIVEL * (nivel - 1), MAX_BLOQUES);
        return this;
    }

    /** Valida lo configurado y crea la matriz de bloques. */
    public BlockGrid build() {
        if (filas <= 0 || columnas <= 0) {
            throw new IllegalStateException("La matriz necesita al menos 1 fila y 1 columna");
        }
        if (filasIniciales <= 0 || filasIniciales > filas) {
            throw new IllegalStateException("filasIniciales debe estar entre 1 y " + filas);
        }
        if (bloquesIniciales < 0 || bloquesIniciales > filasIniciales * columnas) {
            throw new IllegalStateException("bloquesIniciales no cabe en las filas iniciales (maximo "
                    + filasIniciales * columnas + ")");
        }
        return new BlockGrid(filas, columnas, bloquesIniciales, filasIniciales);
    }
}
