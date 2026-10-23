package com.mygdx.game.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Grilla (matriz) de bloques.
 *
 * Cada bloque esta en celdas[fila][columna]; la fila 0 es la de arriba.
 * Un bloque destruido queda como null. Al tener posicion (fila, columna)
 * es facil saber quienes son los vecinos de un bloque (arriba, abajo,
 * izquierda, derecha), lo que se usara para las fusiones tipo 2048.
 */
public class BlockGrid {
    private static final int BLOCK_WIDTH = 70;
    private static final int BLOCK_HEIGHT = 26;
    private static final int SEPARACION = 10;
    private static final int MARGEN_X = 5;

    private final Block[][] celdas;
    private final int filas;
    private final int columnas;
    private int restantes;

    public BlockGrid(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.celdas = new Block[filas][columnas];

        int altoPantalla = Gdx.graphics.getHeight();
        for (int f = 0; f < filas; f++) {
            int y = altoPantalla - (f + 1) * (BLOCK_HEIGHT + SEPARACION);
            for (int c = 0; c < columnas; c++) {
                int x = MARGEN_X + c * (BLOCK_WIDTH + SEPARACION);
                celdas[f][c] = new Block(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
                restantes++;
            }
        }
    }

    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }

    /** Bloque en (fila, columna), o null si esta fuera de la grilla o ya fue destruido. */
    public Block getBlock(int fila, int columna) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            return null;
        }
        return celdas[fila][columna];
    }

    /** Vecinos (arriba, abajo, izquierda, derecha) que todavia existen. */
    public List<Block> getVecinos(int fila, int columna) {
        List<Block> vecinos = new ArrayList<Block>();
        int[][] direcciones = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
        for (int[] d : direcciones) {
            Block b = getBlock(fila + d[0], columna + d[1]);
            if (b != null) {
                vecinos.add(b);
            }
        }
        return vecinos;
    }

    public void draw(ShapeRenderer shape) {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] != null) {
                    celdas[f][c].draw(shape);
                }
            }
        }
    }

    public void checkCollision(PingBall ball) {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] != null) {
                    ball.checkCollision(celdas[f][c]);
                }
            }
        }
    }

    /** Saca de la grilla los bloques destruidos y devuelve cuantos eran. */
    public int eliminarDestruidos() {
        int eliminados = 0;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] != null && celdas[f][c].isDestroyed()) {
                    celdas[f][c] = null;
                    restantes--;
                    eliminados++;
                }
            }
        }
        return eliminados;
    }

    public boolean estaVacia() {
        return restantes == 0;
    }
}
