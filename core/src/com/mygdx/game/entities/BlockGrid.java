package com.mygdx.game.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.mygdx.game.Config;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Grilla (matriz) de bloques, al estilo del juego 2048.
 *
 * Cada bloque esta en celdas[fila][columna] (la fila 0 es la de arriba) y
 * una celda vacia es null. La posicion en pantalla de cada bloque se calcula
 * siempre a partir de su celda, y mover(Direccion) desliza todos los bloques
 * por la matriz fusionando los vecinos de igual valor.
 */
public class BlockGrid {
    private static final int BLOCK_WIDTH = 70;
    private static final int BLOCK_HEIGHT = 26;
    private static final int SEPARACION = 10;
    private static final int MARGEN_X = 5;
    /** Al fusionar dos bloques y llegar a este valor, cae un premio de vida. */
    public static final int VALOR_PREMIO = 256;

    private final Block[][] celdas;
    private final int filas;
    private final int columnas;
    private int restantes;
    private final List<Block> premiosPendientes = new ArrayList<Block>(); // bloques que llegaron a VALOR_PREMIO
    private final Random random = new Random();
    private final GlyphLayout layout = new GlyphLayout(); // para centrar los numeros

    /**
     * La matriz parte casi vacia: se ponen "bloquesIniciales" bloques (2 o 4) en
     * celdas elegidas al azar, solo dentro de las primeras "filasIniciales" filas
     * (las de arriba), para que abajo quede espacio libre por donde moverlos.
     *
     * @param filas            filas totales de la matriz
     * @param columnas         columnas totales de la matriz
     * @param bloquesIniciales cuantos bloques parten en la matriz
     * @param filasIniciales   en cuantas filas (desde arriba) pueden aparecer al inicio
     */
    public BlockGrid(int filas, int columnas, int bloquesIniciales, int filasIniciales) {
        this.filas = filas;
        this.columnas = columnas;
        this.celdas = new Block[filas][columnas];

        List<int[]> candidatas = new ArrayList<int[]>();
        for (int f = 0; f < filasIniciales && f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                candidatas.add(new int[] { f, c });
            }
        }
        Collections.shuffle(candidatas, random);
        for (int i = 0; i < bloquesIniciales && i < candidatas.size(); i++) {
            int[] celda = candidatas.get(i);
            celdas[celda[0]][celda[1]] = crearBloque(celda[0], celda[1]);
            restantes++;
        }
    }

    private Block crearBloque(int fila, int columna) {
        return new Block(xCelda(columna), yCelda(fila), BLOCK_WIDTH, BLOCK_HEIGHT, valorInicial());
    }

    /** Los bloques nuevos parten con 2 o 4 (50% cada uno). */
    private int valorInicial() {
        return random.nextBoolean() ? 2 : 4;
    }

    // Posicion en pantalla de una celda
    private int xCelda(int columna) {
        return MARGEN_X + columna * (BLOCK_WIDTH + SEPARACION);
    }

    private int yCelda(int fila) {
        return Config.ALTO - (fila + 1) * (BLOCK_HEIGHT + SEPARACION);
    }

    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }

    /** Bloque en (fila, columna), o null si esta fuera de la grilla o la celda esta vacia. */
    public Block getBlock(int fila, int columna) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            return null;
        }
        return celdas[fila][columna];
    }

    /** Vecinos (arriba, abajo, izquierda, derecha) que existen. */
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

    // ------------------------------------------------------------------
    //  Movimiento estilo 2048
    // ------------------------------------------------------------------

    /**
     * Desliza todos los bloques en la direccion dada: se pegan al borde y los
     * vecinos de igual valor se fusionan en uno de valor doble (cada bloque se
     * fusiona como maximo una vez por movimiento). Si el movimiento cambio algo,
     * aparece un bloque nuevo (2 o 4) en una celda vacia al azar.
     *
     * Fusionar NO da puntos: el puntaje solo sube cuando la pelota rompe un
     * bloque (ver eliminarDestruidos()). Si una fusion llega a VALOR_PREMIO,
     * el bloque queda anotado para soltar un corazon (ver retirarPremios()).
     */
    public void mover(Direccion dir) {
        boolean cambio = false;

        boolean vertical = (dir == Direccion.ARRIBA || dir == Direccion.ABAJO);
        int lineas = vertical ? columnas : filas;   // filas o columnas a procesar
        int largo = vertical ? filas : columnas;    // celdas por linea

        for (int l = 0; l < lineas; l++) {
            // 1) bloques de la linea, ordenados desde el borde hacia el que se mueven
            List<Block> lista = new ArrayList<Block>();
            for (int i = 0; i < largo; i++) {
                Block b = getEnLinea(dir, l, i);
                if (b != null) {
                    lista.add(b);
                }
            }

            // 2) fusionar vecinos iguales
            List<Block> resultado = new ArrayList<Block>();
            int i = 0;
            while (i < lista.size()) {
                Block b = lista.get(i);
                if (i + 1 < lista.size() && lista.get(i + 1).getValor() == b.getValor()) {
                    b.setValor(b.getValor() * 2);
                    if (b.getValor() == VALOR_PREMIO) {
                        premiosPendientes.add(b);
                    }
                    restantes--;
                    cambio = true;
                    i += 2;
                } else {
                    i++;
                }
                resultado.add(b);
            }

            // 3) volver a poner la linea en la matriz, pegada al borde
            for (int j = 0; j < largo; j++) {
                Block nuevo = (j < resultado.size()) ? resultado.get(j) : null;
                if (getEnLinea(dir, l, j) != nuevo) {
                    cambio = true;
                }
                setEnLinea(dir, l, j, nuevo);
            }
        }

        reposicionar();
        if (cambio) {
            agregarBloqueNuevo();
        }
    }

    /**
     * Posiciones (x, y) de los bloques que llegaron a VALOR_PREMIO desde la ultima vez que se
     * pregunto. Cada una es un premio de vida que debe empezar a caer. La lista se vacia al leerla.
     */
    public List<int[]> retirarPremios() {
        List<int[]> posiciones = new ArrayList<int[]>();
        for (Block b : premiosPendientes) {
            posiciones.add(new int[] { b.getX(), b.getY() });
        }
        premiosPendientes.clear();
        return posiciones;
    }

    /** Celda numero i de la linea l, contando desde el borde hacia el que se mueve. */
    private Block getEnLinea(Direccion dir, int l, int i) {
        switch (dir) {
            case IZQUIERDA: return celdas[l][i];
            case DERECHA:   return celdas[l][columnas - 1 - i];
            case ARRIBA:    return celdas[i][l];
            default:        return celdas[filas - 1 - i][l]; // ABAJO
        }
    }

    private void setEnLinea(Direccion dir, int l, int i, Block b) {
        switch (dir) {
            case IZQUIERDA: celdas[l][i] = b; break;
            case DERECHA:   celdas[l][columnas - 1 - i] = b; break;
            case ARRIBA:    celdas[i][l] = b; break;
            default:        celdas[filas - 1 - i][l] = b; break; // ABAJO
        }
    }

    /** Coloca cada bloque en la posicion de pantalla que le corresponde a su celda. */
    private void reposicionar() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] != null) {
                    celdas[f][c].setXY(xCelda(c), yCelda(f));
                }
            }
        }
    }

    /** Crea un bloque (2 o 4) en una celda vacia al azar, si queda alguna. */
    private void agregarBloqueNuevo() {
        List<int[]> vacias = new ArrayList<int[]>();
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] == null) {
                    vacias.add(new int[] { f, c });
                }
            }
        }
        if (vacias.isEmpty()) {
            return;
        }
        int[] celda = vacias.get(random.nextInt(vacias.size()));
        celdas[celda[0]][celda[1]] = crearBloque(celda[0], celda[1]);
        restantes++;
    }

    // ------------------------------------------------------------------
    //  Dibujo, colisiones y estado
    // ------------------------------------------------------------------

    public void draw(ShapeRenderer shape) {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] != null) {
                    celdas[f][c].draw(shape);
                }
            }
        }
    }

    /** Dibuja el numero de cada bloque, centrado. Se llama con el SpriteBatch abierto. */
    public void drawValores(SpriteBatch batch, BitmapFont font) {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Block b = celdas[f][c];
                if (b == null) continue;
                font.setColor(b.getColorTexto());
                layout.setText(font, String.valueOf(b.getValor()));
                float tx = b.getX() + (b.getWidth() - layout.width) / 2;
                float ty = b.getY() + (b.getHeight() + layout.height) / 2;
                font.draw(batch, layout, tx, ty);
            }
        }
    }

    /**
     * La pelota solo choca con UN bloque por fotograma: el mas cercano a su centro.
     * (Antes chocaba con todos los que tocaba a la vez, y por eso "atravesaba" varios.)
     */
    public void checkCollision(PingBall ball) {
        Block masCercano = null;
        long mejorDistancia = Long.MAX_VALUE;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Block b = celdas[f][c];
                if (b != null && !b.isDestroyed() && ball.collidesWith(b)) {
                    long dx = b.getX() + b.getWidth() / 2 - ball.getX();
                    long dy = b.getY() + b.getHeight() / 2 - ball.getY();
                    long distancia = dx * dx + dy * dy;
                    if (distancia < mejorDistancia) {
                        mejorDistancia = distancia;
                        masCercano = b;
                    }
                }
            }
        }
        if (masCercano != null) {
            ball.checkCollision(masCercano);
        }
    }

    /** Saca de la grilla los bloques destruidos y devuelve los puntos que dan (la suma de sus valores). */
    public int eliminarDestruidos() {
        int puntos = 0;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] != null && celdas[f][c].isDestroyed()) {
                    puntos += celdas[f][c].getValor();
                    celdas[f][c] = null;
                    restantes--;
                }
            }
        }
        return puntos;
    }

    public boolean estaVacia() {
        return restantes == 0;
    }
}